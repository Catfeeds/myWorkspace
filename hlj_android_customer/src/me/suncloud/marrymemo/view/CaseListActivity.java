package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.hunliji.hljtrackerlibrary.TrackerSite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.CaseListFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.Label;
import me.suncloud.marrymemo.model.MenuItem;
import me.suncloud.marrymemo.model.MerchantProperty;
import me.suncloud.marrymemo.util.ImageLoadUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.newsearch.NewSearchActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import me.suncloud.marrymemo.widget.PropertySortMenuFilterView;

/**
 * Created by wangtao on 2016/12/8.
 */

public class CaseListActivity extends HljBaseNoBarActivity {
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.property_menu_layout)
    GridLayout propertyMenuLayout;
    @BindView(R.id.menu_filter_view)
    PropertySortMenuFilterView merchantMenuFilterView;
    @BindView(R.id.scrollable_layout)
    ScrollableLayout scrollableLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private City city;
    private long propertyId;
    public String firstQueryTime; //时间戳
    private ScrollAbleFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        city = Session.getInstance()
                .getMyCity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_list);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initView();
        progressBar.setVisibility(View.VISIBLE);
        new GetMerchantFilter().executeOnExecutor(Constants.INFOTHEADPOOL,
                Constants.getAbsUrl(String.format(Constants.HttpPath.GET_MERCHANT_FILTER,
                        city.getId())));
    }

    private void initView() {
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

        //筛选视图
        merchantMenuFilterView.setScrollableLayout(scrollableLayout);
        merchantMenuFilterView.setProperty(propertyId);
        //刷新回调
        merchantMenuFilterView.setFilterCallback(new PropertySortMenuFilterView.FilterCallback() {
            @Override
            public void onFilterChange(Map<String, Label> filterMap) {
                onRefreshFragment(filterMap);
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

    /**
     * 返回
     */
    @OnClick(R.id.btn_back)
    public void onBack() {
        onBackPressed();
    }

    /**
     * 搜索
     */
    @OnClick(R.id.btn_search)
    public void onSearch() {
        if (merchantMenuFilterView.hideMenu(0)) {
            return;
        }
        Intent intent = new Intent(this, NewSearchActivity.class);
        startActivity(intent);
    }

    /**
     * 跳转通知私信
     */
    @OnClick(R.id.msg_layout)
    public void onMessage() {
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        Intent intent = new Intent(this, MessageHomeActivity.class);
        startActivity(intent);
    }


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
                JSONArray array = jsonObject.optJSONArray("properties");
                List<MerchantProperty> properties = new ArrayList<>();
                if (array != null && array.length() > 0) {
                    for (int i = 0, size = array.length(); i < size; i++) {
                        MerchantProperty merchantProperty = new MerchantProperty(array
                                .optJSONObject(
                                i));
                        if (merchantProperty.getId() != Constants.Property.HOTEL) {
                            if (merchantProperty.getChildren() != null && !merchantProperty
                                    .getChildren()
                                    .isEmpty()) {
                                MerchantProperty allProperty = new MerchantProperty(null);
                                allProperty.setName(getString(R.string.label_all));
                                merchantProperty.getChildren()
                                        .add(0, allProperty);
                            }
                            properties.add(merchantProperty);
                        }
                    }
                }
                initPropertyMenu(properties);
                merchantMenuFilterView.setProperties(properties);

                List<MenuItem> sortItems = new ArrayList<>();
                MenuItem menuItem1 = new MenuItem(null);
                menuItem1.setName(getString(R.string.sort_label14));
                menuItem1.setKeyWord("default");
                menuItem1.setOrder("desc");
                sortItems.add(menuItem1);
                MenuItem menuItem2 = new MenuItem(null);
                menuItem2.setName(getString(R.string.sort_labe21));
                menuItem2.setKeyWord("collectors_count");
                menuItem2.setOrder("desc");
                sortItems.add(menuItem2);
                MenuItem menuItem3 = new MenuItem(null);
                menuItem3.setName(getString(R.string.sort_label3));
                menuItem3.setKeyWord("published_time");
                menuItem3.setOrder("desc");
                sortItems.add(menuItem3);
                merchantMenuFilterView.setSortItems(sortItems);
                //刷新商家列表
                merchantMenuFilterView.onRefresh();
            }
            super.onPostExecute(jsonObject);
        }
    }

    private void initPropertyMenu(List<MerchantProperty> properties) {
        int iconSize = Util.dp2px(this, 40);
        propertyMenuLayout.setVisibility(View.VISIBLE);
        for (MerchantProperty property : properties) {
            View.inflate(this, R.layout.merchant_filter_property, propertyMenuLayout);
            int index = propertyMenuLayout.getChildCount() - 1;
            View view = propertyMenuLayout.getChildAt(index);
            view.setTag(property);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MerchantProperty property = (MerchantProperty) v.getTag();
                    merchantMenuFilterView.setProperty(property.getId());
                    scrollableLayout.scrollToBottom();
                    merchantMenuFilterView.onRefresh();
                }
            });
            ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            ImageLoadUtil.loadImageView(this,
                    JSONUtil.getImagePathWithoutFormat(property.getIcon(), iconSize),
                    R.mipmap.icon_empty_image,
                    ivIcon,
                    true);
            tvName.setText(property.getName());
        }
    }


    private void onRefreshFragment(Map<String, Label> filterMap) {
        fragment = (ScrollAbleFragment) getSupportFragmentManager().findFragmentByTag(
                "caseListFragment");
        if (TextUtils.isEmpty(firstQueryTime)) {
            firstQueryTime = Util.time2UtcString(new Date());
        }
        MerchantProperty property = (MerchantProperty) filterMap.get(PropertySortMenuFilterView
                .MenuType.PROPERTY);
        MerchantProperty childProperty = (MerchantProperty) filterMap.get
                (PropertySortMenuFilterView.MenuType.CHILD_PROPERTY);
        MenuItem sort = (MenuItem) filterMap.get(PropertySortMenuFilterView.MenuType.SORT);

        HashMap<String, String> queries = new HashMap<>();
        queries.put("first_query_time", firstQueryTime);
        queries.put("property", String.valueOf(property.getId()));
        if (city.getId() > 0) {
            queries.put("city", String.valueOf(city.getId()));
        }
        if (childProperty != null) {
            queries.put("category_id", String.valueOf(childProperty.getId()));
        }
        if (sort != null) {
            queries.put("sort[key]", sort.getKeyWord());
            queries.put("sort[order]", sort.getOrder());
        }
        if (fragment == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            fragment = CaseListFragment.newInstance(queries);
            ft.add(R.id.content_layout, fragment, "caseListFragment");
            ft.commitAllowingStateLoss();
        } else {
            fragment.refresh(queries);
        }
    }

}
