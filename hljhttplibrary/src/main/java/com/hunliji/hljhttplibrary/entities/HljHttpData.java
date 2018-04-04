package com.hunliji.hljhttplibrary.entities;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.interfaces.HljRZData;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.Collection;

/**
 * Created by werther on 16/7/18.
 * 婚礼纪Http接口返回的真正有用的数据结构
 * 这是其中一种结构
 */
public class HljHttpData<T> implements HljRZData {
    @SerializedName(value = "page_count")
    int pageCount;
    @SerializedName(value = "total_count", alternate = {"total"})
    int totalCount;
    @SerializedName(value = "list")
    T data;

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public boolean isEmpty() {
        if (getData() instanceof Collection && !CommonUtil.isCollectionEmpty((Collection) getData
                ())) {
            return false;
        } else if (totalCount > 0) {
            return false;
        }
        return true;
    }
}
