package com.hunliji.marrybiz.fragment.market;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.ShopStatic;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.widget.CheckableLinearGroup;
import com.hunliji.marrybiz.widget.CheckableLinearLayout2;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 店铺数据fragment
 * Created by jinxin on 2016/6/17.
 */
public class ShopDataFragment extends RefreshFragment implements CheckableLinearGroup
        .OnCheckedChangeListener {


    public final static int STORE_SHOP_DATA = 0;//店铺数据
    public final static int WORK_SHOP_DATA = 1;//套餐数据
    public final static int CASE_SHOP_DATA = 2;//案例数据
    @BindView(R.id.store_count)
    TextView storeCount;
    @BindView(R.id.store_menu)
    CheckableLinearLayout2 storeMenu;
    @BindView(R.id.work_count)
    TextView workCount;
    @BindView(R.id.work_menu)
    CheckableLinearLayout2 workMenu;
    @BindView(R.id.case_count)
    TextView caseCount;
    @BindView(R.id.case_menu)
    CheckableLinearLayout2 caseMenu;
    @BindView(R.id.shop_data_tab)
    CheckableLinearGroup shopDataTab;
    @BindView(R.id.fragment)
    ViewPager fragment;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private ShopDataInfoFragment storeFragment;//店铺数据
    private ShopDataInfoFragment workFragment;//套餐数据
    private ShopDataInfoFragment caseFragment;//案例数据

    private boolean isLoad;
    private String currentUrl;
    private ShopStatic shopStatic;

    private Unbinder unbinder;

    public static ShopDataFragment newInstance() {
        return new ShopDataFragment();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_data, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getTask();
    }

    private void getTask() {
        progressBar.setVisibility(View.VISIBLE);
        currentUrl = Constants.getAbsUrl(Constants.HttpPath.GET_SHOP_STATIC);
        new GetShopStaticTask().executeOnExecutor(Constants.LISTTHEADPOOL, currentUrl);
    }

    private void initEvent() {
        shopDataTab.setOnCheckedChangeListener(this);
    }

    private class GetShopStaticTask extends AsyncTask<String, Object, JSONObject> {
        private String url;

        private GetShopStaticTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            url = strings[0];
            try {
                String json = JSONUtil.getStringFromUrl(getActivity(), url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (getActivity() == null || getActivity().isFinishing() || isDetached()) {
                return;
            }
            if (url.equals(currentUrl)) {
                progressBar.setVisibility(View.GONE);
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject("data");
                    if (jsonObject != null) {
                        try {
                            shopStatic = new ShopStatic(jsonObject);
                            initView();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                isLoad = false;
            }
            super.onPostExecute(jsonObject);
        }
    }

    private void initView() {
        initEvent();
        fragment.setAdapter(new SectionsPagerAdapter(getFragmentManager()));
        fragment.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        storeMenu.setChecked(true);
                        break;
                    case 1:
                        workMenu.setChecked(true);
                        break;
                    case 2:
                        caseMenu.setChecked(true);
                        break;
                }
                super.onPageSelected(position);
            }
        });
        fragment.setOffscreenPageLimit(2);

        //昨天数据取最后一个
        if (shopStatic != null) {
            if (shopStatic.isPr0()) {
                if (shopStatic.getShopDateList() != null && shopStatic.getShopDateList()
                        .size() > 0) {

                    int position = shopStatic.getShopDateList()
                            .size() - 1;
                    storeCount.setText(String.valueOf(shopStatic.getStoreList()
                            .get(position)));
                    workCount.setText(String.valueOf(shopStatic.getWorkList()
                            .get(position)));
                    caseCount.setText(String.valueOf(shopStatic.getCaseList()
                            .get(position)));
                } else {
                    storeCount.setText("0");
                    workCount.setText("0");
                    caseCount.setText("0");
                }
            } else {
                storeCount.setText("*");
                workCount.setText("*");
                caseCount.setText("*");
            }
        } else {
            storeCount.setText("0");
            workCount.setText("0");
            caseCount.setText("0");
        }
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCheckedChanged(CheckableLinearGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.store_menu:
                fragment.setCurrentItem(0, false);
                break;
            case R.id.work_menu:
                fragment.setCurrentItem(1, false);
                break;
            case R.id.case_menu:
                fragment.setCurrentItem(2, false);
                break;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (storeFragment == null) {
                        storeFragment = ShopDataInfoFragment.newInstance(shopStatic,
                                STORE_SHOP_DATA);
                    }
                    return storeFragment;
                case 1:
                    if (workFragment == null) {
                        workFragment = ShopDataInfoFragment.newInstance(shopStatic, WORK_SHOP_DATA);
                    }
                    return workFragment;
                case 2:
                    if (caseFragment == null) {
                        caseFragment = ShopDataInfoFragment.newInstance(shopStatic, CASE_SHOP_DATA);
                    }
                    return caseFragment;
                default:
                    if (storeFragment == null) {
                        storeFragment = ShopDataInfoFragment.newInstance(shopStatic,
                                STORE_SHOP_DATA);
                    }
                    return storeFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.label_store_data_yesterday).toUpperCase();
                case 1:
                    return getString(R.string.label_work_data_yesterday).toUpperCase();
                case 2:
                    return getString(R.string.label_case_data_yesterday).toUpperCase();
                default:
                    return getString(R.string.label_store_data_yesterday).toUpperCase();
            }
        }
    }
}
