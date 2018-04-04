package com.hunliji.hljhttplibrary.utils;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.modelwrappers.ChildrenArea;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.R;
import com.hunliji.hljhttplibrary.api.CommonApi;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 专门用于省市区数据的同步和解析的帮助类
 * 解析比较耗费时间,需要异步操作
 * Created by chen_bin on 2017/8/21 0021.
 */
public class AddressAreaUtil {
    private ArrayList<ChildrenArea> addressAreas;
    private List<String> primaryLevelNames;
    private List<List<String>> secondaryLevelNames;
    private List<List<List<String>>> tertiaryLevelNames;
    private Subscription initSub;
    private HljHttpSubscriber syncSub;
    private static AddressAreaUtil INSTANCE;

    private AddressAreaUtil() {}

    public static AddressAreaUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AddressAreaUtil();
        }
        return INSTANCE;
    }

    public void getAddressAreasData(
            final Context context, final OnFinishedListener onFinishedListener) {
        if (initSub == null || initSub.isUnsubscribed()) {
            initSub = Observable.create(new Observable.OnSubscribe<Void>() {
                @Override
                public void call(Subscriber<? super Void> subscriber) {
                    if (subscriber.isUnsubscribed()) {
                        return;
                    }
                    try {
                        InputStream in;
                        File file = context.getFileStreamPath(HljCommon.FileNames
                                .ADDRESS_AREA_FILE);
                        if (file != null && file.exists()) {
                            in = context.openFileInput(HljCommon.FileNames.ADDRESS_AREA_FILE);
                        } else {
                            in = context.getResources()
                                    .openRawResource(R.raw.address_area);
                        }
                        String str = CommonUtil.readStreamToString(in);
                        parseAddressAreasData(GsonUtil.getGsonInstance()
                                .fromJson(str, JsonArray.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Void>() {

                        @Override
                        public void call(Void aVoid) {
                            syncOnlineAddressAreaData(context);
                            onFinishedListener.onFinished(addressAreas,
                                    primaryLevelNames,
                                    secondaryLevelNames,
                                    tertiaryLevelNames);
                        }
                    });
        }
    }

    @SuppressWarnings("unchecked")
    private void syncOnlineAddressAreaData(final Context context) {
        if (syncSub == null || syncSub.isUnsubscribed()) {
            syncSub = HljHttpSubscriber.buildSubscriber(context)
                    .setOnNextListener(new SubscriberOnNextListener<List<ChildrenArea>>() {

                        @Override
                        public void onNext(List<ChildrenArea> areas) {
                            try {
                                String str = GsonUtil.getGsonInstance()
                                        .toJson(areas,
                                                new TypeToken<ArrayList<ChildrenArea>>() {}
                                                .getType());
                                OutputStreamWriter out = new OutputStreamWriter(context
                                        .openFileOutput(
                                        HljCommon.FileNames.ADDRESS_AREA_FILE,
                                        Context.MODE_PRIVATE));
                                out.write(str);
                                out.flush();
                                out.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .build();
            CommonApi.getAllAddressObb()
                    .subscribe(syncSub);
        }
    }

    public int[] getIndexesFromArea(
            List<ChildrenArea> areas,
            ChildrenArea selectedProvince,
            ChildrenArea selectedCity,
            ChildrenArea selectedArea) {
        int[] result = new int[]{0, 0, 0};
        if (selectedProvince != null & !CommonUtil.isCollectionEmpty(areas)) {
            for (ChildrenArea province : areas) {
                if (province.getId() == selectedProvince.getId()) {
                    result[0] = areas.indexOf(province);
                    if (selectedCity != null && !CommonUtil.isCollectionEmpty(province
                            .getChildrenAreas())) {
                        for (ChildrenArea city : province.getChildrenAreas()) {
                            if (city.getId() == selectedCity.getId()) {
                                result[1] = province.getChildrenAreas()
                                        .indexOf(city);
                                if (selectedArea != null && !CommonUtil.isCollectionEmpty(city
                                        .getChildrenAreas())) {
                                    for (ChildrenArea area : city.getChildrenAreas()) {
                                        if (area.getId() == selectedArea.getId()) {
                                            result[2] = city.getChildrenAreas()
                                                    .indexOf(area);
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
        return result;
    }

    public ChildrenArea[] getAreasFromName(
            List<ChildrenArea> areas, String provinceName, String cityName, String areaName) {
        ChildrenArea[] result = new ChildrenArea[]{null, null, null};
        if (!TextUtils.isEmpty(provinceName) & !CommonUtil.isCollectionEmpty(areas)) {
            for (ChildrenArea province : areas) {
                if (TextUtils.equals(province.getName(), provinceName)) {
                    result[0] = province;
                    if (!TextUtils.isEmpty(cityName) && !CommonUtil.isCollectionEmpty(province
                            .getChildrenAreas())) {
                        for (ChildrenArea city : province.getChildrenAreas()) {
                            if (TextUtils.equals(city.getName(), cityName)) {
                                result[1] = city;
                                if (!TextUtils.isEmpty(areaName) && !CommonUtil.isCollectionEmpty(
                                        city.getChildrenAreas())) {
                                    for (ChildrenArea area : city.getChildrenAreas()) {
                                        if (TextUtils.equals(area.getName(), areaName)) {
                                            result[2] = area;
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
        return result;
    }

    private void parseAddressAreasData(JsonArray jsonArray) {
        if (jsonArray == null || jsonArray.size() == 0) {
            return;
        }
        addressAreas = new ArrayList<>();
        primaryLevelNames = new ArrayList<>();
        secondaryLevelNames = new ArrayList<>();
        tertiaryLevelNames = new ArrayList<>();
        for (int i = 0, size = jsonArray.size(); i < size; i++) {
            ChildrenArea parentArea = GsonUtil.getGsonInstance()
                    .fromJson(jsonArray.get(i), ChildrenArea.class);
            if (!CommonUtil.isCollectionEmpty(parentArea.getChildrenAreas())) {
                addressAreas.add(parentArea);
                primaryLevelNames.add(parentArea.getName());
                secondaryLevelNames.add(parentArea.getChildrenNames());
                List<List<String>> childrenNames = new ArrayList<>();
                for (ChildrenArea childrenArea : parentArea.getChildrenAreas()) {
                    childrenNames.add(childrenArea.getChildrenNames());
                }
                tertiaryLevelNames.add(childrenNames);
            }
        }
    }

    public void onDestroy() {
        CommonUtil.unSubscribeSubs(initSub, syncSub);
    }
}