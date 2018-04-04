package me.suncloud.marrymemo.adpter.themephotography;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

public class AmorousLevel2Adapter extends RecyclerView.Adapter<AmorousLevel2Adapter
        .AmorousLevel2ViewHolder> {
    private int imgWidth;
    private int imgHeight;
    private Context mContext;
    private List<PosterFloor> posters;
    private OnItemClickListener onItemClickListener;
    private int radius;

    public AmorousLevel2Adapter(Context mContext) {
        this.mContext = mContext;
        posters = new ArrayList<>();
        DisplayMetrics dm = mContext.getResources()
                .getDisplayMetrics();
        imgWidth = Math.round(dm.density * 110);
        imgHeight = Math.round(imgWidth * 10 / 16);
        radius = Math.round(dm.density * 4);
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


    @Override
    public AmorousLevel2ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.theme_destination_item, parent, false);
        return new AmorousLevel2ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(
            AmorousLevel2ViewHolder holder, final int position) {
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
        holder.cardView.setRadius(radius);
        holder.itemView.getLayoutParams().width = imgWidth;
        holder.itemView.getLayoutParams().height = imgHeight;
    }

    @Override
    public int getItemCount() {
        return posters == null ? 0 : posters.size();
    }

    public class AmorousLevel2ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView imgBanner;
        CardView cardView;

        public AmorousLevel2ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            this.imgBanner = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Poster poster, int position);
    }
}
