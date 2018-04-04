package com.hunliji.hljvideolibrary;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;


import com.hunliji.hljvideolibrary.interfaces.OnProgressVideoListener;
import com.hunliji.hljvideolibrary.interfaces.OnRangeSeekBarListener;
import com.hunliji.hljvideolibrary.interfaces.OnTrimVideoListener;
import com.hunliji.hljvideolibrary.interfaces.VideoTrimObservable;
import com.hunliji.hljvideolibrary.utils.BackgroundExecutor;
import com.hunliji.hljvideolibrary.utils.TrimVideoUtils;
import com.hunliji.hljvideolibrary.utils.UiThreadExecutor;
import com.hunliji.hljvideolibrary.view.ProgressBarView;
import com.hunliji.hljvideolibrary.view.RangeSeekBarView;
import com.hunliji.hljvideolibrary.view.TimeLineView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HljVideoTrimmer extends FrameLayout implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, SeekBar
                .OnSeekBarChangeListener, OnRangeSeekBarListener, OnProgressVideoListener {

    private SeekBar mHolderTopView;
    private RangeSeekBarView mRangeSeekBarView;
    private RelativeLayout mLinearVideo;
    private VideoView mVideoView;
    private ImageView mPlayView;
    private TextView mTextSize;
    private TextView mTextTimeFrame;
    private TextView mTextTime;
    private TextView tvCurrentVideoTime;
    private TimeLineView mTimeLineView;

    private Uri mSrc;
    private String mFinalPath;

    private int mMaxDuration;
    private int mMinDuration;
    private List<OnProgressVideoListener> mListeners;
    private Subscriber<Uri> subscriber;

    private int mDuration = 0;
    private int mCurrentTrimVideoTime = 0;
    private int mStartPosition = 0;
    private int mEndPosition = 0;
    private long mOriginSizeFile;
    private boolean mResetSeekBar = true;

    @NonNull
    private final MessageHandler mMessageHandler = new MessageHandler(this);
    private static final int SHOW_PROGRESS = 2;

    private GestureDetector mGestureDetector;

    @NonNull
    private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector
            .SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mVideoView.isPlaying()) {
                mPlayView.setVisibility(View.VISIBLE);
                mMessageHandler.removeMessages(SHOW_PROGRESS);
                mVideoView.pause();
            } else {
                mPlayView.setVisibility(View.GONE);

                if (mResetSeekBar) {
                    mResetSeekBar = false;
                    mVideoView.seekTo(mStartPosition);
                }

                mMessageHandler.sendEmptyMessage(SHOW_PROGRESS);
                mVideoView.start();
            }
            return true;
        }
    };

    @NonNull
    private final OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, @NonNull MotionEvent event) {
            mGestureDetector.onTouchEvent(event);
            return true;
        }
    };

    public HljVideoTrimmer(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HljVideoTrimmer(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context)
                .inflate(R.layout.video_trimmer_time_line, this, true);

        mHolderTopView = ((SeekBar) findViewById(R.id.handlerTop));
        ProgressBarView progressVideoView = ((ProgressBarView) findViewById(R.id.timeVideoView));
        mRangeSeekBarView = ((RangeSeekBarView) findViewById(R.id.timeLineBar));
        mLinearVideo = ((RelativeLayout) findViewById(R.id.layout_surface_view));
        mVideoView = ((VideoView) findViewById(R.id.video_loader));
        mPlayView = ((ImageView) findViewById(R.id.icon_video_play));
        mTextSize = ((TextView) findViewById(R.id.textSize));
        mTextTimeFrame = ((TextView) findViewById(R.id.textTimeSelection));
        mTextTime = ((TextView) findViewById(R.id.textTime));
        mTimeLineView = ((TimeLineView) findViewById(R.id.timeLineView));
        tvCurrentVideoTime = (TextView) findViewById(R.id.tv_current_video_length);

        mListeners = new ArrayList<>();
        mListeners.add(this);
        mListeners.add(progressVideoView);

        mHolderTopView.setMax(1000);
        mHolderTopView.setSecondaryProgress(0);

        mRangeSeekBarView.addOnRangeSeekBarListener(this);
        mRangeSeekBarView.addOnRangeSeekBarListener(progressVideoView);

        int marge = mRangeSeekBarView.getThumbs()
                .get(0)
                .getWidthBitmap();
        int widthSeek = mHolderTopView.getThumb()
                .getMinimumWidth() / 2;

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mHolderTopView
                .getLayoutParams();
        lp.setMargins(marge - widthSeek, 0, marge - widthSeek, 0);
        mHolderTopView.setLayoutParams(lp);

        lp = (RelativeLayout.LayoutParams) mTimeLineView.getLayoutParams();
        lp.setMargins(marge, 0, marge, 0);
        mTimeLineView.setLayoutParams(lp);

        lp = (RelativeLayout.LayoutParams) progressVideoView.getLayoutParams();
        lp.setMargins(marge, 0, marge, 0);
        progressVideoView.setLayoutParams(lp);

        mHolderTopView.setOnSeekBarChangeListener(this);

        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnErrorListener(this);

        mGestureDetector = new GestureDetector(getContext(), mGestureListener);
        mVideoView.setOnTouchListener(mTouchListener);

        setDefaultDestinationPath();
    }

    @SuppressWarnings("unused")
    public void setVideoURI(final Uri videoURI) {
        mSrc = videoURI;

        getSizeFile();

        mVideoView.setVideoURI(mSrc);
        mVideoView.requestFocus();

        mTimeLineView.setVideo(mSrc);
    }

    @SuppressWarnings("unused")
    public void setDestinationPath(final String finalPath) {
        mFinalPath = finalPath;
    }

    private void setDefaultDestinationPath() {
        File folder = Environment.getExternalStorageDirectory();
        mFinalPath = folder.getPath() + File.separator;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        int duration = (int) ((mDuration * progress) / 1000L);

        if (fromUser) {
            if (duration < mStartPosition) {
                setProgressBarPosition(mStartPosition);
                duration = mStartPosition;
            } else if (duration > mEndPosition) {
                setProgressBarPosition(mEndPosition);
                duration = mEndPosition;
            }
            setTimeVideo(duration);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mMessageHandler.removeMessages(SHOW_PROGRESS);
        mVideoView.pause();
        mPlayView.setVisibility(View.VISIBLE);
        updateProgress(false);
    }

    @Override
    public void onStopTrackingTouch(@NonNull SeekBar seekBar) {
        mMessageHandler.removeMessages(SHOW_PROGRESS);
        mVideoView.pause();
        mPlayView.setVisibility(View.VISIBLE);

        int duration = (int) ((mDuration * seekBar.getProgress()) / 1000L);
        mVideoView.seekTo(duration);
        setTimeVideo(duration);
        updateProgress(false);
    }

    @Override
    public void onPrepared(@NonNull MediaPlayer mp) {
        // Adjust the size of the video
        // so it fits on the screen
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = mLinearVideo.getWidth();
        int screenHeight = mLinearVideo.getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        mVideoView.setLayoutParams(lp);

        mPlayView.setVisibility(View.VISIBLE);

        mDuration = mVideoView.getDuration();
        setSeekBarPosition();

        setTimeFrames();
        setTimeVideo(0);
    }

    private void setSeekBarPosition() {
        if (mDuration >= mMaxDuration) {
            mStartPosition = 0;
            mEndPosition = mMaxDuration;

            mRangeSeekBarView.setThumbValue(0, (mStartPosition * 100) / mDuration);
            mRangeSeekBarView.setThumbValue(1, (mEndPosition * 100) / mDuration);
        } else {
            mMaxDuration = mDuration;
            mStartPosition = 0;
            mEndPosition = mDuration;
        }

        setProgressBarPosition(mStartPosition);
        mVideoView.seekTo(mStartPosition);

        mCurrentTrimVideoTime = mEndPosition - mStartPosition;
        if (mMinDuration < mDuration) {
            mRangeSeekBarView.setPixelMinLength(mMinDuration * 100 / mDuration);
        }
        mRangeSeekBarView.initMaxWidth();
    }

    /**
     * 开始剪辑视频
     */
    public void confirmTrim() {
        if (mStartPosition <= 0 && mEndPosition >= mDuration) {
            subscriber.onNext(mSrc);
            subscriber.onCompleted();
        } else {
            mPlayView.setVisibility(View.VISIBLE);
            mVideoView.pause();

            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(getContext(), mSrc);
            long METADATA_KEY_DURATION = Long.parseLong(mediaMetadataRetriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION));

            File file = new File(mSrc.getPath());

            // 滑动有误差，游标所计算得出的截取时间小于最小时间时，前后截取补足以满足最小时间要求
            if (mCurrentTrimVideoTime < mMinDuration) {
                if ((METADATA_KEY_DURATION - mEndPosition) > (mMinDuration -
                        mCurrentTrimVideoTime)) {
                    mEndPosition += (mMinDuration - mCurrentTrimVideoTime);
                } else if (mStartPosition > (mMinDuration - mCurrentTrimVideoTime)) {
                    mStartPosition -= (mMinDuration - mCurrentTrimVideoTime);
                }
            }
            // 最大值误差修正
            if (mCurrentTrimVideoTime > mMaxDuration) {
                // 直接剪去尾部多出的部分
                mEndPosition = mStartPosition + mMaxDuration;
            } else if (mCurrentTrimVideoTime > mMaxDuration - 1000) {
                // 如果尾部可以增加，则补足为最大值
                if ((METADATA_KEY_DURATION - mEndPosition) > (mMaxDuration -
                        mCurrentTrimVideoTime)) {
                    mEndPosition += (mMaxDuration - mCurrentTrimVideoTime);
                }
                // 不使用头部补足，不够就不够吧
            }

            startTrimVideo(file, mFinalPath, mStartPosition, mEndPosition, subscriber);
        }
    }

    private void startTrimVideo(
            @NonNull final File file,
            @NonNull final String dst,
            final int startVideo,
            final int endVideo,
            @NonNull final Subscriber<Uri> subscriber) {
        Observable.create(new Observable.OnSubscribe<Uri>() {

            @Override
            public void call(Subscriber<? super Uri> subscriber) {
                try {
                    VideoTrimObservable.startTrim(file,
                            dst,
                            startVideo,
                            endVideo,
                            (Subscriber<Uri>) subscriber);
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        //        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0L, "") {
        //            @Override
        //            public void execute() {
        //                try {
        //                    TrimVideoUtils.startTrim(file, dst, startVideo, endVideo, callback);
        //                } catch (final Throwable e) {
        //                    Thread.getDefaultUncaughtExceptionHandler()
        //                            .uncaughtException(Thread.currentThread(), e);
        //                }
        //            }
        //        });
    }

    private void setTimeFrames() {
        String seconds = "秒";
        mTextTimeFrame.setText(String.format("%s %s - %s %s",
                stringForTime(mStartPosition),
                seconds,
                stringForTime(mEndPosition),
                seconds));

        // 游标选择的区域是不会存在大于最大值或者小于最小值的，但滑动游标选取有误差，
        // 所以显示当前截取的长度时，处理误差，显示正确的最大最小值，在实际截取时再进行修正
        int showingTime = mCurrentTrimVideoTime;
        if (showingTime < mMinDuration) {
            // 当时间小于最小值时，忽略误差，直接认为是最小值
            showingTime = mMinDuration;
        } else if (showingTime > mMaxDuration - 1000) {
            // 当时间大于最大值或，时间小于但是接近（允许1s的误差）最大值时，直接认为是最大值
            showingTime = mMaxDuration;
        }
        int totalSeconds = showingTime / 1000;
        Formatter mFormatter = new Formatter();
        String secondsStr = mFormatter.format("%3d", totalSeconds)
                .toString();
        tvCurrentVideoTime.setText(String.format("当前长度%s秒", secondsStr));
    }

    private void setTimeVideo(int position) {
        String seconds = "秒";
        mTextTime.setText(String.format("%s %s", stringForTime(position), seconds));
    }

    @Override
    public void onCreate(RangeSeekBarView rangeSeekBarView, int index, float value) {

    }

    @Override
    public void onSeek(RangeSeekBarView rangeSeekBarView, int index, float value) {
        // 0 is Left selector
        // 1 is right selector
        switch (index) {
            case 0: {
                mStartPosition = (int) ((mDuration * value) / 100L);
                mVideoView.seekTo(mStartPosition);
                break;
            }
            case 1: {
                mEndPosition = Math.round((mDuration * value) / 100L);
                break;
            }
        }
        setProgressBarPosition(mStartPosition);

        setTimeFrames();
        mCurrentTrimVideoTime = mEndPosition - mStartPosition;
    }

    @Override
    public void onSeekStart(RangeSeekBarView rangeSeekBarView, int index, float value) {

    }

    @Override
    public void onSeekStop(RangeSeekBarView rangeSeekBarView, int index, float value) {
        mMessageHandler.removeMessages(SHOW_PROGRESS);
        mVideoView.pause();
        mPlayView.setVisibility(View.VISIBLE);
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        Formatter mFormatter = new Formatter();
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
                    .toString();
        } else if (minutes > 0) {
            return mFormatter.format("%02d:%02d", minutes, seconds)
                    .toString();
        } else {
            return mFormatter.format("%2d", seconds)
                    .toString();
        }
    }

    private void getSizeFile() {

        if (mOriginSizeFile == 0) {
            File file = new File(mSrc.getPath());

            mOriginSizeFile = file.length();
            long fileSizeInKB = mOriginSizeFile / 1024;

            if (fileSizeInKB > 1000) {
                long fileSizeInMB = fileSizeInKB / 1024;
                mTextSize.setText(String.format("%s %s", fileSizeInMB, "MB"));
            } else {
                mTextSize.setText(String.format("%s %s", fileSizeInKB, "KB"));
            }
        }
    }

    @SuppressWarnings("unused")
    //    public void setOnTrimVideoListener(OnTrimVideoListener onTrimVideoListener) {
    //        this.mOnTrimVideoListener = onTrimVideoListener;
    //    }

    public void setSubscriber(Subscriber<Uri> subscriber) {
        this.subscriber = subscriber;
    }

    @SuppressWarnings("unused")
    public void setMaxDuration(int maxDuration) {
        this.mMaxDuration = maxDuration * 1000;
    }

    public void setMinDuration(int mMinDuration) {
        this.mMinDuration = mMinDuration * 1000;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mVideoView.seekTo(0);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    private static class MessageHandler extends Handler {

        @NonNull
        private final WeakReference<HljVideoTrimmer> mView;

        MessageHandler(HljVideoTrimmer view) {
            mView = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            HljVideoTrimmer view = mView.get();
            if (view == null || view.mVideoView == null) {
                return;
            }

            view.updateProgress(true);
            if (view.mVideoView.isPlaying()) {
                sendEmptyMessageDelayed(0, 10);
            }
        }
    }

    private void updateProgress(boolean all) {
        if (mDuration == 0)
            return;

        int position = mVideoView.getCurrentPosition();
        if (all) {
            for (OnProgressVideoListener item : mListeners) {
                item.updateProgress(position, mDuration, ((position * 100) / mDuration));
            }
        } else {
            mListeners.get(1)
                    .updateProgress(position, mDuration, ((position * 100) / mDuration));
        }
    }

    @Override
    public void updateProgress(int time, int max, float scale) {
        if (mVideoView == null) {
            return;
        }

        if (time >= mEndPosition) {
            mMessageHandler.removeMessages(SHOW_PROGRESS);
            mVideoView.pause();
            mPlayView.setVisibility(View.VISIBLE);
            mResetSeekBar = true;
            return;
        }

        if (mHolderTopView != null) {
            // use long to avoid overflow
            setProgressBarPosition(time);
        }
        setTimeVideo(time);
    }


    private void setProgressBarPosition(int position) {
        if (mDuration > 0) {
            long pos = 1000L * position / mDuration;
            mHolderTopView.setProgress((int) pos);
        }
    }

    public void destroy() {
        BackgroundExecutor.cancelAll("", true);
        UiThreadExecutor.cancelAll("");
    }
}
