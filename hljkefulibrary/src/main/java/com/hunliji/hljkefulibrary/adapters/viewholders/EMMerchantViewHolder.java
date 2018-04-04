package com.hunliji.hljkefulibrary.adapters.viewholders;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljkefulibrary.R;
import com.hunliji.hljkefulibrary.moudles.EMChat;

/**
 * Created by wangtao on 2017/10/23.
 */

public class EMMerchantViewHolder extends EMChatMessageBaseViewHolder {


    private ImageView imgLogo;
    private TextView tvName;
    private TextView workCount;
    private TextView caseCount;
    private TextView shopGiftContent;
    private TextView costEffectiveContent;
    private View bondIcon;
    private View refundIcon;
    private View promiseIcon;
    private View freeIcon;
    private View shopGiftLayout;
    private View costEffectiveLayout;
    private int logoSize;

    public EMMerchantViewHolder(ViewGroup parent, boolean isReceive) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(isReceive ? R.layout.em_chat_merchant_left_item___kefu : R.layout
                                .em_chat_merchant_right_item___kefu,
                        parent,
                        false));
    }

    private EMMerchantViewHolder(View itemView) {
        super(itemView);
        itemView.findViewById(R.id.chat_merchant_item)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onChatClickListener != null) {
                            Merchant merchant = getItem().getMerchant();
                            if (merchant != null && merchant.getId() > 0) {
                                onChatClickListener.onMerchantClick(merchant);
                            }
                        }
                    }
                });
        imgLogo = (ImageView) itemView.findViewById(R.id.img_logo);
        tvName = (TextView) itemView.findViewById(R.id.tv_name);
        bondIcon = itemView.findViewById(R.id.bond_icon);
        refundIcon = itemView.findViewById(R.id.refund_icon);
        promiseIcon = itemView.findViewById(R.id.promise_icon);
        freeIcon = itemView.findViewById(R.id.free_icon);
        workCount = (TextView) itemView.findViewById(R.id.work_count);
        caseCount = (TextView) itemView.findViewById(R.id.case_count);
        shopGiftContent = (TextView) itemView.findViewById(R.id.shop_gift_content);
        costEffectiveContent = (TextView) itemView.findViewById(R.id.cost_effective_content);
        shopGiftLayout = itemView.findViewById(R.id.shop_gift_layout);
        costEffectiveLayout = itemView.findViewById(R.id.cost_effective_layout);
        logoSize = CommonUtil.dp2px(itemView.getContext(), 60);
    }

    @Override
    protected void setViewData(
            Context mContext, EMChat item, int position, int viewType) {
        Merchant merchant=item.getMerchant();
        tvName.setText(merchant.getName());
        bondIcon.setVisibility(merchant.getBondSign() != null ? View.VISIBLE : View.GONE);
        freeIcon.setVisibility(merchant.getActiveWorkCount() > 0 ? View.VISIBLE : View
                .GONE);
        promiseIcon.setVisibility(merchant.getMerchantPromise() != null && merchant
                .getMerchantPromise()
                .size() > 0 ? View.VISIBLE : View.GONE);
        refundIcon.setVisibility(merchant.getChargeBack() != null && merchant
                .getChargeBack()
                .size() > 0 ? View.VISIBLE : View.GONE);
        workCount.setText(workCount.getContext().getString(R.string.label_work_count___cm,
                merchant.getActiveWorkCount()));
        caseCount.setText(caseCount.getContext().getString(R.string.label_case_count___cm,
                merchant.getActiveCaseCount()));
        shopGiftContent.setText(merchant.getShopGift());
        shopGiftLayout.setVisibility(TextUtils.isEmpty(merchant.getShopGift()) ? View.GONE
                : View.VISIBLE);
        costEffectiveContent.setText(merchant.getCostEffective());
        costEffectiveLayout.setVisibility(TextUtils.isEmpty(merchant.getCostEffective()) ?
                View.GONE : View.VISIBLE);
        String url = ImagePath.buildPath(merchant.getLogoPath()).width(logoSize).cropPath();
        Glide.with(imgLogo).load(url).apply(new RequestOptions().fitCenter()
                .dontAnimate()
                .placeholder(R.mipmap.icon_avatar_primary))
                .into(imgLogo);
    }
}
