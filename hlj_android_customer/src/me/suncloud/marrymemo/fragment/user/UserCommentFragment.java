package me.suncloud.marrymemo.fragment.user;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.user.UserCommentListAdapter;
import me.suncloud.marrymemo.adpter.user.viewholder.UserCommentViewHolder;
import me.suncloud.marrymemo.api.user.UserApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.comment.CommentServiceActivity;
import me.suncloud.marrymemo.view.comment.ServiceCommentDetailActivity;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 用户主页-用户评价列表
 * Created by chen_bin on 2017/6/12 0012.
 */
public class UserCommentFragment extends ScrollAbleFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, OnItemClickListener<ServiceComment>,
        UserCommentViewHolder.OnMenuListener {
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private View footerView;
    private View endView;
    private View loadView;
    private Dialog menuDialog;
    private UserCommentListAdapter adapter;
    private ServiceComment comment;
    private long userId;
    private boolean isHim;
    private int position;
    private Unbinder unbinder;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private Subscription rxBusEventSub;

    public static UserCommentFragment newInstance(long userId) {
        UserCommentFragment fragment = new UserCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getLong("user_id");
            User user = Session.getInstance()
                    .getCurrentUser(getContext());
            isHim = user == null || !user.getId()
                    .equals(userId);
        }
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new UserCommentListAdapter(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_ptr_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        initViews();
        return rootView;
    }

    private void initViews() {
        emptyView.setHintId(isHim ? R.string.label_him_no_comment : R.string.label_no_comment);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .setPadding(0, CommonUtil.dp2px(getContext(), 10), 0, 0);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setFooterView(footerView);
        adapter.setOnItemClickListener(this);
        adapter.setOnMenuListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh(null);
        registerRxBusEvent();
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ServiceComment>>>() {
                        @Override
                        public void onNext(HljHttpData<List<ServiceComment>> listHljHttpData) {
                            adapter.setShowMenu(!isHim); //是TA的话不显示编辑按钮
                            adapter.setComments(listHljHttpData.getData());
                            initPagination(listHljHttpData.getPageCount());
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .build();
            UserApi.getUserCommentsObb(userId, 1, HljCommon.PER_PAGE)
                    .subscribe(refreshSubscriber);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<ServiceComment>>> observable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<ServiceComment>>>() {
                    @Override
                    public Observable<HljHttpData<List<ServiceComment>>> onNextPage(int page) {
                        return UserApi.getUserCommentsObb(userId, page, Constants.PER_PAGE);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ServiceComment>>>() {
                    @Override
                    public void onNext(HljHttpData<List<ServiceComment>> listHljHttpData) {
                        adapter.addComments(listHljHttpData.getData());
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public void onItemClick(int position, ServiceComment comment) {
        if (comment != null && comment.getId() > 0) {
            this.position = position;
            this.comment = comment;
            Intent intent = new Intent(getContext(), ServiceCommentDetailActivity.class);
            intent.putExtra("id", comment.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onMenu(final int position, final ServiceComment comment) {
        if (menuDialog != null && menuDialog.isShowing()) {
            return;
        }
        if (menuDialog == null) {
            menuDialog = new Dialog(getContext(), R.style.BubbleDialogTheme);
            menuDialog.setContentView(R.layout.dialog_bottom_menu___cm);
            menuDialog.findViewById(R.id.btn_cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            menuDialog.dismiss();
                        }
                    });
            Window win = menuDialog.getWindow();
            if (win != null) {
                WindowManager.LayoutParams params = win.getAttributes();
                params.width = CommonUtil.getDeviceSize(getContext()).x;
                win.setAttributes(params);
                win.setGravity(Gravity.BOTTOM);
                win.setWindowAnimations(R.style.dialog_anim_rise_style);
            }
        }
        Button btnMenu = menuDialog.findViewById(R.id.btn_menu);
        btnMenu.setText(R.string.label_edit);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialog.dismiss();
                UserCommentFragment.this.position = position;
                UserCommentFragment.this.comment = comment;
                Intent intent = new Intent(getContext(), CommentServiceActivity.class);
                intent.putExtra(CommentServiceActivity.ARG_COMMENT, comment);
                startActivity(intent);
            }
        });
        menuDialog.show();
    }

    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case EDIT_COMMENT_SUCCESS:
                                    if (position < 0) {
                                        return;
                                    }
                                    ServiceComment c = (ServiceComment) rxEvent.getObject();
                                    if (comment == null || c == null || comment.getId() != c
                                            .getId()) {
                                        return;
                                    }
                                    comment.setContent(c.getContent());
                                    comment.setRating(c.getRating());
                                    comment.setPhotos(c.getPhotos());
                                    adapter.notifyItemChanged(position);
                                    break;

                            }
                        }
                    });
        }
    }

    @Override
    public View getScrollableView() {
        return recyclerView == null ? null : recyclerView.getRefreshableView();
    }

    @Override
    public void refresh(Object... params) {
        if (params != null && params.length > 0) {
            userId = (long) params[0];
        }
        isHim = !Session.getInstance()
                .getCurrentUser(getContext())
                .getId()
                .equals(userId);
        emptyView.setHintId(isHim ? R.string.label_him_no_comment : R.string.label_no_comment);
        onRefresh(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber, rxBusEventSub);
    }
}