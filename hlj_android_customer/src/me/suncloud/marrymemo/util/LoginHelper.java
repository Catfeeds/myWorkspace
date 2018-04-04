package me.suncloud.marrymemo.util;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.lang.ref.WeakReference;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.login.LoginApi;
import com.hunliji.hljcommonlibrary.models.CertifyCodeMsg;
import me.suncloud.marrymemo.model.login.CertifyPostBody;
import me.suncloud.marrymemo.model.login.LoginPwdPostBody;
import me.suncloud.marrymemo.model.login.LoginResult;
import rx.Observable;

/**
 * Created by jinxin on 2016/10/17.
 */

public class LoginHelper {
    //绑定手机号语音：bindPhoneVoice 登录语音：loginVoice 登录短信：loginMsg 绑定手机号短信：bindPhone 找回密码短信：setPwd
    // 注册短信：register 活动手机号短信检测: activityApplySms 活动手机号语音检测: activityApplyVoice 可不区分大小写 【限制】
    // 1、同一个手机号1分钟内不能重复发起发送验证码请求，时间可能会有所调整； 2、同一个ip地址 5分钟内不能大于10次
    //验证码类别
    public static final String BINDPHONEVOICE = "bindPhoneVoice";//绑定手机号语音
    public static final String LOGINVOICE = "loginVoice";//登录语音
    public static final String LOGINMSG = "loginMsg";//登录短信
    public static final String BINDPHONE = "bindPhone";//绑定手机号短信
    public static final String QQ_TAG = "qq";
    public static final String WEIXIN_TAG = "weixin";
    public static final String WEIBO_TAG = "sina";
    public static final String ACTIVITYAPPLYSMS = "activityApplySms"; //活动手机号短信检测
    public static final String ACTIVITYAPPLYVOICE = "activityApplyVoice";//活动手机号语音检测

    public static final long CERTIFY_TIME = 30 * 1000;//验证码倒计时时间
    public static final long CERTIFY_LONG_TIME = 60 * 1000;//验证码60s倒计时时间
    public static final long TIME_GAP = 1000;//倒计时 时间间隔
    public static final int CERTIFY_CODE = 503;//提示"稍后再提交验证码"的retCode

    private static LoginHelper helper;
    private CountDownTimer timeDown;
    private HljHttpSubscriber checkSubscriber;
    private HljHttpSubscriber certifySubscriber;
    private WeakReference<View> progressBarWeakReference;

    private LoginHelper() {

    }

    public static LoginHelper getInstance() {
        if (helper == null) {
            synchronized (LoginHelper.class) {
                if (helper == null) {
                    helper = new LoginHelper();
                }
            }
        }
        return helper;
    }

    public void setTimeDown(CountDownTimer timeDown) {
        this.timeDown = timeDown;
    }

    public void setProgressBar(View progressBar) {
        this.progressBarWeakReference = new WeakReference<>(progressBar);
    }

    /**
     * 账密登录
     */
    public void phoneLoginWithPwd(
            HljHttpSubscriber loginPwdSubscriber, String account, String pwd) {
        LoginPwdPostBody pwdPostBody = new LoginPwdPostBody();
        pwdPostBody.setPhone(account);
        pwdPostBody.setPwd(JSONUtil.getMD5(pwd));
        Observable<LoginResult> observable = LoginApi.phonePwdLogin(pwdPostBody);
        observable.subscribe(loginPwdSubscriber);
    }


    /**
     * @param flag
     * @param check           check true 需要检测
     * @param btnSmsCertify   短信验证码
     * @param btnVoiceCertify 语音验证码
     */
    public void getCertify(
            final Context mContext,
            final String phone,
            final String flag,
            final boolean check,
            final Button btnSmsCertify,
            final Button btnVoiceCertify) {
        if (timeDown != null) {
            timeDown.start();
        }
        if (!check) {
            certify(mContext,phone, flag, btnSmsCertify, btnVoiceCertify);
        } else {
            //验证手机号是否可用
            Observable<HljHttpResult> observable = LoginApi.certifyPhone(phone);
            checkSubscriber = HljHttpSubscriber.buildSubscriber(mContext)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                        @Override
                        public void onNext(
                                HljHttpResult hljHttpResult) {
                            if (hljHttpResult != null) {
                                HljHttpStatus status = hljHttpResult.getStatus();
                                if (status != null) {
                                    if (status.getRetCode() == 0) {
                                        certify(mContext,phone, flag, btnSmsCertify, btnVoiceCertify);
                                    } else {
                                        if (timeDown != null) {
                                            timeDown.cancel();
                                        }
                                        btnSmsCertify.setText(R.string.btn_phone_code);
                                        btnSmsCertify.setEnabled(true);
                                        btnVoiceCertify.setText(R.string
                                                .label_voice_certify_unreceive);
                                        btnVoiceCertify.setEnabled(true);
                                        Toast.makeText(mContext,
                                                status.getMsg(),
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                }
                            }
                        }
                    })
                    .build();
            observable.subscribe(checkSubscriber);
        }
    }

    public void onDestroy() {
        if(timeDown!=null){
            timeDown.cancel();
            timeDown=null;
        }
        if (checkSubscriber != null && !checkSubscriber.isUnsubscribed()) {
            checkSubscriber.unsubscribe();
        }
        if (certifySubscriber != null && !certifySubscriber.isUnsubscribed()) {
            certifySubscriber.unsubscribe();
        }
    }

    public void getCertify(
            Context context,
            String phone,
            String flag,
            Button btnSmsCertify,
            Button btnVoiceCertify) {
        getCertify(context, phone, flag, false, btnSmsCertify, btnVoiceCertify);
    }

    public void certify(
            final Context mContext,
            String phone,
            final String flag,
            final Button btnSmsCertify,
            final Button btnVoiceCertify) {
        CertifyPostBody body = new CertifyPostBody(phone, flag);

        Observable<HljHttpResult<CertifyCodeMsg>> observable = LoginApi.getPostCertifyCode(body);
        certifySubscriber = HljHttpSubscriber.buildSubscriber(mContext)
                .setProgressBar(progressBarWeakReference == null ? null :
                        progressBarWeakReference.get())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<CertifyCodeMsg>>() {
                    @Override
                    public void onNext(HljHttpResult<CertifyCodeMsg> result) {
                        if (result != null) {
                            HljHttpStatus status = result.getStatus();
                            if (status != null) {
                                if (status.getRetCode() != 0) {
                                    if (status.getRetCode() == CERTIFY_CODE) {
                                        //重复提交 取消倒计时
                                        if (timeDown != null) {
                                            timeDown.onFinish();
                                            timeDown.cancel();
                                        }
                                    }
                                    String msg = status.getMsg();
                                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                            CertifyCodeMsg codeMsg = result.getData();
                            if (codeMsg != null) {
                                if (Constants.DEBUG && !TextUtils.isEmpty(codeMsg.getMsg())) {
                                    Toast.makeText(mContext, codeMsg.getMsg(), Toast.LENGTH_SHORT)
                                            .show();
                                }
                                if (codeMsg.getStatus() == 0) {
                                    RxBus.getDefault()
                                            .post(new RxEvent(RxEvent.RxEventType.LOGIN_CERTIFY,
                                                    null));
                                }
                            } else {
                                onCertifyError(btnSmsCertify,
                                        btnVoiceCertify,
                                        mContext.getString(R.string.hint_send_code_error));
                            }
                        }
                    }
                })
                .build();
        observable.subscribe(certifySubscriber);
    }

    private void onCertifyError(Button btnSmsCertify, Button btnVoiceCertify, String msg) {
        if (timeDown != null) {
            timeDown.cancel();
        }
        btnVoiceCertify.setEnabled(true);
        btnVoiceCertify.setText(R.string.label_voice_certify_unreceive);
        btnSmsCertify.setEnabled(true);
        btnSmsCertify.setText(R.string.label_reload);
        Toast.makeText(btnSmsCertify.getContext(), msg, Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * 取消倒计时
     */
    public void cancelTimeDown(Button btnSms, Button btnVoice) {
        if (timeDown != null) {
            timeDown.cancel();
        }
        if (btnSms != null) {
            btnSms.setEnabled(true);
            btnSms.setText(R.string.label_reload);
        }
        if (btnVoice != null) {
            btnVoice.setEnabled(true);
            btnVoice.setText(R.string.label_voice_certify_unreceive);
        }
    }

}
