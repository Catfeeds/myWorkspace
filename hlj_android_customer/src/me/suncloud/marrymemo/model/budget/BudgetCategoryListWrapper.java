package me.suncloud.marrymemo.model.budget;

/**
 * Created by jinxin on 2017/11/22 0022.
 */

public class BudgetCategoryListWrapper {

    long pid;//parent对应的id
    int collectPosition;
    BudgetCategory budgetCategory;
    boolean showHeader;

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public int getCollectPosition() {
        return collectPosition;
    }

    public void setCollectPosition(int collectPosition) {
        this.collectPosition = collectPosition;
    }

    public BudgetCategory getBudgetCategory() {
        return budgetCategory;
    }

    public void setBudgetCategory(BudgetCategory budgetCategory) {
        this.budgetCategory = budgetCategory;
    }

    public boolean isShowHeader() {
        return showHeader;
    }

    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
    }
}
