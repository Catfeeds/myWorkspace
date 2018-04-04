package me.suncloud.marrymemo.view.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcardcustomerlibrary.models.RedPacket;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.wallet.CertificateCenterRecyclerAdapter;
import me.suncloud.marrymemo.api.wallet.WalletApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.wallet.MemberRedPacket;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.widget.merchant.MerchantCouponDialog;
import rx.Subscription;


/**
 * 领券中心
 * Created by chen_bin on 2016/11/30 0030.
 */
public class CertificateCenterListActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, CertificateCenterRecyclerAdapter.OnReceiveListener {
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private HeaderViewHolder headerViewHolder;
    private CertificateCenterRecyclerAdapter adapter;
    private long id;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber receiveRedPacketSub;
    private HljHttpSubscriber receiveCouponSub;
    private Subscription rxBusEventSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        ButterKnife.bind(this);
        id = getIntent().getLongExtra("id", 0);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        View headerView = View.inflate(this,
                R.layout.certificate_center_member_packet_list_item,
                null);
        headerViewHolder = new HeaderViewHolder(headerView);
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(this));
        adapter = new CertificateCenterRecyclerAdapter(this);
        adapter.setHeaderView(headerView);
        adapter.setOnReceiveListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        registerRxBusEvent();
        onRefresh(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            refreshSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<SparseArray<Object>>() {
                        @Override
                        public void onNext(SparseArray<Object> sparseArray) {
                            if (headerViewHolder == null) {
                                return;
                            }
                            List<RedPacket> redPackets = (List<RedPacket>) sparseArray.get(0);
                            List<CouponInfo> coupons = (List<CouponInfo>) sparseArray.get(1);
                            MemberRedPacket memberRedPacket = (MemberRedPacket) sparseArray.get(2);
                            User user = Session.getInstance()
                                    .getCurrentUser(CertificateCenterListActivity.this);
                            if (memberRedPacket == null || memberRedPacket.getId() == 0 || (user
                                    != null && user.getMember() != null)) {
                                headerViewHolder.memberLayout.setVisibility(View.GONE);
                            } else {
                                headerViewHolder.memberLayout.setVisibility(View.VISIBLE);
                                headerViewHolder.tvValue.setText(CommonUtil.formatDouble2String(
                                        memberRedPacket.getTotalMoney()));
                                headerViewHolder.tvRedPacketName.setText(memberRedPacket
                                        .getGroupName());
                                headerViewHolder.tvMoneySill.setText(getString(R.string
                                                .label_member_money_sill,
                                        CommonUtil.formatDouble2String(memberRedPacket
                                                .getTotalMoney())));
                            }
                            if (headerViewHolder.memberLayout.getVisibility() == View.GONE &&
                                    CommonUtil.isCollectionEmpty(
                                    redPackets) && CommonUtil.isCollectionEmpty(coupons)) {
                                emptyView.showEmptyView();
                                recyclerView.setVisibility(View.GONE);
                            }
                            adapter.setData(redPackets, coupons);
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .build();
            WalletApi.getCouponsAndRedPacketsObb(id)
                    .subscribe(refreshSub);
        }
    }

    @Override
    public void onReceiveRedPacket(final int position, final RedPacket redPacket) {
        if (redPacket == null || TextUtils.isEmpty(redPacket.getRedemptionCode())) {
            return;
        }
        if (!Util.loginBindChecked(this, Constants.Login.RECEIVE_COUPON_RED_PACKET)) {
            return;
        }
        CommonUtil.unSubscribeSubs(receiveRedPacketSub);
        receiveRedPacketSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        ToastUtil.showCustomToast(CertificateCenterListActivity.this,
                                R.string.msg_get_succeed);
                        redPacket.setGetStatus(4);
                        adapter.notifyItemChanged(position);
                    }
                })
                .setDataNullable(true)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .build();
        WalletApi.receiveRedPacketObb(redPacket.getRedemptionCode())
                .subscribe(receiveRedPacketSub);
    }

    @Override
    public void onReceiveCoupon(final int position, final CouponInfo couponInfo) {
        if (couponInfo == null || couponInfo.getId() == 0 || couponInfo.getGetStatus() == 4) {
            return;
        }
        if (!Util.loginBindChecked(this, Constants.Login.RECEIVE_COUPON_RED_PACKET)) {
            return;
        }
        CommonUtil.unSubscribeSubs(receiveCouponSub);
        receiveCouponSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        // 领券成功之后直接跳转到商家私信界面
                        if (couponInfo.getMerchant() != null && couponInfo.getMerchant()
                                .getUserId() > 0) {
                            MerchantCouponDialog.saveCouponTipsToWsMessage(
                                    CertificateCenterListActivity.this,
                                    couponInfo.getMerchant()
                                            .getUserId());
                            Intent intent = new Intent(CertificateCenterListActivity.this,
                                    WSCustomerChatActivity.class);
                            intent.putExtra(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER_ID,
                                    couponInfo.getMerchant()
                                            .getUserId());
                            startActivity(intent);
                        }

                        couponInfo.setGetStatus(4);
                        adapter.notifyItemChanged(position);
                    }
                })
                .setDataNullable(true)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .build();
        WalletApi.receiveCouponObb(String.valueOf(couponInfo.getId()))
                .subscribe(receiveCouponSub);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.Login.RECEIVE_COUPON_RED_PACKET:
                    onRefresh(null);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                                    User user = Session.getInstance()
                                            .getCurrentUser(CertificateCenterListActivity.this);
                                    if (user == null || user.getMember() == null) {
                                        headerViewHolder.memberLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        headerViewHolder.memberLayout.setVisibility(View.GONE);
                                    }
                                    break;
                            }
                        }
                    });
        }
    }

    public class HeaderViewHolder {
        @BindView(R.id.member_layout)
        View memberLayout;
        @BindView(R.id.img_edge)
        ImageView imgEdge;
        @BindView(R.id.tv_value)
        TextView tvValue;
        @BindView(R.id.tv_red_packet_name)
        TextView tvRedPacketName;
        @BindView(R.id.btn_receive)
        Button btnReceive;
        @BindView(R.id.btn_go_see)
        Button btnGoSee;
        @BindView(R.id.tv_money_sill)
        TextView tvMoneySill;

        HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
            btnReceive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CertificateCenterListActivity.this,
                            OpenMemberActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            });
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSub,
                receiveRedPacketSub,
                receiveCouponSub,
                rxBusEventSub);
    }
}
