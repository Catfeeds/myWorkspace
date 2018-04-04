package me.suncloud.marrymemo.adpter.work_case;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.WeddingDressWorkViewHolder;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.WorkActivity;

/**
 * Created by mo_yu on 2017/8/3.婚纱礼服
 */

public class WeddingDressWorkRecyclerAdapter extends BaseMerchantServiceWorkRecyclerAdapter {

    public WeddingDressWorkRecyclerAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                ExtraBaseViewHolder footerViewHolder = new ExtraBaseViewHolder(footerView);
                footerViewHolder.itemView.setLayoutParams(new StaggeredGridLayoutManager
                        .LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                return footerViewHolder;
            case ITEM_TYPE_CPM:
                ExtraBaseViewHolder cpmViewHolder = new ExtraBaseViewHolder(cpmView);
                cpmViewHolder.itemView.setLayoutParams(new StaggeredGridLayoutManager.LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                return cpmViewHolder;
            default:
                WeddingDressWorkViewHolder workViewHolder = new WeddingDressWorkViewHolder
                        (inflater.inflate(
                        R.layout.wedding_dress_work_item___cv,
                        parent,
                        false));
                workViewHolder.setOnItemClickListener(new OnItemClickListener<Work>() {
                    @Override
                    public void onItemClick(int position, Work work) {
                        Intent intent = new Intent(context, WorkActivity.class);
                        intent.putExtra("id", work.getId());
                        context.startActivity(intent);
                    }
                });
                return workViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                holder.setView(context, works.get(position), position, viewType);
                break;
        }
        if (viewType != ITEM_TYPE_LIST) {
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            if (params != null && params instanceof StaggeredGridLayoutManager.LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams) params).setFullSpan(true);
            }
        }
    }

}
