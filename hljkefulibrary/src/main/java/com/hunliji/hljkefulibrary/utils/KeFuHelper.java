package com.hunliji.hljkefulibrary.utils;

import android.content.Context;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljhttplibrary.entities.HljApiException;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljkefulibrary.moudles.EMTrack;
import com.hunliji.hljkefulibrary.moudles.TransferToKefu;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.model.ContentFactory;
import com.hyphenate.helpdesk.model.EvaluationInfo;

import org.json.JSONObject;

/**
 * Created by wangtao on 2017/10/19.
 */

public class KeFuHelper {

    public static EMTrack getTrack(Message message) {
        try {
            if (message == null || message.getJSONObjectAttribute("msgtype") == null) {
                return null;
            }
            JSONObject trackJson = message.getJSONObjectAttribute("msgtype")
                    .optJSONObject("track");
            if (trackJson == null) {
                return null;
            }
            return GsonUtil.getGsonInstance()
                    .fromJson(trackJson.toString(), EMTrack.class);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static Merchant getMerchant(Message message) {
        try {
            if (message == null || message.getJSONObjectAttribute("merchant") == null) {
                return null;
            }
            return GsonUtil.getGsonInstance()
                    .fromJson(message.getJSONObjectAttribute("merchant")
                            .toString(), Merchant.class);
        } catch (Exception ignored) {
        }
        return null;
    }


    public static boolean isEvalResult(Message message) {
        try {
            if (message.getJSONObjectAttribute("weichat") != null) {
                String ctrlType = message.getJSONObjectAttribute("weichat")
                        .optString("ctrlType");
                return "enquiry".equals(ctrlType);
            }
        } catch (Exception ignored) {
        }
        return false;
    }


    public static TransferToKefu getTransferToKefuMsg(Message message) {
        try {
            if (message.getJSONObjectAttribute("weichat") != null) {
                JSONObject jsonWeiChat = message.getJSONObjectAttribute("weichat");
                String ctrlType = jsonWeiChat.optString("ctrlType");
                if ("TransferToKfHint".equals(ctrlType) && jsonWeiChat.has("ctrlArgs")) {
                    return GsonUtil.getGsonInstance()
                            .fromJson(jsonWeiChat.optJSONObject("ctrlArgs")
                                    .toString(), TransferToKefu.class);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
