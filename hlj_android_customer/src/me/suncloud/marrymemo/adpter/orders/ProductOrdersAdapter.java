package me.suncloud.marrymemo.adpter.orders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hunlijicalendar.ResizeAnimation;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.orders.ProductOrder;
import me.suncloud.marrymemo.model.orders.ProductRefundStatus;
import me.suncloud.marrymemo.model.orders.ProductSubOrder;
import me.suncloud.marrymemo.util.AnimUtil;
import me.suncloud.marrymemo.view.ShippingStatusActivity;
import me.suncloud.marrymemo.view.comment.CommentProductOrderActivity;

/**
 * Created by werther on 17/1/3.
 */

public class ProductOrdersAdapter extends RecyclerView.Adapter<BaseViewHolder<ProductOrder>> {

    private Context context;
    private ArrayList<ProductOrder> orders;
    private View headerView;
    private View footerView;
    private static final int ITEMS_LIMIT = 2;
    private int singleAppendHeight;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private SparseBooleanArray collapseFlags;
    private int itemViewHeight;

    private OnConfirmOrderListener onConfirmOrderListener;
    private OnDeleteOrderListener onDeleteOrderListener;
    private OnCancelOrderListener onCancelOrderListener;
    private OnItemClickListener onItemClickListener;
    private OnPayOrderListener onPayOrderListener;

    public ProductOrdersAdapter(
            Context context, ArrayList<ProductOrder> orders) {
        this.context = context;
        this.orders = orders;
        itemViewHeight = CommonUtil.dp2px(context, (float) 88.5);
        singleAppendHeight = CommonUtil.dp2px(context, 36);
        collapseFlags = new SparseBooleanArray();
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setOnConfirmOrderListener(OnConfirmOrderListener onConfirmOrderListener) {
        this.onConfirmOrderListener = onConfirmOrderListener;
    }

    public void setOnDeleteOrderListener(OnDeleteOrderListener onDeleteOrderListener) {
        this.onDeleteOrderListener = onDeleteOrderListener;
    }

    public void setOnCancelOrderListener(OnCancelOrderListener onCancelOrderListener) {
        this.onCancelOrderListener = onCancelOrderListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnPayOrderListener(OnPayOrderListener onPayOrderListener) {
        this.onPayOrderListener = onPayOrderListener;
    }

    @Override
    public BaseViewHolder<ProductOrder> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new ExtraViewHolder(headerView);
            case TYPE_ITEM:
                return new OrderViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.product_order_list_item, parent, false));
            case TYPE_FOOTER:
                return new ExtraViewHolder(footerView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<ProductOrder> holder, int position) {
        holder.setView(context, getItem(position), position, getItemViewType(position));
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
        } else {
            return TYPE_ITEM;
        }
    }

    public ProductOrder getItem(int position) {
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

    public void setItem(int position, ProductOrder order) {
        if (orders.isEmpty()) {
            return;
        }
        int headSize = 0;
        if (headerView != null) {
            headSize = 1;
        }
        if (position - headSize >= 0 && position - headSize < orders.size()) {
            orders.set(position - headSize, order);
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

    class OrderViewHolder extends BaseViewHolder<ProductOrder> {
        @BindView(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @BindView(R.id.tv_order_status)
        TextView tvOrderStatus;
        @BindView(R.id.items_layout)
        LinearLayout itemsLayout;
        @BindView(R.id.tv_rest_label)
        TextView tvRestLabel;
        @BindView(R.id.img_arrow)
        ImageView imgArrow;
        @BindView(R.id.collapse_button_layout)
        LinearLayout collapseButtonLayout;
        @BindView(R.id.line_layout)
        View lineLayout;
        @BindView(R.id.red_packet_layout)
        LinearLayout redPacketLayout;
        @BindView(R.id.tv_product_summation)
        TextView tvProductSummation;
        @BindView(R.id.tv_total_price)
        TextView tvTotalPrice;
        @BindView(R.id.btn_see_shipping)
        Button btnSeeShipping;
        @BindView(R.id.btn_confirm_receive)
        Button btnConfirmReceive;
        @BindView(R.id.btn_go_pay)
        Button btnGoPay;
        @BindView(R.id.btn_delete_order)
        Button btnDelete;
        @BindView(R.id.btn_review)
        Button btnReview;
        @BindView(R.id.btn_cancel_order)
        Button btnCancelOrder;
        @BindView(R.id.actions_layout)
        LinearLayout actionsLayout;

        private View view;

        OrderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }

        @Override
        protected void setViewData(
                Context mContext, final ProductOrder item, final int position, int viewType) {
            if (item == null) {
                return;
            }
            this.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition(), item);
                    }
                }
            });
            tvMerchantName.setText(item.getMerchant()
                    .getName());
            tvOrderStatus.setText(item.getStatusStr());
            if (TextUtils.isEmpty(item.getRedPacketNo())) {
                redPacketLayout.setVisibility(View.GONE);
            } else {
                redPacketLayout.setVisibility(View.VISIBLE);
            }

            itemsLayout.removeAllViews();
            int appendHeight = 0;
            int appendHeightAll = 0;
            for (int i = 0; i < item.getSubOrders()
                    .size(); i++) {
                int[] heights = addProductItemView(itemsLayout, item, i);
                appendHeight += heights[0];
                appendHeightAll += heights[1];
            }

            // 设置婚品展示个数限制，展开收起的动画和监听
            if (item.getSubOrders()
                    .size() > ITEMS_LIMIT) {
                collapseButtonLayout.setVisibility(View.VISIBLE);

                final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)
                        itemsLayout.getLayoutParams();
                if (collapseFlags.get(position)) {
                    params.height = itemViewHeight * item.getSubOrders()
                            .size() + appendHeightAll;
                    tvRestLabel.setText(context.getString(R.string.label_hide_rest_product,
                            item.getSubOrders()
                                    .size() - ITEMS_LIMIT));
                } else {
                    params.height = itemViewHeight * ITEMS_LIMIT + appendHeight;
                    tvRestLabel.setText(context.getString(R.string.label_show_rest_product,
                            item.getSubOrders()
                                    .size() - ITEMS_LIMIT));
                }

                collapseButtonLayout.setOnClickListener(new CollapseListener(position,
                        itemsLayout,
                        item.getSubOrders()
                                .size(),
                        imgArrow,
                        tvRestLabel,
                        appendHeight,
                        appendHeightAll));
            } else {
                collapseButtonLayout.setVisibility(View.GONE);
                final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)
                        itemsLayout.getLayoutParams();
                params.height = itemViewHeight * item.getSubOrders()
                        .size() + appendHeight;
            }

            String priceStr = context.getString(R.string.label_real_pay2,
                    context.getString(R.string.label_price,
                            NumberFormatUtil.formatDouble2StringWithTwoFloat(item.getActualMoney
                                    () + item.getShippingFee() - item.getRedPacketMoney()- item.getAidMoney()))) +
                    context.getString(
                    R.string.label_include_shipping_fee,
                    NumberFormatUtil.formatDouble2StringWithTwoFloat(item.getShippingFee()));

            tvTotalPrice.setText(priceStr);
            tvProductSummation.setText(context.getString(R.string.label_product_summation,
                    item.getSubOrders()
                            .size()));
            switch (item.getStatus()) {
                case ProductOrder.STATUS_WAITING_FOR_THE_PAYMENT:
                    // 代付款
                    actionsLayout.setVisibility(View.VISIBLE);
                    btnCancelOrder.setVisibility(View.VISIBLE);
                    btnGoPay.setVisibility(View.VISIBLE);
                    btnConfirmReceive.setVisibility(View.GONE);
                    btnReview.setVisibility(View.GONE);
                    btnSeeShipping.setVisibility(View.GONE);
                    btnDelete.setVisibility(View.GONE);
                    break;
                case ProductOrder.STATUS_WAITING_FOR_ACCEPT_SHIPPING:
                    // 待收货
                    actionsLayout.setVisibility(View.VISIBLE);
                    btnGoPay.setVisibility(View.GONE);
                    btnCancelOrder.setVisibility(View.GONE);
                    btnConfirmReceive.setVisibility(View.VISIBLE);
                    btnReview.setVisibility(View.GONE);
                    btnDelete.setVisibility(View.GONE);
                    if (item.getExpressId() > 0) {
                        btnSeeShipping.setVisibility(View.VISIBLE);
                    } else {
                        btnSeeShipping.setVisibility(View.GONE);
                    }
                    break;
                case ProductOrder.STATUS_ORDER_SUCCED:
                    // 交易完成,待评价
                    actionsLayout.setVisibility(View.VISIBLE);
                    btnGoPay.setVisibility(View.GONE);
                    btnConfirmReceive.setVisibility(View.GONE);
                    btnCancelOrder.setVisibility(View.GONE);
                    btnReview.setVisibility(View.VISIBLE);
                    btnSeeShipping.setVisibility(View.GONE);
                    btnDelete.setVisibility(View.GONE);
                    break;
                case ProductOrder.STATUS_ORDER_CLOSED:
                case ProductOrder.STATUS_AUTO_CLOSED:
                    actionsLayout.setVisibility(View.VISIBLE);
                    btnGoPay.setVisibility(View.GONE);
                    btnConfirmReceive.setVisibility(View.GONE);
                    btnReview.setVisibility(View.GONE);
                    btnSeeShipping.setVisibility(View.GONE);
                    btnCancelOrder.setVisibility(View.GONE);
                    btnDelete.setVisibility(View.VISIBLE);
                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (onDeleteOrderListener != null) {
                                onDeleteOrderListener.onDeleteOrder(item, getAdapterPosition());
                            }
                        }
                    });
                    break;
                default:
                    actionsLayout.setVisibility(View.GONE);
                    break;
            }

            btnSeeShipping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 查看物流
                    Intent intent = new Intent(context, ShippingStatusActivity.class);
                    intent.putExtra("order_id",item.getId());
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            });
            btnReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 评价
                    commentShipping(item);
                }
            });
            btnConfirmReceive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 确认收货
                    if (item.isRefunding()) {
                        Toast.makeText(context, R.string.msg_cannot_confirming, Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        if (onConfirmOrderListener != null) {
                            onConfirmOrderListener.onConfirmOrder(item, getAdapterPosition());
                        }
                    }
                }
            });
            btnGoPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPayOrderListener != null) {
                        onPayOrderListener.onPay(item, getAdapterPosition());
                    }
                }
            });
            btnCancelOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCancelOrderListener.onCancel(item, getAdapterPosition());
                }
            });
        }

    }

    /**
     * // 填充婚品item
     *
     * @param itemsLayout
     * @param order
     * @param i
     */
    private int[] addProductItemView(
            LinearLayout itemsLayout, ProductOrder order, int i) {
        ProductSubOrder subOrder = order.getSubOrders()
                .get(i);
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.product_order_item, null);
        SubOrderViewHolder holder = new SubOrderViewHolder(itemView);

        holder.tvTitle.setText(subOrder.getProduct()
                .getTitle());
        Glide.with(context)
                .load(ImageUtil.getImagePath(subOrder.getProduct()
                        .getCoverPath(), holder.imgCover.getLayoutParams().width))
                .into(holder.imgCover);
        // 退款信息
        holder.refundInfoLayout.setVisibility(View.VISIBLE);
        holder.tvRefundInfo.setVisibility(View.VISIBLE);

        switch (subOrder.getRefundStatus()) {
            case ProductRefundStatus.NO_REFUND:
                holder.refundInfoLayout.setVisibility(View.GONE);
                break;
            case ProductRefundStatus.OLD_REFUND_PROCESSING:
                // 退款申请中
                holder.tvRefundInfo.setText(context.getString(R.string.label_refund_processing,
                        NumberFormatUtil.formatDouble2StringWithTwoFloat(subOrder.getRefundInfo()
                                .getPayMoney())));
                break;
            case ProductRefundStatus.OLD_REFUND_SUCCESS:
                // 退款成功
                holder.tvRefundInfo.setText(context.getString(R.string.label_refunded_money,
                        NumberFormatUtil.formatDouble2StringWithTwoFloat(subOrder.getRefundInfo()
                                .getPayMoney())));
                break;
            case ProductRefundStatus.OLD_REFUND_FAIL:
                holder.refundInfoLayout.setVisibility(View.GONE);
                break;
            case ProductRefundStatus.MERCHANT_HANDLING_REFUND:
                holder.tvRefundInfo.setText(R.string.label_refund_reviewing);
                break;
            case ProductRefundStatus.MERCHANT_HANDLING_RETURN:
                holder.tvRefundInfo.setText(R.string.label_return_reviewing);
                break;
            case ProductRefundStatus.REFUND_DECLINED:
                holder.tvRefundInfo.setText(R.string.label_refund_declined);
                break;
            case ProductRefundStatus.RETURN_DECLINED:
                holder.tvRefundInfo.setText(R.string.label_return_declined);
                break;
            case ProductRefundStatus.BUYER_RETURN_PRODUCT:
                holder.tvRefundInfo.setText(R.string.label_return_product2);
                break;
            case ProductRefundStatus.MERCHANT_CONFIRMING:
                holder.tvRefundInfo.setText(R.string.label_merchant_confirming);
                break;
            case ProductRefundStatus.MERCHANT_PRODUCT_UNRECEIVED:
                holder.tvRefundInfo.setText(R.string.label_merchant_unreceived);
                break;
            case ProductRefundStatus.REFUND_COMPLETE:
                holder.tvRefundInfo.setText(context.getString(R.string.label_refunded_money,
                        NumberFormatUtil.formatDouble2StringWithTwoFloat(subOrder.getRefundInfo()
                                .getPayMoney())));
                break;
            case ProductRefundStatus.REFUND_CANCELED:
                if (order.getStatus() == 89) {
                    // 待发货状态下的退货取消
                    holder.tvRefundInfo.setText(R.string.label_return_canceled);
                } else {
                    holder.tvRefundInfo.setText(R.string.label_refund_canceled);
                }
                break;
            case ProductRefundStatus.REFUND_CLOSED:
                if (order.getStatus() == 89) {
                    // 待发货状态下的退货取消
                    holder.tvRefundInfo.setText(R.string.label_return_closed2);
                } else {
                    holder.tvRefundInfo.setText(R.string.label_refund_closed);
                }
                break;
        }

        holder.tvSkuInfo.setText(context.getString(R.string.label_sku2,
                subOrder.getSku()
                        .getName()));
        holder.tvPrice.setText(context.getString(R.string.label_price,
                NumberFormatUtil.formatDouble2StringWithTwoFloat(subOrder.getActualMoney() /
                        subOrder.getQuantity())));
        holder.tvQuantity.setText("x" + String.valueOf(subOrder.getQuantity()));

        itemsLayout.addView(itemView);

        int appendHeight = 0;
        int appendHeightAll = 0;
        if (order.getStatus() == 10) {
            if (subOrder.getActivityStatus() == 2) {
                holder.tvActivityOver.setVisibility(View.VISIBLE);
                if (i < ITEMS_LIMIT) {
                    appendHeight += singleAppendHeight;
                }

                appendHeightAll += singleAppendHeight;
            } else {
                holder.tvActivityOver.setVisibility(View.GONE);
            }
        } else {
            holder.tvActivityOver.setVisibility(View.GONE);
        }

        return new int[]{appendHeight, appendHeightAll};
    }

    private void commentShipping(ProductOrder order) {
        if (order == null || order.getSubOrders() == null || order.getSubOrders().isEmpty()) {
            return;
        }
        Intent intent = new Intent(context, CommentProductOrderActivity.class);
        intent.putExtra(CommentProductOrderActivity.ARG_PRODUCT_ORDER, order);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,R.anim.activity_anim_default);
    }

    private class CollapseListener implements View.OnClickListener {
        int position;
        View itemsLayout;
        int itemsCount;
        View imgView;
        TextView collapseLabel;
        int appendHeight;
        int appendHeightAll;

        public CollapseListener(int p, View v, int c, View v2, TextView label, int ah, int aha) {
            position = p;
            itemsLayout = v;
            itemsCount = c;
            imgView = v2;
            collapseLabel = label;
            appendHeight = ah;
            appendHeightAll = aha;
        }

        @Override
        public void onClick(View v) {
            ResizeAnimation itemsRA;
            if (collapseFlags.get(position)) {
                // 已经展开过,现在收起
                imgView.startAnimation(AnimUtil.getAnimArrowDown(context));

                itemsRA = new ResizeAnimation(itemsLayout,
                        itemViewHeight * ITEMS_LIMIT + appendHeight);
                itemsRA.setDuration(100);
                itemsRA.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        collapseFlags.put(position, false);
                        collapseLabel.setText(context.getString(R.string.label_show_rest_product,
                                itemsCount - ITEMS_LIMIT));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            } else {
                // 没有展开过,现在展开
                imgView.startAnimation(AnimUtil.getAnimArrowUp(context));

                itemsRA = new ResizeAnimation(itemsLayout,
                        itemViewHeight * itemsCount + appendHeightAll);
                itemsRA.setDuration(100);
                itemsRA.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        collapseFlags.put(position, true);
                        collapseLabel.setText(context.getString(R.string.label_hide_rest_product,
                                itemsCount - ITEMS_LIMIT));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            itemsLayout.startAnimation(itemsRA);
        }
    }

    static class SubOrderViewHolder {
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.layout1)
        LinearLayout layout1;
        @BindView(R.id.tv_sku_info)
        TextView tvSkuInfo;
        @BindView(R.id.tv_quantity)
        TextView tvQuantity;
        @BindView(R.id.layout2)
        LinearLayout layout2;
        @BindView(R.id.tv_refund_info)
        TextView tvRefundInfo;
        @BindView(R.id.btn_refund_status)
        Button btnRefundStatus;
        @BindView(R.id.layout3)
        LinearLayout refundInfoLayout;
        @BindView(R.id.tv_activity_over)
        TextView tvActivityOver;

        SubOrderViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    public interface OnConfirmOrderListener {
        void onConfirmOrder(ProductOrder order, int position);
    }

    public interface OnDeleteOrderListener {
        void onDeleteOrder(ProductOrder order, int position);
    }

    public interface OnCancelOrderListener {
        void onCancel(ProductOrder order, int position);
    }

    public interface OnPayOrderListener {
        void onPay(ProductOrder order, int position);
    }
}
