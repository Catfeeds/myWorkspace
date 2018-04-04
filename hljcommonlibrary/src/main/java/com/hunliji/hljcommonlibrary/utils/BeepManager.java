package com.hunliji.hljcommonlibrary.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;

import com.hunliji.hljcommonlibrary.R;

import java.io.IOException;

/**
 * Created by chen_bin on 2018/1/25 0025.
 */
public class BeepManager {
    private Context context;
    private MediaPlayer mediaPlayer;

    private final static float BEEP_VOLUME = 0.10f;
    private final static long VIBRATE_DURATION = 200L;

    public BeepManager(Context context) {
        this.context = context;
        this.mediaPlayer = buildMediaPlayer(context);
    }

    @SuppressLint("NewApi")
    private MediaPlayer buildMediaPlayer(Context context) {
        try (AssetFileDescriptor file = context.getResources()
                .openRawResourceFd(R.raw.beep)) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(),
                    file.getLength());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(false);
            mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
            mediaPlayer.prepare();
            return mediaPlayer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized void playBeepSoundAndVibrate(boolean playBeep, boolean vibrate) {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    public synchronized void close() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
