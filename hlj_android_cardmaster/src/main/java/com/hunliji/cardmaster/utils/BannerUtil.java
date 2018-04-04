package com.hunliji.cardmaster.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.cardmaster.activities.OpenMemberActivity;
import com.hunliji.hljcardcustomerlibrary.views.activities.BalanceActivity;
import com.hunliji.hljcardcustomerlibrary.views.activities.CardListActivity;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.AuthUtil;

import org.json.JSONObject;

/**
 * Created by wangtao on 2017/11/29.
 */

public class BannerUtil {

    private static final int WEB = 9;
    private static final int CARD_LIST = 26;
    private static final int SUPPORT = 93;
    private static final int MEMBER = 98;
    private static final int BALANCE = 212;

    /**
     * @param mContext  上下文
     * @param poster    Poster实例
     * @param trackData 统计数据,可为空
     */
    public static void bannerJump(
            Context mContext, Poster poster, @Nullable JSONObject trackData) {
        Intent intent = null;
        User user= UserSession.getInstance().getUser(mContext);
        switch (poster.getTargetType()) {
            case WEB:
                if (TextUtils.isEmpty(poster.getUrl())) {
                    return;
                }
                intent = new Intent(mContext, HljWebViewActivity.class);
                intent.putExtra("bar_style", (int) poster.getTargetId());
                intent.putExtra("path", poster.getUrl());
                break;
            case CARD_LIST:
                if (AuthUtil.loginBindCheck(mContext)) {
                    intent = new Intent(mContext, CardListActivity.class);
                }
                break;
            case SUPPORT:
                CardMasterSupportUtil.goToSupport(mContext, (int) poster.getTargetId());
                break;
            case MEMBER:
                //会员尊享跳转
                intent = new Intent(mContext, OpenMemberActivity.class);
                break;
            case BALANCE:
                if(user==null||user.getId()==0){
                    return;
                }
                //余额明细页
                intent = new Intent(mContext, BalanceActivity.class);
                break;
        }
        if (intent != null) {
            mContext.startActivity(intent);
        }
    }
}
