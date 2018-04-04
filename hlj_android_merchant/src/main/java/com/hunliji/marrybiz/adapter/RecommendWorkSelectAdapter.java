package com.hunliji.marrybiz.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.WorkActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.width;

/**
 * Created by mo_yu on 2017/2/4.推荐套餐选择
 */

public class RecommendWorkSelectAdapter extends RecyclerView.Adapter<com.hunliji.hljcommonlibrary
        .adapters.BaseViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private View footerView;
    private Context context;
    private List<Work> works;
    private long checkedId;

    public RecommendWorkSelectAdapter(Context context, List<Work> list) {
        this.context = context;
        this.works = list;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public long getCheckedId() {
        return checkedId;
    }

    public void setCheckedId(long checkedId) {
        this.checkedId = checkedId;
    }

    @Override
    public int getItemViewType(int position) {
        if (footerView != null && position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public com.hunliji.hljcommonlibrary.adapters.BaseViewHolder onCreateViewHolder(
            ViewGroup parent,
            int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                WorkSelectViewHolder holder = new WorkSelectViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.recommend_work_select_item, parent, false));
                return holder;
            case TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(
            com.hunliji.hljcommonlibrary.adapters.BaseViewHolder holder,
            int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_ITEM:
                holder.setView(context, works.get(position), position, viewType);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return works.size() + (footerView == null ? 0 : 1);
    }

    public class WorkSelectViewHolder extends com.hunliji.hljcommonlibrary.adapters
            .BaseViewHolder<Work> {
        @BindView(R.id.checked_view)
        ImageView checkedView;
        @BindView(R.id.iv_cover)
        ImageView ivCover;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.work_item_view)
        RelativeLayout workItemView;
        @BindView(R.id.recommend_work_view)
        RelativeLayout recommendWorkView;

        public WorkSelectViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void setViewData(
                final Context mContext, final Work work, final int position, int viewType) {
            final Activity activity = (Activity) mContext;
            workItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, WorkActivity.class);
                    intent.putExtra("w_id", work.getId());
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            });
            String path = ImageUtil.getImagePath(work.getCoverPath(), width);
            Glide.with(activity)
                    .load(path)
                    .into(ivCover);
            tvTitle.setText(work.getTitle());
            tvPrice.setText(activity.getString(R.string.label_price5,
                    Util.formatDouble2String(work.getShowPrice())));
            checkedView.setImageResource(checkedId == work.getId() ? R.drawable
                    .icon_check_round_40_40_selected : R.drawable.icon_check_round_40_40_normal);
            checkedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkedId = work.getId();
                    notifyDataSetChanged();
                }
            });
        }
    }

}
