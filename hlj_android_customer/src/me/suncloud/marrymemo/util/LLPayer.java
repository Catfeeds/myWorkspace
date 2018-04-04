package me.suncloud.marrymemo.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Identifiable;
import me.suncloud.marrymemo.model.UserBindBankCard;
import me.suncloud.marrymemo.view.EnterBankCardIDActivity;
import me.suncloud.marrymemo.view.FindPayPasswordActivity;
import me.suncloud.marrymemo.view.LLPayIdentificationActivity;
import me.suncloud.marrymemo.view.SelectBindCardListActivity;
import me.suncloud.marrymemo.view.SetPayPasswordActivity;
import me.suncloud.marrymemo.view.VerificationPayPasswordActivity;

/**
 * Created by werther on 15/12/29.
 */
public class LLPayer implements Identifiable {

    private static final long serialVersionUID = 1869017871276921437L;
    public String feeStr;
    public String payUrl;
    public String jsonString;

    private Class<?> nextActivity;
    private Map<String, Object> nextData;
    private static final int RQF_PAY = 1;
    private static Handler orderHandler;
    private static final String RET_CODE_SUCCESS = "0000";// 0000 交易成功
    private static final String RET_CODE_PROCESS = "2008";// 2008 支付处理中
    private static final String RESULT_PAY_SUCCESS = "SUCCESS";
    private static final String RESULT_PAY_PROCESSING = "PROCESSING";
    public boolean isBindNewCard; // 是否是添加新的银行卡,如果是则说明用户已经设置过支付密码,在支付完成之后不需要再次设置支付密码
    public long bindCardId; // 如果是使用已绑定的银行卡支付,这个就是绑定的银行卡的绑定ID
    private String shareString;
    private String retStr; // llpay支付结果字符串

    public static final int TYPE_FIND_PASSWORD = 1;
    public static final int TYPE_RESET_PASSWORD = 2;
    public static final int TYPE_VERIFY_RESET_PASSWORD = 1;
    public static final int TYPE_VERIFY_BIND_NEW_CARD = 2;

    private String freeOrderLink;

    /**
     * 连连快捷支付,构造方法
     *
     * @param fee          要付款的金额
     * @param payUrl       请求付款信息的url
     * @param jsonString   请求付款信息时需要提交的参数
     * @param nextActivity 付款完成之后跳转的页面
     * @param nextData     付款完成之后跳转页面的传入参数
     * @param handler      付款完成之后支付发起页面的回调函数
     * @param bindNewCard  是否是添加新卡
     */
    public LLPayer(String fee, String payUrl, String jsonString, Class<?> nextActivity,
                   Map<String, Object> nextData, Handler handler, boolean bindNewCard) {
        this.feeStr = fee;
        this.payUrl = payUrl;
        this.jsonString = jsonString;
        this.nextActivity = nextActivity;
        this.nextData = nextData;
        orderHandler = handler;
        this.isBindNewCard = bindNewCard;
    }

    /**
     * 不使用来做订单支付,而是用来做找回密码,修改密码的
     * 构造方法
     */
    public LLPayer() {

    }

    /**
     * 启动连连支付,未指定绑定银行卡时使用
     *
     * @param activity
     */
    public void llPay(Activity activity) {
        if (isBindNewCard) {
            // 如果是添加新卡则需要先验证密码
            Intent intent = new Intent(activity, VerificationPayPasswordActivity.class);
            intent.putExtra("type", TYPE_VERIFY_BIND_NEW_CARD);
            intent.putExtra("payer", this);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }else {
            goEnterBankCardId(activity);
        }

    }

    /**
     * 启动连连快捷支付,指定了已绑定的银行卡时使用
     *
     * @param activity
     * @param bindCardId 用户已绑定的银行卡的绑定卡ID
     */
    public void llPay(Activity activity, long bindCardId) {
        this.bindCardId = bindCardId;
        Intent intent = new Intent(activity, VerificationPayPasswordActivity.class);
        intent.putExtra("payer", this);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    /**
     * 跳转到输入银行卡号页面
     *
     * @param activity
     */
    public void goEnterBankCardId(Activity activity) {
        Intent intent = new Intent(activity, EnterBankCardIDActivity.class);
        intent.putExtra("payer", this);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    /**
     * 跳转到连连支付所需的认证信息填写页面,传入必要参数
     *
     * @param activity
     * @param bankName 银行名称
     * @param bankCode 银行编号
     * @param cardId   银行卡号
     */
    public void goLLPayIdentification(Activity activity, String bankName, String bankCode, String
            cardId, String bankLogoPath) {
        Intent intent = new Intent(activity, LLPayIdentificationActivity.class);
        intent.putExtra("bank_code", bankCode);
        intent.putExtra("bank_name", bankName);
        intent.putExtra("bank_card_id", cardId);
        intent.putExtra("bank_logo_path", bankLogoPath);
        intent.putExtra("payer", this);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    /**
     * 从服务器得到连连签名支付参数后,启动连连支付控件使用支付参数开始支付,并设置回调函数
     *
     * @param activity
     * @param payParams   支付参数
     * @param handler     认证信息界面的回调函数
     * @param shareObject @Nullable,支付信息顺便带回来的分享信息,可以为空
     */
    public void securePay(final Activity activity, String payParams, final Handler handler,
                          @Nullable final JSONObject shareObject) {
        if (shareObject != null) {
            this.shareString = shareObject.toString();
        }
        LLPaySecurePayer llPaySecurePayer = new LLPaySecurePayer();
        Handler mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                retStr = (String) msg.obj;
                switch (msg.what) {
                    case RQF_PAY:
                        try {
                            JSONObject contentObj = new JSONObject(retStr);
                            String retCode = contentObj.optString("ret_code");
                            String retMsg = contentObj.optString("ret_msg");
                            if (RET_CODE_SUCCESS.equals(retCode)) {
                                String resultPay = contentObj.optString("result_pay");
                                if (RESULT_PAY_SUCCESS.equals(resultPay)) {
                                    // 支付成功,先调用订单支付页面的handler,不同支付页面对应的不同的订单类型,需要进行不同的刷新操作
                                    if (orderHandler != null) {
                                        Message message = new Message();
                                        message.what = Constants.PayResultStatus.WHAT_SUCCESS;
                                        message.obj = freeOrderLink;
                                        orderHandler.sendMessage(message);
                                    }

                                    // 延迟调用支付页面的handler,finish支付页面
                                    Message message = new Message();
                                    message.what = Constants.PayResultStatus.WHAT_SUCCESS;
                                    message.obj = retMsg;
                                    handler.sendMessageDelayed(message, 1000);
                                } else {
                                    // 返回去显示错误信息
                                    Message message = new Message();
                                    message.what = Constants.PayResultStatus.WHAT_FAIL;
                                    message.obj = retMsg;
                                    handler.sendMessage(message);
                                }
                            } else if (RET_CODE_PROCESS.equals(retCode)) {
                                String resultPay = contentObj.optString("result_pay");
                                if (RESULT_PAY_PROCESSING.equalsIgnoreCase(resultPay)) {
                                    // 返回去显示错误信息
                                    Message message = new Message();
                                    message.what = Constants.PayResultStatus.WHAT_FAIL;
                                    message.obj = retMsg;
                                    handler.sendMessage(message);
                                }
                            } else {
                                // 返回去显示错误信息
                                Message message = new Message();
                                message.what = Constants.PayResultStatus.WHAT_FAIL;
                                message.obj = retMsg;
                                handler.sendMessage(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                return false;
            }
        });
        llPaySecurePayer.pay(payParams, mHandler, RQF_PAY, activity, false);
    }


    /**
     * 零元支付
     *
     * @param activity
     * @param handler
     * @param shareObject @Nullable,支付信息顺便带回来的分享信息,可以为空
     */
    public void zeroPay(Activity activity, Handler handler, @Nullable JSONObject shareObject, JSONObject zeroPostJson) {
        this.shareString = shareObject.toString();
        // 支付成功,先调用订单支付页面的handler,不同支付页面对应的不同的订单类型,需要进行不同的刷新操作
        if(orderHandler!=null) {
            Message zeroMsg = new Message();
            zeroMsg.obj = zeroPostJson;
            zeroMsg.what = Constants.PayResultStatus.WHAT_ZERO_PAY;
            orderHandler.sendMessage(zeroMsg);
            Message message = new Message();
            message.what = Constants.PayResultStatus.WHAT_SUCCESS;
            message.obj = freeOrderLink;
            orderHandler.sendMessage(message);
        }

        // 零元支付成功之后不会要求设置密码
        goNextActivity(activity);

        // 延迟调用支付页面的handler,finish支付页面
        Message message = new Message();
        message.what = Constants.PayResultStatus.WHAT_SUCCESS;
        message.obj = "支付成功";
        handler.sendMessageDelayed(message, 1000);
    }

    /**
     * 发送红包绑定信息
     */
    public void afterBindRedPacket() {
        orderHandler.sendEmptyMessage(Constants.PayResultStatus.WHAT_BIND_RED_PACKET);
    }

    /**
     * 启动设置支付密码页面
     */
    public void goSetPayPasswordActivity(Activity activity, String cardHolder, String idNumber) {
        Intent intent = new Intent(activity, SetPayPasswordActivity.class);
        intent.putExtra("payer", this);
        intent.putExtra("card_holder", cardHolder);
        intent.putExtra("id_number", idNumber);
        activity.startActivityForResult(intent, Constants.RequestCode.SET_PAY_PASSWORD);
        activity.overridePendingTransition(R.anim.slide_in_from_bottom, R.anim
                .activity_anim_default);
    }

    public void goAfterPayActivity(Activity activity) {
        goNextActivity(activity);
    }

    /**
     * 支付成功之后的跳转,需要将之前保留的参数nextData在这里进行解析成合法参数传递到nextActivity中去
     * @param activity
     */
    public void goNextActivity(Activity activity) {
        if(nextActivity==null){
            return;
        }
        Intent intent = new Intent(activity, nextActivity);
        if (nextData != null && !nextData.isEmpty()) {
            Iterator<?> iterator = nextData.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                Object value = nextData.get(key);
                // !!!注意!!!
                // 这里的参数转换传递是手动解析的,所以能够使用的类型是有限制的,需要将这里传入的数据和LLPayer中的goNextActivity方法中的解析数据对应才行
                if (value instanceof String) {
                    intent.putExtra(key, (String) value);
                } else if (value instanceof Serializable) {
                    intent.putExtra(key, (Serializable) value);
                }else if (value instanceof Integer) {
                    intent.putExtra(key, (int) value);
                }
            }
        }
        if (!TextUtils.isEmpty(shareString)) {
            intent.putExtra("share_json", shareString);
        }
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.activity_anim_default, R.anim
                .activity_anim_default);
    }

    /**
     * 到找回密码的第一个界面,选择已绑定的银行卡
     *
     * @param activity
     */
    public void findPayPassword(Activity activity) {
        Intent intent = new Intent(activity, SelectBindCardListActivity.class);
        intent.putExtra("payer", this);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    public void findPasswordStep2(Activity activity, UserBindBankCard userBindBankCard) {
        Intent intent = new Intent(activity, FindPayPasswordActivity.class);
        intent.putExtra("payer", this);
        intent.putExtra("user_bind_card", userBindBankCard);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        activity.finish();
    }

    public void resetPayPassword(Activity activity) {
        Intent intent = new Intent(activity, VerificationPayPasswordActivity.class);
        intent.putExtra("type", TYPE_VERIFY_RESET_PASSWORD);
        intent.putExtra("payer", this);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    /**
     * 重新设置密码
     * 有两个地方会用到这个步骤: 1:找回支付密码, 在支付0.01元钱后带着支付成功的返回参数进行找回密码重置操作,2:使用现有密码修改设置新密码
     *
     * @param activity
     * @param type      1:找回支付密码, 在支付0.01元钱后带着支付成功的返回参数进行找回密码重置操作,2:使用现有密码修改设置新密码
     * @param oldPswMD5 修改密码的时候需要的老密码MD5加密值
     */
    public void goResetPayPasswordActivity(Activity activity, int type, String oldPswMD5) {
        Intent intent = new Intent(activity, SetPayPasswordActivity.class);
        intent.putExtra("payer", this);
        intent.putExtra("type", type);
        intent.putExtra("extra_para", retStr);
        if (type == TYPE_RESET_PASSWORD) {
            intent.putExtra("old_psw_md5", oldPswMD5);
        }
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_from_bottom, R.anim
                .activity_anim_default);
        activity.finish();
    }

    public void goFindPayPasswordActivity(Activity activity) {
        goResetPayPasswordActivity(activity, TYPE_FIND_PASSWORD, "");
    }

    /**
     * 返回进入忘记密码前的页面,即关闭找回密码的两个页面
     *
     * @param activity
     */
    public void backBeforeResetPassword(Activity activity) {
        activity.finish();
    }

    @Override
    public Long getId() {
        return serialVersionUID;
    }

    public void setFreeOrderLink(String freeOrderLink) {
        this.freeOrderLink = freeOrderLink;
    }
}
