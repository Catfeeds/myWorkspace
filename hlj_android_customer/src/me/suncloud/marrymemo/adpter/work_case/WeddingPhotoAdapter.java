package me.suncloud.marrymemo.adpter.work_case;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.work_case.viewholder.WeddingPhotoViewHolder;

/**
 * Created by hua_rong on 2017/7/31.
 * 婚礼摄影
 */

public class WeddingPhotoAdapter extends BaseMerchantServiceWorkRecyclerAdapter {


    public WeddingPhotoAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                return super.onCreateViewHolder(parent, viewType);
            case ITEM_TYPE_CPM:
                return super.onCreateViewHolder(parent, viewType);
            default:
                View itemView = inflater.inflate(R.layout.property_wedding_photo_item,
                        parent,
                        false);
                return new WeddingPhotoViewHolder(itemView);
        }
    }


}
