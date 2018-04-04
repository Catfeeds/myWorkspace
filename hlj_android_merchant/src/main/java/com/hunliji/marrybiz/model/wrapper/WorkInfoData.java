package com.hunliji.marrybiz.model.wrapper;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Work;

/**
 * Created by wangtao on 2016/11/23.
 */

public class WorkInfoData {
    @SerializedName("work")
    Work work;

    public Work getWork() {
        return work;
    }
}
