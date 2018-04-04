package com.hunliji.marrybiz.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.market.MarketAdapter;
import com.hunliji.marrybiz.adapter.market.viewholder.MarketHeaderViewHolder;
import com.hunliji.marrybiz.api.merchant.MerchantApi;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.Privilege;
import com.hunliji.marrybiz.model.merchant.MerchantPrivilegeRecord;
import com.hunliji.marrybiz.model.merchant.ShopInfo;
import com.hunliji.marrybiz.model.merchantservice.MarketGroup;
import com.hunliji.marrybiz.model.merchantservice.MarketItem;
import com.hunliji.marrybiz.model.merchantservice.MerchantServer;
import com.hunliji.marrybiz.model.merchantservice.MerchantServerEnum;
import com.hunliji.marrybiz.model.orders.BdProduct;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.popuptip.PopupRule;
import com.hunliji.marrybiz.view.EventLeafletsListActivity;
import com.hunliji.marrybiz.view.PrivilegeActivity;
import com.hunliji.marrybiz.view.WXWallActivity;
import com.hunliji.marrybiz.view.WorkMarketActivity;
import com.hunliji.marrybiz.view.coupon.MyCouponListActivity;
import com.hunliji.marrybiz.view.easychat.EasyChatActivity;
import com.hunliji.marrybiz.view.event.MyEventListActivity;
import com.hunliji.marrybiz.view.merchantservice.MarketingDetailActivity;
import com.hunliji.marrybiz.view.shop.ShopThemeActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;

import static com.hunliji.marrybiz.model.merchantservice.MerchantServerEnum.DAO_DIAN_LI;
import static com.hunliji.marrybiz.model.merchantservice.MerchantServerEnum.DING_DAN_KE_TUI;
import static com.hunliji.marrybiz.model.merchantservice.MerchantServerEnum.DUO_DIAN_GUAN_LI;
import static com.hunliji.marrybiz.model.merchantservice.MerchantServerEnum.HUO_DONG_WEI_CHUAN_DAN;
import static com.hunliji.marrybiz.model.merchantservice.MerchantServerEnum.JU_KE_BAO;
import static com.hunliji.marrybiz.model.merchantservice.MerchantServerEnum.QING_SONG_LIAO;
import static com.hunliji.marrybiz.model.merchantservice.MerchantServerEnum.SHANG_JIA_CHENG_NUO;
import static com.hunliji.marrybiz.model.merchantservice.MerchantServerEnum.TAO_CAN_RE_BIAO;
import static com.hunliji.marrybiz.model.merchantservice.MerchantServerEnum.TIAN_YAN_XI_TONG;
import static com.hunliji.marrybiz.model.merchantservice.MerchantServerEnum.TUI_JIAN_CHU_CHUANG;
import static com.hunliji.marrybiz.model.merchantservice.MerchantServerEnum.WEDDING_WALL;
import static com.hunliji.marrybiz.model.merchantservice.MerchantServerEnum.WEI_GUAN_WANG;
import static com.hunliji.marrybiz.model.merchantservice.MerchantServerEnum.XIAO_CHENG_XU;
import static com.hunliji.marrybiz.model.merchantservice.MerchantServerEnum.YOU_HUI_JUAN;
import static com.hunliji.marrybiz.model.merchantservice.MerchantServerEnum.ZHU_TI_MU_BAN;

/**
 * Created by hua_rong on 17/12/25.
 * <p>
 * 营销fragment
 */
public class MarketingFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, OnItemClickListener<MarketItem> {


    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.action_layout_holder)
    FrameLayout actionLayoutHolder;

    private Unbinder unbinder;
    private HljHttpSubscriber refreshSubscriber;
    private MarketAdapter adapter;
    private View headerView;
    private MarketHeaderViewHolder headerViewHolder;
    private List<MarketGroup> marketGroups;
    private List<Privilege> privileges;
    private Privilege giftPrivilege;
    private Privilege orderPrivilege;
    private Privilege promisePrivilege;
    private Privilege chatPrivilege;
    private MerchantUser merchantUser;
    private OnItemClickListener<MarketItem> onItemClickListener;
    private ShopInfo shopInfo;
    private HljHttpSubscriber initSubscriber;
    private Subscription rxBusEventSub;
    private MerchantServerEnum[] obtainArtifactEnums = {YOU_HUI_JUAN, TIAN_YAN_XI_TONG,
            JU_KE_BAO, QING_SONG_LIAO, HUO_DONG_WEI_CHUAN_DAN, WEDDING_WALL};
    private MerchantServerEnum[] storeExpandEnums = {XIAO_CHENG_XU, WEI_GUAN_WANG, ZHU_TI_MU_BAN,
            TUI_JIAN_CHU_CHUANG, DUO_DIAN_GUAN_LI};
    private MerchantServerEnum[] qualityServiceEnums = {DAO_DIAN_LI, SHANG_JIA_CHENG_NUO,
            DING_DAN_KE_TUI, TAO_CAN_RE_BIAO};

    public static MarketingFragment newInstance() {
        return new MarketingFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        marketGroups = new ArrayList<>();
        privileges = new ArrayList<>();
        initHeaderView();
    }

    private void initHeaderView() {
        headerView = View.inflate(getContext(), R.layout.market_header_layout, null);
        headerViewHolder = new MarketHeaderViewHolder(headerView);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_marketing, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        HljBaseActivity.setActionBarPadding(getContext(), actionLayoutHolder);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initError();
        onRefresh(recyclerView);
        registerRxBusEvent();
        initMarketItems();
    }

    private void initMarketItems() {
        marketGroups.clear();
        //获客神器
        MarketGroup obtainArtifact = getObtainArtifact();
        marketGroups.add(obtainArtifact);
        //门店拓展
        MarketGroup storeExpand = getStoreExpand();
        marketGroups.add(storeExpand);
        //品质服务
        MarketGroup qualityService = getQualityService();
        marketGroups.add(qualityService);
        headerViewHolder.setView(getContext(), null, 0, 0);
        adapter.setRecords(marketGroups);
    }

    @Override
    public void onResume() {
        super.onResume();
        merchantUser = Session.getInstance()
                .getCurrentUser(getContext());
    }

    private void initError() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(recyclerView);
            }
        });
    }

    private void initView() {
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setOnRefreshListener(this);
        adapter = new MarketAdapter(getContext());
        adapter.setOnItemClickListener(this);
        adapter.setHeaderView(headerView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .addItemDecoration(new MarketItemDecoration(getContext()));
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    public void refresh(Object... params) {}

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        CommonUtil.unSubscribeSubs(refreshSubscriber);
        Observable<MerchantPrivilegeRecord> recordObb = MerchantApi
                .getMerchantPrivilegeRecordList();
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                .setPullToRefreshBase(refreshView)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<MerchantPrivilegeRecord>() {
                    @Override
                    public void onNext(MerchantPrivilegeRecord record) {
                        privileges.clear();
                        if (record != null && !CommonUtil.isCollectionEmpty(record.getRecords())) {
                            privileges.addAll(record.getRecords());
                        }
                        initPrivilege();
                    }
                })
                .build();
        recordObb.subscribe(refreshSubscriber);
    }

    @Override
    public void onItemClick(int position, final MarketItem marketItem) {
        if (merchantUser == null || marketItem == null) {
            return;
        }
        PopupRule popupRule = PopupRule.getDefault();
        if (popupRule.showShopReview(getActivity(), merchantUser)) {
            return;
        }
        switch ((int) marketItem.getProductId()) {
            case BdProduct.WEDDING_WALL://婚礼墙
                Intent intent = new Intent(getContext(), WXWallActivity.class);
                startActivity(intent);
                break;
            case BdProduct.TIAN_YAN_XI_TONG://天眼系统
            case BdProduct.WEI_GUAN_WANG://微官网
            case BdProduct.DUO_DIAN_GUAN_LI://多店管理
            case BdProduct.TUI_JIAN_CHU_CHUANG://推荐橱窗
            case BdProduct.XIAO_CHENG_XU://小程序
            case BdProduct.JU_KE_BAO://聚客宝
            case BdProduct.HUO_DONG_WEI_CHUAN_DAN://活动微传单
            case BdProduct.ZHU_TI_MU_BAN://主题模板
                goMarketDetail(marketItem);
                break;
            case BdProduct.DAO_DIAN_LI://到店礼
            case BdProduct.SHANG_JIA_CHENG_NUO://商家承诺
            case BdProduct.DING_DAN_KE_TUI://订单可退
            case BdProduct.TAO_CAN_RE_BIAO://套餐热标
            case BdProduct.QING_SONG_LIAO://轻松聊
            case BdProduct.YOU_HUI_JUAN://优惠券
                if (merchantUser.isSpecialChildMerchant()) {
                    //特殊店铺的子店铺无法使用此功能
                    popupRule.showSpecialDisableDlg(getContext());
                    return;
                }
                goMarketDetail(marketItem);
                break;
        }
    }

    private void goTianYanXiTong(final MarketItem marketItem) {
        //重新请求 是否开通是cpm商家
        if (merchantUser == null) {
            return;
        }
        int pro = merchantUser.getIsPro();//0 普通 1 专业 2旗舰
        if (pro == Merchant.MERCHANT_ULTIMATE) {
            goUserMarketClick(marketItem);
        } else if (shopInfo != null) {
            if (shopInfo.getCpmStatus() == ShopInfo.TYPE_OPEN) {
                goUserMarketClick(marketItem);
            } else {
                Intent intent = new Intent(getContext(), MarketingDetailActivity.class);
                intent.putExtra(MarketingDetailActivity.ARG_PRODUCT_ID, marketItem.getProductId());
                startActivity(intent);
            }
        } else {
            CommonUtil.unSubscribeSubs(initSubscriber);
            initSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<ShopInfo>() {
                        @Override
                        public void onNext(ShopInfo shopInfo) {
                            MarketingFragment.this.shopInfo = shopInfo;
                            if (shopInfo.getCpmStatus() == ShopInfo.TYPE_OPEN) {//开通cpm
                                goUserMarketClick(marketItem);
                            } else {
                                Intent intent = new Intent(getContext(),
                                        MarketingDetailActivity.class);
                                intent.putExtra(MarketingDetailActivity.ARG_PRODUCT_ID,
                                        marketItem.getProductId());
                                startActivity(intent);
                            }
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            MerchantApi.getShopInfoObb()
                    .subscribe(initSubscriber);
        }
    }

    private void goMarketDetail(MarketItem marketItem) {
        if (getContext() == null || merchantUser == null) {
            return;
        }
        long productId = marketItem.getProductId();
        switch ((int) productId) {
            case BdProduct.QING_SONG_LIAO://轻松聊
            case BdProduct.TIAN_YAN_XI_TONG://天眼系统
                goTianYanXiTong(marketItem);
                return;
            case BdProduct.XIAO_CHENG_XU://小程序
            case BdProduct.TUI_JIAN_CHU_CHUANG://推荐橱窗
                Intent intent = new Intent(getContext(), MarketingDetailActivity.class);
                intent.putExtra(MarketingDetailActivity.ARG_PRODUCT_ID, productId);
                startActivity(intent);
                break;
        }
        if (merchantUser.isPro()) {
            Intent intent = null;
            switch ((int) productId) {
                case BdProduct.WEI_GUAN_WANG://微官网
                case BdProduct.DUO_DIAN_GUAN_LI://多店管理
                    intent = new Intent(getContext(), MarketingDetailActivity.class);
                    intent.putExtra(MarketingDetailActivity.ARG_PRODUCT_ID, productId);
                    break;
                case BdProduct.JU_KE_BAO://聚客宝
                    intent = new Intent(getContext(), MyEventListActivity.class);
                    break;
                case BdProduct.HUO_DONG_WEI_CHUAN_DAN://活动微传单
                    if (merchantUser.getIsPro() == Merchant.MERCHANT_ULTIMATE) {
                        intent = new Intent(getContext(), EventLeafletsListActivity.class);
                    } else {
                        intent = new Intent(getContext(), MarketingDetailActivity.class);
                        intent.putExtra(MarketingDetailActivity.ARG_PRODUCT_ID, productId);
                    }
                    break;
                case BdProduct.ZHU_TI_MU_BAN://主题模板
                    if (merchantUser.getIsPro() == Merchant.MERCHANT_ULTIMATE) {
                        intent = new Intent(getContext(), ShopThemeActivity.class);
                    } else {
                        intent = new Intent(getContext(), MarketingDetailActivity.class);
                        intent.putExtra(MarketingDetailActivity.ARG_PRODUCT_ID, productId);
                    }
                    break;
                case BdProduct.TAO_CAN_RE_BIAO://套餐热标
                    intent = new Intent(getContext(), WorkMarketActivity.class);
                    break;
                case BdProduct.YOU_HUI_JUAN://优惠券
                    intent = new Intent(getContext(), MyCouponListActivity.class);
                    break;
                case BdProduct.DAO_DIAN_LI://到店礼
                    if (giftPrivilege != null) {
                        intent = new Intent(getContext(), PrivilegeActivity.class);
                        intent.putExtra("privilege", giftPrivilege);
                    }
                    break;
                case BdProduct.SHANG_JIA_CHENG_NUO://商家承诺
                    if (merchantUser.isBondSign()) {
                        if (promisePrivilege != null) {
                            intent = new Intent(getContext(), PrivilegeActivity.class);
                            intent.putExtra("privilege", promisePrivilege);
                        }
                    } else {
                        intent = new Intent(getContext(), MarketingDetailActivity.class);
                        intent.putExtra(MarketingDetailActivity.ARG_PRODUCT_ID, productId);
                    }
                    break;
                case BdProduct.DING_DAN_KE_TUI://订单可退
                    if (merchantUser.isBondSign()) {
                        if (orderPrivilege != null) {
                            intent = new Intent(getContext(), PrivilegeActivity.class);
                            intent.putExtra("privilege", orderPrivilege);
                        }
                    } else {
                        intent = new Intent(getContext(), MarketingDetailActivity.class);
                        intent.putExtra(MarketingDetailActivity.ARG_PRODUCT_ID, productId);
                    }
                    break;
            }
            if (intent != null) {
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(getContext(), MarketingDetailActivity.class);
            intent.putExtra(MarketingDetailActivity.ARG_PRODUCT_ID, productId);
            startActivity(intent);
        }
    }

    private void initPrivilege() {
        if (CommonUtil.isCollectionEmpty(privileges)) {
            return;
        }
        for (Privilege privilege : privileges) {
            if (privilege.getType() == Privilege.Market.TYPE_SHOP_GIFT) {
                giftPrivilege = privilege;
            } else if (privilege.getType() == Privilege.Market.TYPE_RETURN_ORDER) {
                orderPrivilege = privilege;
            } else if (privilege.getType() == Privilege.Market.TYPE_MERCHANT_PROMISE) {
                promisePrivilege = privilege;
            } else if (privilege.getType() == Privilege.Market.TYPE_EASY_CHAT) {
                chatPrivilege = privilege;
            }
        }
    }

    /**
     * 跳转到消息页面
     */
    private void goUserMarketClick(MarketItem marketItem) {
        if (marketItem == null) {
            return;
        }
        if (marketItem.getProductId() == BdProduct.QING_SONG_LIAO) {
            int status = chatPrivilege == null ? 0 : chatPrivilege.getStatus();
            Intent chatIntent = new Intent(getContext(), EasyChatActivity.class);
            chatIntent.putExtra("isActive", status == 1);
            startActivity(chatIntent);
        } else {
            Intent intent = new Intent(getContext(), MarketingDetailActivity.class);
            intent.putExtra(MarketingDetailActivity.ARG_PRODUCT_ID, marketItem.getProductId());
            startActivity(intent);
        }
    }


    private class ResultZip extends HljHttpResultZip {

        @HljRZField
        MerchantPrivilegeRecord record;
        @HljRZField
        List<MerchantServer> data;

        private ResultZip(
                MerchantPrivilegeRecord record, List<MerchantServer> data) {
            this.record = record;
            this.data = data;
        }
    }

    /**
     * 获客神器
     */
    private MarketGroup getObtainArtifact() {
        MarketGroup obtainArtifactGroup = new MarketGroup(getString(R.string
                .label_obtain_artifact));
        List<MarketItem> marketItems = new ArrayList<>();
        for (MerchantServerEnum merchantServerEnum : obtainArtifactEnums) {
            MarketItem marketItem = new MarketItem();
            marketItem.setLogo(merchantServerEnum.getIcon());
            marketItem.setTitle(merchantServerEnum.getTitle());
            marketItem.setProductId(merchantServerEnum.getProductId());
            marketItems.add(marketItem);
        }
        obtainArtifactGroup.setMarketItems(marketItems);
        return obtainArtifactGroup;
    }

    /**
     * 门店拓展
     */
    private MarketGroup getStoreExpand() {
        MarketGroup storeExpandGroup = new MarketGroup(getString(R.string.label_store_expand));
        List<MarketItem> marketItems = new ArrayList<>();
        for (MerchantServerEnum merchantServerEnum : storeExpandEnums) {
            MarketItem marketItem = new MarketItem();
            marketItem.setLogo(merchantServerEnum.getIcon());
            marketItem.setTitle(merchantServerEnum.getTitle());
            marketItem.setProductId(merchantServerEnum.getProductId());
            marketItems.add(marketItem);
        }
        storeExpandGroup.setMarketItems(marketItems);
        return storeExpandGroup;
    }

    /**
     * 品质服务
     */
    private MarketGroup getQualityService() {
        MarketGroup qualityServiceGroup = new MarketGroup(getString(R.string
                .label_quality_service));
        List<MarketItem> marketItems = new ArrayList<>();
        for (MerchantServerEnum merchantServerEnum : qualityServiceEnums) {
            MarketItem marketItem = new MarketItem();
            marketItem.setLogo(merchantServerEnum.getIcon());
            marketItem.setTitle(merchantServerEnum.getTitle());
            marketItem.setProductId(merchantServerEnum.getProductId());
            marketItems.add(marketItem);
        }
        qualityServiceGroup.setMarketItems(marketItems);
        return qualityServiceGroup;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, initSubscriber, rxBusEventSub);
    }

    public class MarketItemDecoration extends RecyclerView.ItemDecoration {

        private int bottom;

        MarketItemDecoration(Context context) {
            bottom = CommonUtil.dp2px(context, 10);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = bottom;
        }

    }

    public void setOnItemClickListener(OnItemClickListener<MarketItem> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
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
                                    MarketItem marketItem = (MarketItem) rxEvent.getObject();
                                    if (marketItem != null) {
                                        merchantUser = Session.getInstance()
                                                .getCurrentUser(getContext());
                                        //购买旗舰版完成回调，用户信息同步可能比较慢，先手动设置
                                        // 旗舰版状态和时间
                                        long afterTime = 365L * 24L * 60L * 60L * 1000L;
                                        long time = 0;
                                        if (merchantUser.getProDate() == null) {
                                            //未开通过旗舰
                                            time = HljTimeUtils.getServerCurrentTimeMillis() +
                                                    afterTime;
                                        } else if (merchantUser.getIsPro() == Merchant
                                                .MERCHANT_ULTIMATE) {
                                            //旗舰版续费，在原有的过期时间上加一年
                                            time = merchantUser.getProDate()
                                                    .getTime() + afterTime;
                                        }
                                        //专业版升级，不需要重新设置过期时间
                                        if (time > 0) {
                                            Date proDate = new Date();
                                            proDate.setTime(time);
                                            merchantUser.setProDate(proDate);
                                        }
                                        merchantUser.setIsPro(Merchant.MERCHANT_ULTIMATE);
                                        goMarketDetail(marketItem);
                                    }
                                    break;
                                case OPEN_MERCHANT_SERVICE_SUCCESS:
                                    MarketItem merchantServer = (MarketItem) rxEvent.getObject();
                                    if (merchantServer != null) {
                                        merchantServer.setStatus(MerchantServer.STATUS_IN_SERVICE);
                                        goMarketDetail(merchantServer);
                                    }
                                    break;
                            }
                        }
                    });
        }
    }
}