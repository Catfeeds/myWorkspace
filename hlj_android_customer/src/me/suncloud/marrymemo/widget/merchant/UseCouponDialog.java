package me.suncloud.marrymemo.widget.merchant;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.Hashtable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.wallet.WalletApi;
import me.suncloud.marrymemo.model.wallet.CouponRecord;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 使用优惠券的Dialog
 * Created by chen_bin on 2016/10/19 0019.
 */
public class UseCouponDialog extends Dialog implements View.OnTouchListener {
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.tv_city_name)
    TextView tvCityName;
    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.img_used)
    ImageView imgUsed;
    @BindView(R.id.img_code)
    ImageView imgCode;
    @BindView(R.id.coupon_layout)
    LinearLayout couponLayout;
    private CouponRecord couponRecord;
    private boolean mIsRunning;
    private float mDownX;
    private float mMaxOffset;
    private float mCurrentRotation;
    private int mPivotX;
    private int mCodeWidth;
    private int mCodeHeight;
    private Subscription initSub;
    private HljHttpSubscriber useCouponSub;
    private OnFinishedListener onFinishedListener;

    private final static float MAX_ROTATION = 4.0f;

    public UseCouponDialog(@NonNull Context context, CouponRecord couponRecord) {
        super(context, R.style.BubbleDialogTheme);
        this.couponRecord = couponRecord;
        setContentView(R.layout.dialog_use_coupon);
        ButterKnife.bind(this);
        initValues();
        initViews();
        initLoad();
    }

    private void initValues() {
        this.mPivotX = ContextCompat.getDrawable(getContext(),
                R.drawable.image_bg_merchant_coupon_white)
                .getIntrinsicWidth();
        this.mCodeWidth = mPivotX;
        this.mCodeHeight = CommonUtil.dp2px(getContext(), 40);
        this.mMaxOffset = Math.round(mPivotX * 3 / 5);
    }

    private void initViews() {
        if (getWindow() != null) {
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
        }
        couponLayout.setPivotX(mPivotX);
        couponLayout.setOnTouchListener(this);
        tvMerchantName.setText(couponRecord.getCoupon()
                .getMerchant()
                .getName());
        tvCityName.setText(couponRecord.getCoupon()
                .getMerchant()
                .getCityName());
        tvCode.setText(couponRecord.getCode());
    }

    private void initLoad() {
        if (initSub == null || initSub.isUnsubscribed()) {
            initSub = Observable.create(new Observable.OnSubscribe<Bitmap>() {
                @Override
                public void call(Subscriber<? super Bitmap> subscriber) {
                    if (subscriber.isUnsubscribed()) {
                        return;
                    }
                    Bitmap bitmap = null;
                    try {
                        Hashtable<EncodeHintType, Object> qrParam = new Hashtable<>();
                        qrParam.put(EncodeHintType.CHARACTER_SET, "utf-8");
                        qrParam.put(EncodeHintType.MARGIN, 0);
                        BitMatrix matrix = new MultiFormatWriter().encode(couponRecord.getCode(),
                                BarcodeFormat.CODE_128,
                                mCodeWidth,
                                mCodeHeight,
                                qrParam);
                        int width = matrix.getWidth();
                        int height = matrix.getHeight();
                        int[] pixels = new int[width * height];
                        for (int y = 0; y < height; y++) {
                            for (int x = 0; x < width; x++) {
                                if (matrix.get(x, y)) {
                                    pixels[y * width + x] = 0xff000000;
                                }
                            }
                        }
                        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(bitmap);
                    subscriber.onCompleted();
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Bitmap>() {
                        @Override
                        public void call(Bitmap bitmap) {
                            if (bitmap != null) {
                                imgCode.setImageBitmap(bitmap);
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mIsRunning) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                mDownX = 0;
                mCurrentRotation = couponLayout.getRotation();
                if (mCurrentRotation != 0 && Math.abs(mCurrentRotation) < MAX_ROTATION) {
                    mCurrentRotation = 0;
                    couponLayout.setRotation(0);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX() - mDownX;
                float rotation = mCurrentRotation - x / mMaxOffset * MAX_ROTATION;
                if (x > 0) {
                    couponLayout.setRotation(Math.abs(couponLayout.getRotation()) > MAX_ROTATION
                            ? -MAX_ROTATION : rotation);
                    if (Math.abs(couponLayout.getRotation()) >= MAX_ROTATION) {
                        mIsRunning = true;
                        AnimatorSet animatorSet = new AnimatorSet();
                        Animator alphaAnim = ObjectAnimator.ofFloat(couponLayout, View.ALPHA, 1, 0);
                        Animator transAnim = ObjectAnimator.ofFloat(couponLayout,
                                View.TRANSLATION_Y,
                                0,
                                300);
                        animatorSet.playTogether(alphaAnim, transAnim);
                        animatorSet.setDuration(400);
                        animatorSet.start();
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                couponLayout.setVisibility(View.INVISIBLE);
                                CommonUtil.unSubscribeSubs(useCouponSub);
                                useCouponSub = HljHttpSubscriber.buildSubscriber(getContext())
                                        .setOnNextListener(new SubscriberOnNextListener() {
                                            @Override
                                            public void onNext(Object o) {
                                                if (onFinishedListener != null) {
                                                    onFinishedListener.onFinished();
                                                }
                                                imgUsed.setVisibility(View.VISIBLE);
                                                AnimatorSet animatorSet = new AnimatorSet();
                                                Animator alphaAnim = ObjectAnimator.ofFloat(imgUsed,
                                                        View.ALPHA,
                                                        0,
                                                        1);
                                                Animator scaleXAnim = ObjectAnimator.ofFloat
                                                        (imgUsed,
                                                        View.SCALE_X,
                                                        1.4f,
                                                        1.0f);
                                                Animator scaleYAnim = ObjectAnimator.ofFloat
                                                        (imgUsed,
                                                        View.SCALE_Y,
                                                        1.4f,
                                                        1.0f);
                                                animatorSet.playTogether(alphaAnim,
                                                        scaleXAnim,
                                                        scaleYAnim);
                                                animatorSet.setDuration(400);
                                                animatorSet.start();
                                            }
                                        })
                                        .build();
                                WalletApi.useCouponObb(couponRecord.getId(), 1)
                                        .subscribe(useCouponSub);
                            }
                        });
                    }
                } else if (x < 0) {
                    if (couponLayout.getRotation() <= 0) {
                        couponLayout.setRotation(rotation > 0 ? 0 : rotation);
                    }
                }
                break;
        }
        return true;
    }

    @OnClick(R.id.btn_close)
    void onClose() {
        dismiss();
    }

    public void setOnFinishedListener(OnFinishedListener onFinishedListener) {
        this.onFinishedListener = onFinishedListener;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        CommonUtil.unSubscribeSubs(initSub, useCouponSub);
    }
}