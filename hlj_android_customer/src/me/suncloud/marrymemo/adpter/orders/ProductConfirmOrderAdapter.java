package me.suncloud.marrymemo.adpter.orders;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljimagelibrary.adapters.viewholders.ExtraViewHolder;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.shoppingcard.ShoppingCartGroup;
import me.suncloud.marrymemo.model.shoppingcard.ShoppingCartItem;
import me.suncloud.marrymemo.model.wallet.CouponRecord;
import me.suncloud.marrymemo.model.wallet.MerchantCouponList;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Util;

/**
 * Created by mo_yu on 2017/11/9.确认婚品订单
 */

public class ProductConfirmOrderAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private Context context;
    private ArrayList<ShoppingCartGroup> cartGroups;
    private View headerView;
    private View footerView;
    private HashMap<Long, MerchantCouponList> merchantCouponMap;
    private boolean isShippingFeeError;
    private OnRefreshListener onRefreshListener;

    public ProductConfirmOrderAdapter(
            Context context, ArrayList<ShoppingCartGroup> cartGroups) {
        this.context = context;
        this.cartGroups = cartGroups;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public void setCartGroups(ArrayList<ShoppingCartGroup> cartGroups) {
        this.cartGroups = cartGroups;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setMerchantCouponMap(HashMap<Long, MerchantCouponList> merchantCouponMap) {
        this.merchantCouponMap = merchantCouponMap;
    }

    public void setShippingFeeError(boolean shippingFeeError) {
        isShippingFeeError = shippingFeeError;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new ExtraViewHolder(headerView);
            case TYPE_ITEM:
                return new OrderConfirmViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.shopping_cart_list_item2, parent, false));
            case TYPE_FOOTER:
                return new ExtraViewHolder(footerView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setView(context, getItem(position), position, getItemViewType(position));
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

    @Override
    public int getItemCount() {
        return (headerView == null ? 0 : 1) + (footerView == null ? 0 : 1) + (cartGroups == null
                ? 0 : cartGroups.size());
    }

    public ShoppingCartGroup getItem(int position) {
        if (cartGroups.isEmpty()) {
            return null;
        }
        int headSize = 0;
        if (headerView != null) {
            headSize = 1;
        }
        if (position - headSize >= 0 && position - headSize < cartGroups.size()) {
            return cartGroups.get(position - headSize);
        } else {
            return null;
        }
    }

    class OrderConfirmViewHolder extends BaseViewHolder<ShoppingCartGroup> {
        @BindView(R.id.merchant_layout)
        View merchantLayout;
        @BindView(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @BindView(R.id.items_layout)
        LinearLayout itemsLayout;
        @BindView(R.id.tv_total_price)
        TextView tvTotalPrice;
        @BindView(R.id.et_leave_memo)
        EditText etLeaveMemo;
        @BindView(R.id.merchant_coupon_layout)
        LinearLayout merchantCouponLayout;
        @BindView(R.id.tv_coupon_saved_money)
        TextView tvCouponSavedMoney;
        @BindView(R.id.shipping_fee_layout)
        LinearLayout shippingFeeLayout;
        @BindView(R.id.tv_shipping_fee)
        TextView tvShippingFee;
        @BindView(R.id.tv_merchant_coupon)
        TextView tvMerchantCoupon;
        private Dialog couponListDialog;
        private CouponsListAdapter couponsAdapter;
        private CouponRecord selectedCoupon;//当前选中的优惠券
        private CouponRecord pendingCoupon;//选中但未点击使用的优惠券
        double totalProductPrice;
        double couponValue;
        double shippingFee;
        CouponRecord unUserCoupon;

        OrderConfirmViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            unUserCoupon = new CouponRecord();
            unUserCoupon.setId(-1);
        }

        /**
         * @param mContext
         * @param group
         * @param position
         * @param viewType
         */
        @Override
        protected void setViewData(
                final Context mContext, final ShoppingCartGroup group, int position, int viewType) {
            final Activity activity = (Activity) mContext;
            OnEditTextChangeListener onEditTextChangeListener = new OnEditTextChangeListener(group);
            etLeaveMemo.addTextChangedListener(onEditTextChangeListener);
            tvMerchantName.setText(group.getMerchant()
                    .getName());
            itemsLayout.removeAllViews();
            // 设置邮费,默认为零,即免邮
            shippingFeeLayout.setVisibility(View.VISIBLE);
            if (!isShippingFeeError) {
                shippingFeeLayout.setVisibility(View.VISIBLE);
                if (group.getShippingFee() > 0) {
                    tvShippingFee.setText(mContext.getString(R.string.label_shipping_fee2,
                            NumberFormatUtil.formatDouble2StringWithTwoFloat(group.getShippingFee
                                    ())));
                } else {
                    tvShippingFee.setText(mContext.getString(R.string.label_shipping_fee2, "免邮"));
                }
            } else {
                shippingFeeLayout.setVisibility(View.GONE);
            }
            totalProductPrice = 0;
            couponValue = 0;
            shippingFee = group.getShippingFee();
            //优惠券
            MerchantCouponList merchantCouponList = merchantCouponMap.get(group.getMerchant()
                    .getId());
            if (merchantCouponList != null && !CommonUtil.isCollectionEmpty(merchantCouponList
                    .getCouponList())) {
                CouponRecord couponRecord = group.getCouponRecord();
                if (couponRecord == null) {
                    couponRecord = merchantCouponList.getCouponList()
                            .get(0);
                    group.setCouponRecord(couponRecord);
                }
                selectedCoupon = couponRecord;
                couponValue = couponRecord.getValue();
                refreshCouponItemView(couponRecord,
                        merchantCouponList.getCouponList()
                                .size());

                /***=======================统计=====================================****/
                HljVTTagger.buildTagger(merchantCouponLayout)
                        .tagName("merchant_coupon_choose_item")
                        .dataType("Merchant")
                        .dataId(group.getMerchant()
                                .getId())
                        .hitTag();
                /***========================================================**/

                merchantCouponLayout.setVisibility(View.VISIBLE);
                merchantCouponLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCoupon(group, mContext);
                    }
                });
            } else {
                merchantCouponLayout.setVisibility(View.GONE);
            }

            merchantLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //                    Intent intent = new Intent(mContext,
                    // ProductMerchantActivity.class);
                    //                    intent.putExtra("id",
                    //                            group.getMerchantComment()
                    //                                    .getId());
                    //                    mContext.startActivity(intent);
                    //                    activity.overridePendingTransition(R.anim.slide_in_right,
                    //                            R.anim.activity_anim_default);
                }
            });
            for (final ShoppingCartItem item : group.getCartList()) {
                View itemView = View.inflate(mContext, R.layout.shopping_cart_item2, null);
                TextView tvProductTitle = itemView.findViewById(R.id.tv_title);
                ImageView imgCover = itemView.findViewById(R.id.img_cover);
                TextView tvSkuInfo = itemView.findViewById(R.id.tv_sku_info);
                TextView tvPrice = itemView.findViewById(R.id.tv_price);
                TextView tvQuantity = itemView.findViewById(R.id.tv_quantity);

                tvProductTitle.setText(item.getProduct()
                        .getTitle());

                String url = JSONUtil.getImagePath2(item.getProduct()
                        .getCoverPath(), imgCover.getLayoutParams().width);
                if (!JSONUtil.isEmpty(url)) {
                    imgCover.setTag(url);
                    ImageLoadTask task = new ImageLoadTask(imgCover, null, 0);
                    task.loadImage(url,
                            imgCover.getLayoutParams().width,
                            ScaleMode.WIDTH,
                            new AsyncBitmapDrawable(mContext.getResources(),
                                    R.mipmap.icon_empty_image,
                                    task));
                } else {
                    imgCover.setImageBitmap(null);
                }

                tvSkuInfo.setText(mContext.getString(R.string.label_sku2,
                        item.getSku()
                                .getName()));
                tvPrice.setText(mContext.getString(R.string.label_price,
                        NumberFormatUtil.formatDouble2StringWithTwoFloat(item.getSku()
                                .getShowPrice())));
                tvQuantity.setText("x" + String.valueOf(item.getQuantity()));

                totalProductPrice += item.getSku()
                        .getShowPrice() * item.getQuantity();
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //                        Intent intent = new Intent(mContext,
                        // ProductDetailActivity.class);
                        //                        intent.putExtra("id",
                        //                                item.getProduct()
                        //                                        .getId());
                        //                        activity.startActivity(intent);
                        //                        activity.overridePendingTransition(R.anim
                        // .slide_in_right,
                        //                                R.anim.activity_anim_default);
                    }
                });

                itemsLayout.addView(itemView);
            }
            refreshTotalGroupPrice(mContext);
            onEditTextChangeListener.setGroup(group);
            etLeaveMemo.setText(group.getLeaveMessage());
        }

        private class OnEditTextChangeListener implements TextWatcher {

            private ShoppingCartGroup group;

            public OnEditTextChangeListener(ShoppingCartGroup group) {
                this.group = group;
            }

            public void setGroup(ShoppingCartGroup group) {
                this.group = group;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString()
                        .equals(group.getLeaveMessage())) {
                    group.setLeaveMessage(s.toString());
                }
            }
        }

        void onCoupon(final ShoppingCartGroup group, final Context context) {
            if (couponListDialog != null && couponListDialog.isShowing()) {
                return;
            }
            final ArrayList<CouponRecord> couponRecords = new ArrayList<>();
            if (merchantCouponMap.get(group.getMerchant()
                    .getId()) != null) {
                couponRecords.addAll(merchantCouponMap.get(group.getMerchant()
                        .getId())
                        .getCouponList());
                couponRecords.add(unUserCoupon);
            }
            if (couponsAdapter == null) {
                couponsAdapter = new CouponsListAdapter(couponRecords);
            } else {
                couponsAdapter.notifyDataSetChanged();
            }
            selectedCoupon = group.getCouponRecord();
            couponListDialog = DialogUtil.createdCouponListDialog(couponListDialog,
                    context,
                    couponsAdapter,
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(
                                AdapterView<?> parent, View view, int position, long id) {
                            pendingCoupon = (CouponRecord) parent.getAdapter()
                                    .getItem(position);
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            couponListDialog.cancel();
                            CouponRecord oldSelectedCoupon = selectedCoupon;
                            if (pendingCoupon != null) {
                                selectedCoupon = pendingCoupon;
                            }
                            group.setCouponRecord(selectedCoupon);
                            // 选择不同的优惠券后需要重新刷新红包列表
                            if (oldSelectedCoupon == null || selectedCoupon == null ||
                                    oldSelectedCoupon.getId() != selectedCoupon.getId()) {
                                if (onRefreshListener != null) {
                                    onRefreshListener.onRefresh();
                                }
                            }
                            //减去不使用优惠券这一项
                            refreshCouponItemView(selectedCoupon, couponRecords.size() - 1);
                            refreshTotalGroupPrice(context);
                        }
                    },
                    couponRecords.indexOf(selectedCoupon));
            couponListDialog.show();
            couponsAdapter.notifyDataSetChanged();
        }

        void refreshCouponItemView(CouponRecord selectedCoupon, int couponCount) {
            if (selectedCoupon == null || selectedCoupon.getId() < 0) {
                tvCouponSavedMoney.setText(context.getString(R.string.label_available_packet_count2,
                        couponCount));
                tvCouponSavedMoney.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
                tvMerchantCoupon.setText(context.getString(R.string.label_merchant_coupon));
                couponValue = 0;
                return;
            } else {
                couponValue = selectedCoupon.getValue();
            }
            String moneySillStr;
            if (selectedCoupon.getMoneySill() > 0) {
                moneySillStr = "满" + Util.formatDouble2String(selectedCoupon.getMoneySill()) +
                        "减" + Util.formatDouble2String(
                        selectedCoupon.getValue());
            } else {
                moneySillStr = "下单减" + Util.formatDouble2String(selectedCoupon.getValue()) + "元";
            }
            tvMerchantCoupon.setText(context.getString(R.string.label_merchant_coupon_price,
                    moneySillStr));
            tvCouponSavedMoney.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }

        void refreshTotalGroupPrice(Context context) {
            double totalGroupPrice = totalProductPrice - couponValue;
            if (totalGroupPrice < 0) {
                totalGroupPrice = 0;
                if (selectedCoupon != null && selectedCoupon.getId() > 0) {
                    tvCouponSavedMoney.setText("省" + Util.formatDouble2String(totalProductPrice)
                            + "元");
                }
            } else {
                if (selectedCoupon != null && selectedCoupon.getId() > 0) {
                    tvCouponSavedMoney.setText("省" + Util.formatDouble2String(couponValue) + "元");
                }
            }

            totalGroupPrice = totalGroupPrice + shippingFee;
            tvTotalPrice.setText(context.getString(R.string.label_price,
                    NumberFormatUtil.formatDouble2StringWithTwoFloat(totalGroupPrice)));
        }
    }
}
