package me.suncloud.marrymemo.api.weddingdress;

import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import me.suncloud.marrymemo.model.weddingdress.WeddingCommentBody;
import me.suncloud.marrymemo.model.weddingdress.WeddingInfoBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by hua_rong on 2017/5/8.
 * 晒婚纱照
 */

public interface WeddingDressService {

    /**
     * 填写晒婚纱的商家城市拍摄金额等
     *
     * @return
     */
    @POST("/p/wedding/Home/APICommunityThread/modify_wedding_photo")
    Observable<HljHttpResult> postWeddingInfo(@Body WeddingInfoBody body);

    /**
     * 晒婚纱照大图预览评价话题
     * @return
     */
    @POST("p/wedding/Home/APICommunityPost/CreateNewPostV2")
    Observable<HljHttpResult> postWeddingPhotoComment(@Body WeddingCommentBody body);


}
