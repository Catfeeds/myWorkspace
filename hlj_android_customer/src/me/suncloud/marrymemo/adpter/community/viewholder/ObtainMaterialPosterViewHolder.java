package me.suncloud.marrymemo.adpter.community.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.Session;

/**
 * Created by jinxin on 2018/3/15 0015.
 */

public class ObtainMaterialPosterViewHolder extends BaseViewHolder<Poster> {

    @BindView(R.id.image)
    ImageView image;

    private int width;
    private int height;
    private Context mContext;
    private City city;

    public ObtainMaterialPosterViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        width = CommonUtil.getDeviceSize(mContext).x;
        height = width / 3;
        city = Session.getInstance()
                .getMyCity(mContext);
    }

    @Override
    protected void setViewData(
            final Context mContext, final Poster poster, int position, int viewType) {
        if (poster == null) {
            return;
        }
        image.getLayoutParams().width = width;
        image.getLayoutParams().height = height;
        image.postInvalidate();
        Glide.with(mContext)
                .load(ImagePath.buildPath(poster.getPath())
                        .width(width)
                        .height(height)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BannerUtil.bannerAction(mContext, poster, city, false, null);
            }
        });
    }
}
