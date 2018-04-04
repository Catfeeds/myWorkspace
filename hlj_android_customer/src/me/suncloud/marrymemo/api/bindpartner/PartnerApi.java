package me.suncloud.marrymemo.api.bindpartner;

import com.hunliji.hljcommonlibrary.models.userprofile.PartnerInvitation;
import com.hunliji.hljcommonlibrary.models.userprofile.UserPartnerWrapper2;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import me.suncloud.marrymemo.model.bindpartner.BindAgreePostBody;
import me.suncloud.marrymemo.model.bindpartner.PartnerBindResult;
import me.suncloud.marrymemo.model.bindpartner.PartnerPostBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by werther on 16/8/22.
 * 绑定伴侣相关的API
 */
public class PartnerApi {

    /**
     * 绑定伴侣 post方法
     *
     * @param body PartnerPostBody
     * @return
     */
    public static Observable<PartnerBindResult> getPostBindPartnerObb(PartnerPostBody body) {
        return HljHttp.getRetrofit()
                .create(PartnerService.class)
                .bindPartner(body)
                .map(new HljHttpResultFunc<PartnerBindResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 统一结伴 post方法
     *
     * @param partnerId 伴侣的user id
     * @return
     */
    public static Observable getPostAgreeBindPartnerObb(long partnerId) {
        BindAgreePostBody body = new BindAgreePostBody(partnerId);
        return HljHttp.getRetrofit()
                .create(PartnerService.class)
                .agreeBindPartner(body)
                .map(new HljHttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取用户和伴侣的个人简略信息
     *
     * @return
     */
    public static Observable<UserPartnerWrapper2> getUserPartnerInfoObb() {
        return HljHttp.getRetrofit()
                .create(PartnerService.class)
                .getUserPartnerInfo()
                .map(new HljHttpResultFunc<UserPartnerWrapper2>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取最新的邀请（我发出的或者邀请我的）
     *
     * @return
     */
    public static Observable<PartnerInvitation> getPartnerInvitationStatusObb() {
        return HljHttp.getRetrofit()
                .create(PartnerService.class)
                .getPartnerInvitation()
                .map(new HljHttpResultFunc<PartnerInvitation>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Object> unBindPartner() {
        return HljHttp.getRetrofit()
                .create(PartnerService.class)
                .unBindPartner()
                .map(new HljHttpResultFunc<Object>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
