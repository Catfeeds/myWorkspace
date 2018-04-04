package me.suncloud.marrymemo.api.bindpartner;

import com.hunliji.hljcommonlibrary.models.userprofile.PartnerInvitation;
import com.hunliji.hljcommonlibrary.models.userprofile.UserPartnerWrapper2;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import me.suncloud.marrymemo.model.bindpartner.BindAgreePostBody;
import me.suncloud.marrymemo.model.bindpartner.PartnerBindResult;
import me.suncloud.marrymemo.model.bindpartner.PartnerPostBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by werther on 16/8/22.
 */
public interface PartnerService {

    @POST("p/wedding/index.php/Home/APIUserPartner/addPartner")
    Observable<HljHttpResult<PartnerBindResult>> bindPartner(@Body PartnerPostBody body);

    @POST("p/wedding/index.php/Home/APIUserPartner/agree")
    Observable<HljHttpResult<Object>> agreeBindPartner(
            @Body BindAgreePostBody postBody);

    @GET("p/wedding/index.php/Home/APIUserPartner/partner")
    Observable<HljHttpResult<UserPartnerWrapper2>> getUserPartnerInfo();

    /**
     * 获取最新的邀请（我发出的或者邀请我的）
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APIUserPartner/status")
    Observable<HljHttpResult<PartnerInvitation>> getPartnerInvitation();


    /**
     * 解除另一半绑定
     *
     * @return
     */
    @POST("p/wedding/index.php/Home/APIUserPartner/unbind")
    Observable<HljHttpResult<Object>> unBindPartner();
}

