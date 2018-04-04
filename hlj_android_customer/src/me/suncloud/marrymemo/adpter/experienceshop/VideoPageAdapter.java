package me.suncloud.marrymemo.adpter.experienceshop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljvideolibrary.activities.VideoPreviewActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.experience.StoreVideo;

/**
 * 体验店 视屏播放 viewpager adaper
 * Created by jixnin on 2017/3/23 0023.
 */

public class VideoPageAdapter extends PagerAdapter {

    private List<StoreVideo> videos;
    private Context mContext;
    private int videoHeight;
    private int videoWidth;

    public VideoPageAdapter(Context mContext) {
        this.mContext = mContext;
        videos = new ArrayList<>();
        videoHeight = Math.round(CommonUtil.getDeviceSize(mContext).x * 9 / 16);
        videoWidth = CommonUtil.getDeviceSize(mContext).x - CommonUtil.dp2px(mContext, 32);
    }

    public void setVideos(List<StoreVideo> videos) {
        if (videos != null) {
            this.videos.clear();
            this.videos.addAll(videos);
        }
    }

    @Override
    public int getCount() {
        return videos == null ? 0 : videos.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.experience_shop_video_item, container, false);
        ViewHolder holder = new ViewHolder(itemView);
        StoreVideo video = videos.get(position);
        setHolderVideo(holder, video);
        container.addView(itemView);
        return itemView;
    }

    private void setHolderVideo(ViewHolder holder, StoreVideo video) {
        holder.layoutVideo.getLayoutParams().height = videoHeight;
        if (video != null) {
            holder.tvTitle.setText(video.getTitle());
            holder.tvDes.setText("我们为你提供");
            String path = ImageUtil.getImagePath(video.getVframe(), videoWidth);
            if (!TextUtils.isEmpty(path)) {
                Glide.with(mContext)
                        .load(path)
                        .apply(new RequestOptions().placeholder(null))
                        .into(holder.imgVideo);
            } else {
                Glide.with(mContext)
                        .clear(holder.imgVideo);
            }
        }
        holder.layoutVideo.setTag(video.getPath());
        holder.layoutVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = (String) v.getTag();
                if (TextUtils.isEmpty(path)) {
                    return;
                }
                Intent intent = new Intent(mContext, VideoPreviewActivity.class);
                intent.putExtra("uri", Uri.parse(path));
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_up_to_top,
                        R.anim.activity_anim_default);
            }
        });
    }

    class ViewHolder {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_des)
        TextView tvDes;
        @BindView(R.id.img_video)
        ImageView imgVideo;
        @BindView(R.id.layout_video)
        RelativeLayout layoutVideo;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
