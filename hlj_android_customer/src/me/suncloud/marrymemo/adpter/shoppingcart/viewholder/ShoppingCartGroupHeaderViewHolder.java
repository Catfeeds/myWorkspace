package me.suncloud.marrymemo.adpter.shoppingcart.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljtrackerlibrary.HljTracker;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.shoppingcard.ShoppingCartGroup;
import me.suncloud.marrymemo.view.ProductMerchantActivity;

/**
 * Created by jinxin on 2017/11/7 0007.
 */

public class ShoppingCartGroupHeaderViewHolder extends BaseViewHolder<ShoppingCartGroup> {

    @BindView(R.id.cb_merchant)
    CheckBox cbMerchant;
    @BindView(R.id.img_merchant)
    ImageView imgMerchant;
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.shop_gift_right)
    ImageView shopGiftRight;
    @BindView(R.id.merchant_layout)
    LinearLayout merchantLayout;
    @BindView(R.id.tv_get_coupon)
    TextView tvGetCoupon;

    private Context mContext;
    private OnGroupHeaderClickListener onGroupHeaderClickListener;

    public ShoppingCartGroupHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
    }

    @Override
    protected void setViewData(
            final Context mContext, final ShoppingCartGroup cartGroup, int position, int viewType) {
        if (!cartGroup.isInvalidGroup()) {
            OnMerchantClickListener OnMerchantClickListener = new OnMerchantClickListener
                    (cartGroup);
            merchantLayout.setVisibility(View.VISIBLE);
            if (cartGroup.getMerchant() == null) {
                return;
            }
            tvMerchantName.setText(cartGroup.getMerchant()
                    .getName());
            if (cartGroup.isAllInvalid()) {
                cbMerchant.setVisibility(View.INVISIBLE);
            } else {
                cbMerchant.setVisibility(View.VISIBLE);
                cbMerchant.setChecked(cartGroup.isAllMerchantProductChecked());
                cbMerchant.setOnClickListener(OnMerchantClickListener);
            }
            tvGetCoupon.setVisibility((cartGroup.getCouponList() == null || cartGroup
                    .getCouponList()
                    .isEmpty()) ? View.GONE : View.VISIBLE);
            tvGetCoupon.setOnClickListener(OnMerchantClickListener);
            tvMerchantName.setOnClickListener(OnMerchantClickListener);
            shopGiftRight.setOnClickListener(OnMerchantClickListener);
        } else {
            merchantLayout.setVisibility(View.GONE);
        }
    }

    class OnMerchantClickListener implements View.OnClickListener {

        private ShoppingCartGroup group;

        public OnMerchantClickListener(ShoppingCartGroup group) {
            this.group = group;
        }

        @Override
        public void onClick(View v) {
            if (group.getMerchant() == null) {
                return;
            }
            int id = v.getId();
            switch (id) {
                case R.id.tv_merchant_name:
                case R.id.shop_gift_right:
                    Intent intent = new Intent(mContext, ProductMerchantActivity.class);
                    intent.putExtra("id",
                            group.getMerchant()
                                    .getId());
                    intent.putExtra("sid", "AC1/B1");
                    intent.putExtra("desc", "查看商家");
                    intent.putExtra("position", 2);
                    mContext.startActivity(intent);
                    break;
                case R.id.tv_get_coupon:
                    if(onGroupHeaderClickListener != null){
                        onGroupHeaderClickListener.onGetCoupon(group);
                    }
                    break;
                case R.id.cb_merchant:
                    new HljTracker.Builder(mContext).sid("AC1/B1")
                            .pos(1)
                            .desc("选中")
                            .build()
                            .send();
                    if (onGroupHeaderClickListener != null) {
                        onGroupHeaderClickListener.onCbMerchantClick(group);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void setOnGroupHeaderClickListener(
            OnGroupHeaderClickListener onGroupHeaderClickListener) {
        this.onGroupHeaderClickListener = onGroupHeaderClickListener;
    }

    public interface OnGroupHeaderClickListener {
        void onCbMerchantClick(ShoppingCartGroup cartGroup);
        void onGetCoupon(ShoppingCartGroup cartGroup);
    }
}
