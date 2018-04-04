package com.hunliji.hljcarlibrary.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jinxin on 2015/11/5.
 */
public class WeddingCarHeaderImageAdapter extends PagerAdapter {
    private LayoutInflater mInflater;
    private ArrayList<Photo> photos;
    private Context mContext;
    private int width;
    private int height;
    private OnItemClickListener onItemClickListener;

    public WeddingCarHeaderImageAdapter(Context context, List<Photo> list) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        photos = new ArrayList<>();
        if(list != null){
            photos.addAll(list);
        }
        width = CommonUtil.getDeviceSize(mContext).x;
        height = Math.round(width * 9.0F / 16);
    }

    public void setPhotos(List<Photo> photos){
        this.photos.clear();
        if(photos != null){
            this.photos.addAll(photos);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return photos == null ? 0 : photos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = getView(position, container);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public View getView(final int position, ViewGroup container) {
        View convertView = mInflater.inflate(R.layout.image_item___cm, container, false);
        ViewHolder holder = new ViewHolder(convertView);
        holder.imageView.getLayoutParams().width = width;
        holder.imageView.getLayoutParams().height = height;
        final Photo photo = photos.get(position);
        if (photo != null) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.OnPhotoClickListener(photo, position);
                    }
                }
            });
            Glide.with(mContext)
                    .load(ImagePath.buildPath(photo.getImagePath())
                            .width(width)
                            .height(height)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(holder.imageView);
        }
        return convertView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void OnPhotoClickListener(Object item, int position);
    }

     class ViewHolder {
        @BindView(R2.id.image)
        ImageView imageView;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
