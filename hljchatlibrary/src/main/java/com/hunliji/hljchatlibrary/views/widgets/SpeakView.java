package com.hunliji.hljchatlibrary.views.widgets;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.hunliji.hljchatlibrary.R;
import com.hunliji.hljcommonlibrary.utils.SoundRecord;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;

/**
 * Created by Suncloud on 2016/10/18.
 * 语音按钮，语言功能封装
 */

public class SpeakView extends Button {

    private PowerManager.WakeLock wakeLock;
    private OnSpeakStatusListener onSpeakStatusListener;
    private ChatRecordView chatRecordView;
    private SoundRecord recorder;
    private String userName;
    private int maxSec; //最大录音时间

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SoundRecord.REFRESH:
                    //录音波形刷新
                    if (chatRecordView != null) {
                        chatRecordView.setMicResourcesIndex(msg.arg1 * 9 / 50);
                    }
                    break;
                case SoundRecord.PERMISSION_ERR:
                case SoundRecord.RECORD_ERR:
                    //录音失败，结束录音
                    setPressed(false);
                    setText(R.string.btn_speak_start___chat);
                    if (getWakeLock().isHeld())
                        getWakeLock().release();
                    if (recorder != null)
                        recorder.onDestroy();
                    if (chatRecordView != null) {
                        chatRecordView.onStop();
                    }
                    ToastUtil.showToast(getContext(),
                            null,
                            msg.what == SoundRecord.PERMISSION_ERR ? R.string
                                    .msg_recording_permission___cm : R.string
                                    .msg_err_recording___cm);
                    break;
                case SoundRecord.RECORD_OVERTIME:
                    recorderDone(0);
                    break;
            }
            return false;
        }
    });

    public SpeakView(Context context) {
        super(context);
    }

    public SpeakView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpeakView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @param onSpeakStatusListener 录音状态监听
     */
    public void setOnSpeakStatusListener(OnSpeakStatusListener onSpeakStatusListener) {
        this.onSpeakStatusListener = onSpeakStatusListener;
    }

    /**
     * 绑定录音视图
     *
     * @param chatRecordView
     */
    public void setChatRecordView(ChatRecordView chatRecordView) {
        this.chatRecordView = chatRecordView;
    }


    /**
     * @param userName 录音存放目录
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        if (TextUtils.isEmpty(userName)) {
            userName = "temp";
        }
        return userName;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public PowerManager.WakeLock getWakeLock() {
        if (wakeLock == null) {
            wakeLock = ((PowerManager) getContext().getSystemService(Context.POWER_SERVICE))
                    .newWakeLock(
                    PowerManager.SCREEN_DIM_WAKE_LOCK,
                    "speak");
        }
        return wakeLock;
    }

    private SoundRecord getRecorder() {
        if (recorder == null) {
            recorder = new SoundRecord(handler);
            if (maxSec > 1) {
                recorder.setMaxTime(maxSec);
            }
        }
        return recorder;
    }

    /**
     * 最大录音时间  单位秒
     * @param maxSec
     */
    public void setMaxSec(int maxSec) {
        this.maxSec = maxSec;
        if (recorder != null) {
            recorder.setMaxTime(maxSec);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (chatRecordView == null) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按钮按下进入录音状态
                try {
                    setPressed(true);
                    getWakeLock().acquire();
                    if (getRecorder().isRecording())
                        getRecorder().onDestroy();
                    chatRecordView.onStart();
                    getRecorder().startRecording(getContext(), getUserName(), true);
                    setText(R.string.btn_speak_done___chat);
                } catch (Exception e) {
                    e.printStackTrace();
                    setPressed(false);
                    setText(R.string.btn_speak_start___chat);
                    if (getWakeLock().isHeld())
                        getWakeLock().release();
                    if (recorder != null)
                        recorder.onDestroy();
                    chatRecordView.onStop();
                    ToastUtil.showToast(getContext(), null, R.string.msg_err_recording___cm);
                    return false;
                }
                return true;
            case MotionEvent.ACTION_MOVE: {
                //录音过程中手指滑动，根据滑动距离判断取消状态
                if (recorder != null && recorder.getState() < 0) {
                    return false;
                }
                if (event.getY() < -chatRecordView.getTop() / 2) {
                    chatRecordView.onCancelState();
                    setText(R.string.label_release_to_cancel___cm);
                } else {
                    chatRecordView.onRecordState();
                    setText(R.string.btn_speak_done___chat);
                }
                return true;
            }
            default:
                //取消录音状态
                recorderDone(event.getY());
                return event.getAction() == MotionEvent.ACTION_UP;

        }
    }

    /**
     * 结束录音
     *
     * @param y 结束是手指滑动位置
     */
    private void recorderDone(float y) {
        if (!isPressed()) {
            return;
        }
        setPressed(false);
        setText(R.string.btn_speak_start___chat);
        chatRecordView.setVisibility(View.GONE);
        if (getWakeLock().isHeld())
            getWakeLock().release();
        if (y < -chatRecordView.getTop() / 2) {
            if (recorder != null) {
                recorder.onDestroy();
            }
        } else if (recorder != null) {
            recorder.stopRecording();
            if (recorder.getRecordFile() != null && recorder.getRecordFile()
                    .exists()) {
                if (recorder.getRecordTime() < 1000) {
                    recorder.onDestroy();
                    ToastUtil.showToast(getContext(),
                            null,
                            R.string.msg_err_recording_too_short___cm);
                } else {
                    if (onSpeakStatusListener != null) {
                        onSpeakStatusListener.recorderDone(recorder.getRecordFile()
                                .getAbsolutePath(), (double) (recorder.getRecordTime() / 100) / 10);
                    }
                    recorder.onFinish();
                }
            } else {
                recorder.onDestroy();
                ToastUtil.showToast(getContext(), null, R.string.msg_err_recording___cm);
            }
        } else {
            ToastUtil.showToast(getContext(), null, R.string.msg_err_recording___cm);
        }
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

    /**
     * 录音状态监听
     */
    public interface OnSpeakStatusListener {

        /**
         * 录音成功
         *
         * @param filePath 音频文件
         * @param time     录音时长
         */
        void recorderDone(String filePath, double time);
    }
}
