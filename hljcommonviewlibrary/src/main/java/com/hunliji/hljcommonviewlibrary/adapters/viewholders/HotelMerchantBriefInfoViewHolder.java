package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.merchant.Hotel;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 酒店商家的部分信息viewHolder
 * Created by chen_bin on 2017/10/18 0018.
 */
public class HotelMerchantBriefInfoViewHolder extends BaseViewHolder<Merchant> {
    @BindView(R2.id.img_merchant_logo)
    RoundedImageView imgMerchantLogo;
    @BindView(R2.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R2.id.tv_table_num)
    TextView tvTableNum;
    @BindView(R2.id.tv_address)
    TextView tvAddress;
    private int logoSize;

    public HotelMerchantBriefInfoViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.logoSize = CommonUtil.dp2px(itemView.getContext(), 60);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Merchant merchant = getItem();
                if (merchant != null && merchant.getId() > 0) {
                    ARouter.getInstance()
                            .build(RouterPath.IntentPath.Customer.MERCHANT_HOME)
                            .withLong("id", merchant.getId())
                            .navigation(v.getContext());
                }
            }
        });
    }

    @Override
    protected void setViewData(Context mContext, Merchant merchant, int position, int viewType) {
        if (merchant == null) {
            return;
        }
        Glide.with(mContext)
                .load(ImagePath.buildPath(merchant.getLogoPath())
                        .width(logoSize)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_avatar_primary)
                        .error(R.mipmap.icon_avatar_primary))
                .into(imgMerchantLogo);
        tvMerchantName.setText(merchant.getName());
        tvAddress.setText(merchant.getAddress());
        Hotel hotel = merchant.getHotel();
        tvTableNum.setText(mContext.getString(R.string.label_table_num___cv,
                hotel == null ? 0 : hotel.getTableNum()));
    }
}
