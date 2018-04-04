package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcarlibrary.models.CarShoppingCartItem;
import com.hunliji.hljcarlibrary.util.WeddingCarSession;
import com.hunliji.hljcarlibrary.views.activities.WeddingCarProductDetailActivity;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.hunliji.hunlijicalendar.ResizeAnimation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.RedPacket;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;

public class NewCarOrderConfirmActivity extends HljBaseActivity {


    @BindView(R.id.tv_total_price2)
    TextView tvTotalPrice2;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.tv_car_use_time)
    TextView tvCarUseTime;
    @BindView(R.id.tv_car_use_addr)
    TextView tvCarUseAddr;
    @BindView(R.id.tv_contact_name)
    TextView tvContactName;
    @BindView(R.id.tv_contact_phone)
    TextView tvContactPhone;
    @BindView(R.id.address_layout)
    RelativeLayout addressLayout;
    @BindView(R.id.items_layout)
    LinearLayout itemsLayout;
    @BindView(R.id.tv_total_car_price)
    TextView tvTotalCarPrice;
    @BindView(R.id.tv_discount_price)
    TextView tvDiscountPrice;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.agreement)
    CheckBox insuranceAgreement;
    @BindView(R.id.et_groom_name)
    EditText etGroomName;
    @BindView(R.id.et_groom_id)
    EditText etGroomId;
    @BindView(R.id.et_bride_name)
    EditText etBrideName;
    @BindView(R.id.et_bride_id)
    EditText etBrideId;
    @BindView(R.id.tv_insurance_agreement)
    TextView tvInsuranceAgreement;
    @BindView(R.id.car_insurance_items)
    LinearLayout carInsuranceItems;
    @BindView(R.id.insurance_layout)
    LinearLayout insuranceLayout;
    @BindView(R.id.tv_available_count)
    TextView tvAvailableCount;
    @BindView(R.id.red_packet_layout)
    LinearLayout redPacketLayout;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.user_info_layout)
    LinearLayout userInfoLayout;
    @BindView(R.id.layout_discount)
    LinearLayout layoutDiscount;

    private ArrayList<RedPacket> redPackets;
    private ArrayList<CarShoppingCartItem> cartItems;
    private int insuranceLayoutHeight;
    private double totalPrice;
    private double discountMoney;
    private Date useCarDate;
    private String useCarAddr;
    private String contactName;
    private String contactPhone;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat simpleDateFormat2;
    private Dialog successDialog;
    private static final String PINGAN_INSURANCE_ABOUT_URL = "https://home.pingan.com" + "" + ""
            + ".cn/productCommonClause.screen?productId=PR000682";
    private Dialog progressDialog;
    private Dialog confirmDialog;

    private Handler handler = new Handler();
    private Runnable submitRunnable = new Runnable() {
        @Override
        public void run() {
            if (confirmDialog != null && confirmDialog.isShowing()) {
                confirmDialog.dismiss();
                submitOrder();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_car_order_confirm);
        ButterKnife.bind(this);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        insuranceLayoutHeight = (int) Math.round(dm.density * 218.5);
        dateFormat = new SimpleDateFormat(getString(R.string.format_date_type10),
                Locale.getDefault());
        simpleDateFormat2 = new SimpleDateFormat(getString(R.string.format_date),
                Locale.getDefault());

        cartItems = getIntent().getParcelableArrayListExtra("items");
        setCarItems();
        setPrices();
        setLastContactInfo();
        setInsuranceLayout();

        progressBar.setVisibility(View.VISIBLE);
        redPackets = new ArrayList<>();
        getExtraInfos();
    }

    private void setLastContactInfo() {
        SharedPreferences sp = getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
        User user = Session.getInstance()
                .getCurrentUser(this);
        String lastTime = "";
        String lastAddr = "";
        String lastName = "";
        String lastPhone = "";
        if (user != null && user.getId() != 0) {
            String userId = String.valueOf(user.getId());

            lastTime = sp.getString(Constants.PREF_LAST_CAR_USE_TIME + userId, "");
            lastName = sp.getString(Constants.PREF_LAST_SERVE_CUSTORMER + userId, "");
            lastPhone = sp.getString(Constants.PREF_LAST_SERVE_PHONE + userId, "");
            lastAddr = sp.getString(Constants.PREF_LAST_SERVE_ADDR + userId, "");

            if (JSONUtil.isEmpty(lastPhone)) {
                lastPhone = user.getPhone();
            }

            if (JSONUtil.isEmpty(lastName)) {
                lastName = user.getRealName();
            }

        }
        if (!JSONUtil.isEmpty(lastTime) && !JSONUtil.isEmpty(lastName) && !JSONUtil.isEmpty(
                lastPhone) && !JSONUtil.isEmpty(lastAddr)) {
            tvHint.setVisibility(View.GONE);
            userInfoLayout.setVisibility(View.VISIBLE);
        }

        if (JSONUtil.isEmpty(lastTime)) {
            tvCarUseTime.setText(R.string.label_empty_car_use_time);
        } else {
            useCarDate = JSONUtil.getDateFromString(lastTime);
            tvCarUseTime.setText(getString(R.string.label_car_use_time,
                    dateFormat.format(useCarDate)));
        }

        if (JSONUtil.isEmpty(lastName)) {
            tvContactName.setText(R.string.label_empty_contact_name);
        } else {
            contactName = lastName;
            tvContactName.setText(contactName);
        }

        if (JSONUtil.isEmpty(lastPhone)) {
            tvContactPhone.setText(R.string.label_empty_contact_phone);
        } else {
            contactPhone = lastPhone;
            tvContactPhone.setText(contactPhone);
        }

        if (JSONUtil.isEmpty(lastAddr)) {
            tvCarUseAddr.setText(R.string.label_empty_car_use_addr);
        } else {
            useCarAddr = lastAddr;
            tvCarUseAddr.setText(getString(R.string.label_car_use_addr, useCarAddr));
        }
    }

    @OnClick(R.id.address_layout)
    void onSetContactInfo() {
        Intent intent = new Intent(this, ContactInfoActivity.class);
        intent.putExtra("name", contactName);
        if (useCarDate != null) {
            intent.putExtra("time", dateFormat.format(useCarDate));
        }
        intent.putExtra("phone", contactPhone);
        intent.putExtra("address", useCarAddr);
        startActivityForResult(intent, Constants.RequestCode.EDIT_CAR_USE_INFO);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.btn_submit)
    void onSubmit() {
        // 验证数据有效性
        if (JSONUtil.isEmpty(useCarAddr)) {
            Toast.makeText(this, R.string.msg_empty_car_addr, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (useCarDate == null) {
            Toast.makeText(this, R.string.msg_empty_car_date, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (JSONUtil.isEmpty(contactName)) {
            Toast.makeText(this, R.string.msg_empty_contact_name, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (JSONUtil.isEmpty(contactPhone)) {
            Toast.makeText(this, R.string.msg_empty_contact_phone, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (insuranceAgreement.isChecked() && (etGroomName.length() == 0 || etGroomId.length() ==
                0 || etBrideName.length() == 0 || etBrideId.length() == 0)) {
            Toast.makeText(this, R.string.msg_uncomplete_insrance_info, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (confirmDialog != null && confirmDialog.isShowing()) {
            return;
        }
        if (confirmDialog == null) {
            confirmDialog = new Dialog(this, R.style.BubbleDialogTheme);
            confirmDialog.setContentView(R.layout.dialog_order_confirm);
            confirmDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    handler.postDelayed(submitRunnable, 5000);
                }
            });
            confirmDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    handler.removeCallbacks(submitRunnable);
                }
            });

            confirmDialog.findViewById(R.id.btn_confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmDialog.dismiss();
                            submitOrder();
                        }
                    });
            confirmDialog.findViewById(R.id.btn_cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmDialog.dismiss();
                            onSetContactInfo();
                        }
                    });
            confirmDialog.findViewById(R.id.car_address_layout)
                    .setVisibility(View.VISIBLE);
        }
        ((TextView) confirmDialog.findViewById(R.id.tv_address)).setText(useCarAddr);
        ((TextView) confirmDialog.findViewById(R.id.tv_serve_time)).setText(dateFormat.format(
                useCarDate));
        ((TextView) confirmDialog.findViewById(R.id.tv_serve_customer)).setText(contactName);
        ((TextView) confirmDialog.findViewById(R.id.tv_phone)).setText(contactPhone);

        confirmDialog.show();

    }


    private void submitOrder() {
        new HljTracker.Builder(this).eventableType("Car")
                .action("confirm")
                .additional(String.valueOf(totalPrice))
                .build()
                .send();
        JSONObject submitObj = new JSONObject();
        final JSONObject extraObject = new JSONObject();
        try {
            JSONObject itemsObj = new JSONObject();
            JSONArray itemsArr = new JSONArray();
            JSONArray extraArray = new JSONArray();
            for (CarShoppingCartItem item : cartItems) {
                JSONObject itemObj = new JSONObject();
                itemObj.put("sku_id",
                        item.getCarSku()
                                .getId());
                itemObj.put("product_id",
                        item.getCarProduct()
                                .getId());
                itemObj.put("quantity", item.getQuantity());
                itemsArr.put(itemObj);

                extraArray.put(itemObj);
            }

            extraObject.put("sub_items", extraArray);

            itemsObj.put("sub_items", itemsArr);
            itemsObj.put("address", useCarAddr);
            itemsObj.put("serve_time", simpleDateFormat2.format(useCarDate));
            itemsObj.put("buyer_name", contactName);
            itemsObj.put("buyer_phone", contactPhone);
            if (insuranceAgreement.isChecked()) {
                JSONObject insuranceObj = new JSONObject();
                insuranceObj.put("bride_name",
                        etBrideName.getText()
                                .toString());
                insuranceObj.put("bride_identity",
                        etBrideId.getText()
                                .toString());
                insuranceObj.put("groom_name",
                        etGroomName.getText()
                                .toString());
                insuranceObj.put("groom_identity",
                        etGroomId.getText()
                                .toString());
                itemsObj.put("insurance", insuranceObj);
            } else {
                itemsObj.put("insurance", "");
            }

            submitObj.put("items", itemsObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(this);
        }
        progressDialog.show();
        new StatusHttpPostTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                for (CarShoppingCartItem cartItem : cartItems) {
                    WeddingCarSession.getInstance()
                            .removeCarCart(NewCarOrderConfirmActivity.this, cartItem);
                }
                // 订单提交成功,显示提示信息
                if (successDialog != null && successDialog.isShowing()) {
                    return;
                }
                successDialog = DialogUtil.createSingleButtonDialog(successDialog,
                        NewCarOrderConfirmActivity.this,
                        getString(R.string.msg_success_to_submit_car_order),
                        null,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(NewCarOrderConfirmActivity.this,
                                        MyOrderListActivity.class);
                                intent.putExtra(RouterPath.IntentPath.Customer.MyOrder
                                                .ARG_BACK_MAIN,
                                        true);
                                intent.putExtra(RouterPath.IntentPath.Customer.MyOrder
                                                .ARG_SELECT_TAB,
                                        RouterPath.IntentPath.Customer.MyOrder.Tab.CAR_ORDER);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.activity_anim_default);
                            }
                        });

                successDialog.setCancelable(false);
                successDialog.show();

            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Util.postFailToast(NewCarOrderConfirmActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_submit_order,
                        network);

            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.SUBMIT_CAR_ORDER_URL),
                submitObj.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case Constants.RequestCode.EDIT_CAR_USE_INFO:
                    SharedPreferences sp = getSharedPreferences(Constants.PREF_FILE, MODE_PRIVATE);
                    User user = Session.getInstance()
                            .getCurrentUser(this);
                    contactName = data.getStringExtra("name");
                    contactPhone = data.getStringExtra("phone");
                    String time = data.getStringExtra("time");
                    useCarAddr = data.getStringExtra("address");
                    if (user != null && user.getId() != 0) {
                        sp.edit()
                                .putString(Constants.PREF_LAST_SERVE_CUSTORMER + user.getId(),
                                        contactName)
                                .apply();
                        sp.edit()
                                .putString(Constants.PREF_LAST_SERVE_PHONE + user.getId(),
                                        contactPhone)
                                .apply();
                        sp.edit()
                                .putString(Constants.PREF_LAST_CAR_USE_TIME + user.getId(), time)
                                .apply();
                        sp.edit()
                                .putString(Constants.PREF_LAST_SERVE_ADDR + user.getId(),
                                        useCarAddr)
                                .apply();
                    }
                    if (!JSONUtil.isEmpty(time)) {
                        useCarDate = JSONUtil.getDateFromString(time);
                        tvCarUseTime.setText(getString(R.string.label_car_use_time, time));
                    }
                    tvCarUseAddr.setText(getString(R.string.label_car_use_addr, useCarAddr));
                    tvContactPhone.setText(contactPhone);
                    tvContactName.setText(contactName);
                    tvHint.setVisibility(View.GONE);
                    userInfoLayout.setVisibility(View.VISIBLE);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setCarItems() {
        itemsLayout.removeAllViews();
        for (final CarShoppingCartItem item : cartItems) {
            View itemView = getLayoutInflater().inflate(R.layout.car_shop_cart_item, null);
            TextView tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            ImageView imgCover = (ImageView) itemView.findViewById(R.id.img_cover);
            TextView tvSkuInfo = (TextView) itemView.findViewById(R.id.tv_sku_info);
            TextView tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            TextView tvQuantity = (TextView) itemView.findViewById(R.id.tv_quantity);

            tvTitle.setText(item.getCarProduct()
                    .getTitle());

            Glide.with(this)
                    .load(ImagePath.buildPath(item.getCarProduct()
                            .getCoverImage() == null ? null : item.getCarProduct()
                            .getCoverImage()
                            .getImagePath())
                            .width(imgCover.getLayoutParams().width)
                            .height(imgCover.getLayoutParams().width)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(imgCover);

            tvSkuInfo.setText(getString(R.string.label_sku2,
                    item.getCarSku()
                            .getSkuNames()));
            tvPrice.setText(getString(R.string.label_price,
                    CommonUtil.formatDouble2String((item.getCarCartCheck()
                            .getShowPrice()))));
            tvQuantity.setText("x" + String.valueOf(item.getQuantity()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(NewCarOrderConfirmActivity
                            .this, WeddingCarProductDetailActivity.class);
                    intent.putExtra(WeddingCarProductDetailActivity.ARG_ID,
                            item.getCarProduct()
                                    .getId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            });
            itemsLayout.addView(itemView);
        }
    }

    private void setPrices() {
        if (cartItems == null || cartItems.isEmpty()) {
            bottomLayout.setVisibility(View.GONE);
        } else {
            bottomLayout.setVisibility(View.VISIBLE);
            totalPrice = 0;
            for (CarShoppingCartItem item : cartItems) {
                totalPrice += item.getCarCartCheck()
                        .getShowPrice() * item.getQuantity();
            }

            tvTotalPrice2.setText(CommonUtil.formatDouble2String(totalPrice - discountMoney));
        }
        if (discountMoney <= 0) {
            layoutDiscount.setVisibility(View.GONE);
        } else {
            tvDiscountPrice.setText(getString(R.string.label_price7,
                    CommonUtil.formatDouble2String(discountMoney)));
            layoutDiscount.setVisibility(View.VISIBLE);
        }
        tvTotalCarPrice.setText(getString(R.string.label_price,
                CommonUtil.formatDouble2String(totalPrice)));
        tvTotalPrice.setText(getString(R.string.label_price,
                CommonUtil.formatDouble2String(totalPrice - discountMoney)));
    }

    private void setRedPacketsInfo() {
        redPacketLayout.setVisibility(View.VISIBLE);
        if (redPackets.isEmpty()) {
            tvAvailableCount.setText(R.string.label_no_avaliable_red_packet);
            tvAvailableCount.setBackgroundColor(getResources().getColor(android.R.color
                    .transparent));
            tvAvailableCount.setTextColor(getResources().getColor(R.color.colorGray));
        } else {
            tvAvailableCount.setText(getString(R.string.label_available_packet_count,
                    redPackets.size()));
            tvAvailableCount.setBackgroundResource(R.drawable.sp_r15_yellow);
            tvAvailableCount.setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    private void setInsuranceLayout() {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) insuranceLayout
                .getLayoutParams();
        insuranceAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ResizeAnimation resizeAnimation;
                if (isChecked) {
                    resizeAnimation = new ResizeAnimation(carInsuranceItems, insuranceLayoutHeight);
                    resizeAnimation.setDuration(100);
                } else {
                    resizeAnimation = new ResizeAnimation(carInsuranceItems, 0);
                    resizeAnimation.setDuration(100);
                }

                insuranceLayout.startAnimation(resizeAnimation);
            }
        });
        tvInsuranceAgreement.setText(Html.fromHtml(getString(R.string.label_insurance_agreement)));
    }

    @OnClick(R.id.insurance_about_layout)
    void goSeeAboutInsurance() {
        Intent intent = new Intent(this, HljWebViewActivity.class);
        intent.putExtra("path", PINGAN_INSURANCE_ABOUT_URL);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }


    private void getExtraInfos() {
        JSONObject jsonObject = new JSONObject();
        if (cartItems != null && cartItems.size() > 0) {
            try {
                JSONArray jsonArray = new JSONArray();
                for (CarShoppingCartItem item : cartItems) {
                    JSONObject itemObj = new JSONObject();
                    itemObj.put("sku_id",
                            item.getCarSku()
                                    .getId());
                    itemObj.put("product_id",
                            item.getCarProduct()
                                    .getId());
                    itemObj.put("quantity", item.getQuantity());

                    jsonArray.put(itemObj);
                }

                jsonObject.put("sub_items", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            getRedPacketList(jsonObject);
            getDiscountAmount(jsonObject);
        }
    }

    private void getRedPacketList(JSONObject jsonObject) {
        new StatusHttpPostTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                progressBar.setVisibility(View.GONE);
                JSONObject resultObject = (JSONObject) object;
                if (resultObject != null) {
                    JSONArray jsonArray = resultObject.optJSONArray("list");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            RedPacket redPacket = new RedPacket(jsonArray.optJSONObject(i));
                            redPackets.add(redPacket);
                        }
                    }
                }

                // 设置红包显示
                setRedPacketsInfo();
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                setRedPacketsInfo();
                Util.postFailToast(NewCarOrderConfirmActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_get_car_red_packets,
                        network);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.GET_CAR_RED_PACKT_LIST),
                jsonObject.toString());
    }


    private void getDiscountAmount(final JSONObject jsonObject) {
        new StatusHttpPostTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                progressBar.setVisibility(View.GONE);
                JSONObject resultObject = (JSONObject) object;
                if (resultObject != null) {
                    discountMoney = resultObject.optDouble("discount_amount", 0);
                }

                // 刷新订单优惠金额
                setPrices();
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                Util.postFailToast(NewCarOrderConfirmActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_get_car_order_discount,
                        network);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.GET_CAR_ORDER_DISCOUNT),
                jsonObject.toString());
    }
}
