package me.suncloud.marrymemo.fragment.work_case;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljchatlibrary.utils.ChatUtil;
import com.hunliji.hljcommonlibrary.behavior.AppBarLayoutOverScrollViewBehavior;
import com.hunliji.hljcommonlibrary.models.CasePhoto;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.ServiceCommentMark;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantChatData;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.OverScrollViewPager;
import com.hunliji.hljcommonlibrary.views.widgets.OverscrollContainer;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.models.HljHttpQuestion;
import com.hunliji.hljquestionanswer.models.QARxEvent;
import com.makeramen.rounded.RoundedImageView;
import com.slider.library.Indicators.CirclePageIndicator;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.WorkItemAdapter;
import me.suncloud.marrymemo.adpter.work_case.WorkDetailAdapter;
import me.suncloud.marrymemo.adpter.work_case.viewholder.WorkDetailInfoViewHolder;
import me.suncloud.marrymemo.api.comment.CommentApi;
import me.suncloud.marrymemo.api.merchant.MerchantApi;
import me.suncloud.marrymemo.api.work_case.WorkApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.Item;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.model.merchant.wrappers.MerchantChatLinkTriggerPostBody;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.ViewHelper;
import me.suncloud.marrymemo.util.merchant.ChatBubbleTimer;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.work_case.WorkMediaImageActivity;
import me.suncloud.marrymemo.widget.TabPageIndicator;
import me.suncloud.marrymemo.widget.merchant.MerchantCouponDialog;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.functions.Func3;

/**
 * Created by luohanlin on 2017/12/21.
 * 套餐详情
 */

public class WorkDetailFragment extends RefreshFragment implements WorkItemAdapter
        .OnItemClickListener, OverscrollContainer.OnLoadListener, WorkDetailAdapter
        .OnTabSelectedListener, TabPageIndicator.OnTabChangeListener, WorkDetailInfoViewHolder
        .OnInfoListener {

    // 页面滑动距离触发轻松聊
    public static final int SCROLL_Y_DELTA = 1300;
    // 页面滑动距离触发轻松聊public static final int SCROLL_Y_DELTA = 1300;
    public static final int TIME_STAYED_IN_PAGE = MerchantChatLinkTriggerPostBody
            .TIME_TILL_SHOWING * 1000;
    // 页面停留时间触发轻松聊
    public static final int TIME_HIDE_AFTER = MerchantChatLinkTriggerPostBody.TIME_TILL_HIDE * 1000;

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.btn_share)
    ImageButton btnShare;
    @BindView(R.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R.id.btn_back2)
    ImageButton btnBack2;
    @BindView(R.id.tab_indicator)
    TabPageIndicator tabIndicator;
    @BindView(R.id.btn_share2)
    ImageButton btnShare2;
    @BindView(R.id.action_holder_layout2)
    LinearLayout actionHolderLayout2;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.back_top_btn)
    ImageButton backTopBtn;
    @BindView(R.id.img_logo)
    RoundedImageView imgLogo;
    @BindView(R.id.tv_bubble_msg)
    TextView tvBubbleMsg;
    @BindView(R.id.chat_click_layout)
    RelativeLayout chatClickLayout;
    @BindView(R.id.iv_bubble_arrow)
    ImageView ivBubbleArrow;
    @BindView(R.id.chat_bubble_layout)
    LinearLayout chatBubbleLayout;
    @BindView(R.id.scroll_view_pager)
    OverScrollViewPager scrollViewPager;
    @BindView(R.id.pager_indicator)
    CirclePageIndicator pagerIndicator;
    @BindView(R.id.shadow_view)
    FrameLayout shadowView;
    @BindView(R.id.btn_msg)
    ImageButton btnMsg;
    @BindView(R.id.btn_msg2)
    ImageButton btnMsg2;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.layout_header)
    RelativeLayout layoutHeader;

    Unbinder unbinder;
    private Work work;
    private boolean isSnapshot;
    private ArrayList<Item> mediaItems;
    private int itemsHeight;
    private ChatBubbleTimer bubbleTimer;
    private WorkItemAdapter itemAdapter;
    private WorkDetailAdapter detailAdapter;
    private ProgressBar progressBar;
    private LinearLayoutManager layoutManager;
    private int verticalOffset;
    private int scrollStartDelta;
    private HljHttpSubscriber chatTrigSub;
    private HljHttpSubscriber recommendSub;
    private HljHttpSubscriber chatDataSub;
    private HljHttpSubscriber cqZipSub;
    private Subscription rxBusEventSub;
    private MerchantCouponDialog couponDialog;
    private StickyRecyclerHeadersDecoration headersDecor;

    public static WorkDetailFragment newInstance(Work work, boolean isSnapShot, String jsonString) {
        Bundle args = new Bundle();
        WorkDetailFragment fragment = new WorkDetailFragment();
        args.putSerializable("work", work);
        args.putBoolean("is_snapshot", isSnapShot);
        args.putString("json_string", jsonString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValues();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_work_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initViews();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLoad();
        initBubbleTimer();
        initTracker();
    }

    private void initValues() {
        itemsHeight = Math.round(CommonUtil.getDeviceSize(getContext()).x * 3 / 4);

        if (getArguments() != null) {
            work = (Work) getArguments().getSerializable("work");
            isSnapshot = getArguments().getBoolean("is_snapshot", false);
            String jsonString = getArguments().getString("json_string");

            mediaItems = new ArrayList<>();
            itemAdapter = new WorkItemAdapter(getActivity(), mediaItems, true);
            itemAdapter.setOnItemClickListener(this);
            parseMediaItems(jsonString);

            detailAdapter = new WorkDetailAdapter(getContext(), work, isSnapshot);
            detailAdapter.setOnTabSelectedListener(this);
            detailAdapter.setOnInfoListener(this);

            registerRxBusEvent();
        }
    }

    @SuppressLint("ResourceType")
    private void initViews() {
        setWorkInfo();
        scrollViewPager.setHintStringStart("滑动查看图文详情");
        scrollViewPager.setHintStringEnd("释放查看图文详情");
        scrollViewPager.setArrowImageResId(R.drawable.icon_arrow_left_round_gray);
        scrollViewPager.setHintTextColorResId(R.color.colorBlack3);

        scrollViewPager.setOverable(true);
        scrollViewPager.setOnLoadListener(this);
        scrollViewPager.getOverscrollView()
                .setAdapter(itemAdapter);
        scrollViewPager.getLayoutParams().height = itemsHeight;
        actionHolderLayout.setPadding(0, HljBaseActivity.getStatusBarHeight(getContext()), 0, 0);
        actionHolderLayout2.setPadding(0, HljBaseActivity.getStatusBarHeight(getContext()), 0, 0);
        pagerIndicator.setViewPager(scrollViewPager.getOverscrollView());
        tabIndicator.setTabViewId(R.layout.menu_community_tab_widget);
        tabIndicator.setPagerAdapter(new TabPagerAdapter(getChildFragmentManager()));
        tabIndicator.setOnTabChangeListener(this);
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int vo) {
                if (appbar == null) {
                    return;
                }
                WorkDetailFragment.this.verticalOffset = vo;
                if (Math.abs(vo) >= appbar.getTotalScrollRange()) {
                    actionHolderLayout2.setAlpha(1);
                    shadowView.setAlpha(0);
                } else {
                    float alpha = (float) Math.abs(vo) / appbar.getTotalScrollRange();
                    actionHolderLayout2.setAlpha(alpha);
                    shadowView.setAlpha(1 - alpha);
                    tabIndicator.setCurrentItem(0);
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (appbar.getTotalScrollRange() > Math.abs(verticalOffset)) {
                    tabIndicator.setCurrentItem(0);
                    return;
                }
                if (scrollStartDelta > 0 && bubbleTimer != null) {
                    scrollStartDelta -= dy;
                    if (scrollStartDelta <= 0) {
                        bubbleTimer.overScrollDelta();
                    }
                }
                onScrollTabChange();
            }
        });
        try {
            AppBarLayoutOverScrollViewBehavior overScrollViewBehavior =
                    (AppBarLayoutOverScrollViewBehavior) ((CoordinatorLayout.LayoutParams) appbar
                            .getLayoutParams()).getBehavior();
            if (overScrollViewBehavior != null) {
                overScrollViewBehavior.setMaxOverScroll(itemsHeight * 7 / 10);
                overScrollViewBehavior.addOnOverScrollListener(new AppBarLayoutOverScrollViewBehavior.OnOverScrollListener() {
                    @Override
                    public void onOverScrollBy(int height, int overScroll) {
                        if(layoutHeader==null){
                            return;
                        }
                        layoutHeader.getLayoutParams().height=height+overScroll;
                        layoutHeader.requestLayout();
                        ViewHelper.setPivotX(scrollViewPager, scrollViewPager.getWidth() / 2);
                        ViewHelper.setPivotY(scrollViewPager, 0);
                        scrollViewPager.setScaleY(1 + ((float) overScroll) / itemsHeight);
                        scrollViewPager.setScaleX(1 + ((float) overScroll) / itemsHeight);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onScrollTabChange() {
        int currentPosition = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0));
        if (currentPosition >= detailAdapter.getDetailStartIndex()) {
            backTopBtn.setVisibility(View.VISIBLE);
            tabIndicator.setCurrentItem(2);
        } else if (currentPosition >= detailAdapter.getCommentIndex()) {
            tabIndicator.setCurrentItem(1);
            backTopBtn.setVisibility(View.GONE);
        } else {
            backTopBtn.setVisibility(View.GONE);
            tabIndicator.setCurrentItem(0);
        }
    }

    private void initLoad() {
        progressBar = getWorkActivity().getProgressBar();
        progressBar.setVisibility(View.GONE);
        loadCommentsAndQuestionAndCasePhoto();
        if (!isSnapshot) {
            loadRecommendMeals();
        }
        if (getFragmentManager() != null && getWorkActivity() != null) {
            getFragmentManager().addOnBackStackChangedListener(new FragmentManager
                    .OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    if (getFragmentManager().getBackStackEntryCount() == 0) {
                        getWorkActivity().setSwipeBackEnable(true);
                    } else {
                        getWorkActivity().setSwipeBackEnable(false);
                    }
                }
            });
        }
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(QARxEvent.class)
                    .subscribe(new RxBusSubscriber<QARxEvent>() {
                        @Override
                        protected void onEvent(QARxEvent qaRxEvent) {
                            switch (qaRxEvent.getType()) {
                                case ASK_QUESTION_SUCCESS:
                                    loadCommentsAndQuestionAndCasePhoto();
                                    break;
                            }
                        }
                    });
        }
    }

    private void initBubbleTimer() {
        // 启动轻松聊计时器
        if (bubbleTimer != null) {
            bubbleTimer.cancel();
            bubbleTimer = null;
        }
        scrollStartDelta = SCROLL_Y_DELTA - appbar.getTotalScrollRange();
        bubbleTimer = new ChatBubbleTimer(TIME_STAYED_IN_PAGE,
                false,
                new ChatBubbleTimer.ShowBubbleCallBack() {
                    @Override
                    public void toggleBubble(boolean isShow) {
                        showChatBubble(isShow);
                    }
                });
        bubbleTimer.start();
    }

    private void initTracker() {
        if (work == null) {
            return;
        }
        HljVTTagger.buildTagger(chatBubbleLayout)
                .tagName("free_chat_bubble")
                .dataId(work.getId())
                .dataType("Package")
                .tag();
    }


    private void loadCommentsAndQuestionAndCasePhoto() {
        cqZipSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setDataNullable(true)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<WorkCommentsQuestionsCasePhotosZip>() {

                    @Override
                    public void onNext(
                            WorkCommentsQuestionsCasePhotosZip workCommentsQuestionsCasePhotosZip) {
                        detailAdapter.setCqZip(workCommentsQuestionsCasePhotosZip);
                    }
                })
                .build();

        Observable<HljHttpData<List<ServiceCommentMark>>> commentObb = CommentApi
                .getServiceCommentMarksObb(
                work.getMerchant()
                        .getId());
        Observable<HljHttpQuestion<List<Question>>> questionObb = QuestionAnswerApi.getQAList
                (work.getMerchant()
                .getId(), 1, 1);
        Observable<HljHttpData<List<CasePhoto>>> casePhotoObb = Observable.just(work.getPropertyId())
                .flatMap(new Func1<Long, Observable<HljHttpData<List<CasePhoto>>>>() {
                    @Override
                    public Observable<HljHttpData<List<CasePhoto>>> call(Long propertyId) {
                        if(propertyId==Merchant.PROPERTY_WEDDING_DRESS_PHOTO){
                            return WorkApi.getCasePhotoList(work.getId());
                        }
                        return Observable.just(null);
                    }
                });

        Observable.zip(commentObb,
                questionObb,
                casePhotoObb,
                new Func3<HljHttpData<List<ServiceCommentMark>>, HljHttpQuestion<List<Question>>,
                        HljHttpData<List<CasePhoto>>, Object>() {
                    @Override
                    public Object call(
                            HljHttpData<List<ServiceCommentMark>> listHljHttpData,
                            HljHttpQuestion<List<Question>> question,
                            HljHttpData<List<CasePhoto>> caseHttpData) {
                        WorkCommentsQuestionsCasePhotosZip zip = new
                                WorkCommentsQuestionsCasePhotosZip();
                        zip.httpCommentMark = listHljHttpData;
                        zip.httpQuestion = question;
                        zip.casePhotoList = caseHttpData;
                        return zip;
                    }
                })
                .subscribe(cqZipSub);
    }

    public void loadRecommendMeals() {
        City city = Session.getInstance()
                .getMyCity(getContext());
        recommendSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<List<com.hunliji.hljcommonlibrary
                        .models.Work>>() {
                    @Override
                    public void onNext(List<com.hunliji.hljcommonlibrary.models.Work> works) {
                        detailAdapter.setRecommendWorks(works);
                    }
                })
                .build();
        WorkApi.getRecommendsMeals(city.getId(), work.getId())
                .subscribe(recommendSub);
    }

    /**
     * 套餐信息不需要加载，直接设置
     */
    private void setWorkInfo() {
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        headersDecor = new StickyRecyclerHeadersDecoration(detailAdapter);
        recyclerView.addItemDecoration(headersDecor);
        detailAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });
        StickyRecyclerHeadersTouchListener touchListener = new StickyRecyclerHeadersTouchListener(
                recyclerView,
                headersDecor);
        touchListener.setOnHeaderClickListener(new StickyRecyclerHeadersTouchListener
                .OnHeaderClickListener() {
            @Override
            public void onHeaderClick(View header, int position, long headerId) {
                Log.d("Sticky", "OnHeaderClicked");
            }
        });
        recyclerView.addOnItemTouchListener(touchListener);
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(detailAdapter);
    }

    private void parseMediaItems(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray array = jsonObject.optJSONArray("media_items");
            if (array != null && array.length() > 0) {
                for (int i = 0, size = array.length(); i < size; i++) {
                    Item item = new Item(array.optJSONObject(i));
                    if (!JSONUtil.isEmpty(item.getMediaPath())) {
                        item.setType(Constants.ItemType.WorkItem);
                        mediaItems.add(item);
                    }
                }
                itemAdapter.setmDate(mediaItems);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showChatBubble(boolean isShow) {
        if (!isShow) {
            chatBubbleLayout.setVisibility(View.GONE);
            return;
        }
        // 如果用户在今天之内联系过商家，则不再显示气泡
        User user = Session.getInstance()
                .getCurrentUser(getContext());
        if (user != null && ChatUtil.hasChatWithMerchant(user.getId(),
                work.getMerchant()
                        .getUserId())) {
            return;
        }
        // 触发轻松聊事件，触发接口和加载内容
        chatDataSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<MerchantChatData>() {
                    @Override
                    public void onNext(MerchantChatData merchantChatData) {
                        if (!TextUtils.isEmpty(merchantChatData.getPackageSpeech())) {
                            bubbleTimer.cancel();
                            bubbleTimer = new ChatBubbleTimer(TIME_HIDE_AFTER,
                                    true,
                                    new ChatBubbleTimer.ShowBubbleCallBack() {
                                        @Override
                                        public void toggleBubble(boolean isShow) {
                                            showChatBubble(isShow);
                                        }
                                    });
                            bubbleTimer.start();

                            chatBubbleLayout.setVisibility(View.VISIBLE);
                            Glide.with(getContext())
                                    .load(ImagePath.buildPath(work.getMerchant()
                                            .getLogoPath())
                                            .width(CommonUtil.dp2px(getContext(), 38))
                                            .height(CommonUtil.dp2px(getContext(), 38))
                                            .cropPath())
                                    .into(imgLogo);
                            tvBubbleMsg.setText(merchantChatData.getPackageSpeech());
                        }
                    }
                })
                .build();
        MerchantApi.getMerchantChatData(work.getMerchant()
                .getId())
                .subscribe(chatDataSub);
    }

    private WorkActivity getWorkActivity() {
        WorkActivity activity = null;
        if (getActivity() instanceof WorkActivity) {
            activity = (WorkActivity) getActivity();
        }
        return activity;
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CommonUtil.unSubscribeSubs(chatTrigSub, recommendSub, chatDataSub, cqZipSub);
        unbinder.unbind();
        if (bubbleTimer != null) {
            bubbleTimer.cancel();
            bubbleTimer = null;
        }
    }

    /**
     * 顶部图片点击事件
     *
     * @param item
     * @param position
     */
    @Override
    public void OnItemClickListener(Object item, int position) {
        Item t = (Item) item;
        if (t != null) {
            Intent intent = new Intent(getActivity(), WorkMediaImageActivity.class);
            intent.putExtra("items", mediaItems);
            intent.putExtra("work", work);
            intent.putExtra("position", mediaItems.indexOf(t));
            startActivityForResult(intent, Constants.RequestCode.WORK_MEDIA_ITEM_IMAGE);
        }
    }

    /**
     * 顶部图片最后一页滑动事件
     */
    @Override
    public void onLoad() {
        selectDetailTabInside(0);
    }


    /**
     * 顶部的，套餐，评价，详情三个tab切换
     *
     * @param position
     */
    @Override
    public void onTabChanged(final int position) {
        final int offsetPosition;
        final int offset;
        switch (position) {
            case 1:
                offsetPosition = detailAdapter.getCommentIndex();
                offset = -CommonUtil.dp2px(getContext(), 10);
                break;
            case 2:
                offsetPosition = detailAdapter.getDetailStartIndex();
                offset = -CommonUtil.dp2px(getContext(), 10);
                break;
            default:
                offsetPosition = 0;
                offset = 0;
                break;
        }

        appbar.setExpanded(position == 0);
        layoutManager.scrollToPositionWithOffset(offsetPosition, offset);
    }

    @OnClick(R.id.chat_click_layout)
    void onChatBubbleClick() {
        bubbleTimer.cancel();
        chatBubbleLayout.setVisibility(View.GONE);
        if (getWorkActivity() != null) {
            getWorkActivity().onContact();
        }
        chatTrigSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                    }
                })
                .build();
        MerchantApi.postMerchantChatLinkTrigger(work.getMerchant()
                .getId(), MerchantChatLinkTriggerPostBody.MerchantChatLinkType.TYPE_PACKAGE, null)
                .subscribe(chatTrigSub);
    }

    @OnClick(R.id.back_top_btn)
    void onBackToTop() {
        appbar.setExpanded(true);
        layoutManager.scrollToPositionWithOffset(0, 0);
    }

    @OnClick({R.id.btn_back, R.id.btn_back2})
    void onBack() {
        if (getWorkActivity() != null) {
            getWorkActivity().onBackPressed();
        }
    }

    @OnClick({R.id.btn_share, R.id.btn_share2})
    void onShare(View view) {
        if (getWorkActivity() != null) {
            getWorkActivity().onShare(view);
        }
    }

    @OnClick({R.id.btn_msg, R.id.btn_msg2})
    void onMsgHome(View view) {
        if (getWorkActivity() != null) {
            getWorkActivity().onMessage(view);
        }
    }

    /**
     * 随列表滚动的"图文介绍"和"服务内容"的切换
     *
     * @param index
     */
    @Override
    public void onTabSelected(int index) {
        selectDetailTabInside(index);
    }

    private void selectDetailTabInside(int position) {
        detailAdapter.setTypeDetailTab(position);
        detailAdapter.notifyDataSetChanged();
        onTabChanged(2);
    }

    @Override
    public void onServiceInfo() {
        tabIndicator.setCurrentItem(detailAdapter.getDetailStartIndex());
        onTabChanged(detailAdapter.getDetailStartIndex());
        selectDetailTabInside(1);
    }

    @Override
    public void onCoupon() {
        if (couponDialog != null && couponDialog.isShowing()) {
            return;
        }
        if (couponDialog == null) {
            couponDialog = new MerchantCouponDialog(getContext(),
                    work.getMerchant()
                            .getId(),
                    work.getMerchant()
                            .getUserId());
        }
        couponDialog.getCoupons();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.WORK_MEDIA_ITEM_IMAGE:
                    if (data.getBooleanExtra(WorkMediaImageActivity.ARG_IS_LOAD, false)) {
                        onLoad();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class TabPagerAdapter extends FragmentStatePagerAdapter {
        TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1:
                    return "评价";
                case 2:
                    return "详情";
                default:
                    return "套餐";
            }
        }
    }

    public static class WorkCommentsQuestionsCasePhotosZip {
        public HljHttpData<List<ServiceCommentMark>> httpCommentMark;
        public HljHttpQuestion<List<Question>> httpQuestion;
        public HljHttpData<List<CasePhoto>> casePhotoList;
    }
}
