package me.suncloud.marrymemo.model.user;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

/**
 * Created by chen_bin on 2017/11/8 0008.
 */
public class UserStatistics {
    @SerializedName(value = "cash_book_money")
    private double cashBookMoney;  //账本支出金额
    @SerializedName(value = "collection_count")
    private int collectionCount; //收藏数
    @SerializedName(value = "community_count")
    private int communityCount; //发布数
    @SerializedName(value = "order_count")
    private int orderCount;
    @SerializedName(value = "to_do")
    private JsonElement todo;

    private transient int finishCount = -1;
    private transient int totalCount = -1;

    public double getCashBookMoney() {
        return cashBookMoney;
    }

    public int getCollectionCount() {
        return collectionCount;
    }

    public int getCommunityCount() {
        return communityCount;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public int getFinishCount() {
        if (finishCount == -1) {
            finishCount = CommonUtil.getAsInt(todo, "finish_count");
        }
        return finishCount;
    }

    public int getTotalCount() {
        if (totalCount == -1) {
            totalCount = CommonUtil.getAsInt(todo, "total_count");
        }
        return totalCount;
    }

}
