package com.hunliji.hljhttplibrary.entities;

import android.text.TextUtils;

/**
 * Created by werther on 16/7/25.
 * 婚礼纪接口返回的错误报告的时候当做网络请求异常处理,默认就使用status中的msg作为异常提示信息
 * 默认不需要做特殊处理,当异常提示信息需要根据特殊处理时,可在这里判断status retCode进行相应处理
 */
public class HljApiException extends RuntimeException {


    public HljApiException(
            String detailMessage) {
        super(detailMessage);
    }

    public HljApiException(HljHttpStatus status) {
        this(getExceptionsMsg(status));
    }

    public static String getExceptionsMsg(HljHttpStatus status) {
        String msg;
        switch (status.getRetCode()) {
            // 在这里添加特殊处理
            default:
                if (TextUtils.isEmpty(status.getMsg())) {
                    msg = "未知错误";
                } else {
                    msg = status.getMsg();
                }
                break;
        }

        return msg;
    }
}
