package me.suncloud.marrymemo.adpter.budget.viewholder;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.budget.BudgetCategory;
import me.suncloud.marrymemo.task.HttpGetTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.NewBudgetUtil;
import me.suncloud.marrymemo.widget.WeddingBudgetPieChart;

/**
 * Created by jinxin on 2017/11/21 0021.
 */

public class BudgetHeaderViewHolder extends BaseViewHolder {

    @BindView(R.id.budget_all)
    TextView budgetAll;
    @BindView(R.id.rate)
    TextView tvRate;
    @BindView(R.id.rate_layout)
    LinearLayout rateLayout;
    @BindView(R.id.header_all_layout)
    RelativeLayout headerAllLayout;
    @BindView(R.id.pie_chart)
    WeddingBudgetPieChart pieChart;
    @BindView(R.id.all_layout)
    LinearLayout allLayout;
    @BindView(R.id.budget_money)
    TextView budgetMoney;
    @BindView(R.id.budget_money1)
    TextView budgetMoney1;
    @BindView(R.id.money_layout)
    LinearLayout moneyLayout;
    @BindView(R.id.load_layout)
    LinearLayout loadLayout;
    @BindView(R.id.center_layout)
    LinearLayout centerLayout;
    @BindView(R.id.modify_category)
    TextView modifyCategory;
    @BindView(R.id.revert_budget)
    TextView revertBudget;

    private OnBudgetHeaderListener onBudgetHeaderListener;
    private Context mContext;

    public BudgetHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        centerLayout.setVisibility(View.GONE);
        initPieChart();
    }

    @Override
    protected void setViewData(
            Context mContext, Object item, int position, int viewType) {

    }

    private void initPieChart() {
        pieChart.setDescription(null);
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(false);
        pieChart.setExtraOffsets(5, 20, 5, 16);
        pieChart.setOutOffset(35);
        pieChart.setHoleRadius(56f);
        pieChart.setTransparentCircleRadius(62f);
        pieChart.setDrawCenterText(true);
        Legend legend = pieChart.getLegend();
        if (legend != null) {
            legend.setEnabled(false);
        }
    }

    public void setHeaderData(
            LongSparseArray<List<BudgetCategory>> groupCategoryList,
            List<BudgetCategory> allCategoryList,
            double figure) {
        if (groupCategoryList == null) {
            return;
        }

        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < groupCategoryList.size(); i++) {
            long key = groupCategoryList.keyAt(i);
            String money = NewBudgetUtil.getInstance()
                    .getComputeMoney(groupCategoryList.get(key));
            xVals.add(mContext.getString(R.string.rmb) + money);
        }

        ArrayList<Entry> yVals1 = new ArrayList<>();
        for (int i = 0; i < groupCategoryList.size(); i++) {
            long key = groupCategoryList.keyAt(i);
            BudgetCategory category = NewBudgetUtil.getInstance()
                    .getCategoryById(allCategoryList, key);
            String selectMoney = NewBudgetUtil.getInstance()
                    .getComputeMoney(groupCategoryList.get(key));
            if (category == null || TextUtils.isEmpty(selectMoney)) {
                continue;
            }
            String title = category.getTitle();
            double selectMoneyD = Double.parseDouble(selectMoney);
            float rate = (float) (selectMoneyD / figure);
            yVals1.add(new Entry(rate, i, title.substring(0, 2)));
        }

        budgetAll.setText(" " + String.valueOf(Double.valueOf(figure).intValue())+ " ");
        PieDataSet dataSet = new PieDataSet(yVals1, "Election Results");
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setDrawValues(false);
        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(0f);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#ff7866"));
        colors.add(Color.parseColor("#ff985c"));
        colors.add(Color.parseColor("#ffba66"));
        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();

    }

    public void setRate(int rate){
        if(rate <=0){
            rateLayout.setVisibility(View.GONE);
        }else {
            rateLayout.setVisibility(View.VISIBLE);
            tvRate.setText(" "+ String.valueOf(rate));
        }
    }

    public void setOnBudgetHeaderListener(OnBudgetHeaderListener onBudgetHeaderListener) {
        this.onBudgetHeaderListener = onBudgetHeaderListener;
    }

    @OnClick(R.id.modify_category)
    void modifyCategory() {
        if (onBudgetHeaderListener != null) {
            onBudgetHeaderListener.modifyCategory();
        }
    }

    @OnClick(R.id.revert_budget)
    void revertBudget() {
        if (onBudgetHeaderListener != null) {
            onBudgetHeaderListener.revertBudget();
        }
    }


    public interface OnBudgetHeaderListener {
        void modifyCategory();

        void revertBudget();
    }
}
