package me.suncloud.marrymemo.view;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.fragment.RecordingFragment;
import me.suncloud.marrymemo.model.Audio;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.Music;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.V2.CardV2;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.task.QiNiuUploadTask;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.FileUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.TimeUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.RoundProgressDialog;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by Suncloud on 2015/12/26.
 */
@RuntimePermissions
public class CardMusicListActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<Music>, AdapterView.OnItemClickListener, RecordingFragment.RecordingListener {

    @BindView(R.id.list)
    ListView list;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private ArrayList<Music> musics;
    private ObjectBindAdapter<Music> adapter;
    private CardV2 cardV2;
    private Audio audio;
    private View recordView;
    private View fileView;
    private MediaPlayer mPlayer;
    private MediaPlayer mTimePlayer;
    private DialogFragment dialogFragment;
    private int recordTime;

    private RoundProgressDialog progressDialog;
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

    private ArrayList<Music> deleteMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit()
                .putLong("musicClickTime", System.currentTimeMillis())
                .apply();
        musics = new ArrayList<>();
        deleteMusic = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, musics, R.layout.music_list_item_new, this);
        cardV2 = (CardV2) getIntent().getSerializableExtra("cardV2");
        if (cardV2 != null && cardV2.getAudio() != null) {
            getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit()
                    .putLong("musicClickTimeV2", System.currentTimeMillis())
                    .apply();
            audio = cardV2.getAudio();
        } else {
            audio = new Audio(null);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        setSwipeBackEnable(false);
        setOkText(R.string.label_save);
        ButterKnife.bind(this);
        progressBar.setVisibility(View.GONE);

        View headerView = View.inflate(this, R.layout.music_list_header, null);
        recordView = View.inflate(this, R.layout.music_list_item_new, null);
        fileView = View.inflate(this, R.layout.music_list_item_new, null);
        setRecordView();
        setFileView();
        View headerView2 = View.inflate(this, R.layout.music_list_header2, null);
        View footer = View.inflate(this, R.layout.list_foot_no_more_2, null);

        list.addHeaderView(headerView);
        list.addHeaderView(recordView);
        list.addHeaderView(fileView);
        list.addHeaderView(headerView2, null, false);
        list.addFooterView(footer, null, false);
        list.setOnItemClickListener(this);
        list.setItemsCanFocus(true);
        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        list.setAdapter(adapter);
        getMusicList();
        switch (audio.getKind()) {
            case 1:
                list.setItemChecked(1, true);
                break;
            case 2:
                list.setItemChecked(2, true);
                break;
            case 3:
                list.setItemChecked(4, true);
                break;
            default:
                list.setItemChecked(0, true);
                break;
        }
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
                                CardMusicListActivity.super.onBackPressed();
                            }
                        });
                TextView tvMsg = (TextView) dialog.findViewById(R.id.tv_alert_msg);
                tvMsg.setText(R.string.hint_story_back);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                Point point = JSONUtil.getDeviceSize(this);
                params.width = Math.round(point.x * 27 / 32);
                window.setAttributes(params);
            }
            dialog.show();
        } else {
            super.onBackPressed();
        }
    }

    private void setRecordView() {
        ViewHolder holder = (ViewHolder) recordView.getTag();
        if (holder == null) {
            holder = new ViewHolder(recordView);
            holder.line.setVisibility(View.GONE);
            holder.name.setText(R.string.label_recording);
            holder.action.setText(R.string.label_rerecording);
            holder.action.setOnClickListener(new OnActionClickListener(1));
            recordView.setTag(holder);
        }
        if (!JSONUtil.isEmpty(audio.getRecordMusicPath())) {
            holder.time.setVisibility(View.VISIBLE);
            holder.iconArrow.setVisibility(View.GONE);
            holder.action.setVisibility(View.VISIBLE);
            mediaTimePrepare(audio.getRecordMusicPath(), holder.time);
        } else {
            holder.time.setVisibility(View.GONE);
            holder.iconArrow.setVisibility(View.VISIBLE);
            holder.action.setVisibility(View.GONE);
        }
    }

    private void setRecordTimeView(int currentPosition) {
        ViewHolder holder = (ViewHolder) recordView.getTag();
        if (holder == null) {
            holder = new ViewHolder(recordView);
            holder.line.setVisibility(View.GONE);
            holder.name.setText(R.string.label_recording);
            holder.action.setText(R.string.label_rerecording);
            holder.action.setOnClickListener(new OnActionClickListener(1));
            recordView.setTag(holder);
        }
        int minute1 = currentPosition / 60000;
        int second1 = (currentPosition / 1000) % 60;
        int minute2 = recordTime / 60000;
        int second2 = (recordTime / 1000) % 60;
        holder.time.setText(getString(R.string.label_time_down,
                minute1,
                second1,
                minute2,
                second2));
    }

    public void setFileView() {
        ViewHolder holder = (ViewHolder) fileView.getTag();
        if (holder == null) {
            holder = new ViewHolder(fileView);
            holder.iconArrow.setVisibility(View.VISIBLE);
            holder.action.setText(R.string.label_change);
            holder.action.setOnClickListener(new OnActionClickListener(2));
            fileView.setTag(holder);
        }
        if (!JSONUtil.isEmpty(audio.getFileMusicPath())) {
            holder.name.setText(audio.getFileMusicName());
            holder.iconArrow.setVisibility(View.GONE);
            holder.action.setVisibility(View.VISIBLE);
        } else {
            holder.name.setText(R.string.label_upload_sound);
            holder.iconArrow.setVisibility(View.VISIBLE);
            holder.action.setVisibility(View.GONE);
        }
    }

    private void getMusicList() {
        JSONArray array = null;
        if (getFileStreamPath(Constants.MUSICS_FILE) != null && getFileStreamPath(Constants
                .MUSICS_FILE).exists()) {
            try {
                InputStream in = openFileInput(Constants.MUSICS_FILE);
                String jsonStr = JSONUtil.readStreamToString(in);
                array = new JSONArray(jsonStr);
            } catch (FileNotFoundException | JSONException e) {
                e.printStackTrace();
            }

        }
        if (array == null || array.length() == 0) {
            try {
                array = (new JSONObject(JSONUtil.readStreamToString(getResources()
                        .openRawResource(R.raw.muisc)))).optJSONArray(
                        "musics");

            } catch (Resources.NotFoundException | JSONException e) {
                e.printStackTrace();
            }
        }
        if (array != null && array.length() > 0) {
            int size = array.length();
            for (int i = 0; i < size; i++) {
                Music music = new Music(array.optJSONObject(i));
                music.setId(0);
                if (audio.getKind() == 3 && audio.getClassicMusicPath()
                        .equals(music.getAudioPath())) {
                    if (audio.getClassicMusic() != null) {
                        audio.getClassicMusic()
                                .setM3u8Path(music.getM3u8Path());
                    }
                    musics.add(0, music);
                } else {
                    musics.add(music);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onOkButtonClick() {
        stopPlaying();
        if (progressDialog == null) {
            progressDialog = JSONUtil.getRoundProgress(this);
        }
        progressDialog.show();
        if (!JSONUtil.isEmpty(audio.getRecordMusicPath()) && !audio.getRecordMusicPath()
                .startsWith("http")) {
            progressDialog.setMessage(getString(R.string.msg_upload_record_music));
            upLoadMusic(audio.getRecordMusic());
        } else if (!JSONUtil.isEmpty(audio.getFileMusicPath()) && !audio.getFileMusicPath()
                .startsWith("http")) {
            progressDialog.setMessage(getString(R.string.msg_upload_file_music));
            upLoadMusic(audio.getFileMusic());
        } else {
            upLoadAudioInfo();
        }
        super.onOkButtonClick();
    }

    public void upLoadMusic(final Music music) {
        new QiNiuUploadTask(this, new OnHttpRequestListener() {

            @Override
            public void onRequestFailed(Object obj) {

            }

            @Override
            public void onRequestCompleted(Object obj) {
                if (isFinishing()) {
                    return;
                }
                JSONObject json = (JSONObject) obj;
                if (json != null) {
                    String domain = JSONUtil.getString(json, "domain");
                    String key = JSONUtil.getString(json, "audio_path");
                    String persistent_id = JSONUtil.getString(json, "persistent_id");
                    if (!JSONUtil.isEmpty(persistent_id)) {
                        music.setPersistentId(persistent_id);
                    }
                    if (!JSONUtil.isEmpty(domain) && !JSONUtil.isEmpty(key)) {
                        music.setAudioPath(domain + key);
                        if (progressDialog != null && progressDialog.isShowing()) {
                            onOkButtonClick();
                        }
                        return;
                    }
                }
                progressDialog.dismiss();
            }
        }, progressDialog).executeOnExecutor(Constants.UPLOADTHEADPOOL,
                Constants.getAbsUrl(cardV2 != null ? Constants.HttpPath.QINIU_AUDIO_V2_URL :
                        Constants.HttpPath.QINIU_AUDIO_URL),
                new File(music.getAudioPath()));
    }

    private void upLoadAudioInfo() {
        JSONArray jsonArray = new JSONArray();
        addDeleteJson(deleteMusic, jsonArray);
        addDeleteJson(audio.getOtherMusics(), jsonArray);
        addMusicJson(audio.getRecordMusic(), jsonArray, 1);
        addMusicJson(audio.getFileMusic(), jsonArray, 2);
        addMusicJson(audio.getClassicMusic(), jsonArray, 3);
        JSONObject jsonObject = new JSONObject();
        try {
            if (cardV2 != null) {
                jsonObject.put("id", cardV2.getId());
                jsonObject.put("theme_id", cardV2.getThemeId());
                jsonObject.put("title", cardV2.getTitle());
                jsonObject.put("groom_name", cardV2.getGroomName());
                jsonObject.put("bride_name", cardV2.getBrideName());
                jsonObject.put("time", TimeUtil.getCardDateStr(cardV2.getTime()));
                jsonObject.put("place", cardV2.getPlace());
                jsonObject.put("latitude", cardV2.getLatitude());
                jsonObject.put("longtitude", cardV2.getLongitude());
                jsonObject.put("map_type", cardV2.getMapType());
                jsonObject.put("speech", cardV2.getSpeech());
            }
            if (jsonArray.length() > 0) {
                jsonObject.put("audios_attributes", jsonArray);
            }
            progressDialog.onLoadComplate();
            new StatusHttpPostTask(this,
                    new StatusRequestListener() {
                        @Override
                        public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                            isChange = false;
                            if (cardV2 != null) {
                                cardV2 = new CardV2((JSONObject) object);
                                EventBus.getDefault()
                                        .post(new MessageEvent(MessageEvent.EventType
                                                .CARD_UPDATE_FLAG,
                                                cardV2));
                            }
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.setCancelable(false);
                                progressDialog.onComplate(new RoundProgressDialog
                                        .OnUpLoadComplate() {

                                    @Override
                                    public void onUpLoadCompleted() {
                                        onBackPressed();
                                    }
                                });
                            } else {
                                onBackPressed();
                            }
                        }

                        @Override
                        public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                            progressDialog.dismiss();
                            Util.postFailToast(CardMusicListActivity.this,
                                    returnStatus,
                                    R.string.msg_err_card_sound,
                                    network);
                        }
                    },
                    progressDialog).execute(Constants.getAbsUrl(cardV2 != null ? Constants
                            .HttpPath.CARD_V2_SAVE : Constants.HttpPath.UPDATE_CARD_URL),
                    jsonObject.toString());
        } catch (JSONException ignored) {
        }

    }

    private void addDeleteJson(ArrayList<Music> musics, JSONArray jsonArray) {
        if (musics != null && !musics.isEmpty()) {
            for (Music music : musics) {
                if (music.getId() > 0) {
                    if (audio.getClassicMusic() != null && audio.getClassicMusic()
                            .getId()
                            .equals(music.getId())) {
                        continue;
                    }
                    JSONObject musicJson = new JSONObject();
                    try {
                        musicJson.put("id", music.getId());
                        musicJson.put("_destroy", true);
                        musicJson.put("selected", false);
                        jsonArray.put(musicJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void addMusicJson(Music music, JSONArray jsonArray, int kind) {
        if (music != null && !JSONUtil.isEmpty(music.getAudioPath())) {
            JSONObject musicJson = new JSONObject();
            try {
                if (audio.getKind() == 3 || kind != 3) {
                    if (music.getId() > 0) {
                        musicJson.put("id", music.getId());
                    }
                    musicJson.put("name", music.getTitle());
                    musicJson.put("persistent_id", music.getPersistentId());
                    musicJson.put("audio_path", music.getAudioPath());
                    musicJson.put("kind", kind);
                    musicJson.put("selected", audio.getKind() == kind);
                } else if (music.getId() > 0) {
                    musicJson.put("id", music.getId());
                    musicJson.put("_destroy", true);
                    musicJson.put("selected", false);
                }
                jsonArray.put(musicJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setViewValue(View view, Music music, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.line.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
        holder.name.setText(music.getTitle());
        holder.name.getLayoutParams().width = 0;
        holder.name.requestLayout();
        holder.iconHot.setVisibility(music.isHot() ? View.VISIBLE : View.GONE);
        holder.iconNew.setVisibility(music.isNew() ? View.VISIBLE : View.GONE);

    }

    @Override
    public void onRecordDone(String filePtah) {
        if (!JSONUtil.isEmpty(filePtah)) {
            if (!JSONUtil.isEmpty(audio.getRecordMusicPath()) && !audio.getRecordMusicPath()
                    .startsWith("http")) {
                File file = new File(audio.getRecordMusicPath());
                if (file.exists()) {
                    FileUtil.deleteFile(file);
                }
            }
            isChange = true;
            Music music = new Music(new JSONObject());
            music.setAudioPath(filePtah);
            if (audio.getRecordMusic() != null && audio.getRecordMusic()
                    .getId() != 0) {
                deleteMusic.add(audio.getRecordMusic());
            }
            audio.setKind(1);
            audio.setRecordMusic(music);
            setRecordView();
            list.setItemChecked(1, true);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'music_list_item_new.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.line)
        View line;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.icon_hot)
        ImageView iconHot;
        @BindView(R.id.icon_new)
        ImageView iconNew;
        @BindView(R.id.icon_arrow)
        ImageView iconArrow;
        @BindView(R.id.action)
        TextView action;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0 || position == 3) {
            isChange = isChange | audio.getKind() != 0;
            stopPlaying();
            audio.setKind(0);
        } else if (position == 1) {
            if (!JSONUtil.isEmpty(audio.getRecordMusicPath())) {
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
        } else if (position == 2) {
            if (!JSONUtil.isEmpty(audio.getFileMusicPath())) {
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
                startActivityForResult(intent, Constants.RequestCode.AUDIO_FROM_PHONE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
            audio.setKind(2);
        } else {
            Music music = (Music) parent.getAdapter()
                    .getItem(position);
            if (music != null && !JSONUtil.isEmpty(music.getAudioPath())) {
                if (!music.getAudioPath()
                        .equals(audio.getCurrentPath()) || mPlayer == null) {
                    if (audio.getClassicMusic() != null && audio.getClassicMusic()
                            .getId() != 0) {
                        deleteMusic.add(audio.getClassicMusic());
                    }
                    isChange = isChange | !music.getAudioPath()
                            .equals(audio.getCurrentPath());
                    stopPlaying();
                    progressBar.setVisibility(View.VISIBLE);
                    mediaPlayerPrepare(JSONUtil.isEmpty(music.getM3u8Path()) ? music.getAudioPath
                                    () : music.getM3u8Path(),
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
        }
    }

    private class OnActionClickListener implements View.OnClickListener {

        private int kind;

        private OnActionClickListener(int kind) {
            this.kind = kind;
        }

        @Override
        public void onClick(View v) {
            stopPlaying();
            if (kind == 1) {
                CardMusicListActivityPermissionsDispatcher.onRecordAudioWithCheck(
                        CardMusicListActivity.this);
            } else if (kind == 2) {
                Intent intent = new Intent(CardMusicListActivity.this, MusicListActivity.class);
                startActivityForResult(intent, Constants.RequestCode.AUDIO_FROM_PHONE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null && requestCode == Constants.RequestCode
                .AUDIO_FROM_PHONE) {
            isChange = true;
            Music music = (Music) data.getSerializableExtra("music");
            if (audio.getFileMusic() != null && audio.getFileMusic()
                    .getId() != 0) {
                deleteMusic.add(audio.getFileMusic());
            }
            audio.setKind(2);
            audio.setFileMusic(music);
            setFileView();
            list.setItemChecked(2, true);
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
                    timeView.setText(getString(R.string.label_time_down,
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
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            handler.removeCallbacks(playRunnable);
        }
        super.onPause();
    }

    @Override
    protected void onFinish() {
        if (!JSONUtil.isEmpty(audio.getRecordMusicPath()) && !audio.getRecordMusicPath()
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
        super.onFinish();
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    void onRecordAudio() {
        if (dialogFragment == null) {
            dialogFragment = new RecordingFragment(this);
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
                getString(R.string.permission_r_for_record_audio));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        CardMusicListActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
