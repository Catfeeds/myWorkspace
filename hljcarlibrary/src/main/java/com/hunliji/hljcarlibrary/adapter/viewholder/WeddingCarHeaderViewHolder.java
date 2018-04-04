package com.hunliji.hljcarlibrary.adapter.viewholder;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcarlibrary.HljCar;
import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcarlibrary.adapter.WeddingCarHeaderImageAdapter;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Rule;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarOrder;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarSku;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.WeddingCarRouteService;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.slider.library.Indicators.CirclePageIndicator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jinxin on 2017/12/26 0026.
 */

public class WeddingCarHeaderViewHolder extends BaseViewHolder<WeddingCarProduct> {

    @BindView(R2.id.view_pager)
    ViewPager viewPager;
    @BindView(R2.id.flow_indicator)
    CirclePageIndicator flowIndicator;
    @BindView(R2.id.tv_release_count)
    TextView tvReleaseCount;
    @BindView(R2.id.tv_notice)
    TextView tvNotice;
    @BindView(R2.id.tv_rule_price)
    TextView tvRulePrice;
    @BindView(R2.id.tv_rule_show_price)
    TextView tvRuleShowPrice;
    @BindView(R2.id.layout_miao_sha)
    LinearLayout layoutMiaoSha;
    @BindView(R2.id.tv_lead_car)
    TextView tvLeadCar;
    @BindView(R2.id.tv_follow_car)
    TextView tvFollowCar;
    @BindView(R2.id.tv_actual_price)
    TextView tvShowPrice;
    @BindView(R2.id.img_discounts_type)
    ImageView imgDiscountsType;
    @BindView(R2.id.tv_market_price)
    TextView tvMarketPrice;
    @BindView(R2.id.tv_price_des)
    TextView tvPriceDes;
    @BindView(R2.id.tv_member)
    TextView tvMember;
    @BindView(R2.id.layout_member)
    LinearLayout layoutMember;
    @BindView(R2.id.layout_price)
    LinearLayout layoutPrice;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.layout_main_car)
    LinearLayout layoutMainCar;
    @BindView(R2.id.layout_follow_car)
    LinearLayout layoutFollowCar;
    @BindView(R2.id.layout_header_photo)
    RelativeLayout layoutHeaderPhoto;

    private WeddingCarHeaderImageAdapter carHeaderImageAdapter;
    private Context mContext;
    private WeddingCarRouteService service;
    private int width;
    private int height;
    private WeddingCarProduct carProduct;

    public WeddingCarHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        mContext = itemView.getContext();
        tvMarketPrice.getPaint()
                .setAntiAlias(true);//抗锯齿
        tvMarketPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tvRuleShowPrice.getPaint()
                .setAntiAlias(true);
        tvRuleShowPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        carHeaderImageAdapter = new WeddingCarHeaderImageAdapter(mContext, null);
        viewPager.setAdapter(carHeaderImageAdapter);
        flowIndicator.setViewPager(viewPager);
        service = (WeddingCarRouteService) ARouter.getInstance()
                .build(RouterPath.ServicePath.WEDDING_CAR_SERVICE)
                .navigation();
        width = CommonUtil.getDeviceSize(mContext).x;
        height = Math.round(width * 9.0F / 16);
        layoutHeaderPhoto.getLayoutParams().height = height;
    }


    @Override
    protected void setViewData(
            Context mContext, WeddingCarProduct carProduct, int position, int viewType) {
        this.carProduct = carProduct;
        if (carProduct == null) {
            return;
        }
        carHeaderImageAdapter.setPhotos(carProduct.getHeaderPhotos());
        if (carProduct.getType() == WeddingCarProduct.TYPE_SELF) {
            layoutMainCar.setVisibility(View.GONE);
            layoutFollowCar.setVisibility(View.GONE);
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(carProduct.getTitle());
        } else if (carProduct.getType() == WeddingCarProduct.TYPE_WORK) {
            layoutMainCar.setVisibility(View.VISIBLE);
            layoutFollowCar.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.GONE);
            tvLeadCar.setText(TextUtils.isEmpty(carProduct.getMainCar()) ? carProduct.getTitle()
                    : carProduct.getMainCar());
            tvFollowCar.setText(carProduct.getShowSubCarTitle());
        }

        if (carProduct.isMiaoSha()) {
            layoutMiaoSha.setVisibility(View.VISIBLE);
            layoutPrice.setVisibility(View.GONE);
            tvRulePrice.setText(mContext.getString(R.string.label_price4___car,
                    NumberFormatUtil.formatDouble2String(carProduct.getShowPrice())));
            tvRuleShowPrice.setText(mContext.getString(R.string.label_market_price2___car,
                    NumberFormatUtil.formatDouble2String(carProduct.getMarketPrice())));
        } else {
            layoutMiaoSha.setVisibility(View.GONE);
            layoutPrice.setVisibility(View.VISIBLE);
            tvShowPrice.setText(mContext.getString(R.string.label_price4___car,
                    NumberFormatUtil.formatDouble2String(carProduct.getShowPrice())));
            tvMarketPrice.setText(mContext.getString(R.string.label_market_price1___car,
                    NumberFormatUtil.formatDouble2String(carProduct.getMarketPrice())));
        }

        Rule rule = carProduct.getRule();
        if (rule != null) {
            if (rule.getType() != 2) {
                //不是限量活动
                tvReleaseCount.setVisibility(ViewGroup.GONE);
            } else {
                tvReleaseCount.setVisibility(ViewGroup.VISIBLE);
                List<WeddingCarSku> skus = carProduct.getSkus();
                int count = 0;
                if (skus != null) {
                    for (WeddingCarSku sku : skus) {
                        int limit = sku.getLimitNum() - sku.getLimitSoldOut();
                        count += limit;
                    }
                    tvReleaseCount.setText("仅剩" + count + "份");
                } else {
                    tvReleaseCount.setVisibility(ViewGroup.GONE);
                }
            }
        }

        WeddingCarOrder carLastOrder = carProduct.getLastOrder();
        if (carLastOrder == null) {
            tvNotice.setVisibility(ViewGroup.GONE);
        } else {
            String text = "最新订单来自" + carLastOrder.getBuyerPhone() + "," + carLastOrder
                    .getCreatedAt() + "秒前";
            tvNotice.setText(text);
            tvNotice.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showNoticeAnimation();
                }
            }, 1700);
        }

        CustomerUser customerUser = (CustomerUser) UserSession.getInstance()
                .getUser(mContext);
        if (customerUser != null && customerUser.getMember() != null) {
            layoutMember.setVisibility(View.GONE);
        } else {
            if (service != null) {
                String memberRemind = service.getMemberRemind(mContext);
                tvMember.setText(memberRemind);
                layoutMember.setVisibility(TextUtils.isEmpty(memberRemind) ? View.GONE : View
                        .VISIBLE);
            }
        }
    }


    private void showNoticeAnimation() {
        if (carProduct == null || carProduct.isShowBuyerAnim()) {
            return;
        }
        carProduct.setShowBuyerAnim(true);
        tvNotice.setVisibility(View.VISIBLE);

        ValueAnimator animator = ValueAnimator.ofFloat(tvNotice.getTranslationY(),
                -(height - CommonUtil.dp2px(mContext, 20)));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (tvNotice == null || ((Activity) mContext).isFinishing()) {
                    return;
                }
                float y = (float) animation.getAnimatedValue();
                tvNotice.setTranslationY(y);
            }
        });
        animator.setDuration(1700);
        AnimatorSet set = new AnimatorSet();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (tvNotice == null || ((Activity) mContext).isFinishing()) {
                    return;
                }
                tvNotice.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (tvNotice != null && !((Activity) mContext).isFinishing()) {
                            tvNotice.setVisibility(View.GONE);
                        }
                    }
                }, getRandomShowTime() * 1000);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.play(animator);
        set.start();

    }


    private int getRandomShowTime() {
        return (int) (3 + Math.random() * (10 - 3 + 1));
    }


    @OnClick(R2.id.layout_member)
    void onMember() {
        ARouter.getInstance()
                .build(RouterPath.IntentPath.Customer.OPEN_MEMBER)
                .navigation(mContext);
    }
}
