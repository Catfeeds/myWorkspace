package me.suncloud.marrymemo.viewholder.experienceshop;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.PosterImageUtil;
import me.suncloud.marrymemo.util.Session;

/**
 * experience_shop_item_image
 * Created by jinxin on 2017/3/24 0024.
 */

public class ExperienceShopImageViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.img_banner_single)
    public ImageView imgBannerSingle;

    private Context mContext;
    private City mCity;

    public ExperienceShopImageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mCity = Session.getInstance()
                .getMyCity(mContext);
    }

    public void setPosterView(Poster poster) {
        if (poster == null) {
            return;
        }
        PosterImageUtil.getInstance(mContext, mCity)
                .setPosterViewValue(imgBannerSingle,
                        imgBannerSingle,
                        null,
                        poster,
                        null,
                        0,
                        CommonUtil.getDeviceSize(mContext).x);
    }
}
