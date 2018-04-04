package me.suncloud.marrymemo.api.kepler;

import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljResultAction;

import me.suncloud.marrymemo.model.kepler.Kepler;
import me.suncloud.marrymemo.model.kepler.KeplerBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 平安普惠
 * Created by jinxin on 2016/11/4.
 */

public interface KeplerService {
    /**
     * 平安普惠 提交用户信息
     * @param body
     * @return
     */
    @POST("/p/wedding/index.php/home/APILoanApplication/apply")
    Observable<HljHttpResult<HljResultAction>> applyKepler(@Body KeplerBody body);

    /**
     * 获得用户信息
     * @return
     */
    @GET("/p/wedding/index.php/home/APILoanApplication/lastApply")
    Observable<HljHttpResult<Kepler>> lastApply();

}
