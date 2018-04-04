package com.hunliji.hljcommonlibrary.view_tracker;

import android.text.TextUtils;
import android.view.View;

import com.hunliji.hljcommonlibrary.R;
import com.hunliji.hljcommonlibrary.models.Poster;

import java.util.HashMap;
import java.util.Map;

import static com.hunliji.hljcommonlibrary.view_tracker.HljViewTracker.getParentTagName;

/**
 * Created by luohanlin on 24/03/2017.
 */

public class HljVTTagger {

    public static Tagger buildTagger(View view) {
        return new Tagger(view);
    }

    public static void tagViewParentName(View view, String tagName) {
        view.setTag(R.id.hlj_tracker_parent_tag_id, tagName);
    }


    /**
     * 给view添加page属性
     * @param view
     * @param pageName
     * @param pageData
     */
    public static void tagViewPage(View view, String pageName,VTMetaData pageData) {
        view.setTag(R.id.hlj_tracker_view_page_name, pageName);
        view.setTag(R.id.hlj_tracker_view_page_data, pageData);
    }

    public static void clearTag(View view) {
        view.setTag(R.id.hlj_tracker_tag_id, null);
        HljViewTracker.INSTANCE.clearView(view);
    }


    public static class Tagger {
        private View target;
        private String tagName;
        private String tagParentName;
        private int position = -1;
        private Long dataId;
        private String dataType;
        private Map<String, Object> extraData;
        private String miaoZhenImpUrl;
        private String miaoZhenClickUrl;

        public Tagger(View target) {
            this.target = target;
        }

        /**
         * 统计时给予这个视图对应的别名，非常重要
         *
         * @param tagName
         * @return
         */
        public Tagger tagName(String tagName) {
            this.tagName = tagName;
            return this;
        }

        /**
         * 设置此视图的父视图的tag
         *
         * @param tagParentName
         * @return
         */
        public Tagger tagParentName(String tagParentName) {
            this.tagParentName = tagParentName;
            return this;
        }

        /**
         * 如果是列表中需要统计位置的数据，需要设置对应的位置Position值
         * 非列表视图不需要设置
         *
         * @param position
         * @return
         */
        public Tagger atPosition(int position) {
            this.position = position;
            return this;
        }

        /**
         * 设置Meta Data的ID，例如Poster的ID
         *
         * @param dataId
         * @return
         */
        public Tagger dataId(long dataId) {
            this.dataId = dataId;
            return this;
        }

        /**
         * 设置Meta Data的数据类型标识，用于给数据处理提示数据类别，例如"Poster"
         *
         * @param dataType
         * @return
         */
        public Tagger dataType(String dataType) {
            this.dataType = dataType;
            return this;
        }


        public Tagger poster(Poster poster) {
            if (!TextUtils.isEmpty(poster.getCpm()) && (poster.getTargetType() == 5 || poster
                    .getTargetType() == 41)) {
                dataId(poster.getTargetId());
                dataType(VTMetaData.DATA_TYPE.DATA_TYPE_MERCHANT);
                addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_FLAG, poster.getCpm());
            } else {
                dataId(poster.getId());
                dataType(VTMetaData.DATA_TYPE.DATA_TYPE_POSTER);
                addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_FLAG, poster.getCpm());
            }
            return this;
        }

        /**
         * 在基本的ID，Type之外需要添加特定的Meta Data，例如：feed_property_id -> 4
         *
         * @param key
         * @param value
         * @return
         */
        public Tagger addDataExtra(String key, Object value) {
            if (TextUtils.isEmpty(key)) {
                return this;
            }
            if (extraData == null) {
                extraData = new HashMap<>();
            }
            extraData.put(key, value);

            return this;
        }

        public Tagger addMiaoZhenImpUrl(String impUrl) {
            if (TextUtils.isEmpty(impUrl)) {
                return this;
            }
            this.miaoZhenImpUrl=impUrl;
            return this;
        }

        public Tagger addMiaoZhenClickUrl(String clickUrl) {
            if (TextUtils.isEmpty(clickUrl)) {
                return this;
            }
            this.miaoZhenClickUrl=clickUrl;
            return this;
        }

        public void hitTag() {
            VTMetaData metaData = new VTMetaData(dataId, dataType, extraData);
            if (TextUtils.isEmpty(tagParentName)) {
                tagParentName = getParentTagName(target);
            }
            ViewTraceData traceData = new ViewTraceData(tagName, tagParentName, metaData, position);
            traceData.setMiaoZhenClickUrl(miaoZhenClickUrl);
            traceData.setMiaoZhenImpUrl(miaoZhenImpUrl);
            this.target.setTag(R.id.hlj_tracker_tag_id, traceData);
        }

        /**
         * 必须调用这个方法才能是之前生效
         */
        public void tag() {
            hitTag();
            HljViewTracker.INSTANCE.trackView(this.target);
        }

        public void clear() {
            this.target.setTag(R.id.hlj_tracker_tag_id, null);
        }
    }
}
