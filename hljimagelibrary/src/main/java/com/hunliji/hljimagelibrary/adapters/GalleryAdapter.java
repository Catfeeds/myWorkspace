package com.hunliji.hljimagelibrary.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.ObjectBindAdapter;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.R;
import com.hunliji.hljimagelibrary.models.Gallery;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangtao on 2017/5/10.
 */

public class GalleryAdapter extends ObjectBindAdapter<Gallery> implements ObjectBindAdapter
        .ViewBinder<Gallery> {

    private List<Long> galleryIds;

    public GalleryAdapter(
            Context context) {
        super(context, new ArrayList<Gallery>(), R.layout.gallery_item___img);
        setViewBinder(this);
    }

    public void setGalleries(List<Gallery> galleries) {
        if (galleries == null) {
            return;
        }
        data.clear();
        data.addAll(galleries);
        notifyDataSetChanged();
    }

    public void setGalleryIds(List<Long> galleryIds) {
        this.galleryIds = galleryIds;
        notifyDataSetChanged();
    }

    @Override
    public void setViewValue(View view, Gallery gallery, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder();
            holder.selectedView = view.findViewById(R.id.icon_selected);
            holder.imageView = (ImageView) view.findViewById(R.id.file_icon);
            holder.titleView = (TextView) view.findViewById(R.id.file_title);
            holder.countView = (TextView) view.findViewById(R.id.file_count);
            view.setTag(holder);
        }
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.titleView.setText(gallery.getName());
        holder.countView.setText("(" + String.valueOf(gallery.getPhotoCount()) + ")");
        if (!CommonUtil.isCollectionEmpty(galleryIds) && (gallery.getId() == 0 || galleryIds
                .contains(
                gallery.getId()))) {
            holder.selectedView.setVisibility(View.VISIBLE);
        } else {
            holder.selectedView.setVisibility(View.GONE);
        }
        String path = gallery.getPath();
        if (CommonUtil.isHttpUrl(path)) {
            path = ImagePath.buildPath(path)
                    .width(CommonUtil.dp2px(view.getContext(), 40))
                    .cropPath();
        }
        Glide.with(view.getContext())
                .load(path)
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_image_mark_white_66_52)
                        .centerCrop())
                .into(holder.imageView);

    }

    private class ViewHolder {
        View selectedView;
        ImageView imageView;
        TextView titleView;
        TextView countView;
    }
}
