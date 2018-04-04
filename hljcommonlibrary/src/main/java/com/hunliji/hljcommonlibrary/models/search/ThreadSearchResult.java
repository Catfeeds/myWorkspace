package com.hunliji.hljcommonlibrary.models.search;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;

import java.util.List;

/**
 * Created by werther on 16/12/7.
 */
public class ThreadSearchResult extends BaseSearchResult {
    @SerializedName("list")
    List<CommunityThread> communityThreadList;

    public List<CommunityThread> getCommunityThreadList() {
        return communityThreadList;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() || communityThreadList == null || communityThreadList.isEmpty();
    }
}
