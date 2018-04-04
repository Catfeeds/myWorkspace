package com.hunliji.marrybiz.view.coupon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;
import com.umeng.analytics.MobclickAgent;

import rx.Subscription;

/**
 * 优惠券教育页
 * Created by chen_bin on 2016/9/5 0005.
 */
public class CouponEduActivity extends HljWebViewActivity {
    private Subscription rxBusEventSub;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerRxBusEvent();
    }

    @Override
    protected void initBottomLayout(ViewGroup bottomLayout) {
        super.initBottomLayout(bottomLayout);
        View bottomView = View.inflate(this, R.layout.coupon_edu_bottom_layout, bottomLayout);
        bottomView.findViewById(R.id.btn_create_coupon)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), CreateCouponActivity.class);
                        intent.putExtra("is_from_edu", true);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
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
                                case CREATE_COUPON_SUCCESS:
                                    finish();
                                    break;
                            }
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(rxBusEventSub);
    }
}