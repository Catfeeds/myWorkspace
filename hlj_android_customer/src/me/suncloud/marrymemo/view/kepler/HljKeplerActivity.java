package me.suncloud.marrymemo.view.kepler;

import android.Manifest;
import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hunliji.hljcommonlibrary.models.Location;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.entities.HljResultAction;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.paem.kepler.api.KeplerAgent;
import com.paem.kepler.api.KeplerCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.BuildConfig;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.kepler.KeplerApi;
import me.suncloud.marrymemo.model.kepler.Kepler;
import me.suncloud.marrymemo.model.kepler.KeplerBody;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Observable;
import rx.Subscriber;

/**
 * 平安普惠
 * Created by jinxin on 2016/11/1.
 */
@RuntimePermissions
public class HljKeplerActivity extends HljBaseActivity implements KeplerCallback {

    @BindView(R.id.img_header)
    ImageView imgHeader;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.img_name_error)
    ImageView imgNameError;
    @BindView(R.id.et_identity)
    EditText etIdentity;
    @BindView(R.id.img_identity_error)
    ImageView imgIdentityError;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.img_phone_error)
    ImageView imgPhoneError;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_switch)
    TextView tvSwitch;

    public static final String ENV_STG1 = "stg1";
    public static final String ENV_STG2 = "stg2";
    public static final String ENV_STG3 = "stg3";
    public static final String ENV_STG4 = "stg4";
    public static final String ENV_STG5 = "stg5";
    public static final String ENV_PRD = "prd";

    private Location location;
    private HljHttpSubscriber lastSubscriber;
    private String ssEnv;
    private String pafEnv;
    private String bigDataEnv;
    private Dialog envDialog;
    private DialogClickListener dialogClickListener;
    private String TAG = "HLJkepler";
    private AMapLocationClient client;
    private JSONObject keplerPageUiInfoJson;
    private JSONObject keplerOptionJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kepler);
        ButterKnife.bind(this);

        Point point = JSONUtil.getDeviceSize(this);
        int height = Math.round(point.x * 7 / 16);
        imgHeader.getLayoutParams().height = height;
        etName.addTextChangedListener(new KeplerTextWatcher(etName));
        etIdentity.addTextChangedListener(new KeplerTextWatcher(etIdentity));
        etPhone.addTextChangedListener(new KeplerTextWatcher(etPhone));
        if (BuildConfig.KEPLER_DEBUG) {
            tvSwitch.setVisibility(View.VISIBLE);
        } else {
            tvSwitch.setVisibility(View.GONE);
        }

        initKeplerSetting();
        location = LocationSession.getInstance()
                .getLocation(this);
        lastApply();
    }

    /**
     * 初始化普安普惠kepler的一些参数
     */
    private void initKeplerSetting() {
        ssEnv = ENV_PRD;
        pafEnv = ENV_PRD;
        bigDataEnv = ENV_PRD;

        keplerPageUiInfoJson = new JSONObject();
        try {
            keplerPageUiInfoJson.put("btnBgColor", "#f83244");
            keplerPageUiInfoJson.put("productName", getString(R.string.app_name));
            keplerPageUiInfoJson.put("naviBgColor", "#0081D4");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION})
    public void location() {
        client = new AMapLocationClient(this.getApplicationContext());
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setGpsFirst(true);
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setInterval(3000);
        option.setNeedAddress(true);
        client.setLocationOption(option);
        client.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                    Location sessionLoc = LocationSession.getInstance()
                            .getLocation(HljKeplerActivity.this);
                    if (sessionLoc == null) {
                        sessionLoc = new Location();
                    }
                    sessionLoc.setDistrict(aMapLocation.getDistrict());
                    sessionLoc.setLatitude(aMapLocation.getLatitude());
                    sessionLoc.setLongitude(aMapLocation.getLongitude());
                    sessionLoc.setAddress(aMapLocation.getAddress());
                    sessionLoc.setCity(aMapLocation.getCity());
                    sessionLoc.setProvince(aMapLocation.getProvince());
                    LocationSession.getInstance()
                            .saveLocation(HljKeplerActivity.this, sessionLoc);
                    HljKeplerActivity.this.location = sessionLoc;
                } else {
                    Toast.makeText(HljKeplerActivity.this,
                            getString(R.string.permission_for_kepler),
                            Toast.LENGTH_SHORT)
                            .show();
                    HljKeplerActivity.this.finish();
                }
                client.stopLocation();
            }
        });
        client.startLocation();
    }

    private void lastApply() {
        lastSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener<Kepler>() {
                    @Override
                    public void onNext(Kepler kepler) {
                        initKepler(kepler);
                    }
                })
                .setDataNullable(true)
                .build();
        Observable<Kepler> lastObservable = KeplerApi.lastApply();
        lastObservable.subscribe(lastSubscriber);
    }

    public void initKepler(Kepler kepler) {
        if (kepler != null && kepler.getId() > 0) {
            //有数据
            etName.setText(kepler.getName());
            etPhone.setText(kepler.getPhone());
            etIdentity.setText(kepler.getCertify());
            if (location != null) {
                tvCity.setText(location.getCity());
                tvAddress.setText(location.getAddress());
            } else {
                tvCity.setText(kepler.getCityName());
                tvAddress.setText(kepler.getStreet());
            }
        } else {
            if (location != null) {
                tvCity.setText(location.getCity());
                tvAddress.setText(location.getAddress());
            }
        }

    }

    public void onSwitch(View view) {
        if (envDialog == null) {
            dialogClickListener = new DialogClickListener();
            Point point = JSONUtil.getDeviceSize(this);
            envDialog = new Dialog(this, R.style.BubbleDialogTheme);
            envDialog.setContentView(R.layout.dialog_kepler_env);
            Window win = envDialog.getWindow();
            ViewGroup.LayoutParams params = win.getAttributes();
            params.width = point.x;
            win.setGravity(Gravity.CENTER);
            envDialog.findViewById(R.id.btn_stg1_ssEnv)
                    .setOnClickListener(dialogClickListener);
            envDialog.findViewById(R.id.btn_stg2_ssEnv)
                    .setOnClickListener(dialogClickListener);
            envDialog.findViewById(R.id.btn_stg3_ssEnv)
                    .setOnClickListener(dialogClickListener);
            envDialog.findViewById(R.id.btn_stg4_ssEnv)
                    .setOnClickListener(dialogClickListener);
            envDialog.findViewById(R.id.btn_stg5_ssEnv)
                    .setOnClickListener(dialogClickListener);
            envDialog.findViewById(R.id.btn_prd_ssEnv)
                    .setOnClickListener(dialogClickListener);
            envDialog.findViewById(R.id.btn_stg1_pafEnv)
                    .setOnClickListener(dialogClickListener);
            envDialog.findViewById(R.id.btn_stg2_pafEnv)
                    .setOnClickListener(dialogClickListener);
            envDialog.findViewById(R.id.btn_stg3_pafEnv)
                    .setOnClickListener(dialogClickListener);
            envDialog.findViewById(R.id.btn_stg4_pafEnv)
                    .setOnClickListener(dialogClickListener);
            envDialog.findViewById(R.id.btn_stg5_pafEnv)
                    .setOnClickListener(dialogClickListener);
            envDialog.findViewById(R.id.btn_prd_pafEnv)
                    .setOnClickListener(dialogClickListener);
        }
        envDialog.show();
    }


    public void onKepler(View view) {
        apply();
    }

    @OnShowRationale({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
            .ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest
            .permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest
            .permission.WRITE_CONTACTS, Manifest.permission.CAMERA})
    void onPermission(PermissionRequest request) {
        //权限提醒
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.permission_for_kepler));

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        HljKeplerActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
    }

    public void apply() {
        String name = etName.getText()
                .toString()
                .trim();
        if (JSONUtil.isEmpty(name)) {
            Toast.makeText(this, R.string.label_name_hint, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        String identity = etIdentity.getText()
                .toString();
        if (!Util.validIdStr(identity)) {
            Toast.makeText(this, R.string.hint_id_number, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        String phone = etPhone.getText()
                .toString();
        if (!CommonUtil.isMobileNO(phone)) {
            Toast.makeText(this, R.string.label_phone_hint, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        KeplerBody body = new KeplerBody();
        body.setBank(getString(R.string.label_kepler));
        body.setName(name);
        body.setCertify(identity);
        body.setPhone(phone);
        if (location != null) {
            body.setLatitude(String.valueOf(location.getLatitude()));
            body.setLongitude(String.valueOf(location.getLongitude()));
            body.setCityName(location.getCity());
            body.setStreet(location.getAddress());
        }
        Observable<HljResultAction> applyObservable = KeplerApi.applyKepler(body);
        applyObservable.subscribe(new Subscriber<HljResultAction>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(HljResultAction hljResultAction) {

            }
        });

        HljKeplerActivityPermissionsDispatcher.loginKeplerWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
            .ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest
            .permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest
            .permission.WRITE_CONTACTS, Manifest.permission.CAMERA})
    public void loginKepler() {
        if (!Util.loginBindChecked(this)) {
            return;
        }

        if (location == null) {
            Toast.makeText(this, getString(R.string.label_permission_quit_open), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (JSONUtil.isEmpty(ssEnv) || JSONUtil.isEmpty(pafEnv)) {
            Toast.makeText(this, "请选择sdk环境", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());

        String identity = etIdentity.getText()
                .toString();
        String city = location.getCity();
        String address = location.getAddress();
        String mobile = etPhone.getText()
                .toString()
                .trim();
        String name = etName.getText()
                .toString()
                .trim();
        long id = Session.getInstance()
                .getCurrentUser(this)
                .getId();
        String province = location.getProvince();

        try {
            keplerOptionJson = new JSONObject();
            // 必填字段
            keplerOptionJson.put("ssEnv", ssEnv);
            keplerOptionJson.put("pafEnv", pafEnv);
            keplerOptionJson.put("bigDataEnv", bigDataEnv);
            keplerOptionJson.put("channelId", Constants.PayAgent.KEPLER);
            keplerOptionJson.put("mobile", mobile);
            keplerOptionJson.put("externalNo", String.valueOf(id));
            keplerOptionJson.put("lat", lat);
            keplerOptionJson.put("lng", lon);
            keplerOptionJson.put("city", city);
            keplerOptionJson.put("province", province);
            keplerOptionJson.put("address", address);
            // 选填字段
            keplerOptionJson.put("name", name);
            keplerOptionJson.put("id", identity);
            keplerOptionJson.put("pageUiInfo", keplerPageUiInfoJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (Constants.DEBUG) {
            String tagStr = "loginChannelId--> " + Constants.PayAgent.KEPLER + " || lat-->  " +
                    lat + " || lon-->" + lon + " || city-->" + city + " || address--> " + address
                    + " " + "|| " + "phone--> " + mobile + " || loginId-->" + identity + " || " +
                    "name-->" + name;
            Log.e(TAG, tagStr);
            Log.e(TAG, "开始启动sdk");
            Log.e(TAG, "ssEnv--> " + ssEnv);
            Log.e(TAG, "pafEnv--> " + pafEnv);
        }
        try {
            KeplerAgent.startILoan(this, keplerOptionJson.toString(), this);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "kepler sdk error");
        }
    }

    @Override
    protected void onPause() {
        if (client != null) {
            client.stopLocation();
        }
        super.onPause();
    }

    @Override
    protected void onFinish() {
        KeplerAgent.destroyILoan(this);
        if (lastSubscriber != null && !lastSubscriber.isUnsubscribed()) {
            lastSubscriber.unsubscribe();
        }
        super.onFinish();
    }

    /**
     * kepler回调
     *
     * @param bundle
     */
    @Override
    public void onCompleted(Bundle bundle) {
        Log.e(TAG, "complete 回调 --> " + bundle.toString());
        Toast.makeText(HljKeplerActivity.this, bundle.getString("msg"), Toast.LENGTH_LONG)
                .show();
    }

    /**
     * kepler回调
     *
     * @param i
     * @param bundle
     */
    @Override
    public void onFailure(int i, Bundle bundle) {
        Log.e(TAG, "failure 回调 -->" + bundle.toString());
        Toast.makeText(HljKeplerActivity.this, bundle.getString("msg"), Toast.LENGTH_LONG)
                .show();
    }

    /**
     * kepler回调
     *
     * @param bundle
     */
    @Override
    public void onExit(Bundle bundle) {

    }

    class DialogClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                // ss env
                case R.id.btn_stg1_ssEnv:
                    ssEnv = ENV_STG1;
                    break;
                case R.id.btn_stg2_ssEnv:
                    ssEnv = ENV_STG2;
                    break;
                case R.id.btn_stg3_ssEnv:
                    ssEnv = ENV_STG3;
                    break;
                case R.id.btn_stg4_ssEnv:
                    ssEnv = ENV_STG4;
                    break;
                case R.id.btn_stg5_ssEnv:
                    ssEnv = ENV_STG5;
                    break;
                case R.id.btn_prd_ssEnv:
                    ssEnv = ENV_PRD;
                    break;
                // paf env
                case R.id.btn_stg1_pafEnv:
                    pafEnv = ENV_STG1;
                    break;
                case R.id.btn_stg2_pafEnv:
                    pafEnv = ENV_STG2;
                    break;
                case R.id.btn_stg3_pafEnv:
                    pafEnv = ENV_STG3;
                    break;
                case R.id.btn_stg4_pafEnv:
                    pafEnv = ENV_STG4;
                    break;
                case R.id.btn_stg5_pafEnv:
                    pafEnv = ENV_STG5;
                    break;
                case R.id.btn_prd_pafEnv:
                    pafEnv = ENV_PRD;
                    break;
                // bigData env
                case R.id.btn_stg1_bigDataEnv:
                    bigDataEnv = ENV_STG1;
                    break;
                case R.id.btn_stg2_bigDataEnv:
                    bigDataEnv = ENV_STG2;
                    break;
                case R.id.btn_prd_bigDataEnv:
                    bigDataEnv = ENV_PRD;
                    break;
                default:
                    break;
            }
            Toast.makeText(v.getContext(),
                    "ssEnv = " + ssEnv + "  ||   " + "pafEnv = " + pafEnv + "  ||   " +
                            "bigDataEnv = " + bigDataEnv,
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    class KeplerTextWatcher implements TextWatcher {
        private EditText etText;

        public KeplerTextWatcher(EditText text) {
            etText = text;
        }

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
            int id = etText.getId();
            String text = s.toString()
                    .trim();
            switch (id) {
                case R.id.et_name:
                    //姓名
                    if (JSONUtil.isEmpty(text)) {
                        imgNameError.setVisibility(View.VISIBLE);
                    } else {
                        imgNameError.setVisibility(View.GONE);
                    }
                    break;
                case R.id.et_identity:
                    //身份证
                    if (!Util.validIdStr(text)) {
                        imgIdentityError.setVisibility(View.VISIBLE);
                    } else {
                        imgIdentityError.setVisibility(View.GONE);
                    }
                    break;
                case R.id.et_phone:
                    //手机
                    if (!Util.isMobileNO(text)) {
                        imgPhoneError.setVisibility(View.VISIBLE);
                    } else {
                        imgPhoneError.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    }

}