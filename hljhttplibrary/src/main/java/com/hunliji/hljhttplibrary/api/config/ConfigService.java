package com.hunliji.hljhttplibrary.api.config;

import com.google.gson.JsonElement;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by wangtao on 2018/2/24.
 */

public interface ConfigService {


    @GET("http://{host}:8080/diamond-server/diamond")
    Observable<Response<ResponseBody>> getServerIp(@Path("host") String host);


    @GET("p/wedding/index.php/Home/Index/ACM")
    Observable<HljHttpResult<JsonElement>> getConfig(
            @Query("dataId") String dataId,
            @Query("group") String group);
}
