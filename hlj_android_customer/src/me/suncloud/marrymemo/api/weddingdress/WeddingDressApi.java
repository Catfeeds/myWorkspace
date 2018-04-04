package me.suncloud.marrymemo.api.weddingdress;

import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import me.suncloud.marrymemo.model.weddingdress.WeddingCommentBody;
import me.suncloud.marrymemo.model.weddingdress.WeddingInfoBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2017/5/8.
 * 晒婚纱照
 */

public class WeddingDressApi {


    /**
     * 填写晒婚纱的商家城市拍摄金额等
     *
     * @param weddingInfoPost
     * @return
     */
    public static Observable postWeddingInfo(WeddingInfoBody weddingInfoPost) {
        return HljHttp.getRetrofit()
                .create(WeddingDressService.class)
                .postWeddingInfo(weddingInfoPost)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 晒婚纱照大图预览评价话题
     * @param weddingCommentBody
     * @return
     */
    public static Observable postWeddingPhotoComment(WeddingCommentBody weddingCommentBody) {
        return HljHttp.getRetrofit()
                .create(WeddingDressService.class)
                .postWeddingPhotoComment(weddingCommentBody)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
