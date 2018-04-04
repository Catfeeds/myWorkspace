package me.suncloud.marrymemo.view;

import android.Manifest;
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
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kankan.wheel.widget.CitiesPickerView;
import kankan.wheel.widget.DTPicker;
import kankan.wheel.widget.DatePickerView;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.MenuItem;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.task.QiNiuUploadTask;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.widget.RoundProgressDialog;
import me.suncloud.marrymemo.widget.RoundProgressDialog.OnUpLoadComplate;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class AccountEditActivity extends HljBaseActivity implements DTPicker.OnPickerDateListener {


    @BindView(R.id.user_avatar)
    ImageView userAvatar;
    @BindView(R.id.tv_nick)
    TextView tvNick;
    @BindView(R.id.tv_wedding_day)
    TextView tvWeddingDay;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.tv_home_town)
    TextView tvHomeTown;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.tv_description)
    TextView tvDescription;

    private Calendar calendar;
    private Dialog dialog;
    private RoundProgressDialog progressDialog;
    private User u;
    private Uri cropUri;
    private String cropPath;
    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat postDateFormat;
    private Dialog datePickerDlg;
    private Calendar tempCalendar;
    private String time;
    private Dialog cityPickerDlg;
    private ArrayList<MenuItem> provinceNameList;
    private LinkedHashMap<Integer, ArrayList<City>> cityMap;
    private LinkedHashMap<String, ArrayList<String>> cityStrMap;
    private City selectedCity;
    private MenuItem selectedProvince;
    private Dialog genderDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);
        ButterKnife.bind(this);
        setSwipeBackEnable(false);
        simpleDateFormat = new SimpleDateFormat(getString(R.string.format_date_type7),
                Locale.getDefault());
        postDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_SHORT, Locale.getDefault());

        getCitiesFromFile();
        calendar = Calendar.getInstance();

        setUserInfo();
    }

    private void setUserInfo() {
        u = Session.getInstance()
                .getCurrentUser(this);

        if (!JSONUtil.isEmpty(u.getNick())) {
            tvNick.setText(u.getNick());
        }

        if (JSONUtil.getAge(u.getBirthday()) >= 0) {
            tvAge.setText(JSONUtil.getAge(u.getBirthday()) + "");
        } else {
            tvAge.setText(R.string.hint_setting_birthday);
        }

        if (!JSONUtil.isEmpty(u.getDescription())) {
            tvDescription.setText(u.getDescription());
        }

        String url = u.getAvatar();
        if (!JSONUtil.isEmpty(url) && !url.equals(userAvatar.getTag())) {
            ImageLoadTask task = new ImageLoadTask(userAvatar, 0);
            userAvatar.setTag(url);
            task.loadImage(url,
                    userAvatar.getLayoutParams().width,
                    ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(), R.mipmap.icon_avatar_primary, task));
        }

        //排除user中 isPending对weddingdate的干扰  然后恢复ispengding的值 避免对逻辑的影响
        int isPending = u.getIsPending();
        u.setIsPending(2);
        if (u.getWeddingDay() != null) {
            tvWeddingDay.setText(simpleDateFormat.format(u.getWeddingDay()));
        } else {
            tvWeddingDay.setText(R.string.label_set_wedding_date3);
        }
        u.setIsPending(isPending);

        tvGender.setText(u.getGender() == 1 ? R.string.label_male : R.string.label_female);

        if (!JSONUtil.isEmpty(u.getHometown()) && !"0".equals(u.getHometown())) {
            tvHomeTown.setText(u.getHometown());
        } else {
            tvHomeTown.setText(R.string.hint_fill_your_home);
        }
    }

    @OnClick(R.id.nick_layout)
    void editNick() {
        Intent intent = new Intent(this, EditNickNameActivity.class);
        startActivityForResult(intent, Constants.RequestCode.EDIT_NICK_NAME);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.avatar_layout)
    void changeAvatar() {
        showPopup();
    }

    @OnClick(R.id.wedding_date_layout)
    void pickWeddingDate() {
        if (datePickerDlg != null && datePickerDlg.isShowing()) {
            return;
        }
        datePickerDlg = new Dialog(this, R.style.BubbleDialogTheme);
        View v = getLayoutInflater().inflate(R.layout.dialog_date_picker, null);
        v.findViewById(R.id.close)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePickerDlg.dismiss();
                    }
                });
        v.findViewById(R.id.confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePickerDlg.dismiss();
                        if (tempCalendar != null) {
                            calendar.setTime(tempCalendar.getTime());
                        }
                        time = postDateFormat.format(calendar.getTime());

                        upLoadInfo("weddingday", time);
                    }
                });
        DatePickerView picker = (DatePickerView) v.findViewById(R.id.picker);
        picker.setYearLimit(2000, 49);
        if (u.getWeddingDay() != null) {
            calendar.setTime(u.getWeddingDay());
        }
        picker.setCurrentCalender(calendar);
        picker.setOnPickerDateListener(this);
        picker.getLayoutParams().height = Math.round(getResources().getDisplayMetrics().density *
                (24 * 8));
        datePickerDlg.setContentView(v);
        Window win = datePickerDlg.getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        Point point = JSONUtil.getDeviceSize(this);
        params.width = point.x;
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialog_anim_rise_style);

        datePickerDlg.show();
    }

    @OnClick(R.id.shipping_address_layout)
    void shippingAddressList() {
        Intent intent = new Intent(this, ShippingAddressListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.hometown_layout)
    void selectHometown() {
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
                //                tvHomeTown.setText(selectedProvince.getName() + selectedCity
                // .getName());
                cityPickerDlg.cancel();

                upLoadInfo("hometown", String.valueOf(selectedCity.getId()));
            }
        });
        cityPickerDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });

        cityPickerDlg.show();
    }

    @OnClick(R.id.gender_layout)
    void selectGender() {
        if (genderDlg != null && genderDlg.isShowing()) {
            return;
        }
        if (genderDlg == null) {
            genderDlg = new Dialog(this, R.style.BubbleDialogTheme);
            genderDlg.setContentView(R.layout.dialog_gender_menu);
            genderDlg.findViewById(R.id.action_cancel)
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            genderDlg.dismiss();
                        }
                    });
            genderDlg.findViewById(R.id.btn_male)
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            upLoadInfo("gender", String.valueOf(1));
                            genderDlg.dismiss();
                        }
                    });
            genderDlg.findViewById(R.id.btn_female)
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            upLoadInfo("gender", String.valueOf(2));
                            genderDlg.dismiss();
                        }
                    });
            Window win = genderDlg.getWindow();
            LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = point.x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        genderDlg.show();
    }

    @OnClick(R.id.intro_layout)
    void editIntro() {
        Intent intent = new Intent(this, EditIntroActivity.class);
        startActivityForResult(intent, Constants.RequestCode.EDIT_INTRO);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.age_layout)
    void selectBirthday() {
        if (datePickerDlg != null && datePickerDlg.isShowing()) {
            return;
        }
        datePickerDlg = new Dialog(this, R.style.BubbleDialogTheme);
        View v = getLayoutInflater().inflate(R.layout.dialog_date_picker, null);
        v.findViewById(R.id.close)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePickerDlg.dismiss();
                    }
                });
        TextView titleTv = (TextView) v.findViewById(R.id.tv_title);
        titleTv.setText(R.string.label_select_birthday);
        v.findViewById(R.id.confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar nowCalendar = Calendar.getInstance();
                        // 如果选择的时间在当前时间之前，则提示错误
                        if (tempCalendar != null && tempCalendar.after(nowCalendar)) {
                            Toast.makeText(AccountEditActivity.this,
                                    getString(R.string.msg_wrong_time3),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            datePickerDlg.dismiss();
                            if (tempCalendar != null) {
                                calendar.setTime(tempCalendar.getTime());
                            }
                            time = postDateFormat.format(calendar.getTime());

                            upLoadInfo("birthday", time);
                        }
                    }
                });
        DatePickerView picker = (DatePickerView) v.findViewById(R.id.picker);
        picker.setYearLimit(1900, 120);
        if (u.getBirthday() != null) {
            calendar.setTime(u.getBirthday());
        }
        picker.setCurrentCalender(calendar);
        picker.setOnPickerDateListener(this);
        picker.getLayoutParams().height = Math.round(getResources().getDisplayMetrics().density *
                (24 * 8));
        datePickerDlg.setContentView(v);
        Window win = datePickerDlg.getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        Point point = JSONUtil.getDeviceSize(this);
        params.width = point.x;
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialog_anim_rise_style);

        datePickerDlg.show();
    }

    private void getCitiesFromFile() {
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


    @Override
    public void onPickerDate(int year, int month, int day) {
        if (tempCalendar == null) {
            tempCalendar = new GregorianCalendar(year, month - 1, day, 0, 0);
        } else {
            tempCalendar.set(year, month - 1, day, 0, 0);
        }
    }

    private void uploadAvatarThenUploadInfo() {
        if (progressDialog == null) {
            progressDialog = JSONUtil.getRoundProgress(this);
        }
        progressDialog.show();
        if (!JSONUtil.isEmpty(cropPath) && !cropPath.startsWith("http://")) {
            new QiNiuUploadTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    JSONObject json = (JSONObject) obj;
                    if (json != null) {
                        String key = JSONUtil.getString(json, "image_path");
                        String domain = JSONUtil.getString(json, "domain");
                        if (!JSONUtil.isEmpty(key) && !JSONUtil.isEmpty(domain)) {
                            cropPath = domain + key;
                            if (progressDialog != null && progressDialog.isShowing()) {
                                upLoadInfo("avatar", cropPath);
                            }
                        }
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {
                    progressDialog.dismiss();
                }
            }, progressDialog).execute(Constants.getAbsUrl(Constants.HttpPath.QINIU_IMAGE_URL),
                    new File(cropPath));

        } else {
            upLoadInfo("avatar", cropPath);
        }
    }

    private void upLoadInfo(String key, String value) {
        if (progressDialog == null) {
            progressDialog = JSONUtil.getRoundProgress(this);
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        final Map<String, Object> data = new HashMap<>();
        data.put(key, value);


        progressDialog.onLoadComplate();
        new NewHttpPostTask(this, new OnHttpRequestListener() {

            @Override
            public void onRequestFailed(Object obj) {
                progressDialog.dismiss();
                Toast.makeText(AccountEditActivity.this,
                        getString(R.string.msg_fail_to_complete_profile),
                        Toast.LENGTH_SHORT)
                        .show();

            }

            @Override
            public void onRequestCompleted(Object obj) {
                JSONObject resultObj = (JSONObject) obj;
                if (resultObj != null && resultObj.optJSONObject("status") != null && resultObj
                        .optJSONObject(
                        "status")
                        .optInt("RetCode", -1) == 0) {
                    Toast.makeText(AccountEditActivity.this,
                            getString(R.string.msg_success_to_complete_profile),
                            Toast.LENGTH_SHORT)
                            .show();
                    JSONObject dataObj = resultObj.optJSONObject("data");
                    Session.getInstance()
                            .editCurrentUser(AccountEditActivity.this, dataObj);

                    Intent intent = getIntent();
                    intent.putExtra("refresh", true);
                    setResult(RESULT_OK, intent);
                } else {
                    Toast.makeText(AccountEditActivity.this,
                            getString(R.string.msg_fail_to_complete_profile),
                            Toast.LENGTH_SHORT)
                            .show();
                }

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.onLoadComplate();
                    progressDialog.setCancelable(false);
                    progressDialog.onComplate();
                    progressDialog.setOnUpLoadComplate(new OnUpLoadComplate() {
                        @Override
                        public void onUpLoadCompleted() {
                            setUserInfo();
                        }
                    });
                } else {
                    setUserInfo();
                }
            }
        }, progressDialog).execute(Constants.getAbsUrl(Constants.HttpPath.COMPLETE_USER_PROFILE),
                data);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.EDIT_NICK_NAME:
                    if (data != null && data.getBooleanExtra("refresh", false)) {
                        Intent intent = getIntent();
                        intent.putExtra("refresh", true);
                        setResult(RESULT_OK, intent);
                        setUserInfo();
                    }
                    break;
                case Constants.RequestCode.EDIT_INTRO:
                    if (data != null && data.getBooleanExtra("refresh", false)) {
                        Intent intent = getIntent();
                        intent.putExtra("refresh", true);
                        setResult(RESULT_OK, intent);
                        setUserInfo();
                    }
                    break;
                default:
                    if (requestCode == Source.CAMERA.ordinal() || requestCode == Source.CROP
                            .ordinal() || requestCode == Source.GALLERY.ordinal()) {
                        if (requestCode == Source.CROP.ordinal()) {
                            if (cropUri == null) {
                                return;
                            }
                            // 开始上传头像和同步资料
                            String path = JSONUtil.getImagePathForUri(cropUri, this);
                            if (!JSONUtil.isEmpty(path)) {
                                cropPath = path;
                                uploadAvatarThenUploadInfo();
                            }
                            return;
                        }
                        File file = null;
                        if (requestCode == Source.CAMERA.ordinal()) {
                            String path = Environment.getExternalStorageDirectory() + File
                                    .separator + "temp.jpg";
                            file = new File(path);
                        }
                        if (requestCode == Source.GALLERY.ordinal()) {
                            if (data == null) {
                                return;
                            }
                            file = new File(JSONUtil.getImagePathForUri(data.getData(),
                                    AccountEditActivity.this));
                        }
                        if (file != null) {
                            showPhotoCrop(file);
                        }
                    }
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showPopup() {
        if (dialog == null) {
            dialog = new Dialog(this, R.style.BubbleDialogTheme);
            dialog.setContentView(R.layout.dialog_add_menu);
            dialog.findViewById(R.id.action_camera_video)
                    .setVisibility(View.GONE);
            dialog.findViewById(R.id.action_cancel)
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            dialog.findViewById(R.id.action_gallery)
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            AccountEditActivityPermissionsDispatcher.onReadPhotosWithCheck(
                                    AccountEditActivity.this);
                        }
                    });
            dialog.findViewById(R.id.action_camera_photo)
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            AccountEditActivityPermissionsDispatcher.onTakePhotosWithCheck(
                                    AccountEditActivity.this);
                        }
                    });
            Window win = dialog.getWindow();
            LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = point.x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        dialog.show();
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onReadPhotos() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Source.GALLERY.ordinal());
        dialog.dismiss();
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onTakePhotos() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this,
                    getPackageName(),
                    new File(Environment.getExternalStorageDirectory(), "temp.jpg"));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        } else {
            imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                    "temp.jpg"));
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, Source.CAMERA.ordinal());
        dialog.dismiss();
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onRationaleCamera(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.permission_r_for_camera));
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onRationaleReadExternal(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.permission_r_for_read_external_storage));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AccountEditActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        //        cropUri = FileProvider.getUriForFile(this,
        //                getString(R.string.file_provider_authorities___cm),
        //                FileUtil.createCropImageFile());
        cropUri = Uri.fromFile(FileUtil.createCropImageFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, Source.CROP.ordinal());
    }


    private enum Source {
        GALLERY, CAMERA, CROP;
    }

}
