package com.hunliji.hljcommonlibrary.models.search;

import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Created by werther on 16/12/1.
 */

public class ToolsSearchResult extends BaseSearchResult {

    @SerializedName("list")
    List<ToolSearchResult> toolSearchResultList;

    public List<ToolSearchResult> getToolSearchResultList() {
        return toolSearchResultList;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() || toolSearchResultList == null || toolSearchResultList.isEmpty();
    }
}
