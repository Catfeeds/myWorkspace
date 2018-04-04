package me.suncloud.marrymemo.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.comment.CommentServiceActivity;
import rx.Subscription;

/**
 * 评价店铺
 * Created by jinxin on 2016/5/12.
 */
public class CommentMerchantActivity extends HljBaseActivity {
    @BindView(R.id.merchant_icon)
    RoundedImageView merchantIcon;
    @BindView(R.id.merchant_name)
    TextView merchantName;
    @BindView(R.id.bought_work)
    ImageView boughtWork;
    @BindView(R.id.bought_merchant)
    ImageView boughtMerchant;
    private Merchant merchant;

    private static final int WORKLOGIN = 100;
    private static final int MERCHANTLOGIN = 101;

    private Subscription rxBusEventSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        merchant = getIntent().getParcelableExtra("merchant");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_merchant);
        ButterKnife.bind(this);
        if (merchant != null) {
            if (merchant.getShopType() == Merchant.SHOP_TYPE_HOTEL) {
                boughtWork.setImageResource(R.drawable.image_hint_bought_hotel_work);
                boughtMerchant.setImageResource(R.drawable.image_hint_bought_hotel_merchant);
            }
            merchantName.setText(merchant.getName());
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int logoWidth = Math.round(dm.density * 80);
            String logoPath = JSONUtil.getImagePath2(merchant.getLogoPath(), logoWidth);
            if (!JSONUtil.isEmpty(logoPath)) {
                ImageLoadTask loadTask = new ImageLoadTask(merchantIcon, null, 0);
                merchantIcon.setTag(logoPath);
                loadTask.loadImage(logoPath,
                        logoWidth,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(),
                                R.mipmap.icon_empty_image,
                                loadTask));
            }
        }
        registerRxBusEvent();
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
                                case COMMENT_SERVICE_SUCCESS:
                                    finish();
                                    break;
                            }
                        }
                    });
        }
    }

    public void onWork(View view) {
        if (merchant == null) {
            return;
        }
        if (!Util.loginBindChecked(this, WORKLOGIN)) {
            return;
        }
        Intent intent = new Intent(this, CommentServiceActivity.class);
        intent.putExtra(CommentServiceActivity.ARG_KNOW_TYPE, ServiceComment.KNOW_TYPE_BOUGHT);
        intent.putExtra(CommentServiceActivity.ARG_MERCHANT_ID, merchant.getId());
        startActivity(intent);
    }

    public void onMerchant(View view) {
        if (merchant == null) {
            return;
        }
        if (!Util.loginBindChecked(this, MERCHANTLOGIN)) {
            return;
        }
        Intent intent = new Intent(this, CommentServiceActivity.class);
        intent.putExtra(CommentServiceActivity.ARG_KNOW_TYPE,
                ServiceComment.KNOW_TYPE_MERCHANT);
        intent.putExtra(CommentServiceActivity.ARG_MERCHANT_ID, merchant.getId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == WORKLOGIN) {
                onWork(null);
            }
            if (requestCode == MERCHANTLOGIN) {
                onMerchant(null);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(rxBusEventSub);
    }
}
