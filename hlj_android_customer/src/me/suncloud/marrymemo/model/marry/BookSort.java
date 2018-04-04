package me.suncloud.marrymemo.model.marry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hua_rong on 2017/11/7
 * 结婚记账 中间过度model
 */

public class BookSort {

    private double money;
    private Long parentId;
    private List<MarryBook> marryBooks;
    private long typeId;

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public List<MarryBook> getMarryBooks() {
        if (marryBooks == null) {
            marryBooks = new ArrayList<>();
        }
        return marryBooks;
    }

    public void setMarryBooks(List<MarryBook> marryBooks) {
        this.marryBooks = marryBooks;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }
}
