package me.suncloud.marrymemo.fragment;

import android.app.Dialog;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.SoundRecord;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.RecordAmplitudeView;

/**
 * Created by Suncloud on 2015/12/30.
 */
public class RecordingFragment extends DialogFragment {

    @BindView(R.id.btn_close)
    TextView btnClose;
    @BindView(R.id.time_down)
    TextView timeDown;
    @BindView(R.id.time_down_layout)
    LinearLayout timeDownLayout;
    @BindView(R.id.btn_play)
    ImageButton btnPlay;
    @BindView(R.id.btn_record)
    ImageButton btnRecord;
    @BindView(R.id.btn_done)
    ImageButton btnDone;
    @BindView(R.id.hint)
    TextView hint;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.left)
    RecordAmplitudeView left;
    @BindView(R.id.right)
    RecordAmplitudeView right;

    private MediaPlayer mPlayer = null;
    private RecordingListener listener;
    private Dialog dialog;
    private SoundRecord soundRecord;
    private Unbinder unbinder;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SoundRecord.REFRESH:
                    left.addFloat((float) msg.arg1 / 100);
                    right.addFloat((float) msg.arg1 / 100);
                    timeDown.setText(secondsFormat(msg.arg2));
                    break;
                case SoundRecord.PERMISSION_ERR:
                    btnRecord.setImageResource(R.drawable.sl_icon_sound_record);
                    btnClose.setVisibility(View.VISIBLE);
                    hint.setText(R.string.hint_recording_permission);
                    break;
                case SoundRecord.RECORD_DONE:
                    btnPlay.setVisibility(View.VISIBLE);
                    btnDone.setVisibility(View.VISIBLE);
                    btnPlay.setImageResource(R.drawable.sl_icon_sound_play);
                case SoundRecord.RECORD_DONE_ERR:
                    progressBar.setVisibility(View.GONE);
                case SoundRecord.RECORD_ERR:
                    btnRecord.setImageResource(R.drawable.sl_icon_sound_record);
                    btnClose.setVisibility(View.VISIBLE);
                    hint.setText(R.string.hint_record3);
                    if (msg.what != SoundRecord.RECORD_DONE) {
                        Util.showToast(getActivity(), null, R.string.msg_err_recording);
                    }
                    break;
            }
            return false;
        }
    });

    public RecordingFragment() {
        super();
    }

    public RecordingFragment(RecordingListener listener) {
        super();
        this.listener = listener;
    }

    private Runnable playRunnable = new Runnable() {
        public void run() {
            handler.postDelayed(this, 100);
            int currentPosition = mPlayer.getCurrentPosition();
            left.setIndex(currentPosition / 100);
            right.setIndex(currentPosition / 100);
            timeDown.setText(secondsFormat(currentPosition / 1000));
        }
    };


    private String secondsFormat(int second) {
        int min = second / 60;
        int sec = second % 60;
        return getString(R.string.label_music_time, min, sec);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.bubble_dialog_fragment);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recording, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.dialog_anim_rise_style);
            getDialog().setCancelable(false);
            getDialog().setCanceledOnTouchOutside(false);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    @OnClick(R.id.btn_close)
    public void onClose() {
        onPlayPause();
        if (soundRecord != null && soundRecord.getRecordFile() != null) {
            if (dialog != null && dialog.isShowing()) {
                return;
            }
            if (dialog == null) {
                dialog = new Dialog(getActivity(), R.style.BubbleDialogTheme);
                dialog.setContentView(R.layout.dialog_confirm);
                dialog.findViewById(R.id.btn_cancel)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                            }
                        });
                dialog.findViewById(R.id.btn_confirm)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                                dismiss();
                            }
                        });
                TextView tvMsg = (TextView) dialog.findViewById(R.id.tv_alert_msg);
                tvMsg.setText(R.string.hint_recording_cancel);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                Point point = JSONUtil.getDeviceSize(getActivity());
                params.width = Math.round(point.x * 27 / 32);
                window.setAttributes(params);
            }
            dialog.show();
        } else {
            dismiss();
        }
    }

    @OnClick(R.id.btn_record)
    public void onRecord() {
        if (soundRecord != null && soundRecord.isRecording()) {
            progressBar.setVisibility(View.VISIBLE);
            soundRecord.stopRecording();
        } else {
            onPlayStop();
            if (soundRecord == null) {
                soundRecord = new SoundRecord(handler);
            }
            try {
                soundRecord.startRecording(getActivity(), null);
                timeDownLayout.setVisibility(View.VISIBLE);
                btnRecord.setImageResource(R.drawable.sl_icon_sound_record_stop);
                btnPlay.setVisibility(View.GONE);
                btnDone.setVisibility(View.GONE);
                btnClose.setVisibility(View.GONE);
                hint.setText(R.string.hint_record2);
                timeDown.setText(secondsFormat((int) (soundRecord.getRecordTime() / 1000)));
            } catch (IOException e) {
                Util.showToast(getActivity(), null, R.string.msg_err_recording);
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.btn_play)
    public void onPlay() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            onPlayPause();
        } else {
            onPlayStart();
        }
    }

    @OnClick(R.id.btn_done)
    public void onDone() {
        if (listener != null && soundRecord != null && soundRecord.getRecordFile() != null) {
            listener.onRecordDone(soundRecord.getRecordFile()
                    .getAbsolutePath());
            soundRecord.onFinish();
        }
        dismiss();
    }

    private void onPlayStart() {
        if (mPlayer != null) {
            handler.post(playRunnable);
            mPlayer.start();
            btnPlay.setImageResource(R.drawable.sl_icon_sound_stop);
        } else if (soundRecord != null && soundRecord.getRecordFile() != null) {
            progressBar.setVisibility(View.VISIBLE);
            mediaPlayerPrepare(soundRecord.getRecordFile()
                    .getAbsolutePath());
        }
    }

    private void onPlayPause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            handler.removeCallbacks(playRunnable);
            mPlayer.pause();
            btnPlay.setImageResource(R.drawable.sl_icon_sound_play);
        }
    }

    private void onPlayStop() {
        progressBar.setVisibility(View.GONE);
        if (mPlayer != null) {
            handler.removeCallbacks(playRunnable);
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            btnPlay.setImageResource(R.drawable.sl_icon_sound_play);
        }
    }


    private void mediaPlayerPrepare(String path) {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        } else {
            mPlayer.reset();
        }
        try {
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    btnPlay.setImageResource(R.drawable.sl_icon_sound_play);
                    handler.removeCallbacks(playRunnable);
                    timeDown.setText(secondsFormat(mPlayer.getDuration() / 1000));
                }
            });
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    onPlayStop();
                    return false;
                }
            });
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    progressBar.setVisibility(View.GONE);
                    btnPlay.setImageResource(R.drawable.sl_icon_sound_stop);
                    if (!mPlayer.isPlaying()) {
                        mPlayer.start();
                        handler.post(playRunnable);
                    }
                }
            });
            mPlayer.setDataSource(path);
            mPlayer.prepareAsync();
        } catch (IllegalArgumentException | SecurityException | IllegalStateException |
                IOException e) {
            mPlayer = null;
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroyView() {
        onPlayStop();
        if (soundRecord != null) {
            soundRecord.onDestroy();
        }
        super.onDestroyView();
        unbinder.unbind();
    }

    public interface RecordingListener {
        void onRecordDone(String filePtah);
    }

}
