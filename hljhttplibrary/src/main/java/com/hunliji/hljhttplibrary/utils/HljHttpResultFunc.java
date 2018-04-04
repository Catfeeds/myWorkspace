package com.hunliji.hljhttplibrary.utils;


import com.hunliji.hljcommonlibrary.models.MaintainEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljhttplibrary.entities.HljApiException;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import retrofit2.adapter.rxjava.HttpException;
import rx.functions.Func1;

/**
 * Created by werther on 16/7/25.
 * 用来统一处理Http的retCode,并将HttpResult的Data部分剥离出来返回给subscriber
 *
 * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
 */
public class HljHttpResultFunc<T> implements Func1<HljHttpResult<T>, T> {

    @Override
    public T call(HljHttpResult<T> tHljHttpResult) {
        if (tHljHttpResult.getStatus()
                .getRetCode() != 0) {
            if (tHljHttpResult.getStatus().getRetCode() == 291) {
                RxBus.getDefault()
                        .post(new MaintainEvent(MaintainEvent.EventType.USER_TOKEN_ERROR,
                                System.currentTimeMillis()));
            }
            throw new HljApiException(tHljHttpResult.getStatus());
        }
        if (tHljHttpResult.getCurrentTime() > 0) {
            HljTimeUtils.setTimeOffset(tHljHttpResult.getCurrentTime() * 1000);
        }

        return tHljHttpResult.getData();
    }
}
