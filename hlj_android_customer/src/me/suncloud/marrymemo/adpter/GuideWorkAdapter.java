package me.suncloud.marrymemo.adpter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.BigWorkViewHolder;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.view.WorkActivity;


/**
 * 旅拍全部套餐
 * Created by jinxin on 2016/9/26.
 */

public class GuideWorkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int FOOTER_TYPE = 4;
    public static final int ITEM = 3;
    private Context mContext;
    private ArrayList<Work> works;
    private View footView;


    public GuideWorkAdapter(
            Context mContext, ArrayList<Work> works) {
        this.mContext = mContext;
        this.works = works;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        if (viewType == ITEM) {
            View itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.big_commom_work_item__cv, parent, false);
            BigWorkViewHolder holder = new BigWorkViewHolder(itemView);
            return holder;
        } else if (viewType == FOOTER_TYPE) {
            footView = LayoutInflater.from(mContext)
                    .inflate(R.layout.hlj_foot_no_more___cm, parent, false);
            footView.findViewById(R.id.no_more_hint)
                    .setVisibility(View.VISIBLE);
            footView.setBackgroundResource(R.color.colorBackground);
            return new ExtraBaseViewHolder(footView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == ITEM) {
            BigWorkViewHolder workViewHolder = (BigWorkViewHolder) holder;
            workViewHolder.setShowBottomLineView(position < works.size() - 1);
            workViewHolder.setView(mContext, works.get(position), position, type);
            workViewHolder.setOnItemClickListener(new com.hunliji.hljcommonlibrary.adapters
                    .OnItemClickListener() {
                @Override
                public void onItemClick(int position, Object object) {
                    if (object != null) {
                        Work work = (Work) object;
                        String link = work.getLink();
                        if (JSONUtil.isEmpty(link)) {
                            Intent intent = new Intent(mContext, WorkActivity.class);
                            intent.putExtra("id", work.getId());
                            mContext.startActivity(intent);
                            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        } else {
                            HljWeb.startWebView((Activity) mContext, link);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return works.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        int type = -1;
        if (position == getItemCount() - 1) {
            type = FOOTER_TYPE;
        } else {
            type = ITEM;
        }
        return type;
    }

}
