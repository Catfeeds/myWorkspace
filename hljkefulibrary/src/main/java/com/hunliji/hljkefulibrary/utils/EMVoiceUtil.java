package com.hunliji.hljkefulibrary.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;

import com.hunliji.hljkefulibrary.moudles.EMChat;
import com.hyphenate.chat.ChatClient;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/3/13.
 */

public class EMVoiceUtil {

    private static EMVoiceUtil INSTANCE;
    private Map<String, Subscription> downloadSubscriptions;

    private MediaPlayer mPlayer;
    private Uri currentSource;
    private PlayStatusListener playListener;

    public static EMVoiceUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EMVoiceUtil();
        }
        return INSTANCE;
    }

    public synchronized File getLocalFile(Context context, final EMChat chat) {
        File voiceFile = null;
        if (!TextUtils.isEmpty(chat.getVoicePath())) {
            voiceFile = new File(chat.getVoicePath());
        }
        if (voiceFile != null && voiceFile.exists() && voiceFile.isFile()) {
            return voiceFile;
        } else if (!TextUtils.isEmpty(chat.getRemoteUrl())) {
            final String path = chat.getRemoteUrl();
            if (downloadSubscriptions == null) {
                downloadSubscriptions = new HashMap<>();
            }
            Subscription downloadSubscription = downloadSubscriptions.get(path);
            if (downloadSubscription != null && !downloadSubscription.isUnsubscribed()) {
                return null;
            }
            downloadSubscription = Observable.create(new Observable.OnSubscribe<Object>() {
                @Override
                public void call(Subscriber<? super Object> subscriber) {
                    ChatClient.getInstance()
                            .chatManager()
                            .downloadAttachment(chat.getMessage());
                    subscriber.onCompleted();
                }
            })
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<Object>() {
                        @Override
                        public void onCompleted() {
                            downloadSubscriptions.remove(path);
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            downloadSubscriptions.remove(path);

                        }

                        @Override
                        public void onNext(Object o) {

                        }
                    });
            downloadSubscriptions.put(path, downloadSubscription);
        }
        return null;
    }

    public void onVoicePlayer(Context context, File file, PlayStatusListener listener) {
        if (!file.exists() || !file.isFile()) {
            return;
        }
        Uri playSource = Uri.fromFile(file);
        if (currentSource != null && currentSource.equals(playSource)) {
            onStop();
            return;
        }
        onStop();
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        } else {
            mPlayer.reset();
        }
        try {
            currentSource = playSource;
            this.playListener = listener;
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    onStop();
                }
            });
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    onStop();
                    return false;
                }
            });
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (!mp.isPlaying()) {
                        mp.start();
                        playListener.onStart();
                    }
                }
            });
            mPlayer.setDataSource(context, playSource);
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPlayListener(File file, PlayStatusListener playListener) {
        if (file == null || !file.exists() || !file.isFile()) {
            playListener.onStop();
            return;
        }
        Uri fileUri = Uri.fromFile(file);
        if (currentSource != null && currentSource.equals(fileUri)) {
            if (mPlayer != null && mPlayer.isPlaying()) {
                this.playListener = playListener;
                this.playListener.onStart();
            } else {
                playListener.onStop();
            }
        } else {
            playListener.onStop();
        }
    }

    public void onStop() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        currentSource = null;
        if (playListener != null) {
            playListener.onStop();
        }
    }

    public interface PlayStatusListener {
        void onStop();

        void onStart();
    }
}
