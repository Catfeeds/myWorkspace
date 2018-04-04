package me.suncloud.marrymemo.adpter.product;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.product.ProductTopic;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.product.viewholder.ProductSubPageViewHolder;

/**
 * 婚品专题adapter
 * Created by chen_bin on 2016/11/26 0026.
 */
public class ProductSubPageRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View footerView;
    private List<ProductTopic> topics;
    private LayoutInflater inflater;
    private static final int ITEM_TYPE_LIST = 0;
    private static final int ITEM_TYPE_FOOTER = 1;

    public ProductSubPageRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setTopics(List<ProductTopic> topics) {
        this.topics = topics;
        notifyDataSetChanged();
    }

    public void addTopics(List<ProductTopic> topics) {
        if (!CommonUtil.isCollectionEmpty(topics)) {
            int start = getItemCount() - getFooterViewCount();
            this.topics.addAll(topics);
            notifyItemRangeInserted(start, topics.size());
        }
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void addFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public int getItemCount() {
        return getFooterViewCount() + CommonUtil.getCollectionSize(topics);
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
                return new ProductSubPageViewHolder(inflater.inflate(R.layout
                                .product_sub_page_list_item,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                holder.setView(context, topics.get(position), position, viewType);
                break;
        }
    }
}

