package me.suncloud.marrymemo.view.themephotography;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljcommonlibrary.views.widgets.TabView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.slider.library.Indicators.CirclePageIndicator;
import com.slider.library.SliderLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.RecyclingPagerAdapter;
import me.suncloud.marrymemo.fragment.themephotography.OnePayAllInclusiveWorkListFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import rx.Observable;

/**
 * 一价全包activity
 * Created by jinxin on 2018/3/1 0001.
 */

public class OnePayAllInclusiveActivity extends HljBaseNoBarActivity implements TabPageIndicator
        .OnTabChangeListener {

    @BindView(R.id.ll_bg)
    LinearLayout llBg;
    @BindView(R.id.line_action2)
    View lineAction2;

    @Override
    public String pageTrackTagName() {
        return "一价全包页";
    }

    //1婚庆送四大 2婚纱N件套 3跟妆全员包
    private final int TAG_TYPE1 = 1;
    private final int TAG_TYPE2 = 2;
    private final int TAG_TYPE3 = 3;

    @BindView(R.id.layout_bot_poster)
    LinearLayout layoutBotPoster;
    @BindView(R.id.bot_poster_line)
    View botPosterLine;
    @BindView(R.id.header_line)
    View headerLine;
    @BindView(R.id.scroll_view_pager)
    SliderLayout scrollViewPager;
    @BindView(R.id.pager_indicator)
    CirclePageIndicator pagerIndicator;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.img_poster_left)
    ImageView imgPosterLeft;
    @BindView(R.id.img_poster_right)
    ImageView imgPosterRight;
    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_msg)
    ImageButton btnMsg;
    @BindView(R.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R.id.btn_back2)
    ImageButton btnBack2;
    @BindView(R.id.btn_msg2)
    ImageButton btnMsg2;
    @BindView(R.id.action_holder_layout2)
    LinearLayout actionHolderLayout2;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.msg_notice_layout)
    RelativeLayout msgNoticeLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @BindView(R.id.img_top)
    ImageView imgTop;
    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.layout_top_poster)
    RelativeLayout layoutTopPoster;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private int headerWidth;
    private int headerHeight;
    private int posterWidth;
    private int posterHeight;
    private NoticeUtil noticeUtil;
    private HljHttpSubscriber posterSub;
    private List<Poster> topPoster;
    private TopBannerAdapter topBannerAdapter;
    private City city;
    private OnePayAddFragmentAdapter onePayAddFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_pay_all_inclusive);
        ButterKnife.bind(this);

        initValues();
        initWidgets();
        initLoad();
    }

    private void initValues() {
        setActionBarPadding(layoutHeader);
        headerWidth = CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 32);
        headerHeight = headerWidth * 276 / 686;

        posterWidth = (CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 64)) / 2;
        posterHeight = posterWidth * 124 / 308;
        noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        onePayAddFragmentAdapter = new OnePayAddFragmentAdapter(getSupportFragmentManager());
        topPoster = new ArrayList<>();
        topBannerAdapter = new TopBannerAdapter(topPoster, headerWidth, headerHeight);
        topBannerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object object) {
                if (object != null) {
                    Poster poster = (Poster) object;
                    onPosterClick(poster);
                }
            }
        });
    }

    private void initWidgets() {
        setActionBarPadding(actionHolderLayout, actionHolderLayout2, msgNoticeLayout);
        imgTop.getLayoutParams().height = CommonUtil.dp2px(this, 45) + getStatusBarHeight();

        layoutTopPoster.getLayoutParams().height = headerHeight;
        imgPosterLeft.getLayoutParams().width = posterWidth;
        imgPosterLeft.getLayoutParams().height = posterHeight;
        imgPosterRight.getLayoutParams().width = posterWidth;
        imgPosterRight.getLayoutParams().height = posterHeight;

        scrollViewPager.getmViewPager()
                .setAdapter(topBannerAdapter);
        pagerIndicator.setViewPager(scrollViewPager.getmViewPager());
        scrollViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(onePayAddFragmentAdapter);
        indicator.setTabViewId(R.layout.one_pay_all_tab_view);
        indicator.setPagerAdapter(onePayAddFragmentAdapter);

        for (int i = 0, size = 3; i < size; i++) {
            View tabView = indicator.getTabView(i);
            TabViewHolder tabViewHolder = (TabViewHolder) tabView.getTag();
            if (tabViewHolder == null) {
                tabViewHolder = new TabViewHolder(tabView, i);
                tabView.setTag(tabViewHolder);
            }
            String title = null;
            String hint = null;
            switch (i) {
                case 0:
                    title = "婚礼现场";
                    hint = "策划+四大";
                    break;
                case 1:
                    title = "婚纱礼服";
                    hint = "婚纱3件套";
                    break;
                case 2:
                    title = "婚礼跟妆";
                    hint = "伴娘+妈妈妆";
                    break;
                default:
                    break;
            }
            tabViewHolder.tvTitle.setText(title);
            tabViewHolder.tvTitleHint.setText(hint);
        }
        indicator.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                indicator.setCurrentItem(position);
            }
        });
        indicator.setOnTabChangeListener(this);
        appbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (appbarLayout == null) {
                    return;
                }
                if (Math.abs(verticalOffset) >= appbarLayout.getTotalScrollRange()) {
                    actionHolderLayout2.setAlpha(1F);
                    headerLine.setVisibility(View.GONE);
                    hideDividerView();
                    lineAction2.setVisibility(View.GONE);
                } else {
                    headerLine.setVisibility(View.VISIBLE);
                    showDividerView();
                    lineAction2.setVisibility(View.VISIBLE);
                    float alpha = (float) Math.abs(verticalOffset) / appbarLayout
                            .getTotalScrollRange();
                    actionHolderLayout2.setAlpha(alpha);
                }
            }
        });
    }

    private int getTabType(int position) {
        int tabType = TAG_TYPE1;
        switch (position) {
            case 0:
                tabType = TAG_TYPE1;
                break;
            case 1:
                tabType = TAG_TYPE2;
                break;
            case 2:
                tabType = TAG_TYPE3;
                break;
            default:
                break;
        }
        return tabType;
    }

    private void initLoad() {
        city = Session.getInstance()
                .getMyCity(this);
        if (city == null) {
            return;
        }
        Observable<PosterData> posterObb = CommonApi.getBanner(this,
                HljCommon.BLOCK_ID.ONE_PAY_ALL_INCLUSIVE,
                city.getId());
        posterSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<PosterData>() {
                    @Override
                    public void onNext(PosterData posterData) {
                        setPoster(posterData);
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        setPosterError();
                    }
                })
                .build();
        posterObb.subscribe(posterSub);
    }

    private void setPosterError() {
        llBg.setVisibility(View.GONE);
        layoutHeader.setVisibility(View.GONE);
        headerLine.setVisibility(View.GONE);
        appbarLayout.setExpanded(false);
        hideDividerView();
    }

    private void setPoster(PosterData poster) {
        if (poster == null) {
            setPosterError();
            return;
        }
        JsonObject jsonObject = poster.getFloors();
        List<Poster> posters = PosterUtil.getPosterList(jsonObject,
                HljCommon.POST_SITES.SITE_ALL_INCLUSIVE_BANNER,
                false);
        topPoster.clear();
        topPoster.addAll(posters);
        if (CommonUtil.isCollectionEmpty(topPoster)) {
            layoutTopPoster.setVisibility(View.GONE);
        } else {
            layoutTopPoster.setVisibility(View.VISIBLE);
            topBannerAdapter.setPhotos(topPoster);
        }
        posters = PosterUtil.getPosterList(jsonObject,
                HljCommon.POST_SITES.SITE_ALL_INCLUSIVE_MARKETING,
                false);
        if (CommonUtil.getCollectionSize(posters) < 2) {
            layoutBotPoster.setVisibility(View.GONE);
        } else {
            layoutBotPoster.setVisibility(View.VISIBLE);
            botPosterLine.setVisibility(View.VISIBLE);
            setPosterImage(imgPosterLeft, posters.get(0), posterWidth, posterHeight);
            setPosterImage(imgPosterRight, posters.get(1), posterWidth, posterHeight);
        }
        if (layoutTopPoster.getVisibility() == View.VISIBLE || layoutBotPoster.getVisibility() ==
                View.VISIBLE) {
            layoutHeader.setVisibility(View.VISIBLE);
            llBg.setVisibility(View.VISIBLE);
            headerLine.setVisibility(View.VISIBLE);
        } else {
            setPosterError();
        }
    }

    private void setPosterImage(ImageView img, final Poster poster, int width, int height) {
        if (poster == null || img == null) {
            return;
        }
        Glide.with(this)
                .load(ImagePath.buildPath(poster.getPath())
                        .width(width)
                        .height(height)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(img);
        img.setVisibility(View.VISIBLE);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPosterClick(poster);
            }
        });
    }

    private void onPosterClick(Poster poster) {
        if (poster == null) {
            return;
        }
        BannerUtil.bannerAction(OnePayAllInclusiveActivity.this, poster, city, false, null);
    }

    @OnClick({R.id.btn_back, R.id.btn_back2})
    void onBack() {
        onBackPressed();
    }

    @OnClick({R.id.btn_msg, R.id.btn_msg2})
    void onMessage() {
        if (AuthUtil.loginBindCheck(this)) {
            Intent intent = new Intent(this, MessageHomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(posterSub);
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    class OnePayAddFragmentAdapter extends FragmentStatePagerAdapter {

        public OnePayAddFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return OnePayAllInclusiveWorkListFragment.newInstance(getTabType(position));
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    class TabViewHolder {

        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_title_hint)
        TextView tvTitleHint;
        @BindView(R.id.tab_view)
        TabView tabView;

        public TabViewHolder(View view, int position) {
            ButterKnife.bind(this, view);
            HljVTTagger.buildTagger(view)
                    .atPosition(position)
                    .tagName(HljTaggerName.ONE_PAY_ALL_INCLUSIVE_TAB)
                    .hitTag();
        }
    }

    public class TopBannerAdapter extends RecyclingPagerAdapter {

        private ArrayList<Poster> posters;
        private OnItemClickListener onItemClickListener;
        private int width;
        private int height;

        public TopBannerAdapter(List<Poster> list, int width, int height) {
            this.posters = new ArrayList<>(list);
            this.width = width;
            this.height = height;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        public void setPhotos(List<Poster> photos) {
            if (photos != null) {
                posters.clear();
                posters.addAll(photos);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return CommonUtil.getCollectionSize(posters);
        }

        @Override
        public View getView(int p, View convertView, final ViewGroup container) {
            final int position = p % this.posters.size();
            if (convertView == null) {
                convertView = View.inflate(container.getContext(),
                        R.layout.round_angle_image_item,
                        null);
                ViewHolder holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.image.getLayoutParams().width = width;
            holder.image.getLayoutParams().height = height;
            final Poster poster = posters.get(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position, poster);
                    }
                }
            });
            if (poster != null) {
                Glide.with(convertView.getContext())
                        .load(ImagePath.buildPath(poster.getPath())
                                .width(width)
                                .height(height)
                                .cropPath())
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                        .into(holder.image);
            }
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.image)
            ImageView image;

            public ViewHolder(View itemView) {
                ButterKnife.bind(this, itemView);
            }
        }

    }
}
