package com.hunliji.marrybiz.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.hunliji.marrybiz.R;


/**
 * Created by Suncloud on 2014/12/5.
 */
public class MessageImageView extends ImageView {

    private Paint paint;
    private NinePatch ninePatch;

    public MessageImageView(Context context) {
        super(context);
        init(context, null);
    }

    public MessageImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MessageImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);


    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.MessageImageView);
            int mMaskSource = a.getResourceId(R.styleable.MessageImageView_mask, 0);
            if(mMaskSource>0) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mMaskSource);
                if(bitmap!=null) {
                    ninePatch = new NinePatch(bitmap, bitmap.getNinePatchChunk(), null);
                }
            }
        }
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }



    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bmp = Bitmap.createBitmap(getWidth()-getPaddingLeft()-getPaddingRight(), getHeight()-getPaddingTop()-getPaddingBottom(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bmp);
        super.onDraw(canvas2);
        if(ninePatch!=null) {
            Rect rectF = new Rect(getPaddingLeft(), getPaddingTop(), getWidth()-getPaddingRight(), getHeight()-getPaddingBottom());
            ninePatch.draw(canvas2, rectF, paint);
        }
        canvas.drawBitmap(bmp, 0, 0, null);
    }
}
