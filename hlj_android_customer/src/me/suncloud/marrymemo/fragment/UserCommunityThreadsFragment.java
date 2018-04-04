package me.suncloud.marrymemo.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljsharelibrary.utils.ShareUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.UserCommunityThreadsAdapter;
import me.suncloud.marrymemo.api.communitythreads.CommunityThreadApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;
import me.suncloud.marrymemo.view.community.CreatePostActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by werther on 16/8/25.
 * 用户主页下话题列表fragment
 */
public class UserCommunityThreadsFragment extends ScrollAbleFragment implements
        UserCommunityThreadsAdapter.onItemClickListener {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private View rootView;
    private Unbinder unbinder;
    private ArrayList<CommunityThread> threads;
    private UserCommunityThreadsAdapter adapter;

    private long userId;
    private HljHttpSubscriber initSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private View endView;
    private View loadView;

    public static UserCommunityThreadsFragment newInstance(long userId) {
        UserCommunityThreadsFragment fragment = new UserCommunityThreadsFragment();
        Bundle args = new Bundle();
        args.putLong("user_id", userId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initValues();
    }

    private void initValues() {
        userId = getArguments().getLong("user_id");
        threads = new ArrayList<>();
        adapter = new UserCommunityThreadsAdapter(getActivity(), threads);
        View footerView = View.inflate(getActivity(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        adapter.setOnItemClickListener(this);
        adapter.setOnReplyItemClickListener(new UserCommunityThreadsAdapter
                .OnReplyItemClickListener() {
            @Override
            public void onReply(CommunityThread item, int position) {
                if (!AuthUtil.loginBindCheck(getContext())) {
                    return;
                }
                Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                intent.putExtra(CreatePostActivity.ARG_POST, item.getPost());
                intent.putExtra(CreatePostActivity.ARG_POSITION, position);
                intent.putExtra(CreatePostActivity.ARG_IS_REPLY_THREAD, true);
                startActivityForResult(intent, 1);
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.activity_anim_default);
            }
        });

        adapter.setFooterView(footerView);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.hlj_common_fragment_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        setEmptyHint();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setPadding(0, CommonUtil.dp2px(getContext(), 10), 0, 0);
        recyclerView.setAdapter(adapter);
        initLoad();

        return rootView;
    }

    private void initLoad() {
        Observable<HljHttpData<List<CommunityThread>>> observable = CommunityThreadApi
                .getUserThreadsObb(
                userId,
                1);
        initSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                .setProgressBar(progressBar)
                .setContentView(recyclerView)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityThread>>>() {
                    @Override
                    public void onNext(HljHttpData<List<CommunityThread>> listHljHttpData) {
                        threads.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                        initAutoPages(listHljHttpData.getPageCount());
                    }
                })
                .build();

        observable.subscribe(initSubscriber);
    }

    private void initAutoPages(int pageCount) {
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        Observable<HljHttpData<List<CommunityThread>>> pageOb = PaginationTool
                .buildPagingObservable(
                recyclerView,
                pageCount,
                new PagingListener<HljHttpData<List<CommunityThread>>>() {
                    @Override
                    public Observable<HljHttpData<List<CommunityThread>>> onNextPage(int page) {
                        return CommunityThreadApi.getUserThreadsObb(userId, page);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        pageSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                .setOnNextListener(

                        new SubscriberOnNextListener<HljHttpData<List<CommunityThread>>>() {
                            @Override
                            public void onNext(HljHttpData<List<CommunityThread>> listHljHttpData) {
                                threads.addAll(listHljHttpData.getData());
                                adapter.notifyDataSetChanged();
                            }
                        })
                .build();
        pageOb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public void refresh(Object... params) {
        if (params != null) {
            userId = (long) params[0];
        }
        setEmptyHint();
        threads.clear();
        adapter.notifyDataSetChanged();
        initLoad();
    }

    private void setEmptyHint() {
        User user = Session.getInstance()
                .getCurrentUser(getActivity());
        if (user != null && user.getId()
                .equals(userId)) {
            emptyView.setHintId(R.string.hint_my_thread_empty);
        } else {
            emptyView.setHintId(R.string.label_him_no_thread);
        }
    }

    @Override
    public View getScrollableView() {
        if (threads.isEmpty()) {
            return null;
        } else {
            return recyclerView;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (initSubscriber != null && !initSubscriber.isUnsubscribed()) {
            initSubscriber.unsubscribe();
        }
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
    }

    @Override
    public void onItemClick(
            int position, CommunityThread item) {
        if (item != null) {
            Intent intent = new Intent(getActivity(), CommunityThreadDetailActivity.class);
            intent.putExtra("id", item.getId());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            int position = data.getIntExtra("position", 0);
            CommunityThread thread = threads.get(position);
            if (thread != null) {
                thread.setPostCount(thread.getPostCount() + 1);
            }
            adapter.notifyDataSetChanged();
        }
    }
}
