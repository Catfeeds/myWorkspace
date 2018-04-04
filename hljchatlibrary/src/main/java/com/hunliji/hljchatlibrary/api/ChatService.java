package com.hunliji.hljchatlibrary.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljchatlibrary.models.modelwrappers.ChannelMessages;
import com.hunliji.hljchatlibrary.models.modelwrappers.Channels;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Suncloud on 2016/10/18.
 */

public interface ChatService {

    @GET
    Observable<Channels> getChannels(@Url String path);


    @DELETE
    Observable<JsonElement> deleteChannel(@Url String path);


    @GET
    Observable<ChannelMessages> getChannelMessages(@Url String path);


    /**
     * 获取简单用户信息，用于用户转商家类型
     *
     * @param id userid
     * @return
     */
    @GET("p/wedding/index.php/Home/APIUser/mini_user")
    Observable<HljHttpResult<JsonObject>> getMiniUser(@Query("user_id") long id);

    /**
     * <a href=http://doc.hunliji.com/workspace/myWorkspace.do?projectId=15#3398>频道置顶</a>
     *
     * @param path /api/v1/channel_stick
     * @return
     */
    @POST
    Observable<HljHttpResult> postChannelStick(
            @Url String path, @Body JsonObject body);

    /**
     * 触发商家智能回复
     *
     * @param path /api/v1/smart_reply
     * @return
     */
    @POST
    Observable<HljHttpResult> postForSmartReplay(
            @Url String path,
            @Query("creator_id") long creatorId,
            @Query("to_user_id") long userId);

    /**
     * <a href=http://doc.hunliji.com/workspace/myWorkspace.do?projectId=15#4406>用户端调API
     * ，和婚车商家聊天的时候，生成客资，同时写入当前城市的city_code</a>
     *
     * @return
     */
    @POST("p/wedding/index.php/Home/APIUser/store_city_code")
    Observable<HljHttpResult> saveUserCity(@Body JsonObject body);

    /**
     * 一个公用的接口用来让商家给当前用户发送私信
     *
     * @param path api/v1/msg_proxy
     * @param map
     * @return
     */
    @POST
    Observable<HljHttpResult> postMsgProxy(@Url String path, @Body Map<String, Object> map);
}
