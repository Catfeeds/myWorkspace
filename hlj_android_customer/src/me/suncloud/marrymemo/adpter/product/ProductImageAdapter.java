package me.suncloud.marrymemo.adpter.product;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.CommonProductViewHolder;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.view.product.ShopProductDetailActivity;

/**
 * Created by Administrator on 2016/11/12.婚品
 */
public class ProductImageAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int HEADER2 = -2;
    public static final int HEADER = -1;
    public static final int IMAGE = 0;
    public static final int ITEM = 1;
    public static final int ITEM_HEADER = 2;

    private View headerView2;
    private View headerView;
    private View itemHeaderView;
    private List<Photo> photos;
    private List<ShopProduct> shopProducts;
    private Context context;

    public ProductImageAdapter(Context context) {
        this.context = context;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setHeaderView2(View headerView) {
        this.headerView2 = headerView;
    }

    public void setItemHeaderView(View itemHeaderView) {
        this.itemHeaderView = itemHeaderView;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public void setShopProducts(List<ShopProduct> shopProducts) {
        this.shopProducts = shopProducts;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER2:
                return new ExtraViewHolder(headerView2);
            case HEADER:
                return new ExtraViewHolder(headerView);
            case ITEM_HEADER:
                return new ExtraViewHolder(itemHeaderView);
            case IMAGE:
                return new ImageViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_detail_photo_item, parent, false));
            case ITEM:
                CommonProductViewHolder productViewHolder = new CommonProductViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.common_product_list_item___cv, parent, false),
                        CommonProductViewHolder.STYLE_RATIO_1_TO_1);
                productViewHolder.setOnItemClickListener(new OnItemClickListener<ShopProduct>() {
                    @Override
                    public void onItemClick(int position, ShopProduct product) {
                        if (product != null && product.getId() > 0) {
                            Intent intent = new Intent(context, ShopProductDetailActivity.class);
                            intent.putExtra("id", product.getId());
                            context.startActivity(intent);
                        }
                    }
                });
                return productViewHolder;
        }
        return new ExtraViewHolder(null);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        switch (itemType) {
            case IMAGE:
            case ITEM:
                holder.setView(context, getItem(position), position, itemType);
                break;
        }
    }

    private Object getItem(int position) {
        if (headerView != null) {
            position--;
        }
        if (headerView2 != null) {
            position--;
        }
        if (photos != null && photos.size() > 0) {
            if (photos.size() > position) {
                return photos.get(position);
            }
            position -= photos.size();
        }
        if (itemHeaderView != null) {
            position--;
        }
        if (shopProducts != null && shopProducts.size() > position) {
            return shopProducts.get(position);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView != null) {
            if (position == 0) {
                return HEADER;
            }
            position--;
        }
        if (headerView2 != null) {
            if (position == 0) {
                return HEADER2;
            }
            position--;
        }
        if (photos != null && photos.size() > 0) {
            if (photos.size() > position) {
                return IMAGE;
            }
            position -= photos.size();
        }
        if (itemHeaderView != null) {
            if (position == 0) {
                return ITEM_HEADER;
            }
            position--;
        }
        if (shopProducts != null && shopProducts.size() > position) {
            return ITEM;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (headerView != null) {
            size++;
        }
        if (headerView2 != null) {
            size++;
        }
        if (photos != null && !photos.isEmpty()) {
            size = size + photos.size();
        }
        if (itemHeaderView != null) {
            size++;
        }
        if (shopProducts != null && !shopProducts.isEmpty()) {
            size = size + Math.min(shopProducts.size(), 4);
        }
        return size;
    }


    private class ExtraViewHolder extends BaseViewHolder<Photo> {

        public ExtraViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void setViewData(Context mContext, Photo item, int position, int viewType) {

        }
    }

    private class ImageViewHolder extends BaseViewHolder<Photo> {

        private ImageView imageView;
        private int imageWidth;
        private int width;

        public ImageViewHolder(View itemView) {
            super(itemView);
            width = imageWidth = CommonUtil.getDeviceSize(itemView.getContext()).x;
            if (imageWidth > 805) {
                imageWidth = imageWidth * 3 / 4;
            }
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
        }

        @Override
        protected void setViewData(Context mContext, Photo item, int position, int viewType) {
            String path = JSONUtil.getImagePath(item.getImagePath(), imageWidth);
            if (!JSONUtil.isEmpty(path)) {
                int height = 0;
                if (item.getHeight() != 0 && item.getWidth() != 0) {
                    height = imageView.getLayoutParams().height = Math.round(width * item
                            .getHeight() / item.getWidth());
                }
                final int finalHeight = height;
                Glide.with(imageView.getContext())
                        .load(path)
                        .apply(new RequestOptions().override(width,
                                height > 0 ? height : ImageUtil.getMaximumTextureSize())
                                .fitCenter()
                                .placeholder(R.mipmap.icon_empty_image))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(
                                    @Nullable GlideException e,
                                    Object model,
                                    Target<Drawable> target,
                                    boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(
                                    Drawable resource,
                                    Object model,
                                    Target<Drawable> target,
                                    DataSource dataSource,
                                    boolean isFirstResource) {
                                if (finalHeight == 0) {
                                    imageView.getLayoutParams().height = Math.round(resource
                                            .getIntrinsicHeight() * width / resource
                                            .getIntrinsicWidth());

                                }
                                return false;
                            }
                        })
                        .into(imageView);
            } else {
                imageView.setVisibility(View.GONE);
                Glide.with(mContext)
                        .clear(imageView);
            }
        }
    }
}
