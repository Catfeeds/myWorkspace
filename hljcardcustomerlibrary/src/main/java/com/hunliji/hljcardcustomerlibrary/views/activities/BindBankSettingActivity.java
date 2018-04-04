package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.google.gson.Gson;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcardlibrary.models.CardRxEvent;
import com.hunliji.hljcommonlibrary.models.BindInfo;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.Label;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.AddSpaceTextWatcher;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kankan.wheel.widget.CitiesPickerView;
import rx.Subscription;

/**
 * Created by mo_yu on 2017/6/12.银行卡绑定设置页
 */

public class BindBankSettingActivity extends HljBaseActivity {

    @BindView(R2.id.edit_bank_card)
    EditText editBankCard;
    @BindView(R2.id.tv_bank_address)
    TextView tvBankAddress;
    @BindView(R2.id.tv_support_bank)
    TextView tvSupportBank;
    @BindView(R2.id.btn_bind_bank)
    Button btnBindBank;
    @BindView(R2.id.unbind_bank_layout)
    LinearLayout unbindBankLayout;
    @BindView(R2.id.tv_bind_user_name)
    TextView tvBindUserName;
    @BindView(R2.id.withdraw_info_layout)
    LinearLayout withdrawInfoLayout;
    @BindView(R2.id.img_bind_bank)
    ImageView imgBindBank;
    @BindView(R2.id.tv_bind_bank_card_no)
    TextView tvBindBankCardNo;
    @BindView(R2.id.tv_bind_bank_address)
    TextView tvBindBankAddress;
    @BindView(R2.id.bind_bank_layout)
    LinearLayout bindBankLayout;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.tv_hint)
    TextView tvHint;
    @BindView(R2.id.tv_bind_bank_tip)
    TextView tvBindBankTip;
    @BindView(R2.id.edit_holder_name)
    EditText etHolderName;
    @BindView(R2.id.tv_back_wx_bind)
    TextView tvBackWxBind;

    private Card card;
    private long cardId;
    private boolean isCanModifyName;
    private boolean isFromWx;

    private String accNo;
    private String idHolder;
    private String address;
    private long cid;
    private int logoWidth;
    private BindInfo bindInfo;
    private Dialog cityPickerDlg;
    private ArrayList<Label> provinceNameList;
    private LinkedHashMap<Integer, ArrayList<City>> cityMap;
    private LinkedHashMap<String, ArrayList<String>> cityStrMap;
    private City selectedCity;
    private Label selectedProvince;
    private HljHttpSubscriber bindSubscriber;
    private HljHttpSubscriber unbindSubscriber;
    private Subscription rxBusEventSub;
    private Subscription cardRxBusSubscription;
    private Dialog failDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_bank_setting___card);
        ButterKnife.bind(this);
        initValue();
        initView();
        refreshBindView();
        registerRxBusEvent();
    }

    private void initView() {
        String tip = getString(R.string.label_bind_bank_tip___card);
        int insuranceStart = tip.indexOf("请绑定与请帖姓名相同的银行卡");
        SpannableString sp = new SpannableString(tip);
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#f83244")),
                insuranceStart,
                insuranceStart + 14,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tvBindBankTip.setText(sp);
        if (HljCard.isCustomer(this)) {
            tvBackWxBind.setVisibility(View.VISIBLE);
        } else {
            tvBackWxBind.setVisibility(View.GONE);
        }
        //银行卡EditText
        AddSpaceTextWatcher watcher = new AddSpaceTextWatcher(editBankCard, 30);
        watcher.setSpaceType(AddSpaceTextWatcher.SpaceType.bankCardNumberType);
    }

    private void initValue() {
        card = getIntent().getParcelableExtra("card");
        if (card != null) {
            cardId = card.getId();
        }
        bindInfo = getIntent().getParcelableExtra("bind_info");
        isCanModifyName = getIntent().getBooleanExtra("can_modify_name", true);
        isFromWx = getIntent().getBooleanExtra("is_from_wx", false);
        logoWidth = CommonUtil.dp2px(this, 20);
        getCitiesFromFile();
    }

    private void refreshBindView() {
        if (bindInfo == null) {
            setTitle("提现设置");
            unbindBankLayout.setVisibility(View.VISIBLE);
            bindBankLayout.setVisibility(View.GONE);
            City city = LocationSession.getInstance()
                    .getCity(this);
            if (city != null) {
                if (city.getCid() != 0) {
                    selectedCity = city;
                    cid = selectedCity.getCid();
                }
                tvBankAddress.setText(city.getCid() == 0 ? "" : city.getName());
            }
        } else {
            setTitle("银行卡提现");
            unbindBankLayout.setVisibility(View.GONE);
            bindBankLayout.setVisibility(View.VISIBLE);
            tvBindUserName.setText(bindInfo.getIdHolder());
            tvBindBankCardNo.setText(getString(R.string.format_bind_info___card,
                    bindInfo.getBankDesc(),
                    bindInfo.getAccNo()));
            tvBindBankAddress.setText(bindInfo.getCity() != null ? bindInfo.getCity()
                    .getName() : "");
            tvHint.setText(CommonUtil.fromHtml(this,
                    getString(R.string.label_switch_cash_hint___card)));
            Glide.with(this)
                    .load(ImagePath.buildPath(bindInfo.getBankLogo())
                            .width(logoWidth)
                            .height(logoWidth)
                            .cropPath())
                    .into(imgBindBank);
        }
    }

    private void bindBank() {
        if (bindSubscriber == null || bindSubscriber.isUnsubscribed()) {
            bindSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<BindInfo>>() {
                        @Override
                        public void onNext(HljHttpResult<BindInfo> bindInfoHljHttpResult) {
                            if (bindInfoHljHttpResult.getStatus()
                                    .getRetCode() == 0) {
                                bindInfo = bindInfoHljHttpResult.getData();

                                refreshBindView();
                                RxBus.getDefault()
                                        .post(new RxEvent(RxEvent.RxEventType.BIND_BANK_SUCCESS,
                                                null));
                            } else if (bindInfoHljHttpResult.getStatus()
                                    .getRetCode() == 2002) {
                                // 修改失败，弹窗显示修改请帖署
                                showModifyNameDlg();
                            } else {
                                Toast.makeText(BindBankSettingActivity.this,
                                        bindInfoHljHttpResult.getStatus()
                                                .getMsg(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            CustomerCardApi.bindBankObb(accNo, cardId, cid, idHolder)
                    .subscribe(bindSubscriber);
        }
    }

    private void showModifyNameDlg() {
        if (failDlg == null) {
            failDlg = new Dialog(this, R.style.BubbleDialogTheme);
            failDlg.setContentView(R.layout.dialog_bind_bank_fail);
            Window window = failDlg.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = CommonUtil.getDeviceSize(this);
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
        }

        Button btnModifyName = failDlg.findViewById(R.id.btn_modify_name);
        Button btnCancel = failDlg.findViewById(R.id.btn_cancel);
        btnModifyName.setText("修改请帖姓名");
        // 是否可修改的判断，到修改详情页面去判断和提示

        btnModifyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failDlg.cancel();
                ARouter.getInstance()
                        .build(RouterPath.IntentPath.Card.CARD_INFO_EDIT)
                        .withParcelable("card", card)
                        .navigation(BindBankSettingActivity.this);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failDlg.cancel();
            }
        });

        failDlg.show();
    }

    private void getCitiesFromFile() {
        provinceNameList = new ArrayList<>();
        cityMap = new LinkedHashMap<>();
        cityStrMap = new LinkedHashMap<>();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(CommonUtil.readStreamToString(getResources()
                    .openRawResource(
                    R.raw.cities)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObject != null) {
            JSONArray provinceArray = jsonObject.optJSONArray("provinces");
            if (provinceArray != null && provinceArray.length() > 0) {
                for (int i = 0; i < provinceArray.length(); i++) {
                    JSONObject provinceObj = provinceArray.optJSONObject(i);
                    Gson gson = GsonUtil.getGsonInstance();
                    Label provinceMenuItem = gson.fromJson(provinceObj.toString(), Label.class);
                    ArrayList<City> cityArrayList = new ArrayList<>();
                    ArrayList<String> cityStrList = new ArrayList<>();
                    JSONArray cityArray = provinceObj.optJSONArray("cities");
                    if (cityArray != null && cityArray.length() > 0) {
                        for (int j = 0; j < cityArray.length(); j++) {
                            City city = gson.fromJson(cityArray.optJSONObject(j)
                                    .toString(), City.class);
                            cityArrayList.add(city);
                            cityStrList.add(city.getName());
                        }
                    }

                    cityMap.put(i, cityArrayList);
                    provinceNameList.add(i, provinceMenuItem);
                    cityStrMap.put(provinceMenuItem.getName(), cityStrList);
                }
            }
        }
    }

    /**
     * 解绑
     *
     * @param cardId
     */
    private void unBindBank(long cardId) {
        if (unbindSubscriber == null || unbindSubscriber.isUnsubscribed()) {
            unbindSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType.UNBIND_BANK_SUCCESS,
                                            null));
                            bindInfo = null;
                            refreshBindView();
                        }
                    })
                    .setDataNullable(true)
                    .setProgressBar(progressBar)
                    .build();
            CustomerCardApi.unbindObb(cardId)
                    .subscribe(unbindSubscriber);
        }
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case BIND_WX_SUCCESS:
                                    finish();
                                    break;
                            }
                        }
                    });
        }
        if (cardRxBusSubscription == null || cardRxBusSubscription.isUnsubscribed()) {
            cardRxBusSubscription = RxBus.getDefault()
                    .toObservable(CardRxEvent.class)
                    .subscribe(new RxBusSubscriber<CardRxEvent>() {
                        @Override
                        protected void onEvent(CardRxEvent cardRxEvent) {
                            switch (cardRxEvent.getType()) {
                                case CARD_INFO_EDIT:
                                    BindBankSettingActivity.this.card = (Card) cardRxEvent
                                            .getObject();
                                    break;
                            }
                        }
                    });
        }
    }

    @OnClick(R2.id.tv_bank_address)
    void onCitySelect() {
        if (cityPickerDlg != null && cityPickerDlg.isShowing() || cityStrMap == null) {
            return;
        }
        cityPickerDlg = new Dialog(this, R.style.BubbleDialogTheme);
        View v = this.getLayoutInflater()
                .inflate(R.layout.dialog_city_picker___card, null);
        final CitiesPickerView cityPickerView = (CitiesPickerView) v.findViewById(R.id.picker);
        cityPickerView.setCityMap(cityStrMap);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cityPickerView
                .getLayoutParams();
        params.height = (int) (getResources().getDisplayMetrics().density * (24 * 8));
        cityPickerDlg.setContentView(v);
        Window win = cityPickerDlg.getWindow();
        WindowManager.LayoutParams params2 = win.getAttributes();
        Point point = CommonUtil.getDeviceSize(this);
        params2.width = point.x;
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialog_anim_rise_style);

        TextView close = (TextView) v.findViewById(R.id.close);
        TextView confirm = (TextView) v.findViewById(R.id.confirm);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityPickerDlg.cancel();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] selectedItems = cityPickerView.getSelectedItemIndexs();
                selectedCity = cityMap.get(selectedItems[0])
                        .get(selectedItems[1]);
                selectedProvince = provinceNameList.get(selectedItems[0]);
                tvBankAddress.setText(selectedProvince.getName() + " " + selectedCity.getName());
                cid = selectedCity.getCid();
                cityPickerDlg.cancel();
            }
        });
        cityPickerDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });

        cityPickerDlg.show();
    }


    @OnClick(R2.id.tv_support_bank)
    public void onSupportBankClicked() {
        String url = HljCard.getInvitationBankListUrl();
        if (!TextUtils.isEmpty(url)) {
            Intent intent = new Intent();
            intent.setClass(this, HljWebViewActivity.class);
            intent.putExtra("path", url);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @OnClick(R2.id.btn_bind_bank)
    public void onBindBankClicked() {
        idHolder = etHolderName.getText()
                .toString();
        accNo = editBankCard.getText()
                .toString();
        address = tvBankAddress.getText()
                .toString();
        if (TextUtils.isEmpty(idHolder)) {
            ToastUtil.showToast(this, "请输入持卡人姓名!", 0);
        } else if (TextUtils.isEmpty(accNo)) {
            ToastUtil.showToast(this, "请输入银行卡号!", 0);
        } else if (TextUtils.isEmpty(address) || cid == 0) {
            ToastUtil.showToast(this, "请选择开户所在地!", 0);
        } else {
            bindBank();
        }
    }

    @OnClick(R2.id.tv_back_wx_bind)
    public void onBackWxBind() {
        if (isFromWx) {
            onBackPressed();
        } else {
            Intent intent = new Intent(this, BindWXSettingActivity.class);
            intent.putExtra("card", card);
            intent.putExtra("is_from_bank", true);
            intent.putExtra("can_modify_name", isCanModifyName);
            startActivity(intent);
        }
    }


    @OnClick(R2.id.btn_unbind)
    public void onUnbind() {
        DialogUtil.createDoubleButtonDialog(this,
                "是否确认更换银行卡？",
                "确认",
                "取消",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unBindBank(cardId);
                    }
                },
                null)
                .show();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(bindSubscriber,
                unbindSubscriber,
                rxBusEventSub,
                cardRxBusSubscription);
    }

}
