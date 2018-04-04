package me.suncloud.marrymemo.adpter.work_case;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.WeddingMakeupWorkViewHolder;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.WorkActivity;

/**
 * 婚礼跟妆套餐adapter
 * Created by chen_bin on 2017/8/1 0001.
 */
public class WeddingMakeupWorkRecyclerAdapter extends BaseMerchantServiceWorkRecyclerAdapter {

    public WeddingMakeupWorkRecyclerAdapter(Context context) {
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
                WeddingMakeupWorkViewHolder workViewHolder = new WeddingMakeupWorkViewHolder(
                        inflater.inflate(R.layout.wedding_makeup_work_list_item___cv,
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
