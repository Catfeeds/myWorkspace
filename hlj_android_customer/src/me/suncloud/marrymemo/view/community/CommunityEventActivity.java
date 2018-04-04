package me.suncloud.marrymemo.view.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.event.CommunityEvent;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.community.CommunityApi;
import me.suncloud.marrymemo.fragment.community.CommunityChannelEventFragment;
import me.suncloud.marrymemo.fragment.community.CommunityThreadListFragment;

/**
 * 新娘说活动详情
 * Created by jinxin on 2018/3/16 0016.
 */

public class CommunityEventActivity extends HljBaseNoBarActivity {

    public static final String ARG_ID = "id";
    public static final String ARG_POSITION = "position";
    private final int REQUESTION_CODE = 101;

    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.rl_img)
    RelativeLayout rlImg;
    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_share)
    ImageButton btnShare;
    @BindView(R.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R.id.btn_back2)
    ImageButton btnBack2;
    @BindView(R.id.btn_share2)
    ImageButton btnShare2;
    @BindView(R.id.action_holder_layout2)
    LinearLayout actionHolderLayout2;
    @BindView(R.id.layout_avatar)
    LinearLayout layoutAvatar;
    @BindView(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tv_title2)
    TextView tvTitle2;
    @BindView(R.id.tv_watch_count)
    TextView tvWatchCount;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.ll_message)
    LinearLayout llMessage;
    @BindView(R.id.btn_join)
    TextView btnJoin;
    @BindView(R.id.rl_join)
    RelativeLayout rlJoin;

    private int imgWidth;
    private int imgHeight;
    private int logoSize;
    private EventFragmentAdapter eventFragmentAdapter;
    private HljHttpSubscriber loadSub;
    private long id;
    private ShareInfo shareInfo;
    private CommunityChannelEventFragment eventFragment;
    private CommunityThreadListFragment threadListFragment;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_channel_event);
        ButterKnife.bind(this);

        initValues();
        initWidget();
        initLoad();
        initTracker();
    }

    private void initValues() {
        id = getIntent().getLongExtra(ARG_ID, 0L);
        position = getIntent().getIntExtra(ARG_POSITION, 0);
        logoSize = CommonUtil.dp2px(this, 14);
        imgWidth = CommonUtil.getDeviceSize(this).x;
        imgHeight = imgWidth * 424 / 750;
        eventFragmentAdapter = new EventFragmentAdapter(getSupportFragmentManager());
    }

    private void initWidget() {
        setActionBarPadding(actionHolderLayout, actionHolderLayout2);
        rlImg.getLayoutParams().width = imgWidth;
        rlImg.getLayoutParams().height = imgHeight;
        appbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float alpha = 0F;
                if (Math.abs(verticalOffset) > appBarLayout.getTotalScrollRange()) {
                    alpha = 1F;
                } else {
                    alpha = Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange();
                }
                llMessage.setAlpha(1F - alpha);
                actionHolderLayout2.setAlpha(alpha);
                CommunityThreadListFragment fragment = getThreadListFragment();
                if (fragment != null) {
                    PullToRefreshVerticalRecyclerView view = (PullToRefreshVerticalRecyclerView)
                            fragment.getScrollableView();
                    if (view != null) {
                        view.setMode(verticalOffset == 0 ? PullToRefreshBase.Mode.PULL_FROM_START
                                : PullToRefreshBase.Mode.DISABLED);
                    }
                }
            }
        });
        viewPager.setAdapter(eventFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0, size = eventFragmentAdapter.getCount(); i < size; i++) {
            View tabView = getLayoutInflater().inflate(R.layout.main_tab_view_with_count___cm,
                    null,
                    false);
            TabViewHolder tabViewHolder = new TabViewHolder(tabView, i);
            tabView.setTag(tabViewHolder);
            tabViewHolder.setTabView(i);
            tabLayout.getTabAt(i)
                    .setCustomView(tabView);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    tab.getCustomView()
                            .findViewById(R.id.tv_count)
                            .setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setCurrentItem(position);
    }

    private void initLoad() {
        CommonUtil.unSubscribeSubs(loadSub);
        loadSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<CommunityEvent>() {
                    @Override
                    public void onNext(CommunityEvent communityEvent) {
                        setCommunityEvent(communityEvent);
                    }
                })
                .build();
        CommunityApi.getCommunityEventDetail(id)
                .subscribe(loadSub);
    }

    private void initTracker() {
        HljVTTagger.buildTagger(rlJoin)
                .tagName(HljTaggerName.BTN_JOIN)
                .hitTag();
        HljVTTagger.buildTagger(btnShare2)
                .tagName(HljTaggerName.BTN_SHARE)
                .hitTag();
    }

    private void setCommunityEvent(CommunityEvent communityEvent) {
        if (communityEvent == null) {
            rlJoin.setVisibility(View.GONE);
            emptyView.showEmptyView();
            return;
        }
        rlJoin.setVisibility(View.VISIBLE);
        emptyView.hideEmptyView();
        tvTitle.setText(communityEvent.getTitle());
        tvTitle2.setText(communityEvent.getTitle());
        shareInfo = communityEvent.getShare();
        Glide.with(this)
                .load(ImagePath.buildPath(communityEvent.getImage())
                        .width(imgWidth)
                        .height(imgHeight)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(imgCover);
        if (!CommonUtil.isCollectionEmpty(communityEvent.getLastUserList())) {
            layoutAvatar.setVisibility(View.VISIBLE);
            int childCount = layoutAvatar.getChildCount();
            for (int i = 0; i < childCount; i++) {
                RoundedImageView imageView = (RoundedImageView) layoutAvatar.getChildAt(i);
                if (i <= communityEvent.getLastUserList()
                        .size() - 1) {
                    imageView.setVisibility(View.VISIBLE);
                    Author author = communityEvent.getLastUserList()
                            .get(i);
                    String path = null;
                    if (author != null) {
                        path = author.getAvatar();
                    }
                    if (!TextUtils.isEmpty(path)) {
                        Glide.with(this)
                                .load(ImagePath.buildPath(path)
                                        .width(logoSize)
                                        .height(logoSize)
                                        .cropPath())
                                .apply(new RequestOptions().placeholder(R.mipmap
                                        .icon_avatar_primary))
                                .into(imageView);
                    } else {
                        imageView.setImageResource(R.mipmap.icon_avatar_primary);
                    }
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }
        } else {
            layoutAvatar.setVisibility(View.INVISIBLE);
        }
        tvWatchCount.setText(String.valueOf(communityEvent.getWatchCount()) + "人感兴趣");
        tvWatchCount.setVisibility(communityEvent.getWatchCount() > 0 ? View.VISIBLE : View.GONE);
        if (communityEvent.getPostCount() > 0) {
            if (tabLayout.getTabCount() >= 2) {
                View tabView = tabLayout.getTabAt(1)
                        .getCustomView();
                if (tabView != null) {
                    TabViewHolder holder = (TabViewHolder) tabView.getTag();
                    if (holder != null) {
                        holder.setCount(communityEvent.getPostCount());
                    }
                }
            }
        }
        eventFragment.refresh(communityEvent);
    }

    private CommunityThreadListFragment getThreadListFragment() {
        Fragment f = eventFragmentAdapter.getItem(viewPager.getCurrentItem());
        if (f != null && f instanceof CommunityThreadListFragment) {
            CommunityThreadListFragment fragment = (CommunityThreadListFragment) f;
            return fragment;
        }
        return null;
    }

    @OnClick(R.id.btn_share2)
    void onShare() {
        ShareDialogUtil.onCommonShare(this, shareInfo);
    }

    @OnClick(R.id.btn_back2)
    void onBack() {
        onBackPressed();
    }

    @OnClick(R.id.rl_join)
    void onJoin() {
        if (AuthUtil.loginBindCheck(this)) {
            Intent intent = new Intent(this, CreateThreadActivity.class);
            intent.putExtra(CreateThreadActivity.ARG_COMMUNITY_ACTIVITY_ID, id);
            startActivityForResult(intent, REQUESTION_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTION_CODE) {
            CommunityThreadListFragment fragment = getThreadListFragment();
            if (fragment != null) {
                fragment.refresh(id);
            }
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(loadSub);
    }

    class EventFragmentAdapter extends FragmentStatePagerAdapter {

        public EventFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (eventFragment == null) {
                        eventFragment = CommunityChannelEventFragment.newInstance();
                    }
                    return eventFragment;
                case 1:
                    if (threadListFragment == null) {
                        threadListFragment = CommunityThreadListFragment.newInstance(id);
                    }
                    return threadListFragment;
                default:
                    break;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    class TabViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.tv_count)
        TextView tvCount;

        private View itemView;
        private int position;

        public TabViewHolder(View itemView, int p) {
            this.itemView = itemView;
            this.position = p;
            ButterKnife.bind(this, itemView);
        }

        public void setTabView(int p) {
            switch (p) {
                case 0:
                    this.tvCount.setVisibility(View.GONE);
                    this.title.setText("活动详情");
                    break;
                case 1:
                    this.tvCount.setVisibility(View.GONE);
                    this.title.setText("大家都在说");
                    break;
                default:
                    break;
            }
        }

        public void setCount(
                int count) {
            tvCount.setText(String.valueOf(count));
            tvCount.setVisibility(View.VISIBLE);
        }
    }
}
