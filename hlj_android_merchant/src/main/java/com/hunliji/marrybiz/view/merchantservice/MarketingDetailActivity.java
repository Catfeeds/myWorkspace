package com.hunliji.marrybiz.view.merchantservice;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.merchant.MerchantApi;
import com.hunliji.marrybiz.api.merchantserver.MerchantServerApi;
import com.hunliji.marrybiz.model.DataConfig;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.merchant.ShopInfo;
import com.hunliji.marrybiz.model.merchantservice.MarketItem;
import com.hunliji.marrybiz.model.merchantservice.WeAppDetail;
import com.hunliji.marrybiz.model.orders.BdProduct;
import com.hunliji.marrybiz.util.MerchantServerUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.view.HomeActivity;
import com.hunliji.marrybiz.view.easychat.EasyChatActivity;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by mo_yu on 2018/1/26.营销详情
 */

public class MarketingDetailActivity extends HljBaseActivity {

    public static final String ARG_PRODUCT_ID = "product_id";
    public static final String YUN_KE_PATH = "http://www.yunkexiongdi.com";
    @BindView(R.id.img_marketing_logo)
    RoundedImageView imgMarketingLogo;
    @BindView(R.id.tv_marketing_title)
    TextView tvMarketingTitle;
    @BindView(R.id.tv_marketing_content)
    TextView tvMarketingContent;
    @BindView(R.id.img_already_open)
    ImageView imgAlreadyOpen;
    @BindView(R.id.tv_validity_date)
    TextView tvValidityDate;
    @BindView(R.id.already_open_layout)
    RelativeLayout alreadyOpenLayout;
    @BindView(R.id.action_open_status)
    TextView actionOpenStatus;
    @BindView(R.id.tv_ultimate_tip)
    TextView tvUltimateTip;
    @BindView(R.id.line_layout)
    View lineLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_validity_date_end)
    TextView tvValidityDateEnd;
    @BindView(R.id.img_preferential_tag)
    ImageView imgPreferentialTag;
    @BindView(R.id.open_status_layout)
    RelativeLayout openStatusLayout;
    @BindView(R.id.img_market_education)
    ImageView imgMarketEducation;
    @BindView(R.id.tv_open_status)
    TextView tvOpenStatus;
    @BindView(R.id.use_tip_layout)
    LinearLayout useTipLayout;
    @BindView(R.id.action_use_market)
    TextView actionUseMarket;
    @BindView(R.id.validity_date_layout)
    LinearLayout validityDateLayout;
    @BindView(R.id.tv_product_price)
    TextView tvProductPrice;
    @BindView(R.id.open_price_layout)
    LinearLayout openPriceLayout;
    @BindView(R.id.header_line_layout)
    View headerLineLayout;

    private long productId;
    private double money = 1299;
    private int width;
    private SimpleDateFormat simpleDateFormat;
    private MerchantUser user;
    private MarketItem marketItem;
    private RxBusSubscriber paySubscriber;
    private Subscription rxBusEventSub;
    private HljHttpSubscriber initSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_detail);
        ButterKnife.bind(this);
        initValue();
        initView();
        registerRxBusEvent();
        initLoad();
    }

    private void initLoad() {
        if (productId == BdProduct.XIAO_CHENG_XU) {
            initWeAppDetailLoad();
        } else if (productId == BdProduct.TIAN_YAN_XI_TONG || productId == BdProduct
                .QING_SONG_LIAO) {
            initShopInfoLoad();
        }
    }

    private void initValue() {
        productId = getIntent().getLongExtra(ARG_PRODUCT_ID, 0);
        user = Session.getInstance()
                .getCurrentUser(this);
        marketItem = MerchantServerUtil.getInstance()
                .getMarketItem(this, productId);
        width = CommonUtil.getDeviceSize(this).x;
        simpleDateFormat = new SimpleDateFormat(getString(R.string.label_validity_date),
                Locale.getDefault());
    }

    private void initView() {
        if (marketItem == null) {
            return;
        }
        initHeaderView();
        switch ((int) productId) {
            case (int) BdProduct.YUN_KE:
                setYunKeView();
                break;
            case BdProduct.XIAO_CHENG_XU:
                setWeAppView();
                break;
            case BdProduct.DUO_DIAN_GUAN_LI:
            case BdProduct.WEI_GUAN_WANG:
                setUltimateAndUnUseAppMarketView();
                break;
            case BdProduct.ZHU_TI_MU_BAN:
            case BdProduct.TIAN_YAN_XI_TONG:
            case BdProduct.QING_SONG_LIAO:
            case BdProduct.HUO_DONG_WEI_CHUAN_DAN:
                setUltimateMarketView();
                break;
            case BdProduct.JU_KE_BAO:
                setJuKeBaoMarketView();
                break;
            case BdProduct.SHANG_JIA_CHENG_NUO:
            case BdProduct.DING_DAN_KE_TUI:
                setBondMarketView();
                break;
            case BdProduct.TUI_JIAN_CHU_CHUANG:
                setTuiJianChuChuangView();
                break;
            default:
                setNormalMarketDetail();
                break;
        }
    }

    private void initHeaderView() {
        setTitle(marketItem.getTitle());
        if (CommonUtil.isEmpty(marketItem.getSubTitle1())) {
            tvMarketingTitle.setText(marketItem.getTitle());
        } else {
            tvMarketingTitle.setText(marketItem.getTitle() + " - " + marketItem.getSubTitle1());
        }
        tvMarketingContent.setText(marketItem.getSubTitle2());
        int imgRes = MerchantServerUtil.getInstance()
                .getMerchantServerImgRes(productId);
        if (imgRes > 0) {
            imgMarketingLogo.setImageResource(imgRes);
        }
        String imagePath = null;
        if (marketItem.getProductId() == BdProduct.YUN_KE) {
            if (user != null && user.getProperty() != null) {
                imagePath = marketItem.getYunKeImagePath(user.getProperty()
                        .getId());
            }
        } else {
            imagePath = marketItem.getImagePath();
        }
        Glide.with(this)
                .load(ImagePath.buildPath(imagePath)
                        .width(width)
                        .path())
                .apply(new RequestOptions().fitCenter())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(
                            @Nullable GlideException e,
                            Object model,
                            Target<Drawable> target,
                            boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(
                            Drawable resource,
                            Object model,
                            Target<Drawable> target,
                            DataSource dataSource,
                            boolean isFirstResource) {
                        imgMarketEducation.getLayoutParams().height = Math.round(resource
                                .getIntrinsicHeight() * width / resource.getIntrinsicWidth());
                        return false;
                    }
                })
                .into(imgMarketEducation);
    }

    /**
     * 云蝌
     */
    private void setYunKeView() {
        useTipLayout.setVisibility(View.GONE);
        tvUltimateTip.setVisibility(View.GONE);
        alreadyOpenLayout.setVisibility(View.GONE);
        openStatusLayout.setBackgroundResource(R.drawable.sp_r2_primary);
        actionOpenStatus.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        actionOpenStatus.setText("立即订购");
        imgPreferentialTag.setVisibility(View.GONE);
    }

    /**
     * 小程序
     */
    private void setWeAppView() {
        useTipLayout.setVisibility(View.VISIBLE);
        tvUltimateTip.setVisibility(View.GONE);
        openStatusLayout.setVisibility(View.VISIBLE);
        actionOpenStatus.setText("立即订购");
        openPriceLayout.setVisibility(View.VISIBLE);
        tvProductPrice.setText(CommonUtil.formatDouble2String(money));
        openStatusLayout.setBackgroundResource(R.drawable.sp_r2_primary);
        actionOpenStatus.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        imgPreferentialTag.setVisibility(View.GONE);
    }

    /**
     * 推荐橱窗
     */
    private void setTuiJianChuChuangView() {
        useTipLayout.setVisibility(View.VISIBLE);
        tvUltimateTip.setVisibility(View.GONE);
        openStatusLayout.setVisibility(View.GONE);
        openPriceLayout.setVisibility(View.GONE);
        if (user.getIsPro() == Merchant.MERCHANT_ULTIMATE) {
            headerLineLayout.setVisibility(View.VISIBLE);
            //已开通
            int day = initValidityDay();
            alreadyOpenLayout.setVisibility(View.VISIBLE);
            if (day <= 30) {
                //已开通快到期
                openStatusLayout.setVisibility(View.VISIBLE);
                actionOpenStatus.setText("续费旗舰版 免费使用");
                tvValidityDateEnd.setVisibility(View.VISIBLE);
            } else {
                //已开通未到期（30天）
                tvUltimateTip.setVisibility(View.GONE);
                tvValidityDateEnd.setVisibility(View.GONE);
                openStatusLayout.setVisibility(View.GONE);
            }
        } else {
            headerLineLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 只有旗舰版可用的营销服务（主题模板，多店管理，微官网，活动微传单,ps 天眼系统和轻松聊开通cpm也可用）
     */
    private void setUltimateMarketView() {
        openStatusLayout.setBackgroundResource(R.drawable.sp_r2_black);
        imgPreferentialTag.setVisibility(View.VISIBLE);
        actionOpenStatus.setTextColor(ContextCompat.getColor(this, R.color.colorUltimateGold));
        tvUltimateTip.setVisibility(View.VISIBLE);
        tvUltimateTip.setText(getString(R.string.msg_merchant_ultimate_tip));
        if (user.isPro()) {
            if (user.getIsPro() == Merchant.MERCHANT_PRO) {
                //专业版
                alreadyOpenLayout.setVisibility(View.GONE);
                openStatusLayout.setVisibility(View.VISIBLE);
                actionOpenStatus.setText("开通旗舰版 免费使用");
                tvUltimateTip.setText("*您已是专业版商家 仅需补齐差价即可升级为旗舰版");
            } else {
                int day = initValidityDay();
                alreadyOpenLayout.setVisibility(View.VISIBLE);
                if (day <= 30) {
                    //已开通快到期
                    openStatusLayout.setVisibility(View.VISIBLE);
                    actionOpenStatus.setText("续费旗舰版 免费使用");
                    tvValidityDateEnd.setVisibility(View.VISIBLE);
                } else {
                    //已开通未到期（30天）
                    tvUltimateTip.setVisibility(View.GONE);
                    tvValidityDateEnd.setVisibility(View.GONE);
                    openStatusLayout.setVisibility(View.GONE);
                }
            }
        } else {
            alreadyOpenLayout.setVisibility(View.GONE);
            actionOpenStatus.setVisibility(View.VISIBLE);
            actionOpenStatus.setText("开通旗舰版 免费使用");
        }
    }

    /**
     * 只有旗舰版可用并且APP不能使用的营销服务（多店管理，微官网）
     */
    private void setUltimateAndUnUseAppMarketView() {
        useTipLayout.setVisibility(View.VISIBLE);
        openStatusLayout.setBackgroundResource(R.drawable.sp_r2_black);
        imgPreferentialTag.setVisibility(View.VISIBLE);
        actionOpenStatus.setTextColor(ContextCompat.getColor(this, R.color.colorUltimateGold));
        tvUltimateTip.setVisibility(View.VISIBLE);
        tvUltimateTip.setText(getString(R.string.msg_merchant_ultimate_tip));
        if (user.isPro()) {
            if (user.getIsPro() == Merchant.MERCHANT_PRO) {
                //专业版
                alreadyOpenLayout.setVisibility(View.GONE);
                openStatusLayout.setVisibility(View.VISIBLE);
                actionOpenStatus.setText("开通旗舰版 免费使用");
                tvUltimateTip.setText("*您已是专业版商家 仅需补齐差价即可升级为旗舰版");
            } else {
                int day = initValidityDay();
                alreadyOpenLayout.setVisibility(View.VISIBLE);
                if (day <= 30 ) {
                    //已开通快到期
                    actionOpenStatus.setText("续费旗舰版 免费使用");
                    openStatusLayout.setVisibility(View.VISIBLE);
                    tvValidityDateEnd.setVisibility(View.VISIBLE);
                } else {
                    //已开通未到期（30天）
                    tvUltimateTip.setVisibility(View.GONE);
                    openStatusLayout.setVisibility(View.GONE);
                    tvValidityDateEnd.setVisibility(View.GONE);
                }
            }
        } else {
            alreadyOpenLayout.setVisibility(View.GONE);
            actionOpenStatus.setVisibility(View.VISIBLE);
            actionOpenStatus.setText("开通旗舰版 免费使用");
        }
    }


    /**
     * 需要缴纳保证金才可用的服务 订单可退，商家承诺
     */
    private void setBondMarketView() {
        openStatusLayout.setBackgroundResource(R.drawable.sp_r2_black);
        imgPreferentialTag.setVisibility(View.VISIBLE);
        actionOpenStatus.setTextColor(ContextCompat.getColor(this, R.color.colorUltimateGold));
        tvUltimateTip.setVisibility(View.VISIBLE);
        tvUltimateTip.setText(getString(R.string.msg_merchant_ultimate_tip));
        if (user.isPro()) {
            int day = initValidityDay();
            alreadyOpenLayout.setVisibility(View.VISIBLE);
            //对于商家承诺和订单可退，不仅要购买旗舰版，还要保证金商家才能使用
            if (user.isBondSign()) {
                if (day <= 30) {
                    actionOpenStatus.setText("续费旗舰版 免费使用");
                } else {
                    //已开通未到期（30天）
                    tvUltimateTip.setVisibility(View.GONE);
                    openStatusLayout.setVisibility(View.GONE);
                    tvValidityDateEnd.setVisibility(View.GONE);
                }
            } else {
                tvUltimateTip.setVisibility(View.GONE);
                actionOpenStatus.setText("缴纳保证金 免费使用");
                alreadyOpenLayout.setVisibility(View.GONE);
            }
        } else {
            alreadyOpenLayout.setVisibility(View.GONE);
            actionOpenStatus.setVisibility(View.VISIBLE);
            if (user.isBondSign()) {
                actionOpenStatus.setText("开通旗舰版 免费使用");
            } else {
                actionOpenStatus.setText("开通旗舰版、缴纳保证金 免费使用");
            }
        }
    }

    /**
     * 聚客宝
     */
    private void setJuKeBaoMarketView() {
        openStatusLayout.setBackgroundResource(R.drawable.sp_r2_black);
        imgPreferentialTag.setVisibility(View.VISIBLE);
        actionOpenStatus.setTextColor(ContextCompat.getColor(this, R.color.colorUltimateGold));
        tvUltimateTip.setVisibility(View.VISIBLE);
        tvUltimateTip.setText(getString(R.string.msg_merchant_ultimate_tip));
        if (user.isPro()) {
            int day = initValidityDay();
            alreadyOpenLayout.setVisibility(View.VISIBLE);
            if (day <= 30) {
                //已开通快到期
                openStatusLayout.setVisibility(View.VISIBLE);
                actionOpenStatus.setText("续费旗舰版 继续使用");
                tvValidityDateEnd.setVisibility(View.VISIBLE);
            } else {
                //已开通未到期（30天）
                tvUltimateTip.setVisibility(View.GONE);
                tvValidityDateEnd.setVisibility(View.GONE);
                openStatusLayout.setVisibility(View.GONE);
            }
        } else {
            alreadyOpenLayout.setVisibility(View.GONE);
            actionOpenStatus.setVisibility(View.VISIBLE);
            actionOpenStatus.setText("开通旗舰版 按次付费使用");
        }
    }

    /**
     * 普通营销
     */
    private void setNormalMarketDetail() {
        openStatusLayout.setBackgroundResource(R.drawable.sp_r2_black);
        imgPreferentialTag.setVisibility(View.VISIBLE);
        actionOpenStatus.setTextColor(ContextCompat.getColor(this, R.color.colorUltimateGold));
        tvUltimateTip.setVisibility(View.VISIBLE);
        tvUltimateTip.setText(getString(R.string.msg_merchant_ultimate_tip));
        if (user.isPro()) {
            alreadyOpenLayout.setVisibility(View.VISIBLE);
            int day = initValidityDay();
            if (day <= 30) {
                //已开通快到期
                openStatusLayout.setVisibility(View.VISIBLE);
                actionOpenStatus.setText("续费旗舰版 免费使用");
                tvValidityDateEnd.setVisibility(View.VISIBLE);
            } else {
                //已开通未到期（30天）
                tvUltimateTip.setVisibility(View.GONE);
                tvValidityDateEnd.setVisibility(View.GONE);
                openStatusLayout.setVisibility(View.GONE);
            }
        } else {
            openStatusLayout.setVisibility(View.VISIBLE);
            alreadyOpenLayout.setVisibility(View.GONE);
            actionOpenStatus.setVisibility(View.VISIBLE);
            actionOpenStatus.setText("开通旗舰版 免费使用");
        }
    }

    /**
     * 获取小程序详情
     */
    private void initWeAppDetailLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<WeAppDetail>() {
                        @Override
                        public void onNext(WeAppDetail weAppDetail) {
                            if (weAppDetail.getStatus() == WeAppDetail.STATUS_IN_USE) {
                                int day = 0;
                                if (weAppDetail.getServerEnd() != null) {
                                    day = HljTimeUtils.getSurplusDay(weAppDetail.getServerEnd());
                                    tvValidityDate.setText(weAppDetail.getServerEnd()
                                            .toString(getString(R.string.label_validity_date)));
                                }
                                alreadyOpenLayout.setVisibility(View.VISIBLE);
                                if (day <= 30) {
                                    //已开通快到期
                                    openStatusLayout.setVisibility(View.VISIBLE);
                                    actionOpenStatus.setText("立即续费");
                                    openPriceLayout.setVisibility(View.VISIBLE);
                                    tvProductPrice.setText(CommonUtil.formatDouble2String(money));
                                    tvValidityDateEnd.setVisibility(View.VISIBLE);
                                } else {
                                    //已开通未到期（30天）
                                    openPriceLayout.setVisibility(View.GONE);
                                    openStatusLayout.setVisibility(View.GONE);
                                    tvValidityDateEnd.setVisibility(View.GONE);
                                }
                            } else {
                                openStatusLayout.setVisibility(View.VISIBLE);
                                actionOpenStatus.setText("立即订购");
                                openPriceLayout.setVisibility(View.VISIBLE);
                                tvProductPrice.setText(CommonUtil.formatDouble2String(money));
                            }
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            MerchantServerApi.getMerchantWeAppDetailObb()
                    .subscribe(initSubscriber);
        }
    }

    /**
     * 获取cpm开通信息
     */
    private void initShopInfoLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ShopInfo>() {
                        @Override
                        public void onNext(ShopInfo shopInfo) {
                            if (shopInfo.getCpmStatus() == ShopInfo.TYPE_OPEN) {
                                validityDateLayout.setVisibility(View.GONE);
                                openStatusLayout.setVisibility(View.GONE);
                                tvUltimateTip.setVisibility(View.GONE);
                                actionUseMarket.setVisibility(View.VISIBLE);
                                alreadyOpenLayout.setVisibility(View.VISIBLE);
                            } else if (user != null && user.getIsPro() == Merchant
                                    .MERCHANT_ULTIMATE) {
                                validityDateLayout.setVisibility(View.VISIBLE);
                                actionUseMarket.setVisibility(View.VISIBLE);
                            } else {
                                validityDateLayout.setVisibility(View.VISIBLE);
                                actionUseMarket.setVisibility(View.GONE);
                            }
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            MerchantApi.getShopInfoObb()
                    .subscribe(initSubscriber);
        }
    }

    @OnClick(R.id.action_open_status)
    public void actionOpenUltimate() {
        Intent intent = new Intent();
        switch ((int) productId) {
            case BdProduct.DING_DAN_KE_TUI:
            case BdProduct.SHANG_JIA_CHENG_NUO:
                if (!user.isPro()) {
                    intent.setClass(this, MerchantUltimateDetailActivity.class);
                    intent.putExtra(MerchantUltimateDetailActivity.ARG_PRODUCT_ID, productId);
                } else {
                    intent.setClass(this, BondPlanDetailActivity.class);
                }
                startActivity(intent);
                break;
            case (int) BdProduct.YUN_KE:
                HljWeb.startWebView(this, YUN_KE_PATH);
                break;
            case BdProduct.XIAO_CHENG_XU:
                if (!user.isPro()) {
                    DialogUtil.createDoubleButtonDialog(this,
                            "购买旗舰版后方可购买该服务",
                            "商家旗舰版是婚礼纪平台为商家打造的集营销、推广、获客、门店管理于一身的高端运营产品",
                            "了解旗舰版",
                            "购买旗舰版",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MarketingDetailActivity.this,
                                            MerchantUltimateDetailActivity.class);
                                    startActivity(intent);
                                }
                            },
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MarketingDetailActivity.this,
                                            MerchantUltimateDetailActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .show();
                } else {
                    payXiaoChengXu();
                }
                break;
            default:
                intent.setClass(this, MerchantUltimateDetailActivity.class);
                intent.putExtra(MerchantUltimateDetailActivity.ARG_PRODUCT_ID, productId);
                startActivity(intent);
                break;
        }
    }

    private int initValidityDay() {
        int day = 0;
        if (user.getProDate() != null) {
            day = HljTimeUtils.getSurplusDay(user.getProDate());
            tvValidityDate.setText(simpleDateFormat.format(user.getProDate()));
        }
        return day;
    }

    @OnClick(R.id.action_use_market)
    public void onUseMarket() {
        switch ((int) productId) {
            case BdProduct.TIAN_YAN_XI_TONG:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("page_index", 3);
                startActivity(intent);
                break;
            case BdProduct.QING_SONG_LIAO:
                Intent chatIntent = new Intent(this, EasyChatActivity.class);
                startActivity(chatIntent);
                break;
        }
    }

    private void payXiaoChengXu() {
        if (money <= 0) {
            return;
        }
        if (paySubscriber == null) {
            paySubscriber = new RxBusSubscriber<PayRxEvent>() {
                @Override
                protected void onEvent(PayRxEvent rxEvent) {
                    switch (rxEvent.getType()) {
                        case PAY_SUCCESS:
                            // 支付成功，跳转成功页面
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType.OPEN_ULTIMATE_SUCCESS,
                                            marketItem));
                            break;
                        case PAY_CANCEL:
                            break;
                    }
                }
            };
        }
        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(this);
        ArrayList<String> payTypes = new ArrayList<>();
        if (dataConfig != null && dataConfig.getPayTypes() != null) {
            payTypes.addAll(dataConfig.getPayTypes());
        }
        new PayConfig.Builder(this).params(new JSONObject())
                .path(Constants.HttpPath.MERCHANT_WEAPP_PAY)
                .price(money)
                .subscriber(paySubscriber)
                .llpayMode(true)
                .payAgents(payTypes, DataConfig.getPayAgents())
                .build()
                .pay();
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case OPEN_ULTIMATE_SUCCESS:
                                    finish();
                                    break;
                            }
                        }
                    });
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSubscriber, paySubscriber, rxBusEventSub);
    }

}
