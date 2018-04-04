package me.suncloud.marrymemo.util;

import com.hunliji.hljcommonviewlibrary.models.CommunityFeed;

import java.util.ArrayList;

/**
 * 社区推荐话题与看帖页数据同步
 * Created by mo_yu on 2017/4/6 0021.
 */
@Deprecated
// TODO: 2018/3/28 wangtao 删除
public class RecommendThreadUtil {

    private ArrayList<CommunityFeed> recommendThreads;

    public static RecommendThreadUtil getInstance() {
        return RecommendThreadUtilHolder.INSTANCE;
    }

    public ArrayList<CommunityFeed> getRecommendThreads() {
        if(recommendThreads==null){
            return new ArrayList<>();
        }
        return recommendThreads;
    }

    public void saveRecommendThreads(ArrayList<CommunityFeed> threads) {
        if (recommendThreads == null) {
            recommendThreads = new ArrayList<>();
        }
        recommendThreads.clear();
        recommendThreads.addAll(threads);
    }

    public void cleanRecommendThreads() {
        if (recommendThreads != null) {
            recommendThreads.clear();
        }
    }

    private static class RecommendThreadUtilHolder {
        private static RecommendThreadUtil INSTANCE = new RecommendThreadUtil();
    }
}