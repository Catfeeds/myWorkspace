package com.hunliji.hljcardlibrary.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcardlibrary.models.CardHideStatus;
import com.hunliji.hljcardlibrary.models.CardSetupStatus;
import com.hunliji.hljcardlibrary.models.ChooseTheme;
import com.hunliji.hljcardlibrary.models.EditAudioBody;
import com.hunliji.hljcardlibrary.models.ModifyNameResult;
import com.hunliji.hljcardlibrary.models.Music;
import com.hunliji.hljcardlibrary.models.SendCardBody;
import com.hunliji.hljcardlibrary.models.SingleTemplate;
import com.hunliji.hljcardlibrary.models.Theme;
import com.hunliji.hljcardlibrary.models.wrappers.CardListData;
import com.hunliji.hljcardlibrary.models.wrappers.PageEditResult;
import com.hunliji.hljcardlibrary.models.wrappers.PostPageBody;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.MinProgramShareInfo;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * 请帖Service
 * Created by mo_yu on 2017/06/13 0015.
 */
public interface CardService {

    /**
     * 请帖音乐标签
     *
     * @param url p/wedding/index.php/home/APIInvitationV3/music_marks
     * @return
     */
    @GET
    Observable<HljHttpResult<HljHttpData<List<Mark>>>> getMusicMark(@Url String url);

    /**
     * 获得某个标签音乐
     *
     * @param url    p/wedding/index.php/home/APIInvitationV3/card_music_list
     * @param markId
     * @return
     * @
     */
    @GET
    Observable<HljHttpResult<HljHttpData<List<Music>>>> getMusicList(
            @Url String url,
            @Query("mark_id") long markId,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 编辑请帖音乐信息
     *
     * @param url  p/wedding/index.php/Home/APIInvitationV3/edit_audios
     * @param body
     * @return
     */
    @POST
    Observable<HljHttpResult> editAudioInfo(
            @Url String url, @Body EditAudioBody body);

    /**
     * 单页模板列表
     *
     * @param url  p/wedding/index.php/home/APIInvitationV3/page_template_list
     * @param id   模板id
     * @param type single_img / two_img / multi_img / text / video
     * @return 接口描述 增加video 节点 返回视频单页列表 新接口
     */
    @GET
    Observable<HljHttpResult<SingleTemplate>> getPageTemplateList(
            @Url String url, @Query("id") long id, @Query("type") String type);

    /**
     * 标签列表
     *
     * @param url p/wedding/index.php/home/APIInvitationV3/card_marks
     * @return
     */
    @GET
    Observable<HljHttpResult<HljHttpData<List<Mark>>>> getCardMarks(@Url String url);


    /**
     * 模板列表
     *
     * @param url p/wedding/index.php/home/APIInvitationV3/theme_list
     * @return
     */
    @GET
    Observable<HljHttpResult<ChooseTheme>> getThemeList(
            @Url String url, @Query("is_member") int isMember, @Query("mark_id") long markId);


    /**
     * 模板 主题分享
     *
     * @param url p/wedding/Home/APISetting/app_share
     * @return
     */
    @GET
    Observable<ShareInfo> getAppShareInfo(@Url String url);

    /**
     * 隐藏/显示致宾客页
     *
     * @param url        p/wedding/index.php/home/APIInvitationV3/hidden_card_page
     * @param jsonObject
     * @return
     */
    @POST
    Observable<HljHttpResult<CardHideStatus>> postHiddenCardPage(
            @Url String url, @Body JsonObject jsonObject);


    /**
     * 删除单页
     *
     * @param url        p/wedding/index.php/home/APIInvitationV3/card_page
     * @param cardPageId
     * @return
     */
    @DELETE
    Observable<HljHttpResult> deleteCardPage(
            @Url String url, @Query("card_page_id") long cardPageId);

    /**
     * 请帖设置信息
     *
     * @param url    p/wedding/index.php/home/APICardSetup/status
     * @param cardId
     * @return
     */
    @GET
    Observable<HljHttpResult<CardSetupStatus>> getCardSetupStatus(
            @Url String url, @Query("id") long cardId);

    /**
     * 请帖设置
     *
     * @param url p/wedding/index.php/home/APICardSetup/set
     * @param map
     * @return
     */
    @POST
    Observable<HljHttpResult> postCardSetup(@Url String url, @Body Map<String, Object> map);


    /**
     * 复制请帖
     *
     * @param url     p/wedding/index.php/home/APIInvitationV3/copy_card
     * @param copyMap
     * @return
     */
    @POST
    Observable<HljHttpResult<JsonElement>> copyCard(
            @Url String url, @Body Map<String, Object> copyMap);

    /**
     * 删除/关闭请帖
     *
     * @param url p/wedding/index.php/home/APIInvitationV3/close_card
     * @param map
     * @return
     */
    @POST
    Observable<HljHttpResult> deleteOrCloseCard(@Url String url, @Body Map<String, Object> map);

    /**
     * 开启请帖
     *
     * @param url     p/wedding/index.php/home/APIInvitationV3/open_card
     * @param openMap
     * @return
     */
    @POST
    Observable<HljHttpResult> openCard(@Url String url, @Body Map<String, Object> openMap);

    /**
     * 删除请帖(新)
     *
     * @param url p/wedding/index.php/home/APIInvitationV3/card
     * @param map
     * @return
     */
    @DELETE
    Observable<HljHttpResult> deleteCard(@Url String url, @QueryMap Map<String, Object> map);

    /**
     * 获得请帖音乐
     *
     * @param url    p/wedding/index.php/home/APIInvitationV3/card_music
     * @param cardId
     * @return
     */
    @GET
    Observable<HljHttpResult<HljHttpData<List<Music>>>> getCardMusic(
            @Url String url, @Query("card_id") long cardId);

    /**
     * 模板详情
     *
     * @param url p/wedding/index.php/home/APIInvitationV3/theme
     * @param id  模板id
     * @return
     */
    @GET
    Observable<HljHttpResult<Theme>> getTheme(@Url String url, @Query("theme_id") long id);


    /**
     * 字体列表
     *
     * @param url p/wedding/index.php/Home/APIInvationV2/fontList
     * @return
     */
    @GET
    Observable<HljHttpResult<JsonArray>> getFonts(@Url String url);


    /**
     * 创建/编辑请帖
     *
     * @param url  p/wedding/index.php/home/APIInvitationV3/edit_card
     * @param card
     * @return
     */
    @POST
    Observable<HljHttpResult<Card>> editCard(@Url String url, @Body JsonElement card);


    /**
     * 我的请帖列表
     *
     * @param url p/wedding/index.php/home/APIInvitationV3/card_list
     * @return
     */
    @GET
    Observable<HljHttpResult<CardListData>> getCardList(@Url String url);


    /**
     * 请帖详情
     *
     * @param url p/wedding/index.php/home/APIInvitationV3/card
     * @param id  请帖id
     * @return
     */
    @GET
    Observable<HljHttpResult<Card>> getCard(@Url String url, @Query("card_id") long id);


    /**
     * 创建/编辑单页
     *
     * @param url  p/wedding/index.php/home/APIInvitationV3/edit_card_page
     * @param page
     * @return
     */
    @POST
    Observable<HljHttpResult<PageEditResult>> editPage(@Url String url, @Body PostPageBody page);

    /**
     * 分享成功后解锁
     *
     * @param url p/wedding/index.php/Home/APIInvationV2/unlockTheme
     * @return
     */
    @POST
    Observable<HljHttpResult> unlockTheme(@Url String url);


    /**
     * 页面排序
     *
     * @param url       p/wedding/index.php/Home/APIInvationV2/changePosition
     * @param pageOrder
     * @return
     */
    @POST
    Observable<HljHttpResult<JsonElement>> changePagePosition(
            @Url String url, @Body JsonObject pageOrder);

    /**
     * 发送
     *
     * @param url  p/wedding/index.php/home/APIInvitationV3/send_card
     * @param body
     * @return
     */
    @POST
    Observable<HljHttpResult> sendCard(@Url String url, @Body SendCardBody body);

    /**
     * 获取分享信息
     *
     * @param url    p/wedding/index.php/home/APIInvitationV3/share_info
     * @param cardId
     * @return
     */
    @GET
    Observable<HljHttpResult<MinProgramShareInfo>> getShareInfo(
            @Url String url, @Query("card_id") long cardId);

    /**
     * 请帖礼物显示隐藏
     *
     * @param url p/wedding/index.php/Home/APICardGift2Recv/hidden
     * @param map
     * @return
     */
    @POST
    Observable<HljHttpResult> hiddenCardGift(@Url String url, @Body Map<String, Object> map);


    /***
     * 当前用户是否可使用模板
     *
     * @param url /p/wedding/index.php/Home/APIInvitationV3/check_theme
     */
    @GET
    Observable<HljHttpResult> chechTheme(
            @Url String url, @Query("theme_id") long cardId);

    /**
     * 获取请帖VIP权限标志位
     *
     * @param url
     * @return
     */
    @GET
    Observable<HljHttpResult<JsonElement>> getPrivilegeFlag(@Url String url);

    /**
     * @param url     p/wedding/index.php/home/APIInvitationV3/checkNameModifyState
     * @param card_id
     * @return
     */
    @GET
    Observable<HljHttpResult<ModifyNameResult>> checkNameModifyState(
            @Url String url, @Query("card_id") long card_id);

    /**
     * 上传修改姓名的资料
     *
     * @param url p/wedding/index.php/home/APIInvitationV3/modifyName
     * @param map
     * @return
     */
    @POST
    Observable<HljHttpResult<Object>> postModifyName(
            @Url String url, @Body Map<String, Object> map);
}
