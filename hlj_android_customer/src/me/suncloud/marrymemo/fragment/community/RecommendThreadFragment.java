package me.suncloud.marrymemo.fragment.community;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonviewlibrary.models.CommunityFeed;
import com.hunliji.hljhttplibrary.entities.HljHttpCountData;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;
import com.hunliji.hljtrackerlibrary.utils.TrackerUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.SocialHotRecyclerAdapter;
import me.suncloud.marrymemo.api.community.CommunityApi;
import me.suncloud.marrymemo.util.RecommendThreadUtil;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;
import me.suncloud.marrymemo.view.community.CreatePostActivity;
import me.suncloud.marrymemo.view.community.RecommendThreadActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2017/3/24.看帖页面
 */
@Deprecated
// TODO: 2018/3/28 wangtao
public class RecommendThreadFragment extends RefreshFragment implements View.OnClickListener,
        PullToRefreshVerticalRecyclerView.OnRefreshListener, SocialHotRecyclerAdapter
                .OnFeedItemClickListener, SocialHotRecyclerAdapter.OnReplyItemClickListener {

    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.backtop_btn)
    ImageButton backTopView;
    @BindView(R.id.msg_refresh_social)
    TextView msgRefreshSocial;

    public final static int REPLY_RESULT = 101;
    private View loadView;
    private View endView;
    private View socialRefreshView;

    private Unbinder unbinder;
    private Handler mHandler;
    private boolean isHide;
    private ArrayList<CommunityFeed> recommendThreadList;
    private LinearLayoutManager layoutManager;
    private SocialHotRecyclerAdapter adapter;

    private int offset;
    private int totalCount;

    private HljHttpSubscriber refreshSubscriber;//热门推荐
    private HljHttpSubscriber pageSubscriber;//feed流分页

    public static RecommendThreadFragment newInstance(int offset, int totalCount) {
        Bundle args = new Bundle();
        RecommendThreadFragment fragment = new RecommendThreadFragment();
        args.putInt("offset", offset);
        args.putInt("totalCount", totalCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_community_thread, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            offset = getArguments().getInt("offset", 0);
            totalCount = getArguments().getInt("totalCount", 0);
        }
        initValue();
        initView();

        initTracker();
    }


    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView, "community_feed_list");
    }


    private void initValue() {
        mHandler = new Handler();
        recommendThreadList = new ArrayList<>();
        recommendThreadList.addAll(RecommendThreadUtil.getInstance()
                .getRecommendThreads());
    }

    private void initView() {
        backTopView.setOnClickListener(this);

        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(recyclerView);
            }
        });
        emptyView.setOnEmptyClickListener(new HljEmptyView.OnEmptyClickListener() {
            @Override
            public void onEmptyClickListener() {
                onRefresh(recyclerView);
            }
        });
        View footView = getActivity().getLayoutInflater()
                .inflate(R.layout.hlj_foot_no_more___cm, null);
        endView = footView.findViewById(R.id.no_more_hint);
        loadView = footView.findViewById(R.id.loading);

        //刷新提示视图
        View refreshTipView = getActivity().getLayoutInflater()
                .inflate(R.layout.social_hot_refresh_view, null);
        socialRefreshView = refreshTipView.findViewById(R.id.social_refresh_view);
        socialRefreshView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollTop();
                recyclerView.setRefreshing(true);
            }
        });
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setOnRefreshListener(this);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        //热门推荐列表
        adapter = new SocialHotRecyclerAdapter(getActivity());
        adapter.setRefreshTipView(refreshTipView);
        adapter.setFooterView(footView);
        adapter.setOnFeedItemClickListener(this);
        adapter.setOnQaItemClickListener(new SocialHotRecyclerAdapter.OnQaItemClickListener() {
            @Override
            public void onQaItemClickListener() {
                if (getActivity() instanceof RecommendThreadActivity) {
                    Activity activity = getActivity();
                    activity.onBackPressed();
                }
            }
        });
        adapter.setOnReplyItemClickListener(this);
        adapter.setRecommendThreads(recommendThreadList);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(
                            RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        switch (newState) {
                            case RecyclerView.SCROLL_STATE_IDLE:
                                //加载更多
                                if (pageSubscriber == null || pageSubscriber.isUnsubscribed()) {
                                    if ((layoutManager != null && layoutManager
                                            .findLastVisibleItemPosition() > offset - 5) &&
                                            offset < totalCount) {
                                        endView.setVisibility(View.GONE);
                                        loadView.setVisibility(View.VISIBLE);
                                        initPageRecommend();
                                    } else {
                                        loadView.setVisibility(View.GONE);
                                        endView.setVisibility(View.VISIBLE);
                                    }
                                }
                                break;
                        }
                    }

                    @Override
                    public void onScrolled(
                            RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (layoutManager != null && layoutManager.findFirstVisibleItemPosition()
                                < 10) {
                            if (!isHide) {
                                hideFiltrateAnimation();
                            }
                        } else if (isHide) {
                            if (backTopView.getVisibility() == View.GONE) {
                                backTopView.setVisibility(View.VISIBLE);
                            }
                            showFiltrateAnimation();
                        }
                    }
                });
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null && !getActivity().isFinishing() && !isDetached()) {
                    recyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
            }
        }, 500);
    }

    public int getOffset() {
        return offset;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            Observable<HljHttpCountData<List<CommunityFeed>>> observable = CommunityApi
                    .getRecommendListObb(
                    1,
                    8,
                    offset);

            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpCountData<List<CommunityFeed>>>() {
                        @Override
                        public void onNext(
                                HljHttpCountData<List<CommunityFeed>> listHljHttpData) {
                            ArrayList<CommunityFeed> recommendThreads = new ArrayList<>();
                            if (listHljHttpData.getData() != null) {
                                recommendThreads.addAll(listHljHttpData.getData());
                                totalCount = listHljHttpData.getTotalCount();
                                offset = offset + listHljHttpData.getCurrentCount();
                            }
                            if (recommendThreads.size() > 0) {
                                socialRefreshView.setVisibility(View.VISIBLE);
                                msgRefreshSocial.setText(getString(R.string
                                                .msg_refresh_recommend_tip,
                                        recommendThreads.size()));

                            } else {
                                socialRefreshView.setVisibility(View.GONE);
                                msgRefreshSocial.setText(getString(R.string
                                        .msg_refresh_recommend_empty_tip));
                            }
                            adapter.setNewCount(recommendThreads.size());

                            msgRefreshSocial.setVisibility(View.VISIBLE);
                            mHandler.removeCallbacks(runnable);
                            mHandler.postDelayed(runnable, 3000);
                            setAdapterView(recommendThreads, true);
                            adapter.notifyDataSetChanged();
                            if (recommendThreads.size() > 0) {
                                socialRefreshView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        scrollTop();
                                    }
                                }, 500);
                            }
                        }
                    })
                    .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setDataNullable(true)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(refreshSubscriber);
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (getActivity() != null && !getActivity().isFinishing())
                msgRefreshSocial.setVisibility(View.GONE);
        }
    };

    /**
     * 分页机制不一样，当offset<totalCount时，表示有下一页
     */
    private void initPageRecommend() {
        Observable<HljHttpCountData<List<CommunityFeed>>> pageObservable = CommunityApi
                .getRecommendListObb(
                0,
                20,
                offset);
        pageSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpCountData<List<CommunityFeed>>>() {
                    @Override
                    public void onNext(
                            HljHttpCountData<List<CommunityFeed>> data) {
                        if (data != null) {
                            if (CommonUtil.isCollectionEmpty(data.getData())) {
                                endView.setVisibility(View.VISIBLE);
                                loadView.setVisibility(View.GONE);
                            }
                            offset = offset + data.getCurrentCount();
                            setAdapterView((ArrayList<CommunityFeed>) data.getData(), false);
                            adapter.notifyDataSetChanged();
                        } else {
                            endView.setVisibility(View.VISIBLE);
                            loadView.setVisibility(View.GONE);
                        }
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        loadView.setVisibility(View.GONE);
                        endView.setVisibility(View.VISIBLE);
                    }
                })
                .setDataNullable(true)
                .build();

        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    private void setAdapterView(ArrayList<CommunityFeed> list, boolean isRefresh) {
        if (isRefresh) {
            recommendThreadList.addAll(0, list);
        } else {
            recommendThreadList.addAll(list);
        }
        RecommendThreadUtil.getInstance()
                .saveRecommendThreads(recommendThreadList);
        adapter.setRecommendThreads(recommendThreadList);
    }

    /**
     * Feed点击事件
     *
     * @param position
     * @param item
     * @param type
     */
    @Override
    public void onFeedItemClickListener(
            int position, long id, Object item, int type) {
        Intent intent = new Intent();
        switch (type) {
            case SocialHotRecyclerAdapter.THREAD_TYPE:
                CommunityThread communityThread = (CommunityThread) item;
                intent.setClass(getActivity(), CommunityThreadDetailActivity.class);
                JSONObject jsonObject = TrackerUtil.getSiteJson("D1/G1", position + 1, null);
                if (jsonObject != null) {
                    intent.putExtra("site", jsonObject.toString());
                }
                intent.putExtra("id", communityThread.getId());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
                break;
            case SocialHotRecyclerAdapter.QUESTION_TYPE:
                Question question = (Question) item;
                intent.setClass(getActivity(), QuestionDetailActivity.class);
                JSONObject jsonObject1 = TrackerUtil.getSiteJson("D1/G1", position + 1, null);
                if (jsonObject1 != null) {
                    intent.putExtra("site", jsonObject1.toString());
                }
                intent.putExtra("questionId", question.getId());
                getActivity().startActivity(intent);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void refresh(Object... params) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backtop_btn:
                scrollTop();
                break;
        }

    }

    //滑动到顶部
    public void scrollTop() {
        if (layoutManager == null || recyclerView == null) {
            return;
        }
        if (layoutManager.findFirstVisibleItemPosition() >= 5) {
            recyclerView.getRefreshableView()
                    .scrollToPosition(5);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.getRefreshableView()
                            .smoothScrollToPosition(0);
                }
            });
        } else {
            recyclerView.getRefreshableView()
                    .smoothScrollToPosition(0);
        }

    }

    private void showFiltrateAnimation() {
        if (backTopView == null) {
            return;
        }
        isHide = false;
        if (isAnimEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.slide_in_bottom2);
            animation.setFillBefore(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isHide) {
                                hideFiltrateAnimation();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            backTopView.startAnimation(animation);
        }
    }

    private boolean isAnimEnded() {
        return backTopView != null && (backTopView.getAnimation() == null || backTopView
                .getAnimation()
                .hasEnded());
    }

    private void hideFiltrateAnimation() {
        if (backTopView == null) {
            return;
        }
        isHide = true;
        if (isAnimEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.slide_out_bottom2);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isHide) {
                                showFiltrateAnimation();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            backTopView.startAnimation(animation);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            int position = data.getIntExtra("position", 0);
            CommunityThread thread = (CommunityThread) recommendThreadList.get(position)
                    .getEntity();
            if (thread != null) {
                thread.setPostCount(thread.getPostCount() + 1);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
    }

    @Override
    public void onReply(CommunityThread item, int position) {
        if (!AuthUtil.loginBindCheck(getContext())) {
            return;
        }
        if (item.getPostCount() == 0) {
            Intent intent = new Intent(getActivity(), CreatePostActivity.class);
            intent.putExtra(CreatePostActivity.ARG_POST, item.getPost());
            intent.putExtra(CreatePostActivity.ARG_POSITION, position);
            intent.putExtra(CreatePostActivity.ARG_IS_REPLY_THREAD, true);
            startActivityForResult(intent, REPLY_RESULT);
            getActivity().overridePendingTransition(R.anim.slide_in_from_bottom,
                    R.anim.activity_anim_default);
        }
    }
}
