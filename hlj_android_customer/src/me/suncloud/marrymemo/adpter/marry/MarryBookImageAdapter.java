package me.suncloud.marrymemo.adpter.marry;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Photo;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.marry.viewholder.BookImageViewHolder;

/**
 * Created by hua_rong on 2017/12/8
 */

public class MarryBookImageAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private List<Photo> photos;
    private LayoutInflater inflater;

    public MarryBookImageAdapter(Context context, List<Photo> photos) {
        this.context = context;
        this.photos = photos;
        this.inflater = LayoutInflater.from(context);
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.image_item___cm, parent, false);
        return new BookImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        BookImageViewHolder viewHolder = (BookImageViewHolder) holder;
        viewHolder.setPhotos(photos);
        viewHolder.setView(context, photos.get(position), position, getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return photos == null ? 0 : photos.size();
    }


}
