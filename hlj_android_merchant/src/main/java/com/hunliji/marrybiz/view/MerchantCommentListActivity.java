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
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
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
 * Created by Suncloud on 2014/11/24.
 */

public class MerchantCommentListActivity extends HljBaseActivity implements AbsListView
        .OnScrollListener, PullToRefreshBase.OnRefreshListener<ListView>, ObjectBindAdapter
        .ViewBinder<Comment> {

    private PullToRefreshListView listView;
    private ArrayList<Comment> comments;
    private ObjectBindAdapter<Comment> adapter;
    private boolean isEnd;
    private boolean isLoad;
    private boolean heardShowFirst;
    private View endView;
    private View loadView;
    private View heardView;
    private View progressBar;
    private int currentPage;
    private DateFormat dateFormatter;
    private long id;
    private int spacing;
    private int pluralWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        id = getIntent().getLongExtra("id", 0);
        comments = new ArrayList<>();
        Point point = JSONUtil.getDeviceSize(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        spacing = Math.round(2 * dm.density);
        pluralWidth = Math.round(point.x / 5);
        adapter = new ObjectBindAdapter<>(this, comments, R.layout.comment_list_item, this);
        View footerView = View.inflate(this, R.layout.list_foot_no_more_2, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        heardView = View.inflate(this, R.layout.comment_list_heard, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        progressBar = findViewById(R.id.progressBar);
        listView = (PullToRefreshListView) findViewById(R.id.list_view);
        listView.getRefreshableView()
                .addFooterView(footerView);
        //        listView.getRefreshableView().addHeaderView(heardView);
        heardView.setVisibility(View.GONE);
        listView.setOnScrollListener(this);
        listView.setOnRefreshListener(this);
        listView.setAdapter(adapter);
        currentPage = 1;
        new GetCommentsTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                String.format(Constants.getAbsUrl(Constants.HttpPath
                        .MERCHANT_COMMENTS_WITH_CUSTOMER_URL),
                        currentPage,
                        20,
                        id));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            heardShowFirst = false;
            currentPage = 1;
            new GetCommentsTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(Constants.getAbsUrl(Constants.HttpPath.MERCHANT_COMMENTS_URL),
                            currentPage,
                            20,
                            id));
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
                            String.format(Constants.getAbsUrl(Constants.HttpPath
                                    .MERCHANT_COMMENTS_URL),
                                    currentPage,
                                    20,
                                    id));
                } else {
                    break;
                }
        }
    }

    @Override
    public void onScroll(AbsListView view, int i, int i2, int i3) {

    }

    @Override
    public void setViewValue(View view, final Comment comment, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.userIcon = (ImageView) view.findViewById(R.id.user_icon);
            holder.userName = (TextView) view.findViewById(R.id.user_name);
            holder.content = (TextView) view.findViewById(R.id.content);
            holder.timeView = (TextView) view.findViewById(R.id.time);
            holder.workName = (TextView) view.findViewById(R.id.work_name);
            holder.rating = (RatingBar) view.findViewById(R.id.rating);
            holder.workName.setVisibility(View.VISIBLE);
            holder.imageGrid = (GridView) view.findViewById(R.id.images_layout);
            holder.imageGrid.getLayoutParams().width = pluralWidth * 3 + spacing * 2;
            holder.photos = new ArrayList<>();
            holder.photoAdapter = new ObjectBindAdapter<>(MerchantCommentListActivity.this,
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
        holder.rating.setRating(comment.getRating());
        holder.rating.setVisibility(View.GONE);
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
        if (comment.getType() == 0 && comment.getWork() != null) {
            holder.workName.setText("#" + comment.getWork()
                    .getTitle() + "#");
        } else if (comment.getType() == 1 && comment.getCustomSetmeal() != null) {
            holder.workName.setText("#" + comment.getCustomSetmeal()
                    .getTitle() + "#");
        }

        holder.workName.setOnClickListener(new OnCommentWorkClickListener(comment));
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

    private void showCommentHeard(JSONObject json) {
        if (!heardShowFirst && json.optInt("id", 0) > 0) {
            heardView.setVisibility(View.VISIBLE);
            heardShowFirst = true;
            SeekBar bar1 = (SeekBar) heardView.findViewById(R.id.rating_bar1);
            SeekBar bar2 = (SeekBar) heardView.findViewById(R.id.rating_bar2);
            SeekBar bar3 = (SeekBar) heardView.findViewById(R.id.rating_bar3);
            SeekBar bar4 = (SeekBar) heardView.findViewById(R.id.rating_bar4);
            SeekBar bar5 = (SeekBar) heardView.findViewById(R.id.rating_bar5);
            TextView count1 = (TextView) heardView.findViewById(R.id.count1);
            TextView count2 = (TextView) heardView.findViewById(R.id.count2);
            TextView count3 = (TextView) heardView.findViewById(R.id.count3);
            TextView count4 = (TextView) heardView.findViewById(R.id.count4);
            TextView count5 = (TextView) heardView.findViewById(R.id.count5);
            TextView ratingNum = (TextView) heardView.findViewById(R.id.rating_num);
            TextView ratingCount = (TextView) heardView.findViewById(R.id.rating_count);
            int commentCount = json.optInt("order_comments_count", 0);
            ratingCount.setText(getString(R.string.label_comment_count4, commentCount));
            String ratingColor = JSONUtil.getString(json, "rating_color");
            RatingBar rating = (RatingBar) heardView.findViewById(R.id.rating);
            if ("grey".equals(ratingColor)) {
                ratingNum.setTextColor(getResources().getColor(R.color.colorGray3));
            }
            rating.setVisibility(View.VISIBLE);
            rating.setRating((float) json.optDouble("rating", 0));
            ratingNum.setText(String.valueOf((float) Math.round(json.optDouble("rating",
                    3) * 10) / 10));
            bar1.setEnabled(false);
            bar2.setEnabled(false);
            bar3.setEnabled(false);
            bar4.setEnabled(false);
            bar5.setEnabled(false);
            JSONArray array = json.optJSONArray("ratings_count");
            if (array != null && array.length() > 0) {
                int size = Math.min(5, array.length());
                for (int i = 0; i < size; i++) {
                    int count = array.optInt(i);
                    int progress = Math.round(100 * count / commentCount);
                    switch (i) {
                        case 0:
                            count1.setText(String.valueOf(count));
                            bar1.setProgress(progress);
                            break;
                        case 1:
                            count2.setText(String.valueOf(count));
                            bar2.setProgress(progress);
                            break;
                        case 2:
                            count3.setText(String.valueOf(count));
                            bar3.setProgress(progress);
                            break;
                        case 3:
                            count4.setText(String.valueOf(count));
                            bar4.setProgress(progress);
                            break;
                        case 4:
                            count5.setText(String.valueOf(count));
                            bar5.setProgress(progress);
                            break;
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        EasyTracker.getInstance(this)
                .activityStart(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        EasyTracker.getInstance(this)
                .activityStop(this);
        super.onPause();
    }

    private class ViewHolder {
        private ObjectBindAdapter<Photo> photoAdapter;
        private ArrayList<Photo> photos;
        private GridView imageGrid;
        private ImageView userIcon;
        private TextView userName;
        private TextView content;
        private TextView timeView;
        private TextView workName;
        private RatingBar rating;
    }


    private class PhotoHolder {
        private ImageView imageView;
    }


    private class GetCommentsTask extends AsyncTask<String, Object, JSONObject> {

        private String url;

        private GetCommentsTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            url = strings[0];
            try {
                String json = JSONUtil.getStringFromUrl(MerchantCommentListActivity.this, url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (url.equals(String.format(Constants.getAbsUrl(Constants.HttpPath
                    .MERCHANT_COMMENTS_WITH_CUSTOMER_URL),
                    currentPage,
                    20,
                    id))) {
                progressBar.setVisibility(View.GONE);
                listView.onRefreshComplete();
                int size = 0;
                if (jsonObject != null) {
                    if (!jsonObject.isNull("data")) {
                        JSONObject data = jsonObject.optJSONObject("data");
                        if (data != null && !data.isNull("list")) {
                            JSONArray array = data.optJSONArray("list");
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
            }
            isLoad = false;
            super.onPostExecute(jsonObject);
        }

    }

    private class OnCommentWorkClickListener implements View.OnClickListener {

        private Comment comment;

        private OnCommentWorkClickListener(Comment comment) {
            this.comment = comment;
        }

        @Override
        public void onClick(View view) {
            if (comment.getType() == 0 && comment.getWork() != null) {
                Intent intent = new Intent(MerchantCommentListActivity.this, WorkActivity.class);
                intent.putExtra("w_id",
                        comment.getWork()
                                .getId());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            } else if (comment.getType() == 1 && comment.getCustomSetmeal() != null) {
                Intent intent = new Intent(MerchantCommentListActivity.this,
                        CustomSetMealDetailActivity.class);
                intent.putExtra("id",
                        comment.getCustomSetmeal()
                                .getId());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }

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
                Intent intent = new Intent(MerchantCommentListActivity.this,
                        PicsPageViewActivity.class);
                intent.putExtra("photos", photos);
                intent.putExtra("position", photos.indexOf(photo));
                startActivity(intent);
            }
        }
    }
}

