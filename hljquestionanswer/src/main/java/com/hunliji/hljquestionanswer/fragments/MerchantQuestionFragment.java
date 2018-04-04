package com.hunliji.hljquestionanswer.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.adapters.MerchantQuestionAdapter;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.models.wrappers.RecQaWrappers;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2016/8/16.问答列表-商家推荐
 */
public class MerchantQuestionFragment extends ScrollAbleFragment implements PullToRefreshBase
        .OnRefreshListener {


    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.btn_scroll_top)
    ImageButton backScrollTop;

    private final static int REC_QUESTION = 0;
    private final static int NEW_QUESTION = 1;
    private final static String REC_QA_URL = "p/wedding/index." +
            "php/Admin/APIQaQuestion/recQuestion";
    private final static String ALL_QA_URL = "p/wedding/index" + "" + "" +
            ".php/home/APIQaQuestion/latestQuestion";
    private final static String ALL_QA_LAST_URL = "p/wedding/index" + "" + "" +
            ".php/home/APIQaQuestion/latestQuestion?last_answer_time=%s";

    private LinearLayoutManager layoutManager;
    private MerchantQuestionAdapter adapter;
    private ArrayList<RecQaWrappers> recQaAnswers;

    private View endView;
    private View loadView;
    private Unbinder unbinder;
    private boolean isHide;
    private Handler mHandler;

    private int type;
    private String url;
    private long lastAnswerTime;//全部问题页面需要传lastAnswerTime，防止重复

    private HljHttpSubscriber pageSubscriber;//分页
    private HljHttpSubscriber refreshSubscriber;//刷新

    public static MerchantQuestionFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        MerchantQuestionFragment fragment = new MerchantQuestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        recQaAnswers = new ArrayList<>();
        Bundle arg = getArguments();
        if (arg != null) {
            type = arg.getInt("type", 0);
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_ptr_recycler_view___cm, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initValue();
        initView();
        initLoad();
        return rootView;
    }

    private void initValue() {
        switch (type) {
            case REC_QUESTION:
                url = REC_QA_URL;
                break;
            case NEW_QUESTION:
                url = ALL_QA_URL;
                break;
        }

    }

    private void initView() {
        mHandler = new Handler();
        //网络异常,可点击屏幕重新加载
        emptyView.setEmptyDrawableId(R.mipmap.icon_empty_message);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                refresh();
            }
        });
        emptyView.setHintId(R.string.hint_rec_qa_empty___qa);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        adapter = new MerchantQuestionAdapter(getActivity());

        View footerView = View.inflate(getActivity(), R.layout.hlj_foot_no_more___qa, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);

        adapter.setFooterView(footerView);

        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(
                            RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (layoutManager != null && layoutManager.findFirstVisibleItemPosition()
                                < 10) {
                            if (!isHide) {
                                hideFiltrateAnimation();
                            }
                        } else if (isHide) {
                            if (backScrollTop.getVisibility() == View.GONE) {
                                backScrollTop.setVisibility(View.VISIBLE);
                            }
                            showFiltrateAnimation();
                        }
                    }
                });
        backScrollTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollTop();
            }
        });
    }

    private void initLoad() {
        refresh();
    }

    private void initPageHotQA(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<RecQaWrappers>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<RecQaWrappers>>>() {
                    @Override
                    public Observable<HljHttpData<List<RecQaWrappers>>> onNextPage(
                            int page) {
                        Log.d("pagination tool", "on load: " + page);
                        if (type == NEW_QUESTION) {
                            if (lastAnswerTime != 0) {
                                url = String.format(ALL_QA_LAST_URL, lastAnswerTime);
                            } else {
                                url = String.format(ALL_QA_URL);
                            }
                        }
                        return QuestionAnswerApi.getMerchantQuestionObb(url, page);

                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        pageSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<RecQaWrappers>>>
                        () {
                    @Override
                    public void onNext(HljHttpData<List<RecQaWrappers>> data) {
                        recQaAnswers.addAll(data.getData());
                        adapter.setData(recQaAnswers);
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }


    @Override
    public void refresh(Object... params) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                    .setContentView(recyclerView)
                    .setEmptyView(emptyView)
                    .setPullToRefreshBase(recyclerView)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<RecQaWrappers>>>() {
                        @Override
                        public void onNext(
                                HljHttpData<List<RecQaWrappers>> data) {
                            initPageHotQA(data.getPageCount());
                            recQaAnswers.clear();
                            recQaAnswers.addAll(data.getData());
                            //最新问题页，存储lastAnswerTime
                            if (type == NEW_QUESTION && recQaAnswers.size() > 0) {
                                if (recQaAnswers.get(0)
                                        .getLastAnswerTime() != null) {
                                    lastAnswerTime = recQaAnswers.get(0)
                                            .getLastAnswerTime()
                                            .getMillis();
                                }
                            }
                            adapter.setData(recQaAnswers);
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .build();
            QuestionAnswerApi.getMerchantQuestionObb(url, 1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .distinctUntilChanged()
                    .subscribe(refreshSubscriber);
        }
    }


    public void scrollTop() {
        if (layoutManager == null) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber.unsubscribe();
        }
    }

    private void showFiltrateAnimation() {
        if (backScrollTop == null) {
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
            backScrollTop.startAnimation(animation);
        }
    }

    private boolean isAnimEnded() {
        return backScrollTop != null && (backScrollTop.getAnimation() == null || backScrollTop
                .getAnimation()
                .hasEnded());
    }

    private void hideFiltrateAnimation() {
        if (backScrollTop == null) {
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
            backScrollTop.startAnimation(animation);
        }
    }

    @Override
    public View getScrollableView() {
        return recyclerView.getRefreshableView();
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        refresh();
    }
}
