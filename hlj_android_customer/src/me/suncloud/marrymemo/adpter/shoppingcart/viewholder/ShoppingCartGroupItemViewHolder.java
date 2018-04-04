package me.suncloud.marrymemo.adpter.shoppingcart.viewholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.product.FreeShipping;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljtrackerlibrary.HljTracker;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.shoppingcard.FreeShippingWrapper;
import me.suncloud.marrymemo.model.shoppingcard.ShoppingCartGroup;
import me.suncloud.marrymemo.model.shoppingcard.ShoppingCartItem;
import me.suncloud.marrymemo.view.ShoppingCartActivity;
import me.suncloud.marrymemo.view.product.ShopProductDetailActivity;

/**
 * Created by jinxin on 2017/11/7 0007.
 */

public class ShoppingCartGroupItemViewHolder extends BaseViewHolder<FreeShippingWrapper> {

    @BindView(R.id.cb_item)
    CheckBox cbItem;
    @BindView(R.id.tv_invalid)
    TextView tvInvalid;
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_sku_info)
    TextView tvSkuInfo;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_market_price)
    TextView tvMarketPrice;
    @BindView(R.id.view_market_price)
    View viewMarketPrice;
    @BindView(R.id.subtract)
    ImageView subtract;
    @BindView(R.id.count)
    TextView count;
    @BindView(R.id.plus)
    ImageView plus;
    @BindView(R.id.quantity_set_layout)
    LinearLayout quantitySetLayout;
    @BindView(R.id.btn_delete)
    ImageButton btnDelete;
    @BindView(R.id.line_shop_product)
    View lineShopProduct;
    @BindView(R.id.line_top)
    View lineTop;
    @BindView(R.id.tv_shipping)
    TextView tvShipping;
    @BindView(R.id.tv_add_on)
    TextView tvAddOn;
    @BindView(R.id.img_arrow)
    ImageView imgArrow;
    @BindView(R.id.line_bottom)
    View lineBottom;
    @BindView(R.id.layout_shipping)
    LinearLayout layoutShipping;
    @BindView(R.id.layout_product)
    LinearLayout layoutProduct;

    private Context mContext;
    private onCartGroupItemClickListener onCartGroupItemClickListener;

    public ShoppingCartGroupItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        initTracker();
    }

    private void initTracker() {
        HljVTTagger.buildTagger(tvAddOn)
                .tagName("shipfee_tip_item")
                .hitTag();
    }

    @Override
    protected void setViewData(
            Context mContext, FreeShippingWrapper wrapper, int position, int viewType) {
    }

    public void setCartItem(
            ShoppingCartGroup shoppingCartGroup,
            final FreeShippingWrapper wrapper,
            int position,
            int size) {
        setCardItem(shoppingCartGroup, wrapper.cartItem);
        if (wrapper.isShowShipping && !shoppingCartGroup.isAllInvalid()) {
            lineShopProduct.setVisibility(View.GONE);
            layoutShipping.setVisibility(View.VISIBLE);
            lineBottom.setVisibility(position == size - 1 ? View.GONE : View.VISIBLE);
            if (wrapper.isFree) {
                //商家包邮
                tvAddOn.setText("已达成");
                tvShipping.setText("商家包邮");
                tvAddOn.setTextColor(mContext.getResources()
                        .getColor(R.color.colorSuccess));
                imgArrow.setVisibility(View.GONE);
            } else {
                FreeShipping freeShipping = wrapper.freeShipping;
                tvAddOn.setText(wrapper.isAchieve ? "已达成" : "去凑单");
                tvAddOn.setTextColor(wrapper.isAchieve ? mContext.getResources()
                        .getColor(R.color.colorSuccess) : mContext.getResources()
                        .getColor(R.color.colorPrimary));
                imgArrow.setVisibility(wrapper.isAchieve ? View.GONE : View.VISIBLE);
                String freeStr = null;
                if (freeShipping != null) {
                    if (freeShipping.getType() == 0) {
                        freeStr = "满" + CommonUtil.formatDouble2String(freeShipping.getMoney()) +
                                "元包邮";
                    } else if (freeShipping.getType() == 1) {
                        freeStr = "满" + freeShipping.getNum() + "件包邮";
                    }
                }
                tvShipping.setText(freeStr);
            }
        } else {
            layoutShipping.setVisibility(View.GONE);
            if (position == size - 1) {
                lineShopProduct.setVisibility(View.GONE);
            } else {
                lineShopProduct.setVisibility(View.VISIBLE);
            }
        }
        tvAddOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wrapper.isFree) {
                    //包邮了不需要在凑单
                    return;
                }
                if (!wrapper.isAchieve) {
                    //去凑单
                    if (onCartGroupItemClickListener != null) {
                        onCartGroupItemClickListener.addOn(wrapper.cartItem.getMerchantId(),
                                wrapper.type);
                    }
                }
            }
        });
    }

    private void setCardItem(
            final ShoppingCartGroup shoppingCartGroup, final ShoppingCartItem item) {
        cbItem.setChecked(shoppingCartGroup.isProductItemChecked(item.getId()));
        tvTitle.setText(item.getProduct()
                .getTitle());
        Glide.with(mContext)
                .load(ImagePath.buildPath(item.getProduct()
                        .getCoverPath())
                        .width(imgCover.getLayoutParams().width)
                        .height(imgCover.getLayoutParams().height)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(imgCover);

        if (item.isValid()) {
            layoutProduct.setBackgroundColor(mContext.getResources()
                    .getColor(R.color.colorWhite));
            tvSkuInfo.setText(mContext.getResources()
                    .getString(R.string.label_sku2,
                            item.getSku()
                                    .getName()));
            quantitySetLayout.setVisibility(View.VISIBLE);
            cbItem.setVisibility(View.VISIBLE);
            tvInvalid.setVisibility(View.GONE);

            if (item.getSku()
                    .getShowNum() > item.getQuantity()) {
                plus.setImageResource(R.drawable.icon_cross_add_primary_24_24);
            } else {
                plus.setImageResource(R.drawable.icon_cross_add_gray_24_24);
            }
            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getSku()
                            .getShowNum() >= item.getQuantity() + 1) {
                        new HljTracker.Builder(mContext).eventableId(item.getProduct()
                                .getId())
                                .eventableType("Article")
                                .additional(String.valueOf(item.getSku()
                                        .getId()))
                                .sid("AC1/C1")
                                .pos(3)
                                .desc("增加")
                                .build()
                                .send();
                        Log.e(ShoppingCartActivity.class.getSimpleName(),
                                "加的线程" + Thread.currentThread()
                                        .getName());
                        item.increaseQuantity();
                        count.setText(String.valueOf(item.getQuantity()));
                        if (item.getQuantity() > 1) {
                            subtract.setImageResource(R.drawable.icon_subtraction_primary_24_4);
                        } else {
                            subtract.setImageResource(R.drawable.icon_subtraction_gray_24_4);
                        }

                        if (onCartGroupItemClickListener != null) {
                            onCartGroupItemClickListener.onPlus(shoppingCartGroup, item);
                        }
                    } else {
                        Toast.makeText(mContext,
                                R.string.msg_cannot_add_more_quantity,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });

            if (item.getQuantity() > 1) {
                subtract.setImageResource(R.drawable.icon_subtraction_primary_24_4);
            } else {
                subtract.setImageResource(R.drawable.icon_subtraction_gray_24_4);
            }

            subtract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getQuantity() > 1) {
                        new HljTracker.Builder(mContext).eventableId(item.getProduct()
                                .getId())
                                .eventableType("Article")
                                .additional(String.valueOf(item.getSku()
                                        .getId()))
                                .sid("AC1/C1")
                                .pos(4)
                                .desc("减少")
                                .build()
                                .send();
                        Log.e(ShoppingCartActivity.class.getSimpleName(),
                                "减的线程" + Thread.currentThread()
                                        .getName());
                        item.decreaseQuantity();
                        count.setText(String.valueOf(item.getQuantity()));
                        if (item.getQuantity() > 1) {
                            subtract.setImageResource(R.drawable.icon_subtraction_primary_24_4);
                        } else {
                            subtract.setImageResource(R.drawable.icon_subtraction_gray_24_4);
                        }
                        if (onCartGroupItemClickListener != null) {
                            onCartGroupItemClickListener.onSubtract(shoppingCartGroup, item);
                        }
                    } else {
                        Toast.makeText(mContext,
                                R.string.msg_cannot_subtract_more_quantity,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });

            cbItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shoppingCartGroup.toggleProduct(item.getId());
                    new HljTracker.Builder(mContext).eventableId(item.getProduct()
                            .getId())
                            .eventableType("Article")
                            .additional(String.valueOf(item.getSku()
                                    .getId()))
                            .sid("AC1/C1")
                            .pos(1)
                            .desc("选中")
                            .build()
                            .send();
                    if (onCartGroupItemClickListener != null) {
                        onCartGroupItemClickListener.onCbItemClick(shoppingCartGroup);
                    }
                }
            });
        } else {
            layoutProduct.setBackgroundColor(mContext.getResources()
                    .getColor(R.color.color_white8));
            tvSkuInfo.setText(item.getReason());
            quantitySetLayout.setVisibility(View.GONE);
            tvInvalid.setVisibility(View.VISIBLE);
            cbItem.setVisibility(View.GONE);
        }
        setPrice(tvPrice,
                mContext.getString(R.string.label_price,
                        NumberFormatUtil.formatDouble2StringWithTwoFloat(item.getSku()
                                .getShowPrice())));
        if (item.getSku()
                .getMarketPrice() > 0) {
            tvMarketPrice.setVisibility(View.VISIBLE);
            viewMarketPrice.setVisibility(View.VISIBLE);
            String markerPrice = mContext.getString(R.string.label_price,
                    NumberFormatUtil.formatDouble2StringWithTwoFloat(item.getSku()
                            .getMarketPrice()));
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(CommonUtil.dp2px(mContext, 12));
            float length = paint.measureText(markerPrice);
            viewMarketPrice.getLayoutParams().width = (int) length;
            setPrice(tvMarketPrice, markerPrice);
        } else {
            viewMarketPrice.setVisibility(View.GONE);
            tvMarketPrice.setVisibility(View.GONE);
        }
        count.setText(String.valueOf(item.getQuantity()));
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCartGroupItemClickListener != null) {
                    onCartGroupItemClickListener.onDelete(shoppingCartGroup, item);
                }
            }
        });
        layoutProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getProduct() != null && item.getProduct()
                        .getId() > 0) {
                    Intent intent = new Intent(mContext, ShopProductDetailActivity.class);
                    intent.putExtra("id",
                            item.getProduct()
                                    .getId());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    private void setPrice(TextView tv, String price) {
        if (TextUtils.isEmpty(price)) {
            return;
        }
        if (price.length() > 2) {
            Spannable span = new SpannableString(price);
            span.setSpan(new AbsoluteSizeSpan(CommonUtil.dp2px(mContext, 10)),
                    span.length() - 2,
                    span.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(span);
        }
    }

    public void setOnCartGroupItemClickListener(
            ShoppingCartGroupItemViewHolder.onCartGroupItemClickListener
                    onCartGroupItemClickListener) {
        this.onCartGroupItemClickListener = onCartGroupItemClickListener;
    }

    public interface onCartGroupItemClickListener {
        void onCbItemClick(ShoppingCartGroup shoppingCartGroup);

        void onDelete(ShoppingCartGroup shoppingCartGroup, ShoppingCartItem item);

        void onPlus(ShoppingCartGroup shoppingCartGroup, ShoppingCartItem item);

        void onSubtract(ShoppingCartGroup shoppingCartGroup, ShoppingCartItem item);

        void addOn(long merchantId, long expressTemplateId);
    }
}
