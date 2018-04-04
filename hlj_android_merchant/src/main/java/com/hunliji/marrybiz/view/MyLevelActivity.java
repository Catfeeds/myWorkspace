package com.hunliji.marrybiz.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.FlowAdapter;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.Poster;
import com.hunliji.marrybiz.model.Privilege;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.LinkUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.widget.CheckableRelativeLayout;
import com.makeramen.rounded.RoundedImageView;
import com.slider.library.Indicators.CirclePageExIndicator;
import com.slider.library.SliderLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Suncloud on 2016/1/7.
 */
public class MyLevelActivity extends HljBaseActivity {

    @BindView(R.id.level_list)
    LinearLayout levelList;
    @BindView(R.id.level_up)
    TextView levelUp;
    @BindView(R.id.privilege_list)
    LinearLayout privilegeList;
    @BindView(R.id.posters_view)
    SliderLayout sliderLayout;
    @BindView(R.id.flow_indicator)
    CirclePageExIndicator indicator;
    @BindView(R.id.banner_layout)
    RelativeLayout bannerLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.scroll_view)
    HorizontalScrollView scrollView;

    private MerchantUser merchantUser;
    private FlowAdapter flowAdapter;
    private SparseArray<ArrayList<Privilege>> privilegesArray;
    private String jsonStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE);
        int month = preferences.getInt("month2", -1);
        privilegesArray = new SparseArray<>();
        merchantUser = Session.getInstance()
                .getCurrentUser(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_level);
        ButterKnife.bind(this);
        setOkText(R.string.title_activity_level_rule);
        initLevelView();
        levelUp.setText(getString(R.string.label_merchant_grade_rate, String.valueOf(merchantUser.getRate())));
        Point point = JSONUtil.getDeviceSize(this);
        progressBar.setVisibility(View.VISIBLE);

        new GetGradeLevelListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                Constants.getAbsUrl(Constants.HttpPath.GRADE_LEVEL_LIST));

        flowAdapter = new FlowAdapter(this);
        bannerLayout.getLayoutParams().height = Math.round(point.x / 5);
        sliderLayout.setPagerAdapter(flowAdapter);
        flowAdapter.setSliderLayout(sliderLayout);
        sliderLayout.setCustomIndicator(indicator);
        sliderLayout.setPresetTransformer(4);
        new GetBannerTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                Constants.getAbsUrl(Constants.HttpPath.POSTER_BLOCK_URL,
                        Constants.BLOCK_ID.MyLevelActivity,
                        merchantUser.getCity_code()));
    }


    @OnClick(R.id.level_up)
    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        progressBar.setVisibility(View.VISIBLE);
        LinkUtil.getInstance(this)
                .getLink(Constants.LinkNames.GRADE_RULES, new OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {
                        progressBar.setVisibility(View.GONE);
                        String url = (String) obj;
                        if (!JSONUtil.isEmpty(url)) {
                            Intent intent = new Intent(MyLevelActivity.this,
                                    HljWebViewActivity.class);
                            intent.putExtra("path", url);
                            intent.putExtra("title", getString(R.string.title_activity_level_rule));
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }

                    @Override
                    public void onRequestFailed(Object obj) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void initLevelView() {
        Point point = JSONUtil.getDeviceSize(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = point.x;
        int itemSize = Math.round(width / 10);
        int logoSize = Math.round(width * 3 / 16);
        int logoWidth = Math.max(logoSize, Math.round(dm.density * 66));
        int lineWidth = Math.round(width * 17 / 80);
        int headerLineWidth = Math.round(width / 2 - (merchantUser.getGradeLevel() > 0 ? itemSize
                : logoWidth) / 2);
        int footerLineWidth = Math.round(width / 2 - (merchantUser.getGradeLevel() < 4 ? itemSize
                : logoWidth) / 2);
        CheckableRelativeLayout lineView = (CheckableRelativeLayout) View.inflate(this,
                R.layout.level_tree_line,
                null);
        levelList.addView(lineView,
                new LinearLayout.LayoutParams(headerLineWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        for (int i = 0; i < 5; i++) {
            View view;
            if (merchantUser.getGradeLevel() == i) {
                view = View.inflate(this, R.layout.level_tree_merchant, null);
                View line1 = view.findViewById(R.id.line1);
                View line2 = view.findViewById(R.id.line2);
                ImageView lvIconView = (ImageView) view.findViewById(R.id.lv_icon);
                RoundedImageView logoView = (RoundedImageView) view.findViewById(R.id.logo);
                line1.getLayoutParams().width = logoWidth / 2;
                line2.getLayoutParams().width = logoWidth / 2;
                logoView.getLayoutParams().width = logoView.getLayoutParams().height = logoSize;
                logoView.setCornerRadius(logoSize / 2);
                ((RelativeLayout.LayoutParams) lvIconView.getLayoutParams()).topMargin = Math.round(
                        logoSize - 7 * dm.density);
                switch (i) {
                    case 0:
                        lvIconView.setImageResource(R.drawable.icon_merchant_level0);
                        break;
                    case 1:
                        lvIconView.setImageResource(R.drawable.icon_merchant_level1);
                        break;
                    case 2:
                        lvIconView.setImageResource(R.drawable.icon_merchant_level2);
                        break;
                    case 3:
                        lvIconView.setImageResource(R.drawable.icon_merchant_level3);
                        break;
                    default:
                        lvIconView.setImageResource(R.drawable.icon_merchant_level4);
                        break;
                }
                String path = JSONUtil.getImagePath(merchantUser.getLogoPath(), logoSize);
                if (!JSONUtil.isEmpty(path)) {
                    ImageLoadTask task = new ImageLoadTask(logoView, null, 0);
                    logoView.setTag(path);
                    task.loadImage(path,
                            logoSize,
                            ScaleMode.WIDTH,
                            new AsyncBitmapDrawable(getResources(),
                                    R.mipmap.icon_avatar_primary,
                                    task));
                } else {
                    logoView.setImageResource(R.mipmap.icon_avatar_primary);
                }
                levelList.addView(view,
                        new LinearLayout.LayoutParams(logoWidth,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
            } else {
                view = View.inflate(this, R.layout.level_tree_item, null);

                TextView levelName = (TextView) view.findViewById(R.id.level_name);
                switch (i) {
                    case 0:
                        levelName.setText(R.string.label_level0);
                        break;
                    case 1:
                        levelName.setText(R.string.label_level1);
                        break;
                    case 2:
                        levelName.setText(R.string.label_level2);
                        break;
                    case 3:
                        levelName.setText(R.string.label_level3);
                        break;
                    default:
                        levelName.setText(R.string.label_level4);
                        break;
                }
                ((Checkable) view).setChecked(i < merchantUser.getGradeLevel());
                levelList.addView(view, new LinearLayout.LayoutParams(itemSize, itemSize));
            }
            view.setTag(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (privilegesArray.size() == 0) {
                        return;
                    }
                    int index = (int) v.getTag();
                    Intent intent = new Intent(MyLevelActivity.this, LevelListActivity.class);
                    intent.putExtra("array", jsonStr);
                    intent.putExtra("position", index);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            });
            lineView = (CheckableRelativeLayout) View.inflate(this, R.layout.level_tree_line, null);
            lineView.setChecked(i < merchantUser.getGradeLevel());
            levelList.addView(lineView,
                    new LinearLayout.LayoutParams(i == 4 ? footerLineWidth : lineWidth,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        scrollView.setTag(merchantUser.getGradeLevel() == 0 ? 0 : merchantUser.getGradeLevel() *
                (lineWidth + itemSize) + (logoWidth - itemSize) / 2);
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo((int) scrollView.getTag(), 0);
            }
        }, 100);
    }

    @OnClick(R.id.more_level_layout)
    public void moreLevel() {
        if (privilegesArray.size() == 0) {
            return;
        }
        Intent intent = new Intent(MyLevelActivity.this, LevelListActivity.class);
        intent.putExtra("array", jsonStr);
        intent.putExtra("position", merchantUser.getGradeLevel());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    private class GetGradeLevelListTask extends AsyncTask<String, Object, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                jsonStr = JSONUtil.getStringFromUrl(MyLevelActivity.this, params[0]);
                if (JSONUtil.isEmpty(jsonStr)) {
                    return null;
                }
                return new JSONObject(jsonStr).optJSONArray("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray array) {
            progressBar.setVisibility(View.GONE);
            if (array != null && array.length() > 0) {
                for (int i = 0, size = array.length(); i < size; i++) {
                    JSONObject jsonObject = array.optJSONObject(i);
                    int level = jsonObject.optInt("level");
                    JSONArray privilegeArray = jsonObject.optJSONArray("privilege_list");
                    ArrayList<Privilege> privileges = new ArrayList<>();
                    if (privilegeArray != null && privilegeArray.length() > 0) {
                        for (int j = 0, s = privilegeArray.length(); j < s; j++) {
                            privileges.add(new Privilege(privilegeArray.optJSONObject(j)));
                        }
                    }
                    privilegesArray.put(level, privileges);
                }
                ArrayList<Privilege> privileges = privilegesArray.get(merchantUser.getGradeLevel());
                if (privileges != null) {
                    DisplayMetrics dm = getResources().getDisplayMetrics();
                    int size = Math.round(dm.density * 36);
                    for (Privilege privilege : privileges) {
                        View view = View.inflate(MyLevelActivity.this,
                                R.layout.level_privilege_item,
                                null);
                        ViewHolder holder = new ViewHolder(view);
                        holder.line.setVisibility(privilegeList.getChildCount() == 0 ? View.GONE
                                : View.VISIBLE);
                        holder.arrow.setVisibility(privilege.isAchieved() && !JSONUtil.isEmpty(
                                privilege.getUrl()) ? View.VISIBLE : View.INVISIBLE);
                        holder.name.setTextColor(getResources().getColor(privilege.isAchieved() ?
                                R.color.colorBlack2 : R.color.colorGray3));
                        holder.describe.setTextColor(getResources().getColor(privilege.isAchieved
                                () ? R.color.colorGray : R.color.colorGray3));
                        holder.name.setText(privilege.getName());
                        holder.describe.setText(privilege.getDescribe());
                        String path = JSONUtil.getImagePathForRound(privilege.getLogo(), size);
                        if (!JSONUtil.isEmpty(path)) {
                            ImageLoadTask task = new ImageLoadTask(holder.icon, 0, true);
                            holder.icon.setTag(path);
                            task.loadImage(path,
                                    size,
                                    ScaleMode.WIDTH,
                                    new AsyncBitmapDrawable(getResources(),
                                            R.mipmap.icon_empty_image,
                                            task));
                        } else {
                            holder.icon.setImageBitmap(null);
                        }
                        view.setTag(privilege);
                        if (privilege.isAchieved()) {
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Privilege p = (Privilege) v.getTag();
                                    if (!JSONUtil.isEmpty(p.getUrl())) {
                                        Intent intent = new Intent(MyLevelActivity.this,
                                                HljWebViewActivity.class);
                                        intent.putExtra("path", p.getUrl());
                                        intent.putExtra("title", p.getName());
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in_right,
                                                R.anim.activity_anim_default);
                                    }
                                }
                            });
                        }
                        privilegeList.addView(view);
                    }
                }

            }
            super.onPostExecute(array);
        }
    }


    private class GetBannerTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String json = JSONUtil.getStringFromUrl(MyLevelActivity.this, params[0]);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                JSONObject jsonObject = new JSONObject(json).optJSONObject("data");
                if (jsonObject != null) {
                    return jsonObject.optJSONObject("floors");
                }
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
            List<Poster> posterList = Util.getPosterList(jsonObject,
                    Constants.POST_SITES.MERCHANT_GRADE_BANNER,
                    false);
            flowAdapter.setmDate(posterList);
            if (bannerLayout != null && sliderLayout != null) {
                if (flowAdapter.getCount() == 0) {
                    sliderLayout.stopAutoCycle();
                    bannerLayout.setVisibility(View.GONE);
                } else {
                    bannerLayout.setVisibility(View.VISIBLE);
                    if (flowAdapter.getCount() > 1) {
                        sliderLayout.startAutoCycle();
                    } else {
                        sliderLayout.stopAutoCycle();
                    }
                }
            }
            super.onPostExecute(jsonObject);
        }
    }


    @Override
    public void onPause() {
        if (sliderLayout != null) {
            sliderLayout.stopAutoCycle();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (sliderLayout != null && flowAdapter.getCount() > 0) {
            sliderLayout.startAutoCycle();
        }
        super.onResume();
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'level_privilege_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.line)
        View line;
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.describe)
        TextView describe;
        @BindView(R.id.arrow)
        ImageView arrow;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
