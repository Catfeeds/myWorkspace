package com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.ExtInfo;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.XiaoxiInstallmentUser;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 小犀分期-基本信息-更多信息
 * Created by chen_bin on 2017/8/10 0010.
 */
public class AddExtUserInfoActivity extends HljBaseActivity {

    @BindView(R2.id.tv_industries)
    TextView tvIndustries;
    @BindView(R2.id.tv_nature_of_work)
    TextView tvNatureOfWork;
    @BindView(R2.id.tv_bulk_property)
    TextView tvBulkProperty;
    @BindView(R2.id.tv_payment_src)
    TextView tvPaymentSrc;
    @BindView(R2.id.tv_others_loan)
    TextView tvOthersLoan;
    @BindView(R2.id.tv_debts)
    TextView tvDebts;
    @BindView(R2.id.tv_credit_info)
    TextView tvCreditInfo;
    @BindView(R2.id.scroll_view)
    ScrollView scrollView;

    private Dialog industriesDialog;
    private Dialog natureOfWorkDialog;
    private Dialog bulkPropertyDialog;
    private Dialog paymentSrcDialog;
    private Dialog othersLoanDialog;
    private Dialog debtsDialog;
    private Dialog creditInfoDialog;

    private List<String> industries;
    private List<String> natureOfWorks;
    private List<String> bulkProperties;
    private List<String> paymentSrcs;
    private List<String> othersLoans;
    private List<String> debts;
    private List<String> creditInfos;

    private XiaoxiInstallmentUser user;

    private int industriesIndex = -1;
    private int natureOfWorkIndex = -1;
    private int bulkPropertyIndex = -1;
    private int paymentSrcIndex = -1;
    private int othersLoanIndex = -1;
    private int debtsIndex = -1;
    private int creditInfoIndex = -1;

    public final static String ARG_USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ext_user_info___pay);
        ButterKnife.bind(this);
        initValues();
    }

    private void initValues() {
        setOkText(R.string.label_save___cm);
        user = getIntent().getParcelableExtra(ARG_USER);
        industries = Arrays.asList(getResources().getStringArray(R.array.industries));
        natureOfWorks = Arrays.asList(getResources().getStringArray(R.array.natureOfWorks));
        bulkProperties = Arrays.asList(getResources().getStringArray(R.array.bulkProperties));
        paymentSrcs = Arrays.asList(getResources().getStringArray(R.array.paymentSrcs));
        othersLoans = Arrays.asList(getResources().getStringArray(R.array.otherLoans));
        debts = Arrays.asList(getResources().getStringArray(R.array.debts));
        creditInfos = Arrays.asList(getResources().getStringArray(R.array.creditInfos));

        ExtInfo extInfo = user.getExtInfo();
        industriesIndex = extInfo.getIndustries() - 1;
        natureOfWorkIndex = extInfo.getNatureOfWork() - 1;
        bulkPropertyIndex = extInfo.getBulkProperty() - 1;
        paymentSrcIndex = extInfo.getPaymentSrc() - 1;
        othersLoanIndex = extInfo.getOthersLoan() - 1;
        debtsIndex = extInfo.getDebts() - 1;
        creditInfoIndex = extInfo.getCreditInfoNearlySixMonth() - 1;

        if (industriesIndex >= 0 && industriesIndex < industries.size()) {
            tvIndustries.setText(industries.get(industriesIndex));
        }
        if (natureOfWorkIndex >= 0 && natureOfWorkIndex < natureOfWorks.size()) {
            tvNatureOfWork.setText(natureOfWorks.get(natureOfWorkIndex));
        }
        if (bulkPropertyIndex >= 0 && bulkPropertyIndex < bulkProperties.size()) {
            tvBulkProperty.setText(bulkProperties.get(bulkPropertyIndex));
        }
        if (paymentSrcIndex >= 0 && paymentSrcIndex < paymentSrcs.size()) {
            tvPaymentSrc.setText(paymentSrcs.get(paymentSrcIndex));
        }
        if (othersLoanIndex >= 0 && othersLoanIndex < othersLoans.size()) {
            tvOthersLoan.setText(othersLoans.get(othersLoanIndex));
        }
        if (debtsIndex >= 0 && debtsIndex < debts.size()) {
            tvDebts.setText(debts.get(debtsIndex));
        }
        if (creditInfoIndex >= 0 && creditInfoIndex < creditInfos.size()) {
            tvCreditInfo.setText(creditInfos.get(creditInfoIndex));
        }
    }

    @OnClick(R2.id.industries_layout)
    void onIndustries() {
        if (industriesDialog != null && industriesDialog.isShowing()) {
            return;
        }
        if (industriesDialog == null) {
            industriesDialog = DialogUtil.createSingleWheelPickerDialog(this,
                    industries,
                    Math.max(0, industriesIndex),
                    new DialogUtil.OnWheelSelectedListener() {
                        @Override
                        public void onWheelSelected(int position, String str) {
                            industriesIndex = position;
                            tvIndustries.setText(str);
                        }
                    });
        }
        industriesDialog.show();
    }

    @OnClick(R2.id.nature_of_work_layout)
    void onNatureOfWork() {
        if (natureOfWorkDialog != null && natureOfWorkDialog.isShowing()) {
            return;
        }
        if (natureOfWorkDialog == null) {
            natureOfWorkDialog = DialogUtil.createSingleWheelPickerDialog(this,
                    natureOfWorks,
                    Math.max(0, natureOfWorkIndex),
                    new DialogUtil.OnWheelSelectedListener() {

                        @Override
                        public void onWheelSelected(int position, String str) {
                            natureOfWorkIndex = position;
                            tvNatureOfWork.setText(str);
                        }
                    });
        }
        natureOfWorkDialog.show();
    }

    @OnClick(R2.id.bulk_property_layout)
    void onBulkProperty() {
        if (bulkPropertyDialog != null && bulkPropertyDialog.isShowing()) {
            return;
        }
        if (bulkPropertyDialog == null) {
            bulkPropertyDialog = DialogUtil.createSingleWheelPickerDialog(this,
                    bulkProperties,
                    Math.max(0, bulkPropertyIndex),
                    new DialogUtil.OnWheelSelectedListener() {

                        @Override
                        public void onWheelSelected(int position, String str) {
                            bulkPropertyIndex = position;
                            tvBulkProperty.setText(str);
                        }
                    });
        }
        bulkPropertyDialog.show();
    }

    @OnClick(R2.id.payment_src_layout)
    void onPaymentSrc() {
        if (paymentSrcDialog != null && paymentSrcDialog.isShowing()) {
            return;
        }
        if (paymentSrcDialog == null) {
            paymentSrcDialog = DialogUtil.createSingleWheelPickerDialog(this,
                    paymentSrcs,
                    Math.max(0, paymentSrcIndex),
                    new DialogUtil.OnWheelSelectedListener() {

                        @Override
                        public void onWheelSelected(int position, String str) {
                            paymentSrcIndex = position;
                            tvPaymentSrc.setText(str);
                        }
                    });
        }
        paymentSrcDialog.show();
    }

    @OnClick(R2.id.others_loan_layout)
    void onOthersLoan() {
        if (othersLoanDialog != null && othersLoanDialog.isShowing()) {
            return;
        }
        if (othersLoanDialog == null) {
            othersLoanDialog = DialogUtil.createSingleWheelPickerDialog(this,
                    othersLoans,
                    Math.max(0, othersLoanIndex),
                    new DialogUtil.OnWheelSelectedListener() {
                        @Override
                        public void onWheelSelected(int position, String str) {
                            othersLoanIndex = position;
                            tvOthersLoan.setText(str);
                        }
                    });
        }
        othersLoanDialog.show();
    }

    @OnClick(R2.id.debts_layout)
    void onDebts() {
        if (debtsDialog != null && debtsDialog.isShowing()) {
            return;
        }
        if (debtsDialog == null) {
            debtsDialog = DialogUtil.createSingleWheelPickerDialog(this,
                    debts,
                    Math.max(0, debtsIndex),
                    new DialogUtil.OnWheelSelectedListener() {
                        @Override
                        public void onWheelSelected(int position, String str) {
                            debtsIndex = position;
                            tvDebts.setText(str);
                        }
                    });
        }
        debtsDialog.show();
    }

    @OnClick(R2.id.credit_info_layout)
    void onCreditInfo() {
        if (creditInfoDialog != null && creditInfoDialog.isShowing()) {
            return;
        }
        if (creditInfoDialog == null) {
            creditInfoDialog = DialogUtil.createSingleWheelPickerDialog(this,
                    creditInfos,
                    Math.max(0, creditInfoIndex),
                    new DialogUtil.OnWheelSelectedListener() {
                        @Override
                        public void onWheelSelected(int position, String str) {
                            creditInfoIndex = position;
                            tvCreditInfo.setText(str);
                        }
                    });
        }
        creditInfoDialog.show();
    }

    @Override
    public void onOkButtonClick() {
        if (user == null) {
            return;
        }
        int industries = industriesIndex + 1;
        if (industries <= 0) {
            industries = ExtInfo.DEFAULT_INDUSTRIES;
        }
        int natureOfWork = natureOfWorkIndex + 1;
        if (natureOfWork <= 0) {
            natureOfWork = ExtInfo.DEFAULT_NATURE_OF_WORK;
        }
        int bulkProperty = bulkPropertyIndex + 1;
        if (bulkProperty <= 0) {
            bulkProperty = ExtInfo.DEFAULT_BULK_PROPERTY;
        }
        int paymentSrc = paymentSrcIndex + 1;
        if (paymentSrcIndex <= 0) {
            paymentSrc = ExtInfo.DEFAULT_PAYMENT_SRC;
        }
        int othersLoan = othersLoanIndex + 1;
        if (othersLoan <= 0) {
            othersLoan = ExtInfo.DEFAULT_OTHERS_LOAN;
        }
        int debts = debtsIndex + 1;
        if (debts <= 0) {
            debts = ExtInfo.DEFAULT_DEBTS;
        }
        int creditInfo = creditInfoIndex + 1;
        if (creditInfo <= 0) {
            creditInfo = ExtInfo.DEFAULT_CREDIT_INFO_NEARLY_SIX_MONTH;
        }
        ExtInfo extInfo = user.getExtInfo();
        extInfo.setIndustries(industries);
        extInfo.setNatureOfWork(natureOfWork);
        extInfo.setBulkProperty(bulkProperty);
        extInfo.setPaymentSrc(paymentSrc);
        extInfo.setOthersLoan(othersLoan);
        extInfo.setDebts(debts);
        extInfo.setCreditInfoNearlySixMonth(creditInfo);

        Intent intent = getIntent();
        intent.putExtra(AddExtUserInfoActivity.ARG_USER, user);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }
}