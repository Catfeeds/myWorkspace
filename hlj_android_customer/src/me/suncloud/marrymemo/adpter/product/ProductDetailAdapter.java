package me.suncloud.marrymemo.adpter.product;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.product.ProductParameter;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by luohanlin on 2018/2/27.
 */

public class ProductDetailAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int HEADER2 = -2;
    public static final int HEADER = -1;
    public static final int IMAGE = 0;
    public static final int PARAMETERS = 1;

    private View headerView2;
    private View headerView;
    private List<Photo> photos;
    private Context context;
    private ShopProduct product;
    public static final int PARAMETER_LIMIT = 6;

    private OnItemImageClickListener itemImageClickListener;

    public ProductDetailAdapter(Context context) {
        this.context = context;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setHeaderView2(View headerView2) {
        this.headerView2 = headerView2;
    }

    public void setItemImageClickListener(OnItemImageClickListener itemImageClickListener) {
        this.itemImageClickListener = itemImageClickListener;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    public void setProduct(ShopProduct product) {
        this.product = product;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER2:
                return new ExtraViewHolder(headerView2);
            case HEADER:
                return new ExtraViewHolder(headerView);
            case IMAGE:
                return new ImageViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_detail_photo_item, parent, false));
            case PARAMETERS:
                return new ParameterViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_parameter_item, parent, false));
        }
        return new ExtraViewHolder(null);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        switch (itemType) {
            case IMAGE:
                holder.setView(context, getItem(position), position, itemType);
                break;
            case PARAMETERS:
                holder.setView(context, null, position, itemType);
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
        position--;
        if (photos != null && photos.size() > 0) {
            if (photos.size() > position) {
                return photos.get(position);
            }
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
        if (position == 0) {
            return PARAMETERS;
        } else {
            position--;
        }
        if (photos != null && photos.size() > 0) {
            if (photos.size() > position) {
                return IMAGE;
            }
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
        size++;
        if (!CommonUtil.isCollectionEmpty(photos)) {
            size += photos.size();
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
        private LinearLayout titleLayout;
        private int imageWidth;
        private int width;

        public ImageViewHolder(View itemView) {
            super(itemView);
            width = imageWidth = CommonUtil.getDeviceSize(itemView.getContext()).x;
            if (imageWidth > 805) {
                imageWidth = imageWidth * 3 / 4;
            }
            imageView = itemView.findViewById(R.id.image_view);
            titleLayout = itemView.findViewById(R.id.title_layout);
        }

        @Override
        protected void setViewData(
                Context mContext, final Photo item, final int position, int viewType) {
            String path = JSONUtil.getImagePath(item.getImagePath(), imageWidth);
            if (photos.indexOf(item) == 0) {
                titleLayout.setVisibility(View.VISIBLE);
            } else {
                titleLayout.setVisibility(View.GONE);
            }
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
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemImageClickListener != null) {
                            itemImageClickListener.onItemImageClick(item, position);
                        }
                    }
                });
            } else {
                imageView.setVisibility(View.GONE);
                Glide.with(mContext)
                        .clear(imageView);
            }
        }
    }

    public interface OnItemImageClickListener {
        void onItemImageClick(Object item, int position);
    }

    class ParameterViewHolder extends BaseViewHolder<Photo> {
        @BindView(R.id.tv_describe)
        TextView tvDescribe;
        @BindView(R.id.tv_param_key)
        TextView tvParamKey;
        @BindView(R.id.tv_param_value)
        TextView tvParamValue;
        @BindView(R.id.bottom_line)
        View bottomLine;
        @BindView(R.id.parameters_views)
        LinearLayout parametersViews;
        @BindView(R.id.tv_open)
        TextView tvOpen;
        @BindView(R.id.img_arrow)
        ImageView imgArrow;
        @BindView(R.id.opens_layout)
        RelativeLayout opensLayout;
        @BindView(R.id.parameters_layout)
        LinearLayout parametersLayout;

        private boolean paramIsOpen;
        int singleHeight = CommonUtil.dp2px(context, 30);

        ParameterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(Context mContext, Photo item, int position, int viewType) {
            if (product == null) {
                return;
            }

            parametersViews.removeAllViews();
            if (CommonUtil.isCollectionEmpty(product.getParameters())) {
                tvDescribe.setText(product.getDescribe());
                tvDescribe.setVisibility(View.VISIBLE);
            } else {
                tvDescribe.setVisibility(View.GONE);
                ArrayList<ProductParameter> parameters = product.getParameters();
                if (CommonUtil.isCollectionEmpty(parameters)) {
                    parametersLayout.setVisibility(View.GONE);
                } else {
                    parametersLayout.setVisibility(View.VISIBLE);
                    final int count = parameters.size();
                    if (count <= PARAMETER_LIMIT) {
                        setParamCollapseState(count);
                        opensLayout.setVisibility(View.GONE);
                    } else {
                        opensLayout.setVisibility(View.VISIBLE);
                        setParamCollapseState(count);
                        opensLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 展开，收起
                                paramIsOpen = !paramIsOpen;
                                setParamCollapseState(count);
                            }
                        });
                    }
                    for (int i = 0; i < count; i++) {
                        View itemView = View.inflate(context, R.layout.products_params_item, null);
                        TextView tvKey = itemView.findViewById(R.id.tv_param_key);
                        TextView tvValue = itemView.findViewById(R.id.tv_param_value);
                        tvKey.setText(parameters.get(i)
                                .getKeyName());
                        tvValue.setText(parameters.get(i)
                                .getValue());
                        itemView.findViewById(R.id.bottom_line)
                                .setVisibility(i == count - 1 ? View.GONE : View.VISIBLE);
                        parametersViews.addView(itemView);
                    }
                }
            }
        }

        private void setParamCollapseState(int count) {
            int collapseCount = Math.min(count, PARAMETER_LIMIT);

            tvOpen.setText(paramIsOpen ? "收起" : "展开");
            imgArrow.setRotation(paramIsOpen ? 0 : 180);
            parametersViews.getLayoutParams().height = paramIsOpen ? count * singleHeight :
                    collapseCount * singleHeight;
        }
    }
}
