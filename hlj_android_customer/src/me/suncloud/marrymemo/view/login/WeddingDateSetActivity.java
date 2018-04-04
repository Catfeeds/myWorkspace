package me.suncloud.marrymemo.view.login;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.models.userprofile.PartnerInvitation;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljpushlibrary.websocket.PushSocket;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kankan.wheel.widget.DTPicker;
import kankan.wheel.widget.DatePickerView;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.bindpartner.PartnerApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.CityUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.CityListActivity;
import me.suncloud.marrymemo.view.MainActivity;
import me.suncloud.marrymemo.widget.CheckableLinearLayout2;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * 设置婚期activity  伴侣的逻辑需要调用者自己实现
 * Created by jinxin on 2016/4/7.
 */
@Route(path = RouterPath.IntentPath.Customer.WEDDING_DATE)
@RuntimePermissions
public class WeddingDateSetActivity extends HljBaseNoBarActivity implements
        CheckableLinearLayout2.OnCheckedChangeListener, DTPicker.OnPickerDateListener {

    @BindView(R.id.tv_top_hint)
    TextView tvTopHint;
    @BindView(R.id.tv_chose_gender)
    TextView tvChoseGender;
    @BindView(R.id.img_groom)
    ImageView imgGroom;
    @BindView(R.id.tv_groom)
    TextView tvGroom;
    @BindView(R.id.check_groom)
    CheckableLinearLayout2 checkGroom;
    @BindView(R.id.img_bride)
    ImageView imgBride;
    @BindView(R.id.tv_bride)
    TextView tvBride;
    @BindView(R.id.check_bride)
    CheckableLinearLayout2 checkBride;
    @BindView(R.id.layout_set)
    View layoutSet;
    @BindView(R.id.layout_date)
    LinearLayout layoutDate;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.onOpen)
    Button onOpen;
    @BindView(R.id.picker)
    DatePickerView picker;
    @BindView(R.id.layout_date_picker)
    LinearLayout layoutDatePicker;
    @BindView(R.id.view_gap1)
    View viewGap1;
    @BindView(R.id.view_gap2)
    View viewGap2;

    private SimpleDateFormat dateFormat;
    private int brideSelectedColor;
    private int groomSelectedColor;
    private Calendar tempCalendar;
    private int currentMonth;
    private boolean hasAnimated;
    private int animTime = 500;
    private boolean isUndetermined;//婚期待定
    private int scaleTranHeight;
    private Subscription partnerInvitationSubscription;
    private boolean showPartnerInvitation;
    private int type;
    private CityUtil cityUtil;
    private boolean isReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_date);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        type = getIntent().getIntExtra("type", 0);
        isReset = getIntent().getBooleanExtra(RouterPath.IntentPath.Customer.Login.ARG_IS_RESET,false);
        initConstant();
        initWidget();
        if (type == Constants.Login.REGISTER) {
            City city = Session.getInstance()
                    .getMyCity(this);
            if (city == null || city.getId() == 0) {
                cityUtil = new CityUtil(this, new CityUtil.OnGetCityResultListener() {
                    @Override
                    public void onResult(City city) {
                        try {
                            Session.getInstance()
                                    .setMyCity(WeddingDateSetActivity
                                            .this, city);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                //提前定位
                WeddingDateSetActivityPermissionsDispatcher.requestPermissionWithCheck(this);
            }
            initPartnerInvitationLoad();
        }
    }

    private void initPartnerInvitationLoad() {
        partnerInvitationSubscription = PartnerApi.getPartnerInvitationStatusObb()
                .map(new Func1<PartnerInvitation, Boolean>() {
                    @Override
                    public Boolean call(PartnerInvitation partnerInvitation) {
                        return partnerInvitation == null;
                    }
                })
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        showPartnerInvitation = true;
                    }
                });
    }

    private void initConstant() {
        currentMonth = Calendar.getInstance()
                .get(Calendar.MONTH);
        brideSelectedColor = Color.parseColor("#ff86b2");
        groomSelectedColor = Color.parseColor("#49b3ee");
        dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_SHORT, Locale.getDefault());
    }

    private void initWidget() {
        checkGroom.setOnCheckedChangeListener(this);
        checkBride.setOnCheckedChangeListener(this);
        picker.setType(0);
        picker.hideLabels();
        picker.setMonthUnDefined(true);
        picker.updateDays();
        picker.setCurrentMonth();
        picker.setOnPickerDateListener(this);

        imgGroom.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int height = imgGroom.getMeasuredHeight();
                        scaleTranHeight = Math.round(height * 0.4f);
                    }
                });
    }

    private boolean enableOpen() {
        return (checkBride.isChecked() || checkGroom.isChecked()) && (tempCalendar != null ||
                isUndetermined);
    }

    @Override
    public void onBackPressed() {
    }

    /**
     * 逻辑 同onSkip
     *
     * @param view
     */
    @OnClick(R.id.onOpen)
    void onOpen(View view) {
        Map<String, Object> params = new HashMap<>();
        if (checkBride.isChecked() && !checkGroom.isChecked()) {
            //新娘
            params.put("gender", 2);
        }
        if (checkGroom.isChecked() && !checkBride.isChecked()) {
            //新郎
            params.put("gender", 1);
        }
        //is_pending 0未设置婚期 1婚期待定 2设置了婚期
        if (isUndetermined) {
            //婚期待定
            params.put("is_pending", 1);
        } else {
            String date = dateFormat.format(tempCalendar.getTime());
            params.put("weddingday", date);
        }
        //下一步
        upLoadInfo(params);
    }

    /**
     * 设置婚期和性别
     */
    private void upLoadInfo(Map<String, Object> params) {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        new NewHttpPostTask(this, new OnHttpRequestListener() {

            @Override
            public void onRequestFailed(Object obj) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(WeddingDateSetActivity.this,
                        getString(R.string.msg_fail_to_complete_profile),
                        Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onRequestCompleted(Object obj) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                JSONObject resultObj = (JSONObject) obj;
                if (resultObj != null && resultObj.optJSONObject("status") != null && resultObj
                        .optJSONObject(
                        "status")
                        .optInt("RetCode", -1) == 0) {
                    Toast.makeText(WeddingDateSetActivity.this,
                            getString(R.string.msg_success_to_complete_profile),
                            Toast.LENGTH_SHORT)
                            .show();
                    JSONObject dataObj = resultObj.optJSONObject("data");
                    Session.getInstance()
                            .setCurrentUser(WeddingDateSetActivity.this, dataObj);
                    if(isReset){
                        Intent intent = new Intent(WeddingDateSetActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.activity_anim_default, R.anim.activity_anim_default);
                    }else if (type == Constants.Login.REGISTER) {
                        gotoMainActivity();
                    }else {
                        PushSocket.INSTANCE.onStart(WeddingDateSetActivity.this);
                    }
                    finish();

                } else {
                    Toast.makeText(WeddingDateSetActivity.this,
                            getString(R.string.msg_fail_to_complete_profile),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.COMPLETE_USER_PROFILE), params);
    }

    private void gotoMainActivity() {
        Intent intent;
        if (Session.getInstance()
                .hasSetMyCity(this)) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, CityListActivity.class);
            intent.putExtra(CityListActivity.ARG_IS_INITIAL_PAGE, true);
        }
        intent.putExtra(MainActivity.ARG_SHOW_PARTNER_INVITATION, showPartnerInvitation);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void onCheckedChange(View view, boolean checked) {
        int id = view.getId();
        switch (id) {
            case R.id.check_bride:
                checkBride.setChecked(checked);
                if (checked) {
                    checkGroom.setChecked(false);
                    imgBride.setImageResource(R.mipmap.image_bride_selected);
                    tvBride.setTextColor(brideSelectedColor);
                } else {
                    imgBride.setImageResource(R.mipmap.image_bride_normal);
                    tvBride.setTextColor(getResources().getColor(R.color.colorBlack3));
                }
                break;
            case R.id.check_groom:
                checkGroom.setChecked(checked);
                if (checked) {
                    checkBride.setChecked(false);
                    imgGroom.setImageResource(R.mipmap.image_groom_selected);
                    tvGroom.setTextColor(groomSelectedColor);
                } else {
                    imgGroom.setImageResource(R.mipmap.image_groom_normal);
                    tvGroom.setTextColor(getResources().getColor(R.color.colorBlack3));
                }
                break;
            default:
                break;
        }
        if (!hasAnimated) {
            hasAnimated = true;
            doAnimation();
        }
        onOpen.setEnabled(enableOpen());
    }

    private void doAnimation() {
        ValueAnimator anim = ValueAnimator.ofInt(animTime);
        anim.setDuration(animTime);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int time = (int) animation.getAnimatedValue();
                scale(time);
                trans(time);
            }
        });

        final int totalDis = viewGap2.getHeight();
        ValueAnimator scrollAnim = ObjectAnimator.ofFloat(-totalDis, 0);
        scrollAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                layoutDatePicker.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        scrollAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float dis = (float) animation.getAnimatedValue();
                layoutDatePicker.setTranslationY(-dis);
            }
        });
        scrollAnim.setDuration(animTime);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tvTopHint, "scaleX", 1.0f, 0.0f);
        scaleX.setDuration(animTime);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tvTopHint, "scaleY", 1.0f, 0.0f);
        scaleY.setDuration(animTime);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(tvTopHint, "alpha", 1.0f, 0.0f);
        alpha.setDuration(animTime);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(anim, scrollAnim, scaleX, scaleY, alpha);
        animatorSet.start();
    }

    private void scale(int time) {
        float scaleRate = (1.0f - 0.6f) / animTime;
        float scale = 1.0f - scaleRate * time;
        imgBride.setPivotX(imgBride.getWidth() / 2);
        imgBride.setPivotY(0);
        imgBride.setScaleX(scale);
        imgBride.setScaleY(scale);
        imgGroom.setPivotX(imgGroom.getWidth() / 2);
        imgGroom.setPivotY(0);
        imgGroom.setScaleX(scale);
        imgGroom.setScaleY(scale);
    }

    private void trans(int time) {
        float scaleTranHeightRate = scaleTranHeight * 1.0f / animTime;
        float tranHeight = -scaleTranHeightRate * time;
        tvGroom.setTranslationY(tranHeight);
        tvBride.setTranslationY(tranHeight);


        float viewGap3DisRate = tvTopHint.getHeight() * 1.0f / animTime;
        float tranGap3 = -viewGap3DisRate * time;
        layoutSet.setTranslationY(tranGap3);
        tvTopHint.setTranslationY(tranGap3 / 2);
    }

    @Override
    public void onPickerDate(int year, int month, int day) {
        if (currentMonth + 1 != month) {
            if (month < currentMonth) {
                month--;
            } else {
                month -= 2;
            }
            if (tempCalendar == null) {
                tempCalendar = new GregorianCalendar(year, month, day, 0, 0);
            } else {
                tempCalendar.set(year, month, day, 0, 0);
            }
            isUndetermined = false;
        } else {
            //婚期待定
            isUndetermined = true;
        }
        onOpen.setEnabled(enableOpen());
    }


    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
            .ACCESS_FINE_LOCATION})
    @OnPermissionDenied({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
            .ACCESS_FINE_LOCATION})
    void requestPermission() {
        if (cityUtil != null) {
            cityUtil.location();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        WeddingDateSetActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(partnerInvitationSubscription);
        super.onFinish();
    }

    @Override
    protected void onResume() {
        if (cityUtil != null) {
            cityUtil.startLocation();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (cityUtil != null) {
            cityUtil.stopLocation();
        }
        super.onPause();
    }
}
