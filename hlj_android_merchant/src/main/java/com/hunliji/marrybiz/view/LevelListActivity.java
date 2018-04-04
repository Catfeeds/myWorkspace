package com.hunliji.marrybiz.view;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.Privilege;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.hunliji.marrybiz.util.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Suncloud on 2016/1/8.
 */
public class LevelListActivity extends HljBaseActivity {

    @BindView(R.id.level_list)
    LinearLayout levelList;
    @BindView(R.id.hint_layout)
    LinearLayout hintLayout;
    @BindView(R.id.level_label)
    TextView levelLabel;
    @BindView(R.id.more_level)
    TextView moreLevel;
    @BindView(R.id.privilege_list)
    LinearLayout privilegeList;
    @BindView(R.id.scroll_view)
    HorizontalScrollView scrollView;

    private View view;
    private int position;
    private MerchantUser merchantUser;
    private SparseArray<ArrayList<Privilege>> privilegesArray;
    private ArrayList<View> views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        privilegesArray = new SparseArray<>();
        views = new ArrayList<>();
        merchantUser = Session.getInstance()
                .getCurrentUser(this);
        position = getIntent().getIntExtra("position", 0);
        String json = getIntent().getStringExtra("array");
        initArrayDate(json);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        ButterKnife.bind(this);
        findViewById(R.id.bg_line).setVisibility(View.VISIBLE);
        initLevelView();
        moreLevel.setVisibility(View.GONE);
        hintLayout.setVisibility(View.GONE);
    }

    private void initLevelView() {
        Point point = JSONUtil.getDeviceSize(this);
        final int width = point.x;
        int itemSize = Math.round(width / 10);
        int lineWidth = Math.round(width * 17 / 80);
        int headerLineWidth = Math.round(width / 2 - (position > 0 ? itemSize : 0) / 2);
        int footerLineWidth = Math.round(width / 2 - (position < 4 ? itemSize : 0) / 2);
        View lineView = View.inflate(this, R.layout.level_tree_line, null);
        lineView.findViewById(R.id.item_line)
                .setVisibility(View.GONE);
        levelList.addView(lineView,
                new LinearLayout.LayoutParams(headerLineWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        for (int i = 0; i < 5; i++) {
            View itemView = View.inflate(this, R.layout.level_tree_item, null);
            itemView.findViewById(R.id.item_line)
                    .setVisibility(View.GONE);
            TextView levelName = (TextView) itemView.findViewById(R.id.level_name);
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
            itemView.setTag(itemSize * i + lineWidth * i);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v != view) {
                        if (view != null) {
                            view.setScaleX(1);
                            view.setScaleY(1);
                        }
                        position = views.indexOf(v);
                        setPrivilegeList();
                    }
                }
            });
            views.add(itemView);
            levelList.addView(itemView, new LinearLayout.LayoutParams(itemSize, itemSize));
            lineView = View.inflate(this, R.layout.level_tree_line, null);
            levelList.addView(lineView,
                    new LinearLayout.LayoutParams(i == 4 ? footerLineWidth : lineWidth,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            lineView.findViewById(R.id.item_line)
                    .setVisibility(View.GONE);
        }
        setPrivilegeList();
    }

    private void initArrayDate(String json) {
        JSONArray array = null;
        try {
            array = new JSONObject(json).optJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        }
    }

    private void setPrivilegeList() {
        view = views.get(position);
        view.setScaleX(1.5f);
        view.setScaleY(1.5f);
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo((int) view.getTag(), 0);
            }
        }, 50);
        ArrayList<Privilege> privileges = privilegesArray.get(position);
        privilegeList.removeAllViews();
        if (merchantUser.getGradeLevel() == position) {
            levelLabel.setText(R.string.label_my_level);
        } else {
            int strId;
            switch (position) {
                case 0:
                    strId = R.string.label_level0;
                    break;
                case 1:
                    strId = R.string.label_level1;
                    break;
                case 2:
                    strId = R.string.label_level2;
                    break;
                case 3:
                    strId = R.string.label_level3;
                    break;
                default:
                    strId = R.string.label_level4;
                    break;
            }
            levelLabel.setText(getString(R.string.label_level, getString(strId)));
        }
        if (privileges != null) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int size = Math.round(dm.density * 36);
            for (Privilege privilege : privileges) {
                View view = View.inflate(LevelListActivity.this,
                        R.layout.level_privilege_item,
                        null);
                ViewHolder holder = new ViewHolder(view);
                holder.line.setVisibility(privilegeList.getChildCount() == 0 ? View.GONE : View
                        .VISIBLE);
                holder.arrow.setVisibility(privilege.isAchieved() && !JSONUtil.isEmpty(privilege
                        .getUrl()) ? View.VISIBLE : View.INVISIBLE);
                holder.name.setTextColor(getResources().getColor(privilege.isAchieved() ? R.color
                        .colorBlack2 : R.color.colorGray3));
                holder.describe.setTextColor(getResources().getColor(privilege.isAchieved() ? R
                        .color.colorGray : R.color.colorGray3));
                holder.name.setText(privilege.getName());
                holder.describe.setText(privilege.getDescribe());
                String path = JSONUtil.getImagePathForRound(privilege.getLogo(), size);
                if (!JSONUtil.isEmpty(path)) {
                    ImageLoadTask task = new ImageLoadTask(holder.icon, 0, true);
                    holder.icon.setTag(path);
                    task.loadImage(path,
                            size,
                            ScaleMode.WIDTH,
                            new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
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
                                Intent intent = new Intent(LevelListActivity.this,
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

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'level_privilege_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     * .com/avast)
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
