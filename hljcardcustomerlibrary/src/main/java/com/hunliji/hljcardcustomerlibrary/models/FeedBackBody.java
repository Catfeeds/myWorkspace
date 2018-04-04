package com.hunliji.hljcardcustomerlibrary.models;

/**
 * Created by hua_rong on 2017/6/20.
 * 意见反馈
 */

public class FeedBackBody {

    public FeedBackBody(String content, String contact) {
        this.content = content;
        this.contact = contact;
        this.device=android.os.Build.MODEL;
        this.system=android.os.Build.VERSION.RELEASE;
    }

    private String content;
    private String contact;
    private String device;
    private String system;
}
