package com.hunliji.hljnotelibrary.models;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.interfaces.HljRZData;
import com.hunliji.hljcommonlibrary.models.search.ToolsSearchResult;

/**
 * Created by jinxin on 2017/7/17 0017.
 */

public class NoteSearchResult implements HljRZData {

    @SerializedName(value = "note")
    NoteSearchResultList noteResult;
    @SerializedName("tool")
    ToolsSearchResult toolsSearchResult;

    public NoteSearchResultList getNoteResult() {
        return noteResult;
    }

    public ToolsSearchResult getToolsSearchResult() {
        return toolsSearchResult;
    }

    @Override
    public boolean isEmpty() {
        return noteResult == null || noteResult.getNoteList() == null || noteResult.getNoteList()
                .isEmpty();
    }
}
