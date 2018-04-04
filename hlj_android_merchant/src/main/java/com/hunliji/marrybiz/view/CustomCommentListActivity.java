package com.hunliji.marrybiz.view;

import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 定制套餐评论列表
 * Created by jinxin on 2016/3/8.
 */
public class CustomCommentListActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<Comment> {
    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private long id;
    private ObjectBindAdapter<Comment> adapter;
    private List<Comment> mData;
    private int pluralWidth;
    private int spacing;
    private DateFormat dateFormatter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        ButterKnife.bind(this);
        Point point = JSONUtil.getDeviceSize(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        pluralWidth = Math.round(point.x / 5);
        spacing = Math.round(2 * dm.density);
        id = getIntent().getLongExtra("id", 0);
        mData = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, mData, R.layout.comment_list_item, this);
        listView.getRefreshableView()
                .setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);
        String url = String.format(Constants.getAbsUrl(Constants.HttpPath.GET_CUSTOM_COMMENT_LIST),
                id);
        new GetCommentListTask().executeOnExecutor(Constants.LISTTHEADPOOL, url);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
    }

    @Override
    public void setViewValue(View view, final Comment comment, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            holder.workName.setVisibility(View.VISIBLE);
            holder.imagesLayout = (GridView) view.findViewById(R.id.images_layout);
            holder.imagesLayout.getLayoutParams().width = pluralWidth * 3 + spacing * 2;
            holder.photos = new ArrayList<>();
            holder.photoAdapter = new ObjectBindAdapter<>(this,
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
            holder.imagesLayout.setAdapter(holder.photoAdapter);
            holder.imagesLayout.setOnItemClickListener(new OnPhotoItemClickListener(holder.photos));
            view.setTag(holder);
        }
        holder.rating.setVisibility(View.GONE);

        holder.content.setText(comment.getContent());
        if (comment.getTime() != null) {
            if (dateFormatter == null) {
                dateFormatter = new SimpleDateFormat(getString(R.string.format_date_type4));
            }
            holder.time.setText(dateFormatter.format(comment.getTime()));
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
            holder.userIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //                    Intent intent = new Intent(CustomCommentListActivity
                    // .this, HomePageActivity.class);
                    //                    intent.putExtra("userId", comment.getUser() == null ? 0
                    // : comment.getUser()
                    //                            .getId());
                    //                    startActivity(intent);
                }
            });
        }
        if (comment.getCustomSetmeal() != null) {
            holder.workName.setText(comment.getCustomSetmeal()
                    .getTitle());
        }
        holder.workName.setOnClickListener(new OnCommentWorkClickListener(comment));
        holder.workName.setVisibility(View.GONE);
        if (!comment.getPhotos()
                .isEmpty()) {
            holder.imagesLayout.setVisibility(View.VISIBLE);
            ViewGroup.MarginLayoutParams params;
            params = (ViewGroup.MarginLayoutParams) holder.imagesLayout.getLayoutParams();
            ArrayList<Photo> photos = comment.getPhotos();
            params.height = pluralWidth * ((photos.size() + 2) / 3) + ((photos.size() - 1) / 3) *
                    spacing;
            holder.photos.clear();
            holder.photos.addAll(photos);
            holder.photoAdapter.notifyDataSetChanged();
        } else {
            holder.imagesLayout.setVisibility(View.GONE);
        }
    }

    private void setEmptyView() {
        if (mData.isEmpty()) {
            View emptyView = listView.getRefreshableView()
                    .getEmptyView();
            if (emptyView == null) {
                emptyView = findViewById(R.id.empty_hint_layout);
                ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id.img_empty_hint);
                ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
                TextView textEmptyHint = (TextView) emptyView.findViewById(R.id.text_empty_hint);

                imgEmptyHint.setVisibility(View.VISIBLE);
                imgNetHint.setVisibility(View.VISIBLE);
                textEmptyHint.setVisibility(View.VISIBLE);

                if (JSONUtil.isNetworkConnected(this)) {
                    imgNetHint.setVisibility(View.GONE);
                    textEmptyHint.setText(R.string.no_item);
                } else {
                    imgEmptyHint.setVisibility(View.GONE);
                    textEmptyHint.setText(R.string.net_disconnected);

                }
            }
            listView.getRefreshableView()
                    .setEmptyView(emptyView);
        }
    }

    class GetCommentListTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(CustomCommentListActivity.this, url);
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
        protected void onPostExecute(JSONObject object) {
            if (object != null) {
                progressBar.setVisibility(View.GONE);
                listView.onRefreshComplete();
                JSONObject data = object.optJSONObject("data");
                if (data != null) {
                    int totalCount = data.optInt("total_count");
                    setTitle(getString(R.string.label_review3, totalCount));
                    JSONArray list = data.optJSONArray("list");
                    if (list != null) {
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject item = list.optJSONObject(i);
                            Comment comment = new Comment(item);
                            mData.add(comment);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                setEmptyView();
            }
            super.onPostExecute(object);
        }
    }


    private class PhotoHolder {
        private ImageView imageView;
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
                Intent intent = new Intent(CustomCommentListActivity.this,
                        PicsPageViewActivity.class);
                intent.putExtra("photos", photos);
                intent.putExtra("position", photos.indexOf(photo));
                startActivity(intent);
            }
        }
    }

    private class OnCommentWorkClickListener implements View.OnClickListener {

        private Comment comment;

        private OnCommentWorkClickListener(Comment comment) {
            this.comment = comment;
        }

        @Override
        public void onClick(View view) {
            if (comment.getWork() != null) {
                Intent intent = new Intent(CustomCommentListActivity.this,
                        CustomSetMealDetailActivity.class);
                intent.putExtra("id",
                        comment.getCustomSetmeal()
                                .getId());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }

        }
    }

    //    static class ViewHolder {
    //        @BindView(R.id.user_icon)
    //        RoundedImageView userIcon;
    //        @BindView(R.id.user_name)
    //        TextView userName;
    //        @BindView(R.id.content)
    //        TextView content;
    //        @BindView(R.id.images_layout)
    //        GridView imagesLayout;
    //        @BindView(R.id.content_layout)
    //        RelativeLayout contentLayout;
    //        @BindView(R.id.work_name)
    //        TextView workName;
    //        @BindView(R.id.time)
    //        TextView time;
    //        private ObjectBindAdapter<Photo> photoAdapter;
    //        private ArrayList<Photo> photos;
    //
    //        ViewHolder(View view) {
    //            ButterKnife.bind(this, view);
    //        }
    //    }

    static class ViewHolder {
        @BindView(R.id.user_icon)
        RoundedImageView userIcon;
        @BindView(R.id.user_name)
        TextView userName;
        @BindView(R.id.rating)
        RatingBar rating;
        @BindView(R.id.user_info_layout)
        LinearLayout userInfoLayout;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.images_layout)
        GridView imagesLayout;
        @BindView(R.id.content_layout)
        RelativeLayout contentLayout;
        @BindView(R.id.work_name)
        TextView workName;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.other_rating1)
        TextView otherRating1;
        @BindView(R.id.other_rating2)
        TextView otherRating2;
        @BindView(R.id.other_rating3)
        TextView otherRating3;

        private ObjectBindAdapter<Photo> photoAdapter;
        private ArrayList<Photo> photos;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
