package com.hunliji.hljcardlibrary.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.CardPage;
import com.hunliji.hljcardlibrary.models.Font;
import com.hunliji.hljcardlibrary.models.TextHole;
import com.hunliji.hljcardlibrary.models.TextInfo;
import com.hunliji.hljcardlibrary.models.wrappers.PageEditResult;
import com.hunliji.hljcardlibrary.utils.FontUtil;
import com.hunliji.hljcardlibrary.utils.PageImageUtil;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljhttplibrary.api.FileApi;
import com.hunliji.hljhttplibrary.entities.HljUploadResult;
import com.hunliji.hljhttplibrary.utils.HljFileUploadBuilder;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/6/26.
 */

public class PageTextEditActivity extends Activity {

    @BindView(R2.id.et_content)
    EditText etContent;

    private CardPage page;
    private TextInfo textInfo;
    private TextHole textHole;

    private boolean isPageChanged;

    private Subscription downloadSubscription;
    private Subscription editSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_text_edit___card);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        page = getIntent().getParcelableExtra("page");
        textHole = getIntent().getParcelableExtra("textHole");
        textInfo = getIntent().getParcelableExtra("textInfo");
        if (textInfo == null) {
            textInfo = new TextInfo(textHole);
        }
        etContent.setText(textInfo.getContent());
        etContent.setSelection(etContent.length());
        if (downloadSubscription != null && !downloadSubscription.isUnsubscribed()) {
            return;
        }
        downloadSubscription = downloadFontObb().subscribe(new Subscriber<File>() {
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

    @OnClick(R2.id.layout)
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }

    @OnClick(R2.id.btn_confirm)
    public void onConfirm() {
        editSubscription = Observable.just(textInfo)
                .concatMap(new Func1<TextInfo, Observable<TextInfo>>() {
                    @Override
                    public Observable<TextInfo> call(final TextInfo textInfo) {
                        if (!etContent.getText()
                                .toString()
                                .equals(textInfo.getContent())) {
                            textInfo.setH5ImagePath(null);
                            textInfo.setContent(etContent.getText()
                                    .toString());
                        }
                        if (!TextUtils.isEmpty(textInfo.getH5ImagePath())) {
                            return Observable.just(textInfo);
                        } else {
                            return Observable.create(new Observable.OnSubscribe<File>() {
                                @Override
                                public void call(Subscriber<? super File> subscriber) {
                                    File textImageFile = PageImageUtil.createTextImage(
                                            PageTextEditActivity.this,
                                            textHole,
                                            textInfo.getContent());
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
                                    .concatMap(new Func1<File, Observable<? extends
                                            HljUploadResult>>() {
                                        @Override
                                        public Observable<? extends HljUploadResult> call(File file) {
                                            return new HljFileUploadBuilder(file).host(HljCard
                                                    .getCardHost())
                                                    .tokenPath(HljFileUploadBuilder
                                                            .QINIU_IMAGE_TOKEN)
                                                    .build();
                                        }
                                    })
                                    .map(new Func1<HljUploadResult, TextInfo>() {
                                        @Override
                                        public TextInfo call(HljUploadResult hljUploadResult) {
                                            isPageChanged = true;
                                            textInfo.setH5ImagePath(hljUploadResult.getUrl());
                                            File file = FileUtil.createPageFile
                                                    (PageTextEditActivity.this,
                                                    textInfo.getHoleId() + textInfo.getContent());
                                            if (FileUtil.isFileExists(file)) {
                                                boolean isRenamed = false;
                                                File pathFile = FileUtil.createPageFile(
                                                        PageTextEditActivity.this,
                                                        textInfo.getH5ImagePath());
                                                if (!FileUtil.isFileExists(pathFile)) {
                                                    isRenamed = file.renameTo(pathFile
                                                            .getAbsoluteFile());
                                                }
                                                if (!isRenamed) {
                                                    FileUtil.deleteFile(file);
                                                }
                                            }
                                            return textInfo;
                                        }
                                    })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread());
                        }
                    }
                })
                .map(new Func1<TextInfo, CardPage>() {
                    @Override
                    public CardPage call(TextInfo textInfo) {
                        page.setTextInfo(textInfo);
                        return page;
                    }
                })
                .concatMap(new Func1<CardPage, Observable<PageEditResult>>() {
                    @Override
                    public Observable<PageEditResult> call(CardPage cardPage) {
                        return CardApi.editPage(cardPage);
                    }
                })
                .doOnNext(new Action1<PageEditResult>() {
                    @Override
                    public void call(PageEditResult pageEditResult) {
                        if (pageEditResult.getCardPage() != null && isPageChanged) {
                            File pageFile = PageImageUtil.getPageThumbFile(PageTextEditActivity
                                            .this,
                                    pageEditResult.getCardPage()
                                            .getId());
                            if (pageFile != null && pageFile.exists()) {
                                FileUtil.deleteFile(pageFile);
                            }
                        }
                    }
                })
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setProgressDialog(DialogUtil.createProgressDialog(this))
                        .setOnNextListener(new SubscriberOnNextListener<PageEditResult>() {
                            @Override
                            public void onNext(PageEditResult pageEditResult) {
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("page_id", page.getId());
                                jsonObject.addProperty("id", textInfo.getHoleId());
                                jsonObject.addProperty("type", "text");
                                jsonObject.addProperty("img", textInfo.getH5ImagePath());
                                JsonArray array = new JsonArray();
                                array.add(jsonObject);
                                Intent intent = getIntent();
                                intent.putExtra("editResult", pageEditResult);
                                intent.putExtra("editHoleStr", array.toString());
                                setResult(RESULT_OK, intent);
                                onBackPressed();
                            }
                        })
                        .build());
    }


    private Observable<File> downloadFontObb() {
        return Observable.just(textHole)
                .map(new Func1<TextHole, Font>() {
                    @Override
                    public Font call(TextHole textHole) {
                        return FontUtil.getInstance()
                                .getFont(PageTextEditActivity.this, textHole.getFontName());
                    }
                })
                .filter(new Func1<Font, Boolean>() {
                    @Override
                    public Boolean call(Font font) {
                        if (font == null) {
                            return false;
                        }
                        File file = FileUtil.createFontFile(PageTextEditActivity.this,
                                font.getUrl());
                        return file == null || !file.exists() || file.length() == 0;
                    }
                })
                .concatMap(new Func1<Font, Observable<File>>() {
                    @Override
                    public Observable<File> call(Font font) {
                        return FileApi.download(font.getUrl(),
                                FileUtil.createFontFile(PageTextEditActivity.this, font.getUrl())
                                        .getAbsolutePath());
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()){
            CommonUtil.unSubscribeSubs(downloadSubscription, editSubscription);
        }
    }

    @Override
    protected void onDestroy() {
        CommonUtil.unSubscribeSubs(downloadSubscription, editSubscription);
        super.onDestroy();
    }
}
