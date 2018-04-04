package com.hunliji.marrybiz.view;

import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ObjectBindAdapter;
import com.hunliji.marrybiz.model.Comment;
import com.hunliji.marrybiz.model.Photo;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Suncloud on 2014/11/22.
 */
public class WorkCommentListActivity extends HljBaseActivity implements AbsListView
        .OnScrollListener, PullToRefreshBase.OnRefreshListener<ListView>, ObjectBindAdapter
        .ViewBinder<Comment> {

    private PullToRefreshListView listView;
    private ArrayList<Comment> comments;
    private ObjectBindAdapter<Comment> adapter;
    private boolean isEnd;
    private boolean isLoad;
    private View endView;
    private View loadView;
    private View progressBar;
    private int currentPage;
    private DateFormat dateFormatter;
    private long id;
    private int spacing;
    private int pluralWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        id = getIntent().getLongExtra("w_id", 0);
        comments = new ArrayList<>();
        Point point = JSONUtil.getDeviceSize(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        spacing = Math.round(2 * dm.density);
        pluralWidth = Math.round(point.x / 5);
        adapter = new ObjectBindAdapter<>(this, comments, R.layout.comment_list_item, this);
        View footerView = View.inflate(this, R.layout.list_foot_no_more_2, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        progressBar = findViewById(R.id.progressBar);
        listView = (PullToRefreshListView) findViewById(R.id.list_view);
        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.setOnScrollListener(this);
        listView.setOnRefreshListener(this);
        listView.setAdapter(adapter);
        currentPage = 1;
        new GetCommentsTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                Constants.getAbsUrl(Constants.HttpPath.WORK_COMMENTS_URL, currentPage, 20, id));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }


    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            currentPage = 1;
            new GetCommentsTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(Constants.getAbsUrl(Constants.HttpPath.WORK_COMMENTS_URL),
                            id,
                            currentPage,
                            20));
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    loadView.setVisibility(View.VISIBLE);
                    currentPage++;
                    new GetCommentsTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                            String.format(Constants.getAbsUrl(Constants.HttpPath.WORK_COMMENTS_URL),
                                    id,
                                    currentPage,
                                    20));
                } else {
                    break;
                }
        }
    }

    @Override
    public void onScroll(AbsListView view, int i, int i2, int i3) {

    }

    @Override
    public void setViewValue(View view, Comment comment, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.userIcon = (ImageView) view.findViewById(R.id.user_icon);
            holder.userName = (TextView) view.findViewById(R.id.user_name);
            holder.content = (TextView) view.findViewById(R.id.content);
            holder.timeView = (TextView) view.findViewById(R.id.time);
            holder.imageGrid = (GridView) view.findViewById(R.id.images_layout);
            holder.describeRating = (TextView) view.findViewById(R.id.other_rating1);
            holder.attitudeRating = (TextView) view.findViewById(R.id.other_rating2);
            holder.qualityRating = (TextView) view.findViewById(R.id.other_rating3);
            holder.imageGrid.getLayoutParams().width = pluralWidth * 3 + spacing * 2;
            holder.photos = new ArrayList<>();
            holder.photoAdapter = new ObjectBindAdapter<>(WorkCommentListActivity.this,
                    holder.photos,
                    R.layout.thread_photos_item,
                    new ObjectBindAdapter.ViewBinder<Photo>() {

                        @Override
                        public void setViewValue(View view, Photo t, int position) {
                            PhotoHolder photoHolder = (PhotoHolder) view.getTag();
                            if (photoHolder == null) {
                                photoHolder = new PhotoHolder();
                                photoHolder.imageView = (ImageView) view.findViewById(R.id.photo);
                                ViewGroup.MarginLayoutParams params = (ViewGroup
                                        .MarginLayoutParams) photoHolder.imageView
                                        .getLayoutParams();
                                params.height = params.width = pluralWidth;
                                view.setTag(photoHolder);
                            }
                            String url = JSONUtil.getImagePath2(t.getImagePath(),
                                    Math.round(pluralWidth * 0.8f));
                            if (!JSONUtil.isEmpty(url)) {
                                photoHolder.imageView.setTag(url);
                                ImageLoadTask loadTask = new ImageLoadTask(photoHolder.imageView);
                                loadTask.loadImage(url,
                                        Math.round(pluralWidth * 0.8f),
                                        ScaleMode.ALL,
                                        new AsyncBitmapDrawable(getResources(),
                                                R.mipmap.icon_empty_image,
                                                loadTask));
                            }
                        }
                    });
            holder.imageGrid.setAdapter(holder.photoAdapter);
            holder.imageGrid.setOnItemClickListener(new OnPhotoItemClickListener(holder.photos));
            view.setTag(holder);
        }
        holder.content.setText(comment.getContent());
        holder.qualityRating.setText(getString(R.string.label_raning, comment.getQualityRating()));
        holder.attitudeRating.setText(getString(R.string.label_raning,
                comment.getAttitudeRating()));
        holder.describeRating.setText(getString(R.string.label_raning,
                comment.getDescribeRating()));
        if (comment.getTime() != null) {
            if (dateFormatter == null) {
                dateFormatter = new SimpleDateFormat(getString(R.string.format_date_type4));
            }
            holder.timeView.setText(dateFormatter.format(comment.getTime()));
        }
        if (comment.getUser() != null) {
            holder.userName.setText(comment.getUser()
                    .getName());
            String url = JSONUtil.getImagePath(comment.getUser()
                    .getAvatar(), holder.userIcon.getMeasuredWidth());
            if (!JSONUtil.isEmpty(url)) {
                holder.userIcon.setTag(url);
                ImageLoadTask task = new ImageLoadTask(holder.userIcon, null, 0);
                task.loadImage(url,
                        holder.userIcon.getMeasuredWidth(),
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(),
                                R.mipmap.icon_avatar_primary,
                                task));
            }
        }
        if (!comment.getPhotos()
                .isEmpty()) {
            holder.imageGrid.setVisibility(View.VISIBLE);
            ViewGroup.MarginLayoutParams params;
            params = (ViewGroup.MarginLayoutParams) holder.imageGrid.getLayoutParams();
            ArrayList<Photo> photos = comment.getPhotos();
            params.height = pluralWidth * ((photos.size() + 2) / 3) + ((photos.size() - 1) / 3) *
                    spacing;
            holder.photos.clear();
            holder.photos.addAll(photos);
            holder.photoAdapter.notifyDataSetChanged();
        } else {
            holder.imageGrid.setVisibility(View.GONE);
        }
    }

    private class ViewHolder {
        private ObjectBindAdapter<Photo> photoAdapter;
        private ArrayList<Photo> photos;
        private GridView imageGrid;
        private ImageView userIcon;
        private TextView userName;
        private TextView content;
        private TextView timeView;
        private TextView describeRating;
        private TextView attitudeRating;
        private TextView qualityRating;
    }

    private class PhotoHolder {
        private ImageView imageView;
    }

    private class GetCommentsTask extends AsyncTask<String, Object, JSONObject> {

        private GetCommentsTask() {
            isLoad = true;
        }

        private String url;

        @Override
        protected JSONObject doInBackground(String... strings) {
            url = strings[0];
            try {
                String json = JSONUtil.getStringFromUrl(WorkCommentListActivity.this, url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (url.equals(String.format(Constants.getAbsUrl(Constants.HttpPath.WORK_COMMENTS_URL),
                    currentPage,
                    20,
                    id))) {
                progressBar.setVisibility(View.GONE);
                listView.onRefreshComplete();
                int size = 0;
                if (jsonObject != null) {
                    if (!jsonObject.isNull("comments")) {
                        JSONArray array = jsonObject.optJSONArray("comments");
                        if (array != null && array.length() > 0) {
                            if (currentPage == 1) {
                                comments.clear();
                            }
                            size = array.length();
                            for (int i = 0; i < size; i++) {
                                Comment comment = new Comment(array.optJSONObject(i));
                                comments.add(comment);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
                if (size < 20) {
                    isEnd = true;
                    endView.setVisibility(View.VISIBLE);
                    loadView.setVisibility(View.GONE);
                } else {
                    isEnd = false;
                    endView.setVisibility(View.GONE);
                    loadView.setVisibility(View.INVISIBLE);
                }

                if (comments.isEmpty()) {
                    View emptyView = listView.getRefreshableView()
                            .getEmptyView();
                    if (emptyView == null) {
                        emptyView = findViewById(R.id.empty_hint_layout);
                        listView.getRefreshableView()
                                .setEmptyView(emptyView);
                        ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id
                                .img_net_hint);
                        TextView textEmptyHint = (TextView) emptyView.findViewById(R.id
                                .text_empty_hint);
                        imgNetHint.setVisibility(View.VISIBLE);
                        textEmptyHint.setVisibility(View.VISIBLE);
                        textEmptyHint.setText(R.string.net_disconnected);
                    }
                }
                isLoad = false;
            }
            super.onPostExecute(jsonObject);
        }

    }

    private class OnPhotoItemClickListener implements AdapterView.OnItemClickListener {
        private ArrayList<Photo> photos;

        private OnPhotoItemClickListener(ArrayList<Photo> photos) {
            this.photos = photos;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (photos == null || photos.isEmpty()) {
                return;
            }
            Photo photo = (Photo) parent.getAdapter()
                    .getItem(position);
            if (photo != null) {
                Intent intent = new Intent(WorkCommentListActivity.this,
                        PicsPageViewActivity.class);
                intent.putExtra("photos", photos);
                intent.putExtra("position", photos.indexOf(photo));
                startActivity(intent);
            }
        }
    }
}
