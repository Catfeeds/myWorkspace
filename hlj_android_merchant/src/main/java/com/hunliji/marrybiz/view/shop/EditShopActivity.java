package com.hunliji.marrybiz.view.shop;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.wheelpickerlibrary.picker.SingleWheelPicker;
import com.example.wheelpickerlibrary.picker.ThreeLevelPicker;
import com.example.wheelpickerlibrary.picker.TwoLevelPicker;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PhotoListUploadUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;
import com.hunliji.hljmaplibrary.views.activities.LocationMapActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.merchant.MerchantApi;
import com.hunliji.marrybiz.model.AddressArea;
import com.hunliji.marrybiz.model.MerchantProperty;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.util.AddressAreaUtil;
import com.hunliji.marrybiz.util.FileUtil;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.MerchantUserSyncUtil;
import com.hunliji.marrybiz.util.PropertiesUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.HomeActivity;
import com.hunliji.marrybiz.view.login.CompanyCertificationActivity;
import com.hunliji.marrybiz.view.login.PreCertificationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import rx.internal.util.SubscriptionList;

/**
 * Created by mo_yu on 2017/8/22.创建，编辑店铺
 */
@RuntimePermissions
public class EditShopActivity extends HljBaseActivity {

    @BindView(R.id.tv_alert_msg)
    TextView tvAlertMsg;
    @BindView(R.id.alert_layout)
    LinearLayout alertLayout;
    @BindView(R.id.img_logo)
    ImageView imgLogo;
    @BindView(R.id.img_bubble)
    ImageView imgBubble;
    @BindView(R.id.et_merchant_name)
    EditText etMerchantName;
    @BindView(R.id.tv_merchant_property)
    TextView tvMerchantProperty;
    @BindView(R.id.tv_merchant_address_area)
    TextView tvMerchantAddressArea;
    @BindView(R.id.et_contact_address)
    EditText etContactAddress;
    @BindView(R.id.tv_map_mark)
    TextView tvMapMark;
    @BindView(R.id.et_contact_mobile)
    EditText etContactMobile;
    @BindView(R.id.et_contact_phone)
    EditText etContactPhone;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.merchant_property)
    TextView merchantProperty;
    @BindView(R.id.merchant_property_hint)
    TextView merchantPropertyHint;
    @BindView(R.id.tv_merchant_photos_hint)
    TextView tvMerchantPhotosHint;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private int logoWidth;
    private int coverWidth;
    private int photoType; // 1: 设置logo, 2: 设置封面
    private boolean isCreate;//是否未提交过店铺审核
    private boolean hasChangedContent; // 是否编辑修改过内容
    private boolean hasChangedMerchantPhoto;//店铺照片是否修改
    private String logoPath;
    private String coverPath;
    private String briefIntro;
    private Uri cropUri;
    private MerchantUser reviewingUser;//当前正在审核中的用户资料
    private MerchantUser currentUser; // 当前用户的用户资料
    private ArrayList<Photo> selectPhotos;
    private ArrayList<Photo> realPhotos;
    private Dialog quitDialog;
    private Dialog photoDialog;
    private Dialog merchantTypeDialog;
    private Dialog areaPickerDlg;
    private Dialog propertyAlertDlg;
    private Dialog submitAlertDlg;
    private ObjectAnimator animator;
    //地区选择
    private LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> addressMap;
    private AddressArea selectedProvince;
    private AddressArea selectedCity;
    private AddressArea selectedArea;
    private String addressString;
    private ArrayList<AddressArea> addressList;
    private static List<String> provinceList;
    private static List<List<String>> cityList;
    private static List<List<List<String>>> areaList;
    private double latitude;
    private double longitude;
    //商家类别选择
    private MerchantProperty selectedMerchantProperty; // 一级分类
    private MerchantProperty selectedMerchantCategory; // 二级分类
    private ArrayList<MerchantProperty> properties;
    private List<String> lpSingle;
    private List<String> propertyList;
    private List<List<String>> categoryList;

    private HljRoundProgressDialog progressDialog;
    private SubscriptionList uploadSubscriptions;
    private HljHttpSubscriber initSubscriber;
    private HljHttpSubscriber submitSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_edit_shop);
        ButterKnife.bind(this);
        initValue();
        initView();
    }

    private void initValue() {
        logoWidth = CommonUtil.dp2px(this, 44);
        coverWidth = Math.round(logoWidth * 16 / 9);
        realPhotos = new ArrayList<>();
        currentUser = Session.getInstance()
                .getCurrentUser(this);
        isCreate = currentUser.getExamine() == -1;
        getAddressAreaData();
    }

    private void initView() {
        setTitle(R.string.title_activity_edit_merchant);
        if (isCreate) {
            progressBar.setVisibility(View.VISIBLE);
            new PropertiesUtil.PropertiesSyncTask(this, new PropertiesUtil.OnFinishedListener() {
                @Override
                public void onFinish(ArrayList<MerchantProperty> properties) {
                    getPropertiesFile(null);
                    progressBar.setVisibility(View.GONE);
                    hideBackButton();
                    scrollView.setVisibility(View.VISIBLE);
                    alertLayout.setVisibility(View.GONE);
                    btnSubmit.setText(R.string.label_create_merchant);
                }
            }).execute();
        } else {
            btnSubmit.setText(R.string.label_submit);
            getMerchantInfo();
        }
    }

    @OnClick(R.id.btn_submit)
    void onSubmit() {
        // 校验信息
        if (TextUtils.isEmpty(logoPath)) {
            ToastUtil.showToast(this, null, R.string.msg_empty_logo2);
            return;
        }
        if (TextUtils.isEmpty(etMerchantName.getText()
                .toString())) {
            ToastUtil.showToast(this, null, R.string.msg_empty_merchant_name);
            return;
        }
        if (Util.calculateLength(etMerchantName.getText()
                .toString()) > 20) {
            ToastUtil.showToast(this, null, R.string.msg_merchant_name_max_20);
            return;
        }
        if (Util.calculateLength(tvDesc.getText()
                .toString()) > 100) {
            ToastUtil.showToast(this, null, R.string.msg_merchant_desc_max_100);
            return;
        }
        if (selectedArea == null) {
            // 没有选择所在地区,根据有没有city,就是当前area text view中有没有值进行不同提示
            if (tvMerchantAddressArea.length() > 0) {
                DialogUtil.createDoubleButtonDialog(this,
                        getString(R.string.msg_select_address_area),
                        getString(R.string.label_confirm),
                        null,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onSelectedAddressAreas();
                            }
                        },
                        null)
                        .show();
                return;
            } else {
                ToastUtil.showToast(this, null, R.string.msg_empty_address_area);
                return;
            }
        }
        if (selectedMerchantProperty == null || (!selectedMerchantProperty.getChildren()
                .isEmpty() && TextUtils.isEmpty(selectedMerchantCategory.getName()))) {
            ToastUtil.showToast(this, null, R.string.msg_empty_service_type);
            return;
        }
        if (TextUtils.isEmpty(etContactAddress.getText()
                .toString())) {
            ToastUtil.showToast(this, null, R.string.msg_empty_contact_address);
            return;
        }
        if (!(latitude > 0 && longitude > 0)) {
            ToastUtil.showToast(this, null, R.string.msg_empty_location);
            return;
        }
        if (TextUtils.isEmpty(etContactMobile.getText()
                .toString())) {
            ToastUtil.showToast(this, null, R.string.msg_empty_contact_phone);
            return;
        }
        if (TextUtils.isEmpty(etContactPhone.getText()
                .toString())) {
            ToastUtil.showToast(this, null, R.string.msg_empty_merchant_phone);
            return;
        }
        if (!Util.isMobileNO(etContactMobile.getText()
                .toString())) {
            ToastUtil.showToast(this, null, R.string.msg_invalid_phone);
            return;
        }
        if (TextUtils.isEmpty(etEmail.getText()
                .toString())) {
            ToastUtil.showToast(this, null, R.string.msg_empty_email);
            return;
        }
        if (!Util.isEmailValid(etEmail.getText()
                .toString())) {
            ToastUtil.showToast(this, null, R.string.msg_wrong_email);
            return;
        }
        if (TextUtils.isEmpty(tvDesc.getText()
                .toString())) {
            ToastUtil.showToast(this, null, R.string.msg_empty_merchant_intro);
            return;
        }
        if (TextUtils.isEmpty(coverPath)) {
            // 如果原本就没有封面,现在也没有选择,则提示需要选择封面
            ToastUtil.showToast(this, null, R.string.msg_empty_cover);
            return;
        }

        if (hasChangedMerchantPhoto) {
            if (selectPhotos != null) {
                int size = selectPhotos.size();
                if (size > 0) {
                    if (size < 3) {
                        ToastUtil.showToast(this, null, R.string.label_merchant_photo_limit);
                        return;
                    }
                }
            }
        }

        if (!hasChangedContent && !hasChangedMerchantPhoto) {
            DialogUtil.createSingleButtonDialog(this,
                    getString(R.string.msg_changed_nothing),
                    null,
                    null)
                    .show();
            return;
        }

        if (submitAlertDlg != null && submitAlertDlg.isShowing()) {
            return;
        }
        submitAlertDlg = DialogUtil.createDoubleButtonDialog(this,
                getString(R.string.msg_confirm_submit),
                getString(R.string.label_confirm),
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isLocalPath(logoPath) && !isLocalPath(coverPath) && CommonUtil
                                .isCollectionEmpty(
                                selectPhotos)) {
                            // 没有需要上传的图片,直接提交资料
                            submitMerchantInfo();
                        } else {
                            uploadImage();
                        }
                    }
                },
                null);
        submitAlertDlg.show();
    }

    @OnClick(R.id.select_address_area_layout)
    void onSelectedAddressAreas() {
        if (areaPickerDlg != null && areaPickerDlg.isShowing()) {
            return;
        }
        if (addressMap == null || addressMap.isEmpty()) {
            return;
        }
        areaPickerDlg = new Dialog(this, R.style.BubbleDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.address_area_picker, null);
        final ThreeLevelPicker picker = (ThreeLevelPicker) view.findViewById(R.id.picker);
        int index[] = AddressAreaUtil.getIndexesFromArea(this,
                selectedProvince,
                selectedCity,
                selectedArea);
        int i1 = index.length > 0 ? index[0] : 0;
        int i2 = index.length > 1 ? index[1] : 0;
        int i3 = index.length > 2 ? index[2] : 0;
        picker.setItems(provinceList, cityList, areaList, i1, i2, i3);

        view.findViewById(R.id.close)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        areaPickerDlg.cancel();
                    }
                });
        view.findViewById(R.id.confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        areaPickerDlg.cancel();
                        int[] selectedItems = picker.getCurrentItems();
                        selectedProvince = addressList.get(selectedItems[0]);
                        selectedCity = selectedProvince.getChildren()
                                .get(selectedItems[1]);
                        selectedArea = selectedCity.getChildren()
                                .size() > 0 ? selectedCity.getChildren()
                                .get(selectedItems[2]) : null;

                        areaPickerDlg.cancel();

                        tvMerchantAddressArea.setText(selectedProvince.getAreaName() + "  " +
                                selectedCity.getAreaName() + (selectedArea == null ? "" : "  " +
                                selectedArea.getAreaName()));
                    }
                });

        areaPickerDlg.setContentView(view);
        Window win = areaPickerDlg.getWindow();
        assert win != null;
        ViewGroup.LayoutParams params = win.getAttributes();
        Point point = new Point();
        getWindowManager().getDefaultDisplay()
                .getSize(point);
        params.width = point.x;
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialog_anim_rise_style);
        areaPickerDlg.show();
    }

    @OnClick(R.id.logo_layout)
    void onChangeLogo() {
        photoType = 1;
        changeLogo();
    }

    @OnClick(R.id.img_bubble)
    void onImgBubble() {
        photoType = 1;
        changeLogo();
    }

    @OnClick(R.id.cover_layout)
    void onChangeCover() {
        photoType = 2;
        changeLogo();
    }

    @OnClick(R.id.merchant_photo)
    void onMerchantPhoto() {
        if (currentUser == null) {
            return;
        }
        Intent intent;
        if (!CommonUtil.isCollectionEmpty(realPhotos) || (selectPhotos != null)) {
            //有照片
            intent = new Intent(this, ShopPhotoActivity.class);
            intent.putExtra("selectedPhotos", selectPhotos == null ? realPhotos : selectPhotos);
        } else {
            //没有照片
            intent = new Intent(this, ImageChooserActivity.class);
            intent.putExtra("limit", 12);
        }
        startActivityForResult(intent, Constants.RequestCode.CHOOSE_PHOTO);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);

    }

    @OnClick(R.id.merchant_property_layout)
    void onSelectProperty() {
        if (merchantTypeDialog != null && merchantTypeDialog.isShowing()) {
            return;
        }
        if (!isCreate) {
            // 编辑的时候要求必须已经有property一级分类
            if (selectedMerchantProperty == null || selectedMerchantProperty.getChildren()
                    .isEmpty()) {
                // 没有二级类目,不予响应
                return;
            }
        }
        if (properties == null || properties.isEmpty()) {
            return;
        }

        if (isCreate) {
            merchantTypeDialog = new Dialog(this, R.style.BubbleDialogTheme);
            View v = getLayoutInflater().inflate(R.layout.properties_wheel_picker, null);
            final TwoLevelPicker picker = (TwoLevelPicker) v.findViewById(R.id.picker);
            picker.setItems(propertyList, categoryList);

            TextView close = (TextView) v.findViewById(R.id.close);
            TextView confirm = (TextView) v.findViewById(R.id.confirm);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    merchantTypeDialog.cancel();
                }
            });
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    merchantTypeDialog.cancel();
                    int[] selectedItems = picker.getCurrentItems();
                    MerchantProperty merchantProperty = properties.get(selectedItems[0]);
                    MerchantProperty merchantCategory = null;
                    if (!merchantProperty.getChildren()
                            .isEmpty()) {
                        merchantCategory = merchantProperty.getChildren()
                                .get(selectedItems[1]);
                    }

                    showPropertyAlertDlg(merchantCategory, merchantProperty);
                }
            });
            merchantTypeDialog.setContentView(v);
        } else {
            merchantTypeDialog = new Dialog(this, R.style.BubbleDialogTheme);
            View v = getLayoutInflater().inflate(R.layout.single_wheel_picker___cm, null);
            final SingleWheelPicker picker = (SingleWheelPicker) v.findViewById(R.id.picker);
            picker.setItems(lpSingle);

            TextView close = (TextView) v.findViewById(R.id.close);
            TextView confirm = (TextView) v.findViewById(R.id.confirm);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    merchantTypeDialog.cancel();
                }
            });
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    merchantTypeDialog.cancel();
                    int selectedItemIndex = picker.getCurrentItem();
                    MerchantProperty merchantCategory = selectedMerchantProperty.getChildren()
                            .get(selectedItemIndex);
                    // 只有新选择类型或者更改才进行修改和提示
                    if (selectedMerchantCategory == null || JSONUtil.isEmpty(
                            selectedMerchantCategory.getName()) || !merchantCategory.getId()
                            .equals(selectedMerchantCategory.getId())) {
                        hasChangedContent = true;
                        selectedMerchantCategory = merchantCategory;

                        tvMerchantProperty.setText(selectedMerchantProperty.getName() + "  " +
                                selectedMerchantCategory.getName());
                        tvMerchantProperty.setTextColor(getResources().getColor(R.color
                                .colorBlack2));
                    }
                }
            });
            merchantTypeDialog.setContentView(v);
        }

        Window win = merchantTypeDialog.getWindow();
        ViewGroup.LayoutParams params = win.getAttributes();
        Point point = new Point();
        getWindowManager().getDefaultDisplay()
                .getSize(point);
        params.width = point.x;
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialog_anim_rise_style);
        merchantTypeDialog.show();
    }

    @OnClick(R.id.mark_on_map_layout)
    void onMarkMap() {
        addressString = etContactAddress.getText()
                .toString();
        if (addressString.length() == 0) {
            Util.showToast(this, "", R.string.msg_address_required2);
        } else {
            EditShopActivityPermissionsDispatcher.onMarkMapsWithCheck(this);
        }
    }

    @OnClick(R.id.edit_intro_layout)
    void onEditIntro() {
        Intent intent = new Intent(this, EditShopIntroActivity.class);
        intent.putExtra("intro",
                tvDesc.getText()
                        .toString());
        startActivityForResult(intent, Constants.RequestCode.EDIT_MERCHANT_INTRO);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.LOCATION:
                    if (data != null) {
                        latitude = data.getDoubleExtra(LocationMapActivity.ARG_LATITUDE, 0);
                        longitude = data.getDoubleExtra(LocationMapActivity.ARG_LONGITUDE, 0);
                        tvMapMark.setText((latitude > 0 && longitude > 0) ? getString(R.string
                                .label_marked_on_map) : getString(
                                R.string.label_mark_on_map));
                        hasChangedContent = true;
                    }
                    break;
                case Constants.RequestCode.EDIT_MERCHANT_INTRO:
                    if (data != null) {
                        briefIntro = data.getStringExtra("intro");
                        tvDesc.setText(briefIntro);
                        hasChangedContent = true;
                    }
                    break;
                case Constants.RequestCode.CHOOSE_PHOTO:
                    if (data != null) {
                        selectPhotos = data.getParcelableArrayListExtra("selectedPhotos");
                        if (selectPhotos != null && merchantPropertyHint != null) {
                            hasChangedMerchantPhoto = true;
                            merchantPropertyHint.setText(getString(R.string.label_upload_photos,
                                    String.valueOf(selectPhotos.size())));
                        }
                    }
                    break;
                default:
                    if (requestCode == Source.CROP.ordinal()) {
                        if (cropUri == null) {
                            return;
                        }
                        if (photoType == 1) {
                            setLogoPhoto(cropUri);
                        } else if (photoType == 2) {
                            setCoverPhoto(cropUri);
                        }
                        return;
                    }
                    String path;
                    File file = null;
                    if (requestCode == Source.CAMERA.ordinal()) {
                        path = Environment.getExternalStorageDirectory() + File.separator +
                                "temp" + ".jpg";
                        file = new File(path);
                    }
                    if (requestCode == Source.GALLERY.ordinal()) {
                        if (data == null) {
                            return;
                        }
                        file = new File(JSONUtil.getImagePathForUri(data.getData(),
                                EditShopActivity.this));
                    }
                    if (file != null) {
                        showPhotoCrop(file);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showPhotoCrop(File file) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, getPackageName(), file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        if (photoType == 1) {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        } else if (photoType == 2) {
            intent.putExtra("aspectX", 1080);
            intent.putExtra("aspectY", 472);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");

        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        File f = FileUtil.createCropImageFile();
        cropUri = Uri.fromFile(f);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, Source.CROP.ordinal());
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    //选择商家类别确认提示框
    private void showPropertyAlertDlg(
            final MerchantProperty merchantCategory, final MerchantProperty merchantProperty) {
        if (propertyAlertDlg != null && propertyAlertDlg.isShowing()) {
            return;
        }
        String propertyName = merchantProperty == null ? selectedMerchantProperty.getName() :
                merchantProperty.getName();
        String categoryName = merchantCategory == null ? "" : "  " + merchantCategory.getName();
        propertyAlertDlg = DialogUtil.createDoubleButtonDialog(this,
                getString(R.string.msg_selected_property, propertyName + categoryName),
                getString(R.string.msg_confirm_selected_property),
                getString(R.string.label_confirm),
                getString(R.string.label_think_again),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hasChangedContent = true;
                        propertyAlertDlg.cancel();
                        if (merchantProperty != null) {
                            selectedMerchantProperty = merchantProperty;
                        }
                        selectedMerchantCategory = merchantCategory;

                        String categoryName = selectedMerchantCategory == null ? "" : "  " +
                                selectedMerchantCategory.getName();
                        tvMerchantProperty.setText(selectedMerchantProperty.getName() +
                                categoryName);
                        //如果是四大的话 修改个人展示的说明文案
                        if (selectedMerchantProperty.getId() == MerchantUser
                                .PROPERTY_WEDDING_PHOTO | selectedMerchantProperty.getId() ==
                                MerchantUser.PROPERTY_WEDDING_SHOOTING ||
                                selectedMerchantProperty.getId() == MerchantUser
                                        .PROPERTY_WEDDING_MAKEUP || selectedMerchantProperty
                                .getId() == MerchantUser.PROPERTY_WEDDING_COMPERE) {
                            tvMerchantPhotosHint.setText(getString(R.string
                                    .hint_merchant_photos_personal));
                        } else {
                            tvMerchantPhotosHint.setText(getString(R.string.hint_merchant_photos));
                        }
                    }
                },
                null);
        propertyAlertDlg.show();
    }

    /**
     * 根据User审核状态,判断当前页面显示的数据
     */
    private void setMerchantContent(MerchantUser user) {
        scrollView.setVisibility(View.VISIBLE);
        if (user.getExamine() == 0 || user.getExamine() == 1) {
            // 已经开店成功(提交过开店资料),正在审核或者已通过第一次审核
            if (user.getExamine() == 1 && user.getStatus() == 2) {
                // 审核通过状态,直接显示当前用户的资料
                alertLayout.setVisibility(View.GONE);
            } else if (user.getStatus() == 0) {
                // 正在审核的状态,显示新提交正在审核的资料
                alertLayout.setVisibility(View.VISIBLE);
                tvAlertMsg.setText(R.string.hint_merchant_reviewing);
            } else if (user.getStatus() == 1) {
                // 审核不通过,显示原因,显示新提交的未通过审核的资料
                alertLayout.setVisibility(View.VISIBLE);
                tvAlertMsg.setText(TextUtils.isEmpty(user.getReason()) ? "未通过审核！" : "未通过审核！原因：" +
                        user.getReason());
            }
            setContentInfo(user);
        } else {
            // 没有开店,显示创建店铺的页面
            alertLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 将特定的用户资料显示在页面上,并且将这个用资料赋值给对应的变量
     *
     * @param user
     */
    private void setContentInfo(MerchantUser user) {
        etContactMobile.addTextChangedListener(textWatcher);
        etContactPhone.addTextChangedListener(textWatcher);
        etContactAddress.addTextChangedListener(textWatcher);
        etMerchantName.addTextChangedListener(textWatcher);
        tvMerchantProperty.addTextChangedListener(textWatcher);
        tvMerchantAddressArea.addTextChangedListener(textWatcher);
        tvDesc.addTextChangedListener(textWatcher);
        etEmail.addTextChangedListener(textWatcher);

        logoPath = user.getLogoPath();
        setImgLogo(ImagePath.buildPath(logoPath)
                .width(logoWidth)
                .height(logoWidth)
                .cropPath());
        etMerchantName.setText(user.getName());
        //省市区
        ArrayList<AddressArea> addressAreas = user.getShopAreas();
        if (addressAreas != null && addressAreas.size() > 0) {
            selectedProvince = addressAreas.get(0);
            if (addressAreas.size() > 1) {
                selectedCity = addressAreas.get(1);
            }
            if (addressAreas.size() > 2) {
                selectedArea = addressAreas.get(2);
            }
        }
        // shop_area里面没有值,但是有city,可能就是老用户
        tvMerchantAddressArea.setText(null);
        if (selectedProvince != null && selectedCity != null && selectedArea != null) {
            String areaStr = selectedProvince.getAreaName() + " " + selectedCity.getAreaName() +
                    " " + selectedArea.getAreaName();
            tvMerchantAddressArea.setText(areaStr);
        } else if (!JSONUtil.isEmpty(user.getCityName())) {
            tvMerchantAddressArea.setHint(R.string.label_select_address_area2);
            tvMerchantAddressArea.setHintTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            tvMerchantAddressArea.setHint(R.string.label_select_address_area);
            tvMerchantAddressArea.setHintTextColor(getResources().getColor(R.color.colorGray2));
        }
        //地图标注
        etContactAddress.setText(user.getAddress());
        latitude = user.getLatitude();
        longitude = user.getLongitude();
        tvMapMark.setText((latitude > 0 && longitude > 0) ? getString(R.string
                .label_marked_on_map) : getString(
                R.string.label_mark_on_map));
        etContactMobile.setText(user.getContactMobile());
        if (!TextUtils.isEmpty(user.getShopPhone())) {
            etContactPhone.setText(user.getShopPhone());
        }

        etEmail.setText(user.getEmail());
        briefIntro = user.getDes();
        tvDesc.setText(briefIntro);
        coverPath = user.getCoverPath();
        setImgCover(ImagePath.buildPath(coverPath)
                .width(coverWidth)
                .height(coverWidth)
                .cropPath());
        showHintImage(user);
        initPropertyView(user);
        hasChangedContent = false;
        hasChangedMerchantPhoto = false;
        if (!CommonUtil.isCollectionEmpty(user.getRealPhotos())) {
            for (com.hunliji.marrybiz.model.Photo realPhoto : user.getRealPhotos()) {
                Photo photo = new Photo();
                photo.setId(realPhoto.getId());
                photo.setHeight(realPhoto.getHeight());
                photo.setWidth(realPhoto.getWidth());
                photo.setImagePath(realPhoto.getImagePath());
                realPhotos.add(photo);
            }
        }
        if (user.isIndividualMerchant()) {
            //四大金刚
            merchantProperty.setText(getString(R.string.label_personal_show));
            tvMerchantPhotosHint.setText(getString(R.string.hint_merchant_photos_personal));
            if (realPhotos != null) {
                merchantPropertyHint.setText(getString(R.string.label_upload_photos,
                        String.valueOf(realPhotos.size())));
            } else {
                merchantPropertyHint.setText(getString(R.string.label_personal_elegant));
            }
        } else {
            long propertyId = user.getPropertyId();
            tvMerchantProperty.setTextColor(ContextCompat.getColor(this, R.color.colorBlack2));
            if (propertyId == 2 || propertyId == 6) {
                //婚礼策划 婚纱摄影
                merchantProperty.setText(getString(R.string.label_merchant_photos));
                if (realPhotos != null) {
                    merchantPropertyHint.setText(getString(R.string.label_upload_photos,
                            String.valueOf(realPhotos.size())));
                } else {
                    merchantPropertyHint.setText(getString(R.string.label_merchant_photos_hint));
                }
            } else if (propertyId == 13) {
                merchantProperty.setText(getString(R.string.label_merchant_hotel));
                if (realPhotos != null) {
                    merchantPropertyHint.setText(getString(R.string.label_upload_photos,
                            String.valueOf(realPhotos.size())));
                } else {
                    merchantPropertyHint.setText(getString(R.string.label_merchant_hotel_hint));
                }
            } else {
                if (realPhotos != null) {
                    merchantPropertyHint.setText(getString(R.string.label_upload_photos,
                            String.valueOf(realPhotos.size())));
                } else {
                    merchantPropertyHint.setText(getString(R.string.label_merchant_photos_hint));
                }
            }
        }
    }

    //初始化商家类别选择
    private void initPropertyView(MerchantUser user) {
        getPropertiesFile(user.getProperty());
        selectedMerchantCategory = user.getCategory();
        String categoryName = selectedMerchantCategory == null || JSONUtil.isEmpty(
                selectedMerchantCategory.getName()) ? "" : "  " + selectedMerchantCategory
                .getName();
        tvMerchantProperty.setText(selectedMerchantProperty.getName() + categoryName);

        if (!selectedMerchantProperty.getChildren()
                .isEmpty() && JSONUtil.isEmpty(selectedMerchantCategory.getName())) {
            tvMerchantProperty.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            tvMerchantProperty.setTextColor(getResources().getColor(R.color.colorBlack2));
        }
    }

    //上传图片
    private void uploadImage() {
        final ArrayList<Photo> photos = new ArrayList<>();
        if (isLocalPath(logoPath)) {
            Photo photo = new com.hunliji.hljcommonlibrary.models.Photo();
            photo.setImagePath(logoPath);
            photo.setId(-1);
            photos.add(photo);
        }
        if (isLocalPath(coverPath)) {
            Photo photo = new Photo();
            photo.setImagePath(coverPath);
            photo.setId(-2);
            photos.add(photo);
        }
        if (!CommonUtil.isCollectionEmpty(selectPhotos)) {
            photos.addAll(selectPhotos);
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }
        progressDialog = DialogUtil.getRoundProgress(this);
        progressDialog.show();
        if (uploadSubscriptions != null) {
            uploadSubscriptions.clear();
        }
        uploadSubscriptions = new SubscriptionList();
        new PhotoListUploadUtil(this,
                photos,
                progressDialog,
                uploadSubscriptions,
                new OnFinishedListener() {
                    @Override
                    public void onFinished(Object... objects) {
                        if (!CommonUtil.isCollectionEmpty(photos)) {

                            for (com.hunliji.hljcommonlibrary.models.Photo photo : photos) {
                                if (photo.getId() == -1) {
                                    logoPath = photo.getImagePath();
                                } else if (photo.getId() == -2) {
                                    coverPath = photo.getImagePath();
                                }
                            }
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            submitMerchantInfo();
                        }
                    }
                }).startUpload();

    }

    private boolean isLocalPath(String path) {
        return !TextUtils.isEmpty(path) && !path.startsWith("http://") && !path.startsWith(
                "https://");
    }

    //新上传logo提示
    private void showHintImage(MerchantUser user) {
        if (TextUtils.isEmpty(user.getSquareLogoPath())) {
            imgBubble.setVisibility(View.VISIBLE);
            if (animator == null) {
                animator = ObjectAnimator.ofFloat(imgBubble, "translationY", 0, -30);
                animator.setDuration(800);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setRepeatCount(5);
                animator.setStartDelay(100);
                animator.setRepeatMode(ValueAnimator.REVERSE);
                animator.start();
            }
        } else {
            imgBubble.setVisibility(View.GONE);
        }
    }

    private void setLogoPhoto(Uri uri) {
        String path = JSONUtil.getImagePathForUri(uri, this);
        if (!JSONUtil.isEmpty(path)) {
            hasChangedContent = true;
            logoPath = path;
            photoType = 0;
            imgBubble.setVisibility(View.GONE);
            setImgLogo(path);
        }
    }

    private void setCoverPhoto(Uri uri) {
        String path = JSONUtil.getImagePathForUri(uri, this);
        if (!JSONUtil.isEmpty(path)) {
            hasChangedContent = true;
            coverPath = path;
            photoType = 0;
            setImgCover(path);
        }
    }

    private void setImgLogo(String path) {
        Glide.with(this)
                .load(path)
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(imgLogo);
    }

    private void setImgCover(String path) {
        Glide.with(this)
                .load(path)
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(imgCover);
    }

    /**
     * 从properties json数据中获取对应的二级列表,并将完成的property赋值给selecMerchantProperty
     *
     * @param currentProperty
     */
    private void getPropertiesFile(MerchantProperty currentProperty) {
        properties = PropertiesUtil.getPropertiesFromFile(EditShopActivity.this);
        lpSingle = new ArrayList<>();
        propertyList = new ArrayList<>();
        categoryList = new ArrayList<>();
        for (MerchantProperty property : properties) {
            // 一级分类无法再修改,所以可选项只有这个一级分类下的二级分类
            propertyList.add(property.getName());
            List<String> childList = new ArrayList<>();
            if (!property.getChildren()
                    .isEmpty()) {
                for (MerchantProperty cp : property.getChildren()) {
                    if (!JSONUtil.isEmpty(cp.getDesc())) {
                        childList.add(cp.getName() + "(" + cp.getDesc() + ")");
                    } else {
                        childList.add(cp.getName());
                    }
                }
            } else {
                childList.add(" ");
            }

            if (currentProperty != null && currentProperty.getId()
                    .equals(property.getId())) {
                selectedMerchantProperty = property;

                if (!selectedMerchantProperty.getChildren()
                        .isEmpty()) {
                    for (MerchantProperty cp : selectedMerchantProperty.getChildren()) {
                        if (!JSONUtil.isEmpty(cp.getDesc())) {
                            lpSingle.add(cp.getName() + "(" + cp.getDesc() + ")");
                        } else {
                            lpSingle.add(cp.getName());
                        }
                    }
                }
            }
            categoryList.add(childList);
        }
        if (selectedMerchantProperty == null) {
            selectedMerchantProperty = new MerchantProperty(new JSONObject());
        }
    }

    //获取区域数据
    private void getAddressAreaData() {
        new AddressAreaUtil.GetAddressAreaDataTask(this, new AddressAreaUtil.OnFinishListener() {
            @Override
            public void onFinish(
                    ArrayList<AddressArea> al,
                    LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> am) {
                addressList = al;
                addressMap = am;
                // 同步区域数据
                new AddressAreaUtil.SyncAddressAreaTask(EditShopActivity.this).execute();
            }

            @Override
            public void onFinish(
                    List<String> ls1, List<List<String>> ls2, List<List<List<String>>> ls3) {
                provinceList = ls1;
                cityList = ls2;
                areaList = ls3;
            }
        }).execute();
    }

    //获取当前正在审核的店铺资料
    private void getMerchantInfo() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                        @Override
                        public void onNext(JsonElement jsonElement) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(jsonElement.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            reviewingUser = new MerchantUser(jsonObject);
                            setMerchantContent(reviewingUser);
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            MerchantApi.getMerchantInfoObb(true)
                    .subscribe(initSubscriber);
        }
    }

    //提交店铺资料
    private void submitMerchantInfo() {
        if (submitSubscriber == null || submitSubscriber.isUnsubscribed()) {
            submitSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            // 提交成功
                            ToastUtil.showToast(EditShopActivity.this,
                                    null,
                                    R.string.msg_succeed_submit_merchant);
                            MerchantUserSyncUtil.getInstance()
                                    .sync(EditShopActivity.this, null);
                            if (currentUser.getCertifyStatus() == 0) {
                                Dialog dialog = DialogUtil.createDoubleButtonDialog(EditShopActivity
                                                .this,

                                        "提交成功",
                                        "检测到您尚未提交实名认证\n是否立即提交",
                                        "立即提交",
                                        "稍后提交",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent();
                                                long propertyId = selectedMerchantProperty ==
                                                        null ? 0 : selectedMerchantProperty.getId();
                                                if (currentUser.isIndividualMerchant(propertyId)
                                                        || currentUser.isIndividualMerchant()) {
                                                    intent.setClass(EditShopActivity.this,
                                                            PreCertificationActivity.class);
                                                } else {
                                                    intent.setClass(EditShopActivity.this,
                                                            CompanyCertificationActivity.class);
                                                }
                                                startActivity(intent);
                                                finish();
                                            }
                                        },
                                        null);
                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener
                                        () {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        EditShopActivity.super.onBackPressed();
                                    }
                                });
                                dialog.show();
                            } else if (currentUser.getExamine() != 1) {
                                Dialog dialog = DialogUtil.createSingleButtonDialog(EditShopActivity
                                                .this,
                                        "提交成功",
                                        "审核预计需要1~2个工作日，审核结果将以短信形式通知到您，请注意查收。",
                                        "返回首页",
                                        null);
                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener
                                        () {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        Intent intent = new Intent();
                                        intent.setClass(EditShopActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                dialog.show();
                            } else {
                                // 提交成功
                                ToastUtil.showToast(EditShopActivity.this,
                                        null,
                                        R.string.msg_succeed_submit_merchant);
                                EditShopActivity.super.onBackPressed();
                            }
                        }
                    })
                    .build();
            HashMap<String, Object> map = new HashMap<>();
            map.put("logo_path_square", logoPath);
            map.put("cover_path", coverPath);
            map.put("name",
                    etMerchantName.getText()
                            .toString());
            map.put("property", selectedMerchantProperty.getId());
            if (selectedMerchantCategory != null && !JSONUtil.isEmpty(selectedMerchantCategory
                    .getName())) {
                map.put("category_id", selectedMerchantCategory.getId());
            }
            map.put("shop_area_id",
                    selectedArea == null ? selectedCity.getId() : selectedArea.getId());
            map.put("address",
                    etContactAddress.getText()
                            .toString());
            if (latitude > 0 && longitude > 0) {
                map.put("latitude", String.valueOf(latitude));
                map.put("longitude", String.valueOf(longitude));
            }
            map.put("contact_mobile",
                    etContactMobile.getText()
                            .toString());
            map.put("shop_phone",
                    etContactPhone.getText()
                            .toString());
            map.put("email",
                    etEmail.getText()
                            .toString());
            JsonArray jsonArray = new JsonArray();
            if (!CommonUtil.isCollectionEmpty(selectPhotos)) {
                for (Photo photo : selectPhotos) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("img", photo.getImagePath());
                    jsonObject.addProperty("height", photo.getHeight());
                    jsonObject.addProperty("width", photo.getWidth());
                    jsonArray.add(jsonObject);
                }
            }
            map.put("real_photos", jsonArray);
            map.put("des",
                    tvDesc.getText()
                            .toString());
            MerchantApi.postMerchantInfoObb(map)
                    .subscribe(submitSubscriber);
        }
    }

    private void changeLogo() {
        photoDialog = new Dialog(this, R.style.BubbleDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_menu, null);
        Button galleryBtn = (Button) view.findViewById(R.id.action_gallery);
        Button videosBtn = (Button) view.findViewById(R.id.action_camera_video);
        Button cameraBtn = (Button) view.findViewById(R.id.action_camera_photo);
        view.findViewById(R.id.action_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photoDialog.dismiss();
                    }
                });
        videosBtn.setVisibility(View.GONE);
        galleryBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditShopActivityPermissionsDispatcher.onReadPhotosWithCheck(EditShopActivity.this);
                photoDialog.dismiss();
            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditShopActivityPermissionsDispatcher.onTakePhotosWithCheck(EditShopActivity.this);
                photoDialog.dismiss();
            }
        });

        photoDialog.setContentView(view);
        Window win = photoDialog.getWindow();
        ViewGroup.LayoutParams params = win.getAttributes();
        Point point = new Point();
        getWindowManager().getDefaultDisplay()
                .getSize(point);
        params.width = point.x;
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialog_anim_rise_style);
        photoDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (hasChangedContent) {
            if (quitDialog != null && quitDialog.isShowing()) {
                return;
            }
            quitDialog = DialogUtil.createDoubleButtonDialog(this,
                    getString(R.string.msg_quit_edit_merchant),
                    getString(R.string.label_confirm),
                    null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditShopActivity.super.onBackPressed();
                        }
                    },
                    null);
            quitDialog.show();
        } else {
            super.onBackPressed();
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(
                CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(
                CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            hasChangedContent = true;
        }
    };

    private enum Source {
        GALLERY, CAMERA, CROP
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onReadPhotos() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Source.GALLERY.ordinal());
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onTakePhotos() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this, getPackageName(), f);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            imageUri = Uri.fromFile(f);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, Source.CAMERA.ordinal());
    }


    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void onMarkMaps() {
        Intent intent = new Intent(this, LocationMapActivity.class);
        intent.putExtra(LocationMapActivity.ARG_ADDRESS, addressString);
        if (latitude > 0 && longitude > 0) {
            intent.putExtra(LocationMapActivity.ARG_LATITUDE, latitude);
            intent.putExtra(LocationMapActivity.ARG_LONGITUDE, longitude);
        }
        startActivityForResult(intent, Constants.RequestCode.LOCATION);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EditShopActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(uploadSubscriptions, initSubscriber, submitSubscriber);
    }
}
