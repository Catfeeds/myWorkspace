package com.hunliji.hljcardlibrary.views.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.Audio;
import com.hunliji.hljcardlibrary.models.CardRxEvent;
import com.hunliji.hljcardlibrary.models.EditAudioBody;
import com.hunliji.hljcardlibrary.models.Music;
import com.hunliji.hljcardlibrary.models.wrappers.MusicNotifyWrapper;
import com.hunliji.hljcardlibrary.views.fragments.CardMusicListFragment;
import com.hunliji.hljcardlibrary.views.fragments.RecordingFragment;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.entities.HljUploadListener;
import com.hunliji.hljhttplibrary.entities.HljUploadResult;
import com.hunliji.hljhttplibrary.utils.HljFileUploadBuilder;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func2;
import rx.internal.util.SubscriptionList;


/**
 * Created by Suncloud on 2015/12/26.
 */
@RuntimePermissions
public class CardMusicListActivity extends HljBaseActivity implements RecordingFragment
        .RecordingListener, TabPageIndicator.OnTabChangeListener, CardMusicListFragment
        .OnMusicCallBack {

    public static final int AUDIO_FROM_PHONE = 86;
    public static final long ALL_MARK_ID = 0L;

    @BindView(R2.id.tab_indicator)
    TabPageIndicator tabIndicator;
    @BindView(R2.id.view_pager)
    ViewPager viewPager;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private MediaPlayer mPlayer;
    private MediaPlayer mTimePlayer;
    private DialogFragment dialogFragment;
    private int recordTime;

    private HljRoundProgressDialog progressDialog;
    private Dialog dialog;
    private boolean isChange;

    private Handler handler = new Handler();
    private Runnable playRunnable = new Runnable() {
        public void run() {
            handler.postDelayed(this, 1000);
            recordTime = mPlayer.getDuration();
            setRecordTimeView(mPlayer.getCurrentPosition());
        }
    };

    private HljHttpSubscriber initSubscriber;
    private List<CardMusicListFragment> fragments;
    private List<Mark> marks;
    private MarkFragmentAdapter fragmentAdapter;
    private Mark allMark;
    private SubscriptionList uploadSubscriptionList;
    private HljHttpSubscriber editInfoSubscriber;
    private long cardId;
    private Audio audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cardId = getIntent().getLongExtra("cardId", 0L);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list__card);
        ButterKnife.bind(this);

        initConstants();
        initWidget();
        initLoad();
    }

    private void initConstants() {
        marks = new ArrayList<>();
        fragments = new ArrayList<>();
        fragmentAdapter = new MarkFragmentAdapter(getSupportFragmentManager());
        allMark = new Mark();
        allMark.setName("全部");
        allMark.setId(ALL_MARK_ID);
        audio = new Audio();
    }

    private void initLoad() {
        initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                    @Override
                    public void onNext(ResultZip resultZip) {
                        setResultZip(resultZip);
                    }
                })
                .build();
        Observable<HljHttpData<List<Mark>>> markObb = CardApi.getMusicMark();
        Observable<HljHttpData<List<Music>>> musicObb = CardApi.getCardMusic(cardId);

        Observable<ResultZip> zipObb = Observable.zip(markObb,
                musicObb,
                new Func2<HljHttpData<List<Mark>>, HljHttpData<List<Music>>, ResultZip>() {
                    @Override
                    public ResultZip call(
                            HljHttpData<List<Mark>> listDataMark,
                            HljHttpData<List<Music>> listDataMusic) {
                        ResultZip zip = new ResultZip();
                        zip.listDataMark = listDataMark;
                        zip.listDataMusic = listDataMusic;
                        return zip;
                    }
                });

        zipObb.subscribe(initSubscriber);
    }

    private void setResultZip(ResultZip zip) {
        if (zip == null) {
            return;
        }

        HljHttpData<List<Music>> listDataMusic = zip.listDataMusic;
        if (listDataMusic != null && listDataMusic.getData() != null) {
            List<Music> musics = listDataMusic.getData();
            int kind = 0;
            for (Music music : musics) {
                if (music.isSelected()) {
                    kind = music.getKind();
                }
                if (music.getKind() == 1) {
                    //录音
                    audio.setRecordMusic(music);
                } else if (music.getKind() == 2) {
                    //本地音乐
                    audio.setFileMusic(music);
                } else if (music.getKind() == 3) {
                    //线上音乐
                    audio.setClassicMusic(music);
                }
            }
            audio.setKind(kind);
        }

        HljHttpData<List<Mark>> listDataMark = zip.listDataMark;
        if (listDataMark != null && listDataMark.getData() != null) {
            marks.clear();
            marks.add(allMark);
            marks.addAll(listDataMark.getData());
            for (int i = 0, size = marks.size(); i < size; i++) {
                Mark mark = marks.get(i);
                fragments.add(CardMusicListFragment.newInstance(mark.getId(), audio));
            }
            tabIndicator.setPagerAdapter(fragmentAdapter);
            fragmentAdapter.notifyDataSetChanged();
            viewPager.setOffscreenPageLimit(fragments.size());
        }
    }

    private void initWidget() {
        setSwipeBackEnable(false);
        setOkText(R.string.label_save___cm);
        tabIndicator.setTabViewId(R.layout.menu_tab_view3___cm);
        tabIndicator.setOnTabChangeListener(this);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabIndicator.setCurrentItem(position);
            }
        });
    }

    @Override
    public void onBackPressed() {
        stopPlaying();
        if (isChange) {
            if (dialog != null && dialog.isShowing()) {
                return;
            }
            if (dialog == null) {
                dialog = new Dialog(this, R.style.BubbleDialogTheme);
                dialog.setContentView(R.layout.hlj_dialog_confirm___cm);
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
                                CardMusicListActivity.super.onBackPressed();
                            }
                        });
                TextView tvMsg = (TextView) dialog.findViewById(R.id.tv_alert_msg);
                tvMsg.setText(R.string.hint_story_back__card);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                Point point = CommonUtil.getDeviceSize(this);
                params.width = Math.round(point.x * 27 / 32);
                window.setAttributes(params);
            }
            dialog.show();
        } else {
            super.onBackPressed();
        }
    }

    private void setRecordView() {
        CardMusicListFragment fragment = getAllMarkFragment();
        if (fragment != null) {
            fragment.setRecordView(audio);
        }
    }

    private void setRecordTimeView(int currentPosition) {
        CardMusicListFragment fragment = getAllMarkFragment();
        if (fragment != null) {
            fragment.setRecordTimeView(recordTime, currentPosition);
        }
    }

    public void setFileView() {
        CardMusicListFragment fragment = getAllMarkFragment();
        if (fragment != null) {
            fragment.setFileView(audio);
        }
    }

    //返回全部标签的那个fragment
    private CardMusicListFragment getAllMarkFragment() {
        if (isFinishing()) {
            return null;
        }
        for (CardMusicListFragment fragment : fragments) {
            if (fragment.getMarkId() == ALL_MARK_ID) {
                return fragment;
            }
        }
        return null;
    }

    @Override
    public void onOkButtonClick() {
        stopPlaying();
        if (progressDialog == null) {
            progressDialog = new HljRoundProgressDialog(this, R.style.BubbleDialogTheme);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setProgress(0);
            progressDialog.setMax(100);
        }
        progressDialog.show();
        if (!CommonUtil.isEmpty(audio.getRecordMusicPath()) && !audio.getRecordMusicPath()
                .startsWith("http")) {
            progressDialog.setMessage(getString(R.string.msg_upload_record_music__card));
            upLoadMusic(audio.getRecordMusic());
        } else if (!CommonUtil.isEmpty(audio.getFileMusicPath()) && !audio.getFileMusicPath()
                .startsWith("http")) {
            progressDialog.setMessage(getString(R.string.msg_upload_file_music__card));
            upLoadMusic(audio.getFileMusic());
        } else {
            upLoadAudioInfo();
        }
        super.onOkButtonClick();
    }

    public void upLoadMusic(final Music music) {
        Subscription uploadSubscription = new HljFileUploadBuilder(new File(music.getAudioPath())
        ).host(
                HljCard.getCardHost())
                .tokenPath(HljFileUploadBuilder.QINIU_VOICE_URL,
                        HljFileUploadBuilder.UploadFrom.CARD_AUDIO)
                .progressListener(new FileUpLoadListener())
                .build()
                .subscribe(new Subscriber<HljUploadResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(HljUploadResult hljUploadResult) {
                        if (hljUploadResult != null) {
                            progressDialog.setMessage("上传完成");
                            String persistent_id = hljUploadResult.getPersistentId();
                            String path = hljUploadResult.getUrl();
                            if (!TextUtils.isEmpty(persistent_id)) {
                                music.setPersistentId(persistent_id);
                            }
                            if (!TextUtils.isEmpty(path)) {
                                music.setAudioPath(path);
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    onOkButtonClick();
                                }
                                return;
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
        if (uploadSubscriptionList == null) {
            uploadSubscriptionList = new SubscriptionList();
        }
        uploadSubscriptionList.add(uploadSubscription);
    }

    private void upLoadAudioInfo() {
        progressDialog.onProgressFinish();
        if (editInfoSubscriber != null && !editInfoSubscriber.isUnsubscribed()) {
            return;
        }
        editInfoSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                    @Override
                    public void onNext(HljHttpResult hljHttpResult) {
                        if (hljHttpResult == null) {
                            return;
                        }
                        HljHttpStatus status = hljHttpResult.getStatus();
                        if (status != null && status.getRetCode() == 0) {
                            isChange = false;
                            RxBus.getDefault()
                                    .post(new CardRxEvent(CardRxEvent.RxEventType.CARD_MUSIC_EDIT,
                                            audio));
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.setCancelable(false);
                                progressDialog.onComplete(new HljRoundProgressDialog
                                        .OnCompleteListener() {
                                    @Override
                                    public void onCompleted() {
                                        onBackPressed();
                                    }
                                });
                            } else {
                                onBackPressed();
                            }
                        } else {
                            progressDialog.dismiss();
                            ToastUtil.showToast(CardMusicListActivity.this,
                                    null,
                                    R.string.msg_err_card_sound__card);
                        }
                    }
                })
                .build();

        List<Music> musics = new ArrayList<>();
        if (audio.getClassicMusic() != null) {
            audio.getClassicMusic()
                    .setSelected(audio.getKind() == 3);
            audio.getClassicMusic()
                    .setKind(3);
            musics.add(audio.getClassicMusic());
        }
        if (audio.getRecordMusic() != null) {
            audio.getRecordMusic()
                    .setSelected(audio.getKind() == 1);
            audio.getRecordMusic()
                    .setKind(1);
            musics.add(audio.getRecordMusic());
        }
        if (audio.getFileMusic() != null) {
            audio.getFileMusic()
                    .setSelected(audio.getKind() == 2);
            audio.getFileMusic()
                    .setKind(2);
            musics.add(audio.getFileMusic());
        }
        EditAudioBody body = new EditAudioBody();
        body.setId(cardId);
        body.setAudios(musics);
        Observable<HljHttpResult> obb = CardApi.editAudioInfo(body);
        obb.subscribe(editInfoSubscriber);
    }

    @Override
    public void onRecordDone(String filePtah) {
        if (!CommonUtil.isEmpty(filePtah)) {
            if (!CommonUtil.isEmpty(audio.getRecordMusicPath()) && !audio.getRecordMusicPath()
                    .startsWith("http")) {
                File file = new File(audio.getRecordMusicPath());
                if (file.exists()) {
                    FileUtil.deleteFile(file);
                }
            }
            isChange = true;
            Music music = new Music();
            music.setAudioPath(filePtah);
            audio.setKind(1);
            audio.setRecordMusic(music);
            setRecordView();
        }
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onItemInitCheck(Music music) {
        if (music != null && !CommonUtil.isEmpty(music.getAudioPath())) {
            if (!music.getAudioPath()
                    .equals(audio.getCurrentPath()) || mPlayer == null) {
                isChange = isChange | !music.getAudioPath()
                        .equals(audio.getCurrentPath());
            }
            audio.setKind(3);
            audio.setClassicMusic(music);
        }
    }

    @Override
    public void onMusicClick(Music music, long markId) {
        if (music != null && !CommonUtil.isEmpty(music.getAudioPath())) {
            if (!music.getAudioPath()
                    .equals(audio.getCurrentPath()) || mPlayer == null) {
                isChange = isChange | !music.getAudioPath()
                        .equals(audio.getCurrentPath());
                stopPlaying();
                progressBar.setVisibility(View.VISIBLE);
                mediaPlayerPrepare(CommonUtil.isEmpty(music.getM3u8Path()) ? music.getAudioPath()
                                : music.getM3u8Path(),
                        false);
            } else if (mPlayer.isPlaying()) {
                mPlayer.pause();
            } else {
                mPlayer.start();
            }
            audio.setKind(3);
            audio.setClassicMusic(music);
        } else {
            stopPlaying();
        }
        RxBus.getDefault()
                .post(new RxEvent(RxEvent.RxEventType.NOTIFY_CARD_MUSIC,
                        new MusicNotifyWrapper(audio.getCurrentPath(), markId)));
    }

    class ViewHolder {
        @BindView(R2.id.line)
        View line;
        @BindView(R2.id.name)
        TextView name;
        @BindView(R2.id.time)
        TextView time;
        @BindView(R2.id.icon_hot)
        ImageView iconHot;
        @BindView(R2.id.icon_new)
        ImageView iconNew;
        @BindView(R2.id.icon_arrow)
        ImageView iconArrow;
        @BindView(R2.id.action)
        TextView action;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onNoMusic() {
        isChange = isChange | audio.getKind() != 0;
        stopPlaying();
        audio.setKind(0);
    }

    @Override
    public void onRecord() {
        if (!CommonUtil.isEmpty(audio.getRecordMusicPath())) {
            if (!audio.getRecordMusicPath()
                    .equals(audio.getCurrentPath()) || mPlayer == null) {
                isChange = isChange | audio.getKind() != 1;
                stopPlaying();
                progressBar.setVisibility(View.VISIBLE);
                mediaPlayerPrepare(audio.getRecordMusicPath(), true);
            } else if (mPlayer.isPlaying()) {
                mPlayer.pause();
                handler.removeCallbacks(playRunnable);
            } else {
                mPlayer.start();
                handler.post(playRunnable);
            }
        } else {
            stopPlaying();
            CardMusicListActivityPermissionsDispatcher.onRecordAudioWithCheck(this);
        }
        audio.setKind(1);
    }

    @Override
    public void onUpload() {
        if (!CommonUtil.isEmpty(audio.getFileMusicPath())) {
            if (!audio.getFileMusicPath()
                    .equals(audio.getCurrentPath()) || mPlayer == null) {
                isChange = isChange | audio.getKind() != 2;
                stopPlaying();
                progressBar.setVisibility(View.VISIBLE);
                mediaPlayerPrepare(audio.getFileMusicPath(), false);
            } else if (mPlayer.isPlaying()) {
                mPlayer.pause();
            } else {
                mPlayer.start();
            }
        } else {
            stopPlaying();
            Intent intent = new Intent(this, MusicListActivity.class);
            startActivityForResult(intent, AUDIO_FROM_PHONE);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
        audio.setKind(2);
    }

    @Override
    public void onActionClick(int kind) {
        stopPlaying();
        if (kind == 1) {
            CardMusicListActivityPermissionsDispatcher.onRecordAudioWithCheck
                    (CardMusicListActivity.this);
        } else if (kind == 2) {
            Intent intent = new Intent(CardMusicListActivity.this, MusicListActivity.class);
            startActivityForResult(intent, AUDIO_FROM_PHONE);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    public void onMediaTimePrepare(String path, TextView timeView) {
        mediaTimePrepare(path, timeView);
    }

    @Override
    public Audio getAudio() {
        return audio;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null && requestCode == AUDIO_FROM_PHONE) {
            isChange = true;
            Music music = data.getParcelableExtra("music");
            audio.setKind(2);
            audio.setFileMusic(music);
            setFileView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void stopPlaying() {
        progressBar.setVisibility(View.GONE);
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        if (recordTime > 0) {
            setRecordTimeView(recordTime);
        }
        handler.removeCallbacks(playRunnable);
    }

    private void mediaPlayerPrepare(String path, final boolean isRecord) {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        } else {
            mPlayer.reset();
        }
        try {
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (isRecord) {
                        handler.removeCallbacks(playRunnable);
                        setRecordTimeView(recordTime);
                    }
                }
            });
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    progressBar.setVisibility(View.GONE);
                    if (!mPlayer.isPlaying()) {
                        mPlayer.start();
                        if (isRecord) {
                            handler.post(playRunnable);
                        }
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

    private void mediaTimePrepare(String path, final TextView timeView) {
        if (mTimePlayer == null) {
            mTimePlayer = new MediaPlayer();
        } else {
            mTimePlayer.reset();
        }
        try {
            mTimePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    progressBar.setVisibility(View.GONE);
                    recordTime = mp.getDuration();
                    int minute = mp.getDuration() / 60000;
                    int second = (mp.getDuration() / 1000) % 60;
                    timeView.setText(getString(R.string.label_time_down__card,
                            minute,
                            second,
                            minute,
                            second));
                    mTimePlayer.stop();
                    mTimePlayer.release();
                    mTimePlayer = null;
                }
            });
            mTimePlayer.setDataSource(path);
            mTimePlayer.prepareAsync();
        } catch (IllegalArgumentException | SecurityException | IllegalStateException |
                IOException e) {
            mTimePlayer = null;
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            handler.removeCallbacks(playRunnable);
        }
    }

    @Override
    protected void onFinish() {
        if (!CommonUtil.isEmpty(audio.getRecordMusicPath()) && !audio.getRecordMusicPath()
                .startsWith("http")) {
            File file = new File(audio.getRecordMusicPath());
            if (file.exists()) {
                FileUtil.deleteFile(file);
            }
        }
        stopPlaying();
        if (mTimePlayer != null) {
            mTimePlayer.stop();
            mTimePlayer.release();
            mTimePlayer = null;
        }
        CommonUtil.unSubscribeSubs(initSubscriber, uploadSubscriptionList, editInfoSubscriber);
        super.onFinish();
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    void onRecordAudio() {
        if (dialogFragment == null) {
            dialogFragment = new RecordingFragment();
            dialogFragment.setCancelable(false);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(dialogFragment, "dialogFragment");
        ft.commitAllowingStateLoss();
    }

    @OnShowRationale(Manifest.permission.RECORD_AUDIO)
    void onRationaleRecordAudio(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.msg_permission_r_for_record_audio___cm));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        CardMusicListActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    class MarkFragmentAdapter extends FragmentStatePagerAdapter {

        public MarkFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return marks.get(position)
                    .getName();
        }
    }

    class FileUpLoadListener implements HljUploadListener {

        private long contentLength;

        @Override
        public void transferred(long transBytes) {
            int progress;
            if (contentLength == 0) {
                progress = 0;
            } else {
                progress = (int) (transBytes * 1.0D / contentLength * 100);
            }
            progressDialog.setProgress(progress);
        }

        @Override
        public void setContentLength(long contentLength) {
            this.contentLength = contentLength;
        }
    }

    class ResultZip {
        HljHttpData<List<Mark>> listDataMark;
        HljHttpData<List<Music>> listDataMusic;
    }
}
