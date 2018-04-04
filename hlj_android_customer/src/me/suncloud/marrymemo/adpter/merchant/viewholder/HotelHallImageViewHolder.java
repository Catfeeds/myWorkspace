package me.suncloud.marrymemo.adpter.merchant.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * 宴会厅图片viewHolder
 * Created by chen_bin on 2017/10/18 0018.
 */
public class HotelHallImageViewHolder extends BaseViewHolder<Photo> {
    @BindView(R.id.image)
    ImageView imgCover;
    private int imageWidth;

    public HotelHallImageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.imageWidth = CommonUtil.getDeviceSize(itemView.getContext()).x;
    }

    @Override
    protected void setViewData(Context mContext, Photo photo, int position, int viewType) {
        if (photo == null) {
            return;
        }
        int imageHeight = photo.getWidth() == 0 ? photo.getWidth() : Math.round(imageWidth *
                photo.getHeight() * 1.0f / photo.getWidth());
        imgCover.getLayoutParams().width = imageWidth;
        imgCover.getLayoutParams().height = imageHeight;
        Glide.with(mContext)
                .load(ImagePath.buildPath(photo.getImagePath())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
    }
}
