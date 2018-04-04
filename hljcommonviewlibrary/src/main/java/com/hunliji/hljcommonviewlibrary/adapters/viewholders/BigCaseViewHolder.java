package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerCaseViewHolder;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangtao on 2016/12/9.
 */

public class BigCaseViewHolder extends TrackerCaseViewHolder {

    @BindView(R2.id.iv_cover)
    ImageView ivCover;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R2.id.tv_collect_count)
    TextView tvCollectCount;
    private int imgWidth;
    private OnItemClickListener<Work> onItemClickListener;

    public static View getView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext())
                .inflate(R.layout.big_commom_case_item___cv, parent, false);
    }

    public BigCaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        Point point = CommonUtil.getDeviceSize(itemView.getContext());
        imgWidth = point.x;
        ivCover.getLayoutParams().height = Math.round(imgWidth * 9 / 16);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    protected void setViewData(
            Context mContext, Work item, int position, int viewType) {

        tvTitle.setText(item.getTitle());
        Glide.with(ivCover.getContext())
                .load(ImageUtil.getImagePath(item.getCoverPath(), imgWidth))
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(ivCover);
        tvCollectCount.setText(mContext.getString(R.string.label_collect_count___cv,
                String.valueOf(item.getCollectorsCount())));
        tvMerchantName.setText(item.getMerchant()
                .getName());
    }

    public void setOnItemClickListener(OnItemClickListener<Work> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
