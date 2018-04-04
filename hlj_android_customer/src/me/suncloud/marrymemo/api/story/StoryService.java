package me.suncloud.marrymemo.api.story;

import com.hunliji.hljcommonlibrary.models.story.Story;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by werther on 16/8/31.
 * 故事相关API接口
 */
public interface StoryService {

    @GET("p/wedding/index.php/Home/APIStory/story_list?page=1&per_page=9999")
    Observable<HljHttpResult<HljHttpData<List<Story>>>> getStories(@Query("user_id") long userId);
}
