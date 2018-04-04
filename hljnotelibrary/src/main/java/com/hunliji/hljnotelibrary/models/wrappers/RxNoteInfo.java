package com.hunliji.hljnotelibrary.models.wrappers;

/**
 * Created by mo_yu on 2017/7/23.用于rxbus传输数据
 */

public class RxNoteInfo {
    private int position;
    private int collectCount;
    private int commentCount;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
