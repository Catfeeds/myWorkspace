package me.suncloud.marrymemo.model.wrappers;

import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljhttplibrary.entities.HljHttpData;

import java.util.List;

/**
 * 商家评价
 * Created by chen_bin on 2017/9/28 0028.
 */
public class HljHttpCommentsData extends HljHttpData<List<ServiceComment>> {
    private transient int firstSixMonthAgoIndex = -1; //出现6个月前的评论的第一条的index

    public int getFirstSixMonthAgoIndex() {
        return firstSixMonthAgoIndex;
    }

    public void setFirstSixMonthAgoIndex(int firstSixMonthAgoIndex) {
        this.firstSixMonthAgoIndex = firstSixMonthAgoIndex;
    }
}
