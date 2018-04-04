package com.hunliji.hljcardlibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.text.TextUtils;

import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.CardPage;
import com.hunliji.hljcardlibrary.models.CardRxEvent;
import com.hunliji.hljcardlibrary.models.ImageHole;
import com.hunliji.hljcardlibrary.models.ImageInfo;
import com.hunliji.hljcardlibrary.models.Template;
import com.hunliji.hljcardlibrary.models.TextHole;
import com.hunliji.hljcardlibrary.models.TextInfo;
import com.hunliji.hljcardlibrary.models.wrappers.PageEditResult;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljHorizontalProgressDialog;
import com.hunliji.hljhttplibrary.api.FileApi;
import com.hunliji.hljhttplibrary.entities.HljUploadListener;
import com.hunliji.hljhttplibrary.entities.HljUploadResult;
import com.hunliji.hljhttplibrary.utils.HljFileUploadBuilder;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/7/4.
 */

public class CardEditObbUtil {


    public static Observable<ImageInfo> uploadVideoInfoObb(
            final ImageInfo imageInfo,
            final File videoFile,
            final HljHorizontalProgressDialog progressBar) {
        return new HljFileUploadBuilder(videoFile).host(HljCard.getCardHost())
                .tokenPath(HljFileUploadBuilder.QINIU_VIDEO_URL,
                        HljFileUploadBuilder.UploadFrom.CARD)
                .progressListener(new HljUploadListener() {
                    @Override
                    public void transferred(long transBytes) {
                        if (progressBar != null) {
                            progressBar.setTransBytes(transBytes);
                        }
                    }

                    @Override
                    public void setContentLength(long contentLength) {
                        if (progressBar != null) {
                            progressBar.setContentLength(contentLength);
                        }
                    }
                })
                .build()
                .map(new Func1<HljUploadResult, ImageInfo>() {
                    @Override
                    public ImageInfo call(HljUploadResult hljUploadResult) {
                        imageInfo.setPath(hljUploadResult.getUrl());
                        imageInfo.setH5ImagePath(null);
                        imageInfo.setPersistentId(hljUploadResult.getPersistentId());
                        return imageInfo;
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (progressBar != null) {
                            progressBar.show();
                        }
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (progressBar != null) {
                            progressBar.dismiss();
                        }
                    }
                });
    }

    public static Observable<PageEditResult> uploadPageForVideoObb(
            final Context context,
            final CardPage page,
            ImageHole imageHole,
            ImageInfo imageInfo,
            final Photo photo) {
        if (imageInfo == null) {
            imageInfo = new ImageInfo(imageHole);
        }
        imageInfo.setVideo(true);
        imageInfo.setVideoHeight(photo.getHeight());
        imageInfo.setVideoWidth(photo.getWidth());
        final ImageInfo finalImageInfo = imageInfo;
        HljHorizontalProgressDialog dialog = DialogUtil.createUploadDialog(context);
        dialog.setTotalCount(1);
        dialog.setCurrentIndex(0);
        dialog.setCancelable(false);
        dialog.setTitle("请帖视频上传中...");
        return uploadVideoInfoObb(finalImageInfo,
                new File(photo.getImagePath()),
                dialog).map(new Func1<ImageInfo, CardPage>() {
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
                            File pageFile = PageImageUtil.getPageThumbFile(context,
                                    pageEditResult.getCardPage()
                                            .getId());
                            if (pageFile != null && pageFile.exists()) {
                                FileUtil.deleteFile(pageFile);
                            }
                        }
                        pageEditResult.setH5PageStr(String.format("\'%s\',%s,%s",
                                finalImageInfo.getPath(),
                                photo.getWidth(),
                                photo.getHeight()));
                        RxBus.getDefault()
                                .post(new CardRxEvent(CardRxEvent.RxEventType.PAGE_VIDEO_EDIT,
                                        pageEditResult));
                    }
                });
    }

    public static Subscription createPageThumbObb(
            final Context context,
            final CardPage cardPage,
            final boolean isFrontPage,
            final boolean isSpeechPage) {
        return Observable.just(cardPage)
                .filter(new Func1<CardPage, Boolean>() {
                    @Override
                    public Boolean call(CardPage cardPage) {
                        if (!isFrontPage && !isSpeechPage) {
                            if (cardPage.getCreatedAt() != null && cardPage.getUpdatedAt() !=
                                    null && cardPage.getCreatedAt()
                                    .equals(cardPage.getUpdatedAt())) {
                                return false;
                            }
                        }
                        File pageFile = PageImageUtil.getPageThumbFile(context, cardPage.getId());
                        if (!FileUtil.isFileExists(pageFile)) {
                            return true;
                        } else if (isFrontPage) {
                            File cardFile = PageImageUtil.getCardThumbFile(context,
                                    cardPage.getCardId());
                            if (!FileUtil.isFileExists(cardFile)) {
                                PageImageUtil.editCardThumbList(context,
                                        cardPage.getCardId(),
                                        pageFile.getAbsolutePath());
                            }
                        }
                        return false;
                    }
                })
                .map(new Func1<CardPage, Template>() {
                    @Override
                    public Template call(CardPage page) {
                        return cardPage.getTemplate();
                    }
                })
                .filter(new Func1<Template, Boolean>() {
                    @Override
                    public Boolean call(Template template) {
                        return template != null && template.getWidth() > 0 && template.getHeight
                                () > 0;
                    }
                })
                .concatMap(new Func1<Template, Observable<CardPage>>() {
                    @Override
                    public Observable<CardPage> call(Template template) {
                        return templateImageDownloadObb(context,
                                template).map(new Func1<List<File>, CardPage>() {
                            @Override
                            public CardPage call(List<File> files) {
                                return cardPage;
                            }
                        });
                    }
                })
                .concatMap(new Func1<CardPage, Observable<CardPage>>() {
                    @Override
                    public Observable<CardPage> call(final CardPage cardPage) {
                        return pageImageDownloadObb(context,
                                cardPage).map(new Func1<List<File>, CardPage>() {
                            @Override
                            public CardPage call(List<File> files) {
                                return cardPage;
                            }
                        });
                    }
                })
                .concatMap(new Func1<CardPage, Observable<Bitmap>>() {
                    @Override
                    public Observable<Bitmap> call(CardPage cardPage) {
                        return drawPageThumbObb(context, cardPage);
                    }
                })
                .filter(new Func1<Bitmap, Boolean>() {
                    @Override
                    public Boolean call(Bitmap bitmap) {
                        return bitmap != null;
                    }
                })
                .map(new Func1<Bitmap, String>() {
                    @Override
                    public String call(Bitmap bitmap) {
                        File pageFile = FileUtil.createPageFile(context,
                                String.valueOf(System.currentTimeMillis()));
                        try {
                            OutputStream out = new FileOutputStream(pageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.close();
                            bitmap.recycle();
                            PageImageUtil.editPageThumbList(context,
                                    cardPage.getId(),
                                    pageFile.getAbsolutePath());
                            if (isFrontPage) {
                                PageImageUtil.editCardThumbList(context,
                                        cardPage.getCardId(),
                                        pageFile.getAbsolutePath());
                                RxBus.getDefault()
                                        .post(new CardRxEvent(CardRxEvent.RxEventType
                                                .CARD_THUMB_UPDATE,
                                                cardPage.getCardId()));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return pageFile.getAbsolutePath();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(String s) {
                    }
                });
    }


    public static Observable<List<File>> templateImageDownloadObb(
            final Context context, Template template) {
        return Observable.from(template.getImagePaths(context))
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String path) {
                        File file = FileUtil.createThemeFile(context, path);
                        return file == null || !file.exists() || file.length() == 0;
                    }
                })
                .concatMap(new Func1<String, Observable<File>>() {
                    @Override
                    public Observable<File> call(String path) {
                        return FileApi.download(path,
                                FileUtil.createThemeFile(context, path)
                                        .getAbsolutePath());
                    }
                })
                .toList();
    }

    public static Observable<List<File>> pageImageDownloadObb(
            final Context context, CardPage cardPage) {
        return Observable.from(cardPage.getImagePaths())
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String path) {
                        File file = FileUtil.createPageFile(context, path);
                        return file == null || !file.exists() || file.length() == 0;
                    }
                })
                .concatMap(new Func1<String, Observable<File>>() {
                    @Override
                    public Observable<File> call(String path) {
                        return FileApi.download(path,
                                FileUtil.createPageFile(context, path)
                                        .getAbsolutePath());
                    }
                })
                .toList();
    }

    public static Observable<Bitmap> drawPageThumbObb(
            final Context context, final CardPage cardPage) {
        return Observable.just(cardPage)
                .map(new Func1<CardPage, Bitmap>() {
                    @Override
                    public Bitmap call(CardPage page) {
                        Template template = page.getTemplate();
                        if (template == null || template.getWidth() == 0 || template.getHeight()
                                == 0) {
                            return null;
                        }
                        try {
                            Bitmap pageBitmap = Bitmap.createBitmap(template.getWidth(),
                                    template.getHeight(),
                                    Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(pageBitmap);
                            for (ImageHole imageHole : template.getSortImageHoles()) {
                                ImageInfo imageInfo = null;
                                for (ImageInfo info : cardPage.getImageInfos()) {
                                    if (info.getHoleId() == imageHole.getId()) {
                                        imageInfo = info;
                                        break;
                                    }
                                }
                                if (imageInfo == null) {
                                    continue;
                                }
                                String imagePath = null;
                                String maskPath = null;
                                if (imageInfo.isVideo()) {
                                    if (!TextUtils.isEmpty(imageInfo.getPath())) {
                                        imagePath = imageInfo.getPath() + HljCommon.QINIU
                                                .SCREEN_SHOT_URL_0_SECONDS;
                                    }
                                } else {
                                    imagePath = imageInfo.getH5ImagePath();
                                    maskPath = imageHole.getMaskImagePath();
                                }
                                if (TextUtils.isEmpty(imagePath)) {
                                    continue;
                                }
                                File file = FileUtil.createPageFile(context, imagePath);
                                if (file == null || !file.exists() || file.length() == 0) {
                                    continue;
                                }
                                Bitmap bitmap = ImageUtil.getImageFromPath(context
                                                .getContentResolver(),
                                        file.getAbsolutePath(),
                                        imageHole.getHoleFrame()
                                                .getWidth(),
                                        imageHole.getHoleFrame()
                                                .getHeight(),
                                        Bitmap.Config.RGB_565);
                                if (bitmap == null) {
                                    continue;
                                }
                                Bitmap newBitmap = Bitmap.createBitmap(imageHole.getHoleFrame()
                                                .getWidth(),
                                        imageHole.getHoleFrame()
                                                .getHeight(),
                                        Bitmap.Config.ARGB_8888);
                                Canvas imageCanvas = new Canvas(newBitmap);
                                float scale = Math.max((float) newBitmap.getWidth() / bitmap
                                                .getWidth(),
                                        (float) newBitmap.getHeight() / bitmap.getHeight());
                                if (scale != 1) {
                                    Matrix matrix = new Matrix();
                                    matrix.postScale(scale, scale);
                                    matrix.postTranslate((newBitmap.getWidth() - bitmap.getWidth
                                                    () * scale) / 2,
                                            (newBitmap.getHeight() - bitmap.getHeight() * scale)
                                                    / 2);
                                    imageCanvas.drawBitmap(bitmap, matrix, null);
                                } else {
                                    imageCanvas.drawBitmap(bitmap, 0, 0, null);
                                }
                                if (!TextUtils.isEmpty(maskPath)) {
                                    file = FileUtil.createThemeFile(context, maskPath);
                                    Bitmap maskBitmap = ImageUtil.getImageFromPath(context
                                                    .getContentResolver(),

                                            file.getAbsolutePath(),
                                            imageHole.getHoleFrame()
                                                    .getWidth(),
                                            imageHole.getHoleFrame()
                                                    .getHeight(),
                                            Bitmap.Config.ALPHA_8);
                                    if (maskBitmap != null) {
                                        Rect rectF = new Rect(0,
                                                0,
                                                imageHole.getHoleFrame()
                                                        .getWidth(),
                                                imageHole.getHoleFrame()
                                                        .getHeight());
                                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                                        paint.setAntiAlias(true);
                                        paint.setFilterBitmap(true);
                                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode
                                                .DST_IN));
                                        imageCanvas.drawBitmap(maskBitmap, null, rectF, paint);
                                        maskBitmap.recycle();
                                    }
                                }
                                canvas.drawBitmap(newBitmap,
                                        imageHole.getHoleFrame()
                                                .getX(),
                                        imageHole.getHoleFrame()
                                                .getY(),
                                        null);
                                newBitmap.recycle();
                                Paint clearPaint = new Paint();
                                clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode
                                        .CLEAR));
                                imageCanvas.drawPaint(clearPaint);
                            }
                            String backgroundPath = template.getBackground();
                            if (!TextUtils.isEmpty(backgroundPath)) {
                                File file = FileUtil.createThemeFile(context, backgroundPath);
                                Bitmap bitmap = ImageUtil.getImageFromPath(context
                                                .getContentResolver(),
                                        file.getAbsolutePath(),
                                        template.getWidth(),
                                        template.getHeight(),
                                        Bitmap.Config.ARGB_8888);
                                if (bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight()
                                        > 0) {
                                    float scale = Math.max((float) pageBitmap.getWidth() / bitmap
                                                    .getWidth(),
                                            (float) pageBitmap.getHeight() / bitmap.getHeight());
                                    if (scale != 1) {
                                        Matrix matrix = new Matrix();
                                        matrix.postScale(scale, scale);
                                        matrix.postTranslate((pageBitmap.getWidth() - bitmap
                                                        .getWidth() * scale) / 2,
                                                (pageBitmap.getHeight() - bitmap.getHeight() *
                                                        scale) / 2);
                                        canvas.drawBitmap(bitmap, matrix, null);
                                    } else {
                                        canvas.drawBitmap(bitmap, 0, 0, null);
                                    }
                                    bitmap.recycle();
                                }
                            }

                            for (TextHole textHole : template.getSortTextHoles()) {
                                TextInfo textInfo = null;
                                for (TextInfo info : page.getTextInfos()) {
                                    if (textHole.getId() == info.getHoleId()) {
                                        textInfo = info;
                                        break;
                                    }
                                }
                                if (textInfo == null) {
                                    continue;
                                }
                                String imagePath = textInfo.getH5ImagePath();
                                if (TextUtils.isEmpty(imagePath)) {
                                    continue;
                                }
                                File file = FileUtil.createPageFile(context, imagePath);
                                if (file == null || !file.exists() || file.length() == 0) {
                                    continue;
                                }
                                Bitmap bitmap = ImageUtil.getImageFromPath(context
                                                .getContentResolver(),
                                        file.getAbsolutePath(),
                                        textHole.getHoleFrame()
                                                .getWidth(),
                                        textHole.getHoleFrame()
                                                .getHeight(),
                                        Bitmap.Config.ARGB_8888);
                                if (bitmap == null) {
                                    continue;
                                }

                                Bitmap newBitmap = Bitmap.createBitmap(textHole.getHoleFrame()
                                                .getWidth(),
                                        textHole.getHoleFrame()
                                                .getHeight(),
                                        Bitmap.Config.ARGB_8888);
                                Canvas imageCanvas = new Canvas(newBitmap);
                                float scale = Math.max((float) newBitmap.getWidth() / bitmap
                                                .getWidth(),
                                        (float) newBitmap.getHeight() / bitmap.getHeight());
                                if (scale != 1) {
                                    Matrix matrix = new Matrix();
                                    matrix.postScale(scale, scale);
                                    imageCanvas.drawBitmap(bitmap, matrix, null);
                                } else {
                                    imageCanvas.drawBitmap(bitmap, 0, 0, null);
                                }
                                canvas.drawBitmap(newBitmap,
                                        textHole.getHoleFrame()
                                                .getX(),
                                        textHole.getHoleFrame()
                                                .getY(),
                                        null);
                                newBitmap.recycle();
                                Paint clearPaint = new Paint();
                                clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode
                                        .CLEAR));
                                imageCanvas.drawPaint(clearPaint);
                            }
                            return pageBitmap;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
    }
}
