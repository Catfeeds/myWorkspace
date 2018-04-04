package me.suncloud.marrymemo.model.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.subpage.SubPageCategoryMark;
import com.hunliji.hljhttplibrary.entities.HljHttpData;

import java.util.List;

/**
 * Created by wangtao on 2017/1/24.
 */

public class HljHttpSubPageCategoryMarksData extends HljHttpData<List<SubPageCategoryMark>> {
    @SerializedName("ranking_img")
    private String rankingImg; //排行榜图片

    public String getRankingImg() {
        return rankingImg;
    }

    public void setRankingImg(String rankingImg) {
        this.rankingImg = rankingImg;
    }
}
