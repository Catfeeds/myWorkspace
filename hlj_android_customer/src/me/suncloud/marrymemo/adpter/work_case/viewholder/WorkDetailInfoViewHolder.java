package me.suncloud.marrymemo.adpter.work_case.viewholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.FinancialSwitch;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljhttplibrary.utils.AuthUtil;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Rule;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.model.WorkParameter;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.TimeUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.orders.XiaoXiInstallmentIntroActivity;
import me.suncloud.marrymemo.view.wallet.FinancialHomeActivity;

/**
 * Created by luohanlin on 2017/12/21.
 */

public class WorkDetailInfoViewHolder extends BaseViewHolder {
    @BindView(R.id.tv_title1)
    TextView tvTitle1;
    @BindView(R.id.tv_title2)
    TextView tvTitle2;
    @BindView(R.id.tv_property)
    TextView tvProperty;
    @BindView(R.id.tv_sub_title)
    TextView tvSubTitle;
    @BindView(R.id.tv_discount_type)
    TextView tvDiscountType;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_original_price)
    TextView tvOriginalPrice;
    @BindView(R.id.limit_line)
    View limitLine;
    @BindView(R.id.tv_limit_count)
    TextView tvLimitCount;
    @BindView(R.id.price_layout)
    LinearLayout priceLayout;
    @BindView(R.id.tv_activity_count_down)
    TextView tvActivityCountDown;
    @BindView(R.id.tv_earnest)
    TextView tvEarnest;
    @BindView(R.id.btn_buy)
    Button btnBuy;
    @BindView(R.id.tv_prepare_price)
    TextView tvPreparePrice;
    @BindView(R.id.tv_prepare_count_down)
    TextView tvPrepareCountDown;
    @BindView(R.id.prepare_layout)
    LinearLayout prepareLayout;
    @BindView(R.id.btn_sale_buy)
    Button btnSaleBuy;
    @BindView(R.id.intent_price_hint_layout)
    LinearLayout intentPriceHintLayout;
    @BindView(R.id.payment_method_layout)
    LinearLayout paymentMethodLayout;
    @BindView(R.id.img_photograph)
    ImageView imgPhotograph;
    @BindView(R.id.tv_service_photograph)
    TextView tvServicePhotograph;
    @BindView(R.id.tv_photograph_info)
    TextView tvPhotographInfo;
    @BindView(R.id.img_shooting)
    ImageView imgShooting;
    @BindView(R.id.tv_service_shooting)
    TextView tvServiceShooting;
    @BindView(R.id.tv_shooting_info)
    TextView tvShootingInfo;
    @BindView(R.id.img_compere)
    ImageView imgCompere;
    @BindView(R.id.tv_service_compere)
    TextView tvServiceCompere;
    @BindView(R.id.tv_compere_info)
    TextView tvCompereInfo;
    @BindView(R.id.img_make_up)
    ImageView imgMakeUp;
    @BindView(R.id.tv_service_make_up)
    TextView tvServiceMakeUp;
    @BindView(R.id.tv_make_up_info)
    TextView tvMakeUpInfo;
    @BindView(R.id.wedding_plan_service_layout)
    LinearLayout weddingPlanServiceLayout;
    @BindView(R.id.img_stylist)
    ImageView imgStylist;
    @BindView(R.id.tv_service_stylist)
    TextView tvServiceStylist;
    @BindView(R.id.tv_stylist_info)
    TextView tvStylistInfo;
    @BindView(R.id.img_photo)
    ImageView imgPhoto;
    @BindView(R.id.tv_service_photo)
    TextView tvServicePhoto;
    @BindView(R.id.tv_photo_info)
    TextView tvPhotoInfo;
    @BindView(R.id.img_trimming)
    ImageView imgTrimming;
    @BindView(R.id.tv_service_trimming)
    TextView tvServiceTrimming;
    @BindView(R.id.tv_trimming_info)
    TextView tvTrimmingInfo;
    @BindView(R.id.img_team)
    ImageView imgTeam;
    @BindView(R.id.tv_service_team)
    TextView tvServiceTeam;
    @BindView(R.id.tv_team_info)
    TextView tvTeamInfo;
    @BindView(R.id.wedding_photo_service_layout)
    LinearLayout weddingPhotoServiceLayout;
    @BindView(R.id.work_services_layout)
    LinearLayout workServicesLayout;
    @BindView(R.id.tv_hlj_installment_hint)
    TextView tvHljInstallmentHint;
    @BindView(R.id.hlj_installment_holder_layout)
    LinearLayout hljInstallmentHolderLayout;
    @BindView(R.id.tv_coupon)
    TextView tvCoupon;
    @BindView(R.id.tv_money_sill)
    TextView tvMoneySill;
    @BindView(R.id.tv_money_sill2)
    TextView tvMoneySill2;
    @BindView(R.id.coupon_layout)
    RelativeLayout couponLayout;
    @BindView(R.id.tv_gift)
    TextView tvGift;
    @BindView(R.id.gift_layout)
    LinearLayout giftLayout;
    @BindView(R.id.tv_pay_all)
    TextView tvPayAll;
    @BindView(R.id.pay_all_layout)
    LinearLayout payAllLayout;
    @BindView(R.id.work_privilege_layout)
    LinearLayout workPrivilegeLayout;
    @BindView(R.id.loan_layout)
    LinearLayout loanLayout;

    private Context context;
    private boolean isSnapshot;
    private Work work;
    private double workPrice;
    private DecimalFormat decimalFormat;
    private View view;
    private OnInfoListener infoListener;
    private Runnable timeDownRun = new Runnable() {
        @Override
        public void run() {
            setSaleView();
        }
    };

    public WorkDetailInfoViewHolder(View v, Work wk, boolean iSS, OnInfoListener listener) {
        super(v);
        this.view = v;
        this.work = wk;
        this.isSnapshot = iSS;
        this.infoListener = listener;
        ButterKnife.bind(this, v);
    }

    @Override
    protected void setViewData(Context mContext, Object item, int position, int viewType) {
        this.context = mContext;
        onAppSubmit();
        setWorkTitle();
        setXiaoxiInstallmentLayout();
        if (TextUtils.isEmpty(work.getSubTitle())) {
            tvSubTitle.setVisibility(View.GONE);
        } else {
            tvSubTitle.setVisibility(View.VISIBLE);
            tvSubTitle.setText(work.getSubTitle());
        }
        setSaleView();
        if (work.getMarketPrice() > 0) {
            tvOriginalPrice.setVisibility(View.VISIBLE);
            tvOriginalPrice.getPaint()
                    .setAntiAlias(true);
            tvOriginalPrice.getPaint()
                    .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            tvOriginalPrice.setText("  " + context.getString(R.string.rmb) + Util
                    .formatDouble2String(
                    work.getMarketPrice()) + "  ");
        } else {
            tvOriginalPrice.setVisibility(View.INVISIBLE);
        }
        if (!isSnapshot) {
            if (work.isInstallment() && work.getInstallment() != null) {
                btnBuy.setText(R.string.btn_buy);
                btnSaleBuy.setText(R.string.btn_buy);
            }
            if (work.getIntentPrice() > 0) {
                btnSaleBuy.setText(Html.fromHtml(context.getString(R.string
                                .html_fmt_intent_price_buy,
                        NumberFormatUtil.formatDouble2String(work.getIntentPrice()))));
            }
            if (work.getLimit_num() > 0 && work.getLimit_num() - work.getLimit_sold_out() <= 0) {
                btnSaleBuy.setEnabled(false);
                btnSaleBuy.setText(R.string.btn_buy_over);
            } else {
                btnSaleBuy.setEnabled(true);
            }

            List<CouponInfo> coupons = null;
            if (work.getMerchant() != null && work.getMerchant()
                    .isCoupon()) {
                coupons = work.getMerchant()
                        .getCoupons();
            }
            if (coupons != null && !coupons.isEmpty()) {
                Collections.sort(coupons, new Comparator<CouponInfo>() {
                    @Override
                    public int compare(CouponInfo o1, CouponInfo o2) {
                        return (int) (o1.getValue() - o2.getValue());
                    }
                });
                couponLayout.setVisibility(View.VISIBLE);
                tvMoneySill.setVisibility(View.VISIBLE);
                setMoneySill(tvMoneySill, coupons.get(0));
                if (coupons.size() >= 2) {
                    tvMoneySill2.setVisibility(View.VISIBLE);
                    setMoneySill(tvMoneySill2, coupons.get(1));
                }
            } else {
                couponLayout.setVisibility(View.GONE);
            }
        }
        setWorkServiceInfo();
    }

    /**
     * 审核特殊处理
     */
    private void onAppSubmit() {
        if (FinancialSwitch.INSTANCE.isClosed(context)) {
            loanLayout.setVisibility(View.GONE);
        } else {
            loanLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setWorkTitle() {
        String city = !CommonUtil.isEmpty(work.getLvPaiCity()) ? work.getLvPaiCity() : work
                .getMerchant()
                .getCityName();
        tvProperty.setText(work.getKind() + "·" + city);
        tvTitle2.setVisibility(View.VISIBLE);
        if (!CommonUtil.isEmpty(work.getTitle())) {
            tvTitle2.getViewTreeObserver()
                    .addOnPreDrawListener(new TitlePreDrawListener(tvTitle1,
                            tvTitle2,
                            work.getTitle()));
            tvTitle2.setText(work.getTitle());
        }
    }

    /**
     * 小犀分期
     */
    private void setXiaoxiInstallmentLayout() {
        if (work.getInstallment() == null || !work.isInstallment()) {
            hljInstallmentHolderLayout.setVisibility(View.GONE);
            return;
        }
        hljInstallmentHolderLayout.setVisibility(View.VISIBLE);
        tvHljInstallmentHint.setText(Html.fromHtml(context.getString(R.string
                        .label_xiaoxi_installment_hint,
                Util.formatDouble2String(work.getInstallment()
                        .getLowestEachPay()))));
    }

    @OnClick(R.id.hlj_installment_holder_layout)
    void onXiaoxiInstallment() {
        Intent intent = new Intent(context, XiaoXiInstallmentIntroActivity.class);
        intent.putExtra(XiaoXiInstallmentIntroActivity.ARG_PRICE, workPrice);
        intent.putExtra(XiaoXiInstallmentIntroActivity.ARG_WORK, work);
        context.startActivity(intent);
    }

    @OnClick({R.id.btn_buy, R.id.btn_sale_buy})
    public void onBuy() {
        if (context != null && context instanceof WorkActivity) {
            ((WorkActivity) context).onBuy();
        }
    }

    @OnClick(R.id.work_services_layout)
    void onWorkService() {
        infoListener.onServiceInfo();
    }


    @OnClick(R.id.loan_layout)
    public void onLoan() {
        Intent intent = new Intent(context, FinancialHomeActivity.class);
        context.startActivity(intent);
    }

    @OnClick(R.id.coupon_layout)
    public void onCoupon() {
        if (work == null || work.getMerchant() == null || work.getMerchant()
                .getId() == 0) {
            return;
        }
        if (!AuthUtil.loginBindCheck(context)) {
            return;
        }
        if (infoListener != null) {
            infoListener.onCoupon();
        }
    }

    private void setSaleView() {
        Rule rule = work.getRule();
        Date date = new Date(HljTimeUtils.getServerCurrentTimeMillis());
        // 如果是快照不显示活动信息
        if (!isSnapshot && rule != null && rule.getId() > 0 && (!rule.isTimeAble() || rule
                .getEnd_time() == null || rule.getEnd_time()
                .after(date))) {
            if (!rule.isTimeAble() || rule.getStart_time() == null || rule.getStart_time()
                    .before(date)) {
                int payAllPercentPrice = Math.round(work.getSalePayAllPercent() * work
                        .getSale_price());
                setWorkPrivilegeLayout(work.getSaleEarnestMoney(),
                        work.getOrderGift(),
                        payAllPercentPrice,
                        work.getPayAllGift());
                prepareLayout.setVisibility(View.GONE);
                workPrice = work.getSale_price();
                tvPrice.setText(Util.formatDouble2String(work.getSale_price()));

                if (JSONUtil.isEmpty(rule.getShowtxt())) {
                    tvDiscountType.setVisibility(View.GONE);
                } else {
                    tvDiscountType.setVisibility(View.VISIBLE);
                    tvDiscountType.setText(work.getRule()
                            .getShowtxt());
                }
                if (work.getLimit_num() > 0) {
                    limitLine.setVisibility(View.VISIBLE);
                    tvLimitCount.setVisibility(View.VISIBLE);
                    tvLimitCount.setText(Html.fromHtml(context.getString(R.string
                                    .html_fmt_limit_count,
                            work.getLimit_num() - work.getLimit_sold_out())));
                } else {
                    limitLine.setVisibility(View.GONE);
                    tvLimitCount.setVisibility(View.GONE);
                }
                if (rule.isTimeAble() && rule.getEnd_time() != null) {
                    tvActivityCountDown.setVisibility(View.VISIBLE);
                    long millisInFuture = rule.getEndTimeInMillis() - date.getTime();
                    tvActivityCountDown.setText(Html.fromHtml(millisEndFormatHtml(millisInFuture)));
                    if (millisInFuture >= 0) {
                        view.postDelayed(timeDownRun, 1000);
                    }
                } else {
                    tvActivityCountDown.setVisibility(View.GONE);
                }
                btnBuy.setVisibility(View.GONE);
                btnSaleBuy.setVisibility(View.VISIBLE);
                intentPriceHintLayout.setVisibility(work.getIntentPrice() > 0 ? View.VISIBLE :
                        View.GONE);
            } else {
                int payAllPercentPrice = Math.round(work.getPayAllPercent() * work.getPrice());
                setWorkPrivilegeLayout(work.getEarnestMoney(),
                        work.getOrderGift(),
                        payAllPercentPrice,
                        work.getPayAllGift());

                long millisInStart = rule.getStartTimeInMillis() - date.getTime();
                prepareLayout.setVisibility(View.VISIBLE);
                workPrice = work.getPrice();
                tvPrice.setText(Util.formatDouble2String(work.getPrice()));
                tvDiscountType.setVisibility(View.GONE);
                limitLine.setVisibility(View.GONE);
                tvLimitCount.setVisibility(View.GONE);
                tvActivityCountDown.setVisibility(View.GONE);
                intentPriceHintLayout.setVisibility(View.GONE);
                tvPreparePrice.setText(context.getString(R.string.fmt_sale_prepare_price,
                        rule.getName(),
                        NumberFormatUtil.unknownPrice(work.getSale_price())));
                tvPrepareCountDown.setText(context.getString(R.string.fmt_work_sale_start_time_down,
                        millisFormat(millisInStart)));
                if (millisInStart >= 0) {
                    view.postDelayed(timeDownRun, 1000);
                }

                btnBuy.setVisibility(View.VISIBLE);
                btnSaleBuy.setVisibility(View.GONE);
            }
        } else {
            workPrice = work.getShowPrice();
            tvPrice.setText(Util.formatDouble2String(work.getShowPrice()));
            tvDiscountType.setVisibility(View.GONE);
            tvActivityCountDown.setVisibility(View.GONE);
            limitLine.setVisibility(View.GONE);
            tvLimitCount.setVisibility(View.GONE);
            prepareLayout.setVisibility(View.GONE);
            intentPriceHintLayout.setVisibility(View.GONE);
            int payAllPercentPrice = Math.round(work.getPayAllPercent() * (float) work
                    .getShowPrice());

            if (!isSnapshot) {
                btnBuy.setVisibility(View.VISIBLE);
                btnSaleBuy.setVisibility(View.GONE);
                setWorkPrivilegeLayout(work.getEarnestMoney(),
                        work.getOrderGift(),
                        payAllPercentPrice,
                        work.getPayAllGift());
            } else {
                btnBuy.setVisibility(View.GONE);
                btnSaleBuy.setVisibility(View.GONE);
            }
        }
    }

    private void setWorkPrivilegeLayout(
            double earnestMoney, String orderGift, double payAllPercentPrice, String payAllGift) {
        if (earnestMoney > 0) {
            tvEarnest.setVisibility(View.VISIBLE);
            tvEarnest.setText(context.getString(R.string.label_earnest,
                    Util.formatDouble2String(earnestMoney)));
        } else {
            tvEarnest.setVisibility(View.GONE);
        }
        String giftStr = null;
        String payAllStr = null;
        if (!JSONUtil.isEmpty(orderGift)) {
            giftStr = context.getString(R.string.label_gift, orderGift);
        }
        if (payAllPercentPrice > 0) {
            payAllStr = context.getString(R.string.label_pay_all_percent,
                    Util.formatDouble2String(payAllPercentPrice));
        } else if (!JSONUtil.isEmpty(payAllGift)) {
            payAllStr = context.getString(R.string.label_pay_all_gift, payAllGift);
        }
        if (JSONUtil.isEmpty(giftStr) && JSONUtil.isEmpty(payAllStr)) {
            workPrivilegeLayout.setVisibility(View.GONE);
        } else {
            workPrivilegeLayout.setVisibility(View.VISIBLE);
            if (!JSONUtil.isEmpty(giftStr)) {
                giftLayout.setVisibility(View.VISIBLE);
                tvGift.setText(giftStr);
            } else {
                giftLayout.setVisibility(View.GONE);
            }
            if (!JSONUtil.isEmpty(payAllStr)) {
                payAllLayout.setVisibility(View.VISIBLE);
                tvPayAll.setText(payAllStr);
            } else {
                payAllLayout.setVisibility(View.GONE);
            }
        }
    }

    private void setWorkServiceInfo() {
        if (work.getPropertyId() == Merchant.PROPERTY_WEDDING_PLAN) {
            // 婚礼策划
            workServicesLayout.setVisibility(View.VISIBLE);
            weddingPlanServiceLayout.setVisibility(View.VISIBLE);
            weddingPhotoServiceLayout.setVisibility(View.GONE);
            for (WorkParameter parameter : work.getParameters()) {
                if ("team_level".equals(parameter.getFieldName())) {
                    for (WorkParameter child : parameter.getChildren()) {
                        switch (child.getFieldName()) {
                            case "photographer":
                                if ("不包含".equals(child.getValues())) {
                                    tvPhotographInfo.setText("无");
                                    tvPhotographInfo.setTextColor(ContextCompat.getColor(context,
                                            R.color.colorGray));
                                } else {
                                    tvPhotographInfo.setText(child.getValues());
                                    tvPhotographInfo.setTextColor(ContextCompat.getColor(context,
                                            R.color.colorPrimary));
                                }
                                break;
                            case "cameraman":
                                if ("不包含".equals(child.getValues())) {
                                    tvShootingInfo.setText("无");
                                    tvShootingInfo.setTextColor(ContextCompat.getColor(context,
                                            R.color.colorGray));
                                } else {
                                    tvShootingInfo.setText(child.getValues());
                                    tvShootingInfo.setTextColor(ContextCompat.getColor(context,
                                            R.color.colorPrimary));
                                }
                                break;
                            case "hoster":
                                if ("不包含".equals(child.getValues())) {
                                    tvCompereInfo.setText("无");
                                    tvCompereInfo.setTextColor(ContextCompat.getColor(context,
                                            R.color.colorGray));
                                } else {
                                    tvCompereInfo.setText(child.getValues());
                                    tvCompereInfo.setTextColor(ContextCompat.getColor(context,
                                            R.color.colorPrimary));
                                }
                                break;
                            case "makeup_artist":
                                if ("不包含".equals(child.getValues())) {
                                    tvMakeUpInfo.setText("无");
                                    tvMakeUpInfo.setTextColor(ContextCompat.getColor(context,
                                            R.color.colorGray));
                                } else {
                                    tvMakeUpInfo.setText(child.getValues());
                                    tvMakeUpInfo.setTextColor(ContextCompat.getColor(context,
                                            R.color.colorPrimary));
                                }
                                break;
                        }
                    }
                }
            }
        } else if (work.getPropertyId() == Merchant.PROPERTY_WEDDING_DRESS_PHOTO) {
            // 婚纱摄影
            workServicesLayout.setVisibility(View.VISIBLE);
            weddingPlanServiceLayout.setVisibility(View.GONE);
            weddingPhotoServiceLayout.setVisibility(View.VISIBLE);
            for (WorkParameter parameter : work.getParameters()) {
                if ("modeling_level".equals(parameter.getFieldName())) {
                    for (WorkParameter child : parameter.getChildren()) {
                        if ("bride_cloth".equals(child.getFieldName())) {
                            if ("不包含".equals(child.getValues())) {
                                tvStylistInfo.setText("无");
                                tvStylistInfo.setTextColor(ContextCompat.getColor(context,
                                        R.color.colorGray));
                            } else {
                                tvStylistInfo.setText(child.getValues());
                                tvStylistInfo.setTextColor(ContextCompat.getColor(context,
                                        R.color.colorPrimary));
                            }
                        }
                    }
                } else if ("shooting_level".equals(parameter.getFieldName())) {
                    for (WorkParameter child : parameter.getChildren()) {
                        switch (child.getFieldName()) {
                            case "shoot_num":
                                if ("不包含".equals(child.getValues())) {
                                    tvPhotoInfo.setText("无");
                                    tvPhotoInfo.setTextColor(ContextCompat.getColor(context,
                                            R.color.colorGray));
                                } else {
                                    tvPhotoInfo.setText(child.getValues());
                                    tvPhotoInfo.setTextColor(ContextCompat.getColor(context,
                                            R.color.colorPrimary));
                                }
                                break;
                            case "shoot_good_num":
                                if ("不包含".equals(child.getValues())) {
                                    tvTrimmingInfo.setText("无");
                                    tvTrimmingInfo.setTextColor(ContextCompat.getColor(context,
                                            R.color.colorGray));
                                } else {
                                    tvTrimmingInfo.setText(child.getValues());
                                    tvTrimmingInfo.setTextColor(ContextCompat.getColor(context,
                                            R.color.colorPrimary));
                                }
                                break;
                        }
                    }
                } else if ("team_level".equals(parameter.getFieldName())) {
                    for (WorkParameter child : parameter.getChildren()) {
                        if ("photographer".equals(child.getFieldName())) {
                            if ("不包含".equals(child.getValues())) {
                                tvTeamInfo.setText("无");
                                tvTeamInfo.setTextColor(ContextCompat.getColor(context,
                                        R.color.colorGray));
                            } else {
                                tvTeamInfo.setText(child.getValues());
                                tvTeamInfo.setTextColor(ContextCompat.getColor(context,
                                        R.color.colorPrimary));
                            }
                        }
                    }
                }
            }
        } else {
            workServicesLayout.setVisibility(View.GONE);
        }
    }


    private String millisFormat(long millisTime) {
        int days = (int) (millisTime / (1000 * 60 * 60 * 24));
        return context.getString(R.string.label_day, days + "") + TimeUtil.countDownMillisFormat(
                context,
                millisTime);
    }

    private String millisEndFormatHtml(long millisTime) {
        int days = (int) (millisTime / (1000 * 60 * 60 * 24));
        long leftMillis = millisTime % (1000 * 60 * 60 * 24);
        int hours = (int) (leftMillis / (1000 * 60 * 60));
        leftMillis %= 1000 * 60 * 60;
        int minutes = (int) (leftMillis / (1000 * 60));
        leftMillis %= 1000 * 60;
        int seconds = (int) (leftMillis / 1000);

        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat("00");
        }
        return context.getString(R.string.html_fmt_work_sale_end_time_down,
                decimalFormat.format(days),
                decimalFormat.format(hours),
                decimalFormat.format(minutes),
                decimalFormat.format(seconds));
    }

    //设置优惠券金额
    private void setMoneySill(TextView tvMoneySill, CouponInfo couponInfo) {
        if (couponInfo.getType() == 1) {
            tvMoneySill.setText(couponInfo.getMoneySill() == 0 ? context.getString(R.string
                    .label_money_sill_empty) : context.getString(
                    R.string.label_full_cut,
                    CommonUtil.formatDouble2String(couponInfo.getMoneySill()),
                    CommonUtil.formatDouble2String(couponInfo.getValue())));
        } else {
            tvMoneySill.setText(context.getString(R.string.label_cash_coupon,
                    CommonUtil.formatDouble2String(couponInfo.getValue())));
        }
    }

    private class TitlePreDrawListener implements ViewTreeObserver.OnPreDrawListener {

        private TextView textView1;
        private TextView textView2;
        private String text;

        private TitlePreDrawListener(TextView tv1, TextView tv2, String txt) {
            this.textView1 = tv1;
            this.textView2 = tv2;
            this.text = txt;
        }

        @Override
        public boolean onPreDraw() {
            if (textView1 == null || textView2 == null) {
                return false;
            }
            Layout layout = textView2.getLayout();
            if (layout != null) {
                textView2.getViewTreeObserver()
                        .removeOnPreDrawListener(this);
                try {
                    if (!textView2.getText()
                            .toString()
                            .equals(text)) {
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                if (layout.getEllipsisCount(0) > 0) {
                    textView1.setText(text);
                    textView1.setVisibility(View.VISIBLE);
                    textView1.getViewTreeObserver()
                            .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                @Override
                                public boolean onPreDraw() {
                                    if (textView1 == null || textView2 == null) {
                                        return false;
                                    }
                                    Layout layout = textView1.getLayout();
                                    if (layout != null) {
                                        textView1.getViewTreeObserver()
                                                .removeOnPreDrawListener(this);
                                        int end = layout.getLineEnd(0);
                                        if (end < text.length()) {
                                            textView2.getLayoutParams().width = 0;
                                            textView2.requestLayout();
                                            textView2.setText(text.substring(end, text.length()));
                                        } else {
                                            textView2.setVisibility(View.GONE);
                                        }
                                    }

                                    return false;
                                }
                            });
                } else {
                    textView1.setVisibility(View.GONE);
                }
            }
            return false;
        }
    }

    public interface OnInfoListener {
        void onServiceInfo();

        void onCoupon();
    }
}
