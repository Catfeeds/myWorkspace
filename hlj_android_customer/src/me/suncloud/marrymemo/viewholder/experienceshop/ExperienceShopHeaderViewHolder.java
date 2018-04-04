package me.suncloud.marrymemo.viewholder.experienceshop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.HomePageFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.Post;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.PosterImageUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.experience.ExperienceShopImageActivity;

/**
 * experience_shop_item_header
 * Created by jinxin on 2017/3/24 0024.
 */

public class ExperienceShopHeaderViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.layout_header)
    public RelativeLayout layoutHeader;
    @BindView(R.id.img_top)
    public ImageView imgTop;

    private Context mContext;
    private City mCity;

    public ExperienceShopHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mCity = Session.getInstance()
                .getMyCity(mContext);
    }

    public void setPosterView(Poster poster, final String panorama, final long mediaId) {
        if (poster == null) {
            return;
        }

        String url = JSONUtil.getImagePath(poster.getPath(), CommonUtil.getDeviceSize(mContext).x);
        if (!TextUtils.isEmpty(url)) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(url)
                    .apply(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888)
                            .placeholder(R.mipmap.icon_empty_image)
                            .dontAnimate())
                    .into(imgTop);

            imgTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(panorama)) {
                        HljWeb.startWebView((Activity) mContext, panorama);
                    } else if (mediaId > 0) {
                        Intent intent = new Intent(mContext, ExperienceShopImageActivity.class);
                        intent.putExtra("id", mediaId);
                        intent.putExtra("title", "店铺全景");
                        mContext.startActivity(intent);
                        ((Activity) (mContext)).overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            });

        } else {
            Glide.with(mContext)
                    .clear(imgTop);
        }
    }

}
