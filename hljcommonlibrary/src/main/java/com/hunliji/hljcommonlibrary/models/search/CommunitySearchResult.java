package com.hunliji.hljcommonlibrary.models.search;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/12/7.
 */
public class CommunitySearchResult {

    @SerializedName("qa")
    QASearchResult qaSearchResult;
    @SerializedName("bbs")
    ThreadSearchResult threadSearchResult;
    @SerializedName("topic")
    TopicSearchResult topicSearchResult;
    @SerializedName("tool")
    ToolsSearchResult toolsSearchResult;

    public QASearchResult getQaSearchResult() {
        return qaSearchResult;
    }

    public ThreadSearchResult getThreadSearchResult() {
        return threadSearchResult;
    }

    public TopicSearchResult getTopicSearchResult() {
        return topicSearchResult;
    }

    public ToolsSearchResult getToolsSearchResult() {
        return toolsSearchResult;
    }
}
