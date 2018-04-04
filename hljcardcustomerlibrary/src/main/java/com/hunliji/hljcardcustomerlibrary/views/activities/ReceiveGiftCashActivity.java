package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.views.fragments.ReceiveGiftCashFragment;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.BannerJumpService;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTime;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by mo_yu on 2017/11/23.收到的礼物礼金
 */
public class ReceiveGiftCashActivity extends HljBaseActivity {

    public static final int CASH_TYPE = 0;
    public static final int GIFT_TYPE = 1;
    @BindView(R2.id.tv_total)
    TextView tvTotal;
    @BindView(R2.id.tv_price)
    TextView tvPrice;
    @BindView(R2.id.cash_view)
    LinearLayout cashView;
    @BindView(R2.id.tv_total_cash)
    TextView tvTotalCash;
    @BindView(R2.id.cb_cash)
    CheckableLinearButton cbCash;
    @BindView(R2.id.tv_total_gift)
    TextView tvTotalGift;
    @BindView(R2.id.cb_gift)
    CheckableLinearButton cbGift;
    @BindView(R2.id.view_pager)
    ViewPager viewPager;
    @BindView(R2.id.iv_card_view)
    RoundedImageView ivCardView;
    @BindView(R2.id.poster_layout)
    LinearLayout posterLayout;
    @BindView(R2.id.cash_triangle)
    ImageView cashTriangle;
    @BindView(R2.id.gift_triangle)
    ImageView giftTriangle;

    private ReceiveGiftCashFragment cashFragment;
    private ReceiveGiftCashFragment giftFragment;
    private boolean isFundUser;
    private Poster poster;
    private Subscription posterSubscription;
    private DateTime cashCreatedAt;
    private DateTime giftCreatedAt;
    private ValueAnimator tvMoneyAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_gift_cash);
        ButterKnife.bind(this);
        initView();
        loadPoster();
    }

    private void initView() {
        hideDividerView();
        setTitle("");
        setOkText("查看余额");
        SamplePagerAdapter pagerAdapter = new SamplePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == CASH_TYPE) {
                    cbCash.setChecked(true);
                    cbGift.setChecked(false);
                    cashTriangle.setVisibility(View.VISIBLE);
                    giftTriangle.setVisibility(View.INVISIBLE);
                } else {
                    cbCash.setChecked(false);
                    cbGift.setChecked(true);
                    cashTriangle.setVisibility(View.INVISIBLE);
                    giftTriangle.setVisibility(View.VISIBLE);
                }
            }
        });
        cbCash.setOnCheckedChangeListener(new CheckableLinearLayout.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(View view, boolean checked) {
                if (checked) {
                    cbCash.setChecked(true);
                    cbGift.setChecked(false);
                    viewPager.setCurrentItem(CASH_TYPE);
                    cashTriangle.setVisibility(View.VISIBLE);
                    giftTriangle.setVisibility(View.INVISIBLE);
                }
            }
        });
        cbGift.setOnCheckedChangeListener(new CheckableLinearLayout.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(View view, boolean checked) {
                if (checked) {
                    cbCash.setChecked(false);
                    cbGift.setChecked(true);
                    viewPager.setCurrentItem(GIFT_TYPE);
                    cashTriangle.setVisibility(View.INVISIBLE);
                    giftTriangle.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(cashFragment!=null) {
            cashFragment.refresh();
        }
        if(giftFragment!=null) {
            giftFragment.refresh();
        }
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        Intent intent = new Intent();
        intent.setClass(this, BalanceActivity.class);
        startActivity(intent);
    }

    private void loadPoster() {
        CommonUtil.unSubscribeSubs(posterSubscription);
        posterSubscription = getPosterObb().filter(new Func1<Poster, Boolean>() {
            @Override
            public Boolean call(Poster poster) {
                return poster != null && poster.getId() > 0;
            }
        })
                .subscribe(new Subscriber<Poster>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        posterLayout.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Poster poster) {
                        ReceiveGiftCashActivity.this.poster = poster;
                        posterLayout.setVisibility(View.VISIBLE);
                        setPostView(poster);
                    }
                });
    }

    protected Observable<Poster> getPosterObb() {
        if (HljCard.isCustomer(this)) {
            City city = LocationSession.getInstance()
                    .getCity(this);
            return CommonApi.getBanner(this,
                    HljCommon.BLOCK_ID.CustomerReceiveGiftCashActivity,
                    city.getCid())
                    .map(new Func1<PosterData, Poster>() {
                        @Override
                        public Poster call(PosterData posterData) {
                            List<Poster> posters = PosterUtil.getPosterList(posterData.getFloors(),
                                    HljCommon.POST_SITES.SITE_CARD_CASH__BOTTOM_BANNER,
                                    false);
                            return CommonUtil.isCollectionEmpty(posters) ? null : posters.get(0);
                        }
                    });
        } else if (HljCard.isCardMaster(this)) {
            return CommonApi.getBanner(this,
                    HljCommon.BLOCK_ID.CardMasterReceiveGiftCashActivity,
                    0)
                    .map(new Func1<PosterData, Poster>() {
                        @Override
                        public Poster call(PosterData posterData) {
                            List<Poster> posters = PosterUtil.getPosterList(posterData.getFloors(),
                                    HljCommon.POST_SITES.SITE_CARD_MASTER_CASH__BOTTOM_BANNER,
                                    false);
                            return CommonUtil.isCollectionEmpty(posters) ? null : posters.get(0);
                        }
                    });
        } else {
            return Observable.empty();
        }
    }

    private void setPostView(Poster poster) {
        if (poster != null) {
            String path = poster.getPath();
            Point point = CommonUtil.getDeviceSize(this);
            int width = point.x - CommonUtil.dp2px(this, 20);
            int height = Math.round(width * 7 / 36);
            ivCardView.getLayoutParams().width = width;
            ivCardView.getLayoutParams().height = height;
            if (!TextUtils.isEmpty(path)) {
                Glide.with(this)
                        .load(ImagePath.buildPath(path)
                                .width(width)
                                .height(height)
                                .cropPath())
                        .apply(new RequestOptions().override(width, height)
                                .placeholder(R.mipmap.icon_empty_image)
                                .error(R.mipmap.icon_empty_image))
                        .into(ivCardView);
            }
        }
    }

    @OnClick(R2.id.iv_card_view)
    public void onNoticeClicked() {
        if (poster == null) {
            return;
        }
        // 使用Name索引寻找ARouter中已注册的对应服务
        BannerJumpService bannerJumpService = (BannerJumpService) ARouter.getInstance()
                .build(RouterPath.ServicePath.BANNER_JUMP)
                .navigation(this);
        if (bannerJumpService != null) {
            bannerJumpService.bannerJump(this, poster, null);
        }
    }

    public class SamplePagerAdapter extends FragmentPagerAdapter {

        public SamplePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case CASH_TYPE:
                    if (cashFragment == null) {
                        cashFragment = ReceiveGiftCashFragment.newInstance
                                (ReceiveGiftCashFragment.CASH_TYPE);
                    }
                    return cashFragment;
                case GIFT_TYPE:
                    if (giftFragment == null) {
                        giftFragment = ReceiveGiftCashFragment.newInstance
                                (ReceiveGiftCashFragment.GIFT_TYPE);
                    }
                    return giftFragment;
            }
            return null;
        }
    }

    public void refreshHeadView(double totalAmount, double cashAmount, double giftAmount) {
        setTotalMoney((float) totalAmount);
        tvTotalCash.setText(getString(R.string.label_price___cm,
                CommonUtil.formatDouble2StringWithTwoFloat(cashAmount)));
        tvTotalGift.setText(getString(R.string.label_price___cm,
                CommonUtil.formatDouble2StringWithTwoFloat(giftAmount)));
        isFundUser = true;
    }

    private void setTotalMoney(float total) {
        tvMoneyAnimator = ValueAnimator.ofFloat(0.00f, total);
        tvMoneyAnimator.setDuration(3000);
        tvMoneyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                String str = CommonUtil.formatDouble2StringWithTwoFloat(f);
                tvPrice.setText(str);
            }
        });
        tvMoneyAnimator.start();
    }

    public void setLatestTimeOfType(int type, DateTime createdAt) {
        if (type == 2) {
            cashCreatedAt = createdAt;
        } else {
            giftCreatedAt = createdAt;
        }
        if (cashCreatedAt != null && giftCreatedAt != null) {
            if (cashCreatedAt.isAfter(giftCreatedAt)) {
                viewPager.setCurrentItem(0);
            } else {
                viewPager.setCurrentItem(1);
            }
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(posterSubscription);
        if (tvMoneyAnimator != null) {
            tvMoneyAnimator.cancel();
            tvMoneyAnimator = null;
        }
    }
}
