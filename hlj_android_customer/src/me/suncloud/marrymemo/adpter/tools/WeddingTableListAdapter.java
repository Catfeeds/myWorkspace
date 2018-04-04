package me.suncloud.marrymemo.adpter.tools;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.adapters.viewholders.BaseDraggableItemViewHolder;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.tools.viewholder.WeddingTableViewHolder;
import me.suncloud.marrymemo.model.tools.WeddingTable;
import me.suncloud.marrymemo.util.user.UserPrefUtil;


/**
 * Created by chen_bin on 2017/11/23 0023.
 */
public class WeddingTableListAdapter extends RecyclerView
        .Adapter<BaseDraggableItemViewHolder<WeddingTable>> implements
        DraggableItemAdapter<BaseDraggableItemViewHolder> {
    private Context context;
    private List<WeddingTable> tables;
    private List<WeddingTable> searchTables;
    private String keyword;
    private boolean isShowTableDragHintView;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;
    private OnItemDragSucceedListener onItemDragSucceedListener;

    public final static int DRAG_HINT_POSITION = 1;
    public final static int START_WITHOUT_TO_BE_ARRANGED = 2;
    public final static int START_WITH_TO_BE_ARRANGED = 3;

    public WeddingTableListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        setHasStableIds(true);
    }

    public List<WeddingTable> getTables() {
        return tables;
    }

    public void setTables(List<WeddingTable> tables) {
        this.tables = tables;
        notifyDataSetChanged();
    }

    public void setSearchTables(List<WeddingTable> searchTables) {
        this.searchTables = searchTables;
        notifyDataSetChanged();
    }

    public void addTables(List<WeddingTable> tables) {
        if (!CommonUtil.isCollectionEmpty(tables)) {
            int positionStart = getItemCount();
            if (hasToBeArranged()) {
                positionStart = positionStart - 1;
            }
            this.tables.addAll(positionStart, tables);
            notifyItemRangeInserted(positionStart, tables.size());
        }
    }

    public boolean hasToBeArranged() {
        return !CommonUtil.isCollectionEmpty(tables) && tables.get(tables.size() - 1)
                .getId() == 0;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setShowTableDragHintView(boolean showTableDragHintView) {
        this.isShowTableDragHintView = showTableDragHintView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemDragSucceedListener(OnItemDragSucceedListener onItemDragSucceedListener) {
        this.onItemDragSucceedListener = onItemDragSucceedListener;
    }

    @Override
    public int getItemCount() {
        return CommonUtil.getCollectionSize(TextUtils.isEmpty(keyword) ? tables : searchTables);
    }

    @Override
    public long getItemId(int position) {
        return tables.get(position)
                .getId();
    }

    @Override
    public BaseDraggableItemViewHolder<WeddingTable> onCreateViewHolder(
            ViewGroup parent, int viewType) {
        WeddingTableViewHolder tableViewHolder = new WeddingTableViewHolder(inflater.inflate(R
                        .layout.wedding_table_list_item,
                parent,
                false));
        tableViewHolder.setOnItemClickListener(onItemClickListener);
        return tableViewHolder;
    }

    @Override
    public void onBindViewHolder(BaseDraggableItemViewHolder<WeddingTable> holder, int position) {
        if (holder instanceof WeddingTableViewHolder) {
            WeddingTableViewHolder tableViewHolder = (WeddingTableViewHolder) holder;
            WeddingTable table;
            if (TextUtils.isEmpty(keyword)) {
                table = tables.get(position);
            } else {
                table = searchTables.get(position);
            }
            tableViewHolder.setKeyword(keyword);
            tableViewHolder.setShowTableDragHintView(TextUtils.isEmpty(keyword) &&
                    isShowTableDragHintView && position == DRAG_HINT_POSITION);
            tableViewHolder.setView(context, table, position, getItemViewType(position));
        }
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return TextUtils.isEmpty(keyword);
    }

    @Override
    public boolean onCheckCanStartDrag(
            BaseDraggableItemViewHolder holder, int position, int x, int y) {
        return TextUtils.isEmpty(keyword) && getItemId(position) > 0;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(
            BaseDraggableItemViewHolder holder, int position) {
        return new ItemDraggableRange(0, tables.size() - (hasToBeArranged() ? 2 : 1));
    }

    @Override
    public void onItemDragStarted(int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }
        WeddingTable table = tables.remove(fromPosition);
        tables.add(toPosition, table);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
        if (fromPosition != toPosition) {
            int position = -1;
            if (toPosition == DRAG_HINT_POSITION) {
                position = fromPosition;
            } else if (fromPosition == DRAG_HINT_POSITION) {
                position = toPosition;
            }
            if (position > -1) {
                hideTableDragHintView(position);
            }
            if (onItemDragSucceedListener != null) {
                onItemDragSucceedListener.onItemDragSucceed();
            }
        }
        notifyDataSetChanged();
    }

    public void hideTableDragHintView(int position) {
        if (isShowTableDragHintView) {
            isShowTableDragHintView = false;
            UserPrefUtil.getInstance(context)
                    .setWeddingTableDragHintClicked(true);
            notifyItemChanged(position);
        }
    }

    public interface OnItemDragSucceedListener {
        void onItemDragSucceed();
    }
}