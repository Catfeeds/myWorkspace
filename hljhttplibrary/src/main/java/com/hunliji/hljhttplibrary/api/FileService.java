package com.hunliji.hljhttplibrary.api;

import com.google.gson.JsonObject;
import com.hunliji.hljhttplibrary.entities.BlockUploadResult;
import com.hunliji.hljhttplibrary.entities.HljUploadResult;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Suncloud on 2016/8/24.
 */
public interface FileService {


    @GET
    Observable<JsonObject> getToken(@Url String url);


    @POST("http://up.qiniu.com")
    @Multipart
    Observable<HljUploadResult> uploadFile(
            @Part("token") RequestBody token,
            @Part MultipartBody.Part filePart,
            @Part("key") RequestBody key);


    @GET
    Observable<Response<ResponseBody>> download(@Url String url);


    @POST("http://up.qiniu.com/mkblk/{blockSize}")
    Observable<BlockUploadResult> makeBlock(
            @Path("blockSize") long blockSize,
            @Body RequestBody body,
            @HeaderMap Map<String, String> headers);


    @POST("http://up.qiniu.com/bput/{uploadContext}/{chunkOffset}")
    Observable<BlockUploadResult> uploadChunk(
            @Path("uploadContext") String uploadContext,
            @Path("chunkOffset") long chunkOffset,
            @Body RequestBody body,
            @HeaderMap Map<String, String> headers);



    @POST("http://up.qiniu.com/mkfile/{fileSize}/key/{encodedKey}")
    Observable<HljUploadResult> makeFile(
            @Path("fileSize") long fileSize,
            @Path("encodedKey") String key,
            @Body RequestBody body,
            @HeaderMap Map<String, String> headers);

    @POST("http://up.qiniu.com/mkfile/{fileSize}")
    Observable<HljUploadResult> makeFile(
            @Path("fileSize") long fileSize,
            @Body RequestBody body,
            @HeaderMap Map<String, String> headers);
}
