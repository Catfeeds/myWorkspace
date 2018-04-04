package com.hunliji.hljcommonlibrary.view_tracker.models;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;

import java.util.List;

/**
 * Created by wangtao on 2018/2/22.
 */

public class TrackerView {

    public static final int TAG_TYPE_PARENT = 1;
    public static final int TAG_TYPE_HIT = 2;
    public static final int TAG_TYPE_VIEW = 3;


    @SerializedName("id")
    private String id;
    @SerializedName("type")
    private int type;
    @SerializedName("tag")
    private String tag;
    @SerializedName("parent_tag")
    private String parentTag;
    @SerializedName("data")
    private List<TrackerData> data;

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getTag() {
        return tag;
    }

    public List<TrackerData> getData() {
        return data;
    }

    public void tag(View view, Object classObject) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        if (type == TAG_TYPE_PARENT) {
            StringBuilder parentTag = new StringBuilder(tag);
            if (!CommonUtil.isCollectionEmpty(data)) {
                for (TrackerData trackerData : data) {
                    Object value = trackerData.getValue(classObject);
                    if (value != null) {
                        if (parentTag.toString()
                                .contains("?")) {
                            parentTag.append("?");
                        } else {
                            parentTag.append("&");
                        }
                        if (TextUtils.isEmpty(trackerData.getKey())) {
                            parentTag.append(trackerData.getKey())
                                    .append("=");
                        }
                        parentTag.append(value);
                    }
                }
            }
            HljVTTagger.tagViewParentName(view, parentTag.toString());
        } else {
            HljVTTagger.Tagger tagger = HljVTTagger.buildTagger(view)
                    .tagName(tag)
                    .tagParentName(parentTag);
            if (!CommonUtil.isCollectionEmpty(data)) {
                for (TrackerData trackerData : data) {
                    Object value = trackerData.getValue(classObject);
                    if (value != null) {
                        tagger.addDataExtra(trackerData.getKey(), value);
                    }
                }
            }
            if (classObject instanceof BaseViewHolder) {
                tagger.atPosition(((BaseViewHolder) classObject).getItemPosition());
            }
            if (type == TAG_TYPE_HIT) {
                tagger.hitTag();
            } else {
                tagger.tag();
            }
        }
    }
}
