package me.suncloud.marrymemo.adpter.merchant;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.merchant.viewholder.MerchantShopImageViewHolder;

/**
 * 商家店铺照片Adapter
 * Created by chen_bin on 2017/5/22 0022.
 */
public class MerchantShopImageRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View headerView;
    private List<Photo> photos;
    private LayoutInflater inflater;
    private final static int ITEM_TYPE_HEADER = 0;
    private final static int ITEM_TYPE_LIST = 1;

    public MerchantShopImageRecyclerAdapter(Context context, List<Photo> photos) {
        this.context = context;
        this.photos = photos;
        this.inflater = LayoutInflater.from(context);
    }

    public void setPhotos(List<Photo> photos) {
        if (this.photos != photos) {
            this.photos = photos;
            notifyDataSetChanged();
        }
    }

    public int getHeaderViewCount() {
        return headerView != null ? 1 : 0;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    @Override
    public int getItemCount() {
        return getHeaderViewCount() + CommonUtil.getCollectionSize(photos);
    }

    @Override
    public int getItemViewType(int position) {
        if (getHeaderViewCount() > 0 && position == 0) {
            return ITEM_TYPE_HEADER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            default:
                MerchantShopImageViewHolder holder = new MerchantShopImageViewHolder(inflater
                        .inflate(
                        R.layout.merchant_shop_image_item,
                        parent,
                        false));
                holder.setOnItemClickListener(new OnItemClickListener<Photo>() {
                    @Override
                    public void onItemClick(int position, Photo photo) {
                        Intent intent = new Intent(context, PicsPageViewActivity.class);
                        intent.putExtra("photos", new ArrayList<>(photos));
                        intent.putExtra("position", photos.indexOf(photo));
                        context.startActivity(intent);
                    }
                });
                return holder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                int index = position - getHeaderViewCount();
                holder.setView(context, photos.get(index), index, 0);
                break;
        }
    }
}
