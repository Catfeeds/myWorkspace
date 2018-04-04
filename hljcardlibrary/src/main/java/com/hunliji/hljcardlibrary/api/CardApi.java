package com.hunliji.hljcardlibrary.api;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcardlibrary.models.CardHideStatus;
import com.hunliji.hljcardlibrary.models.CardPage;
import com.hunliji.hljcardlibrary.models.CardSetupStatus;
import com.hunliji.hljcardlibrary.models.ChooseTheme;
import com.hunliji.hljcardlibrary.models.EditAudioBody;
import com.hunliji.hljcardlibrary.models.Font;
import com.hunliji.hljcardlibrary.models.ModifyNameResult;
import com.hunliji.hljcardlibrary.models.Music;
import com.hunliji.hljcardlibrary.models.SendCardBody;
import com.hunliji.hljcardlibrary.models.SingleTemplate;
import com.hunliji.hljcardlibrary.models.Theme;
import com.hunliji.hljcardlibrary.models.wrappers.CardListData;
import com.hunliji.hljcardlibrary.models.wrappers.PageEditResult;
import com.hunliji.hljcardlibrary.models.wrappers.PostCardBody;
import com.hunliji.hljcardlibrary.models.wrappers.PostPageBody;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.MinProgramShareInfo;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 请帖api
 * Created by mo_yu on 2017/06/13 0015.
 */
public class CardApi {


    /**
     * 请帖设置信息
     *
     * @param cardId 请帖id
     * @return
     */
    public static Observable getCardSetupStatusObb(long cardId) {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .getCardSetupStatus(HljCard.getCardUrl(
                        "p/wedding/index.php/home/APICardSetup/status"), cardId)
                .map(new HljHttpResultFunc<CardSetupStatus>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 请帖设置
     *
     * @param map
     * @return
     */
    public static Observable<HljHttpResult> postCardSetupObb(Map<String, Object> map) {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .postCardSetup(HljCard.getCardUrl("p/wedding/index.php/home/APICardSetup/set"), map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 复制请帖
     *
     * @param cardId 请帖id
     * @return
     */
    public static Observable<JsonElement> copyCardObb(long cardId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("card_id", cardId);
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .copyCard(HljCard.getCardUrl("p/wedding/index.php/home/APIInvitationV3/copy_card"),
                        map)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * @param id   id 模板id
     * @param type single_img / two_img / multi_img / text / video
     * @return 增加video 节点 返回视频单页列表 新接口
     */
    public static Observable<SingleTemplate> getPageTemplateList(long id, String type) {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .getPageTemplateList(HljCard.getCardUrl(
                        "p/wedding/index.php/home/APIInvitationV3/page_template_list"), id, type)
                .map(new HljHttpResultFunc<SingleTemplate>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 标签列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<Mark>>> getCardMarks() {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .getCardMarks(HljCard.getCardUrl(
                        "p/wedding/index.php/home/APIInvitationV3/card_marks"))
                .map(new HljHttpResultFunc<HljHttpData<List<Mark>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 模板列表
     *
     * @return
     */
    public static Observable<ChooseTheme> getThemeList(int isMember, long markId) {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .getThemeList(HljCard.getCardUrl(
                        "p/wedding/index.php/home/APIInvitationV3/theme_list"), isMember, markId)
                .map(new HljHttpResultFunc<ChooseTheme>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 模板 主题分享
     *
     * @return
     */
    public static Observable<ShareInfo> getAppShareInfo() {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .getAppShareInfo(HljCard.getCardUrl("p/wedding/Home/APISetting/app_share"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 隐藏/显示致宾客页
     *
     * @param cardPageId
     * @return
     */
    public static Observable<CardHideStatus> postHiddenCardPage(long cardPageId, int hidden) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("card_page_id", cardPageId);
        jsonObject.addProperty("hidden", hidden);
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .postHiddenCardPage(HljCard.getCardUrl(
                        "p/wedding/index.php/home/APIInvitationV3/hidden_card_page"), jsonObject)
                .map(new HljHttpResultFunc<CardHideStatus>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除单页
     *
     * @param cardPageId
     * @return
     */
    public static Observable deleteCardPage(long cardPageId) {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .deleteCardPage(HljCard.getCardUrl(
                        "p/wedding/index.php/home/APIInvitationV3/card_page"), cardPageId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 删除或关闭请帖
     *
     * @param cardId 请帖id
     * @return
     */
    public static Observable deleteOrCloseCardObb(long cardId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("card_id", cardId);
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .deleteOrCloseCard(HljCard.getCardUrl(
                        "p/wedding/index.php/home/APIInvitationV3/close_card"), map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 开启请帖
     *
     * @param cardId 请帖id
     * @return
     */
    public static Observable openCardObb(long cardId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("card_id", cardId);
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .openCard(HljCard.getCardUrl("p/wedding/index.php/home/APIInvitationV3/open_card"),
                        map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除请帖(新)
     *
     * @param cardId 请帖id
     * @return
     */
    public static Observable deleteCardObb(long cardId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("card_id", cardId);
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .deleteCard(HljCard.getCardUrl("p/wedding/index.php/home/APIInvitationV3/card"),
                        map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 请帖音乐标签
     *
     * @return
     */
    public static Observable<HljHttpData<List<Mark>>> getMusicMark() {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .getMusicMark(HljCard.getCardUrl(
                        "p/wedding/index.php/home/APIInvitationV3/music_marks"))
                .map(new HljHttpResultFunc<HljHttpData<List<Mark>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得某个标签音乐
     *
     * @param markId
     * @return
     */
    public static Observable<HljHttpData<List<Music>>> getMusicList(long markId, int page) {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .getMusicList(HljCard.getCardUrl(
                        "p/wedding/index.php/home/APIInvitationV3/card_music_list"),
                        markId,
                        page,
                        HljCommon.PER_PAGE)
                .map(new HljHttpResultFunc<HljHttpData<List<Music>>>())
                .map(new Func1<HljHttpData<List<Music>>, HljHttpData<List<Music>>>() {
                    @Override
                    public HljHttpData<List<Music>> call(HljHttpData<List<Music>> listHljHttpData) {
                        try {
                            for (Music music : listHljHttpData.getData()) {
                                music.setId(null);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return listHljHttpData;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 编辑请帖音乐信息
     *
     * @param body
     * @return
     */
    public static Observable<HljHttpResult> editAudioInfo(EditAudioBody body) {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .editAudioInfo(HljCard.getCardUrl(
                        "p/wedding/index.php/Home/APIInvitationV3/edit_audios"), body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得请帖音乐
     *
     * @param cardId
     * @return
     */
    public static Observable<HljHttpData<List<Music>>> getCardMusic(long cardId) {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .getCardMusic(HljCard.getCardUrl(
                        "p/wedding/index.php/home/APIInvitationV3/card_music"), cardId)
                .map(new HljHttpResultFunc<HljHttpData<List<Music>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 模板详情
     *
     * @param id 模板id
     */
    public static Observable<Theme> getTheme(long id) {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .getTheme(HljCard.getCardUrl("p/wedding/index.php/home/APIInvitationV3/theme"), id)
                .map(new HljHttpResultFunc<Theme>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 字体列表
     */
    public static Observable<List<Font>> getFont(final Context context) {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .getFonts(HljCard.getCardUrl("p/wedding/index.php/Home/APIInvationV2/fontList"))
                .map(new HljHttpResultFunc<JsonArray>())
                .map(new Func1<JsonArray, List<Font>>() {
                    @Override
                    public List<Font> call(JsonArray jsonArray) {
                        List<Font> fonts = GsonUtil.getGsonInstance()
                                .fromJson(jsonArray, new TypeToken<List<Font>>() {}.getType());
                        if (!CommonUtil.isCollectionEmpty(fonts)) {
                            try {
                                //保存数据
                                OutputStreamWriter out = new OutputStreamWriter(context
                                        .openFileOutput(
                                        HljCard.FONTS_FILE,
                                        Context.MODE_PRIVATE));
                                out.write(jsonArray.toString());
                                out.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return fonts;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 创建/编辑请帖
     *
     * @return
     */
    public static Observable<Card> editCard(PostCardBody postCardBody) {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .editCard(HljCard.getCardUrl("p/wedding/index.php/home/APIInvitationV3/edit_card"),
                        GsonUtil.getGsonInstance()
                                .toJsonTree(postCardBody))
                .map(new HljHttpResultFunc<Card>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 我的请帖列表
     *
     * @return
     */
    public static Observable<CardListData> getCardList() {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .getCardList(HljCard.getCardUrl("p/wedding/index" + "" + "" + "" + "" + "" + "" +
                        ".php/home/APIInvitationV3/card_list"))
                .map(new HljHttpResultFunc<CardListData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 请帖详情
     *
     * @param id 请帖id
     * @return
     */
    public static Observable<Card> getCard(long id) {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .getCard(HljCard.getCardUrl("p/wedding/index.php/home/APIInvitationV3/card"), id)
                .map(new HljHttpResultFunc<Card>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 创建/编辑单页
     *
     * @param cardPage
     * @return
     */
    public static Observable<PageEditResult> editPage(CardPage cardPage) {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .editPage(HljCard.getCardUrl(
                        "p/wedding/index.php/home/APIInvitationV3/edit_card_page"),
                        new PostPageBody(cardPage))
                .map(new HljHttpResultFunc<PageEditResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<HljHttpResult> unlockTheme() {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .unlockTheme(HljCard.getCardUrl("p/wedding/index" + "" + "" + "" + "" + "" + "" +
                        ".php/Home/APIInvationV2/unlockTheme"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<JsonElement> changePagePosition(List<Long> ids) {
        JsonObject sortObject = new JsonObject();
        JsonArray array = new JsonArray();
        for (Long cardId : ids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", cardId);
            jsonObject.addProperty("position", ids.indexOf(cardId) + 1);
            array.add(jsonObject);
        }
        sortObject.add("pageOrders", array);
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .changePagePosition(HljCard.getCardUrl(
                        "p/wedding/index.php/Home/APIInvationV2/changePosition"), sortObject)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 发送
     *
     * @param body
     * @return
     */
    public static Observable<HljHttpResult> sendCard(SendCardBody body) {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .sendCard(HljCard.getCardUrl("p/wedding/index.php/home/APIInvitationV3/send_card"),
                        body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取分享
     */
    public static Observable<MinProgramShareInfo> getShareInfo(long cardId) {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .getShareInfo(HljCard.getCardUrl(
                        "p/wedding/index.php/home/APIInvitationV3/share_info"), cardId)
                .map(new HljHttpResultFunc<MinProgramShareInfo>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 请帖礼物显示隐藏
     *
     * @param id     礼物id
     * @param hidden 当前状态
     * @return
     */
    public static Observable hiddenCardGiftObb(long id, boolean hidden) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("hidden", hidden);
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .hiddenCardGift(HljCard.getCardUrl(
                        "p/wedding/index.php/Home/APICardGift2Recv/hidden"), map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 当前用户是否可使用模板
     *
     * @param themeId theme_id
     * @return
     */
    public static Observable<HljHttpResult> checkTheme(long themeId) {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .chechTheme(HljCard.getCardUrl(
                        "/p/wedding/index.php/Home/APIInvitationV3/check_theme"), themeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取请帖VIP权限标志位
     *
     * @return
     */
    public static Observable<String> getPrivilegeFlag() {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .getPrivilegeFlag(HljCard.getCardUrl("p/wedding/index.php/Home/APIUser/privilege"))
                .map(new HljHttpResultFunc<JsonElement>())
                .map(new Func1<JsonElement, String>() {
                    @Override
                    public String call(JsonElement jsonElement) {
                        return CommonUtil.getAsString(jsonElement, "hlj_member_privilege");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 检测改名状态
     *
     * @param cardId
     * @return
     */
    public static Observable<ModifyNameResult> checkNameModifyState(long cardId) {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .checkNameModifyState(HljCard.getCardUrl(
                        "p/wedding/index.php/home/APIInvitationV3/checkNameModifyState"), cardId)
                .map(new HljHttpResultFunc<ModifyNameResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 修改姓名资料上传
     * @param cardId
     * @param brideName
     * @param groomName
     * @param docType
     * @param groomPhoto
     * @param bridePhoto
     * @return
     */
    public static Observable<Object> postModifyName(
            long cardId,
            String brideName,
            String groomName,
            int docType,
            String groomPhoto,
            String bridePhoto) {
        Map<String, Object> map = new HashMap<>();
        map.put("card_id", cardId);
        map.put("bride_new", brideName);
        map.put("groom_new", groomName);
        map.put("document_type", docType);
        map.put("groom_photo", groomPhoto);
        map.put("bride_photo", bridePhoto);
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .postModifyName(HljCard.getCardUrl(
                        "p/wedding/index.php/home/APIInvitationV3/modifyName"), map)
                .map(new HljHttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
