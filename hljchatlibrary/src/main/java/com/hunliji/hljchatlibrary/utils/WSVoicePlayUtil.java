package com.hunliji.hljchatlibrary.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljhttplibrary.api.FileApi;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by Suncloud on 2016/3/16.
 */
public class WSVoicePlayUtil {

    public static final int PLAY = 1;
    public static final int FAIL = -1;
    public static final int DONE = 0;
    private static WSVoicePlayUtil INSTANCE;
    private WSChat chat;
    private int status;
    private MediaPlayer mPlayer;
    private VoiceStatusListener voiceStatusListener;
    private Subscription downloadSubscription;

    public static WSVoicePlayUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WSVoicePlayUtil();
        }
        return INSTANCE;
    }

    public WSChat getChat() {
        return chat;
    }

    public void playVoice(
            Context context,
            final WSChat chat,
            final VoiceStatusListener voiceStatusListener) {
        if (chat == null || chat.getMedia() == null || TextUtils.isEmpty(chat.getMedia()
                .getPath())) {
            return;
        }
        if (this.chat != null && this.chat == chat) {
            onStop();
            return;
        } else {
            onStop();
        }
        this.chat = chat;
        this.voiceStatusListener = voiceStatusListener;
        String path = chat.getMedia()
                .getPath();
        File voiceFile = null;
        if (!TextUtils.isEmpty(path)) {
            voiceFile = FileUtil.getUserVoiceFile(context, chat.getSessionName(), path);
        }
        if (voiceFile != null && voiceFile.exists() && voiceFile.isFile()) {
            status = PLAY;
            onPlay(voiceFile.getAbsolutePath());
            if (voiceStatusListener != null) {
                voiceStatusListener.onStatusChange(chat, status);
            }
        } else if (voiceFile != null && (path.startsWith("http://") || path.startsWith
                ("https://"))) {
            status = PLAY;
            if (voiceStatusListener != null) {
                voiceStatusListener.onStatusChange(chat, status);
            }
            if(downloadSubscription!=null&&!downloadSubscription.isUnsubscribed()){
                downloadSubscription.unsubscribe();
            }
            downloadSubscription=FileApi.download(path, voiceFile.getAbsolutePath()).subscribe(new Subscriber<File>() {
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
                        status = PLAY;
                        onPlay(file.getAbsolutePath());
                        if (voiceStatusListener != null) {
                            voiceStatusListener.onStatusChange(chat, status);
                        }
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
        if(downloadSubscription!=null&&!downloadSubscription.isUnsubscribed()){
            downloadSubscription.unsubscribe();
        }
        status = DONE;
        if (voiceStatusListener != null) {
            voiceStatusListener.onStatusChange(chat, status);
            voiceStatusListener = null;
        }
        this.chat = null;
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    public int getStatus() {
        return status;
    }


    public interface VoiceStatusListener {
        void onStatusChange(WSChat chat, int status);
    }
}
