package com.hunliji.hljkefulibrary.adapters.viewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.models.Size;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljimagelibrary.views.activities.SingleImageActivity;
import com.hunliji.hljkefulibrary.R;
import com.hunliji.hljkefulibrary.moudles.EMChat;

/**
 * Created by wangtao on 2017/10/20.
 */

public class EMImageViewHolder extends EMChatMessageBaseViewHolder {


    private ImageView imageView;
    private int singleEdge;


    public EMImageViewHolder(ViewGroup parent, boolean isReceive) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(isReceive ? R.layout.chat_image_left___chat : R.layout
                                .chat_image_right___chat,
                        parent,
                        false));
    }

    private EMImageViewHolder(View itemView) {
        super(itemView);
        singleEdge = CommonUtil.dp2px(itemView.getContext(), 150);
        imageView = itemView.findViewById(R.id.image_view);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = getItem().getRemoteUrl();
                if (TextUtils.isEmpty(path)) {
                    path = getItem().getImagePath();
                }
                if (!TextUtils.isEmpty(path)) {
                    Intent intent = new Intent(v.getContext(), SingleImageActivity.class);
                    intent.putExtra("path", path);
                    v.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, EMChat item, int position, int viewType) {
        String path = item.getImagePath();
        if (!TextUtils.isEmpty(path)) {
            int height = item.getHeight();
            int width = item.getWidth();
            if ((width == 0 || height == 0) && !path.startsWith("http://") && !path.startsWith(
                    "https://")) {
                Size size = ImageUtil.getImageSizeFromPath(path);
                width = size.getWidth();
                height = size.getHeight();
            }
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imageView
                    .getLayoutParams();
            if (height != 0 && width != 0) {
                params.width = height > width ? width * singleEdge / height : singleEdge;
                params.height = width > height ? height * singleEdge / width : singleEdge;
            }
            final int finalWidth = width;
            final int finalHeight = height;
            Glide.with(imageView)
                    .load(path)
                    .apply(new RequestOptions().dontAnimate()
                            .fitCenter()
                            .placeholder(R.mipmap.icon_empty_image))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(
                                @Nullable GlideException e,
                                Object model,
                                Target<Drawable> target,
                                boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(
                                Drawable resource,
                                Object model,
                                Target<Drawable> target,
                                DataSource dataSource,
                                boolean isFirstResource) {
                            if (resource != null && (finalHeight == 0 || finalWidth == 0)) {
                                int height = resource.getIntrinsicHeight();
                                int width = resource.getIntrinsicWidth();
                                ViewGroup.MarginLayoutParams params = (ViewGroup
                                        .MarginLayoutParams) imageView.getLayoutParams();
                                params.width = height > width ? width * singleEdge / height :
                                        singleEdge;
                                params.height = width > height ? height * singleEdge / width :
                                        singleEdge;
                            }
                            return false;
                        }
                    })
                    .into(imageView);
        } else {
            imageView.setImageBitmap(null);
            Glide.with(imageView)
                    .clear(imageView);
        }

    }
}
