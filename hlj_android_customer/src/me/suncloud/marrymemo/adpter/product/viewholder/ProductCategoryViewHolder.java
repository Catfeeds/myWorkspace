package me.suncloud.marrymemo.adpter.product.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.BannerUtil;

/**
 * 婚品分类viewHolder
 * Created by chen_bin on 2016/11/26 0026.
 */
public class ProductCategoryViewHolder extends BaseViewHolder<Poster> {
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    public ProductCategoryViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.getLayoutParams().width = (CommonUtil.getDeviceSize(itemView.getContext()).x -
                CommonUtil.dp2px(
                itemView.getContext(),
                20)) / 4;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BannerUtil.bannerJump(view.getContext(), getItem(), null);
            }
        });
    }

    @Override
    protected void setViewData(
            final Context mContext, final Poster poster, final int position, int viewType) {
        if (poster == null) {
            return;
        }
        Glide.with(mContext)
                .load(poster.getPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        tvTitle.setText(poster.getTitle());
    }
}