package com.hunliji.hljlivelibrary.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljhttplibrary.api.FileApi;
import com.hunliji.hljlivelibrary.models.LiveContent;
import com.hunliji.hljlivelibrary.models.LiveMessage;

import java.io.File;
import java.io.IOException;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by Suncloud on 2016/3/16.
 */
public class LiveVoicePlayUtil {

    public static final int PLAY = 1;
    public static final int FAIL = -1;
    public static final int DONE = 0;
    private static LiveVoicePlayUtil INSTANCE;
    private LiveMessage message;
    private LiveContent content;
    private int status;
    private MediaPlayer mPlayer;
    private VoiceStatusListener voiceStatusListener;
    private Subscription downloadSubscription;

    public static LiveVoicePlayUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LiveVoicePlayUtil();
        }
        return INSTANCE;
    }

    public LiveMessage getMessage() {
        return message;
    }

    public LiveContent getContent() {
        return content;
    }

    public void playVoice(
            Context context,
            final LiveMessage message,
            final LiveContent content,
            final VoiceStatusListener voiceStatusListener) {
        if (content == null || TextUtils.isEmpty(content.getVoicePath())) {
            return;
        }
        if (this.content != null && this.content == content) {
            onStop();
            return;
        } else {
            onStop();
        }
        this.message = message;
        this.content = content;
        this.voiceStatusListener = voiceStatusListener;
        String path = content.getVoicePath();
        File voiceFile = FileUtil.getUserVoiceFile(context, "live", path);
        if (voiceFile != null && voiceFile.exists() && voiceFile.isFile()) {
            status = PLAY;
            onPlay(voiceFile.getAbsolutePath());
            if (voiceStatusListener != null) {
                voiceStatusListener.onStatusChange(message, status);
            }
        } else if (voiceFile != null && (path.startsWith("http://") || path.startsWith
                ("https://"))) {
            status = PLAY;
            if (voiceStatusListener != null) {
                voiceStatusListener.onStatusChange(message, status);
            }
            if(downloadSubscription!=null&&!downloadSubscription.isUnsubscribed()){
                downloadSubscription.unsubscribe();
            }
            downloadSubscription=FileApi.download(path, voiceFile.getAbsolutePath())
                    .subscribe(new Subscriber<File>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            onStop();
                        }

                        @Override
                        public void onNext(File file) {
                            if (file != null && file.exists() && file.isFile()) {
                                onPlay(file.getAbsolutePath());
                            } else {
                                onStop();
                            }
                        }
                    });
        }
    }

    private void onPlay(String path) {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        } else {
            mPlayer.reset();
        }
        try {
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    onStop();
                }
            });
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (!mPlayer.isPlaying()) {
                        mPlayer.start();
                    }
                }
            });
            mPlayer.setDataSource(path);
            mPlayer.prepareAsync();
        } catch (IllegalArgumentException | SecurityException | IllegalStateException |
                IOException e) {
            mPlayer = null;
            status = DONE;
            e.printStackTrace();
        }

    }

    public void onStop() {
        if (downloadSubscription != null && !downloadSubscription.isUnsubscribed()) {
            downloadSubscription.unsubscribe();
        }
        status = DONE;
        if (voiceStatusListener != null) {
            voiceStatusListener.onStatusChange(message, status);
            voiceStatusListener = null;
        }
        this.message = null;
        this.content = null;
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    public int getStatus() {
        return status;
    }

    public boolean isPlay(LiveContent content){
        return status==PLAY&&this.content==content;
    }


    public interface VoiceStatusListener {
        void onStatusChange(LiveMessage message, int status);
    }
}
