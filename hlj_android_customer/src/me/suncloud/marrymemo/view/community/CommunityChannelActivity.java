package me.suncloud.marrymemo.view.community;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableHelper;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.makeramen.rounded.RoundedImageView;

import net.robinx.lib.blur.StackBlur;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.community.CommunityApi;
import me.suncloud.marrymemo.fragment.community.CommunityFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.CommunityTogglesUtil;
import me.suncloud.marrymemo.util.ImageLoadUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.RecentChannelUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.CustomTextView;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func2;

/**
 * Created by mo_yu on 2016/5/9. 社区频道
 */
@Route(path = RouterPath.IntentPath.Customer.COMMUNITY_CHANNEL)
public class CommunityChannelActivity extends HljBaseNoBarActivity implements TabPageIndicator
        .OnTabChangeListener {
    @BindView(R.id.iv_channel_head)
    RoundedImageView ivChannelHead;
    @BindView(R.id.tv_channel_title)
    TextView tvChannelTitle;
    @BindView(R.id.tv_channel_threads_count)
    TextView tvChannelThreadsCount;
    @BindView(R.id.tv_channel_watch_count)
    TextView tvChannelWatchCount;
    @BindView(R.id.tv_channel_focus)
    TextView tvChannelFocus;
    @BindView(R.id.tv_channel_focused)
    TextView tvChannelFocused;
    @BindView(R.id.rl_channel_focus)
    RelativeLayout rlChannelFocus;
    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.scrollable_layout)
    ScrollableLayout scrollableLayout;
    @BindView(R.id.shadow_view)
    LinearLayout shadowView;
    @BindView(R.id.action_layout)
    LinearLayout actionLayout;
    @BindView(R.id.tv_community_channel_title)
    TextView tvCommunityChannelTitle;
    @BindView(R.id.iv_community_channel_back)
    ImageButton ivCommunityChannelBack;
    @BindView(R.id.tv_community_channel_title1)
    TextView tvCommunityChannelTitle1;
    @BindView(R.id.iv_community_channel_bg)
    ImageView ivCommunityChannelBg;
    @BindView(R.id.community_channel_view)
    View communityChannelView;
    @BindView(R.id.community_header_layout)
    LinearLayout communityHeaderLayout;
    @BindView(R.id.top_threads_layout)
    LinearLayout topThreadsLayout;
    @BindView(R.id.top_threads_view)
    RelativeLayout topThreadsView;
    @BindView(R.id.btn_create_thread_hint)
    ImageButton btnCreateThreadHint;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_add_view)
    TextView tvAddView;
    @BindView(R.id.community_add_view)
    LinearLayout communityAddView;
    @BindView(R.id.bg_layout)
    RelativeLayout bgLayout;
    private long id;
    private int headWidth;
    private int topHeadWidth;
    private int height;
    private int shadowHeight;
    private int type;
    private CommunityChannel communityChannel;
    private City city;
    private Handler mHandler;
    private ArrayList<CommunityThread> topThreads;//置顶视图
    private SparseArray<CommunityFragment> fragments;
    private HljHttpSubscriber initSubscriber;
    private Subscription rxBusSub;

    @Override
    public String pageTrackTagName() {
        return "社区频道";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_channel);
        ButterKnife.bind(this);

        initValue();
        initView();
        saveRecentChannelIds();
        initTracker();
        onRefresh();
    }

    private void initTracker() {
        JSONObject siteJson = null;
        String site = getIntent().getStringExtra("site");
        if (!TextUtils.isEmpty(site)) {
            try {
                siteJson = new JSONObject(site);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        new HljTracker.Builder(this).eventableId(id)
                .eventableType("CommunityChannel")
                .action("hit")
                .site(siteJson)
                .build()
                .send();
    }

    private void initValue() {
        id = getIntent().getLongExtra("id", 0);
        mHandler = new Handler();
        headWidth = CommonUtil.dp2px(this, 66);
        topHeadWidth = CommonUtil.dp2px(this, 660);
        height = CommonUtil.dp2px(this, 44);
        shadowHeight = CommonUtil.dp2px(this, 94);
        topThreads = new ArrayList<>();
        fragments = new SparseArray<>();
        city = Session.getInstance()
                .getMyCity(this);
    }

    private void initActionBar() {
        height = height + getStatusBarHeight();
        shadowHeight = shadowHeight - getStatusBarHeight();
        setActionBarPadding(actionLayout, shadowView);
        bgLayout.getLayoutParams().height = CommonUtil.dp2px(this, 138) + getStatusBarHeight();
    }

    private void initView() {
        initActionBar();
        actionLayout.setAlpha(0);
        shadowView.setAlpha(1);
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        indicator.setPagerAdapter(pagerAdapter);
        indicator.setOnTabChangeListener(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
                scrollableLayout.getHelper()
                        .setCurrentScrollableContainer(getCurrentScrollableContainer());
            }
        });
        scrollableLayout.setExtraHeight(height);
        scrollableLayout.setHeaderView(communityHeaderLayout);
        scrollableLayout.addOnScrollListener(new ScrollableLayout.OnScrollListener() {
            @Override
            public void onScroll(int currentY, int maxY) {
                scrollableLayout.getHelper()
                        .setCurrentScrollableContainer(getCurrentScrollableContainer());
                if (currentY > shadowHeight) {
                    actionLayout.setAlpha(1);
                    shadowView.setAlpha(0);
                } else {
                    float f = (float) currentY / shadowHeight;
                    actionLayout.setAlpha(f);
                    shadowView.setAlpha(1 - f);
                }
            }
        });
        actionLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return actionLayout.getAlpha() == 1;
            }
        });
        registerRxBusEvent();
    }

    private void saveRecentChannelIds() {
        List<Long> recentChannelIds = RecentChannelUtil.getRecentChannelIds(this);
        for (int i = 0; i < recentChannelIds.size(); i++) {
            if (recentChannelIds.get(i) == id) {
                recentChannelIds.remove(i);
                i--;
            }
        }
        recentChannelIds.add(0, id);
        RecentChannelUtil.setRecentChannelIds(this, recentChannelIds);
    }

    //当用户首次进入频道页时，显示该引导
    private void showFirstEnterTips() {
        if (type == 0) {
            SharedPreferences preferences = getSharedPreferences(Constants.PREF_FILE,
                    Context.MODE_PRIVATE);
            if (!preferences.getBoolean(Constants.PREF_CREATE_THREAD_HINT_CLICKED, false)) {
                preferences.edit()
                        .putBoolean(Constants.PREF_CREATE_THREAD_HINT_CLICKED, true)
                        .apply();
                btnCreateThreadHint.setVisibility(View.VISIBLE);
                btnCreateThreadHint.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onClickedCreateThreadHint();
                    }
                }, 3000);
            }
        }
    }

    public void onRefresh() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            Observable tObservable = CommunityApi.getTopThreadsObb(id, city.getId());
            Observable cObservable = CommunityApi.getCommunityChannelDetailObb(id);
            Observable observable = Observable.zip(tObservable,
                    cObservable,
                    new Func2<List<CommunityThread>, CommunityChannel, ResultZip>() {
                        @Override
                        public ResultZip call(List<CommunityThread> data, CommunityChannel data2) {
                            ResultZip resultZip = new ResultZip();
                            resultZip.topThreads = data;
                            resultZip.communityChannel = data2;
                            return resultZip;
                        }
                    });
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            communityChannel = resultZip.communityChannel;
                            if (communityChannel != null) {
                                type = communityChannel.getType();
                                showFirstEnterTips();
                                communityAddView.setVisibility(View.VISIBLE);
                                if (type == 1) {
                                    tvAddView.setText(R.string.label_wedding_dress_show);
                                } else if (type == 0) {
                                    tvAddView.setText(R.string.title_activity_edit_thread);
                                }
                                if (resultZip.topThreads != null) {
                                    topThreads.clear();
                                    topThreads.addAll(resultZip.topThreads);
                                }
                                initTopView();
                                showTopThreadsList();
                            }
                        }
                    })
                    .setProgressBar(progressBar)
                    .setContentView(scrollableLayout)
                    .build();
            observable.subscribe(initSubscriber);
        }
    }

    private static class ResultZip extends HljHttpResultZip {
        @HljRZField
        List<CommunityThread> topThreads;
        @HljRZField
        CommunityChannel communityChannel;
    }

    /**
     * 注册RxBux事件，同步晒婚纱照发布成功信息，及时刷新
     */
    private void registerRxBusEvent() {
        if (rxBusSub == null || rxBusSub.isUnsubscribed()) {
            rxBusSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case THREAD_WEDDING_PHOTO_RELEASED_SUCCESS:
                                    onRefreshCommunityFragment();
                                    break;
                            }
                        }
                    });
        }
    }

    /**
     * 刷新CommunityFragment
     */
    private void onRefreshCommunityFragment() {
        if (fragments != null && indicator != null && viewPager != null) {
            for (int i = 0, size = fragments.size(); i < size; i++) {
                CommunityFragment fragment = fragments.get(i);
                if (fragment != null && fragment.getScrollableView() != null) {
                    if (viewPager.getCurrentItem() == i) {
                        fragment.refresh();
                    } else {
                        fragment.setNeedRefresh(true);
                    }
                }
            }
            indicator.setCurrentItem(1);
            viewPager.setCurrentItem(1);
        }
    }

    /**
     * 置顶话题
     */
    private void showTopThreadsList() {
        if (topThreads.size() > 0) {
            LayoutInflater inflater = LayoutInflater.from(this);
            topThreadsView.setVisibility(View.VISIBLE);
            topThreadsLayout.removeAllViews();
            for (int i = 0; i < topThreads.size(); i++) {
                final CommunityThread thread = topThreads.get(i);
                if (inflater != null) {
                    View topThreadView = inflater.inflate(R.layout.community_top_thread_layout,
                            topThreadsLayout,
                            false);
                    CustomTextView titleView = (CustomTextView) topThreadView.findViewById(R.id
                            .title);
                    View topView = topThreadView.findViewById(R.id.topview);
                    View bottomLine = topThreadView.findViewById(R.id.bottom_line);

                    int size = 0;
                    int maxWidth = CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 77);
                    maxWidth -= CommonUtil.dp2px(this, 23);
                    topView.setVisibility(View.VISIBLE);
                    titleView.setPadding(0,
                            0,
                            CommonUtil.dp2px(this, 19) * size + CommonUtil.dp2px(this, 19) / 3,
                            0);
                    titleView.setImageSpanText(thread.getShowTitle(),
                            CommonUtil.dp2px(this, 18),
                            ImageSpan.ALIGN_BOTTOM,
                            maxWidth);
                    topThreadsLayout.addView(topThreadView);
                    topThreadView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(CommunityChannelActivity.this,
                                    CommunityThreadDetailActivity.class);
                            intent.putExtra("id", thread.getId());
                            intent.putExtra("is_from_channel", true);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    });
                    if (i == topThreads.size() - 1) {
                        bottomLine.setVisibility(View.GONE);
                    } else {
                        bottomLine.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else {
            topThreadsView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.COMPLETE_PROFILE:
                    if (data != null && data.getBooleanExtra("modified", false)) {
                        onNewThread(null);
                    }
                    break;
                case Constants.RequestCode.SEND_WEDDING_PHOTO_COMPLETE:
                case Constants.RequestCode.SEND_THREAD_COMPLETE:
                    onRefreshCommunityFragment();
                    break;
            }
        }
    }

    //顶部视图
    private void initTopView() {
        //频道头像
        if (communityChannel != null) {
            String url1 = JSONUtil.getImagePath2(communityChannel.getCoverPath(), topHeadWidth);
            if (!JSONUtil.isEmpty(url1)) {
                Glide.with(this)
                        .asBitmap()
                        .load(url1)
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(
                                    final Bitmap resource, Transition glideAnimation) {
                                if (resource != null) {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ivCommunityChannelBg.setImageBitmap(StackBlur
                                                    .blurNativelyPixels(
                                                    resource,
                                                    50,
                                                    false));
                                        }
                                    });

                                }
                            }
                        });
            } else {
                ImageLoadUtil.clear(this, ivCommunityChannelBg);
                ivCommunityChannelBg.setImageBitmap(null);
            }
            String url = JSONUtil.getImagePath(communityChannel.getCoverPath(), headWidth);
            if (!JSONUtil.isEmpty(url)) {
                ImageLoadUtil.loadImageView(this,
                        url,
                        R.mipmap.icon_empty_image,
                        ivChannelHead,
                        true);
            } else {
                ImageLoadUtil.clear(this, ivChannelHead);
                ivChannelHead.setImageBitmap(null);
            }
            communityChannelView.setVisibility(View.VISIBLE);
            tvCommunityChannelTitle.setText(communityChannel.getTitle());
            tvCommunityChannelTitle1.setText(communityChannel.getTitle());
            tvChannelTitle.setText(communityChannel.getDesc());
            tvChannelThreadsCount.setText(getString(R.string.label_community_thread_count,
                    String.valueOf(communityChannel.getThreadsCount())));
            tvChannelWatchCount.setText(getString(R.string.label_find_channel_popularity,
                    String.valueOf(communityChannel.getWatchCount())));
            if (communityChannel.isFollowed()) {
                tvChannelFocus.setVisibility(View.GONE);
                tvChannelFocused.setVisibility(View.VISIBLE);
            } else {
                tvChannelFocus.setVisibility(View.VISIBLE);
                tvChannelFocused.setVisibility(View.GONE);
            }
            tvChannelFocus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    CommunityTogglesUtil.onCommunityChannelFollow(CommunityChannelActivity.this,
                            id,
                            communityChannel,
                            tvChannelFocus,
                            tvChannelFocused);
                }
            });

            tvChannelFocused.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommunityTogglesUtil.onCommunityChannelFollow(CommunityChannelActivity.this,
                            id,
                            communityChannel,
                            tvChannelFocus,
                            tvChannelFocused);
                }
            });

        }
    }

    @OnClick(R.id.community_add_view)
    public void onNewThread(View view) {
        if (btnCreateThreadHint.getVisibility() == View.VISIBLE) {
            onClickedCreateThreadHint();
            return;
        }
        if (!Util.loginBindChecked(this, Constants.Login.SEND_THREAD_LOGIN)) {
            return;
        }
        if (type == 0) {
            if (communityChannel != null) {
                Intent intent = new Intent(this, CreateThreadActivity.class);
                intent.putExtra(CreateThreadActivity.ARG_CHANNEL, communityChannel);
                startActivityForResult(intent, Constants.RequestCode.SEND_THREAD_COMPLETE);
                overridePendingTransition(R.anim.slide_in_from_bottom,
                        R.anim.activity_anim_default);
            }
        } else if (type == 1) {
            Intent intent = new Intent(this, CreateWeddingPhotoActivity.class);
            startActivityForResult(intent, Constants.RequestCode.SEND_WEDDING_PHOTO_COMPLETE);
        }
    }

    //点击取消提示图片
    @OnClick(R.id.btn_create_thread_hint)
    void onClickedCreateThreadHint() {
        if (btnCreateThreadHint != null) {
            btnCreateThreadHint.setVisibility(View.GONE);
        }
    }

    public void onBackPressed(View v) {
        super.onBackPressed();
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            CommunityFragment fragment = fragments.get(position);
            if (fragment != null) {
                return fragment;
            }
            switch (position) {
                case 0:
                    fragment = CommunityFragment.newInstance(getUrl("default"),
                            true,
                            true,
                            true,
                            false);
                    break;
                case 1:
                    fragment = CommunityFragment.newInstance(getUrl("latest"),
                            true,
                            false,
                            true,
                            false);
                    break;
                case 2:
                    fragment = CommunityFragment.newInstance(getUrl("marrow"),
                            false,
                            true,
                            true,
                            false);
                    break;
            }
            if (fragment != null) {
                fragments.put(position, fragment);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1:
                    return getString(R.string.label_newest_thread).toUpperCase();
                case 2:
                    return getString(R.string.label_essence).toUpperCase();
                default:
                    return getString(R.string.label_all).toUpperCase();
            }
        }
    }

    private String getUrl(String sort) {
        return String.format(Constants.HttpPath.GET_COMMUNITY_CHANNEL_THREADS_V2,
                id,
                sort,
                city.getId());
    }

    //获取当前fragment
    private ScrollableHelper.ScrollableContainer getCurrentScrollableContainer() {
        if (viewPager.getAdapter() != null && viewPager.getAdapter() instanceof
                SectionsPagerAdapter) {
            SectionsPagerAdapter adapter = (SectionsPagerAdapter) viewPager.getAdapter();
            Fragment fragment = (Fragment) adapter.instantiateItem(viewPager,
                    viewPager.getCurrentItem());
            if (fragment != null && fragment instanceof ScrollableHelper.ScrollableContainer) {
                return (ScrollableHelper.ScrollableContainer) fragment;
            }
        }
        return null;
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSubscriber, rxBusSub);
    }

}