package me.suncloud.marrymemo.adpter.community.viewholder;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.WeddingBiblesAdapter;
import me.suncloud.marrymemo.model.community.WeddingCatalog;

/**
 * 目录viewHolder
 * Created by chen_bin on 2018/3/16 0016.
 */
public class WeddingCatalogViewHolder extends BaseViewHolder<WeddingCatalog> {

    @BindView(R.id.top_line_layout)
    View topLineLayout;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private WeddingBiblesAdapter adapter;

    public WeddingCatalogViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wedding_catalogs_item, parent, false));
    }

    public WeddingCatalogViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        GridLayoutManager layoutManager = new GridLayoutManager(itemView.getContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int count = adapter.getItemCount();
                if (position == count - 1 && count % 2 != 0) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new WeddingBiblesAdapter(itemView.getContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void setViewData(
            Context mContext, WeddingCatalog catalog, int position, int viewType) {
        if (catalog == null) {
            return;
        }
        topLineLayout.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        tvTitle.setText(catalog.getTitle());
        
        adapter.setBibles(catalog.getChildren());
    }

}