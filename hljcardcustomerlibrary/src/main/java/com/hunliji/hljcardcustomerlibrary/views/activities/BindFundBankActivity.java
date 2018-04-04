package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.adapter.FundBankListAdapter;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.models.BindFundBank;
import com.hunliji.hljcommonlibrary.models.BankCard;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo_yu on 2017/11/23.理财绑定银行卡
 */

public class BindFundBankActivity extends HljBaseActivity {

    public static final int CREDIT_CARD_TYPE = 1;//信用卡
    public static final int CASH_CARD_TYPE = 2;//储蓄卡

    @BindView(R2.id.et_bank_no)
    EditText etBankNo;
    @BindView(R2.id.et_user_name)
    EditText etUserName;
    @BindView(R2.id.et_user_phone)
    EditText etUserPhone;
    @BindView(R2.id.et_id_card)
    EditText etIdCard;
    @BindView(R2.id.tv_bank_name)
    TextView tvBankName;
    @BindView(R2.id.tv_submit)
    TextView tvSubmit;

    private Dialog bankListDialog;
    private Dialog checkDialog;
    private List<BankCard> banks;
    private HljHttpSubscriber initSubscriber;
    private HljHttpSubscriber bindSubscriber;
    private HljHttpSubscriber verifySubscriber;
    private HljHttpSubscriber checkSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_bind_bank);
        ButterKnife.bind(this);
        initValue();
        getFundBankList();
    }

    private void getFundBankList() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<List<BankCard>>() {
                        @Override
                        public void onNext(List<BankCard> list) {
                            banks.clear();
                            banks.addAll(list);
                        }
                    })
                    .build();
            CustomerCardApi.getFundBankListObb()
                    .subscribe(initSubscriber);
        }
    }

    private void initValue() {
        banks = new ArrayList<>();
    }

    @OnClick(R2.id.tv_bank_name)
    public void onSelectBank() {
        if (bankListDialog != null && bankListDialog.isShowing()) {
            return;
        }
        if (bankListDialog == null) {
            bankListDialog = new Dialog(this, R.style.BubbleDialogTheme);
            bankListDialog.setContentView(R.layout.dialog_select_fund_bank_list);
            bankListDialog.findViewById(R.id.btn_back)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bankListDialog.dismiss();
                        }
                    });
            ListView listView = bankListDialog.findViewById(R.id.bank_list);
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setAdapter(new FundBankListAdapter(banks));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BankCard bank = (BankCard) parent.getAdapter()
                            .getItem(position);
                    if (bank != null) {
                        bankListDialog.dismiss();
                        tvBankName.setText(bank.getBankName());
                    }
                }
            });
            Window win = bankListDialog.getWindow();
            if (win != null) {
                WindowManager.LayoutParams params = win.getAttributes();
                Point point = CommonUtil.getDeviceSize(this);
                params.width = point.x;
                win.setGravity(Gravity.BOTTOM);
                win.setWindowAnimations(R.style.dialog_anim_rise_style);
            }
        }
        bankListDialog.show();
    }

    @OnClick(R2.id.tv_submit)
    public void submit() {
        if (tvBankName.length() == 0) {
            ToastUtil.showToast(this, "请选择银行名称", 0);
            return;
        }
        if (etBankNo.length() == 0) {
            ToastUtil.showToast(this, "请输入银行储蓄卡号", 0);
            return;
        }
        if (etBankNo.length() < 16 || etBankNo.length() > 19) {
            ToastUtil.showToast(this, "请输入正确的银行卡号", 0);
            return;
        }
        if (etUserName.length() == 0) {
            ToastUtil.showToast(this, "请输入您的姓名", 0);
            return;
        }
        if (etUserName.length() <= 1) {
            ToastUtil.showToast(this, "请输入正确的姓名", 0);
            return;
        }
        if (etUserPhone.length() == 0) {
            ToastUtil.showToast(this, "请输入银行预留手机号", 0);
            return;
        }
        if (!CommonUtil.isMobileNO(etUserPhone.getText()
                .toString())) {
            ToastUtil.showToast(this, "请输入正确的手机号", 0);
            return;
        }
        if (etIdCard.length() == 0) {
            ToastUtil.showToast(this, "请输入您的身份证号", 0);
            return;
        }
        if (!CommonUtil.validIdStr(etIdCard.getText()
                .toString())) {
            ToastUtil.showToast(this, "请输入正确的身份证号", 0);
            return;
        }
        String accNo = etBankNo.getText()
                .toString();
        String idHolder = etUserName.getText()
                .toString();
        verifyBankCard(accNo, idHolder);
    }

    //验证银行卡是否可用
    private void verifyBankCard(final String accNo, final String idHolder) {
        if (verifySubscriber == null || verifySubscriber.isUnsubscribed()) {
            verifySubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                        @Override
                        public void onNext(JsonElement jsonElement) {
                            try {
                                int cardType = jsonElement.getAsJsonObject()
                                        .get("card_type")
                                        .getAsInt();
                                if (cardType == CASH_CARD_TYPE) {
                                    checkCardName(accNo, idHolder);
                                } else {
                                    ToastUtil.showToast(BindFundBankActivity.this,
                                            "只支持储蓄卡，请重新输入银行卡号",
                                            0);
                                }
                            } catch (Exception e) {
                                ToastUtil.showToast(BindFundBankActivity.this, "请稍后再试", 0);
                            }
                        }
                    })
                    .build();
            CustomerCardApi.getBankCardInfoObb(accNo)
                    .subscribe(verifySubscriber);
        }
    }

    private void checkCardName(final String accNo, final String idHolder) {
        if (checkSubscriber == null || checkSubscriber.isUnsubscribed()) {
            checkSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                        @Override
                        public void onNext(JsonElement jsonElement) {
                            try {
                                boolean canUseCardMoney = jsonElement.getAsJsonObject()
                                        .get("can_use_card_money")
                                        .getAsInt() > 0;
                                if (!canUseCardMoney) {
                                    bindFundBank(accNo, idHolder);
                                } else {
                                    if (checkDialog != null && checkDialog.isShowing()) {
                                        return;
                                    }
                                    if (checkDialog == null) {
                                        checkDialog = DialogUtil.createDoubleButtonDialog(
                                                BindFundBankActivity.this,
                                                "确认姓名",
                                                "您填写的姓名与请帖中不一致，礼金将无法转入，确认继续绑定？",
                                                null,
                                                null,
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        bindFundBank(accNo, idHolder);
                                                    }
                                                },
                                                null);
                                    }
                                    checkDialog.show();
                                }
                            } catch (Exception e) {
                                ToastUtil.showToast(BindFundBankActivity.this, "请稍后再试", 0);
                            }
                        }
                    })
                    .build();
            CustomerCardApi.checkCardNameObb(idHolder)
                    .subscribe(checkSubscriber);
        }
    }

    private void bindFundBank(String accNo, String idHolder) {
        if (bindSubscriber == null || bindSubscriber.isUnsubscribed()) {
            String idCard = etIdCard.getText()
                    .toString();
            String mobile = etUserPhone.getText()
                    .toString();
            bindSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<BindFundBank>() {
                        @Override
                        public void onNext(BindFundBank bindFundBank) {
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType.BIND_FUND_BANK_SUCCESS,
                                            null));
                            Intent intent = new Intent();
                            if (bindFundBank.isCanUseCardMoney()) {
                                //能使用请帖余额，跳转到礼金转入理财页面
                                intent.setClass(BindFundBankActivity.this,
                                        CardRollInFundActivity.class);
                            } else {
                                //跳转到银行卡转入理财页面
                                intent.setClass(BindFundBankActivity.this,
                                        BankRollInFundActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .build();
            CustomerCardApi.bindFundBankObb(accNo, idCard, idHolder, mobile)
                    .subscribe(bindSubscriber);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSubscriber,
                bindSubscriber,
                verifySubscriber,
                checkSubscriber);
    }
}
