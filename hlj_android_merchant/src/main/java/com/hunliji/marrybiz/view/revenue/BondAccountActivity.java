package com.hunliji.marrybiz.view.revenue;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.revenue.RevenueApi;
import com.hunliji.marrybiz.fragment.revenue.BondBankGetSmsCodeFragment;
import com.hunliji.marrybiz.model.AddressArea;
import com.hunliji.marrybiz.model.revenue.Bank;
import com.hunliji.marrybiz.util.AddressAreaUtil;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.widget.CheckableLinearGroup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kankan.wheel.widget.CitiesPickerView;
import rx.Observable;

/**
 * Created by hua_rong on 2017/8/17 0017
 * 绑定结算账号
 */

public class BondAccountActivity extends HljBaseActivity implements CheckableLinearGroup
        .OnCheckedChangeListener, TextWatcher {


    @BindView(R.id.menu)
    CheckableLinearGroup menu;
    @BindView(R.id.et_bank_user_name)
    EditText etBankUserName;
    @BindView(R.id.et_bank_name)
    EditText etBankName;
    @BindView(R.id.et_bank_account)
    EditText etBankAccount;
    @BindView(R.id.et_bank_account_again)
    EditText etBankAccountAgain;
    @BindView(R.id.bank_address)
    TextView bankAddress;
    @BindView(R.id.et_sub_bank)
    EditText etSubBank;
    @BindView(R.id.bank_info)
    LinearLayout bankInfo;
    @BindView(R.id.et_ali_user_name)
    EditText etAliUserName;
    @BindView(R.id.et_ali_account)
    EditText etAliAccount;
    @BindView(R.id.ali_info)
    LinearLayout aliInfo;
    @BindView(R.id.account_hint)
    TextView accountHint;
    @BindView(R.id.btn_open)
    Button btnOpen;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    private Context context;
    private Dialog backDialog;
    private Dialog areaPickerDlg;
    private LinkedHashMap<String, ArrayList<String>> areaMap;
    private ArrayList<AddressArea> areaLists;
    private AddressArea selectedCity;
    private HljHttpSubscriber subscriber;
    private Bank bank;
    private Bank bankBody;


    private static Handler handler = new Handler();

    private Runnable checkRun = new Runnable() {
        @Override
        public void run() {
            btnOpen.setEnabled(isChecked());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_account);
        ButterKnife.bind(this);
        initView();
        initValue();
        onLoad();
        initError();
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v);
                return false;
            }
        });
    }

    private void initView() {
        menu.setOnCheckedChangeListener(this);
        etBankUserName.addTextChangedListener(this);
        etBankName.addTextChangedListener(this);
        etBankAccount.addTextChangedListener(this);
        etBankAccountAgain.addTextChangedListener(this);
        etSubBank.addTextChangedListener(this);
        etAliAccount.addTextChangedListener(this);
        etAliUserName.addTextChangedListener(this);
    }

    private void initValue() {
        bankBody = new Bank();
        context = this;
        new AddressAreaUtil.GetAddressAreaDataTask(this, new AddressAreaUtil.OnFinishListener() {
            @Override
            public void onFinish(
                    ArrayList<AddressArea> al,
                    LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> am) {
                areaLists = al;
                if (am != null) {
                    areaMap = new LinkedHashMap<>();
                    for (String key : am.keySet()) {
                        ArrayList<String> city = new ArrayList<>();
                        city.addAll(am.get(key)
                                .keySet());
                        areaMap.put(key, city);
                    }
                }
                new AddressAreaUtil.SyncAddressAreaTask(BondAccountActivity.this).execute();
            }

            @Override
            public void onFinish(
                    List<String> l1, List<List<String>> l2, List<List<List<String>>> l3) {

            }
        }).execute();
    }

    private void onLoad() {
        if (subscriber == null || subscriber.isUnsubscribed()) {
            Observable<Bank> observable = RevenueApi.getAppBindBank();
            subscriber = HljHttpSubscriber.buildSubscriber(context)
                    .setOnNextListener(new SubscriberOnNextListener<Bank>() {
                        @Override
                        public void onNext(Bank bank) {
                            BondAccountActivity.this.bank = bank;
                            setView();
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            observable.subscribe(subscriber);
        }
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    private void setView() {
        if (bank != null) {
            int type = bank.getType();
            if (type == 1) {
                etBankUserName.setHint(R.string.hint_bank_user_name);
            } else if (type == 2) {
                etBankUserName.setHint(R.string.hint_bank_user_name2);
            }
            int agent = bank.getAgent();
            bankInfo.setVisibility(agent == 0 ? View.VISIBLE : View.GONE);
            aliInfo.setVisibility(agent == 1 ? View.VISIBLE : View.GONE);
            accountHint.setText(agent == 0 ? R.string.hint_account_bank : R.string
                    .hint_account_ali);
            if (agent == 1) {
                menu.check(R.id.ali);
                etAliAccount.setText(bank.getAccount());
                etAliUserName.setText(bank.getName());
            } else if (agent == 0) {
                etBankAccount.setText(bank.getAccount());
                etBankAccountAgain.setText(bank.getAccount());
                etBankName.setText(bank.getBankName());
                etBankUserName.setText(bank.getName());
                etSubBank.setText(bank.getBank());
                String province = bank.getProvince();
                String cityName = bank.getCityName();
                if (!TextUtils.isEmpty(province) && !TextUtils.isEmpty(cityName)) {
                    bankAddress.setText(province + " " + cityName);
                }
            }
            btnOpen.setEnabled(isChecked());
        }
    }

    private void initError() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onLoad();
            }
        });
    }


    @OnClick(R.id.bank_address)
    public void onSelectAddressArea() {
        if (areaPickerDlg != null && areaPickerDlg.isShowing()) {
            return;
        }
        if (areaMap == null || areaMap.isEmpty()) {
            return;
        }
        if (areaPickerDlg == null) {
            areaPickerDlg = new Dialog(this, R.style.BubbleDialogTheme);
            areaPickerDlg.setContentView(R.layout.dialog_city_picker);
            final CitiesPickerView cityPickerView = (CitiesPickerView) areaPickerDlg.findViewById
                    (R.id.picker);
            cityPickerView.setCityMap(areaMap);
            cityPickerView.getLayoutParams().height = (int) (getResources().getDisplayMetrics()
                    .density * 24 * 8);

            Window win = areaPickerDlg.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = point.x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
            areaPickerDlg.findViewById(R.id.close)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            areaPickerDlg.cancel();
                        }
                    });
            areaPickerDlg.findViewById(R.id.confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int[] selectedItemIndexs = cityPickerView.getSelectedItemIndexs();
                            areaPickerDlg.cancel();
                            AddressArea selectedProvince = areaLists.get(selectedItemIndexs[0]);
                            selectedCity = selectedProvince.getChildren()
                                    .get(selectedItemIndexs[1]);
                            bankAddress.setText(selectedProvince.getAreaName() + "  " +
                                    selectedCity.getAreaName());
                            btnOpen.setEnabled(isChecked());
                        }
                    });
        }
        areaPickerDlg.show();
    }


    @OnClick(R.id.btn_open)
    void onOpen() {
        if (menu.getCheckedRadioButtonId() == R.id.bank && !etBankAccountAgain.getText()
                .toString()
                .equals(etBankAccount.getText()
                        .toString())) {
            Util.showToast(this, null, R.string.msg_bank_account_again_err);
            etBankAccountAgain.setSelection(etBankAccountAgain.length());
            etBankAccountAgain.findFocus();
            return;
        }
        if (menu.getCheckedRadioButtonId() == R.id.ali && !Util.isEmailValid(etAliAccount.getText()
                .toString()) && !Util.isMobileNO(etAliAccount.getText()
                .toString())) {
            Util.showToast(this, null, R.string.msg_ali_account_err);
            etAliAccount.setSelection(etAliAccount.length());
            etAliAccount.findFocus();
            return;
        }
        switch (menu.getCheckedRadioButtonId()) {
            case R.id.bank:
                bankBody.setAccount(etBankAccount.getText()
                        .toString());
                bankBody.setAgent(0);
                bankBody.setBank(etSubBank.getText()
                        .toString());
                bankBody.setBankName(etBankName.getText()
                        .toString());
                bankBody.setName(etBankUserName.getText()
                        .toString());
                if (selectedCity != null) {
                    bankBody.setBankCid(selectedCity.getCid());
                }
                break;
            case R.id.ali:
                bankBody.setAgent(1);
                bankBody.setName(etAliUserName.getText()
                        .toString());
                bankBody.setAccount(etAliAccount.getText()
                        .toString());
                break;
        }
        if (bank != null) {
            bankBody.setChangeProtocol(bank.getChangeProtocol());
        }
        BondBankGetSmsCodeFragment checkUserFragment = BondBankGetSmsCodeFragment
                .newInstance(
                bankBody);
        checkUserFragment.show(getSupportFragmentManager(), "BondBankGetSmsCodeFragment");
    }


    private boolean isChecked() {
        synchronized (this) {
            switch (menu.getCheckedRadioButtonId()) {
                case R.id.bank:
                    if (etBankUserName.length() == 0) {
                        return false;
                    }
                    if (etBankAccount.length() == 0) {
                        return false;
                    }
                    if (etBankName.length() == 0) {
                        return false;
                    }
                    if (bankAddress.length() == 0) {
                        return false;
                    }
                    if (etBankAccountAgain.length() == 0) {
                        return false;
                    }
                    if (etSubBank.length() == 0) {
                        return false;
                    }
                    break;
                case R.id.ali:
                    if (etAliAccount.length() == 0) {
                        return false;
                    }
                    if (etAliUserName.length() == 0) {
                        return false;
                    }
                    break;
            }
            return true;
        }
    }


    @Override
    public void onCheckedChanged(CheckableLinearGroup group, int checkedId) {
        bankInfo.setVisibility(checkedId == R.id.bank ? View.VISIBLE : View.GONE);
        aliInfo.setVisibility(checkedId == R.id.ali ? View.VISIBLE : View.GONE);
        accountHint.setText(checkedId == R.id.bank ? R.string.hint_account_bank : R.string
                .hint_account_ali);
        btnOpen.setEnabled(isChecked());
        hideKeyboard(null);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        handler.removeCallbacks(checkRun);
        handler.postDelayed(checkRun, 300);

    }


    @Override
    public void onBackPressed() {
        if (isChange()) {
            if (backDialog != null && backDialog.isShowing()) {
                return;
            }
            if (backDialog == null) {
                backDialog = new Dialog(this, R.style.BubbleDialogTheme);
                backDialog.setContentView(R.layout.dialog_confirm_notice);
                TextView noticeMsg = (TextView) backDialog.findViewById(R.id.tv_notice_msg);
                noticeMsg.setText(R.string.hint_open_trade_back);
                backDialog.findViewById(R.id.btn_notice_confirm)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                backDialog.dismiss();
                                BondAccountActivity.super.onBackPressed();
                            }
                        });
                backDialog.findViewById(R.id.btn_notice_cancel)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                backDialog.cancel();
                            }
                        });
                Window window = backDialog.getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                Point point = CommonUtil.getDeviceSize(this);
                params.width = Math.round(point.x * 27 / 32);
                window.setAttributes(params);
            }
            backDialog.show();
            return;
        }
        super.onBackPressed();
    }

    public boolean isChange() {
        if (bank != null) {
            if (bank.getAgent() == 1 && menu.getCheckedRadioButtonId() == R.id.ali) {
                if (!JSONUtil.isEmpty(bank.getAccount()) && !bank.getAccount()
                        .equals(etAliAccount.getText()
                                .toString())) {
                    return true;
                }
                if (!JSONUtil.isEmpty(bank.getName()) && !bank.getName()
                        .equals(etAliUserName.getText()
                                .toString())) {
                    return true;
                }

            } else if (bank.getAgent() == 0 && menu.getCheckedRadioButtonId() == R.id.bank) {
                if (!JSONUtil.isEmpty(bank.getAccount()) && !bank.getAccount()
                        .equals(etBankAccount.getText()
                                .toString())) {
                    return true;
                }
                if (!JSONUtil.isEmpty(bank.getName()) && !bank.getName()
                        .equals(etBankUserName.getText()
                                .toString())) {
                    return true;
                }
                if (!JSONUtil.isEmpty(bank.getBankName()) && !bank.getBankName()
                        .equals(etBankUserName.getText()
                                .toString())) {
                    return true;
                }
                if (!JSONUtil.isEmpty(bank.getBank()) && !bank.getBank()
                        .equals(etSubBank.getText()
                                .toString())) {
                    return true;
                }
            } else {
                return true;
            }
        } else {
            switch (menu.getCheckedRadioButtonId()) {
                case R.id.bank:
                    if (etBankUserName.length() > 0) {
                        return true;
                    }
                    if (etBankAccount.length() > 0) {
                        return true;
                    }
                    if (etBankName.length() > 0) {
                        return true;
                    }
                    if (bankAddress.length() > 0) {
                        return true;
                    }
                    if (etBankAccountAgain.length() > 0) {
                        return true;
                    }
                    if (etSubBank.length() > 0) {
                        return true;
                    }
                    break;
                case R.id.ali:
                    if (etAliAccount.length() > 0) {
                        return true;
                    }
                    if (etAliUserName.length() > 0) {
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(subscriber);
    }
}
