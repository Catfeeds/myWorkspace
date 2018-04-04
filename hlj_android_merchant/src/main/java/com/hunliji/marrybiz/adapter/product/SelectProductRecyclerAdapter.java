package com.hunliji.marrybiz.adapter.product;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.marrybiz.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chen_bin on 2017/3/30 0030.
 */
public class SelectProductRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View footerView;
    private List<ShopProduct> products;
    private ShopProduct selectedProduct;
    private LayoutInflater inflater;
    private int imageSize;
    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_FOOTER = 1;
    private OnItemClickListener onItemClickListener;

    public SelectProductRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.imageSize = CommonUtil.dp2px(context, 80);
    }

    public void setProducts(List<ShopProduct> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    public void addProducts(List<ShopProduct> products) {
        if (!CommonUtil.isCollectionEmpty(products)) {
            int start = getItemCount() - getFooterViewCount();
            this.products.addAll(products);
            notifyItemRangeInserted(start, products.size());
            if (start > 0) {
                notifyItemChanged(start - 1);
            }
        }
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public ShopProduct getSelectedProduct() {return selectedProduct;}

    public void setSelectedProduct(ShopProduct shopProduct) {
        this.selectedProduct = shopProduct;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return getFooterViewCount() + CommonUtil.getCollectionSize(products);
    }

    @Override
    public int getItemViewType(int position) {
        if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                return new ShopProductViewHolder(inflater.inflate(R.layout.select_product_list_item,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                holder.setView(context, products.get(position), position, viewType);
                break;
        }
    }

    public class ShopProductViewHolder extends BaseViewHolder<ShopProduct> {
        @BindView(R.id.img_check)
        ImageView imgCheck;
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_actual_price)
        TextView tvActualPrice;
        @BindView(R.id.line_layout)
        View lineLayout;

        public ShopProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                Context context, final ShopProduct product, final int position, int viewType) {
            if (product == null) {
                return;
            }
            lineLayout.setVisibility(position < products.size() - 1 ? View.VISIBLE : View.GONE);
            imgCheck.setImageResource(selectedProduct != null && selectedProduct.getId() ==
                    product.getId() ? R.drawable.icon_check_round_40_40_selected : R.drawable
                    .icon_check_round_40_40_normal);
            tvTitle.setText(product.getTitle());
            tvActualPrice.setText(CommonUtil.formatDouble2String(product.getActualPrice()));
            Glide.with(context)
                    .load(ImagePath.buildPath(product.getCoverPath())
                            .width(imageSize)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(imgCover);
        }
    }
}