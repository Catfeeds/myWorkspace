package com.hunliji.hljcommonlibrary.view_tracker;

/**
 * Created by luohanlin on 20/03/2017.
 * 曝光统计时给视图标记的数据
 */

public class ViewTraceData {

    private String eventType;
    private String tagName;
    private String parentTagName;
    private VTMetaData metaData;
    private int position = -1;
    private String belongPageName;
    private VTMetaData pageData;
    private String miaoZhenImpUrl;
    private String miaoZhenClickUrl;

    public ViewTraceData(
            String tagName, String parentTagName, VTMetaData metaData, int position) {
        this.tagName = tagName;
        this.parentTagName = parentTagName;
        this.metaData = metaData;
        this.position = position;
    }


    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public VTMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(VTMetaData metaData) {
        this.metaData = metaData;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getParentTagName() {
        return parentTagName;
    }

    public void setParentTagName(String parentTagName) {
        this.parentTagName = parentTagName;
    }


    public void setBelongPageName(String belongPageName) {
        this.belongPageName = belongPageName;
    }

    public String getBelongPageName() {
        return belongPageName;
    }

    public void setPageData(VTMetaData pageData) {
        this.pageData = pageData;
    }

    public VTMetaData getPageData() {
        return pageData;
    }

    public void setMiaoZhenClickUrl(String miaoZhenClickUrl) {
        this.miaoZhenClickUrl = miaoZhenClickUrl;
    }

    public void setMiaoZhenImpUrl(String miaoZhenImpUrl) {
        this.miaoZhenImpUrl = miaoZhenImpUrl;
    }

    public String getMiaoZhenClickUrl() {
        return miaoZhenClickUrl;
    }

    public String getMiaoZhenImpUrl() {
        return miaoZhenImpUrl;
    }
}
