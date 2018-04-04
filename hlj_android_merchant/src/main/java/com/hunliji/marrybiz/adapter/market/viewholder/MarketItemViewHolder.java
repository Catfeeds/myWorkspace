package com.hunliji.marrybiz.adapter.market.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.merchantservice.MarketItem;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hua_rong on 2017/12/25 营销item
 */

public class MarketItemViewHolder extends BaseViewHolder<MarketItem>{

    @BindView(R.id.iv_logo)
    RoundedImageView ivLogo;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private int imageWidth;
    private int imageHeight;
    private OnItemClickListener onItemClickListener;


    public MarketItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        Context context = itemView.getContext();
        imageWidth = CommonUtil.dp2px(context, 40);
        imageHeight = CommonUtil.dp2px(context, 40);
        ivLogo.getLayoutParams().width = imageWidth;
        ivLogo.getLayoutParams().height = imageHeight;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                }
            }
        });
    }

    @Override
    protected void setViewData(
            Context context, MarketItem marketItem, int position, int viewType) {
        if (marketItem.getLogo() > 0) {
            ivLogo.setImageResource(marketItem.getLogo());
        }
        tvTitle.setText(marketItem.getTitle());
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
