package me.suncloud.marrymemo.adpter.work_case;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerMerchantViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;

/**
 * Created by mo_yu on 2017/9/7.本地服务频道CpmAdapter
 */
public class MerchantServiceCpmAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private ArrayList<Merchant> merchants;


    private static final int ITEM_TYPE = 1;
    private static final int DEFAULT_TYPE = 2;//空视图，防止出现无法解析的数据时，显示异常
    private static final int FOOTER_TYPE = 3;
    private static final int HEADER_TYPE = 4;

    private View footerView;
    private View headerView;

    public MerchantServiceCpmAdapter(
            Context context, ArrayList<Merchant> merchants) {
        this.context = context;
        this.merchants = merchants;
    }


    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_TYPE:
                return new ExtraBaseViewHolder(headerView);
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            case ITEM_TYPE:
                return new MerchantCpmViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.service_merchant_cpm_item, parent, false));
            default:
                return new ExtraBaseViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty_place_holder, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof MerchantCpmViewHolder) {
            holder.setView(context,
                    merchants.get(headerView == null ? position : position - 1),
                    position,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerView != null) {
            return HEADER_TYPE;
        } else if (footerView != null && position == getItemCount() - 1) {
            return FOOTER_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(merchants.size(),
                9) + (footerView == null ? 0 : 1) + (headerView == null ? 0 : 1);
    }

    class MerchantCpmViewHolder extends TrackerMerchantViewHolder {
        @BindView(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @BindView(R.id.star_rating_bar)
        RatingBar starRatingBar;
        @BindView(R.id.tv_merchant_gift)
        TextView tvMerchantGift;
        @BindView(R.id.tv_merchant_gift_content)
        TextView tvMerchantGiftContent;
        @BindView(R.id.merchant_cpm_item)
        LinearLayout merchantCpmItem;
        @BindView(R.id.img_merchant_logo)
        RoundedImageView imgMerchantLogo;

        int width;
        int logoWidth;

        @Override
        public String cpmSource() {
            return "merchant_serve_channel";
        }

        MerchantCpmViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.width = CommonUtil.dp2px(view.getContext(), 142);
            this.logoWidth = CommonUtil.dp2px(view.getContext(), 48);
            merchantCpmItem.getLayoutParams().width = width;
        }

        @Override
        public View trackerView() {
            return merchantCpmItem;
        }

        @Override
        protected void setViewData(
                final Context mContext, final Merchant item, int position, int viewType) {
            String url = ImagePath.buildPath(item.getLogoPath())
                    .width(logoWidth)
                    .cropPath();
            Glide.with(mContext)
                    .load(url)
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_empty_image))
                    .into(imgMerchantLogo);
            tvMerchantName.setText(item.getName());
            if (!CommonUtil.isEmpty(item.getShopGift())) {
                tvMerchantGift.setText("到店礼");
                tvMerchantGiftContent.setText(item.getShopGift());
                tvMerchantGift.setVisibility(View.VISIBLE);
            } else if (item.isCoupon() && item.getCouponCount() > 0) {
                tvMerchantGift.setText("优惠券");
                tvMerchantGiftContent.setText(context.getString(R.string
                                .label_coupon_received_count,
                        item.getCouponCount()));
                tvMerchantGift.setVisibility(View.VISIBLE);
            } else {
                tvMerchantGift.setVisibility(View.INVISIBLE);
                tvMerchantGiftContent.setText("");
            }

            starRatingBar.setRating(item.getCommentStatistics() == null ? 0 : (float) item
                    .getCommentStatistics()
                    .getScore());
            merchantCpmItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MerchantDetailActivity.class);
                    intent.putExtra("id", item.getId());
                    mContext.startActivity(intent);
                }
            });
        }
    }

}

