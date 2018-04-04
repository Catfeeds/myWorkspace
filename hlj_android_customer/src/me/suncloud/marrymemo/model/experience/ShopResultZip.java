package me.suncloud.marrymemo.model.experience;

import com.hunliji.hljcommonlibrary.models.Comment;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljhttplibrary.entities.HljHttpData;

import java.util.List;

/**
 * 体验店首页
 * Created by jinxin on 2017/3/30 0030.
 */

public class ShopResultZip {
    public PosterData posterData;
    public ExperienceShop shop;
    public HljHttpData<List<Comment>> comments;
    public HljHttpData<List<Planner>> planners;
}
