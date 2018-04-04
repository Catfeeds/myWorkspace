package com.hunliji.hljcardlibrary.views.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcardlibrary.adapter.CardTemplateAdapter;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.ChooseTheme;
import com.hunliji.hljcardlibrary.models.SingleTemplate;
import com.hunliji.hljcardlibrary.models.Template;
import com.hunliji.hljcardlibrary.utils.PrivilegeUtil;
import com.hunliji.hljcardlibrary.views.activities.CardTemplateActivity;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.utils.ShareUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;

/**
 * Created by hua_rong on 2017/6/16.
 * 增加新页
 */

public class CardTemplateFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, CardTemplateAdapter.OnTemplateClickListener {

    private static final String TYPE = "type";
    private static final String THEME_ID = "theme_id";
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    Unbinder unbinder;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber checkSub;
    private String type;
    private long themeId;
    private ShareUtil shareUtil;
    private HljHttpSubscriber subscriber;
    private Subscription rxSubscription;
    public static final int SHARE = 0;
    public static final int MEMBER = 1;
    private HljHttpSubscriber unLockSubscriber;
    private CardTemplateAdapter adapter;
    private boolean isThemeLock;

    public static CardTemplateFragment newInstance(String type, long themeId) {
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        args.putLong(THEME_ID, themeId);
        CardTemplateFragment fragment = new CardTemplateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HljShare.RequestCode.SHARE_TO_PENGYOU:
                    if (unLockSubscriber == null || unLockSubscriber.isUnsubscribed()) {
                        Observable<HljHttpResult> observable = CardApi.unlockTheme();
                        unLockSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                                .setOnNextListener(

                                        new SubscriberOnNextListener() {
                                            @Override
                                            public void onNext(Object o) {
                                                if (getCardTemplateActivity() != null) {
                                                    getCardTemplateActivity().refreshFragment();
                                                }
                                            }
                                        })
                                .build();
                        observable.subscribe(unLockSubscriber);
                    }

                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private CardTemplateActivity getCardTemplateActivity() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return null;
        }
        return (CardTemplateActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getString(TYPE);
            themeId = bundle.getLong(THEME_ID);
        }
        registerRxBusEvent();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hlj_common_fragment_ptr_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setBackgroundColor(Color.WHITE);
        recyclerView.getRefreshableView()
                .addItemDecoration(new TemplateItemDecoration(getContext()));
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);

        adapter = new CardTemplateAdapter(getContext());
        adapter.setOnTemplateClickListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                try {
                    switch (adapter.getItemViewType(position)) {
                        case CardTemplateAdapter.LOCK_TEMPLATE_HEADER:
                            return 3;
                        case CardTemplateAdapter.THREE_TEMPLATE_ITEM:
                            return 1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 1;
            }
        });

        onError();
        onRefresh(recyclerView);
    }

    private void onError() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(recyclerView);
            }
        });
    }

    @Override
    public void refresh(Object... params) {
        onRefresh(recyclerView);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (recyclerView == null) {
            return;
        }
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            Observable<SingleTemplate> observable = CardApi.getPageTemplateList(themeId, type);
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<SingleTemplate>() {
                        @Override
                        public void onNext(SingleTemplate singleTemplate) {
                            showSingleTempleView(singleTemplate);
                        }
                    })
                    .setDataNullable(true)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .setPullToRefreshBase(refreshView)
                    .setContentView(refreshView)
                    .build();
            observable.subscribe(refreshSubscriber);
        }
    }

    private void shareToPengYou() {
        if (shareUtil == null) {
            if (subscriber != null && !subscriber.isUnsubscribed()) {
                subscriber.unsubscribe();
            }
            Observable<ShareInfo> observable = CardApi.getAppShareInfo();
            subscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<ShareInfo>() {
                        @Override
                        public void onNext(ShareInfo shareInfo) {
                            if (shareInfo != null) {
                                shareUtil = new ShareUtil(getContext(), shareInfo, handler);
                                shareUtil.shareToPengYou();
                            }
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            Toast.makeText(getContext(), "分享失败", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            observable.subscribe(subscriber);
        } else {
            shareUtil.shareToPengYou();
        }
    }

    private void registerRxBusEvent() {
        rxSubscription = RxBus.getDefault()
                .toObservable(RxEvent.class)
                .subscribe(new RxBusSubscriber<RxEvent>() {
                    @Override
                    protected void onEvent(RxEvent rxEvent) {
                        switch (rxEvent.getType()) {
                            case OPEN_MEMBER_SUCCESS:
                                if (getCardTemplateActivity() != null) {
                                    getCardTemplateActivity().refreshFragment();
                                }
                                break;
                        }
                    }
                });
    }

    private void showSingleTempleView(SingleTemplate singleTemplate) {
        ArrayList<Template> templates = singleTemplate.getThemeTpl();
        if (templates != null && !templates.isEmpty()) {
            recyclerView.getRefreshableView()
                    .setPadding(0,
                            CommonUtil.dp2px(getContext(), 16),
                            0,
                            CommonUtil.dp2px(getContext(), 4));
        } else {
            recyclerView.getRefreshableView()
                    .setPadding(0, 0, 0, CommonUtil.dp2px(getContext(), 4));
        }
        if (getCardTemplateActivity().hasChecked()) {
            singleTemplate.isThemeLock(getCardTemplateActivity().isVipAvailable(),
                    new ChooseTheme.IsLockCallback() {
                        @Override
                        public void isLockBack(boolean isLock) {
                            isThemeLock = isLock;
                            adapter.setShared(!isLock);
                        }
                    });
        } else {
            singleTemplate.isThemeLock(getContext(), checkSub, new ChooseTheme.IsLockCallback() {
                @Override
                public void isLockBack(boolean isLock) {
                    isThemeLock = isLock;
                    adapter.setShared(!isLock);
                }
            });
        }

        adapter.setDataList(templates, singleTemplate.getOtherTpl());
        if ((templates == null || templates.isEmpty()) && (singleTemplate.getOtherTpl() == null
                || singleTemplate.getOtherTpl()
                .isEmpty())) {
            emptyView.setHintId(R.string.hint_no_such_template___card);
            emptyView.setEmptyDrawableId(R.mipmap.icon_empty_wedding_card);
            emptyView.showEmptyView();
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.hideEmptyView();
            recyclerView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.getRefreshableView()
                .setAdapter(null);
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber,
                checkSub,
                subscriber,
                rxSubscription,
                unLockSubscriber);
    }

    @Override
    public void onTemplateShare() {
        onCardShare(SHARE);
    }

    public void onCardShare(final int type) {
        final Dialog shareDialog = new Dialog(getContext(), R.style.BubbleDialogTheme);
        Window window = shareDialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_theme_share___card);
            Button shareBtn = window.findViewById(R.id.share_btn);
            TextView hint = window.findViewById(R.id.hint);
            if (type == SHARE) {
                shareBtn.setText(R.string.btn_share_py___card);
                hint.setText(R.string.hint_theme_share___card);
            } else if (type == MEMBER) {
                shareBtn.setText(R.string.label_join_member___card);
                hint.setText(getString(R.string.hint_join_member___card));
            }
            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareDialog.dismiss();
                    if (type == SHARE) {
                        shareToPengYou();
                    } else if (type == MEMBER) {
                        ARouter.getInstance()
                                .build(RouterPath.IntentPath.Customer.OPEN_MEMBER)
                                .navigation(getContext());
                    }
                }
            });
            window.findViewById(R.id.close_btn)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareDialog.dismiss();
                        }
                    });
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = CommonUtil.getDeviceSize(getContext());
            params.width = Math.round(point.x * 27 / 32);
        }
        shareDialog.show();
    }

    @Override
    public void onTemplateUse(Template template) {
        if (getActivity() != null && getActivity() instanceof TemplateListInterface) {
            ((TemplateListInterface) getActivity()).onCreatePage(template);
        }
    }

    @Override
    public void onTemplateMember(final Template template) {
        if (getCardTemplateActivity().hasChecked()) {
            if (getCardTemplateActivity().isVipAvailable()) {
                onTemplateUse(template);
            } else {
                onCardShare(MEMBER);
            }
        } else {
            PrivilegeUtil.isVipAvailable(getContext(),
                    checkSub,
                    new PrivilegeUtil.PrivilegeCallback() {
                        @Override
                        public void checkDone(boolean isAvailable) {
                            if (isAvailable) {
                                onTemplateUse(template);
                            } else {
                                onCardShare(MEMBER);
                            }
                        }
                    });
        }
    }

    public class TemplateItemDecoration extends RecyclerView.ItemDecoration {

        private int bottom; //模板底边距
        private int space; //模板间距
        private int edge; //左右边距

        public TemplateItemDecoration(Context context) {
            bottom = CommonUtil.dp2px(context, 12);
            space = CommonUtil.dp2px(context, 12);
            edge = CommonUtil.dp2px(context, 20);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (parent.getAdapter() != null && parent.getAdapter() instanceof CardTemplateAdapter) {
                switch (((CardTemplateAdapter) parent.getAdapter()).getSpaceType(position)) {
                    case Header:
                        outRect.top = 0;
                        outRect.left = 0;
                        outRect.right = 0;
                        outRect.bottom = 0;
                        break;
                    case Left:
                        outRect.top = 0;
                        outRect.left = edge;
                        outRect.right = 0;
                        outRect.bottom = bottom;
                        break;
                    case Middle:
                        outRect.top = 0;
                        outRect.left = space;
                        outRect.right = space;
                        outRect.bottom = bottom;
                        break;
                    case Right:
                        outRect.top = 0;
                        outRect.left = 0;
                        outRect.right = edge;
                        outRect.bottom = bottom;
                        break;
                }
            }
        }
    }


    public interface TemplateListInterface {
        void onCreatePage(Template template);
    }
}
