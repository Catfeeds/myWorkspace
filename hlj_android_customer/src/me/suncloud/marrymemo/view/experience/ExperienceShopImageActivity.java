package me.suncloud.marrymemo.view.experience;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalStaggeredRecyclerView;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.experience.ExperienceApi;
import me.suncloud.marrymemo.model.experience.ExperiencePhoto;
import me.suncloud.marrymemo.util.JSONUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 体验店照片
 * Created by jinxin on 2016/10/27.
 */

public class ExperienceShopImageActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener {
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalStaggeredRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;//分页
    private View footView;
    private View endView;
    private View loadView;
    private StaggeredGridLayoutManager manager;
    private StaggeredSpaceItemDecoration itemDecoration;
    private List<ExperiencePhoto> photos;
    private ImageAdapter imageAdapter;
    private int imgWidth;
    private long id;
    private int padding;
    private int offset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        id = getIntent().getLongExtra("id", 0);
        String title = getIntent().getStringExtra("title");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_staggered_recycler_view___cm);
        ButterKnife.bind(this);
        footView = getLayoutInflater().inflate(R.layout.list_foot_no_more_2, null);
        endView = footView.findViewById(com.hunliji.hljquestionanswer.R.id.no_more_hint);
        loadView = footView.findViewById(com.hunliji.hljquestionanswer.R.id.loading);
        setTitle(JSONUtil.isEmpty(title) ? getString(R.string.label_experience_image) : title);
        Point point = JSONUtil.getDeviceSize(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        padding = Math.round(dm.density * 10);
        offset = Math.round(dm.density * 6);
        imgWidth = Math.round((point.x - dm.density * 16) / 2);
        photos = new ArrayList<>();
        imageAdapter = new ImageAdapter();
        manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        manager.setItemPrefetchEnabled(false);
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        itemDecoration = new StaggeredSpaceItemDecoration();
        recyclerView.setBackgroundResource(R.color.colorWhite);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .addItemDecoration(itemDecoration);
        recyclerView.getRefreshableView()
                .setAdapter(imageAdapter);
        recyclerView.setOnRefreshListener(this);
        onRefresh(recyclerView);
    }

    private void initPagination(int pageCount) {
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        Observable<HljHttpData<List<ExperiencePhoto>>> observable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<ExperiencePhoto>>>() {
                    @Override
                    public Observable<HljHttpData<List<ExperiencePhoto>>> onNextPage(
                            int page) {
                        return ExperienceApi.getShopPhoto(id, page);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable();

        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ExperiencePhoto>>>() {
                    @Override
                    public void onNext(
                            HljHttpData<List<ExperiencePhoto>> listHljHttpData) {
                        if (listHljHttpData != null && listHljHttpData.getData() != null) {
                            List<ExperiencePhoto> data = listHljHttpData.getData();
                            photos.addAll(data);
                            setOkText(getString(R.string.label_more_pages,
                                    String.valueOf(listHljHttpData.getTotalCount())));
                            imageAdapter.setPhotos(photos);
                            imageAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .build();

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            Observable<HljHttpData<List<ExperiencePhoto>>> observable = ExperienceApi.getShopPhoto(
                    id,
                    1);
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(refreshView)
                    .setEmptyView(emptyView)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ExperiencePhoto>>>() {
                        @Override
                        public void onNext(
                                HljHttpData<List<ExperiencePhoto>> ph) {
                            photos.clear();
                            List<ExperiencePhoto> data = ph.getData();
                            photos.addAll(data);
                            setOkText(getString(R.string.label_more_pages,
                                    String.valueOf(ph.getTotalCount())));
                            imageAdapter.setPhotos(photos);
                            imageAdapter.notifyDataSetChanged();
                            initPagination(ph.getPageCount());
                        }
                    })
                    .build();
            observable.subscribe(refreshSubscriber);
        }
    }

    @Override
    protected void onFinish() {
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber.unsubscribe();
        }
        super.onFinish();
    }

    class ImageAdapter extends RecyclerView.Adapter<ImageViewHolder> {
        private ArrayList<Photo> photos;

        public ImageAdapter() {
            photos = new ArrayList<>();
        }

        public void setPhotos(List<ExperiencePhoto> ph) {
            List<ExperiencePhoto> experiencePhotos = ph;
            if (photos == null) {
                photos = new ArrayList<>();
            }
            photos.clear();
            for (ExperiencePhoto p : experiencePhotos) {
                if (p != null) {
                    photos.add(p.getCover());
                }
            }
        }

        @Override
        public ImageViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.image_item, parent, false);
            return new ImageViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(
                ImageViewHolder holder, final int position) {
            Photo photo = this.photos.get(position);
            if (photo == null) {
                return;
            }
            String imagePath = photo.getImagePath();
            double scale = photo.getHeight() * 1.0d / photo.getWidth();
            int imageHeight = (int) Math.round(scale * imgWidth);
            holder.image.getLayoutParams().height = imageHeight;
            holder.image.getLayoutParams().width = imgWidth;
            int imgSize = Math.max(imgWidth, imageHeight);
            String imgPath = JSONUtil.getImagePath(imagePath, imgSize);
            if (!JSONUtil.isEmpty(imgPath)) {
                Glide.with(ExperienceShopImageActivity.this)
                        .load(imgPath)
                        .apply(new RequestOptions().override(imgWidth, imageHeight)
                                .placeholder(R.mipmap.icon_empty_image))
                        .into(holder.image);
            } else {
                Glide.with(ExperienceShopImageActivity.this)
                        .clear(holder.image);
            }
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    Intent intent = new Intent(ExperienceShopImageActivity.this,
                            PicsPageViewActivity.class);
                    intent.putExtra("photos", photos);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return photos.size();
        }
    }


    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        View itemView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    class StaggeredSpaceItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager
                    .LayoutParams) view.getLayoutParams();
            if (params == null) {
                return;
            }
            int position = parent.getChildLayoutPosition(view);
            int size = parent.getAdapter()
                    .getItemCount();
            if (position > 1) {
                outRect.top = offset;
            } else {
                outRect.top = padding;
            }

            int span = params.getSpanIndex();
            if (span == 0) {
                //第一列
                outRect.right = Math.round(offset / 2);
                outRect.left = padding;
            }
            if (span == 1) {
                //第二列
                outRect.left = Math.round(offset / 2);
                outRect.right = padding;
            }

            if (position >= size - 2) {
                outRect.bottom = padding;
            } else {
                outRect.bottom = 0;
            }
        }
    }

    /**
     * 防止RecyclerView滑动重新onMeasure 导致ImageView大小变化 图片显示不完全
     */
    class SizeModel {
        int width;
        int height;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

}
