package me.suncloud.marrymemo.model.community;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonviewlibrary.models.CommunityFeed;
import com.hunliji.hljhttplibrary.entities.HljHttpData;

import java.util.List;

/**
 * Created by mo_yu on 2018/3/16.新娘说精选feed流
 */

public class HljHttpCommunityFeedData<T> extends HljHttpData<T> {

    @SerializedName("fix_list")
    List<CommunityFeed> fixList;

    public List<CommunityFeed> getFixList() {
        return fixList;
    }

    public void setFixList(List<CommunityFeed> fixList) {
        this.fixList = fixList;
    }
}
