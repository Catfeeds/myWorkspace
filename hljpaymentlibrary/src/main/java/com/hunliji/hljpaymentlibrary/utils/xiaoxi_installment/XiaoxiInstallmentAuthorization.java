package com.hunliji.hljpaymentlibrary.utils.xiaoxi_installment;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.AuthItem;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers
        .XiaoxiInstallmentAuthItemsData;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.AddBasicUserInfoActivity;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment
        .AddXiaoxiInstallmentBankCardActivity;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.BasicAuthItemListActivity;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment
        .BasicAuthenticationResultActivity;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment
        .RealNameAuthenticationActivity;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment
        .XiaoxiInstallmentWebViewActivity;

import java.util.List;

import rx.functions.Action0;


/**
 * 小犀分期认证
 * Created by chen_bin on 2017/08/22.
 */
public class XiaoxiInstallmentAuthorization {
    private XiaoxiInstallmentAuthItemsData authItemsData;
    private boolean isPay;
    private int index;
    private static XiaoxiInstallmentAuthorization INSTANCE;

    private XiaoxiInstallmentAuthorization() {}

    public static XiaoxiInstallmentAuthorization getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new XiaoxiInstallmentAuthorization();
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public void onAuthorization(final Activity activity, final boolean isPay) {
        this.isPay = isPay;
        index = -1;
        authItemsData = null;
        XiaoxiInstallmentApi.getAuthItemsObb(activity, XiaoxiInstallmentApi.AUTH_ITEM_TYPE_BASIC)
                .subscribe(HljHttpSubscriber.buildSubscriber(activity)
                        .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<XiaoxiInstallmentAuthItemsData>>() {
                            @Override
                            public void onNext(
                                    HljHttpResult<XiaoxiInstallmentAuthItemsData> result) {
                                HljHttpStatus hljHttpStatus = result.getStatus();
                                int retCode = hljHttpStatus == null ? -1 : hljHttpStatus
                                        .getRetCode();
                                if (retCode != 0 && retCode != 7001) { //7001未实名认证
                                    ToastUtil.showToast(activity,
                                            hljHttpStatus == null ? null : hljHttpStatus.getMsg(),
                                            0);
                                    RxBus.getDefault()
                                            .post(new PayRxEvent(PayRxEvent.RxEventType
                                                    .AUTHORIZE_CANCEL,
                                                    null));
                                    return;
                                }
                                authItemsData = result.getData();
                                if (retCode == 0 && (authItemsData == null || authItemsData
                                        .isEmpty())) {
                                    ToastUtil.showToast(activity,
                                            null,
                                            R.string.hint_get_auth_items_empty___pay);
                                    RxBus.getDefault()
                                            .post(new PayRxEvent(PayRxEvent.RxEventType
                                                    .AUTHORIZE_CANCEL,
                                                    null));
                                    return;
                                }

                                //实名未认证，进行实名认证
                                int realNameStatus = AuthItem.STATUS_UNAUTHORIZED;
                                if (authItemsData != null) {
                                    realNameStatus = authItemsData.getAuthItemStatusByCode
                                            (AuthItem.CODE_REAL_NAME);
                                }
                                if (realNameStatus == AuthItem.STATUS_UNAUTHORIZED) {
                                    index = 0;
                                    authorizationJump(activity,
                                            AuthItem.CODE_REAL_NAME,
                                            true,
                                            false);
                                    return;
                                }

                                //银行卡未绑定，进行绑卡操作
                                int bankCardStatus = AuthItem.STATUS_UNAUTHORIZED;
                                if (authItemsData != null) {
                                    bankCardStatus = authItemsData.getAuthItemStatusByCode
                                            (AuthItem.CODE_BANK_CARD);
                                }
                                if (bankCardStatus == AuthItem.STATUS_UNAUTHORIZED) {
                                    index = 1;
                                    authorizationJump(activity,
                                            AuthItem.CODE_BANK_CARD,
                                            true,
                                            false);
                                    return;
                                }

                                //总的授信状态，包含“已认证”，“未认证”，“认证过期”
                                int status = AuthItem.STATUS_UNAUTHORIZED;
                                if (authItemsData != null) {
                                    status = authItemsData.getStatus();
                                }
                                if (status == AuthItem.STATUS_AUTHORIZED) {
                                    RxBus.getDefault()
                                            .post(new PayRxEvent(PayRxEvent.RxEventType
                                                    .HAD_AUTHORIZED,
                                                    null));
                                } else if (status == AuthItem.STATUS_UNAUTHORIZED) {
                                    Intent intent = new Intent(activity,
                                            BasicAuthItemListActivity.class);
                                    intent.putExtra(BasicAuthItemListActivity.ARG_IS_PAY, isPay);
                                    activity.startActivity(intent);
                                } else {
                                    DialogUtil.createSingleButtonDialog(activity,
                                            activity.getString(R.string.msg_limit_expired___pay),
                                            activity.getString(R.string.btn_authorize___pay),
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(activity,
                                                            BasicAuthItemListActivity.class);
                                                    intent.putExtra(BasicAuthItemListActivity
                                                                    .ARG_IS_PAY,
                                                            isPay);
                                                    activity.startActivity(intent);
                                                }
                                            })
                                            .show();
                                }
                            }
                        })
                        .setOnErrorListener(new SubscriberOnErrorListener<Throwable>() {
                            @Override
                            public void onError(Throwable e) {
                                ToastUtil.showToast(activity, e.getMessage(), 0);
                                RxBus.getDefault()
                                        .post(new PayRxEvent(PayRxEvent.RxEventType
                                                .AUTHORIZE_CANCEL,
                                                e.getMessage()));
                            }
                        })
                        .setProgressDialog(DialogUtil.createProgressDialog(activity))
                        .build());
    }

    /**
     * 当前想授信完成
     *
     * @param activity
     * @param code
     * @param isAuto
     */
    public void onCurrentItemAuthorized(Activity activity, int code, boolean isAuto) {
        onCurrentItemAuthorized(activity, code, AuthItem.STATUS_AUTHORIZED, isAuto);
    }

    /**
     * 当前项授信完成
     *
     * @param activity
     * @param code
     * @param status
     * @param isAuto
     */
    public void onCurrentItemAuthorized(Activity activity, int code, int status, boolean isAuto) {
        if (!isAuto) {
            activity.finish();
            return;
        }
        switch (code) {
            case AuthItem.CODE_REAL_NAME:
                onRealNameAuthorized(activity);
                break;
            case AuthItem.CODE_BANK_CARD:
            case AuthItem.CODE_BASIC_USER_INFO:
            case AuthItem.CODE_HOUSE_FUND:
            case AuthItem.CODE_CREDIT_CARD_BILL:
                if (authItemsData == null || authItemsData.isEmpty()) {
                    return;
                }
                //当前项置认证状态，正常是认证成功，“跳过”的是未认证
                List<AuthItem> authItems = authItemsData.getAuthItems();
                AuthItem authItem = authItems.get(index);
                authItem.setStatus(status);

                index++;
                if (index >= authItems.size() || authItemsData.getStatus() == AuthItem
                        .STATUS_AUTHORIZED) {
                    Intent intent = new Intent(activity, BasicAuthenticationResultActivity.class);
                    intent.putExtra(BasicAuthenticationResultActivity.ARG_IS_PAY, isPay);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0);
                    activity.finish();
                } else {
                    AuthItem nextAuthItem = authItems.get(index);
                    //公积金或者信用卡且不是列表最后一项时存在跳过。
                    boolean isCanSkip = index < authItems.size() - 1 && (nextAuthItem.getCode()
                            == AuthItem.CODE_HOUSE_FUND || nextAuthItem.getCode() == AuthItem
                            .CODE_CREDIT_CARD_BILL);
                    authorizationJump(activity,
                            nextAuthItem.getUrl(),
                            nextAuthItem.getCode(),
                            true,
                            isCanSkip,
                            true);
                }
                break;
        }
    }

    /**
     * 授信跳转
     *
     * @param activity
     * @param url
     * @param code
     */
    public void authorizationJump(Activity activity, String url, int code) {
        authorizationJump(activity, url, code, false, false, false);
    }

    /**
     * 授信跳转
     *
     * @param activity
     * @param code
     * @param isAuto
     * @param isFinishCurrentActivity
     */
    public void authorizationJump(
            Activity activity, int code, boolean isAuto, boolean isFinishCurrentActivity) {
        authorizationJump(activity, null, code, isAuto, false, isFinishCurrentActivity);
    }

    /**
     * 授信跳转
     *
     * @param activity
     * @param url
     * @param code
     * @param isAuto
     * @param isCanSkip
     * @param isFinishCurrentActivity
     */
    public void authorizationJump(
            Activity activity,
            String url,
            int code,
            boolean isAuto,
            boolean isCanSkip,
            boolean isFinishCurrentActivity) {
        Intent intent = null;
        switch (code) {
            case AuthItem.CODE_REAL_NAME: //实名认证
                intent = new Intent(activity, RealNameAuthenticationActivity.class);
                intent.putExtra(RealNameAuthenticationActivity.ARG_IS_AUTO, isAuto);
                break;
            case AuthItem.CODE_BANK_CARD: //绑定银行卡
                intent = new Intent(activity, AddXiaoxiInstallmentBankCardActivity.class);
                intent.putExtra(AddXiaoxiInstallmentBankCardActivity.ARG_IS_AUTO, isAuto);
                break;
            case AuthItem.CODE_BASIC_USER_INFO: //基本信息
                intent = new Intent(activity, AddBasicUserInfoActivity.class);
                intent.putExtra(AddBasicUserInfoActivity.ARG_IS_AUTO, isAuto);
                break;
            case AuthItem.CODE_CREDIT_CARD_BILL: //信用卡账单
            case AuthItem.CODE_DEPOSIT_CARD_BILL: //储蓄卡流水导入
            case AuthItem.CODE_HOUSE_FUND: //公积金认证
                if (!TextUtils.isEmpty(url)) {
                    intent = new Intent(activity, XiaoxiInstallmentWebViewActivity.class);
                    intent.putExtra(XiaoxiInstallmentWebViewActivity.ARG_IS_AUTO, isAuto);
                    intent.putExtra(XiaoxiInstallmentWebViewActivity.ARG_IS_CAN_SKIP, isCanSkip);
                    intent.putExtra(XiaoxiInstallmentWebViewActivity.ARG_URL, url);
                    intent.putExtra(XiaoxiInstallmentWebViewActivity.ARG_CODE, code);
                }
                break;
        }
        if (intent != null) {
            activity.startActivity(intent);
            if (isFinishCurrentActivity) {
                activity.finish();
            }
        }
    }

    /**
     * 实名认证完成
     *
     * @param activity
     */
    @SuppressWarnings("unchecked")
    private void onRealNameAuthorized(final Activity activity) {
        index = -1;
        authItemsData = null;
        XiaoxiInstallmentApi.getAuthItemsObb(activity, XiaoxiInstallmentApi.AUTH_ITEM_TYPE_BASIC)
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        activity.finish();
                    }
                })
                .subscribe(HljHttpSubscriber.buildSubscriber(activity)
                        .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<XiaoxiInstallmentAuthItemsData>>() {
                            @Override
                            public void onNext(
                                    HljHttpResult<XiaoxiInstallmentAuthItemsData> result) {
                                HljHttpStatus hljHttpStatus = result.getStatus();
                                if (hljHttpStatus == null || hljHttpStatus.getRetCode() != 0) {
                                    ToastUtil.showToast(activity,
                                            hljHttpStatus == null ? null : hljHttpStatus.getMsg(),
                                            0);
                                    RxBus.getDefault()
                                            .post(new PayRxEvent(PayRxEvent.RxEventType
                                                    .AUTHORIZE_CANCEL,
                                                    null));
                                    return;
                                }
                                authItemsData = result.getData();
                                if (authItemsData == null || authItemsData.isEmpty()) {
                                    ToastUtil.showToast(activity,
                                            null,
                                            R.string.hint_get_auth_items_empty___pay);
                                    RxBus.getDefault()
                                            .post(new PayRxEvent(PayRxEvent.RxEventType
                                                    .AUTHORIZE_CANCEL,
                                                    null));
                                    return;
                                }
                                index = 1;
                                authorizationJump(activity, AuthItem.CODE_BANK_CARD, true, true);
                            }
                        })
                        .setOnErrorListener(new SubscriberOnErrorListener<Throwable>() {
                            @Override
                            public void onError(Throwable e) {
                                ToastUtil.showToast(activity, e.getMessage(), 0);
                                RxBus.getDefault()
                                        .post(new PayRxEvent(PayRxEvent.RxEventType
                                                .AUTHORIZE_CANCEL,
                                                null));
                            }
                        })
                        .setProgressDialog(DialogUtil.createProgressDialog(activity))
                        .build());
    }

}