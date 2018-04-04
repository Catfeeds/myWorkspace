package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.models.BankCard;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kankan.wheel.widget.CitiesPickerView;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.bindbank.BindApi;
import me.suncloud.marrymemo.api.bindbank.BindBankPostBody;
import me.suncloud.marrymemo.api.bindbank.CashGiftStatus;
import me.suncloud.marrymemo.api.bindbank.SwitchCashGiftBody;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.MenuItem;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.widget.BankCardEditText;
import rx.Observable;

/**
 * 收礼金页面
 * Created by jinxin on 2017/4/1 0001.
 */

public class SwitchCashGiftActivity extends HljBaseActivity implements CompoundButton
        .OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {

    private final int REQUEST_CODE = 101;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.switch_box)
    CheckBox switchBox;
    @BindView(R.id.img_bank_logo)
    RoundedImageView imgBankLogo;
    @BindView(R.id.tv_bank_card)
    TextView tvBankCard;
    @BindView(R.id.layout_bind)
    LinearLayout layoutBinded;
    @BindView(R.id.radio_name)
    RadioGroup radioName;
    @BindView(R.id.edit_bank_card)
    BankCardEditText editBankCard;
    @BindView(R.id.tv_province)
    TextView tvProvince;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.tv_support_bank)
    TextView tvSupportBank;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.layout_card)
    LinearLayout layoutCard;
    @BindView(R.id.tv_groom_name)
    RadioButton tvGroomName;
    @BindView(R.id.tv_bride_name)
    RadioButton tvBrideName;
    @BindView(R.id.layout_city)
    LinearLayout layoutCity;
    @BindView(R.id.tv_bind_bank_safe_tip)
    TextView tvBindBankSafeTip;

    private long cardId;//请帖id
    private boolean isSet;
    private HljHttpSubscriber initSubscriber;
    private HljHttpSubscriber bindSubscriber;
    private int checkedId;
    private CashGiftStatus status;
    private String brideName;//当前请帖新郎姓名
    private String groomName;//当前请帖新娘姓名
    private Map<String, HljHttpSubscriber> obbMap;
    private String KEY = "subscriber";
    private Dialog cityPickerDlg;
    private ArrayList<MenuItem> provinceNameList;
    private LinkedHashMap<Integer, ArrayList<City>> cityMap;
    private LinkedHashMap<String, ArrayList<String>> cityStrMap;
    private City selectedCity;
    private MenuItem selectedProvince;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cardId = getIntent().getLongExtra("cardId", 0);
        brideName = getIntent().getStringExtra("brideName");
        groomName = getIntent().getStringExtra("groomName");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_cash_gift);
        ButterKnife.bind(this);
        initConstant();
        initWidget();
        initLoad();
        initCity();
    }

    private void initLoad() {
        Observable<CashGiftStatus> statusObb = BindApi.getCashGiftOn(cardId);
        initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<CashGiftStatus>() {
                    @Override
                    public void onNext(CashGiftStatus cashGiftStatus) {
                        status = cashGiftStatus;
                        setSwitchStatus();
                    }
                })
                .build();

        statusObb.subscribe(initSubscriber);
    }

    private void initConstant() {
        obbMap = new HashMap<>();
    }

    private void initWidget() {
        tvHint.setText(Html.fromHtml(getString(R.string.label_switch_cash_hint)));
        switchBox.setOnCheckedChangeListener(this);
        checkedId = R.id.tv_groom_name;
        editBankCard.addTextChangedListener(textWatcher);
        radioName.setOnCheckedChangeListener(this);
        City city = Session.getInstance()
                .getMyCity(this);
        if (city != null) {
            if (city.getId() != 0) {
                selectedCity = city;
            }
            tvCity.setText(city.getId() == 0 ? "" : city.getName());
        }
    }

    private void initCity() {
        provinceNameList = new ArrayList<>();
        cityMap = new LinkedHashMap<>();
        cityStrMap = new LinkedHashMap<>();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSONUtil.readStreamToString(getResources()
                    .openRawResource(R.raw.cities)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObject != null) {
            JSONArray provinceArray = jsonObject.optJSONArray("provinces");
            if (provinceArray != null && provinceArray.length() > 0) {
                for (int i = 0; i < provinceArray.length(); i++) {
                    JSONObject provinceObj = provinceArray.optJSONObject(i);
                    MenuItem provinceMenuItem = new MenuItem(provinceObj);
                    ArrayList<City> cityArrayList = new ArrayList<>();
                    ArrayList<String> cityStrList = new ArrayList<>();
                    JSONArray cityArray = provinceObj.optJSONArray("cities");
                    if (cityArray != null && cityArray.length() > 0) {
                        for (int j = 0; j < cityArray.length(); j++) {
                            City city = new City(cityArray.optJSONObject(j));
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

    private void setSwitchStatus() {
        if (status != null) {
            changeIsSet(status.isCurrentCaseGiftOn());
            switchBox.setChecked(status.isCurrentCaseGiftOn());
            BankCard bankCard = status.getBankCard();
            if (bankCard == null) {
                setUnBindContent();
            } else {
                setBindContent(bankCard);
            }
        }
    }

    private void setBindContent(BankCard bankCard) {
        layoutBinded.setVisibility(View.VISIBLE);
        layoutCard.setVisibility(View.GONE);
        tvBindBankSafeTip.setText(getString(R.string.label_switch_cash_safe_bind_hint));
        String logoPath = ImageUtil.getImagePath(bankCard.getBankLogo(),
                CommonUtil.dp2px(this, 40));
        if (!TextUtils.isEmpty(logoPath)) {
            Glide.with(this)
                    .asBitmap()
                    .load(logoPath)
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_avatar_primary))
                    .into(imgBankLogo);
        } else {
            Glide.with(this)
                    .clear(imgBankLogo);
        }

        tvBankCard.setText(bankCard.getBankName() + "(" + bankCard.getAccNo() + ")");
    }

    private void setUnBindContent() {
        layoutCard.setVisibility(View.VISIBLE);
        layoutBinded.setVisibility(View.GONE);
        tvGroomName.setText(groomName);
        tvBrideName.setText(brideName);
        tvBindBankSafeTip.setText(getString(R.string.label_switch_cash_safe_unbind_hint));
    }

    private void changeStatue() {
        if (switchBox.isChecked()) {
            switchBox.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (SwitchCashGiftActivity.this.isFinishing()) {
                        return;
                    }
                    isSet = false;
                    switchBox.setChecked(false);
                }
            }, 500);
        }
    }

    private void changeIsSet(boolean status) {
        isSet = !status;
    }

    private void btnStatus(String str) {
        if (TextUtils.isEmpty(str) || selectedCity == null || checkedId == -1) {
            btnConfirm.setEnabled(false);
        } else {
            btnConfirm.setEnabled(true);
        }
    }

    @OnClick(R.id.btn_confirm)
    void onConfirm() {
        if (bindSubscriber != null && !bindSubscriber.isUnsubscribed()) {
            return;
        }

        bindSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                    @Override
                    public void onNext(HljHttpResult result) {
                        if (result.getStatus()
                                .getRetCode() == 0) {
                            //验证成功
                            Toast.makeText(SwitchCashGiftActivity.this,
                                    "银行卡绑定成功，礼金功能已开启",
                                    Toast.LENGTH_SHORT)
                                    .show();
                            //刷新绑定成功的信息
                            initLoad();
                        } else {
                            Toast.makeText(SwitchCashGiftActivity.this,
                                    TextUtils.isEmpty(result.getStatus()
                                            .getMsg()) ? "验证失败" : result.getStatus()
                                            .getMsg(),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                })
                .build();
        BindBankPostBody body = new BindBankPostBody();
        body.setAcc_no(editBankCard.getBankCardText());
        String name = null;
        if (checkedId == R.id.tv_bride_name) {
            name = tvBrideName.getText()
                    .toString();
        } else if (checkedId == R.id.tv_groom_name) {
            name = tvGroomName.getText()
                    .toString();
        }
        body.setId_holder(name);
        if (selectedCity != null) {
            body.setCid(selectedCity.getId());
        }
        Observable<HljHttpResult> obb = BindApi.bindBankCard(body);
        obb.subscribe(bindSubscriber);
    }

    @OnClick(R.id.layout_city)
    void onCitySelect() {
        if (cityPickerDlg != null && cityPickerDlg.isShowing()) {
            return;
        }

        cityPickerDlg = new Dialog(this, R.style.BubbleDialogTheme);
        View v = this.getLayoutInflater()
                .inflate(R.layout.dialog_city_picker, null);
        final CitiesPickerView cityPickerView = (CitiesPickerView) v.findViewById(R.id.picker);
        cityPickerView.setCityMap(cityStrMap);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cityPickerView
                .getLayoutParams();
        params.height = (int) (getResources().getDisplayMetrics().density * (24 * 8));
        cityPickerDlg.setContentView(v);
        Window win = cityPickerDlg.getWindow();
        WindowManager.LayoutParams params2 = win.getAttributes();
        Point point = JSONUtil.getDeviceSize(this);
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
                cityPickerDlg.cancel();
                tvProvince.setVisibility(View.VISIBLE);
                tvProvince.setText(selectedProvince.getName());
                tvCity.setText(selectedCity.getName());
                btnStatus(editBankCard.getBankCardText());
            }
        });
        cityPickerDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });

        cityPickerDlg.show();
    }

    @OnClick(R.id.tv_support_bank)
    void onSupportBank() {
        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(this);
        if (dataConfig != null && !TextUtils.isEmpty(dataConfig.getInvitationCardBankListUrl())) {
            Intent intent = new Intent();
            intent.setClass(this, HljWebViewActivity.class);
            intent.putExtra("path", dataConfig.getInvitationCardBankListUrl());
            intent.putExtra("title", getString(R.string.title_activity_support_card_list___pay));
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    protected void onFinish() {
        hideKeyboard(editBankCard);
        if (initSubscriber != null && !initSubscriber.isUnsubscribed()) {
            initSubscriber.unsubscribe();
        }

        if (bindSubscriber != null && !bindSubscriber.isUnsubscribed()) {
            bindSubscriber.unsubscribe();
        }

        if (obbMap.containsKey(KEY)) {
            HljHttpSubscriber subscriber = obbMap.get(KEY);
            if (subscriber != null) {
                subscriber.unsubscribe();
            }
        }
        super.onFinish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
        if (!isSet) {
            isSet = !isSet;
            return;
        }

        if (obbMap.containsKey(KEY)) {
            HljHttpSubscriber subscriber = obbMap.get(KEY);
            if (subscriber != null) {
                subscriber.unsubscribe();
            }
        }

        HljHttpSubscriber subscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                    @Override
                    public void onNext(HljHttpResult result) {
                        if (result != null) {
                            HljHttpStatus status = result.getStatus();
                            if (status.getRetCode() == 2001) {
                                // 未绑定银行卡
                                layoutBinded.setVisibility(View.GONE);
                                layoutCard.setVisibility(View.VISIBLE);
                                Toast.makeText(SwitchCashGiftActivity.this,
                                        "请先绑定礼金提现银行卡",
                                        Toast.LENGTH_SHORT)
                                        .show();
                                changeStatue();
                            } else if (status.getRetCode() == 2002 || status.getRetCode() == 2003) {
                                //2002请帖姓名验证失败 2003保存失败
                                Toast.makeText(SwitchCashGiftActivity.this,
                                        status.getMsg(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                                changeStatue();
                            } else {
                                Toast.makeText(SwitchCashGiftActivity.this,
                                        status.getRetCode() == 0 ? "操作成功" : status.getMsg(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }
                })
                .build();
        SwitchCashGiftBody body = new SwitchCashGiftBody();
        body.setCard_id(cardId);
        body.setOn(isChecked ? "1" : "0");
        Observable<HljHttpResult> obb = BindApi.cashGiftOn(body);
        obbMap.put(KEY, subscriber);
        obb.subscribe(subscriber);
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            btnStatus(editBankCard.getBankCardText());
        }
    };

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        this.checkedId = checkedId;
        btnStatus(editBankCard.getBankCardText());
    }
}
