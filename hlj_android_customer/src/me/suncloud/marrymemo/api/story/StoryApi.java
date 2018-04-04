package me.suncloud.marrymemo.api.story;

import com.hunliji.hljcommonlibrary.models.story.Story;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by werther on 16/8/31.
 * 故事相关API接口
 */
public class StoryApi {

    /**
     * 获取用户的所有故事的列表
     *
     * @param userId
     * @return
     */
    public static Observable<HljHttpData<List<Story>>> getStoriesObb(long userId) {
        return HljHttp.getRetrofit()
                .create(StoryService.class)
                .getStories(userId)
                .map(new HljHttpResultFunc<HljHttpData<List<Story>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
