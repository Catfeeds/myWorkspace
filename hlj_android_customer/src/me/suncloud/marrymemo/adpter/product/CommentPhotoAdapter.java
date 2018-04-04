package me.suncloud.marrymemo.adpter.product;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;

/**
 * Created by wangtao on 2016/11/15.
 */

public class CommentPhotoAdapter extends BaseAdapter {

    private List<Photo> photos;
    private int pluralSize;

    public CommentPhotoAdapter(int pluralSize, List<Photo> photos) {
        this.photos = photos;
        this.pluralSize = pluralSize;
    }

    public void setPhotos(List<Photo> photos) {
        if(this.photos==null){
            this.photos=new ArrayList<>();
        }else{
            this.photos.clear();
        }
        if(photos!=null){
            this.photos.addAll(photos);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return photos == null ? 0 : photos.size();
    }

    @Override
    public Photo getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(
            int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.thread_photos_item, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.photo);
            imageView.getLayoutParams().width = pluralSize;
            imageView.getLayoutParams().height = pluralSize;
            convertView.setTag(imageView);
        }
        ImageView imageView = (ImageView) convertView.getTag();
        Photo photo = getItem(position);
        String url = ImageUtil.getImagePath2(photo.getImagePath(), pluralSize);
        Glide.with(parent.getContext())
                .load(url)
                .apply(new RequestOptions()
                        .centerCrop()
                .placeholder(com.hunliji.hljlivelibrary.R.mipmap.icon_empty_image))
                .into(imageView);
        return convertView;
    }
}
