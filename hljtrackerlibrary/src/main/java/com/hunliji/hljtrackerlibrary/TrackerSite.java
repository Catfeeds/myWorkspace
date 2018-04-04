package com.hunliji.hljtrackerlibrary;

import com.hunliji.hljtrackerlibrary.utils.TrackerUtil;

import org.json.JSONObject;

/**
 * 统计中固定值的site
 * Created by wangtao on 2016/12/14.
 */

public enum TrackerSite {

    //进入搜索界面
    SEARCH_HOME("B1/A1", 2, "首页搜索"),
    SEARCH_SUB_PAGE("C1/A1", 1, "发现页搜索"),
    SEARCH_SOCIAL("D1/A1", 1, "新娘说搜索"),
    SEARCH_MERCHANT_LIST("B1/C1/A1", 2, "找商家搜"),
    SEARCH_CASE_LIST("B1/C1/A2", 2, "看案例搜索"),
    SEARCH_BUY_WORK("B1/C1/A3", 2, "订套餐入口页搜索"),
    SEARCH_WORK_LIST("B1/C1/A3/A1", 2, "订套餐分类列表搜索"),
    SEARCH_PRODUCT_CHANNEL("B1/C1/A4", 2, "婚品频道搜索"),
    SEARCH_PRODUCT_LIST("B1/C1/A4/A1", 2, "婚品分类页搜索"),

    //搜索关键词
    SERVICE_SEARCH("AH1/A1", 0, ""),
    PRODUCT_SEARCH("AH2/A1", 0, ""),
    COMMUNITY_SEARCH("AH3/A1", 0, ""),
    NOTE_SEARCH("AH4/A1", 0, "");


    private String sid;
    private int pos;
    private String desc;

    TrackerSite(String sid, int pos, String desc) {
        this.sid = sid;
        this.pos = pos;
        this.desc = desc;
    }

    @Override
    public String toString() {
        JSONObject siteJson = TrackerUtil.getSiteJson(sid, pos, desc);
        if (siteJson == null) {
            return null;
        }
        return siteJson.toString();
    }
}
