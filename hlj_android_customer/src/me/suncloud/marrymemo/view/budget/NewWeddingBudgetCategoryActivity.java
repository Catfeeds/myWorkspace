package me.suncloud.marrymemo.view.budget;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.budget.BudgetApi;
import me.suncloud.marrymemo.model.budget.BudgetCategory;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.NewBudgetUtil;
import rx.Observable;

/**
 * 结婚预算类别
 * Created by jinxin on 2016/5/26.
 */
public class NewWeddingBudgetCategoryActivity extends HljBaseActivity implements
        ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener {

    public static final String ARG_FIGURE = "arg_figure";
    public static final String ARG_SELECTED_CATEGORY = "arg_selected_category";

    @BindView(R.id.btn_ok)
    TextView btnOk;
    @BindView(R.id.btn_layout)
    LinearLayout btnLayout;
    @BindView(R.id.bottom_layout)
    RelativeLayout bottomLayout;
    @BindView(R.id.list)
    ExpandableListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;

    private CategoryAdapter adapter;
    private LongSparseArray<List<BudgetCategory>> data;
    private View headerView;
    private double figure;
    private ArrayList<BudgetCategory> selects;
    private boolean isChange;
    private Dialog dialog;
    private Dialog moneyDialog;
    private List<BudgetCategory> allCategoryList;
    private HljHttpSubscriber loadSub;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_category);
        ButterKnife.bind(this);

        initConstant();
        initWidget();
        initLoad();
    }

    private void initConstant() {
        if (getIntent() != null) {
            figure = getIntent().getDoubleExtra(ARG_FIGURE, figure);
            selects = getIntent().getParcelableArrayListExtra(ARG_SELECTED_CATEGORY);
        }
        allCategoryList = new ArrayList<>();
        data = new LongSparseArray<>();
        adapter = new CategoryAdapter();
    }

    private void initWidget() {
        headerView = getLayoutInflater().inflate(R.layout.budget_category_header, null);
        headerView.findViewById(R.id.hint_layout)
                .setVisibility(View.GONE);
        bottomLayout.findViewById(R.id.btn_layout)
                .setVisibility(View.GONE);
        listView.addHeaderView(headerView);
        listView.setAdapter(adapter);
        listView.setOnGroupClickListener(this);
        listView.setOnChildClickListener(this);
    }

    private void initLoad() {
        Observable<JsonElement> budgetCategoryObb = BudgetApi.getBudgetCategory();
        loadSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                    @Override
                    public void onNext(JsonElement jsonElement) {
                        setBudgetCategory(jsonElement);
                        configList();
                    }
                })
                .setProgressBar(progressBar)
                .setEmptyView(emptyView)
                .build();
        budgetCategoryObb.subscribe(loadSub);
    }

    private void setBudgetCategory(JsonElement categoryJson) {
        allCategoryList.clear();
        if (categoryJson != null) {
            JsonArray list = categoryJson.getAsJsonObject()
                    .get("list")
                    .getAsJsonArray();
            if (list != null) {
                try {
                    for (int i = 0, size = list.size(); i < size; i++) {
                        JSONObject item = new JSONObject(list.get(i)
                                .toString());
                        BudgetCategory category = new BudgetCategory(item);
                        if (category.getPid() > 0) {
                            category.setChecked(selects == null);
                        }
                        NewBudgetUtil.getInstance()
                                .configBudCategory(this, category);
                        allCategoryList.add(category);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isChange) {
            if (dialog == null) {
                dialog = DialogUtil.createDoubleButtonDialog(dialog,
                        this,
                        getString(R.string.label_category_back),
                        getString(R.string.action_ok),
                        getString(R.string.action_cancel),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                NewWeddingBudgetCategoryActivity.super.onBackPressed();
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
            }
            dialog.show();
        } else {
            super.onBackPressed();
        }
    }

    private void configList() {
        if (selects != null) {
            for (BudgetCategory sb : selects) {
                for (BudgetCategory b : allCategoryList) {
                    if (sb.getId() == b.getId()) {
                        b.setChecked(true);
                        break;
                    }
                }
            }
        }

        for (BudgetCategory budgetCategory : allCategoryList) {
            long pid = budgetCategory.getPid();
            if(pid == 0){
                continue;
            }
            List<BudgetCategory> value = data.get(pid);
            if (value == null) {
                value = new ArrayList<>();
                data.put(pid, value);
            }
            value.add(budgetCategory);
        }

        headerView.findViewById(R.id.hint_layout)
                .
                        setVisibility(View.VISIBLE);
        bottomLayout.findViewById(R.id.btn_layout)
                .
                        setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            listView.expandGroup(i);
        }
    }


    public ArrayList<BudgetCategory> getSelect() {
        ArrayList<BudgetCategory> selects = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            List<BudgetCategory> values = data.valueAt(i);
            for (BudgetCategory budgetCategory : values) {
                if (budgetCategory != null && budgetCategory.isChecked()) {
                    selects.add(budgetCategory);
                }
            }
        }
        return selects;
    }

    @OnClick(R.id.btn_ok)
    void onBtnOk() {
        ArrayList<BudgetCategory> localSelects = getSelect();
        if (localSelects.size() < 2) {
            Toast.makeText(this, getString(R.string.label_budget_money_hint), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (figure < NewBudgetUtil.getInstance()
                .getMinMoney(localSelects) * NewBudgetUtil.MONEY_UNIT) {
            showMoneyDialog(localSelects);
        } else {
            Intent data =new Intent();
            data.putParcelableArrayListExtra(NewWeddingBudgetActivity.ARG_SELECTED_CATEGORY,
                    localSelects);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void showMoneyDialog(final ArrayList<BudgetCategory> localSelects) {
        if (moneyDialog != null && moneyDialog.isShowing()) {
            return;
        }
        if (moneyDialog == null) {
            moneyDialog = DialogUtil.createDoubleButtonDialog(moneyDialog,
                    this,
                    getString(R.string.label_budget_figure_hint),
                    getString(R.string.action_ok),
                    getString(R.string.action_cancel),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            moneyDialog.dismiss();
                            Intent intent = new Intent(NewWeddingBudgetCategoryActivity
                                    .this, NewWeddingBudgetFigureActivity.class);
                            intent.putExtra(NewWeddingBudgetFigureActivity.ARG_FROM,
                                    NewWeddingBudgetCategoryActivity.class.getSimpleName());
                            intent.putParcelableArrayListExtra(NewWeddingBudgetFigureActivity
                                            .ARG_SELECT_LIST, localSelects);
                            startActivity(intent);
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            moneyDialog.dismiss();
                        }
                    });
        }
        moneyDialog.show();
    }

    @Override
    public boolean onChildClick(
            ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        isChange = true;
        long key = data.keyAt(groupPosition);
        List<BudgetCategory> categories = data.get(key);
        if (categories != null) {
            BudgetCategory budgetCategory = categories.get(childPosition);
            if (budgetCategory != null) {
                budgetCategory.toggleChecked();
            }
        }
        if (getSelect().size() >= 2) {
            btnOk.setEnabled(true);
            bottomLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        } else {
            btnOk.setEnabled(false);
            bottomLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray));
        }
        adapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public boolean onGroupClick(
            ExpandableListView parent, View v, int groupPosition, long id) {
        return false;
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(loadSub);
    }

    class CategoryAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return data.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return data.valueAt(groupPosition)
                    .size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            long key = data.keyAt(groupPosition);
            BudgetCategory category = NewBudgetUtil.getInstance()
                    .getCategoryById(allCategoryList, key);
            return category;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            List<BudgetCategory> categories = data.valueAt(groupPosition);
            if (categories != null) {
                return categories.get(childPosition);
            }
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            List<BudgetCategory> categories = data.valueAt(groupPosition);
            if (categories != null) {
                return categories.get(childPosition)
                        .getId();
            }
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(
                final int groupPosition,
                final boolean isExpanded,
                View convertView,
                final ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.task_template_item, null);
            }
            GroupHolderView holder = (GroupHolderView) convertView.getTag();
            if (holder == null) {
                holder = new GroupHolderView();
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.check);
                holder.title = (TextView) convertView.findViewById(R.id.tv_task_title);
                holder.state = (ImageView) convertView.findViewById(R.id.group_state);
                convertView.setTag(holder);
            }
            long key = data.keyAt(groupPosition);
            BudgetCategory category = NewBudgetUtil.getInstance()
                    .getCategoryById(allCategoryList, key);
            List<BudgetCategory> items = data.get(key);
            if (category != null) {
                holder.title.setText(category.getTitle());
            }
            holder.state.setImageResource(isExpanded ? R.mipmap.icon_arrow_up_gray_26_14 : R
                    .mipmap.icon_arrow_right_gray_14_26);
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(category.isChecked() || isChecked(items));
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener
                    () {
                @Override
                public void onCheckedChanged(
                        CompoundButton buttonView, boolean isChecked) {
                    long key = data.keyAt(groupPosition);
                    isChange = true;
                    List<BudgetCategory> categories = data.get(key);
                    for (BudgetCategory c : categories) {
                        c.setChecked(isChecked);
                    }
                    if (getSelect().size() >= 2) {
                        btnOk.setEnabled(true);
                        bottomLayout.setBackgroundColor(getResources().getColor(R.color
                                .colorPrimary));
                    } else {
                        btnOk.setEnabled(false);
                        bottomLayout.setBackgroundColor(getResources().getColor(R.color.colorGray));
                    }
                    adapter.notifyDataSetChanged();
                    if (!isExpanded) {
                        listView.expandGroup(groupPosition);
                    }
                }
            });
            return convertView;
        }

        @Override
        public View getChildView(
                int groupPosition,
                int childPosition,
                boolean isLastChild,
                View convertView,
                ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.task_template_child_item, null);
            }
            ChildHolderView holder = (ChildHolderView) convertView.getTag();
            if (holder == null) {
                holder = new ChildHolderView();
                holder.checkedTextView = (CheckedTextView) convertView.findViewById(R.id.check);
                holder.title = (TextView) convertView.findViewById(R.id.tv_task_title);

                convertView.setTag(holder);
            }
            List<BudgetCategory> categories = data.valueAt(groupPosition);
            if (categories != null && childPosition < categories.size()) {
                BudgetCategory category = categories.get(childPosition);
                holder.title.setText(category.getTitle());
                holder.checkedTextView.setChecked(categories.get(childPosition)
                        .isChecked());
            }
            return convertView;
        }

        private boolean isChecked(List<BudgetCategory> categories) {
            if (categories != null) {
                for (BudgetCategory category : categories) {
                    if (category.isChecked()) {
                        return true;
                    }
                }
            }
            return false;
        }


        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private class GroupHolderView {
            private CheckBox checkBox;
            private TextView title;
            private ImageView state;
        }

        private class ChildHolderView {
            private CheckedTextView checkedTextView;
            private TextView title;
        }
    }
}
