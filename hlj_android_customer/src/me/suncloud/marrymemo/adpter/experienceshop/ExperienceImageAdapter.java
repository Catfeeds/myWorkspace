package me.suncloud.marrymemo.adpter.experienceshop;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.viewholder.experienceshop.ExperienceImageViewHolder;


/**
 * Created by hua_rong on 2017/3/31.
 * 体验店全部照片
 */

public class ExperienceImageAdapter extends RecyclerView.Adapter<BaseViewHolder<Photo>> {
    private Context context;
    private ArrayList<Photo> photos;
    private LayoutInflater inflater;

    public ExperienceImageAdapter(Context context, ArrayList<Photo> photos) {
        this.context = context;
        this.photos = photos;
        this.inflater = LayoutInflater.from(context);
    }

    public void setItems(List<Photo> items) {
        this.photos.clear();
        if (!CommonUtil.isCollectionEmpty(items)) {
            this.photos.addAll(items);
        }
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder<Photo> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExperienceImageViewHolder(inflater.inflate(R.layout.image_item, parent, false),
                photos);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<Photo> holder, int position) {
        holder.setView(context, photos.get(position), position, getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

}
