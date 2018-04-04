package me.suncloud.marrymemo.model.realm;

import io.realm.RealmObject;

/**
 * Created by hua_rong on 2018/1/4
 * 历史关键词
 */

public class NewHistoryKeyWord extends RealmObject {

    private long id;
    private String category;
    private String keyword;
    private long merchantId; //店铺详情页

    public NewHistoryKeyWord() {
        this.id = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }
}
