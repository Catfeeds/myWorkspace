package com.hunliji.hljcardlibrary.views.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcardlibrary.adapter.CardThemeAdapter;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.ChooseTheme;
import com.hunliji.hljcardlibrary.models.Theme;
import com.hunliji.hljcardlibrary.views.activities.CardPreviewActivity;
import com.hunliji.hljcardlibrary.views.activities.CardThemeActivity;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
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

/**
 * Created by hua_rong on 2017/6/21.
 * 选择主题模板
 */

public class CardThemeFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, CardThemeAdapter.OnCardThemeClickListener {

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    Unbinder unbinder;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber checkSub;
    private static final String MARK_ID = "mark_id";
    private static final String IS_MEMBER = "is_member";
    private int isMember;
    private long markId;
    private Dialog shareDialog;
    private HljHttpSubscriber subscriber;
    private ShareUtil shareUtil;
    private HljHttpSubscriber unLockSubscriber;
    private boolean isThemeLock;
    private CardThemeAdapter adapter;

    public static CardThemeFragment newInstance(long markId, int isMember) {
        Bundle args = new Bundle();
        args.putLong(MARK_ID, markId);
        args.putInt(IS_MEMBER, isMember);
        CardThemeFragment fragment = new CardThemeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            markId = bundle.getLong(MARK_ID);
            isMember = bundle.getInt(IS_MEMBER);
        }
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
        RecyclerView mRecyclerView = recyclerView.getRefreshableView();
        ThemeItemDecoration itemDecoration = new ThemeItemDecoration(getContext());
        mRecyclerView.addItemDecoration(itemDecoration);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);


        adapter = new CardThemeAdapter(getContext());
        adapter.setOnCardThemeClickListener(this);
        mRecyclerView.setAdapter(adapter);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case CardThemeAdapter.LOCK_TEMPLATE_HEADER:
                        return 2;
                    case CardThemeAdapter.THREE_TEMPLATE_ITEM:
                        return 1;
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
                                                if (getCardThemeActivity() != null) {
                                                    getCardThemeActivity().refreshFragment();
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

    private CardThemeActivity getCardThemeActivity() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return null;
        }
        return (CardThemeActivity) getActivity();
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
            Observable<ChooseTheme> observable = CardApi.getThemeList(isMember, markId);
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<ChooseTheme>() {
                        @Override
                        public void onNext(ChooseTheme chooseTheme) {
                            if (chooseTheme != null) {
                                if (chooseTheme.getThemeList() == null || chooseTheme.getThemeList()
                                        .isEmpty()) {
                                    emptyView.setHintId(R.string.hint_no_such_template___card);
                                    emptyView.setEmptyDrawableId(R.mipmap.icon_empty_wedding_card);
                                    emptyView.showEmptyView();
                                    recyclerView.setVisibility(View.GONE);
                                } else {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    emptyView.hideEmptyView();
                                    showView(chooseTheme);
                                }
                            }
                        }
                    })
                    .setDataNullable(true)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .setPullToRefreshBase(refreshView)
                    .build();
            observable.subscribe(refreshSubscriber);
        }
    }

    private void showView(ChooseTheme chooseTheme) {
        ArrayList<Theme> freeThemes = new ArrayList<>();
        ArrayList<Theme> greatThemes = new ArrayList<>();
        ArrayList<Theme> templates = chooseTheme.getThemeList();
        if (templates != null && !templates.isEmpty()) {
            if (isMember == 1) {
                freeThemes.addAll(templates);
            } else {
                for (Theme theme : templates) {
                    if (theme.isLocked()) {
                        greatThemes.add(theme);
                    } else {
                        freeThemes.add(theme);
                    }
                }
            }
        }
        if (!freeThemes.isEmpty()) {
            recyclerView.getRefreshableView()
                    .setPadding(0,
                            CommonUtil.dp2px(getContext(), 16),
                            0,
                            CommonUtil.dp2px(getContext(), 4));
        } else {
            recyclerView.getRefreshableView()
                    .setPadding(0, 0, 0, CommonUtil.dp2px(getContext(), 4));
        }

        if (getCardThemeActivity().hasChecked()) {
            chooseTheme.isThemeLock(getCardThemeActivity().isVipAvailable(),
                    new ChooseTheme.IsLockCallback() {
                        @Override
                        public void isLockBack(boolean isLock) {
                            isThemeLock = isLock;
                            adapter.setShared(!isLock);
                        }
                    });
        } else {
            chooseTheme.isThemeLock(getContext(), checkSub, new ChooseTheme.IsLockCallback() {
                @Override
                public void isLockBack(boolean isLock) {
                    isThemeLock = isLock;
                    adapter.setShared(!isLock);
                }
            });
        }
        adapter.setDataList(freeThemes, greatThemes);
        adapter.setMember(isMember);
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
        CommonUtil.unSubscribeSubs(refreshSubscriber, subscriber, unLockSubscriber, checkSub);
    }

    @Override
    public void onCardShare() {
        if (HljCard.isCardMaster(getContext()) && !AuthUtil.loginBindCheck(getContext())) {
            return;
        }
        if (shareDialog != null && shareDialog.isShowing()) {
            return;
        }
        if (shareDialog == null) {
            shareDialog = new Dialog(getContext(), R.style.BubbleDialogTheme);
            Window window = shareDialog.getWindow();
            if (window != null) {
                window.setContentView(R.layout.dialog_theme_share___card);
                TextView tvHint = window.findViewById(R.id.hint);
                tvHint.setText(R.string.hint_theme_share___card);
                window.findViewById(R.id.share_btn)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareDialog.dismiss();
                                shareToPengYou();
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
        }
        shareDialog.show();
    }

    private void shareToPengYou() {
        if (shareUtil == null) {
            if (subscriber == null || subscriber.isUnsubscribed()) {
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
            }
        } else {
            shareUtil.shareToPengYou();
        }
    }

    @Override
    public void onCardThemePreview(Theme theme) {
        if (theme != null) {
            Intent intent = new Intent(getContext(), CardPreviewActivity.class);
            intent.putExtra("id", theme.getId());
            intent.putExtra("path", theme.getPreviewLink());
            intent.putExtra("isThemeLock", isThemeLock && theme.isLocked());
            startActivity(intent);
        }
    }


    public class ThemeItemDecoration extends RecyclerView.ItemDecoration {

        private int bottom; //模板底边距
        private int space; //模板间距
        private int edge; //左右边距

        public ThemeItemDecoration(Context context) {
            bottom = CommonUtil.dp2px(context, 12);
            space = CommonUtil.dp2px(context, 6);
            edge = CommonUtil.dp2px(context, 16);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (parent.getAdapter() != null && parent.getAdapter() instanceof CardThemeAdapter) {
                switch (((CardThemeAdapter) parent.getAdapter()).getSpaceType(position)) {
                    case Header:
                        outRect.top = 0;
                        outRect.left = 0;
                        outRect.right = 0;
                        outRect.bottom = 0;
                        break;
                    case Left:
                        outRect.top = 0;
                        outRect.left = edge;
                        outRect.right = space;
                        outRect.bottom = bottom;
                        break;
                    case Right:
                        outRect.top = 0;
                        outRect.left = space;
                        outRect.right = edge;
                        outRect.bottom = bottom;
                        break;
                }
            }
        }
    }

}
