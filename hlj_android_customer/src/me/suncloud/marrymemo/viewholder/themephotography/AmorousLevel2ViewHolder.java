package me.suncloud.marrymemo.viewholder.themephotography;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.PosterFloor;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.themephotography.AmorousLevel2Adapter;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by jinxin on 2016/10/13.
 */

public class AmorousLevel2ViewHolder extends RecyclerView.ViewHolder
        implements AmorousLevel2Adapter.OnItemClickListener {
    public View itemView;
    public RecyclerView list;
    public LinearLayoutManager manager;
    public AmorousLevel2Adapter adapter;
    public SpaceItemDecoration itemDecoration;
    public Context mContext;
    public Point point;
    public DisplayMetrics dm;

    public AmorousLevel2ViewHolder(View itemView, Context context) {
        super(itemView);
        this.itemView = itemView;
        mContext = context;
        point = JSONUtil.getDeviceSize(mContext);
        dm = mContext.getResources()
                .getDisplayMetrics();
        list = (RecyclerView) itemView.findViewById(R.id.list);
        manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        adapter = new AmorousLevel2Adapter(mContext);
        adapter.setOnItemClickListener(this);
        itemDecoration = new SpaceItemDecoration();
    }

    public void setAmorousLevel2(List<PosterFloor> holes) {
        if (list.getLayoutManager() == null) {
            list.setLayoutManager(manager);
        }
        if (list.getAdapter() == null) {
            list.setAdapter(adapter);
        }
        list.removeItemDecoration(itemDecoration);
        list.addItemDecoration(itemDecoration);
        adapter.setPosters(holes);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Poster poster, int position) {
        if (poster != null) {
            BannerUtil.bannerJump(mContext, poster,null);
        }
    }

    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        int paddingOffset;
        int padding;

        public SpaceItemDecoration() {
            padding = Math.round(dm.density * 10);
            paddingOffset = Math.round(dm.density * 12);
        }

        @Override
        public void getItemOffsets(
                Rect outRect,
                View view,
                RecyclerView parent,
                RecyclerView.State state) {
            int viewPosition = parent.getChildAdapterPosition(view);
            if (viewPosition == 0) {
                outRect.left = paddingOffset;
                outRect.right = 0;
            } else if (viewPosition == parent.getAdapter()
                    .getItemCount() - 1) {
                outRect.left = padding;
                outRect.right = paddingOffset;
            } else {
                outRect.left = padding;
                outRect.right = 0;
            }
            outRect.top = paddingOffset;
            outRect.bottom = paddingOffset;
        }
    }

}
