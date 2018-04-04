package com.hunliji.hljimagelibrary.views.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.R;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljimagelibrary.views.widgets.TouchMatrixImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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

public class ImageCropEditActivity extends HljBaseNoBarActivity {

    protected final int CHANGE_FILE_REQUEST = 1;

    private TouchMatrixImageView ivImage;
    private View cropAreaView;

    protected int width;
    protected int height;
    protected String outputPath;

    private int cropWidth;
    private int cropHeight;

    private int sourceMinWidth;
    private int sourceMinHeight;

    private Subscription cropSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop_edit___img);
        Point point = CommonUtil.getDeviceSize(this);
        sourceMinWidth = point.x / 2;
        sourceMinHeight = point.y / 2;

        initData();
        initView();
    }

    protected void initData() {
        width = getIntent().getIntExtra("output_width", 100);
        height = getIntent().getIntExtra("output_height", 100);
        outputPath = getIntent().getStringExtra("output_path");
        String sourcePath = getIntent().getStringExtra("source_path");
        if (!TextUtils.isEmpty(sourcePath)) {
            initSource(new File(sourcePath));
        }
    }

    private void initView() {
        ivImage = (TouchMatrixImageView) findViewById(R.id.iv_image);
        cropAreaView = findViewById(R.id.crop_area_view);
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.btn_change_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChange();
            }
        });
        findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirm();
            }
        });
        if (width > 0 && height > 0) {
            ivImage.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (ivImage.getWidth() > 0 && ivImage.getHeight() > 0) {
                                ivImage.getViewTreeObserver()
                                        .removeOnGlobalLayoutListener(this);
                                cropWidth = ivImage.getWidth();
                                cropHeight = ivImage.getHeight() - CommonUtil.dp2px(
                                        ImageCropEditActivity.this,
                                        88);
                                if (cropWidth * height < cropHeight * width) {
                                    cropHeight = Math.round(cropWidth * height / width);
                                } else {
                                    cropWidth = Math.round(cropHeight * width / height);
                                }
                                cropAreaView.getLayoutParams().height = cropHeight;
                                cropAreaView.getLayoutParams().width = cropWidth;
                                cropHeight -= cropAreaView.getPaddingTop() + cropAreaView
                                        .getPaddingBottom();
                                cropWidth -= cropAreaView.getPaddingLeft() + cropAreaView
                                        .getPaddingRight();
                                ivImage.setCropArea(cropWidth, cropHeight);
                            }
                        }
                    });
        }

    }

    protected void initSource(final File file) {
        Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                try {
                    Bitmap bitmap = Glide.with(ImageCropEditActivity.this)
                            .asBitmap()
                            .load(file)
                            .apply(new RequestOptions().skipMemoryCache(true)
                                    .format(DecodeFormat.PREFER_ARGB_8888)
                                    .override(Math.max(width, sourceMinWidth),
                                            Math.max(height, sourceMinHeight)))
                            .submit()
                            .get();
                    subscriber.onNext(bitmap);
                    subscriber.onCompleted();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        ivImage.setImageBitmap(bitmap);
                    }
                });
    }

    protected void onChange() {
        Intent intent = new Intent(this, ImageChooserActivity.class);
        intent.putExtra("limit", 1);
        startActivityForResult(intent, CHANGE_FILE_REQUEST);
    }

    private void onConfirm() {
        Bitmap bitmap = ivImage.getBitmap();
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        if (cropSubscription != null && !cropSubscription.isUnsubscribed()) {
            return;
        }
        cropSubscription = Observable.just(bitmap)
                .map(new Func1<Bitmap, Bitmap>() {
                    @Override
                    public Bitmap call(Bitmap bitmap) {
                        float scale = ((float) cropWidth) / width;
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        paint.setFilterBitmap(true);
                        Matrix matrix = new Matrix();
                        matrix.set(ivImage.getImageMatrix());
                        RectF rect = new RectF(0, 0, ivImage.getWidth(), ivImage.getHeight());
                        matrix.mapRect(rect);
                        float deltaX = -(ivImage.getWidth() - cropWidth) / 2;
                        float deltaY = -(ivImage.getHeight() - cropHeight) / 2;
                        matrix.postTranslate(deltaX, deltaY);
                        matrix.postScale(1 / scale, 1 / scale);
                        float[] m = new float[9];
                        matrix.getValues(m);
                        float mScale = (float) Math.sqrt(Math.pow(m[0], 2) + Math.pow(m[3], 2));
                        int outWidth;
                        int outHeight;
                        if (mScale > 1) {
                            outWidth = (int) (width / mScale);
                            outHeight = (int) (height / mScale);
                            matrix.postScale(1 / mScale, 1 / mScale);
                        } else {
                            outWidth = width;
                            outHeight = height;
                        }
                        Bitmap newBitmap = Bitmap.createBitmap(outWidth,
                                outHeight,
                                Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(newBitmap);
                        canvas.drawColor(Color.WHITE);
                        canvas.drawBitmap(bitmap, matrix, paint);
                        return newBitmap;
                    }
                })
                .map(new Func1<Bitmap, String>() {
                    @Override
                    public String call(Bitmap bitmap) {
                        try {
                            if (TextUtils.isEmpty(outputPath)) {
                                outputPath = FileUtil.createCropImageFile()
                                        .getAbsolutePath();
                            }
                            OutputStream out = new FileOutputStream(outputPath);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.close();
                            return outputPath;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (!bitmap.isRecycled()) {
                                bitmap.recycle();
                                System.gc();
                            }
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setProgressDialog(DialogUtil.createProgressDialog(this))
                        .setOnNextListener(

                                new SubscriberOnNextListener<String>() {
                                    @Override
                                    public void onNext(String outputPath) {
                                        onCropEditDone(outputPath);
                                    }
                                })
                        .build());
    }

    protected void onCropEditDone(String outputPath) {
        Intent intent = getIntent();
        intent.putExtra("output_path", outputPath);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }


    protected void resetFile(File file) {
        initSource(file);
    }

    protected void onImageResult(Photo photo) {
        resetFile(new File(photo.getImagePath()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHANGE_FILE_REQUEST:
                    ArrayList<Photo> selectedPhotos = data.getParcelableArrayListExtra(
                            "selectedPhotos");
                    if (!CommonUtil.isCollectionEmpty(selectedPhotos)) {
                        Photo photo = selectedPhotos.get(0);
                        if (!photo.isVideo()) {
                            onImageResult(selectedPhotos.get(0));
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(cropSubscription);
        super.onFinish();
    }
}
