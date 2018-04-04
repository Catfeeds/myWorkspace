package me.suncloud.marrymemo.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.CommunityChannel;
import me.suncloud.marrymemo.util.ImageLoadUtil;

/**
 * Created by werther on 16/5/26.
 */
public class SelectChannelAdapter extends RecyclerView.Adapter<BaseViewHolder<CommunityChannel>> {

    private Context mContext;
    public static final int ITEM_VIEW_TYPE_SECTION = 1;
    public static final int ITEM_VIEW_TYPE_ITEM = 2;
    private ArrayList<CommunityChannel> channels;
    private OnItemClickListener onItemClickListener;

    public SelectChannelAdapter(
            Context context, ArrayList<CommunityChannel> channels) {
        this.mContext = context;
        this.channels = channels;
    }

    @Override
    public BaseViewHolder<CommunityChannel> onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        BaseViewHolder<CommunityChannel> holder;
        switch (viewType) {
            case ITEM_VIEW_TYPE_SECTION:
                View v = inflater.inflate(R.layout.select_channel_section_header, parent, false);
                holder = new SectionHeaderViewHolder(v);
                break;
            case ITEM_VIEW_TYPE_ITEM:
                v = inflater.inflate(R.layout.select_channel_item, parent, false);
                holder = new ItemViewHolder(v);
                break;
            default:
                throw new IllegalStateException("Unexpected viewType (=" + viewType + ")");
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<CommunityChannel> holder, int position) {
        holder.setView(mContext, channels.get(position), position, getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }


    class ItemViewHolder extends BaseViewHolder<CommunityChannel> {
        private View container;
        private TextView tvTitle;
        private ImageView imgCover;
        private ImageView imgCheck;

        public ItemViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            imgCover = (ImageView) itemView.findViewById(R.id.img_cover);
            imgCheck = (ImageView) itemView.findViewById(R.id.img_check);
        }

        @Override
        protected void setViewData(
                Context mContext, CommunityChannel item, final int position, int viewType) {
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, position);
                    }
                }
            });
            tvTitle.setText(item.getTitle());
            ImageLoadUtil.loadImageViewWithoutTransition(mContext, item.getCover_path(), imgCover);
            imgCheck.setVisibility(item.isSelected() ? View.VISIBLE : View.GONE);
        }
    }

    class SectionHeaderViewHolder extends BaseViewHolder<CommunityChannel> {
        private TextView tvTitle;

        public SectionHeaderViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        }

        @Override
        protected void setViewData(
                Context mContext, CommunityChannel item, int position, int viewType) {
            tvTitle.setText(item.getTitle());
        }
    }

    @Override
    public int getItemViewType(int position) {
        CommunityChannel channel = channels.get(position);
        if (channel.getId() < 0) {
            return ITEM_VIEW_TYPE_SECTION;
        } else {
            return ITEM_VIEW_TYPE_ITEM;
        }
    }

    @Override
    public long getItemId(int position) {
        return channels.isEmpty() ? 0 : channels.get(position)
                .getId();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}
