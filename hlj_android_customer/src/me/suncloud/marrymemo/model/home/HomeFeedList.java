package me.suncloud.marrymemo.model.home;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.entities.HljHttpData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by luohanlin on 2017/4/20.
 * 首页feed列表的返回数据，结构与普通列表不一样
 */

public class HomeFeedList<T> extends HljHttpData<T> {
    @SerializedName("fix_data")
    HomeFeedFixData fixedFeedList;
    transient private List<HomeFeed> mixedList;

    public HomeFeedFixData getFixedFeedList() {
        return fixedFeedList;
    }

    public void parseAndMixedList(List<HomeFeed> feedList) {
        // mix 两个list
        if (mixedList == null) {
            mixedList = new ArrayList<>();
            if (!CommonUtil.isCollectionEmpty(feedList)) {
                mixedList.addAll(feedList);
            }
            if (fixedFeedList != null && fixedFeedList.getList() != null && !fixedFeedList.getList()
                    .isEmpty()) {
                ArrayList<HomeFeed> weightFeeds = new ArrayList<>();
                for (HomeFeed feed : fixedFeedList.getList()) {
                    if (feed.getWeight() > 0) {
                        weightFeeds.add(feed);
                    } else {
                        mixedList.add(feed);
                    }
                }
                if (!weightFeeds.isEmpty()) {
                    Collections.sort(weightFeeds, new Comparator<HomeFeed>() {
                        @Override
                        public int compare(HomeFeed o1, HomeFeed o2) {
                            return o1.getWeight() - o2.getWeight();
                        }
                    });
                    for (HomeFeed feed : weightFeeds) {
                        if (feed.getWeight() < mixedList.size()) {
                            mixedList.add(feed.getWeight() - 1, feed);
                        } else {
                            mixedList.add(feed);
                        }
                    }
                }
            }
        }

        // 解析前两个feed的entity，剩下的子后台解析
        if (mixedList.size() > 0) {
            for (int i = 0; i < 2 && i < mixedList.size(); i++) {
                mixedList.get(i)
                        .parseEntityObj();
            }
        }
        if (mixedList.size() > 2) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 2; i < mixedList.size(); i++) {
                        mixedList.get(i)
                                .parseEntityObj();
                    }
                }
            }).start();
        }
    }

    public List<HomeFeed> getMixedList() {
        return mixedList;
    }

    public void setMixedList(List<HomeFeed> mixedList) {
        this.mixedList = mixedList;
    }
}
