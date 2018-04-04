package com.hunliji.hljpushlibrary.models.live;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.live.LiveChannel;

/**
 * Created by wangtao on 2017/12/5.
 */

public class LiveData {

    @SerializedName("log_id")
    private long logId;
    @SerializedName("live")
    private LiveChannel liveChannel;

    public LiveChannel getLiveChannel() {
        return liveChannel;
    }

    public long getLogId() {
        return logId;
    }
}
