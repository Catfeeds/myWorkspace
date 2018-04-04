package com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.wheelpickerlibrary.picker.ThreeLevelPicker;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.modelwrappers.ChildrenArea;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.AddressAreaUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.XiaoxiInstallmentUser;
import com.hunliji.hljpaymentlibrary.utils.xiaoxi_installment.XiaoxiInstallmentAuthorization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * 小犀分期-添加还款用户基本信息
 * Created by chen_bin on 2017/8/10 0010.
 */
public class AddBasicUserInfoActivity extends HljBaseActivity {

    @BindView(R2.id.tv_address_area)
    TextView tvAddressArea;
    @BindView(R2.id.et_address)
    EditText etAddress;
    @BindView(R2.id.et_company_name)
    EditText etCompanyName;
    @BindView(R2.id.tv_salary)
    TextView tvSalary;
    @BindView(R2.id.btn_next)
    Button btnNext;
    @BindView(R2.id.scroll_view)
    ScrollView scrollView;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private Dialog salaryDialog;

    private List<ChildrenArea> addressAreas;
    private List<String> primaryLevelNames;
    private List<List<String>> secondaryLevelNames;
    private List<List<List<String>>> tertiaryLevelNames;
    private List<String> salaries;
    private ChildrenArea selectedProvince;
    private ChildrenArea selectedCity;
    private ChildrenArea selectedArea;
    private XiaoxiInstallmentUser user;
    private boolean isAuto;
    private boolean isEdit;

    private Subscription rxBusEventSub;
    private HljHttpSubscriber getUserSub;
    private HljHttpSubscriber updateSub;

    public final static int REQUEST_CODE_EXT_INFO = 1;

    public final static String ARG_IS_EDIT = "is_edit";
    public final static String ARG_IS_AUTO = "is_auto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_add_basic_user_info___pay);
        ButterKnife.bind(this);
        initValues();
        initViews();
        getAddressAreasData();
        getUserData();
        registerRxBusEvent();
    }

    private void initValues() {
        isEdit = getIntent().getBooleanExtra(ARG_IS_EDIT, false);
        isAuto = getIntent().getBooleanExtra(ARG_IS_AUTO, false);
        user = new XiaoxiInstallmentUser();
        addressAreas = new ArrayList<>();
        primaryLevelNames = new ArrayList<>();
        secondaryLevelNames = new ArrayList<>();
        tertiaryLevelNames = new ArrayList<>();
        salaries = Arrays.asList(getResources().getStringArray(R.array.salaries));
    }

    private void initViews() {
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setOnEmptyClickListener(new HljEmptyView.OnEmptyClickListener() {
            @Override
            public void onEmptyClickListener() {
                getUserData();
            }
        });
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                getUserData();
            }
        });
    }

    private void getUserData() {
        if (!isEdit) {
            return;
        }
        btnNext.setText(R.string.label_submit___cm);
        if (getUserSub == null || getUserSub.isUnsubscribed()) {
            scrollView.setVisibility(View.GONE);
            getUserSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<XiaoxiInstallmentUser>() {
                        @Override
                        public void onNext(XiaoxiInstallmentUser u) {
                            user = u;
                            user.setRiskDataOff(1);
                            setUserData();
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(scrollView)
                    .setProgressBar(progressBar)
                    .build();
            XiaoxiInstallmentApi.getUserInfoObb(this)
                    .subscribe(getUserSub);
        }
    }

    private void setUserData() {
        ChildrenArea[] areas = AddressAreaUtil.getInstance()
                .getAreasFromName(addressAreas, user.getProvince(), user.getCity(), user.getArea());
        selectedProvince = areas.length > 0 ? areas[0] : null;
        selectedCity = areas.length > 1 ? areas[1] : null;
        selectedArea = areas.length > 2 ? areas[2] : null;
        setAddressAreaData();
        etAddress.setText(user.getAddress());
        etAddress.setSelection(etAddress.length());
        etCompanyName.setText(user.getCompanyName());
        if (user.getSalary() > 0 && user.getSalary() <= salaries.size()) {
            tvSalary.setText(salaries.get(user.getSalary() - 1));
        }
    }

    @SuppressWarnings("unchecked")
    private void getAddressAreasData() {
        AddressAreaUtil.getInstance()
                .getAddressAreasData(this, new OnFinishedListener() {
                    @Override
                    public void onFinished(Object... objects) {
                        if (objects != null && objects.length > 0) {
                            addressAreas.addAll((Collection<? extends ChildrenArea>) objects[0]);
                            primaryLevelNames.addAll((Collection<? extends String>) objects[1]);
                            secondaryLevelNames.addAll((Collection<? extends List<String>>)
                                    objects[2]);
                            tertiaryLevelNames.addAll((Collection<? extends List<List<String>>>)
                                    objects[3]);
                        }
                    }
                });
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(PayRxEvent.class)
                    .subscribe(new RxBusSubscriber<PayRxEvent>() {
                        @Override
                        protected void onEvent(PayRxEvent payRxEvent) {
                            switch (payRxEvent.getType()) {
                                case ADD_BASIC_USER_INFO_SUCCESS:
                                    finish();
                                    break;
                            }
                        }
                    });
        }
    }

    @OnClick(R2.id.address_area_layout)
    void onAddressArea() {
        if (CommonUtil.isCollectionEmpty(addressAreas)) {
            return;
        }
        final Dialog dialog = new Dialog(this, R.style.BubbleDialogTheme);
        dialog.setContentView(R.layout.dialog_address_area_three_level_picker___cm);
        final ThreeLevelPicker picker = dialog.findViewById(R.id.picker);
        int[] indexs = AddressAreaUtil.getInstance()
                .getIndexesFromArea(addressAreas, selectedProvince, selectedCity, selectedArea);
        int i1 = indexs.length > 0 ? indexs[0] : 0;
        int i2 = indexs.length > 1 ? indexs[1] : 0;
        int i3 = indexs.length > 2 ? indexs[2] : 0;
        picker.setItems(primaryLevelNames, secondaryLevelNames, tertiaryLevelNames, i1, i2, i3);
        dialog.findViewById(R.id.close)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        dialog.findViewById(R.id.confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        int[] selectedItemIndexs = picker.getCurrentItems();
                        selectedProvince = null;
                        selectedCity = null;
                        selectedArea = null;
                        if (CommonUtil.getCollectionSize(addressAreas) > selectedItemIndexs[0]) {
                            selectedProvince = addressAreas.get(selectedItemIndexs[0]);
                        }
                        if (selectedProvince != null && CommonUtil.getCollectionSize(
                                selectedProvince.getChildrenAreas()) > selectedItemIndexs[1]) {
                            selectedCity = selectedProvince.getChildrenAreas()
                                    .get(selectedItemIndexs[1]);
                        }
                        if (selectedCity != null && CommonUtil.getCollectionSize(selectedCity
                                .getChildrenAreas()) > selectedItemIndexs[2]) {
                            selectedArea = selectedCity.getChildrenAreas()
                                    .get(selectedItemIndexs[2]);
                        }
                        setAddressAreaData();
                    }
                });
        Window win = dialog.getWindow();
        if (win != null) {
            WindowManager.LayoutParams params = win.getAttributes();
            params.width = CommonUtil.getDeviceSize(this).x;
            win.setAttributes(params);
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        dialog.show();
    }

    @OnClick(R2.id.salary_layout)
    void onSalary() {
        if (salaryDialog != null && salaryDialog.isShowing()) {
            return;
        }
        if (salaryDialog == null) {
            salaryDialog = DialogUtil.createSingleWheelPickerDialog(this,
                    salaries,
                    Math.max(0, user.getSalary() - 1),
                    new DialogUtil.OnWheelSelectedListener() {

                        @Override
                        public void onWheelSelected(int position, String str) {
                            user.setSalary(position + 1);
                            tvSalary.setText(str);
                        }
                    });
        }
        salaryDialog.show();
    }

    @OnClick(R2.id.ext_info_layout)
    void onExtInfo() {
        Intent intent = new Intent(this, AddExtUserInfoActivity.class);
        intent.putExtra(AddExtUserInfoActivity.ARG_USER, user);
        startActivityForResult(intent, REQUEST_CODE_EXT_INFO);
    }

    @OnClick(R2.id.btn_next)
    void onNext() {
        if (tvAddressArea.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_select_address_area___pay);
            return;
        }
        if (etAddress.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_enter_address___pay);
            return;
        }
        if (etCompanyName.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_enter_company_name___pay);
            return;
        }
        if (tvSalary.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_select_salary___pay);
            return;
        }
        if (!isEdit) {
            Intent intent = new Intent(this, AddEmergencyContactActivity.class);
            user.setAddress(etAddress.getText()
                    .toString());
            user.setCompanyName(etCompanyName.getText()
                    .toString());
            intent.putExtra(AddEmergencyContactActivity.ARG_USER, user);
            intent.putExtra(AddEmergencyContactActivity.ARG_IS_AUTO, isAuto);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else {
            CommonUtil.unSubscribeSubs(updateSub);
            updateSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            ToastUtil.showCustomToast(AddBasicUserInfoActivity.this,
                                    R.string.msg_update_user_info_success___pay);
                            onBackPressed();
                        }
                    })
                    .setDataNullable(true)
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .build();
            XiaoxiInstallmentApi.uploadUserInfoObb(this, user)
                    .subscribe(updateSub);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_EXT_INFO:
                    if (data != null) {
                        user = data.getParcelableExtra("user");
                    }
                    break;
            }
        }
    }

    private void setAddressAreaData() {
        String province = "";
        String city = "";
        String area = "";
        if (selectedProvince != null && !TextUtils.isEmpty(selectedProvince.getName())) {
            province = selectedProvince.getName();
        }
        if (selectedCity != null && !TextUtils.isEmpty(selectedCity.getName())) {
            city = selectedCity.getName();
            city = !TextUtils.isEmpty(province) ? " " + city : city;
        }
        if (selectedArea != null && !TextUtils.isEmpty(selectedArea.getName())) {
            area = selectedArea.getName();
            area = !TextUtils.isEmpty(province + city) ? " " + area : area;
        }
        user.setProvince(province);
        user.setCity(city);
        user.setArea(area);
        tvAddressArea.setText(province + city + area);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isAuto) {
            RxBus.getDefault()
                    .post(new PayRxEvent(PayRxEvent.RxEventType.AUTHORIZE_CANCEL, null));
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        AddressAreaUtil.getInstance()
                .onDestroy();
        CommonUtil.unSubscribeSubs(rxBusEventSub);
    }
}
