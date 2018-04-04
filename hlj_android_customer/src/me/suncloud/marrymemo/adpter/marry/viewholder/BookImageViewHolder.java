package me.suncloud.marrymemo.adpter.marry.viewholder;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * Created by hua_rong on 2017/12/8
 */

public class BookImageViewHolder extends BaseViewHolder<Photo> implements View.OnClickListener {

    @BindView(R.id.image)
    ImageView imageView;
    private int width;
    private int height;
    private List<Photo> photos;

    public BookImageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        width = CommonUtil.dp2px(itemView.getContext(), 64);
        height = CommonUtil.dp2px(itemView.getContext(), 64);
        imageView.getLayoutParams().width = width;
        imageView.getLayoutParams().height = height;
        itemView.setOnClickListener(this);
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    @Override
    protected void setViewData(
            Context context, Photo photo, int position, int viewType) {
        if (photo != null && !TextUtils.isEmpty(photo.getImagePath())) {
            Glide.with(context)
                    .load(ImagePath.buildPath(photo.getImagePath())
                            .width(width)
                            .height(height)
                            .cropPath())
                    .apply(new RequestOptions().override(width, height)
                            .placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(imageView);
        }
    }

    @Override
    public void onClick(View view) {
        if (!CommonUtil.isCollectionEmpty(photos)) {
            Intent intent = new Intent(view.getContext(), PicsPageViewActivity.class);
            intent.putExtra("photos", new ArrayList<>(photos));
            intent.putExtra("position", photos.indexOf(getItem()));
            view.getContext()
                    .startActivity(intent);
        }
    }
}
