package com.hunliji.marrybiz.util.work_case;

import java.io.Serializable;

/**
 * Created by chen_bin on 17/05/15.
 * 套餐状态枚举
 */
public enum WorkStatusEnum implements Serializable {

    REVIEW(0, 0, 0, "审核中"), //审核中
    ON(1, 0, 0, "已上架"),   //已上架
    OFF(-1, 1, 0, "已下架"), //已下架
    REJECTED(3, 0, 0, "审核未通过"), //审核不通过
    OPTIMIZE(1, 0, 1, "待优化"); //待优化

    private int status;     //套餐状态
    private int isSoldOut; //是否下架
    private int rating;  //是否需要优化
    private String statusDescription;

    WorkStatusEnum(int status, int isSoldOut, int rating, String statusDescription) {
        this.status = status;
        this.isSoldOut = isSoldOut;
        this.rating = rating;
        this.statusDescription = statusDescription;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int isSoldOut() {
        return isSoldOut;
    }

    public void setSoldOut(int isSoldOut) {
        this.isSoldOut = isSoldOut;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getStatusDescription() {return statusDescription;}

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

}