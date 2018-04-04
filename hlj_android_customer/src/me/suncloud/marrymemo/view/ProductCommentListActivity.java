package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.Photo;
import me.suncloud.marrymemo.model.ProductComment;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.widget.RecyclingImageView;

/**
 * Created by Suncloud on 2015/8/17.
 */
public class ProductCommentListActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<ProductComment>, AbsListView.OnScrollListener, PullToRefreshBase
        .OnRefreshListener<ListView> {

    @Override
    public String pageTrackTagName() {
        return "婚品评价列表";
    }

    @Override
    public VTMetaData pageTrackData() {
        long id = getIntent().getLongExtra("id", 0);
        return new VTMetaData(id, "Article");
    }

    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private ArrayList<ProductComment> comments;
    private ObjectBindAdapter<ProductComment> adapter;
    private SimpleDateFormat simpleDateFormat;
    private long id;
    private int size;
    private int lastPage;
    private int currentPage;
    private boolean isEnd;
    private boolean onLoad;
    private TextView endView;
    private View loadView;
    private int singleEdge;
    private int singleImageW;
    private int pluralSize;
    private int spacing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        Point point = JSONUtil.getDeviceSize(this);
        size = Math.round(dm.density * 30);
        singleEdge = Math.round(point.x - dm.density * 86);
        singleImageW = singleEdge <= 805 ? singleEdge : Math.round(singleEdge * 3 / 4);
        spacing = Math.round(2 * dm.density);
        pluralSize = Math.round(point.x / 3 - dm.density * 26);
        comments = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this,
                comments,
                R.layout.product_comment_list_item,
                this);
        id = getIntent().getLongExtra("id", 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.bind(this);
        View headerView = View.inflate(this, R.layout.red_packet_expiring_header, null);
        View footerView = View.inflate(this, R.layout.list_foot_no_more_2, null);
        endView = (TextView) footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        listView.setOnRefreshListener(this);
        listView.getRefreshableView()
                .setOnScrollListener(this);
        listView.getRefreshableView()
                .addHeaderView(headerView);
        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.setAdapter(adapter);
        currentPage = 1;
        lastPage = currentPage;
        endView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScrollStateChanged(listView.getRefreshableView(),
                        AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
            }
        });
        new GetCommentListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                Constants.getAbsUrl(String.format(Constants.HttpPath.PRODUCT_COMMENT_LIST,
                        id,
                        currentPage)));
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !onLoad) {
                    if (JSONUtil.isNetworkConnected(this)) {
                        currentPage++;
                        endView.setVisibility(View.GONE);
                        loadView.setVisibility(View.VISIBLE);
                        new GetCommentListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                                Constants.getAbsUrl(String.format(Constants.HttpPath
                                                .PRODUCT_COMMENT_LIST,
                                        id,
                                        currentPage)));
                    } else {
                        endView.setVisibility(View.VISIBLE);
                        loadView.setVisibility(View.GONE);
                        endView.setText(R.string.hint_net_disconnected);
                    }
                }
                break;
        }
    }

    @Override
    public void onScroll(
            AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void setViewValue(View view, final ProductComment productComment, int position) {
        if (view.getTag() == null) {
            final ViewHolder holder = new ViewHolder(view);
            holder.photos = new ArrayList<>();
            holder.photoAdapter = new ObjectBindAdapter<>(ProductCommentListActivity.this,
                    holder.photos,
                    R.layout.thread_photos_item,
                    new ObjectBindAdapter.ViewBinder<Photo>() {

                        @Override
                        public void setViewValue(View view, Photo t, int position) {
                            ImageViewHolder imageViewHolder = (ImageViewHolder) view.getTag();
                            if (imageViewHolder == null) {
                                imageViewHolder = new ImageViewHolder();
                                imageViewHolder.imageView = (ImageView) view.findViewById(R.id
                                        .photo);
                                imageViewHolder.imageView.getLayoutParams().width = pluralSize;
                                imageViewHolder.imageView.getLayoutParams().height = pluralSize;
                                view.setTag(imageViewHolder);
                            }
                            String url;
                            url = JSONUtil.getImagePath2(t.getPath(), pluralSize);
                            if (!JSONUtil.isEmpty(url)) {
                                imageViewHolder.imageView.setTag(url);
                                ImageLoadTask loadTask = new ImageLoadTask(imageViewHolder
                                        .imageView,
                                        0);
                                loadTask.loadImage(url,
                                        pluralSize,
                                        ScaleMode.ALL,
                                        new AsyncBitmapDrawable(getResources(),
                                                R.mipmap.icon_empty_image,
                                                loadTask));
                            } else {
                                imageViewHolder.imageView.setTag(null);
                                imageViewHolder.imageView.setImageBitmap(null);
                            }
                        }

                        class ImageViewHolder {
                            ImageView imageView;
                        }
                    });
            holder.imagesLayout.setAdapter(holder.photoAdapter);
            holder.imagesLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Photo photo = (Photo) parent.getAdapter()
                            .getItem(position);
                    if (photo != null) {
                        Intent intent = new Intent(ProductCommentListActivity.this,
                                ThreadPicsPageViewActivity.class);
                        intent.putExtra("photos", holder.photos);
                        intent.putExtra("position", holder.photos.indexOf(photo));
                        startActivity(intent);
                    }

                }
            });
            view.setTag(holder);
        }
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.grade.setText(productComment.getRating() > 0 ? R.string.label_grade_level1 :
                productComment.getRating() < 0 ? R.string.label_grade_level3 : R.string
                        .label_grade_level2);
        holder.lineLayout.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        if (productComment.getUser() != null) {
            String path = productComment.getUser()
                    .getAvatar(size);
            holder.name.setText(productComment.getUser()
                    .getNick());
            holder.name.setPadding(0,
                    0,
                    Math.round(holder.grade.getPaint()
                            .measureText(holder.grade.getText()
                                    .toString()) + getResources().getDisplayMetrics().density * 10),
                    0);
            if (!JSONUtil.isEmpty(path)) {
                holder.userIcon.setTag(path);
                ImageLoadTask task = new ImageLoadTask(holder.userIcon, null, 0);
                task.loadImage(path,
                        size,
                        ScaleMode.ALL,
                        new AsyncBitmapDrawable(getResources(),
                                R.mipmap.icon_avatar_primary,
                                task));

            }
        }
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(getString(R.string.format_date_type4),
                    Locale.getDefault());
        }
        holder.time.setText(simpleDateFormat.format(productComment.getTime()));
        holder.content.setVisibility(JSONUtil.isEmpty(productComment.getContent()) ? View.GONE :
                View.VISIBLE);
        holder.content.setText(productComment.getContent());
        if (!JSONUtil.isEmpty(productComment.getSku())) {
            holder.sku.setVisibility(View.VISIBLE);
            holder.sku.setText(getString(R.string.label_sku) + productComment.getSku());
        } else {
            holder.sku.setVisibility(View.GONE);
        }
        if (productComment.getPhotos() != null && !productComment.getPhotos()
                .isEmpty()) {
            if (productComment.getPhotos()
                    .size() == 1) {
                holder.imageSingle.setVisibility(View.VISIBLE);
                holder.imageSingle.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ProductCommentListActivity.this,
                                ThreadPicsPageViewActivity.class);
                        intent.putExtra("photos", productComment.getPhotos());
                        startActivity(intent);
                    }
                });
                holder.imagesLayout.setVisibility(View.GONE);
                final Photo photo = productComment.getPhotos()
                        .get(0);
                //                int w = photo.getWidth();
                //                int h = photo.getHeight();
                //                if (w > 0 && h > 0) {
                //                    holder.imageSingle.getLayoutParams().height = h > w ?
                // singleEdge : Math.round(singleEdge * h / w);
                //                    holder.imageSingle.getLayoutParams().width = w > h ?
                // singleEdge : Math.round(singleEdge * w / h);
                //                }
                holder.imageSingle.getLayoutParams().height = pluralSize;
                holder.imageSingle.getLayoutParams().width = pluralSize;
                String url = JSONUtil.getImagePathForWxH(photo.getPath(),
                        singleImageW,
                        singleImageW);
                if (!JSONUtil.isEmpty(url)) {
                    ImageLoadTask loadTask = new ImageLoadTask(holder.imageSingle,
                            new OnHttpRequestListener() {
                                @Override
                                public void onRequestFailed(Object obj) {

                                }

                                @Override
                                public void onRequestCompleted(Object obj) {
                                    if ((photo.getWidth() == 0) || (photo.getHeight() == 0)) {
                                        //                                        Bitmap bitmap =
                                        // (Bitmap) obj;
                                        //                                        int w = bitmap
                                        // .getWidth();
                                        //                                        int h = bitmap
                                        // .getHeight();
                                        //                                        holder
                                        // .imageSingle.getLayoutParams().height = h > w ?
                                        // singleEdge : Math.round(singleEdge * h / w);
                                        //                                        holder
                                        // .imageSingle.getLayoutParams().width = w > h ?
                                        // singleEdge : Math.round(singleEdge * w / h);

                                        holder.imageSingle.getLayoutParams().height = pluralSize;
                                        holder.imageSingle.getLayoutParams().width = pluralSize;
                                    }
                                }
                            });
                    holder.imageSingle.setTag(url);
                    loadTask.loadImage(url,
                            singleEdge,
                            ScaleMode.ALL,
                            new AsyncBitmapDrawable(getResources(),
                                    R.mipmap.icon_empty_image,
                                    loadTask));
                } else {
                    holder.imageSingle.setImageBitmap(null);
                }
            } else {
                holder.imageSingle.setVisibility(View.GONE);
                holder.imagesLayout.setVisibility(View.VISIBLE);
                ArrayList<Photo> photos = productComment.getPhotos();
                holder.imagesLayout.getLayoutParams().height = pluralSize * ((photos.size() + 2)
                        / 3) + ((photos.size() - 1) / 3) * spacing;
                holder.photos.clear();
                holder.photos.addAll(photos);
                holder.photoAdapter.notifyDataSetChanged();
            }
        } else {
            holder.imageSingle.setVisibility(View.GONE);
            holder.imagesLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!onLoad) {
            currentPage = 1;
            new GetCommentListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    Constants.getAbsUrl(String.format(Constants.HttpPath.PRODUCT_COMMENT_LIST,
                            id,
                            currentPage)));
        }
    }

    private class GetCommentListTask extends AsyncTask<String, Object, JSONObject> {

        private String url;

        private GetCommentListTask() {
            onLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json).optJSONObject("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (url.equals(Constants.getAbsUrl(String.format(Constants.HttpPath
                            .PRODUCT_COMMENT_LIST,
                    id,
                    currentPage)))) {
                progressBar.setVisibility(View.GONE);
                listView.onRefreshComplete();
                onLoad = false;
                if (jsonObject != null) {
                    lastPage = currentPage;
                    int pageCount = jsonObject.optInt("page_count");
                    isEnd = pageCount <= currentPage;
                    JSONArray array = jsonObject.optJSONArray("list");
                    if (currentPage == 1) {
                        comments.clear();
                    }
                    if (array != null && array.length() > 0) {
                        int size = array.length();
                        for (int i = 0; i < size; i++) {
                            comments.add(new ProductComment(array.optJSONObject(i)));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (isEnd) {
                        endView.setVisibility(View.VISIBLE);
                        loadView.setVisibility(View.GONE);
                        endView.setText(R.string.no_more);
                    } else {
                        endView.setVisibility(View.GONE);
                        loadView.setVisibility(View.INVISIBLE);
                    }
                } else {
                    currentPage = lastPage;
                    endView.setVisibility(View.VISIBLE);
                    loadView.setVisibility(View.GONE);
                    endView.setText(R.string.hint_net_disconnected);
                }
                if (comments.isEmpty()) {
                    View emptyView = listView.getRefreshableView()
                            .getEmptyView();
                    if (emptyView == null) {
                        emptyView = findViewById(R.id.empty_hint_layout);
                        emptyView.findViewById(R.id.text_empty_hint)
                                .setVisibility(View.VISIBLE);
                        listView.getRefreshableView()
                                .setEmptyView(emptyView);
                    }
                    ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                            .img_empty_hint);
                    ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
                    TextView textEmptyHint = (TextView) emptyView.findViewById(R.id
                            .text_empty_hint);
                    if (JSONUtil.isNetworkConnected(ProductCommentListActivity.this)) {
                        imgNetHint.setVisibility(View.GONE);
                        imgEmptyHint.setVisibility(View.VISIBLE);
                        textEmptyHint.setText(R.string.no_item);
                    } else {
                        imgNetHint.setVisibility(View.GONE);
                        imgEmptyHint.setVisibility(View.VISIBLE);
                        textEmptyHint.setText(R.string.net_disconnected);
                    }

                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'product_comment_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        ObjectBindAdapter<Photo> photoAdapter;
        ArrayList<Photo> photos;
        @BindView(R.id.user_icon)
        RoundedImageView userIcon;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.grade)
        TextView grade;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.sku)
        TextView sku;
        @BindView(R.id.image_single)
        RecyclingImageView imageSingle;
        @BindView(R.id.images_layout)
        GridView imagesLayout;
        @BindView(R.id.line_layout)
        View lineLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
