package com.hunliji.hljvideolibrary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.LongSparseArray;
import android.view.View;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljvideolibrary.utils.BackgroundExecutor;
import com.hunliji.hljvideolibrary.utils.UiThreadExecutor;

/**
 * Created by luohanlin on 2017/6/20.
 */

public class HljTimeLineView extends View {

    private Context mContext;
    private int framesInScreen;
    private int timeLinePadding;
    private int thumbSize;
    private LongSparseArray<Bitmap> mBitmapList;
    private Uri mUri;

    public HljTimeLineView(Context context) {
        super(context);
    }

    public HljTimeLineView(
            Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HljTimeLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        this.mContext = context;
        initValues();

        initViews();
    }



    private void initValues() {
    }

    private void initViews() {

    }

    public void setUri(Uri uri) {
        this.mUri = uri;
    }

    private void generatesBitmaps() {
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0L, "") {
            @Override
            public void execute() {
                try {
                    LongSparseArray<Bitmap> thumbnailList = new LongSparseArray<>();

                    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(getContext(), mUri);

                    long videoLengthInMinutes = Integer.parseInt(mediaMetadataRetriever
                            .extractMetadata(
                                    MediaMetadataRetriever.METADATA_KEY_DURATION));
                    for (int i = 0; i < videoLengthInMinutes; i++) {
                        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(i * 1000,
                                MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                        try {
                            bitmap = centerCrop(bitmap);
                            bitmap = Bitmap.createScaledBitmap(bitmap, thumbSize, thumbSize, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        thumbnailList.put(i, bitmap);
                    }
                    mediaMetadataRetriever.release();

                    invalidateDataAndViews(thumbnailList);
                } catch (final Throwable e) {
                    Thread.getDefaultUncaughtExceptionHandler()
                            .uncaughtException(Thread.currentThread(), e);
                }
            }
        });

    }

    private void invalidateDataAndViews(final LongSparseArray<Bitmap> thumbnailList) {
        UiThreadExecutor.runTask("", new Runnable() {
            @Override
            public void run() {
                mBitmapList = thumbnailList;
                invalidate();
            }
        }, 0L);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmapList != null) {

        }
    }

    @Override
    protected void onSizeChanged(final int w, int h, final int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        if (w != oldW) {
            thumbSize = (CommonUtil.getDeviceSize(mContext).x - CommonUtil.dp2px(mContext,
                    getPaddingLeft() + getPaddingRight())) / framesInScreen;
            generatesBitmaps();
        }
    }

    /**
     * 按正方形裁切图片
     */
    public static Bitmap centerCrop(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int wh = w > h ? h : w;// 裁切后所取的正方形区域边长
        int retX = w > h ? (w - h) / 2 : 0;//基于原图，取正方形左上角x坐标
        int retY = w > h ? 0 : (h - w) / 2;

        return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
    }
}
