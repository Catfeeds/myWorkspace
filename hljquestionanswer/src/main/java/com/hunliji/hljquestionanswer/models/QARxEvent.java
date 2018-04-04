package com.hunliji.hljquestionanswer.models;

/**
 * Created by wangtao on 2016/12/22.
 */

public class QARxEvent {
    private RxEventType type;
    private Object object;

    public enum RxEventType {
        QUESTION_POST_DONE, //提问完成
        COMPLAIN_SUCCESS, //申诉成功
        QUESTION_REPLY_SUCCESS,//回答完成
        ASK_QUESTION_SUCCESS,//商家问答提问完成 区别于之前的社区提问
    }

    public QARxEvent(RxEventType type, Object object) {
        this.type = type;
        this.object = object;
    }

    public RxEventType getType() {
        return type;
    }

    public Object getObject() {
        return object;
    }
}
