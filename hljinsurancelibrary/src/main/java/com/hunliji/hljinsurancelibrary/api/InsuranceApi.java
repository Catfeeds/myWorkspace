package com.hunliji.hljinsurancelibrary.api;

import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.api.FileService;
import com.hunliji.hljhttplibrary.entities.HljHttpPosterData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.hljinsurancelibrary.models.MyPolicy;
import com.hunliji.hljinsurancelibrary.models.PolicyDetail;
import com.hunliji.hljinsurancelibrary.models.PostHlbPolicy;
import com.hunliji.hljinsurancelibrary.models.wrappers.HljHttpInsuranceProductsData;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 红包、优惠券Api
 * Created by chen_bin on 2016/10/15 0015.
 */

public class InsuranceApi {

    /**
     * 获取保险产品列表
     */
    public static Observable<HljHttpInsuranceProductsData> getInsuranceProductsObb() {
        return HljHttp.getRetrofit()
                .create(InsuranceService.class)
                .getInsuranceProducts()
                .map(new HljHttpResultFunc<HljHttpInsuranceProductsData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 我的保单列表
     */
    public static Observable<HljHttpPosterData<List<MyPolicy>>> getMyInsurance(
            String tab, int page) {
        return HljHttp.getRetrofit()
                .create(InsuranceService.class)
                .getMyInsurance(tab, page, 20)
                .map(new HljHttpResultFunc<HljHttpPosterData<List<MyPolicy>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 保单详情
     */
    public static Observable<PolicyDetail> getPolicyDetail(String id) {
        return HljHttp.getRetrofit()
                .create(InsuranceService.class)
                .getPolicyDetail(id)
                .map(new HljHttpResultFunc<PolicyDetail>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 填写婚礼保保单
     */
    public static Observable<HljHttpResult> fillHlbObb(PostHlbPolicy postHlbPolicy) {
        return HljHttp.getRetrofit()
                .create(InsuranceService.class)
                .fillHlb(postHlbPolicy)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 下载保单
     *
     * @param url
     * @param filePath
     * @return
     */
    public static Observable<String> download(String url, final String filePath) {
        return HljHttp.getRetrofit()
                .create(FileService.class)
                .download(url)
                .flatMap(new Func1<Response<ResponseBody>, Observable<String>>() {
                    @Override
                    public Observable<String> call(
                            final Response<ResponseBody> response) {
                        return Observable.create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                try {
                                    Headers headers = response.headers();
                                    String contentType = headers.get("Content-Type");
                                    File file = new File(filePath);
                                    BufferedSink sink = Okio.buffer(Okio.sink(file));
                                    sink.writeAll(response.body()
                                            .source());
                                    sink.close();
                                    if (contentType != null && contentType.toLowerCase()
                                            .endsWith("pdf")) {
                                        subscriber.onNext(filePath);
                                    } else {
                                        FileUtil.deleteFile(file);
                                        subscriber.onNext("");
                                    }
                                    subscriber.onCompleted();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                }
                            }
                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 填写蜜月宝保单
     * certi_no	新郎身份证号码
     * full_name	新郎姓名
     * id
     * phone	新郎手机
     * spouse_certi_no	新娘身份证
     * spouse_name	新娘姓名
     * spouse_phone	新娘电话
     * wedding_date	投保生效开始时间
     *
     * @return
     */
    public static Observable<HljHttpResult> fillMyb(Map<String, Object> body) {
        return HljHttp.getRetrofit()
                .create(InsuranceService.class)
                .fillMyb(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 蜜月保（保单详情）
     *
     * @return
     */
    public static Observable<PolicyDetail> getMybDetail(String id) {
        return HljHttp.getRetrofit()
                .create(InsuranceService.class)
                .getMybDetail(id)
                .map(new HljHttpResultFunc<PolicyDetail>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}