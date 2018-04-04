package com.hunliji.hljkefulibrary.moudles;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wangtao on 2017/10/26.
 */

public class SupportListData {

    @SerializedName("supports")
    List<Support> supports;

    public List<Support> getSupports() {
        return supports;
    }
}
