package com.hunliji.hljcardlibrary.views.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.bumptech.glide.Glide;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcardlibrary.models.CardPage;
import com.hunliji.hljcardlibrary.models.CardRxEvent;
import com.hunliji.hljcardlibrary.models.ModifyNameResult;
import com.hunliji.hljcardlibrary.models.TextHole;
import com.hunliji.hljcardlibrary.models.TextInfo;
import com.hunliji.hljcardlibrary.models.Theme;
import com.hunliji.hljcardlibrary.models.wrappers.PostCardBody;
import com.hunliji.hljcardlibrary.utils.Lunar;
import com.hunliji.hljcardlibrary.utils.PageImageUtil;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.utils.OncePrefUtil;
import com.hunliji.hljcommonlibrary.utils.TextCountWatcher;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.MultilineActionEditText;
import com.hunliji.hljhttplibrary.api.FileApi;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljUploadResult;
import com.hunliji.hljhttplibrary.utils.HljFileUploadBuilder;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljmaplibrary.HljMap;
import com.hunliji.hljmaplibrary.views.activities.CardLocationMapActivity;

import org.joda.time.DateTime;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;
import kankan.wheel.widget.DTPicker;
import kankan.wheel.widget.DateTimePickerView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/6/13.
 */

public abstract class BaseCardInfoEditActivity extends HljBaseActivity {

    @BindView(R2.id.et_groom)
    EditText etGroom;
    @BindView(R2.id.et_bride)
    EditText etBride;
    @BindView(R2.id.et_wedding_date)
    EditText etWeddingDate;
    @BindView(R2.id.et_wedding_address)
    MultilineActionEditText etWeddingAddress;
    @BindView(R2.id.btn_location)
    ImageButton btnLocation;
    @BindView(R2.id.iv_map)
    ImageView ivMap;
    @BindView(R2.id.tv_location_hint)
    TextView tvLocationHint;
    @BindView(R2.id.map_preview_layout)
    RelativeLayout mapPreviewLayout;
    @BindView(R2.id.info_layout)
    protected LinearLayout infoLayout; //子类修改布局

    public String showedPrefName;
    private double longitude;
    private double latitude;
    private Calendar calendar;

    private Dialog timePickerDialog;

    private final int REQUEST_LOCATION = 1;

    private Theme theme;
    protected Card card;

    private List<Long> changePageIds;
    private ModifyNameResult modifyNameResult;

    private Subscription canModifySubscription;
    private Subscription editSubscription;
    private Subscription downloadSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_info_edit___card);
        setOkText(R.string.label_save___cm);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        calendar = Calendar.getInstance();
        theme = getIntent().getParcelableExtra("theme");
        card = getIntent().getParcelableExtra("card");
        if (card != null) {
            longitude = card.getLongitude();
            latitude = card.getLatitude();
            if (card.getMapType() == Card.COORD_TYPE_BAIDU && latitude != 0 && longitude != 0) {
                LatLng latLng = HljMap.convertBDPointToAMap(this, latitude, longitude);
                latitude = latLng.latitude;
                longitude = latLng.longitude;
            }
            if (card.getTime() != null) {
                calendar.setTime(card.getTime()
                        .toDate());
            }
        } else {
            User user = UserSession.getInstance()
                    .getUser(this);
            SharedPreferences sp = getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                    Context.MODE_PRIVATE);
            int mapType = sp.getInt("card_info_map_type_" + user.getId(), 0);
            longitude = sp.getFloat("card_info_longitude_" + user.getId(), 0);
            latitude = sp.getFloat("card_info_latitude_" + user.getId(), 0);
            if (mapType == Card.COORD_TYPE_BAIDU && latitude != 0 && longitude != 0) {
                LatLng latLng = HljMap.convertBDPointToAMap(this, latitude, longitude);
                latitude = latLng.latitude;
                longitude = latLng.longitude;
            }
        }
        if (card != null && card.getId() > 0) {
            showedPrefName = HljCommon.SharedPreferencesNames.SHOWED_CARD_RENAME_DENIED + "_" +
                    card.getId();
            checkModifyNameStatus();
        }
        if (card != null) {
            List<String> fontPaths = new ArrayList<>();
            if (card.getFrontPage() != null && card.getFrontPage()
                    .getTemplate() != null) {
                for (String path : card.getFrontPage()
                        .getTemplate()
                        .getFontPaths(this)) {
                    if (!fontPaths.contains(path)) {
                        fontPaths.add(path);
                    }
                }
            }
            if (card.getSpeechPage() != null && card.getSpeechPage()
                    .getTemplate() != null) {
                for (String path : card.getSpeechPage()
                        .getTemplate()
                        .getFontPaths(this)) {
                    if (!fontPaths.contains(path)) {
                        fontPaths.add(path);
                    }
                }
            }
            downloadFonts(fontPaths);
        } else if (theme != null) {
            downloadFonts(theme.getFontPaths(this));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (card != null && card.getId() > 0) {
            checkModifyNameStatus();
        }
    }

    private void checkModifyNameStatus() {
        canModifySubscription = CardApi.checkNameModifyState(card.getId())
                .subscribe(new Action1<ModifyNameResult>() {
                    @Override
                    public void call(ModifyNameResult modifyNameResult) {
                        setModifyNameStatus(modifyNameResult);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    private void setModifyNameStatus(ModifyNameResult result) {
        modifyNameResult = result;
        if (result.getStatus() == ModifyNameResult.STATUS_ALLOW_MODIFY) {
            // 允许直接修改
            etBride.setEnabled(true);
            etGroom.setEnabled(true);
        } else {
            etBride.setEnabled(false);
            etGroom.setEnabled(false);
            if (result.getStatus() == ModifyNameResult.STATUS_MODIFY_FAIL) {
                // 修改审核不通过
                showModifyDeniedIcon();
            } else if (result.getStatus() == ModifyNameResult.STATUS_MODIFY_REVIEWING) {
                // 正在审核中
                Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.icon_rename_reviewing);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                etBride.setCompoundDrawables(null, null, drawable, null);
                etGroom.setCompoundDrawables(null, null, drawable, null);
            } else {
                // 修改需要上传资料，或者不能修改
                etBride.setCompoundDrawables(null, null, null, null);
                etGroom.setCompoundDrawables(null, null, null, null);
            }
        }
    }

    /**
     * 显示失败的图标，点击过就不在显示
     */
    private void showModifyDeniedIcon() {
        if (OncePrefUtil.hasDoneThis(this, showedPrefName)) {
            etBride.setCompoundDrawables(null, null, null, null);
            etGroom.setCompoundDrawables(null, null, null, null);
        } else {
            Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.icon_rename_review_denied);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            etBride.setCompoundDrawables(null, null, drawable, null);
            etGroom.setCompoundDrawables(null, null, drawable, null);
        }
    }

    private void downloadFonts(List<String> fontPaths) {
        downloadSubscription = Observable.from(fontPaths)
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String path) {
                        File file = FileUtil.createFontFile(BaseCardInfoEditActivity.this, path);
                        return file == null || !file.exists() || file.length() == 0;
                    }
                })
                .concatMap(new Func1<String, Observable<File>>() {
                    @Override
                    public Observable<File> call(String path) {
                        return FileApi.download(path,
                                FileUtil.createFontFile(BaseCardInfoEditActivity.this, path)
                                        .getAbsolutePath());
                    }
                })
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(File file) {

                    }
                });
    }

    @OnTouch({R2.id.bride_layout, R2.id.groom_layout})
    public boolean onTouch() {
        if (modifyNameResult != null) {
            if (modifyNameResult.getStatus() == ModifyNameResult.STATUS_MODIFY_LOCKED) {
                // 正在提现中，不能修改
                Toast.makeText(this, "提现中，暂时无法修改姓名", Toast.LENGTH_SHORT)
                        .show();
                return false;
            } else if (modifyNameResult.getStatus() == ModifyNameResult.STATUS_MODIFY_PASS) {
                modifyWithCredential();
            } else if (modifyNameResult.getStatus() == ModifyNameResult.STATUS_MODIFY_REVIEWING) {
                // 审核中
                DialogUtil.createDoubleButtonDialog(BaseCardInfoEditActivity.this,
                        "请帖姓名修改申请已提交，预计1个工作日内审核完成",
                        "查看详情",
                        "取消",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(BaseCardInfoEditActivity.this,
                                        ModifyNameStatusActivity.class);
                                intent.putExtra("id", card.getId());
                                startActivity(intent);
                            }
                        },
                        null)
                        .show();
            } else if (modifyNameResult.getStatus() == ModifyNameResult.STATUS_MODIFY_FAIL) {
                // 审核不通过
                if (OncePrefUtil.hasDoneThis(this, showedPrefName)) {
                    // 如果已经查看过则，还是审核修改
                    modifyWithCredential();
                } else {
                    // 没有查看过则提示查看
                    DialogUtil.createDoubleButtonDialog(BaseCardInfoEditActivity.this,
                            "抱歉，请帖姓名修改申请未通过",
                            "查看详情",
                            "取消",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(BaseCardInfoEditActivity.this,
                                            ModifyNameStatusActivity.class);
                                    intent.putExtra("id", card.getId());
                                    startActivity(intent);
                                }
                            },
                            null)
                            .show();
                }
            }
        }
        return false;
    }

    private void modifyWithCredential() {
        // 不允许直接修改，需要上传资料的修改
        DialogUtil.createDoubleButtonDialog(BaseCardInfoEditActivity.this,
                "为保证您的资金安全，请上传证件照修改请帖姓名",
                "申请修改",
                "取消",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(BaseCardInfoEditActivity.this,
                                ModifyNameApplyActivity.class);
                        intent.putExtra("id", card.getId());
                        startActivity(intent);
                    }
                },
                null)
                .show();
    }

    private void initView() {
        etGroom.addTextChangedListener(new TextCountWatcher(etGroom, 8));
        etBride.addTextChangedListener(new TextCountWatcher(etBride, 8));
        etWeddingDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatetimePicker();
                }

            }
        });
        etWeddingDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showDatetimePicker();
                }
                return true;
            }
        });

        int width = CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 32);
        int height = Math.round(width * 9 / 16);
        mapPreviewLayout.getLayoutParams().height = height;
        if (longitude > 0 && latitude > 0) {
            tvLocationHint.setVisibility(View.GONE);
            etWeddingAddress.setDisAction(true);
            Glide.with(this)
                    .load(HljMap.getAMapUrl(longitude,
                            latitude,
                            width,
                            height,
                            12,
                            HljCommon.MARKER_ICON_RED))
                    .into(ivMap);
        }

        if (card != null) {
            etGroom.setText(card.getGroomName());
            etBride.setText(card.getBrideName());
            etWeddingAddress.setText(card.getPlace());
            etWeddingDate.setText(new DateTime(calendar).toString(getString(R.string
                    .format_card_time___card)));
        } else {
            User user = UserSession.getInstance()
                    .getUser(this);
            SharedPreferences sp = getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                    Context.MODE_PRIVATE);
            etGroom.setText(sp.getString("card_info_groom_" + user.getId(), null));
            etBride.setText(sp.getString("card_info_bride_" + user.getId(), null));
            etWeddingAddress.setText(sp.getString("card_info_address_" + user.getId(), null));
            long time = sp.getLong("card_info_time_" + user.getId(), 0);
            if (time > 0) {
                calendar.setTimeInMillis(time);
                etWeddingDate.setText(new DateTime(calendar).toString(getString(R.string
                        .format_card_time___card)));
            }
        }

        etWeddingAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onLocation(v);
                }
                return false;
            }
        });
    }

    @OnTextChanged(R2.id.et_wedding_address)
    public void onAddressChange(Editable s) {
        String address = s.toString()
                .trim();
        mapPreviewLayout.setVisibility(!TextUtils.isEmpty(address) ? View.VISIBLE : View.GONE);
        btnLocation.setVisibility(!TextUtils.isEmpty(address) ? View.VISIBLE : View.GONE);
    }

    @OnClick({R2.id.btn_location, R2.id.iv_map, R2.id.tv_location_hint})
    public void onLocation(View view) {
        String address = etWeddingAddress.getText()
                .toString()
                .trim();
        if (TextUtils.isEmpty(address)) {
            return;
        }
        Intent intent;
        intent = new Intent(this, CardLocationMapActivity.class);
        intent.putExtra(CardLocationMapActivity.ARG_ADDRESS, address);
        intent.putExtra(CardLocationMapActivity.ARG_LONGITUDE, longitude);
        intent.putExtra(CardLocationMapActivity.ARG_LATITUDE, latitude);
        startActivityForResult(intent, REQUEST_LOCATION);
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        if (!verifyEditInfo()) {
            return;
        }
        if (card == null) {
            card = new Card();
            card.setTheme(theme);
        }
        card.editCard(etBride.getText()
                        .toString(),
                etGroom.getText()
                        .toString(),
                latitude,
                longitude,
                etWeddingAddress.getText()
                        .toString(),
                calendar.getTime(),
                Card.COORD_TYPE_GAO_DE);
        saveCardInfoPreferences(card);
        editCard(card);

    }

    private void saveCardInfoPreferences(Card card) {
        User user = UserSession.getInstance()
                .getUser(this);
        getSharedPreferences(HljCommon.FileNames.PREF_FILE, Context.MODE_PRIVATE).edit()
                .putString("card_info_bride_" + user.getId(), card.getBrideName())
                .putString("card_info_groom_" + user.getId(), card.getGroomName())
                .putFloat("card_info_latitude_" + user.getId(), (float) card.getLatitude())
                .putFloat("card_info_longitude_" + user.getId(), (float) card.getLongitude())
                .putString("card_info_address_" + user.getId(), card.getPlace())
                .putInt("card_info_map_type_" + user.getId(), card.getMapType())
                .putLong("card_info_time_" + user.getId(),
                        card.getTime()
                                .getMillis())
                .apply();
    }

    @SuppressWarnings("unchecked")
    private void editCard(final Card card) {
        editSubscription = Observable.merge(Observable.just(card),
                createPageObb(card.getFrontPage()).map(new Func1<CardPage, Card>() {
                    @Override
                    public Card call(CardPage frontPage) {
                        return card;
                    }
                }),
                createPageObb(card.getSpeechPage()).map(new Func1<CardPage, Card>() {
                    @Override
                    public Card call(CardPage speechPage) {
                        return card;
                    }
                }))
                .last()
                .concatMap(new Func1<Card, Observable<Card>>() {
                    @Override
                    public Observable<Card> call(Card card) {
                        return getEditCardObb();
                    }
                })
                .doOnNext(new Action1<Card>() {
                    @Override
                    public void call(Card card) {
                        if (CommonUtil.isCollectionEmpty(changePageIds)) {
                            return;
                        }
                        if (card.getFrontPage() != null && changePageIds.contains(card
                                .getFrontPage()
                                .getId())) {
                            File pageFile = PageImageUtil.getPageThumbFile(BaseCardInfoEditActivity
                                            .this,
                                    card.getFrontPage()
                                            .getId());
                            if (pageFile != null && pageFile.exists()) {
                                FileUtil.deleteFile(pageFile);
                            }
                            File cardFile = PageImageUtil.getCardThumbFile(BaseCardInfoEditActivity
                                    .this, card.getId());
                            if (cardFile != null && cardFile.exists()) {
                                FileUtil.deleteFile(cardFile);
                            }
                        }
                        if (card.getSpeechPage() != null && changePageIds.contains(card
                                .getSpeechPage()
                                .getId())) {
                            File pageFile = PageImageUtil.getPageThumbFile(BaseCardInfoEditActivity
                                            .this,
                                    card.getSpeechPage()
                                            .getId());
                            if (pageFile != null && pageFile.exists()) {
                                FileUtil.deleteFile(pageFile);
                            }
                        }

                    }
                })
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setProgressDialog(DialogUtil.createProgressDialog(this))
                        .setOnNextListener(new SubscriberOnNextListener<Card>() {
                            @Override
                            public void onNext(Card card) {
                                if (BaseCardInfoEditActivity.this.card.getId() > 0) {
                                    RxBus.getDefault()
                                            .post(new CardRxEvent(CardRxEvent.RxEventType
                                                    .CARD_INFO_EDIT,
                                                    card));
                                    onBackPressed();
                                } else {
                                    RxBus.getDefault()
                                            .post(new CardRxEvent(CardRxEvent.RxEventType
                                                    .CREATE_CARD,
                                                    card));
                                    Intent intent = new Intent(BaseCardInfoEditActivity.this,
                                            CardWebActivity.class);
                                    intent.putExtra("card", card);
                                    intent.putExtra("id", card.getId());
                                    intent.putExtra("path", card.getEditLink());
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        })
                        .build());
    }


    protected Observable<Card> getEditCardObb() {
        return CardApi.editCard(new PostCardBody(card));
    }

    private Observable<CardPage> createPageObb(final CardPage page) {
        if (page == null || CommonUtil.isCollectionEmpty(page.getTextInfos())) {
            return Observable.just(page);
        }
        return Observable.from(page.getTextInfos())
                .map(new Func1<TextInfo, TextInfoZip>() {
                    @Override
                    public TextInfoZip call(TextInfo textInfo) {
                        TextInfoZip zip = new TextInfoZip();
                        zip.info = textInfo;
                        zip.content = textInfo.getContent();
                        if (page.getTemplate() != null) {
                            for (TextHole textHole : page.getTemplate()
                                    .getTextHoles()) {
                                if (textHole.getId() == textInfo.getHoleId()) {
                                    zip.hole = textHole;
                                    break;
                                }
                            }
                        }
                        if (zip.hole != null && !TextUtils.isEmpty(zip.hole.getType())) {
                            switch (zip.hole.getType()) {
                                case "groom":
                                    zip.content = etGroom.getText()
                                            .toString();
                                    break;
                                case "bride":
                                    zip.content = etBride.getText()
                                            .toString();
                                    break;
                                case "time":
                                    zip.content = etWeddingDate.getText()
                                            .toString();
                                    break;
                                case "lunar":
                                    zip.content = new Lunar(calendar.getTime()).toString();
                                    break;
                                case "location":
                                    zip.content = etWeddingAddress.getText()
                                            .toString();
                                    break;
                            }
                        }
                        return zip;
                    }
                })
                .filter(new Func1<TextInfoZip, Boolean>() {
                    @Override
                    public Boolean call(TextInfoZip textInfoZip) {
                        if (!textInfoZip.content.equals(textInfoZip.info.getContent())) {
                            textInfoZip.info.setH5ImagePath(null);
                            textInfoZip.info.setContent(textInfoZip.content);
                        }
                        return textInfoZip.hole != null && TextUtils.isEmpty(textInfoZip.info
                                .getH5ImagePath());
                    }
                })
                .concatMap(new Func1<TextInfoZip, Observable<?>>() {
                    @Override
                    public Observable<TextInfo> call(final TextInfoZip textInfoZip) {
                        return Observable.create(new Observable.OnSubscribe<File>() {
                            @Override
                            public void call(Subscriber<? super File> subscriber) {
                                File textImageFile = PageImageUtil.createTextImage(
                                        BaseCardInfoEditActivity.this,
                                        textInfoZip.hole,
                                        textInfoZip.info.getContent());
                                subscriber.onNext(textImageFile);
                                subscriber.onCompleted();
                            }
                        })
                                .filter(new Func1<File, Boolean>() {
                                    @Override
                                    public Boolean call(File file) {
                                        return file != null && file.exists();
                                    }
                                })
                                .concatMap(new Func1<File, Observable<? extends HljUploadResult>>
                                        () {
                                    @Override
                                    public Observable<? extends HljUploadResult> call(File file) {
                                        return new HljFileUploadBuilder(file).host(HljCard
                                                .getCardHost())
                                                .tokenPath(HljFileUploadBuilder.QINIU_IMAGE_TOKEN)
                                                .build();
                                    }
                                })
                                .map(new Func1<HljUploadResult, TextInfo>() {
                                    @Override
                                    public TextInfo call(HljUploadResult hljUploadResult) {
                                        textInfoZip.info.setH5ImagePath(hljUploadResult.getUrl());
                                        return textInfoZip.info;
                                    }
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .toList()
                .doOnNext(new Action1<List<Object>>() {
                    @Override
                    public void call(List<Object> objects) {
                        if (!CommonUtil.isCollectionEmpty(objects)) {
                            if (changePageIds == null) {
                                changePageIds = new ArrayList<>();
                            }
                            if (page.getId() > 0) {
                                changePageIds.add(page.getId());
                            }
                            ;
                        }
                    }
                })
                .map(new Func1<List<Object>, CardPage>() {
                    @Override
                    public CardPage call(List<Object> objects) {
                        return page;
                    }
                });
    }

    private class TextInfoZip {
        private TextInfo info;
        private TextHole hole;
        private String content;
    }


    protected boolean verifyEditInfo() {
        if (etGroom.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_groom_name___card);
            return false;
        }
        if (etBride.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_bride_name___card);
            return false;
        }
        if (etGroom.length() < 2 || etGroom.length() > 8 || etBride.length() < 2 || etBride
                .length() > 8) {
            ToastUtil.showToast(this, "请输入2~8字的姓名", 0);
            return false;
        }
        if (etWeddingDate.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_wedding_date___card);
            return false;
        }
        if (etWeddingAddress.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_wedding_address___card);
            return false;
        }
        if (latitude == 0 || longitude == 0) {
            ToastUtil.showToast(this, "请标注地址位置", 0);
            return false;
        }
        return true;
    }

    private void showDatetimePicker() {
        if (timePickerDialog != null && timePickerDialog.isShowing()) {
            return;
        }
        if (timePickerDialog == null) {
            timePickerDialog = new Dialog(this, R.style.BubbleDialogTheme);
            timePickerDialog.setContentView(R.layout.dialog_date_time_picker___cm);
            DateTimePickerView picker = (DateTimePickerView) timePickerDialog.findViewById(R.id
                    .picker);
            picker.setYearLimit(2000, 49);
            picker.setCurrentCalender(calendar);

            final Calendar tempCalendar = Calendar.getInstance();
            tempCalendar.setTime(calendar.getTime());

            picker.setOnPickerDateTimeListener(new DTPicker.OnPickerDateTimeListener() {
                @Override
                public void onPickerDateAndTime(
                        int year, int month, int day, int hour, int minute) {
                    tempCalendar.set(year, month - 1, day, hour, minute);
                }
            });
            timePickerDialog.findViewById(R.id.btn_close)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            timePickerDialog.dismiss();
                        }
                    });
            timePickerDialog.findViewById(R.id.btn_confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            timePickerDialog.dismiss();
                            calendar.setTime(tempCalendar.getTime());
                            etWeddingDate.setText(new DateTime(tempCalendar).toString(getString(R
                                    .string.format_card_time___card)));
                        }
                    });


            picker.getLayoutParams().height = Math.round(CommonUtil.dp2px(this, 24 * 8));
            Window win = timePickerDialog.getWindow();
            if (win != null) {
                WindowManager.LayoutParams params = win.getAttributes();
                Point point = CommonUtil.getDeviceSize(this);
                params.width = point.x;
                win.setGravity(Gravity.BOTTOM);
                win.setWindowAnimations(R.style.dialog_anim_rise_style);
            }
        }
        timePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_LOCATION:
                    longitude = data.getDoubleExtra(CardLocationMapActivity.ARG_LONGITUDE, 0);
                    latitude = data.getDoubleExtra(CardLocationMapActivity.ARG_LATITUDE, 0);

                    etWeddingAddress.setDisAction(true);
                    etGroom.requestFocus();
                    tvLocationHint.setVisibility(View.GONE);
                    Glide.with(this)
                            .load(HljMap.getAMapUrl(longitude,
                                    latitude,
                                    ivMap.getWidth(),
                                    ivMap.getHeight(),
                                    12,
                                    HljCommon.MARKER_ICON_RED))
                            .into(ivMap);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(editSubscription, canModifySubscription, downloadSubscription);
        super.onFinish();
    }
}
