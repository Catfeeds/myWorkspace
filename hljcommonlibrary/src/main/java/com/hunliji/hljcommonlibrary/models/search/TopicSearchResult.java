package com.hunliji.hljcommonlibrary.models.search;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.TopicUrl;

import java.util.List;


/**
 * Created by werther on 16/12/7.
 */
public class TopicSearchResult extends BaseSearchResult {
    @SerializedName("list")
    List<TopicUrl> topicList;

    public List<TopicUrl> getTopicList() {
        return topicList;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() || topicList == null || topicList.isEmpty();
    }
}
