package me.suncloud.marrymemo.view;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ProgressBar;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.merchant.MerchantListFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.widget.MerchantMenuFilterView;
import rx.Subscription;

/**
 * Created by mo_yu on 2017/11/3.分期商城
 */
@Route(path = RouterPath.IntentPath.Customer.INSTALMENT_MERCHANT_LIST_ACTIVITY)
public class InstalmentMerchantListActivity extends HljBaseActivity {

    private static final String CPM_SOURCE = "instalment_merchant_list";

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.menu_filter_view)
    MerchantMenuFilterView merchantMenuFilterView;
    @BindView(R.id.scrollable_layout)
    ScrollableLayout scrollableLayout;
    private City mCity;
    private long propertyId;
    private Subscription rxBusEventSub;
    private boolean isCityRefresh;
    private static final String TAG_FRAGMENT = "tag_fragment";

    @Override
    public String pageTrackTagName() {
        return getString(R.string.title_activity_instalment_merchant_list);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mCity = (City) getIntent().getSerializableExtra("city");
        if (mCity == null) {
            mCity = Session.getInstance()
                    .getMyCity(this);
        }
        propertyId = getIntent().getLongExtra("propertyId",
                Merchant.PROPERTY_WEEDING_HOTEL); //默认选择婚宴分期
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instalment_merchant_list);
        ButterKnife.bind(this);

        registerRxBusEvent();
        initView();
        progressBar.setVisibility(View.VISIBLE);
        isCityRefresh = false;
        new GetMerchantFilter().executeOnExecutor(Constants.INFOTHEADPOOL,
                Constants.getAbsUrl(String.format(Constants.HttpPath.GET_MERCHANT_FILTER,
                        mCity.getId())));
    }

    private void initView() {
        //筛选视图
        merchantMenuFilterView.setScrollableLayout(scrollableLayout);
        merchantMenuFilterView.setPropertyId(propertyId);
        merchantMenuFilterView.setCity(mCity);
        //刷新回调
        merchantMenuFilterView.setOnRefreshCallback(new MerchantMenuFilterView.onRefreshCallback() {
            @Override
            public void onRefresh(long propertyId) {
                onRefreshFragment();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (merchantMenuFilterView.hideMenu(0)) {
            return;
        }
        super.onBackPressed();
    }

    @SuppressLint("StaticFieldLeak")
    private class GetMerchantFilter extends AsyncTask<String, Object, JSONObject> {

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
            progressBar.setVisibility(View.GONE);
            if (jsonObject != null) {
                merchantMenuFilterView.setTabMenuView(View.VISIBLE);
                merchantMenuFilterView.initMerchantMenu(jsonObject, isCityRefresh);
                if (!isCityRefresh) {
                    JSONObject hotelFilterJson = jsonObject.optJSONObject("hotel");
                    if (hotelFilterJson != null) {
                        merchantMenuFilterView.initHotelMenu(hotelFilterJson);
                    }
                }
                //刷新商家列表
                merchantMenuFilterView.onRefresh();
            }
            super.onPostExecute(jsonObject);
        }
    }

    private void onRefreshFragment() {
        MerchantListFragment fragment = (MerchantListFragment) getSupportFragmentManager()
                .findFragmentByTag(
                TAG_FRAGMENT);
        if (fragment == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            fragment = MerchantListFragment.newInstance(getInstallmentUrl(merchantMenuFilterView
                            .getUrlQuery()),
                    mCity,
                    CPM_SOURCE,
                    true);
            ft.add(R.id.content_layout, fragment, TAG_FRAGMENT);
            ft.commitAllowingStateLoss();
        } else {
            fragment.refresh(getInstallmentUrl(merchantMenuFilterView.getUrlQuery()), mCity);
        }
    }

    private String getInstallmentUrl(String url) {
        return url + "&is_installment=1";
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
                                        isCityRefresh = true;
                                        new GetMerchantFilter().executeOnExecutor(Constants
                                                        .INFOTHEADPOOL,
                                                Constants.getAbsUrl(String.format(Constants
                                                                .HttpPath.GET_MERCHANT_FILTER,
                                                        mCity.getId())));
                                    }
                                    break;
                            }
                        }
                    });
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(rxBusEventSub);
    }
}
