package me.suncloud.marrymemo.adpter.orders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljpaymentlibrary.PayAgent;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.CustomSetmealOrder;
import me.suncloud.marrymemo.model.orders.BasicServiceOrderWrapper;
import me.suncloud.marrymemo.model.orders.ServiceOrder;
import me.suncloud.marrymemo.model.orders.ServiceOrderSub;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.CommentNewWorkActivity;
import me.suncloud.marrymemo.view.CustomSetmealOrderDetailActivity;
import me.suncloud.marrymemo.view.CustomSetmealOrderPaymentActivity;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.comment.CommentServiceActivity;
import me.suncloud.marrymemo.view.orders.ServiceOrderDetailActivity;
import me.suncloud.marrymemo.view.orders.ServiceOrderPayRestActivity;
import me.suncloud.marrymemo.view.orders.ServiceOrderPaymentActivity;
import me.suncloud.marrymemo.widget.RecyclingImageView;

/**
 * Created by werther on 16/10/11.
 * 订单列表页面,服务订单列表的fragment的Adapter
 * 包含定制套餐和普通套餐两种view
 */

public class ServiceOrdersAdapter extends RecyclerView
        .Adapter<BaseViewHolder<BasicServiceOrderWrapper>> {

    private Context context;
    private ArrayList<BasicServiceOrderWrapper> orders;
    private View headerView;
    private View footerView;
    private OnCancelOrderListener onCancelOrderListener;
    private OnDeleteOrderListener onDeleteOrderListener;
    private OnConfirmOrderListener onConfirmOrderListener;
    private OnCancelCSOrderListener onCancelCSOrderListener;
    private OnConfirmCSOrderListener onConfirmCSOrderListener;
    private OnDelayConfirmListener onDelayConfirmListener;
    private RxBusSubscriber<PayRxEvent> paySubscriber;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM_CUSTOM_SET_MEAL = 1;
    private static final int TYPE_ITEM_SERVICE_ORDER = 2;
    private static final int TYPE_FOOTER = 3;

    public ServiceOrdersAdapter(
            Context context, ArrayList<BasicServiceOrderWrapper> orders) {
        this.context = context;
        this.orders = orders;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setPaySubscriber(RxBusSubscriber paySubscriber) {
        this.paySubscriber = paySubscriber;
    }

    public void setOnCancelOrderListener(OnCancelOrderListener onCancelOrderListener) {
        this.onCancelOrderListener = onCancelOrderListener;
    }

    public void setOnDeleteOrderListener(OnDeleteOrderListener onDeleteOrderListener) {
        this.onDeleteOrderListener = onDeleteOrderListener;
    }

    public void setOnConfirmOrderListener(OnConfirmOrderListener onConfirmOrderListener) {
        this.onConfirmOrderListener = onConfirmOrderListener;
    }

    public void setOnCancelCSOrderListener(OnCancelCSOrderListener onCancelCSOrderListener) {
        this.onCancelCSOrderListener = onCancelCSOrderListener;
    }

    public void setOnConfirmCSOrderListener(OnConfirmCSOrderListener onConfirmCSOrderListener) {
        this.onConfirmCSOrderListener = onConfirmCSOrderListener;
    }

    public void setOnDelayConfirmListener(OnDelayConfirmListener onDelayConfirmListener) {
        this.onDelayConfirmListener = onDelayConfirmListener;
    }

    @Override
    public BaseViewHolder<BasicServiceOrderWrapper> onCreateViewHolder(
            ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new ExtraViewHolder(headerView);
            case TYPE_FOOTER:
                return new ExtraViewHolder(footerView);
            case TYPE_ITEM_CUSTOM_SET_MEAL:
                return new CustomSetmealOrderViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.custom_setmeal_order_list_item, parent, false));
            default:
                return new ServiceOrderViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.service_order_list_item2, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<BasicServiceOrderWrapper> holder, int position) {
        holder.setView(context, getItem(position), position, getItemViewType(position));
    }

    public BasicServiceOrderWrapper getItem(int position) {
        if (orders.isEmpty()) {
            return null;
        }
        int headSize = 0;
        if (headerView != null) {
            headSize = 1;
        }
        if (position - headSize >= 0 && position - headSize < orders.size()) {
            return orders.get(position - headSize);
        } else {
            return null;
        }
    }

    public void removeItem(int position) {
        if (orders.isEmpty()) {
            return;
        }
        int headSize = 0;
        if (headerView != null) {
            headSize = 1;
        }
        if (position - headSize >= 0 && position - headSize < orders.size()) {
            orders.remove(position - headSize);
        }
    }

    @Override
    public int getItemCount() {
        return (headerView == null ? 0 : 1) + (footerView == null ? 0 : 1) + (orders == null ? 0
                : orders.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerView != null) {
            return TYPE_HEADER;
        } else if (position == getItemCount() - 1 && footerView != null) {
            return TYPE_FOOTER;
        } else if (getItem(position).getType() == 1) {
            return TYPE_ITEM_CUSTOM_SET_MEAL;
        } else {
            return TYPE_ITEM_SERVICE_ORDER;
        }
    }

    private class ExtraViewHolder extends BaseViewHolder {
        public ExtraViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void setViewData(
                Context mContext, Object item, int position, int viewType) {
        }
    }


    class ServiceOrderViewHolder extends BaseViewHolder<BasicServiceOrderWrapper> {
        @BindView(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @BindView(R.id.tv_order_status)
        TextView tvOrderStatus;
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.img_installment)
        ImageView imgInstallment;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_serve_time)
        TextView tvServeTime;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_price_label1)
        TextView tvPriceLabel1;
        @BindView(R.id.tv_price1)
        TextView tvPrice1;
        @BindView(R.id.price_layout1)
        LinearLayout priceLayout1;
        @BindView(R.id.tv_price_label2)
        TextView tvPriceLabel2;
        @BindView(R.id.tv_price2)
        TextView tvPrice2;
        @BindView(R.id.btn_action1)
        Button btnAction1;
        @BindView(R.id.btn_action2)
        Button btnAction2;
        @BindView(R.id.action_layout)
        View actionLayout;
        @BindView(R.id.content_layout)
        View contentLayout;
        @BindView(R.id.sales_layout)
        View salesLayout;
        @BindView(R.id.tv_sales_text)
        TextView tvSalesText;
        @BindView(R.id.img_intent_money)
        ImageView imgIntentMoney;

        ServiceOrderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(
                final Context mContext,
                final BasicServiceOrderWrapper item,
                final int position,
                int viewType) {
            if (item == null) {
                return;
            }
            final ServiceOrder order = (ServiceOrder) item.getOrder();
            final ServiceOrderSub orderSub = order.getOrderSub();
            if (order == null) {
                return;
            }
            contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ServiceOrderDetailActivity.class);
                    intent.putExtra("id", order.getId());
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            });
            tvMerchantName.setText(orderSub.getMerchant()
                    .getName());
            Glide.with(mContext)
                    .load(orderSub.getWork()
                            .getCoverPath())
                    .into(imgCover);
            imgIntentMoney.setVisibility(order.getOrderPayType() == ServiceOrder
                    .ORDER_PAY_TYPE_INTENT ? View.VISIBLE : View.GONE);
            if (order.isInstallment()) {
                imgIntentMoney.setVisibility(View.GONE);
            }
            imgInstallment.setVisibility(order.isInstallment() ? View.VISIBLE : View.GONE);

            tvTitle.setText(orderSub.getWork()
                    .getTitle());
            tvPrice.setText(mContext.getString(R.string.label_price4,
                    Util.formatDouble2String(orderSub.getActualPrice())));
            if (order.getWeddingTime() != null) {
                tvServeTime.setVisibility(View.VISIBLE);
                tvServeTime.setText(mContext.getString(R.string.label_serve_time,
                        order.getWeddingTime()
                                .toString("yyyy-MM-dd")));
            } else {
                tvServeTime.setVisibility(View.INVISIBLE);
            }
            if (order.getRule() != null && !TextUtils.isEmpty(order.getRule()
                    .getName())) {
                salesLayout.setVisibility(View.VISIBLE);
                tvSalesText.setText(order.getRule()
                        .getName());
            } else {
                salesLayout.setVisibility(View.GONE);
            }

            tvOrderStatus.setText(orderSub.getStatusStr());
            final double needPay;

            actionLayout.setVisibility(View.GONE);
            // 根据订单状态，设置付款信息和操作按钮
            switch (orderSub.getStatus()) {
                case ServiceOrderSub.STATUS_WAITING_FOR_THE_PAYMENT:
                    // 等待付款
                    if (order.getOrderPayType() == ServiceOrder.ORDER_PAY_TYPE_DEPOSIT || order
                            .getOrderPayType() == ServiceOrder.ORDER_PAY_TYPE_INTENT) {
                        // 选择定金付款或意向金支付
                        // 付款信息显示总金额和定金
                        priceLayout1.setVisibility(View.VISIBLE);
                        tvPriceLabel1.setText("总金额：");
                        if (order.getOrderPayType() == ServiceOrder.ORDER_PAY_TYPE_INTENT) {
                            // 意向金
                            tvPriceLabel2.setText("意向金：");
                            btnAction2.setText("支付意向金");
                            needPay = orderSub.getIntentMoney();
                            btnAction2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onPay(order, needPay, PayConfig.PAY_TYPE_INTENT);
                                }
                            });
                        } else {
                            // 定金
                            tvPriceLabel2.setText("定金：");
                            btnAction2.setText("支付定金");
                            needPay = orderSub.getEarnestMoney();
                            btnAction2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onPay(order, needPay, PayConfig.PAY_TYPE_DEPOSIT);
                                }
                            });
                        }
                        // 定金支付方式下的实付和实收需要根据红包和优惠券的值重新计算
                        // 用户实付尾款对于意向金也是同样的算法
                        tvPrice1.setText(mContext.getString(R.string.label_price,
                                Util.formatDouble2StringPositive(order.getCustomerRealPayMoney())));
                        tvPrice2.setText(mContext.getString(R.string.label_price,
                                Util.formatDouble2StringPositive(needPay)));
                        actionLayout.setVisibility(View.VISIBLE);
                        btnAction1.setVisibility(View.VISIBLE);
                        btnAction1.setText("取消订单");
                        btnAction1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onCancelOrder(order, position);
                            }
                        });

                    } else {
                        // 选择全额付款
                        priceLayout1.setVisibility(View.GONE);
                        tvPriceLabel2.setText("总金额：");
                        needPay = order.getCustomerRealPayMoney();
                        tvPrice2.setText(mContext.getString(R.string.label_price,
                                Util.formatDouble2StringPositive(order.getCustomerRealPayMoney())));
                        actionLayout.setVisibility(View.VISIBLE);
                        btnAction1.setVisibility(View.VISIBLE);
                        btnAction1.setText("取消订单");
                        btnAction1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onCancelOrder(order, position);
                            }
                        });
                        btnAction2.setText("去付款");
                        btnAction2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onPay(order, needPay, PayConfig.PAY_TYPE_PAY_ALL);
                            }
                        });
                    }
                    break;
                case ServiceOrderSub.STATUS_WAITING_FOR_ACCEPT_ORDER:
                    // 等待商家接单
                    if (orderSub.getMoneyStatus() == ServiceOrderSub.MONEY_STATUS_PAID_INTENT ||
                            orderSub.getMoneyStatus() == ServiceOrderSub
                                    .MONEY_STATUS_PAID_DEPOSIT) {
                        // 已付意向金
                        priceLayout1.setVisibility(View.VISIBLE);
                        tvPriceLabel1.setText("还需支付：");
                        needPay = order.getCustomerRealPayMoney() - orderSub.getPaidMoney();
                        tvPrice1.setText(mContext.getString(R.string.label_price,
                                Util.formatDouble2StringPositive(needPay)));
                        tvPriceLabel2.setText("已付金额：");
                        tvPrice2.setText(mContext.getString(R.string.label_price,
                                Util.formatDouble2String(orderSub.getPaidMoney())));
                        actionLayout.setVisibility(View.VISIBLE);
                        btnAction1.setVisibility(View.GONE);
                        btnAction1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onCancelOrder(order, position);
                            }
                        });
                        btnAction2.setText("继续付款");
                        btnAction2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (orderSub.getMoneyStatus() == ServiceOrderSub
                                        .MONEY_STATUS_PAID_INTENT) {
                                    onPayAfterIntentMoney(order);
                                } else {
                                    onPayRest(order, needPay, PayConfig.PAY_TYPE_REST);
                                }
                            }
                        });
                    } else {
                        // 已付全款
                        priceLayout1.setVisibility(View.GONE);
                        tvPriceLabel2.setText("总金额：");
                        tvPrice2.setText(mContext.getString(R.string.label_price,
                                Util.formatDouble2StringPositive(order.getCustomerRealPayMoney())));
                        // 没有进一步的操作
                        actionLayout.setVisibility(View.GONE);
                    }
                    break;
                case ServiceOrderSub.STATUS_MERCHANT_REFUSE_ORDER:
                    // 商家拒绝接单，没有操作，但需要显示付款信息
                    priceLayout1.setVisibility(View.VISIBLE);
                    tvPriceLabel1.setText("总金额：");
                    tvPrice1.setText(mContext.getString(R.string.label_price,
                            Util.formatDouble2StringPositive(order.getCustomerRealPayMoney())));
                    tvPriceLabel2.setText("已付金额：");
                    tvPrice2.setText(mContext.getString(R.string.label_price,
                            Util.formatDouble2String(order.getOrderSub()
                                    .getPaidMoney())));
                    actionLayout.setVisibility(View.GONE);
                    break;
                case ServiceOrderSub.STATUS_ORDER_AUTO_CLOSED:
                case ServiceOrderSub.STATUS_ORDER_CLOSED:
                    actionLayout.setVisibility(View.VISIBLE);
                    priceLayout1.setVisibility(View.GONE);
                    tvPriceLabel2.setText("总金额：");
                    tvPrice2.setText(mContext.getString(R.string.label_price,
                            Util.formatDouble2StringPositive(order.getCustomerRealPayMoney())));
                    // 操作按钮显示 删除和重新下单
                    btnAction1.setVisibility(View.VISIBLE);
                    btnAction1.setText("删除订单");
                    btnAction1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onDeleteOrder(order, getAdapterPosition());
                        }
                    });
                    btnAction2.setText("重新下单");
                    btnAction2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goToWorkActivity(order);
                        }
                    });
                    break;
                case ServiceOrderSub.STATUS_MERCHANT_ACCEPT_ORDER:
                    // 商家已接单
                    if (orderSub.getMoneyStatus() == ServiceOrderSub.MONEY_STATUS_PAID_DEPOSIT ||
                            orderSub.getMoneyStatus() == ServiceOrderSub.MONEY_STATUS_PAID_INTENT) {
                        // 已付定金，还需要付余款
                        priceLayout1.setVisibility(View.VISIBLE);
                        tvPriceLabel1.setText("还需支付：");
                        needPay = order.getCustomerRealPayMoney() - orderSub.getPaidMoney();
                        tvPrice1.setText(mContext.getString(R.string.label_price,
                                Util.formatDouble2StringPositive(needPay)));
                        tvPriceLabel2.setText("已付金额：");
                        tvPrice2.setText(mContext.getString(R.string.label_price,
                                Util.formatDouble2String(orderSub.getPaidMoney())));
                        actionLayout.setVisibility(View.VISIBLE);
                        btnAction1.setVisibility(View.GONE);
                        btnAction2.setText("继续付款");
                        btnAction2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (orderSub.getMoneyStatus() == ServiceOrderSub
                                        .MONEY_STATUS_PAID_INTENT) {
                                    onPayAfterIntentMoney(order);
                                } else {
                                    onPayRest(order, needPay, PayConfig.PAY_TYPE_REST);
                                }
                            }
                        });
                    } else {
                        if (orderSub.isFinished()) {
                            // 商家已确认服务
                            actionLayout.setVisibility(View.VISIBLE);
                            priceLayout1.setVisibility(View.GONE);
                            tvPriceLabel2.setText("总金额：");
                            tvPrice2.setText(mContext.getString(R.string.label_price,
                                    Util.formatDouble2StringPositive(order
                                            .getCustomerRealPayMoney())));
                            if (order.getOrderSub()
                                    .getMerchant()
                                    .getProperty()
                                    .getId() == Merchant.PROPERTY_WEDDING_DRESS_PHOTO) {
                                btnAction1.setVisibility(View.VISIBLE);
                                btnAction1.setText(R.string.label_delay_confirm);
                                btnAction1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onDelayConfirm(order, position);
                                    }
                                });
                            } else {
                                btnAction1.setVisibility(View.GONE);
                            }
                            btnAction2.setText("确认已消费");
                            btnAction2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onConfirmOrder(order, position);
                                }
                            });
                        } else {
                            // 已付全款，等待商家确认服务
                            priceLayout1.setVisibility(View.GONE);
                            tvPriceLabel2.setText("总金额：");
                            tvPrice2.setText(mContext.getString(R.string.label_price,
                                    Util.formatDouble2StringPositive(order
                                            .getCustomerRealPayMoney())));
                            actionLayout.setVisibility(View.GONE);
                        }
                    }
                    break;
                case ServiceOrderSub.STATUS_SERVICE_COMPLETE:
                    // 交易成功
                    priceLayout1.setVisibility(View.GONE);
                    tvPriceLabel2.setText("总金额：");
                    tvPrice2.setText(mContext.getString(R.string.label_price,
                            Util.formatDouble2StringPositive(order.getCustomerRealPayMoney())));
                    actionLayout.setVisibility(View.VISIBLE);
                    btnAction1.setVisibility(View.GONE);
                    btnAction2.setText("评价");
                    btnAction2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onComment(order);
                        }
                    });
                    break;
                case ServiceOrderSub.STATUS_ORDER_COMMENTED:
                    // 已评价
                    priceLayout1.setVisibility(View.GONE);
                    tvPriceLabel2.setText("总金额：");
                    tvPrice2.setText(mContext.getString(R.string.label_price,
                            Util.formatDouble2StringPositive(order.getCustomerRealPayMoney())));
                    actionLayout.setVisibility(View.GONE);
                    break;
                case ServiceOrderSub.STATUS_REFUND_REVIEWING:
                case ServiceOrderSub.STATUS_CANCEL_REFUND:
                case ServiceOrderSub.STATUS_REFUND_APPROVED:
                case ServiceOrderSub.STATUS_REFUND_SUCCEED:
                case ServiceOrderSub.STATUS_REFUSE_REFUND:
                    if (orderSub.getMoneyStatus() == ServiceOrderSub.MONEY_STATUS_PAID_DEPOSIT) {
                        // 已付定金，还需要付余款
                        priceLayout1.setVisibility(View.VISIBLE);
                        tvPriceLabel1.setText("还需支付：");
                        needPay = order.getCustomerRealPayMoney() - orderSub.getPaidMoney();
                        tvPrice1.setText(mContext.getString(R.string.label_price,
                                Util.formatDouble2StringPositive(needPay)));
                        tvPriceLabel2.setText("已付金额：");
                        tvPrice2.setText(mContext.getString(R.string.label_price,
                                Util.formatDouble2String(orderSub.getPaidMoney())));
                    } else {
                        // 已付全款
                        priceLayout1.setVisibility(View.GONE);
                        tvPriceLabel2.setText("总金额：");
                        tvPrice2.setText(mContext.getString(R.string.label_price,
                                Util.formatDouble2StringPositive(order.getCustomerRealPayMoney())));
                    }
                    actionLayout.setVisibility(View.GONE);
                    break;

            }
        }
    }

    public interface OnCancelOrderListener {
        void onCancel(ServiceOrder serviceOrder, int position);
    }

    public interface OnDeleteOrderListener {
        void onDelete(ServiceOrder serviceOrder, int position);
    }

    public interface OnConfirmOrderListener {
        void onConfirm(ServiceOrder serviceOrder, int position);
    }

    public interface OnCancelCSOrderListener {
        void onCancelCSOrder(CustomSetmealOrder order, int position);
    }

    public interface OnConfirmCSOrderListener {
        void onConfirmCSOrder(CustomSetmealOrder order, int position);
    }

    public interface OnDelayConfirmListener {
        void onDelayConfirm(ServiceOrder serviceOrder, int position);
    }

    private void goToWorkActivity(ServiceOrder serviceOrder) {
        Intent intent = new Intent(context, WorkActivity.class);
        intent.putExtra("id",
                serviceOrder.getOrderSub()
                        .getWork()
                        .getId());
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    private void onComment(ServiceOrder serviceOrder) {
        Intent intent = new Intent(context, CommentServiceActivity.class);
        intent.putExtra(CommentServiceActivity.ARG_SUB_ORDER_NO,
                serviceOrder.getOrderSub()
                        .getOrderNo());
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    private void onDeleteOrder(ServiceOrder serviceOrder, int position) {
        if (this.onDeleteOrderListener != null) {
            this.onDeleteOrderListener.onDelete(serviceOrder, position);
        }
    }

    private void onCancelOrder(ServiceOrder serviceOrder, int position) {
        if (this.onCancelOrderListener != null) {
            this.onCancelOrderListener.onCancel(serviceOrder, position);
        }
    }

    private void onConfirmOrder(ServiceOrder serviceOrder, int position) {
        if (this.onConfirmOrderListener != null) {
            this.onConfirmOrderListener.onConfirm(serviceOrder, position);
        }
    }

    private void onCancelCSOrder(CustomSetmealOrder order, int position) {
        if (this.onCancelCSOrderListener != null) {
            this.onCancelCSOrder(order, position);
        }
    }

    private void onConfirmCSOrder(CustomSetmealOrder order, int position) {
        if (this.onConfirmCSOrderListener != null) {
            this.onConfirmCSOrder(order, position);
        }
    }

    private void onDelayConfirm(ServiceOrder order, int position) {
        if (this.onDelayConfirmListener != null) {
            this.onDelayConfirmListener.onDelayConfirm(order, position);
        }
    }

    private void onCommentCSOrder(CustomSetmealOrder customSetmealOrder, int position) {
        Intent intent = new Intent(context, CommentNewWorkActivity.class);
        intent.putExtra("is_custom_order", true);
        intent.putExtra("custom_order", (Serializable) customSetmealOrder);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    /**
     * 支付意向金后的后续支付
     *
     * @param serviceOrder
     */
    private void onPayAfterIntentMoney(ServiceOrder serviceOrder) {
        if (serviceOrder.getOrderSub()
                .getMerchant()
                .getPropertyId() != Merchant.PROPERTY_WEDDING_DRESS_PHOTO && serviceOrder
                .getWeddingTime() == null) {
            // 需要向检测婚期
            Toast.makeText(context, R.string.msg_service_order_no_wedding_time, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // 进入选择支付方式的支付页面
        Intent intent = new Intent(context, ServiceOrderPaymentActivity.class);
        intent.putExtra("order", serviceOrder);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    private void onPayRest(ServiceOrder serviceOrder, double money, int payType) {
        if (serviceOrder.getOrderSub()
                .getMerchant()
                .getPropertyId() == Merchant.PROPERTY_WEDDING_PLAN) {
            // 婚礼策划使用分笔支付
            Intent intent = new Intent(context, ServiceOrderPayRestActivity.class);
            intent.putExtra("order", serviceOrder);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        } else {
            onPay(serviceOrder, money, payType);
        }
    }

    private void onPay(ServiceOrder serviceOrder, double money, int payType) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_id", serviceOrder.getId());
            jsonObject.put("pay_type", payType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PayConfig.Builder builder = new PayConfig.Builder((Activity) context);
        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(context);
        List<String> payAgents = DataConfig.getServicePayAgents();
        if (!serviceOrder.getOrderSub()
                .getWork()
                .isInstallment() || payType == PayConfig.PAY_TYPE_DEPOSIT || payType == PayConfig
                .PAY_TYPE_INTENT) {
            // 移除分期支付
            payAgents.remove(PayAgent.XIAO_XI_PAY);
        }
        builder.payAgents(dataConfig != null ? dataConfig.getPayTypes() : null, payAgents);
        builder.params(jsonObject)
                .path(Constants.getAbsUrl(Constants.HttpPath.SERVICE_ORDER_PAYMENT))
                .price(money > 0 ? money : 0)
                .subscriber(paySubscriber)
                .build()
                .pay();
    }

    class CustomSetmealOrderViewHolder extends BaseViewHolder<BasicServiceOrderWrapper> {
        @BindView(R.id.content_layout)
        View contentLayout;
        @BindView(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @BindView(R.id.tv_order_status)
        TextView tvOrderStatus;
        @BindView(R.id.img_cover)
        RecyclingImageView imgCover;
        @BindView(R.id.img_custom)
        ImageView imgCustom;
        @BindView(R.id.img_cover_layout)
        RelativeLayout imgCoverLayout;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_refund_money)
        TextView tvRefundMoney;
        @BindView(R.id.tv_serve_time)
        TextView tvServeTime;
        @BindView(R.id.items_layout)
        LinearLayout itemsLayout;
        @BindView(R.id.allow_deposit_layout)
        LinearLayout allowDepositLayout;
        @BindView(R.id.tv_rest_to_pay)
        TextView tvRestToPay;
        @BindView(R.id.tv_total_money)
        TextView tvTotalMoney;
        @BindView(R.id.prices_layout)
        LinearLayout pricesLayout;
        @BindView(R.id.btn_nothing)
        Button btnNothing;
        @BindView(R.id.btn_action1)
        Button btnAction1;
        @BindView(R.id.btn_action2)
        Button btnAction2;
        @BindView(R.id.actions_layout)
        LinearLayout actionsLayout;

        CustomSetmealOrderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(
                final Context mContext,
                final BasicServiceOrderWrapper item,
                final int position,
                int viewType) {
            if (item == null) {
                return;
            }
            final CustomSetmealOrder cso = (CustomSetmealOrder) item.getOrder();
            if (cso == null) {
                return;
            }
            contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CustomSetmealOrderDetailActivity.class);
                    intent.putExtra("order", (Serializable) cso);
                    mContext.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            });
            tvOrderStatus.setText(cso.getStatusStr());
            tvMerchantName.setText(cso.getCustomSetmeal()
                    .getMerchant()
                    .getName());
            tvTitle.setText(cso.getCustomSetmeal()
                    .getTitle());
            tvPrice.setText(mContext.getString(R.string.label_price4,
                    Util.roundDownDouble2StringPositive(cso.getActualPrice())));
            Glide.with(mContext)
                    .load(ImageUtil.getImagePath(cso.getCustomSetmeal()
                            .getImgCover(), CommonUtil.dp2px(mContext, 160)))
                    .into(imgCover);

            if (cso.getWeddingTime() != null) {
                tvServeTime.setVisibility(View.VISIBLE);
                tvServeTime.setText(mContext.getString(R.string.label_serve_time,
                        cso.getWeddingTime()
                                .toString("yyyy-MM-dd")));
            } else {
                tvServeTime.setVisibility(View.GONE);
            }

            if (10 == cso.getStatus()) {
                // 等待商家接单
                tvServeTime.setVisibility(View.GONE);
                pricesLayout.setVisibility(View.GONE);
                actionsLayout.setVisibility(View.GONE);
                tvRefundMoney.setVisibility(View.GONE);
            } else if (11 == cso.getStatus()) {
                // 等待付款
                pricesLayout.setVisibility(View.VISIBLE);
                if (cso.isEarnest()) {
                    // 允许定金支付
                    tvRestToPay.setVisibility(View.VISIBLE);
                    tvRestToPay.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));
                    tvRestToPay.setText(Html.fromHtml(mContext.getString(R.string
                                    .label_allow_deposit3,
                            Util.roundDownDouble2StringPositive(cso.getEarnestMoney()))));
                } else {
                    // 不允许定金支付
                    tvRestToPay.setVisibility(View.GONE);
                }

                tvTotalMoney.setText(mContext.getString(R.string.label_total_price5,
                        Util.roundDownDouble2StringPositive(cso.getActualPrice() - cso
                                .getRedPacketMoney())));
                actionsLayout.setVisibility(View.VISIBLE);
                btnAction1.setText(R.string.label_cancel_order);
                btnAction2.setText(R.string.label_pay2);
                btnAction1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 取消定制套餐订单
                        onCancelCSOrder(cso, position);
                    }
                });
                btnAction2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 去付款
                        Intent intent = new Intent(mContext,
                                CustomSetmealOrderPaymentActivity.class);
                        intent.putExtra("order", (Serializable) cso);
                        intent.putExtra("is_pay_rest", false);
                        mContext.startActivity(intent);
                        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
                tvRefundMoney.setVisibility(View.GONE);
            } else if (91 == cso.getStatus() || 93 == cso.getStatus()) {
                // 订单关闭
                actionsLayout.setVisibility(View.GONE);
                pricesLayout.setVisibility(View.VISIBLE);
                tvRestToPay.setVisibility(View.GONE);
                if (cso.getPaidMoney() > 0) {
                    tvTotalMoney.setText(Html.fromHtml(mContext.getString(R.string.label_price10,
                            Util.roundDownDouble2StringPositive(cso.getPaidMoney()))));
                } else {
                    tvTotalMoney.setText(mContext.getString(R.string.label_total_price5,
                            Util.formatDouble2String(cso.getActualPrice() - cso.getRedPacketMoney
                                    ())));
                }

                tvRefundMoney.setVisibility(View.GONE);
            } else if (87 == cso.getStatus()) {
                // 已付款
                actionsLayout.setVisibility(View.VISIBLE);
                pricesLayout.setVisibility(View.VISIBLE);

                if (cso.isPayAll()) {
                    // 付款完成
                    tvRestToPay.setVisibility(View.GONE);
                    btnAction2.setText(R.string.label_confirm_service);
                    btnAction1.setVisibility(View.INVISIBLE);
                    btnAction2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onConfirmCSOrder(cso, position);
                        }
                    });
                    tvTotalMoney.setText(Html.fromHtml(mContext.getString(R.string.label_price10,
                            Util.roundDownDouble2StringPositive(cso.getPaidMoney()))));
                } else {
                    // 还需继续支付
                    tvTotalMoney.setText(Html.fromHtml(mContext.getString(R.string.label_price10,
                            Util.roundDownDouble2StringPositive(cso.getPaidMoney()))));
                    tvRestToPay.setVisibility(View.VISIBLE);
                    tvRestToPay.setTextColor(ContextCompat.getColor(mContext,
                            R.color.colorPrimary));
                    tvRestToPay.setText(mContext.getString(R.string.label_rest_to_pay2,
                            Util.roundDownDouble2StringPositive(cso.getActualPrice() - cso
                                    .getRedPacketMoney() - cso.getPaidMoney())));
                    btnAction2.setText(R.string.label_continue_pay);
                    btnAction1.setVisibility(View.INVISIBLE);
                    btnAction2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 继续付余款
                            Intent intent = new Intent(mContext,
                                    CustomSetmealOrderPaymentActivity.class);
                            intent.putExtra("order", (Serializable) cso);
                            intent.putExtra("is_pay_rest", true);
                            mContext.startActivity(intent);
                            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    });
                }
                tvRefundMoney.setVisibility(View.GONE);
            } else if (cso.getStatus() == 90 || cso.getStatus() == 92) {
                // 交易完成,
                // 已付款
                pricesLayout.setVisibility(View.VISIBLE);
                tvTotalMoney.setText(Html.fromHtml(mContext.getString(R.string.label_price10,
                        Util.roundDownDouble2StringPositive(cso.getActualPrice() - cso
                                .getRedPacketMoney()))));
                tvRestToPay.setVisibility(View.GONE);
                if (cso.getStatus() == 92) {
                    // 已评价
                    actionsLayout.setVisibility(View.GONE);
                } else {
                    // 待评价
                    actionsLayout.setVisibility(View.VISIBLE);
                    btnAction2.setText(R.string.label_comment);
                    btnAction1.setVisibility(View.INVISIBLE);
                    btnAction2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onCommentCSOrder(cso, position);
                        }
                    });
                }
                tvRefundMoney.setVisibility(View.GONE);
            } else if (cso.getStatus() == 20 || cso.getStatus() == 21 || cso.getStatus() == 23 ||
                    cso.getStatus() == 24) {
                pricesLayout.setVisibility(View.VISIBLE);
                tvTotalMoney.setText(Html.fromHtml(mContext.getString(R.string.label_price10,
                        Util.roundDownDouble2StringPositive(cso.getPaidMoney()))));
                tvRestToPay.setVisibility(View.GONE);
                actionsLayout.setVisibility(View.GONE);
                // 价格信息,多一个退款
                if (cso.getStatus() == 24) {
                    tvRefundMoney.setVisibility(View.VISIBLE);
                    tvRefundMoney.setText(mContext.getString(R.string.label_refunded_money4,
                            Util.roundDownDouble2StringPositive(cso.getCsoRefundInfo()
                                    .getPayMoney())));
                } else {
                    // 退款中不需要显示退款金额
                    tvRefundMoney.setVisibility(View.GONE);
                }
            }
        }
    }
}
