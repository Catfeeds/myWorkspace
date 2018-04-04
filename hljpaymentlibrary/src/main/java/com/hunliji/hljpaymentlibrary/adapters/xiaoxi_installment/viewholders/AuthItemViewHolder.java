package com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;


import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.AuthItem;

/**
 * 授信列表viewHolder
 * Created by chen_bin on 2017/8/10 0010.
 */
public class AuthItemViewHolder extends BaseViewHolder<AuthItem> {
    @BindView(R2.id.img_logo)
    RoundedImageView imgLogo;
    @BindView(R2.id.tv_name)
    TextView tvName;
    @BindView(R2.id.tv_status)
    TextView tvStatus;
    @BindView(R2.id.line_layout)
    View lineLayout;
    private OnItemClickListener onItemClickListener;

    public AuthItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
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
    public void setViewData(Context mContext, AuthItem authItem, int position, int viewType) {
        if (authItem == null) {
            return;
        }
        imgLogo.setImageResource(authItem.getAuthItemLogo());
        tvName.setText(authItem.getName());
        if (authItem.getStatus() == AuthItem.STATUS_AUTHORIZED) {
            itemView.setClickable(false);
            tvStatus.setText(R.string.label_authorized___pay);
            tvStatus.setTextColor(Color.parseColor("#02ca5b"));
            tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_check_green_28_20,
                    0,
                    0,
                    0);
        } else {
            itemView.setClickable(true);
            tvStatus.setText(R.string.label_unauthorized___pay);
            tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorLink));
            tvStatus.setCompoundDrawablesWithIntrinsicBounds(0,
                    0,
                    R.mipmap.icon_arrow_right_gray_14_26,
                    0);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setShowBottomLineView(boolean showBottomLineView) {
        lineLayout.setVisibility(showBottomLineView ? View.VISIBLE : View.GONE);
    }

}
