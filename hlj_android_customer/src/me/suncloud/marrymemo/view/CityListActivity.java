package me.suncloud.marrymemo.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.amap.api.location.AMapLocation;
import com.google.gson.stream.JsonReader;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.CityListAdapter;
import me.suncloud.marrymemo.api.CustomCommonApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.CityData;
import me.suncloud.marrymemo.util.CityUtil;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.widget.FlowLayoutForTextView;
import me.suncloud.marrymemo.widget.MyStickyListHeadersListView;
import me.suncloud.marrymemo.widget.NewSideBar;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by Suncloud on 2015/10/31.城市选择
 */
@Route(path = RouterPath.IntentPath.Customer.CITY_LIST_ACTIVITY)
@RuntimePermissions
public class CityListActivity extends HljBaseActivity implements View.OnClickListener,
        TextWatcher, CityListAdapter.OnCityClickListener {

    @BindView(R.id.list_view)
    MyStickyListHeadersListView listView;
    @BindView(R.id.sideBar)
    NewSideBar sideBar;
    @BindView(R.id.side_layout)
    FrameLayout sideLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.search)
    EditText searchView;

    public static final String ARG_IS_INITIAL_PAGE = "is_initial_page"; // 是不是首次打开进入的页面
    public static final String ARG_NON_NULL = "nonNull";

    private HeaderViewHolder headerViewHolder;
    private CityListAdapter adapter;
    private City city;
    private City locationCity;
    private ArrayList<City> accessCityList;
    private int width;
    private int height;
    private boolean resultCity;
    private boolean nonNull;
    private int hotCityType;
    private CityUtil cityUtil;

    private Subscription loadSubscription;
    private Subscription allCitySubscription;
    private Subscription hotCitySubscription;
    private Subscription relativeCitySubscription;
    private boolean isInitialPage;
    private InputMethodManager imm;

    static class HotCityType {
        static final int COMMON = 0;
        static final int CAR = 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_city_list);
        ButterKnife.bind(this);
        initData();
        initView();
        loadData();
        initLocation();
    }

    private void initLocation() {
        // 初次进入的时候，没有定位城市数据，先申请权限
        if (isInitialPage) {
            CityListActivityPermissionsDispatcher.requestLocationPermissionWithCheck(this);
        }
    }

    private void initData() {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        cityUtil = new CityUtil(this, new CityUtil.OnGetCityResultListener() {
            @Override
            public void onResult(City city) {
                onLocationCityRefresh(city);
            }
        });
        cityUtil.setOnLocationListener(new CityUtil.OnLocationListener() {
            @Override
            public void onLocation(AMapLocation location) {
                //定位失败监听
                if (location.getErrorCode() != 0) {
                    headerViewHolder.localCity.setText(getString(R.string.action_repeat));
                    headerViewHolder.localCity.setTextColor(ContextCompat.getColor
                            (CityListActivity.this,
                            R.color.colorBlack3));
                    headerViewHolder.imgLocalCity.setImageResource(R.mipmap
                            .icon_refresh_gray_27_27);
                }
            }
        });

        Point point = JSONUtil.getDeviceSize(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = Math.round((point.x - dm.density * 56) / 3 - 1f);
        height = Math.round(dm.density * 30);
        city = (City) getIntent().getSerializableExtra("city");
        resultCity = getIntent().getBooleanExtra("resultCity", false);
        nonNull = getIntent().getBooleanExtra(ARG_NON_NULL, false);
        hotCityType = getIntent().getIntExtra("hot_city_type", HotCityType.COMMON);
        isInitialPage = getIntent().getBooleanExtra(ARG_IS_INITIAL_PAGE, false);

        if (city == null) {
            city = Session.getInstance()
                    .getMyCity(this);
        }
        locationCity = Session.getInstance()
                .getLocationCity(this);
        accessCityList = Session.getInstance()
                .getAccessCities(this);
    }

    private void initView() {
        adapter = new CityListAdapter(this, sideBar, this);
        searchView.addTextChangedListener(this);
        sideBar.setListView(listView);
        setBackButton(R.mipmap.icon_cross_close_primary_36_36);
        if (isInitialPage) {
            hideBackButton();
            setTitle("选择所在城市");
        } else {
            setTitle(getString(R.string.title_city_list, city.getName()));
        }

        View headerView = View.inflate(this, R.layout.city_list_header, null);
        headerViewHolder = new HeaderViewHolder(headerView);
        switch (hotCityType) {
            case HotCityType.CAR:
                headerViewHolder.tvHot.setText("提供婚车服务的城市");
                break;
            default:
                headerViewHolder.tvHot.setText(R.string.label_hot_city2);
                break;
        }
        if (nonNull && city.getId() == 0) {
            headerViewHolder.currentLayout.setVisibility(View.GONE);
        } else {
            headerViewHolder.currentLayout.setVisibility(View.VISIBLE);
            if (nonNull) {
                headerViewHolder.allCity.setVisibility(View.GONE);
            } else {
                headerViewHolder.allCity.setVisibility(View.VISIBLE);
                headerViewHolder.allCity.setText(R.string.all_city);
                headerViewHolder.allCity.setOnClickListener(this);
            }
            if (locationCity != null && locationCity.getId() > 0) {
                headerViewHolder.localCity.setText(locationCity.getName());
                headerViewHolder.localCity.setTextColor(ContextCompat.getColor(this,
                        R.color.colorPrimary));
                headerViewHolder.imgLocalCity.setImageResource(R.mipmap
                        .icon_location_primary_28_34);
            } else {
                headerViewHolder.localCity.setText(getString(R.string.action_repeat));
                headerViewHolder.localCity.setTextColor(ContextCompat.getColor(this,
                        R.color.colorBlack3));
                headerViewHolder.imgLocalCity.setImageResource(R.mipmap.icon_refresh_gray_27_27);
            }
            headerViewHolder.localLayout.setOnClickListener(this);
        }

        listView.addHeaderView(headerView);
        listView.setAdapter(adapter);
    }

    private void loadData() {
        loadSubscription = Observable.create(new Observable.OnSubscribe<CityData>() {
            @Override
            public void call(Subscriber<? super CityData> subscriber) {
                if (getFileStreamPath(Constants.NEW_CITIES_FILE) != null && getFileStreamPath(
                        Constants.NEW_CITIES_FILE).exists()) {
                    try {
                        InputStream in = openFileInput(Constants.NEW_CITIES_FILE);
                        CityData data = GsonUtil.getGsonInstance()
                                .fromJson(new JsonReader(new InputStreamReader(in)),
                                        CityData.class);
                        subscriber.onNext(data);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatWith(CustomCommonApi.getCities(this, city.getId()))
                .filter(new Func1<CityData, Boolean>() {
                    @Override
                    public Boolean call(CityData cityData) {
                        return cityData.getCityMap() != null;
                    }
                })
                .subscribe(new Subscriber<CityData>() {

                    @Override
                    public void onStart() {
                        progressBar.setVisibility(View.VISIBLE);
                        super.onStart();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CityData cityData) {
                        progressBar.setVisibility(View.GONE);
                        initAllCity(cityData);
                        initHotCity(cityData);
                        initRelativeCity(cityData);
                        initAccessCity();
                        if (listView.getEmptyView() == null) {
                            listView.setEmptyView(findViewById(R.id.search_empty));
                        }
                    }
                });
    }

    private void initAllCity(CityData cityData) {
        CommonUtil.unSubscribeSubs(allCitySubscription);
        allCitySubscription = Observable.from(cityData.getCityMap()
                .entrySet())
                .sorted(new Func2<Map.Entry<String, List<City>>, Map.Entry<String, List<City>>,
                        Integer>() {
                    @Override
                    public Integer call(
                            Map.Entry<String, List<City>> entry,
                            Map.Entry<String, List<City>> entry2) {
                        return entry.getKey()
                                .toUpperCase()
                                .compareTo(entry2.getKey()
                                        .toUpperCase());
                    }
                })
                .map(new Func1<Map.Entry<String, List<City>>, Map.Entry<String, List<City>>>() {
                    @Override
                    public Map.Entry<String, List<City>> call(Map.Entry<String, List<City>> entry) {
                        for (City city : entry.getValue()) {
                            city.setLetter(entry.getKey());
                        }
                        return entry;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Map.Entry<String, List<City>>>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Map.Entry<String, List<City>>> entries) {
                        adapter.setCities(entries);
                    }
                });
    }

    private void initHotCity(final CityData cityData) {
        CommonUtil.unSubscribeSubs(hotCitySubscription);
        hotCitySubscription = Observable.just(hotCityType)
                .concatMap(new Func1<Integer, Observable<List<City>>>() {
                    @Override
                    public Observable<List<City>> call(Integer type) {
                        switch (type) {
                            case HotCityType.CAR:
                                DataConfig dataConfig = Session.getInstance()
                                        .getDataConfig(CityListActivity.this);
                                if (dataConfig != null && dataConfig.getCityIds() != null) {
                                    final List<Long> cids = dataConfig.getCityIds();
                                    return Observable.from(cityData.getCityMap()
                                            .entrySet())
                                            .concatMap(new Func1<Map.Entry<String, List<City>>,
                                                    Observable<? extends City>>() {
                                                @Override
                                                public Observable<? extends City> call(Map.Entry<String, List<City>> entry) {
                                                    return Observable.from(entry.getValue());
                                                }
                                            })
                                            .filter(new Func1<City, Boolean>() {
                                                @Override
                                                public Boolean call(City city) {
                                                    return city.getId() > 0 && !TextUtils.isEmpty(
                                                            city.getName()) && cids.contains(city
                                                            .getId());
                                                }
                                            })
                                            .toSortedList(new Func2<City, City, Integer>() {
                                                @Override
                                                public Integer call(City city, City city2) {
                                                    return cids.indexOf(city.getId()) - cids
                                                            .indexOf(
                                                            city2.getId());
                                                }
                                            });
                                }
                                return Observable.empty();
                            default:
                                return Observable.from(cityData.getHotCities())
                                        .filter(new Func1<City, Boolean>() {
                                            @Override
                                            public Boolean call(City city) {
                                                return city.getId() > 0 && !TextUtils.isEmpty
                                                        (city.getName());
                                            }
                                        })
                                        .toList();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<City>>() {
                    @Override
                    public void call(List<City> cities) {
                        if (!cities.isEmpty()) {
                            headerViewHolder.hotLayout.setVisibility(View.VISIBLE);
                            headerViewHolder.hotCities.removeAllChildViews();
                            for (City city : cities) {
                                Button button = (Button) View.inflate(CityListActivity.this,
                                        R.layout.city_item_view,
                                        null);
                                button.setTag(city);
                                button.setText(city.getName());
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onCityRefresh((City) v.getTag());
                                    }
                                });
                                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width,
                                        height);
                                headerViewHolder.hotCities.addView(button, params);
                            }
                        } else {
                            headerViewHolder.hotLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }


    private void initRelativeCity(final CityData cityData) {
        CommonUtil.unSubscribeSubs(relativeCitySubscription);
        relativeCitySubscription = Observable.from(cityData.getRelativeCities())
                .filter(new Func1<City, Boolean>() {
                    @Override
                    public Boolean call(City c) {
                        return cityData.getCityId() == city.getId() && c.getId() > 0 &&
                                !TextUtils.isEmpty(
                                c.getName());
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<City>>() {
                    @Override
                    public void call(List<City> cities) {
                        if (!cities.isEmpty()) {
                            headerViewHolder.relativeLayout.setVisibility(View.VISIBLE);
                            headerViewHolder.relativeCities.removeAllChildViews();
                            for (City city : cities) {
                                Button button = (Button) View.inflate(CityListActivity.this,
                                        R.layout.city_item_view,
                                        null);
                                button.setTag(city);
                                button.setText(city.getName());
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onCityRefresh((City) v.getTag());
                                    }
                                });
                                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width,
                                        height);
                                headerViewHolder.relativeCities.addView(button, params);
                            }
                        } else {
                            headerViewHolder.relativeLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void initAccessCity() {
        if (!accessCityList.isEmpty()) {
            headerViewHolder.accessLayout.setVisibility(View.VISIBLE);
            headerViewHolder.accessCities.removeAllChildViews();
            int size = accessCityList.size() > 3 ? 3 : accessCityList.size();
            for (int i = 0; i < size; i++) {
                City city = accessCityList.get(i);
                Button button = (Button) View.inflate(CityListActivity.this,
                        R.layout.city_item_view,
                        null);
                button.setTag(city);
                button.setText(city.getName());
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCityRefresh((City) v.getTag());
                    }
                });
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
                headerViewHolder.accessCities.addView(button, params);
            }
        } else {
            headerViewHolder.accessLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.local_layout:
                if (locationCity != null && locationCity.getId() > 0) {
                    onCityRefresh(locationCity);
                } else if (cityUtil != null) {
                    cityUtil.location();
                    headerViewHolder.localCity.setText(getString(R.string.action_location));
                    headerViewHolder.localCity.setTextColor(ContextCompat.getColor(this,
                            R.color.colorBlack3));
                }
                break;
            case R.id.all_city:
                onCityRefresh(new City(0, getString(R.string.all_city)));
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        headerViewHolder.cityLayout.setVisibility(s.length() > 0 ? View.GONE : View.VISIBLE);
        sideLayout.setVisibility(s.length() > 0 ? View.GONE : View.VISIBLE);
        adapter.setSearchText(s.toString());
    }

    @Override
    public void onCityClick(City city) {
        if (city != null) {
            onCityRefresh(city);
        }
    }

    private void onCityRefresh(City city) {
        // 隐藏软键盘
        if (imm != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (resultCity) {
            Intent intent = getIntent();
            intent.putExtra("city", city);
            setResult(RESULT_OK, intent);
            onBackPressed();
        } else {
            if (city.getId() != 0) {
                //存储最近选择的城市
                for (City accessCity : accessCityList) {
                    if (accessCity.getId()
                            .equals(city.getId())) {
                        accessCityList.remove(accessCity);
                        break;
                    }
                }
                accessCityList.add(0, city);
                Session.getInstance()
                        .saveAccessCities(this, accessCityList);
            }
            try {
                Session.getInstance()
                        .setMyCity(this, city);
            } catch (IOException e) {
                e.printStackTrace();
            }
            RxBus.getDefault()
                    .post(new RxEvent(RxEvent.RxEventType.CITY_CHANGE, city));
            if (isInitialPage) {
                // 首次进入选定城市，进入主界面
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.activity_anim_default,
                        R.anim.activity_anim_default);
            } else {
                onBackPressed();
            }
        }
    }

    private void onLocationCityRefresh(City result) {
        if (result != null && result.getId() > 0) {
            locationCity = result;
            headerViewHolder.localCity.setText(locationCity.getName());
            headerViewHolder.localCity.setTextColor(ContextCompat.getColor(this,
                    R.color.colorPrimary));
            headerViewHolder.imgLocalCity.setImageResource(R.mipmap.icon_location_primary_28_34);

        } else {
            headerViewHolder.localCity.setText(getString(R.string.action_repeat));
            headerViewHolder.localCity.setTextColor(ContextCompat.getColor(this,
                    R.color.colorBlack3));
            headerViewHolder.imgLocalCity.setImageResource(R.mipmap.icon_refresh_gray_27_27);
        }
    }

    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    void requestLocationPermission() {
        if (cityUtil != null) {
            cityUtil.location();
            headerViewHolder.localCity.setText(getString(R.string.action_location));
            headerViewHolder.localCity.setTextColor(ContextCompat.getColor(this,
                    R.color.colorBlack3));
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CityListActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
    }

    @Override
    public void onBackPressed() {
        if (isInitialPage) {
            return;
        }
        finish();
        overridePendingTransition(0, R.anim.slide_out_down);
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(loadSubscription,
                allCitySubscription,
                hotCitySubscription,
                relativeCitySubscription);
        super.onFinish();
    }

    static class HeaderViewHolder {
        @BindView(R.id.local_layout)
        LinearLayout localLayout;
        @BindView(R.id.local_city)
        TextView localCity;
        @BindView(R.id.all_city)
        TextView allCity;
        @BindView(R.id.img_local_city)
        ImageView imgLocalCity;
        @BindView(R.id.current_layout)
        LinearLayout currentLayout;
        @BindView(R.id.relative_cities)
        FlowLayoutForTextView relativeCities;
        @BindView(R.id.relative_layout)
        LinearLayout relativeLayout;
        @BindView(R.id.access_cities)
        FlowLayoutForTextView accessCities;
        @BindView(R.id.access_layout)
        LinearLayout accessLayout;
        @BindView(R.id.tv_hot)
        TextView tvHot;
        @BindView(R.id.hot_cities)
        FlowLayoutForTextView hotCities;
        @BindView(R.id.hot_layout)
        LinearLayout hotLayout;
        @BindView(R.id.city_layout)
        LinearLayout cityLayout;

        HeaderViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cityUtil != null) {
            cityUtil.startLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cityUtil != null) {
            cityUtil.stopLocation();
        }
    }
}