package com.hunliji.hljpaymentlibrary.api.xiaoxi_installment;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.Bank;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.CreditLimit;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.Debt;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.DebtTransferRecord;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.InstallmentData;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.Investor;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.DebtInfo;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.XiaoxiInstallmentSubmitResult;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.XiaoxiInstallmentUser;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers
        .XiaoxiInstallmentAuthItemsData;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers.XiaoxiInstallmentOrdersData;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers
        .XiaoxiInstallmentPreviewSchedulesData;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers
        .XiaoxiInstallmentSchedulesData;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 小犀分期相关请求
 * Created by chen_bin on 2016/12/6 0006.
 */
public interface XiaoxiInstallmentService {

    /**
     * 认证项查询
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/risk/verify/query")
    Observable<HljHttpResult<XiaoxiInstallmentAuthItemsData>> getAuthItems(
            @Body Map<String, Object> map);

    /**
     * 获取银行列表
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APINutGate/bankList")
    Observable<HljHttpResult<List<Bank>>> getBanks();

    /**
     * 协议预览
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/agreement/preview")
    Observable<HljHttpResult<JsonElement>> getPreviewAgreementByType(@Body Map<String, Object> map);

    /**
     * 协议查询
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/agreement/query")
    Observable<HljHttpResult<JsonElement>> getAgreementByType(@Body Map<String, Object> map);

    /**
     * 实名认证
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/account/realname/verify")
    Observable<HljHttpResult> authorizeRealName(@Body Map<String, Object> map);

    /**
     * 实名信息
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APINutGate/realName")
    Observable<HljHttpResult<JsonElement>> getRealName();

    /**
     * 实名绑卡
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/account/card/bind")
    Observable<HljHttpResult<JsonElement>> bindBankCard(@Body Map<String, Object> map);

    /**
     * 签约绑卡验证
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/account/card/verify")
    Observable<HljHttpResult> verifyBankCard(@Body Map<String, Object> map);

    /**
     * 绑卡信息
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APINutGate/bank")
    Observable<HljHttpResult<JsonElement>> getBankCardBindedInfo();

    /**
     * 个人资料保存
     *
     * @param user
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/risk/userInfo/upload")
    Observable<HljHttpResult> uploadUserInfo(@Body XiaoxiInstallmentUser user);

    /**
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/risk/credit/query")
    Observable<HljHttpResult<CreditLimit>> getCreditLimit(@Body Map<String, Object> map);

    /**
     * 我的账单列表
     *
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/order/query/list")
    Observable<HljHttpResult<XiaoxiInstallmentOrdersData>> getMyBills(
            @Body Map<String, Object> map);

    /**
     * 还款计划列表
     *
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/schedule/query")
    Observable<HljHttpResult<XiaoxiInstallmentSchedulesData>> getRepaymentSchedules(@Body Map<String, Object> map);

    /**
     * 提前结清
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/repayment/settleUp")
    Observable<HljHttpResult> settleUp(@Body Map<String, Object> map);

    /**
     * 正常还款
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/repayment/repay")
    Observable<HljHttpResult> repay(@Body Map<String, Object> map);

    /**
     * 根据金额返回分期信息
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APISetMeal/installmentPrice")
    Observable<HljHttpResult<InstallmentData>> getInstallmentInfo(@Query("price") double price);

    /**
     * 预览还款计划
     *
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/schedule/preview")
    Observable<HljHttpResult<XiaoxiInstallmentPreviewSchedulesData>> previewPaybackSchedule(@Body Map<String, Object> map);

    /**
     * 收单
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/order/submit")
    Observable<HljHttpResult<XiaoxiInstallmentSubmitResult>> submitOrder(
            @Body Map<String, Object> map);

    /**
     * 确认订单
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/order/confirm")
    Observable<HljHttpResult> confirmInstallment(@Body Map<String, Object> map);

    /**
     * 重发验证码
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/order/vc/resend")
    Observable<HljHttpResult> resendSms(@Body Map<String, Object> map);

    /**
     * 小犀分期开关
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APINutGate/isSupported")
    Observable<HljHttpResult<JsonElement>> isSupported();

    /**
     * 订单债权人查询
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/order/debt/query")
    Observable<HljHttpResult<HljHttpData<List<Debt>>>> getDebts(@Body Map<String, Object> map);

    /**
     * 订单债权记录查询
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/order/debt/transfer/records")
    Observable<HljHttpResult<HljHttpData<List<DebtTransferRecord>>>> getDebtTransferRecords(@Body Map<String, Object> map);

    /**
     * 个人资料查询
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/risk/userInfo/query")
    Observable<HljHttpResult<XiaoxiInstallmentUser>> getUserInfo(@Body Map<String, Object> map);

    /**
     * 借款使用情况查询
     *
     * @param params
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/order/debt/info/query")
    Observable<HljHttpResult<DebtInfo>> getDebtInfo(@Body Map<String, Object> params);

    /**
     * 借款使用情况数据提交
     *
     * @param declare
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/order/debt/info/submit")
    Observable<HljHttpResult> submitDebtInfo(@Body DebtInfo declare);

    /**
     * 出借人列表
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APINutGate/proxy?action=v1/loan/order/investor/list")
    Observable<HljHttpResult<HljHttpData<List<Investor>>>> getInvestors(
            @Body Map<String, Object> map);
}
