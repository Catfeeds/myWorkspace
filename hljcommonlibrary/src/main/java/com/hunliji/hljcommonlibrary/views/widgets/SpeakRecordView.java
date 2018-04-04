package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.R;
import com.hunliji.hljcommonlibrary.utils.SoundRecord;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;

import java.io.File;
import java.io.IOException;


/**
 * Created by Suncloud on 2016/10/14.
 */

public class SpeakRecordView extends FrameLayout {

    private ImageView ivMic;
    private TextView tvRecordHint;
    private int[] micResources = {R.mipmap.icon_record_animate_01___cm, R.mipmap
            .icon_record_animate_02___cm, R.mipmap.icon_record_animate_03___cm, R.mipmap
            .icon_record_animate_04___cm, R.mipmap.icon_record_animate_05___cm, R.mipmap
            .icon_record_animate_06___cm, R.mipmap.icon_record_animate_07___cm, R.mipmap
            .icon_record_animate_08___cm, R.mipmap.icon_record_animate_09___cm, R.mipmap
            .icon_record_animate_10___cm, R.mipmap.icon_record_animate_11___cm, R.mipmap
            .icon_record_animate_12___cm, R.mipmap.icon_record_animate_13___cm, R.mipmap
            .icon_record_animate_14___cm, R.mipmap.icon_record_animate_15___cm, R.mipmap
            .icon_record_animate_16___cm, R.mipmap.icon_record_animate_17___cm, R.mipmap
            .icon_record_animate_18___cm, R.mipmap.icon_record_animate_19___cm};

    private int micResourcesIndex;
    private int state; //0 录音；1 取消


    private PowerManager.WakeLock wakeLock;
    private OnSpeakStatusListener onSpeakStatusListener;
    private OnRecordFileCallback callback;
    private SoundRecord recorder;
    private String userName;
    private int maxSec; //最大录音时间

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SoundRecord.REFRESH:
                    //录音波形刷新
                    setMicResourcesIndex(msg.arg1 * 9 / 50);
                    break;
                case SoundRecord.PERMISSION_ERR:
                case SoundRecord.RECORD_ERR:
                    //录音失败，结束录音
                    onStop();
                    ToastUtil.showToast(getContext(),
                            null,
                            msg.what == SoundRecord.PERMISSION_ERR ? R.string
                                    .msg_recording_permission___cm : R.string
                                    .msg_err_recording___cm);
                    break;
                case SoundRecord.RECORD_OVERTIME:
                    onDone(0);
                    break;
            }
            return false;
        }
    });

    public SpeakRecordView(Context context) {
        super(context);
        init();
    }

    public SpeakRecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpeakRecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext())
                .inflate(R.layout.widget_record_view___cm, this);
        ivMic = (ImageView) findViewById(R.id.iv_mic);
        tvRecordHint = (TextView) findViewById(R.id.tv_record_hint);
    }

    private String getUserName() {
        if (TextUtils.isEmpty(userName)) {
            userName = "temp";
        }
        return userName;
    }

    /**
     * @param userName 录音存放目录
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 最大录音时间  单位秒
     *
     * @param maxSec
     */
    public void setMaxSec(int maxSec) {
        this.maxSec = maxSec;
        if (recorder != null) {
            recorder.setMaxTime(maxSec);
        }
    }

    public boolean onSpeakTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按钮按下进入录音状态
                try {
                    onStart();
                } catch (Exception e) {
                    e.printStackTrace();
                    onStop();
                    ToastUtil.showToast(getContext(), null, R.string.msg_err_recording___cm);
                    return false;
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                //录音过程中手指滑动，根据滑动距离判断取消状态
                if (recorder != null && recorder.getState() < 0) {
                    //声音录制异常
                    return false;
                }
                if (event.getY() < -getTop() / 2) {
                    //取消状态
                    onCancelState();
                } else {
                    //正常录音状态
                    onRecordState();
                }
                return true;
            default:
                //结束录音操作
                onDone(state);
                return event.getAction() == MotionEvent.ACTION_UP;

        }
    }

    private void onStart() throws IOException {
        getWakeLock().acquire();
        if (recorder == null) {
            recorder = new SoundRecord(handler);
            if (maxSec > 1) {
                recorder.setMaxTime(maxSec);
            }
        }
        recorder.startRecording(getContext(), getUserName(), true);

        setVisibility(VISIBLE);
        micResourcesIndex = 0;
        if (onSpeakStatusListener != null) {
            onSpeakStatusListener.onStart();
        }
        onRecordState();
    }

    private void onStop() {
        if (getWakeLock().isHeld()) {
            getWakeLock().release();
        }
        if (recorder != null) {
            recorder.onDestroy();
        }
        setVisibility(GONE);
        if (onSpeakStatusListener != null) {
            onSpeakStatusListener.onStop();
        }
    }

    private void onRecordState() {
        state = 0;
        ivMic.setImageResource(micResources[micResourcesIndex]);
        tvRecordHint.setText(R.string.label_move_up_to_cancel___cm);
        if (onSpeakStatusListener != null) {
            onSpeakStatusListener.onRecord();
        }
    }

    private void onCancelState() {
        state = 1;
        ivMic.setImageResource(R.mipmap.icon_record_cancel___cm);
        tvRecordHint.setText(R.string.label_release_to_cancel___cm);
        if (onSpeakStatusListener != null) {
            onSpeakStatusListener.onCancel();
        }
    }

    private void setMicResourcesIndex(int micResourcesIndex) {
        this.micResourcesIndex = micResourcesIndex;
        if (state == 0) {
            ivMic.setImageResource(micResources[micResourcesIndex]);
        }
    }

    private PowerManager.WakeLock getWakeLock() {
        if (wakeLock == null) {
            wakeLock = ((PowerManager) getContext().getSystemService(Context.POWER_SERVICE))
                    .newWakeLock(
                    PowerManager.SCREEN_DIM_WAKE_LOCK,
                    "speak");
        }
        return wakeLock;
    }

    /**
     * 结束录音
     *
     * @param state 当前状态
     */
    private void onDone(int state) {
        if (recorder == null) {
            ToastUtil.showToast(getContext(), null, R.string.msg_err_recording___cm);
            return;
        }
        if (!recorder.isRecording()) {
            return;
        }
        if (state == 0) {
            //非取消状态结束
            recorder.stopRecording();
            File file = recorder.getRecordFile();
            if (file != null && file.exists()) {
                if (recorder.getRecordTime() < 1000) {
                    ToastUtil.showToast(getContext(),
                            null,
                            R.string.msg_err_recording_too_short___cm);
                } else {
                    if (callback != null) {
                        callback.onDone(file.getAbsolutePath(),
                                (double) (recorder.getRecordTime() / 100) / 10);
                    }
                    recorder.onFinish();
                }
            } else {
                ToastUtil.showToast(getContext(), null, R.string.msg_err_recording___cm);
            }

        }
        onStop();
    }

    /**
     * 控件消除时销毁录音状态
     */
    @Override
    protected void onDetachedFromWindow() {
        if (recorder != null)
            recorder.onDestroy();
        super.onDetachedFromWindow();
    }

    public void setOnSpeakStatusListener(OnSpeakStatusListener onSpeakStatusListener) {
        this.onSpeakStatusListener = onSpeakStatusListener;
    }

    public void setFileCallback(OnRecordFileCallback callback) {
        this.callback = callback;
    }

    /**
     * 录音状态监听
     */
    public interface OnSpeakStatusListener {

        void onStart();

        void onStop();

        void onCancel();

        void onRecord();
    }

    public interface OnRecordFileCallback {
        /**
         * 录音完成
         *
         * @param filePath 音频文件
         * @param duration 时长
         */
        void onDone(String filePath, double duration);
    }
}
