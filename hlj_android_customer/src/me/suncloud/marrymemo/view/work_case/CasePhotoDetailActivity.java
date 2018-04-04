package me.suncloud.marrymemo.view.work_case;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hunliji.hljcommonlibrary.models.CasePhoto;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.WorkMediaItem;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;

import net.robinx.lib.blur.StackBlur;
import net.robinx.lib.blur.utils.BlurUtils;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.work_case.WorkApi;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import me.suncloud.marrymemo.widget.guestanim.BaseEffects;
import me.suncloud.marrymemo.widget.guestanim.EffectStyle;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2018/1/31
 * 客照详情 相册回忆
 */

public class CasePhotoDetailActivity extends HljBaseNoBarActivity {

    @Override
    public String pageTrackTagName() {
        return "客照回忆相册";
    }

    @Override
    public VTMetaData pageTrackData() {
        long casePhotoId = getIntent().getLongExtra(ARG_ID, 0);
        return new VTMetaData(casePhotoId, VTMetaData.DATA_TYPE.DATA_TYPE_CASE_PHOTO);
    }

    @BindView(R.id.blur_bg)
    ImageView blurBg;
    @BindView(R.id.iv_cover)
    ImageView ivCover;
    @BindView(R.id.iv_music)
    ImageView ivMusic;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.ib_collect)
    ImageButton ibCollect;
    @BindView(R.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R.id.rl_message_merchant)
    RelativeLayout rlMessageMerchant;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.rl_convert_view)
    RelativeLayout rlConvertView;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.rl_tools)
    RelativeLayout rlTools;
    @BindView(R.id.cover_shadow)
    View coverShadow;

    private MediaPlayer mediaPlayer;
    private int screenWidth;
    private int screenHeight;
    private static final long DURING = 2000;
    private EffectStyle[] typeTran = {EffectStyle.TRANS_LEFT, EffectStyle.TRANS_Right,
            EffectStyle.SCALE_SHRINK};
    private EffectStyle[] typeScale = {EffectStyle.SCALE_ZOOM, EffectStyle.SCALE_SHRINK};
    private AnimatorSet animatorSet;
    public static final int STATUS_START = 0;
    public static final int STATUS_PLAY = 1;
    public static final int STATUS_PAUSE = 2;
    public static final int STATUS_ANIMATION_END = 3;
    private boolean lowVersionPause;
    private HljHttpSubscriber httpSubscriber;
    private HljHttpSubscriber collectSubscriber;
    private int status;
    private ObjectAnimator musicAnimator;
    private long casePhotoId;
    private CasePhoto casePhoto;
    private static final int[] collectPic = {R.mipmap.icon_collect_white_45_45, R.mipmap
            .icon_collect_yellow_45_45};
    public static final String ARG_ID = "id";
    private NoticeUtil noticeUtil;
    private int imageWidth;
    private int imageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_case_photo_detail);
        ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout);
        setSwipeBackEnable(false);
        initValue();
        initView();
        initError();
        initLoad();
        initTracker();
    }

    private void initTracker() {
        HljVTTagger.buildTagger(rlMessageMerchant)
                .tagName(HljTaggerName.PRIVATE_LETTER_MERCHANT_BTN)
                .hitTag();
    }

    private void initValue() {
        casePhotoId = getIntent().getLongExtra(ARG_ID, 0);
        Point point = CommonUtil.getDeviceSize(this);
        screenWidth = point.x;
        screenHeight = point.y;
        imageWidth = screenWidth;
        imageHeight = screenHeight;
    }

    private void initView() {
        noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        noticeUtil.onResume();
        setMediaPlayer();
        musicAnimator = ObjectAnimator.ofFloat(ivMusic, "rotation", 0f, 360f);
        musicAnimator.setInterpolator(new LinearInterpolator());
        musicAnimator.setDuration(3000);
        musicAnimator.setRepeatCount(Animation.INFINITE);
    }

    private void setMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void initError() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                initLoad();
            }
        });
    }

    private void initLoad() {
        progressBar.setVisibility(View.VISIBLE);
        httpSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<CasePhoto>() {
                    @Override
                    public void onNext(CasePhoto casePhotoDetail) {
                        casePhoto = casePhotoDetail;
                        if (TextUtils.isEmpty(casePhoto.getMarkContent())) {
                            tvContent.setText("客照");
                        } else {
                            tvContent.setText(casePhoto.getMarkContent());
                        }
                        ibCollect.setImageResource(casePhoto.isCollected() ? collectPic[1] :
                                collectPic[0]);
                        tvTime.setText(casePhoto.getShootingTime());
                        casePhoto.setDetailPhotos(casePhotoDetail.getDetailPhotos());
                        preLoadImage(0);
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .setEmptyView(emptyView)
                .setContentView(rlConvertView)
                .build();
        WorkApi.getCasePhotoDetail(casePhotoId)
                .subscribe(httpSubscriber);
    }

    private void preLoadImage(final int index) {
        if (casePhoto == null) {
            return;
        }
        List<WorkMediaItem> photos = casePhoto.getDetailPhotos();
        if (CommonUtil.isCollectionEmpty(photos)) {
            progressBar.setVisibility(View.GONE);
            actionHolderLayout.setVisibility(View.VISIBLE);
            emptyView.showEmptyView();
            rlMessageMerchant.setVisibility(View.VISIBLE);
            rlTools.setVisibility(View.VISIBLE);
            return;
        }
        int photoSize = CommonUtil.getCollectionSize(photos);
        if (index >= photoSize) {
            progressBar.setVisibility(View.GONE);
            return;
        }
        WorkMediaItem photo = photos.get(index);
        if (photo == null || TextUtils.isEmpty(photo.getMediaPath())) {
            return;
        }
        if ((photoSize < 9 && index >= photoSize / 3) || (photoSize >= 9 && index >= 2)) {
            progressBar.setVisibility(View.GONE);
        }
        if ((photoSize < 9 && index == photoSize / 3) || (photoSize >= 9 && index == 2)) {
            rlTools.setVisibility(View.VISIBLE);
            musicPlay();
        }
        Glide.with(CasePhotoDetailActivity.this)
                .asBitmap()
                .load(ImagePath.buildPath(photo.getMediaPath())
                        .width(imageWidth)
                        .height(imageHeight)
                        .quality(75)
                        .path())
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(
                            @Nullable GlideException e,
                            Object model,
                            Target<Bitmap> target,
                            boolean isFirstResource) {
                        preLoadImage(index + 1);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(
                            Bitmap resource,
                            Object model,
                            Target<Bitmap> target,
                            DataSource dataSource,
                            boolean isFirstResource) {
                        preLoadImage(index + 1);
                        return false;
                    }
                })
                .submit();
    }

    private Observable<BitmapHolder> getBitmapObb(final String imagePath) {
        return Observable.create(new Observable.OnSubscribe<BitmapHolder>() {
            @Override
            public void call(Subscriber<? super BitmapHolder> subscriber) {
                try {
                    Bitmap bitmap = Glide.with(CasePhotoDetailActivity.this)
                            .asBitmap()
                            .load(imagePath)
                            .submit()
                            .get();
                    Bitmap blurBitmap = StackBlur.blurNativelyPixels(BlurUtils.compressBitmap
                            (bitmap,
                            6), 30, false);
                    subscriber.onNext(new BitmapHolder(bitmap, blurBitmap));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                }
            }
        });
    }


    @OnClick(R.id.ib_message)
    void onMsgLayout() {
        if (Util.loginBindChecked(this)) {
            Intent intent = new Intent(this, MessageHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @OnClick(R.id.ib_collect)
    void onCollect() {
        if (casePhoto != null && Util.loginBindChecked(this)) {
            if (collectSubscriber == null || collectSubscriber.isUnsubscribed()) {
                collectState(!casePhoto.isCollected());//先改变收藏状态 取反
                collectSubscriber = HljHttpSubscriber.buildSubscriber(this)
                        .setOnNextListener(new SubscriberOnNextListener() {
                            @Override
                            public void onNext(Object o) {
                                ToastUtil.showCustomToast(CasePhotoDetailActivity.this,
                                        casePhoto.isCollected() ? R.string.hint_collect_complete
                                                : R.string.hint_discollect_complete);
                            }
                        })
                        .setOnErrorListener(new SubscriberOnErrorListener<Object>() {
                            @Override
                            public void onError(Object o) {
                                collectState(!casePhoto.isCollected());//还原状态 取反
                                ToastUtil.showCustomToast(CasePhotoDetailActivity.this,
                                        casePhoto.isCollected() ? R.string
                                                .msg_product_del_collect_fail : R.string
                                                .msg_product_collect_fail);
                            }
                        })
                        .setProgressBar(progressBar)
                        .build();
                WorkApi.postCaseCollect(casePhoto.getId())
                        .subscribe(collectSubscriber);
            }
        }
    }

    /**
     * 改变收藏状态
     *
     * @param collect 是否收藏
     */
    private void collectState(boolean collect) {
        casePhoto.setCollected(collect);
        ibCollect.setImageResource(collect ? collectPic[1] : collectPic[0]);
    }

    @OnClick(R.id.ib_share)
    void onShare() {
        if (casePhoto != null) {
            ShareInfo shareInfo = casePhoto.getShare();
            if (shareInfo != null) {
                ShareDialogUtil.onCommonShare(this, shareInfo);
            }
        }
    }

    @OnClick(R.id.rl_message_merchant)
    void onMessageMerchant() {
        if (casePhoto != null) {
            if (!AuthUtil.loginBindCheck(this)) {
                return;
            }
            Merchant merchant = casePhoto.getMerchant();
            if (merchant != null && merchant.getId() > 0) {
                Intent intent = new Intent(this, WSCustomerChatActivity.class);
                intent.putExtra(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER_ID,
                        merchant.getUserId());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        }
    }


    @OnClick(R.id.btn_back)
    void onBack() {
        stopPlaying();
        super.onBackPressed();
    }


    @OnClick(R.id.iv_music)
    void onMusicClick() {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            ivMusic.setImageResource(R.mipmap.icon_music_pause_gray_76_76);
            musicAnimator.cancel();
        } else {
            mediaPlayer.start();
            ivMusic.setImageResource(R.mipmap.icon_music_play_gray_76_76);
            musicAnimator.start();
        }
    }

    @OnClick(R.id.blur_bg)
    void onCoverTouch() {
        if (status == STATUS_PLAY) {
            setStatusPause();
        }
    }


    private void setStatusPause() {
        if (mediaPlayer == null) {
            return;
        }
        if (animatorSet == null) {
            return;
        }
        status = STATUS_PAUSE;
        showLayout(actionHolderLayout, true, true);
        showLayout(rlMessageMerchant, true, false);
        ivMusic.setVisibility(View.GONE);
        ivPlay.setVisibility(View.VISIBLE);
        ivPlay.setImageResource(R.mipmap.icon_stop_round_yellow_135_135);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        if (animatorSet.isRunning()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                animatorSet.pause();
            } else {
                lowVersionPause = true;
                animatorSet.cancel();
            }
        }
        if (musicAnimator.isRunning()) {
            ivMusic.setImageResource(R.mipmap.icon_music_pause_gray_76_76);
            musicAnimator.cancel();
        }
    }


    private void setStatusResume() {
        if (mediaPlayer == null) {
            return;
        }
        status = STATUS_PLAY;
        ivPlay.setVisibility(View.GONE);
        showLayout(actionHolderLayout, false, true);
        showLayout(rlMessageMerchant, false, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (animatorSet != null && animatorSet.isPaused()) {
                animatorSet.resume();
            }
        } else {
            if (animatorSet != null) {
                animatorSet.start();
                lowVersionPause = false;
            }
        }
        ivMusic.setVisibility(View.VISIBLE);
        ivMusic.setImageResource(R.mipmap.icon_music_play_gray_76_76);
        musicAnimator.start();
        mediaPlayer.start();
    }


    private void setStatusPlay() {
        status = STATUS_PLAY;
        llContent.setVisibility(View.GONE);
        ivPlay.setVisibility(View.GONE);
        ivMusic.setVisibility(View.VISIBLE);
        ivMusic.setImageResource(R.mipmap.icon_music_play_gray_76_76);
        actionHolderLayout.setVisibility(View.GONE);
        rlMessageMerchant.setVisibility(View.GONE);
        musicAnimator.start();
        loadImageWithAnimation(0);
    }

    private void setStatusFinish() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.stop();
        status = STATUS_START;
        ivMusic.setVisibility(View.GONE);
        musicAnimator.end();
        actionHolderLayout.setVisibility(View.VISIBLE);
        rlMessageMerchant.setVisibility(View.VISIBLE);
        ivPlay.setVisibility(View.VISIBLE);
        ivPlay.setImageResource(R.mipmap.icon_stop_round_yellow_135_135);
    }

    public void musicPlay() {
        if (casePhoto == null) {
            return;
        }
        if (mediaPlayer == null) {
            setMediaPlayer();
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(casePhoto.getMusicUrl());
            mediaPlayer.setLooping(true);
            mediaPlayer.prepareAsync();
            setStatusPlay();
            ivMusic.setVisibility(View.GONE);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    if (status == STATUS_PAUSE) {
                        mediaPlayer.pause();
                    } else {
                        ivMusic.setVisibility(View.VISIBLE);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mediaPlayer = null;
        }
    }

    @OnClick(R.id.iv_play)
    void onPlay() {
        switch (status) {
            case STATUS_PAUSE:
                setStatusResume();
                break;
            case STATUS_START:
                musicPlay();
                break;
            default:
                break;
        }
    }

    /**
     * @param view view
     * @param top  在顶部 为true 否则为false
     */
    private void showLayout(final View view, final boolean show, boolean top) {
        if (view != null) {
            view.clearAnimation();
            TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0,
                    Animation.RELATIVE_TO_SELF,
                    0,
                    Animation.RELATIVE_TO_SELF,
                    show ? (top ? -1 : 1) : 0,
                    Animation.RELATIVE_TO_SELF,
                    show ? 0 : (top ? -1 : 1));
            animation.setDuration(300);
            animation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (!show) {
                        view.setVisibility(View.GONE);
                    }
                }
            });
            view.startAnimation(animation);
        }
    }

    private class BitmapHolder {
        Bitmap bitmap;
        Bitmap blurBitmap;

        BitmapHolder(Bitmap bitmap, Bitmap blurBitmap) {
            this.bitmap = bitmap;
            this.blurBitmap = blurBitmap;
        }
    }

    private void loadImageWithAnimation(final Integer aLong) {
        final List<WorkMediaItem> photos = casePhoto.getDetailPhotos();
        if (CommonUtil.isCollectionEmpty(photos)) {
            return;
        }
        if (aLong >= photos.size()) {
            return;
        }
        final WorkMediaItem photo = photos.get(aLong);
        if (photo == null) {
            return;
        }
        getBitmapObb(ImagePath.buildPath(photo.getMediaPath())
                .width(imageWidth)
                .height(imageHeight)
                .quality(75)
                .path()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BitmapHolder>() {
                    @Override
                    public void call(BitmapHolder bitmapHolder) {
                        Bitmap bitmap = bitmapHolder.bitmap;
                        if (bitmap == null) {
                            return;
                        }
                        int bitmapWith = bitmap.getWidth();
                        int bitmapHeight = bitmap.getHeight();
                        float rate = (float) screenWidth / bitmapWith;
                        int h = Math.round(bitmapHeight * rate);
                        if ((float) h / screenHeight > 0.8) {
                            ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        } else {
                            ivCover.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        }
                        blurBg.setImageBitmap(bitmapHolder.blurBitmap);
                        ivCover.setImageBitmap(bitmap);
                        EffectStyle effectStyle;
                        if (bitmapWith < bitmapHeight) {
                            if (aLong % 2 == 0) {
                                effectStyle = typeScale[new Random().nextInt(typeScale.length)];
                            } else {
                                effectStyle = EffectStyle.SCALE_SHRINK;
                            }
                        } else {
                            effectStyle = typeTran[new Random().nextInt(typeTran.length)];
                        }
                        BaseEffects animator = effectStyle.getAnimator();
                        animator.setDuration(DURING);
                        if (aLong == 0) {
                            animatorSet = addFirstAnim();
                        } else {
                            animatorSet = animator.getAnimatorSet();
                            animator.setupAnimation(ivCover, screenWidth);
                        }
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (!lowVersionPause) {
                                    if (aLong == photos.size() - 1) {
                                        setStatusFinish();
                                    } else {
                                        status = STATUS_ANIMATION_END;
                                        loadImageWithAnimation(aLong + 1);
                                    }
                                }
                                if (aLong == 0) {
                                    llContent.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onAnimationStart(Animator animation) {
                                status = STATUS_PLAY;
                                if (aLong == 1) {
                                    coverShadow.setVisibility(View.GONE);
                                }
                            }
                        });
                        ivCover.setX(0);
                        animatorSet.start();
                    }
                });
    }

    private AnimatorSet addFirstAnim() {
        long startDuring = 2000;
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new LinearInterpolator());
        llContent.setVisibility(View.VISIBLE);
        coverShadow.setVisibility(View.VISIBLE);
        ObjectAnimator coverScaleBeforeX = ObjectAnimator.ofFloat(ivCover, "scaleX", 1.1f, 1.2f)
                .setDuration(startDuring);
        ObjectAnimator coverScaleBeforeY = ObjectAnimator.ofFloat(ivCover, "scaleY", 1.1f, 1.2f)
                .setDuration(startDuring);
        ObjectAnimator textScaleX = ObjectAnimator.ofFloat(llContent, "scaleX", 1.15f, 1)
                .setDuration(startDuring);
        ObjectAnimator textScaleY = ObjectAnimator.ofFloat(llContent, "scaleY", 1.15f, 1)
                .setDuration(startDuring);
        animatorSet.play(coverScaleBeforeX)
                .with(coverScaleBeforeY)
                .with(textScaleX)
                .with(textScaleY);
        return animatorSet;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (status == STATUS_PLAY) {
            setStatusPause();
        }
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onFinish() {
        stopPlaying();
        CommonUtil.unSubscribeSubs(httpSubscriber, collectSubscriber);
        super.onFinish();
    }
}
