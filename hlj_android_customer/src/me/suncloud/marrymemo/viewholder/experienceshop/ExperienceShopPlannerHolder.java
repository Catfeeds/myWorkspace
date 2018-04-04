package me.suncloud.marrymemo.viewholder.experienceshop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.experience.Planner;
import me.suncloud.marrymemo.model.experience.Store;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.experience.ExperienceShopReservationActivity;
import me.suncloud.marrymemo.view.experience.PlannerDetailActivity;

/**
 * experience_shop_item_planners
 * Created by jinxin on 2017/3/24 0024.
 */

public class ExperienceShopPlannerHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.grid_view)
    public GridLayout gridView;

    private Context mContext;
    private int itemWidth;
    private Store store;

    public ExperienceShopPlannerHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        itemWidth = Math.round((CommonUtil.getDeviceSize(mContext).x - Util.dp2px(mContext,
                24)) / 3);
    }

    public void setPlanner(List<Planner> planners, Store store) {
        if (planners == null || planners.isEmpty()) {
            return;
        }
        this.store = store;
        int childCount = gridView.getChildCount();
        int size = planners.size();
        size = size > childCount ? size : childCount;
        if (childCount > size) {
            gridView.removeViews(size, childCount - size);
        }

        for (int i = 0; i < size; i++) {
            View childView = null;
            if (i < childCount) {
                childView = gridView.getChildAt(i);
            }
            if (childView == null) {
                childView = LayoutInflater.from(mContext)
                        .inflate(R.layout.experience_shop_planner_item, gridView, false);
                childView.getLayoutParams().width = itemWidth;
                gridView.addView(childView);
            }
            PlannerViewHolder holder = new PlannerViewHolder(childView);
            Planner planner = planners.get(i);
            setPlannerView(holder, planner);
        }
    }

    private void setPlannerView(PlannerViewHolder holder, final Planner planner) {
        if (planner.getId() != -1) {
            String path = ImageUtil.getImagePath(planner.getHeadImg(), Util.dp2px(mContext, 50));
            if (!TextUtils.isEmpty(path)) {
                Glide.with(mContext)
                        .load(path)
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_avatar_primary)
                                .dontAnimate())
                        .into(holder.imgAvatar);
            } else {
                Glide.with(mContext)
                        .clear(holder.imgAvatar);
            }
            holder.tvPriseCount.setText(String.valueOf(planner.getLikesCount()));
            holder.tvPriseDes.setVisibility(View.VISIBLE);
        } else {
            holder.imgAvatar.setImageResource(R.mipmap.icon_planner_to_shop);
            holder.tvPriseCount.setText(planner.getIntroduce());
            holder.tvPriseDes.setVisibility(View.GONE);
        }
        switch (planner.getTitle()) {
            case 0:
                break;
            case 1:
                holder.imgType.setImageResource(R.mipmap.icon_planner_type1);
                break;
            case 2:
                holder.imgType.setImageResource(R.mipmap.icon_planner_type2);
                break;
            case 3:
                holder.imgType.setImageResource(R.mipmap.icon_planner_type3);
                break;
            default:
                break;
        }

        holder.tvName.setText(planner.getFullName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = planner.getId();
                if (id != -1) {
                    Intent intent = new Intent(mContext, PlannerDetailActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("store", store);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                } else {
                    if (Util.loginBindChecked((Activity) mContext, Constants.Login.SUBMIT_LOGIN)) {
                        Intent intent = new Intent(mContext,
                                ExperienceShopReservationActivity.class);
                        intent.putExtra("store", store);
                        mContext.startActivity(intent);
                        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            }
        });
    }

    class PlannerViewHolder {
        @BindView(R.id.img_avatar)
        RoundedImageView imgAvatar;
        @BindView(R.id.img_type)
        ImageView imgType;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_prise_count)
        TextView tvPriseCount;
        @BindView(R.id.tv_prise_des)
        TextView tvPriseDes;

        View itemView;

        PlannerViewHolder(View view) {
            ButterKnife.bind(this, view);
            this.itemView = view;
        }
    }

}
