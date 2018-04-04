package me.suncloud.marrymemo.util;

import android.content.Context;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.PointD;
import me.suncloud.marrymemo.model.budget.BudgetCategory;

/**
 * Created by jinxin on 2017/11/21 0021.
 */

public class NewBudgetUtil {
    public static final int MONEY_UNIT = 10000;
    public static final int METHOD_COUNT = 4;//四种算法
    private JSONArray budgetConfig;

    private NewBudgetUtil() {

    }

    public static NewBudgetUtil getInstance() {
        return NewBudgetUtilHolder.INSTANCE;
    }

    public BudgetCategory getCategoryByTitle(List<BudgetCategory> allData, String title) {
        for (BudgetCategory category : allData) {
            if (category.getTitle()
                    .equals(title)) {
                return category;
            }
        }

        return null;
    }

    public JSONArray getBudgetConfig(Context mContext) {
        if (budgetConfig == null && mContext != null) {
            try {
                String budget = CommonUtil.readStreamToString(mContext.getResources()
                        .openRawResource(R.raw.budget));
                JSONObject object = new JSONObject(budget);
                budgetConfig = object.getJSONArray("budgets");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return budgetConfig;
    }

    public BudgetCategory configBudCategory(Context mContext, BudgetCategory category) {
        if (category != null) {
            if (budgetConfig == null) {
                getBudgetConfig(mContext);
            }
            for (int j = 0; j < budgetConfig.length(); j++) {
                JSONObject config = budgetConfig.optJSONObject(j);
                if (config != null) {
                    String name = config.optString("name");
                    if (category.getTitle() != null && category.getTitle()
                            .equals(name)) {
                        JSONArray rates = config.optJSONArray("rates");
                        JSONArray range = config.optJSONArray("moneyRange");
                        for (int z = 0; z < 4; z++) {
                            JSONObject point = range.optJSONObject(z);
                            double min = point.optDouble("min");
                            double max = point.optDouble("max") == 0 ? Integer.MAX_VALUE : point
                                    .optDouble(
                                    "max");
                            category.setRates(z,
                                    rates.optJSONObject(z)
                                            .optDouble("rate", 0));
                            category.setMoneyRange(z, new PointD(min, max));
                        }
                    }
                }
            }
        }
        return category;
    }

    public String getComputeMoney(List<BudgetCategory> categoryList) {
        if (categoryList == null) {
            return null;
        }
        double money = 0d;
        for (BudgetCategory category : categoryList) {
            money += category.getMoney();
        }
        return CommonUtil.formatDouble2String(money);
    }

    public BudgetCategory getCategoryById(List<BudgetCategory> allCategoryList, long id) {
        if (allCategoryList == null) {
            return null;
        }
        for (BudgetCategory bc : allCategoryList) {
            if (bc.getId() == id) {
                return bc;
            }
        }
        return null;
    }

    public double getMinMoney(List<BudgetCategory> selects) {
        double min = 0.0d;
        if (selects == null) {
            return min;
        }
        for (BudgetCategory category : selects) {
            ArrayList<PointD> moneyRange = category.getMoneyRange();
            if (category.getPid() > 0 && !CommonUtil.isCollectionEmpty(moneyRange)) {
                PointD minPoint = moneyRange.get(0);
                min += minPoint.getX();
            }
        }
        return min;
    }

    /**
     * 获得分配算法
     *
     * @return
     */
    public int getKind(double figure, List<BudgetCategory> selects) {
        int kind = -1;
        double min = 0;
        double max = 0;
        List<ArrayList<PointD>> ranges = new ArrayList<>();
        for (BudgetCategory category : selects) {
            if (category.getPid() > 0) {
                ArrayList<PointD> moneyRange = category.getMoneyRange();
                ranges.add(moneyRange);
            }
        }
        //四种算法
        for (int i = 0; i < METHOD_COUNT; i++) {
            min = 0;
            max = 0;
            for (ArrayList<PointD> pointDs : ranges) {
                PointD pointD = pointDs.get(i);
                min += pointD.getX();
                max += pointD.getY();
            }
            double minMoney = min * MONEY_UNIT;
            double maxMoney = max * MONEY_UNIT;
            if (figure >= minMoney && figure < maxMoney) {
                kind = i;
                break;
            }

            if (i == METHOD_COUNT - 1) {
                //the last range money just need >= min
                if (figure >= minMoney) {
                    kind = i;
                    break;
                }
            }
        }
        return kind;
    }


    /**
     * 计算每一个category对应的Money
     *
     * @param kind
     * @param figure
     * @param selects
     */
    public void computeMoney(
            int kind, double figure, List<BudgetCategory> selects) {
        //算出总的比例
        double allRate = 0;
        for (BudgetCategory category : selects) {
            if (category.getPid() > 0) {
                allRate += category.getRates()
                        .get(kind);
            }
        }
        for (BudgetCategory category : selects) {
            if (category.getPid() > 0) {
                double rate = category.getRates()
                        .get(kind);
                double computeRate = rate / allRate;
                double money = computeRate * figure;
                category.setMoney(Math.round(money));
            }
        }
    }


    private static class NewBudgetUtilHolder {
        private static final NewBudgetUtil INSTANCE = new NewBudgetUtil();
    }
}
