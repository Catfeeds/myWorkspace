package com.hunliji.marrybiz.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.Label;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.LinkUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.widget.TabPageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Suncloud on 2016/2/1.
 */
public class CustomOrdersFragment extends RefreshFragment implements TabPageIndicator
        .OnTabChangeListener {

    private ArrayList<CustomOrderListFragment> fragmentList;
    private ViewPager viewPager;
    private TabPageIndicator tabPageIndicator;
    private ArrayList<Label> titleLabels;
    private View rootView;
    private View progressBar;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_custom_orders, container, false);

        progressBar = rootView.findViewById(R.id.progressBar);
        tabPageIndicator = (TabPageIndicator) rootView.findViewById(R.id.tab_indicator);
        viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        MerchantUser user = Session.getInstance()
                .getCurrentUser(getActivity());
        boolean customSetMeal = false;
        if (user != null && user.getId() > 0) {
            SharedPreferences preferences = getActivity().getSharedPreferences(Constants.PREF_FILE,
                    Context.MODE_PRIVATE);
            customSetMeal = preferences.getBoolean("custom_setmeal_" + user.getId(), false);
        }
        if (customSetMeal) {
            initTabPager();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            new GetCustomSetMealsTask().executeOnExecutor(Constants.INFOTHEADPOOL);
        }

        return rootView;
    }

    private void initTabPager() {
        tabPageIndicator.setOnTabChangeListener(this);
        rootView.findViewById(R.id.menu)
                .setVisibility(View.VISIBLE);

        titleLabels = new ArrayList<>();
        Label title = new Label(null);
        title.setId(0);
        title.setName(getString(R.string.label_all));
        titleLabels.add(title);

        Label title1 = new Label(null);
        title1.setId(1);
        title1.setName(getString(R.string.label_wait_to_accept));
        titleLabels.add(title1);

        Label title2 = new Label(null);
        title2.setId(2);
        title2.setName(getString(R.string.label_wait_to_pay));
        titleLabels.add(title2);

        Label title3 = new Label(null);
        title3.setId(3);
        title3.setName(getString(R.string.label_wait_service));
        titleLabels.add(title3);

        Label title4 = new Label(null);
        title4.setId(4);
        title4.setName(getString(R.string.label_wait_refund));
        titleLabels.add(title4);

        Label title5 = new Label(null);
        title5.setId(5);
        title5.setName(getString(R.string.label_finished));
        titleLabels.add(title5);

        Label title6 = new Label(null);
        title6.setId(6);
        title6.setName(getString(R.string.label_closed));
        titleLabels.add(title6);

        fragmentList = new ArrayList<>();
        for (Label label : titleLabels) {
            fragmentList.add(CustomOrderListFragment.newInstance(label.getId()
                    .intValue(), label.getName()));
        }

        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getChildFragmentManager());
        tabPageIndicator.setPagerAdapter(tabPagerAdapter);
        viewPager.setAdapter(tabPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabPageIndicator.setCurrentItem(position);
                super.onPageSelected(position);
            }
        });
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    private class TabPagerAdapter extends FragmentPagerAdapter {

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleLabels.get(position)
                    .getName();
        }
    }

    private class GetCustomSetMealsTask extends AsyncTask<Object, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                String json = JSONUtil.getStringFromUrl(getActivity(),
                        Constants.getAbsUrl(String.format(Constants.HttpPath.CUSTOM_SETMEAL_LIST,
                                1,
                                1)));
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
            progressBar.setVisibility(View.GONE);
            if (getActivity() != null && getActivity().isFinishing()) {
                return;
            }
            MerchantUser user = Session.getInstance()
                    .getCurrentUser(getActivity());
            int totalCount = 0;
            if (jsonObject != null && user != null && user.getId() > 0) {
                totalCount = jsonObject.optInt("total_count");
                if (totalCount > 0) {
                    SharedPreferences preferences = getActivity().getSharedPreferences(Constants
                                    .PREF_FILE,
                            Context.MODE_PRIVATE);
                    preferences.edit()
                            .putBoolean("custom_setmeal_" + user.getId(), true)
                            .apply();
                }
                initTabPager();
            }
            if (totalCount == 0) {
                //                rootView.findViewById(R.id.empty_view)
                //                        .setVisibility(View.VISIBLE);
                if (user.getPropertyId() == Merchant.PROPERTY_WEDDING_PLAN) {
                    rootView.findViewById(R.id.notice_layout)
                            .setVisibility(View.GONE);
                } else {
                    rootView.findViewById(R.id.notice_layout)
                            .setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.notice_layout)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    LinkUtil.getInstance(getActivity())
                                            .getLink(Constants.LinkNames.CUSTOM_MEAL_EDU,
                                                    new OnHttpRequestListener() {
                                                        @Override
                                                        public void onRequestCompleted(Object obj) {
                                                            progressBar.setVisibility(View.GONE);
                                                            String url = (String) obj;
                                                            if (!JSONUtil.isEmpty(url)) {
                                                                Intent intent = new Intent(
                                                                        getActivity(),
                                                                        HljWebViewActivity.class);
                                                                intent.putExtra("path", url);
                                                                startActivity(intent);
                                                                getActivity()
                                                                        .overridePendingTransition(
                                                                        R.anim.slide_in_right,
                                                                        R.anim.activity_anim_default);
                                                            }
                                                        }

                                                        @Override
                                                        public void onRequestFailed(Object obj) {
                                                            progressBar.setVisibility(View.GONE);
                                                        }
                                                    });
                                }
                            });
                }
            }
            super.onPostExecute(jsonObject);
        }
    }
}
