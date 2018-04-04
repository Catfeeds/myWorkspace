package me.suncloud.marrymemo.view.topBrand;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Label;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableHelper;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.topBrand.TopBrandApi;
import me.suncloud.marrymemo.fragment.topBrand.WeddingBrandFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.topBrand.BrandHall;
import me.suncloud.marrymemo.model.topBrand.WeddingBrand;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.widget.TabPageIndicator;
import rx.Observable;
import rx.functions.Func2;

/**
 * 品牌馆activity
 * Created by jinxin on 2016/11/10.
 */

public class WeddingBrandActivity extends HljBaseNoBarActivity implements TabPageIndicator
        .OnTabChangeListener, ScrollableLayout.OnScrollListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.img_brand)
    ImageView imgBrand;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.scrollable_layout)
    ScrollableLayout scrollableLayout;
    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.action_holder_layout)
    RelativeLayout actionHolderLayout;
    @BindView(R.id.action_holder_layout2)
    RelativeLayout actionHolderLayout2;
    private SectionsPagerAdapter pagerAdapter;
    private ArrayList<Label> labels;
    private WeddingBrand weddingBrand;
    private City city;
    private Point point;
    private HljHttpSubscriber refreshSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_brand);
        ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout, actionHolderLayout2);
        labels = new ArrayList<>();
        city = Session.getInstance()
                .getMyCity(this);
        point = CommonUtil.getDeviceSize(this);
        imgBrand.getLayoutParams().height = Math.round(point.x / 2.0f);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh();
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
                scrollableLayout.getHelper()
                        .setCurrentScrollableContainer(getCurrentScrollableContainer());
            }
        });
        scrollableLayout.addOnScrollListener(this);
        scrollableLayout.setExtraHeight(CommonUtil.dp2px(this, 45) + getStatusBarHeight());
        indicator.setOnTabChangeListener(this);
        indicator.setTabViewId(R.layout.menu_wedding_brand_tab_widget);
        onRefresh();
    }

    private void onRefresh() {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            Observable<PosterData> postersObb = CommonApi.getBanner(this,
                    HljCommon.BLOCK_ID.PINGPAI_TOP_BANNER,
                    city.getId());
            Observable<WeddingBrand> brandObb = TopBrandApi.getBrand();
            Observable<ResultZip> observable = Observable.zip(postersObb,
                    brandObb,
                    new Func2<PosterData, WeddingBrand, ResultZip>() {
                        @Override
                        public ResultZip call(
                                PosterData posterData, WeddingBrand weddingBrandData) {
                            ResultZip zip = new ResultZip();
                            zip.posterData = posterData;
                            zip.weddingBrandData = weddingBrandData;
                            return zip;
                        }
                    });
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            if (resultZip.posterData == null || resultZip.weddingBrandData ==
                                    null) {
                                scrollableLayout.setVisibility(View.INVISIBLE);
                                emptyView.showEmptyView();
                                return;
                            }
                            scrollableLayout.setVisibility(View.VISIBLE);
                            setData(resultZip);
                        }
                    })
                    .setProgressBar(progressBar)
                    .setEmptyView(emptyView)
                    .setContentView(scrollableLayout)
                    .build();
            observable.subscribe(refreshSubscriber);
        }
    }

    private class ResultZip extends HljHttpResultZip {
        @HljRZField
        PosterData posterData;
        @HljRZField
        WeddingBrand weddingBrandData;
    }

    private void setData(ResultZip resultZip) {
        if (resultZip.posterData != null) {
            List<Poster> posters = PosterUtil.getPosterList(resultZip.posterData.getFloors(),
                    Constants.POST_SITES.PINGPAI_TOP_BANNER,
                    false);
            if (posters != null && !posters.isEmpty()) {
                final Poster poster = posters.get(0);
                if (poster != null && poster.getId() > 0) {
                    imgBrand.setVisibility(View.VISIBLE);
                    String imagePath = ImageUtil.getImagePath(poster.getPath(), point.x);
                    if (!JSONUtil.isEmpty(imagePath)) {
                        Glide.with(this)
                                .load(imagePath)
                                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                                .into(imgBrand);
                    } else {
                        Glide.with(this)
                                .clear(imgBrand);
                        imgBrand.setImageBitmap(null);
                    }
                    imgBrand.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BannerUtil.bannerAction(WeddingBrandActivity.this,
                                    poster,
                                    city,
                                    false,
                                    null);
                        }
                    });
                }
            } else {
                imgBrand.setVisibility(View.GONE);
            }
        }
        weddingBrand = resultZip.weddingBrandData;
        if (weddingBrand != null && weddingBrand.getId() > 0) {
            labels.clear();
            if (!TextUtils.isEmpty(weddingBrand.getWeddingPhotoTitle())) {
                labels.add(0, new Label(weddingBrand.getWeddingPhotoTitle()));
            }
            if (!TextUtils.isEmpty(weddingBrand.getWeddingPlannerTitle())) {
                labels.add(new Label(weddingBrand.getWeddingPlannerTitle()));
            }
            if (pagerAdapter != null) {
                pagerAdapter.notifyDataSetChanged();
                indicator.notifyDataSetChanged(labels);
            } else {
                pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                viewPager.setAdapter(pagerAdapter);
                indicator.setPagerAdapter(pagerAdapter);
            }
            viewPager.setOffscreenPageLimit(labels.size() - 1);
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 1 || TextUtils.isEmpty(weddingBrand.getWeddingPhotoTitle())) {
                return WeddingBrandFragment.newInstance((ArrayList<BrandHall>) weddingBrand
                        .getWeddingPlannerMerchants());
            } else {
                return WeddingBrandFragment.newInstance((ArrayList<BrandHall>) weddingBrand
                        .getWeddingPhotoMerchants());
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return labels.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return labels.get(position)
                    .getName();
        }
    }

    @Override
    public void onScroll(int currentY, int maxY) {
        if (currentY > maxY) {
            tvTitle.setAlpha(1);
            actionHolderLayout.setAlpha(1);
        } else {
            float f = (float) currentY / maxY;
            tvTitle.setAlpha(f);
            actionHolderLayout.setAlpha(f);
        }
        if (scrollableLayout.getHelper()
                .getScrollableView() == null) {
            scrollableLayout.getHelper()
                    .setCurrentScrollableContainer(getCurrentScrollableContainer());
        }
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

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

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSubscriber);
    }
}