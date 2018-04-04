package me.suncloud.marrymemo.adpter.marry;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.marry.viewholder.MarryBookGroupViewHolder;
import me.suncloud.marrymemo.adpter.marry.viewholder.MarryBookViewHolder;
import me.suncloud.marrymemo.api.marry.MarryApi;
import me.suncloud.marrymemo.model.marry.MarryBook;

/**
 * Created by hua_rong on 2017/11/3
 * 结婚账本
 */

public class MarryBookAdapter extends RecyclerView.Adapter<BaseViewHolder> implements
        StickyRecyclerHeadersAdapter<BaseViewHolder> {

    private Context context;
    private List<MarryBook> marryBooks = new ArrayList<>();
    private View headerView;
    private LayoutInflater inflater;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private OnItemClickListener<MarryBook> onItemClickListener;

    public MarryBookAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setMarryBooks(List<MarryBook> marryBooks) {
        this.marryBooks = marryBooks;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            default:
                View itemView = inflater.inflate(R.layout.marry_book_item, parent, false);
                return new MarryBookViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof MarryBookViewHolder) {
            MarryBookViewHolder viewHolder = (MarryBookViewHolder) holder;
            viewHolder.setOnItemClickListener(onItemClickListener);
            viewHolder.setView(context,
                    getItem(position),
                    getPosition(position),
                    getItemViewType(position));
        }
    }

    private MarryBook getItem(int position) {
        if (isHeader(position)) {
            return null;
        }
        return marryBooks.get(getPosition(position));
    }

    @Override
    public long getHeaderId(int position) {
        if (isHeader(position)) {
            return -1;
        }
        return marryBooks.get(getPosition(position))
                .getType()
                .getParent()
                .getId();
    }

    private int getPosition(int position) {
        return headerView == null ? position : position - 1;
    }

    private boolean isHeader(int position) {
        return position == 0 && headerView != null;
    }

    @Override
    public BaseViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View groupView = inflater.inflate(R.layout.marry_book_group_item, parent, false);
        return new MarryBookGroupViewHolder(groupView);
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int position) {
        MarryBookGroupViewHolder viewHolder = (MarryBookGroupViewHolder) holder;
        viewHolder.setView(context,
                getItem(position),
                getPosition(position),
                getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return (marryBooks == null ? 0 : marryBooks.size()) + (headerView == null ? 0 : 1);
    }

    public int getGiftPosition() {
        for (MarryBook marryBook : marryBooks) {
            if (marryBook.getTypeId() == MarryApi.TYPE_GIFT_ID) {
                return (headerView == null ? 0 : 1) + marryBooks.indexOf(marryBook);
            }
        }
        return -1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    public void setOnItemClickListener(OnItemClickListener<MarryBook> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}
