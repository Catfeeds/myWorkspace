package com.hunliji.hljtrackerlibrary.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Suncloud on 2016/8/1.
 */
public interface TrackerService {

    @POST("http://www.hunliji.com/v1/api/app/tracker/batch.json")
    Observable<JsonElement> postTrackers(@Body JsonElement trackers);

    @POST("http://www.hunliji.com/v2/api/app/tracker/batch.json")
    Observable<JsonElement> postTrackersV2(@Body JsonElement trackers);


    /**
     * <a href="http://doc.hunliji.com/workspace/myWorkspace.do?projectId=15#3002">分享采集</a>
     *
     * @return
     */
    @POST("p/wedding/index.php/home/APIMerchantPotentialCustomer/share")
    Observable<HljHttpResult<JsonElement>> postShareAction(@Body JsonObject jsonObject);



    @GET
    Observable<Response<ResponseBody>> miaoZhenTracker(@Url String url);
}
