package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.AnimRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.sunfusheng.marqueeview.Utils;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.MarqueeModel;

/**
 * Created by mo_yu on 18/3/14.带图片跑马灯
 */
public class MarqueeWithImageView extends ViewFlipper {

    private int interval = 3000;
    private boolean hasSetAnimDuration = false;
    private int animDuration = 1000;
    private int textSize = 14;
    private int textColor = 0xffffffff;
    private boolean singleLine = false;

    private int gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
    private static final int GRAVITY_LEFT = 0;
    private static final int GRAVITY_CENTER = 1;
    private static final int GRAVITY_RIGHT = 2;

    private boolean hasSetDirection = false;
    private int direction = DIRECTION_BOTTOM_TO_TOP;
    private static final int DIRECTION_BOTTOM_TO_TOP = 0;
    private static final int DIRECTION_TOP_TO_BOTTOM = 1;
    private static final int DIRECTION_RIGHT_TO_LEFT = 2;
    private static final int DIRECTION_LEFT_TO_RIGHT = 3;

    @AnimRes
    private int inAnimResId = com.sunfusheng.marqueeview.R.anim.anim_bottom_in;
    @AnimRes
    private int outAnimResId = com.sunfusheng.marqueeview.R.anim.anim_top_out;

    private int position;
    private List<? extends MarqueeModel> notices = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private Context mContext;

    public MarqueeWithImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                com.sunfusheng.marqueeview.R.styleable.MarqueeViewStyle,
                defStyleAttr,
                0);
        mContext = context;
        interval = typedArray.getInteger(com.sunfusheng.marqueeview.R.styleable
                        .MarqueeViewStyle_mvInterval,
                interval);
        hasSetAnimDuration = typedArray.hasValue(com.sunfusheng.marqueeview.R.styleable
                .MarqueeViewStyle_mvAnimDuration);
        animDuration = typedArray.getInteger(com.sunfusheng.marqueeview.R.styleable
                        .MarqueeViewStyle_mvAnimDuration,
                animDuration);
        singleLine = typedArray.getBoolean(com.sunfusheng.marqueeview.R.styleable
                        .MarqueeViewStyle_mvSingleLine,
                false);
        if (typedArray.hasValue(com.sunfusheng.marqueeview.R.styleable
                .MarqueeViewStyle_mvTextSize)) {
            textSize = (int) typedArray.getDimension(com.sunfusheng.marqueeview.R.styleable
                            .MarqueeViewStyle_mvTextSize,
                    textSize);
            textSize = Utils.px2sp(context, textSize);
        }
        textColor = typedArray.getColor(com.sunfusheng.marqueeview.R.styleable
                        .MarqueeViewStyle_mvTextColor,
                textColor);

        int gravityType = typedArray.getInt(com.sunfusheng.marqueeview.R.styleable
                        .MarqueeViewStyle_mvGravity,
                GRAVITY_LEFT);
        switch (gravityType) {
            case GRAVITY_LEFT:
                gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                break;
            case GRAVITY_CENTER:
                gravity = Gravity.CENTER;
                break;
            case GRAVITY_RIGHT:
                gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                break;
        }

        hasSetDirection = typedArray.hasValue(com.sunfusheng.marqueeview.R.styleable
                .MarqueeViewStyle_mvDirection);
        direction = typedArray.getInt(com.sunfusheng.marqueeview.R.styleable
                        .MarqueeViewStyle_mvDirection,
                direction);
        if (hasSetDirection) {
            switch (direction) {
                case DIRECTION_BOTTOM_TO_TOP:
                    inAnimResId = com.sunfusheng.marqueeview.R.anim.anim_bottom_in;
                    outAnimResId = com.sunfusheng.marqueeview.R.anim.anim_top_out;
                    break;
                case DIRECTION_TOP_TO_BOTTOM:
                    inAnimResId = com.sunfusheng.marqueeview.R.anim.anim_top_in;
                    outAnimResId = com.sunfusheng.marqueeview.R.anim.anim_bottom_out;
                    break;
                case DIRECTION_RIGHT_TO_LEFT:
                    inAnimResId = com.sunfusheng.marqueeview.R.anim.anim_right_in;
                    outAnimResId = com.sunfusheng.marqueeview.R.anim.anim_left_out;
                    break;
                case DIRECTION_LEFT_TO_RIGHT:
                    inAnimResId = com.sunfusheng.marqueeview.R.anim.anim_left_in;
                    outAnimResId = com.sunfusheng.marqueeview.R.anim.anim_right_out;
                    break;
            }
        } else {
            inAnimResId = com.sunfusheng.marqueeview.R.anim.anim_bottom_in;
            outAnimResId = com.sunfusheng.marqueeview.R.anim.anim_top_out;
        }

        typedArray.recycle();
        setFlipInterval(interval);
    }

    /**
     * 根据字符串，启动翻页公告
     *
     * @param notice 字符串
     */
    public void startWithText(String notice) {
        startWithText(notice, inAnimResId, outAnimResId);
    }

    /**
     * 根据字符串，启动翻页公告
     *
     * @param notice       字符串
     * @param inAnimResId  进入动画的resID
     * @param outAnimResID 离开动画的resID
     */
    @SuppressWarnings("deprecation")
    public void startWithText(
            final String notice, final @AnimRes int inAnimResId, final @AnimRes int outAnimResID) {
        if (TextUtils.isEmpty(notice))
            return;
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                startWithFixedWidth(notice, inAnimResId, outAnimResID);
            }
        });
    }

    /**
     * 根据字符串和宽度，启动翻页公告
     *
     * @param notice 字符串
     */
    private void startWithFixedWidth(
            String notice, @AnimRes int inAnimResId, @AnimRes int outAnimResID) {
        int noticeLength = notice.length();
        int width = Utils.px2dip(getContext(), getWidth());
        if (width == 0) {
            throw new RuntimeException("Please set the width of MarqueeView !");
        }
        int limit = width / textSize;
        List list = new ArrayList();

        if (noticeLength <= limit) {
            list.add(notice);
        } else {
            int size = noticeLength / limit + (noticeLength % limit != 0 ? 1 : 0);
            for (int i = 0; i < size; i++) {
                int startIndex = i * limit;
                int endIndex = ((i + 1) * limit >= noticeLength ? noticeLength : (i + 1) * limit);
                list.add(notice.substring(startIndex, endIndex));
            }
        }

        if (notices == null)
            notices = new ArrayList<>();
        notices.clear();
        notices.addAll(list);
        postStart(inAnimResId, outAnimResID);
    }

    /**
     * 根据字符串列表，启动翻页公告
     *
     * @param notices 字符串列表
     */
    public void startWithList(List<? extends MarqueeModel> notices) {
        startWithList(notices, inAnimResId, outAnimResId);
    }

    /**
     * 根据字符串列表，启动翻页公告
     *
     * @param notices      字符串列表
     * @param inAnimResId  进入动画的resID
     * @param outAnimResID 离开动画的resID
     */
    public void startWithList(
            List<? extends MarqueeModel> notices,
            @AnimRes int inAnimResId,
            @AnimRes int outAnimResID) {
        if (Utils.isEmpty(notices))
            return;
        setNotices(notices);
        postStart(inAnimResId, outAnimResID);
    }

    private void postStart(final @AnimRes int inAnimResId, final @AnimRes int outAnimResID) {
        post(new Runnable() {
            @Override
            public void run() {
                start(inAnimResId, outAnimResID);
            }
        });
    }

    private void start(final @AnimRes int inAnimResId, final @AnimRes int outAnimResID) {
        removeAllViews();
        clearAnimation();

        position = 0;
        addView(createTextView(position));

        if (notices.size() > 1) {
            setInAndOutAnimation(inAnimResId, outAnimResID);
            startFlipping();
        }

        if (getInAnimation() != null) {
            getInAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    position++;
                    if (position >= notices.size()) {
                        position = 0;
                    }
                    View view = createTextView(position);
                    if (view.getParent() == null) {
                        addView(view);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    private View createTextView(int position) {
        MarqueeModel marquee = notices.get(position);
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.marquee_with_image_layout, null);
        if (view != null) {
            ImageView ivMarquee = view.findViewById(R.id.riv_auth_avatar);
            TextView tvMarquee = view.findViewById(R.id.tv_marquee);
            tvMarquee.setText(marquee.getTitle());
            if (marquee.isHasImage()) {
                int avatarSize = CommonUtil.dp2px(mContext, 20);
                ivMarquee.setVisibility(VISIBLE);
                Glide.with(mContext)
                        .load(ImagePath.buildPath(marquee.getImagePath())
                                .width(avatarSize)
                                .height(avatarSize)
                                .cropPath())
                        .apply(new RequestOptions().dontAnimate()
                                .placeholder(R.mipmap.icon_avatar_primary))
                        .into(ivMarquee);
            } else {
                ivMarquee.setVisibility(GONE);
            }
            tvMarquee.setGravity(gravity);
            tvMarquee.setTextColor(textColor);
            tvMarquee.setTextSize(textSize);
            tvMarquee.setSingleLine(singleLine);
            tvMarquee.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getPosition(), (TextView) v);
                    }
                }
            });
            view.setTag(position);
        }
        return view;
    }

    public int getPosition() {
        return (int) getCurrentView().getTag();
    }

    public List<? extends MarqueeModel> getNotices() {
        return notices;
    }

    public void setNotices(List<? extends MarqueeModel> notices) {
        this.notices = notices;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, TextView textView);
    }

    /**
     * 设置进入动画和离开动画
     *
     * @param inAnimResId  进入动画的resID
     * @param outAnimResID 离开动画的resID
     */
    private void setInAndOutAnimation(@AnimRes int inAnimResId, @AnimRes int outAnimResID) {
        Animation inAnim = AnimationUtils.loadAnimation(getContext(), inAnimResId);
        if (hasSetAnimDuration)
            inAnim.setDuration(animDuration);
        setInAnimation(inAnim);

        Animation outAnim = AnimationUtils.loadAnimation(getContext(), outAnimResID);
        if (hasSetAnimDuration)
            outAnim.setDuration(animDuration);
        setOutAnimation(outAnim);
    }

}
