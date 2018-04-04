package com.hunliji.marrybiz.adapter.experience.viewholder;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Space;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.experience.AdvDetail;
import com.hunliji.marrybiz.model.experience.ShowHistory;
import com.hunliji.marrybiz.view.experience.AdvListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jinxin on 2017/12/19 0019.
 */

public class AdvOrderDetailItemViewHolder extends BaseViewHolder<ShowHistory> {

    @BindView(R.id.line_top)
    View lineTop;
    @BindView(R.id.line_bottom)
    View lineBottom;
    @BindView(R.id.divider)
    View divider;
    @BindView(R.id.divider_top)
    Space dividerTop;
    @BindView(R.id.dot_top)
    View dotTop;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_remark)
    TextView tvRemark;

    private Context mContext;
    private int arriveColor;
    private int advType;

    public AdvOrderDetailItemViewHolder(View itemView, int type) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        arriveColor = Color.parseColor("#02ca5b");
        mContext = itemView.getContext();
        this.advType = type;
    }

    @Override
    protected void setViewData(
            Context mContext, ShowHistory showHistory, int position, int size) {
        if (showHistory == null) {
            return;
        }
        lineBottom.setVisibility(position == size - 1 ? View.GONE : View.VISIBLE);
        lineTop.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
        divider.setVisibility(position == size - 1 ? View.VISIBLE : View.GONE);
        dotTop.setBackgroundResource(position == 0 ? R.drawable.sp_oval_primary : R.drawable
                .sp_oval_gray2);
        tvTime.setText(showHistory.getCreateAt() == null ? null : showHistory.getCreateAt()
                .toString(Constants.DATE_FORMAT_LONG));
        tvRemark.setText(showHistory.getMessage());
        tvRemark.setVisibility(TextUtils.isEmpty(showHistory.getMessage()) ? View.GONE : View
                .VISIBLE);

        if (showHistory.isCome()) {
            tvStatus.setText("已到店");
            tvStatus.setBackgroundResource(R.drawable.sp_r2_stroke_02ca5b);
            tvStatus.setTextColor(arriveColor);
        } else {
            tvStatus.setText(AdvDetail.getStatusStringByType(showHistory.getStatus(), advType));
            tvStatus.setBackgroundResource(getStatusBg(showHistory.getStatus()));
            tvStatus.setTextColor(getStatusColor(showHistory.getStatus()));
        }
    }

    private int getStatusBg(int status) {
        if (advType == AdvListActivity.ADV_FOR_EXPERIENCE) {
            switch (status) {
                case AdvDetail.ORDER_UN_SEND:
                    return R.drawable.sp_r2_stroke_accent;
                case AdvDetail.ORDER_UN_READ:
                    return R.drawable.sp_r2_stroke_primary;
                case AdvDetail.ORDER_HAVE_EXPIRED:
                    return R.drawable.sp_r2_stroke_gray;
                case AdvDetail.ORDER_COME_SHOP:
                    return R.drawable.sp_r2_stroke_02ca5b;
                case AdvDetail.ORDER_FOLLOW_UP_FAILED:
                    return R.drawable.sp_r2_stroke_gray;
                case AdvDetail.ORDER_HAVE_CREATE:
                    return R.drawable.sp_r2_stroke_primary;
                case AdvDetail.ORDER_HAVE_REFUND:
                    return R.drawable.sp_r2_stroke_gray;
                default:
                    return R.drawable.sp_r2_stroke_gray;
            }
        } else {
            return R.drawable.sp_r2_stroke_primary;
        }
    }

    private int getStatusColor(int status) {
        if (advType == AdvListActivity.ADV_FOR_EXPERIENCE) {
            switch (status) {
                case AdvDetail.ORDER_UN_SEND:
                    return mContext.getResources()
                            .getColor(R.color.colorAccent);
                case AdvDetail.ORDER_HAVE_READ:
                    return mContext.getResources()
                            .getColor(R.color.colorPrimary);
                case AdvDetail.ORDER_HAVE_EXPIRED:
                    return mContext.getResources()
                            .getColor(R.color.colorGray);
                case AdvDetail.ORDER_COME_SHOP:
                    return arriveColor;
                case AdvDetail.ORDER_FOLLOW_UP_FAILED:
                    return mContext.getResources()
                            .getColor(R.color.colorGray);
                case AdvDetail.ORDER_HAVE_CREATE:
                    return mContext.getResources()
                            .getColor(R.color.colorPrimary);
                case AdvDetail.ORDER_HAVE_REFUND:
                    return mContext.getResources()
                            .getColor(R.color.colorGray);
                default:
                    return mContext.getResources()
                            .getColor(R.color.colorGray);
            }
        } else {
            return mContext.getResources()
                    .getColor(R.color.colorPrimary);
        }
    }

}
