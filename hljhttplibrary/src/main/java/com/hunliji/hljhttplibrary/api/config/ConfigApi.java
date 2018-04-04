package com.hunliji.hljhttplibrary.api.config;

import android.util.Base64;

import com.google.gson.JsonElement;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.io.IOException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2018/2/24.
 */

public class ConfigApi {

    public static Observable<String> getConfig(
            String dataId, String group) {
        return HljHttp.getRetrofit()
                .create(ConfigService.class)
                .getConfig(dataId, group)
                .map(new HljHttpResultFunc<JsonElement>())
                .map(new Func1<JsonElement, String>() {
                    @Override
                    public String call(JsonElement jsonElement) {
                        return jsonElement.toString();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    private static String HmacSHA1Encrypt(
            String namespace, String group, String timeStamp, String encryptKey) throws Exception {
        String encryptText = namespace + "+" + group + "+" + timeStamp;
        byte[] data = encryptKey.getBytes("UTF-8");
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
        // 生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance("HmacSHA1");
        // 用给定密钥初始化 Mac 对象
        mac.init(secretKey);
        byte[] text = mac.doFinal(encryptText.getBytes());
        // 完成 Mac 操作, base64编码，将byte数组转换为字符串
        return new String(Base64.encode(text, Base64.NO_WRAP));
    }
}
