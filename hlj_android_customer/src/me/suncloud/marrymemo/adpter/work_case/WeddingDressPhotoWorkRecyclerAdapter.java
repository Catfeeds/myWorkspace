package me.suncloud.marrymemo.adpter.work_case;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.WeddingDressPhotoWorkViewHolder;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.WorkActivity;

/**
 * 婚纱摄影套餐adapter
 * Created by chen_bin on 2017/8/1 0001.
 */
public class WeddingDressPhotoWorkRecyclerAdapter extends BaseMerchantServiceWorkRecyclerAdapter {

    public WeddingDressPhotoWorkRecyclerAdapter(Context context) {
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
                WeddingDressPhotoWorkViewHolder workViewHolder = new
                        WeddingDressPhotoWorkViewHolder(
                        inflater.inflate(R.layout.wedding_dress_photo_work_list_item___cv,
                                parent,
                                false));
                workViewHolder.setOnItemClickListener(new OnItemClickListener<Work>() {
                    @Override
                    public void onItemClick(int position, Work work) {
                        if (work != null && work.getId() > 0) {
                            Intent intent = new Intent(context, WorkActivity.class);
                            intent.putExtra("id", work.getId());
                            context.startActivity(intent);
                        }
                    }
                });
                return workViewHolder;
        }
    }
}
