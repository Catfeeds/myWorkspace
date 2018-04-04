package com.hunliji.hljnotelibrary.views.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Property;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.adapters.TagAdapter;
import com.hunliji.hljnotelibrary.interfaces.ITagView;
import com.hunliji.hljnotelibrary.models.Direction;

import java.util.ArrayList;

public class TagViewGroup extends ViewGroup {

    public static final int DEFAULT_RADIUS = 8;//默认外圆半径
    public static final int DEFAULT_INNER_RADIUS = 4;//默认内圆半径
    public static final int DEFAULT_TOUCH_SLOP = 7;
    public static final int DEFAULT_V_DISTANCE = 28;//默认竖直(上/下)方向线条长度
    public static final int DEFAULT_TILT_DISTANCE = 20;//默认斜线长度
    public static final int DEFAULT_LINES_WIDTH = 1;//默认线宽
    public static final int DEFAULT_RIPPLE_MAX_RADIUS = 20;//水波纹默认最大半径
    public static final int DEFAULT_RIPPLE_ALPHA = 100;//默认水波纹起始透明度

    private static class ItemInfo {
        ITagView item;
        int position;
        RectF rectF = new RectF();
    }

    private Paint mPaint;
    private Path mPath;
    private Path mDstPath;
    private PathMeasure mPathMeasure;
    private Animator mShowAnimator;
    private Animator mHideAnimator;
    private TagAdapter mAdapter;
    private GestureDetectorCompat mGestureDetector;
    private OnTagGroupClickListener mClickListener;
    private OnTagGroupDragListener mScrollListener;
    private final TagSetObserver mObserver = new TagSetObserver();

    private RippleView mRippleView;
    private int mRippleMaxRadius;//水波纹最大半径
    private int mRippleMinRadius;//水波纹最小半径
    private int mRippleAlpha;//默认水波纹起始透明度
    private int mRadius;//外圆半径
    private int mInnerRadius;//内圆半径
    private int mTouchSlop; //圆心点击区域扩大
    private int mTDistance;//斜线长度
    private int mVDistance;//竖直(上/下)方向线条长度
    private float mTagAlpha;//Tag标签的透明度
    private RectF mCenterRect;
    private ArrayList<ItemInfo> mItems = new ArrayList<>();
    private int[] mChildUsed;
    private int mCenterX;//圆心 X 坐标
    private int mCenterY;//圆心 Y 坐标
    private float mPercentX;
    private float mPercentY;
    private int mLinesWidth;//线条宽度
    private float mLinesRatio = 1;

    public TagViewGroup(Context context) {
        this(context, null);
    }

    public TagViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        Resources.Theme theme = context.getTheme();
        TypedArray array = theme.obtainStyledAttributes(attrs,
                R.styleable.TagViewGroup,
                defStyleAttr,
                0);
        mRadius = array.getDimensionPixelSize(R.styleable.TagViewGroup_radius,
                CommonUtil.dp2px(context, DEFAULT_RADIUS));
        mInnerRadius = array.getDimensionPixelSize(R.styleable.TagViewGroup_inner_radius,
                CommonUtil.dp2px(context, DEFAULT_INNER_RADIUS));
        mTouchSlop = array.getDimensionPixelSize(R.styleable.TagViewGroup_touch_slop,
                CommonUtil.dp2px(context, DEFAULT_TOUCH_SLOP));
        mTDistance = array.getDimensionPixelSize(R.styleable.TagViewGroup_tilt_distance,
                CommonUtil.dp2px(context, DEFAULT_TILT_DISTANCE));
        mVDistance = array.getDimensionPixelSize(R.styleable.TagViewGroup_v_distance,
                CommonUtil.dp2px(context, DEFAULT_V_DISTANCE));
        mLinesWidth = array.getDimensionPixelSize(R.styleable.TagViewGroup_line_width,
                CommonUtil.dp2px(context, DEFAULT_LINES_WIDTH));
        mRippleMaxRadius = array.getDimensionPixelSize(R.styleable.TagViewGroup_ripple_maxRadius,
                CommonUtil.dp2px(context, DEFAULT_RIPPLE_MAX_RADIUS));
        mRippleAlpha = array.getInteger(R.styleable.TagViewGroup_ripple_alpha,
                DEFAULT_RIPPLE_ALPHA);
        mRippleMinRadius = mInnerRadius + (mRadius - mInnerRadius) / 2;
        array.recycle();
        mPaint = new Paint();
        mPath = new Path();
        mDstPath = new Path();
        mPathMeasure = new PathMeasure();
        mPaint.setAntiAlias(true);
        mGestureDetector = new GestureDetectorCompat(context, new TagOnGestureListener());
        mChildUsed = new int[4];
        mCenterRect = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setChildViewsMaxWidth();
        mChildUsed = getChildUsed();
        mCenterX = (int) (getMeasuredWidth() * mPercentX);
        mCenterY = (int) (getMeasuredHeight() * mPercentY);
        mCenterRect.set(mCenterX - mRadius - mTouchSlop,
                mCenterY - mRadius - mTouchSlop,
                mCenterX + mRadius + mTouchSlop,
                mCenterY + mRadius + mTouchSlop);
        if (mRippleView != null) {
            mRippleView.setCenterPoint(mCenterX, mCenterY);
        }
    }

    /**
     * 获取中心圆上下左右各个方向的宽度
     *
     * @return int[]{left,top,right,bottom}
     */
    private int[] getChildUsed() {
        int childCount = getChildCount();
        int leftMax = mVDistance, topMax = mVDistance, rightMax = mVDistance, bottomMax =
                mVDistance;
        for (int i = 0; i < childCount; i++) {
            ITagView child = (ITagView) getChildAt(i);
            switch (child.getDirection()) {
                case RIGHT_TOP_TILT:
                    rightMax = Math.max(rightMax, mTDistance + child.getMeasuredWidth());
                    topMax = Math.max(topMax, child.getMeasuredHeight() + mTDistance);
                    break;
                case RIGHT_TOP:
                    rightMax = Math.max(rightMax, child.getMeasuredWidth());
                    topMax = Math.max(topMax, child.getMeasuredHeight() + mVDistance);
                    break;
                case RIGHT_CENTER:
                    rightMax = Math.max(rightMax, child.getMeasuredWidth());
                    topMax = Math.max(topMax, Math.max(mVDistance, child.getMeasuredHeight()));
                    break;
                case RIGHT_BOTTOM:
                    rightMax = Math.max(rightMax, child.getMeasuredWidth());
                    bottomMax = mVDistance;
                    break;
                case RIGHT_BOTTOM_TILT:
                    rightMax = Math.max(rightMax, mTDistance + child.getMeasuredWidth());
                    bottomMax = mTDistance;
                    break;
                case LEFT_TOP:
                    leftMax = Math.max(leftMax, child.getMeasuredWidth());
                    topMax = Math.max(topMax, child.getMeasuredHeight() + mVDistance);
                    break;
                case LEFT_TOP_TILT:
                    leftMax = Math.max(leftMax, child.getMeasuredWidth() + mTDistance);
                    topMax = Math.max(topMax, child.getMeasuredHeight() + mTDistance);
                    break;
                case LEFT_CENTER:
                    leftMax = Math.max(leftMax, child.getMeasuredWidth());
                    topMax = Math.max(topMax, Math.max(mVDistance, child.getMeasuredHeight()));
                    break;
                case LEFT_BOTTOM:
                    leftMax = Math.max(leftMax, child.getMeasuredWidth());
                    bottomMax = mVDistance;
                    break;
                case LEFT_BOTTOM_TILT:
                    leftMax = Math.max(leftMax, child.getMeasuredWidth() + mTDistance);
                    bottomMax = mTDistance;
                    break;
            }

        }
        return new int[]{leftMax, topMax, rightMax, bottomMax};
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0, top = 0;
        for (int i = 0; i < getChildCount(); i++) {
            ITagView child = (ITagView) getChildAt(i);
            switch (child.getDirection()) {
                case RIGHT_TOP_TILT:
                    top = mCenterY - mTDistance - child.getMeasuredHeight();
                    left = mCenterX + mTDistance;
                    break;
                case RIGHT_TOP:
                    left = mCenterX;
                    top = mCenterY - mVDistance - child.getMeasuredHeight();
                    break;
                case RIGHT_CENTER:
                    left = mCenterX;
                    top = mCenterY - child.getMeasuredHeight();
                    break;
                case RIGHT_BOTTOM:
                    left = mCenterX;
                    top = mVDistance + mCenterY - child.getMeasuredHeight();
                    break;
                case RIGHT_BOTTOM_TILT:
                    left = mCenterX + mTDistance;
                    top = mTDistance + mCenterY - child.getMeasuredHeight();
                    break;
                case LEFT_TOP:
                    left = mCenterX - child.getMeasuredWidth();
                    top = mCenterY - mVDistance - child.getMeasuredHeight();
                    break;
                case LEFT_TOP_TILT:
                    left = mCenterX - child.getMeasuredWidth() - mTDistance;
                    top = mCenterY - mTDistance - child.getMeasuredHeight();
                    break;
                case LEFT_CENTER:
                    left = mCenterX - child.getMeasuredWidth();
                    top = mCenterY - child.getMeasuredHeight();
                    break;
                case LEFT_BOTTOM:
                    left = mCenterX - child.getMeasuredWidth();
                    top = mVDistance + mCenterY - child.getMeasuredHeight();
                    break;
                case LEFT_BOTTOM_TILT:
                    left = mCenterX - child.getMeasuredWidth() - mTDistance;
                    top = mTDistance + mCenterY - child.getMeasuredHeight();
                    break;
                case CENTER:
                    left = 0;
                    top = 0;
                    break;
            }
            child.layout(left,
                    top,
                    left + child.getMeasuredWidth(),
                    top + child.getMeasuredHeight());
            refreshTagsInfo(child);
        }
    }

    private void setChildViewsMaxWidth() {
        for (int i = 0, size = getChildCount(); i < size; i++) {
            int width = 0;
            ITagView child = (ITagView) getChildAt(i);
            switch (child.getDirection()) {
                case RIGHT_TOP_TILT:
                case RIGHT_BOTTOM_TILT:
                    width = getMeasuredWidth() - mCenterX - mTDistance;
                    break;
                case RIGHT_TOP:
                case RIGHT_CENTER:
                case RIGHT_BOTTOM:
                    width = getMeasuredWidth() - mCenterX;
                    break;
                case LEFT_TOP:
                case LEFT_CENTER:
                case LEFT_BOTTOM:
                    width = mCenterX;
                    break;
                case LEFT_TOP_TILT:
                case LEFT_BOTTOM_TILT:
                    width = mCenterX - mTDistance;
                    break;
                case CENTER:
                    width = 0;
                    break;
            }
            child.setMaxWidth(width);
        }
    }

    @Override
    @SuppressLint("NewApi")
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //绘制折线
        drawLines(canvas);
        //绘制外圆
        mPaint.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#30000000"));
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
        //绘制内圆
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(mCenterX, mCenterY, mInnerRadius, mPaint);
    }

    private void drawLines(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(mLinesWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setShadowLayer(6, 0, 0, Color.parseColor("#7F000000"));
        for (int i = 0; i < getChildCount(); i++) {
            ITagView child = (ITagView) getChildAt(i);
            mPath.reset();
            mPath.moveTo(mCenterX, mCenterY);
            mDstPath.reset();
            mDstPath.rLineTo(0, 0);
            switch (child.getDirection()) {
                case RIGHT_TOP:
                case RIGHT_BOTTOM:
                case RIGHT_TOP_TILT:
                case RIGHT_BOTTOM_TILT:
                    mPath.lineTo(child.getLeft(), child.getBottom());
                case RIGHT_CENTER:
                    mPath.lineTo(child.getRight(), child.getBottom());
                    break;
                case LEFT_TOP:
                case LEFT_TOP_TILT:
                case LEFT_BOTTOM:
                case LEFT_BOTTOM_TILT:
                    mPath.lineTo(child.getRight(), child.getBottom());
                case LEFT_CENTER:
                    mPath.lineTo(child.getLeft(), child.getBottom());
                    break;
            }
            mPathMeasure.setPath(mPath, false);
            mPathMeasure.getSegment(0, mPathMeasure.getLength() * mLinesRatio, mDstPath, true);
            canvas.drawPath(mDstPath, mPaint);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        if (mCenterRect.contains(x, y) || isTouchingTags(x, y) != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (mClickListener != null) {
            return mGestureDetector.onTouchEvent(e);
        }
        return super.onTouchEvent(e);
    }

    private class TagOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            if (mCenterRect.contains(x, y) || isTouchingTags(x, y) != null) {
                return true;
            }
            return super.onDown(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            if (mCenterRect.contains(x, y)) {
                mClickListener.onCircleClick(TagViewGroup.this);
            } else {
                ItemInfo info = isTouchingTags(x, y);
                if (info != null) {
                    mClickListener.onTagClick(TagViewGroup.this, info.item, info.position);
                }
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mScrollListener != null) {
                float currentX = mCenterX - distanceX;
                float currentY = mCenterY - distanceY;
                currentX = Math.min(Math.max(currentX, mVDistance),
                        getMeasuredWidth() - mVDistance);
                currentY = Math.min(Math.max(currentY, mChildUsed[1]),
                        getMeasuredHeight() - mChildUsed[3]);
                mPercentX = currentX / getMeasuredWidth();
                mPercentY = currentY / getMeasuredHeight();
                invalidate();
                requestLayout();
                mScrollListener.onDrag(TagViewGroup.this, mPercentX, mPercentY);
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            float x = e.getX();
            float y = e.getY();
            if (mCenterRect.contains(x, y) || isTouchingTags(x, y) != null) {
                mClickListener.onLongPress(TagViewGroup.this);
            }
        }
    }

    public void setOnTagGroupClickListener(OnTagGroupClickListener listener) {
        mClickListener = listener;
    }

    public void setOnTagGroupDragListener(OnTagGroupDragListener listener) {
        this.mScrollListener = listener;
    }

    public interface OnTagGroupClickListener {

        void onCircleClick(TagViewGroup container);

        void onTagClick(TagViewGroup container, ITagView tag, int position);

        void onLongPress(TagViewGroup container);
    }

    public interface OnTagGroupDragListener {
        void onDrag(TagViewGroup container, float percentX, float percentY);
    }

    private class TagSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            clearGroup();
            populate();
        }
    }

    public void setTagAdapter(TagAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
            clearGroup();
        }
        mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(mObserver);
        }
        populate();
    }

    public TagAdapter getTagAdapter() {
        return mAdapter;
    }

    private void clearGroup() {
        for (int i = 0; i < mItems.size(); i++) {
            ItemInfo itemInfo = mItems.get(i);
            mAdapter.destroyItem(this, itemInfo.position, itemInfo.item);
            mItems.clear();
            removeAllViews();
        }
    }

    private void populate() {
        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.position = i;
            itemInfo.item = mAdapter.instantiateItem(this, i);
            mItems.add(itemInfo);
        }
    }

    /**
     * child rectF
     *
     * @param child
     */
    private void refreshTagsInfo(ITagView child) {
        if (child.getDirection() != Direction.CENTER) {
            for (int i = 0; i < mItems.size(); i++) {
                ItemInfo info = mItems.get(i);
                if (info.item.equals(child)) {
                    info.rectF.set(child.getLeft(),
                            child.getTop(),
                            child.getRight(),
                            child.getBottom());
                }
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mRippleView != null) {
            mRippleView.startRipple();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mRippleView != null) {
            mRippleView.stopRipple();
        }
    }

    /**
     * 设置中心圆点坐标占整个 ViewGroup 的比例
     *
     * @param percentX
     * @param percentY
     */
    public void setPercent(float percentX, float percentY) {
        mPercentX = percentX;
        mPercentY = percentY;
    }

    /**
     * 添加水波纹
     */
    public void addRippleView() {
        mRippleView = new RippleView(getContext());
        mRippleView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        mRippleView.setDirection(Direction.CENTER);
        mRippleView.initAnimator(mRippleMinRadius, mRippleMaxRadius, mRippleAlpha);
        addView(mRippleView);
    }

    /**
     * 检测 Touch 事件发生在哪个 Tag 上
     *
     * @param x
     * @param y
     * @return
     */
    private ItemInfo isTouchingTags(float x, float y) {
        for (int i = 0; i < mItems.size(); i++) {
            ItemInfo info = mItems.get(i);
            if (info.rectF.contains(x, y)) {
                return info;
            }
        }
        return null;
    }

    public int getCircleRadius() {
        return mRadius;
    }

    @SuppressWarnings("unused")
    public void setCircleRadius(int radius) {
        this.mRadius = radius;
        invalidate();
    }

    public int getCircleInnerRadius() {
        return mInnerRadius;
    }

    @SuppressWarnings("unused")
    public void setCircleInnerRadius(int innerRadius) {
        this.mInnerRadius = innerRadius;
        invalidate();
    }

    public float getLinesRatio() {
        return mLinesRatio;
    }

    @SuppressWarnings("unused")
    public void setLinesRatio(float ratio) {
        this.mLinesRatio = ratio;
        invalidate();
    }

    public float getTagAlpha() {
        return mTagAlpha;
    }

    @SuppressWarnings("unused")
    public void setTagAlpha(float alpha) {
        this.mTagAlpha = alpha;
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setAlpha(mTagAlpha);
        }
    }

    public int getLineWidth() {
        return mLinesWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.mLinesWidth = lineWidth;
        invalidate();
    }

    public int getVDistance() {
        return mVDistance;
    }

    public void setVDistance(int vDistance) {
        this.mVDistance = vDistance;
    }

    public int getTDistance() {
        return mTDistance;
    }

    public void setTDistance(int tDistance) {
        this.mTDistance = tDistance;
    }

    public int getRippleMaxRadius() {
        return mRippleMaxRadius;
    }

    public void setRippleMaxRadius(int radius) {
        this.mRippleMaxRadius = radius;
    }

    public int getRippleAlpha() {
        return mRippleAlpha;
    }

    public void setRippleAlpha(int alpha) {
        this.mRippleAlpha = alpha;
    }


    public void startShowAnimator() {
        if (mShowAnimator != null && !mShowAnimator.isRunning()) {
            mShowAnimator.start();
        }
    }

    public void startHideAnimator() {
        if (mHideAnimator != null && !mHideAnimator.isRunning()) {
            mHideAnimator.start();
        }
    }

    /**
     * 外圆属性动画值
     */
    private static final Property<TagViewGroup, Integer> CIRCLE_RADIUS = new
            Property<TagViewGroup, Integer>(
            Integer.class,
            "circleRadius") {
        @Override
        public Integer get(TagViewGroup object) {
            return object.getCircleRadius();
        }

        @Override
        public void set(TagViewGroup object, Integer value) {
            object.setCircleRadius(value);
        }
    };

    /**
     * 内圆属性动画值
     */
    private static final Property<TagViewGroup, Integer> CIRCLE_INNER_RADIUS = new
            Property<TagViewGroup, Integer>(
            Integer.class,
            "circleInnerRadius") {
        @Override
        public Integer get(TagViewGroup object) {
            return object.getCircleInnerRadius();
        }

        @Override
        public void set(TagViewGroup object, Integer value) {
            object.setCircleInnerRadius(value);
        }
    };

    /**
     * 折线属性动画值
     */
    private static final Property<TagViewGroup, Float> LINES_RATIO = new Property<TagViewGroup,
            Float>(
            Float.class,
            "linesRatio") {
        @Override
        public Float get(TagViewGroup object) {
            return object.getLinesRatio();
        }

        @Override
        public void set(TagViewGroup object, Float value) {
            object.setLinesRatio(value);
        }
    };

    /**
     * 标签动画属性值
     */
    private static final Property<TagViewGroup, Float> TAG_ALPHA = new Property<TagViewGroup,
            Float>(
            Float.class,
            "tagAlpha") {
        @Override
        public Float get(TagViewGroup object) {
            return object.getTagAlpha();
        }

        @Override
        public void set(TagViewGroup object, Float value) {
            object.setTagAlpha(value);
        }
    };

    /**
     * 设置tagViewGroup动画
     */
    public void setTagGroupAnimation() {
        mShowAnimator = getTagShowAnimator();
        mShowAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                setVisibility(VISIBLE);
            }
        });
        mHideAnimator = getTagHideAnimator();
        mHideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisibility(INVISIBLE);
            }
        });
        addRippleView();
    }

    /**
     * tagViewGroup显示动画
     *
     * @return
     */
    public Animator getTagShowAnimator() {
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(circleRadiusAnimator(), linesAnimator(), tagTextAnimator());
        return set;
    }

    /**
     * tagViewGroup隐藏动画
     *
     * @return
     */
    public Animator getTagHideAnimator() {
        AnimatorSet together = new AnimatorSet();
        AnimatorSet sequential = new AnimatorSet();
        ObjectAnimator linesAnimator = ObjectAnimator.ofFloat(this, LINES_RATIO, 1, 0);
        ObjectAnimator tagTextAnimator = ObjectAnimator.ofFloat(this, TAG_ALPHA, 1, 0);
        Animator circleAnimator = circleRadiusAnimator();
        together.playTogether(linesAnimator, tagTextAnimator);
        together.setDuration(400);
        together.setInterpolator(new DecelerateInterpolator());
        sequential.playSequentially(circleAnimator, together);
        return sequential;
    }

    /**
     * 标签文本动画
     *
     * @return
     */
    private Animator tagTextAnimator() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, TAG_ALPHA, 0, 1);
        animator.setDuration(200);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    /**
     * 折线动画
     *
     * @return
     */
    private Animator linesAnimator() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, LINES_RATIO, 0, 1);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    /**
     * 圆动画
     *
     * @return
     */
    private AnimatorSet circleRadiusAnimator() {
        int radius = getCircleRadius();
        int innerRadius = getCircleInnerRadius();
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofInt(this,
                CIRCLE_RADIUS,
                radius - 10,
                radius + 10,
                radius),
                ObjectAnimator.ofInt(this,
                        CIRCLE_INNER_RADIUS,
                        innerRadius - 10,
                        innerRadius + 10,
                        innerRadius));
        set.setDuration(400);
        return set;
    }

}
