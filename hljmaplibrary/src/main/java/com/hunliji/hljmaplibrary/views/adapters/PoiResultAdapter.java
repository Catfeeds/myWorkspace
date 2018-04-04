package com.hunliji.hljmaplibrary.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljmaplibrary.R;
import com.hunliji.hljmaplibrary.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by luohanlin on 2017/7/25.
 */

public class PoiResultAdapter extends RecyclerView.Adapter<BaseViewHolder<PoiItem>> {

    private Context context;
    private List<PoiItem> poiItems;
    private OnItemClickListener onItemClickListener;

    public PoiResultAdapter(
            Context context, List<PoiItem> poiItems, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.poiItems = poiItems;
        this.onItemClickListener = onItemClickListener;
    }

    public void clear() {
        if (poiItems != null) {
            poiItems.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder<PoiItem> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.poi_search_item, parent, false);
        return new PoiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<PoiItem> holder, final int position) {
        ((PoiViewHolder) holder).getView()
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(position, poiItems.get(position));
                        }
                    }
                });
        holder.setView(context, poiItems.get(position), position, getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return poiItems == null ? 0 : poiItems.size();
    }

    static class PoiViewHolder extends BaseViewHolder<PoiItem> {
        @BindView(R2.id.line)
        View line;
        @BindView(R2.id.tv_name)
        TextView tvName;
        @BindView(R2.id.tv_address)
        TextView tvAddress;
        private View view;

        PoiViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }

        public View getView() {
            return view;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void setViewData(Context mContext, PoiItem item, int position, int viewType) {
            line.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            tvName.setText(item.getTitle());
            tvAddress.setText(item.getAdName() + "" + item.getSnippet());
        }
    }
}
