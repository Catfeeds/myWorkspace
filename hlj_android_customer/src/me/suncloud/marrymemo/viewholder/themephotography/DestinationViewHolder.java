package me.suncloud.marrymemo.viewholder.themephotography;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.PosterFloor;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.ImageLoadUtil;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by jinxin on 2016/10/13.
 */

public class DestinationViewHolder extends RecyclerView.ViewHolder {
    public View itemView;
    public int imgWidth;
    public int imgHeight;
    public Context mContext;
    public Point point;
    public DisplayMetrics dm;

    @BindView(R.id.layout_destination)
    public LinearLayout layoutDestination;
    @BindView(R.id.layout_title)
    public LinearLayout layoutTitle;

    public DestinationViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.itemView = itemView;
        mContext = context;
        point = JSONUtil.getDeviceSize(mContext);
        dm = mContext.getResources()
                .getDisplayMetrics();
        imgWidth = Math.round(Math.round(point.x - Math.round(12 * 3 * dm.density)) / 2);
        imgHeight = Math.round(imgWidth * 1.0f * 10 / 16);
    }

    public void setDestination(View view, PosterFloor posterFloor, boolean showTitle) {
        layoutTitle.setVisibility(showTitle ? View.VISIBLE : View.GONE);
        if (posterFloor != null) {
            setViewValue(view, posterFloor, 0);
        }
    }

    public void setViewValue(
            View view, PosterFloor floor, int position) {
        ItemViewHolder holder = (ItemViewHolder) view.getTag();
        if (holder == null) {
            holder = new ItemViewHolder(view);
            view.setTag(holder);
        }
        Poster poster = floor.getPoster();
        if (poster == null) {
            return;
        }
        String imgPath = JSONUtil.getImagePath(poster.getPath(), imgWidth);
        if (!JSONUtil.isEmpty(imgPath)) {
            ImageLoadUtil.loadImageView(mContext, imgPath, holder.image);
        } else {
            ImageLoadUtil.clear(mContext, holder.image);
        }
        holder.itemView.getLayoutParams().height = imgHeight;
        OnItemClickListener onItemClickListener = new OnItemClickListener(poster);
        holder.itemView.setOnClickListener(onItemClickListener);
    }

    class ItemViewHolder {
        View itemView;
        ImageView image;

        public ItemViewHolder(View itemView) {
            this.itemView = itemView;
            this.image = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    class OnItemClickListener implements View.OnClickListener {
        private Poster poster;

        public OnItemClickListener(Poster poster) {
            this.poster = poster;
        }

        @Override
        public void onClick(View v) {
            if (poster != null) {
                BannerUtil.bannerAction(mContext, poster, null, false, null);
            }
        }
    }
}
