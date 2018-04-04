package me.suncloud.marrymemo.view.product;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.behavior.AppBarLayoutOverScrollViewBehavior;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.WorkRule;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljcommonlibrary.models.product.FreeShipping;
import com.hunliji.hljcommonlibrary.models.product.ProductComment;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.product.wrappers.CollectUsers;
import com.hunliji.hljcommonlibrary.models.product.wrappers.FreeShippingFeeWrapper;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;
import com.hunliji.hljcommonlibrary.views.widgets.OverScrollViewPager;
import com.hunliji.hljcommonlibrary.views.widgets.OverscrollContainer;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.makeramen.rounded.RoundedImageView;
import com.slider.library.Indicators.CirclePageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.product.ProductDetailAdapter;
import me.suncloud.marrymemo.adpter.product.ProductHeaderPhotoAdapter;
import me.suncloud.marrymemo.adpter.product.ShopBestSaleProductsAdapter;
import me.suncloud.marrymemo.adpter.product.WishListAdapter;
import me.suncloud.marrymemo.adpter.product.viewholder.ProductCommentViewHolder;
import me.suncloud.marrymemo.api.product.ProductApi;
import me.suncloud.marrymemo.api.wallet.WalletApi;
import me.suncloud.marrymemo.fragment.ProductSkuFragment;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.ShippingAddress;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.modulehelper.ModuleUtils;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.TimeUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.util.ViewHelper;
import me.suncloud.marrymemo.view.EditShippingAddressActivity;
import me.suncloud.marrymemo.view.ProductCommentListActivity;
import me.suncloud.marrymemo.view.ProductMerchantActivity;
import me.suncloud.marrymemo.view.ShippingAddressListActivity;
import me.suncloud.marrymemo.view.ShoppingCartActivity;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.view.wallet.OpenMemberActivity;
import me.suncloud.marrymemo.view.work_case.WorkMediaImageActivity;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutButton;
import me.suncloud.marrymemo.widget.merchant.MerchantCouponDialog;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func2;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;

@Route(path = RouterPath.IntentPath.Customer.SHOP_PRODUCT)
public class ShopProductDetailActivity extends HljBaseNoBarActivity implements
        ProductHeaderPhotoAdapter.OnItemClickListener, OverscrollContainer.OnLoadListener,
        ProductDetailAdapter.OnItemImageClickListener {

    private AppBarLayoutOverScrollViewBehavior overScrollViewBehavior;

    @Override
    public String pageTrackTagName() {
        return "婚品详情";
    }

    @Override
    public VTMetaData pageTrackData() {
        long id = getIntent().getLongExtra(ARG_ID, 0);
        return new VTMetaData(id, VTMetaData.DATA_TYPE.DATA_TYPE_PRODUCT);
    }

    public final static String ARG_ID = "id";
    public static final int SALE_STATE_NONE = 1; // 没有或结束
    public static final int SALE_STATE_NOW = 2; // 进行中
    public static final int SALE_STATE_PRE = 3; // 预告中

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.layout_header)
    RelativeLayout layoutHeader;
    @BindView(R.id.shadow_view)
    FrameLayout shadowView;
    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.btn_merchant)
    ImageView btnMerchant;
    @BindView(R.id.btn_share)
    ImageButton btnShare;
    @BindView(R.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R.id.btn_back2)
    ImageButton btnBack2;
    @BindView(R.id.btn_merchant2)
    ImageView btnMerchant2;
    @BindView(R.id.btn_share2)
    ImageButton btnShare2;
    @BindView(R.id.action_holder_layout2)
    LinearLayout actionHolderLayout2;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.back_top_btn)
    ImageButton backTopBtn;
    @BindView(R.id.tv_sold_out)
    TextView tvSoldOut;
    @BindView(R.id.btn_chat)
    LinearLayout btnChat;
    @BindView(R.id.img_shopping_cart)
    ImageView imgShoppingCart;
    @BindView(R.id.tv_shopping_cart)
    TextView tvShoppingCart;
    @BindView(R.id.tv_cart_count)
    TextView tvCartCount;
    @BindView(R.id.btn_shopping_cart)
    RelativeLayout btnShoppingCart;
    @BindView(R.id.iv_collect)
    ImageView ivCollect;
    @BindView(R.id.tv_collect)
    TextView tvCollect;
    @BindView(R.id.btn_collect)
    LinearLayout btnCollect;
    @BindView(R.id.btn_cart)
    Button btnCart;
    @BindView(R.id.btn_buy)
    Button btnBuy;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.shop_product_skuImage)
    RoundedImageView shopProductSkuImage;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.items_view)
    OverScrollViewPager itemsView;
    @BindView(R.id.flow_indicator)
    CirclePageIndicator flowIndicator;
    @BindView(R.id.limit_count)
    TextView limitCount;
    @BindView(R.id.limit_count_layout)
    LinearLayout limitCountLayout;
    @BindView(R.id.tv_sale_market_price)
    TextView tvSaleMarketPrice;
    @BindView(R.id.tv_sale_price)
    TextView tvSalePrice;
    @BindView(R.id.tv_days)
    TextView tvDays;
    @BindView(R.id.tv_hour)
    TextView tvHour;
    @BindView(R.id.tv_minute)
    TextView tvMinute;
    @BindView(R.id.tv_second)
    TextView tvSecond;
    @BindView(R.id.sales_layout)
    LinearLayout salesLayout;
    @BindView(R.id.info_content)
    FrameLayout infoContent;
    @BindView(R.id.check_1)
    CheckableLinearLayoutButton cb1;
    @BindView(R.id.check_2)
    CheckableLinearLayoutButton cb2;

    private long productId;
    private boolean isFromShop;
    private City city;
    private String site;
    private ShopProduct product;
    private ShippingAddress selectAddress;
    private int headerImageWidth;
    private ProductDetailAdapter adapter;
    private HeaderViewHolder holder1;
    private HeaderViewHolder2 holder2;
    private MerchantCouponDialog couponDialog;
    private int verticalOffset;
    private ArrayList<ShopProduct> shopProducts;
    private ShopBestSaleProductsAdapter productsAdapter;
    private LinearLayoutManager layoutManager;
    private HljHttpSubscriber initSubscriber;
    private HljHttpSubscriber freeShippingFeeSubscriber;
    private HljHttpSubscriber merchantSubscriber;
    private Subscription collectSubscription;
    private Subscription rxBusEventSub;
    private int lastSaleState = SALE_STATE_NONE; // 上次的活动状态

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (appbar.getTotalScrollRange() > Math.abs(verticalOffset)) {
                cb1.setChecked(true);
                cb2.setChecked(false);
            } else {
                onScrollTabChange();
            }
        }
    };

    AppBarLayout.OnOffsetChangedListener offsetChangedListener = new AppBarLayout
            .OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int vo) {
            if (appbar == null) {
                return;
            }
            verticalOffset = vo;
            if (Math.abs(vo) > appbar.getTotalScrollRange()) {
                actionHolderLayout2.setAlpha(1);
                shadowView.setAlpha(0);
                onScrollTabChange();
            } else {
                float alpha = (float) Math.abs(vo) / appbar.getTotalScrollRange();
                actionHolderLayout2.setAlpha(alpha);
                shadowView.setAlpha(1 - alpha);
                cb1.setChecked(true);
                cb2.setChecked(false);
            }
        }
    };

    View.OnClickListener onCheckClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final int offsetPosition;
            final int offset;
            switch (v.getId()) {
                case R.id.check_2:
                    offsetPosition = 2;
                    offset = -CommonUtil.dp2px(ShopProductDetailActivity.this, 10);
                    break;
                default:
                    offsetPosition = 0;
                    offset = 0;
                    break;
            }

            appbar.setExpanded(offsetPosition == 0);
            layoutManager.scrollToPositionWithOffset(offsetPosition, offset);

            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (cb2 != null && cb1 != null) {
                        if (v.getId() == R.id.check_2) {
                            cb1.setChecked(false);
                            cb2.setChecked(true);
                        } else {
                            cb1.setChecked(true);
                            cb2.setChecked(false);
                        }
                    }
                }
            }, 400);
        }
    };

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HljShare.RequestCode.SHARE_TO_QQZONE:
                    trackerShare("QQZone");
                    break;
                case HljShare.RequestCode.SHARE_TO_QQ:
                    trackerShare("QQ");
                    break;
                case HljShare.RequestCode.SHARE_TO_PENGYOU:
                    trackerShare("Timeline");
                    break;
                case HljShare.RequestCode.SHARE_TO_WEIXIN:
                    trackerShare("Session");
                    break;
            }
            return false;
        }
    });

    /**
     * 活动倒计时
     */
    private Runnable timeDownRun = new Runnable() {
        @Override
        public void run() {
            setSaleView();
        }
    };
    private ProductHeaderPhotoAdapter photoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_product_detail);
        ButterKnife.bind(this);

        initValues();
        initTracker();
        initViews();
        initLoad(progressBar);

        registerRxBusEvent();
    }

    private void initValues() {
        site = getIntent().getStringExtra("site");
        productId = getIntent().getLongExtra(ARG_ID, 0);
        isFromShop = getIntent().getBooleanExtra("is_from_shop", false);

        city = LocationSession.getInstance()
                .getCity(this);
        shopProducts = new ArrayList<>();
        headerImageWidth = CommonUtil.getDeviceSize(this).x;

        adapter = new ProductDetailAdapter(this);
        adapter.setItemImageClickListener(this);
        productsAdapter = new ShopBestSaleProductsAdapter(this, shopProducts);
    }

    private void initViews() {
        setActionBarPadding(actionHolderLayout, actionHolderLayout2);
        tvSaleMarketPrice.getPaint()
                .setAntiAlias(true);
        tvSaleMarketPrice.getPaint()
                .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    }

    private void initLoad(final View pgBar) {
        //获取详情
        initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(pgBar)
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        actionHolderLayout2.setAlpha(1);
                        shadowView.setAlpha(0);
                    }
                })
                .setOnNextListener(new SubscriberOnNextListener<ShopProduct>() {
                    @Override
                    public void onNext(ShopProduct shopProduct) {
                        product = shopProduct;
                        actionHolderLayout2.setAlpha(0);
                        shadowView.setAlpha(1);
                        bottomLayout.setVisibility(View.VISIBLE);
                        initProductInfo();
                        initAddressData(pgBar);

                        Intent intent = getIntent();
                        intent.putExtra("product", product);
                        setResult(RESULT_OK, intent);
                    }
                })
                .build();
        ProductApi.getShopProduct(productId, city.getCid())
                .subscribe(initSubscriber);
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
                                case OPEN_MEMBER_SUCCESS:
                                    refreshMemberLayout();
                                    break;
                                case REFRESH_CART_ITEM_COUNT:
                                    int count = (int) rxEvent.getObject();
                                    if (count > 0) {
                                        tvCartCount.setVisibility(View.VISIBLE);
                                        tvCartCount.setText(String.valueOf(count));
                                    } else {
                                        tvCartCount.setVisibility(View.GONE);
                                    }
                                    break;
                            }
                        }
                    });
        }
    }

    private void initTracker() {
        //详情统计
        new HljTracker.Builder(this).action("hit")
                .eventableType("Article")
                .eventableId(productId)
                .site(site)
                .build()
                .send();
        HljVTTagger.buildTagger(cb1)
                .tagName("shop_product_nav_tab")
                .hitTag();
        HljVTTagger.buildTagger(cb2)
                .tagName("shop_product_nav_tab")
                .hitTag();
    }

    private void initProductInfo() {
        product.initProductInfo();

        tvSaleMarketPrice.setVisibility(View.VISIBLE);
        tvSaleMarketPrice.setText(getString(R.string.label_price,
                NumberFormatUtil.formatDouble2StringWithTwoFloat(product.getMarketPrice())) + "  ");

        initHeaderView();
        setSaleView();
        if (product.isCollect()) {
            ivCollect.setImageResource(R.drawable.icon_collect_primary_44_44_selected);
            tvCollect.setText(R.string.label_collected___cm);
        }
        adapter.setProduct(product);
        adapter.setPhotos(product.getDetailPhotos());
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        appbar.addOnOffsetChangedListener(offsetChangedListener);
        recyclerView.addOnScrollListener(scrollListener);
        cb1.setOnClickListener(onCheckClickListener);
        cb2.setOnClickListener(onCheckClickListener);

        try {
            overScrollViewBehavior = (AppBarLayoutOverScrollViewBehavior) ((CoordinatorLayout
                    .LayoutParams) appbar.getLayoutParams()).getBehavior();
            if (overScrollViewBehavior != null) {
                overScrollViewBehavior.setMaxOverScroll(headerImageWidth * 7 / 10);
                overScrollViewBehavior.addOnOverScrollListener(new AppBarLayoutOverScrollViewBehavior.OnOverScrollListener() {
                    @Override
                    public void onOverScrollBy(int height, int overScroll) {
                        if (layoutHeader == null) {
                            return;
                        }
                        layoutHeader.getLayoutParams().height = height + overScroll;
                        layoutHeader.requestLayout();
                        ViewHelper.setPivotX(itemsView, itemsView.getWidth() / 2);
                        ViewHelper.setPivotY(itemsView, 0);
                        itemsView.setScaleY(1 + ((float) overScroll) / headerImageWidth);
                        itemsView.setScaleX(1 + ((float) overScroll) / headerImageWidth);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onScrollTabChange() {
        int currentPosition = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0));
        Log.d("Product Position", currentPosition + "");
        if (currentPosition >= 2) {
            backTopBtn.setVisibility(View.VISIBLE);
            cb1.setChecked(false);
            cb2.setChecked(true);
        } else {
            backTopBtn.setVisibility(View.GONE);
            cb1.setChecked(true);
            cb2.setChecked(false);
        }
    }

    /**
     * 活动相关ui
     */
    private void setSaleView() {
        if (holder1 == null) {
            return;
        }
        long millisInFuture = 0;
        long millisInStart = 0;
        WorkRule rule = product.getRule();
        if (rule != null) {
            long currentTimeMillis = HljTimeUtils.getServerCurrentTimeMillis();
            millisInFuture = rule.getEndTime() == null ? 0 : rule.getEndTime()
                    .getMillis() - currentTimeMillis;
            millisInStart = rule.getStartTime() == null ? 0 : rule.getStartTime()
                    .getMillis() - currentTimeMillis;
        }
        holder1.priceLayout.setVisibility(View.VISIBLE);
        int curSaleState;
        if (millisInFuture <= 0) {
            //活动结束或无活动
            curSaleState = SALE_STATE_NONE;
            product.saleEnd();
            holder1.discountType.setVisibility(View.GONE);
            holder1.prepareLayout.setVisibility(View.GONE);
            limitCountLayout.setVisibility(View.GONE);
            salesLayout.setVisibility(View.GONE);
        } else if (millisInStart <= 0) {
            //活动进行中
            curSaleState = SALE_STATE_NOW;
            product.saleStart();
            salesLayout.setVisibility(View.VISIBLE);
            holder1.priceLayout.setVisibility(View.GONE);

            holder1.prepareLayout.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(rule.getShowText())) {
                holder1.discountType.setVisibility(View.VISIBLE);
                holder1.discountType.setText(product.getRule()
                        .getShowText());
            } else {
                holder1.discountType.setVisibility(View.GONE);
            }
            if (rule.getType() == 2) {
                limitCountLayout.setVisibility(View.VISIBLE);
                limitCount.setText(String.valueOf(product.getLimitCount()));
            } else {
                limitCountLayout.setVisibility(View.GONE);
            }

            setCountdownTimer(millisInFuture);
        } else {
            //活动未开始
            curSaleState = SALE_STATE_PRE;
            holder1.discountType.setVisibility(View.GONE);
            limitCountLayout.setVisibility(View.GONE);
            salesLayout.setVisibility(View.GONE);
            holder1.prepareLayout.setVisibility(View.VISIBLE);
            holder1.prepareLabel.setText(rule.getName() + getString(R.string
                    .label_work_discount_label));
            holder1.preparePrice.setText(getString(R.string.label_price5,
                    NumberFormatUtil.formatDouble2StringWithTwoFloat(product.getSalePrice())));
            holder1.prepareCountDown.setText(getString(R.string.label_work_left1) + millisFormat(
                    millisInStart));
        }
        if (product.getFloorPrice() == product.getTopPrice()) {
            holder1.price.setText(NumberFormatUtil.formatDouble2StringWithTwoFloat(product
                    .getFloorPrice()));
            tvSalePrice.setText("¥ " + NumberFormatUtil.formatDouble2StringWithTwoFloat(product
                    .getFloorPrice()));
        } else {
            holder1.price.setText(getString(R.string.label_shop_by_price2,
                    NumberFormatUtil.formatDouble2StringWithTwoFloat(product.getFloorPrice()),
                    NumberFormatUtil.formatDouble2StringWithTwoFloat(product.getTopPrice())));
            tvSalePrice.setText("¥ " + getString(R.string.label_shop_by_price2,
                    NumberFormatUtil.formatDouble2StringWithTwoFloat(product.getFloorPrice()),
                    NumberFormatUtil.formatDouble2StringWithTwoFloat(product.getTopPrice())));
        }

        if (curSaleState != lastSaleState) {
            initLoad(null);
            lastSaleState = curSaleState;
        } else if (millisInFuture > 0) {
            handler.postDelayed(timeDownRun, 1000);
        }
        onProductStatus();
    }

    private void setCountdownTimer(long millisTime) {
        int days = (int) (millisTime / (1000 * 60 * 60 * 24));
        if (days > 0) {
            tvDays.setVisibility(View.VISIBLE);
            tvDays.setText(String.valueOf(days) + "天");
        } else {
            tvDays.setVisibility(View.GONE);
        }
        long leftMillis = millisTime % (1000 * 60 * 60 * 24);
        int hours = (int) (leftMillis / (1000 * 60 * 60));
        leftMillis %= 1000 * 60 * 60;
        int minutes = (int) (leftMillis / (1000 * 60));
        leftMillis %= 1000 * 60;
        int seconds = (int) (leftMillis / 1000);
        String hourStr = hours < 10 ? "0" + hours : String.valueOf(hours);
        String minuteStr = minutes < 10 ? "0" + minutes : String.valueOf(minutes);
        String secondStr = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
        tvHour.setText(hourStr);
        tvMinute.setText(minuteStr);
        tvSecond.setText(secondStr);
    }

    private String millisFormat(long millisTime) {
        int days = (int) (millisTime / (1000 * 60 * 60 * 24));
        return (days > 0 ? getString(R.string.label_day,
                days + "") : "") + TimeUtil.countDownMillisFormat(this, millisTime);
    }

    private void onProductStatus() {
        if (!product.isPublished()) {
            ToastUtil.showToast(this, null, R.string.msg_product_error);
            tvSoldOut.setVisibility(View.VISIBLE);
            tvSoldOut.setText(getString(R.string.label_sold_out,
                    getString(R.string.label_product2)));
            btnBuy.setEnabled(false);
            btnBuy.setText(R.string.btn_sold_out);
            btnCart.setVisibility(View.GONE);
        } else {
            tvSoldOut.setVisibility(View.GONE);
            if (product.getProductCount() == 0) {
                btnBuy.setEnabled(false);
                btnBuy.setText(R.string.btn_buy_over);
                btnCart.setVisibility(View.GONE);
            } else {
                btnBuy.setEnabled(true);
                btnBuy.setText(R.string.btn_buy);
                btnCart.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initCouponAndProducts(long merchantId) {
        CommonUtil.unSubscribeSubs(merchantSubscriber);
        merchantSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                    @Override
                    public void onNext(ResultZip resultZip) {
                        if (!CommonUtil.isCollectionEmpty(resultZip.couponInfos)) {
                            holder1.tvCoupon.setText(getCouponListStr(resultZip.couponInfos));
                            holder1.couponLayout.setVisibility(View.VISIBLE);
                        } else {
                            holder1.couponLayout.setVisibility(View.GONE);
                        }
                        if (!CommonUtil.isCollectionEmpty(resultZip.shopProducts)) {
                            holder1.rvProducts.setVisibility(View.VISIBLE);
                            shopProducts.clear();
                            shopProducts.addAll(resultZip.shopProducts);
                            productsAdapter.notifyDataSetChanged();
                        } else {
                            holder1.rvProducts.setVisibility(View.GONE);
                        }
                    }
                })
                .build();
        StringBuilder url = new StringBuilder(String.format(Constants.HttpPath.GET_MERCHANT_GOODS,
                merchantId));
        url.append("&order=")
                .append("sold_count");
        Observable<HljHttpData<List<ShopProduct>>> cObservable = CommonApi.getProductsObb(url
                        .toString(),
                1,
                7);
        Observable<List<CouponInfo>> wObservable = WalletApi.getMerchantCouponListObb(merchantId);
        Observable<ResultZip> observable = Observable.zip(cObservable,
                wObservable,
                new Func2<HljHttpData<List<ShopProduct>>, List<CouponInfo>, ResultZip>() {
                    @Override
                    public ResultZip call(
                            HljHttpData<List<ShopProduct>> listHljHttpData,
                            List<CouponInfo> couponInfos) {
                        ResultZip resultZip = new ResultZip();
                        if (listHljHttpData != null) {
                            resultZip.shopProducts = new ArrayList<>();
                            for (ShopProduct shopProduct : listHljHttpData.getData()) {
                                if (shopProduct.getId() != productId && resultZip.shopProducts
                                        .size() < 6) {
                                    resultZip.shopProducts.add(shopProduct);
                                }
                            }
                        }
                        resultZip.couponInfos = couponInfos;
                        return resultZip;
                    }
                });
        observable.subscribe(merchantSubscriber);
    }

    private void initHeaderView() {
        View headerView = View.inflate(this, R.layout.product_detail_header, null);
        holder1 = (HeaderViewHolder) headerView.getTag();
        if (holder1 == null) {
            holder1 = new HeaderViewHolder(headerView);
            headerView.setTag(holder1);
        }
        refreshMemberLayout();
        adapter.setHeaderView(headerView);

        if (!CommonUtil.isCollectionEmpty(product.getHeaderPhotos())) {
            layoutHeader.setVisibility(View.VISIBLE);
            layoutHeader.getLayoutParams().width = headerImageWidth;
            itemsView.getLayoutParams().height = headerImageWidth * 350 / 375;
            itemsView.setOverable(!CommonUtil.isCollectionEmpty(product.getDetailPhotos()));
            photoAdapter = new ProductHeaderPhotoAdapter(product.getHeaderPhotos(),
                    headerImageWidth);
            photoAdapter.setOnItemClickListener(this);
            itemsView.getOverscrollView()
                    .setAdapter(photoAdapter);
            flowIndicator.setViewPager(itemsView.getOverscrollView());
            itemsView.setOnLoadListener(this);
        } else {
            layoutHeader.setVisibility(View.GONE);
        }

        if (product.getMerchant()
                .isSelfRun()) {
            SpannableStringBuilder builder = new SpannableStringBuilder(" " + product.getTitle());
            View view = View.inflate(this,
                    com.hunliji.hljlivelibrary.R.layout.tag_introduced_layout___live,
                    null);
            ((TextView) view.findViewById(com.hunliji.hljlivelibrary.R.id.tv_title)).setText("自营");
            builder.setSpan(new HljImageSpan(ImageUtil.getDrawingCache(this, view)),
                    0,
                    1,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            holder1.title.setText(builder);
        } else {
            holder1.title.setText(product.getTitle());
        }
        holder1.tvCommentCount.setText(product.getCommentCount() > 999 ? "999+条" : String.valueOf(
                product.getCommentCount()) + "条");
        holder1.tvCommentCount.setTextColor(ContextCompat.getColor(this,
                product.getCommentCount() > 99 ? R.color.color_orange2 : R.color.colorBlack3));
        holder1.originalPrice.setVisibility(View.VISIBLE);
        holder1.originalPrice.setText("原价" + getString(R.string.label_price,
                NumberFormatUtil.formatDouble2StringWithTwoFloat(product.getMarketPrice())) + "  ");
        holder1.rvProducts.setLayoutManager(new LinearLayoutManager(this, HORIZONTAL, false));
        holder1.rvProducts.setAdapter(productsAdapter);

        if (!product.isCanRefund()) {
            holder1.canRefundIcon.setVisibility(View.GONE);
            holder1.tvRefundMsg.setVisibility(View.GONE);
        } else {
            holder1.canRefundIcon.setVisibility(View.VISIBLE);
            holder1.tvRefundMsg.setVisibility(View.VISIBLE);
        }
        if (product.getDeliverTimeType() == ShopProduct.DELIVER_TIME_48_HOURS) {
            holder1.tvShippingMsg.setText("48小时发货");
        } else if (product.getDeliverTimeType() == ShopProduct.DELIVER_TIME_72_HOURS) {
            holder1.tvShippingMsg.setText("72小时发货");
        }
        holder1.distributionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AuthUtil.loginBindCheck(ShopProductDetailActivity.this)) {
                    if (selectAddress != null) {
                        Intent intent = new Intent(ShopProductDetailActivity.this,
                                ShippingAddressListActivity.class);
                        intent.putExtra("select", true);
                        if (selectAddress != null) {
                            intent.putExtra("selected_address", selectAddress);
                        }
                        startActivityForResult(intent,
                                Constants.RequestCode.SELECT_SHIPPING_ADDRESS);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    } else {
                        Intent intent = new Intent(ShopProductDetailActivity.this,
                                EditShippingAddressActivity.class);
                        intent.putExtra("is_first", true);
                        intent.putExtra("select", true);
                        startActivityForResult(intent,
                                Constants.RequestCode.SELECT_SHIPPING_ADDRESS);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            }
        });
        initShippingFee(product.getFreeShipping(), product.getShipingFee());
        initMerchantView(holder1);

        View headerView2 = View.inflate(this, R.layout.product_detail_header2, null);
        holder2 = (HeaderViewHolder2) headerView2.getTag();
        if (holder2 == null) {
            holder2 = new HeaderViewHolder2(headerView2);
            headerView2.setTag(holder2);
        }
        adapter.setHeaderView2(headerView2);

        initCommentView(holder2);
        initWishListView(holder2);
    }

    private void initShippingFee(FreeShipping freeShipping, double shippingFee) {
        if (freeShipping == null) {
            holder1.tvShipping.setText(shippingFee <= 0 ? getString(R.string.label_free_postage1)
                    : getString(
                    R.string.label_shipping_fee1,
                    CommonUtil.formatDouble2String(shippingFee)));
            if (shippingFee <= 0) {
                holder1.tvFreeShippingMsg.setVisibility(View.VISIBLE);
                holder1.tvFreeShippingMsg.setText("商家包邮");
            } else {
                holder1.tvFreeShippingMsg.setVisibility(View.GONE);
            }
        } else {
            String shippingText = getFreeShipping(freeShipping);
            holder1.tvShipping.setText(shippingText);
            holder1.tvFreeShippingMsg.setText(shippingText);
            holder1.tvFreeShippingMsg.setVisibility(View.VISIBLE);
        }
    }

    private String getFreeShipping(FreeShipping freeShipping) {
        if (freeShipping == null) {
            return null;
        }
        String freeShippingStr = null;
        int type = freeShipping.getType();
        if (type == 0) {
            freeShippingStr = "满" + CommonUtil.formatDouble2String(freeShipping.getMoney()) + "元包邮";
        } else if (type == 1) {
            freeShippingStr = "满" + freeShipping.getNum() + "件包邮";
        }
        return freeShippingStr;
    }

    /**
     * 初始化商家相关信息
     */
    private void initMerchantView(HeaderViewHolder holder) {
        if (product.getMerchant() == null) {
            return;
        }
        final Merchant merchant = product.getMerchant();
        initCouponAndProducts(merchant.getId());
        holder.merchantLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMerchant();
            }
        });
        holder.bondIcon.setVisibility(merchant.getBondSign() != null ? View.VISIBLE : View.GONE);
        holder.merchantName.setText(merchant.getName());
        holder.tvSaleCount.setText("累计销量 " + merchant.getSaleCount());
        String goodRateStr = merchant.getGoodRatingPercent() > 0 ? merchant.getGoodRatingPercent
                () * 100 + "%" : "——";
        holder.tvGoodRate.setText("好评率 " + goodRateStr);
        int logoSize = CommonUtil.dp2px(this, 44);
        String logoPath = JSONUtil.getImagePath2(merchant.getLogoPath(), logoSize);
        Glide.with(this)
                .load(logoPath)
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_avatar_primary))
                .into(holder.merchantLogo);
        holder.couponLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AuthUtil.loginBindCheck(ShopProductDetailActivity.this)) {
                    return;
                }
                if (couponDialog != null && couponDialog.isShowing()) {
                    return;
                }
                if (couponDialog == null) {
                    couponDialog = new MerchantCouponDialog(ShopProductDetailActivity.this,
                            merchant.getId(),
                            0);
                }
                couponDialog.getCoupons();
            }
        });
    }

    /**
     * 评论
     */
    private void initCommentView(HeaderViewHolder2 holder) {
        if (product.getLastComment() == null) {
            holder.commentLayout.setVisibility(View.GONE);
            return;
        }
        ProductComment comment = product.getLastComment();
        holder.commentLayout.setVisibility(View.VISIBLE);
        holder.commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product == null) {
                    return;
                }
                Intent intent = new Intent(ShopProductDetailActivity.this,
                        ProductCommentListActivity.class);
                intent.putExtra("id", product.getId());
                startActivity(intent);
            }
        });
        holder.tvProductGoodRate.setText(product.getGoodRatingPercent() > 0 ? product
                .getGoodRatingPercent() * 100 + "% 好评" : "");
        holder.commentCount.setText(getString(R.string.label_user_comment2,
                product.getCommentCount() + ""));
        new ProductCommentViewHolder(holder.commentItemLayout).setView(this, comment, 0, 0);
    }

    /**
     * 用户收藏
     */
    private void initWishListView(HeaderViewHolder2 holder) {
        if (product.getCollectUsers() == null || product.getCollectUsers()
                .getUsers() == null || product.getCollectUsers()
                .getUsers()
                .isEmpty()) {
            holder.collectLayout.setVisibility(View.GONE);
            return;
        }
        CollectUsers collectUsers = product.getCollectUsers();
        holder.collectLayout.setVisibility(View.VISIBLE);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int space = Math.round(dm.density * 6);
        int limitCount = 4;

        holder.productCollectCount.setText(Html.fromHtml(getString(R.string
                        .label_product_collect_list,
                collectUsers.getNum() + "")));
        View footView = View.inflate(this, R.layout.user_icon_footer_view, null);
        WishListAdapter wishListAdapter = new WishListAdapter(this);
        wishListAdapter.setLimitCount(limitCount);
        wishListAdapter.setUsers(collectUsers.getUsers());
        wishListAdapter.setFootView(footView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.collectUsersView.addItemDecoration(new SpacesItemDecoration(space));
        holder.collectUsersView.setLayoutManager(layoutManager);
        holder.collectUsersView.setAdapter(wishListAdapter);
    }

    private void initAddressData(View pgBar) {
        User user = Session.getInstance()
                .getCurrentUser(this);
        if (selectAddress == null && user != null && user.getId() > 0) {
            // 请求默认地址
            new GetDefaultAddressTask().execute();
        } else {
            refreshDistributionInfo(pgBar);
        }
    }


    private void refreshDistributionInfo(View pgBar) {
        if (holder1 != null && product != null && product.getMerchant() != null) {
            String merchantCity = product.getMerchant()
                    .getCityName();
            if (TextUtils.isEmpty(merchantCity)) {
                merchantCity = "杭州";
            }
            if (selectAddress != null) {
                holder1.tvDistribution.setText(merchantCity + " 到 " + selectAddress.toString());
                refreshFreeShippingAndFee(selectAddress.getId(), pgBar);
            } else {
                holder1.tvDistribution.setText(merchantCity + " 到 " + (city == null ? "杭州" : city
                        .getCid() > 0 ? city.getName() : ""));
            }
        }
    }

    private void refreshFreeShippingAndFee(long addressId, View pgBar) {
        CommonUtil.unSubscribeSubs(freeShippingFeeSubscriber);
        freeShippingFeeSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<FreeShippingFeeWrapper>() {
                    @Override
                    public void onNext(FreeShippingFeeWrapper freeShippingFeeWrapper) {
                        initShippingFee(freeShippingFeeWrapper.getFreeShipping(),
                                freeShippingFeeWrapper.getShippingFee());
                    }
                })
                .setProgressBar(pgBar)
                .build();
        ProductApi.getFreeShippingFeeObb(addressId, productId)
                .subscribe(freeShippingFeeSubscriber);
    }

    /**
     * 会员开通入口，已开通不显示
     */
    private void refreshMemberLayout() {
        User user = Session.getInstance()
                .getCurrentUser(this);
        if (user == null || user.getMember() == null) {
            DataConfig dataConfig = Session.getInstance()
                    .getDataConfig(this);
            if (dataConfig != null && !TextUtils.isEmpty(dataConfig.getProductDetailMemberRemind
                    ())) {
                holder1.tvMemberRemind.setText("使用会员红包," + dataConfig
                        .getProductDetailMemberRemind());
            }
            holder1.memberPrivilegeLayout.setVisibility(View.VISIBLE);
            holder1.memberPrivilegeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(ShopProductDetailActivity.this, OpenMemberActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            holder1.memberPrivilegeLayout.setVisibility(View.GONE);
        }
    }

    //优惠券按照满多少减多少 从大到小排序
    private String getCouponListStr(List<CouponInfo> couponInfoList) {
        if (couponInfoList == null) {
            return null;
        }
        //无门槛
        List<CouponInfo> noMoneySillList = new ArrayList<>();
        //有门槛
        List<CouponInfo> moneySillList = new ArrayList<>();
        //已排序的优惠券
        List<CouponInfo> sortedList = new LinkedList<>();
        for (CouponInfo info : couponInfoList) {
            if (info.getMoneySill() <= 0D) {
                noMoneySillList.add(info);
            } else {
                moneySillList.add(info);
            }
        }
        Collections.sort(noMoneySillList, new Comparator<CouponInfo>() {
            @Override
            public int compare(CouponInfo o1, CouponInfo o2) {
                return (int) (o2.getValue() - o1.getValue());
            }
        });

        Collections.sort(moneySillList, new Comparator<CouponInfo>() {
            @Override
            public int compare(CouponInfo info1, CouponInfo info2) {
                return (int) (info2.getValue() - info1.getValue());
            }
        });
        Collections.sort(moneySillList, new Comparator<CouponInfo>() {
            @Override
            public int compare(CouponInfo info1, CouponInfo info2) {
                return (int) (info1.getMoneySill() - info2.getMoneySill());
            }
        });

        sortedList.addAll(noMoneySillList);
        sortedList.addAll(moneySillList);
        StringBuilder couponStr = new StringBuilder();
        for (CouponInfo couponInfo : sortedList) {
            couponStr.append(getCouponStr(couponInfo))
                    .append("  ");
        }
        return couponStr.toString();
    }

    private String getCouponStr(CouponInfo couponInfo) {
        String couponStr;
        if (couponInfo.getMoneySill() > 0) {
            couponStr = "满" + Util.formatDouble2String(couponInfo.getMoneySill()) + "减" + Util
                    .formatDouble2String(
                    couponInfo.getValue());
        } else {
            couponStr = "下单减" + Util.formatDouble2String(couponInfo.getValue()) + "元";
        }
        return couponStr;
    }

    private void trackerShare(String shareInfo) {
        new HljTracker.Builder(this).eventableId(product.getId())
                .eventableType("Article")
                .action("share")
                .additional(shareInfo)
                .sid("AA1/A1")
                .pos(3)
                .desc("分享")
                .build()
                .send();
    }


    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSubscriber,
                freeShippingFeeSubscriber,
                merchantSubscriber,
                collectSubscription,
                rxBusEventSub);
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(timeDownRun);
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (product != null && product.getRule() != null && product.getRule()
                .getId() > 0) {
            setSaleView();
        }
        int count = Session.getInstance()
                .getCartCount(this);
        if (count > 0) {
            tvCartCount.setVisibility(View.VISIBLE);
            tvCartCount.setText(String.valueOf(count));
        } else {
            tvCartCount.setVisibility(View.GONE);
        }
        super.onResume();
    }

    @OnClick({R.id.btn_back, R.id.btn_back2})
    void onBack() {
        onBackPressed();
    }

    @OnClick({R.id.btn_share, R.id.btn_share2})
    public void onShare() {
        if (product != null && product.getShare() != null) {
            ShareDialogUtil.onCommonShare(this, product.getShare(), handler);
        }
    }

    /**
     * 商品购物车
     */
    @OnClick(R.id.btn_shopping_cart)
    public void onToCart() {
        if (AuthUtil.loginBindCheck(this)) {
            new HljTracker.Builder(this).eventableType("Cart")
                    .action("hit")
                    .sid("AA1/A1")
                    .pos(4)
                    .desc("购物车")
                    .build()
                    .send();
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    /**
     * 联系商家
     */
    @OnClick(R.id.btn_chat)
    public void onChat() {
        if (product == null || product.getMerchant() == null || product.getMerchant()
                .getUserId() == 0) {
            return;
        }
        if (AuthUtil.loginBindCheck(this)) {
            Intent intent = new Intent(this, WSCustomerChatActivity.class);
            intent.putExtra("user",
                    product.getMerchant()
                            .toUser());
            intent.putExtra("ws_track", ModuleUtils.getWSTrack(product));
            startActivity(intent);
        }
    }

    /**
     * 商家主页
     */
    @OnClick(R.id.btn_merchant)
    public void onMerchant() {
        if (product == null || product.getMerchant() == null) {
            return;
        }
        if (isFromShop) {
            onBackPressed();
        } else {
            Intent intent = new Intent(this, ProductMerchantActivity.class);
            intent.putExtra("id",
                    product.getMerchant()
                            .getId());
            intent.putExtra("sid", "AA1/D1");
            startActivity(intent);
        }
    }

    @OnClick(R.id.back_top_btn)
    void onBackToTop() {
        appbar.setExpanded(true);
        layoutManager.scrollToPositionWithOffset(0, 0);
    }

    @SuppressWarnings("unchecked")
    @OnClick(R.id.btn_collect)
    public void onCollect() {
        if (product == null || !AuthUtil.loginBindCheck(this)) {
            return;
        }
        if (collectSubscription != null && !collectSubscription.isUnsubscribed()) {
            return;
        }
        if (!product.isCollect()) {
            new HljTracker.Builder(this).eventableType("Article")
                    .eventableId(product.getId())
                    .action("collect")
                    .sid("AA1/A1")
                    .pos(2)
                    .desc("收藏")
                    .build()
                    .send();
            product.setCollect(true);
            ivCollect.setImageResource(R.drawable.icon_collect_primary_44_44_selected);
            tvCollect.setText(R.string.label_collected___cm);
            collectSubscription = ProductApi.collect(product.getId())
                    .subscribe(HljHttpSubscriber.buildSubscriber(this)
                            .setOnNextListener(new SubscriberOnNextListener<Boolean>() {
                                @Override
                                public void onNext(Boolean b) {
                                    if (b) {
                                        //收藏成功
                                        product.setCollectCount(product.getCollectCount() + 1);
                                        if (Util.isNewFirstCollect(ShopProductDetailActivity.this,
                                                4)) {
                                            //首次弹窗
                                            Util.showFirstCollectNoticeDialog(
                                                    ShopProductDetailActivity.this,
                                                    4);
                                        } else {
                                            ToastUtil.showToast(ShopProductDetailActivity.this,
                                                    null,
                                                    R.string.msg_product_collect_succeed);
                                        }
                                        updateWishListView(true);
                                    }
                                }
                            })
                            .build());
        } else {
            new HljTracker.Builder(this).eventableType("Article")
                    .eventableId(product.getId())
                    .action("del_collect")
                    .sid("AA1/A1")
                    .pos(2)
                    .desc("取消收藏")
                    .build()
                    .send();
            product.setCollect(false);
            ivCollect.setImageResource(R.drawable.icon_collect_black_44_44);
            tvCollect.setText(R.string.label_collect___cm);
            collectSubscription = ProductApi.cancelCollect(product.getId())
                    .subscribe(HljHttpSubscriber.buildSubscriber(this)
                            .setOnNextListener(new SubscriberOnNextListener<Boolean>() {
                                @Override
                                public void onNext(Boolean b) {
                                    if (b) {
                                        //取消收藏成功
                                        product.setCollectCount(product.getCollectCount() - 1);
                                        ToastUtil.showToast(ShopProductDetailActivity.this,
                                                null,
                                                R.string.msg_product_del_collect_succeed);
                                        updateWishListView(false);
                                    }
                                }
                            })
                            .build());
        }
    }

    /**
     * 加入购物车
     */
    @OnClick(R.id.btn_cart)
    public void onAddCart() {
        showProductSku(true);
    }

    /**
     * 立即购买
     */
    @OnClick(R.id.btn_buy)
    public void onBuy() {
        showProductSku(false);
    }

    /**
     * 商品sku 详情
     *
     * @param addCart 是否加入购物车
     */
    private void showProductSku(boolean addCart) {
        if (product == null) {
            return;
        }
        ProductSkuFragment productSkuFragment = (ProductSkuFragment) getSupportFragmentManager()
                .findFragmentByTag(
                "productSkuFragment");
        if (productSkuFragment != null) {
            return;
        }
        infoContent.setAlpha(1);
        productSkuFragment = (ProductSkuFragment) Fragment.instantiate(this,
                ProductSkuFragment.class.getName());
        Bundle bundle = new Bundle();
        bundle.putParcelable("product", product);
        bundle.putBoolean("addCart", addCart);
        productSkuFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_up_to_top,
                R.anim.slide_out_down,
                R.anim.slide_in_up_to_top,
                R.anim.slide_out_down);
        ft.add(R.id.info_content, productSkuFragment, "productSkuFragment");
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }


    private void updateWishListView(boolean isCollect) {
        if (holder2 == null || UserSession.getInstance()
                .getUser(this) == null || holder2.collectUsersView.getAdapter() == null) {
            return;
        }
        Author author = UserSession.getInstance()
                .getUser(this)
                .toAuthor();
        WishListAdapter wishListAdapter = (WishListAdapter) holder2.collectUsersView.getAdapter();
        if (isCollect) {
            wishListAdapter.addUser(author);
        } else {
            wishListAdapter.removeUser(author);
        }
        holder2.collectLayout.setVisibility(wishListAdapter.getItemCount() > 0 ? View.VISIBLE :
                View.GONE);
        holder2.productCollectCount.setText(Html.fromHtml(getString(R.string
                        .label_product_collect_list,
                product.getCollectCount() + "")));


    }

    @Override
    public void onPhotoItemClick(Object item, int position) {
        Photo t = (Photo) item;
        if (t != null) {
            Intent intent = new Intent(this, ProductImageActivity.class);
            intent.putExtra(ProductImageActivity.ARG_PRODUCT, product);
            intent.putParcelableArrayListExtra(ProductImageActivity.ARG_PHOTOS,
                    product.getHeaderPhotos());
            intent.putExtra(ProductImageActivity.ARG_POSITION, position);
            startActivityForResult(intent, Constants.RequestCode.PRODUCT_IMAGE_ITEM_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.PRODUCT_IMAGE_ITEM_IMAGE:
                    if (data.getBooleanExtra(WorkMediaImageActivity.ARG_IS_LOAD, false)) {
                        onLoad();
                    }
                    break;
                case HljShare.RequestCode.SHARE_TO_TXWEIBO:
                    trackerShare("TXWeibo");
                    break;
                case HljShare.RequestCode.SHARE_TO_WEIBO:
                    trackerShare("Weibo");
                    break;
                case Constants.RequestCode.SELECT_SHIPPING_ADDRESS:
                    if (data != null) {
                        ShippingAddress address = (ShippingAddress) data.getSerializableExtra(
                                "address");
                        boolean deletedSelectedAddress = data.getBooleanExtra(
                                "deleted_selected_address",
                                false);
                        if (address != null) {
                            selectAddress = address;
                            refreshDistributionInfo(progressBar);
                        }
                        if (deletedSelectedAddress) {
                            selectAddress = null;
                            progressBar.setVisibility(View.VISIBLE);
                            new GetDefaultAddressTask().execute();
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onLoad() {
        if (product == null) {
            return;
        }
        onCheckClickListener.onClick(cb2);

    }

    @Override
    public void onItemImageClick(Object item, int position) {
        Photo t = (Photo) item;
        if (t != null) {
            Intent intent = new Intent(this, ProductImageActivity.class);
            intent.putExtra(ProductImageActivity.ARG_PRODUCT, product);
            intent.putParcelableArrayListExtra(ProductImageActivity.ARG_PHOTOS,
                    product.getDetailPhotos());
            intent.putExtra(ProductImageActivity.ARG_POSITION,
                    product.getDetailPhotos()
                            .indexOf(t));
            intent.putExtra(ProductImageActivity.ARG_LOAD_DISABLE, true);
            startActivityForResult(intent, Constants.RequestCode.PRODUCT_IMAGE_ITEM_IMAGE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetDefaultAddressTask extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.getAbsUrl(Constants.HttpPath.DEFAULT_SHIPPING_ADDRESS);
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus.getRetCode() == 0) {
                    JSONObject dataObject = jsonObject.optJSONObject("data");
                    if (dataObject != null) {
                        selectAddress = new ShippingAddress(dataObject);
                    }
                }

            }
            refreshDistributionInfo(null);
            super.onPostExecute(jsonObject);
        }
    }

    class HeaderViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.discount_type)
        TextView discountType;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.original_price)
        TextView originalPrice;
        @BindView(R.id.prepare_label)
        TextView prepareLabel;
        @BindView(R.id.prepare_price)
        TextView preparePrice;
        @BindView(R.id.prepare_count_down)
        TextView prepareCountDown;
        @BindView(R.id.prepare_layout)
        LinearLayout prepareLayout;
        @BindView(R.id.tv_free_shipping_msg)
        TextView tvFreeShippingMsg;
        @BindView(R.id.img_shipping_hint)
        ImageView imgShippingHint;
        @BindView(R.id.tv_shipping_msg)
        TextView tvShippingMsg;
        @BindView(R.id.can_refund_icon)
        ImageView canRefundIcon;
        @BindView(R.id.tv_refund_msg)
        TextView tvRefundMsg;
        @BindView(R.id.tv_coupon)
        TextView tvCoupon;
        @BindView(R.id.coupon_layout)
        LinearLayout couponLayout;
        @BindView(R.id.tv_member_remind)
        TextView tvMemberRemind;
        @BindView(R.id.img_arrow_right)
        ImageView imgArrowRight;
        @BindView(R.id.member_privilege_layout)
        LinearLayout memberPrivilegeLayout;
        @BindView(R.id.tv_distribution)
        TextView tvDistribution;
        @BindView(R.id.distribution_layout)
        LinearLayout distributionLayout;
        @BindView(R.id.tv_shipping)
        TextView tvShipping;
        @BindView(R.id.shipping_layout)
        LinearLayout shippingLayout;
        @BindView(R.id.merchant_logo)
        RoundedImageView merchantLogo;
        @BindView(R.id.merchant_name)
        TextView merchantName;
        @BindView(R.id.bond_icon)
        ImageView bondIcon;
        @BindView(R.id.merchant_name_layout)
        LinearLayout merchantNameLayout;
        @BindView(R.id.tv_sale_count)
        TextView tvSaleCount;
        @BindView(R.id.tv_good_rate)
        TextView tvGoodRate;
        @BindView(R.id.merchant_layout)
        LinearLayout merchantLayout;
        @BindView(R.id.price_layout)
        LinearLayout priceLayout;
        @BindView(R.id.comment_count_layout)
        LinearLayout commentCountLayout;
        @BindView(R.id.tv_comment_count)
        TextView tvCommentCount;
        @BindView(R.id.rv_merchant_products)
        RecyclerView rvProducts;

        HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
            originalPrice.getPaint()
                    .setAntiAlias(true);
            originalPrice.getPaint()
                    .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            initTracker();
            view.setFocusable(false);
            merchantLayout.setFocusable(false);
            rvProducts.setFocusable(false);
        }

        @OnClick(R.id.comment_count_layout)
        void onCommentLayout() {
            if (product == null || product.getCommentCount() <= 0) {
                return;
            }
            Intent intent = new Intent(ShopProductDetailActivity.this,
                    ProductCommentListActivity.class);
            intent.putExtra("id", product.getId());
            startActivity(intent);
        }

        private void initTracker() {
            HljVTTagger.buildTagger(distributionLayout)
                    .tagName("address_item")
                    .hitTag();
            HljVTTagger.buildTagger(couponLayout)
                    .tagName("get_coupon_item")
                    .dataType("Article")
                    .dataId(productId)
                    .hitTag();
            HljVTTagger.buildTagger(commentCountLayout)
                    .tagName("shop_product_header_comment")
                    .hitTag();
        }
    }

    static class HeaderViewHolder2 {
        @BindView(R.id.comment_count)
        TextView commentCount;
        @BindView(R.id.more_comment_layout)
        LinearLayout moreCommentLayout;
        @BindView(R.id.comment_item_layout)
        View commentItemLayout;
        @BindView(R.id.comment_layout)
        LinearLayout commentLayout;
        @BindView(R.id.product_collect_count)
        TextView productCollectCount;
        @BindView(R.id.users_view)
        RecyclerView collectUsersView;
        @BindView(R.id.collect_layout)
        LinearLayout collectLayout;
        @BindView(R.id.tv_product_good_rate)
        TextView tvProductGoodRate;
        @BindView(R.id.line_layout)
        View dividerLine;

        HeaderViewHolder2(View view) {
            ButterKnife.bind(this, view);
            dividerLine.setVisibility(View.GONE);
        }
    }

    private class ResultZip {
        List<ShopProduct> shopProducts;
        List<CouponInfo> couponInfos;
    }


    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        private SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) != 0)
                outRect.left = space;
        }
    }

}
