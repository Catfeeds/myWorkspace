package me.suncloud.marrymemo.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcarlibrary.views.activities.WeddingCarSubPageActivity;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljtrackerlibrary.HljTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import kankan.wheel.widget.CitiesPickerView;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.merchant.FindMerchantHomeFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.MenuItem;
import me.suncloud.marrymemo.task.HttpGetTask;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.kefu.AdvHelperActivity;

/**
 * Created by Suncloud on 2015/4/22.
 */
public class LightUpActivity extends Activity {

    private int type;
    private TextView hintView;
    private TextView titleView;
    private TextView lightUpBtn;
    private TextView selectedBtn;
    private Dialog cityDialog;
    private View infoView;
    private City city;
    private LinkedHashMap<Integer, ArrayList<City>> cityMap;
    private LinkedHashMap<String, ArrayList<String>> cityStrMap;
    private boolean isLightUp;
    private DataConfig dataConfig;
    private String url;
    private int completeId;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        type = getIntent().getIntExtra("type", 0);
        city = (City) getIntent().getSerializableExtra("city");
        dataConfig = Session.getInstance()
                .getDataConfig(this);
        Point point = JSONUtil.getDeviceSize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_up);
        infoView = findViewById(R.id.info_layout);
        infoView.getLayoutParams().width = Math.round(point.x * 27 / 32);
        findViewById(R.id.background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titleView = (TextView) findViewById(R.id.title);
        ImageView iconView = (ImageView) findViewById(R.id.icon);
        hintView = (TextView) findViewById(R.id.hint);
        selectedBtn = (TextView) findViewById(R.id.selected_btn);
        lightUpBtn = (TextView) findViewById(R.id.light_up);
        onCityChange(city);
        switch (type) {
            case 1:
                url = Constants.getAbsUrl(Constants.HttpPath.LIGHT_UP_CAR);
                completeId = R.string.msg_light_up_car_complete;
                iconView.setImageResource(R.drawable.icon_new_wedding_car);
                titleView.setText(getString(R.string.title_activity_light_car));
                break;
            case 3:
                url = Constants.getAbsUrl(Constants.HttpPath.LIGHT_UP);
                completeId = R.string.msg_light_up_adv_complete;
                iconView.setImageResource(R.drawable.icon_new_adv);
                titleView.setText(getString(R.string.title_activity_light_adv));
                break;
            default:
                url = Constants.getAbsUrl(Constants.HttpPath.LIGHT_UP_HOTEL);
                completeId = R.string.msg_light_up_hotel_complete;
                iconView.setImageResource(R.drawable.icon_new_hotel);
                titleView.setText(getString(R.string.title_activity_light_hotel));
                break;
        }
        getCitiesFromFile();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    public void onLightUp(View view) {
        if (isLightUp) {
            switch (type) {
                case 1:
                    Intent intent = new Intent(this, WeddingCarSubPageActivity.class);
                    intent.putExtra(WeddingCarSubPageActivity.ARG_CITY_ID, city.getId());
                    intent.putExtra(WeddingCarSubPageActivity.ARG_CITY_NAME, city.getName());
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                    break;
                case 3:
                    intent = new Intent(this, AdvHelperActivity.class);

                    boolean isHotel = getIntent().getBooleanExtra(AdvHelperActivity.ARG_IS_HOTEL,
                            false);
                    boolean isCalendar = getIntent().getBooleanExtra(AdvHelperActivity
                                    .ARG_IS_CALENDAR,
                            false);
                    String from = getIntent().getStringExtra(AdvHelperActivity.ARG_ADV_FROM);
                    String firstMsg = getIntent().getStringExtra(AdvHelperActivity.ARG_FIRST_MSG);
                    intent.putExtra(AdvHelperActivity.ARG_IS_HOTEL, isHotel);
                    intent.putExtra(AdvHelperActivity.ARG_IS_CALENDAR, isCalendar);
                    intent.putExtra(AdvHelperActivity.ARG_ADV_FROM, from);
                    intent.putExtra(AdvHelperActivity.ARG_FIRST_MSG, firstMsg);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                    break;
                default:
                    ARouter.getInstance()
                            .build(RouterPath.IntentPath.Customer.MerchantListActivityPath
                                    .MERCHANT_LIST_ACTIVITY)
                            .withLong(RouterPath.IntentPath.Customer.MerchantListActivityPath
                                            .ARG_PROPERTY_ID,
                                    RouterPath.IntentPath.Customer.MerchantListActivityPath
                                            .Property.HOTEL)
                            .withSerializable(FindMerchantHomeFragment.ARG_CITY, city)
                            .navigation(this);
                    finish();
                    break;
            }
        } else {
            OnHttpRequestListener httpRequestListener = new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    if (isFinishing()) {
                        return;
                    }
                    JSONObject jsonObject = (JSONObject) obj;
                    if (jsonObject != null) {
                        if (toast == null) {
                            toast = Toast.makeText(LightUpActivity.this,
                                    getString(completeId, city.getName()),
                                    Toast.LENGTH_SHORT);
                        } else {
                            toast.setText(getString(completeId, city.getName()));
                        }
                        onBackPressed();
                        toast.show();
                    } else {
                        if (toast == null) {
                            toast = Toast.makeText(LightUpActivity.this,
                                    R.string.msg_light_up_error,
                                    Toast.LENGTH_SHORT);
                        } else {
                            toast.setText(R.string.msg_light_up_error);
                        }
                        toast.show();
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {
                    if (isFinishing()) {
                        return;
                    }
                    if (toast == null) {
                        toast = Toast.makeText(LightUpActivity.this,
                                R.string.msg_light_up_error,
                                Toast.LENGTH_SHORT);
                    } else {
                        toast.setText(R.string.msg_light_up_error);
                    }
                    toast.show();
                }
            };
            if (type == 3) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("function_id", 3);
                    jsonObject.put("city", city.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new NewHttpPostTask(this, httpRequestListener).execute(url, jsonObject.toString());
            } else {
                new HttpGetTask(httpRequestListener).execute(String.format(url, city.getId()));
            }
        }
    }

    public void selectCity(View view) {
        if (cityDialog != null && cityDialog.isShowing()) {
            return;
        }
        switch (type) {
            case 1:
                new HljTracker.Builder(this).screen("highlight_city_car_page")
                        .action("change_city_hit")
                        .build()
                        .add();
                break;
            case 2:
                new HljTracker.Builder(this).screen("highlight_city_hotel_page")
                        .action("change_city_hit")
                        .build()
                        .add();
                break;
            default:
                break;
        }
        if (cityDialog == null) {
            cityDialog = new Dialog(this, R.style.BubbleDialogTheme);
            View v = getLayoutInflater().inflate(R.layout.dialog_city_picker, null);
            final CitiesPickerView cityPickerView = (CitiesPickerView) v.findViewById(R.id.picker);
            cityPickerView.setCityMap(cityStrMap);

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cityPickerView
                    .getLayoutParams();
            params.height = (int) (getResources().getDisplayMetrics().density * (24 * 8));
            cityDialog.setContentView(v);
            Window win = cityDialog.getWindow();
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
                    cityDialog.cancel();
                }
            });
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cityDialog.cancel();
                    int[] selectedItems = cityPickerView.getSelectedItemIndexs();
                    int i = Math.min(selectedItems[0], cityMap.size() - 1);
                    ArrayList<City> cities = cityMap.get(i);
                    int j = Math.min(selectedItems[1], cities.size() - 1);
                    city = cities.get(j);
                    onCityChange(city);
                }
            });
            cityDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                            infoView.getLayoutParams();
                    layoutParams.bottomMargin = 0;
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                    layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    infoView.setLayoutParams(layoutParams);
                }
            });
            cityDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                            infoView.getLayoutParams();
                    layoutParams.bottomMargin = Math.round(getResources().getDisplayMetrics()
                            .density * 222);
                    layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, 0);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    infoView.setLayoutParams(layoutParams);
                }
            });
        }

        cityDialog.show();
    }

    private void getCitiesFromFile() {
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
            JSONArray jsonArray = jsonObject.optJSONArray("provinces");
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject provinceObj = jsonArray.optJSONObject(i);
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
                    cityStrMap.put(provinceMenuItem.getName(), cityStrList);
                }
            }
        }
    }

    private void onCityChange(City city) {
        if (city == null || city.getId() == 0) {
            lightUpBtn.setVisibility(View.GONE);
            findViewById(R.id.line).setVisibility(View.GONE);
            findViewById(R.id.selected_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.selected_btn1).setVisibility(View.GONE);
            selectedBtn.setText(R.string.btn_select_city);
            switch (type) {
                case 1:
                    hintView.setText(R.string.hint_light_up1);
                    break;
                case 3:
                    hintView.setText(R.string.hint_light_up3);
                    break;
                default:
                    hintView.setText(R.string.hint_light_up2);
                    break;
            }
        } else {
            lightUpBtn.setVisibility(View.VISIBLE);
            findViewById(R.id.selected_layout).setVisibility(View.GONE);
            findViewById(R.id.selected_btn1).setVisibility(View.VISIBLE);
            findViewById(R.id.line).setVisibility(View.VISIBLE);
            int stringId = 0;
            switch (type) {
                case 1:
                    if (dataConfig.getCityIds() != null && !dataConfig.getCityIds()
                            .isEmpty()) {
                        isLightUp = dataConfig.getCityIds()
                                .contains(city.getId());
                        stringId = R.string.hint_car_is_light_up;
                    }
                    if (isLightUp) {
                        titleView.setText(getString(R.string.title_activity_is_light_car));
                    } else {
                        titleView.setText(getString(R.string.title_activity_light_car));
                    }
                    break;
                case 3:
                    if (dataConfig.getAdvCids() != null && !dataConfig.getAdvCids()
                            .isEmpty()) {
                        isLightUp = dataConfig.getAdvCids()
                                .contains(city.getId());
                        stringId = R.string.hint_adv_is_light_up;
                    }
                    if (isLightUp) {
                        titleView.setText(getString(R.string.title_activity_is_light_adv));
                    } else {
                        titleView.setText(getString(R.string.title_activity_light_adv));
                    }
                    break;
                default:
                    if (dataConfig.getHotelCityIds() != null && !dataConfig.getHotelCityIds()
                            .isEmpty()) {
                        isLightUp = dataConfig.getHotelCityIds()
                                .contains(city.getId());
                        stringId = R.string.hint_hotel_is_light_up;
                    }
                    if (isLightUp) {
                        titleView.setText(getString(R.string.title_activity_is_light_hotel));
                    } else {
                        titleView.setText(getString(R.string.title_activity_light_hotel));
                    }
                    break;
            }
            selectedBtn.setText(R.string.btn_change_city);
            if (isLightUp) {
                lightUpBtn.setText(R.string.btn_is_light_up);
                hintView.setText(getString(stringId, city.getName()));
            } else {
                lightUpBtn.setText(getString(R.string.btn_light_up_city, city.getName()));
                hintView.setText(R.string.hint_light_up);
            }
        }
    }
}
