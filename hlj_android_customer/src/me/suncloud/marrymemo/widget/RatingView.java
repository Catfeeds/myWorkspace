package me.suncloud.marrymemo.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import me.suncloud.marrymemo.R;

/**
 * 显示半星的时候 不支持点击
 * Created by Suncloud on 2015/1/16.
 * edit b jinxin 2016/11/23
 */
public class RatingView extends LinearLayout implements View.OnClickListener {

    public ImageButton ratingView1;
    public ImageButton ratingView2;
    public ImageButton ratingView3;
    public ImageButton ratingView4;
    public ImageButton ratingView5;
    public OnRatingChangeListener onRatingChangeListener;
    public int rating;

    private Drawable offDrawable;
    private Drawable onDrawable;
    private Drawable halfDrawable;
    private int itemMargin;
    private int itemHeigth;
    private boolean isindicator;
    private boolean isHalf;//是否显示半星 最大ratting 10

    private int tintOffColor;
    private int tintOnColor;

    public RatingView(Context context) {
        this(context, null);
    }

    public RatingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RatingView(
            Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context mContext, AttributeSet attrs) {
        offDrawable = getResources().getDrawable(R.drawable.icon_rating_off_88_58);
        onDrawable = getResources().getDrawable(R.drawable.icon_rating_on_88_58);

        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.RatingView);
        offDrawable = array.getDrawable(R.styleable.RatingView_offDrawable);
        onDrawable = array.getDrawable(R.styleable.RatingView_onDrawable);
        halfDrawable = array.getDrawable(R.styleable.RatingView_halfDrawable);
        itemMargin = (int) array.getDimension(R.styleable.RatingView_itemMargin, 0);
        itemHeigth = (int) array.getDimension(R.styleable.RatingView_itemHeight, 32);
        isindicator = array.getBoolean(R.styleable.RatingView_isindicator, false);
        isHalf = array.getBoolean(R.styleable.RatingView_isHalf, false);
        try {
            tintOffColor = array.getColor(R.styleable.RatingView_tintOff, 0);
        } catch (Exception e) {
        }
        try {
            tintOnColor = array.getColor(R.styleable.RatingView_tintOn, 0);
        } catch (Exception e) {
        }
        array.recycle();

        if (offDrawable != null && tintOffColor != 0) {
            offDrawable.mutate().setColorFilter(tintOffColor, PorterDuff.Mode.SRC_IN);
        }

        if (onDrawable != null && tintOnColor != 0) {
            onDrawable.mutate().setColorFilter(tintOnColor, PorterDuff.Mode.SRC_IN);
        }

        View view = inflate(mContext, R.layout.rating_layout, this);
        ratingView1 = (ImageButton) view.findViewById(R.id.rating1);
        ratingView2 = (ImageButton) view.findViewById(R.id.rating2);
        ratingView3 = (ImageButton) view.findViewById(R.id.rating3);
        ratingView4 = (ImageButton) view.findViewById(R.id.rating4);
        ratingView5 = (ImageButton) view.findViewById(R.id.rating5);
        if (ratingView1 != null) {
            ratingView1.setOnClickListener(this);
            ratingView1.setImageDrawable(offDrawable);
            ViewGroup.LayoutParams params = ratingView1.getLayoutParams();
            if (params != null) {
                params.height = itemHeigth;
            }
        }
        if (ratingView2 != null) {
            ratingView2.setOnClickListener(this);
            ratingView2.setImageDrawable(offDrawable);
            ViewGroup.LayoutParams params = ratingView2.getLayoutParams();
            if (params != null) {
                params.height = itemHeigth;
            }
        }
        if (ratingView3 != null) {
            ratingView3.setOnClickListener(this);
            ratingView3.setImageDrawable(offDrawable);
            ViewGroup.LayoutParams params = ratingView3.getLayoutParams();
            if (params != null) {
                params.height = itemHeigth;
            }
        }
        if (ratingView4 != null) {
            ratingView4.setOnClickListener(this);
            ratingView4.setImageDrawable(offDrawable);
            ViewGroup.LayoutParams params = ratingView4.getLayoutParams();
            if (params != null) {
                params.height = itemHeigth;
            }
        }
        if (ratingView5 != null) {
            ratingView5.setOnClickListener(this);
            ratingView5.setImageDrawable(offDrawable);
            ViewGroup.LayoutParams params = ratingView5.getLayoutParams();
            if (params != null) {
                params.height = itemHeigth;
            }
        }
    }

    @Override
    public void onClick(View v) {
        //显示半星的时候 不支持点击
        if (isindicator || isHalf) {
            return;
        }
        switch (v.getId()) {
            case R.id.rating1:
                rating = 1;
                break;
            case R.id.rating2:
                rating = 2;
                break;
            case R.id.rating3:
                rating = 3;
                break;
            case R.id.rating4:
                rating = 4;
                break;
            case R.id.rating5:
                rating = 5;
                break;
            default:
                break;
        }
        if (onRatingChangeListener != null) {
            onRatingChangeListener.onRatingChanged(rating);
        }
        ratingOff(rating);
        ratingOn(rating);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        int height = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.measure(child.getMeasuredWidth(),
                    MeasureSpec.makeMeasureSpec(itemHeigth, MeasureSpec.EXACTLY));
            width += child.getMeasuredWidth();
            height = Math.max(height, child.getMeasuredHeight());
        }
        setMeasuredDimension(width + (childCount - 1) * itemMargin, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                if (child != null) {
                    int left = i * (width + itemMargin);
                    int right = left + +width;
                    child.layout(left, 0, right, height);
                }
            }
        }
    }

    public void setRating(int rating) {
        this.rating = rating;
        if (onRatingChangeListener != null) {
            onRatingChangeListener.onRatingChanged(rating);
        }
        ratingOff(rating);
        ratingOn(rating);
    }

    public int getRating() {
        return rating;
    }

    private void ratingOff(int rating) {
        if (isHalf) {
            rating = 0;
        }
        // 最少一颗星
        switch (rating) {
            case 0:
                if (ratingView1.getTag() != null && (Boolean) ratingView1.getTag()) {
                    ratingView1.setImageDrawable(offDrawable);
                    ratingView1.setTag(false);
                }
            case 1:
                if (ratingView2.getTag() != null && (Boolean) ratingView2.getTag()) {
                    ratingView2.setImageDrawable(offDrawable);
                    ratingView2.setTag(false);
                }
            case 2:
                if (ratingView3.getTag() != null && (Boolean) ratingView3.getTag()) {
                    ratingView3.setImageDrawable(offDrawable);
                    ratingView3.setTag(false);
                }
            case 3:
                if (ratingView4.getTag() != null && (Boolean) ratingView4.getTag()) {
                    ratingView4.setImageDrawable(offDrawable);
                    ratingView4.setTag(false);
                }
            case 4:
                if (ratingView5.getTag() != null && (Boolean) ratingView5.getTag()) {
                    ratingView5.setImageDrawable(offDrawable);
                    ratingView5.setTag(false);
                }
                break;

        }
    }

    private void ratingOn(int rating) {
        int mode = 0;
        if (isHalf) {
            mode = rating % 2;
            rating = rating / 2;
        }
        switch (rating) {
            case 5:
                if (ratingView5.getTag() == null || !(Boolean) ratingView5.getTag()) {
                    ratingView5.setImageDrawable(onDrawable);
                    ratingView5.setTag(true);
                }
            case 4:
                if (ratingView4.getTag() == null || !(Boolean) ratingView4.getTag()) {
                    ratingView4.setImageDrawable(onDrawable);
                    ratingView4.setTag(true);
                }
                if (mode != 0) {
                    //9
                    if (ratingView5.getTag() == null || !(Boolean) ratingView5.getTag()) {
                        ratingView5.setImageDrawable(halfDrawable);
                        ratingView5.setTag(true);
                    }
                }
            case 3:
                if (ratingView3.getTag() == null || !(Boolean) ratingView3.getTag()) {
                    ratingView3.setImageDrawable(onDrawable);
                    ratingView3.setTag(true);
                }
                if (mode != 0) {
                    //7
                    if (ratingView4.getTag() == null || !(Boolean) ratingView4.getTag()) {
                        ratingView4.setImageDrawable(halfDrawable);
                        ratingView4.setTag(true);
                    }
                }
            case 2:
                if (ratingView2.getTag() == null || !(Boolean) ratingView2.getTag()) {
                    ratingView2.setImageDrawable(onDrawable);
                    ratingView2.setTag(true);
                }
                if (mode != 0) {
                    //5
                    if (ratingView3.getTag() == null || !(Boolean) ratingView3.getTag()) {
                        ratingView3.setImageDrawable(halfDrawable);
                        ratingView3.setTag(true);
                    }
                }
            case 1:
                if (ratingView1.getTag() == null || !(Boolean) ratingView1.getTag()) {
                    ratingView1.setImageDrawable(onDrawable);
                    ratingView1.setTag(true);
                }
                if (mode != 0) {
                    //5
                    if (ratingView2.getTag() == null || !(Boolean) ratingView2.getTag()) {
                        ratingView2.setImageDrawable(halfDrawable);
                        ratingView2.setTag(true);
                    }
                }

            case 0:
                if (mode != 0) {
                    //1
                    if (ratingView1.getTag() == null || !(Boolean) ratingView1.getTag()) {
                        ratingView1.setImageDrawable(halfDrawable);
                        ratingView1.setTag(true);
                    }
                }
                break;

        }
    }

    public interface OnRatingChangeListener {
        public void onRatingChanged(int rating);
    }

    public void setOnRatingChangeListener(
            OnRatingChangeListener onRatingChangeListener) {
        this.onRatingChangeListener = onRatingChangeListener;
    }
}
