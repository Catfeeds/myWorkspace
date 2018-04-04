package com.hunliji.marrybiz.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.wrapper.RecommendWork;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.WorkActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.width;

/**
 * Created by mo_yu on 2017/2/4.推荐橱窗
 */

public class RecommendWorkAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private View footerView;
    private Context context;
    private ArrayList<RecommendWork> recommendWorks;
    private OnCancelClickListener onCancelClickListener;
    private OnAddClickListener onAddClickListener;

    public RecommendWorkAdapter(Context context, ArrayList<RecommendWork> list) {
        this.context = context;
        this.recommendWorks = list;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
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
    public BaseViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                return new RecommendWorkViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.recommend_work_item, parent, false));
            case TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_ITEM:
                holder.setView(context, recommendWorks.get(position), position, viewType);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return recommendWorks.size() + (footerView == null ? 0 : 1);
    }

    public class RecommendWorkViewHolder extends BaseViewHolder<RecommendWork> {
        @BindView(R.id.iv_cover)
        ImageView ivCover;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.btn_delete)
        ImageButton btnDelete;
        @BindView(R.id.tv_seat)
        TextView tvSeat;
        @BindView(R.id.recommend_work_view)
        View recommendWorkView;
        @BindView(R.id.work_item_view)
        View workItemView;
        @BindView(R.id.tv_add_work)
        TextView tvAddWork;

        public RecommendWorkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void setViewData(
                final Context mContext,
                final RecommendWork item,
                final int position,
                int viewType) {
            final Activity activity = (Activity) mContext;
            tvSeat.setText(String.valueOf(position + 1));
            if (item.getWork() != null) {
                workItemView.setVisibility(View.VISIBLE);
                tvAddWork.setVisibility(View.GONE);
                final Work work = item.getWork();
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
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onCancelClickListener != null) {
                            onCancelClickListener.onCancel(item.getId());
                        }
                    }
                });
            } else {
                workItemView.setVisibility(View.GONE);
                tvAddWork.setVisibility(View.VISIBLE);
                tvAddWork.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onAddClickListener != null) {
                            onAddClickListener.onAdd(position + 1);
                        }
                    }
                });
            }
        }
    }

    public interface OnCancelClickListener {
        void onCancel(long id);
    }

    public interface OnAddClickListener {
        void onAdd(int seat);
    }

    public void setOnAddClickListener(OnAddClickListener onAddClickListener) {
        this.onAddClickListener = onAddClickListener;
    }

    public void setOnCancelClickListener(OnCancelClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
    }
}
