package me.suncloud.marrymemo.util.merchant;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.gson.JsonElement;
import com.hunliji.hljchatlibrary.WSRealmHelper;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSExtObject;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSTips;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.merchant.MerchantApi;
import me.suncloud.marrymemo.model.merchant.wrappers.AppointmentPostBody;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by luohanlin on 2017/11/1.
 * 预约帮助类
 */

public class AppointmentUtil {

    public static final int DEFAULT = 0;
    public static final int MERCHANT = 1;//商家
    public static final int HOTEL = 2;//酒店
    public static final int EVNET = 3;//活动
    public static final int WORKCASE = 4;//套餐案例
    public static final int OTHER = 5;//其他婚礼纪
    public static final int SMALLSHOP = 6;//微店
    public static final int ADV = 7;//聚客宝
    public static final int OUTSIDE = 8;//外部平台报名
    public static final int MERCHANT_CHAT = 11; //聊天界面的预约

    /**
     * 预约商家
     *
     * @param context
     * @param merchantId
     * @param merchantUserId
     * @param type
     * @param callback       成功之后的回调，如果没有回调则直接跳转私信界面
     */
    public static Subscription makeAppointment(
            final Context context,
            final long merchantId,
            final long merchantUserId,
            int type,
            @Nullable final AppointmentCallback callback) {
        Subscriber postSubscriber = HljHttpSubscriber.buildSubscriber(context)
                .setProgressDialog(DialogUtil.createProgressDialog(context))
                .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                    @Override
                    public void onNext(JsonElement jsonElement) {
                        if (callback != null) {
                            callback.onCallback();
                        } else if (merchantUserId > 0) {
                            saveAppointmentTipsToWsMessage(context, merchantUserId);

                            Intent intent = new Intent(context, WSCustomerChatActivity.class);
                            intent.putExtra(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER_ID,
                                    merchantUserId);
                            context.startActivity(intent);
                        } else {
                            // 弹窗
                            DialogUtil.createAppointmentDlg(context)
                                    .show();
                        }
                    }
                })
                .build();
        AppointmentPostBody body = new AppointmentPostBody();
        body.setFromType(type);
        body.setFormId(merchantId);
        body.setMerchantId(merchantId);
        return MerchantApi.makeAppointmentObb(body)
                .subscribe(postSubscriber);
    }

    private static void saveAppointmentTipsToWsMessage(Context context, long merchantUserId) {
        WSRealmHelper.saveWSChatToLocal(context,
                WSChat.TIPS,
                merchantUserId,
                new WSExtObject(new WSTips(WSTips.ACTION_APPOINTMENT_SUCCESS_TIP,
                        context.getString(R.string.label_success_make_appointment___chat),
                        context.getString(R.string.msg_success_make_appointment___chat))));
    }

    public interface AppointmentCallback {
        void onCallback();
    }
}
