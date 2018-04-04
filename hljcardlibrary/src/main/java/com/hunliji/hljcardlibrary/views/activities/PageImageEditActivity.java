package com.hunliji.hljcardlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.CardPage;
import com.hunliji.hljcardlibrary.models.CardRxEvent;
import com.hunliji.hljcardlibrary.models.ImageHole;
import com.hunliji.hljcardlibrary.models.ImageInfo;
import com.hunliji.hljcardlibrary.models.wrappers.PageEditResult;
import com.hunliji.hljcardlibrary.utils.PageImageUtil;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljhttplibrary.entities.HljUploadResult;
import com.hunliji.hljhttplibrary.utils.HljFileUploadBuilder;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.views.activities.ImageCropEditActivity;

import java.io.File;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by wangtao on 2017/6/27.
 */

public class PageImageEditActivity extends ImageCropEditActivity {

    private CardPage page;
    private ImageInfo imageInfo;

    private Subscription editSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {
        page = getIntent().getParcelableExtra("page");
        ImageHole imageHole = getIntent().getParcelableExtra("imageHole");
        imageInfo = getIntent().getParcelableExtra("imageInfo");
        Photo photo = getIntent().getParcelableExtra("photo");
        if (imageInfo == null) {
            imageInfo = new ImageInfo(imageHole);
        }
        width = (int) (imageHole.getHoleFrame()
                .getWidth() * PageImageUtil.CREATE_SCALE);
        height = (int) (imageHole.getHoleFrame()
                .getHeight() * PageImageUtil.CREATE_SCALE);
        outputPath = FileUtil.createPageFile(this, "temp_h5_image_path", "jpg")
                .getAbsolutePath();
        initSource(new File(photo.getImagePath()));
    }

    @Override
    protected void onCropEditDone(String outputPath) {
        editSubscription = uploadCropObb(outputPath).map(new Func1<ImageInfo, CardPage>() {
            @Override
            public CardPage call(ImageInfo imageInfo) {
                page.setImageInfo(imageInfo);
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
                        if (pageEditResult.getCardPage() != null) {
                            File pageFile = PageImageUtil.getPageThumbFile(PageImageEditActivity
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
                                jsonObject.addProperty("id", imageInfo.getHoleId());
                                jsonObject.addProperty("type", "image");
                                jsonObject.addProperty("img", imageInfo.getH5ImagePath());
                                JsonArray array = new JsonArray();
                                array.add(jsonObject);

                                pageEditResult.setH5PageStr(array.toString());
                                RxBus.getDefault()
                                        .post(new CardRxEvent(CardRxEvent.RxEventType
                                                .PAGE_IMAGE_EDIT,
                                                pageEditResult));
                                onBackPressed();
                            }
                        })
                        .build());
    }


    private Observable<ImageInfo> uploadCropObb(final String outputPath) {
        return new HljFileUploadBuilder(new File(outputPath)).host(HljCard.getCardHost())
                .compress(this, 70)
                .tokenPath(HljFileUploadBuilder.QINIU_IMAGE_TOKEN)
                .build()
                .map(new Func1<HljUploadResult, ImageInfo>() {
                    @Override
                    public ImageInfo call(HljUploadResult hljUploadResult) {
                        imageInfo.setH5ImagePath(hljUploadResult.getUrl());
                        FileUtil.copyFile(outputPath,
                                FileUtil.createPageFile(PageImageEditActivity.this,
                                        hljUploadResult.getUrl())
                                        .getAbsolutePath());
                        return imageInfo;
                    }
                });
    }

    @Override
    protected void resetFile(File file) {
        imageInfo.setPath(file.getAbsolutePath());
        super.resetFile(file);
    }


    @Override
    protected void onChange() {
        Intent intent = new Intent(this, PageImageChooserActivity.class);
        intent.putExtra("result", true);
        startActivityForResult(intent, CHANGE_FILE_REQUEST);
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(editSubscription);
        super.onFinish();
    }
}
