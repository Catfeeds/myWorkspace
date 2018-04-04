package com.hunliji.hljcardlibrary.models.wrappers;

/**
 * 用作CardMusicActivity 发送RxEvent
 * Created by jinxin on 2017/11/22 0022.
 */

public class MusicNotifyWrapper {

    long markId;
    String path;

    public MusicNotifyWrapper(String path,long markId){
        this.path = path;
        this.markId =markId;
    }

    public long getMarkId() {
        return markId;
    }

    public String getPath() {
        return path;
    }
}
