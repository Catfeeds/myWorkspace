package me.suncloud.marrymemo.adpter.community.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerPosterViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.BannerUtil;

/**
 * Created by mo_yu on 2018/3/13.feeds流poster
 */

public class CommunityPosterLayoutViewHolder extends TrackerPosterViewHolder {

    @BindView(R.id.tv_poster_title)
    TextView tvPosterTitle;
    @BindView(R.id.img_poster)
    ImageView imgPoster;
    @BindView(R.id.poster_layout)
    View posterLayout;
    private int posterWidth;
    private int posterHeight;

    public CommunityPosterLayoutViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.community_poster_layout, parent, false));
    }

    public CommunityPosterLayoutViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        posterWidth = CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px
                (itemView.getContext(),
                32);
        posterHeight = posterWidth * 276 / 686;
        imgPoster.getLayoutParams().height = posterHeight;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Poster poster = getItem();
                BannerUtil.bannerAction(v.getContext(), poster, null, false, null);
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, Poster poster, int position, int viewType) {
        if (poster == null || CommonUtil.isEmpty(poster.getPath())) {
            posterLayout.setVisibility(View.GONE);
        } else {
            posterLayout.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(ImagePath.buildPath(poster.getPath())
                            .width(posterWidth)
                            .height(posterHeight)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(imgPoster);
            tvPosterTitle.setText(poster.getTitle());
        }
    }

    @Override
    public View trackerView() {
        return itemView;
    }

}
