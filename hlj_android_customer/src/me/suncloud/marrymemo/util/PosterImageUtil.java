package me.suncloud.marrymemo.util;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Poster;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.City;

/**
 * poster ImageView 设置工具
 * Created by jinxin on 2017/3/30 0030.
 */

public class PosterImageUtil {

    private Context mContext;
    private City mCity;

    public static PosterImageUtil util;

    public static PosterImageUtil getInstance(Context mContext, City mCity) {
        synchronized (PosterImageUtil.class) {
            if (util == null) {
                util = new PosterImageUtil();
            }
            util.setContext(mContext);
            util.setCity(mCity);
        }
        return util;
    }

    public PosterImageUtil setContext(Context mContext) {
        this.mContext = mContext;
        return this;
    }

    public PosterImageUtil setCity(City mCity) {
        this.mCity = mCity;
        return this;
    }

    private PosterImageUtil() {

    }

    public void setPosterViewValue(
            View posterView,
            ImageView posterImageView,
            TextView posterTitleView,
            Poster poster,
            String sid,
            int position,
            int width) {
        if (poster != null) {
            if (poster.getId() > 0) {
                posterView.setVisibility(View.VISIBLE);
                OnPosterClickListener clickListener = new OnPosterClickListener(poster,
                        position + 1,
                        sid);
                clickListener.setCity(mCity);
                posterView.setOnClickListener(clickListener);
            } else {
                posterView.setVisibility(View.INVISIBLE);
                posterView.setOnClickListener(null);
            }
            if (posterTitleView != null) {
                posterTitleView.setText(poster.getTitle());
            }
            if (posterImageView != null) {
                String url = JSONUtil.getImagePath(poster.getPath(), width);
                Glide.with(mContext)
                        .asBitmap()
                        .load(url)
                        .apply(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888)
                                .placeholder(R.mipmap.icon_empty_image)
                                .dontAnimate())
                        .into(posterImageView);
            }
        } else {
            posterView.setVisibility(View.GONE);
        }
    }

    class OnPosterClickListener implements View.OnClickListener {

        private Poster poster;
        private String sid;
        private int position;
        private City city;

        public void setCity(City city) {
            this.city = city;
        }

        private OnPosterClickListener(Poster poster, int position, String sid) {
            this.position = position;
            this.poster = poster;
            this.sid = sid;
        }

        @Override
        public void onClick(View v) {
            if (poster != null) {
                BannerUtil.bannerAction(mContext,
                        poster,
                        city,
                        false,
                        com.hunliji.hljtrackerlibrary.utils.TrackerUtil.getSiteJson(sid,
                                position,
                                poster.getTitle()));
            }
        }

    }
}
