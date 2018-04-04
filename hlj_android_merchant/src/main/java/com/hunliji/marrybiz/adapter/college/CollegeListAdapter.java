package com.hunliji.marrybiz.adapter.college;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.college.CollegeItem;
import com.hunliji.marrybiz.util.JSONUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by luohanlin on 2017/11/22.
 */

public class CollegeListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private List<CollegeItem> preList;
    private List<CollegeItem> itemList;
    private OnItemClickListener onItemClickListener;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_FOOTER = 2;
    private View footerView;
    private int imgWidth;
    private int imgHeight;

    public CollegeListAdapter(
            Context context, List<CollegeItem> preList, List<CollegeItem> itemList) {
        this.context = context;
        this.preList = preList;
        this.itemList = itemList;
        imgWidth = CommonUtil.dp2px(context, 107);
        imgHeight = CommonUtil.dp2px(context, 60);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                return new CollegeItemViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.college_list_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof CollegeItemViewHolder) {
            List<CollegeItem> items = new ArrayList<>();
            if (getPreCount() > 0) {
                items.addAll(preList);
            }
            items.addAll(itemList);
            holder.setView(context, items.get(position), position, getItemViewType(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1 && footerView != null) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public int getPreCount() {
        return preList == null ? 0 : preList.size();
    }

    @Override
    public int getItemCount() {
        return getPreCount() + (itemList == null ? 0 : itemList.size()) + (footerView == null ? 0
                : 1);
    }

    class CollegeItemViewHolder extends BaseViewHolder<CollegeItem> {
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.tv_stick)
        TextView tvStick;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_sub_title)
        TextView tvSubTitle;
        @BindView(R.id.section_divider)
        View sectionDivider;
        @BindView(R.id.bottom_divider)
        View bottomDivider;

        CollegeItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CollegeItem item = getItem();
                    if (item == null) {
                        return;
                    }
                    Intent intent = new Intent(context, HljWebViewActivity.class);
                    //                    intent.putExtra("title", item.getTypeTitle());
                    intent.putExtra("path", JSONUtil.getWebPath(item.getUrl()));
                    context.startActivity(intent);
                }
            });
        }

        @Override
        protected void setViewData(Context mContext, CollegeItem item, int position, int viewType) {
            if (getPreCount() > 0 && position == getPreCount() - 1) {
                sectionDivider.setVisibility(View.VISIBLE);
                bottomDivider.setVisibility(View.GONE);
            } else {
                sectionDivider.setVisibility(View.GONE);
                if (position == getItemCount() - 1 - (footerView == null ? 0 : 1)) {
                    bottomDivider.setVisibility(View.GONE);
                } else {
                    bottomDivider.setVisibility(View.VISIBLE);
                }
            }
            Glide.with(context)
                    .load(ImagePath.buildPath(item.getImgPath())
                            .width(imgWidth)
                            .height(imgHeight)
                            .path())
                    .into(imgCover);
            tvStick.setVisibility(item.isStick() ? View.VISIBLE : View.GONE);
            tvTitle.setText(item.getTitle());
            tvSubTitle.setText(item.getSubTitle());
        }
    }
}
