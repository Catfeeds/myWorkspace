package me.suncloud.marrymemo.adpter.themephotography;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.PosterFloor;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by jinxin on 2016/10/9.
 */

public class AmorousAdapter extends RecyclerView.Adapter<AmorousAdapter.AmorousViewHolder> {
    private int imgWidth;
    private int itemWidth;
    private Context mContext;
    private List<PosterFloor> posters;
    private OnItemClickListener onItemClickListener;

    public AmorousAdapter(Context mContext) {
        this.mContext = mContext;
        posters = new ArrayList<>();
        DisplayMetrics dm = mContext.getResources()
                .getDisplayMetrics();
        imgWidth = Math.round(dm.density * 36);
    }

    public void setPosters(List<PosterFloor> posters) {
        if (posters == null) {
            return;
        }
        this.posters.clear();
        this.posters.addAll(posters);
    }

    public void setOnItemClickListener(
            OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    @Override
    public AmorousViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.theme_amorous_item, parent, false);
        return new AmorousViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(
            AmorousViewHolder holder, final int position) {
        final Poster poster = posters.get(position)
                .getPoster();
        if (poster == null) {
            return;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(poster, position);
                }
            }
        });

        holder.itemView.getLayoutParams().width = itemWidth;

        String imgPath = JSONUtil.getImagePath(poster.getPath(), imgWidth);
        if (!JSONUtil.isEmpty(imgPath)) {
            Glide.with(mContext)
                    .load(imgPath)
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(holder.imgBanner);
        } else {
            Glide.with(mContext)
                    .clear(holder.imgBanner);
        }
        holder.tvTitle.setText(poster.getTitle());
    }

    @Override
    public int getItemCount() {
        return posters == null ? 0 : posters.size();
    }

    public class AmorousViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView imgBanner;
        TextView tvTitle;

        public AmorousViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.imgBanner = (ImageView) itemView.findViewById(R.id.img_banner);
            this.tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Poster poster, int position);
    }
}
