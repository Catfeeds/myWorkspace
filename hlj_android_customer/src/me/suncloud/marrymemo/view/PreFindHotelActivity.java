package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.AreaLabel;
import com.hunliji.hljcommonlibrary.models.DeskPriceLabel;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.merchant.MerchantApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.Desk;
import me.suncloud.marrymemo.model.MenuItem;
import me.suncloud.marrymemo.model.wrappers.HotelFilter;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import me.suncloud.marrymemo.widget.hotel.DeskPriceViewHolder;
import me.suncloud.marrymemo.widget.hotel.DeskViewHolder;
import me.suncloud.marrymemo.widget.hotel.SingleAreaViewHolder;
import rx.Subscription;

/**
 * Created by mo_yu on 2017/10/17.三步找酒店
 */

public class PreFindHotelActivity extends HljBaseNoBarActivity {


    @BindView(R.id.img_pre_hotel)
    ImageView imgPreHotel;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.city_view)
    RelativeLayout cityView;
    @BindView(R.id.tv_area)
    TextView tvArea;
    @BindView(R.id.area_view)
    RelativeLayout areaView;
    @BindView(R.id.tv_desk)
    TextView tvDesk;
    @BindView(R.id.desk_view)
    RelativeLayout deskView;
    @BindView(R.id.tv_desk_price)
    TextView tvDeskPrice;
    @BindView(R.id.price_view)
    RelativeLayout priceView;
    @BindView(R.id.btn_search_hotel)
    Button btnSearchHotel;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.btn_message)
    ImageView btnMessage;
    @BindView(R.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R.id.service_filter_view)
    RelativeLayout serviceFilterView;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    private ArrayList<AreaLabel> areaLabels;
    private ArrayList<Desk> desks;
    private ArrayList<DeskPriceLabel> desksPrices;
    private HotelFilter hotelFilter;
    private MenuItem areaItem;
    private City mCity;
    private SingleAreaViewHolder singleAreaViewHolder;
    private DeskViewHolder deskViewHolder;
    private DeskPriceViewHolder deskPriceViewHolder;
    private NoticeUtil noticeUtil;

    private HljHttpSubscriber initSubscriber;
    private Subscription rxBusEventSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_find_hotel);
        ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout);
        initValue();
        initView();
        initLoad();
        registerRxBusEvent();
    }

    @Override
    protected void onResume() {
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
        super.onPause();
    }

    private void initValue() {
        hotelFilter = new HotelFilter();
        desks = new ArrayList<>();
        desksPrices = new ArrayList<>();
        areaLabels = new ArrayList<>();
        mCity = Session.getInstance()
                .getMyCity(this);
        areaItem = new MenuItem();
    }

    private void initView() {
        noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        if (mCity != null) {
            tvCity.setText(mCity.getName());
        } else {
            tvCity.setText("请选择");
        }
        singleAreaViewHolder = SingleAreaViewHolder.newInstance(this,
                new SingleAreaViewHolder.OnAreaFilterListener() {
                    @Override
                    public void onFilterRefresh(AreaLabel area) {
                        areaItem.setName(area.getName());
                        areaItem.setId(area.getId());
                        tvArea.setText(areaItem.getName());
                        hotelFilter.setAreaItem(areaItem);
                    }

                });
        deskViewHolder = DeskViewHolder.newInstance(this,
                new DeskViewHolder.OnDeskFilterListener() {

                    @Override
                    public void onFilterRefresh(Desk desk) {
                        if (desk != null) {
                            tvDesk.setText(desk.getDescribe());
                            hotelFilter.setCurrentDesk(desk);
                        }
                    }
                });
        deskPriceViewHolder = DeskPriceViewHolder.newInstance(this,
                new DeskPriceViewHolder.OnDeskPriceFilterListener() {

                    @Override
                    public void onFilterRefresh(DeskPriceLabel deskPriceLabel) {
                        if (deskPriceLabel != null) {
                            tvDeskPrice.setText(deskPriceLabel.getName());
                            hotelFilter.setMinPrice(deskPriceLabel.getMinPrice());
                            hotelFilter.setMaxPrice(deskPriceLabel.getMaxPrice());
                        }
                    }
                });
        serviceFilterView.removeAllViews();
        serviceFilterView.addView(singleAreaViewHolder.getRootView());
        serviceFilterView.addView(deskViewHolder.getRootView());
        serviceFilterView.addView(deskPriceViewHolder.getRootView());
    }

    private void initLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<JsonObject>() {
                        @Override
                        public void onNext(JsonObject jsonObject) {
                            initAreaMenu(jsonObject);
                            JsonObject hotelFilterJson = jsonObject.getAsJsonObject("hotel");
                            if (hotelFilterJson != null) {
                                initHotelMenu(hotelFilterJson);
                            }
                        }
                    })
                    .build();
            MerchantApi.getMerchantFilter(mCity.getId())
                    .subscribe(initSubscriber);
        }
    }

    /**
     * 初始化区域选择
     */
    public void initAreaMenu(JsonObject jsonObject) {
        try {
            JsonArray array = jsonObject.getAsJsonArray("area");
            areaLabels.clear();
            AreaLabel allArea = new AreaLabel();
            allArea.setName(getResources().getString(R.string.all_area));
            areaLabels.add(allArea);
            if (array != null && array.size() > 0) {
                Gson gson = GsonUtil.getGsonInstance();
                for (int i = 0, size = array.size(); i < size; i++) {
                    AreaLabel areaLabel = gson.fromJson(array.get(i)
                            .getAsJsonObject(), AreaLabel.class);
                    areaLabels.add(areaLabel);
                }

            }
            singleAreaViewHolder.refreshAreas(areaLabels);
        } catch (Exception ignored) {

        }
    }

    /**
     * 初始化酒店相关数据
     */
    public void initHotelMenu(JsonObject hotelFilterJson) {
        JsonArray array = hotelFilterJson.getAsJsonArray("table");
        if (array != null && array.size() > 0) {
            desks.clear();
            for (int i = 0, size = array.size(); i < size; i++) {
                JsonObject deskObject = array.get(i)
                        .getAsJsonObject();
                String name;
                int deskStart;
                int deskEnd;
                try {
                    deskStart = deskObject.get("min")
                            .getAsInt();
                } catch (Exception e) {
                    deskStart = 0;
                }
                try {
                    deskEnd = deskObject.get("max")
                            .getAsInt();
                } catch (Exception e) {
                    deskEnd = 0;
                }
                if (deskStart > 0) {
                    if (deskEnd > 0) {
                        name = getResources().getString(R.string.label_desk_name,
                                deskStart,
                                deskEnd);
                    } else {
                        name = getResources().getString(R.string.label_desk_name3, deskStart);
                    }
                } else {
                    name = getResources().getString(R.string.label_desk_name2, deskEnd);
                }
                desks.add(new Desk(name, deskStart, deskEnd, i));
            }
            desks.add(new Desk("待定", 0, 0, -1));
        }
        deskViewHolder.refreshDesks(desks);
        array = hotelFilterJson.getAsJsonArray("table_price");
        if (array != null && array.size() > 0) {
            desksPrices.clear();
            for (int i = 0, size = array.size(); i < size; i++) {
                JsonObject deskPriceObject = array.get(i)
                        .getAsJsonObject();
                String name;
                int minPrice;
                int maxPrice;
                try {
                    minPrice = deskPriceObject.get("min")
                            .getAsInt();
                } catch (Exception e) {
                    minPrice = 0;
                }
                try {
                    maxPrice = deskPriceObject.get("max")
                            .getAsInt();
                } catch (Exception e) {
                    maxPrice = 0;
                }
                if (minPrice > 0) {
                    if (maxPrice > 0) {
                        name = getResources().getString(R.string.label_desk_price_name,
                                minPrice,
                                maxPrice);
                    } else {
                        name = getResources().getString(R.string.label_desk_price_name3, minPrice);
                    }
                } else {
                    name = getResources().getString(R.string.label_desk_price_name2, maxPrice);
                }
                DeskPriceLabel deskPriceLabel = new DeskPriceLabel();
                deskPriceLabel.setName(name);
                deskPriceLabel.setMinPrice(String.valueOf(minPrice));
                deskPriceLabel.setMaxPrice(String.valueOf(maxPrice));
                desksPrices.add(deskPriceLabel);
            }
            DeskPriceLabel deskPriceLabel = new DeskPriceLabel();
            deskPriceLabel.setName("待定");
            desksPrices.add(deskPriceLabel);
        }
        deskPriceViewHolder.refreshDeskPrice(desksPrices);
    }


    @OnClick(R.id.city_view)
    public void onCityViewClicked() {
        Intent intent = new Intent(this, CityListActivity.class);
        intent.putExtra("resultCity", true);
        intent.putExtra("nonNull", true);
        startActivityForResult(intent, Constants.RequestCode.CITY_CHANGE);
    }

    @OnClick(R.id.area_view)
    public void onAreaViewClicked() {
        singleAreaViewHolder.showAreaView();
    }

    @OnClick(R.id.desk_view)
    public void onDeskViewClicked() {
        deskViewHolder.showDeskView();
    }

    @OnClick(R.id.price_view)
    public void onPriceViewClicked() {
        deskPriceViewHolder.showDeskView();
    }

    @OnClick(R.id.btn_message)
    public void onSearchClicked() {
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        Intent intent = new Intent(this, MessageHomeActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_search_hotel)
    public void onSearchHotelClicked() {
        Intent intent = new Intent(this, HotelChannelActivity.class);
        intent.putExtra("hotel_filter", hotelFilter);
        intent.putExtra("city", mCity);
        startActivity(intent);
    }

    @OnClick(R.id.btn_back)
    public void onBtnBackClicked() {
        onBackPressed();
    }


    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSubscriber, rxBusEventSub);
    }

    private void refreshCity(City city) {
        mCity = city;
        tvCity.setText(city.getName());
        initLoad();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case Constants.RequestCode.CITY_CHANGE:
                    City city = (City) data.getSerializableExtra("city");
                    if (city != null) {
                        refreshCity(city);
                    }
                    break;

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                                        refreshCity(city);
                                    }
                                    break;
                            }
                        }
                    });
        }
    }
}
