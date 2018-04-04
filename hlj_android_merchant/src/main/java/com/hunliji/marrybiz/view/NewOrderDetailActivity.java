package com.hunliji.marrybiz.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.order.OrderApi;
import com.hunliji.marrybiz.model.Label;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.MessageEvent;
import com.hunliji.marrybiz.model.NewOrder;
import com.hunliji.marrybiz.model.OrderPayHistory;
import com.hunliji.marrybiz.task.NewHttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.TimeUtil;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.chat.WSMerchantChatActivity;
import com.hunliji.marrybiz.view.orders.UploadProtocolImageActivity;
import com.hunliji.marrybiz.widget.PriceKeyboardView;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class NewOrderDetailActivity extends HljBaseActivity {
    private static final String TAG = NewOrderDetailActivity.class.getSimpleName();
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
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.status_info)
    TextView statusInfo;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.call)
    ImageButton call;
    @BindView(R.id.chat)
    ImageButton chat;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.customer_message_layout)
    LinearLayout customerMessageLayout;
    @BindView(R.id.wedding_date)
    TextView weddingDate;
    @BindView(R.id.wedding_date_layout)
    RelativeLayout weddingDateLayout;
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.installment)
    ImageView installment;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_price_extra)
    TextView tvPriceExtra;
    @BindView(R.id.work_layout)
    RelativeLayout workLayout;
    @BindView(R.id.tv_sales_text)
    TextView tvSalesText;
    @BindView(R.id.sales_layout)
    LinearLayout salesLayout;
    @BindView(R.id.tv_work_price2)
    TextView tvWorkPrice2;
    @BindView(R.id.tv_pay_all_saved_money)
    TextView tvPayAllSavedMoney;
    @BindView(R.id.pay_all_saved_money_layout)
    LinearLayout payAllSavedMoneyLayout;
    @BindView(R.id.tv_coupon_money)
    TextView tvCouponMoney;
    @BindView(R.id.coupon_money_layout)
    LinearLayout couponMoneyLayout;
    @BindView(R.id.tv_red_packet_name)
    TextView tvRedPacketName;
    @BindView(R.id.tv_red_packet_money)
    TextView tvRedPacketMoney;
    @BindView(R.id.red_packet_money_layout)
    LinearLayout redPacketMoneyLayout;
    @BindView(R.id.tv_customer_real_pay_money)
    TextView tvCustomerRealPayMoney;
    @BindView(R.id.tv_merchant_real_get_money)
    TextView tvMerchantRealGetMoney;
    @BindView(R.id.tv_order_no)
    TextView tvOrderNo;
    @BindView(R.id.tv_order_account)
    TextView tvOrderAccount;
    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;
    @BindView(R.id.tv_finish_time)
    TextView tvFinishTime;
    @BindView(R.id.finish_time_layout)
    LinearLayout finishTimeLayout;
    @BindView(R.id.ordering_info_layout)
    LinearLayout orderingInfoLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.ic_intent_money)
    ImageView imgIntentMoney;
    @BindView(R.id.btn_upload)
    Button btnUpload;
    @BindView(R.id.tv_upload_protocol_hint)
    TextView tvUploadProtocolHint;
    @BindView(R.id.grid_protocol_images)
    GridLayout gridProtocolImages;
    @BindView(R.id.protocol_images_layout)
    LinearLayout protocolImagesLayout;
    @BindView(R.id.money_history_layout)
    LinearLayout moneyHistoryLayout;
    @BindView(R.id.tv_work_price_label)
    TextView tvWorkPriceLabel;
    @BindView(R.id.tv_base_price)
    TextView tvBasePrice;
    @BindView(R.id.base_price_layout)
    LinearLayout basePriceLayout;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;

    private NewOrder order;
    private int imgWidth;
    private int imgHeight;
    private DisplayMetrics dm;
    private Dialog acceptDlg;
    private ArrayList<Label> reasons;
    private Dialog rejectDlg;
    private Label selectReason;
    private Dialog changePriceDlg;
    private TextView newPriceTv;
    private StringBuilder newPriceStr = new StringBuilder("");
    private double newPrice = Double.MIN_VALUE;
    private String title;
    private Dialog confirmDlg;
    private Dialog callDlg;
    private TextView oldPriceTv;
    private long orderId;

    private Handler handler = new Handler();
    private Runnable timeDownRun = new Runnable() {
        @Override
        public void run() {
            if (order != null) {
                if (order.getStatus() == 10) {
                    statusInfoTimeDown(R.string.label_pay_time_down);
                } else if (order.getStatus() == 11 && order.isFinished()) {
                    statusInfoTimeDown(R.string.label_user_confirm_time_down);
                }
            }
        }
    };
    private Dialog confirmRestDlg;
    private HljHttpSubscriber confirmSub;
    private int protocolImgViewSize;
    private int protocolImgSize;
    private HljHttpSubscriber changePricesSub;
    private Dialog priceChangeDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order_detail);
        ButterKnife.bind(this);
        orderId = getIntent().getLongExtra("id", 0);
        title = getIntent().getStringExtra("title");
        if (JSONUtil.isEmpty(title)) {
            title = getString(R.string.title_activity_new_order_detail);
        }
        setTitle(title);
        dm = getResources().getDisplayMetrics();
        Point point = JSONUtil.getDeviceSize(this);
        imgWidth = Math.round(point.x * 100 / 320);
        imgHeight = Math.round(imgWidth * 212 / 338);

        protocolImgViewSize = Math.round((point.x - 18 * dm.density) / 3);
        protocolImgSize = Math.round(protocolImgViewSize - 14 * dm.density);

        progressBar.setVisibility(View.VISIBLE);
        new GetOrderDetailTask().execute(String.format(Constants.getAbsUrl(Constants.HttpPath
                        .GET_NEW_ORDER_DETAIL),
                orderId));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_right);
    }

    private void setOrderDetail() {
        if (isFinishing()) {
            return;
        }
        setOrderAlert();
        setCustomerInfoAndMsg();
        setWorkInfo();
        setPricesStatus();
        setPriceDetails();
        setProtocolImages();
        setOrderInfo();
        setOrderActions();
    }

    private void setCustomerInfoAndMsg() {
        // 买家信息
        name.setText(order.getBuyerRealName());
        phone.setText(order.getBuyerPhone());
        if (TextUtils.isEmpty(order.getMessage())) {
            customerMessageLayout.setVisibility(View.GONE);
        } else {
            customerMessageLayout.setVisibility(View.VISIBLE);
            tvMessage.setText(order.getMessage());
        }
    }

    private void setWorkInfo() {
        // 服务时间
        if (order.getWeddingTime() != null) {
            weddingDateLayout.setVisibility(View.VISIBLE);
            weddingDate.setText(order.getWeddingTime()
                    .toString(getString(R.string.format_date_type8)));
        } else {
            weddingDateLayout.setVisibility(View.GONE);
        }
        imgIntentMoney.setVisibility(order.getPayType() == NewOrder.PAY_TYPE_INTENT ? View
                .VISIBLE : View.GONE);
        // 套餐信息
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imgCover
                .getLayoutParams();
        params.width = imgWidth;
        params.height = imgHeight;
        findViewById(R.id.installment).setVisibility(order.isInstallment() ? View.VISIBLE : View
                .GONE);
        Glide.with(this)
                .load(ImageUtil.getImagePath(order.getPrdCoverPath(), imgWidth))
                .into(imgCover);
        tvTitle.setText(order.getPrdTitle());
        tvPrice.setText(getString(R.string.label_price,
                Util.formatDouble2String(order.getActualPrice())));

        // 优惠活动
        if (order.getRule() != null && !TextUtils.isEmpty(order.getRule()
                .getName())) {
            salesLayout.setVisibility(View.VISIBLE);
            tvSalesText.setText(order.getRule()
                    .getName());
        } else {
            salesLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 价格状态信息
     */
    private void setPricesStatus() {
        moneyHistoryLayout.removeAllViews();
        if (order.getMoneyStatus() >= NewOrder.MONEY_STATUS_PAID_INTENT) {
            // 付过钱，直接显示付款历史记录
            for (int i = order.getPayHistories()
                    .size() - 1; i >= 0; i--) {
                OrderPayHistory history = order.getPayHistories()
                        .get(i);
                switch (history.getEvent()) {
                    case OrderPayHistory.PAY_HISTORY_EVENT_INTENT:
                        addHistoryView("意向金：",
                                CommonUtil.positive(history.getMoney()),
                                history.getCreatedAt());
                        break;
                    case OrderPayHistory.PAY_HISTORY_EVENT_DEPOSITE:
                        addHistoryView("定金：",
                                CommonUtil.positive(history.getMoney()),
                                history.getCreatedAt());
                        break;
                    case OrderPayHistory.PAY_HISTORY_EVENT_REST:
                        addHistoryView("余款：",
                                CommonUtil.positive(history.getMoney()),
                                history.getCreatedAt());
                        break;
                    case OrderPayHistory.PAY_HISTORY_EVENT_CONFIRM_REST:
                        addHistoryView(getString(R.string.label_offline_pay_rest),
                                -1,
                                history.getCreatedAt());
                        break;
                    case OrderPayHistory.PAY_HISTORY_EVENT_ALL:
                        addHistoryView("全款：",
                                CommonUtil.positive(history.getMoney()),
                                history.getCreatedAt());
                        break;
                }
            }
            if (order.getMoneyStatus() != NewOrder.MONEY_STATUS_PAID_ALL) {
                // 还有余款没有支付
                double rest = order.getCustomerRealPayMoney() - order.getPaidMoney();
                addHistoryView("余款：", CommonUtil.positive(rest), null);
            }
        } else {
            // 没有付过钱，根据付款类型显示几种不同的格式
            if (order.getPayType() == NewOrder.PAY_TYPE_INTENT) {
                // 意向金支付，显示意向金和余款
                addHistoryView("意向金：", CommonUtil.positive(order.getIntentMoney()), null);
                // 还有余款没有支付
                double rest = order.getCustomerRealPayMoney() - order.getIntentMoney();
                addHistoryView("余款：", CommonUtil.positive(rest), null);
            } else if (order.getPayType() == NewOrder.PAY_TYPE_DEPOSIT) {
                // 定金支付，显示定金和余款
                addHistoryView("定金：", CommonUtil.positive(order.getEarnestMoney()), null);
                double rest = order.getCustomerRealPayMoney() - order.getEarnestMoney();
                addHistoryView("余款：", CommonUtil.positive(rest), null);
            } else {
                // 全款
                addHistoryView("全款：", CommonUtil.positive(order.getCustomerRealPayMoney()), null);
            }
        }
    }

    private void addHistoryView(String label, double money, @Nullable DateTime dateTime) {
        View view = View.inflate(this, R.layout.money_history_item, null);
        PayHistoryViewHolder holder = new PayHistoryViewHolder(view);
        holder.tvPayHistoryLabel.setText(label);
        String moneyStr = money >= 0 ? getString(R.string.label_price,
                CommonUtil.formatDouble2String(money)) : "";
        holder.tvPayHistoryMoney.setText(moneyStr);
        if (dateTime != null) {
            holder.tvPayHistoryTime.setText(dateTime.toString("yyyy-MM-dd HH:mm:ss"));
        } else {
            holder.tvPayHistoryTime.setText("未付");
        }
        moneyHistoryLayout.addView(view);
    }

    private void setPriceDetails() {
        if (order.getPrdBasePrice() > 0) {
            tvWorkPriceLabel.setText("成交价：");
            basePriceLayout.setVisibility(View.VISIBLE);
            tvBasePrice.setText(getString(R.string.label_price,
                    Util.formatDouble2String(order.getPrdBasePrice())));
        } else {
            basePriceLayout.setVisibility(View.GONE);
            tvWorkPriceLabel.setText("套餐价格：");
        }

        tvWorkPrice2.setText(getString(R.string.label_price,
                Util.formatDouble2String(order.getActualPrice())));
        if (order.getAidMoney() > 0) {
            couponMoneyLayout.setVisibility(View.VISIBLE);
            tvCouponMoney.setText("-" + getString(R.string.label_price,
                    Util.formatDouble2String(order.getAidMoney())));
        } else {
            couponMoneyLayout.setVisibility(View.GONE);
        }
        if (order.getRedPacketMoney() > 0) {
            redPacketMoneyLayout.setVisibility(View.VISIBLE);
            tvRedPacketMoney.setText("-" + getString(R.string.label_price,
                    Util.formatDouble2String(order.getRedPacketMoney())));
            if (!TextUtils.isEmpty(order.getRedPacketInfo())) {
                tvRedPacketName.setText("(" + order.getRedPacketInfo() + ")");
            } else {
                tvRedPacketMoney.setVisibility(View.GONE);
            }
        } else {
            redPacketMoneyLayout.setVisibility(View.GONE);
        }

        if ((order.getPayType() == NewOrder.PAY_TYPE_PAY_ALL || order.getMoneyStatus() >=
                NewOrder.MONEY_STATUS_PAID_ALL) && order.getPayAllSavedMoney() > 0) {
            payAllSavedMoneyLayout.setVisibility(View.VISIBLE);
            tvPayAllSavedMoney.setText("-" + getString(R.string.label_price,
                    Util.formatDouble2String(order.getPayAllSavedMoney())));
        } else {
            payAllSavedMoneyLayout.setVisibility(View.GONE);
        }

        tvCustomerRealPayMoney.setText(getString(R.string.label_price,
                Util.formatDouble2StringPositive(order.getCustomerRealPayMoney())));
        tvMerchantRealGetMoney.setText(getString(R.string.label_price,
                Util.formatDouble2StringPositive(order.getMerchantRealGetMoney())));
    }

    private void setProtocolImages() {
        MerchantUser user = Session.getInstance()
                .getCurrentUser(this);
        if (user != null && user.getPropertyId() == Merchant.PROPERTY_WEDDING_PLAN) {
            // 按钮在订单完成之前，或关闭之前一直都存在
            if (order.getStatus() == NewOrder.STATUS_WAITING_FOR_THE_PAYMENT || order.getStatus()
                    == NewOrder.STATUS_WAITING_FOR_ACCEPT_ORDER || (order.getStatus() == NewOrder
                    .STATUS_MERCHANT_ACCEPT_ORDER && !order.isFinished())) {
                // 婚礼策划可以有合同图片
                protocolImagesLayout.setVisibility(View.VISIBLE);
                btnUpload.setVisibility(View.VISIBLE);
            } else {
                protocolImagesLayout.setVisibility(View.GONE);
                btnUpload.setVisibility(View.GONE);
            }

            if (order.getProtocolPhotos() != null && order.getProtocolPhotos()
                    .size() > 0) {
                protocolImagesLayout.setVisibility(View.VISIBLE);
                gridProtocolImages.setVisibility(View.VISIBLE);
                tvUploadProtocolHint.setVisibility(View.GONE);
                for (int i = 0; i < order.getProtocolPhotos()
                        .size(); i++) {
                    View view = gridProtocolImages.getChildAt(i);
                    Photo photo = order.getProtocolPhotos()
                            .get(i);
                    if (view == null) {
                        view = View.inflate(NewOrderDetailActivity.this,
                                R.layout.protocol_image_item2,
                                null);
                        gridProtocolImages.addView(view, protocolImgViewSize, protocolImgViewSize);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(NewOrderDetailActivity.this,
                                        PicsPageViewActivity.class);
                                intent.putExtra("photos", order.getProtocolPhotos());
                                intent.putExtra("position", gridProtocolImages.indexOfChild(v));
                                startActivity(intent);
                            }
                        });
                    }
                    ImageViewHolder holder = (ImageViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ImageViewHolder(view);
                        holder.btnDelete.setVisibility(View.GONE);
                        holder.imgCover.setBackgroundColor(ContextCompat.getColor(this,
                                R.color.colorLine));
                        view.setTag(holder);
                    }
                    Glide.with(this)
                            .load(ImagePath.buildPath(photo.getImagePath())
                                    .width(protocolImgSize)
                                    .height(protocolImgSize)
                                    .cropPath())
                            .into(holder.imgCover);
                }
            } else {
                gridProtocolImages.setVisibility(View.GONE);
                tvUploadProtocolHint.setVisibility(View.VISIBLE);
            }
        } else {
            protocolImagesLayout.setVisibility(View.GONE);
        }
    }

    private void setOrderInfo() {
        // 下单信息
        orderingInfoLayout.setVisibility(View.VISIBLE);
        tvOrderNo.setText(order.getOrderNo());
        tvOrderTime.setText(order.getCreatedAt()
                .toString(getString(R.string.format_date)));
        tvOrderAccount.setText(order.getUserNick());
    }

    private void setOrderActions() {
        singleAction.setEnabled(true);
        // 根据不同状态显示不同的操作
        if (order.getStatus() == NewOrder.STATUS_WAITING_FOR_ACCEPT_ORDER) {
            // 待接单
            bottomLayout.setVisibility(View.VISIBLE);
            singleAction.setVisibility(View.GONE);
            doubleActionLayout.setVisibility(View.VISIBLE);
            action1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray));
            action1.setText(R.string.label_decline_order);
            action2.setText(R.string.label_accept_order2);
        } else if (order.getStatus() == NewOrder.STATUS_WAITING_FOR_THE_PAYMENT) {
            // 待付款
            bottomLayout.setVisibility(View.VISIBLE);
            singleAction.setVisibility(View.VISIBLE);
            doubleActionLayout.setVisibility(View.GONE);
            singleAction.setText("修改价格");
        } else if (order.getStatus() == NewOrder.STATUS_REFUND_REVIEWING || order.getStatus() ==
                NewOrder.STATUS_REFUND_SUCCESS) {
            // 退款信息,只有审核中和退款成功才有详细退款信息
            //20 用户申请退款，退款处理中
            //22 同意退款处理
            //23 拒绝退款
            //24 退款成功
            bottomLayout.setVisibility(View.VISIBLE);
            singleAction.setVisibility(View.VISIBLE);
            doubleActionLayout.setVisibility(View.GONE);
            singleAction.setText(R.string.label_refund_detail);
        } else if ((order.getMoneyStatus() == NewOrder.MONEY_STATUS_PAID_ALL || order
                .getMoneyStatus() == NewOrder.MONEY_STATUS_PAID_INTENT) && !order.isFinished() &&
                order.getStatus() != NewOrder.STATUS_ORDER_CLOSED && order.getStatus() !=
                NewOrder.STATUS_ORDER_AUTO_CLOSED && order.getStatus() != NewOrder
                .STATUS_MERCHANT_REFUSE_ORDER) {
            NewOrder.MerchantAction confirmAction = null;
            for (NewOrder.MerchantAction action : order.getMerchantActions()) {
                if ("finish_order".equals(action.getAction()) || "finish_order_photo".equals
                        (action.getAction()) || "finish_pay_rest".equals(
                        action.getAction())) {
                    confirmAction = action;
                    break;
                }
            }
            bottomLayout.setVisibility(View.VISIBLE);
            singleAction.setVisibility(View.VISIBLE);
            singleAction.setVisibility(View.VISIBLE);
            doubleActionLayout.setVisibility(View.GONE);
            if (confirmAction != null) {
                singleAction.setText(confirmAction.getActionTxt());
            } else {
                singleAction.setEnabled(false);
                singleAction.setText(R.string.hint_confirm_service);
            }
        } else {
            if (order.getMoneyStatus() == NewOrder.MONEY_STATUS_PAID_DEPOSIT && !order.isFinished
                    ()) {
                // 商家已接单,并且已付定金，未付尾款，已超过服务时间
                // 显示确认已收尾款操作按钮
                NewOrder.MerchantAction confirmAction = null;
                for (NewOrder.MerchantAction action : order.getMerchantActions()) {
                    if ("finish_pay_rest".equals(action.getAction())) {
                        confirmAction = action;
                        break;
                    }
                }
                if (confirmAction != null) {
                    bottomLayout.setVisibility(View.VISIBLE);
                    singleAction.setVisibility(View.VISIBLE);
                    doubleActionLayout.setVisibility(View.GONE);
                    singleAction.setEnabled(true);
                    singleAction.setText(confirmAction.getActionTxt());
                } else {
                    bottomLayout.setVisibility(View.GONE);
                }
            } else {
                bottomLayout.setVisibility(View.GONE);
            }
        }
    }

    private void setOrderAlert() {
        statusInfo.setVisibility(View.GONE);
        if (order.isInvalid()) {
            // 无效单,直接显示无效但信息
            status.setText(R.string.label_invalid_order_status);
            statusInfo.setVisibility(View.VISIBLE);
            statusInfo.setText(R.string.label_invalid_order_msg);
        } else {
            switch (order.getStatus()) {
                case NewOrder.STATUS_WAITING_FOR_THE_PAYMENT:
                    // 代付款
                    status.setText(R.string.label_wait_to_pay2);
                    statusInfo.setVisibility(View.VISIBLE);
                    statusInfoTimeDown(R.string.label_pay_time_down);
                    break;
                case NewOrder.STATUS_WAITING_FOR_ACCEPT_ORDER:
                    // 待接单
                    status.setText(R.string.label_wait_to_accept2);
                    break;
                case NewOrder.STATUS_MERCHANT_ACCEPT_ORDER:
                    // 已接单
                    if (order.isFinished()) {
                        status.setText(R.string.label_wait_user_confirm);
                        statusInfo.setVisibility(View.VISIBLE);
                        statusInfoTimeDown(R.string.label_user_confirm_time_down);
                    } else {
                        status.setText(R.string.label_wait_to_service);
                    }
                    break;
                case NewOrder.STATUS_SERVICE_COMPLETE:
                case NewOrder.STATUS_ORDER_COMMENTED:
                    status.setText(!order.isFinished() ? R.string.label_buyer_confirmed : R
                            .string.label_both_confirm);
                    break;
                case NewOrder.STATUS_MERCHANT_REFUSE_ORDER:
                    // 商家拒绝接单
                    status.setText(R.string.label_rejected);
                    statusInfo.setVisibility(View.VISIBLE);
                    statusInfo.setText(order.getReason());
                    break;
                case NewOrder.STATUS_ORDER_CLOSED:
                    // 用户取消订单
                    status.setText(R.string.label_buyer_cancel);
                    break;
                case NewOrder.STATUS_ORDER_AUTO_CLOSED:
                    // 系统自动关闭订单
                    status.setText(R.string.label_system_auto_cancel);
                    break;
                case NewOrder.STATUS_REFUND_REVIEWING:
                    // 退款中
                    status.setText(R.string.label_refund_processing);
                    if (order.getRefundInfo() != null) {
                        statusInfo.setText(getString(R.string.label_refund_reson,
                                order.getRefundInfo()
                                        .getReason()));
                        statusInfo.setVisibility(View.VISIBLE);
                    }
                    break;
                case NewOrder.STATUS_REFUND_SUCCESS:
                    // 退款成功
                    if (order.getRefundInfo() != null) {
                        status.setText(getString(R.string.label_refunded_money2,
                                Util.formatDouble2String(order.getRefundInfo()
                                        .getMerchantPayMoney())));
                        statusInfo.setText(getString(R.string.label_refund_reson,
                                order.getRefundInfo()
                                        .getReason()));
                        statusInfo.setVisibility(View.VISIBLE);
                    } else {
                        status.setText(title);
                    }
                    break;
                default:
                    status.setText(title);
                    break;
            }
        }
    }

    @OnClick(R.id.single_action)
    void singleBtnAction() {
        if (order.getStatus() == NewOrder.STATUS_WAITING_FOR_THE_PAYMENT) {
            // 代付款并且是付全款
            if ((order.getPayType() == NewOrder.PAY_TYPE_DEPOSIT || order.getPayType() ==
                    NewOrder.PAY_TYPE_INTENT) && order.getEarnestMoney() > 0) {
                onChangePrice(PriceKeyboardView.MODE_BOTH);
            } else {
                onChangePrice(PriceKeyboardView.MODE_TOTAL);
            }
        } else {
            if (order.getStatus() == NewOrder.STATUS_MERCHANT_ACCEPT_ORDER && order
                    .getMoneyStatus() == NewOrder.MONEY_STATUS_PAID_ALL && !order.isFinished()) {
                // 用户付款,但商家未确认服务
                confirmService();
            } else if (order.getStatus() == NewOrder.STATUS_REFUND_REVIEWING || order.getStatus()
                    == NewOrder.STATUS_REFUND_APPROVED || order.getStatus() == 23 || order
                    .getStatus() == NewOrder.STATUS_REFUND_SUCCESS) {
                // 退款信息
                Intent intent = new Intent(this, NewOrderRefundActivity.class);
                intent.putExtra("order", order);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            } else if (order.getStatus() == NewOrder.STATUS_MERCHANT_ACCEPT_ORDER && (order
                    .getMoneyStatus() == NewOrder.MONEY_STATUS_PAID_DEPOSIT || order
                    .getMoneyStatus() == NewOrder.MONEY_STATUS_PAID_INTENT) && !order.isFinished
                    ()) {
                // 确认已收尾款
                confirmReceiveRestMoney();
            }
        }
    }

    @OnClick(R.id.action1)
    void actionBtn1() {
        if (order == null) {
            return;
        }
        // 根据不同状态显示不同的操作
        if (order.getStatus() == NewOrder.STATUS_WAITING_FOR_ACCEPT_ORDER) {
            // 待接单
            declineOrder();
        }
    }

    @OnClick(R.id.action2)
    void actionBtn2() {
        if (order == null) {
            return;
        }
        if (order.getStatus() == NewOrder.STATUS_WAITING_FOR_ACCEPT_ORDER) {
            // 待接单
            acceptOrder();
        }
    }

    private void acceptOrder() {
        if (acceptDlg != null && acceptDlg.isShowing()) {
            return;
        }
        acceptDlg = new Dialog(this, R.style.BubbleDialogTheme);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_notice, null);

        TextView noticeMsg = (TextView) dialogView.findViewById(R.id.tv_notice_msg);
        Button confirm = (Button) dialogView.findViewById(R.id.btn_notice_confirm);
        Button cancel = (Button) dialogView.findViewById(R.id.btn_notice_cancel);
        cancel.setVisibility(View.VISIBLE);
        //日程已满接单
        if (order.getCalStatus()) {
            noticeMsg.setText(getString(R.string.msg_accept_risk_order,
                    order.getWeddingTime() == null ? "" : order.getWeddingTime()
                            .toString(getString(R.string.format_date_type12,
                                    Locale.getDefault()))));
        } else {
            noticeMsg.setText(R.string.msg_accept_order);
        }
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptDlg.dismiss();
                submitAcceptOrder();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptDlg.cancel();
            }
        });
        acceptDlg.setContentView(dialogView);
        Window window = acceptDlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(this);
        params.width = Math.round(point.x * 27 / 32);
        window.setAttributes(params);
        acceptDlg.show();
    }

    private void declineOrder() {
        progressBar.setVisibility(View.VISIBLE);
        // 先获取拒绝的原因
        reasons = new ArrayList<>();
        new GetRejectReasonTask().execute();
    }

    @OnClick(R.id.call)
    void callBuyer() {
        if (order == null) {
            return;
        }
        if (!JSONUtil.isEmpty(order.getBuyerPhone())) {
            if (callDlg != null && callDlg.isShowing()) {
                return;
            }

            callDlg = new Dialog(this, R.style.BubbleDialogTheme);
            final View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_notice,
                    null);

            TextView noticeMsg = (TextView) dialogView.findViewById(R.id.tv_notice_msg);
            Button confirm = (Button) dialogView.findViewById(R.id.btn_notice_confirm);
            Button cancel = (Button) dialogView.findViewById(R.id.btn_notice_cancel);

            noticeMsg.setText(order.getBuyerPhone()
                    .trim());
            confirm.setText(R.string.label_call);

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callDlg.dismiss();
                    callUp(Uri.parse("tel:" + order.getBuyerPhone()
                            .trim()));
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callDlg.cancel();
                }
            });

            callDlg.setContentView(dialogView);
            Window window = callDlg.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
            callDlg.show();
        }
    }

    @OnClick(R.id.work_layout)
    void goWorkDetail() {
        Intent intent = new Intent(this, WorkActivity.class);
        intent.putExtra("isSnapshot", true);
        intent.putExtra("order_no", order.getParentOrderNo());
        intent.putExtra("work_id", order.getPrdId());
        intent.putExtra("type", order.getPrdType());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.btn_upload)
    void onUploadProtocolImages() {
        if (order.getProtocolPhotos() != null && order.getProtocolPhotos()
                .size() >= 12) {
            Toast.makeText(this, R.string.msg_protocol_imgs_max, Toast.LENGTH_SHORT)
                    .show();
        } else {
            Intent intent = new Intent(this, UploadProtocolImageActivity.class);
            int currentSize = order.getProtocolPhotos() == null ? 0 : order.getProtocolPhotos()
                    .size();
            intent.putExtra("photos_limit", 12 - currentSize);
            intent.putExtra("order_id", order.getId());
            startActivityForResult(intent, Constants.RequestCode.UPLOAD_PROTOCOL_PHOTOS);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    private void confirmReceiveRestMoney() {
        confirmSub = HljHttpSubscriber.buildSubscriber(NewOrderDetailActivity.this)
                .setProgressDialog(DialogUtil.createProgressDialog(NewOrderDetailActivity.this))
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        progressBar.setVisibility(View.VISIBLE);
                        Toast.makeText(NewOrderDetailActivity.this,
                                R.string.msg_confirm_rest_money_success,
                                Toast.LENGTH_SHORT)
                                .show();
                        new GetOrderDetailTask().execute(String.format(Constants.getAbsUrl
                                        (Constants.HttpPath.GET_NEW_ORDER_DETAIL),
                                orderId));
                    }
                })
                .build();
        confirmRestDlg = DialogUtil.createDoubleButtonDialog(this,
                getString(R.string.txt_msg_confirm_rest_money),
                "确认已收尾款",
                "继续等待用户",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrderApi.confirmRestMoney(orderId)
                                .subscribe(confirmSub);
                    }
                },
                null);
        confirmRestDlg.show();
    }

    private void confirmService() {
        MerchantUser user = Session.getInstance()
                .getCurrentUser(this);
        if (user.getPropertyId() == 6) {
            Intent intent = new Intent(this, OrderConfirmActivity.class);
            intent.putExtra("order", order);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            return;
        }
        if (confirmDlg != null && confirmDlg.isShowing()) {
            return;
        }

        confirmDlg = new Dialog(this, R.style.BubbleDialogTheme);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_notice, null);

        TextView noticeMsg = (TextView) dialogView.findViewById(R.id.tv_notice_msg);
        Button confirm = (Button) dialogView.findViewById(R.id.btn_notice_confirm);
        Button cancel = (Button) dialogView.findViewById(R.id.btn_notice_cancel);
        cancel.setVisibility(View.VISIBLE);

        noticeMsg.setText(R.string.msg_confirm_service);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDlg.dismiss();
                submitConfirmService();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDlg.cancel();
            }
        });

        confirmDlg.setContentView(dialogView);
        Window window = confirmDlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(this);
        params.width = Math.round(point.x * 27 / 32);
        window.setAttributes(params);
        confirmDlg.show();
    }

    @OnClick(R.id.chat)
    void contactUser() {
        if (order.getUserId() != 0) {
            CustomerUser user = new CustomerUser();
            user.setId(order.getUserId());
            user.setNick(order.getUserNick());
            user.setAvatar(order.getUserAvatar());
            Intent intent = new Intent(this, WSMerchantChatActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    /**
     * 修改价格
     *
     * @param type MODE_TOTAL = 0; int MODE_DEPOSIT = 1; MODE_BOTH = 2;
     */
    private void onChangePrice(final int type) {
        if (changePriceDlg != null && changePriceDlg.isShowing()) {
            return;
        }

        if (changePriceDlg == null) {
            changePriceDlg = new Dialog(this, R.style.BubbleDialogTheme);
            changePriceDlg.setContentView(R.layout.dialog_change_price_new);
            changePriceDlg.findViewById(R.id.btn_key_hide)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            changePriceDlg.cancel();
                        }
                    });
            Window win = changePriceDlg.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = point.x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        PriceKeyboardView keyboardView = (PriceKeyboardView) changePriceDlg.findViewById(R.id
                .keyboard);
        keyboardView.setConfirmOnClickListener(new PriceKeyboardView.ConfirmOnClickListener() {
            @Override
            public void priceConfirm(double newTotalPrice, double newDepositPrice) {
                if (newTotalPrice == order.getActualPrice() && newDepositPrice == order
                        .getEarnestMoney()) {
                    Toast.makeText(NewOrderDetailActivity.this,
                            getString(R.string.msg_need_new_price),
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Double actualPrice = null;
                    Double earnestMoney = null;
                    if (newTotalPrice > 0 && newTotalPrice != order.getActualPrice()) {
                        actualPrice = newTotalPrice;
                        if (order.getPrdBasePrice() > 0 && actualPrice < order.getPrdBasePrice()) {
                            showPriceChangeMsgDlg();
                            return;
                        }
                    }
                    if (newDepositPrice > 0 && newDepositPrice != order.getEarnestMoney()) {
                        earnestMoney = newDepositPrice;
                    }
                    onChangePrice(order.getId(), actualPrice, earnestMoney);
                    changePriceDlg.dismiss();
                }
            }
        });

        keyboardView.setPriceModifyMode(type);
        keyboardView.setDepositPrices(order.getEarnestMoney(), order.getOriginalEarnestMoney());
        keyboardView.setTotalPrices(order.getActualPrice(), order.getOriginalActualPrice());

        changePriceDlg.show();
    }

    private void onChangePrice(
            long orderSubId, final Double actualPrice, final Double earnestMoney) {
        changePricesSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                    @Override
                    public void onNext(JsonElement jsonElement) {
                        Toast.makeText(NewOrderDetailActivity.this,
                                R.string.msg_success_change_price,
                                Toast.LENGTH_SHORT)
                                .show();
                        try {
                            order = new NewOrder(new JSONObject(jsonElement.toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setOrderDetail();
                        EventBus.getDefault()
                                .post(new MessageEvent(7, order));
                    }
                })
                .build();
        OrderApi.postChangeOrderPrice(orderSubId, actualPrice, earnestMoney)
                .subscribe(changePricesSub);
    }

    /**
     * 修改价格小于结算价
     */
    private void showPriceChangeMsgDlg() {
        if (priceChangeDlg == null) {
            priceChangeDlg = DialogUtil.createSingleButtonDialog(this,
                    "修改失败",
                    "修改后的价格不能小于结算价",
                    "重试",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            priceChangeDlg.cancel();
                        }
                    });
        }
        priceChangeDlg.show();
    }

    //接单
    private void submitAcceptOrder() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", String.valueOf(order.getId()));
            jsonObject.put("is_detail", String.valueOf(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);
        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                progressBar.setVisibility(View.GONE);
                if (obj != null) {
                    JSONObject jsonObject1 = (JSONObject) obj;

                    if (jsonObject1 != null && jsonObject1.optJSONObject("status") != null) {
                        int retCode = jsonObject1.optJSONObject("status")
                                .optInt("RetCode", -1);
                        if (retCode == 0) {
                            Toast.makeText(NewOrderDetailActivity.this,
                                    R.string.msg_success_accept_order,
                                    Toast.LENGTH_SHORT)
                                    .show();
                            JSONObject dataObject = jsonObject1.optJSONObject("data");
                            if (dataObject != null) {
                                order = new NewOrder(dataObject);
                                setOrderDetail();
                                EventBus.getDefault()
                                        .post(new MessageEvent(7, order));
                            }

                        } else {
                            String msg = JSONUtil.getString(jsonObject1.optJSONObject("status"),
                                    "msg");
                            Toast.makeText(NewOrderDetailActivity.this, msg, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(NewOrderDetailActivity.this,
                                R.string.msg_fail_to_accept_order,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(NewOrderDetailActivity.this,
                            R.string.msg_fail_to_accept_order,
                            Toast.LENGTH_SHORT)
                            .show();
                }

            }

            @Override
            public void onRequestFailed(Object obj) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(NewOrderDetailActivity.this,
                        R.string.msg_fail_to_accept_order,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.ACCEPT_NEW_ORDER), jsonObject.toString());
    }

    private void submitConfirmService() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", String.valueOf(order.getId()));
            jsonObject.put("is_detail", String.valueOf(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);
        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                progressBar.setVisibility(View.GONE);
                if (obj != null) {
                    JSONObject jsonObject1 = (JSONObject) obj;

                    if (jsonObject1 != null && jsonObject1.optJSONObject("status") != null) {
                        int retCode = jsonObject1.optJSONObject("status")
                                .optInt("RetCode", -1);
                        if (retCode == 0) {
                            Toast.makeText(NewOrderDetailActivity.this,
                                    R.string.msg_success_confirm_service2,
                                    Toast.LENGTH_SHORT)
                                    .show();
                            JSONObject dataObject = jsonObject1.optJSONObject("data");
                            if (dataObject != null) {
                                order = new NewOrder(dataObject);
                                order.setIsFinished(true);
                                setOrderDetail();
                                EventBus.getDefault()
                                        .post(new MessageEvent(7, order));
                            }
                        } else {
                            String msg = JSONUtil.getString(jsonObject1.optJSONObject("status"),
                                    "msg");
                            Toast.makeText(NewOrderDetailActivity.this, msg, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(NewOrderDetailActivity.this,
                                R.string.msg_fail_to_confirm_service,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(NewOrderDetailActivity.this,
                            R.string.msg_fail_to_confirm_service,
                            Toast.LENGTH_SHORT)
                            .show();
                }

            }

            @Override
            public void onRequestFailed(Object obj) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(NewOrderDetailActivity.this,
                        R.string.msg_fail_to_confirm_service,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_CONFIRM_SERVICE),
                jsonObject.toString());
    }

    private void showRejectReasons() {
        if (rejectDlg != null && rejectDlg.isShowing()) {
            return;
        }

        selectReason = null;
        if (rejectDlg == null) {
            rejectDlg = new Dialog(this, R.style.BubbleDialogTheme);
            View v = getLayoutInflater().inflate(R.layout.dialog_reject_reasons, null);
            v.findViewById(R.id.btn_close)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rejectDlg.dismiss();
                        }
                    });
            v.findViewById(R.id.btn_confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 确定拒绝服务
                            if (selectReason == null) {
                                Toast.makeText(NewOrderDetailActivity.this,
                                        getString(R.string.msg_no_reason_selected),
                                        Toast.LENGTH_SHORT)
                                        .show();
                                return;
                            } else {
                                rejectDlg.dismiss();
                                submitDeclineOrder();
                            }
                        }
                    });
            final ListView listView = (ListView) v.findViewById(R.id.list);

            RejectReasonAdapter rejectReasonAdapter = new RejectReasonAdapter(reasons, this);
            listView.setAdapter(rejectReasonAdapter);
            listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    listView.setItemChecked(position, true);
                    selectReason = reasons.get(position);
                }
            });

            rejectDlg.setContentView(v);

            Window win = rejectDlg.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = point.x;
            params.height = point.y / 2;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }

        rejectDlg.show();
    }

    private void submitDeclineOrder() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", String.valueOf(order.getId()));
            jsonObject.put("reason", String.valueOf(selectReason.getId()));
            jsonObject.put("is_detail", String.valueOf(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);
        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                progressBar.setVisibility(View.GONE);
                if (obj != null) {
                    JSONObject resultObject = (JSONObject) obj;

                    if (resultObject != null && resultObject.optJSONObject("status") != null) {
                        int retCode = resultObject.optJSONObject("status")
                                .optInt("RetCode", -1);
                        if (retCode == 0) {
                            // 拒绝成功
                            Toast.makeText(NewOrderDetailActivity.this,
                                    R.string.msg_success_reject_order,
                                    Toast.LENGTH_SHORT)
                                    .show();
                            JSONObject dataObject = resultObject.optJSONObject("data");
                            if (dataObject != null) {
                                order = new NewOrder(dataObject);
                                setOrderDetail();
                                EventBus.getDefault()
                                        .post(new MessageEvent(7, order));
                            }
                        } else {
                            String msg = JSONUtil.getString(resultObject.optJSONObject("status"),
                                    "msg");
                            Toast.makeText(NewOrderDetailActivity.this, msg, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(NewOrderDetailActivity.this,
                                R.string.msg_fail_to_reject_order,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(NewOrderDetailActivity.this,
                            R.string.msg_fail_to_reject_order,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onRequestFailed(Object obj) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(NewOrderDetailActivity.this,
                        R.string.msg_fail_to_reject_order,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.REJECT_NEW_ORDER), jsonObject.toString());
    }

    private class GetRejectReasonTask extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.getAbsUrl(Constants.HttpPath.GET_REJECT_REASON);
            try {
                String json = JSONUtil.getStringFromUrl(NewOrderDetailActivity.this, url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressBar.setVisibility(View.GONE);
            if (jsonObject != null) {
                if (jsonObject.optJSONObject("status") != null && jsonObject.optJSONObject("status")
                        .optInt("RetCode", -1) == 0) {
                    // 获取成功
                    JSONArray dataArray = jsonObject.optJSONArray("data");
                    if (dataArray != null && dataArray.length() > 0) {
                        for (int i = 0; i < dataArray.length(); i++) {
                            Label reason = new Label(null);
                            reason.setId(dataArray.optJSONObject(i)
                                    .optLong("id", 0));
                            reason.setName(dataArray.optJSONObject(i)
                                    .optString("name", ""));
                            reasons.add(reason);
                        }

                        // 显示弹窗选择
                        showRejectReasons();
                    } else {
                        Toast.makeText(NewOrderDetailActivity.this,
                                getString(R.string.msg_error_server),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    // 获取失败
                    Toast.makeText(NewOrderDetailActivity.this,
                            getString(R.string.msg_error_server),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(NewOrderDetailActivity.this,
                        getString(R.string.msg_error_server),
                        Toast.LENGTH_SHORT)
                        .show();
            }

            super.onPostExecute(jsonObject);
        }
    }

    private class RejectReasonAdapter extends BaseAdapter {

        private ArrayList<Label> mData;
        private Context mContext;

        public RejectReasonAdapter(ArrayList<Label> mData, Context mContext) {
            this.mData = mData;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData == null ? null : mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mData == null ? 0 : mData.get(position)
                    .getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.reject_reason_item_view, parent, false);
                ViewHolder holder = new ViewHolder();
                holder.ctv = (CheckedTextView) convertView.findViewById(R.id.ctv_amount);
                convertView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder != null) {
                Label reason = mData.get(position);
                holder.ctv.setText(reason.getName());
            }

            return convertView;
        }

        private class ViewHolder {
            CheckedTextView ctv;
        }
    }

    private class GetOrderDetailTask extends AsyncTask<String, Integer, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            String url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(NewOrderDetailActivity.this, url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                Log.d(TAG, "Order Detail: " + json);
                return new JSONObject(json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressBar.setVisibility(View.GONE);
            if (jsonObject != null) {
                if (jsonObject.optJSONObject("status") != null && jsonObject.optJSONObject("status")
                        .optInt("RetCode", -1) == 0) {
                    order = new NewOrder(jsonObject.optJSONObject("data"));
                    contentLayout.setVisibility(View.VISIBLE);
                    setOrderDetail();
                } else {
                    Toast.makeText(NewOrderDetailActivity.this,
                            getString(R.string.msg_error_server),
                            Toast.LENGTH_SHORT)
                            .show();

                }
            } else {
                Toast.makeText(NewOrderDetailActivity.this,
                        getString(R.string.msg_error_server),
                        Toast.LENGTH_SHORT)
                        .show();
            }
            super.onPostExecute(jsonObject);
        }
    }

    private void statusInfoTimeDown(int strId) {
        long millisInFuture = (order.getExpiredAt() == null ? 0 : order.getExpiredAt()
                .getMillis() - HljTimeUtils.getServerCurrentTimeMillis());
        statusInfo.setText(getString(strId, millisFormat(millisInFuture)));
        if (millisInFuture >= 0) {
            handler.postDelayed(timeDownRun, 1000);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            if (order != null) {
                orderId = order.getId();
            }
            new GetOrderDetailTask().execute(String.format(Constants.getAbsUrl(Constants.HttpPath
                            .GET_NEW_ORDER_DETAIL),
                    orderId));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.UPLOAD_PROTOCOL_PHOTOS:
                    // 刷新订单信息
                    new GetOrderDetailTask().execute(String.format(Constants.getAbsUrl(Constants
                                    .HttpPath.GET_NEW_ORDER_DETAIL),
                            orderId));
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        if (order != null) {
            if (order.getStatus() == 10) {
                statusInfoTimeDown(R.string.label_pay_time_down);
            } else if (order.getStatus() == 11 && order.isFinished()) {
                statusInfoTimeDown(R.string.label_user_confirm_time_down);
            }
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(timeDownRun);
        super.onPause();
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(confirmSub, changePricesSub);
        super.onFinish();
    }

    private String millisFormat(long millisTime) {
        int days = (int) (millisTime / (1000 * 60 * 60 * 24));
        return (days > 0 ? getString(R.string.label_day,
                days) : "") + TimeUtil.countDownMillisFormat(this, millisTime);
    }

    static class ImageViewHolder {
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.btn_delete)
        ImageButton btnDelete;

        ImageViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class PayHistoryViewHolder {
        @BindView(R.id.tv_pay_history_label)
        TextView tvPayHistoryLabel;
        @BindView(R.id.tv_pay_history_money)
        TextView tvPayHistoryMoney;
        @BindView(R.id.tv_pay_history_time)
        TextView tvPayHistoryTime;

        PayHistoryViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
