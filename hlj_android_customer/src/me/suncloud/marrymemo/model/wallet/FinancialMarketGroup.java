package me.suncloud.marrymemo.model.wallet;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by luohanlin on 2017/9/4.
 * 金融超市分组数据
 */

public class FinancialMarketGroup {
    int group;
    @SerializedName("financial_product")
    ArrayList<FinancialProduct> financialProduct;

    public int getGroup() {
        return group;
    }

    public ArrayList<FinancialProduct> getFinancialProduct() {
        return financialProduct;
    }
}
