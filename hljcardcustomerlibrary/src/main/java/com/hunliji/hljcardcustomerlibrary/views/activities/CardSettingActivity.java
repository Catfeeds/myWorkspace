package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CompoundButton;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.CardSetupStatus;
import com.hunliji.hljcardlibrary.models.Music;
import com.hunliji.hljcardlibrary.views.activities.BaseCardSettingActivity;
import com.hunliji.hljcommonlibrary.models.BindInfo;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2017/6/12.请帖设置页面
 */

@Route(path = RouterPath.IntentPath.Card.CARD_SETTING)
public class CardSettingActivity extends BaseCardSettingActivity {

    private BindInfo bindInfo;

    @Override
    protected void initCashView() {
        cbGift.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (lockState) {
                    return;
                }
                map.clear();
                map.put("gift", isChecked);
                map.put("id", cardId);
                postCardSetting(buttonView, isChecked);
            }
        });
        cbCash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (lockState) {
                    return;
                }
                map.clear();
                map.put("gold", isChecked);
                map.put("id", cardId);
                postCardSetting(buttonView, isChecked);
            }
        });
    }

    @Override
    public void onWithdrawInfoClicked() {
        if (cardId == 0) {
            return;
        }
        Intent intent = new Intent();
        if (bindInfo == null) {
            //未绑定过提现方式，优先跳银行卡提现设置页
            intent.setClass(this, BindBankSettingActivity.class);
        } else if (bindInfo.getType() == BindInfo.BIND_BANK) {
            //已绑定过银行卡提现
            intent.setClass(this, BindBankSettingActivity.class);
            intent.putExtra("bind_info", bindInfo);
        } else {
            //已绑定过微信提现
            intent.setClass(this, BindWXSettingActivity.class);
            intent.putExtra("bind_info", bindInfo);
        }
        intent.putExtra("card", card);
        intent.putExtra("can_modify_name",
                cardSetupStatus != null && cardSetupStatus.isCanModifyName());
        startActivity(intent);
    }

    @Override
    public void initLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            if (cardId == 0) {
                return;
            }
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            settingLayout.setVisibility(View.VISIBLE);
                            if (resultZip != null) {
                                bindInfo = resultZip.bindInfo;
                                cardSetupStatus = resultZip.cardSet;
                                refreshMusicInfo(resultZip.musics);
                                refreshCardSetupStatus();
                                refreshCashStatus();
                            }
                            refreshBindView();
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            settingLayout.setVisibility(View.VISIBLE);
                            refreshBindView();
                        }
                    })
                    .setDataNullable(true)
                    .setProgressBar(progressBar)
                    .build();
            Observable bObservable = CustomerCardApi.getBindInfo(cardId);
            Observable cObservable = CardApi.getCardSetupStatusObb(cardId);
            Observable<HljHttpData<List<Music>>> mObservable = CardApi.getCardMusic(cardId);
            Observable observable = Observable.zip(bObservable,
                    cObservable,
                    mObservable,
                    new Func3<BindInfo, CardSetupStatus, HljHttpData<List<Music>>, ResultZip>() {
                        @Override
                        public ResultZip call(
                                BindInfo bindInfo,
                                CardSetupStatus cardSet,
                                HljHttpData<List<Music>> listHljHttpData) {
                            ResultZip resultZip = new ResultZip();
                            resultZip.bindInfo = bindInfo;
                            resultZip.cardSet = cardSet;
                            if (listHljHttpData != null) {
                                resultZip.musics = listHljHttpData.getData();
                            }
                            return resultZip;
                        }
                    });
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(initSubscriber);
        }
    }

    private class ResultZip extends HljHttpResultZip {
        @HljRZField
        BindInfo bindInfo;
        @HljRZField
        CardSetupStatus cardSet;
        @HljRZField
        List<Music> musics;
    }

    protected void refreshCashStatus() {
        lockState = true;
        cbCash.setChecked(cardSetupStatus.isGold());
        cbGift.setChecked(cardSetupStatus.isGift());
        lockState = false;
    }

    public void refreshBindView() {
        if (bindInfo != null) {
            tvWithdrawInfo.setTextColor(ContextCompat.getColor(CardSettingActivity.this,
                    R.color.colorBlack2));
            imgBindLogo.setVisibility(View.VISIBLE);
            if (bindInfo.getType() == BindInfo.BIND_BANK) {
                tvWithdrawInfo.setText(getString(R.string.format_bind_info___card,
                        bindInfo.getBankDesc(),
                        bindInfo.getAccNo()));
                Glide.with(CardSettingActivity.this)
                        .load(ImagePath.buildPath(bindInfo.getBankLogo())
                                .width(logoWidth)
                                .height(logoWidth)
                                .cropPath())
                        .into(imgBindLogo);
            } else if (bindInfo.getType() == BindInfo.BIND_WX) {
                imgBindLogo.setImageResource(R.mipmap.icon_wx_63_63___card);
                tvWithdrawInfo.setText(getString(R.string.format_bind_info___card,
                        "微信钱包",
                        bindInfo.getIdHolder()));
            }
        } else {
            imgBindLogo.setVisibility(View.GONE);
            tvWithdrawInfo.setTextColor(ContextCompat.getColor(CardSettingActivity.this,
                    R.color.colorPrimary));
            tvWithdrawInfo.setText(getString(R.string.label_set_card_bind___card));
        }
    }

    @Override
    public void onBackPressed() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("chat_state", cbBarrage.isChecked());
        jsonObject.addProperty("chat_gift", cbGift.isChecked());
        jsonObject.addProperty("chat_price", cbCash.isChecked());
        Intent intent = getIntent();
        intent.putExtra("other_state", jsonObject.toString());
        intent.putExtra("is_closed", card.isClosed());
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
