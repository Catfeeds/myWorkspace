package com.hunliji.hljcommonlibrary.models.search;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.note.Note;

import java.util.List;

/**
 * Created by luohanlin on 2017/7/19.
 */
public class NoteSearchResult extends BaseSearchResult {
    @SerializedName("list")
    List<Note> noteList;

    public List<Note> getNoteList() {
        return noteList;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() || noteList == null || noteList.isEmpty();
    }
}
