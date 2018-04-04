package com.hunliji.hljcommonlibrary.models.search;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

/**
 * Created by werther on 16/12/1.
 */

public class WorksSearchResult extends BaseSearchResult {

    @SerializedName("list")
    List<Work> workList;

    public List<Work> getWorkList() {
        return workList;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() || CommonUtil.isCollectionEmpty(workList);
    }
}
