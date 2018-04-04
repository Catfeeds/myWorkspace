package com.hunliji.hljnotelibrary.models.wrappers;

/**
 * Created by mo_yu on 2017/7/25.webview评论参数
 */

public class RxWebNoteComment {

    private long replyId;
    private String callback;

    public RxWebNoteComment(long replyId, String callback){
        this.replyId = replyId;
        this.callback = callback;
    }

    public long getReplyId() {
        return replyId;
    }

    public void setReplyId(long replyId) {
        this.replyId = replyId;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }
}
