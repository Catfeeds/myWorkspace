package com.hunliji.hljcommonlibrary.models.communitythreads;

import com.google.gson.annotations.SerializedName;

/**
 * 新娘圈资料
 * Created by jinxin on 2018/3/20 0020.
 */

public class CommunityMaterial  {

    public static final int NOT_START = 0;//未开始
    public static final int UNDER_WAY = 1;//进行中
    public static final int COMPLETED = 2;//已完成

    long id;
    String content;//任务描述
    @SerializedName("current_num")
    int currentNum;//当前完成数量
    @SerializedName("finish_count")
    int finishCount;//该任务完成人数
    String link;//领取地址
    int status;//任务状态
    String title;//任务标题
    int num;//完成数量指标
    String password;//领取密码

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public int getCurrentNum() {
        return currentNum;
    }

    public int getFinishCount() {
        return finishCount;
    }

    public String getLink() {
        return link;
    }

    public int getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public int getNum() {
        return num;
    }

    public String getPassword() {
        return password;
    }
}
