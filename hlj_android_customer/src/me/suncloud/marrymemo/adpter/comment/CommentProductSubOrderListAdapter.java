package me.suncloud.marrymemo.adpter.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.product.ProductComment;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.adapters.DraggableImgGridAdapter;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.comment.viewholder.CommentProductSubOrderViewHolder;

/**
 * Created by chen_bin on 2018/1/8 0008.
 */
public class CommentProductSubOrderListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private View footerView;
    private List<ProductComment> comments;
    private boolean isShowedAddPhotosHintView;
    private LayoutInflater inflater;

    private DraggableImgGridAdapter.OnItemAddListener onItemAddListener;
    private CommentProductSubOrderViewHolder.OnSyncNoteCheckedChangeListener
            onSyncNoteCheckedChangeListener;

    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_FOOTER = 1;

    public CommentProductSubOrderListAdapter(
            Context context, List<ProductComment> comments) {
        this.context = context;
        this.comments = comments;
        this.inflater = LayoutInflater.from(context);
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public boolean isShowedAddPhotosHintView() {
        return isShowedAddPhotosHintView;
    }

    public void setShowedAddPhotosHintView(boolean showedAddPhotosHintView) {
        this.isShowedAddPhotosHintView = showedAddPhotosHintView;
    }

    public void setOnItemAddListener(DraggableImgGridAdapter.OnItemAddListener onItemAddListener) {
        this.onItemAddListener = onItemAddListener;
    }

    public void setOnSyncNoteCheckedChangeListener(
            CommentProductSubOrderViewHolder.OnSyncNoteCheckedChangeListener
                    onSyncNoteCheckedChangeListener) {
        this.onSyncNoteCheckedChangeListener = onSyncNoteCheckedChangeListener;
    }

    @Override
    public int getItemCount() {
        return CommonUtil.getCollectionSize(comments) + getFooterViewCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - getFooterViewCount()) {
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
                CommentProductSubOrderViewHolder holder = new CommentProductSubOrderViewHolder(
                        inflater.inflate(R.layout.comment_product_sub_order_list_item,
                                parent,
                                false));
                holder.setOnItemAddListener(onItemAddListener);
                holder.setOnSyncNoteCheckedChangeListener(onSyncNoteCheckedChangeListener);
                return holder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                if (holder instanceof CommentProductSubOrderViewHolder) {
                    CommentProductSubOrderViewHolder vh = (CommentProductSubOrderViewHolder) holder;
                    vh.setShowAddPhotosHintView(context,
                            !isShowedAddPhotosHintView && position == 0);
                    vh.setView(context, comments.get(position), position, viewType);
                }
                break;
        }
    }

}
