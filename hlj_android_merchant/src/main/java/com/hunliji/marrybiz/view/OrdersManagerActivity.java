package com.hunliji.marrybiz.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.fragment.CustomOrdersFragment;
import com.hunliji.marrybiz.fragment.OnlineOrdersFragment;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.view.orders.ServiceOrderSearchActivity;
import com.hunliji.marrybiz.widget.CheckableRelativeGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OrdersManagerActivity extends HljBaseNoBarActivity implements CheckableRelativeGroup
        .OnCheckedChangeListener {

    private View dotMsgView;

    private int lastPosition = -1;
    private View searchImg;
    private MerchantUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_manager);
        setDefaultStatusBarPadding();
        CheckableRelativeGroup topTabRg = (CheckableRelativeGroup) findViewById(R.id.rg_tab);
        searchImg = findViewById(R.id.search);
        topTabRg.setOnCheckedChangeListener(this);
        dotMsgView = findViewById(R.id.dot_msg);

        user = Session.getInstance()
                .getCurrentUser(this);

        if (user != null && user.getPropertyId() == 2) {
            //婚礼策划商家 有定制套餐 需要显示tab
            topTabRg.setVisibility(View.VISIBLE);
            findViewById(R.id.rb_custom).setVisibility(View.VISIBLE);
            findViewById(R.id.title).setVisibility(View.GONE);
        } else {
            topTabRg.setVisibility(View.GONE);
            findViewById(R.id.title).setVisibility(View.VISIBLE);
        }
        selectTab(0);
        //请求未处理提醒接口
        new GetNotifyTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                Constants.getAbsUrl(Constants.HttpPath.GET_NOTIFY_INFO));
    }

    public void onBackPressed(View view) {
        super.onBackPressed();
    }

    public void onSearch(View view) {
        switch (lastPosition) {
            case 0:
                Intent intent = new Intent(this, ServiceOrderSearchActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_bottom,
                        R.anim.activity_anim_default);
                break;
            case 1:
                intent = new Intent(this, CustomOrdersSearchActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_bottom,
                        R.anim.activity_anim_default);
                break;
        }
    }

    private void selectTab(int position) {
        if (lastPosition == position) {
            return;
        }

        lastPosition = position;

        FragmentManager fm = getSupportFragmentManager();

        OnlineOrdersFragment onlineOrdersFragment = (OnlineOrdersFragment) fm.findFragmentByTag(
                "online_fragment");
        CustomOrdersFragment customOrdersFragment = (CustomOrdersFragment) fm.findFragmentByTag(
                "custom_fragment");

        FragmentTransaction ft = fm.beginTransaction();
        if (onlineOrdersFragment != null && !onlineOrdersFragment.isHidden()) {
            ft.hide(onlineOrdersFragment);
        }
        if (customOrdersFragment != null && !customOrdersFragment.isHidden()) {
            ft.hide(customOrdersFragment);
        }


        switch (position) {
            case 0:
                if (onlineOrdersFragment == null) {
                    ft.add(R.id.fragment_content, new OnlineOrdersFragment(), "online_fragment");
                } else {
                    ft.show(onlineOrdersFragment);
                }
                break;
            case 1:
                if (customOrdersFragment == null) {
                    ft.add(R.id.fragment_content, new CustomOrdersFragment(), "custom_fragment");
                } else {
                    ft.show(customOrdersFragment);
                }
                break;
        }

        ft.commitAllowingStateLoss();
    }

    @Override
    public void onCheckedChanged(CheckableRelativeGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_online:
                selectTab(0);
                break;
            case R.id.rb_custom:
                selectTab(1);
                break;
        }
    }

    private class GetNotifyTask extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(OrdersManagerActivity.this, url);
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
            super.onPostExecute(jsonObject);
            if (jsonObject != null) {
                int statusCode = jsonObject.optJSONObject("status")
                        .optInt("RetCode");
                if (statusCode == 0) {
                    boolean is_appointment_count_new = jsonObject.optJSONObject("data")
                            .optInt("appointment_count") > 0;
                    if (user != null && user.getPropertyId() == 2) {
                        if (is_appointment_count_new) {
                            dotMsgView.setVisibility(View.VISIBLE);
                        } else {
                            dotMsgView.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }

        }
    }
}
