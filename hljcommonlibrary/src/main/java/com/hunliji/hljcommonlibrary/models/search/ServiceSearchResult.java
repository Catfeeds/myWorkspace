package com.hunliji.hljcommonlibrary.models.search;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/12/1.
 */

public class ServiceSearchResult {

    @SerializedName("package")
    WorksSearchResult worksSearchResult;
    @SerializedName("example")
    WorksSearchResult casesSearchResult;
    @SerializedName("merchant")
    MerchantsSearchResult merchantsSearchResult;
    @SerializedName("hotel")
    MerchantsSearchResult hotelsSearchResult;
    @SerializedName("tool")
    ToolsSearchResult toolsSearchResult;



    /**
     * 得到这个请求结果所使用的page参数，应该也就是列表结果的页码，但不是从请求结果中获取的
     * 而是在请求发出请求的时候记录，然后返回结果的时候将此参数添加到结果里
     */
    private int page;

    public MerchantsSearchResult getHotelsSearchResult() {
        return hotelsSearchResult;
    }

    public WorksSearchResult getWorksSearchResult() {
        return worksSearchResult;
    }

    public WorksSearchResult getCasesSearchResult() {
        return casesSearchResult;
    }

    public MerchantsSearchResult getMerchantsSearchResult() {
        return merchantsSearchResult;
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
