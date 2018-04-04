package me.suncloud.marrymemo.view;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.MediaColumns;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter.ViewBinder;
import me.suncloud.marrymemo.model.Music;
import me.suncloud.marrymemo.model.MusicInfo;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MusicListActivity extends HljBaseActivity implements ViewBinder<MusicInfo>,
        OnItemClickListener {

    private ArrayList<MusicInfo> musicInfos;
    private ObjectBindAdapter<MusicInfo> adapter;
    private ListView list;
    private MusicInfo chooseMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        setOkText(R.string.label_done);
        musicInfos = new ArrayList<>();
        list = (ListView) findViewById(R.id.list);
        adapter = new ObjectBindAdapter<>(this, musicInfos, R.layout.music_file_item, this);
        list.setOnItemClickListener(this);
        list.setItemsCanFocus(true);
        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        list.setAdapter(adapter);
        MusicListActivityPermissionsDispatcher.getMusicsWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void getMusics() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        new GetMusicsTask().executeOnExecutor(Constants.LISTTHEADPOOL);
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MusicListActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onDeniedReadMusics() {
        View emptyView = list.getEmptyView();
        if (emptyView == null) {
            emptyView = findViewById(R.id.empty_hint_layout);
            TextView textView = (TextView) emptyView.findViewById(R.id.text_empty_hint);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.findViewById(R.id.img_empty_hint)
                    .setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            textView.setText("未获取权限不能读取音乐");
            list.setEmptyView(emptyView);
        }
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onRationale(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(com.hunliji.hljimagelibrary.R.string
                        .msg_permission_r_for_read_external_storage___cm));
    }


    @Override
    public void onOkButtonClick() {
        if (chooseMusic != null) {
            Music music = new Music(new JSONObject());
            music.setAudioPath(chooseMusic.getUrl());
            music.setTitle(chooseMusic.getName());
            Intent intent = getIntent();
            intent.putExtra("music", music);
            setResult(RESULT_OK, intent);
        }
        finish();
        super.onOkButtonClick();
    }

    @Override
    public void setViewValue(View view, MusicInfo t, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.line.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        holder.name.setText(t.getName());
        holder.duration.setText(millisFormat(t.getDuration()));
        holder.size.setText(t.getSize() + "M");

    }

    private String millisFormat(long millisecond) {
        int min = (int) (millisecond / 60000);
        int sec = (int) (millisecond % 60000) / 1000;
        return getString(R.string.label_music_time, min, sec);

    }

    @Override
    public void onItemClick(
            AdapterView<?> parent, View view, int position, long id) {
        MusicInfo info = (MusicInfo) parent.getAdapter()
                .getItem(position);
        if (info != null) {
            chooseMusic = info;
        }

    }

    private class GetMusicsTask extends AsyncTask<Object, Object, ArrayList<MusicInfo>> {
        private Uri contentUri = Media.EXTERNAL_CONTENT_URI;
        private String[] projection = {Media._ID, Media.DISPLAY_NAME, Media.DATA, Media.DURATION,
                Media.SIZE};
        private String where = (new StringBuilder().append(Media.SIZE + ">=" + 50 * 1024)
                .append(" AND " + MediaColumns.TITLE + " != ''")).toString();
        private String sortOrder = Media.DATA;

        @Override
        protected ArrayList<MusicInfo> doInBackground(Object... params) {
            ArrayList<MusicInfo> musicInfos = new ArrayList<>();
            Cursor cursor = getContentResolver().query(contentUri,
                    projection,
                    where,
                    null,
                    sortOrder);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        MusicInfo musicInfo = new MusicInfo(Long.valueOf(cursor.getString(0)),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getLong(3),
                                cursor.getInt(4));
                        musicInfos.add(musicInfo);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            return musicInfos;
        }

        @Override
        protected void onPostExecute(ArrayList<MusicInfo> result) {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            if (!result.isEmpty()) {
                musicInfos.addAll(result);
                adapter.notifyDataSetChanged();
            } else {
                View emptyView = list.getEmptyView();
                if (emptyView == null) {
                    emptyView = findViewById(R.id.empty_hint_layout);
                    ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                            .img_empty_hint);
                    TextView textEmptyHint = (TextView) emptyView.findViewById(R.id
                            .text_empty_hint);
                    textEmptyHint.setText(R.string.hint_music_file_empty);
                    textEmptyHint.setVisibility(View.VISIBLE);
                    imgEmptyHint.setVisibility(View.VISIBLE);
                    list.setEmptyView(emptyView);
                }
            }
            super.onPostExecute(result);
        }

    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'music_file_item.xml'
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
        @BindView(R.id.size)
        TextView size;
        @BindView(R.id.duration)
        TextView duration;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}