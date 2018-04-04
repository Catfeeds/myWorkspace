package me.suncloud.marrymemo.view.wedding_dress;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.weddingdress.WeddingDressApi;
import me.suncloud.marrymemo.model.weddingdress.WeddingInfoBody;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;
import me.suncloud.marrymemo.view.community.SelectMerchantListActivity;
import rx.Observable;

/**
 * Created by hua_rong on 2017/5/3.
 * 婚纱照发布成功
 */

public class WeddingReleaseSuccessActivity extends HljBaseActivity {


    @BindView(R.id.iv_background)
    ImageView ivBackground;
    @BindView(R.id.rl_top_background)
    RelativeLayout rlTopBackground;
    @BindView(R.id.rl_merchant)
    RelativeLayout rlMerchant;
    @BindView(R.id.et_pay_money)
    EditText etPayMoney;
    @BindView(R.id.et_address)
    EditText etAddress;
    @BindView(R.id.btn_finish)
    Button btnFinish;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tv_image_title)
    TextView tvImageTitle;
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;

    private HljHttpSubscriber subscriber;
    private Unbinder unBinder;
    private long id;//发布成功后，服务器返回的id
    private Merchant merchant;
    private String unRecordedMerchantName;
    private int gold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_release_success);
        unBinder = ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setCannotBack(true);
        Point point = CommonUtil.getDeviceSize(this);
        int height = point.x * 9 / 16;
        rlTopBackground.getLayoutParams().height = height;
        Intent intent = getIntent();
        id = intent.getLongExtra("id", 0);
        String title = intent.getStringExtra("title");
        String imagePath = intent.getStringExtra("url");
        gold = getIntent().getIntExtra("gold", 0);
        boolean showMerchant = intent.getBooleanExtra("showMerchant", true);
        rlMerchant.setVisibility(showMerchant ? View.VISIBLE : View.GONE);
        setBackButton(R.drawable.icon_cross_close_primary_32_32);
        tvImageTitle.setText(title);
        if (!TextUtils.isEmpty(imagePath)) {
            Glide.with(this)
                    .load(ImagePath.buildPath(imagePath)
                            .width(point.x)
                            .height(height)
                            .path())
                    .into(ivBackground);
        }
    }

    @OnClick(R.id.rl_merchant)
    public void onMerchant() {
        Intent intent = new Intent();
        intent.setClass(this, SelectMerchantListActivity.class);
        startActivityForResult(intent, Constants.RequestCode.SELECT_MERCHANT_LIST);
    }

    @OnClick(R.id.btn_finish)
    public void onClick() {
        String payMoney = etPayMoney.getText()
                .toString()
                .trim();
        String city = etAddress.getText()
                .toString()
                .trim();
        String merchantName = tvMerchantName.getText()
                .toString()
                .trim();
        if (TextUtils.isEmpty(city) && TextUtils.isEmpty(payMoney) && TextUtils.isEmpty
                (merchantName)) {
            goWeddingDressDetail();
        } else {
            if (subscriber == null || subscriber.isUnsubscribed()) {
                WeddingInfoBody weddingInfoBody = new WeddingInfoBody();
                if (!TextUtils.isEmpty(city)) {
                    weddingInfoBody.setCity(city);
                }
                weddingInfoBody.setId(id);
                if (merchant != null && merchant.getId() != 0) {
                    weddingInfoBody.setMerchantId(merchant.getId());
                    weddingInfoBody.setUnrecordedMerchantName(null);
                } else if (!TextUtils.isEmpty(merchantName)) {
                    weddingInfoBody.setUnrecordedMerchantName(merchantName);
                    weddingInfoBody.setMerchantId(null);
                } else {
                    weddingInfoBody.setMerchantId(null);
                    weddingInfoBody.setUnrecordedMerchantName(null);
                }
                weddingInfoBody.setPrice(payMoney);
                Observable observable = WeddingDressApi.postWeddingInfo(weddingInfoBody);
                subscriber = HljHttpSubscriber.buildSubscriber(this)
                        .setOnNextListener(new SubscriberOnNextListener() {
                            @Override
                            public void onNext(Object o) {
                                goWeddingDressDetail();
                            }
                        })
                        .setProgressBar(progressBar)
                        .build();
                observable.subscribe(subscriber);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.SELECT_MERCHANT_LIST:
                    if (data != null) {
                        merchant = data.getParcelableExtra("merchant");
                        if (merchant != null) {
                            unRecordedMerchantName = null;
                            tvMerchantName.setText(merchant.getName());
                        } else {
                            merchant = null;
                            unRecordedMerchantName = data.getStringExtra("unRecordedMerchant");
                            tvMerchantName.setText(unRecordedMerchantName);
                        }
                    }
                    break;
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goWeddingDressDetail();
        }
        return false;
    }

    private void goWeddingDressDetail() {
        RxBus.getDefault()
                .post(new RxEvent(RxEvent.RxEventType.THREAD_WEDDING_PHOTO_RELEASED_SUCCESS, null));
        Intent intent = new Intent(this, CommunityThreadDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("is_finish_share", true);
        intent.putExtra("gold", gold);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        finish();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (unBinder != null) {
            unBinder.unbind();
        }
        CommonUtil.unSubscribeSubs(subscriber);
    }
}
