package com.hunliji.hljcommonlibrary.models.search;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hua_rong on 2018/1/22
 * 下拉提示分类
 */

public class TipSearchType {

    private String key;
    @SerializedName(value = "doc_count")
    private int docCount;

    /**
     * 结果数量文本
     *
     * @return
     */
    public String getDocStringContent() {
        return docCount + "个结果";
    }

    public int getDocCount() {
        return docCount;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setDocCount(int docCount) {
        this.docCount = docCount;
    }
}
