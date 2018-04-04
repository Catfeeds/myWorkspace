package me.suncloud.marrymemo.model.tools.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljhttplibrary.entities.HljHttpData;

import org.joda.time.DateTime;

import java.util.List;

import me.suncloud.marrymemo.model.tools.WeddingCalendarItem;

/**
 * Created by chen_bin on 2018/2/2 0002.
 */
public class HljWeddingCalendarItemsData extends HljHttpData<List<WeddingCalendarItem>> {
    @SerializedName(value = "statistic_end_at")
    private DateTime statisticEndAt;

    public DateTime getStatisticEndAt() {
        return statisticEndAt;
    }
}
