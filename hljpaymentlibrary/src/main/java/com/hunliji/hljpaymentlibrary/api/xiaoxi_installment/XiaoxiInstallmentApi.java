package com.hunliji.hljpaymentlibrary.api.xiaoxi_installment;

import android.content.Context;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.Bank;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.AuthItem;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.CreditLimit;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.Debt;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.DebtInfo;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.DebtTransferRecord;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.InstallmentData;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.Investor;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.RepaymentSchedule;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.XiaoxiInstallmentSubmitResult;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.XiaoxiInstallmentUser;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers
        .XiaoxiInstallmentAuthItemsData;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers.XiaoxiInstallmentOrdersData;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers
        .XiaoxiInstallmentPreviewSchedulesData;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers
        .XiaoxiInstallmentSchedulesData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 小犀分期相关请求
 * Created by chen_bin on 2016/12/6 0006.
 */
public class XiaoxiInstallmentApi {
    public final static int AUTH_ITEM_TYPE_ALL = 0; //查询所有认证项
    public final static int AUTH_ITEM_TYPE_BASIC = -1; //表示基础认证项
    public final static int AUTH_ITEM_TYPE_INCREASE_LIMIT = 2; //表示提额认证项
    public final static String XIAOXI_INSTALLMENT_EDU_URL = "https://m.hunliji" +
            ".com/p/wedding/Public/wap/m/51-myInstalment-nopassed.html";
    public final static String XIAOXI_INSTALLMENT_QUESTIONS_URL = "https://m.hunliji" +
            ".com/p/wedding/Public/wap/m/51-instalment-questions.html";
    public final static String XIAOXI_INSTALLMENT_LOAN_AND_RISK_URL = "https://m.hunliji" +
            ".com/p/wedding/Public/wap/activity/20180123_Informing.html"; //51分期借款须知和风险告知书
    public final static String XIAOXI_INSTALLMENT_BANK_CARD_LIMIT_URL = "https://m.hunliji" +
            ".com/p/wedding/Public/wap/activity/20180309-bank-limit.html"; //银行卡限额说明

    private static void addAssetUserId(Context context, Map<String, Object> map) {
        User user = UserSession.getInstance()
                .getUser(context);
        if (user != null && user.getId() > 0) {
            map.put("assetUserId", user.getId());
        }
    }

    /**
     * 获取银行列表
     *
     * @return
     */
    public static Observable<List<Bank>> getBanksObb() {
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .getBanks()
                .map(new HljHttpResultFunc<List<Bank>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 认证项查询
     *
     * @param authItemType 0 查询所有认证项,-1 表示基础认证项,2 表示提额认证项。
     * @return
     */
    public static Observable<HljHttpResult<XiaoxiInstallmentAuthItemsData>> getAuthItemsObb(
            Context context, final int authItemType) {
        Map<String, Object> map = new HashMap<>();
        addAssetUserId(context, map);
        map.put("authItemType", authItemType);
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .getAuthItems(map)
                .subscribeOn(Schedulers.io())
                .map(new Func1<HljHttpResult<XiaoxiInstallmentAuthItemsData>,
                        HljHttpResult<XiaoxiInstallmentAuthItemsData>>() {
                    @Override
                    public HljHttpResult<XiaoxiInstallmentAuthItemsData> call
                            (HljHttpResult<XiaoxiInstallmentAuthItemsData> result) {
                        if (authItemType == AUTH_ITEM_TYPE_BASIC && result != null) {
                            XiaoxiInstallmentAuthItemsData authItemsData = result.getData();
                            if (authItemsData != null && !authItemsData.isEmpty()) {
                                List<AuthItem> authItems = authItemsData.getAuthItems();
                                for (int i = 0, size = authItems.size(); i < size; i++) {
                                    AuthItem authItem = authItems.get(i);
                                    if (i < size - 1 && (authItem.getCode() == AuthItem
                                            .CODE_HOUSE_FUND || authItem.getCode() == AuthItem
                                            .CODE_CREDIT_CARD_BILL)) {
                                        authItemsData.setGroupIndex(i);
                                        break;
                                    }
                                }
                            }
                        }
                        return result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 协议预览
     *
     * @param context
     * @param agreementType 协议类型
     * @return
     */
    public static Observable<JsonElement> getPreviewAgreementByTypeObb(
            Context context, int agreementType) {
        Map<String, Object> map = new HashMap<>();
        addAssetUserId(context, map);
        map.put("agreementType", agreementType);
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .getPreviewAgreementByType(map)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 协议查询
     *
     * @param context
     * @param assetOrderId  //第三方订单
     * @param agreementType //协议类型
     * @param investorId
     * @return
     */
    public static Observable<JsonElement> getAgreementByTypeObb(
            Context context, String assetOrderId, int agreementType, long investorId) {
        Map<String, Object> map = new HashMap<>();
        addAssetUserId(context, map);
        map.put("assetOrderId", assetOrderId);
        map.put("agreementType", agreementType);
        if (investorId > 0) {
            map.put("investorId", investorId);
        }
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .getAgreementByType(map)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 实名认证
     *
     * @param context
     * @param realName
     * @param idCardNo
     * @return
     */
    public static Observable authorizeRealNameObb(
            Context context, String realName, String idCardNo) {
        Map<String, Object> map = new HashMap<>();
        addAssetUserId(context, map);
        map.put("realName", realName);
        map.put("idCard", idCardNo);
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .authorizeRealName(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 实名信息
     *
     * @return
     */
    public static Observable<JsonElement> getRealNameObb() {
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .getRealName()
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 实名绑卡
     *
     * @param context
     * @param realName   用户姓名
     * @param idCardNo   身份证号
     * @param bankCode   银行编号
     * @param bankCardNo 银行卡号
     * @param mobile     预留手机号
     * @return
     */
    public static Observable<JsonElement> bindBankCardObb(
            Context context,
            String realName,
            String idCardNo,
            String bankCode,
            String bankCardNo,
            String mobile) {
        Map<String, Object> map = new HashMap<>();
        addAssetUserId(context, map);
        map.put("realName", realName);
        map.put("idCard", idCardNo);
        map.put("bank", bankCode);
        map.put("bankAccount", bankCardNo);
        map.put("mobile", mobile);
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .bindBankCard(map)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 签约绑卡验证
     *
     * @param context
     * @param realName
     * @param idCardNo
     * @param bankCode
     * @param bankCardNo
     * @param mobile
     * @param smsSerialNo
     * @param orderNo
     * @param smsCode
     * @return
     */
    public static Observable verifyBankCardObb(
            Context context,
            String realName,
            String idCardNo,
            String bankCode,
            String bankCardNo,
            String mobile,
            String smsSerialNo,
            String orderNo,
            String smsCode) {
        Map<String, Object> map = new HashMap<>();
        addAssetUserId(context, map);
        map.put("realName", realName);
        map.put("idCard", idCardNo);
        map.put("bank", bankCode);
        map.put("bankAccount", bankCardNo);
        map.put("mobile", mobile);
        map.put("smsSerialNo", smsSerialNo);
        map.put("orderNo", orderNo);
        map.put("verifyCode", smsCode);
        map.put("isDefaultCard", true);
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .verifyBankCard(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 绑卡信息
     *
     * @return
     */
    public static Observable<JsonElement> getBankCardBindedInfoObb() {
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .getBankCardBindedInfo()
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 个人资料保存
     *
     * @param user
     * @return
     */
    public static Observable uploadUserInfoObb(Context context, XiaoxiInstallmentUser user) {
        user.setAssetUserId(String.valueOf(UserSession.getInstance()
                .getUser(context)
                .getId()));
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .uploadUserInfo(user)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 授信查询
     *
     * @param context
     * @return
     */
    public static Observable<HljHttpResult<CreditLimit>> getCreditLimitObb(Context context) {
        Map<String, Object> map = new HashMap<>();
        addAssetUserId(context, map);
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .getCreditLimit(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 我的账单列表
     *
     * @return
     */
    public static Observable<XiaoxiInstallmentOrdersData> getMyBillsObb(Context context) {
        Map<String, Object> map = new HashMap<>();
        addAssetUserId(context, map);
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .getMyBills(map)
                .map(new HljHttpResultFunc<XiaoxiInstallmentOrdersData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 还款计划列表
     *
     * @param context
     * @param assetOrderId
     * @return
     */
    public static Observable<XiaoxiInstallmentSchedulesData> getRepaymentSchedulesObb(
            Context context, String assetOrderId) {
        Map<String, Object> map = new HashMap<>();
        addAssetUserId(context, map);
        map.put("assetOrderId", assetOrderId);
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .getRepaymentSchedules(map)
                .map(new HljHttpResultFunc<XiaoxiInstallmentSchedulesData>())
                .map(new Func1<XiaoxiInstallmentSchedulesData, XiaoxiInstallmentSchedulesData>() {
                    @Override
                    public XiaoxiInstallmentSchedulesData call(
                            XiaoxiInstallmentSchedulesData schedulesData) {
                        if (schedulesData != null && !schedulesData.isEmpty()) {
                            List<RepaymentSchedule> schedules = schedulesData.getSchedules();
                            for (Iterator iterator = schedules.iterator(); iterator.hasNext(); ) {
                                RepaymentSchedule schedule = (RepaymentSchedule) iterator.next();
                                if (schedule.isClear() && schedule.getAmount() <= 0) {
                                    iterator.remove();
                                }
                            }
                        }
                        return schedulesData;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 提前结清
     *
     * @param context
     * @param assetOrderId 订单ID
     * @return
     */
    public static Observable settleUpObb(Context context, String assetOrderId) {
        Map<String, Object> map = new HashMap<>();
        addAssetUserId(context, map);
        map.put("assetOrderId", assetOrderId);
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .settleUp(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 正常还款
     *
     * @param context
     * @param assetOrderId 订单ID
     * @param stage        所属第几期
     * @return
     */
    public static Observable repayObb(
            Context context, String assetOrderId, int stage) {
        Map<String, Object> map = new HashMap<>();
        addAssetUserId(context, map);
        map.put("assetOrderId", assetOrderId);
        map.put("stage", stage);
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .repay(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 还款预览
     *
     * @param context
     * @param amount
     * @param stageNum
     * @return
     */
    public static Observable<XiaoxiInstallmentPreviewSchedulesData> previewPaybackSchedule(
            Context context, double amount, int stageNum) {
        Map<String, Object> map = new HashMap<>();
        addAssetUserId(context, map);
        map.put("applyAmount", String.valueOf(amount));
        map.put("period", stageNum);
        map.put("stageUnit", 2);// 计息方式，1按日计息 2：按月计息。固定为2
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .previewPaybackSchedule(map)
                .map(new HljHttpResultFunc<XiaoxiInstallmentPreviewSchedulesData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 根据金额返回分期信息
     *
     * @return
     */
    public static Observable<InstallmentData> getInstallmentInfo(double price) {
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .getInstallmentInfo(price)
                .map(new HljHttpResultFunc<InstallmentData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 收单
     *
     * @param context
     * @param orderId
     * @param amount
     * @param stageNum
     * @return
     */
    public static Observable<XiaoxiInstallmentSubmitResult> submitOrder(
            Context context, long orderId, double amount, int stageNum) {
        Map<String, Object> map = new HashMap<>();
        addAssetUserId(context, map);
        map.put("assetOrderId", String.valueOf(orderId));
        map.put("applyAmount", String.valueOf(amount));
        map.put("period", stageNum);
        map.put("stageUnit", 2);// 计息方式，1按日计息 2：按月计息。固定为2
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .submitOrder(map)
                .map(new HljHttpResultFunc<XiaoxiInstallmentSubmitResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 用验证码确认订单
     *
     * @param context
     * @param smsSerialNo
     * @param verifyCode
     * @param assetOrderId
     * @return
     */
    public static Observable<Object> confirmInstallment(
            Context context, String smsSerialNo, String verifyCode, String assetOrderId) {
        Map<String, Object> map = new HashMap<>();
        addAssetUserId(context, map);
        map.put("assetOrderId", String.valueOf(assetOrderId));
        map.put("verifyCode", verifyCode);
        map.put("smsSerialNo", smsSerialNo);
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .confirmInstallment(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 重发验证码
     *
     * @param context
     * @param assetOrderId
     * @param smsSerialNo
     * @return
     */
    public static Observable resendSms(Context context, String assetOrderId, String smsSerialNo) {
        Map<String, Object> map = new HashMap<>();
        addAssetUserId(context, map);
        map.put("assetOrderId", String.valueOf(assetOrderId));
        map.put("smsSerialNo", smsSerialNo);
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .resendSms(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 小犀分期开关
     *
     * @return
     */
    public static Observable<Boolean> isSupportedObb() {
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .isSupported()
                .map(new HljHttpResultFunc<JsonElement>())
                .map(new Func1<JsonElement, Boolean>() {
                    @Override
                    public Boolean call(JsonElement jsonElement) {
                        return CommonUtil.getAsInt(jsonElement, "is_supported") > 0;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 订单债权人查询
     *
     * @param assetOrderId
     * @param pageNo
     * @param pageSize
     * @return
     */
    public static Observable<HljHttpData<List<Debt>>> getDebtsObb(
            String assetOrderId, int pageNo, int pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("assetOrderId", assetOrderId);
        map.put("pageNo", pageNo);
        map.put("pageSize", pageSize);
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .getDebts(map)
                .map(new HljHttpResultFunc<HljHttpData<List<Debt>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 单债权记录查询
     *
     * @param assetOrderId
     * @param pageNo
     * @param pageSize
     * @return
     */
    public static Observable<HljHttpData<List<DebtTransferRecord>>> getDebtTransferRecordsObb(
            String assetOrderId, int pageNo, int pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("assetOrderId", assetOrderId);
        map.put("pageNo", pageNo);
        map.put("pageSize", pageSize);
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .getDebtTransferRecords(map)
                .map(new HljHttpResultFunc<HljHttpData<List<DebtTransferRecord>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 个人资料查询
     *
     * @param context
     * @return
     */
    public static Observable<XiaoxiInstallmentUser> getUserInfoObb(Context context) {
        Map<String, Object> map = new HashMap<>();
        addAssetUserId(context, map);
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .getUserInfo(map)
                .map(new HljHttpResultFunc<XiaoxiInstallmentUser>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 借款使用情况查询
     *
     * @return
     */
    public static Observable<DebtInfo> getDebtInfoObb(String assetOrderId) {
        Map<String, Object> params = new HashMap<>();
        params.put("assetOrderId", assetOrderId);
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .getDebtInfo(params)
                .map(new HljHttpResultFunc<DebtInfo>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 借款使用情况数据提交
     *
     * @param debtInfo
     * @return
     */
    public static Observable submitDebtInfoObb(DebtInfo debtInfo) {
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .submitDebtInfo(debtInfo)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 出借人列表
     *
     * @param assetOrderId
     * @return
     */
    public static Observable<HljHttpData<List<Investor>>> getInvestorsObb(String assetOrderId) {
        Map<String, Object> map = new HashMap<>();
        map.put("assetOrderId", assetOrderId);
        return HljHttp.getRetrofit()
                .create(XiaoxiInstallmentService.class)
                .getInvestors(map)
                .map(new HljHttpResultFunc<HljHttpData<List<Investor>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}