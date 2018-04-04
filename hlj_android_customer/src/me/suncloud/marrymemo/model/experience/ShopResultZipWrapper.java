package me.suncloud.marrymemo.model.experience;

import com.hunliji.hljcommonlibrary.models.Comment;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljhttplibrary.entities.HljHttpData;

import java.util.List;

/**
 * 体验店首页model warpper
 * Created by jinxin on 2017/3/30 0030.
 */

public class ShopResultZipWrapper {
    public List<Poster> topPosters;//顶图
    public List<Poster> singlePoster;//单图
    public List<ExperiencePhoto> atlas;//图集
    public List<ExperienceEvent> choice;//活动
    public Store store;//体验店model
    public List<String> commentTag;//印象
    public String contactPhone;//电话
    public String location;//位置
    public List<StoreVideo> storeVideo;//视频
    public int activityCount;//活动数目
    public String panorama;//全景
    public List<Comment> comments;//评论
    public List<Planner> planners;//统筹师
    public long mediaAlbumId;//图集Id
    public String name;//名字
    public String address;//地址
}

