package com.hunliji.hljsharelibrary.third;

import com.google.gson.JsonObject;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface ThirdService {

    /**
     * 获取微信token
     * @param appid
     * @param secret
     * @param code
     * @return
     */
    @GET("https://api.weixin.qq.com/sns/oauth2/access_token?grant_type=authorization_code")
    Observable<JsonObject> getWeixnAccessToken(
            @Query("appid") String appid,
            @Query("secret") String secret,
            @Query("code") String code);


    /**
     * 获取微信用户信息
     * @param accessToken
     * @param openid
     * @return
     */
    @GET("https://api.weixin.qq.com/sns/userinfo")
    Observable<JsonObject> getWeixnUserInfo(
            @Query("access_token") String accessToken, @Query("openid") String openid);


    /**
     * 获取微博用户信息
     * @param accessToken 采用OAuth授权方式为必填参数，OAuth授权后获得。
     * @param uid         需要查询的用户ID。
     * @return
     */
    @GET("https://api.weibo.com/2/users/show.json")
    Observable<JsonObject> getWeiboUserInfo(
            @Query("access_token") String accessToken,@Query("uid") String uid);
}
