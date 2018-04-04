package com.hunliji.hljvideolibrary.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljvideolibrary.R;

import java.util.List;
import java.util.Vector;


public class Thumb {

    private Context context;
    private int mIndex;
    private float mVal;
    private float mPos;
    private Bitmap mBitmap;
    private int mWidthBitmap;
    private int mHeightBitmap;
    private float mLastTouchX;

    private final static int THUMP_WIDTH = 20;

    private Thumb(Context c) {
        this.context = c;
        mVal = 0;
        mPos = 0;
    }

    public int getIndex() {
        return mIndex;
    }

    private void setIndex(int index) {
        mIndex = index;
    }

    public float getVal() {
        return mVal;
    }

    public void setVal(float val) {
        mVal = val;
    }

    public float getPos() {
        return mPos;
    }

    public void setPos(float pos) {
        mPos = pos;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    private void setBitmap(@NonNull Bitmap bitmap) {
        mBitmap = bitmap;
        mWidthBitmap = CommonUtil.dp2px(this.context, THUMP_WIDTH);
        mHeightBitmap = CommonUtil.dp2px(this.context, TimeLineView.TIME_LINE_VIEW_HEIGHT_DP);
    }

    @NonNull
    public static List<Thumb> initThumbs(Resources resources, Context context) {
        List<Thumb> thumbs = new Vector<>();

        for (int i = 0; i < 2; i++) {
            Thumb th = new Thumb(context);
            th.setIndex(i);
            if (i == 0) {
                int resImageLeft = R.drawable.sp_video_trim_cursor_left;
                th.setBitmap(getBitmap(resImageLeft, context));
            } else {
                int resImageRight = R.drawable.sp_video_trim_cursor_right;
                th.setBitmap(getBitmap(resImageRight, context));
            }

            thumbs.add(th);
        }

        return thumbs;
    }

    private static Bitmap getBitmap(int drawableRes, Context context) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static int getWidthBitmap(@NonNull List<Thumb> thumbs) {
        return thumbs.get(0)
                .getWidthBitmap();
    }

    public static int getHeightBitmap(@NonNull List<Thumb> thumbs) {
        return thumbs.get(0)
                .getHeightBitmap();
    }

    public float getLastTouchX() {
        return mLastTouchX;
    }

    public void setLastTouchX(float lastTouchX) {
        mLastTouchX = lastTouchX;
    }

    public int getWidthBitmap() {
        return mWidthBitmap;
    }

    private int getHeightBitmap() {
        return mHeightBitmap;
    }
}
