package me.suncloud.marrymemo.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.PageV2Adapter;
import me.suncloud.marrymemo.api.card.CardApi;
import me.suncloud.marrymemo.fragment.CardV2ShareFragment;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.Music;
import me.suncloud.marrymemo.model.V2.CardPageV2;
import me.suncloud.marrymemo.model.V2.CardV2;
import me.suncloud.marrymemo.model.card.ShareLink;
import me.suncloud.marrymemo.util.CardResourceUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by Suncloud on 2016/4/25.
 */
public class CardV2EditActivity extends HljBaseNoBarActivity implements PageV2Adapter
        .OnPageItemClickListener {

    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.bottom_layout)
    FrameLayout bottomLayout;
    @BindView(R.id.viewpager)
    RecyclerView mRecyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.layout)
    RelativeLayout layout;
    @BindView(R.id.new_music_layout)
    LinearLayout newMusicLayout;
    @BindView(R.id.hint_layout)
    View hintLayout;
    @BindView(R.id.iv_drag)
    ImageView ivDrag;
    private CardV2 card;
    private PageV2Adapter adapter;
    private float itemWidth;
    private float itemHeight;
    private float itemMargin;
    private int padding;

    private ObjectAnimator newMusicAnimator;
    private Subscription linkSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Point point = JSONUtil.getDeviceSize(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        itemMargin = dm.density * 10;
        itemWidth = (0.645f * point.y * 8) / 13;
        itemHeight = 0.645f * point.y;
        int height = Math.round(itemHeight + dm.density * 10);
        int width = Math.round(itemWidth + dm.density * 10);
        card = (CardV2) getIntent().getSerializableExtra("card");
        super.onCreate(savedInstanceState);
        if (card == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_card_v2_edit);
        ButterKnife.bind(this);

        setDefaultStatusBarPadding();
        setSwipeBackEnable(false);

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return v.getScaleX() != 1;
            }
        });
        padding = Math.round((point.x - width - dm.density * 14) / 2);
        LinearLayoutManager layout = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,
                false);
        mRecyclerView.setLayoutManager(layout);
        adapter = new PageV2Adapter(this, width, height, padding, this);
        adapter.setCard(card);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setHasFixedSize(true);
        new PagerSnapHelper().attachToRecyclerView(mRecyclerView);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                mRecyclerViewTranslate(recyclerView);
            }
        });

        EventBus.getDefault()
                .register(this);
        showNewMusicHint();
        showCardDragHint();
    }

    @OnTouch(R.id.layout)
    public boolean onParentTouch(MotionEvent e) {
        if (mRecyclerView.getScaleX() == 1) {
            mRecyclerView.onTouchEvent(e);
        }
        return true;
    }

    private void mRecyclerViewTranslate(RecyclerView recyclerView) {
        int childCount = mRecyclerView.getChildCount();
        //        int padding = mRecyclerView.getPaddingLeft();

        for (int j = 0; j < childCount; j++) {
            View v = recyclerView.getChildAt(j);
            float rate = 0;
            if (v.getLeft() <= padding) {
                if (v.getLeft() >= padding - v.getWidth()) {
                    rate = (padding - v.getLeft()) * 1f / v.getWidth();
                } else {
                    rate = 1;
                }
                v.setRotationY(15 * rate);
                v.setPivotX(v.getWidth());
                v.setPivotY(v.getHeight() * .5f);
                v.setScaleX(1 - 0.28f * rate);
                v.setScaleY(1 - 0.28f * rate);

            } else {
                if (v.getLeft() <= recyclerView.getWidth() - padding) {
                    rate = (recyclerView.getWidth() - padding - v.getLeft()) * 1f / v.getWidth();
                }
                v.setPivotX(0);
                v.setPivotY(v.getHeight() * .5f);
                v.setScaleX(0.72f + 0.28f * rate);
                v.setScaleY(0.72f + 0.28f * rate);
                v.setRotationY(15 * (rate - 1));
            }
        }
    }

    @Override
    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    @OnClick(R.id.btn_feedback)
    public void onFeedback(View view) {
        Intent intent = new Intent(this, FeedbackActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.cash_gift_layout)
    public void onCashGift(View view) {
        if (card == null) {
            return;
        }
        Intent intent = new Intent(this, SwitchCashGiftActivity.class);
        intent.putExtra("cardId", card.getId());
        intent.putExtra("brideName", card.getBrideName());
        intent.putExtra("groomName", card.getGroomName());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);

    }

    @OnClick(R.id.sort_layout)
    public void onSort(View view) {
        Intent intent = new Intent(this, PageV2SortActivity.class);
        intent.putExtra("card", card);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.music_layout)
    public void onSoundEdit(View view) {
        if (newMusicLayout.getVisibility() == View.VISIBLE) {
            newMusicLayout.setVisibility(View.GONE);
        }
        Intent intent = new Intent(this, CardMusicListActivity.class);
        intent.putExtra("cardV2", card);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.preview_layout)
    public void onPreview(View view) {
        CommonUtil.unSubscribeSubs(linkSubscription);
        linkSubscription = CardApi.getShareLinkObb(card.getId())
                .map(new Func1<ShareLink, String>() {
                    @Override
                    public String call(ShareLink shareLink) {
                        return shareLink.getLink();
                    }
                })
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return !TextUtils.isEmpty(s);
                    }
                })
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setProgressDialog(DialogUtil.createProgressDialog(this))
                        .setOnNextListener(new SubscriberOnNextListener<String>() {
                            @Override
                            public void onNext(String path) {
                                Intent intent = new Intent(CardV2EditActivity.this,
                                        CardV2WebActivity.class);
                                intent.putExtra("card", card);
                                intent.putExtra("path", path);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.activity_anim_default);

                            }
                        })
                        .build());
    }

    @OnClick(R.id.send_layout)
    public void onShare(View view) {
        CardV2ShareFragment dialogFragment = CardV2ShareFragment.newInstance(card);
        dialogFragment.show(getSupportFragmentManager(), "shareFragment");
    }

    @Override
    public void pageClick(final CardPageV2 cardPage, int position) {
        //        if (position != mRecyclerView.getCurrentPosition()) {
        //            return;
        //        }
        if (cardPage == null) {
            Intent intent = new Intent(CardV2EditActivity.this, TemplateV2ListActivity.class);
            intent.putExtra("card", card);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else {
            if (mRecyclerView.getScaleX() == 1) {
                float mScale = Math.min((layout.getMeasuredWidth() - itemMargin * 2) / itemWidth,
                        (layout.getMeasuredHeight() - itemMargin * 2) / itemHeight);
                ObjectAnimator actionAnimation = ObjectAnimator.ofFloat(actionLayout,
                        "translationY",
                        -actionLayout.getHeight());
                ObjectAnimator bottomAnimation = ObjectAnimator.ofFloat(bottomLayout,
                        "translationY",
                        bottomLayout.getHeight());
                AnimatorSet animatorSet = new AnimatorSet();
                ObjectAnimator scaleAnimationX = ObjectAnimator.ofFloat(mRecyclerView,
                        "scaleX",
                        mScale);
                ObjectAnimator scaleAnimationY = ObjectAnimator.ofFloat(mRecyclerView,
                        "scaleY",
                        mScale);
                animatorSet.play(scaleAnimationX)
                        .with(scaleAnimationY)
                        .with(actionAnimation)
                        .with(bottomAnimation);
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Intent intent = new Intent(CardV2EditActivity.this,
                                PageV2EditActivity.class);
                        intent.putExtra("cardPage", cardPage);
                        intent.putExtra("card", card);
                        startActivity(intent);
                        overridePendingTransition(R.anim.page_anim_in, R.anim.page_anim_out);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animatorSet.setDuration(300);
                animatorSet.start();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            Fragment shareFragment = getSupportFragmentManager().findFragmentByTag("shareFragment");
            if (shareFragment != null) {
                shareFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void showNewMusicHint() {
        CardResourceUtil cardResourceUtil = CardResourceUtil.getInstance();
        Date date = new Date(getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE).getLong("musicClickTimeV2", 0));
        boolean isNew = false;
        if (cardResourceUtil.getMusics()
                .isEmpty()) {
            cardResourceUtil.executeMusicTask(this);
        } else {
            for (Music music : cardResourceUtil.getMusics()) {
                if (music.isNew(date)) {
                    isNew = true;
                    break;
                }
            }
        }
        if (isNew) {
            newMusicLayout.setVisibility(View.VISIBLE);
            newMusicLayout.post(new Runnable() {
                @Override
                public void run() {
                    if (newMusicAnimator == null) {
                        newMusicAnimator = ObjectAnimator.ofFloat(newMusicLayout,
                                "y",
                                newMusicLayout.getY(),
                                newMusicLayout.getY() + newMusicLayout.getHeight() * 0.1f);
                        newMusicAnimator.setDuration(800);
                        newMusicAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                        newMusicAnimator.setRepeatCount(5);
                        newMusicAnimator.setStartDelay(100);
                        newMusicAnimator.setRepeatMode(ValueAnimator.REVERSE);
                        newMusicAnimator.start();
                    }
                }
            });
        } else if (newMusicLayout.getVisibility() == View.VISIBLE) {
            newMusicLayout.setVisibility(View.GONE);
        }
    }


    private void showCardDragHint() {
        boolean b = getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).getBoolean(
                "cardDrag",
                false);
        if (b) {
            hintLayout.setVisibility(View.GONE);
            return;
        }
        hintLayout.setVisibility(View.VISIBLE);
        hintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setVisibility(View.GONE);
                getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit()
                        .putBoolean("cardDrag", true)
                        .apply();
                return false;
            }
        });
        hintLayout.post(new Runnable() {
            @Override
            public void run() {
                AnimatorSet animatorSet = new AnimatorSet();
                ObjectAnimator translateAnimation = ObjectAnimator.ofFloat(ivDrag,
                        "translationX",
                        -CommonUtil.dp2px(CardV2EditActivity.this, 130))
                        .setDuration(1400);
                ObjectAnimator alphaAnimation1 = ObjectAnimator.ofFloat(ivDrag, "alpha", 0f, 1f)
                        .setDuration(980);
                ObjectAnimator alphaAnimation2 = ObjectAnimator.ofFloat(ivDrag, "alpha", 1f, 0f)
                        .setDuration(420);
                animatorSet.play(translateAnimation)
                        .with(alphaAnimation1);
                animatorSet.play(alphaAnimation1)
                        .before(alphaAnimation2);
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (isFinishing()) {
                            return;
                        }
                        if (hintLayout.getVisibility() == View.VISIBLE) {
                            animation.start();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animatorSet.start();
            }
        });

    }


    public void onEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.EventType.CARD_UPDATE_FLAG) {
            card = (CardV2) event.getObject();
            adapter.setCard(card);
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecyclerViewTranslate(mRecyclerView);
                }
            }, 300);
        } else if (event.getType() == MessageEvent.EventType.CARD_MUSIC_TEMPLATE_UPDATE_FLAG) {
            if (!CardResourceUtil.getInstance()
                    .getMusics()
                    .isEmpty()) {
                showNewMusicHint();
            }
        }
    }

    @Override
    protected void onResume() {
        if (mRecyclerView.getScaleX() > 1) {
            ObjectAnimator actionAnimation = ObjectAnimator.ofFloat(actionLayout,
                    "translationY",
                    0);
            ObjectAnimator bottomAnimation = ObjectAnimator.ofFloat(bottomLayout,
                    "translationY",
                    0);
            ObjectAnimator scaleAnimationX = ObjectAnimator.ofFloat(mRecyclerView, "scaleX", 1);
            ObjectAnimator scaleAnimationY = ObjectAnimator.ofFloat(mRecyclerView, "scaleY", 1);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(scaleAnimationX)
                    .with(scaleAnimationY)
                    .with(actionAnimation)
                    .with(bottomAnimation);
            animatorSet.setDuration(300);
            animatorSet.start();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onFinish() {
        EventBus.getDefault()
                .unregister(this);
        CommonUtil.unSubscribeSubs(linkSubscription);
        super.onFinish();
    }
}