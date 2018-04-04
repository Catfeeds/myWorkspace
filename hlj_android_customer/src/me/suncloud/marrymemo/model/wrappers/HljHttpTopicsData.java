package me.suncloud.marrymemo.model.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljhttplibrary.entities.HljHttpData;

import java.util.List;

/**
 * 专题数据包含了有用数(useful_count)
 * Created by chen_bin on 2017/6/26 0026.
 */
public class HljHttpTopicsData extends HljHttpData<List<TopicUrl>> {
    @SerializedName("useful_count")
    private int usefulCount; //有用数

    public int getUsefulCount() {
        return usefulCount;
    }
}
