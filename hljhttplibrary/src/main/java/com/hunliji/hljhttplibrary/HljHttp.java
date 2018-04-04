package com.hunliji.hljhttplibrary;

import android.content.Context;
import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljhttplibrary.authorization.UserConverterDelegate;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpHeader;
import com.hunliji.hljhttplibrary.entities.HljHttpHeaderBase;
import com.hunliji.hljhttplibrary.utils.HljRetrofitBuilder;
import com.hunliji.hljhttplibrary.utils.UriUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import static com.hunliji.hljhttplibrary.utils.HljRetrofitBuilder.OK_CACHE_DIR;

/**
 * Created by werther on 16/7/22.
 * HljHttp这个类库中的一些Constants参数
 * 和一些常用的,暴露给使用者的静态方法
 */
public class HljHttp {

    public static final String TAG = HljHttp.class.getSimpleName();
    public static boolean debug = true;

    private static Context mContext;
    private static Retrofit retrofit;
    private static OkHttpClient okHttpClient;
    private static String HOST = "http://www.hunliji.com/";
    private static HljHttpHeaderBase hljHttpHeaderBase;

    public class TimeFormatPattern {
        public static final String PATTERN_1 = "yyyy-MM-dd HH:mm:ss";
        public static final String PATTERN_2 = "MM-dd HH:mm";
    }

    /**
     * HljHttp模块初始化
     * 任何使用到该模块的地方都要确保先进行正确的初始化工作
     * 如果需要自定义http header帮助类，则用这个版本，指明特定的HljHttpHeaderBase
     *
     * @param debug             debug开关
     * @param host              HljHttp的全局host
     * @param converterDelegate 登陆用户信息的转换代理方法的实现, 目前有CustomerUserConverter和MerchantUserConverter两种实现
     * @param hljHttpHeaderBase 需要自定义的http header帮助类，用于获取http header
     */
    public static OkHttpClient init(
            Context context,
            boolean debug,
            String host,
            UserConverterDelegate converterDelegate,
            HljHttpHeaderBase hljHttpHeaderBase) {
        HljHttp.debug = debug;
        HljHttp.HOST = host;
        UserSession.getInstance()
                .registerConverter(converterDelegate);
        HljHttp.mContext = context;
        HljHttp.hljHttpHeaderBase = hljHttpHeaderBase;
        build();

        return okHttpClient;
    }

    private static void build() {
        HljRetrofitBuilder builder = new HljRetrofitBuilder(mContext, hljHttpHeaderBase);
        retrofit = builder.build();
        okHttpClient = builder.getOkHttpClient();
    }

    /**
     * HljHttp模块初始化
     * 任何使用到该模块的地方都要确保先进行正确的初始化工作
     * 不需要自定义http header的版本，使用默认的http header生成方法，支持婚礼纪用户端和商家端
     *
     * @param context
     * @param debug
     * @param host
     * @param converterDelegate
     */
    public static OkHttpClient init(
            Context context, boolean debug, String host, UserConverterDelegate converterDelegate) {
        HljHttpHeader hljHttpHeader = new HljHttpHeader(context);
        return init(context, debug, host, converterDelegate, hljHttpHeader);
    }


    /**
     * 获取全局通用的retrofit实例
     *
     * @return
     */
    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            build();
        }
        return retrofit;
    }

    public static OkHttpClient rebuildRetrofit() {
        build();

        return okHttpClient;
    }

    /**
     * 设置HljHttp库的debug开关
     *
     * @param debug
     */
    public static void setDebug(boolean debug) {
        HljHttp.debug = debug;
    }

    /**
     * 获取Host地址,所有需要获得这个地址的地方都应该直接从这里获取
     * 全局配置
     */
    public static String getHOST() {
        return HOST;
    }

    /**
     * 修改HljHttp中的host配置,可以运行时修改
     * 修改host之后必须重建retrofit
     *
     * @param HOST
     */
    public static void setHOST(String HOST) {
        HljHttp.HOST = HOST;
        build();
    }

    /**
     * 清除缓存
     *
     * @param context
     */
    public static void clearCache(Context context) {
        try {
            File file = new File(context.getExternalCacheDir(), OK_CACHE_DIR);
            FileUtil.deleteFolder(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAuthorization(String method, HttpUrl url, String string, String timestamp) {
        String AUTH_TYPE = "HUNLIJI ";
        String SHARED_CLIENT_SECRET = "196702C9-2171-4AA2-A101-6B4C304DC9B6";
        String CLIENT_ID = "49F14C2D-F0F9-4BB2-BC49-B6159CBDEAA2";

        StringBuilder authorization = new StringBuilder(AUTH_TYPE);
        StringBuilder sha1Str = new StringBuilder(method).append(url.uri()
                .getPath());
        String query = url.query();
        if (!TextUtils.isEmpty(query)) {
            sha1Str.append("?")
                    .append(query);
        }
        if (!TextUtils.isEmpty(string)) {
            sha1Str.append(string);
        }

        sha1Str.append(CLIENT_ID)
                .append(SHARED_CLIENT_SECRET)
                .append(timestamp);
        try {
            authorization.append(UriUtil.SHA1(sha1Str.toString()));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        authorization.append(":")
                .append(CLIENT_ID);
        return authorization.toString();
    }
}
