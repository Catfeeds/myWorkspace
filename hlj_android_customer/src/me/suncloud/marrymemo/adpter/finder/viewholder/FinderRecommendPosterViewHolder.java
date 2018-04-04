package me.suncloud.marrymemo.adpter.finder.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * 发现页推荐poster
 * Created by chen_bin on 2018/2/5 0005.
 */
public class FinderRecommendPosterViewHolder extends BaseViewHolder<Poster> {

    @BindView(R.id.img_cover)
    RoundedImageView imgCover;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private int coverWidth;

    public FinderRecommendPosterViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        coverWidth = (CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px
                (itemView.getContext(),
                28)) / 2;
        imgCover.getLayoutParams().height = coverWidth;
    }

    @Override
    protected void setViewData(Context mContext, Poster poster, int position, int viewType) {
        if (poster == null) {
            return;
        }
        Glide.with(mContext)
                .load(ImagePath.buildPath(poster.getPath())
                        .width(coverWidth)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        tvTitle.setText(poster.getTitle());
    }
}
