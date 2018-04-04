package com.hunliji.hljcardlibrary.views.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcardlibrary.models.CardPage;
import com.hunliji.hljcardlibrary.models.CardRxEvent;
import com.hunliji.hljcardlibrary.models.ImageHole;
import com.hunliji.hljcardlibrary.models.ImageInfo;
import com.hunliji.hljcardlibrary.utils.CardEditObbUtil;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljvideolibrary.player.EasyVideoCallback;
import com.hunliji.hljvideolibrary.player.EasyVideoPlayer;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangtao on 2017/7/4.
 */

public class PageVideoPreviewActivity extends HljBaseNoBarActivity implements EasyVideoCallback {


    @BindView(R2.id.player)
    EasyVideoPlayer player;
    @BindView(R2.id.tv_max_sec)
    TextView tvMaxSec;
    @BindView(R2.id.edit_hint_layout)
    LinearLayout editHintLayout;
    @BindView(R2.id.btn_edit)
    Button btnEdit;
    @BindView(R2.id.btn_edit2)
    Button btnEdit2;
    @BindView(R2.id.btn_choose_ok)
    Button btnChooseOk;
    @BindView(R2.id.action_holder_layout)
    LinearLayout actionHolderLayout;

    private CardPage page;
    private ImageHole imageHole;
    private ImageInfo imageInfo;
    private Photo photo;
    private Uri uri;
    private boolean isStartPause;

    private Subscription editSubscription;
    private Subscription rxBusSubscription;

    public static final int MAX_SEC = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_video_preview___card);
        ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout);

        initValues();
        initViews();

        registerRxBusSub();
    }

    private void initValues() {
        photo = getIntent().getParcelableExtra("photo");
        page = getIntent().getParcelableExtra("page");
        imageHole = getIntent().getParcelableExtra("imageHole");
        imageInfo = getIntent().getParcelableExtra("imageInfo");
        uri = Uri.fromFile(new File(photo.getImagePath()));
        isStartPause = true;
    }

    private void initViews() {
        setSwipeBackEnable(false);

        player.setCallback(this);
        if (uri == null) {
            ToastUtil.showToast(this, "视频资源错误", 0);
        } else {
            player.setSource(uri);
        }
        if (photo.getDuration() > MAX_SEC * 1000) {
            editHintLayout.setVisibility(View.VISIBLE);
            btnEdit2.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
            btnChooseOk.setVisibility(View.GONE);
            tvMaxSec.setText(String.valueOf(MAX_SEC));
        } else {
            editHintLayout.setVisibility(View.GONE);
            btnEdit2.setVisibility(View.GONE);
            btnEdit.setVisibility(View.VISIBLE);
            btnChooseOk.setVisibility(View.VISIBLE);
        }
    }

    private void registerRxBusSub() {
        rxBusSubscription = RxBus.getDefault()
                .toObservable(CardRxEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxBusSubscriber<CardRxEvent>() {
                    @Override
                    protected void onEvent(CardRxEvent cardRxEvent) {
                        switch (cardRxEvent.getType()) {
                            case PAGE_VIDEO_EDIT:
                                finish();
                                break;
                        }
                    }
                });
    }

    @Override
    public void onStarted(final EasyVideoPlayer player) {
        if (isStartPause) {
            isStartPause = false;
            player.postDelayed(new Runnable() {
                @Override
                public void run() {
                    player.pause();
                }
            }, 100);
        }

    }

    @Override
    public void onPaused(EasyVideoPlayer player) {

    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {

    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {
    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {

    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {

    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {

    }

    @Override
    public void onClickVideoFrame(EasyVideoPlayer player) {

    }

    @OnClick(R2.id.btn_close)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick({R2.id.btn_edit, R2.id.btn_edit2})
    public void onEdit() {
        Intent intent = new Intent(this, PageVideoTrimActivity.class);
        intent.putExtra("page", page);
        intent.putExtra("imageInfo", imageInfo);
        intent.putExtra("imageHole", imageHole);
        intent.putExtra("photo", photo);
        startActivity(intent);
    }

    @OnClick(R2.id.btn_choose_ok)
    public void onChoose() {
        if (photo.getDuration() > 6000) {
            onEdit();
        } else {
            editSubscription = CardEditObbUtil.uploadPageForVideoObb(this,
                    page,
                    imageHole,
                    imageInfo,
                    photo)
                    .subscribe(HljHttpSubscriber.buildSubscriber(this)
                            .setProgressDialog(DialogUtil.createProgressDialog(this))
                            .setOnNextListener(new SubscriberOnNextListener() {
                                @Override
                                public void onNext(Object o) {
                                    onBackPressed();
                                }
                            })
                            .build());
        }
    }


    @Override
    protected void onPause() {
        player.pause();
        super.onPause();
    }

    @Override
    protected void onFinish() {
        player.release();
        CommonUtil.unSubscribeSubs(editSubscription, rxBusSubscription);
        super.onFinish();
    }
}
