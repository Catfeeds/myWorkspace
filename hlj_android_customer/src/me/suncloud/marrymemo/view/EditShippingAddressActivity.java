package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wheelpickerlibrary.picker.ThreeLevelPicker;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.modelwrappers.ChildrenArea;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.AddressAreaUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.ShippingAddress;
import me.suncloud.marrymemo.task.StatusHttpDeleteTask;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusHttpPutTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.ShSwitchView;

public class EditShippingAddressActivity extends HljBaseActivity {

    @BindView(R.id.et_buyer_name)
    EditText etBuyerName;
    @BindView(R.id.et_mobile_phone)
    EditText etMobilePhone;
    @BindView(R.id.tv_region)
    TextView tvRegion;
    @BindView(R.id.et_address_detail)
    EditText etAddressDetail;
    @BindView(R.id.switch_default)
    ShSwitchView switchDefault;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;
    @BindView(R.id.default_layout)
    LinearLayout defaultLayout;
    @BindView(R.id.tv_auto_default)
    TextView tvAutoDefault;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private Dialog areaPickerDlg;
    private boolean isFirst;
    private boolean selectMode; // 从确认订单过来
    private ShippingAddress editAddress;
    private Dialog confirmDialog;
    private Dialog deleteDlg;
    private boolean isEditSelectedAddress; // 是否是在编辑选中的地址
    private InputMethodManager imm;


    private List<ChildrenArea> addressAreas;
    private List<String> primaryLevelNames;
    private List<List<String>> secondaryLevelNames;
    private List<List<List<String>>> tertiaryLevelNames;
    private ChildrenArea selectedProvince;
    private ChildrenArea selectedCity;
    private ChildrenArea selectedArea;

    private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText et = null;
            try {
                et = (EditText) v;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (et != null) {
                et.setHint("");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shipping_address);
        ButterKnife.bind(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        setOkText(R.string.label_save);
        setSwipeBackEnable(false);


        addressAreas = new ArrayList<>();
        primaryLevelNames = new ArrayList<>();
        secondaryLevelNames = new ArrayList<>();
        tertiaryLevelNames = new ArrayList<>();

        isFirst = getIntent().getBooleanExtra("is_first", false);
        selectMode = getIntent().getBooleanExtra("select", false);
        isEditSelectedAddress = getIntent().getBooleanExtra("edit_selected_address", false);
        editAddress = (ShippingAddress) getIntent().getSerializableExtra("edit_address");
        etBuyerName.setOnFocusChangeListener(onFocusChangeListener);
        etMobilePhone.setOnFocusChangeListener(onFocusChangeListener);
        etAddressDetail.setOnFocusChangeListener(onFocusChangeListener);
        etBuyerName.setHint(R.string.hint_receiver_name);

        progressBar.setVisibility(View.VISIBLE);

        AddressAreaUtil.getInstance()
                .getAddressAreasData(this, new OnFinishedListener() {
                    @Override
                    public void onFinished(Object... objects) {
                        progressBar.setVisibility(View.GONE);
                        if (objects != null && objects.length > 0) {
                            addressAreas.addAll((Collection<? extends ChildrenArea>) objects[0]);
                            primaryLevelNames.addAll((Collection<? extends String>) objects[1]);
                            secondaryLevelNames.addAll((Collection<? extends List<String>>)
                                    objects[2]);
                            tertiaryLevelNames.addAll((Collection<? extends List<List<String>>>)
                                    objects[3]);
                        }
                        if (isFirst) {
                            // 第一次添加地址,显示默认为默认地址,去掉默认选项
                            tvAutoDefault.setVisibility(View.VISIBLE);
                            defaultLayout.setVisibility(View.GONE);
                            deleteLayout.setVisibility(View.GONE);
                        } else if (editAddress == null) {
                            // 不是第一次添加,也不是编辑现有地址
                            tvAutoDefault.setVisibility(View.GONE);
                            defaultLayout.setVisibility(View.VISIBLE);
                            deleteLayout.setVisibility(View.GONE);
                        } else {
                            // 编辑现有地址
                            etBuyerName.setText(editAddress.getBuyerName());
                            etMobilePhone.setText(editAddress.getMobilePhone());
                            etAddressDetail.setText(editAddress.getStreet());
                            convertAreaIdToObj(editAddress.getRegionId());
                            tvRegion.setText(editAddress.getProvince() + "  " + editAddress
                                    .getCity() + (JSONUtil.isEmpty(
                                    editAddress.getDistrict()) ? "" : editAddress.getDistrict()));
                            deleteLayout.setVisibility(View.VISIBLE);

                            if (editAddress.isDefault()) {
                                tvAutoDefault.setVisibility(View.VISIBLE);
                                tvAutoDefault.setText(R.string.label_already_default);
                                defaultLayout.setVisibility(View.GONE);
                            } else {
                                tvAutoDefault.setVisibility(View.GONE);
                                defaultLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
        if (editAddress == null) {
            setTitle(R.string.label_add_shipping_address);
        }
    }

    @Override
    public void onOkButtonClick() {
        if (etBuyerName.length() == 0) {
            Toast.makeText(this, R.string.msg_empty_buyer_name, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (Util.calculateLength(etBuyerName.getText()) < 2 || Util.calculateLength(etBuyerName
                .getText()) > 15) {
            Toast.makeText(this, R.string.msg_wrong_name_length, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (etMobilePhone.length() == 0) {
            Toast.makeText(this, R.string.msg_empty_phone, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (!Util.isMobileNO(etMobilePhone.getText()
                .toString())) {
            Toast.makeText(this, R.string.hint_new_number_error, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (tvRegion.length() == 0) {
            Toast.makeText(this, R.string.msg_empty_area, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (etAddressDetail.length() == 0) {
            Toast.makeText(this, R.string.msg_empty_address, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (editAddress == null) {
            try {
                String url = Constants.getAbsUrl(Constants.HttpPath.ADD_SHIPPING_ADDRESS);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("ragion_id",
                        selectedArea == null ? selectedCity.getId() : selectedArea.getId());
                jsonObject.put("buyer_name",
                        etBuyerName.getText()
                                .toString());
                jsonObject.put("mobile",
                        etMobilePhone.getText()
                                .toString());
                jsonObject.put("street",
                        etAddressDetail.getText()
                                .toString());
                jsonObject.put("is_default", isFirst ? 1 : switchDefault.isOn() ? 1 : 0);

                progressBar.setVisibility(View.VISIBLE);
                new StatusHttpPostTask(this, new StatusRequestListener() {
                    @Override
                    public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                        progressBar.setVisibility(View.GONE);
                        Util.showToast(R.string.msg_success_to_add_shipping_address,
                                EditShippingAddressActivity.this);
                        Intent intent = getIntent();
                        if (selectMode) {
                            // 直接添加选择返回当前地址
                            JSONObject resultObj = (JSONObject) object;
                            if (resultObj != null) {
                                long id = resultObj.optLong("id");
                                ShippingAddress newAddress = new ShippingAddress(id);
                                newAddress.setBuyerName(etBuyerName.getText()
                                        .toString());
                                newAddress.setMobilePhone(etMobilePhone.getText()
                                        .toString());
                                newAddress.setStreet(etAddressDetail.getText()
                                        .toString());
                                newAddress.setIsDefault(isFirst ? true : switchDefault.isOn());
                                newAddress.setProvince(selectedProvince == null ? "" :
                                        selectedProvince.getName());
                                newAddress.setCity(selectedCity == null ? "" : selectedCity
                                        .getName());
                                newAddress.setDistrict(selectedArea == null ? "" : selectedArea
                                        .getName());
                                newAddress.setRegionId(selectedArea == null ? selectedCity.getId
                                        () : selectedArea.getId());
                                intent.putExtra("address", newAddress);
                            }
                        } else {
                            intent.putExtra("changed", true);
                        }
                        setResult(RESULT_OK, intent);
                        if (imm != null && getCurrentFocus() != null) {
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        EditShippingAddressActivity.super.onBackPressed();
                    }

                    @Override
                    public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                        progressBar.setVisibility(View.GONE);
                        Util.postFailToast(EditShippingAddressActivity.this,
                                returnStatus,
                                R.string.msg_fail_to_add_shipping_address,
                                network);
                    }
                }).execute(url, jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // 更新数据
            try {
                String url = Constants.getAbsUrl(Constants.HttpPath.ADD_SHIPPING_ADDRESS);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("ragion_id",
                        selectedArea == null ? selectedCity.getId() : selectedArea.getId());
                jsonObject.put("buyer_name",
                        etBuyerName.getText()
                                .toString());
                jsonObject.put("mobile",
                        etMobilePhone.getText()
                                .toString());
                jsonObject.put("street",
                        etAddressDetail.getText()
                                .toString());
                if (editAddress.isDefault()) {
                    jsonObject.put("is_default", 1);
                } else {
                    jsonObject.put("is_default", switchDefault.isOn() ? 1 : 0);
                }
                jsonObject.put("id", editAddress.getId());
                progressBar.setVisibility(View.VISIBLE);
                new StatusHttpPutTask(this, new StatusRequestListener() {
                    @Override
                    public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                        progressBar.setVisibility(View.GONE);
                        Util.showToast(R.string.msg_success_to_add_shipping_address,
                                EditShippingAddressActivity.this);
                        Intent intent = getIntent();
                        intent.putExtra("changed", true);
                        setResult(RESULT_OK, intent);
                        if (imm != null && getCurrentFocus() != null) {
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        EditShippingAddressActivity.super.onBackPressed();
                    }

                    @Override
                    public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                        progressBar.setVisibility(View.GONE);
                        Util.postFailToast(EditShippingAddressActivity.this,
                                returnStatus,
                                R.string.msg_fail_to_add_shipping_address,
                                network);
                    }
                }).execute(url, jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        super.onOkButtonClick();
    }

    @Override
    public void onBackPressed() {
        if (editAddress != null) {
            // 编辑状态与之前的编辑信息有改变,退出的时候提示
            boolean regionChanged = false;
            if (selectedCity != null && editAddress.getRegionId() != (selectedArea == null ?
                    selectedCity.getId() : selectedArea.getId())) {
                regionChanged = true;
            }
            boolean isDefaultChanges = false;
            if (editAddress.isDefault()) {
                isDefaultChanges = false;
            } else {
                if (switchDefault.isOn()) {
                    isDefaultChanges = true;
                }
            }
            if ((!editAddress.getBuyerName()
                    .equals(etBuyerName.getText()
                            .toString())) || !editAddress.getMobilePhone()
                    .equals(etMobilePhone.getText()
                            .toString()) || regionChanged || !editAddress.getStreet()
                    .equals(etAddressDetail.getText()
                            .toString()) || isDefaultChanges) {
                // 有数据改变,显示提示
                confirmBack();
            } else {
                // 没有改变,直接返回
                if (imm != null && getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                super.onBackPressed();
            }
        } else {
            // 添加状态,判断是否有输入即可
            if (etBuyerName.length() > 0 || etAddressDetail.length() > 0 || etMobilePhone.length
                    () > 0 || selectedCity != null) {
                confirmBack();
            } else {
                if (imm != null && getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                super.onBackPressed();
            }
        }
    }

    public void confirmBack() {
        if (confirmDialog != null && confirmDialog.isShowing()) {
            return;
        }
        if (confirmDialog == null) {
            confirmDialog = new Dialog(this, R.style.BubbleDialogTheme);
            View v = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
            TextView msgAlertTv = (TextView) v.findViewById(R.id.tv_alert_msg);
            Button confirmBtn = (Button) v.findViewById(R.id.btn_confirm);
            Button cancelBtn = (Button) v.findViewById(R.id.btn_cancel);
            msgAlertTv.setText(R.string.msg_confirm_drop_edited);
            confirmBtn.setText(R.string.label_give_up);
            cancelBtn.setText(R.string.label_wrong_action);
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDialog.dismiss();
                    if (imm != null && getCurrentFocus() != null) {
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    EditShippingAddressActivity.super.onBackPressed();
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDialog.dismiss();
                }
            });
            confirmDialog.setContentView(v);
            Window window = confirmDialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
        }
        confirmDialog.show();
    }

    @OnClick(R.id.area_layout)
    void selectArea() {
        if (areaPickerDlg != null && areaPickerDlg.isShowing()) {
            return;
        }
        if (CommonUtil.isCollectionEmpty(addressAreas)) {
            return;
        }
        if (areaPickerDlg == null) {
            areaPickerDlg = new Dialog(this, R.style.BubbleDialogTheme);
            areaPickerDlg.setContentView(R.layout.dialog_address_area_three_level_picker___cm);
            final ThreeLevelPicker picker = areaPickerDlg.findViewById(R.id.picker);
            int index[] = AddressAreaUtil.getInstance()
                    .getIndexesFromArea(addressAreas, selectedProvince, selectedCity, selectedArea);
            int i1 = index.length > 0 ? index[0] : 0;
            int i2 = index.length > 1 ? index[1] : 0;
            int i3 = index.length > 2 ? index[2] : 0;
            picker.setItems(primaryLevelNames, secondaryLevelNames, tertiaryLevelNames, i1, i2, i3);
            areaPickerDlg.findViewById(R.id.close)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            areaPickerDlg.dismiss();
                        }
                    });
            areaPickerDlg.findViewById(R.id.confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            areaPickerDlg.dismiss();
                            int[] selectedItemIndexs = picker.getCurrentItems();
                            selectedProvince = null;
                            selectedCity = null;
                            selectedArea = null;
                            String province = "";
                            String city = "";
                            String area = "";
                            if (CommonUtil.getCollectionSize(addressAreas) >
                                    selectedItemIndexs[0]) {
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
                            if (selectedProvince != null && !TextUtils.isEmpty(selectedProvince
                                    .getName())) {
                                province = selectedProvince.getName();
                            }
                            if (selectedCity != null && !TextUtils.isEmpty(selectedCity.getName()
                            )) {
                                city = selectedCity.getName();
                                city = !TextUtils.isEmpty(province) ? " " + city : city;
                            }
                            if (selectedArea != null && !TextUtils.isEmpty(selectedArea.getName()
                            )) {
                                area = selectedArea.getName();
                                area = !TextUtils.isEmpty(province + city) ? " " + area : area;
                            }
                            tvRegion.setText(province + city + area);
                        }
                    });
            Window win = areaPickerDlg.getWindow();
            if (win != null) {
                WindowManager.LayoutParams params = win.getAttributes();
                params.width = CommonUtil.getDeviceSize(this).x;
                win.setAttributes(params);
                win.setGravity(Gravity.BOTTOM);
                win.setWindowAnimations(R.style.dialog_anim_rise_style);
            }
        }
        areaPickerDlg.show();
    }

    @OnClick(R.id.delete_layout)
    void onDelete() {
        if (deleteDlg != null && deleteDlg.isShowing()) {
            return;
        }
        if (deleteDlg == null) {
            deleteDlg = new Dialog(this, R.style.BubbleDialogTheme);
            View v = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
            TextView msgAlertTv = (TextView) v.findViewById(R.id.tv_alert_msg);
            Button confirmBtn = (Button) v.findViewById(R.id.btn_confirm);
            Button cancelBtn = (Button) v.findViewById(R.id.btn_cancel);
            msgAlertTv.setText(R.string.msg_confirm_delete);
            confirmBtn.setText(R.string.label_delete_address);
            cancelBtn.setText(R.string.label_wrong_action);
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDlg.dismiss();

                    deleteAddress();
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDlg.dismiss();
                }
            });
            deleteDlg.setContentView(v);
            Window window = deleteDlg.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
        }
        deleteDlg.show();
    }

    private void deleteAddress() {
        progressBar.setVisibility(View.VISIBLE);

        new StatusHttpDeleteTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                // 删除成功
                progressBar.setVisibility(View.GONE);
                Util.showToast(R.string.msg_success_to_delete_shipping_address,
                        EditShippingAddressActivity.this);
                Intent intent = getIntent();
                intent.putExtra("changed", true);
                if (isEditSelectedAddress) {
                    // 删除选中的地址
                    intent.putExtra("deleted_selected_address", true);
                }
                setResult(RESULT_OK, intent);
                if (imm != null && getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                EditShippingAddressActivity.super.onBackPressed();
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                Util.postFailToast(EditShippingAddressActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_delete_shipping_address,
                        network);

            }
        }).execute(Constants.getAbsUrl(String.format(Constants.HttpPath.DELETE_SHIPPING_ADDRESS,
                editAddress.getId())));
    }

    private void convertAreaIdToObj(long id) {
        if (CommonUtil.isCollectionEmpty(addressAreas)) {
            return;
        }
        for (ChildrenArea province : addressAreas) {
            if (CommonUtil.isCollectionEmpty(province.getChildrenAreas())) {
                continue;
            }
            for (ChildrenArea city : province.getChildrenAreas()) {
                if (city.getId() == id) {
                    selectedProvince = province;
                    selectedCity = city;
                    return;
                }
                if (CommonUtil.isCollectionEmpty(city.getChildrenAreas())) {
                    continue;
                }
                for (ChildrenArea area : city.getChildrenAreas()) {
                    if (area.getId() == id) {
                        selectedProvince = province;
                        selectedCity = city;
                        selectedArea = area;
                        return;
                    }
                }
            }
        }
    }


    @Override
    protected void onFinish() {
        super.onFinish();
        AddressAreaUtil.getInstance()
                .onDestroy();
    }
}
