package com.hunliji.hljvideolibrary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljvideolibrary.R;
import com.hunliji.hljvideolibrary.R2;
import com.hunliji.hljvideolibrary.interfaces.HljOnRangeSeekBarListener;
import com.hunliji.hljvideolibrary.interfaces.HljVideoTrimListener;
import com.hunliji.hljvideolibrary.interfaces.VideoTrimObservable;
import com.hunliji.hljvideolibrary.interfaces.VideoUtil;
import com.hunliji.hljvideolibrary.scalablevideo.ScalableType;
import com.hunliji.hljvideolibrary.scalablevideo.ScalableVideoView;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by luohanlin on 2017/6/19.
 */

public class HljVideoTrimView extends FrameLayout implements HljOnRangeSeekBarListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer
                .OnErrorListener {
    public static final int VIEW_HEIGHT = 50;
    public static final String TAG = "HLJ_VIDEO_TRIM";

    private Context mContext;
    private Uri mUri;
    private TrimViewHolder holder;
    private Subscriber<Uri> trimSubscriber;
    private int thumbSize;
    private long mDuration; // milliseconds length of the video
    private long numThumbs;
    private long mMinDuration = 2 * 1000;
    private long mMaxDuration = 6 * 1000;
    private long mStartPosition;
    private long mEndPosition;
    private long timeLineLength;
    private long validScreenLength; // 可用于显示time line的有效的屏幕宽度
    private long validScreenDuration; // 滑动条time line中属于屏幕内（不包含两边padding）的部分
    private long trimVideoDuration;
    private long videoScrollOffset; // 滑动造成裁剪范围的偏移值
    private long trimStart;
    private long trimEnd;
    private long interval;
    private String mFinalPath;
    private TimelineAdapter adapter;
    private HljVideoTrimListener videoSeekListener;

    private ScalableType mScalableType = ScalableType.FIT_CENTER;
    private boolean muted = false; // 是否静音
    private float fixedProportion = 750f / 1220f; // 默认的视频显示的比例，在centerCrop模式下有效

    public HljVideoTrimView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HljVideoTrimView(
            @NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HljVideoTrimView(
            @NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        initValues();
        initViews();
    }

    public void initWithParams(Uri uri, int minDuration, int maxDuration) {
        this.mUri = uri;
        if (minDuration < 1) {
            minDuration = 1;
        }
        this.mMinDuration = minDuration * 1000;
        this.mMaxDuration = maxDuration * 1000;

        try {
            holder.videoView.setDataSource(mContext, uri);
            holder.videoView.prepare(this);
            holder.videoView.setOnCompletionListener(this);
            holder.videoView.setOnErrorListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.videoView.requestFocus();

        setTimeLineSeeker();
    }

    private void initValues() {
        validScreenLength = (CommonUtil.getDeviceSize(mContext).x - CommonUtil.dp2px(mContext,
                16 * 2));
        thumbSize = CommonUtil.dp2px(mContext, VIEW_HEIGHT);
    }

    private void initViews() {
        inflate(mContext, R.layout.hlj_video_trim_view, this);
        holder = new TrimViewHolder(getRootView());

        holder.seekBar.addOnRangeSeekBarListener(this);

        final GestureDetector gestureDetector = new GestureDetector(mContext,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        if (holder != null) {
                            if (holder.videoView.isPlaying()) {
                                holder.imgPlay.setVisibility(VISIBLE);
                                holder.videoView.pause();
                            } else {
                                holder.imgPlay.setVisibility(GONE);
                                long startOffset = videoScrollOffset + mStartPosition;
                                holder.videoView.seekTo((int) startOffset);
                                holder.videoView.start();
                            }
                        }
                        return true;
                    }
                });
        holder.videoView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    private void setTimeLineSeeker() {
        // 计算时间和interval
        final MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(getContext(), mUri);
        final long videoLengthInMs = Integer.parseInt(mediaMetadataRetriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION));

        if (mMaxDuration > videoLengthInMs) {
            mMaxDuration = (int) videoLengthInMs;
        }

        // 屏幕内能够显示的thumb数量
        int thumbsInScreen = (int) (validScreenLength / thumbSize);
        final int mspf; // milliseconds per frame
        if ((mMaxDuration / 1000) <= thumbsInScreen) {
            if (mMaxDuration / 500 <= thumbsInScreen) {
                mspf = 500;
            } else {
                mspf = 1000;
            }
        } else {
            mspf = (int) (mMaxDuration / thumbsInScreen);
        }

        interval = mspf * 1000;

        numThumbs = (int) Math.ceil(videoLengthInMs / mspf);

        if (numThumbs <= thumbsInScreen) {
            validScreenLength = numThumbs * thumbSize;
            holder.seekBar.getLayoutParams().width = (int) (validScreenLength + CommonUtil.dp2px(
                    mContext,
                    52));
        }

        timeLineLength = numThumbs * thumbSize;
        validScreenDuration = (int) ((validScreenLength * videoLengthInMs) / timeLineLength);
        mediaMetadataRetriever.release();

        initRecycler();
    }

    private void initRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL,
                false);
        holder.timeLineRecycler.setLayoutManager(layoutManager);
        adapter = new TimelineAdapter(mContext, thumbSize, mUri, numThumbs, interval);
        holder.timeLineRecycler.setAdapter(adapter);
        holder.timeLineRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int scrollX = recyclerView.computeHorizontalScrollOffset();
                videoScrollOffset = scrollX * mDuration / timeLineLength;
                Log.d(TAG, "dx = " + scrollX + " offset = " + videoScrollOffset);
                calcTrimVideoTime();
                videoViewSeekToTrimStart();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        holder.videoView.setScalableType(mScalableType);
        if (mScalableType == ScalableType.CENTER_CROP) {
            // 只有在centerCrop模式下，需要重新设置视频显示区域，需要显示mask
            int videoWidth = mp.getVideoWidth();
            int videoHeight = mp.getVideoHeight();
            float videoProportion = (float) videoWidth / (float) videoHeight;

            int validScreenWidth = holder.videoLayout.getWidth();
            int validScreenHeight = holder.videoLayout.getHeight();
            float validScreenProportion = (float) validScreenWidth / (float) validScreenHeight;

            ViewGroup.LayoutParams lp = holder.videoView.getLayoutParams();
            if (videoProportion >= validScreenProportion) {
                // 视频的宽高比大于有效屏幕宽高比
                lp.height = validScreenHeight;
                lp.width = validScreenWidth;
            } else if (videoProportion >= fixedProportion) {
                // 视频的宽高比大于固定截屏宽高比
                lp.height = validScreenHeight;
                lp.width = (int) (videoProportion * (float) validScreenHeight);
            } else {
                // 视频的高度偏高, 视频宽高比小于固定截屏宽高比
                lp.height = validScreenHeight;
                lp.width = (int) (fixedProportion * (float) validScreenHeight);
            }

            int fixedWidth = (int) (validScreenHeight * fixedProportion);
            int maskWidth = (validScreenWidth - fixedWidth) / 2;
            if (maskWidth > 0) {
                holder.maskLayout.setVisibility(VISIBLE);
                holder.maskLeft.getLayoutParams().width = maskWidth;
                holder.maskRight.getLayoutParams().width = maskWidth;
            }
            holder.videoView.setLayoutParams(lp);
        } else {
            holder.maskLayout.setVisibility(GONE);
        }

        holder.videoView.setVisibility(View.VISIBLE);
        if (muted) {
            holder.videoView.setVolume(0, 0);
        }

        mDuration = holder.videoView.getDuration();

        holder.videoView.seekTo(0);
        holder.videoView.start();
        setSeekBar();
    }

    private void setSeekBar() {
        trimVideoDuration = mStartPosition = 0;
        if (validScreenDuration >= mMaxDuration) {
            trimEnd = mEndPosition = mMaxDuration;

            holder.seekBar.setThumbValue(0, (mStartPosition * 100) / validScreenDuration);
            holder.seekBar.setThumbValue(1, (mEndPosition * 100) / validScreenDuration);
        } else {
            mMaxDuration = validScreenDuration;
            trimEnd = mEndPosition = validScreenDuration;
        }

        videoViewSeekToTrimStart();

        trimVideoDuration = mEndPosition - mStartPosition;
        if (mMinDuration < validScreenDuration) {
            holder.seekBar.setPixelMinLength(mMinDuration * 100 / validScreenDuration);
        }
        holder.seekBar.initMaxWidth();

        if (this.videoSeekListener != null) {
            this.videoSeekListener.onSeeking(trimStart, trimEnd, trimVideoDuration);
        }
    }

    public void setDestinationPath(String path) {
        mFinalPath = path;
    }

    private void setDefaultDestinationPath() {
        File folder = Environment.getExternalStorageDirectory();
        mFinalPath = folder.getPath() + File.separator;
    }

    public void setVideoSeekListener(HljVideoTrimListener videoSeekListener) {
        this.videoSeekListener = videoSeekListener;
    }

    public void setTrimSubscriber(Subscriber<Uri> trimSubscriber) {
        this.trimSubscriber = trimSubscriber;
    }

    /**
     * 设置播放视频声音与否，需要在初始化前设置
     *
     * @param muted
     */
    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    /**
     * 设置视频的scale模式，默认没有，自适应
     * 设置为centerCrop模式将会显示对应的视频描边选框，
     * 需要在初始化前设置
     *
     * @param type
     */
    public void setScalableType(ScalableType type) {
        this.mScalableType = type;
    }

    /**
     * 设置视频固定款高比例，在centerCrop模式下有效
     * 需要在初始化前设置
     *
     * @param proportion
     */
    public void setFixedProportion(float proportion) {
        this.fixedProportion = proportion;
    }

    public void startTrim() {
        if (TextUtils.isEmpty(mFinalPath)) {
            setDefaultDestinationPath();
        }
        if (trimStart <= 0 && trimEnd >= mDuration) {
            trimSubscriber.onNext(mUri);
            trimSubscriber.onCompleted();
        } else {
            holder.imgPlay.setVisibility(VISIBLE);
            holder.videoView.pause();

            // 滑动有误差，游标所计算得出的截取时间小于最小时间时，前后截取补足以满足最小时间要求
            // 最小值误差修正
            if (trimVideoDuration < mMinDuration) {
                if ((mDuration - trimEnd) > (mMinDuration - trimVideoDuration)) {
                    trimEnd += (mMinDuration - trimVideoDuration);
                } else if (trimStart > (mMinDuration - trimVideoDuration)) {
                    trimStart -= (mMinDuration - trimVideoDuration);
                }
            }
            // 最大值误差修正
            if (trimVideoDuration > mMaxDuration) {
                // 直接剪去尾部多出的部分
                trimEnd = trimStart + mMaxDuration;
            } else if (trimVideoDuration > mMaxDuration - 100) {
                // 如果尾部可以增加，则补足为最大值
                if ((mDuration - trimEnd) > (mMaxDuration - trimVideoDuration)) {
                    trimEnd += (mMaxDuration - trimVideoDuration);
                }
                // 不使用头部补足，不够就不够吧
            }

            final File file = new File(VideoUtil.getVideoPathForUri(mContext, mUri));

            Observable.create(new Observable.OnSubscribe<Uri>() {
                @Override
                public void call(Subscriber<? super Uri> subscriber) {
                    try {
                        VideoTrimObservable.startTrim(file,
                                mFinalPath,
                                trimStart,
                                trimEnd,
                                (Subscriber<Uri>) subscriber);
                    } catch (IOException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(trimSubscriber);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        holder.videoView.seekTo(0);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        CommonUtil.unSubscribeSubs(trimSubscriber);
        super.onDetachedFromWindow();
    }

    @Override
    public void onCreate(HljRangeSeekBarView rangeSeekBarView, int index, float value) {

    }

    @Override
    public void onSeek(HljRangeSeekBarView rangeSeekBarView, int index, float value) {
        if (index == 0) {
            mStartPosition = (int) ((validScreenDuration * value) / 100L);
            calcTrimVideoTime();
            videoViewSeekToTrimStart();
        } else {
            mEndPosition = (int) ((validScreenDuration * value) / 100L);
            calcTrimVideoTime();
        }
    }

    @Override
    public void onSeekStart(HljRangeSeekBarView rangeSeekBarView, int index, float value) {

    }

    @Override
    public void onSeekStop(HljRangeSeekBarView rangeSeekBarView, int index, float value) {
        holder.videoView.pause();
        holder.imgPlay.setVisibility(View.VISIBLE);
    }

    private void calcTrimVideoTime() {
        final int DELTA = 100;
        trimVideoDuration = mEndPosition - mStartPosition;

        trimStart = videoScrollOffset + mStartPosition;
        trimEnd = videoScrollOffset + mEndPosition;

        // 游标选择的区域是不会存在大于最大值或者小于最小值的，但滑动游标选取有误差，
        // 所以显示当前截取的长度时，处理误差，显示正确的最大最小值，在实际截取时再进行修正
        long durationForShow = trimVideoDuration;
        if (durationForShow < mMinDuration) {
            // 当时间小于最小值时，忽略误差，直接认为是最小值
            durationForShow = mMinDuration;
        } else if (durationForShow > mMaxDuration - DELTA) {
            // 当时间大于最大值或，时间小于但是接近（允许1s的误差）最大值时，直接认为是最大值
            durationForShow = mMaxDuration;
        }

        if (this.videoSeekListener != null) {
            this.videoSeekListener.onSeeking(trimStart, trimEnd, durationForShow);
        }
    }

    private void videoViewSeekToTrimStart() {
        int thumbDuration = (int) (thumbSize * mDuration / timeLineLength);
        int videoStart = (int) CommonUtil.positive(trimStart - thumbDuration / 2);
        holder.videoView.seekTo(videoStart);
    }

    static class TrimViewHolder {
        @BindView(R2.id.video_layout)
        RelativeLayout videoLayout;
        @BindView(R2.id.video_view)
        ScalableVideoView videoView;
        @BindView(R2.id.mask_top)
        View maskTop;
        @BindView(R2.id.mask_left)
        View maskLeft;
        @BindView(R2.id.mask_right)
        View maskRight;
        @BindView(R2.id.mask_bottom)
        View maskBottom;
        @BindView(R2.id.mask_layout)
        RelativeLayout maskLayout;
        @BindView(R2.id.img_play)
        ImageView imgPlay;
        @BindView(R2.id.recycler_view)
        RecyclerView timeLineRecycler;
        @BindView(R2.id.seek_bar)
        HljRangeSeekBarView seekBar;
        @BindView(R2.id.seek_layout)
        RelativeLayout seekLayout;

        TrimViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    private class IBitmap {
        private int i;
        private Bitmap bitmap;

        public IBitmap(int i, Bitmap bitmap) {
            this.i = i;
            this.bitmap = bitmap;
        }

        public int getI() {
            return i;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }
    }
}

