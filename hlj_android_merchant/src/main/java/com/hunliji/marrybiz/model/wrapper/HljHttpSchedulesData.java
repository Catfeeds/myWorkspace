package com.hunliji.marrybiz.model.wrapper;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.marrybiz.model.tools.Schedule;

import java.util.List;

/**
 * 日程管理专用，获取商家类型跟当日的日程是否已满
 * Created by chen_bin on 2016/9/13 0013.
 */
public class HljHttpSchedulesData extends HljHttpData<List<Schedule>> {
    @SerializedName(value = "m_property")
    private int property;

    public int getProperty() {return property;}

    public void setProperty(int property) {
        this.property = property;
    }
}
