package com.hunliji.marrybiz.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.CustomSetmeal;
import com.hunliji.marrybiz.model.CustomSetmealOrder;
import com.hunliji.marrybiz.model.MessageEvent;
import com.hunliji.marrybiz.model.OrderPayHistory;
import com.hunliji.marrybiz.model.Photo;
import com.hunliji.marrybiz.model.Status;
import com.hunliji.marrybiz.model.User;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.task.NewHttpPostTask;
import com.hunliji.marrybiz.task.NewHttpPutTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.hunliji.marrybiz.util.TimeUtil;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.chat.WSMerchantChatActivity;
import com.hunliji.marrybiz.widget.RoundProgressDialog;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Suncloud on 2016/2/17.
 */
public class CustomOrderDetailActivity extends HljBaseActivity {

    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.status_info)
    TextView statusInfo;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.wedding_date)
    TextView weddingDate;
    @BindView(R.id.wedding_date_layout)
    RelativeLayout weddingDateLayout;
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.pay_price)
    TextView payPrice;
    @BindView(R.id.pay_info_layout)
    RelativeLayout payInfoLayout;
    @BindView(R.id.earnest)
    TextView earnest;
    @BindView(R.id.gift)
    TextView gift;
    @BindView(R.id.earnest_layout)
    RelativeLayout earnestLayout;
    @BindView(R.id.protocol_images_layout)
    GridLayout protocolImagesLayout;
    @BindView(R.id.images_layout)
    LinearLayout imagesLayout;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.message_layout)
    LinearLayout messageLayout;
    @BindView(R.id.order_protocol_info)
    LinearLayout orderProtocolInfo;
    @BindView(R.id.order_no)
    TextView orderNo;
    @BindView(R.id.order_user)
    TextView orderUser;
    @BindView(R.id.order_time)
    TextView orderTime;
    @BindView(R.id.info_layout)
    ScrollView infoLayout;
    @BindView(R.id.action1)
    Button action1;
    @BindView(R.id.action2)
    Button action2;
    @BindView(R.id.double_action_layout)
    LinearLayout doubleActionLayout;
    @BindView(R.id.single_action)
    Button singleAction;
    @BindView(R.id.bottom_layout)
    RelativeLayout bottomLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.set_meal_layout)
    RelativeLayout setMealLayout;
    @BindView(R.id.transaction_time)
    TextView transactionTime;
    @BindView(R.id.transaction_time_layout)
    LinearLayout transactionTimeLayout;

    private int imgWidth;
    private int imageViewSize;
    private int imageSize;
    private CustomSetmealOrder order;
    private Handler handler = new Handler();
    private Runnable timeDownRun = new Runnable() {
        @Override
        public void run() {
            if (order != null && (order.getStatus() == 10 || order.getStatus() == 11)) {
                statusInfoTimeDown(order.getStatus() == 10 ? R.string.label_accept_time_down : R
                        .string.label_pay_time_down);
            }
        }
    };
    private Dialog dialog;

    private RoundProgressDialog progressDialog;
    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Point point = JSONUtil.getDeviceSize(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        imageViewSize = Math.round((point.x - 20 * dm.density) / 3);
        imageSize = Math.round(imageViewSize - 20 * dm.density);
        imgWidth = Math.round(point.x * 100 / 320);
        int mealHeight = Math.round(imgWidth * 212 / 338 + 24 * dm.density);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_order_detail);
        ButterKnife.bind(this);
        progressBar.setVisibility(View.VISIBLE);
        setMealLayout.getLayoutParams().height = mealHeight;
        imgCover.getLayoutParams().width = imgWidth;
        id = getIntent().getLongExtra("id", 0);
        order = (CustomSetmealOrder) getIntent().getSerializableExtra("order");
        if (order != null) {
            id = order.getId();
        }
        String title = getIntent().getStringExtra("title");
        if (!JSONUtil.isEmpty(title)) {
            setTitle(title);
        }
        if (!EventBus.getDefault()
                .isRegistered(this)) {
            EventBus.getDefault()
                    .register(this);
        }
        new GetOrderInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                Constants.getAbsUrl(String.format(Constants.HttpPath.CUSTOM_ORDER_INFO, id)));

    }

    private class GetOrderInfoTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String json = JSONUtil.getStringFromUrl(CustomOrderDetailActivity.this, params[0]);
                return new JSONObject(json).optJSONObject("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressBar.setVisibility(View.GONE);
            if (jsonObject != null) {
                infoLayout.setVisibility(View.VISIBLE);
                CustomSetmealOrder order1 = new CustomSetmealOrder(jsonObject);
                if (order != null && order.getStatus() != order1.getStatus()) {
                    EventBus.getDefault()
                            .post(new MessageEvent(8, order1));
                } else {
                    order = order1;
                    setOrderDetail();
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    private void setOrderDetail() {
        setTitle(order.getStatusStr());
        name.setText(order.getBuyerName());
        phone.setText(order.getBuyerPhone());
        if (order.getWeddingTime() != null) {
            weddingDateLayout.setVisibility(View.VISIBLE);
            weddingDate.setText(new DateTime(order.getWeddingTime()).toString(Constants
                    .DATE_FORMAT_SHORT));
        } else {
            weddingDateLayout.setVisibility(View.GONE);
        }
        CustomSetmeal setmeal = order.getCustomSetmeal();
        String path = null;
        if (setmeal != null) {
            tvTitle.setText(setmeal.getTitle());
            path = setmeal.getCoverPath();
        }
        path = JSONUtil.getImagePath(path, imgWidth);
        if (!JSONUtil.isEmpty(path)) {
            Glide.with(CustomOrderDetailActivity.this)
                    .load(path)
                    .into(imgCover);
        }
        tvPrice.setText(getString(R.string.label_price4,
                Util.formatDouble2String(order.getActualPrice())));
        if (order.getPaidMoney() > 0) {
            payInfoLayout.setVisibility(View.VISIBLE);
            payPrice.setText(Util.formatDouble2String(Math.min(order.getPaidMoney() + order
                    .getRedPacketMoney(),
                    order.getActualPrice())));
        } else {
            payInfoLayout.setVisibility(View.GONE);
        }
        if (setmeal != null && !JSONUtil.isEmpty(setmeal.getOrderGift())) {
            gift.setText(setmeal.getOrderGift());
        } else {
            gift.setText(R.string.label_unable);
        }
        line.setVisibility(View.VISIBLE);
        orderProtocolInfo.setVisibility(View.GONE);
        if (!JSONUtil.isEmpty(order.getMessage())) {
            messageLayout.setVisibility(View.VISIBLE);
            orderProtocolInfo.setVisibility(View.VISIBLE);
            message.setText(order.getMessage());
        } else {
            messageLayout.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }
        if (order.getProtocolPhotos() != null && !order.getProtocolPhotos()
                .isEmpty()) {
            earnestLayout.setVisibility(View.VISIBLE);
            if (order.getEarnestMoney() > 0) {
                earnest.setText(getString(R.string.label_price,
                        Util.formatDouble2String(order.getEarnestMoney())));
            } else {
                earnest.setText(R.string.label_unable);
            }
            orderProtocolInfo.setVisibility(View.VISIBLE);
            imagesLayout.setVisibility(View.VISIBLE);
            int size = order.getProtocolPhotos()
                    .size();
            int count = protocolImagesLayout.getChildCount();
            if (count > size) {
                protocolImagesLayout.removeViews(size, count - size);
            }
            for (int i = 0; i < size; i++) {
                View view = protocolImagesLayout.getChildAt(i);
                Photo photo = order.getProtocolPhotos()
                        .get(i);
                if (view == null) {
                    view = View.inflate(CustomOrderDetailActivity.this,
                            R.layout.protocol_image_item,
                            null);
                    protocolImagesLayout.addView(view, imageViewSize, imageViewSize);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(CustomOrderDetailActivity.this,
                                    PicsPageViewActivity.class);
                            intent.putExtra("photos", order.getProtocolPhotos());
                            intent.putExtra("position", protocolImagesLayout.indexOfChild(v));
                            startActivity(intent);
                        }
                    });
                }
                ViewHolder holder = (ViewHolder) view.getTag();
                if (holder == null) {
                    holder = new ViewHolder(view);
                    holder.delete.setVisibility(View.GONE);
                    holder.image.setBackgroundColor(getResources().getColor(R.color.colorLine));
                    view.setTag(holder);
                }
                if (!JSONUtil.isEmpty(path)) {
                    path = JSONUtil.getImagePath2(photo.getImagePath(), imageSize);
                    if (!JSONUtil.isEmpty(path) && !path.equals(holder.image.getTag())) {
                        ImageLoadTask task = new ImageLoadTask(holder.image, 0);
                        holder.image.setTag(path);
                        task.loadImage(path,
                                imageSize,
                                ScaleMode.WIDTH,
                                new AsyncBitmapDrawable(getResources(),
                                        R.mipmap.icon_empty_image,
                                        task));
                    }
                }
            }
        } else {
            earnestLayout.setVisibility(View.GONE);
            imagesLayout.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }
        orderNo.setText(order.getOrderNo());
        if (order.getUser() != null) {
            orderUser.setText(order.getUser()
                    .getNick());
        } else {
            orderUser.setText(order.getBuyerName());
        }
        if (order.getCreatedAt() != null) {
            orderTime.setText(new DateTime(order.getCreatedAt()).toString(getString(R.string
                    .format_date_type11)));
        }
        if (order.getPayHistories() != null && !order.getPayHistories()
                .isEmpty()) {
            transactionTimeLayout.setVisibility(View.VISIBLE);
            OrderPayHistory history = order.getPayHistories()
                    .get(order.getPayHistories()
                            .size() - 1);
            if (history.getCreatedAt() != null) {
                transactionTime.setText(new DateTime(history.getCreatedAt()).toString(getString(R
                        .string.format_date_type11)));
            }
        } else {
            transactionTimeLayout.setVisibility(View.GONE);
        }
        switch (order.getStatus()) {
            case 10:
                status.setText(R.string.label_order_status1);
                statusInfo.setVisibility(View.VISIBLE);
                statusInfoTimeDown(R.string.label_accept_time_down);
                bottomLayout.setVisibility(View.VISIBLE);
                singleAction.setVisibility(View.GONE);
                doubleActionLayout.setVisibility(View.VISIBLE);
                action2.setText(R.string.label_accept_order2);
                action1.setText(R.string.label_decline_order);
                action1.setBackgroundColor(getResources().getColor(R.color.colorGray));
                action1.setOnClickListener(new OnOrderActionClickListener(order));
                action2.setOnClickListener(new OnOrderActionClickListener(order));
                break;
            case 11:
                status.setText(R.string.label_order_status2);
                statusInfo.setVisibility(View.VISIBLE);
                statusInfoTimeDown(R.string.label_pay_time_down);
                bottomLayout.setVisibility(View.VISIBLE);
                singleAction.setVisibility(View.GONE);
                doubleActionLayout.setVisibility(View.VISIBLE);
                action2.setText(R.string.label_edit_protocol);
                action1.setText(R.string.title_activity_change_order_price);
                action1.setBackgroundResource(R.drawable.sl_color_primary_2_dark);
                action1.setOnClickListener(new OnOrderActionClickListener(order));
                action2.setOnClickListener(new OnOrderActionClickListener(order));
                break;
            case 87:
                if (order.isFinished()) {
                    status.setText(R.string.label_order_status12);
                    statusInfo.setVisibility(View.VISIBLE);
                    statusInfo.setText(R.string.label_order_status13);
                    bottomLayout.setVisibility(View.VISIBLE);
                    singleAction.setVisibility(View.VISIBLE);
                    singleAction.setEnabled(false);
                    singleAction.setText(R.string.label_confirm_service_finish);
                } else {
                    status.setText(R.string.label_order_status3);
                    OrderPayHistory history = null;
                    if (order.getPayHistories() != null && !order.getPayHistories()
                            .isEmpty()) {
                        history = order.getPayHistories()
                                .get(0);
                    }
                    if (history != null && history.getCreatedAt() != null) {
                        statusInfo.setVisibility(View.VISIBLE);
                        statusInfo.setText(new DateTime(history.getCreatedAt()).toString
                                (getString(R.string.label_pay_time)));
                    } else {
                        statusInfo.setVisibility(View.GONE);
                    }
                    if (order.isPayAll()) {
                        bottomLayout.setVisibility(View.VISIBLE);
                        singleAction.setVisibility(View.VISIBLE);
                        singleAction.setEnabled(true);
                        singleAction.setOnClickListener(new OnOrderActionClickListener(order));
                        singleAction.setText(R.string.label_confirm_service);
                        doubleActionLayout.setVisibility(View.GONE);
                    } else {
                        bottomLayout.setVisibility(View.GONE);
                    }
                }
                break;
            case 20:
            case 21:
            case 23:
                status.setText(R.string.label_order_status4);
                if (!JSONUtil.isEmpty(order.getRefundReasonName())) {
                    statusInfo.setVisibility(View.VISIBLE);
                    statusInfo.setText(getString(R.string.label_reason_name,
                            order.getRefundReasonName()));
                } else {
                    statusInfo.setVisibility(View.GONE);
                }
                bottomLayout.setVisibility(View.VISIBLE);
                singleAction.setVisibility(View.VISIBLE);
                singleAction.setEnabled(true);
                doubleActionLayout.setVisibility(View.GONE);
                singleAction.setOnClickListener(new OnOrderActionClickListener(order));
                singleAction.setText(R.string.btn_refund_info);
                break;
            case 24:
                status.setText(getString(R.string.label_order_status5,
                        Util.formatDouble2String(order.getRefundPayMoney() + (order.isInvalid() ?
                                0 : order.getRedPacketMoney()))));
                if (!JSONUtil.isEmpty(order.getRefundReasonName())) {
                    statusInfo.setVisibility(View.VISIBLE);
                    statusInfo.setText(getString(R.string.label_reason_name,
                            order.getRefundReasonName()));
                } else {
                    statusInfo.setVisibility(View.GONE);
                }
                bottomLayout.setVisibility(View.VISIBLE);
                singleAction.setVisibility(View.VISIBLE);
                singleAction.setEnabled(true);
                doubleActionLayout.setVisibility(View.GONE);
                singleAction.setOnClickListener(new OnOrderActionClickListener(order));
                singleAction.setText(R.string.btn_refund_info);
                break;
            case 90:
            case 92:
                status.setText(order.isFinished() ? R.string.label_order_status6 : R.string
                        .label_order_status7);
                statusInfo.setVisibility(View.GONE);
                if (!order.isFinished()) {
                    bottomLayout.setVisibility(View.VISIBLE);
                    singleAction.setVisibility(View.VISIBLE);
                    singleAction.setEnabled(true);
                    doubleActionLayout.setVisibility(View.GONE);
                    singleAction.setOnClickListener(new OnOrderActionClickListener(order));
                    singleAction.setText(R.string.label_confirm_service);
                } else {
                    bottomLayout.setVisibility(View.GONE);
                }
                break;
            case 93:
                status.setText(R.string.label_order_status9);
                if (JSONUtil.isEmpty(order.getReasonName())) {
                    statusInfo.setVisibility(View.VISIBLE);
                    statusInfo.setText(order.getReasonName());
                } else if (order.getReason() == 4) {
                    statusInfo.setVisibility(View.VISIBLE);
                    statusInfo.setText(R.string.label_order_close_reason1);
                } else if (order.getReason() == 3) {
                    statusInfo.setVisibility(View.VISIBLE);
                    statusInfo.setText(R.string.label_order_close_reason2);
                } else {
                    statusInfo.setVisibility(View.GONE);
                }
                bottomLayout.setVisibility(View.GONE);
                break;
            case 91:
                status.setText(order.getReason() == 2 ? R.string.label_order_status8 : R.string
                        .label_order_status10);
                statusInfo.setVisibility(View.GONE);
                bottomLayout.setVisibility(View.GONE);
                break;
        }

        if (order.isInvalid()) {
            status.setText(R.string.label_order_status11);
            statusInfo.setVisibility(View.VISIBLE);
            statusInfo.setText(R.string.label_order_close_invalid);
        }
    }

    @OnClick({R.id.call, R.id.chat, R.id.set_meal_layout, R.id.pay_info_layout})
    public void onClick(View view) {
        if (order == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.call:
                if (!JSONUtil.isEmpty(order.getBuyerPhone())) {
                    callUp(Uri.parse("tel:" + order.getBuyerPhone()
                            .trim()));
                }
                break;
            case R.id.chat:
                User user = order.getUser();
                if (user == null) {
                    user = new User(new JSONObject());
                    user.setId(order.getUserId());
                    user.setNick(order.getBuyerName());
                }
                if (user.getId() > 0) {
                    Intent intent = new Intent(this, WSMerchantChatActivity.class);
                    CustomerUser customerUser = new CustomerUser();
                    customerUser.setId(user.getId());
                    customerUser.setNick(user.getNick());
                    customerUser.setAvatar(user.getAvatar2());
                    intent.putExtra("user", customerUser);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
                break;
            case R.id.set_meal_layout:
                if (order.getCustomSetmeal() != null && order.getCustomSetmeal()
                        .getId() > 0) {
                    Intent intent = new Intent(this, CustomSetMealDetailActivity.class);
                    intent.putExtra("id",
                            order.getCustomSetmeal()
                                    .getId());
                    intent.putExtra("isPublished", -1);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
                break;
            case R.id.pay_info_layout:
                Intent intent = new Intent(this, CustomerPaymentHistoryActivity.class);
                intent.putExtra("order", order);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                break;

        }
    }

    private void statusInfoTimeDown(int strId) {
        long millisInFuture = (order.getExpiredAt() == null ? 0 : order.getExpiredAt()
                .getTime()) - System.currentTimeMillis();
        statusInfo.setText(getString(strId, millisFormat(millisInFuture)));
        if (millisInFuture >= 0) {
            handler.postDelayed(timeDownRun, 1000);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            new GetOrderInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                    Constants.getAbsUrl(String.format(Constants.HttpPath.CUSTOM_ORDER_INFO,
                            order.getId())));
        }
    }

    @Override
    protected void onResume() {
        if (order != null && (order.getStatus() == 10 || order.getStatus() == 11)) {
            statusInfoTimeDown(order.getStatus() == 10 ? R.string.label_accept_time_down : R
                    .string.label_pay_time_down);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(timeDownRun);
        super.onPause();
    }


    private String millisFormat(long millisTime) {
        int days = (int) (millisTime / (1000 * 60 * 60 * 24));
        return (days > 0 ? getString(R.string.label_day,
                days) : "") + TimeUtil.countDownMillisFormat(this, millisTime);
    }


    private class OnOrderActionClickListener implements View.OnClickListener {

        private CustomSetmealOrder order;

        private OnOrderActionClickListener(CustomSetmealOrder order) {
            this.order = order;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.action1:
                    if (order.getStatus() == 10) {
                        showConfirmDialog(0);
                    } else if (order.getStatus() == 11) {
                        Intent intent = new Intent(CustomOrderDetailActivity.this,
                                CustomOrderEditActivity.class);
                        intent.putExtra("order", order);
                        intent.putExtra("isEdit", true);
                        intent.putExtra("editType", 1);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                    break;
                case R.id.action2:
                    if (order.getStatus() == 10 || order.getStatus() == 11) {
                        Intent intent = new Intent(CustomOrderDetailActivity.this,
                                CustomOrderEditActivity.class);
                        intent.putExtra("order", order);
                        intent.putExtra("isEdit", order.getStatus() == 11);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                    break;
                case R.id.single_action:
                    if (order.getStatus() == 20 || order.getStatus() == 21 || order.getStatus()
                            == 23 || order.getStatus() == 24) {
                        //                        Intent intent = new Intent
                        // (CustomOrderDetailActivity.this, order
                        //                                .getAuthorizationStatusObb() == 24 ?
                        // CustomerReturnedActivity.class :
                        //                                CustomerReturningActivity.class);
                        Intent intent = new Intent(CustomOrderDetailActivity.this,
                                CustomerReturnedActivity.class);
                        intent.putExtra("order", order);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    } else if (order.getStatus() == 90 || order.getStatus() == 92) {
                        showConfirmDialog(1);
                    } else if (order.getStatus() == 87) {
                        showConfirmDialog(2);
                    }
                    break;
            }
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'protocol_image_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.delete)
        ImageButton delete;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void onEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.EventType.CUSTOM_ORDER_REFRESH_WITH_OBJECT &&
                event.getObject() != null && event.getObject() instanceof CustomSetmealOrder && (
                (CustomSetmealOrder) event.getObject()).getId() == id) {
            //            progressBar.setVisibility(View.VISIBLE);
            //            new GetOrderInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL,
            //                    Constants.getAbsUrl(String.format(Constants.HttpPath
            // .CUSTOM_ORDER_INFO, order.getId())));
            order = (CustomSetmealOrder) event.getObject();
            setOrderDetail();
        }
    }

    //type 0：拒绝接单 1：确认完成 2：待服务确认完成
    private void showConfirmDialog(final int type) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (dialog == null) {
            dialog = new Dialog(this, R.style.BubbleDialogTheme);
            dialog.setContentView(R.layout.dialog_confirm_notice);
            dialog.findViewById(R.id.btn_notice_cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
        }
        TextView noticeTitle = (TextView) dialog.findViewById(R.id.tv_msg_title);
        TextView noticeMsg = (TextView) dialog.findViewById(R.id.tv_notice_msg);
        dialog.findViewById(R.id.btn_notice_confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (progressDialog == null) {
                            progressDialog = JSONUtil.getRoundProgress(CustomOrderDetailActivity
                                    .this);
                        }
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        progressDialog.onLoadComplate();
                        if (type == 0) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("id", order.getId());
                                jsonObject.put("type ", 2);
                                new NewHttpPostTask(CustomOrderDetailActivity.this,
                                        new OrderHttpRequestListener(R.string.label_declined_order,
                                                R.string.msg_fail_to_reject_order),
                                        progressDialog).execute(Constants.getAbsUrl(Constants
                                        .HttpPath.CUSTOM_ORDER_RECEIVING),
                                        jsonObject.toString());
                            } catch (JSONException ignored) {

                            }
                        } else if (type == 1 || type == 2) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("order_id", order.getId());
                                new NewHttpPutTask(CustomOrderDetailActivity.this,
                                        new OrderHttpRequestListener(R.string
                                                .msg_success_confirm_service,
                                                R.string.msg_fail_to_confirm_service),
                                        progressDialog).execute(Constants.getAbsUrl(Constants
                                        .HttpPath.CUSTOM_ORDER_CONFIRM),
                                        jsonObject.toString());
                            } catch (JSONException ignored) {

                            }
                        }
                    }
                });
        if (type == 0) {
            noticeTitle.setVisibility(View.VISIBLE);
            noticeTitle.setText(R.string.label_decline_order);
            noticeMsg.setText(R.string.hint_decline_order);
        } else if (type == 1) {
            noticeTitle.setVisibility(View.GONE);
            noticeMsg.setText(R.string.hint_confirm_order);
        } else if (type == 2) {
            noticeTitle.setVisibility(View.VISIBLE);
            noticeTitle.setText(R.string.hint_confirm_order);
            noticeMsg.setText(R.string.hint_confirm_order2);
        }
        dialog.show();
    }

    private class OrderHttpRequestListener implements OnHttpRequestListener {

        private int completedId;
        private int failedId;

        private OrderHttpRequestListener(int completedId, int failedId) {
            this.completedId = completedId;
            this.failedId = failedId;
        }

        @Override
        public void onRequestCompleted(Object obj) {
            if (isFinishing()) {
                return;
            }
            JSONObject object = (JSONObject) obj;
            Status status = null;
            if (object != null && object.optJSONObject("status") != null) {
                status = new Status(object.optJSONObject("status"));
            }
            if (status != null && status.getRetCode() == 0) {
                if (object.optJSONObject("data") != null) {
                    order = new CustomSetmealOrder(object.optJSONObject("data"));
                    EventBus.getDefault()
                            .post(new MessageEvent(8, order));
                }
                new GetOrderInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                        Constants.getAbsUrl(String.format(Constants.HttpPath.CUSTOM_ORDER_INFO,
                                order.getId())));
                Util.showToast(CustomOrderDetailActivity.this, null, completedId);
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.onComplate();
                }
            } else {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Util.showToast(CustomOrderDetailActivity.this,
                        status == null ? null : status.getErrorMsg(),
                        failedId);
            }
        }

        @Override
        public void onRequestFailed(Object obj) {
            if (isFinishing()) {
                return;
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Util.showToast(CustomOrderDetailActivity.this, null, failedId);
        }
    }

    @Override
    protected void onFinish() {
        if (!EventBus.getDefault()
                .isRegistered(this)) {
            EventBus.getDefault()
                    .unregister(this);
        }
        super.onFinish();
    }
}