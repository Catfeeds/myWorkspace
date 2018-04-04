package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
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
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.cardview.CardView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.merchant.MerchantListFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.wrappers.HotelFilter;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.newsearch.NewSearchActivity;
import me.suncloud.marrymemo.widget.HotelMenuFilterView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2016/11/28.婚宴闪电定（酒店频道页）
 */

public class HotelChannelActivity extends HljBaseNoBarActivity {

    @BindView(R.id.tv_hotel_title)
    TextView tvHotelTitle;
    @BindView(R.id.hotel_header_view)
    LinearLayout hotelHeaderView;
    @BindView(R.id.iv_adv)
    ImageView ivAdv;
    @BindView(R.id.iv_gift)
    ImageView ivGift;
    @BindView(R.id.banner_view)
    CardView bannerView;
    @BindView(R.id.hotel_channel_header_view)
    RelativeLayout hotelChannelHeaderView;
    @BindView(R.id.menu_filter_view)
    HotelMenuFilterView hotelMenuFilterView;
    @BindView(R.id.scrollable_layout)
    ScrollableLayout scrollableLayout;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.btn_search)
    ImageView btnSearch;
    @BindView(R.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R.id.btn_back2)
    ImageButton btnBack2;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_search2)
    ImageView btnSearch2;
    @BindView(R.id.action_holder_layout2)
    LinearLayout actionHolderLayout2;
    @BindView(R.id.root_view)
    RelativeLayout rootView;
    @BindView(R.id.iv_adv_bottom)
    ImageView ivAdvBottom;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.empty_header_view)
    View emptyHeaderView;
    private long propertyId;
    private int bannerHeight;
    private boolean isCityRefresh;
    private int emptyHeight;
    private HotelFilter hotelFilter;
    private City mCity;
    private MerchantListFragment fragment;
    private static final String TAG_FRAGMENT = "tag_fragment";

    private Subscription rxBusEventSub;
    private HljHttpSubscriber initSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_channel);
        ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout, actionHolderLayout2);
        initValue();
        initView();
        refreshMerchantFilter();
        registerRxBusEvent();
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(initSubscriber, rxBusEventSub);
        super.onFinish();
    }

    private void initValue() {
        bannerHeight = (CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 32)) * 80 / 343;
        emptyHeight = CommonUtil.dp2px(this, 45) + getStatusBarHeight();
        isCityRefresh = false;
        propertyId = Constants.Property.HOTEL;
        mCity = (City) getIntent().getSerializableExtra("city");
        if (mCity == null) {
            mCity = Session.getInstance()
                    .getMyCity(this);
        }
        hotelFilter = (HotelFilter) getIntent().getSerializableExtra("hotel_filter");
    }

    private void initView() {
        if (hotelFilter != null) {
            tvTitle.setText("三步找酒店");
            hotelHeaderView.setVisibility(View.GONE);
            emptyHeaderView.setVisibility(View.VISIBLE);
            emptyHeaderView.getLayoutParams().height = emptyHeight;
            actionHolderLayout2.setAlpha(1);
            actionHolderLayout.setAlpha(0);
            scrollableLayout.setExtraHeight(emptyHeight);
            scrollableLayout.addOnScrollListener(new ScrollableLayout.OnScrollListener() {
                @Override
                public void onScroll(int currentY, int maxY) {
                    if (scrollableLayout.getHelper()
                            .getScrollableView() == null) {
                        scrollableLayout.getHelper()
                                .setCurrentScrollableContainer(fragment);
                    }
                }
            });
        } else {
            if (mCity != null) {
                tvTitle.setText(mCity.getName() + "婚宴酒店");
                tvHotelTitle.setText(mCity.getName() + "婚宴酒店");
            }
            hotelHeaderView.setVisibility(View.VISIBLE);
            emptyHeaderView.setVisibility(View.GONE);
            scrollableLayout.setExtraHeight(emptyHeight);
            scrollableLayout.addOnScrollListener(new ScrollableLayout.OnScrollListener() {
                @Override
                public void onScroll(int currentY, int maxY) {
                    if (currentY > maxY) {
                        actionHolderLayout2.setAlpha(1);
                        actionHolderLayout.setAlpha(0);
                    } else {
                        float f = (float) currentY / maxY;
                        actionHolderLayout2.setAlpha(f);
                        actionHolderLayout.setAlpha(1 - f);
                    }
                    if (scrollableLayout.getHelper()
                            .getScrollableView() == null) {
                        scrollableLayout.getHelper()
                                .setCurrentScrollableContainer(fragment);
                    }
                }
            });
        }

        hotelMenuFilterView.setScrollableLayout(scrollableLayout);
        hotelMenuFilterView.setPropertyId(propertyId);
        hotelMenuFilterView.setCity(mCity);
        //刷新回调
        hotelMenuFilterView.setOnRefreshCallback(new HotelMenuFilterView.onRefreshCallback() {
            @Override
            public void onRefresh(long propertyId) {
                onRefreshFragment();
            }
        });
    }

    private void refreshMerchantFilter() {
        if (hotelFilter == null && (initSubscriber == null || initSubscriber.isUnsubscribed())) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setDataNullable(true)
                    .setOnNextListener(new SubscriberOnNextListener<PosterData>() {
                        @Override
                        public void onNext(PosterData posterData) {
                            setBannerView(posterData);
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            bannerView.setVisibility(View.GONE);
                        }
                    })
                    .build();
            CommonApi.getBanner(this,
                    HljCommon.BLOCK_ID.BLOCK_HOTEL_CHANNEL,
                    mCity != null ? mCity.getId() : 0)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(initSubscriber);
        }
        new GetHotelMerchantFilter().executeOnExecutor(Constants.INFOTHEADPOOL,
                Constants.getAbsUrl(String.format(Constants.HttpPath.GET_MERCHANT_FILTER,
                        mCity != null ? mCity.getId() : 0)));
    }

    private void setBannerView(PosterData posterData) {
        //婚宴顾问
        List<Poster> advList = PosterUtil.getPosterList(posterData.getFloors(),
                Constants.POST_SITES.SITE_HOTEL_ADV,
                false);
        if (advList != null && advList.size() > 0) {
            bannerView.setVisibility(View.VISIBLE);
            setAdvView(advList.get(0));
        } else {
            bannerView.setVisibility(View.GONE);
        }

        //婚宴礼物
        List<Poster> giftList = PosterUtil.getPosterList(posterData.getFloors(),
                Constants.POST_SITES.SITE_HOTEL_GIFT,
                false);
        if (giftList != null && giftList.size() > 0) {
            bannerView.setVisibility(View.VISIBLE);
            setGiftView(giftList.get(0));
        } else {
            bannerView.setVisibility(View.GONE);
        }
        if (bannerView.getVisibility() == View.VISIBLE) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) bannerView
                    .getLayoutParams();
            params.height = bannerHeight;
            params.topMargin = CommonUtil.getDeviceSize(this).x * 346 / 750 - bannerHeight / 2;
            params.bottomMargin = CommonUtil.dp2px(this, 10);
            params = (ViewGroup.MarginLayoutParams) tvHotelTitle.getLayoutParams();
            params.topMargin = CommonUtil.getDeviceSize(this).x * 14 / 75;
            params = (ViewGroup.MarginLayoutParams) hotelHeaderView.getLayoutParams();
            params.height = CommonUtil.getDeviceSize(this).x * 346 / 750;
            ivAdvBottom.setVisibility(View.GONE);
        } else {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tvHotelTitle
                    .getLayoutParams();
            params.topMargin = CommonUtil.getDeviceSize(this).x * 13 / 75;
            hotelHeaderView.getLayoutParams().height = CommonUtil.getDeviceSize
                    (HotelChannelActivity.this).x * 28 / 75;
            List<Poster> advBottomList = PosterUtil.getPosterList(posterData.getFloors(),
                    Constants.POST_SITES.SITE_HOTEL_ADV_BOTTOM,
                    false);
            if (advBottomList != null && advBottomList.size() > 0) {
                ivAdvBottom.setVisibility(View.VISIBLE);
                setAdvBottomView(advBottomList.get(0));
            } else {
                ivAdvBottom.setVisibility(View.GONE);
            }
        }
        hotelChannelHeaderView.requestLayout();
    }

    /**
     * 结婚顾问
     */
    private void setAdvView(final Poster poster) {
        int advWidth = Math.round((CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this,
                32)) * 163 / 343);
        String advUrl = ImagePath.buildPath(poster.getPath())
                .width(advWidth)
                .height(bannerHeight)
                .cropPath();
        if (!TextUtils.isEmpty(advUrl)) {
            ivAdv.setVisibility(View.VISIBLE);
            Glide.with(HotelChannelActivity.this)
                    .load(advUrl)
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(ivAdv);
        } else {
            ivAdv.setVisibility(View.GONE);
        }
        ivAdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BannerUtil.bannerAction(HotelChannelActivity.this, poster, mCity, false, null);
            }
        });
    }

    /**
     * 婚宴礼品
     */
    private void setGiftView(final Poster poster) {
        int giftWidth = Math.round((CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this,
                32)) * 180 / 343);
        String giftUrl = ImagePath.buildPath(poster.getPath())
                .width(giftWidth)
                .height(bannerHeight)
                .cropPath();
        if (!TextUtils.isEmpty(giftUrl)) {
            ivGift.setVisibility(View.VISIBLE);
            Glide.with(HotelChannelActivity.this)
                    .load(giftUrl)
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(ivGift);
        } else {
            ivGift.setVisibility(View.GONE);
        }
        ivGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BannerUtil.bannerAction(HotelChannelActivity.this, poster, mCity, false, null);
            }
        });
    }

    /**
     * 底部结婚顾问
     */
    private void setAdvBottomView(final Poster poster) {
        int advWidth = CommonUtil.dp2px(this, 138);
        int advHeight = CommonUtil.dp2px(this, 64);
        String advUrl = ImagePath.buildPath(poster.getPath())
                .width(advWidth)
                .height(advHeight)
                .cropPath();
        if (!TextUtils.isEmpty(advUrl)) {
            ivAdvBottom.setVisibility(View.VISIBLE);
            Glide.with(HotelChannelActivity.this)
                    .load(advUrl)
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(ivAdvBottom);
        } else {
            ivAdvBottom.setVisibility(View.GONE);
        }
        ivAdvBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BannerUtil.bannerAction(HotelChannelActivity.this, poster, mCity, false, null);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (hotelMenuFilterView.hideMenu(0)) {
            return;
        }
        super.onBackPressed();
    }

    @OnClick({R.id.btn_back, R.id.btn_back2})
    public void onBackClicked(View view) {
        onBackPressed();
    }

    @OnClick({R.id.btn_search, R.id.btn_search2})
    public void onSearchClicked(View view) {
        Intent intent = new Intent(this, NewSearchActivity.class);
        intent.putExtra(NewSearchApi.ARG_SEARCH_TYPE, NewSearchApi.SearchType.SEARCH_TYPE_HOTEL);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    private class GetHotelMerchantFilter extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String json = JSONUtil.getStringFromUrl(params[0]);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json).optJSONObject("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (isFinishing()) {
                return;
            }
            if (jsonObject != null) {
                hotelMenuFilterView.setVisibility(View.VISIBLE);
                hotelMenuFilterView.setTabMenuView(View.VISIBLE);
                hotelMenuFilterView.initMerchantMenu(jsonObject);
                if (!isCityRefresh) {
                    JSONObject hotelFilterJson = jsonObject.optJSONObject("hotel");
                    if (hotelFilterJson != null) {
                        hotelMenuFilterView.initHotelMenu(hotelFilterJson);
                        if (hotelFilter != null) {
                            hotelMenuFilterView.setHotelFilter(hotelFilter);
                        }
                    }
                }
                //刷新商家列表
                hotelMenuFilterView.onRefresh();
            }
            super.onPostExecute(jsonObject);
        }
    }

    private void onRefreshFragment() {
        fragment = (MerchantListFragment) getSupportFragmentManager().findFragmentByTag(
                TAG_FRAGMENT);
        if (fragment == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            fragment = MerchantListFragment.newInstance(hotelMenuFilterView.getUrlQuery(),
                    mCity);
            ft.add(R.id.content_layout, fragment, TAG_FRAGMENT);
            ft.commitAllowingStateLoss();
        } else {
            fragment.refresh(hotelMenuFilterView.getUrlQuery(), mCity);
        }
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case CITY_CHANGE:
                                    City city = (City) rxEvent.getObject();
                                    if (city != null && !city.getId()
                                            .equals(mCity.getId())) {
                                        mCity = city;
                                        if (hotelFilter == null) {
                                            tvTitle.setText(mCity.getName() + "婚宴酒店");
                                            tvHotelTitle.setText(mCity.getName() + "婚宴酒店");
                                        }
                                        isCityRefresh = true;
                                        refreshMerchantFilter();
                                    }
                                    break;
                            }
                        }
                    });
        }
    }
}
