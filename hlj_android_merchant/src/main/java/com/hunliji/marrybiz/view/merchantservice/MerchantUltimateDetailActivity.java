package com.hunliji.marrybiz.view.merchantservice;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.google.gson.JsonElement;
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
import com.hunliji.marrybiz.api.merchantserver.MerchantServerApi;
import com.hunliji.marrybiz.model.DataConfig;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.merchantservice.MarketItem;
import com.hunliji.marrybiz.model.orders.BdProduct;
import com.hunliji.marrybiz.util.MerchantServerUtil;
import com.hunliji.marrybiz.util.MerchantUserSyncUtil;
import com.hunliji.marrybiz.util.Session;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

/**
 * Created by mo_yu on 2018/1/26.旗舰版详情
 */

public class MerchantUltimateDetailActivity extends HljBaseActivity {

    public static final String ARG_PRODUCT_ID = "product_id";
    public static final String NEED_ULTIMATE_DIALOG = "need_ultimate_dialog";

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
    LinearLayout alreadyOpenLayout;
    @BindView(R.id.tv_preferential_tag)
    TextView tvPreferentialTag;
    @BindView(R.id.tv_product_price)
    TextView tvProductPrice;
    @BindView(R.id.open_price_layout)
    LinearLayout openPriceLayout;
    @BindView(R.id.tv_ultimate_tip)
    TextView tvUltimateTip;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.action_open_status)
    TextView actionOpenStatus;
    @BindView(R.id.tv_validity_date_end)
    TextView tvValidityDateEnd;
    @BindView(R.id.tv_pro_tip)
    TextView tvProTip;
    @BindView(R.id.merchant_service_list_view)
    LinearLayout merchantServiceListView;
    @BindView(R.id.action_see_all)
    TextView actionSeeAll;
    @BindView(R.id.img_merchant_ultimate_education)
    ImageView imgMerchantUltimateEducation;
    private MerchantUser user;
    private List<MarketItem> marketItems;
    private MarketItem jumpMarketItem;
    private MarketItem ultimateMarketItem;
    private long productId;
    private double money;
    private int width;
    private Subscriber<PayRxEvent> paySubscriber;
    private HljHttpSubscriber initSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_ultimate_detail);
        ButterKnife.bind(this);
        initValue();
        initView();
        initLoad();
        setMerchantUltimateView();
    }

    private void initValue() {
        productId = getIntent().getLongExtra(ARG_PRODUCT_ID, 0);
        user = Session.getInstance()
                .getCurrentUser(this);
        marketItems = MerchantServerUtil.getInstance()
                .getMarketItemFromFile(this);
        if (isNotProProduct(productId)) {
            jumpMarketItem = MerchantServerUtil.getInstance()
                    .getMarketItem(this, productId);
        }
        ultimateMarketItem = MerchantServerUtil.getInstance()
                .getMarketItem(this, BdProduct.QI_JIAN_BAN);
        width = CommonUtil.getDeviceSize(this).x;
        boolean needUltimateDialog = getIntent().getBooleanExtra(NEED_ULTIMATE_DIALOG, false);
        if (needUltimateDialog) {
            showNeedUltimateDialog();
        }
    }

    private void showNeedUltimateDialog() {
        DialogUtil.createSingleButtonDialog(this,
                "请尽快升级旗舰版",
                "专业版已全面升级为旗舰版，专业版不再单独售卖，请您及时升级为旗舰版，免费使用更多工具，享受更多权益。",
                "确定",
                null)
                .show();
    }

    private boolean isNotProProduct(long productId) {
        return productId > 0 && productId != BdProduct.QI_JIAN_BAN && productId != BdProduct
                .ZHUAN_YE_BAN;
    }

    private void initView() {
        merchantServiceListView.removeAllViews();
        if (!CommonUtil.isCollectionEmpty(marketItems)) {
            int count = merchantServiceListView.getChildCount();
            int size = Math.min(3, marketItems.size());
            for (int i = 0; i < size; i++) {
                View view = null;
                final MarketItem marketItem = marketItems.get(i);
                if (count > i) {
                    view = merchantServiceListView.getChildAt(i);
                }
                if (view == null) {
                    View.inflate(this,
                            R.layout.merchant_ultimate_list_item,
                            merchantServiceListView);
                    view = merchantServiceListView.getChildAt(merchantServiceListView
                            .getChildCount() - 1);
                }
                TextView tvTitle = view.findViewById(R.id.tv_marketing_title);
                TextView tvContent = view.findViewById(R.id.tv_marketing_content);
                ImageView imageView = view.findViewById(R.id.img_merchant_ultimate_item);
                View lineLayout = view.findViewById(R.id.line_layout);
                tvTitle.setText(marketItem.getTitle());
                tvContent.setText(marketItem.getSubTitle2());
                int imgRes = MerchantServerUtil.getInstance()
                        .getMerchantServerRoundImgRes(marketItem.getProductId());
                if (imgRes > 0) {
                    imageView.setImageResource(imgRes);
                }
                if (i == 0) {
                    lineLayout.setVisibility(View.GONE);
                } else {
                    lineLayout.setVisibility(View.VISIBLE);
                }
            }
        }
        if (ultimateMarketItem != null) {
            tvMarketingContent.setText(ultimateMarketItem.getSubTitle2());
            Glide.with(this)
                    .load(ImagePath.buildPath(ultimateMarketItem.getImagePath())
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
                            imgMerchantUltimateEducation.getLayoutParams().height = Math.round(
                                    resource.getIntrinsicHeight() * width / resource
                                            .getIntrinsicWidth());

                            return false;
                        }
                    })
                    .into(imgMerchantUltimateEducation);
        }
    }

    private void setMerchantUltimateView() {
        if (user.getIsPro() == Merchant.MERCHANT_ULTIMATE) {
            tvProTip.setVisibility(View.GONE);
            int day = 0;
            if (user.getProDate() != null) {
                day = HljTimeUtils.getSurplusDay(user.getProDate());
                SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.label_validity_date),
                        Locale.getDefault());
                tvValidityDate.setText(sdf.format(user.getProDate()));
            }
            alreadyOpenLayout.setVisibility(View.VISIBLE);
            if (day <= 30) {
                //已开通快到期
                openPriceLayout.setVisibility(View.VISIBLE);
                actionOpenStatus.setVisibility(View.VISIBLE);
                actionOpenStatus.setText("立即续费");
                tvValidityDateEnd.setVisibility(View.VISIBLE);
            } else {
                //已开通未到期（30天）
                openPriceLayout.setVisibility(View.GONE);
                actionOpenStatus.setVisibility(View.GONE);
                tvValidityDateEnd.setVisibility(View.GONE);
                imgAlreadyOpen.setImageResource(R.mipmap.icon_complete_green_44_44);
                tvValidityDate.setTextSize(14);
            }
        } else if (user.getIsPro() == Merchant.MERCHANT_PRO) {
            //专业版
            openPriceLayout.setVisibility(View.VISIBLE);
            tvProTip.setVisibility(View.VISIBLE);
            alreadyOpenLayout.setVisibility(View.GONE);
            actionOpenStatus.setVisibility(View.VISIBLE);
            actionOpenStatus.setText("立即订购");
        } else {
            openPriceLayout.setVisibility(View.VISIBLE);
            tvProTip.setVisibility(View.GONE);
            alreadyOpenLayout.setVisibility(View.GONE);
            actionOpenStatus.setVisibility(View.VISIBLE);
            actionOpenStatus.setText("立即订购");
        }
    }

    private void initLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                        @Override
                        public void onNext(JsonElement jsonElement) {
                            money = CommonUtil.getAsDouble(jsonElement, "fee");
                            tvProductPrice.setText(CommonUtil.formatDouble2String(money));
                        }
                    })
                    .build();
            MerchantServerApi.getMerchantProMoneyObb()
                    .subscribe(initSubscriber);
        }
    }

    private void syncMerchantUser() {
        MerchantUserSyncUtil.getInstance()
                .sync(this, new MerchantUserSyncUtil.OnMerchantUserSyncListener() {
                    @Override
                    public void onUserSyncFinish(MerchantUser merchantUser) {
                        user = merchantUser;
                        setMerchantUltimateView();
                    }
                });
    }


    @OnClick(R.id.action_open_status)
    public void onViewClicked() {
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
                            if (isNotProProduct(productId)) {
                                finish();
                                RxBus.getDefault()
                                        .post(new RxEvent(RxEvent.RxEventType.OPEN_ULTIMATE_SUCCESS,
                                                jumpMarketItem));
                            } else {
                                syncMerchantUser();
                            }
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
                .path(Constants.HttpPath.PRO_PAY_URL)
                .price(money)
                .subscriber(paySubscriber)
                .llpayMode(true)
                .payAgents(payTypes, DataConfig.getPayAgents())
                .build()
                .pay();
    }

    @OnClick(R.id.action_see_all)
    public void actionSeeAll() {
        Intent intent = new Intent(this, MerchantUltimateServerListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSubscriber, paySubscriber);
    }
}
