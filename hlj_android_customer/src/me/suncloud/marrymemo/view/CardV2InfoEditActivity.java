package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.hunliji.hljcommonlibrary.utils.TextCountWatcher;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljmaplibrary.HljMap;
import com.hunliji.hljmaplibrary.views.activities.LocationMapActivity;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import kankan.wheel.widget.DateTimePickerView;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.V2.CardPageV2;
import me.suncloud.marrymemo.model.V2.CardV2;
import me.suncloud.marrymemo.model.V2.ThemeV2;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;

/**
 * Created by Suncloud on 2016/4/26.
 */
public class CardV2InfoEditActivity extends HljBaseActivity implements View.OnClickListener, View
        .OnFocusChangeListener, DateTimePickerView.OnPickerDateTimeListener {

    @BindView(R.id.groom_name)
    EditText groomName;
    @BindView(R.id.bride_name)
    EditText brideName;
    @BindView(R.id.wedding_time)
    EditText weddingTime;
    @BindView(R.id.wedding_address)
    EditText weddingAddress;
    @BindView(R.id.et_title)
    EditText etTitle;

    private double latitude;
    private double longitude;
    private CardV2 card;
    private ThemeV2 theme;
    private Calendar calendar;
    private Dialog dialog;
    private Calendar tempCalendar;
    private boolean isCreate;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        card = (CardV2) getIntent().getSerializableExtra("card");
        if (card == null) {
            card = (CardV2) getIntent().getSerializableExtra("lastCard");
            isCreate = true;
        }
        theme = (ThemeV2) getIntent().getSerializableExtra("theme");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_info_v2_edit);
        setOkText(R.string.label_save);
        calendar = Calendar.getInstance();
        ButterKnife.bind(this);
        etTitle.addTextChangedListener(new TextCountWatcher(etTitle, 10));
        groomName.addTextChangedListener(new TextCountWatcher(groomName, 6));
        brideName.addTextChangedListener(new TextCountWatcher(brideName, 6));
        weddingAddress.addTextChangedListener(new TextCountWatcher(weddingAddress, 40));
        weddingTime.setOnFocusChangeListener(this);
        weddingTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showDatetimePicker(v);
                }
                return true;
            }
        });

        if (card != null) {
            if (card.getTime() != null) {
                calendar.setTime(card.getTime());
                weddingTime.setText(Util.getCardTimeString(this, card.getTime()));
            }
            etTitle.setText(card.getTitle());
            groomName.setText(card.getGroomName());
            brideName.setText(card.getBrideName());
            weddingAddress.setText(card.getPlace());
            latitude = card.getLatitude();
            longitude = card.getLongitude();
            if (latitude != 0 && longitude != 0 && card.getMapType() != CardV2.COORD_TYPE_GAO_DE) {
                LatLng latLng = HljMap.convertBDPointToAMap(this, latitude, longitude);
                latitude = latLng.latitude;
                longitude = latLng.longitude;
            }
            address = card.getPlace();
        }
    }


    public void showMap(View v) {
        if (weddingAddress.length() == 0) {
            Toast.makeText(this, R.string.msg_address_required, Toast.LENGTH_SHORT)
                    .show();
        } else {
            Intent intent = new Intent(this, LocationMapActivity.class);
            address = weddingAddress.getText()
                    .toString();
            intent.putExtra(LocationMapActivity.ARG_ADDRESS, address);
            if (latitude > 0 && longitude > 0) {
                intent.putExtra(LocationMapActivity.ARG_LATITUDE, latitude);
                intent.putExtra(LocationMapActivity.ARG_LONGITUDE, longitude);
            }
            startActivityForResult(intent, Constants.RequestCode.LOCATION);
        }
    }

    public void showDatetimePicker(View view) {
        if (view.getId() != R.id.wedding_time) {
            return;
        }
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (dialog == null) {
            dialog = new Dialog(this, R.style.BubbleDialogTheme);
            dialog.setContentView(R.layout.dialog_date_time_picker___cm);
            dialog.findViewById(R.id.btn_close)
                    .setOnClickListener(this);
            dialog.findViewById(R.id.btn_confirm)
                    .setOnClickListener(this);
            DateTimePickerView picker = (DateTimePickerView) dialog.findViewById(R.id.picker);
            picker.setYearLimit(2000, 49);
            picker.setCurrentCalender(calendar);
            picker.setOnPickerDateTimeListener(this);
            picker.getLayoutParams().height = Math.round(getResources().getDisplayMetrics()
                    .density * (24 * 8));
            Window win = dialog.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = point.x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        dialog.show();
    }

    @Override
    public void onOkButtonClick() {
        if (!cardInfoChecked()) {
            return;
        }
        if (card == null || isCreate) {
            card = new CardV2(new JSONObject());
            card.setTheme(theme);
        }
        card.setTitle(etTitle.getText()
                .toString());
        card.setGroomName(groomName.getText()
                .toString());
        card.setBrideName(brideName.getText()
                .toString());
        card.setPlace(weddingAddress.getText()
                .toString());
        card.setLongitude(longitude);
        card.setLatitude(latitude);
        card.setMapType(CardV2.COORD_TYPE_GAO_DE);
        if (calendar != null) {
            card.setTime(calendar.getTime());
        }
        card.editDefaultPage(this, card);
        if (!isCreate) {
            Intent intent = getIntent();
            intent.putExtra("card", card);
            setResult(RESULT_OK, intent);
            onBackPressed();
        } else {
            CardPageV2 frontPage = card.getFrontPage();
            if (frontPage != null && !frontPage.getImageHoles()
                    .isEmpty()) {
                Intent intent = new Intent(this, PageV2ChoosePhotoActivity.class);
                intent.putExtra("card", card);
                intent.putExtra("limit",
                        frontPage.getImageHoles()
                                .size());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            } else if (frontPage != null) {
                Intent intent = new Intent(this, PageV2EditActivity.class);
                intent.putExtra("cardPage", card.getFrontPage());
                intent.putExtra("card", card);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
            finish();
        }
        super.onOkButtonClick();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                dialog.dismiss();
                break;
            case R.id.btn_confirm:
                dialog.dismiss();
                if (tempCalendar != null) {
                    calendar.setTime(tempCalendar.getTime());
                }
                weddingTime.setText(Util.getCardTimeString(this, calendar.getTime()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            showDatetimePicker(v);
        }
    }

    @Override
    public void onPickerDateAndTime(int year, int month, int day, int hour, int minute) {
        if (tempCalendar == null) {
            tempCalendar = new GregorianCalendar(year, month - 1, day, hour, minute);
        } else {
            tempCalendar.set(year, month - 1, day, hour, minute);
        }
    }

    private boolean cardInfoChecked() {
        if (groomName.getText()
                .length() == 0) {
            Toast.makeText(this, R.string.msg_name_required, Toast.LENGTH_SHORT)
                    .show();
            groomName.requestFocus();
            return false;
        }
        if (brideName.getText()
                .length() == 0) {
            Toast.makeText(this, R.string.msg_name_required, Toast.LENGTH_SHORT)
                    .show();
            brideName.requestFocus();
            return false;
        }
        if (weddingTime.getText()
                .length() == 0) {
            Toast.makeText(this, R.string.msg_time_required, Toast.LENGTH_SHORT)
                    .show();
            weddingTime.requestFocus();
            return false;
        }
        if (weddingAddress.getText()
                .length() == 0) {
            Toast.makeText(this, R.string.msg_address_required, Toast.LENGTH_SHORT)
                    .show();
            weddingAddress.requestFocus();
            return false;
        }

        if (latitude == 0 || longitude == 0) {
            Toast.makeText(this, R.string.msg_geo_required, Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case Constants.RequestCode.LOCATION:
                    latitude = data.getDoubleExtra(LocationMapActivity.ARG_LATITUDE, 0);
                    longitude = data.getDoubleExtra(LocationMapActivity.ARG_LONGITUDE, 0);
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
