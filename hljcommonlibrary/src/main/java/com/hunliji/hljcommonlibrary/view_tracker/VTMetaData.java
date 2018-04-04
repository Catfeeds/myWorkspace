package com.hunliji.hljcommonlibrary.view_tracker;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luohanlin on 24/03/2017.
 * 曝光统计中视图中附加的meta data
 */

public class VTMetaData {

    public static class DATA_TYPE {
        public static final String DATA_TYPE_POSTER = "Poster"; //广告
        public static final String DATA_TYPE_PACKAGE = "Package"; //套餐
        public static final String DATA_TYPE_MERCHANT = "Merchant"; //商家
        public static final String DATA_TYPE_EXAMPLE = "Example"; //案例
        public static final String DATA_TYPE_COMMUNITY_THREAD = "CommunityThread";//帖子
        public static final String DATA_TYPE_PRODUCT = "Article";//商品
        public static final String DATA_TYPE_CAR = "Car";//婚车
        public static final String DATA_TYPE_CITY = "City";//城市
        public static final String DATA_TYPE_CAR_LESSON = "CarLesson";//婚车必修课
        public static final String DATA_TYPE_CAR_BRAND = "CarBrand";//婚车品牌
        public static final String DATA_TYPE_GUIDE = "Guide";//攻略
        public static final String DATA_TYPE_KEYWORD = "Keyword";//关键词
        public static final String DATA_TYPE_SEARCH_CATEGORY = "SearchCategory";//搜索分类
        public static final String DATA_TYPE_LIVE = "Live";//直播
        public static final String DATA_TYPE_CASE_PHOTO = "CasePhoto"; //客照
        public static final String DATA_TYPE_NOTE = "Note"; //笔记
        public static final String DATA_TYPE_QUESTION = "Question";//问答
        public static final String DATA_TYPE_COMMUNITY_CHANNEL = "CommunityChannel";//频道
        public static final String DATA_TYPE_COMMUNITY_EVENT = "CommunityEvent";//社区活动
        public static final String DATA_TYPE_WEDDING_BIBLE = "WeddingBible"; //结婚宝典
        public static final String DATA_TYPE_TASK_LOG = "TaskLog";
        public static final String DATA_TYPE_POP_ACTIVITY = "ActivityLog";
        public static final String DATA_TYPE_LIVE_LOG = "LiveLog";
    }


    public static class EXTRA_DATA_KEY {
        public static final String KEY_CPM_FLAG = "cpm_flag"; //cpm统计标识
        public static final String KEY_CPM_SOURCE = "cpm_source";//cpm页面来源
        public static final String KEY_CPM_MID = "mid";//cpm相关商家id
        public static final String KEY_PROPERTY_ID = "property_id";//商家类别
        public static final String KEY_FEED_PROPERTY_ID = "feed_property_id";//首页分类类别
        public static final String KEY_DT_EXTEND = "dt_extend";//推荐数据标识,统计展示效果
        public static final String KEY_KEYWORD = "keyword";//关键词
        public static final String KEY_SEARCH_CATEGORY = "search_category";//搜索分类
    }

    private Long dataId;
    private String dataType;

    private Map<String, Object> extraData;

    public VTMetaData() {
    }

    public VTMetaData(Long dataId, String dataType) {
        this.dataId = dataId;
        this.dataType = dataType;
    }

    public VTMetaData(
            Long dataId, String dataType, Map<String, Object> extraData) {
        this.dataId = dataId;
        this.dataType = dataType;
        this.extraData = extraData;
    }

    public long getDataId() {
        return dataId;
    }

    public void setDataId(long dataId) {
        this.dataId = dataId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void addExtraData(String key, Object value) {
        if (this.extraData == null) {
            this.extraData = new HashMap<>();
        }

        this.extraData.put(key, value);
    }

    public JSONObject toJson() {
        if (dataId == null && TextUtils.isEmpty(dataType) && extraData == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data_id", this.dataId);
            jsonObject.put("data_type", this.dataType);
            if (this.extraData != null) {
                for (Map.Entry<String, Object> entry : this.extraData.entrySet()) {
                    jsonObject.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
