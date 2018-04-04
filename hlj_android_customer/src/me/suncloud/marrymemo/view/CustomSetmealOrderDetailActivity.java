package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ContactsAdapter;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.CustomSetmealOrder;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.OrderPayHistory;
import me.suncloud.marrymemo.model.Photo;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.StatusHttpPutTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.CustomerSupportUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.widget.HorizontalListView;

public class CustomSetmealOrderDetailActivity extends HljBaseActivity implements
        PullToRefreshScrollView.OnRefreshListener {

    @BindView(R.id.pay_history_items_view)
    LinearLayout payHistoryItemsView;
    @BindView(R.id.scroll_view)
    PullToRefreshScrollView scrollView;
    @BindView(R.id.tv_real_pay_label)
    TextView tvRealPayLabel;
    @BindView(R.id.tv_refund_status)
    TextView tvRefundStatus;
    @BindView(R.id.tv_refund_money)
    TextView tvRefundMoney;
    @BindView(R.id.tv_earnest)
    TextView tvEarnest;
    @BindView(R.id.tv_gift)
    TextView tvGift;
    @BindView(R.id.privilege_layout)
    LinearLayout privilegeLayout;
    private boolean isLoad;
    @BindView(R.id.tv_pay_label)
    TextView tvPayLabel;
    @BindView(R.id.tv_final_total_price)
    TextView tvFinalTotalPrice;
    @BindView(R.id.tv_price_extra_info)
    TextView tvPriceExtraInfo;
    @BindView(R.id.btn_order_action)
    Button btnOrderAction;
    @BindView(R.id.order_action_layout)
    LinearLayout orderActionLayout;
    @BindView(R.id.btn_order_action2)
    Button btnOrderAction2;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.tv_alert_title)
    TextView tvAlertTitle;
    @BindView(R.id.tv_alert_detail)
    TextView tvAlertDetail;
    @BindView(R.id.order_alert_layout)
    LinearLayout orderAlertLayout;
    @BindView(R.id.img_pic1)
    ImageView imgPic1;
    @BindView(R.id.img_pic2)
    ImageView imgPic2;
    @BindView(R.id.img_pic3)
    ImageView imgPic3;
    @BindView(R.id.img_pic4)
    ImageView imgPic4;
    @BindView(R.id.img_pic5)
    ImageView imgPic5;
    @BindView(R.id.img_pic6)
    ImageView imgPic6;
    @BindView(R.id.tv_merchant_additional_info)
    TextView tvMerchantAdditionalInfo;
    @BindView(R.id.tv_serve_time)
    TextView tvServeTime;
    @BindView(R.id.serve_time_layout)
    LinearLayout serveTimeLayout;
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.merchant_layout)
    LinearLayout merchantLayout;
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.items_layout)
    LinearLayout itemsLayout;
    @BindView(R.id.setmeal_detail_layout)
    LinearLayout setmealDetailLayout;
    @BindView(R.id.setmeal_price_layout)
    LinearLayout setmealPriceLayout;
    @BindView(R.id.red_packet_layout)
    LinearLayout redPacketLayout;
    @BindView(R.id.real_pay_layout)
    LinearLayout realPayLayout;
    @BindView(R.id.pay_history_layout)
    LinearLayout payHistoryLayout;
    @BindView(R.id.tv_total_price_label)
    TextView tvTotalPriceLabel;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.total_price_layout)
    LinearLayout totalPriceLayout;
    @BindView(R.id.prices_layout)
    LinearLayout pricesLayout;
    @BindView(R.id.user_icon)
    RoundedImageView userIcon;
    @BindView(R.id.tv_user_nick)
    TextView tvUserNick;
    @BindView(R.id.user_info_layout)
    LinearLayout userInfoLayout;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.comment_imgs_list)
    HorizontalListView commentImgsList;
    @BindView(R.id.comment_content_layout)
    RelativeLayout commentContentLayout;
    @BindView(R.id.comment_layout)
    LinearLayout commentLayout;
    @BindView(R.id.allow_deposit_layout)
    LinearLayout allowDepositLayout;
    @BindView(R.id.btn_chat)
    Button btnChat;
    @BindView(R.id.btn_call)
    Button btnCall;
    @BindView(R.id.actions_layout)
    LinearLayout actionsLayout;
    @BindView(R.id.tv_order_no)
    TextView tvOrderNo;
    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;
    @BindView(R.id.btn_contact_service)
    Button btnContactService;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.protocol_pics_layout)
    LinearLayout protocolPicsLayout;
    @BindView(R.id.merchant_message_layout)
    LinearLayout merchantMessageLayout;
    @BindView(R.id.tv_setmeal_price)
    TextView tvSetmealPrice;
    @BindView(R.id.tv_red_packet_money)
    TextView tvRedPacketMoney;
    @BindView(R.id.tv_real_pay_price)
    TextView tvRealPayPrice;
    private long id;
    private CustomSetmealOrder order;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat dateFormat2;
    private Dialog cancelOrderDlg;
    private Dialog contactDialog;
    private List<ImageView> protocolImgs;
    private int imgWidth;
    private Dialog confirmDlg;
    private ObjectBindAdapter<Photo> commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_setmeal_order_detail);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        Point point = JSONUtil.getDeviceSize(this);
        imgWidth = (int) ((point.x - 20 * dm.density * 4) / 3);

        ButterKnife.bind(this);

        scrollView.setOnRefreshListener(this);
        id = getIntent().getLongExtra("id", 0);
        order = (CustomSetmealOrder) getIntent().getSerializableExtra("order");
        dateFormat = new SimpleDateFormat(getString(R.string.format_date_type10));
        dateFormat2 = new SimpleDateFormat(getString(R.string.format_date_type8));

        protocolImgs = new ArrayList<>();
        protocolImgs.add(imgPic1);
        protocolImgs.add(imgPic2);
        protocolImgs.add(imgPic3);
        protocolImgs.add(imgPic4);
        protocolImgs.add(imgPic5);
        protocolImgs.add(imgPic6);

        if (order != null) {
            setOrderDetail();
            id = order.getId();
        }

        progressBar.setVisibility(View.VISIBLE);
        new GetOrderDetailTask().executeOnExecutor(Constants.INFOTHEADPOOL);
    }


    /**
     * 设置订单详情到界面
     */
    private void setOrderDetail() {
        if (order != null) {
            contentLayout.setVisibility(View.VISIBLE);
            setActivityTitle();
            setOkButton();
            setRefundStatus();
            setAlertLayout();
            setProtocolsAndWeddingTime();
            setSetmealDetail();
            setPrices();
            setActionsLayout();
            //            setComment();
            setPrivileges();
            setOrderingInfo();
            setBottom();
        } else {
            // 订单详情获取错误处理
            contentLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 除了等待付款的时候有专有的title,其他都是使用订单状态作为activity的标题
     */
    private void setActivityTitle() {
        if (order.getStatus() == 11) {
            setTitle(R.string.label_buyer_paying_status);
        }
    }

    /**
     * 取消订单和退款的按钮
     */
    private void setOkButton() {
        if (order.getStatus() == 10 || order.getStatus() == 11) {
            // 可以取消订单
            showOkText();
            setOkText(R.string.label_cancel_order);
        } else if (order.getStatus() == 87) {
            showOkText();
            setOkText(R.string.label_refund);
        } else if (order.getStatus() == 20) {
            // 审核中可以撤销退款
            showOkText();
            setOkText(R.string.label_cancel_refund2);
        } else {
            hideOkText();
        }
    }

    /**
     * 设置退款详情
     */
    private void setRefundStatus() {
        tvRefundStatus.setVisibility(View.GONE);
        if (((order.getStatus() == 20 || order.getStatus() == 24) && order.getCsoRefundInfo() !=
                null)) {
            // 正常情况下,订单详情里面只有在退款中和退款成功才显示退款信息
            tvRefundStatus.setText(order.getCsoRefundInfo()
                    .getStatusStr());
            tvRefundStatus.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置状态alert view
     */
    private void setAlertLayout() {
        if (order.getStatus() == 10) {
            // 等待商家接单
            orderAlertLayout.setVisibility(View.VISIBLE);
            tvAlertTitle.setVisibility(View.VISIBLE);
            tvAlertDetail.setVisibility(View.VISIBLE);
            tvAlertTitle.setText(order.getStatusStr());
            // 显示自动关闭剩余时间时
            Calendar calendar = Calendar.getInstance();
            if (order.getExpiredAt()
                    .toDate()
                    .after(calendar.getTime())) {
                // 还未超时
                String leftTimeStr = getLeftTimeStr(calendar);
                tvAlertDetail.setText(Html.fromHtml(getString(R.string.label_order_auto_close3,
                        leftTimeStr)));
            } else {
                // 已经超时,但状态有错误,需要重新刷新列表
                progressBar.setVisibility(View.VISIBLE);
                new GetOrderDetailTask().executeOnExecutor(Constants.INFOTHEADPOOL);
            }
        } else if (order.getStatus() == 11) {
            orderAlertLayout.setVisibility(View.VISIBLE);
            tvAlertTitle.setVisibility(View.VISIBLE);
            tvAlertDetail.setVisibility(View.VISIBLE);
            tvAlertTitle.setText(R.string.label_custom_order_alert);
            // 显示自动关闭剩余时间时
            Calendar calendar = Calendar.getInstance();
            if (order.getExpiredAt()
                    .toDate()
                    .after(calendar.getTime())) {
                // 还未超时
                String leftTimeStr = getLeftTimeStr(calendar);
                tvAlertDetail.setText(Html.fromHtml(getString(R.string.label_order_auto_close3,
                        leftTimeStr)));
            } else {
                // 已经超时,但状态有错误,需要重新刷新列表
                progressBar.setVisibility(View.VISIBLE);
                new GetOrderDetailTask().executeOnExecutor(Constants.INFOTHEADPOOL);
            }
        } else if (order.getStatus() == 91 || order.getStatus() == 93) {
            // 用户取消订单
            orderAlertLayout.setVisibility(View.VISIBLE);
            tvAlertTitle.setVisibility(View.VISIBLE);
            tvAlertDetail.setVisibility(View.GONE);
            tvAlertTitle.setText(order.getReasonName());
        } else if (order.getStatus() == 87) {
            // 已付款
            if (order.isPayAll()) {
                orderAlertLayout.setVisibility(View.VISIBLE);
                tvAlertTitle.setVisibility(View.VISIBLE);
                tvAlertDetail.setVisibility(View.VISIBLE);
                tvAlertTitle.setText(R.string.label_confirm_custom_order);
                tvAlertDetail.setText(R.string.label_auto_confirm_time_alert2);
            } else {
                // 待付余款
                orderAlertLayout.setVisibility(View.VISIBLE);
                tvAlertTitle.setVisibility(View.VISIBLE);
                tvAlertDetail.setVisibility(View.VISIBLE);
                tvAlertTitle.setText(R.string.label_need_pay_rest2);
                tvAlertDetail.setText(R.string.msg_pay_rest_alert);
            }
        } else if (order.getStatus() == 90 || order.getStatus() == 92) {
            // 交易完成
            orderAlertLayout.setVisibility(View.VISIBLE);
            tvAlertTitle.setVisibility(View.VISIBLE);
            tvAlertDetail.setVisibility(View.GONE);
            tvAlertTitle.setText(order.getStatusStr());
        } else if (order.getStatus() == 20 || order.getStatus() == 21 || order.getStatus() == 23
                || order.getStatus() == 24) {
            // 退款
            orderAlertLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 协议图片设置和监听设置
     */
    private void setProtocolsAndWeddingTime() {
        if (order.getProtocolPhotos() != null && order.getProtocolPhotos()
                .size() > 0) {
            protocolPicsLayout.setVisibility(View.VISIBLE);
            for (int i = 0; i < order.getProtocolPhotos()
                    .size() && i < 6; i++) {
                ImageView imageView = protocolImgs.get(i);
                imageView.setVisibility(View.VISIBLE);
                imageView.getLayoutParams().width = imgWidth;
                imageView.getLayoutParams().height = imgWidth;
                String imgUrl = JSONUtil.getImagePath(order.getProtocolPhotos()
                        .get(i)
                        .getImagePath(), imgWidth);
                if (!JSONUtil.isEmpty(imgUrl)) {
                    ImageLoadTask task = new ImageLoadTask(imageView);
                    imageView.setTag(imgUrl);
                    task.loadImage(imgUrl,
                            imgWidth,
                            ScaleMode.ALL,
                            new AsyncBitmapDrawable(getResources(),
                                    R.mipmap.icon_empty_image,
                                    task));
                } else {
                    imageView.setImageBitmap(null);
                }
                imageView.setOnClickListener(new OnProtocolPhotoClickListener(i));
            }
        } else {
            protocolPicsLayout.setVisibility(View.GONE);
        }

        // 商家补充信息
        if (!JSONUtil.isEmpty(order.getMessage())) {
            merchantMessageLayout.setVisibility(View.VISIBLE);
            tvMerchantAdditionalInfo.setText(order.getMessage());
        } else {
            merchantMessageLayout.setVisibility(View.GONE);
        }

        // 服务时间
        if (order.getWeddingTime() != null && order.getStatus() != 10) {
            serveTimeLayout.setVisibility(View.VISIBLE);
            tvServeTime.setText(dateFormat2.format(order.getWeddingTime()
                    .toDate()));
        } else {
            serveTimeLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 套餐简介和商家名称
     */
    private void setSetmealDetail() {
        tvMerchantName.setText(order.getCustomSetmeal()
                .getMerchant()
                .getName());
        tvPrice.setText(getString(R.string.label_price4,
                Util.roundDownDouble2StringPositive(order.getActualPrice())));
        tvTitle.setText(order.getCustomSetmeal()
                .getTitle());
        String coverUrl = JSONUtil.getImagePath(order.getCustomSetmeal()
                .getImgCover(), imgCover.getLayoutParams().width);
        if (!JSONUtil.isEmpty(coverUrl)) {
            ImageLoadTask task = new ImageLoadTask(imgCover);
            imgCover.setTag(coverUrl);
            task.loadImage(coverUrl,
                    imgCover.getLayoutParams().width,
                    ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
        } else {
            imgCover.setImageBitmap(null);
        }

        if (order.getStatus() == 24) {
            // 退款成功显示退款金额
            tvRefundMoney.setText(getString(R.string.label_refunded_money4,
                    Util.roundDownDouble2StringPositive(order.getCsoRefundInfo()
                            .getPayMoney())));
            tvRefundMoney.setVisibility(View.VISIBLE);
        } else {
            tvRefundMoney.setVisibility(View.GONE);
        }
    }

    /**
     * 所有有关价格计算和显示的处理
     */
    private void setPrices() {
        if (order.getStatus() == 11 || ((order.getStatus() == 91 || order.getStatus() == 93) &&
                order.getProtocolPhotos() != null && !order.getProtocolPhotos()
                .isEmpty())) {
            // 第一种情况,状态是11的时候,等待买家付款,显示价格
            // 第二种情况,订单被取消并且订单具有商家的接单信息的情况下,也要显示价格 ((order.getStatusObb() == 91 || order
            // .getStatusObb()
            // == 93) && !order.getProtocolPhotos().isEmpty())

            pricesLayout.setVisibility(View.VISIBLE);
            // 只显示实付金额和可支付定金数额两个价格
            setmealPriceLayout.setVisibility(View.GONE);
            redPacketLayout.setVisibility(View.GONE);
            realPayLayout.setVisibility(View.VISIBLE);
            payHistoryLayout.setVisibility(View.GONE);
            totalPriceLayout.setVisibility(View.GONE);

            // 实付金额价格显示红色
            tvRealPayLabel.setText(R.string.label_total_price4);
            tvRealPayPrice.setTextColor(getResources().getColor(R.color.colorPrimary));
            tvRealPayPrice.setText(getString(R.string.label_price,
                    Util.roundDownDouble2StringPositive(order.getActualPrice())));

            // 显示价格的时候不显示定金信息和联系按钮
            actionsLayout.setVisibility(View.GONE);
            allowDepositLayout.setVisibility(View.GONE);

            // 待付款,但有绑定红包,这里的界面与已付款类似
            if (order.getRedPacketMoney() > 0) {
                setmealPriceLayout.setVisibility(View.VISIBLE);
                realPayLayout.setVisibility(View.GONE);
                totalPriceLayout.setVisibility(View.VISIBLE);

                // 实付金额显示黑色
                tvSetmealPrice.setText(getString(R.string.label_price,
                        Util.roundDownDouble2StringPositive(order.getActualPrice())));
                // 总金额要减去红包优惠
                tvTotalPriceLabel.setText(R.string.label_total_price4);
                tvTotalPrice.setText(Util.roundDownDouble2StringPositive(order.getActualPrice() -
                        order.getRedPacketMoney()));

                if (order.getRedPacketMoney() > 0) {
                    redPacketLayout.setVisibility(View.VISIBLE);
                    tvRedPacketMoney.setText(getString(R.string.label_price7,
                            Util.roundDownDouble2StringPositive(order.getRedPacketMoney())));
                } else {
                    redPacketLayout.setVisibility(View.GONE);
                }
            }
        } else if (order.getStatus() == 87 || order.getStatus() == 90 || order.getStatus() == 92
                || order.getStatus() == 20 || order.getStatus() == 24) {
            // 已付款
            // 即时在退款审核中或成功状态下,一样显示付款信息
            pricesLayout.setVisibility(View.VISIBLE);

            setmealPriceLayout.setVisibility(View.VISIBLE);
            realPayLayout.setVisibility(View.VISIBLE);
            payHistoryLayout.setVisibility(View.VISIBLE);
            totalPriceLayout.setVisibility(View.VISIBLE);

            // 实付金额显示黑色
            tvSetmealPrice.setText(getString(R.string.label_price,
                    Util.roundDownDouble2StringPositive(order.getActualPrice())));
            if (order.getRedPacketMoney() > 0) {
                redPacketLayout.setVisibility(View.VISIBLE);
                tvRedPacketMoney.setText(getString(R.string.label_price7,
                        Util.roundDownDouble2StringPositive(order.getRedPacketMoney())));
            } else {
                redPacketLayout.setVisibility(View.GONE);
            }
            tvRealPayLabel.setText(R.string.label_real_pay);
            tvRealPayPrice.setTextColor(getResources().getColor(R.color.colorBlack2));
            tvRealPayPrice.setText(getString(R.string.label_price,
                    Util.roundDownDouble2StringPositive(order.getPaidMoney())));

            // 付款记录
            if (order.getPaidMoney() > 0 && !order.getPayHistories()
                    .isEmpty()) {
                payHistoryLayout.setVisibility(View.VISIBLE);
                payHistoryItemsView.removeAllViews();
                for (int i = order.getPayHistories()
                        .size() - 1; i >= 0; i--) {
                    OrderPayHistory payHistory = order.getPayHistories()
                            .get(i);
                    View itemView = getLayoutInflater().inflate(R.layout.pay_history_item_layout,
                            null);
                    TextView tvDate = (TextView) itemView.findViewById(R.id.tv_history_pay_date);
                    TextView tvMoney = (TextView) itemView.findViewById(R.id.tv_history_money);

                    tvDate.setText(dateFormat.format(payHistory.getCreatedAt()
                            .toDate()));
                    tvMoney.setText(getString(R.string.label_price,
                            Util.roundDownDouble2StringPositive(payHistory.getMoney())));

                    payHistoryItemsView.addView(itemView);
                }
            } else {
                payHistoryLayout.setVisibility(View.GONE);
            }

            if (order.isPayAll()) {
                // 付完全款
                tvTotalPriceLabel.setText(R.string.label_real_pay);
                tvTotalPrice.setText(Util.roundDownDouble2StringPositive(order.getPaidMoney()));
                actionsLayout.setVisibility(View.GONE);
                realPayLayout.setVisibility(View.GONE);
            } else {
                // 待付余款
                tvTotalPriceLabel.setText(R.string.label_need_pay_rest);
                tvTotalPrice.setText(Util.roundDownDouble2StringPositive(order.getActualPrice() -
                        order.getRedPacketMoney() - order.getPaidMoney()));
                actionsLayout.setVisibility(View.VISIBLE);
            }

            // 显示价格的时候不显示定金信息和联系按钮
            allowDepositLayout.setVisibility(View.GONE);
        } else {
            if (order.getStatus() == 10) {
                allowDepositLayout.setVisibility(View.GONE);
            }
            actionsLayout.setVisibility(View.VISIBLE);
            pricesLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 联系商家的两个按钮的显示
     */
    private void setActionsLayout() {
        if (order.getStatus() == 10) {
            actionsLayout.setVisibility(View.VISIBLE);
        } else {
            actionsLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 显示此用户针对该订单的评价
     */
    private void setComment() {
        if (order.getStatus() == 92 && order.getComment() != null) {
            commentLayout.setVisibility(View.VISIBLE);
            String iconUrl = JSONUtil.getImagePath(order.getComment()
                    .getUser()
                    .getAvatar(), userIcon.getLayoutParams().width);
            if (!JSONUtil.isEmpty(iconUrl)) {
                ImageLoadTask task = new ImageLoadTask(userIcon, 0);
                imgCover.setTag(iconUrl);
                task.loadImage(iconUrl,
                        userIcon.getLayoutParams().width,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
            }
            tvUserNick.setText(order.getComment()
                    .getUser()
                    .getName());
            content.setText(order.getComment()
                    .getContent());
            if (order.getComment()
                    .getPhotos() != null && order.getComment()
                    .getPhotos()
                    .size() > 0) {
                commentImgsList.setVisibility(View.VISIBLE);
                commentAdapter = new ObjectBindAdapter<>(this,
                        order.getComment()
                                .getPhotos(),
                        R.layout.special_topic_item_view,
                        commentViewBinder);
                commentImgsList.setAdapter(commentAdapter);
                commentImgsList.setOnItemClickListener(commentListener);
                commentImgsList.getLayoutParams().height = imgWidth;
                commentImgsList.setParentView(scrollView.getRefreshableView());
            } else {
                commentImgsList.setVisibility(View.GONE);
            }
        } else {
            commentLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 显示订单礼
     */
    private void setPrivileges() {
        if (!JSONUtil.isEmpty(order.getCustomSetmeal()
                .getOrderGift()) || order.isEarnest()) {
            privilegeLayout.setVisibility(View.VISIBLE);

            // 定金
            if (order.isEarnest()) {
                tvEarnest.setVisibility(View.VISIBLE);
                tvEarnest.setText("• " + getString(R.string.label_earnest,
                        Util.formatDouble2String(order.getEarnestMoney())));
            } else {
                tvEarnest.setVisibility(View.GONE);
            }

            // 订单礼品
            if (!JSONUtil.isEmpty(order.getCustomSetmeal()
                    .getOrderGift())) {
                tvGift.setVisibility(View.VISIBLE);
                tvGift.setText("• " + getString(R.string.label_gift,
                        order.getCustomSetmeal()
                                .getOrderGift()));
            } else {
                tvGift.setVisibility(View.GONE);
            }
        } else {
            privilegeLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 订单概述
     */
    private void setOrderingInfo() {
        tvOrderNo.setText(order.getOrderNo());
        if (order.getCreatedAt() != null) {
            tvOrderTime.setText(dateFormat.format(order.getCreatedAt()
                    .toDate()));
        }
        btnContactService.setVisibility(View.VISIBLE);
    }

    /**
     * 底部操作栏的状态样式
     */
    private void setBottom() {
        if (order.getStatus() == 10) {
            bottomLayout.setVisibility(View.GONE);
        } else if (order.getStatus() == 11) {
            // 等待买家付款
            bottomLayout.setVisibility(View.VISIBLE);
            tvPayLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
            tvPayLabel.setText(R.string.label_total_price4);
            tvFinalTotalPrice.setText(Util.roundDownDouble2StringPositive(order.getActualPrice()
                    - order.getRedPacketMoney()));
            if (order.isEarnest()) {
                tvPriceExtraInfo.setVisibility(View.VISIBLE);
                tvPriceExtraInfo.setText(getString(R.string.label_allow_deposit5,
                        Util.roundDownDouble2StringPositive(order.getEarnestMoney())));
            } else {
                tvPriceExtraInfo.setVisibility(View.GONE);
            }
            btnOrderAction.setText(R.string.label_go_pay);
        } else if (order.getStatus() == 87 && !order.isPayAll()) {
            // 待付余款
            bottomLayout.setVisibility(View.VISIBLE);
            tvPayLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
            tvPayLabel.setText(R.string.label_rest_to_pay3);
            tvFinalTotalPrice.setText(Util.roundDownDouble2StringPositive(order.getActualPrice()
                    - order.getRedPacketMoney() - order.getPaidMoney()));
            tvPriceExtraInfo.setVisibility(View.GONE);
            btnOrderAction.setText(R.string.label_go_pay);
        } else if (order.getStatus() == 87 && order.isPayAll()) {
            // 付完全款
            bottomLayout.setVisibility(View.VISIBLE);
            orderActionLayout.setVisibility(View.GONE);
            btnOrderAction2.setVisibility(View.VISIBLE);
            btnOrderAction2.setText(R.string.label_confirm_service);
        } else if (order.getStatus() == 90) {
            // 交易完成,待评价
            bottomLayout.setVisibility(View.VISIBLE);
            orderActionLayout.setVisibility(View.GONE);
            btnOrderAction2.setVisibility(View.VISIBLE);
            btnOrderAction2.setText(R.string.label_comment_now);
        } else if (order.getStatus() == 92) {
            bottomLayout.setVisibility(View.GONE);
        } else {
            bottomLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onOkButtonClick() {
        if (order.getStatus() == 10 || order.getStatus() == 11) {
            // 取消订单
            cancelOrder();
        } else if (order.getStatus() == 87) {
            onRefund();
        } else if (order.getStatus() == 20) {
            withdrawRefundApply();
        }
        super.onOkButtonClick();
    }

    @OnClick(R.id.merchant_layout)
    void onMerchant() {
        Intent intent = new Intent(this, MerchantDetailActivity.class);
        intent.putExtra("id",
                order.getCustomSetmeal()
                        .getMerchant()
                        .getId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.btn_order_action)
    void onActionBtn() {
        if (order.getStatus() == 11) {
            // 去付款
            Intent intent = new Intent(this, CustomSetmealOrderPaymentActivity.class);
            intent.putExtra("order", (Serializable) order);
            intent.putExtra("is_pay_rest", false);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else if (order.getStatus() == 87 && !order.isPayAll()) {
            // 付余款
            Intent intent = new Intent(this, CustomSetmealOrderPaymentActivity.class);
            intent.putExtra("order", (Serializable) order);
            intent.putExtra("is_pay_rest", true);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @OnClick(R.id.btn_order_action2)
    void onAction2Btn() {
        if (order.getStatus() == 87 && order.isPayAll()) {
            // 已付完全款,确认服务
            confirmService();
        } else if (order.getStatus() == 90) {
            Intent intent = new Intent(this, CommentNewWorkActivity.class);
            intent.putExtra("is_custom_order", true);
            intent.putExtra("custom_order", (Serializable) order);
            intent.putExtra("commentType", Constants.ENTRY_ITEM.WORK);
            startActivityForResult(intent, Constants.RequestCode.ORDER_COMMENT);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    private void onRefund() {
        // 发起退款申请
        Intent intent = new Intent(this, RefundApplyActivity.class);
        intent.putExtra("is_custom_order", true);
        intent.putExtra("order_num", String.valueOf(order.getId()));
        startActivityForResult(intent, Constants.RequestCode.REFUND_APPLY);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    private void withdrawRefundApply() {
        if (order == null || (confirmDlg != null && confirmDlg.isShowing())) {
            return;
        }

        confirmDlg = new Dialog(this, R.style.BubbleDialogTheme);
        View v = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
        TextView msgAlertTv = (TextView) v.findViewById(R.id.tv_alert_msg);
        final Button confirmBtn = (Button) v.findViewById(R.id.btn_confirm);
        Button cancelBtn = (Button) v.findViewById(R.id.btn_cancel);
        msgAlertTv.setText(R.string.msg_confirm_withdraw_refund_apply);
        confirmBtn.setText(R.string.label_confirm_withdraw);
        cancelBtn.setText(R.string.label_wrong_action);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDlg.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                postCancelRefund();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDlg.dismiss();
            }
        });
        confirmDlg.setContentView(v);
        Window window = confirmDlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(this);
        params.width = Math.round(point.x * 27 / 32);
        window.setAttributes(params);
        confirmDlg.show();
    }

    private void postCancelRefund() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("refund_id",
                    order.getCsoRefundInfo()
                            .getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new StatusHttpPutTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                progressBar.setVisibility(View.GONE);
                JSONObject orderObject = (JSONObject) object;
                if (orderObject != null) {
                    order = new CustomSetmealOrder(orderObject);
                    // 取消退款申请,发送刷新订单列表的消息
                    EventBus.getDefault()
                            .post(new MessageEvent(MessageEvent.EventType
                                    .CUSTOM_SETMEAL_ORDER_REFRESH_WITH_OBJECT,
                                    order));
                    setOrderDetail();
                }
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                Util.postFailToast(CustomSetmealOrderDetailActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_cancel_refund,
                        network);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.CUSTOM_SETMEAL_ORDER_CANCEL_REFUND),
                jsonObject.toString());
    }

    private void confirmService() {
        if (order == null || (confirmDlg != null && confirmDlg.isShowing())) {
            return;
        }

        confirmDlg = new Dialog(this, R.style.BubbleDialogTheme);
        View v = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
        TextView msgAlertTv = (TextView) v.findViewById(R.id.tv_alert_msg);
        final Button confirmBtn = (Button) v.findViewById(R.id.btn_confirm);
        Button cancelBtn = (Button) v.findViewById(R.id.btn_cancel);
        msgAlertTv.setText(R.string.msg_confirm_service);
        confirmBtn.setText(R.string.label_confirm_service);
        cancelBtn.setText(R.string.label_wrong_action);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDlg.dismiss();
                // 确认服务
                progressBar.setVisibility(View.VISIBLE);
                postConfirmService();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDlg.dismiss();
            }
        });
        confirmDlg.setContentView(v);
        Window window = confirmDlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(this);
        params.width = Math.round(point.x * 27 / 32);
        window.setAttributes(params);
        confirmDlg.show();
    }

    private void postConfirmService() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_id", order.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new StatusHttpPutTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                progressBar.setVisibility(View.GONE);
                JSONObject orderObject = (JSONObject) object;
                if (orderObject != null) {
                    order = new CustomSetmealOrder(orderObject);
                    // 确认服务,发送刷新订单列表的消息
                    EventBus.getDefault()
                            .post(new MessageEvent(MessageEvent.EventType
                                    .CUSTOM_SETMEAL_ORDER_REFRESH_WITH_OBJECT,
                                    order));
                    Intent intent = new Intent(CustomSetmealOrderDetailActivity.this,
                            AfterConfirmReceiveActivity.class);
                    intent.putExtra("custom_order", (Serializable) order);
                    intent.putExtra("is_custom_order", true);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_bottom, 0);
                    finish();
                }
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                Util.postFailToast(CustomSetmealOrderDetailActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_confirm_shipping,
                        network);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.CUSTOM_SETMEAL_ORDER_CONFIRM_SERVICE),
                jsonObject.toString());
    }

    private void cancelOrder() {
        if (order == null || (cancelOrderDlg != null && cancelOrderDlg.isShowing())) {
            return;
        }
        cancelOrderDlg = new Dialog(this, R.style.BubbleDialogTheme);
        View v = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
        TextView msgAlertTv = (TextView) v.findViewById(R.id.tv_alert_msg);
        Button confirmBtn = (Button) v.findViewById(R.id.btn_confirm);
        Button cancelBtn = (Button) v.findViewById(R.id.btn_cancel);
        msgAlertTv.setText(R.string.msg_cancel_order);
        confirmBtn.setText(R.string.label_cancel_order);
        cancelBtn.setText(R.string.label_wrong_action);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 取消订单
                cancelOrderDlg.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                postCancelOrder();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrderDlg.dismiss();
            }
        });
        cancelOrderDlg.setContentView(v);
        Window window = cancelOrderDlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(this);
        params.width = Math.round(point.x * 27 / 32);
        window.setAttributes(params);
        cancelOrderDlg.show();

        super.onOkButtonClick();
    }

    private void postCancelOrder() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", order.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new StatusHttpPutTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                progressBar.setVisibility(View.GONE);
                JSONObject orderObject = (JSONObject) object;
                if (orderObject != null) {
                    order = new CustomSetmealOrder(orderObject);
                    // 支付成功,发送刷新订单列表的消息
                    EventBus.getDefault()
                            .post(new MessageEvent(MessageEvent.EventType
                                    .CUSTOM_SETMEAL_ORDER_REFRESH_WITH_OBJECT,
                                    order));
                    setOrderDetail();
                }
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                Util.postFailToast(CustomSetmealOrderDetailActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_cancel_order,
                        network);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.CUSTOM_SETMEAL_ORDER_CANCEL),
                jsonObject.toString());
    }

    private String getLeftTimeStr(Calendar calendar) {
        String leftStr;
        long minutesLeft = (order.getExpiredAt()
                .toDate()
                .getTime() - calendar.getTimeInMillis()) / (1000 * 60);
        if (minutesLeft > 60 * 24) {
            // 剩余时间大于1天
            int days = (int) (minutesLeft / (24 * 60));
            int hours = (int) ((minutesLeft - days * 24 * 60) / 60);
            leftStr = days + "天" + hours + "小时";
        } else if (minutesLeft > 60) {
            // 剩余时间大于1小时
            int hours = (int) (minutesLeft / 60);
            int minutes = (int) ((minutesLeft - hours * 60) / 60);
            leftStr = hours + "小时" + minutes + "分钟";
        } else {
            // 剩余时间显示分钟,最小一分钟
            if (minutesLeft == 0) {
                minutesLeft = 1;
            }
            leftStr = minutesLeft + "分钟";
        }

        return leftStr;
    }

    @OnClick(R.id.btn_contact_service)
    void onContactService() {
        CustomerSupportUtil.goToSupport(this, Support.SUPPORT_KIND_DEFAULT_ROBOT);
    }

    @OnClick(R.id.btn_chat)
    void onChatMerchant() {
        Intent intent = new Intent(this, WSCustomerChatActivity.class);
        MerchantUser user = new MerchantUser();
        user.setNick(order.getCustomSetmeal()
                .getMerchant()
                .getName());
        user.setId(order.getCustomSetmeal()
                .getMerchant()
                .getUserId());
        user.setAvatar(order.getCustomSetmeal()
                .getMerchant()
                .getLogoPath());
        user.setMerchantId(order.getCustomSetmeal()
                .getMerchant()
                .getId());

        intent.putExtra("user", user);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.btn_call)
    void onCallMerchant() {
        if (order.getCustomSetmeal()
                .getMerchant()
                .getContactPhone() != null && !order.getCustomSetmeal()
                .getMerchant()
                .getContactPhone()
                .isEmpty()) {
            callUp();
        } else {
            // 提示没有商家的联系电话
            Toast.makeText(CustomSetmealOrderDetailActivity.this,
                    getString(R.string.msg_no_merchant_number),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void callUp() {
        if (order.getCustomSetmeal()
                .getMerchant()
                .getContactPhone()
                .size() == 1) {
            String phone = order.getCustomSetmeal()
                    .getMerchant()
                    .getContactPhone()
                    .get(0);
            if (!JSONUtil.isEmpty(phone) && phone.trim()
                    .length() != 0) {
                try {
                    callUp(Uri.parse("tel:" + phone.trim()));
                } catch (Exception e) {
                }
            }
            return;
        }
        if (contactDialog != null && contactDialog.isShowing()) {
            return;
        }

        if (contactDialog == null) {
            contactDialog = new Dialog(this, R.style.BubbleDialogTheme);
            Point point = JSONUtil.getDeviceSize(this);
            View view = getLayoutInflater().inflate(R.layout.dialog_contact_phones, null);
            ListView listView = (ListView) view.findViewById(R.id.contact_list);
            ContactsAdapter contactsAdapter = new ContactsAdapter(this,
                    order.getCustomSetmeal()
                            .getMerchant()
                            .getContactPhone());
            listView.setAdapter(contactsAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String phone = (String) adapterView.getAdapter()
                            .getItem(i);
                    if (!JSONUtil.isEmpty(phone) && phone.trim()
                            .length() != 0) {
                        try {
                            callUp(Uri.parse("tel:" + phone.trim()));
                        } catch (Exception e) {

                        }
                    }
                }
            });
            contactDialog.setContentView(view);
            Window win = contactDialog.getWindow();
            ViewGroup.LayoutParams params = win.getAttributes();
            params.width = Math.round(point.x * 3 / 4);
            win.setGravity(Gravity.CENTER);
        }

        contactDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case Constants.RequestCode.REFUND_APPLY:
                    // 退款申请成功后刷新订单信息
                    CustomSetmealOrder customOrder = (CustomSetmealOrder) data.getSerializableExtra(
                            "custom_order");
                    if (customOrder != null) {
                        order = customOrder;
                        setOrderDetail();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (!isLoad) {
            new GetOrderDetailTask().executeOnExecutor(Constants.INFOTHEADPOOL);
        }
    }

    /**
     * 协议图片点击监听器
     */
    private class OnProtocolPhotoClickListener implements View.OnClickListener {
        private int position;

        public OnProtocolPhotoClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(CustomSetmealOrderDetailActivity
                    .this, PicsPageViewActivity.class);
            intent.putExtra("photos", order.getProtocolPhotos());
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }

    private class GetOrderDetailTask extends AsyncTask<String, Object, JSONObject> {
        public GetOrderDetailTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.getAbsUrl(String.format(Constants.HttpPath
                            .CUSTOM_SETMEAL_ORDER_DETAIL,
                    id));
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

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
            scrollView.onRefreshComplete();

            if (jsonObject != null) {
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus.getRetCode() == 0) {
                    JSONObject dataObj = jsonObject.optJSONObject("data");
                    if (dataObj != null) {
                        order = new CustomSetmealOrder(dataObj);
                    }
                }
            }

            EventBus.getDefault()
                    .post(new MessageEvent(MessageEvent.EventType
                            .CUSTOM_SETMEAL_ORDER_REFRESH_WITH_OBJECT,
                            order));
            setOrderDetail();
            isLoad = false;
            super.onPostExecute(jsonObject);
        }
    }

    private ObjectBindAdapter.ViewBinder<Photo> commentViewBinder = new ObjectBindAdapter
            .ViewBinder<Photo>() {
        @Override
        public void setViewValue(View view, Photo photo, int position) {
            CommentViewHolder holder = (CommentViewHolder) view.getTag();
            if (holder == null) {
                holder = new CommentViewHolder();
                holder.imageView = (ImageView) view.findViewById(R.id.cover);
                holder.headView = view.findViewById(R.id.head_view);
                holder.footView = view.findViewById(R.id.foot_view);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder
                        .imageView.getLayoutParams();
                params.width = imgWidth;
                params.height = imgWidth;
                view.setTag(holder);
            }

            if (position == 0) {
                holder.headView.setVisibility(View.VISIBLE);
            } else {
                holder.headView.setVisibility(View.GONE);
            }
            if (order.getComment()
                    .getPhotos()
                    .size() - 1 == position) {
                holder.footView.setVisibility(View.VISIBLE);
            } else {
                holder.footView.setVisibility(View.GONE);
            }

            String url = JSONUtil.getImagePath(photo.getPath(), imgWidth);
            if (!JSONUtil.isEmpty(url)) {
                ImageLoadTask task = new ImageLoadTask(holder.imageView, 0);
                task.loadImage(url,
                        imgWidth,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
            } else {
                holder.imageView.setImageBitmap(null);
            }
        }
    };

    private class CommentViewHolder {
        ImageView imageView;
        View headView;
        View footView;
    }

    private AdapterView.OnItemClickListener commentListener = new AdapterView.OnItemClickListener
            () {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Photo photo = (Photo) parent.getAdapter()
                    .getItem(position);
            if (photo != null) {
                Intent intent = new Intent(CustomSetmealOrderDetailActivity.this,
                        ThreadPicsPageViewActivity.class);
                intent.putExtra("photos",
                        order.getComment()
                                .getPhotos());
                intent.putExtra("position", position);
                startActivity(intent);
            }
        }
    };
}

