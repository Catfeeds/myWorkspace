package com.hunliji.hljcommonlibrary.models.search;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luohanlin on 2017/7/19.
 * 搜索笔记类别的结果
 */
public class NoteTypeSearchResult {
    @SerializedName("package")
    WorksSearchResult worksSearchResult;
    @SerializedName("product")
    ProductSearchResultList productList;
    @SerializedName("note")
    NoteSearchResult noteSearchResult;
    @SerializedName("tool")
    ToolsSearchResult toolsSearchResult;


    /**
     * 得到这个请求结果所使用的page参数，应该也就是列表结果的页码，但不是从请求结果中获取的
     * 而是在请求发出请求的时候记录，然后返回结果的时候将此参数添加到结果里
     */
    private int page;

    public WorksSearchResult getWorksSearchResult() {
        return worksSearchResult;
    }

    public ProductSearchResultList getProductList() {
        return productList;
    }

    public NoteSearchResult getNoteSearchResult() {
        return noteSearchResult;
    }

    public ToolsSearchResult getToolsSearchResult() {
        return toolsSearchResult;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
