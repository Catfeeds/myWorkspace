package com.hunliji.hljcardlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.JsonElement;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.Audio;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcardlibrary.models.CardRxEvent;
import com.hunliji.hljcardlibrary.models.CardSetupStatus;
import com.hunliji.hljcardlibrary.models.Music;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by mo_yu on 2017/9/11.请帖设置base
 */
public abstract class BaseCardSettingActivity extends HljBaseActivity {

    @BindView(R2.id.information_layout)
    public LinearLayout informationLayout;
    @BindView(R2.id.card_music_layout)
    public LinearLayout cardMusicLayout;
    @BindView(R2.id.cb_barrage)
    public CheckBox cbBarrage;
    @BindView(R2.id.barrage_layout)
    public LinearLayout barrageLayout;
    @BindView(R2.id.withdraw_info_layout)
    public LinearLayout withdrawInfoLayout;
    @BindView(R2.id.cb_gift)
    public CheckBox cbGift;
    @BindView(R2.id.gift_layout)
    public LinearLayout giftLayout;
    @BindView(R2.id.cb_cash)
    public CheckBox cbCash;
    @BindView(R2.id.cash_layout)
    public LinearLayout cashLayout;
    @BindView(R2.id.tv_withdraw_info)
    public TextView tvWithdrawInfo;
    @BindView(R2.id.withdraw_layout)
    public LinearLayout withdrawLayout;
    @BindView(R2.id.btn_copy_card)
    public Button btnCopyCard;
    @BindView(R2.id.btn_delete_card)
    public Button btnDeleteCard;
    @BindView(R2.id.img_bind_logo)
    public ImageView imgBindLogo;
    @BindView(R2.id.tv_card_music)
    public TextView tvCardMusic;
    @BindView(R2.id.setting_layout)
    public View settingLayout;
    @BindView(R2.id.progress_bar)
    public ProgressBar progressBar;

    protected long cardId;
    protected Card card;
    protected HashMap<String, Object> map;
    protected int logoWidth;
    protected CardSetupStatus cardSetupStatus;
    protected boolean lockState;
    private HljHttpSubscriber setSubscriber;
    protected HljHttpSubscriber initSubscriber;
    private HljHttpSubscriber copySubscriber;
    private HljHttpSubscriber deleteSubscriber;
    private HljHttpSubscriber openSubscriber;
    private Subscription rxBusEventSub;
    private Subscription cardRxBusSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_setting___card);
        ButterKnife.bind(this);

        initValue();
        initView();
        initCashView();
        initLoad();

        registerRxBusEvent();
    }

    protected abstract void initCashView();

    private void initValue() {
        card = getIntent().getParcelableExtra("card");
        if (card != null) {
            cardId = card.getId();
        }
        logoWidth = CommonUtil.dp2px(this, 32);
        map = new HashMap<>();
    }

    private void initView() {
        if (card != null) {
            tvCardMusic.setText("");
        }
        cbBarrage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (lockState) {
                    return;
                }
                map.clear();
                map.put("danmu", isChecked);
                map.put("id", cardId);
                postCardSetting(buttonView, isChecked);
            }
        });
    }

    public abstract void initLoad();

    protected void refreshCardSetupStatus() {
        lockState = true;
        cbBarrage.setChecked(cardSetupStatus.isDanmu());
        lockState = false;
        if (card != null && card.isClosed()) {
            btnDeleteCard.setText("开启请帖");
            btnDeleteCard.setBackgroundResource(R.drawable.sl_color_primary_2_dark);
            btnDeleteCard.setTextColor(ContextCompat.getColor(BaseCardSettingActivity.this,
                    R.color.colorWhite));
        } else {
            btnDeleteCard.setText("删除请帖");
            btnDeleteCard.setBackgroundResource(R.drawable.sl_color_white_2_background2);
            btnDeleteCard.setTextColor(ContextCompat.getColor(BaseCardSettingActivity.this,
                    R.color.colorPrimary));
        }
    }

    protected void refreshMusicInfo(List<Music> musics) {
        if (CommonUtil.isCollectionEmpty(musics)) {
            tvCardMusic.setText("无音乐");
            return;
        }
        int kind = 0;
        for (Music music : musics) {
            if (music.isSelected()) {
                kind = music.getKind();
                if (kind == 1) {
                    //录音
                    tvCardMusic.setText("录音");
                } else {
                    //本地音乐
                    tvCardMusic.setText(music.getName());
                }
            }
        }
        if (kind == 0) {
            tvCardMusic.setText("无音乐");
        }
    }

    protected void postCardSetting(final CompoundButton compoundButton, final boolean isCheck) {
        CommonUtil.unSubscribeSubs(setSubscriber);
        setSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                    @Override
                    public void onNext(HljHttpResult result) {
                        if (result != null && result.getStatus() != null && result.getStatus()
                                .getRetCode() != 0) {
                            lockState = true;
                            compoundButton.setChecked(!isCheck);
                            lockState = false;
                            ToastUtil.showToast(BaseCardSettingActivity.this,
                                    result.getStatus()
                                            .getMsg(),
                                    0);
                        }
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        lockState = true;
                        compoundButton.setChecked(!isCheck);
                        lockState = false;
                    }
                })
                .setProgressBar(progressBar)
                .build();
        CardApi.postCardSetupObb(map)
                .subscribe(setSubscriber);
    }

    private void copyCard() {
        if (copySubscriber == null || copySubscriber.isUnsubscribed()) {
            copySubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<Long>() {
                        @Override
                        public void onNext(Long cardId) {
                            RxBus.getDefault()
                                    .post(new CardRxEvent(CardRxEvent.RxEventType.CARD_COPY,
                                            cardId));
                            finish();
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .build();
            CardApi.copyCardObb(cardId)
                    .map(new Func1<JsonElement, Long>() {
                        @Override
                        public Long call(JsonElement jsonElement) {
                            return jsonElement.getAsJsonObject()
                                    .get("id")
                                    .getAsLong();
                        }
                    })
                    .subscribe(copySubscriber);
        }
    }

    private void deleteCard() {
        if (deleteSubscriber == null || deleteSubscriber.isUnsubscribed()) {
            deleteSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            RxBus.getDefault()
                                    .post(new CardRxEvent(CardRxEvent.RxEventType.CARD_DELETE,
                                            card));
                            finish();
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .build();
            CardApi.deleteCardObb(cardId)
                    .subscribe(deleteSubscriber);
        }
    }

    private void openCard() {
        if (openSubscriber == null || openSubscriber.isUnsubscribed()) {
            openSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            ToastUtil.showToast(BaseCardSettingActivity.this, "请帖已重新开启！", 0);
                            RxBus.getDefault()
                                    .post(new CardRxEvent(CardRxEvent.RxEventType.CARD_CLOSE_CHANGE,
                                            card));
                            card.setClosed(false);
                            btnDeleteCard.setText("删除请帖");
                            btnDeleteCard.setBackgroundResource(R.drawable
                                    .sl_color_white_2_background2);
                            btnDeleteCard.setTextColor(ContextCompat.getColor(
                                    BaseCardSettingActivity
                                            .this,
                                    R.color.colorPrimary));
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .build();
            CardApi.openCardObb(cardId)
                    .subscribe(openSubscriber);
        }
    }

    @OnClick(R2.id.information_layout)
    public void onInformationClicked() {
        if (cardId == 0) {
            return;
        }
        ARouter.getInstance()
                .build(RouterPath.IntentPath.Card.CARD_INFO_EDIT)
                .withParcelable("card", card)
                .navigation(this);
    }

    @OnClick(R2.id.card_music_layout)
    public void onCardMusicClicked() {
        if (cardId == 0) {
            return;
        }
        Intent intent = new Intent(this, CardMusicListActivity.class);
        intent.putExtra("cardId", cardId);
        startActivity(intent);
    }

    @OnClick(R2.id.withdraw_layout)
    public abstract void onWithdrawInfoClicked();

    @OnClick(R2.id.btn_delete_card)
    public void onDeleteCardClicked() {
        if (card != null && card.isClosed()) {
            openCard();
        } else {
            DialogUtil.createDialogWithIcon(this,
                    null,
                    getString(R.string.hint_delete_card___card),
                    null,
                    null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteCard();
                        }
                    },
                    null)
                    .show();

        }
    }

    @OnClick(R2.id.btn_copy_card)
    public void onCopyCard() {
        if (cardId == 0) {
            return;
        }
        copyCard();
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
                                case BIND_BANK_SUCCESS:
                                case BIND_WX_SUCCESS:
                                case UNBIND_BANK_SUCCESS:
                                case UNBIND_WX_SUCCESS:
                                    initLoad();
                                    break;
                            }
                        }
                    });
        }
        if (cardRxBusSubscription == null || cardRxBusSubscription.isUnsubscribed()) {
            cardRxBusSubscription = RxBus.getDefault()
                    .toObservable(CardRxEvent.class)
                    .subscribe(new RxBusSubscriber<CardRxEvent>() {
                        @Override
                        protected void onEvent(CardRxEvent cardRxEvent) {
                            switch (cardRxEvent.getType()) {
                                case CARD_INFO_EDIT:
                                    BaseCardSettingActivity.this.card = (Card) cardRxEvent
                                            .getObject();
                                    //请帖信息变更需要刷新礼物礼金开关状态
                                    initLoad();
                                    break;
                                case CARD_MUSIC_EDIT:
                                    Audio audio = (Audio) cardRxEvent.getObject();
                                    if (audio != null && !TextUtils.isEmpty(audio.getCurrentPath
                                            ())) {

                                        // 1 录音 2 本地音乐 3 线上音乐
                                        if (audio.getKind() == 1) {
                                            //录音
                                            tvCardMusic.setText("录音");
                                        } else if (audio.getKind() == 2) {
                                            //本地音乐
                                            tvCardMusic.setText(audio.getFileMusicName());
                                        } else if (audio.getKind() == 3) {
                                            //线上音乐
                                            tvCardMusic.setText(audio.getClassicMusicName());
                                        } else {
                                            tvCardMusic.setText("无音乐");
                                        }
                                    } else {
                                        tvCardMusic.setText("无音乐");
                                    }
                                    break;
                            }
                        }
                    });
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(copySubscriber,
                setSubscriber,
                deleteSubscriber,
                initSubscriber,
                openSubscriber,
                rxBusEventSub,
                cardRxBusSubscription);
    }

}
