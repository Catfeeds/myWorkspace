package com.hunliji.marrybiz.adapter.market.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.MerchantProperty;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.orders.BdProduct;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.popuptip.PopupRule;
import com.hunliji.marrybiz.view.merchantservice.MarketingDetailActivity;
import com.hunliji.marrybiz.view.merchantservice.MerchantUltimateDetailActivity;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hua_rong on 2017/12/25
 */

public class MarketHeaderViewHolder extends BaseViewHolder<String> {

    @BindView(R.id.iv_flag_ship)
    RoundedImageView ivFlagShip;
    @BindView(R.id.iv_bond_plan)
    RoundedImageView ivBondPlan;
    @BindView(R.id.iv_market_yunke)
    RoundedImageView ivMarketYunke;
    @BindView(R.id.market_yunke_layout)
    View marketYunkeLayout;
    private MerchantUser user;

    public MarketHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        Context context = itemView.getContext();
        user = Session.getInstance()
                .getCurrentUser(context);
        int screenWidth = CommonUtil.getDeviceSize(context).x;
        int picWith = Math.round((screenWidth - CommonUtil.dp2px(context, 42)) / 2);
        int picHeight = Math.round(picWith * 20 / 37);
        ivFlagShip.getLayoutParams().width = picWith;
        ivFlagShip.getLayoutParams().height = picHeight;
        ivBondPlan.getLayoutParams().width = picWith;
        ivBondPlan.getLayoutParams().height = picHeight;
        ivMarketYunke.getLayoutParams().width = picWith;
        ivMarketYunke.getLayoutParams().height = picHeight;
        if (user != null && user.getProperty() != null && (user.getProperty()
                .getId() == Merchant.PROPERTY_WEDDING_PLAN || user.getProperty()
                .getId() == Merchant.PROPERTY_WEDDING_DRESS_PHOTO)) {
            marketYunkeLayout.setVisibility(View.VISIBLE);
        } else {
            marketYunkeLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setViewData(
            Context context, String item, int position, int viewType) {
        itemView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.iv_flag_ship)
    void onFlagShip(View view) {
        if (user == null) {
            return;
        }
        Activity activity = (Activity) view.getContext();
        PopupRule popupRule = PopupRule.getDefault();
        if (popupRule.showShopReview(activity, user)) {
            return;
        }
        Intent intent = new Intent(activity, MerchantUltimateDetailActivity.class);
        activity.startActivity(intent);
    }

    @OnClick(R.id.iv_bond_plan)
    void onBondPlan(View view) {
        if (user == null) {
            return;
        }
        Activity activity = (Activity) view.getContext();
        PopupRule popupRule = PopupRule.getDefault();
        if (popupRule.showShopReview(activity, user)) {
            return;
        }
        PopupRule.getDefault()
                .onBondPlanLayout(activity, user, null);
    }

    @OnClick(R.id.iv_market_yunke)
    void onMarketYunke(View view) {
        Activity activity = (Activity) view.getContext();
        Intent intent = new Intent(activity, MarketingDetailActivity.class);
        intent.putExtra(MarketingDetailActivity.ARG_PRODUCT_ID, BdProduct.YUN_KE);
        activity.startActivity(intent);
    }
}
