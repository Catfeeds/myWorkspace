package me.suncloud.marrymemo.model.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljhttplibrary.entities.HljHttpData;

/**
 * 专题标签列表，带title
 * Created by chen_bin on 2016/9/13 0013.
 */
public class HljHttpMarksData<T> extends HljHttpData<T> {
    @SerializedName(value = "mark_name", alternate = "mark_group_name")
    private String markName;

    public String getMarkName() {
        return markName;
    }

}