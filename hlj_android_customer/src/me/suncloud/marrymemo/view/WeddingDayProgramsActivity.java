package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljtrackerlibrary.HljTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.WeddingProgram;
import me.suncloud.marrymemo.task.StatusHttpDeleteTask;
import me.suncloud.marrymemo.task.StatusHttpGetTask;
import me.suncloud.marrymemo.task.StatusHttpPatchTask;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.TextShareUtil;
import me.suncloud.marrymemo.util.TrackerUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.DSExpandableController;
import me.suncloud.marrymemo.widget.DSExpandableListView;
import me.suncloud.marrymemo.widget.SwipeExpandableListView;


public class WeddingDayProgramsActivity extends HljBaseNoBarActivity implements AbsListView
        .OnScrollListener {

    @Override
    public String pageTrackTagName() {
        return "婚礼当日流程";
    }

    private ArrayList<WeddingProgram> weddingPrograms;
    private DSExpandableListView listView;
    private PlanExpandableListAdapter adapter;
    private View headView;
    private View tipsView;
    private View progressBar;
    private View empty;
    private int addedViewWidth;
    private View addGroupView;
    private Dialog dialog;
    private Dialog editDialog;
    private int expandedGroupCount = 0;
    private TextView editBtn;
    private boolean isEditMode;
    private View addView;
    private int selectedGroupPosition = -1;
    public static final int TITLE_LIMIT = 10;
    private Dialog shareDialog;
    private TextShareUtil shareUtil;
    private Dialog progressDialog;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HljShare.RequestCode.SHARE_TO_QQZONE:
                    trackerShare("QQZone");
                    break;
                case HljShare.RequestCode.SHARE_TO_QQ:
                    trackerShare("QQ");
                    break;
                case HljShare.RequestCode.SHARE_TO_PENGYOU:
                    trackerShare("Timeline");
                    break;
                case HljShare.RequestCode.SHARE_TO_WEIXIN:
                    trackerShare("Session");
                    break;
            }
            return false;
        }
    });

    private DSExpandableListView.DropListener onDrop = new DSExpandableListView.DropListener() {

        @Override
        public void drop(int from, int to) {
            WeddingProgram target = weddingPrograms.get(from);
            weddingPrograms.remove(from);
            weddingPrograms.add(to, target);
            long position = target.getPosition();
            adapter.notifyDataSetChanged();

            if (from > to) {
                for (int i = to; i < from; i++) {
                    WeddingProgram tmp = weddingPrograms.get(i);
                    tmp.setPosition(weddingPrograms.get(i + 1)
                            .getPosition());
                }
            } else {
                for (int i = to; i > from; i--) {
                    WeddingProgram tmp = weddingPrograms.get(i);
                    tmp.setPosition(weddingPrograms.get(i - 1)
                            .getPosition());
                }
            }
            target = weddingPrograms.get(from);
            target.setPosition(position);

            resortPrograms();
        }
    };
    private String TAG = WeddingDayProgramsActivity.class.getSimpleName();
    private int selectedChildPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_day_programs);
        setDefaultStatusBarPadding();

        headView = View.inflate(this, R.layout.wedding_day_plan_head, null);
        View footView = View.inflate(this, R.layout.wedding_plan_footer, null);
        tipsView = headView.findViewById(R.id.tip_layout);
        addGroupView = headView.findViewById(R.id.add_group_layout);
        editBtn = (TextView) findViewById(R.id.edit);
        progressBar = findViewById(R.id.progressBar);
        empty = findViewById(R.id.empty);
        addView = findViewById(R.id.add_btn);

        // 头部提示显示与否
        if (getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).getBoolean(
                "wedding_plan_tip_closed",
                false)) {
            tipsView.setVisibility(View.GONE);
        } else {
            tipsView.setVisibility(View.VISIBLE);
        }

        listView = (DSExpandableListView) findViewById(R.id.list);
        listView.addHeaderView(headView);
        listView.addFooterView(footView);
        listView.setOnScrollListener(this);
        addedViewWidth = listView.getmAddedViewWidth();

        weddingPrograms = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        new GetWeddingProgramsTask().executeOnExecutor(Constants.LISTTHEADPOOL);

    }


    private class GetWeddingProgramsTask extends AsyncTask<String, Object, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... params) {
            String url = Constants.getAbsUrl(Constants.HttpPath.WEDDING_PROGRAMS_URL);
            try {
                String resultStr = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(resultStr)) {
                    return null;
                }

                return new JSONObject(resultStr).optJSONArray("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            progressBar.setVisibility(View.GONE);
            if (jsonArray != null) {
                weddingPrograms.clear();
                expandedGroupCount = 0;
                if (jsonArray.length() > 0) {
                    for (int i = 0, size = jsonArray.length(); i < size; i++) {
                        weddingPrograms.add(new WeddingProgram(jsonArray.optJSONObject(i)));
                    }
                    if (!weddingPrograms.isEmpty()) {
                        // 不为空时重新排序
                        Collections.sort(weddingPrograms, new Comparator<WeddingProgram>() {
                            @Override
                            public int compare(WeddingProgram lhs, WeddingProgram rhs) {
                                return lhs.getSortIndex()
                                        .compareTo(rhs.getSortIndex());
                            }
                        });
                    }
                    addView.setVisibility(View.VISIBLE);
                    editBtn.setVisibility(View.VISIBLE);
                    findViewById(R.id.share).setVisibility(View.VISIBLE);
                }

            }
            if (weddingPrograms.isEmpty()) {
                View emptyView = listView.getEmptyView();
                if (emptyView == null) {
                    emptyView = findViewById(R.id.empty_hint_layout);
                    listView.setEmptyView(emptyView);
                    emptyView.findViewById(R.id.btn_empty_hint)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 恢复模板数据
                                    restoreWeddingPrograms();
                                }
                            });
                }
                Util.setEmptyView(WeddingDayProgramsActivity.this,
                        emptyView,
                        R.string.label_restore_programs,
                        R.drawable.icon_empty_task,
                        0,
                        R.string.btn_back_default);
                findViewById(R.id.share).setVisibility(View.GONE);
                editBtn.setVisibility(View.GONE);
                addView.setVisibility(View.GONE);
            }

            dragSortListViewInit();
            empty.setVisibility(View.GONE);
            super.onPostExecute(jsonArray);
        }
    }

    /**
     * 重置默认模板
     */
    private void restoreWeddingPrograms() {
        if (listView.getEmptyView() != null) {
            listView.getEmptyView()
                    .setVisibility(View.GONE);
        }
        progressBar.setVisibility(View.VISIBLE);
        new StatusHttpGetTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                new GetWeddingProgramsTask().executeOnExecutor(Constants.LISTTHEADPOOL);
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                if (listView.getEmptyView() != null) {
                    listView.getEmptyView()
                            .setVisibility(View.VISIBLE);
                }
                Util.postFailToast(WeddingDayProgramsActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_restore_wedding_prog,
                        network);

            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.RESTORE_WEDDING_PROGRAMS_URL));
    }


    /**
     * 提交重新排序
     */
    private void resortPrograms() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray idArray = new JSONArray();
            for (WeddingProgram weddingProgram : weddingPrograms) {
                idArray.put(weddingProgram.getId());
            }
            jsonObject.put("program_ids", idArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new StatusHttpPatchTask(this,
                null).execute(Constants.getAbsUrl(Constants.HttpPath
                        .WEDDING_PROGRAMS_SORT_PATCH_URL),
                jsonObject.toString());
    }

    public class PlanExpandableListAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return weddingPrograms.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return weddingPrograms.get(groupPosition)
                    .getItems()
                    .size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return weddingPrograms.get(groupPosition)
                    .getItems()
                    .get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return weddingPrograms.get(groupPosition)
                    .getId();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return (weddingPrograms.get(groupPosition)
                    .getItems()
                    .get(childPosition)).getId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(
                final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.wedding_plan_item,
                        parent,
                        false);
            }
            GroupHolderView holderView = (GroupHolderView) convertView.getTag();
            if (holderView == null) {
                holderView = new GroupHolderView();
                holderView.itemLeft = convertView.findViewById(R.id.item_left);
                holderView.itemRight = convertView.findViewById(R.id.item_right);
                holderView.itemRightText = (TextView) convertView.findViewById(R.id
                        .item_right_text);
                holderView.title = (TextView) convertView.findViewById(R.id.title);
                holderView.state = (ImageView) convertView.findViewById(R.id.group_state);
                holderView.groupBottomLine = convertView.findViewById(R.id.group_bottom_line);
                holderView.dragView = convertView.findViewById(R.id.drag_view);

                convertView.setTag(holderView);
            }

            holderView.title.setText(weddingPrograms.get(groupPosition)
                    .getTitle());
            holderView.state.setImageResource(isExpanded ? R.drawable.icon_collapse_up : R
                    .drawable.icon_collapse_down);

            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            holderView.itemLeft.setLayoutParams(lp1);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(addedViewWidth,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            holderView.itemRight.setLayoutParams(lp2);

            holderView.itemRightText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteGroup(groupPosition);
                }
            });
            if (isExpanded) {
                holderView.groupBottomLine.setVisibility(View.GONE);
            } else {
                holderView.groupBottomLine.setVisibility(View.VISIBLE);
            }

            holderView.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEditTitle(groupPosition);
                }
            });

            if (isEditMode && expandedGroupCount <= 0) {
                holderView.dragView.setVisibility(View.VISIBLE);
            } else {
                holderView.dragView.setVisibility(View.GONE);
            }

            if (isEditMode) {
                holderView.title.setCompoundDrawablesWithIntrinsicBounds(0,
                        0,
                        R.drawable.icon_edit_round_primary_40_40,
                        0);
                holderView.title.setClickable(true);
            } else {
                holderView.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                holderView.title.setClickable(false);
            }

            return convertView;
        }

        @Override
        public View getChildView(
                final int groupPosition,
                final int childPosition,
                boolean isLastChild,
                View convertView,
                ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.wedding_plan_child_item,
                        parent,
                        false);
            }
            ChildHolderView holderView = (ChildHolderView) convertView.getTag();
            if (holderView == null) {
                holderView = new ChildHolderView();
                holderView.time = (TextView) convertView.findViewById(R.id.plan_time);
                holderView.content = (TextView) convertView.findViewById(R.id.plan_content);
                holderView.crew = (TextView) convertView.findViewById(R.id.plan_crew);
                holderView.lastChildBottomLine = convertView.findViewById(R.id.bottom_line);
                holderView.deleteChildLayout = convertView.findViewById(R.id.delete_child_layout);
                holderView.addChildLayout = convertView.findViewById(R.id.add_child_layout);
                holderView.collapseLayout = convertView.findViewById(R.id.collapse_group_layout);
                holderView.bottomMarginLayout = convertView.findViewById(R.id.bottom_margin_layout);
                holderView.planContentLayout = convertView.findViewById(R.id.plan_content_layout);

                convertView.setTag(holderView);
            }

            if (!weddingPrograms.get(groupPosition)
                    .isEmptyInit()) {
                holderView.planContentLayout.setVisibility(View.VISIBLE);
                WeddingProgram.Item item = weddingPrograms.get(groupPosition)
                        .getItems()
                        .get(childPosition);
                String timeStr = (item.getHour() > 9 ? item.getHour() : "0" + item.getHour()) +
                        "" + " : " + (item.getMinute() > 9 ? item.getMinute() : "0" + item
                        .getMinute());
                holderView.time.setText(timeStr);
                holderView.content.setText(item.getSummary());
                holderView.crew.setText(item.getPartners());
            } else {
                holderView.planContentLayout.setVisibility(View.GONE);
            }

            if (childPosition == weddingPrograms.get(groupPosition)
                    .getItems()
                    .size() - 1) {
                holderView.collapseLayout.setVisibility(View.VISIBLE);
                holderView.collapseLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listView.collapseGroup(groupPosition);
                    }
                });
                holderView.lastChildBottomLine.setVisibility(View.VISIBLE);
                holderView.bottomMarginLayout.setVisibility(View.GONE);
            } else {
                holderView.collapseLayout.setVisibility(View.GONE);
                holderView.lastChildBottomLine.setVisibility(View.GONE);
                holderView.bottomMarginLayout.setVisibility(View.VISIBLE);
            }

            if (weddingPrograms.get(groupPosition)
                    .isEmptyInit()) {
                holderView.addChildLayout.setVisibility(View.VISIBLE);
                holderView.addChildLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAddChildPlan(v, groupPosition);
                    }
                });
            } else {
                holderView.addChildLayout.setVisibility(View.GONE);
            }

            if (isEditMode && !weddingPrograms.get(groupPosition)
                    .isEmptyInit()) {
                holderView.deleteChildLayout.setVisibility(View.VISIBLE);
                holderView.deleteChildLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onDeleteChildPlan(groupPosition, childPosition);
                    }
                });
            } else {
                holderView.deleteChildLayout.setVisibility(View.GONE);
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public class GroupHolderView {
            private View itemLeft;
            private View itemRight;
            private TextView title;
            private ImageView state;
            private TextView itemRightText;
            private View groupBottomLine;
            private View dragView;
        }

        public class ChildHolderView {
            private TextView time;
            private TextView content;
            private TextView crew;
            private View deleteChildLayout;
            private View addChildLayout;
            private View collapseLayout;
            private View planContentLayout;
            private View lastChildBottomLine;
            private View bottomMarginLayout;
        }

    }

    public void dragSortListViewInit() {
        adapter = new PlanExpandableListAdapter();
        listView.setmAddedViewWidth(addedViewWidth);
        listView.setAdapter(adapter);
        listView.setDropListener(onDrop);
        listView.setDividerHeight(0);
        DSExpandableController mController = new DSExpandableController(listView);
        mController.setDragInitMode(DSExpandableController.ON_DRAG);
        mController.setDragHandleId(R.id.drag_view);
        mController.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        listView.setFloatViewManager(mController);
        listView.setOnTouchListener(mController);

        listView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                expandedGroupCount--;
                if (expandedGroupCount <= 0 && isEditMode) {
                    listView.setDragEnabled(true);
                    listView.setSwipeEnabled(true);
                }
            }
        });

        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                expandedGroupCount++;
                listView.setDragEnabled(false);
                listView.setSwipeEnabled(false);

                if (addGroupView.getVisibility() == View.VISIBLE) {
                    addGroupView.setVisibility(View.GONE);
                }
            }
        });

        // 直接监听 group item点击事件,在这里触发扩展和收缩,并且阻断事件
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(
                    ExpandableListView parent, View v, int groupPosition, long id) {
                if (parent.isGroupExpanded(groupPosition)) {
                    parent.collapseGroup(groupPosition);
                } else {
                    parent.expandGroup(groupPosition, true);
                }

                return true;
            }
        });

        listView.setOnSwipeListener(new SwipeExpandableListView.OnSwipeListener() {
            @Override
            public void onSwipe(boolean show) {
                if (show) {
                    listView.setDragEnabled(false);
                } else {
                    listView.setDragEnabled(true);
                }
            }
        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(
                    ExpandableListView parent,
                    View v,
                    int groupPosition,
                    int childPosition,
                    long id) {
                if (isEditMode) {
                    Log.e("Wedding programs", "on child click-------------------------");
                    onEditChildPlan(v, groupPosition, childPosition);
                }
                return false;
            }
        });

        if (isEditMode) {
            listView.setDragEnabled(true);
            listView.setSwipeEnabled(true);
        } else {
            listView.setDragEnabled(false);
            listView.setSwipeEnabled(false);
        }

        if (selectedGroupPosition > 0) {
            listView.expandGroup(selectedGroupPosition);
            int offsetPosition = weddingPrograms.get(selectedGroupPosition)
                    .getItems()
                    .size() - 1;
            if (selectedGroupPosition > 0) {
                offsetPosition = selectedChildPosition;
            }
            listView.smoothScrollToPosition(selectedGroupPosition + offsetPosition);
        }
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 关闭提示
     */
    public void onCloseTip(View view) {
        getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit()
                .putBoolean("wedding_plan_tip_closed", true)
                .commit();
        tipsView.setVisibility(View.GONE);
    }

    /**
     * 分享流程给亲朋好友
     */
    public void onShare(View v) {
        if (weddingPrograms == null || weddingPrograms.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_empty_wedding_programs), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (shareUtil == null) {
            shareUtil = new TextShareUtil(WeddingDayProgramsActivity.this,
                    weddingPrograms.get(0)
                            .getSharePath(),
                    getString(R.string.label_programs_share),
                    getString(R.string.label_programs_share_content),
                    getString(R.string.label_programs_share_content),
                    handler);
        }
        if (shareDialog != null && shareDialog.isShowing()) {
            return;
        }
        if (shareDialog == null) {
            shareDialog = Util.initTextShareDialog(this, null,shareUtil,null);
        }

        shareDialog.show();
    }


    /**
     * 进入编辑模式
     */
    public void onEditMode(View v) {
        DisplayMetrics dm = getResources().getDisplayMetrics();

        if (isEditMode) {
            TrackerUtil.onTCAgentPageEnd(this, "编辑婚礼流程");
            editBtn.setText(getString(R.string.label_edit));
            editBtn.setBackgroundResource(R.drawable.sp_r12_stroke_primary);
            editBtn.setTextColor(getResources().getColor(R.color.primary_white));
            editBtn.setPadding((int) (8 * dm.density), 0, (int) (8 * dm.density), 0);
            listView.setDragEnabled(false);
            listView.setSwipeEnabled(false);
            headView.findViewById(R.id.add_group_item)
                    .setVisibility(View.GONE);
        } else {
            TrackerUtil.onTCAgentPageStart(this, "编辑婚礼流程");
            editBtn.setText(getString(R.string.label_finished));
            editBtn.setBackgroundResource(R.drawable.sl_r15_primary_2_dark);
            editBtn.setPadding((int) (8 * dm.density), 0, (int) (8 * dm.density), 0);
            editBtn.setTextColor(getResources().getColor(R.color.colorWhite));
            listView.setDragEnabled(true);
            listView.setSwipeEnabled(true);
            headView.findViewById(R.id.add_group_item)
                    .setVisibility(View.VISIBLE);

            /**
             * 显示一次编辑提示，只显示一次
             */
            if (!getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).getBoolean(
                    "showed_programs_edit_hint",
                    false)) {
                dialog = DialogUtil.createDoubleButtonDialog(dialog,
                        this,
                        getString(R.string.msg_programs_edit_hint),
                        null,
                        null,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharedPreferences preferences = getSharedPreferences(Constants
                                                .PREF_FILE,
                                        Context.MODE_PRIVATE);
                                preferences.edit()
                                        .putBoolean("showed_programs_edit_hint", true)
                                        .apply();
                                dialog.cancel();
                            }
                        },
                        null);
                dialog.show();
            }
        }
        isEditMode = !isEditMode;

        adapter.notifyDataSetChanged();
    }

    /**
     * 添加子流程
     *
     * @param view
     */
    public void onAddChildPlan(View view, int groupPosition) {
        Intent intent = new Intent(this, AddWeddingProgItemActivity.class);
        String[] wps = new String[weddingPrograms.size()];
        long[] wpids = new long[weddingPrograms.size()];
        for (int i = 0; i < weddingPrograms.size(); i++) {
            wps[i] = weddingPrograms.get(i)
                    .getTitle();
            wpids[i] = weddingPrograms.get(i)
                    .getId();
        }

        intent.putExtra("group_position", groupPosition);
        intent.putExtra("mode", "add");
        intent.putExtra("program_id",
                weddingPrograms.get(groupPosition)
                        .getId());
        intent.putExtra("wedding_program_titles", wps);
        intent.putExtra("wedding_program_ids", wpids);
        startActivityForResult(intent, Constants.RequestCode.ADD_WEDDING_PLAN);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    public void onAddChildProg(View view) {
        Intent intent = new Intent(this, AddWeddingProgItemActivity.class);
        String[] wps = new String[weddingPrograms.size()];
        long[] wpids = new long[weddingPrograms.size()];
        for (int i = 0; i < weddingPrograms.size(); i++) {
            wps[i] = weddingPrograms.get(i)
                    .getTitle();
            wpids[i] = weddingPrograms.get(i)
                    .getId();
        }

        intent.putExtra("mode", "add");
        intent.putExtra("wedding_program_titles", wps);
        intent.putExtra("wedding_program_ids", wpids);
        startActivityForResult(intent, Constants.RequestCode.ADD_WEDDING_PLAN);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    /**
     * 修改子流程
     *
     * @param view
     * @param groupPosition
     * @param childPosition
     */
    public void onEditChildPlan(View view, int groupPosition, int childPosition) {
        WeddingProgram.Item item = weddingPrograms.get(groupPosition)
                .getItems()
                .get(childPosition);
        Intent intent = new Intent(this, AddWeddingProgItemActivity.class);

        String[] wps = new String[weddingPrograms.size()];
        long[] wpids = new long[weddingPrograms.size()];
        for (int i = 0; i < weddingPrograms.size(); i++) {
            wps[i] = weddingPrograms.get(i)
                    .getTitle();
            wpids[i] = weddingPrograms.get(i)
                    .getId();
        }

        intent.putExtra("wedding_program_titles", wps);
        intent.putExtra("wedding_program_ids", wpids);
        intent.putExtra("group_position", groupPosition);
        intent.putExtra("child_position", childPosition);
        intent.putExtra("program_id",
                weddingPrograms.get(groupPosition)
                        .getId());
        intent.putExtra("item_id",
                weddingPrograms.get(groupPosition)
                        .getItems()
                        .get(childPosition)
                        .getId());
        intent.putExtra("mode", "edit");
        intent.putExtra("hour", item.getHour());
        intent.putExtra("minute", item.getMinute());
        intent.putExtra("summary", item.getSummary());
        intent.putExtra("partners", item.getPartners());
        startActivityForResult(intent, Constants.RequestCode.ADD_WEDDING_PLAN);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    public void onDeleteChildPlan(final int groupPosition, final int childPosition) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }

        dialog = DialogUtil.createDoubleButtonDialog(dialog,
                this,
                getString(R.string.label_detele_child_plan),
                getString(R.string.label_delete),
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteProgramsItem(groupPosition, childPosition);
                    }
                },
                null);
        dialog.show();
    }

    /**
     * 提交删除子项目
     */
    private void deleteProgramsItem(final int groupPosition, final int childPosition) {
        progressBar.setVisibility(View.VISIBLE);
        new StatusHttpDeleteTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                progressBar.setVisibility(View.GONE);
                WeddingProgram weddingProgram = weddingPrograms.get(groupPosition);
                weddingProgram.removeItem(childPosition);
                weddingPrograms.set(groupPosition, weddingProgram);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                Util.postFailToast(WeddingDayProgramsActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_delete_prog_item,
                        network);
            }
        }).execute(Constants.getAbsUrl(TextUtils.concat(Constants.HttpPath.WEDDING_PROGRAM_ITEM_URL,
                "?id=" + weddingPrograms.get(groupPosition)
                        .getItems()
                        .get(childPosition)
                        .getId())
                .toString()));
    }

    /**
     * 删除组项目
     */
    public void onDeleteGroup(final int groupPosition) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }

        dialog = DialogUtil.createDoubleButtonDialog(dialog,
                this,
                getString(R.string.label_detele_plan_group),
                getString(R.string.label_delete),
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listView.hiddenItemAddedView();
                        deleteWeddingProgram(groupPosition);
                    }
                },
                null);
        dialog.show();
    }

    /**
     * 提交流程组删除事件
     */
    private void deleteWeddingProgram(final int groupPosition) {
        progressBar.setVisibility(View.VISIBLE);
        new StatusHttpDeleteTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                progressBar.setVisibility(View.GONE);
                weddingPrograms.remove(groupPosition);
                adapter.notifyDataSetChanged();

                if (weddingPrograms.isEmpty()) {
                    View emptyView = listView.getEmptyView();

                    if (emptyView == null) {
                        emptyView = findViewById(R.id.empty_hint_layout);
                        listView.setEmptyView(emptyView);
                        emptyView.findViewById(R.id.btn_empty_hint)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // 恢复模板数据
                                        restoreWeddingPrograms();
                                    }
                                });
                    }
                    Util.setEmptyView(WeddingDayProgramsActivity.this,
                            emptyView,
                            R.string.label_restore_programs,
                            R.drawable.icon_empty_task,
                            0,
                            R.string.btn_back_default);
                    findViewById(R.id.share).setVisibility(View.GONE);
                    editBtn.setVisibility(View.GONE);
                    addView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                Util.postFailToast(WeddingDayProgramsActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_delete_group,
                        network);

            }
        }).execute(Constants.getAbsUrl(TextUtils.concat(Constants.HttpPath.WEDDING_PROGRAMS_URL,
                "?id=" + weddingPrograms.get(groupPosition)
                        .getId())
                .toString()));
    }

    /**
     * 弹出框编辑group title
     */
    public void onEditTitle(final int groupPosition) {
        if (editDialog != null && editDialog.isShowing()) {
            return;
        }

        final WeddingProgram weddingProgram = weddingPrograms.get(groupPosition);

        editDialog = new Dialog(this, R.style.BubbleDialogTheme);
        final View dialogView = View.inflate(this, R.layout.dialog_programs_title_edit, null);

        final EditText newTitle = (EditText) dialogView.findViewById(R.id.title);
        newTitle.setText(weddingProgram.getTitle());
        ImageButton confirm = (ImageButton) dialogView.findViewById(R.id.confirm_btn);
        ImageButton cancel = (ImageButton) dialogView.findViewById(R.id.cancel_btn);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = newTitle.getText()
                        .toString();
                if (JSONUtil.isEmpty(title)) {
                    Util.showToast(getString(R.string.msg_empty_plan_title),
                            WeddingDayProgramsActivity.this);
                } else if (title.length() > TITLE_LIMIT) {
                    Util.showToast(getString(R.string.msg_over_title_count, TITLE_LIMIT),
                            WeddingDayProgramsActivity.this);
                } else {
                    patchProgramTitle(groupPosition, title);

                    editDialog.cancel();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog.cancel();
            }
        });

        editDialog.setContentView(dialogView);
        Point point = JSONUtil.getDeviceSize(this);
        Window win = editDialog.getWindow();
        win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        win.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = win.getAttributes();
        params.width = Math.round(point.x);
        win.setAttributes(params);
        win.setWindowAnimations(R.style.dialog_anim_rise_style);
        editDialog.show();
    }

    /**
     * 提交组项目标题修改
     */
    private void patchProgramTitle(final int groupPosition, final String newTitle) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",
                    weddingPrograms.get(groupPosition)
                            .getId());
            JSONObject programObject = new JSONObject();
            programObject.put("title", newTitle);
            programObject.put("sort_no",
                    weddingPrograms.get(groupPosition)
                            .getSortIndex());
            jsonObject.put("program", programObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(this);
        }
        progressDialog.show();
        new StatusHttpPatchTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                WeddingProgram weddingProgram = weddingPrograms.get(groupPosition);
                weddingProgram.setTitle(newTitle);
                weddingPrograms.set(groupPosition, weddingProgram);
                adapter.notifyDataSetChanged();
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Util.postFailToast(WeddingDayProgramsActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_update_program,
                        network);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.WEDDING_PROGRAMS_URL),
                jsonObject.toString());

    }


    /**
     * 添加新的组项目
     *
     * @param view
     */
    public void addWeddingProgram(View view) {
        if (editDialog != null && editDialog.isShowing()) {
            return;
        }

        editDialog = new Dialog(this, R.style.BubbleDialogTheme);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_programs_title_edit,
                null);

        final EditText newTitle = (EditText) dialogView.findViewById(R.id.title);
        ImageButton confirm = (ImageButton) dialogView.findViewById(R.id.confirm_btn);
        ImageButton cancel = (ImageButton) dialogView.findViewById(R.id.cancel_btn);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = newTitle.getText()
                        .toString();
                if (JSONUtil.isEmpty(title)) {
                    Toast.makeText(WeddingDayProgramsActivity.this,
                            getString(R.string.msg_empty_plan_title),
                            Toast.LENGTH_SHORT)
                            .show();
                } else if (title.length() > TITLE_LIMIT) {
                    Util.showToast(getString(R.string.msg_over_title_count, TITLE_LIMIT),
                            WeddingDayProgramsActivity.this);
                } else {
                    onPostWeddingProgram(title);

                    editDialog.cancel();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog.cancel();
            }
        });

        editDialog.setContentView(dialogView);
        Point point = JSONUtil.getDeviceSize(this);
        Window win = editDialog.getWindow();
        win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        win.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = win.getAttributes();
        params.width = Math.round(point.x);
        win.setAttributes(params);
        win.setWindowAnimations(R.style.dialog_anim_rise_style);
        editDialog.show();
    }

    /**
     * 提交新建主流程组项目
     */
    public void onPostWeddingProgram(final String programTitle) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject programObject = new JSONObject();
            programObject.put("title", programTitle);
            programObject.put("sort_no", 0);
            jsonObject.put("program", programObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(this);
        }
        progressDialog.show();
        new StatusHttpPostTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                progressBar.setVisibility(View.VISIBLE);
                new GetWeddingProgramsTask().executeOnExecutor(Constants.LISTTHEADPOOL);
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Util.postFailToast(WeddingDayProgramsActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_create_program,
                        network);

            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.WEDDING_PROGRAMS_URL),
                jsonObject.toString());
    }

    /**
     * 滚动到列表底部
     */
    private void scrollToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(adapter.getGroupCount() - 1);
            }
        });
    }

    /**
     * 将所有的展开项都收起来
     */
    private void collapseAll() {
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            listView.collapseGroup(i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case HljShare.RequestCode.SHARE_TO_TXWEIBO:
                    trackerShare("TXWeibo");
                    break;
                case HljShare.RequestCode.SHARE_TO_WEIBO:
                    trackerShare("Weibo");
                    break;
                case Constants.RequestCode.ADD_WEDDING_PLAN:
                    if (data.getStringExtra("mode")
                            .equals("add") || data.getStringExtra("mode")
                            .equals("edit")) {
                        selectedGroupPosition = data.getIntExtra("group_position", -1);
                        selectedChildPosition = data.getIntExtra("child_position", -1);
                        progressBar.setVisibility(View.VISIBLE);
                        new GetWeddingProgramsTask().executeOnExecutor(Constants.LISTTHEADPOOL);
                    }
                    break;
                default:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isHide;
    private boolean animStart;

    private void showSendAnimation() {
        isHide = false;
        if (!animStart) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right2);
            animation.setFillBefore(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    animStart = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animStart = false;
                    if (isHide) {
                        hideSendAnimation();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            addView.startAnimation(animation);
        }
    }


    private void hideSendAnimation() {
        isHide = true;
        if (!animStart) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_right2);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    animStart = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animStart = false;
                    if (!isHide) {
                        showSendAnimation();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            addView.startAnimation(animation);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                showSendAnimation();
                break;
            default:
                hideSendAnimation();
                break;
        }
    }

    @Override
    public void onScroll(
            AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    private void trackerShare(String shareInfo) {
        new HljTracker.Builder(this).eventableType("ToolDayProcess")
                .action("share")
                .additional(shareInfo)
                .build()
                .send();
    }
}
