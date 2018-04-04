package com.hunliji.hljquestionanswer.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.BannerJumpService;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.MarkFlowLayout;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpCountData;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.activities.LastQuestionListActivity;
import com.hunliji.hljquestionanswer.activities.QAMarkDetailActivity;
import com.hunliji.hljquestionanswer.activities.WeekQaActivity;
import com.hunliji.hljquestionanswer.adapters.AnswerAdapter;
import com.hunliji.hljcommonviewlibrary.adapters.FlowAdapter;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.makeramen.rounded.RoundedImageView;
import com.slider.library.Indicators.CirclePageExIndicator;
import com.slider.library.SliderLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func4;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2016/9/12.社区首页-问答列表
 */
public class SocialQuestionAnswerFragment extends ScrollAbleFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.btn_scroll_top)
    ImageButton backScrollTop;

    private View endView;
    private View loadView;
    private boolean isHide;
    private Handler mHandler;
    private Unbinder unbinder;
    private City city;
    private User user;

    private LinearLayoutManager layoutManager;
    private AnswerAdapter adapter;
    private ArrayList<Answer> answers;
    private FlowAdapter flowAdapter;
    private ArrayList<Poster> posters;
    private Poster headLinePosters;
    private ArrayList<Mark> marks;
    private int markCount;//第一屏mark显示个数
    private boolean isFirstMark;//是否为第一屏
    private int bannerHeight;
    private String appVersion;
    private Mark localMark;
    private View headerView;
    private HeaderViewHolder headerViewHolder;
    private int imgWidth;
    private int imgHeight;
    private long lastRefreshTime;

    private HljHttpSubscriber refreshSubscriber;//刷新
    private HljHttpSubscriber pageSubscriber;//分页

    public static SocialQuestionAnswerFragment newInstance() {
        SocialQuestionAnswerFragment fragment = new SocialQuestionAnswerFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValue();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_social_question_answer___qa, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initView();
        initTracker();
        return rootView;
    }

    private void initTracker(){
        HljVTTagger.tagViewParentName(recyclerView,"question_list");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLoad();
    }

    private void initValue() {
        bannerHeight = CommonUtil.getDeviceSize(getContext()).x * 5 / 16;
        imgWidth = (CommonUtil.getDeviceSize(getContext()).x - CommonUtil.dp2px(getContext(),
                34)) / 2;
        imgHeight = imgWidth * 7 / 16;
        city = LocationSession.getInstance()
                .getCity(getActivity());
        posters = new ArrayList<>();
        user = UserSession.getInstance()
                .getUser(getActivity());
        marks = new ArrayList<>();
        mHandler = new Handler();
        answers = new ArrayList<>();
        try {
            appVersion = getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        headerView = getActivity().getLayoutInflater()
                .inflate(R.layout.social_question_answer_header___qa, null);
        headerViewHolder = new HeaderViewHolder(headerView);
        //顶部广告位
        flowAdapter = new FlowAdapter(getActivity(), posters, 6, R.layout.flow_item___cv);
        headerViewHolder.viewFlow.setOverScrollMode(View.OVER_SCROLL_NEVER);
        headerViewHolder.viewFlow.getLayoutParams().height = bannerHeight;
        headerViewHolder.viewFlow.setPagerAdapter(flowAdapter);
        flowAdapter.setSliderLayout(headerViewHolder.viewFlow);
        headerViewHolder.viewFlow.setCustomIndicator(headerViewHolder.flowIndicator);
        if (flowAdapter.getCount() > 0) {
            headerViewHolder.bannerLayout.setVisibility(View.VISIBLE);
            if (flowAdapter.getCount() > 1) {
                headerViewHolder.viewFlow.startAutoCycle();
            }
        }
        headerViewHolder.viewFlow.setOnPageChangeListener(new ViewPager
                .SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
        //中间方块区域
        headerViewHolder.ivQaHeadline.getLayoutParams().height = imgHeight;
        headerViewHolder.ivNewQuestion.getLayoutParams().height = imgHeight;
        headerViewHolder.ivRecommendMark.getLayoutParams().height = imgHeight;
        headerViewHolder.ivWeekHot.getLayoutParams().height = imgHeight;

        //本周最受欢迎
        headerViewHolder.ivWeekHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), WeekQaActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        });

        //最新问题
        headerViewHolder.ivNewQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), LastQuestionListActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        });

        //网络异常,可点击屏幕重新加载
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                recyclerView.setRefreshing(true);
            }
        });
        emptyView.setOnEmptyClickListener(new HljEmptyView.OnEmptyClickListener() {
            @Override
            public void onEmptyClickListener() {
                recyclerView.setRefreshing(true);
            }
        });

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        adapter = new AnswerAdapter(getActivity(), answers);

        View footerView = View.inflate(getActivity(), R.layout.hlj_foot_no_more___qa, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);

        adapter.setHeaderView(headerView);
        adapter.setFooterView(footerView);

        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(
                            RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
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

    @Override
    public void refresh(Object... params) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            city = LocationSession.getInstance()
                    .getCity(getActivity());
            Observable<PosterData> bObservable = QuestionAnswerApi.getQaBannerObb(HljCommon
                            .BLOCK_ID.QuestionAnswerFragment,
                    appVersion,
                    city.getCid());
            Observable<PosterData> hObservable = QuestionAnswerApi.getQaBannerObb(HljCommon
                            .BLOCK_ID.QuestionHeadlineFragment,
                    appVersion,
                    city.getCid());
            Observable<HljHttpCountData<List<Mark>>> mObservable = QuestionAnswerApi.getMarkListObb(
                    1,
                    100,
                    city.getCid());

            Observable<HljHttpData<List<Answer>>> aObservable = QuestionAnswerApi.getHotQaAnswerObb(
                    1);

            Observable observable = Observable.zip(bObservable,
                    mObservable,
                    aObservable,
                    hObservable,
                    new Func4<PosterData, HljHttpCountData<List<Mark>>,
                            HljHttpData<List<Answer>>, PosterData, ResultZip>() {
                        @Override
                        public ResultZip call(
                                PosterData posterData,
                                HljHttpCountData<List<Mark>> listHljHttpData,
                                HljHttpData<List<Answer>> listHljHttpData1,
                                PosterData posterData1) {
                            ResultZip resultZip = new ResultZip();
                            resultZip.posterData = posterData;
                            resultZip.marks = listHljHttpData;
                            resultZip.answes = listHljHttpData1;
                            resultZip.posterData1 = posterData1;
                            return resultZip;
                        }
                    });
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {

                        @Override
                        public void onNext(ResultZip resultZip) {
                            //记录最后的刷新时间
                            lastRefreshTime = Calendar.getInstance()
                                    .getTimeInMillis();
                            //banner
                            List<Poster> posterList = PosterUtil.getPosterList(resultZip
                                            .posterData.getFloors(),
                                    HljCommon.POST_SITES.SITE_USER_ASQUESTION_BANNER,
                                    false);
                            posters.clear();
                            posters.addAll(posterList);
                            //问答头条
                            List<Poster> posterList1 = PosterUtil.getPosterList(resultZip
                                            .posterData1.getFloors(),
                                    HljCommon.POST_SITES.USER_QA_HEADLINE,
                                    false);
                            if (posterList1 != null && posterList1.size() > 0) {
                                headLinePosters = posterList1.get(0);
                            }
                            //标签
                            marks.clear();
                            if (resultZip.marks.getData() != null) {
                                marks.addAll(resultZip.marks.getData());
                            }
                            if (resultZip.marks.getLocalMark() != null) {
                                localMark = resultZip.marks.getLocalMark();
                            } else {
                                localMark = null;
                            }
                            isFirstMark = true;
                            setHeaderView();
                            //列表
                            int pageCount = 0;
                            if (resultZip.answes != null) {
                                pageCount = resultZip.answes.getPageCount();
                                answers.clear();
                                answers.addAll(resultZip.answes.getData());
                                adapter.notifyDataSetChanged();
                            }
                            initPageQA(pageCount);
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(refreshSubscriber);
        }
    }

    private void initPageQA(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<Answer>>> pageObservable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Answer>>>() {
                    @Override
                    public Observable<HljHttpData<List<Answer>>> onNextPage(
                            int page) {
                        Log.d("pagination tool", "on load: " + page);
                        return QuestionAnswerApi.getHotQaAnswerObb(page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        pageSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Answer>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Answer>> data) {
                        answers.addAll(data.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    private void setPosterView() {
        flowAdapter.setmDate(posters);
        if (headerViewHolder.bannerLayout != null && headerViewHolder.viewFlow != null) {
            if (flowAdapter.getCount() == 0 || posters.size() == 0) {
                headerViewHolder.viewFlow.stopAutoCycle();
                headerViewHolder.bannerLayout.setVisibility(View.GONE);
            } else {
                headerViewHolder.bannerLayout.setVisibility(View.VISIBLE);
                if (flowAdapter.getCount() > 1) {
                    headerViewHolder.viewFlow.startAutoCycle();
                } else {
                    headerViewHolder.viewFlow.stopAutoCycle();
                }
            }
        }
        flowAdapter.notifyDataSetChanged();
    }

    //设置标签视图
    public void setFlowLayout(final List<Mark> items) {
        headerViewHolder.switchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSwitch();
            }
        });
        headerViewHolder.markFlow.removeAllViews();
        headerViewHolder.markFlow.setMaxLineCount(3);
        for (int i = 0; i < items.size(); i++) {
            final Mark item = items.get(i);
            TextView markText = (TextView) getActivity().getLayoutInflater()
                    .inflate(R.layout.question_hot_mark_list_item___qa,
                            headerViewHolder.markFlow,
                            false);
            markText.setText(item.getName());
            markText.setTag(item);
            //热标文字显示为红色
            if (items.get(i)
                    .isHighLight()) {
                markText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            } else {
                markText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack3));
            }
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT,
                    CommonUtil.dp2px(getContext(), 28));
            markText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("id", item.getId());
                    intent.putExtra("isShowHot", true);
                    intent.setClass(getContext(), QAMarkDetailActivity.class);
                    startActivity(intent);
                }
            });
            headerViewHolder.markFlow.addView(markText, params);
        }
        headerViewHolder.markFlow.post(new Runnable() {
            @Override
            public void run() {
                markCount = headerViewHolder.markFlow.getTotalCount();
                if (markCount < items.size() || !isFirstMark) {
                    headerViewHolder.switchView.setVisibility(View.VISIBLE);
                } else {
                    headerViewHolder.switchView.setVisibility(View.GONE);
                }
            }
        });
    }

    //设置中间方块区域
    private void setHeaderView() {
        headerViewHolder.headerView.setVisibility(View.VISIBLE);
        setFlowLayout(marks);
        setPosterView();
        if (headLinePosters != null && localMark != null) {
            headerViewHolder.recommendView.setVisibility(View.VISIBLE);
            //问答头条
            String url = ImageUtil.getImagePath(headLinePosters.getPath(), imgWidth);
            Glide.with(getContext())
                    .load(url)
                    .apply(new RequestOptions().dontAnimate())
                    .into(headerViewHolder.ivQaHeadline);
            headerViewHolder.ivQaHeadline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 使用Name索引寻找ARouter中已注册的对应服务
                    BannerJumpService bannerJumpService = (BannerJumpService) ARouter.getInstance()
                            .build(RouterPath.ServicePath.BANNER_JUMP)
                            .navigation();
                    if (bannerJumpService != null) {
                        bannerJumpService.bannerJump(getContext(), headLinePosters, null);
                    }
                }
            });
            //推荐标签，有本地标签取本地标签，没有取旅拍
            String url1 = ImageUtil.getImagePath(localMark.getImagePath(), imgWidth);
            Glide.with(getContext())
                    .load(url1)
                    .apply(new RequestOptions().dontAnimate())
                    .into(headerViewHolder.ivRecommendMark);
            headerViewHolder.tvRecommendMark.setText(localMark.getName());
            headerViewHolder.ivRecommendMark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), QAMarkDetailActivity.class);
                    intent.putExtra("id", localMark.getId());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            });
        } else {
            headerViewHolder.recommendView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        refresh();
    }

    public void onSwitch() {
        if (isFirstMark) {
            markCount = headerViewHolder.markFlow.getTotalCount();
            if (markCount >= marks.size() || markCount == 0) {
                return;
            }
            isFirstMark = false;
            ArrayList<Mark> otherMarks = new ArrayList<>();
            for (int i = markCount - 1; i < marks.size(); i++) {
                otherMarks.add(marks.get(i));
            }
            setFlowLayout(otherMarks);
        } else {
            isFirstMark = true;
            setFlowLayout(marks);
        }
    }

    /**
     * 选择城市之后，刷新数据
     */
    public void cityRefresh() {
        if (flowAdapter != null) {
            flowAdapter.setCity(city);
        }
        refresh();
        scrollTop();
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

    /**
     * 退出登录或者切换用户之后，刷新数据
     *
     * @param userId
     */
    public void userRefresh(long userId) {
        if (userId != 0) {
            if (user != null) {
                if (userId != (user.getId())) {
                    refresh();
                }
            }
        } else {
            refresh();
        }
    }

    @Override
    public View getScrollableView() {
        return recyclerView == null ? null : recyclerView.getRefreshableView();
    }

    private static class ResultZip extends HljHttpResultZip {
        @HljRZField
        PosterData posterData;
        @HljRZField
        HljHttpCountData<List<Mark>> marks;
        @HljRZField
        HljHttpData<List<Answer>> answes;
        @HljRZField
        PosterData posterData1;
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
    public void onResume() {
        super.onResume();
        long nowTime = Calendar.getInstance()
                .getTimeInMillis();
        if (lastRefreshTime != 0 && (nowTime - lastRefreshTime) > 60 * 60 * 1000) {
            recyclerView.setRefreshing(true);
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
        return backScrollTop != null && (backScrollTop.getAnimation() == null || backScrollTop.getAnimation()
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

    static class HeaderViewHolder {
        @BindView(R2.id.view_flow)
        SliderLayout viewFlow;
        @BindView(R2.id.flow_indicator)
        CirclePageExIndicator flowIndicator;
        @BindView(R2.id.banner_layout)
        RelativeLayout bannerLayout;
        @BindView(R2.id.iv_switch)
        ImageView ivSwitch;
        @BindView(R2.id.switch_view)
        LinearLayout switchView;
        @BindView(R2.id.line_layout)
        View lineLayout;
        @BindView(R2.id.mark_flow)
        MarkFlowLayout markFlow;
        @BindView(R2.id.iv_recommend_mark)
        RoundedImageView ivRecommendMark;
        @BindView(R2.id.tv_recommend_mark)
        TextView tvRecommendMark;
        @BindView(R2.id.iv_qa_headline)
        RoundedImageView ivQaHeadline;
        @BindView(R2.id.recommend_view)
        LinearLayout recommendView;
        @BindView(R2.id.iv_week_hot)
        RoundedImageView ivWeekHot;
        @BindView(R2.id.iv_new_question)
        RoundedImageView ivNewQuestion;
        @BindView(R2.id.header_view)
        LinearLayout headerView;

        HeaderViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
