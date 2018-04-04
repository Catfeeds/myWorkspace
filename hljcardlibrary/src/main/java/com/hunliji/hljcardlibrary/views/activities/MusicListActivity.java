package com.hunliji.hljcardlibrary.views.activities;


import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.MediaColumns;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcardlibrary.models.Music;
import com.hunliji.hljcardlibrary.models.MusicInfo;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class MusicListActivity extends HljBaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.list_view)
    PullToRefreshListView listView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private ArrayList<MusicInfo> musicInfoList;
    private MusicInfoListAdapter adapter;
    private MusicInfo chooseMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_list_view___cm);
        ButterKnife.bind(this);
        setOkText(R.string.label_done___cm);
        musicInfoList = new ArrayList<>();
        adapter = new MusicInfoListAdapter();
        listView.getRefreshableView()
                .setOnItemClickListener(this);
        listView.getRefreshableView()
                .setItemsCanFocus(true);
        listView.getRefreshableView()
                .setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.getRefreshableView()
                .setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        MusicListActivityPermissionsDispatcher.getMusicsWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void getMusics() {
        progressBar.setVisibility(View.GONE);
        new GetMusicsTask().execute();
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
        View emptyView = listView.getRefreshableView()
                .getEmptyView();
        if (emptyView == null) {
            this.emptyView.setHintId(R.string.err_permission_music__card);
            this.emptyView.showEmptyView();
            listView.getRefreshableView()
                    .setEmptyView(emptyView);
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
            Music music = new Music();
            music.setAudioPath(chooseMusic.getUrl());
            music.setName(chooseMusic.getName());
            Intent intent = getIntent();
            intent.putExtra("music", music);
            setResult(RESULT_OK, intent);
        }
        finish();
        super.onOkButtonClick();
    }

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
        return getString(R.string.label_music_time__card, min, sec);

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
            progressBar.setVisibility(View.GONE);
            if (!result.isEmpty()) {
                musicInfoList.addAll(result);
                adapter.notifyDataSetChanged();
            } else {
                View emptyView = listView.getRefreshableView()
                        .getEmptyView();
                if (emptyView == null) {
                    MusicListActivity.this.emptyView.setHintId(R.string
                            .hint_music_file_empty__card);
                    listView.getRefreshableView()
                            .setEmptyView(emptyView);
                }
            }
            super.onPostExecute(result);
        }

    }

    class ViewHolder {
        @BindView(R2.id.line)
        View line;
        @BindView(R2.id.name)
        TextView name;
        @BindView(R2.id.size)
        TextView size;
        @BindView(R2.id.duration)
        TextView duration;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class MusicInfoListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return musicInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return musicInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(MusicListActivity.this)
                        .inflate(R.layout.music_file_item__card, parent, false);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            MusicInfo info = musicInfoList.get(position);
            holder.line.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            holder.name.setText(info.getName());
            holder.duration.setText(millisFormat(info.getDuration()));
            holder.size.setText(info.getSize() + "M");
            return convertView;
        }
    }
}