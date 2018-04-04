package me.suncloud.marrymemo.viewholder.themephotography;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.PosterFloor;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.themephotography.AmorousAdapter;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by jinxin on 2016/10/13.
 */

public class AmorousViewHolder extends RecyclerView.ViewHolder implements
        AmorousAdapter.OnItemClickListener  {
    public View itemView;
    public RecyclerView list;
    public  LinearLayoutManager manager;
    public int offset;
    public int itemWidth;
    public AmorousAdapter adapter;
    public Context mContext;
    public Point point;
    public DisplayMetrics dm;

    public AmorousViewHolder(View itemView,Context context) {
        super(itemView);
        mContext = context;
        point = JSONUtil.getDeviceSize(mContext);
        dm = mContext.getResources()
                .getDisplayMetrics();
        offset = Math.round(dm.density * 5);
        itemWidth = Math.round((point.x - offset * 2) / 5);
        this.itemView = itemView;
        list = (RecyclerView) itemView.findViewById(R.id.list);
        list.setPadding(offset,
                itemView.getPaddingTop(),
                offset,
                itemView.getPaddingBottom());
        manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        adapter = new AmorousAdapter(mContext);
        adapter.setItemWidth(itemWidth);
        adapter.setOnItemClickListener(this);
    }

    public void setAmorous(List<PosterFloor> holes) {
        if (list.getLayoutManager() == null) {
            list.setLayoutManager(manager);
        }
        if (list.getAdapter() == null) {
            list.setAdapter(adapter);
        }
        adapter.setPosters(holes);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Poster poster, int position) {
        if (poster != null) {
            BannerUtil.bannerJump(mContext,
                    poster,
                    null);
        }
    }

}
