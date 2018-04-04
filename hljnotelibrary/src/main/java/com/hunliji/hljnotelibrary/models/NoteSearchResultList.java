package com.hunliji.hljnotelibrary.models;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.search.BaseSearchResult;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

/**
 * Created by jinxin on 2017/7/17 0017.
 */

public class NoteSearchResultList extends BaseSearchResult {

    @SerializedName(value = "list")
    List<Note> noteList;

    public List<Note> getNoteList() {
        return noteList;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() || CommonUtil.isCollectionEmpty(noteList);
    }
}
