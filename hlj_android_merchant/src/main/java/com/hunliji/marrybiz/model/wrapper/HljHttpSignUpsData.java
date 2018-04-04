package com.hunliji.marrybiz.model.wrapper;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.models.event.SignUpInfo;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.marrybiz.model.event.EventPoint;

import java.util.List;

/**
 * 报名列表带活动详情
 * Created by chen_bin on 2016/9/13 0013.
 */
public class HljHttpSignUpsData extends HljHttpData<List<SignUpInfo>> {
    @SerializedName(value = "activity")
    private EventInfo eventInfo;

    public EventInfo getEventInfo() {
        return eventInfo;
    }

}
