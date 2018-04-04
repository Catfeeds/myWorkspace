package com.hunliji.hljquestionanswer;


import android.content.Context;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

/**
 * Created by mo_yu on 16/8/16.
 * HljQuestionAnswer这个类库中的一些Constants参数
 * 和一些常用的,暴露给使用者的静态方法
 */
public class HljQuestionAnswer {
    public static final String DATE_FORMAT_SHORT = "yyyy-MM-dd";

    //举报类型 thread:话题 post:回帖 question:问题 answer:回答
    public final static String REPORT_THREAD = "thread";
    public final static String REPORT_POST = "post";
    public final static String REPORT_QUESTION = "question";
    public final static String REPORT_ANSWER = "answer";
    public final static String REPORT_COMMENT = "comment";

    public final static String ANSWER_DETAIL_HINT = "answer_detail_hint1";


    public static boolean isMerchant(Context context) {
        if (CommonUtil.getAppType() == CommonUtil.PacketType.MERCHANT) {
            return true;
        }else {
            return false;
        }
    }

    public static class RequestCode {
    }
}
