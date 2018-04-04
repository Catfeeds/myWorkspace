package com.hunliji.hljcommonlibrary.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Suncloud on 2016/3/10.
 */
public class SoundRecord {

    public static final int REFRESH = 1;
    public static final int RECORD_DONE = 2;
    public static final int RECORD_OVERTIME = 3;
    public static final int PERMISSION_ERR = -1;
    public static final int RECORD_ERR = -2;
    public static final int RECORD_DONE_ERR = -3;
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder;
    private File recordFile;
    private File tempFile;
    private Handler handler;
    private long recordTime;
    private long startTime;
    private boolean isRecording;
    private boolean permissionCheck;
    private long checkTime;
    private int state;
    private long maxTime;

    public SoundRecord(Handler handler) {
        this.handler = handler;
    }

    /**
     * 最大录音时间
     *
     * @param maxSec 单位为秒，最小为1
     */
    public void setMaxTime(int maxSec) {
        this.maxTime = maxSec * 1000;
    }

    public void startRecording(final Context mContext, String userName) throws IOException {
        startRecording(mContext, userName, false);
    }

    public void startRecording(
            final Context mContext, String userName, boolean isVoice) throws IOException {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        } else {
            mRecorder.reset();
        }
        if (TextUtils.isEmpty(userName) && isVoice) {
            userName = "temp";
        }
        if (TextUtils.isEmpty(userName)) {
            if (recordFile == null) {
                recordFile = FileUtil.createSoundFile(mContext);
                recordTime = 0;
            }
            if (tempFile == null) {
                tempFile = new File(FileUtil.getTemporaryFileName(mContext));
            }
        } else if (recordFile == null) {
            recordFile = FileUtil.createUserVoiceFile(mContext, userName);
            tempFile = recordFile;
            recordTime = 0;
        }
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        if (isVoice) {
            mRecorder.setAudioSamplingRate(8000);
            mRecorder.setAudioEncodingBitRate(64);
            mRecorder.setAudioChannels(1);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        } else {
            mRecorder.setAudioChannels(2);
            mRecorder.setAudioEncodingBitRate(131072);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        }
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(tempFile.getAbsolutePath());
        mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                startTime = 0;
                onRecordingStop();
                state = RECORD_ERR;
                if (handler != null) {
                    handler.sendEmptyMessage(RECORD_ERR);
                }
            }
        });
        mRecorder.prepare();
        mRecorder.start();
        startTime = 0;
        checkTime = 0;
        isRecording = true;
        permissionCheck = false;
        state = 0;
        new Thread(new Runnable() {
            int i = 0;

            public void run() {
                while (isRecording) {
                    if (!permissionCheck) {
                        i++;
                        if (i == 5) {
                            i = 0;
                            File anotherFile = tempFile;
                            if (anotherFile.exists() && anotherFile.length() > 0) {
                                if (isRecording) {
                                    mediaPlayerPrepare(tempFile.getAbsolutePath());
                                }
                            } else {
                                startTime = 0;
                                onRecordingStop();
                                state = PERMISSION_ERR;
                                if (handler != null) {
                                    handler.sendEmptyMessage(PERMISSION_ERR);
                                }
                                return;
                            }
                        }
                    } else {
                        i = 0;
                    }
                    int ratio = mRecorder.getMaxAmplitude() / 600;
                    int db = 0;
                    if (ratio > 1) {
                        db = (int) (80 * Math.log10(ratio));
                    }
                    int f = Math.min(100, db);
                    if (startTime == 0) {
                        startTime = System.currentTimeMillis();
                    }
                    long time = recordTime + System.currentTimeMillis() - startTime;
                    if (maxTime > 1000 && time >= maxTime) {
                        state = RECORD_OVERTIME;
                    } else {
                        state = REFRESH;
                    }
                    if (handler != null) {
                        Message message = new Message();
                        message.what = state;
                        message.arg1 = f;
                        message.arg2 = (int) (time / 1000);
                        handler.sendMessage(message);
                    }
                    SystemClock.sleep(100L);
                }
            }
        }).start();
    }

    private void mediaPlayerPrepare(String path) {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        } else {
            mPlayer.reset();
        }
        try {
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    onPlayStop();
                    permissionCheck = true;
                    return false;
                }
            });
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (checkTime != 0) {
                        permissionCheck = checkTime == mp.getDuration() && mp.getDuration() < 1000;
                    }
                    checkTime = mp.getDuration();
                    if (permissionCheck) {
                        startTime = 0;
                        onRecordingStop();
                        state = PERMISSION_ERR;
                        if (handler != null) {
                            handler.sendEmptyMessage(PERMISSION_ERR);
                        }
                    }
                    onPlayStop();
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


    public void stopRecording() {
        onRecordingStop();
        if (recordFile != tempFile) {
            new Thread(new Runnable() {
                public void run() {
                    boolean b = false;
                    try {
                        b = appendToFile(recordFile.getAbsolutePath(), tempFile.getAbsolutePath());
                    } catch (Exception ignored) {

                    }
                    if (b) {
                        state = RECORD_DONE;
                        handler.sendEmptyMessage(RECORD_DONE);
                    } else {
                        state = RECORD_DONE_ERR;
                        handler.sendEmptyMessage(RECORD_DONE_ERR);
                    }
                }
            }).start();
        } else {
            state = RECORD_DONE;
            if (handler != null) {
                handler.sendEmptyMessage(RECORD_DONE);
            }
        }

    }


    private void onPlayStop() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        onDestroy();
    }

    public void onDestroy() {
        onRecordingStop();
        onPlayStop();
        if (tempFile != null && tempFile.exists()) {
            FileUtil.deleteFile(tempFile);
            recordFile = null;
        }
        if (recordFile != null && recordFile.exists()) {
            FileUtil.deleteFile(recordFile);
            recordFile = null;
        }
    }

    private void onRecordingStop() {
        isRecording = false;
        if (mRecorder != null) {
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }
            recordTime = recordTime + System.currentTimeMillis() - startTime;
            startTime = 0;
            try {
                mRecorder.stop();
                mRecorder.release();
            } catch (Exception ignored) {
            } finally {
                mRecorder = null;
            }
        }
    }

    private boolean appendToFile(
            @NonNull final String targetFileName, @NonNull final String newFileName) {
        return Mp4ParserWrapper.append(targetFileName, newFileName);
    }

    public boolean isRecording() {
        return isRecording;
    }

    public long getRecordTime() {
        return recordTime;
    }

    public File getRecordFile() {
        if (recordFile != null && recordFile.exists() && recordFile.length() > 0) {
            return recordFile;
        }
        return null;
    }

    public void onFinish() {
        onRecordingStop();
        onPlayStop();
        if (tempFile != null && tempFile.exists()) {
            if (tempFile != recordFile) {
                FileUtil.deleteFile(tempFile);
            } else {
                tempFile = null;
            }
        }
        if (recordFile != null) {
            recordFile = null;
        }
    }

    public int getState() {
        return state;
    }
}
