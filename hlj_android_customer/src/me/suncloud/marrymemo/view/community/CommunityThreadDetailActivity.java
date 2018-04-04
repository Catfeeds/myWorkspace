package me.suncloud.marrymemo.view.community;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.views.widgets.CustomWebView;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityAuthor;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPost;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPostPraiseResult;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.communitythreads.WeddingPhotoItem;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.SystemNotificationUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearGroup;
import com.hunliji.hljcommonlibrary.views.widgets.CopyPopupWindow;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;
import com.hunliji.hljcommonlibrary.views.widgets.ParallaxScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableHelper;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.OriginalImageScaleListener;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.hljsharelibrary.utils.ShareUtil;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kankan.wheel.widget.PagerWheelView;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.ThreadDetailsAdapter;
import me.suncloud.marrymemo.api.communitythreads.CommunityThreadApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.community.HljCommunityPostsData;
import me.suncloud.marrymemo.util.AnimUtil;
import me.suncloud.marrymemo.util.CommunityTogglesUtil;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.DisableLayoutManager;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.util.community.CommunityIntentUtil;
import me.suncloud.marrymemo.view.ReportThreadActivity;
import me.suncloud.marrymemo.view.ThreadPraisedUsersActivity;
import me.suncloud.marrymemo.view.UserProfileActivity;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutButton;
import rx.Subscription;

/**
 * Created by luohanlin on 2017/5/8.
 * 话题详情父类，普通话题和晒婚纱照话题的一些通用操作在这里实现
 */

@Route(path = RouterPath.IntentPath.Customer.COMMUNITY_THREAD_DETAIL)
public class CommunityThreadDetailActivity extends HljBaseNoBarActivity implements
        ThreadDetailsAdapter.OnTabViewCheckedListener {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.details_layout)
    LinearLayout detailsLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.scrollable_layout)
    ParallaxScrollableLayout scrollableLayout;
    @BindView(R.id.shadow_view)
    ImageView shadowView;
    @BindView(R.id.btn_share)
    ImageView btnShare;
    @BindView(R.id.btn_menu)
    ImageView btnMenu;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_share2)
    ImageButton btnShare2;
    @BindView(R.id.btn_menu2)
    ImageView btnMenu2;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.line_layout)
    View lineLayout;
    @BindView(R.id.btn_reply)
    Button btnReply;
    @BindView(R.id.img_thumb_up)
    ImageView imgThumbUp;
    @BindView(R.id.tv_praise_label)
    TextView tvPraiseLabel;
    @BindView(R.id.tv_praise_count)
    TextView tvPraiseCount;
    @BindView(R.id.checkable_praised)
    CheckableLinearLayoutButton checkablePraised;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    @BindView(R.id.img_page)
    ImageView imgPage;
    @BindView(R.id.pagination_layout)
    LinearLayout paginationLayout;
    @BindView(R.id.img_collect_thread)
    ImageView imgCollectThread;
    @BindView(R.id.tv_collect_label)
    TextView tvCollectLabel;
    @BindView(R.id.checkable_collect)
    CheckableLinearLayoutButton checkableCollect;
    @BindView(R.id.send_post_layout)
    LinearLayout sendPostLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.trans_action_layout)
    RelativeLayout transActionLayout;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.btn_back_2)
    ImageButton btnBack2;
    @BindView(R.id.tab_layout_holder)
    LinearLayout tabLayoutHolder;
    @BindView(R.id.tab_layout_holder_2)
    LinearLayout tabLayoutHolder2;
    @BindView(R.id.header_layout)
    LinearLayout headerLayout;
    @BindView(R.id.top_place_holder)
    View topPlaceHolder;

    public static final String ARG_IS_AFTER_CREATED = "is_after_created";
    public static final String ARG_ID = "id";

    private CommunityThread thread;
    private ArrayList<CommunityPost> posts;
    private ArrayList<CommunityPost> hotPosts;
    private HljHttpSubscriber detailSub;
    private HljHttpSubscriber postListSub;
    private HljHttpSubscriber hotPostListSub;
    private HljHttpSubscriber praiseSub;
    private HljHttpSubscriber collectSub;
    private HljHttpSubscriber deleteSub;
    private Subscription rxBusSub;
    private ThreadDetailsAdapter adapter;
    private DisableLayoutManager layoutManager;
    private boolean isPageTail;
    private boolean isPageHead;
    private int currentLastNo;
    private TextView endView;
    private View loadView;
    private int orderCheckedId;
    private int currentFirstNo;
    private ArrayList<Integer> paginationNoList;
    private String paramsOrder;
    private int paramsSerialNo;
    private String paramsWatch;
    private boolean isFromChannel; // 是否是从频道页面过来的
    private boolean isFinishShare; // 加载之后显示发布完成分享弹窗
    private int shareGold; // 分享显示金币数量
    private long id;
    private PopupWindow orderPopupWindow;
    private CopyPopupWindow copyPopupWindow;
    private CheckableLinearGroup orderCheckGroup;
    private boolean isSortHintShowed;

    public final int DETAIL_GRID_IMG_LIMIT = 4;
    private NormalThreadViewHolder normalThreadViewHolder;
    private boolean isWeddingPhotoThread;
    private TabLayoutViewHolder tabLayoutViewHolder;
    private Dialog repliedDlg;
    private int jumpSerialNo; // 指定楼层跳转

    private WeddingPhotoHeaderViewHolder photoHeaderViewHolder;
    private int alphaHeight;
    private int coverWidth;
    private int coverHeight;
    private int actionBarHeight;
    private Dialog postEditDlg;
    private Dialog deleteDlg;
    private Dialog moreSettingDlg;
    private boolean isAfterCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_thread_detail);
        ButterKnife.bind(this);

        initValues();
        intViews();
        initLoads();
        setDefaultStatusBarPadding();

        initTracker();
    }

    private void initTracker() {
        JSONObject siteJson = null;
        String site = getIntent().getStringExtra("site");
        if (!JSONUtil.isEmpty(site)) {
            try {
                siteJson = new JSONObject(site);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        new HljTracker.Builder(this).eventableId(id)
                .eventableType("CommunityThread")
                .screen("topic_detail_page")
                .action("hit")
                .site(siteJson)
                .build()
                .send();
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(detailSub,
                postListSub,
                hotPostListSub,
                praiseSub,
                collectSub,
                deleteSub,
                rxBusSub);
        super.onFinish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (orderPopupWindow != null) {
            orderPopupWindow.dismiss();
        }
    }

    private void initValues() {
        id = getIntent().getLongExtra(ARG_ID, 0);
        jumpSerialNo = getIntent().getIntExtra("serial_no", 0);
        isFromChannel = getIntent().getBooleanExtra("is_from_channel", false);
        isFinishShare = getIntent().getBooleanExtra("is_finish_share", false);
        shareGold = getIntent().getIntExtra("gold", 0);
        isAfterCreated = getIntent().getBooleanExtra(ARG_IS_AFTER_CREATED, false);
        orderCheckedId = R.id.cb_hot;
        paginationNoList = new ArrayList<>();
        posts = new ArrayList<>();
        hotPosts = new ArrayList<>();

        actionBarHeight = CommonUtil.dp2px(this, 45);
        coverWidth = CommonUtil.getDeviceSize(this).x;
        coverHeight = coverWidth * 9 / 16;
        alphaHeight = coverHeight - actionBarHeight;

        paramsOrder = CommunityThreadApi.COMMUNITY_POST_ORDER_ASC;
        paramsWatch = CommunityThreadApi.COMMUNITY_POST_PARAMS_ALL;
        paramsSerialNo = 0;
    }

    private void intViews() {
        adapter = new ThreadDetailsAdapter(this,
                posts,
                recyclerView,
                (isFromChannel || isFinishShare));
        layoutManager = new DisableLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        endView = (TextView) footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        adapter.setFooterView(footerView);
        adapter.setOnTabViewCheckedListener(this);
        adapter.setOnItemClickListener(onItemClickListener);
        adapter.setOnContentLongClickListener(copyListener);
        adapter.setOnItemReplyListener(onItemReplyListener);
        adapter.setOnPraisePostListener(onPraisePostListener);
        adapter.setOnPraiseGroupPhotoListener(onPraiseGroupPhotoListener);

        registerRxBusEvent();

        scrollableLayout.getHelper()
                .setCurrentScrollableContainer(new ScrollableHelper.ScrollableContainer() {
                    @Override
                    public View getScrollableView() {
                        return recyclerView;
                    }

                    @Override
                    public boolean isDisable() {
                        return false;
                    }
                });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        checkAndAutoLoading();
                        break;
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        if (isAfterCreated) {
            initNotifyDialog();
        }
    }

    private void initNotifyDialog() {
        Dialog dialog = SystemNotificationUtil.getNotificationOpenDlgOfPrefName(this,
                Constants.PREF_NOTICE_OPEN_DLG_THREAD,
                "话题发布成功",
                "新娘们一定会喜欢你发布的话题，立即开启消息通知，以免错过点赞和回复哦~",
                R.drawable.icon_dlg_appointment);
        if (dialog != null) {
            dialog.show();
        }
    }

    /**
     * 注册RxBux事件，监听大图查看时的点赞和收藏事件，以同步信息
     */
    private void registerRxBusEvent() {
        if (rxBusSub == null || rxBusSub.isUnsubscribed()) {
            rxBusSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case THREAD_WEDDING_PHOTO_GROUP_PRAISED:
                                    // 婚纱照组图大图查看时的点赞事件，obj为点赞图片所属组图在组图列表中的index
                                    try {
                                        int index = Integer.valueOf(rxEvent.getObject()
                                                .toString());
                                        boolean isPraisedBefore = thread.getWeddingPhotoItems()
                                                .get(index)
                                                .isPraised();
                                        int praiseCount = thread.getWeddingPhotoItems()
                                                .get(index)
                                                .getLikesCount();
                                        if (isPraisedBefore) {
                                            praiseCount--;
                                            thread.setPraisedSum(thread.getPraisedSum() - 1);
                                        } else {
                                            praiseCount++;
                                            thread.setPraisedSum(thread.getPraisedSum() + 1);
                                        }
                                        thread.getWeddingPhotoItems()
                                                .get(index)
                                                .setPraised(!isPraisedBefore);
                                        thread.getWeddingPhotoItems()
                                                .get(index)
                                                .setLikesCount(praiseCount);
                                        adapter.notifyItemChanged(index);
                                        setBottomPraisedView();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case THREAD_WEDDING_PHOTO_COLLECTED:
                                    // 用户在大图查看时收藏该话题
                                    thread.setCollected(!thread.isCollected());
                                    checkableCollect.setChecked(thread.isCollected());
                                    tvCollectLabel.setText(thread.isCollected() ? R.string
                                            .label_has_collection : R.string.label_collect);
                                    break;
                            }
                        }
                    });
        }
    }

    private void checkAndAutoLoading() {
        if (!posts.isEmpty() && layoutManager.findLastVisibleItemPosition() >= (layoutManager
                .getItemCount() - 5) && !isPageTail) {
            loadTailPosts();
        } else if (!posts.isEmpty() && layoutManager.findFirstVisibleItemPosition() < 2 &&
                !isPageHead) {
            // 没有到顶，继续加载
            loadHeadPosts();
        }
    }

    private void invalidateTabView() {
        int tabViewPosition = adapter.getTabViewPosition();
        int position = layoutManager.findFirstVisibleItemPosition();
        if (tabViewPosition > 0 && position >= tabViewPosition) {
            tabLayoutViewHolder.tabLayout.setVisibility(View.VISIBLE);
        } else {
            tabLayoutViewHolder.tabLayout.setVisibility(View.GONE);
        }
    }

    private void loadTailPosts() {
        loadView.setVisibility(View.VISIBLE);
        endView.setVisibility(View.GONE);
        if (orderCheckedId == R.id.cb_hot) {
            // 正序
            paramsOrder = CommunityThreadApi.COMMUNITY_POST_ORDER_ASC;
            paramsSerialNo = currentLastNo + 1;
        } else {
            // 倒序
            paramsOrder = CommunityThreadApi.COMMUNITY_POST_ORDER_DESC;
            paramsSerialNo = currentLastNo - 1;
        }

        layoutManager.setScrollDisable(true);
        scrollableLayout.setScrollDisable(true);
        loadPostsList();
    }

    private void loadHeadPosts() {
        tabLayoutViewHolder.headLoadingView.setVisibility(View.VISIBLE);
        if (orderCheckedId == R.id.cb_hot) {
            // 加载正序内容的上一页，需要倒序获取
            paramsOrder = CommunityThreadApi.COMMUNITY_POST_ORDER_DESC;
            paramsSerialNo = currentFirstNo - 1;
        } else {
            // 加载倒叙内容的下一页
            paramsOrder = CommunityThreadApi.COMMUNITY_POST_ORDER_ASC;
            paramsSerialNo = currentFirstNo + 1;
        }

        layoutManager.setScrollDisable(true);
        scrollableLayout.setScrollDisable(true);
        loadPostsList();
    }

    private void initLoads() {
        detailSub = HljHttpSubscriber.buildSubscriber(this)
                .setEmptyView(emptyView)
                .setContentView(scrollableLayout)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<CommunityThread>() {

                    @Override
                    public void onNext(CommunityThread communityThread) {
                        thread = communityThread;
                        setCommunityThread();
                    }
                })
                .build();
        CommunityThreadApi.getCommunityThread(id)
                .subscribe(detailSub);

    }

    private void loadPostsList() {
        if (postListSub != null && !postListSub.isUnsubscribed()) {
            return;
        }
        postListSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljCommunityPostsData>() {
                    @Override
                    public void onNext(HljCommunityPostsData hljCommunityPostsData) {
                        setPosts(hljCommunityPostsData);
                    }
                })
                .build();
        CommunityThreadApi.getCommunityPosts(id, paramsSerialNo, paramsWatch, paramsOrder)
                .subscribe(postListSub);
    }

    private void loadHotPostsList() {
        if (hotPostListSub != null && !hotPostListSub.isUnsubscribed()) {
            return;
        }
        hotPostListSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<List<CommunityPost>>() {
                    @Override
                    public void onNext(List<CommunityPost> communityPosts) {
                        setHotPosts(communityPosts);
                    }
                })
                .build();
        CommunityThreadApi.getHotCommunityPost(id)
                .subscribe(hotPostListSub);
    }

    /**
     * 筛选参数或者排序参数改变，需要重新刷新post列表
     */
    private void reloadPostsList() {
        if (isWeddingPhotoThread) {
            tabLayoutViewHolder.tabLayout.setVisibility(View.GONE);
        }

        posts.clear();
        if ((paginationNoList.size() == 0 && paramsSerialNo > 0) || (paginationNoList.size() > 0
                && paramsSerialNo > paginationNoList.get(
                0))) {
            // 获取的定位不是从头获取
            hideThreadDetailsAndHotPost();
        } else {
            showThreadDetailsAndHotPost();
        }
        adapter.notifyDataSetChanged();

        loadPostsList();
        if (paramsWatch.equals(CommunityThreadApi.COMMUNITY_POST_PARAMS_ALL) && paramsOrder.equals(
                CommunityThreadApi.COMMUNITY_POST_ORDER_ASC)) {
            // 只有在全部显示并且顺序排列条件下，才需要重新刷新hot posts
            loadHotPostsList();
        }
    }

    private void setCommunityThread() {
        if (thread.getWeddingPhotoContent() != null) {
            // 晒婚纱照类别的话题
            isWeddingPhotoThread = true;
            setWeddingPhotoThreadDetailHead();
        } else {
            // 图文类普通话提
            setPicsThreadDetailHead();
        }

        paginationNoList = thread.getPaginationNoList();
        if (paginationNoList == null) {
            paginationNoList = new ArrayList<>();
        }
        if (paginationNoList.size() > 1 && jumpSerialNo >= paginationNoList.get(1)) {
            // 如果需要跳楼并且不在第一页则直接加载该楼层所在的那一页
            int jumpStart = 0;
            for (int i = paginationNoList.size() - 1; i >= 0; i--) {
                int pageStart = paginationNoList.get(i);
                if (pageStart <= jumpSerialNo) {
                    jumpStart = pageStart;
                    break;
                }
            }
            paramsSerialNo = jumpStart;
            reloadPostsList();
        } else {
            // 否则正常加载第一页即可
            loadPostsList();
            loadHotPostsList();
        }

        checkableCollect.setChecked(thread.isCollected());
        tvCollectLabel.setText(thread.isCollected() ? R.string.label_has_collection : R.string
                .label_collect);
        setBottomPraisedView();
        if (isFinishShare) {
            showFinishShare();
        }
    }

    private void setBottomPraisedView() {
        checkablePraised.setChecked(thread.isPraised());
        String praiseLabel;
        if (thread.isPraised()) {
            praiseLabel = "已赞";
        } else {
            praiseLabel = "赞";
        }
        praiseLabel = praiseLabel + (thread.getPraisedSum() > 0 ? "·" + String.valueOf(thread
                .getPraisedSum()) : " ");
        tvPraiseLabel.setText(praiseLabel);
    }

    private void hideThreadDetailsAndHotPost() {
        detailsLayout.setVisibility(View.GONE);
        adapter.setTagAndHotHide(true);
        if (isWeddingPhotoThread) {
            topPlaceHolder.getLayoutParams().height = CommonUtil.dp2px(this, 44);
            scrollableLayout.setExtraHeight(0);
            scrollableLayout.setParallaxView(null, 0);
            scrollableLayout.removeOnScrollListeners();
            recyclerView.removeOnScrollListener(tabViewScrollListener);
            setTabLayoutHolder(false);
            actionLayout.setAlpha(1);
            shadowView.setAlpha(0);
        }
    }

    private void showThreadDetailsAndHotPost() {
        layoutManager.setScrollDisable(true);
        scrollableLayout.setScrollDisable(true);
        if (adapter.isTagAndHotHide() && isWeddingPhotoThread) {
            topPlaceHolder.getLayoutParams().height = 0;
            setTabLayoutHolder(isWeddingPhotoThread);
            tabLayoutViewHolder.tabLayout.setVisibility(View.VISIBLE);
            scrollableLayout.setExtraHeight(actionBarHeight);
            scrollableLayout.setParallaxView(photoHeaderViewHolder.coverLayout, coverHeight);
            scrollableLayout.addOnScrollListener(alphaScrollListener);
            recyclerView.addOnScrollListener(tabViewScrollListener);
        } else if (isWeddingPhotoThread) {
            tabLayoutViewHolder.tabLayout.setVisibility(View.GONE);
        }
        detailsLayout.setVisibility(View.VISIBLE);
        adapter.setHotPosts(hotPosts);
        adapter.setTagAndHotHide(false);
    }

    private void setWeddingPhotoThreadDetailHead() {
        transActionLayout.setVisibility(View.VISIBLE);
        actionLayout.setAlpha(0);
        shadowView.setVisibility(View.VISIBLE);

        tvTitle.setText(R.string.title_activity_thread_wedding_photo);
        User user = Session.getInstance()
                .getCurrentUser(this);
        if (user != null && thread.getAuthor()
                .getId() == user.getId()) {
            btnMenu.setVisibility(View.GONE);
            btnMenu2.setVisibility(View.GONE);
        } else {
            btnMenu.setVisibility(View.VISIBLE);
            btnMenu2.setVisibility(View.VISIBLE);
        }

        View weddingPhotoHeader = LayoutInflater.from(this)
                .inflate(R.layout.wedding_photo_thread_header, null, false);
        photoHeaderViewHolder = new WeddingPhotoHeaderViewHolder(weddingPhotoHeader);
        detailsLayout.removeAllViews();
        photoHeaderViewHolder.setView();
        detailsLayout.addView(weddingPhotoHeader);

        setTabLayoutHolder(isWeddingPhotoThread);
        scrollableLayout.setExtraHeight(actionBarHeight);
        scrollableLayout.setParallaxView(photoHeaderViewHolder.coverLayout, coverHeight);
        scrollableLayout.setViewsBounds(2);
        scrollableLayout.addOnScrollListener(alphaScrollListener);
        recyclerView.addOnScrollListener(tabViewScrollListener);

        adapter.setThread(thread);
        adapter.notifyDataSetChanged();
    }

    private void setPicsThreadDetailHead() {
        transActionLayout.setVisibility(View.VISIBLE);
        actionLayout.setAlpha(1);
        shadowView.setVisibility(View.GONE);

        tvTitle.setText(R.string.title_activity_thread_detail);
        btnMenu.setVisibility(View.VISIBLE);
        btnMenu2.setVisibility(View.VISIBLE);

        View view = LayoutInflater.from(this)
                .inflate(R.layout.thread_detail_header, null, false);

        scrollableLayout.setExtraHeight(0);
        topPlaceHolder.getLayoutParams().height = CommonUtil.dp2px(this, 44);
        normalThreadViewHolder = new NormalThreadViewHolder(view);
        normalThreadViewHolder.setView(this);
        detailsLayout.removeAllViews();
        detailsLayout.addView(view);

        setTabLayoutHolder(isWeddingPhotoThread);
        scrollableLayout.setParallaxView(null, 0);

        adapter.setThread(thread);
        adapter.notifyDataSetChanged();
    }

    private void setTabLayoutHolder(boolean isPhoto) {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.thread_detail_tab_layout_holder, null, false);
        tabLayoutViewHolder = new TabLayoutViewHolder(view);
        tabLayoutViewHolder.setView();
        if (isPhoto) {
            tabLayoutHolder.setVisibility(View.GONE);
            tabLayoutHolder2.setVisibility(View.VISIBLE);
            tabLayoutHolder2.removeAllViews();
            tabLayoutHolder2.addView(view);
            tabLayoutViewHolder.tabLayout.setVisibility(View.GONE);
        } else {
            tabLayoutHolder.setVisibility(View.VISIBLE);
            tabLayoutHolder2.setVisibility(View.GONE);
            tabLayoutHolder.removeAllViews();
            tabLayoutHolder.addView(view);
            tabLayoutViewHolder.tabLayout.setVisibility(View.VISIBLE);
            tabLayoutViewHolder.headLoadingView.setVisibility(View.GONE);
        }
    }

    private void setPosts(HljCommunityPostsData hljCommunityPostsData) {
        if (paramsSerialNo == 0 && paramsOrder.equals(CommunityThreadApi
                .COMMUNITY_POST_ORDER_ASC)) {
            // 下拉刷新或者首次加载，清空数据
            posts.clear();
        }
        if (paramsSerialNo == Integer.MAX_VALUE && paramsOrder.equals(CommunityThreadApi
                .COMMUNITY_POST_ORDER_DESC)) {
            // 倒序加载第一页时，清空数据
            posts.clear();
        }

        // 将新获取的数据加入到本地列表中
        int jumpIndex = 0;
        for (CommunityPost post : hljCommunityPostsData.getList()) {
            if ((paramsOrder.equals(CommunityThreadApi.COMMUNITY_POST_ORDER_DESC) &&
                    orderCheckedId == R.id.cb_hot) || (paramsOrder.equals(
                    CommunityThreadApi.COMMUNITY_POST_ORDER_ASC) && orderCheckedId == R.id
                    .cb_new)) {
                // 倒序加载时，数据列表插入顺序相反
                posts.add(0, post);
            } else {
                posts.add(post);
            }
            // 定位跳转楼层
            if (jumpSerialNo > 0 && post.getSerialNo() == jumpSerialNo) {
                jumpIndex = posts.indexOf(post) + 1;
                jumpSerialNo = 0;
            }
        }

        paginationNoList = new ArrayList<>();
        for (String noStr : hljCommunityPostsData.getStartSerialNos()) {
            try {
                paginationNoList.add(Integer.valueOf(noStr));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 设置各种定位参数
        if (!posts.isEmpty()) {
            currentFirstNo = posts.get(0)
                    .getSerialNo();
            currentLastNo = posts.get(posts.size() - 1)
                    .getSerialNo();
        } else {
            currentFirstNo = 0;
            currentLastNo = 0;
        }
        isPageHead = false;
        isPageTail = false;
        if (hljCommunityPostsData.getStartSerialNos()
                .isEmpty()) {
            isPageTail = posts.size() < 20;
        } else if (orderCheckedId == R.id.cb_hot) {
            isPageHead = currentFirstNo <= paginationNoList.get(0);
            isPageTail = currentLastNo >= paginationNoList.get(paginationNoList.size() - 1);
        } else {
            isPageHead = currentFirstNo >= paginationNoList.get(paginationNoList.size() - 1);
            isPageTail = currentLastNo <= paginationNoList.get(0);
        }

        if (isPageHead) {
            showThreadDetailsAndHotPost();
            if (paramsOrder.equals(CommunityThreadApi.COMMUNITY_POST_ORDER_DESC) &&
                    orderCheckedId == R.id.cb_hot) {
                scrollableLayout.scrollTo(0,
                        scrollableLayout.getHeaderView()
                                .getMeasuredHeight());
            }
        }

        if (jumpIndex > 0) {
            // 跳楼
            jumpIndex = jumpIndex + adapter.getAllPostTagIndex() - 1;
            if (jumpIndex > 0 && jumpIndex <= adapter.getItemCount()) {
                scrollableLayout.scrollTo(0,
                        scrollableLayout.getHeaderView()
                                .getMeasuredHeight());
                layoutManager.scrollToPositionWithOffset(jumpIndex, 0);
            }
        } else if (!posts.isEmpty() && paramsOrder.equals(CommunityThreadApi
                .COMMUNITY_POST_ORDER_DESC) && orderCheckedId == R.id.cb_hot) {
            // 如果是倒序加载,则重新定位当前新加载的尾部,即之前的头部
            int gotTo = hljCommunityPostsData.getList()
                    .size() + 1 + adapter.getAllPostTagIndex();
            if (isWeddingPhotoThread) {
                layoutManager.scrollToPositionWithOffset(gotTo, CommonUtil.dp2px(this, 89));
            } else {
                layoutManager.scrollToPositionWithOffset(gotTo, CommonUtil.dp2px(this, 44));
            }
        }

        loadView.setVisibility(View.GONE);
        if (tabLayoutViewHolder != null) {
            tabLayoutViewHolder.headLoadingView.setVisibility(View.GONE);
        }
        endView.setVisibility(isPageTail ? View.VISIBLE : View.GONE);

        adapter.setTotalPostCount(hljCommunityPostsData.getTotalCount());
        adapter.notifyDataSetChanged();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layoutManager.setScrollDisable(false);
                scrollableLayout.setScrollDisable(false);
            }
        }, 300);
    }

    private void setHotPosts(List<CommunityPost> communityPosts) {
        this.hotPosts.clear();
        this.hotPosts.addAll(communityPosts);
        adapter.setHotPosts(hotPosts);
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.btn_back, R.id.btn_back_2})
    void onBack() {
        super.onBackPressed();
    }

    @OnClick({R.id.btn_share, R.id.btn_share2})
    void onShare() {
        if (thread == null) {
            return;
        }
        ShareDialogUtil.onCommonShare(this,
                new ShareInfo(thread.getShareInfo()
                        .getTitle(),
                        thread.getShareInfo()
                                .getDesc(),
                        thread.getShareInfo()
                                .getDesc2(),
                        thread.getShareInfo()
                                .getUrl(),
                        thread.getShareInfo()
                                .getIcon()));
    }

    /**
     * 显示发布完成分享
     */
    private void showFinishShare() {
        ShareInfo shareInfo = new ShareInfo(thread.getShareInfo()
                .getTitle(),
                thread.getShareInfo()
                        .getDesc(),
                thread.getShareInfo()
                        .getDesc2(),
                thread.getShareInfo()
                        .getUrl(),
                thread.getShareInfo()
                        .getIcon());

        final Dialog dialog = new Dialog(this, R.style.BubbleDialogTheme);
        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_thread_finish_share_menu, null, false);
        FinishShareViewHolder holder = new FinishShareViewHolder(view);
        holder.setView(dialog, shareInfo);
        dialog.setContentView(view);

        Window win = dialog.getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        params.width = (int) Math.round(CommonUtil.getDeviceSize(this).x * 0.8125);
        win.setAttributes(params);
        win.setGravity(Gravity.CENTER);

        dialog.show();
    }

    @OnClick({R.id.btn_menu, R.id.btn_menu2})
    void onMenu() {
        if (thread == null || thread.isHidden()) {
            return;
        }
        if (moreSettingDlg == null) {
            moreSettingDlg = new Dialog(this, R.style.BubbleDialogTheme);
            View view = getLayoutInflater().inflate(R.layout.dialog_thread_menu, null);
            User user = Session.getInstance()
                    .getCurrentUser();
            View reportLayout = view.findViewById(R.id.report_layout);
            View editLayout = view.findViewById(R.id.edit_layout);
            reportLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (thread == null || thread.isHidden()) {
                        return;
                    }
                    moreSettingDlg.cancel();
                    if (Util.loginBindChecked(CommunityThreadDetailActivity.this,
                            Constants.Login.INFORM_LOGIN)) {
                        onReport(thread.getId(), "thread");
                    }
                }
            });
            editLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 编辑当前话题
                    if (thread.isAllowModify()) {
                        Intent intent = new Intent(CommunityThreadDetailActivity.this,
                                CreateThreadActivity.class);
                        intent.putExtra(CreateThreadActivity.ARG_THREAD, thread);
                        startActivityForResult(intent, Constants.RequestCode.EDIT_COMMUNITY_THREAD);
                        moreSettingDlg.cancel();
                    } else {
                        //话题不可编辑时，提示该话题已被限制编辑
                        Toast.makeText(CommunityThreadDetailActivity.this,
                                R.string.msg_thread_modify,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
            view.findViewById(R.id.btn_cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            moreSettingDlg.cancel();
                        }
                    });
            if (user != null && user.getId() == thread.getAuthor()
                    .getId()) {
                editLayout.setVisibility(View.VISIBLE);
                reportLayout.setVisibility(View.GONE);
            } else {
                editLayout.setVisibility(View.GONE);
                reportLayout.setVisibility(View.VISIBLE);
            }

            moreSettingDlg.setContentView(view);
            Window win = moreSettingDlg.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(CommunityThreadDetailActivity.this);
            params.width = point.x;
            win.setAttributes(params);
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        moreSettingDlg.show();
    }

    @OnClick(R.id.btn_reply)
    void onReplyThread() {
        if (thread != null && !thread.isHidden()) {
            CommunityPost post = thread.getPost();
            post.setAuthor(thread.getAuthor());
            onReplyPost(post, true);
        }
    }

    @OnClick(R.id.checkable_praised)
    void onPraiseThread() {
        if (thread == null) {
            return;
        }
        onCommunityThreadPraise(checkablePraised, imgThumbUp, tvAdd);
    }

    @OnClick(R.id.pagination_layout)
    void onPagination() {
        if (thread == null) {
            return;
        }
        showPaginationDlg();
    }

    @OnClick(R.id.checkable_collect)
    void onCollectThread() {
        if (thread == null) {
            return;
        }
        collectThread();
    }

    private void onReplyPost(CommunityPost post, boolean isReplyThread) {
        if (Util.loginBindChecked(this)) {
            Intent intent = new Intent(this, CreatePostActivity.class);
            intent.putExtra(CreatePostActivity.ARG_POST, post);
            if (thread != null) {
                intent.putExtra(CreatePostActivity.ARG_REPAY_TITLE,
                        thread.getAuthor()
                                .getName());
            }
            intent.putExtra(CreatePostActivity.ARG_IS_REPLY_THREAD, isReplyThread);
            startActivityForResult(intent, Constants.RequestCode.REPLY_THREAD_POST);
            overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.activity_anim_default);
        }
    }

    private void onCommunityThreadPraise(
            final CheckableLinearLayoutButton praiseView,
            final ImageView imgThumb,
            final TextView tvAdd) {
        onCommunityThreadPraise(praiseView, imgThumb, tvAdd, null);
    }

    private void onCommunityThreadPraise(
            final CheckableLinearLayoutButton praiseView,
            final ImageView imgThumb,
            final TextView tvAdd,
            final TextView tvCount) {
        if (AuthUtil.loginBindCheck(this)) {
            final CommunityAuthor author = new CommunityAuthor();
            final User user = Session.getInstance()
                    .getCurrentUser(this);
            author.setId(user.getId());
            author.setName(user.getNick());
            author.setAvatar(user.getAvatar());
            author.setGender(user.getGender());
            author.setPending(user.getIsPending());
            author.setSpecialty(user.getSpecialty());
            author.setWeddingDay(new DateTime(user.getWeddingDay()));

            praiseView.setClickable(false);
            // 先变化,再进行网络请求
            if (thread.isPraised()) {
                // 之前已经赞过,现在取消
                thread.setPraised(false);
                thread.setPraisedSum(thread.getPraisedSum() - 1);
                thread.removePraisedUser(user.getId());
            } else {
                // 没有赞过,变为赞
                AnimUtil.pulseAnimate(imgThumb);
                AnimUtil.zoomInUpAnimate(tvAdd);
                thread.setPraised(true);
                thread.setPraisedSum(thread.getPraisedSum() + 1);
                thread.pushedPraisedUser(author);
            }
            String praiseLabel;
            if (thread.isPraised()) {
                praiseLabel = "已赞";
            } else {
                praiseLabel = "赞";
            }
            praiseLabel = praiseLabel + (thread.getPraisedSum() > 0 ? "·" + String.valueOf(thread
                    .getPraisedSum()) : " ");
            tvPraiseLabel.setText(praiseLabel);
            praiseView.setChecked(thread.isPraised());
            if (tvCount != null) {
                tvCount.setText(String.valueOf(thread.getPraisedSum()));
            }

            praiseSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<CommunityPostPraiseResult>() {
                        @Override
                        public void onNext(CommunityPostPraiseResult communityPostPraiseResult) {
                            boolean isPraise = communityPostPraiseResult.getBelongPraise() > 0;
                            thread.setPraised(isPraise);
                            Util.showToast(isPraise ? R.string.hint_praised_complete : R.string
                                            .hint_dispraised_complete,
                                    CommunityThreadDetailActivity.this);
                            if (normalThreadViewHolder != null) {
                                // 刷新详情中的点赞按钮
                                normalThreadViewHolder.setPraisedCountView(
                                        CommunityThreadDetailActivity
                                                .this);
                            }
                            praiseView.setClickable(true);
                            setBottomPraisedView();
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            // 失败的话变回原样
                            if (thread.isPraised()) {
                                // 之前已经赞过,现在取消
                                thread.setPraised(false);
                                thread.setPraisedSum(thread.getPraisedSum() - 1);
                                thread.removePraisedUser(user.getId());
                            } else {
                                // 没有赞过,变为赞
                                thread.setPraised(true);
                                thread.setPraisedSum(thread.getPraisedSum() + 1);
                                thread.pushedPraisedUser(author);
                            }
                            String praiseLabel;
                            if (thread.isPraised()) {
                                praiseLabel = "已赞";
                            } else {
                                praiseLabel = "赞";
                            }
                            praiseLabel = praiseLabel + (thread.getPraisedSum() > 0 ? "·" +
                                    String.valueOf(
                                    thread.getPraisedSum()) : " ");
                            tvPraiseLabel.setText(praiseLabel);
                            praiseView.setChecked(thread.isPraised());
                            praiseView.setClickable(true);
                            if (tvCount != null) {
                                tvCount.setText(thread.getPraisedSum() == 0 ? "赞" : String.valueOf(
                                        thread.getPraisedSum()));
                            }
                            setBottomPraisedView();
                        }
                    })
                    .build();

            CommunityThreadApi.postPraise(thread.getPost()
                    .getId())
                    .subscribe(praiseSub);
        }
    }

    private void showPaginationDlg() {
        if (posts.isEmpty() || orderCheckedId == R.id.cb_new) {
            if (orderCheckedId == R.id.cb_new) {
                Toast.makeText(this, R.string.msg_pagination_disabled_on_desc, Toast.LENGTH_SHORT)
                        .show();
            }
            return;
        }
        final Dialog pageDialog = new Dialog(this, R.style.BubbleDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.dialog_flip_over, null);
        final PagerWheelView pageWheel = (PagerWheelView) view.findViewById(R.id.pager);
        pageWheel.setVauleChange(1, Math.max(1, paginationNoList.size()));
        view.findViewById(R.id.close_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageDialog.dismiss();
                    }
                });
        view.findViewById(R.id.ok_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageDialog.dismiss();
                        paramsSerialNo = 0;
                        if (paginationNoList.size() >= pageWheel.getCurrentItem() + 1) {
                            paramsSerialNo = paginationNoList.get(pageWheel.getCurrentItem());
                        }
                        paramsOrder = CommunityThreadApi.COMMUNITY_POST_ORDER_ASC;
                        orderCheckedId = R.id.cb_hot;
                        progressBar.setVisibility(View.VISIBLE);
                        reloadPostsList();
                    }
                });
        pageDialog.setContentView(view);
        Window window = pageDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(this);
        params.width = point.x;
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_anim_rise_style);
        pageDialog.show();
    }

    private void showPostEditDialog(final int position, final CommunityPost post) {
        postEditDlg = new Dialog(this, R.style.BubbleDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.dialog_post_edit_memu, null);
        User user = Session.getInstance()
                .getCurrentUser();
        Button btnCopy = (Button) view.findViewById(R.id.btn_copy);
        Button btnReport = (Button) view.findViewById(R.id.btn_report);
        Button btnDelete = (Button) view.findViewById(R.id.btn_delete);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        Button btnReply = (Button) view.findViewById(R.id.btn_reply);
        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReplyPost(post, false);
                postEditDlg.cancel();
            }
        });

        if (user != null && user.getId() == post.getAuthor()
                .getId()) {
            // 本人回帖,不显示举报
            btnReport.setVisibility(View.GONE);
            // 本人回帖,可以删除
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeletePost(post);
                    postEditDlg.cancel();
                }
            });
        } else {
            btnReport.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.GONE);
            btnReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReport(post.getId(), "post");
                    postEditDlg.cancel();
                }
            });
        }
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 复制帖子文字内容
                postEditDlg.cancel();
                ClipboardManager cmb = (ClipboardManager) CommunityThreadDetailActivity.this
                        .getSystemService(
                        CommunityThreadDetailActivity.this.CLIPBOARD_SERVICE);
                cmb.setPrimaryClip(ClipData.newPlainText(CommunityThreadDetailActivity.this
                        .getString(
                        R.string.app_name), post.getMessage()));
                Toast.makeText(CommunityThreadDetailActivity.this,
                        R.string.hint_post_copy,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postEditDlg.cancel();
            }
        });

        postEditDlg.setContentView(view);
        Window win = postEditDlg.getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        Point point = JSONUtil.getDeviceSize(CommunityThreadDetailActivity.this);
        params.width = point.x;
        win.setAttributes(params);
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialog_anim_rise_style);

        postEditDlg.show();
    }

    private void onDeletePost(final CommunityPost post) {
        deleteSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(com.hunliji.hljcommonlibrary.utils.DialogUtil
                        .createProgressDialog(
                        CommunityThreadDetailActivity.this))
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        Toast.makeText(CommunityThreadDetailActivity.this,
                                R.string.msg_success_to_delete_post,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .build();

        deleteDlg = DialogUtil.createDoubleButtonDialog(deleteDlg,
                this,
                getString(R.string.msg_delete_post),
                getString(R.string.label_delete),
                getString(R.string.label_cancel),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommunityThreadApi.deletePost(thread.getId(), post.getId())
                                .subscribe(deleteSub);
                        deleteDlg.cancel();
                    }
                });

        deleteDlg.show();
    }

    private void onReport(long id, String kind) {
        Intent intent = new Intent(this, ReportThreadActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("kind", kind);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    private void collectThread() {
        if (thread == null) {
            return;
        }
        if (AuthUtil.loginBindCheck(this)) {
            collectSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            if (thread.isCollected()) {
                                thread.setCollected(false);
                                checkableCollect.setChecked(false);
                                tvCollectLabel.setText(R.string.label_collect);
                                Util.showToast(R.string.hint_discollect_complete,
                                        CommunityThreadDetailActivity.this);
                            } else {
                                thread.setCollected(true);
                                checkableCollect.setChecked(true);
                                tvCollectLabel.setText(R.string.label_has_collection);
                                if (Util.isNewFirstCollect(CommunityThreadDetailActivity.this, 5)) {
                                    Util.showFirstCollectNoticeDialog
                                            (CommunityThreadDetailActivity.this,
                                            5);
                                } else {
                                    Util.showToast(R.string.hint_collect_complete,
                                            CommunityThreadDetailActivity.this);
                                }
                            }
                        }
                    })
                    .build();
            CommunityThreadApi.postThreadCollect(thread.getId())
                    .subscribe(collectSub);
        }
    }

    @Override
    public void onAllChecked() {
        tabLayoutViewHolder.cbAll.setChecked(true);

        orderCheckedId = R.id.cb_hot;
        tabLayoutViewHolder.sortLayout.setVisibility(View.VISIBLE);

        paramsOrder = CommunityThreadApi.COMMUNITY_POST_ORDER_ASC;
        paramsWatch = CommunityThreadApi.COMMUNITY_POST_PARAMS_ALL;
        paramsSerialNo = 0;

        reloadPostsList();
    }

    @Override
    public void onOwnerChecked() {
        tabLayoutViewHolder.cbOwner.setChecked(true);

        orderCheckedId = R.id.cb_hot;
        tabLayoutViewHolder.sortLayout.setVisibility(View.GONE);

        paramsOrder = CommunityThreadApi.COMMUNITY_POST_ORDER_ASC;
        paramsWatch = CommunityThreadApi.COMMUNITY_POST_PARAMS_ORIGINAL;
        paramsSerialNo = 0;

        reloadPostsList();
    }

    @Override
    public void onOrderMenuClicked(View view) {
        isSortHintShowed = true;
        showSortWindow(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.EDIT_COMMUNITY_THREAD:
                    // 编辑成功,更新当前话题
                    if (data != null) {
                        scrollableLayout.setVisibility(View.GONE);
                        posts.clear();
                        initLoads();
                    }
                    break;
                case Constants.RequestCode.REPLY_THREAD_POST:
                    onReplyPostSuccess();
                    break;
                case Constants.RequestCode.WEDDING_PHOTO_THREAD_VIEW_LARGE_IMAGE:
                    if (data != null && data.getBooleanExtra("replied_thread", false)) {
                        onReplyPostSuccess();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onReplyPostSuccess() {
        thread.setPostCount(thread.getPostCount() + 1);

        // 如果当前页面已经到最后了,去请求最后一条添加到现有列表最后
        if (isPageTail && orderCheckedId == R.id.cb_hot) {
            // 顺序排列并且已到最后一页的话直接请求尾部数据即可
            loadTailPosts();
        } else if (isPageHead && orderCheckedId == R.id.cb_new) {
            // 倒序排列并且在顶部的话直接加载头部数据即可
            loadHeadPosts();
        }
        final boolean isReplied = getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE).getBoolean(Constants.PREF_THREAD_REPLIED, false);
        // 如果是首次回帖，则显示弹窗
        if (!isReplied) {
            repliedDlg = DialogUtil.createSingleButtonDialog(repliedDlg,
                    CommunityThreadDetailActivity.this,
                    getString(R.string.label_first_thread_replied),
                    getString(R.string.label_ok2),
                    R.mipmap.icon_new_praise___cm,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            repliedDlg.cancel();
                            getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit()
                                    .putBoolean(Constants.PREF_THREAD_REPLIED, true)
                                    .apply();
                        }
                    });
            repliedDlg.setCancelable(false);
            repliedDlg.show();
        }
    }

    private void showSortWindow(View view) {
        if (orderPopupWindow != null && orderPopupWindow.isShowing()) {
            orderPopupWindow.dismiss();
        }
        if (orderPopupWindow == null) {
            View contentView = LayoutInflater.from(this)
                    .inflate(R.layout.dialog_thread_sort, null);
            contentView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            orderPopupWindow = new PopupWindow(contentView,
                    contentView.getMeasuredWidth(),
                    contentView.getMeasuredHeight(),
                    true);
            orderPopupWindow.setContentView(contentView);
            ColorDrawable mDrawable = new ColorDrawable(ContextCompat.getColor(this,
                    android.R.color.transparent));
            orderPopupWindow.setBackgroundDrawable(mDrawable);
            orderPopupWindow.setOutsideTouchable(true);
            orderCheckGroup = (CheckableLinearGroup) contentView.findViewById(R.id.check_group);
            orderCheckGroup.setOnCheckedChangeListener(new CheckableLinearGroup
                    .OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CheckableLinearGroup group, int checkedId) {
                    orderCheckedId = checkedId;
                    if (checkedId == R.id.cb_new) {
                        // 最新排序,倒序
                        tabLayoutViewHolder.tvSortLabel.setText(R.string.label_sort_new);
                        paramsOrder = CommunityThreadApi.COMMUNITY_POST_ORDER_DESC;
                        paramsSerialNo = Integer.MAX_VALUE;
                        reloadPostsList();
                        orderPopupWindow.dismiss();
                    } else if (checkedId == R.id.cb_hot) {
                        // 最热排序，正序
                        tabLayoutViewHolder.tvSortLabel.setText(R.string.label_sort_default);
                        paramsOrder = CommunityThreadApi.COMMUNITY_POST_ORDER_ASC;
                        paramsSerialNo = 0;
                        reloadPostsList();
                        orderPopupWindow.dismiss();
                    }
                    adapter.setTabParams(tabLayoutViewHolder.checkGroup.getCheckedId() == R.id
                                    .cb_all,
                            orderCheckedId == R.id.cb_hot);
                }
            });
        }

        // 选中当前设置的排序选项
        orderCheckGroup.check(orderCheckedId);
        orderPopupWindow.showAsDropDown(view, 0, -CommonUtil.dp2px(this, 12));
    }

    class WeddingPhotoHeaderViewHolder {
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.img_avatar)
        RoundedImageView imgAvatar;
        @BindView(R.id.img_vip)
        ImageView imgVip;
        @BindView(R.id.avatar_layout)
        RelativeLayout avatarLayout;
        @BindView(R.id.cover_layout)
        RelativeLayout coverLayout;
        @BindView(R.id.tv_nick)
        TextView tvNick;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_photo_title)
        TextView tvPhotoTitle;
        @BindView(R.id.tv_photo_count)
        TextView tvPhotoCount;
        @BindView(R.id.tv_view_count)
        TextView tvViewCount;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.tv_city_name)
        TextView tvCityName;
        @BindView(R.id.tv_photo_price)
        TextView tvPhotoPrice;
        @BindView(R.id.img_triangle)
        ImageView imgTriangle;


        WeddingPhotoHeaderViewHolder(View view) {ButterKnife.bind(this, view);}

        void setView() {
            int coverWidth = CommonUtil.getDeviceSize(CommunityThreadDetailActivity.this).x;
            int avatarOffset = CommonUtil.dp2px(CommunityThreadDetailActivity.this, 10);
            int coverHeight = coverWidth * 9 / 16 + avatarOffset;
            int avatarSize = coverWidth * 5 / 32;
            avatarLayout.getLayoutParams().height = avatarSize;
            imgTriangle.getLayoutParams().height = avatarSize - 2 * avatarOffset;
            imgAvatar.getLayoutParams().width = avatarSize;
            imgAvatar.getLayoutParams().height = avatarSize;
            imgAvatar.setCornerRadius(avatarSize / 2);
            drawTriangle(avatarSize, avatarOffset);

            Glide.with(CommunityThreadDetailActivity.this)
                    .load(ImagePath.buildPath(thread.getAuthor()
                            .getAvatar())
                            .width(avatarSize)
                            .height(avatarSize)
                            .path())
                    .into(imgAvatar);
            Glide.with(CommunityThreadDetailActivity.this)
                    .asBitmap()
                    .load(ImagePath.buildPath(thread.getWeddingPhotoContent()
                            .getPhoto()
                            .getImagePath())
                            .width(coverWidth)
                            .height(coverHeight)
                            .cropPath())
                    .apply(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
                    .into(imgCover);
            tvPhotoTitle.setText(thread.getTitle());
            tvNick.setText(thread.getAuthor()
                    .getName());
            tvDate.setText(HljTimeUtils.getWeddingDate(thread.getAuthor()
                            .getWeddingDay(),
                    thread.getAuthor()
                            .isPending(),
                    thread.getAuthor()
                            .getGender() == 1,
                    "yyyy/MM/dd"));
            if (TextUtils.isEmpty(thread.getWeddingPhotoContent()
                    .getPreface())) {
                tvContent.setVisibility(View.GONE);
            } else {
                tvContent.setText(thread.getWeddingPhotoContent()
                        .getPreface());
                tvContent.setVisibility(View.VISIBLE);
            }
            tvPhotoCount.setText(getString(R.string.label_wedding_photo_count,
                    thread.getWeddingPhotoCount()));
            if (thread.getWeddingPhotoContent()
                    .getPrice() > 0) {
                tvPhotoPrice.setVisibility(View.VISIBLE);
                tvPhotoPrice.setText(getString(R.string.label_wedding_photo_price,
                        CommonUtil.formatDouble2String(thread.getWeddingPhotoContent()
                                .getPrice())));
            } else {
                tvPhotoPrice.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(thread.getWeddingPhotoContent()
                    .getCity())) {
                tvCityName.setVisibility(View.VISIBLE);
                tvCityName.setText(getString(R.string.label_wedding_photo_city,
                        thread.getWeddingPhotoContent()
                                .getCity()));
            } else {
                tvCityName.setVisibility(View.GONE);
            }
            tvViewCount.setText(String.valueOf(thread.getWeddingPhotoContent()
                    .getWatchCount()));
            imgAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CommunityThreadDetailActivity.this,
                            UserProfileActivity.class);
                    intent.putExtra("id",
                            thread.getAuthor()
                                    .getId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            });
            if (TextUtils.isEmpty(thread.getAuthor()
                    .getSpecialty()) || thread.getAuthor()
                    .getSpecialty()
                    .equals("普通用户")) {
                if (thread.getAuthor()
                        .getMember() != null) {
                    imgVip.setVisibility(View.VISIBLE);
                    imgVip.setImageResource(R.mipmap.icon_member_28_28);
                } else {
                    imgVip.setVisibility(View.GONE);
                }
            } else {
                imgVip.setVisibility(View.VISIBLE);
                imgVip.setImageResource(R.mipmap.icon_vip_yellow_28_28);
            }
        }

        private void drawTriangle(int avatarSize, int avatarOffset) {
            int width = coverWidth;
            int height = avatarSize - 2 * avatarOffset;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint p = new Paint();
            p.setAntiAlias(true);
            p.setColor(ContextCompat.getColor(CommunityThreadDetailActivity.this,
                    R.color.colorWhite));
            Path path = new Path();
            path.moveTo(0, 0);// 此点为多边形的起点
            path.lineTo(0, height);
            path.lineTo(width, height);
            path.close(); // 使这些点构成封闭的多边形
            canvas.drawPath(path, p);
            imgTriangle.setImageBitmap(bitmap);
        }
    }


    class NormalThreadViewHolder {
        @BindView(R.id.tv_post_title)
        TextView tvPostTitle;
        @BindView(R.id.img_channel_cover)
        RoundedImageView imgChannelCover;
        @BindView(R.id.tv_from_channel)
        TextView tvFromChannel;
        @BindView(R.id.from_layout)
        LinearLayout fromLayout;
        @BindView(R.id.title_tags_layout)
        LinearLayout titleTagsLayout;
        @BindView(R.id.img_avatar)
        RoundedImageView imgAvatar;
        @BindView(R.id.img_daren)
        ImageView imgDaren;
        @BindView(R.id.tv_user_nick)
        TextView tvUserNick;
        @BindView(R.id.tv_owner_label)
        TextView tvOwnerLabel;
        @BindView(R.id.tv_user_specialty)
        TextView tvUserSpecialty;
        @BindView(R.id.tv_wedding_time)
        TextView tvWeddingTime;
        @BindView(R.id.tv_post_time)
        TextView tvPostTime;
        @BindView(R.id.web_view)
        CustomWebView webView;
        @BindView(R.id.web_view_layout)
        LinearLayout webViewLayout;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.img_4_1)
        ImageView img41;
        @BindView(R.id.img_4_2)
        ImageView img42;
        @BindView(R.id.img_4_3)
        ImageView img43;
        @BindView(R.id.img_4_4)
        ImageView img44;
        @BindView(R.id.four_images_layout)
        LinearLayout fourImagesLayout;
        @BindView(R.id.img_9_1)
        ImageView img91;
        @BindView(R.id.img_9_2)
        ImageView img92;
        @BindView(R.id.img_9_3)
        ImageView img93;
        @BindView(R.id.img_9_4)
        ImageView img94;
        @BindView(R.id.img_9_5)
        ImageView img95;
        @BindView(R.id.img_9_6)
        ImageView img96;
        @BindView(R.id.img_9_7)
        ImageView img97;
        @BindView(R.id.img_9_8)
        ImageView img98;
        @BindView(R.id.img_9_9)
        ImageView img99;
        @BindView(R.id.tv_img_count)
        TextView tvImgCount;
        @BindView(R.id.img_count_view)
        LinearLayout imgCountView;
        @BindView(R.id.img_9_9_layout)
        RelativeLayout img99Layout;
        @BindView(R.id.nine_images_line3)
        LinearLayout nineImagesLine3;
        @BindView(R.id.nine_images_layout)
        LinearLayout nineImagesLayout;
        @BindView(R.id.images_layout)
        LinearLayout imagesLayout;
        @BindView(R.id.tv_city)
        TextView tvCity;
        @BindView(R.id.tv_update_time)
        TextView tvUpdateTime;
        @BindView(R.id.img_avatar1)
        RoundedImageView imgAvatar1;
        @BindView(R.id.img_avatar2)
        RoundedImageView imgAvatar2;
        @BindView(R.id.img_avatar3)
        RoundedImageView imgAvatar3;
        @BindView(R.id.img_avatar4)
        RoundedImageView imgAvatar4;
        @BindView(R.id.img_avatar5)
        RoundedImageView imgAvatar5;
        @BindView(R.id.check_praised)
        CheckableLinearLayoutButton checkPraised;
        @BindView(R.id.praised_layout)
        LinearLayout praisedLayout;
        @BindView(R.id.tv_praise_hint)
        TextView tvPraiseHint;
        @BindView(R.id.tv_praise_count)
        TextView tvLikeCount;

        NormalThreadViewHolder(View view) {ButterKnife.bind(this, view);}

        void setView(final Context context) {
            setFromChannelView(context);
            setThreadDetails(context);
            setPraisedCountView(context);
        }

        private void setFromChannelView(final Context context) {
            if (!isFromChannel && thread.getChannel() != null) {
                fromLayout.setVisibility(View.VISIBLE);
                tvFromChannel.setText(thread.getChannel()
                        .getTitle());
                int coverSize = CommonUtil.dp2px(context, 30);
                Glide.with(context)
                        .load(ImagePath.buildPath(thread.getChannel()
                                .getCoverPath())
                                .width(coverSize)
                                .height(coverSize)
                                .path())
                        .apply(new RequestOptions().dontAnimate())
                        .into(imgChannelCover);
            } else {
                fromLayout.setVisibility(View.GONE);
            }
            fromLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommunityIntentUtil.startCommunityChannelIntent(v.getContext(),
                            thread.getChannel().getId());
                }
            });
        }

        void setPraisedCountView(final Context context) {
            tvUpdateTime.setText(HljTimeUtils.getShowTime(context, thread.getCreatedAt()));
            if (TextUtils.isEmpty(thread.getPost()
                    .getCityName())) {
                tvCity.setVisibility(View.INVISIBLE);
            } else {
                tvCity.setVisibility(View.VISIBLE);
                tvCity.setText(thread.getPost()
                        .getCityName());
            }
            checkPraised.setChecked(thread.isPraised());
            tvLikeCount.setText(String.valueOf(thread.getPraisedSum()));
            checkPraised.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimUtil.pulseAnimate(imgThumbUp);
                    onCommunityThreadPraise(checkablePraised, imgThumbUp, tvAdd, tvLikeCount);
                    // 因为点赞变化会影响点赞头像的变化,所以调用后要立即刷新
                    if (normalThreadViewHolder != null) {
                        normalThreadViewHolder.setPraisedCountView(context);
                    }
                }
            });
            ArrayList<ImageView> imgAvatars = new ArrayList<ImageView>(Arrays.asList(imgAvatar1,
                    imgAvatar2,
                    imgAvatar3,
                    imgAvatar4,
                    imgAvatar5));
            if (thread.getPraisedUsers()
                    .size() > 0) {
                tvPraiseHint.setVisibility(View.GONE);
                for (int i = 0; i < 5; i++) {
                    if (thread.getPraisedUsers()
                            .size() > i) {
                        imgAvatars.get(i)
                                .setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load(ImagePath.buildPath(thread.getPraisedUsers()
                                        .get(i)
                                        .getAvatar())
                                        .width(CommonUtil.dp2px(context, 40))
                                        .path())
                                .apply(new RequestOptions().dontAnimate())
                                .into(imgAvatars.get(i));
                    } else {
                        imgAvatars.get(i)
                                .setVisibility(View.GONE);
                    }
                }
            } else {
                tvPraiseHint.setVisibility(View.VISIBLE);
                tvPraiseHint.setText("喜欢话题，请为楼主点赞吧");
                imgAvatar1.setVisibility(View.GONE);
                imgAvatar2.setVisibility(View.GONE);
                imgAvatar3.setVisibility(View.GONE);
                imgAvatar4.setVisibility(View.GONE);
                imgAvatar5.setVisibility(View.GONE);
            }
            praisedLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ThreadPraisedUsersActivity.class);
                    intent.putExtra("id",
                            thread.getPost()
                                    .getId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            });
        }

        // 话题内容详情
        private void setThreadDetails(final Context context) {
            int emojiSize = CommonUtil.dp2px(context, 20);

            if (thread.getPages() != null) {
                // 富文本详情设置
                webViewLayout.setVisibility(View.VISIBLE);
                tvContent.setVisibility(View.GONE);
                webView.loadDataWithBaseURL(null,
                        thread.getPages()
                                .getContent(),
                        "text/html",
                        "UTF-8",
                        null);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    webView.getSettings()
                            .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                }
                Drawable drawable;
                if (thread.isRefined()) {
                    // 如果是加精，则优先加精
                    drawable = ContextCompat.getDrawable(context,
                            com.hunliji.hljcommonviewlibrary.R.mipmap.icon_refined_tag_32_32);
                } else {
                    drawable = ContextCompat.getDrawable(context,
                            com.hunliji.hljcommonviewlibrary.R.mipmap.icon_recommend_tag_32_32);
                }
                SpannableStringBuilder builder = EmojiUtil.parseEmojiByText2(context,
                        "  " + thread.getPages()
                                .getTitle(),
                        emojiSize);
                drawable.setBounds(0,
                        0,
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
                builder.setSpan(new HljImageSpan(drawable), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvPostTitle.setText(builder);
            } else {
                // 图文排列详情
                if (thread.isRefined()) {
                    SpannableStringBuilder builder = EmojiUtil.parseEmojiByText2(context,
                            "  " + thread.getTitle(),
                            emojiSize);
                    Drawable drawable = ContextCompat.getDrawable(context,
                            com.hunliji.hljcommonviewlibrary.R.mipmap.icon_refined_tag_32_32);
                    drawable.setBounds(0,
                            0,
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight());
                    builder.setSpan(new HljImageSpan(drawable),
                            0,
                            1,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvPostTitle.setText(builder);
                } else {
                    tvPostTitle.setText(EmojiUtil.parseEmojiByText2(context,
                            thread.getTitle(),
                            emojiSize));
                }
                webViewLayout.setVisibility(View.GONE);

                tvContent.setText(EmojiUtil.parseEmojiByText2(context,
                        thread.getPost()
                                .getMessage(),
                        emojiSize));
                tvContent.setOnLongClickListener(copyListener);

                // 显示图片
                if (thread.getPost()
                        .getPhotos() != null && thread.getPost()
                        .getPhotos()
                        .size() >= DETAIL_GRID_IMG_LIMIT) {
                    ImageView[] fourImages = new ImageView[]{img41, img42, img43, img44};
                    ImageView[] nineImages = new ImageView[]{img91, img92, img93, img94, img95,
                            img96, img97, img98, img99};
                    setGridImages(context,
                            fourImagesLayout,
                            nineImagesLayout,
                            fourImages,
                            nineImages,
                            img99Layout,
                            imgCountView,
                            tvImgCount);
                } else {
                    fourImagesLayout.setVisibility(View.GONE);
                    nineImagesLayout.setVisibility(View.GONE);
                    setVerticalImages(context, imagesLayout);
                }
            }

            titleTagsLayout.setVisibility(View.GONE);
            Glide.with(context)
                    .load(ImagePath.buildPath(thread.getAuthor()
                            .getAvatar())
                            .width(CommonUtil.dp2px(context, 40))
                            .height(CommonUtil.dp2px(context, 40))
                            .path())
                    .apply(new RequestOptions().dontAnimate())
                    .into(imgAvatar);
            tvUserNick.setText(thread.getAuthor()
                    .getName());
            if (TextUtils.isEmpty(thread.getAuthor()
                    .getSpecialty()) || thread.getAuthor()
                    .getSpecialty()
                    .equals("普通用户")) {
                if (thread.getAuthor()
                        .getMember() != null) {
                    imgDaren.setVisibility(View.VISIBLE);
                    imgDaren.setImageResource(R.mipmap.icon_member_28_28);
                } else {
                    imgDaren.setVisibility(View.GONE);
                }
                tvUserSpecialty.setVisibility(View.GONE);
            } else {
                imgDaren.setVisibility(View.VISIBLE);
                imgDaren.setImageResource(R.mipmap.icon_vip_yellow_28_28);
                tvUserSpecialty.setText(thread.getAuthor()
                        .getSpecialty());
                tvUserSpecialty.setVisibility(View.VISIBLE);
            }
            if (thread.getCreatedAt() != null) {
                tvPostTime.setText(DateUtils.getRelativeTimeSpanString(thread.getCreatedAt()
                                .getMillis(),
                        Calendar.getInstance()
                                .getTimeInMillis(),
                        DateUtils.MINUTE_IN_MILLIS));
            }
            tvPostTime.setVisibility(View.GONE);
            if (thread.getAuthor()
                    .getWeddingDay() != null && thread.getAuthor()
                    .isPending() != 0) {
                if (thread.getAuthor()
                        .getWeddingDay()
                        .isBefore(new DateTime())) {
                    tvWeddingTime.setText(thread.getAuthor()
                            .getGender() == 1 ? getString(R.string.label_married2) : getString(R
                            .string.label_married));
                } else {
                    tvWeddingTime.setText(getString(R.string.label_wedding_time1,
                            thread.getAuthor()
                                    .getWeddingDay()
                                    .toString("yyyy-MM-dd")));
                }
                tvWeddingTime.setVisibility(View.VISIBLE);
            } else {
                tvWeddingTime.setText(thread.getAuthor()
                        .getGender() == 1 ? "" : getString(R.string.label_no_wedding_day));
                tvWeddingTime.setVisibility(View.VISIBLE);
            }

            imgAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("id",
                            thread.getAuthor()
                                    .getId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            });

        }

        private void setVerticalImages(Context context, LinearLayout imagesLayout) {
            int threadImgWidth = CommonUtil.getDeviceSize(context).x;

            imagesLayout.removeAllViews();
            if (thread.getPost()
                    .getPhotos() == null) {
                return;
            }
            for (int i = 0; i < thread.getPost()
                    .getPhotos()
                    .size(); i++) {
                Photo photo = thread.getPost()
                        .getPhotos()
                        .get(i);
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.thread_detail_img_layout, null, false);
                ImageView imageView = (ImageView) view.findViewById(R.id.img);
                if (photo.getWidth() > 0 && photo.getHeight() > 0) {
                    int height = threadImgWidth * photo.getHeight() / photo.getWidth();
                    Glide.with(context)
                            .load(ImagePath.buildPath(photo.getImagePath())
                                    .width(threadImgWidth)
                                    .height(height)
                                    .path())
                            .apply(new RequestOptions().override(threadImgWidth, height)
                                    .fitCenter())
                            .into(imageView);
                } else {
                    Glide.with(context)
                            .load(ImagePath.buildPath(photo.getImagePath())
                                    .width(threadImgWidth)
                                    .path())
                            .listener(new OriginalImageScaleListener(imageView, threadImgWidth, 0))
                            .into(imageView);
                }
                imageView.setOnClickListener(new OnPicClickListener(context,
                        thread.getPost()
                                .getPhotos(),
                        i));

                imagesLayout.addView(view);
            }
        }

        private void setGridImages(
                Context context,
                LinearLayout fourImagesLayout,
                LinearLayout nineImagesLayout,
                ImageView[] fourImages,
                ImageView[] nineImages,
                RelativeLayout img99Layout,
                View imgCountView,
                TextView tvImgCount) {
            int fourGridImgSize = (CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context,
                    28)) / 2;
            int nineGridImgSize = (CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context,
                    32)) / 3;
            ArrayList<Photo> photos = thread.getPost()
                    .getPhotos();
            if (photos.size() > 4) {
                fourImagesLayout.setVisibility(View.GONE);
                nineImagesLayout.setVisibility(View.VISIBLE);
                for (int i = 0; i < 9; i++) {
                    ImageView imageView = nineImages[i];
                    if (i < photos.size()) {
                        imageView.setVisibility(View.VISIBLE);
                        imageView.getLayoutParams().width = nineGridImgSize;
                        imageView.getLayoutParams().height = nineGridImgSize;
                        Glide.with(context)
                                .load(ImagePath.buildPath(photos.get(i)
                                        .getImagePath())
                                        .width(nineGridImgSize)
                                        .height(nineGridImgSize)
                                        .path())
                                .into(imageView);
                        imageView.setOnClickListener(new OnPicClickListener(context, photos, i));
                    } else {
                        imageView.setVisibility(View.GONE);
                    }
                    if (i == 8) {
                        img99Layout.setVisibility(View.VISIBLE);
                        if (photos.size() > 9) {
                            imgCountView.setVisibility(View.VISIBLE);
                            tvImgCount.setText(String.valueOf(photos.size()));
                        } else {
                            imgCountView.setVisibility(View.GONE);
                        }
                    }
                }
                if (photos.size() > 6) {
                    nineImagesLayout.findViewById(R.id.nine_images_line3)
                            .setVisibility(View.VISIBLE);
                } else {
                    nineImagesLayout.findViewById(R.id.nine_images_line3)
                            .setVisibility(View.GONE);
                }
            } else {
                fourImagesLayout.setVisibility(View.VISIBLE);
                nineImagesLayout.setVisibility(View.GONE);
                for (int i = 0; i < 4; i++) {
                    ImageView imageView = fourImages[i];
                    if (i < photos.size()) {
                        imageView.setVisibility(View.VISIBLE);
                        imageView.getLayoutParams().width = fourGridImgSize;
                        imageView.getLayoutParams().height = fourGridImgSize;
                        Glide.with(context)
                                .load(ImagePath.buildPath(photos.get(i)
                                        .getImagePath())
                                        .width(fourGridImgSize)
                                        .height(fourGridImgSize)
                                        .path())
                                .into(imageView);
                        imageView.setOnClickListener(new OnPicClickListener(context, photos, i));
                    } else {
                        imageView.setVisibility(View.GONE);
                    }
                }
            }
        }

    }


    private static class OnPicClickListener implements View.OnClickListener, AdapterView
            .OnItemClickListener {
        private Context context;
        private ArrayList<Photo> photos;
        private int position;

        public OnPicClickListener(
                Context context, ArrayList<Photo> photos, int position) {
            this.context = context;
            this.photos = photos;
            this.position = position;
        }

        public OnPicClickListener(
                Context context, ArrayList<Photo> photos) {
            this.context = context;
            this.photos = photos;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, PicsPageViewActivity.class);
            intent.putExtra("photos", photos);
            intent.putExtra("position", position);
            context.startActivity(intent);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int p, long id) {
            Intent intent = new Intent(context, PicsPageViewActivity.class);
            intent.putExtra("photos", photos);
            intent.putExtra("position", p);
            context.startActivity(intent);
        }
    }

    class TabLayoutViewHolder implements CheckableLinearGroup.OnCheckedChangeListener {
        @BindView(R.id.cb_all)
        CheckableLinearButton cbAll;
        @BindView(R.id.cb_owner)
        CheckableLinearButton cbOwner;
        @BindView(R.id.check_group)
        CheckableLinearGroup checkGroup;
        @BindView(R.id.tv_sort_label)
        TextView tvSortLabel;
        @BindView(R.id.sort_layout)
        LinearLayout sortLayout;
        @BindView(R.id.content_layout)
        RelativeLayout contentLayout;
        @BindView(R.id.head_loading_view)
        LinearLayout headLoadingView;
        @BindView(R.id.tab_layout)
        LinearLayout tabLayout;

        TabLayoutViewHolder(View view) {ButterKnife.bind(this, view);}

        void setView() {
            checkGroup.setOnCheckedChangeListener(this);
            headLoadingView.setVisibility(View.GONE);
        }

        @Override
        public void onCheckedChanged(CheckableLinearGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.cb_all:
                    adapter.setTabParams(true, true);
                    onAllChecked();
                    break;
                case R.id.cb_owner:
                    adapter.setTabParams(false, true);
                    onOwnerChecked();
                    break;
            }
        }

        @OnClick(R.id.sort_layout)
        public void onSortLayout(View view) {
            onOrderMenuClicked(view);
        }
    }

    class FinishShareViewHolder {
        @BindView(R.id.btn_close)
        ImageButton btnClose;
        @BindView(R.id.tv_coin)
        TextView tvCoin;
        @BindView(R.id.share_pengyou)
        LinearLayout sharePengyou;
        @BindView(R.id.share_weixing)
        LinearLayout shareWeixing;
        @BindView(R.id.share_weibo)
        LinearLayout shareWeibo;
        @BindView(R.id.share_qq)
        LinearLayout shareQq;

        ShareUtil shareUtil;
        Dialog dialog;

        FinishShareViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        private void setView(Dialog dialog, ShareInfo shareInfo) {
            this.dialog = dialog;
            shareUtil = new ShareUtil(CommunityThreadDetailActivity.this, shareInfo, null);
            if (shareGold > 0) {
                tvCoin.setVisibility(View.VISIBLE);
                tvCoin.setText("+" + shareGold);
            } else {
                tvCoin.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.share_pengyou)
        void sharePengYou() {
            shareUtil.shareToPengYou();
            dialog.cancel();
        }

        @OnClick(R.id.share_weixing)
        void shareWeiXing() {
            shareUtil.shareToWeiXin();
            dialog.cancel();
        }

        @OnClick(R.id.share_weibo)
        void shareWeibo() {
            shareUtil.shareToWeiBo();
            dialog.cancel();
        }

        @OnClick(R.id.share_qq)
        void shareQQ() {
            shareUtil.shareToQQ();
            dialog.cancel();
        }

        @OnClick(R.id.btn_close)
        void onClose() {
            dialog.cancel();
        }
    }


    private View.OnLongClickListener copyListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(final View view) {
            if (copyPopupWindow != null && copyPopupWindow.isShowing()) {
                copyPopupWindow.dismiss();
            }
            if (copyPopupWindow == null) {
                copyPopupWindow = new CopyPopupWindow(CommunityThreadDetailActivity.this,
                        R.layout.copy_menu);
            }
            view.setBackgroundColor(ContextCompat.getColor(CommunityThreadDetailActivity.this,
                    R.color.colorGray));
            View v = copyPopupWindow.getContentView();
            v.findViewById(R.id.copy_btn)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager cmb = (ClipboardManager) getSystemService(
                                    CLIPBOARD_SERVICE);
                            cmb.setPrimaryClip(ClipData.newPlainText(getString(R.string.app_name),
                                    thread.getPost()
                                            .getMessage()));
                            if (copyPopupWindow != null) {
                                copyPopupWindow.dismiss();
                                view.setBackgroundColor(ContextCompat.getColor(
                                        CommunityThreadDetailActivity.this,
                                        R.color.colorWhite));
                            }
                            Toast.makeText(CommunityThreadDetailActivity.this,
                                    R.string.hint_post_copy,
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
            int width = copyPopupWindow.getWidth();
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int xoff = (screenWidth - width) / 2;
            int yoff = copyPopupWindow.getHeight() + Math.max(view.getBottom() - recyclerView
                            .getHeight(),
                    0) + (Math.min(view.getBottom(),
                    recyclerView.getHeight()) - Math.max(view.getTop(), 0)) / 2;
            copyPopupWindow.showAsDropDown(view, xoff, -yoff);
            return true;
        }
    };


    private RecyclerView.OnScrollListener tabViewScrollListener = new RecyclerView
            .OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (isPageHead && !posts.isEmpty() && layoutManager.getChildCount() > 0) {
                invalidateTabView();
            }
            super.onScrolled(recyclerView, dx, dy);
        }
    };
    private ScrollableLayout.OnScrollListener alphaScrollListener = new ScrollableLayout
            .OnScrollListener() {
        @Override
        public void onScroll(int currentY, int maxY) {
            if (currentY > alphaHeight) {
                actionLayout.setAlpha(1);
                shadowView.setAlpha(0);
            } else {
                float f = (float) currentY / alphaHeight;
                actionLayout.setAlpha(f);
                shadowView.setAlpha(1 - f);
            }
        }
    };

    private ThreadDetailsAdapter.OnPraisePostListener onPraisePostListener = new
            ThreadDetailsAdapter.OnPraisePostListener() {
        @Override
        public void onPraisePost(
                int position,
                CommunityPost post,
                CheckableLinearLayoutButton checkPraised,
                ImageView imgThumb,
                TextView tvPraisedCount,
                TextView tvAdd) {
            CommunityTogglesUtil.onCommunityPostPraise(CommunityThreadDetailActivity.this,
                    checkPraised,
                    imgThumb,
                    tvPraisedCount,
                    tvAdd,
                    post);
        }
    };

    private ThreadDetailsAdapter.OnItemReplyListener onItemReplyListener = new
            ThreadDetailsAdapter.OnItemReplyListener() {
        @Override
        public void onItemReply(
                int position, CommunityPost post) {
            onReplyPost(post, false);
        }
    };

    private ThreadDetailsAdapter.OnItemClickListener onItemClickListener = new
            ThreadDetailsAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(
                int position, CommunityPost post) {
            showPostEditDialog(position, post);
        }
    };

    private ThreadDetailsAdapter.OnPraiseGroupPhotoListener onPraiseGroupPhotoListener = new
            ThreadDetailsAdapter.OnPraiseGroupPhotoListener() {
        @Override
        public void onPraiseGroup(
                int position,
                final WeddingPhotoItem item,
                final CheckableLinearLayoutButton checkPraised,
                ImageView imgThumb,
                final TextView tvPraisedCount,
                TextView tvAdd) {
            checkPraised.setClickable(false);
            if (item.isPraised()) {
                item.setLikesCount(item.getLikesCount() - 1);
                thread.setPraisedSum(thread.getPraisedSum() - 1);
            } else {
                AnimUtil.pulseAnimate(imgThumb);
                AnimUtil.zoomInUpAnimate(tvAdd);
                item.setLikesCount(item.getLikesCount() + 1);
                thread.setPraisedSum(thread.getPraisedSum() + 1);
            }
            item.setPraised(!item.isPraised());
            checkPraised.setChecked(item.isPraised());
            tvPraisedCount.setText(item.getLikesCount() > 0 ? String.valueOf(item.getLikesCount()
            ) : "赞");
            setBottomPraisedView();
            HljHttpSubscriber praisePhotoSub = HljHttpSubscriber.buildSubscriber(
                    CommunityThreadDetailActivity.this)
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            item.setPraised(!item.isPraised());
                            checkPraised.setChecked(item.isPraised());
                            if (item.isPraised()) {
                                item.setLikesCount(item.getLikesCount() - 1);
                            } else {
                                item.setLikesCount(item.getLikesCount() + 1);
                            }
                            tvPraisedCount.setText(item.getLikesCount() > 0 ? String.valueOf(item
                                    .getLikesCount()) : "赞");
                            checkPraised.setClickable(true);
                        }
                    })
                    .setOnNextListener(new SubscriberOnNextListener<CommunityPostPraiseResult>() {
                        @Override
                        public void onNext(CommunityPostPraiseResult communityPostPraiseResult) {
                            Util.showToast(communityPostPraiseResult.getBelongPraise() > 0 ? R
                                            .string.hint_praised_complete : R.string.hint_dispraised_complete,
                                    CommunityThreadDetailActivity.this);
                            checkPraised.setClickable(true);
                        }
                    })
                    .build();
            CommunityThreadApi.praiseWeddingPhotoGroup(item.getId())
                    .subscribe(praisePhotoSub);
        }
    };

}
