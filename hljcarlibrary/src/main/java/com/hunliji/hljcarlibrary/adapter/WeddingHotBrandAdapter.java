package com.hunliji.hljcarlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcarlibrary.adapter.viewholder.tracker.TrackerCarBrandViewHolder;
import com.hunliji.hljcarlibrary.models.Brand;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/12/28.热门品牌
 */

public class WeddingHotBrandAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<Brand> hotBrands;
    private Context mContext;
    private OnItemClickListener<Brand> onItemClickListener;

    public static final int ITEM = 1;

    public WeddingHotBrandAdapter(Context context) {
        this.mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener<Brand> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setHotBrands(List<Brand> hotBrands) {
        this.hotBrands = hotBrands;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM:
                return new BrandViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.hot_brand_list_item___car, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case ITEM:
                holder.setView(mContext, getItem(position), position, type);
                break;
        }
    }

    private Brand getItem(int position) {
        if (hotBrands != null && position < hotBrands.size()) {
            return hotBrands.get(position);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM;
    }

    @Override
    public int getItemCount() {
        return hotBrands == null ? 0 : hotBrands.size();
    }


    class BrandViewHolder extends TrackerCarBrandViewHolder {
        @BindView(R2.id.img_brand_logo)
        RoundedImageView imgBrandLogo;
        @BindView(R2.id.tv_brand_name)
        TextView tvBrandName;
        @BindView(R2.id.hot_brand_layout)
        RelativeLayout hotBrandLayout;
        private int brandWidth;
        private int space;

        BrandViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            brandWidth = CommonUtil.dp2px(itemView.getContext(), 48);
            float brandCount;
            int maxWidth = Math.round(brandWidth * 5.5f + CommonUtil.dp2px(view.getContext(), 16));
            if (CommonUtil.getDeviceSize(view.getContext()).x - maxWidth > 0) {
                brandCount = 5.5f;
            } else {
                brandCount = 4.5f;
            }
            space = Math.round((CommonUtil.getDeviceSize(view.getContext()).x - brandWidth *
                    brandCount - CommonUtil.dp2px(
                    view.getContext(),
                    16)) / ((brandCount - 0.5f) * 2));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                    }
                }
            });
        }

        @Override
        public View trackerView() {
            return itemView;
        }

        @Override
        protected void setViewData(Context mContext, Brand item, int position, int viewType) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams)
                    hotBrandLayout.getLayoutParams();
            if (position == 0) {
                layoutParams.leftMargin = CommonUtil.dp2px(mContext, 10);
                layoutParams.rightMargin = space;
            } else if (position == hotBrands.size() - 1) {
                layoutParams.rightMargin = CommonUtil.dp2px(mContext, 10);
                layoutParams.leftMargin = space;
            } else {
                layoutParams.rightMargin = space;
                layoutParams.leftMargin = space;
            }
            Glide.with(imgBrandLogo.getContext())
                    .load(ImagePath.buildPath(item.getLogo())
                            .width(brandWidth)
                            .height(brandWidth)
                            .path())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(imgBrandLogo);
            tvBrandName.setText(item.getTitle());
        }
    }
}
