package me.suncloud.marrymemo.viewholder.experienceshop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * Created by hua_rong on 2017/3/31.
 * 体验店图片
 */
public class ExperienceImageViewHolder extends BaseViewHolder<Photo> {
    @BindView(R.id.image)
    ImageView imgCover;
    private ArrayList<Photo> photos;
    private int imageWidth;

    public ExperienceImageViewHolder(View itemView, ArrayList<Photo> photos) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.photos = photos;
        this.imageWidth = Math.round((CommonUtil.getDeviceSize(itemView.getContext()).x -
                CommonUtil.dp2px(
                itemView.getContext(),
                16)) / 2);
    }

    @Override
    protected void setViewData(final Context context, Photo photo, int position, int viewType) {
        if (photo == null) {
            return;
        }
        int width = photo.getWidth();
        int height = photo.getHeight();
        float scale = 1.0f;
        if (width > 0 && height > 0) {
            scale = height * 1.0f / width;
        }
        int imageHeight = Math.round(scale * imageWidth);
        imgCover.getLayoutParams().width = imageWidth;
        imgCover.getLayoutParams().height = imageHeight;
        Glide.with(context)
                .load(ImagePath.buildPath(photo.getImagePath())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        itemView.setTag(position);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                Intent intent = new Intent(context, PicsPageViewActivity.class);
                intent.putExtra("photos", photos);
                intent.putExtra("position", position);
                context.startActivity(intent);
                if (context instanceof Activity) {
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            }
        });

    }
}