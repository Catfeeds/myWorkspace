package me.suncloud.marrymemo.adpter.budget;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Work;

import org.json.JSONObject;

import java.util.List;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.budget.viewholder.BudgetFooterViewHolder;
import me.suncloud.marrymemo.adpter.budget.viewholder.BudgetHeaderViewHolder;
import me.suncloud.marrymemo.adpter.budget.viewholder.BudgetItemViewHolder;
import me.suncloud.marrymemo.model.budget.BudgetCategory;
import me.suncloud.marrymemo.model.budget.BudgetCategoryListWrapper;
import me.suncloud.marrymemo.task.HttpGetTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;


/**
 * Created by jinxin on 2017/11/21 0021.
 */

public class BudgetAdapter extends RecyclerView.Adapter<BaseViewHolder> implements
        BudgetHeaderViewHolder.OnBudgetHeaderListener, BudgetItemViewHolder
        .onBudgetItemViewListener {

    final int ITEM_HEADER = 11;
    final int ITEM_CATEGORY = 12;
    final int ITEM_FOOTER = 13;

    private Context mContext;
    private LayoutInflater inflater;
    private LongSparseArray<List<BudgetCategory>> groupCategoryList;
    private List<BudgetCategoryListWrapper> selectCategoryList;
    private List<BudgetCategory> allCategoryList;
    private List<Work> cpmWorkList;
    private double figure;
    private onBudgetAdapterListener onBudgetAdapterListener;
    private BudgetHeaderViewHolder headerViewHolder;
    private Handler handler;
    private RateRunnable rateRunnable;

    public BudgetAdapter(
            Context mContext,
            LongSparseArray<List<BudgetCategory>> groupCategoryList,
            List<BudgetCategoryListWrapper> selectCategoryList,
            List<BudgetCategory> allCategoryList,
            List<Work> cpmWorkList) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(this.mContext);
        this.groupCategoryList = groupCategoryList;
        this.selectCategoryList = selectCategoryList;
        this.allCategoryList = allCategoryList;
        this.cpmWorkList = cpmWorkList;
        rateRunnable = new RateRunnable();
        handler = new Handler();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case ITEM_HEADER:
                itemView = inflater.inflate(R.layout.budget_header_chart, parent, false);
                headerViewHolder = new BudgetHeaderViewHolder(itemView);
                headerViewHolder.setOnBudgetHeaderListener(this);
                return headerViewHolder;
            case ITEM_FOOTER:
                itemView = inflater.inflate(R.layout.budget_list_footer, parent, false);
                BudgetFooterViewHolder footerViewHolder = new BudgetFooterViewHolder(itemView);
                return footerViewHolder;
            case ITEM_CATEGORY:
                itemView = inflater.inflate(R.layout.budget_list_item, parent, false);
                BudgetItemViewHolder itemViewHolder = new BudgetItemViewHolder(itemView);
                itemViewHolder.setOnBudgetItemViewListener(this);
                return itemViewHolder;
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof BudgetHeaderViewHolder) {
            ((BudgetHeaderViewHolder) holder).setHeaderData(groupCategoryList,
                    allCategoryList,
                    figure);
            postRunnable();
        } else if (holder instanceof BudgetItemViewHolder) {
            int budgetPosition = position - 1;
            BudgetCategoryListWrapper wrapper = selectCategoryList.get(budgetPosition);
            ((BudgetItemViewHolder) holder).setBudgetItem(wrapper,
                    allCategoryList,
                    cpmWorkList,
                    budgetPosition);
        }
    }

    public void notifyCpm() {
        int cpmPosition = -1;
        for (int i = 0, size = selectCategoryList.size(); i < size; i++) {
            BudgetCategory cpmCategory = selectCategoryList.get(i)
                    .getBudgetCategory();
            if (cpmCategory.getTitle()
                    .equals(BudgetCategory.CPM_TITLE)) {
                cpmPosition = i;
                break;
            }
        }
        //加上header的位置
        cpmPosition++;
        notifyItemChanged(cpmPosition);
    }

    public void setOnBudgetAdapterListener(
            BudgetAdapter.onBudgetAdapterListener onBudgetAdapterListener) {
        this.onBudgetAdapterListener = onBudgetAdapterListener;
    }

    public void setFigure(double figure) {
        this.figure = figure;
    }

    @Override
    public int getItemCount() {
        int itemCount = selectCategoryList.size() + 1 + (figure <= 0 ? 0 : 1);
        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_HEADER;
        } else if (position == getItemCount() - 1 && figure > 0) {
            return ITEM_FOOTER;
        } else {
            return ITEM_CATEGORY;
        }
    }

    @Override
    public void modifyCategory() {
        if (onBudgetAdapterListener != null) {
            onBudgetAdapterListener.onModifyCategory();
        }
    }

    @Override
    public void revertBudget() {
        if (onBudgetAdapterListener != null) {
            onBudgetAdapterListener.onRevertBudget();
        }
    }

    @Override
    public void onEditTextChanged(BudgetCategory category, double oldMoney, double categoryMoney) {
        if (category != null && category.getTitle()
                .equals(BudgetCategory.CPM_TITLE) && oldMoney != categoryMoney) {
            if (onBudgetAdapterListener != null) {
                onBudgetAdapterListener.onLoadCpm(categoryMoney);
            }
        }
        calculateFigure();
        if (headerViewHolder != null) {
            headerViewHolder.setHeaderData(groupCategoryList, allCategoryList, figure);
        }
        postRunnable();
    }

    public void calculateFigure() {
        figure = 0D;
        for (BudgetCategoryListWrapper wrapper : selectCategoryList) {
            BudgetCategory category = wrapper.getBudgetCategory();
            if (category != null) {
                figure += category.getMoney();
            }
        }

        if (onBudgetAdapterListener != null) {
            onBudgetAdapterListener.onCalculateFigure(figure);
        }
    }

    private void postRunnable() {
        handler.removeCallbacks(rateRunnable);
        handler.postDelayed(rateRunnable, 500);
    }

    private String getRankUrl() {
        return Constants.getAbsUrl(Constants.HttpPath.BUDGET_RANK, figure);
    }


    class RateRunnable implements Runnable {

        @Override
        public void run() {
            new HttpGetTask(new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    if (obj != null) {
                        JSONObject json = (JSONObject) obj;
                        JSONObject data = json.optJSONObject("data");
                        int rate = 0;
                        if (data != null) {
                            rate = data.optInt("level", 0);
                        }
                        if (headerViewHolder != null) {
                            headerViewHolder.setRate(rate);
                        }
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {
                }
            }).execute(getRankUrl());
        }
    }

    public interface onBudgetAdapterListener {
        void onModifyCategory();

        void onRevertBudget();

        void onLoadCpm(double money);

        void onCalculateFigure(double figure);
    }

}
