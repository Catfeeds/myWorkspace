package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljkefulibrary.moudles.Support;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.ExpressInfo;
import me.suncloud.marrymemo.model.ExpressMethod;
import me.suncloud.marrymemo.model.Label;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.Photo;
import me.suncloud.marrymemo.model.RefundHistory;
import me.suncloud.marrymemo.model.RefundMessage;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.ShippingAddress;
import me.suncloud.marrymemo.model.orders.ProductOrder;
import me.suncloud.marrymemo.model.orders.ProductRefundStatus;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.CustomerSupportUtil;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.ImageLoadUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.TimeUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.widget.RefundFlowWidget;
import me.suncloud.marrymemo.widget.RefundFlowWidget2;

/**
 * 婚品退款单详情,包括只退款和退款退货
 */
public class ProductRefundDetailActivity extends HljBaseActivity implements
        PullToRefreshScrollView.OnRefreshListener {

    @BindView(R.id.refund_flow_1)
    RefundFlowWidget refundFlow1;
    @BindView(R.id.refund_flow_2)
    RefundFlowWidget2 refundFlow2;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.items_layout)
    LinearLayout itemsLayout;
    @BindView(R.id.scroll_view)
    PullToRefreshScrollView scrollView;
    @BindView(R.id.btn_support)
    Button btnSupport;
    @BindView(R.id.btn_contact)
    Button btnContact;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    private long subOrderId;
    private int refundStatus;
    private int type;
    private boolean needRefresh;

    private LayoutInflater layoutInflater;
    private DisplayMetrics dm;
    private Point point;
    private SimpleDateFormat simpleDateFormat;
    private List<View> viewList;
    private Date expireTime;
    private ProductOrder order;
    private Dialog cancelRefundDlg;
    private Dialog selectExpressDialog;
    private TextView tvExpressHolder;
    private Label selectedExpress;
    private ArrayList<Label> expresses;
    private ExpireTimeCountDown expireTimeCountDown;
    private boolean isLoad;
    private boolean isFromDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_refund_detail);
        ButterKnife.bind(this);

        EventBus.getDefault()
                .register(this);

        expresses = new ArrayList<>();
        dm = getResources().getDisplayMetrics();
        point = JSONUtil.getDeviceSize(this);
        simpleDateFormat = new SimpleDateFormat(getString(R.string.format_date));

        subOrderId = getIntent().getLongExtra("id", 0);
        order = getIntent().getParcelableExtra("order");
        isFromDetail = getIntent().getBooleanExtra("from_detail", false);
        if (isFromDetail) {
            setSwipeBackEnable(true);
        } else {
            setSwipeBackEnable(false);
        }

        layoutInflater = LayoutInflater.from(this);
        scrollView.setOnRefreshListener(this);

        progressBar.setVisibility(View.VISIBLE);
        new GetRefundDetailTask().execute();
    }

    @Override
    public void onBackPressed() {
        if (isFromDetail) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(this, ProductOrderDetailActivity.class);
            intent.putExtra("id", order.getId());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_right);
            finish();
        }
    }

    private void setContent(JSONObject jsonObject) {
        if (refundStatus < ProductRefundStatus.MERCHANT_HANDLING_REFUND) {
            contentLayout.setVisibility(View.GONE);
            return;
        }
        contentLayout.setVisibility(View.VISIBLE);
        if (type == 1) {
            // 仅退款
            refundFlow1.setVisibility(View.VISIBLE);
            refundFlow2.setVisibility(View.GONE);
            refundFlow1.setCurrentStep(1);
        } else {
            // 退货/退款
            refundFlow1.setVisibility(View.GONE);
            refundFlow2.setVisibility(View.VISIBLE);
            refundFlow2.setCurrentStep(1);
        }

        itemsLayout.removeAllViews();

        setOkText(type == 1 ? R.string.label_cancel_refund3 : R.string.label_cancel_return);
        // 所有的过期时间都使用同一个地方的值
        expireTime = JSONUtil.getDateFromFormatLong(jsonObject, "expire_time", true);

        JSONArray messageArray = jsonObject.optJSONArray("message");
        JSONArray historyArray = jsonObject.optJSONArray("history");

        View topActionView = getTopActionView(jsonObject);

        boolean skipFirst = false;
        if (topActionView != null) {
            itemsLayout.addView(topActionView);
            // 有的状态下,顶部操作视图和历史记录会重合,要去掉第一个历史记录
            if (refundStatus == ProductRefundStatus.REFUND_CANCELED || refundStatus ==
                    ProductRefundStatus.REFUND_CLOSED || refundStatus == ProductRefundStatus
                    .BUYER_RETURN_PRODUCT || refundStatus == ProductRefundStatus
                    .MERCHANT_PRODUCT_UNRECEIVED || refundStatus == ProductRefundStatus
                    .REFUND_DECLINED || refundStatus == ProductRefundStatus.RETURN_DECLINED) {
                skipFirst = true;
            }
        }

        viewList = new ArrayList<>();
        // 将所有的message和history的信息的view对应的渲染出来并放入list中,view的tag为对应的创建时间
        if (messageArray != null && messageArray.length() > 0) {
            for (int i = 0; i < messageArray.length(); i++) {
                JSONObject messageObj = messageArray.optJSONObject(i);
                RefundMessage refundMessage = new RefundMessage(messageObj);
                View view = getMessageView(refundMessage);
                view.setTag(refundMessage.getCreatedAt()
                        .getTime());

                viewList.add(view);
            }
        }
        if (historyArray != null && historyArray.length() > 0) {
            int i = skipFirst ? 1 : 0;
            for (; i < historyArray.length(); i++) {
                JSONObject historyObj = historyArray.optJSONObject(i);
                RefundHistory refundHistory = new RefundHistory(historyObj);
                View view;
                switch (refundHistory.getStatus()) {
                    case ProductRefundStatus.MERCHANT_HANDLING_REFUND:
                        // 申请退款的信息
                    case ProductRefundStatus.MERCHANT_HANDLING_RETURN:
                        // 申请退货的信息
                        view = getRefundInfoView(refundHistory);
                        break;
                    case ProductRefundStatus.REFUND_DECLINED:
                        // 拒绝退款
                        view = getRefundDeclinedHistoryView(refundHistory);
                        break;
                    case ProductRefundStatus.RETURN_DECLINED:
                        // 拒绝退款
                        view = getRefundDeclinedHistoryView(refundHistory);
                        break;
                    case ProductRefundStatus.BUYER_RETURN_PRODUCT:
                        // 卖家同意退货时的信息
                        if (refundStatus != ProductRefundStatus.BUYER_RETURN_PRODUCT) {
                            view = getReturnAgreeHistoryView(refundHistory);
                        } else {
                            // 当前状态就是待退货状态的时候,不需要显示商家的收货地址信息,因为顶部操作视图里面已经包含了这些信息
                            view = null;
                        }
                        break;
                    case ProductRefundStatus.REFUND_COMPLETE:
                        if (refundHistory.getRefundType() == 1) {
                            view = getRefundAgreeHistoryView(refundHistory);
                        } else {
                            // 退款完成,系统自动确认收货或者卖家确认收货
                            view = getMerchantConfirmedView(refundHistory);
                        }
                        break;
                    case ProductRefundStatus.MERCHANT_CONFIRMING:
                        // 买家填写的物流信息
                        view = getBuyerReturnHistoryView(refundHistory);
                        break;
                    case ProductRefundStatus.REFUND_CANCELED:
                        view = getRefundCanceledHistoryView(refundHistory);
                        break;
                    case ProductRefundStatus.REFUND_CLOSED:
                        // 订单关闭的历史记录
                        view = getAutoCloseHistoryView(refundHistory);
                        break;
                    default:
                        view = null;
                        break;
                }
                if (view != null) {
                    view.setTag(refundHistory.getCreatedAt()
                            .getTime());
                    viewList.add(view);
                }
            }
        }

        // 对viewList中的数据根据创建时间重新排序
        Collections.sort(viewList, new Comparator<View>() {
            @Override
            public int compare(View lhs, View rhs) {
                long lhsT = (long) lhs.getTag();
                long rhsT = (long) rhs.getTag();
                if (lhsT > rhsT) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        for (View view : viewList) {
            itemsLayout.addView(view);
        }
    }

    /**
     * 根据不同的状态得到不同的顶部操作视图
     *
     * @param jsonObject
     * @return
     */
    private View getTopActionView(JSONObject jsonObject) {
        JSONArray historyArray = jsonObject.optJSONArray("history");
        View topActionView = null;
        // 最顶部的是一个对应状态显示的不同的view,顶部操作视图
        // 首先处理顶部操作视图,根据退款单的类型和退款单当前的状态来决定显示哪一个操作视图,每一个操作视图的数据来源也不一样
        // 有的数据来自退款单概略信息,有的则是需要从历史记录中最新的记录中获取对应当前状态的记录,其中包含的数据
        RefundHistory refundHistory;
        switch (refundStatus) {
            case ProductRefundStatus.MERCHANT_HANDLING_REFUND:
                // 等待商家处理退款
                topActionView = getMerchantHandlingView();
                refundFlow1.setCurrentStep(1);
                setTitle(R.string.label_merchant_handling2);
                showOkText();
                break;
            case ProductRefundStatus.MERCHANT_HANDLING_RETURN:
                // 等待商家处理退货
                topActionView = getMerchantHandlingView();
                refundFlow2.setCurrentStep(1);
                setTitle(R.string.label_merchant_handling2);
                showOkText();
                break;
            case ProductRefundStatus.REFUND_DECLINED:
                // 商家拒绝退款
                topActionView = getRefundDeclinedView(getDataFromHistoryByStatus(historyArray));
                refundFlow1.setCurrentStep(2);
                setTitle(R.string.label_refund_declined);
                hideOkText();
                break;
            case ProductRefundStatus.RETURN_DECLINED:
                // 商家拒绝退货
                topActionView = getRefundDeclinedView(getDataFromHistoryByStatus(historyArray));
                refundFlow2.setCurrentStep(2);
                setTitle(R.string.label_return_declined);
                showOkText();
                break;
            case ProductRefundStatus.BUYER_RETURN_PRODUCT:
                // 商家同意退货,等待买家退货
                topActionView = getReturnAgreeView(getDataFromHistoryByStatus(historyArray));
                refundFlow2.setCurrentStep(2);
                setTitle(R.string.label_merchant_agree_return);
                showOkText();
                break;
            case ProductRefundStatus.MERCHANT_CONFIRMING:
                // 用户已退货,等待商家确认收货
                topActionView = getMerchantConfirmingView();
                refundFlow2.setCurrentStep(3);
                setTitle(R.string.label_merchant_agree_return);
                hideOkText();
                break;
            case ProductRefundStatus.MERCHANT_PRODUCT_UNRECEIVED:
                // 商家确认未收到货
                topActionView = getMerchantReceivedNoReturns(getDataFromHistoryByStatus
                        (historyArray));
                refundFlow2.setCurrentStep(3);
                setTitle(R.string.label_merchant_unreceived);
                showOkText();
                break;
            case ProductRefundStatus.REFUND_COMPLETE:
                // 退款退货完成
                refundHistory = getDataFromHistoryByStatus(historyArray);
                topActionView = getRefundSuccessView(jsonObject.optJSONObject("refund"),
                        refundHistory);
                if (refundHistory.getRefundType() == 1) {
                    refundFlow1.setCurrentStep(3);
                    setTitle(R.string.label_refund_complete);
                } else {
                    refundFlow2.setCurrentStep(5);
                    setTitle(R.string.label_return_complete);
                }
                hideOkText();
                break;
            case ProductRefundStatus.REFUND_CANCELED:
                // 退款/退货取消
                refundHistory = getDataFromHistoryByStatus(historyArray);
                if (refundHistory.getRefundType() == 1) {
                    refundFlow1.setCurrentStep(0);
                    setTitle(R.string.label_refund_canceled2);
                } else {
                    refundFlow2.setCurrentStep(0);
                    setTitle(R.string.label_return_canceled2);
                }
                topActionView = getRefundCanceledView(refundHistory);
                hideOkText();
                break;
            case ProductRefundStatus.REFUND_CLOSED:
                // 自动关闭
                refundHistory = getDataFromHistoryByStatus(historyArray);
                if (refundHistory.getRefundType() == 1) {
                    refundFlow1.setCurrentStep(0);
                    setTitle(R.string.label_refund_closed2);
                } else {
                    refundFlow2.setCurrentStep(0);
                    setTitle(R.string.label_return_closed);
                }
                topActionView = getAutoClosedView(refundHistory);
                hideOkText();
                break;
            default:
                hideOkText();
                break;


        }
        return topActionView;
    }

    /**
     * 从历史记录中获取最新的那个记录,原则上就是当前状态的所需要的数据,但保险起见增加status对比判断从最新一个开始读取
     * 取与当前状态一致的最新的记录
     *
     * @return
     */
    private @Nullable
    RefundHistory getDataFromHistoryByStatus(JSONArray historyArr) {
        for (int i = 0; i < historyArr.length(); i++) {
            JSONObject jsonObject = historyArr.optJSONObject(i);
            if (jsonObject.optInt("status", 0) == refundStatus) {
                RefundHistory refundHistory = new RefundHistory(jsonObject);
                return refundHistory;
            }
        }

        return null;
    }

    /**
     * 用户已发货,等待商家确认收货,顶部视图就是一个倒计时
     *
     * @return
     */
    private View getMerchantConfirmingView() {
        View view = layoutInflater.inflate(R.layout.pr_refund_history, null);
        SimpleViewHolder holder = new SimpleViewHolder(view);
        long timeUntil = expireTime.getTime() - Calendar.getInstance()
                .getTimeInMillis();
        holder.tvRefundStatus.setText(R.string.label_merchant_confirming2);
        if (timeUntil > 0) {
            expireTimeCountDown = new ExpireTimeCountDown(timeUntil,
                    1000,
                    holder.tvContent,
                    R.string.msg_auto_confirm_received);
            expireTimeCountDown.start();
        } else {
            holder.tvContent.setText(Html.fromHtml(getString(R.string.msg_auto_confirm_received,
                    "0天00时00分")));
        }

        return view;
    }

    /**
     * 买家退货的物流信息视图
     * 不是顶部操作视图,而是作为历史记录视图
     *
     * @param refundHistory
     * @return
     */
    private View getBuyerReturnHistoryView(RefundHistory refundHistory) {
        View view = layoutInflater.inflate(R.layout.pr_buyer_shipped_away, null);
        if (refundHistory != null && refundHistory.getObjectChanges() != null) {
            final BuyerShippingViewHolder holder = new BuyerShippingViewHolder(view);
            holder.tvTime.setText(simpleDateFormat.format(refundHistory.getCreatedAt()));

            JSONObject expressObject = refundHistory.getObjectChanges()
                    .optJSONObject("express");
            ExpressInfo expressInfo = null;
            if (expressObject != null) {
                expressInfo = new ExpressInfo(expressObject);
                String expressName = JSONUtil.getString(expressObject, "type_name");
                String expressNo = JSONUtil.getString(expressObject, "tracking_no");

                holder.tvShippingCompany.setText(expressName);
                holder.tvShippingNo.setText(expressNo);
                holder.tvCopyNumber.setVisibility(TextUtils.isEmpty(expressNo) ? View.GONE : View
                        .VISIBLE);
            }
            if (refundStatus == ProductRefundStatus.MERCHANT_CONFIRMING || refundStatus ==
                    ProductRefundStatus.MERCHANT_PRODUCT_UNRECEIVED) {
                // 只有在商家待确认收货或者商家确认未收到货的状态才能修改物流信息
                holder.btnChangeShipping.setVisibility(View.VISIBLE);
                final ExpressInfo finalExpressInfo1 = expressInfo;
                holder.btnChangeShipping.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onChangeExpressInfo(finalExpressInfo1);
                    }
                });
            } else {
                holder.btnChangeShipping.setVisibility(View.GONE);
            }
            holder.tvCopyNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    cm.setPrimaryClip(ClipData.newPlainText(null, holder.tvShippingNo.getText()));
                    Toast.makeText(ProductRefundDetailActivity.this,
                            R.string.msg_copied_shipping_no,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }

        return view;
    }


    /**
     * 商家同意退货或者自动同意退货的顶部操作视图
     *
     * @param refundHistory
     * @return
     */
    private View getReturnAgreeView(RefundHistory refundHistory) {
        View view = layoutInflater.inflate(R.layout.pr_merchant_agree_return, null);
        final AgreeReturnViewHolder holder = new AgreeReturnViewHolder(view);
        if (refundHistory != null) {
            holder.tvTime.setText(simpleDateFormat.format(refundHistory.getCreatedAt()));
            // 倒计时信息
            long timeUntil = expireTime.getTime() - Calendar.getInstance()
                    .getTimeInMillis();
            if (timeUntil > 0) {
                expireTimeCountDown = new ExpireTimeCountDown(timeUntil,
                        1000,
                        holder.tvTimeHint,
                        R.string.msg_auto_cancel_return);
                expireTimeCountDown.start();
            } else {
                holder.tvTimeHint.setText(Html.fromHtml(getString(R.string.msg_auto_cancel_return,
                        "0天00时00分")));
            }

            // 是否自动确认同意
            if (refundHistory.isAutoBySystem()) {
                holder.tvAutoAgree.setVisibility(View.VISIBLE);
            } else {
                holder.tvAutoAgree.setVisibility(View.GONE);
            }

            // 填写卖家收货地址信息
            if (refundHistory.getObjectChanges() != null) {
                if (!refundHistory.getObjectChanges()
                        .isNull("address")) {
                    ShippingAddress shippingAddress = new ShippingAddress(refundHistory
                            .getObjectChanges()
                            .optJSONObject("address"));

                    holder.tvReceiverName.setText(shippingAddress.getBuyerName());
                    holder.tvReceiverPhone.setText(shippingAddress.getMobilePhone());
                    holder.tvReceiverAddress.setText(shippingAddress.toString());
                    holder.tvReceiverZipCode.setText(shippingAddress.getZip());

                    // 只在有商家收货地址的时候物流信息才有意义
                    holder.setShippingMethodLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 物流选择
                            tvExpressHolder = holder.tvShippingCompany;
                            onSelectExpress();
                        }
                    });

                    holder.btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InputMethodManager inputManager = (InputMethodManager) getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                            if (inputManager != null && getCurrentFocus() != null) {
                                inputManager.hideSoftInputFromWindow(getCurrentFocus()
                                                .getWindowToken(),
                                        InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                            onSubmitExpressInfo(holder.etShippingNo);
                        }
                    });
                }
            }

        }

        return view;
    }

    /**
     * 退款完成的顶部操作视图
     * 只退款和退货/退款的视图文案都是一样的
     *
     * @param refundObj
     * @return
     */
    private View getRefundSuccessView(JSONObject refundObj, RefundHistory refundHistory) {

        View view = layoutInflater.inflate(R.layout.pr_refund_success, null);
        RefundSuccessViewHolder holder = new RefundSuccessViewHolder(view);

        if (refundObj != null) {
            double payMoney = refundObj.optDouble("pay_money", 0); //实际退款金额

            holder.tvRefundStatusDesc.setText(getString(R.string.status_refund_success,
                    NumberFormatUtil.formatDouble2StringWithTwoFloat(payMoney)));
        }

        // 完成时间
        if (refundHistory != null && refundHistory.getObjectChanges() != null) {
            boolean hasRedPacket = refundHistory.getObjectChanges()
                    .optInt("red_package", 0) > 0;
            if (hasRedPacket) {
                holder.tvRefundRedPacket.setVisibility(View.VISIBLE);
            } else {
                holder.tvRefundRedPacket.setVisibility(View.GONE);
            }
            // 根据当前时间和退款完成时间的间隔,10天之后开始显示一个联系客服的按钮
            Calendar now = Calendar.getInstance();
            if (refundHistory.getCreatedAt() != null && (now.getTimeInMillis() - refundHistory
                    .getCreatedAt()
                    .getTime()) > (10 * 24 * 60 * 60 * 1000)) {
                holder.contactServiceLayout.setVisibility(View.VISIBLE);
                holder.contactServiceLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onContactService();
                    }
                });
            } else {
                holder.contactServiceLayout.setVisibility(View.GONE);
            }
        }

        return view;
    }

    /**
     * 退款申请关闭的顶部操作视图
     * 根据用的收货状态,分为未确认收货和已确认收货两种状态
     *
     * @param refundHistory
     * @return
     */
    private View getAutoClosedView(RefundHistory refundHistory) {
        View view = layoutInflater.inflate(R.layout.pr_refund_closed, null);
        RefundClosedViewHolder holder = new RefundClosedViewHolder(view);

        holder.tvRefundStatus.setText(refundHistory.getRefundType() == 1 ? R.string
                .label_refund_auto_closed : R.string.label_return_auto_closed2);
        holder.tvDesc.setVisibility(View.GONE);
        holder.tvHint.setText(R.string.hint_auto_closed_refund);
        if (refundHistory != null) {
            int oldStatus = refundHistory.getObjectChanges()
                    .optInt("old_refund_status", 0);
            if (oldStatus == ProductRefundStatus.BUYER_RETURN_PRODUCT || oldStatus ==
                    ProductRefundStatus.RETURN_DECLINED) {
                holder.tvHint.setText(refundHistory.getRefundType() == 1 ? R.string
                        .hint_auto_closed_refund : R.string.hint_auto_closed_refund3);
            } else if (oldStatus == ProductRefundStatus.MERCHANT_PRODUCT_UNRECEIVED) {
                holder.tvHint.setText(R.string.hint_auto_closed_refund2);
            }

            holder.tvTime.setText(simpleDateFormat.format(refundHistory.getCreatedAt()));

            // 如果用户没有确认收货
            if (order.getStatus() <= 89) {
                holder.applyAgainLayout.setVisibility(View.VISIBLE);
            } else {
                holder.applyAgainLayout.setVisibility(View.GONE);
            }
        }

        holder.tvApplyAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onApplyAgain();
            }
        });
        holder.tvContactService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContactService();
            }
        });


        return view;
    }

    /**
     * 自动关闭的历史记录视图
     *
     * @param refundHistory
     * @return
     */
    private View getAutoCloseHistoryView(RefundHistory refundHistory) {
        View view = layoutInflater.inflate(R.layout.pr_refund_history, null);
        SimpleViewHolder holder = new SimpleViewHolder(view);
        holder.imgIcon.setVisibility(View.VISIBLE);

        holder.tvRefundStatus.setText(refundHistory.getRefundType() == 1 ? R.string
                .label_refund_auto_closed : R.string.label_return_auto_closed2);
        holder.tvContent.setText(R.string.hint_auto_closed_refund);
        if (refundHistory != null) {
            int oldStatus = refundHistory.getObjectChanges()
                    .optInt("old_refund_status", 0);
            if (oldStatus == ProductRefundStatus.BUYER_RETURN_PRODUCT || oldStatus ==
                    ProductRefundStatus.RETURN_DECLINED) {
                holder.imgIcon.setImageResource(R.drawable.icon_refund_tag_primary);
                holder.tvContent.setText(refundHistory.getRefundType() == 1 ? R.string
                        .hint_auto_closed_refund : R.string.hint_auto_closed_refund3);
            } else if (oldStatus == ProductRefundStatus.MERCHANT_PRODUCT_UNRECEIVED) {
                holder.tvContent.setText(R.string.hint_auto_closed_refund2);
                holder.imgIcon.setImageResource(R.drawable.icon_refund_tag_blue);
            }

            holder.tvTime.setText(simpleDateFormat.format(refundHistory.getCreatedAt()));
        }

        return view;
    }

    /**
     * 用户取消退款申请的顶部操作视图
     *
     * @param refundHistory
     * @return
     */
    private View getRefundCanceledView(RefundHistory refundHistory) {
        View view = layoutInflater.inflate(R.layout.pr_refund_closed, null);
        if (refundHistory != null) {
            RefundClosedViewHolder holder = new RefundClosedViewHolder(view);
            holder.tvRefundStatus.setText(refundHistory.getRefundType() == 1 ? R.string
                    .label_buyer_cancel_refund : R.string.label_buyer_cancel_return);
            holder.tvHint.setText(refundHistory.getRefundType() == 1 ? R.string
                    .label_buyer_cancel_refund2 : R.string.label_buyer_cancel_return2);
            holder.tvTime.setText(simpleDateFormat.format(refundHistory.getCreatedAt()));
            if (order.getStatus() <= 89) {
                // 待收货之前才能重新申请
                holder.applyAgainLayout.setVisibility(View.VISIBLE);
                holder.tvOr.setVisibility(View.GONE);
                holder.tvContactService.setVisibility(View.GONE);
                holder.tvApplyAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onApplyAgain();
                    }
                });
            } else {
                holder.applyAgainLayout.setVisibility(View.GONE);
            }

        }

        return view;
    }

    /**
     * 用户或者自动取消退款退货的历史记录视图
     *
     * @param refundHistory
     * @return
     */
    private View getRefundCanceledHistoryView(RefundHistory refundHistory) {
        View view = layoutInflater.inflate(R.layout.pr_refund_history, null);
        if (refundHistory != null) {
            SimpleViewHolder holder = new SimpleViewHolder(view);
            holder.imgIcon.setVisibility(View.VISIBLE);
            holder.imgIcon.setImageResource(R.drawable.icon_refund_tag_primary);
            holder.tvRefundStatus.setText(refundHistory.getRefundType() == 1 ? R.string
                    .label_buyer_cancel_refund : R.string.label_buyer_cancel_return);
            holder.tvContent.setText(refundHistory.getRefundType() == 1 ? R.string
                    .label_buyer_cancel_refund2 : R.string.label_buyer_cancel_return2);
            holder.tvTime.setText(simpleDateFormat.format(refundHistory.getCreatedAt()));
        }

        return view;
    }

    /**
     * 商家未收到货的历史记录视图
     *
     * @param refundHistory
     * @return
     */
    private View getMerchantUnreceivedHistoryView(RefundHistory refundHistory) {
        View view = layoutInflater.inflate(R.layout.pr_refund_history, null);
        if (refundHistory != null) {
            SimpleViewHolder holder = new SimpleViewHolder(view);
            holder.imgIcon.setVisibility(View.VISIBLE);
            holder.imgIcon.setImageResource(R.drawable.icon_refund_tag_blue);
            holder.tvRefundStatus.setText(R.string.label_merchant_unreceived);
            holder.tvTime.setText(simpleDateFormat.format(refundHistory.getCreatedAt()));
            holder.tvContent.setText(R.string.label_merchant_unreceived3);
        }

        return view;
    }

    /**
     * 商家确认未收到货
     *
     * @param refundHistory
     * @return
     */
    private View getMerchantReceivedNoReturns(RefundHistory refundHistory) {
        View view = layoutInflater.inflate(R.layout.pr_refund_closed, null);
        if (refundHistory != null) {
            RefundClosedViewHolder holder = new RefundClosedViewHolder(view);
            holder.tvRefundStatus.setText(R.string.label_merchant_unreceived);
            holder.tvTime.setText(simpleDateFormat.format(refundHistory.getCreatedAt()));
            holder.tvDesc.setVisibility(View.VISIBLE);
            holder.tvDesc.setText(getString(R.string.label_refund_declined_reason___cm,
                    JSONUtil.getString(refundHistory.getObjectChanges(), "not_receive_desc")));
            long timeUntil = expireTime.getTime() - Calendar.getInstance()
                    .getTimeInMillis();
            if (timeUntil > 0) {
                expireTimeCountDown = new ExpireTimeCountDown(timeUntil,
                        1000,
                        holder.tvHint,
                        R.string.label_merchant_unreceived2);
                expireTimeCountDown.start();
            } else {
                holder.tvHint.setText(Html.fromHtml(getString(R.string.label_merchant_unreceived2,
                        "0天00时00分")));
            }

            holder.tvApplyAgain.setVisibility(View.GONE);
            holder.tvOr.setVisibility(View.GONE);
            holder.applyAgainLayout.setVisibility(View.VISIBLE);
            holder.tvContactService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onContactService();
                }
            });
        }

        return view;
    }

    /**
     * 商家拒绝退款(包含"退款"和"退款/退货"两种类型)的顶部操作视图
     * 内部已处理根据不同退款类型显示不同的倒计时提示文案
     *
     * @param refundHistory
     * @return
     */
    private View getRefundDeclinedView(RefundHistory refundHistory) {
        View view = layoutInflater.inflate(R.layout.pr_refund_closed, null);

        if (refundHistory != null) {
            RefundClosedViewHolder holder = new RefundClosedViewHolder(view);
            holder.tvRefundStatus.setText(refundHistory.getRefundType() == 1 ? R.string
                    .label_refund_declined : R.string.label_return_declined2);
            String declinedReason = "";
            if (refundHistory.getObjectChanges() != null) {
                declinedReason = JSONUtil.getString(refundHistory.getObjectChanges(),
                        "refuse_reason");
            }

            holder.tvHint.setVisibility(View.GONE);

            holder.tvDesc.setVisibility(View.VISIBLE);
            holder.tvDesc.setText(getString(R.string.label_refund_declined_reason___cm,
                    declinedReason));
            holder.tvTime.setText(simpleDateFormat.format(refundHistory.getCreatedAt()));

            if (order.getStatus() <= 89) {
                // 待收货之前才能重新申请
                holder.applyAgainLayout.setVisibility(View.VISIBLE);

                holder.tvContactService.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onContactService();
                    }
                });
                holder.tvApplyAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 待发货
                        onApplyAgain();
                    }
                });
            } else {
                holder.applyAgainLayout.setVisibility(View.GONE);
            }
        }

        return view;
    }

    /**
     * 商家拒绝退款的历史记录视图
     *
     * @param refundHistory
     * @return
     */
    private View getRefundDeclinedHistoryView(RefundHistory refundHistory) {
        View view = layoutInflater.inflate(R.layout.pr_refund_closed, null);
        if (refundHistory != null) {
            RefundClosedViewHolder holder = new RefundClosedViewHolder(view);
            holder.imgIcon.setVisibility(View.VISIBLE);
            String declinedReason = "";
            if (refundHistory.getObjectChanges() != null) {
                declinedReason = JSONUtil.getString(refundHistory.getObjectChanges(),
                        "refuse_reason");
            }
            if (refundHistory.getRefundType() == 2) {
                // 拒绝退货
                holder.tvRefundStatus.setText(R.string.label_return_declined);
            }
            holder.tvTime.setText(simpleDateFormat.format(refundHistory.getCreatedAt()));
            holder.tvHint.setText(getString(R.string.label_refund_declined_reason___cm,
                    declinedReason));
        }
        return view;
    }

    /**
     * 卖家同意退款的历史记录试图
     *
     * @param refundHistory
     * @return
     */
    private View getRefundAgreeHistoryView(RefundHistory refundHistory) {
        View view = layoutInflater.inflate(R.layout.pr_refund_history, null);
        if (refundHistory != null) {
            SimpleViewHolder holder = new SimpleViewHolder(view);
            holder.imgIcon.setVisibility(View.VISIBLE);
            holder.imgIcon.setImageResource(R.drawable.icon_refund_tag_blue);
            String content;
            if (refundHistory.isAutoBySystem()) {
                // 自动同意退款
                content = getString(R.string.msg_auto_agree_refund);
            } else {
                // 卖家同意退款
                content = getString(R.string.msg_merchant_agree_refund);
            }
            holder.tvTime.setText(simpleDateFormat.format(refundHistory.getCreatedAt()));
            holder.tvContent.setText(content);
            holder.tvRefundStatus.setText(R.string.label_merchant_agree_refund);
        }
        return view;
    }

    /**
     * 卖家确认收到退货商品
     *
     * @param refundHistory
     * @return
     */
    private View getMerchantConfirmedView(RefundHistory refundHistory) {
        View view = layoutInflater.inflate(R.layout.pr_refund_history, null);
        if (refundHistory != null) {
            SimpleViewHolder holder = new SimpleViewHolder(view);
            holder.imgIcon.setVisibility(View.VISIBLE);
            holder.imgIcon.setImageResource(R.drawable.icon_refund_tag_blue);
            holder.tvTime.setText(simpleDateFormat.format(refundHistory.getCreatedAt()));
            holder.tvRefundStatus.setText(R.string.label_merchant_confirmed);
            holder.tvContent.setText(R.string.label_merchant_confirmed2);
        }

        return view;
    }

    /**
     * 历史记录中的商家同意退货,不是顶部操作视图
     *
     * @param refundHistory
     * @return
     */
    private View getReturnAgreeHistoryView(RefundHistory refundHistory) {
        View view = layoutInflater.inflate(R.layout.pr_merchant_agree_return2, null);
        if (refundHistory != null) {
            AgreeReturnViewHolder2 holder = new AgreeReturnViewHolder2(view);
            holder.tvTime.setText(simpleDateFormat.format(refundHistory.getCreatedAt()));
            // 填写卖家收货地址信息
            if (refundHistory.getObjectChanges() != null) {
                if (!refundHistory.getObjectChanges()
                        .isNull("address")) {
                    ShippingAddress shippingAddress = new ShippingAddress(refundHistory
                            .getObjectChanges()
                            .optJSONObject("address"));

                    holder.tvReceiverName.setText(shippingAddress.getBuyerName());
                    holder.tvReceiverPhone.setText(shippingAddress.getMobilePhone());
                    holder.tvReceiverAddress.setText(shippingAddress.toString());
                    holder.tvReceiverZipCode.setText(shippingAddress.getZip());
                }
            }
        }
        return view;
    }

    /**
     * 买家留言视图
     *
     * @param refundMessage
     * @return
     */
    private View getMessageView(RefundMessage refundMessage) {
        View view = layoutInflater.inflate(R.layout.pr_message_layout, null);

        if (refundMessage != null) {
            MessageViewHolder holder = new MessageViewHolder(view);

            if (refundMessage.isMerchant()) {
                holder.imgIcon.setImageResource(R.drawable.icon_refund_tag_blue);
                holder.tvTitle.setText(R.string.label_merchant_message);
            } else {
                holder.tvTitle.setText(R.string.label_buyer_message);
                holder.imgIcon.setImageResource(R.drawable.icon_refund_tag_primary);
            }

            holder.tvMessageContent.setText(refundMessage.getContent());
            holder.tvMessageTime.setText(simpleDateFormat.format(refundMessage.getCreatedAt()));

            if (refundMessage.getProofPhotos()
                    .size() > 0) {
                holder.imgsLayout.setVisibility(View.VISIBLE);

                int imgWidth = (int) (66 * dm.density);
                holder.img1.setVisibility(View.VISIBLE);
                ImageLoadUtil.loadImageView(this,
                        JSONUtil.getImagePath(refundMessage.getProofPhotos()
                                .get(0)
                                .getPath(), imgWidth),
                        holder.img1);
                holder.img1.setOnClickListener(new OnPhotosClickListener(refundMessage
                        .getProofPhotos(),
                        0));
                if (refundMessage.getProofPhotos()
                        .size() > 1) {
                    holder.img2.setVisibility(View.VISIBLE);
                    ImageLoadUtil.loadImageView(this,
                            JSONUtil.getImagePath(refundMessage.getProofPhotos()
                                    .get(1)
                                    .getPath(), imgWidth),
                            holder.img2);
                    holder.img2.setOnClickListener(new OnPhotosClickListener(refundMessage
                            .getProofPhotos(),
                            1));
                } else {
                    holder.img2.setVisibility(View.GONE);
                }
                if (refundMessage.getProofPhotos()
                        .size() > 2) {
                    holder.img3.setVisibility(View.VISIBLE);
                    ImageLoadUtil.loadImageView(this,
                            JSONUtil.getImagePath(refundMessage.getProofPhotos()
                                    .get(2)
                                    .getPath(), imgWidth),
                            holder.img3);
                    holder.img3.setOnClickListener(new OnPhotosClickListener(refundMessage
                            .getProofPhotos(),
                            2));
                } else {
                    holder.img3.setVisibility(View.GONE);
                }
            } else {
                holder.imgsLayout.setVisibility(View.GONE);
            }
        }

        return view;
    }


    /**
     * 等待商家处理(包括"退款"和"退款/退货"两种类型)的顶部操作视图
     * 内部已处理根据退款类型显示不同的倒计时提示文案
     *
     * @return
     */
    private View getMerchantHandlingView() {
        View view = layoutInflater.inflate(R.layout.pr_merchant_handling_layout, null);
        HandlingViewHolder holder = new HandlingViewHolder(view);

        int hintId = type == 1 ? R.string.status_refund_handling : R.string.status_refund_handling2;
        long timeUntil = expireTime.getTime() - Calendar.getInstance()
                .getTimeInMillis();
        if (timeUntil > 0) {
            expireTimeCountDown = new ExpireTimeCountDown(timeUntil,
                    1000,
                    holder.tvRefundHint,
                    hintId);
            expireTimeCountDown.start();
        } else {
            holder.tvRefundHint.setText(Html.fromHtml(getString(hintId, "0天00时00分")));
        }

        holder.btnPostProof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductRefundDetailActivity.this,
                        RefundMessageActivity.class);
                intent.putExtra("order_sub_id", subOrderId);
                startActivityForResult(intent, Constants.RequestCode.ADD_REFUND_MESSAGE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        });


        return view;
    }

    /**
     * 申请退款时提交的信息视图
     *
     * @param refundHistory
     * @return
     */
    private View getRefundInfoView(RefundHistory refundHistory) {
        View view = layoutInflater.inflate(R.layout.pr_apply_info_layout, null);

        if (refundHistory != null && refundHistory.getObjectChanges() != null) {
            RefundInfoViewHolder viewHolder = new RefundInfoViewHolder(view);
            viewHolder.tvRefundReason.setText(JSONUtil.getString(refundHistory.getObjectChanges(),
                    "reason"));

            viewHolder.tvRefundMoney.setText(getString(R.string.label_price,
                    NumberFormatUtil.formatDouble2StringWithTwoFloat(refundHistory
                            .getObjectChanges()
                            .optDouble("money", 0))));
            viewHolder.tvRefundDesc.setText(JSONUtil.getString(refundHistory.getObjectChanges(),
                    "desc"));
            viewHolder.tvCreatedAt.setText(simpleDateFormat.format(refundHistory.getCreatedAt()));
            viewHolder.tvRefundStatus.setText(refundHistory.getRefundType() == 1 ? R.string
                    .label_apply_refund2 : R.string.label_apply_return);

            JSONArray imgArr = refundHistory.getObjectChanges()
                    .optJSONArray("proof_photos");

            if (imgArr != null && imgArr.length() > 0) {
                ArrayList<Photo> photos = new ArrayList<>();
                viewHolder.tvRefundImgHolder.setVisibility(View.GONE);
                viewHolder.imgsHolderLayout.setVisibility(View.VISIBLE);
                for (int i = 0; i < imgArr.length(); i++) {
                    Photo photo = new Photo(imgArr.optJSONObject(i));
                    photos.add(photo);
                }
                int imgWidth = (int) (point.x - (80 + 24 + 26 + 8) * dm.density) / 3;

                viewHolder.imgApplyProof1.getLayoutParams().width = imgWidth;
                viewHolder.imgApplyProof1.getLayoutParams().height = imgWidth;
                ImageLoadUtil.loadImageView(this,
                        JSONUtil.getImagePath(photos.get(0)
                                .getPath(), imgWidth),
                        viewHolder.imgApplyProof1);
                viewHolder.imgApplyProof1.setOnClickListener(new OnPhotosClickListener(photos, 0));

                if (imgArr.length() > 1) {
                    viewHolder.imgApplyProof2.getLayoutParams().width = imgWidth;
                    viewHolder.imgApplyProof2.getLayoutParams().height = imgWidth;
                    ImageLoadUtil.loadImageView(this,
                            JSONUtil.getImagePath(photos.get(1)
                                    .getPath(), imgWidth),
                            viewHolder.imgApplyProof2);
                    viewHolder.imgApplyProof2.setOnClickListener(new OnPhotosClickListener(photos,
                            1));
                }
                if (imgArr.length() > 2) {
                    viewHolder.imgApplyProof3.getLayoutParams().width = imgWidth;
                    viewHolder.imgApplyProof3.getLayoutParams().height = imgWidth;
                    ImageLoadUtil.loadImageView(this,
                            JSONUtil.getImagePath(photos.get(2)
                                    .getPath(), imgWidth),
                            viewHolder.imgApplyProof3);
                    viewHolder.imgApplyProof3.setOnClickListener(new OnPhotosClickListener(photos,
                            2));
                }
            } else {
                viewHolder.imgsHolderLayout.setVisibility(View.GONE);
                viewHolder.tvRefundImgHolder.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }

    private void onApplyAgain() {
        Intent intent;
        if (order.getStatus() == 88) {
            intent = new Intent(ProductRefundDetailActivity.this, ProductRefundApplyActivity.class);
            intent.putExtra("order", order);
            intent.putExtra("id", subOrderId);
            intent.putExtra("apply_again", true);
        } else {
            intent = new Intent(ProductRefundDetailActivity.this, SelectRefundTypeActivity.class);
            intent.putExtra("order", order);
            intent.putExtra("id", subOrderId);
            intent.putExtra("apply_again", true);
        }

        startActivityForResult(intent, Constants.RequestCode.APPLY_REFUND_AGAIN);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    /**
     * 选择物流
     */
    private void onSelectExpress() {
        if (expresses.isEmpty()) {
            // 首先获取物流信息
            progressBar.setVisibility(View.VISIBLE);
            new GetExpressTask().execute();
        } else {
            showSelectExpress();
        }
    }

    private void showSelectExpress() {
        selectExpressDialog = DialogUtil.createSingleWheelPickerDialog
                (ProductRefundDetailActivity.this,
                expresses,
                new DialogUtil.onWheelSelectedListener() {
                    @Override
                    public void selected(Label label) {
                        selectedExpress = label;
                        if (tvExpressHolder != null) {
                            tvExpressHolder.setText(selectedExpress.getName());
                        }
                    }
                });
        selectExpressDialog.show();
    }

    /**
     * 提交物流信息
     *
     * @param etShippingNo
     */
    private void onSubmitExpressInfo(final EditText etShippingNo) {
        if (selectedExpress == null) {
            Toast.makeText(ProductRefundDetailActivity.this,
                    R.string.msg_empty_express,
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (etShippingNo.length() <= 0) {
            Toast.makeText(ProductRefundDetailActivity.this,
                    R.string.msg_empty_shipping_no,
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        String trackingNo = etShippingNo.getText()
                .toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_sub_id", subOrderId);
            jsonObject.put("type_code", selectedExpress.getKeyWord());
            jsonObject.put("tracking_no", trackingNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);
        new StatusHttpPostTask(ProductRefundDetailActivity.this, new StatusRequestListener() {

            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                // 提交物流信息成功,刷新页面
                Toast.makeText(ProductRefundDetailActivity.this,
                        R.string.msg_success_to_submit_express,
                        Toast.LENGTH_SHORT)
                        .show();
                new GetRefundDetailTask().execute();
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                Util.postFailToast(ProductRefundDetailActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_submit_express,
                        network);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_EXPRESS_INFO),
                jsonObject.toString());
    }

    /**
     * 修改物流信息
     */
    private void onChangeExpressInfo(ExpressInfo expressInfo) {
        Intent intent = new Intent(this, ChangeExpressInfoActivity.class);
        intent.putExtra("sub_order_id", subOrderId);
        intent.putExtra("express_info", expressInfo);
        startActivityForResult(intent, Constants.RequestCode.EDIT_EXPRESS_INFO);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    private void onContactService() {
        CustomerSupportUtil.goToSupport(this, Support.SUPPORT_KIND_DEFAULT_ROBOT, order);
    }

    @Override
    public void onOkButtonClick() {
        cancelRefundDlg = DialogUtil.createDoubleButtonDialog(cancelRefundDlg,
                this,
                type == 1 ? getString(R.string.msg_cancel_refund2) : getString(R.string
                        .msg_cancel_return2),
                null,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 撤销退款申请
                        cancelRefundDlg.dismiss();
                        progressBar.setVisibility(View.VISIBLE);
                        postCancelRefund();
                    }
                },
                null);
        cancelRefundDlg.show();
    }

    private void postCancelRefund() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_sub_id", subOrderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new StatusHttpPostTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                EventBus.getDefault()
                        .post(new MessageEvent(MessageEvent.EventType.PRODUCT_ORDER_REFRESH_FLAG,
                                null));
                new GetRefundDetailTask().execute();
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                Util.postFailToast(ProductRefundDetailActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_cancel_refund,
                        network);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.CANCEL_PRODUCT_ORDER_REFUND),
                jsonObject.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.ADD_REFUND_MESSAGE:
                case Constants.RequestCode.EDIT_EXPRESS_INFO:
                case Constants.RequestCode.APPLY_REFUND_AGAIN:
                    if (data != null && data.getBooleanExtra("refresh", false)) {
                        progressBar.setVisibility(View.VISIBLE);
                        new GetRefundDetailTask().execute();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (!isLoad) {
            new GetRefundDetailTask().execute();
        }
    }

    private class ExpireTimeCountDown extends CountDownTimer {

        private TextView textView;
        private int sId;

        public ExpireTimeCountDown(
                long millisInFuture, long countDownInterval, TextView tv, int id) {
            // 多加20秒缓冲时间,避免结束发起刷新的时候服务器计时任务没有执行
            super(millisInFuture + 20 * 1000, countDownInterval);
            textView = tv;
            sId = id;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // 每秒更新倒计时字面值
            String timeLeft = TimeUtil.getSpecialTimeLiteral(ProductRefundDetailActivity.this,
                    millisUntilFinished);
            textView.setText(Html.fromHtml(getString(sId, timeLeft)));
        }

        @Override
        public void onFinish() {
            // 倒计时完成,刷新信息
            boolean isDead;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isDead = isDestroyed();
            } else {
                isDead = isFinishing();
            }
            if (!isDead) {
                EventBus.getDefault()
                        .post(new MessageEvent(MessageEvent.EventType.PRODUCT_ORDER_REFRESH_FLAG,
                                null));
                progressBar.setVisibility(View.VISIBLE);
                new GetRefundDetailTask().execute();
            }
        }
    }

    private class OnPhotosClickListener implements View.OnClickListener {

        private ArrayList<Photo> photos;
        private int position;

        public OnPhotosClickListener(ArrayList<Photo> photos, int position) {
            this.photos = photos;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Photo photo = photos.get(position);
            if (photo != null) {
                Intent intent = new Intent(ProductRefundDetailActivity.this,
                        ThreadPicsPageViewActivity.class);
                intent.putExtra("photos", photos);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        }
    }

    private class GetRefundDetailTask extends AsyncTask<String, Object, JSONObject> {

        public GetRefundDetailTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = String.format(Constants.getAbsUrl(Constants.HttpPath
                            .GET_PRODUCT_REFUND_DETAIL),
                    subOrderId);
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
            if (jsonObject != null) {
                scrollView.onRefreshComplete();
                progressBar.setVisibility(View.GONE);
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus != null && returnStatus.getRetCode() == 0) {
                    JSONObject dataObj = jsonObject.optJSONObject("data");

                    refundStatus = dataObj.optInt("refund_status", 0);
                    type = dataObj.optJSONObject("refund")
                            .optInt("type", 1);

                    setContent(dataObj);
                }
            }

            isLoad = false;
            super.onPostExecute(jsonObject);
        }
    }

    private class GetExpressTask extends AsyncTask<String, Object, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.getAbsUrl(Constants.HttpPath.GET_EXPRESS_METHODS);
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
            if (jsonObject != null) {
                progressBar.setVisibility(View.GONE);
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus.getRetCode() == 0) {
                    // 显示待选数据
                    expresses.clear();
                    JSONArray jsonArray = jsonObject.optJSONArray("data");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ExpressMethod expressMethod = new ExpressMethod(jsonArray.optJSONObject(
                                    i));
                            expresses.add(expressMethod);
                        }
                    }
                    if (!expresses.isEmpty()) {
                        showSelectExpress();
                    }
                } else {
                    Toast.makeText(ProductRefundDetailActivity.this,
                            returnStatus.getErrorMsg(),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    static class RefundInfoViewHolder {
        @BindView(R.id.tv_refund_status)
        TextView tvRefundStatus;
        @BindView(R.id.tv_created_at)
        TextView tvCreatedAt;
        @BindView(R.id.line_layout)
        View lineLayout;
        @BindView(R.id.tv_refund_reason)
        TextView tvRefundReason;
        @BindView(R.id.tv_refund_money)
        TextView tvRefundMoney;
        @BindView(R.id.tv_refund_desc)
        TextView tvRefundDesc;
        @BindView(R.id.tv_img_title)
        TextView tvImgTitle;
        @BindView(R.id.tv_refund_img_holder)
        TextView tvRefundImgHolder;
        @BindView(R.id.img_apply_proof_1)
        ImageView imgApplyProof1;
        @BindView(R.id.img_apply_proof_2)
        ImageView imgApplyProof2;
        @BindView(R.id.img_apply_proof_3)
        ImageView imgApplyProof3;
        @BindView(R.id.imgs_holder_layout)
        LinearLayout imgsHolderLayout;
        @BindView(R.id.imgs_layout)
        LinearLayout imgsLayout;

        RefundInfoViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    static class HandlingViewHolder {
        @BindView(R.id.tv_refund_hint)
        TextView tvRefundHint;
        @BindView(R.id.btn_post_proof)
        Button btnPostProof;

        HandlingViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    static class MessageViewHolder {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_message_time)
        TextView tvMessageTime;
        @BindView(R.id.tv_message_content)
        TextView tvMessageContent;
        @BindView(R.id.imgs_layout)
        View imgsLayout;
        @BindView(R.id.img_1)
        ImageView img1;
        @BindView(R.id.img_2)
        ImageView img2;
        @BindView(R.id.img_3)
        ImageView img3;
        @BindView(R.id.img_icon)
        ImageView imgIcon;

        MessageViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    static class RefundClosedViewHolder {
        @BindView(R.id.tv_refund_status)
        TextView tvRefundStatus;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_desc)
        TextView tvDesc;
        @BindView(R.id.tv_apply_again)
        TextView tvApplyAgain;
        @BindView(R.id.tv_or)
        TextView tvOr;
        @BindView(R.id.tv_contact_service)
        TextView tvContactService;
        @BindView(R.id.tv_hint)
        TextView tvHint;
        @BindView(R.id.apply_again_layout)
        LinearLayout applyAgainLayout;
        @BindView(R.id.img_icon)
        ImageView imgIcon;

        RefundClosedViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    static class RefundSuccessViewHolder {
        @BindView(R.id.tv_refund_status)
        TextView tvRefundStatus;
        @BindView(R.id.tv_refund_status_desc)
        TextView tvRefundStatusDesc;
        @BindView(R.id.tv_refund_red_packet)
        TextView tvRefundRedPacket;
        @BindView(R.id.contact_service_layout)
        RelativeLayout contactServiceLayout;

        RefundSuccessViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    static class AgreeReturnViewHolder {
        @BindView(R.id.tv_refund_status)
        TextView tvRefundStatus;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_auto_agree)
        TextView tvAutoAgree;
        @BindView(R.id.tv_time_hint)
        TextView tvTimeHint;
        @BindView(R.id.tv_receiver_name)
        TextView tvReceiverName;
        @BindView(R.id.tv_receiver_phone)
        TextView tvReceiverPhone;
        @BindView(R.id.tv_receiver_address)
        TextView tvReceiverAddress;
        @BindView(R.id.tv_receiver_zip_code)
        TextView tvReceiverZipCode;
        @BindView(R.id.tv_shipping_company)
        TextView tvShippingCompany;
        @BindView(R.id.set_shipping_method_layout)
        LinearLayout setShippingMethodLayout;
        @BindView(R.id.et_shipping_no)
        EditText etShippingNo;
        @BindView(R.id.btn_submit)
        Button btnSubmit;
        @BindView(R.id.return_product_layout)
        LinearLayout returnProductLayout;

        AgreeReturnViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    static class AgreeReturnViewHolder2 {
        @BindView(R.id.tv_refund_status)
        TextView tvRefundStatus;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_receiver_name)
        TextView tvReceiverName;
        @BindView(R.id.tv_receiver_phone)
        TextView tvReceiverPhone;
        @BindView(R.id.tv_receiver_address)
        TextView tvReceiverAddress;
        @BindView(R.id.tv_receiver_zip_code)
        TextView tvReceiverZipCode;

        AgreeReturnViewHolder2(View view) {ButterKnife.bind(this, view);}
    }

    static class BuyerShippingViewHolder {
        @BindView(R.id.tv_refund_status)
        TextView tvRefundStatus;
        @BindView(R.id.tv_shipping_company)
        TextView tvShippingCompany;
        @BindView(R.id.tv_shipping_no)
        TextView tvShippingNo;
        @BindView(R.id.btn_change_shipping)
        Button btnChangeShipping;
        @BindView(R.id.shipping_info_action_layout)
        LinearLayout shippingInfoActionLayout;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.img_icon)
        ImageView imgIcon;
        @BindView(R.id.tv_copy_number)
        TextView tvCopyNumber;

        BuyerShippingViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    static class SimpleViewHolder {
        @BindView(R.id.tv_refund_status)
        TextView tvRefundStatus;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.img_icon)
        ImageView imgIcon;

        SimpleViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    @Override
    protected void onFinish() {
        EventBus.getDefault()
                .unregister(this);
        super.onFinish();
    }

    public void onEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.EventType.PRODUCT_REFUND_DETAIL_REFRESH_FLAG) {
            needRefresh = true;
        }
    }

    @Override
    protected void onResume() {
        if (needRefresh) {
            progressBar.setVisibility(View.VISIBLE);
            new GetRefundDetailTask().execute();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (expireTimeCountDown != null) {
            expireTimeCountDown.cancel();
            expireTimeCountDown = null;
        }
        super.onPause();
    }

    @OnClick(R.id.btn_support)
    void onSupport() {
        CustomerSupportUtil.goToSupport(this, Support.SUPPORT_KIND_DEFAULT_ROBOT, order);
    }

    @OnClick(R.id.btn_contact)
    void onContact() {
        Intent intent = new Intent(this, WSCustomerChatActivity.class);
        intent.putExtra("user",
                order.getMerchant()
                        .toUser(1));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }
}
