package me.suncloud.marrymemo.adpter.merchant;

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
import me.suncloud.marrymemo.adpter.merchant.viewholder.HotelHallImageViewHolder;

/**
 * 宴会厅图片adapter
 * Created by chen_bin on 2017/10/18 0018.
 */
public class HotelHallImageListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private List<Photo> photos;
    private LayoutInflater inflater;

    public HotelHallImageListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return CommonUtil.getCollectionSize(photos);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HotelHallImageViewHolder(inflater.inflate(R.layout.image_item___cm,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setView(context, photos.get(position), position, getItemViewType(position));
    }

}
