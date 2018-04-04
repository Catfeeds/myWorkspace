package com.slider.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;

import com.slider.library.Animations.BaseAnimationInterface;
import com.slider.library.Indicators.CirclePageExIndicator;
import com.slider.library.Indicators.PageIndicator;
import com.slider.library.Transformers.AccordionTransformer;
import com.slider.library.Transformers.BackgroundToForegroundTransformer;
import com.slider.library.Transformers.BaseTransformer;
import com.slider.library.Transformers.CubeInTransformer;
import com.slider.library.Transformers.DefaultTransformer;
import com.slider.library.Transformers.DepthPageTransformer;
import com.slider.library.Transformers.FadeTransformer;
import com.slider.library.Transformers.FlipHorizontalTransformer;
import com.slider.library.Transformers.FlipPageViewTransformer;
import com.slider.library.Transformers.ForegroundToBackgroundTransformer;
import com.slider.library.Transformers.RotateDownTransformer;
import com.slider.library.Transformers.RotateUpTransformer;
import com.slider.library.Transformers.StackTransformer;
import com.slider.library.Transformers.TabletTransformer;
import com.slider.library.Transformers.ZoomInTransformer;
import com.slider.library.Transformers.ZoomOutSlideTransformer;
import com.slider.library.Transformers.ZoomOutTransformer;
import com.slider.library.Tricks.FixedSpeedScroller;
import com.slider.library.Tricks.InfinitePagerAdapter;
import com.slider.library.Tricks.InfiniteViewPager;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;


public class SliderLayout extends RelativeLayout {

    private Context mContext;
    private InfiniteViewPager mViewPager;

    private PagerAdapter mPagerAdapter;

    private PageIndicator mIndicator;


    private Timer mCycleTimer;
    private TimerTask mCycleTask;

    private Timer mResumingTimer;
    private TimerTask mResumingTask;

    private boolean mCycling;

    private boolean mAutoRecover = true;

    private boolean mTouchPause = true;

    private int mTransformerId;

    private int mTransformerSpan = 1100;

    private boolean mAutoCycle;

    private long mSliderDuration = 4000;

    private boolean mIsWrapContentHeight;

    private BaseTransformer mViewPagerTransformer;

    private boolean isPagerAnimation;//viewPager是否需要翻页动画 默认为true

    private BaseAnimationInterface mCustomAnimation;
    private android.os.Handler mh = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (scroller != null) {
                scroller.setmDuration(3000);
            }
            mViewPager.nextItem();
        }
    };

    private FixedSpeedScroller scroller;

    public SliderLayout(Context context) {
        this(context, null);
    }

    public SliderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SliderLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.slider_layout, this, true);

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R
                        .styleable.SliderLayout,
                defStyle, 0);

        mTransformerSpan = attributes.getInteger(R.styleable.SliderLayout_pager_animation_span,
                1100);
        mTransformerId = attributes.getInt(R.styleable.SliderLayout_pager_animation,
                Transformer.Default.ordinal());
        isPagerAnimation = attributes.getBoolean(R.styleable.SliderLayout_is_pager_animation,true);
        mAutoCycle = attributes.getBoolean(R.styleable.SliderLayout_auto_cycle, false);
        mTouchPause = attributes.getBoolean(R.styleable.SliderLayout_touch_pause, true);
        mIsWrapContentHeight = attributes.getBoolean(R.styleable
                .SliderLayout_is_height_wrap_content, false);
        mViewPager = (InfiniteViewPager) findViewById(R.id.daimajia_slider_viewpager);
        mViewPager.setmIsWrapContentHeight(mIsWrapContentHeight);

        mViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!mTouchPause){
                    return false;
                }
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        recoverCycle();
                        break;
                }
                return false;
            }
        });

        attributes.recycle();
        if(isPagerAnimation){
            setPresetTransformer(mTransformerId);
            setSliderTransformDuration(mTransformerSpan, null);
        }
        if (mAutoCycle) {
            startAutoCycle();
        }
    }

    public void setPagerAdapter(PagerAdapter mPagerAdapter) {
        this.mPagerAdapter = mPagerAdapter;
        PagerAdapter wrappedAdapter = new InfinitePagerAdapter(mPagerAdapter);
        mViewPager.setAdapter(wrappedAdapter);
        mViewPager.setCurrentItem(0);
    }

    public void setCurrentItem(int item) {
        if (mPagerAdapter != null && item < mPagerAdapter.getCount()) {
            mViewPager.setCurrentItem(item);
        }
    }

    public void nextItem() {
        if (mPagerAdapter != null) {
            mViewPager.nextItem();
        }
    }

    public void setCustomIndicator(PageIndicator indicator) {
        if (mIndicator != null) {
            mIndicator = null;
        }
        mIndicator = indicator;
        mIndicator.setViewPager(mViewPager);
    }

    public void startAutoCycle() {
        mAutoCycle = true;
        if(isAttachedToWindow) {
            startAutoCycle(1000, mSliderDuration, mAutoRecover);
        }
    }

    /**
     * start auto cycle.
     *
     * @param delay       delay time
     * @param duration    animation duration time.
     * @param autoRecover if recover after user touches the slider.
     */
    public void startAutoCycle(long delay, long duration, boolean autoRecover) {
        if (mCycleTimer != null) mCycleTimer.cancel();
        if (mCycleTask != null) mCycleTask.cancel();
        if (mResumingTask != null) mResumingTask.cancel();
        if (mResumingTimer != null) mResumingTimer.cancel();
        mSliderDuration = duration;
        mCycleTimer = new Timer();
        mAutoRecover = autoRecover;
        mCycleTask = new TimerTask() {
            @Override
            public void run() {
                mh.sendEmptyMessage(0);
            }
        };
        mCycleTimer.schedule(mCycleTask, delay, mSliderDuration);
        mCycling = true;
        mAutoCycle = true;
    }

    /**
     * pause auto cycle.
     */
    private void pauseAutoCycle() {
        if (scroller != null) {
            scroller.setmDuration(800);
        }
        if (mCycling) {
            mCycleTimer.cancel();
            mCycleTask.cancel();
            mCycling = false;
        } else {
            if (mResumingTimer != null && mResumingTask != null) {
                recoverCycle();
            }
        }
    }

    /**
     * set the duration between two slider changes. the duration value must >= 500
     *
     * @param duration
     */
    public void setDuration(long duration) {
        if (duration >= 500) {
            mSliderDuration = duration;
            if (mAutoCycle && mCycling) {
                startAutoCycle();
            }
        }
    }

    /**
     * stop the auto circle
     */
    public void stopAutoCycle() {
        if (mCycleTask != null) {
            mCycleTask.cancel();
        }
        if (mCycleTimer != null) {
            mCycleTimer.cancel();
        }
        if (mResumingTimer != null) {
            mResumingTimer.cancel();
        }
        if (mResumingTask != null) {
            mResumingTask.cancel();
        }
        mAutoCycle = false;
        mCycling = false;
    }

    /**
     * when paused cycle, this method can weak it up.
     */
    private void recoverCycle() {
        if (!mAutoRecover || !mAutoCycle) {
            return;
        }

        if (!mCycling) {
            if (mResumingTask != null && mResumingTimer != null) {
                mResumingTimer.cancel();
                mResumingTask.cancel();
            }
            mResumingTimer = new Timer();
            mResumingTask = new TimerTask() {
                @Override
                public void run() {
                    startAutoCycle();
                }
            };
            mResumingTimer.schedule(mResumingTask, 6000);
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(!mTouchPause){
            return false;
        }
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                pauseAutoCycle();
                break;
        }
        return false;
    }

    /**
     * set ViewPager transformer.
     *
     * @param reverseDrawingOrder
     * @param transformer
     */
    public void setPagerTransformer(boolean reverseDrawingOrder, BaseTransformer transformer) {
        mViewPagerTransformer = transformer;
        mViewPagerTransformer.setCustomAnimationInterface(mCustomAnimation);
        mViewPager.setPageTransformer(reverseDrawingOrder, mViewPagerTransformer);
    }


    /**
     * set the duration between two slider changes.
     *
     * @param period
     * @param interpolator
     */
    public void setSliderTransformDuration(int period, Interpolator interpolator) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new FixedSpeedScroller(mViewPager.getContext(), interpolator, period);
            mScroller.set(mViewPager, scroller);
        } catch (Exception e) {

        }
    }

    public void setOnPageChangeListener(ViewPager.SimpleOnPageChangeListener onPageChangeListener) {
        if (mViewPager != null) {
            mViewPager.addOnPageChangeListener(onPageChangeListener);
        }
    }

    /**
     * set a preset viewpager transformer by id.
     *
     * @param transformerId
     */
    public void setPresetTransformer(int transformerId) {
        for (Transformer t : Transformer.values()) {
            if (t.ordinal() == transformerId) {
                setPresetTransformer(t);
                break;
            }
        }
    }

    ;

    /**
     * set preset PagerTransformer via the name of transforemer.
     *
     * @param transformerName
     */
    public void setPresetTransformer(String transformerName) {
        for (Transformer t : Transformer.values()) {
            if (t.equals(transformerName)) {
                setPresetTransformer(t);
                return;
            }
        }
    }

    public void setCustomAnimation(BaseAnimationInterface animation) {
        mCustomAnimation = animation;
        if (mViewPagerTransformer != null) {
            mViewPagerTransformer.setCustomAnimationInterface(mCustomAnimation);
        }
    }

    /**
     * pretty much right? enjoy it. :-D
     *
     * @param ts
     */
    public void setPresetTransformer(Transformer ts) {
        //
        // special thanks to https://github.com/ToxicBakery/ViewPagerTransforms
        //
        BaseTransformer t = null;
        switch (ts) {
            case Default:
                t = new DefaultTransformer();
                break;
            case Accordion:
                t = new AccordionTransformer();
                break;
            case Background2Foreground:
                t = new BackgroundToForegroundTransformer();
                break;
            case CubeIn:
                t = new CubeInTransformer();
                break;
            case DepthPage:
                t = new DepthPageTransformer();
                break;
            case Fade:
                t = new FadeTransformer();
                break;
            case FlipHorizontal:
                t = new FlipHorizontalTransformer();
                break;
            case FlipPage:
                t = new FlipPageViewTransformer();
                break;
            case Foreground2Background:
                t = new ForegroundToBackgroundTransformer();
                break;
            case RotateDown:
                t = new RotateDownTransformer();
                break;
            case RotateUp:
                t = new RotateUpTransformer();
                break;
            case Stack:
                t = new StackTransformer();
                break;
            case Tablet:
                t = new TabletTransformer();
                break;
            case ZoomIn:
                t = new ZoomInTransformer();
                break;
            case ZoomOutSlide:
                t = new ZoomOutSlideTransformer();
                break;
            case ZoomOut:
                t = new ZoomOutTransformer();
                break;
        }
        setPagerTransformer(true, t);
    }

    private InfinitePagerAdapter getWrapperAdapter() {
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter != null) {
            return (InfinitePagerAdapter) adapter;
        } else {
            return null;
        }
    }

    private PagerAdapter getRealAdapter() {
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter != null) {
            return ((InfinitePagerAdapter) adapter).getRealAdapter();
        }
        return null;
    }

    /**
     * get the current item position
     *
     * @return
     */
    public int getCurrentPosition() {

        if (getRealAdapter() == null)
            throw new IllegalStateException("You did not set a slider adapter");
        if (getRealAdapter().getCount() == 0)
            return 0;
        return mViewPager.getCurrentItem() % getRealAdapter().getCount();

    }

    /**
     * preset transformers and their names
     */
    public enum Transformer {
        Default("Default"),
        Accordion("Accordion"),
        Background2Foreground("Background2Foreground"),
        CubeIn("CubeIn"),
        DepthPage("DepthPage"),
        Fade("Fade"),
        FlipHorizontal("FlipHorizontal"),
        FlipPage("FlipPage"),
        Foreground2Background("Foreground2Background"),
        RotateDown("RotateDown"),
        RotateUp("RotateUp"),
        Stack("Stack"),
        Tablet("Tablet"),
        ZoomIn("ZoomIn"),
        ZoomOutSlide("ZoomOutSlide"),
        ZoomOut("ZoomOut");

        private final String name;

        private Transformer(String s) {
            name = s;
        }

        public String toString() {
            return name;
        }

        public boolean equals(String other) {
            return other != null && name.equals(other);
        }
    }

    public InfiniteViewPager getmViewPager() {
        return mViewPager;
    }

    private boolean isAttachedToWindow;

    @Override
    protected void onAttachedToWindow() {
        isAttachedToWindow=true;
        if (mPagerAdapter != null && mPagerAdapter.getCount() > 1 && mAutoCycle) {
            startAutoCycle();
        }
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        isAttachedToWindow=false;
        if (mCycleTask != null) {
            mCycleTask.cancel();
        }
        if (mCycleTimer != null) {
            mCycleTimer.cancel();
        }
        if (mResumingTimer != null) {
            mResumingTimer.cancel();
        }
        if (mResumingTask != null) {
            mResumingTask.cancel();
        }
        mCycling = false;
        super.onDetachedFromWindow();
    }
}
