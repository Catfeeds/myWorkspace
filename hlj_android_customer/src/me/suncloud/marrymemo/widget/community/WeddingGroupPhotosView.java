package me.suncloud.marrymemo.widget.community;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.OriginalImageScaleListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * Created by luohanlin on 2017/5/4.
 * 按特定排序方式显示一个Group图片
 */

public class WeddingGroupPhotosView extends FrameLayout {

    public static final String TAG = WeddingGroupPhotosView.class.getSimpleName();
    private Context context;
    private ArrayList<Photo> originalPhotos;
    private ArrayList<Photo> photos;
    private ArrayList<Photo> restPhotos;
    private boolean collapsed;
    private OnPhotoClickListener onPhotoClickListener;
    private OnShowAllPhotoListener onShowAllPhotoListener;
    private LayoutInflater inflater;
    private LinearLayout contentLayout;
    private LinearLayout collapseLayout;
    private TextView tvCollapse;
    private int bigSize;
    private int smallSize;

    public WeddingGroupPhotosView(Context context) {
        super(context);
        init(context);
    }

    public WeddingGroupPhotosView(
            Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WeddingGroupPhotosView(
            Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.wedding_group_photos_layout, this, true);
        contentLayout = (LinearLayout) findViewById(R.id.content_layout);
        collapseLayout = (LinearLayout) findViewById(R.id.collapse_layout);
        tvCollapse = (TextView) findViewById(R.id.tv_collapse);
    }

    public void setOnShowAllPhotoListener(OnShowAllPhotoListener onShowAllPhotoListener) {
        this.onShowAllPhotoListener = onShowAllPhotoListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (bigSize == 0) {
            bigSize = MeasureSpec.getSize(widthMeasureSpec);
            smallSize = Math.round((bigSize - CommonUtil.dp2px(context, 2)) / 2);
            setPhotos(this.originalPhotos, this.collapsed, this.onPhotoClickListener);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置图片和是否隐藏多于4张的图片，默认全部展开
     *
     * @param list 图片列表
     */
    public void setPhotos(ArrayList<Photo> list, OnPhotoClickListener onPhotoClickListener) {
        setPhotos(list, false, onPhotoClickListener);
    }

    /**
     * 设置图片和是否隐藏多于4张的图片
     *
     * @param list 图片列表
     * @param c    是否隐藏
     */
    public void setPhotos(
            ArrayList<Photo> list, boolean c, OnPhotoClickListener onPhotoClickListener) {
        this.collapsed = c;
        this.originalPhotos = list;
        this.onPhotoClickListener = onPhotoClickListener;

        contentLayout.removeAllViews();
        if (list.isEmpty()) {
            return;
        }

        if (collapsed && list.size() > 4) {
            this.photos = new ArrayList<>(list.subList(0, 4));
            this.restPhotos = new ArrayList<>(list.subList(4, list.size()));

            collapseLayout.setVisibility(VISIBLE);
            tvCollapse.setText(context.getString(R.string.label_open_wedding_photo,
                    String.valueOf(list.size() - 4)));
            collapseLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRestPhotos();
                }
            });

            showPhotos();
        } else {
            collapseLayout.setVisibility(GONE);
            this.photos = list;
            if (photos.size() == 5) {
                showFivePhotos();
            } else {
                showPhotos();
            }
        }
    }

    private void showFivePhotos() {
        // 5 = 3 + 1 + 1
        contentLayout.addView(generateItemView(photos.subList(0, 3), true, 0));
        contentLayout.addView(generateItemView(photos.get(3), false, 3));
        contentLayout.addView(generateItemView(photos.get(4), false, 4));
    }

    private void showPhotos() {
        // n = (n / 3) * 3 + (n % 3)
        int x = photos.size() / 3;
        if (x > 0) {
            for (int i = 0; i < x; i++) {
                boolean isFirstSet = i == 0;
                List<Photo> list = photos.subList(i * 3, (i + 1) * 3);
                contentLayout.addView(generateItemView(list, isFirstSet, i * 3));
            }
        }
        int rest = photos.size() % 3;
        if (rest > 0) {
            boolean isFirstSet = x == 0;
            List<Photo> list = photos.subList(x * 3, photos.size());
            contentLayout.addView(generateItemView(list, isFirstSet, x * 3));
        }
    }

    private void showRestPhotos() {
        collapseLayout.setVisibility(GONE);
        if (this.onShowAllPhotoListener != null) {
            this.onShowAllPhotoListener.onShowAllPhoto();
        }
        int indexAddUp = 4;
        if (restPhotos.size() >= 2) {
            contentLayout.addView(generateItemView(restPhotos.subList(0, 2), false, indexAddUp));
            restPhotos = new ArrayList<>(restPhotos.subList(2, restPhotos.size()));
            indexAddUp += 2;
        }

        int x = restPhotos.size() / 3;
        if (x > 0) {
            for (int i = 0; i < x; i++) {
                List<Photo> list = restPhotos.subList(i * 3, (i + 1) * 3);
                contentLayout.addView(generateItemView(list, false, i * 3 + indexAddUp));
            }
        }
        int rest = restPhotos.size() % 3;
        if (rest > 0) {
            List<Photo> list = restPhotos.subList(x * 3, restPhotos.size());
            contentLayout.addView(generateItemView(list, false, x * 3 + indexAddUp));
        }
    }

    private View generateItemView(Photo photo, boolean isFirstSet, int startIndex) {
        List<Photo> list = new ArrayList<>();
        list.add(photo);
        return generateItemView(list, isFirstSet, startIndex);
    }

    private View generateItemView(List<Photo> list, boolean isFirstSet, int startIndex) {
        ArrayList arrayList = new ArrayList<>(list);
        View itemView = inflater.inflate(R.layout.wedding_group_photos_item, null, false);
        PhotoSetViewHolder holder = new PhotoSetViewHolder(itemView);
        holder.setPhotos(arrayList, isFirstSet, startIndex);

        return itemView;
    }

    public interface OnPhotoClickListener {
        void onPhotoClick(Photo photo, int position);
    }

    public interface OnShowAllPhotoListener {
        void onShowAllPhoto();
    }

    class PhotoSetViewHolder {
        @BindView(R.id.line_1)
        View line1;
        @BindView(R.id.img_1)
        ImageView img1;
        @BindView(R.id.line_2)
        View line2;
        @BindView(R.id.img_2)
        ImageView img2;
        @BindView(R.id.img_3)
        ImageView img3;


        PhotoSetViewHolder(View view) {ButterKnife.bind(this, view);}

        /**
         * 显示一set特定数目的图片（这里的set是视图中的二级组图，三个图片控件构成一个set，多个set顺序排列构成一个group）
         *
         * @param photos     photo set
         * @param isFirstSet 是否是这个group中的第一个set
         * @param startIndex 这一set在整个group列表中的起始index
         */
        void setPhotos(ArrayList<Photo> photos, boolean isFirstSet, int startIndex) {
            if (photos.size() > 3) {
                Log.e(TAG, "不能显示3张图片以上的item");
            }
            line1.setVisibility(isFirstSet ? GONE : VISIBLE);
            switch (photos.size()) {
                case 1:
                    img1.setVisibility(VISIBLE);
                    img2.setVisibility(GONE);
                    img3.setVisibility(GONE);
                    line2.setVisibility(GONE);
                    if (isFirstSet) {
                        // 如果是第一个，并且只有一个，则显示原图比例的图片，除此之外的所有图片都是方形的
                        setFixedWithImage(img1, photos.get(0), bigSize, startIndex);
                    } else {
                        setFixedSizeImage(img1, photos.get(0), bigSize, startIndex);
                    }
                    break;
                case 2:
                    img1.setVisibility(GONE);
                    img2.setVisibility(VISIBLE);
                    img3.setVisibility(VISIBLE);
                    line2.setVisibility(GONE);
                    setFixedSizeImage(img2, photos.get(0), smallSize, startIndex);
                    setFixedSizeImage(img3, photos.get(1), smallSize, startIndex + 1);
                    break;
                case 3:
                    img1.setVisibility(VISIBLE);
                    img2.setVisibility(VISIBLE);
                    img3.setVisibility(VISIBLE);
                    line2.setVisibility(VISIBLE);
                    setFixedSizeImage(img1, photos.get(0), bigSize, startIndex);
                    setFixedSizeImage(img2, photos.get(1), smallSize, startIndex + 1);
                    setFixedSizeImage(img3, photos.get(2), smallSize, startIndex + 2);
                    break;

            }
        }

        private void setFixedWithImage(
                ImageView imageView, final Photo photo, int width, final int index) {
            if (width == 0) {
                return;
            }
            if (photo.getWidth() > 0 && photo.getHeight() > 0) {
                int height = width * photo.getHeight() / photo.getWidth();
                Glide.with(context)
                        .load(ImagePath.buildPath(photo.getImagePath())
                                .width(width)
                                .path())
                        .apply(new RequestOptions().override(width, height)
                                .fitCenter())
                        .into(imageView);
            } else {
                Glide.with(context)
                        .load(ImagePath.buildPath(photo.getImagePath())
                                .width(width)
                                .path())
                        .listener(new OriginalImageScaleListener(imageView, width, 0))
                        .into(imageView);
            }
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPhotoClickListener != null) {
                        onPhotoClickListener.onPhotoClick(photo, index);
                    }
                }
            });
        }

        private void setFixedSizeImage(
                ImageView imageView, final Photo photo, int size, final int index) {
            if (size == 0) {
                return;
            }
            imageView.getLayoutParams().width = size;
            imageView.getLayoutParams().height = size;
            Glide.with(context)
                    .load(ImagePath.buildPath(photo.getImagePath())
                            .width(size)
                            .height(size)
                            .cropPath())
                    .apply(new RequestOptions().override(size, size))
                    .into(imageView);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPhotoClickListener != null) {
                        onPhotoClickListener.onPhotoClick(photo, index);
                    }
                }
            });
        }
    }
}
