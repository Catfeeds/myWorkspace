package me.suncloud.marrymemo.adpter.product;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.product.viewholder.SelectedProductViewHolder;
import me.suncloud.marrymemo.view.product.ShopProductDetailActivity;

/**
 * 婚品精选列表Adapter
 * Created by chen_bin on 2017/5/2 0002.
 */
public class SelectedProductRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View footerView;
    private View headerView;
    private List<ShopProduct> products;
    private LayoutInflater inflater;
    private static final int ITEM_TYPE_LIST = 0;
    private static final int ITEM_TYPE_HEADER = 1;
    private static final int ITEM_TYPE_FOOTER = 2;

    public SelectedProductRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<ShopProduct> getProducts() {
        return products;
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

    public int getHeaderViewCount() {
        return headerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    @Override
    public int getItemCount() {
        return getHeaderViewCount() + getFooterViewCount() + CommonUtil.getCollectionSize(products);
    }

    @Override
    public int getItemViewType(int position) {
        if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else if (getHeaderViewCount() > 0 && position == 0) {
            return ITEM_TYPE_HEADER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                SelectedProductViewHolder productViewHolder = new SelectedProductViewHolder
                        (inflater.inflate(
                        R.layout.selected_product_list_item,
                        parent,
                        false));
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
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                if (holder instanceof SelectedProductViewHolder) {
                    SelectedProductViewHolder productViewHolder = (SelectedProductViewHolder)
                            holder;
                    productViewHolder.setShowBottomLineView(position < products.size() - 1 +
                            getHeaderViewCount());
                    productViewHolder.setView(context,
                            products.get(position - getHeaderViewCount()),
                            position,
                            viewType);
                }
                break;
        }
    }
}