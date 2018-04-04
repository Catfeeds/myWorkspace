package me.suncloud.marrymemo.widget.merchant;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.hunliji.hljchatlibrary.WSRealmHelper;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSExtObject;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSTips;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnCompletedListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.merchant.MerchantCouponRecyclerAdapter;
import me.suncloud.marrymemo.api.wallet.WalletApi;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;

/**
 * 商家主页优惠券列表
 * Created by chen_bin on 2017/5/22 0022.
 */
public class MerchantCouponDialog extends Dialog implements MerchantCouponRecyclerAdapter
        .OnReceiveCouponListener {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.btn_receive_all)
    Button btnReceiveAll;
    private MerchantCouponRecyclerAdapter adapter;
    private long merchantId;
    private long merchantUserId;
    private HljHttpSubscriber initSub;
    private HljHttpSubscriber receiveSub;

    public MerchantCouponDialog(Context context, long merchantId, long merchantUserId) {
        super(context, R.style.BubbleDialogTheme);
        setContentView(R.layout.dialog_merchant_horizontal_coupons);
        ButterKnife.bind(this);
        this.merchantId = merchantId;
        this.merchantUserId = merchantUserId;
        initValues();
        initViews();
    }

    private void initValues() {

    }

    private void initViews() {
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MerchantCouponRecyclerAdapter(getContext());
        adapter.setOnReceiveCouponListener(this);
        recyclerView.setAdapter(adapter);
        Window win = getWindow();
        if (win != null) {
            WindowManager.LayoutParams params = win.getAttributes();
            params.width = CommonUtil.getDeviceSize(getContext()).x;
            win.setAttributes(params);
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
    }

    public void getCoupons() {
        if (initSub == null || initSub.isUnsubscribed()) {
            initSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<List<CouponInfo>>() {
                        @Override
                        public void onNext(List<CouponInfo> coupons) {
                            adapter.setCoupons(coupons);
                            show();
                            checkAllReceivedStatus(coupons);
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                    .build();
            WalletApi.getMerchantCouponListObb(merchantId)
                    .subscribe(initSub);
        }
    }

    public void setCoupons(ArrayList<CouponInfo> coupons) {
        adapter.setCoupons(coupons);
        adapter.notifyDataSetChanged();
        show();
        checkAllReceivedStatus(coupons);
    }


    @OnClick(R.id.btn_receive_all)
    void onReceiveAll() {
        onReceiveCoupon(-1, null);
    }

    @Override
    public void onReceiveCoupon(final int position, final CouponInfo couponInfo) {
        final List<CouponInfo> coupons = adapter.getCoupons();
        CommonUtil.unSubscribeSubs(receiveSub);
        receiveSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        if (couponInfo != null) {
                            couponInfo.setUsed(true);
                            adapter.notifyDataSetChanged();
                            checkAllReceivedStatus(coupons);
                        } else {
                            for (CouponInfo coupon : coupons) {
                                if (!coupon.isUsed()) {
                                    coupon.setUsed(true);
                                }
                            }
                            btnReceiveAll.setEnabled(false);
                            btnReceiveAll.setText(R.string.label_all_received);
                            adapter.notifyDataSetChanged();
                        }

                        // 领券成功之后直接跳转到商家私信界面
                        if (merchantUserId > 0) {
                            saveCouponTipsToWsMessage(getContext(), merchantUserId);
                            Intent intent = new Intent(getContext(), WSCustomerChatActivity.class);
                            intent.putExtra(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER_ID,
                                    merchantUserId);
                            getContext().startActivity(intent);
                        }else {
                            ToastUtil.showToast(getContext(), "领取成功", 0);
                        }
                    }
                })
                .setOnCompletedListener(new SubscriberOnCompletedListener() {
                    @Override
                    public void onCompleted() {
                        cancel();
                    }
                })
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .build();
        StringBuilder sb = new StringBuilder();
        if (couponInfo != null) {
            sb.append(couponInfo.getId());
        } else {
            for (CouponInfo info : coupons) {
                if (!info.isUsed()) {
                    sb.append(info.getId())
                            .append(",");
                }
            }
            if (!TextUtils.isEmpty(sb) && sb.lastIndexOf(",") > 0) {
                sb.deleteCharAt(sb.length() - 1); //移除最后的逗号
            }
        }
        WalletApi.receiveCouponObb(sb.toString())
                .subscribe(receiveSub);
    }

    public static void saveCouponTipsToWsMessage(Context context, long merchantUserId) {
        WSRealmHelper.saveWSChatToLocal(context,
                WSChat.TIPS,
                merchantUserId,
                new WSExtObject(new WSTips(WSTips.ACTION_COUPON_SUCCESS_TIP,
                        "领券成功！",
                        "聊一聊，了解更多商家优惠活动~")));
    }

    //判断当前优惠券列表是否全部领取了
    private void checkAllReceivedStatus(List<CouponInfo> coupons) {
        if (CommonUtil.isCollectionEmpty(coupons)) {
            return;
        }
        boolean isAllReceived = true;
        for (CouponInfo coupon : coupons) {
            isAllReceived = coupon.isUsed();
            if (!isAllReceived) {
                break;
            }
        }
        if (isAllReceived) {
            btnReceiveAll.setEnabled(false);
            btnReceiveAll.setText(R.string.label_all_received);
        } else {
            btnReceiveAll.setEnabled(true);
            btnReceiveAll.setText(R.string.label_receive_all);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        CommonUtil.unSubscribeSubs(initSub, receiveSub);
    }
}