package me.suncloud.marrymemo.adpter.work_case;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.BigWorkViewHolder;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.WeddingDressPhotoWorkViewHolder;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.view.WorkActivity;

/**
 * 纯套餐列表adapter
 * Created by jinxin on 2017/12/11 0011.
 */

public class ChildWorkListAdapter extends BaseMerchantServiceWorkRecyclerAdapter implements
        OnItemClickListener<Work> {

    public ChildWorkListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        if(viewType == ITEM_TYPE_LIST){
            View itemView = inflater.inflate(R.layout.wedding_dress_photo_work_list_item___cv, parent, false);
            WeddingDressPhotoWorkViewHolder holder = new WeddingDressPhotoWorkViewHolder(itemView);
            holder.setOnItemClickListener(this);
            return holder;
        }else{
            return super.onCreateViewHolder(parent,viewType);
        }
    }

    @Override
    public void onItemClick(int position, Work work) {
        if (work == null) {
            return;
        }
        String link = work.getLink();
        if (JSONUtil.isEmpty(link)) {
            Intent intent = new Intent(context, WorkActivity.class);
            intent.putExtra("id", work.getId());
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        } else {
            HljWeb.startWebView((Activity) context, link);
        }
    }
}
