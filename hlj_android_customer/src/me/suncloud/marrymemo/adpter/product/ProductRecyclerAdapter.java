package me.suncloud.marrymemo.adpter.product;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.CommonProductViewHolder;
import com.hunliji.hljtrackerlibrary.utils.TrackerUtil;

import org.json.JSONObject;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.ProductMerchantActivity;
import me.suncloud.marrymemo.view.product.ShopProductDetailActivity;

/**
 * 婚品通用瀑布流Adapter
 * Created by mo_yu on 2016/11/16.
 */
public class ProductRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View headerView;
    private View footerView;
    private List<ShopProduct> products;
    private LayoutInflater inflater;
    private static final int ITEM_TYPE_HEADER = 0;
    private static final int ITEM_TYPE_LIST_RATIO_1_TO_1 = 1; //1:1比例的图片类型
    private static final int ITEM_TYPE_LIST_RATIO_2_TO_3 = 2;//1:1.5比例的图片类型
    private static final int ITEM_TYPE_FOOTER = 3;

    public ProductRecyclerAdapter(Context context) {
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
        }
    }

    public int getHeaderViewCount() {
        return headerView != null ? 1 : 0;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public int getItemCount() {
        return getHeaderViewCount() + getFooterViewCount() + CommonUtil.getCollectionSize(products);
    }

    @Override
    public int getItemViewType(int position) {
        if (getHeaderViewCount() > 0 && position == 0) {
            return ITEM_TYPE_HEADER;
        } else if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            ShopProduct product = getItem(position);
            Photo photo = product.getCoverImage();
            if (photo == null) {
                return ITEM_TYPE_LIST_RATIO_1_TO_1;
            } else {
                int width = photo.getWidth();
                int height = photo.getHeight();
                return width == 0 || height * 1.0f / width < 1.2f ? ITEM_TYPE_LIST_RATIO_1_TO_1 :
                        ITEM_TYPE_LIST_RATIO_2_TO_3;
            }
        }
    }

    public ShopProduct getItem(int position) {
        int index = position - getHeaderViewCount();
        return products.get(index);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                ExtraBaseViewHolder headerViewHolder = new ExtraBaseViewHolder(headerView);
                headerViewHolder.itemView.setLayoutParams(new StaggeredGridLayoutManager
                        .LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                return headerViewHolder;
            case ITEM_TYPE_FOOTER:
                ExtraBaseViewHolder footerViewHolder = new ExtraBaseViewHolder(footerView);
                footerViewHolder.itemView.setLayoutParams(new StaggeredGridLayoutManager
                        .LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                return footerViewHolder;
            default:
                int style;
                if (viewType == ITEM_TYPE_LIST_RATIO_1_TO_1) {
                    style = CommonProductViewHolder.STYLE_RATIO_1_TO_1;
                } else {
                    style = CommonProductViewHolder.STYLE_RATIO_2_TO_3;
                }
                CommonProductViewHolder productViewHolder = new CommonProductViewHolder(inflater
                        .inflate(
                        R.layout.common_product_list_item___cv,
                        parent,
                        false), style);
                productViewHolder.setOnItemClickListener(new OnItemClickListener<ShopProduct>() {
                    @Override
                    public void onItemClick(int position, ShopProduct product) {
                        if (product != null && product.getId() > 0) {
                            Intent intent = new Intent(context, ShopProductDetailActivity.class);
                            JSONObject siteJson = null;
                            if (context instanceof ProductMerchantActivity) {
                                siteJson = TrackerUtil.getSiteJson("AB1/C1",
                                        products.indexOf(product) + 1,
                                        null);
                                intent.putExtra("is_from_shop", true);
                            }
                            if (siteJson != null) {
                                intent.putExtra("site", siteJson.toString());
                            }
                            intent.putExtra("id", product.getId());
                            context.startActivity(intent);
                        }
                    }
                });
                return productViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_HEADER:
            case ITEM_TYPE_FOOTER:
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                if (params != null && params instanceof StaggeredGridLayoutManager.LayoutParams) {
                    ((StaggeredGridLayoutManager.LayoutParams) params).setFullSpan(true);
                }
                break;
            case ITEM_TYPE_LIST_RATIO_1_TO_1:
            case ITEM_TYPE_LIST_RATIO_2_TO_3:
                holder.setView(context, getItem(position), position, viewType);
                break;
        }
    }

}
