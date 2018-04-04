package com.hunliji.marrybiz.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.views.activities.CrashListActivity;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.merchantserver.MerchantServerApi;
import com.hunliji.marrybiz.api.order.OrderApi;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.merchantservice.MerchantServer;
import com.hunliji.marrybiz.model.orders.BdProduct;
import com.hunliji.marrybiz.util.CacheClear;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.MerchantSupportUtil;
import com.hunliji.marrybiz.util.MerchantUserSyncUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.view.AboutActivity;
import com.hunliji.marrybiz.view.AccountActivity;
import com.hunliji.marrybiz.view.HomeActivity;
import com.hunliji.marrybiz.view.merchantservice.BondPlanDetailActivity;
import com.hunliji.marrybiz.view.merchantservice.MarketingDetailActivity;
import com.hunliji.marrybiz.view.merchantservice.MerchantUltimateDetailActivity;
import com.hunliji.marrybiz.view.merchantservice.MyMerchantServiceListActivity;
import com.hunliji.marrybiz.view.orders.MerchantOrderListActivity;
import com.hunliji.marrybiz.view.shop.EditShopActivity;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTime;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;

/**
 * Created by werther on 16/9/9.
 * 我的fragment
 */
public class UserInfoFragment extends RefreshFragment {

    @BindView(R.id.tv_shop_service)
    TextView tvShopService;
    @BindView(R.id.cache_size)
    TextView cacheSize;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tv_unbind_wechant)
    TextView tvUnbindWechant;
    @BindView(R.id.tv_order_count)
    TextView tvOrderCount;
    @BindView(R.id.pay_order_layout)
    RelativeLayout payOrderLayout;
    @BindView(R.id.action_layout_holder)
    FrameLayout actionLayoutHolder;
    Unbinder unbinder;
    @BindView(R.id.layout_look_more_service)
    LinearLayout layoutLookMoreService;
    @BindView(R.id.layout_service)
    LinearLayout layoutService;
    @BindView(R.id.layout_add_service)
    LinearLayout layoutAddService;
    @BindView(R.id.layout_service_content)
    LinearLayout layoutServiceContent;
    @BindView(R.id.merchant_pro_layout)
    LinearLayout merchantProLayout;
    private MerchantUser user;
    private Dialog callDialog;
    private Subscription rxBusSubscription;
    private Subscription rxBus;
    private HljHttpSubscriber countSubscriber;
    private HljHttpSubscriber serverSub;

    public static UserInfoFragment newInstance() {
        Bundle args = new Bundle();
        UserInfoFragment fragment = new UserInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = Session.getInstance()
                .getCurrentUser(getActivity());
        registerRxBus();
    }

    private void registerRxBus() {
        if (rxBus == null || rxBus.isUnsubscribed()) {
            rxBus = RxBus.getDefault()
                    .toObservable(PayRxEvent.class)
                    .subscribe(new RxBusSubscriber<PayRxEvent>() {
                        @Override
                        protected void onEvent(PayRxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case PAY_SUCCESS:
                                    syncUserInfo();
                                    syncMerchantServer();
                                    break;
                            }
                        }
                    });
        }
    }

    private void syncUserInfo() {
        MerchantUserSyncUtil.getInstance()
                .sync(getContext(), new MerchantUserSyncUtil.OnMerchantUserSyncListener() {
                    @Override
                    public void onUserSyncFinish(MerchantUser user) {
                        UserInfoFragment.this.user = user;
                    }
                });
    }

    /**
     * 商家服务信息
     */
    private void syncMerchantServer() {
        if (user.getShopType() != MerchantUser.SHOP_TYPE_SERVICE) {
            return;
        }
        CommonUtil.unSubscribeSubs(serverSub);
        serverSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(progressBar)
                .setDataNullable(true)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<MerchantServer>>>() {
                    @Override
                    public void onNext(
                            HljHttpData<List<MerchantServer>> listHljHttpData) {
                        setShopService(listHljHttpData);
                    }
                })
                .build();
        MerchantServerApi.getMerchantServerList()
                .subscribe(serverSub);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_info, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        HljBaseActivity.setActionBarPadding(getContext(), actionLayoutHolder);
        try {
            cacheSize.setText((float) (CacheClear.getFolderSize(getActivity().getCacheDir()) /
                    104857) / 10 + "M");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 婚品商家区别
        if (user.getShopType() == MerchantUser.SHOP_TYPE_SERVICE) {
            merchantProLayout.setVisibility(View.VISIBLE);
            payOrderLayout.setVisibility(View.VISIBLE);
            syncMerchantServer();
        } else {
            merchantProLayout.setVisibility(View.GONE);
            payOrderLayout.setVisibility(View.GONE);
        }

        return rootView;
    }

    private void getWaitPayCount() {
        if (countSubscriber == null || countSubscriber.isUnsubscribed())
            countSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                        @Override
                        public void onNext(JsonElement jsonElement) {
                            int count = 0;
                            try {
                                count = jsonElement.getAsJsonObject()
                                        .get("num")
                                        .getAsInt();
                            } catch (Exception ignore) {
                            }
                            if (count > 0) {
                                tvOrderCount.setVisibility(View.VISIBLE);
                                tvOrderCount.setText(getContext().getString(R.string
                                                .format_bd_wait_pay_count,
                                        String.valueOf(count)));
                            } else {
                                tvOrderCount.setVisibility(View.GONE);
                            }
                        }
                    })
                    .build();
        OrderApi.getWaitPayCountObb()
                .subscribe(countSubscriber);
    }

    private void setShopService(
            HljHttpData<List<MerchantServer>> merchantServerData) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        merchantProLayout.setVisibility(View.VISIBLE);
        if (merchantServerData == null || merchantServerData.getData() == null ||
                merchantServerData.getData() == null) {
            return;
        }
        List<MerchantServer> serverList = merchantServerData.getData();
        int size = merchantServerData.getData()
                .size();
        if (size >= 2) {
            serverList = serverList.subList(0, 2);
        }
        if (CommonUtil.isCollectionEmpty(serverList)) {
            layoutService.setVisibility(View.GONE);
            layoutAddService.setVisibility(View.VISIBLE);
        } else {
            layoutService.setVisibility(View.VISIBLE);
            layoutAddService.setVisibility(View.GONE);
            for (int i = 0, childCount = layoutServiceContent.getChildCount(); i < childCount;
                 i++) {
                View childView = layoutServiceContent.getChildAt(i);
                if (i < serverList.size()) {
                    childView.setVisibility(View.VISIBLE);
                    MerchantServerViewHolder holder = null;
                    if (childView.getTag() == null) {
                        holder = new MerchantServerViewHolder(childView);
                        childView.setTag(holder);
                    }
                    holder = (MerchantServerViewHolder) childView.getTag();
                    if (holder != null) {
                        holder.setMerchantServer(serverList.get(i));
                    }
                } else {
                    childView.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void refresh(Object... params) {
        getWaitPayCount();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            try {
                cacheSize.setText((float) (CacheClear.getFolderSize(getActivity().getCacheDir())
                        / 104857) / 10 + "M");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onHiddenChanged(hidden);
    }

    public void clearCache() {
        CacheClear.cleanInternalCache(getActivity());
        CacheClear.clearWebStorage();
        try {
            cacheSize.setText(CacheClear.getFolderSize(getActivity().getCacheDir()) + "M");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void aboutHunliji() {
        Intent intent = new Intent(getActivity(), AboutActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    public void feedback() {
        MerchantSupportUtil.goToSupport(getContext(), Support.SUPPORT_KIND_DEFAULT);
    }

    @OnClick({R.id.clear_row, R.id.service_phone_layout, R.id.about_row, R.id.feed_back_row, R.id
            .account_manager_layout, R.id.merchant_data_layout, R.id.pay_order_layout, R.id
            .layout_look_more_service, R.id.layout_add_service})
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.clear_row:
                clearCache();
                break;
            case R.id.service_phone_layout:
                onServiceCalling();
                break;
            case R.id.about_row:
                aboutHunliji();
                break;
            case R.id.feed_back_row:
                feedback();
                break;
            case R.id.account_manager_layout:
                intent = new Intent(getActivity(), AccountActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
                break;
            case R.id.merchant_data_layout:
                intent = new Intent(getActivity(), EditShopActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
                break;
            case R.id.pay_order_layout:
                intent = new Intent(getActivity(), MerchantOrderListActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
                break;
            case R.id.layout_look_more_service:
                intent = new Intent(getActivity(), MyMerchantServiceListActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
                break;
            case R.id.layout_add_service:
                HomeActivity activity = (HomeActivity) getActivity();
                if (activity == null) {
                    return;
                }
                activity.switchMarkingFragment();
                break;
            default:
                break;
        }
    }

    private void onServiceCalling() {
        if (callDialog != null && callDialog.isShowing()) {
            return;
        }
        callDialog = new Dialog(getActivity(), R.style.BubbleDialogTheme);
        View dialogView = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_confirm_notice, null);
        TextView noticeMsg = (TextView) dialogView.findViewById(R.id.tv_notice_msg);
        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_msg_title);
        tvTitle.setVisibility(View.GONE);
        noticeMsg.setText(R.string.service_phone);
        Button confirm = (Button) dialogView.findViewById(R.id.btn_notice_confirm);
        Button cancel = (Button) dialogView.findViewById(R.id.btn_notice_cancel);
        cancel.setText(R.string.label_cancel);
        confirm.setText(R.string.label_call2);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog.cancel();
                callUp(Uri.parse("tel:" + getString(R.string.service_phone).trim()));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog.cancel();
            }
        });

        callDialog.setContentView(dialogView);
        Window window = callDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(getActivity());
        params.width = Math.round(point.x * 5 / 7);
        params.height = Math.round(params.width * 256 / 380);
        window.setAttributes(params);
        callDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        getWaitPayCount();
        if (rxBusSubscription == null || rxBusSubscription.isUnsubscribed()) {
            rxBusSubscription = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case WECHAT_BIND_CHANGE:
                                    if (rxEvent.getObject() != null && rxEvent.getObject()
                                            instanceof Boolean) {
                                        tvUnbindWechant.setVisibility((Boolean) rxEvent.getObject
                                                () ? View.GONE : View.VISIBLE);
                                    }
                                    break;
                                case MERCHANT_ORDER_PAID:
                                    getWaitPayCount();
                                    break;
                            }
                        }
                    });
        }
        tvUnbindWechant.setVisibility(MerchantUserSyncUtil.getInstance()
                .isBind() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        CommonUtil.unSubscribeSubs(rxBusSubscription, rxBus, countSubscriber, serverSub);
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    class MerchantServerViewHolder {

        @BindView(R.id.img_cover)
        RoundedImageView imgCover;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_des)
        TextView tvDes;
        @BindView(R.id.tv_status)
        TextView tvStatus;
        @BindView(R.id.line)
        View line;

        MerchantServer merchantServer;

        public MerchantServerViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick();
                }
            });
        }

        @OnClick({R.id.img_cover, R.id.tv_name, R.id.tv_des, R.id.tv_status})
        void onItemClick() {
            if (merchantServer == null) {
                return;
            }
            Intent intent;
            switch ((int) merchantServer.getProductId()) {
                case BdProduct.QI_JIAN_BAN:
                    intent = new Intent(getContext(), MerchantUltimateDetailActivity.class);
                    intent.putExtra(MerchantUltimateDetailActivity.ARG_PRODUCT_ID,
                            merchantServer.getProductId());
                    break;
                case BdProduct.ZHUAN_YE_BAN:
                    intent = new Intent(getContext(), MerchantUltimateDetailActivity.class);
                    intent.putExtra(MerchantUltimateDetailActivity.ARG_PRODUCT_ID,
                            merchantServer.getProductId());
                    intent.putExtra(MerchantUltimateDetailActivity.NEED_ULTIMATE_DIALOG, true);
                    break;
                case BdProduct.BAO_ZHENG_JIN:
                    intent = new Intent(getContext(), BondPlanDetailActivity.class);
                    break;
                default:
                    intent = new Intent(getContext(), MarketingDetailActivity.class);
                    intent.putExtra(MarketingDetailActivity.ARG_PRODUCT_ID,
                            merchantServer.getProductId());
                    break;
            }
            startActivity(intent);
        }

        public void setMerchantServer(
                MerchantServer merchantServer) {
            this.merchantServer = merchantServer;
            if (merchantServer == null) {
                return;
            }
            BdProduct product = merchantServer.getProduct();
            tvName.setText(product == null ? null : product.getTitle());
            tvStatus.setText(getStatusString(merchantServer));
            if (merchantServer.getServerEnd() == null ) {
                tvDes.setVisibility(View.GONE);
            } else {
                tvDes.setVisibility(View.VISIBLE);
                if (getDesStringColor(merchantServer) != -1) {
                    tvDes.setTextColor(getDesStringColor(merchantServer));
                }
                tvDes.setText(getDesString(merchantServer));
            }
            if (product != null) {
                int res = getImgRes(product.getId());
                if (res > 0) {
                    Glide.with(getContext())
                            .load(res)
                            .into(imgCover);
                }
            }
        }

        private int getImgRes(long id) {
            if (id == BdProduct.ZHUAN_YE_BAN) {
                return R.mipmap.icon_zhuan_ye_ban;
            } else if (id == BdProduct.BAO_ZHENG_JIN) {
                return R.mipmap.icon_bao_zhengz_jin;
            } else if (id == BdProduct.QI_JIAN_BAN) {
                return R.mipmap.icon_qi_jian_ban;
            } else if (id == BdProduct.XIAO_CHENG_XU) {
                return R.mipmap.icon_xiao_cheng_xu;
            } else if (id == BdProduct.TAO_CAN_RE_BIAO) {
                return R.mipmap.icon_tao_can_re_biao;
            } else if (id == BdProduct.DING_DAN_KE_TUI) {
                return R.mipmap.icon_ding_dan_ke_tui;
            } else if (id == BdProduct.SHANG_JIA_CHENG_NUO) {
                return R.mipmap.icon_shang_jia_cheng_nuo;
            } else if (id == BdProduct.DAO_DIAN_LI) {
                return R.mipmap.icon_dao_dian_li;
            } else if (id == BdProduct.DUO_DIAN_GUAN_LI) {
                return R.mipmap.icon_duo_dian_guan_li;
            } else if (id == BdProduct.TUI_JIAN_CHU_CHUANG) {
                return R.mipmap.icon_tui_jian_chu_chuang;
            } else if (id == BdProduct.ZHU_TI_MU_BAN) {
                return R.mipmap.icon_zhu_ti_mu_ban;
            } else if (id == BdProduct.WEI_GUAN_WANG) {
                return R.mipmap.icon_wei_guan_wang;
            } else if (id == BdProduct.HUO_DONG_WEI_CHUAN_DAN) {
                return R.mipmap.icon_huo_dong_wei_chuan_dan;
            } else if (id == BdProduct.QING_SONG_LIAO) {
                return R.mipmap.icon_qing_song_liao;
            } else if (id == BdProduct.TIAN_YAN_XI_TONG) {
                return R.mipmap.icon_tian_yan_xi_tong;
            } else if (id == BdProduct.YOU_HUI_JUAN) {
                return R.mipmap.icon_you_hui_juan;
            }
            return -1;
        }

        private String getStatusString(MerchantServer merchantServer) {
            if (merchantServer == null) {
                return null;
            }

            if (merchantServer.getProductId() == BdProduct.ZHUAN_YE_BAN) {
                return "升级为旗舰版";
            }

            DateTime serverEnd = merchantServer.getServerEnd();
            if (serverEnd == null) {
                if (merchantServer.getProductId() == BdProduct.BAO_ZHENG_JIN) {
                    return "已加入";
                } else {
                    return null;
                }
            } else {
                switch (merchantServer.getStatus()) {
                    case MerchantServer.STATUS_IN_SERVICE:
                        int day = HljTimeUtils.getSurplusDay(merchantServer.getServerEnd());
                        if (day > 0 && day <= 30) {
                            return "续费";
                        }else{
                            return null;
                        }
                    case MerchantServer.STATUS_OUT_DATE:
                        return "续费";
                    default:
                        return null;
                }
            }
        }

        private String getDesString(MerchantServer merchantServer) {
            if (merchantServer == null) {
                return null;
            }
            if(merchantServer.getStatus() == MerchantServer.STATUS_OUT_DATE){
                return "已到期";
            }
            int day = HljTimeUtils.getSurplusDay(merchantServer.getServerEnd());
            if (day < 0) {
                return null;
            } else if (day <= 30) {
                //即将到期
                return "还有" + day + "天到期";
            } else {
                return "剩" + day + "天到期";
            }
        }

        private int getDesStringColor(MerchantServer merchantServer) {
            if (merchantServer == null) {
                return -1;
            }
            switch (merchantServer.getStatus()) {
                case MerchantServer.STATUS_OUT_DATE:
                    return getContext().getResources()
                            .getColor(R.color.colorAccent);
                case MerchantServer.STATUS_IN_SERVICE:
                    int day = HljTimeUtils.getSurplusDay(merchantServer.getServerEnd());
                    if (day > 0 && day <= 30) {
                        return getContext().getResources()
                                .getColor(R.color.colorAccent);
                    } else {
                        return getContext().getResources()
                                .getColor(R.color.colorGray);
                    }
                default:
                    return getContext().getResources()
                            .getColor(R.color.colorGray);
            }
        }

    }
}
