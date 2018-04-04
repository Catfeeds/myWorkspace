package me.suncloud.marrymemo.fragment.newsearch;

import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;

/**
 * Created by hua_rong on 2018/1/5
 * 搜索结果筛选
 */

public interface OnSearchMenuListener {

    void onSearchMenuRefresh(NewSearchApi.SearchType searchType);

}
