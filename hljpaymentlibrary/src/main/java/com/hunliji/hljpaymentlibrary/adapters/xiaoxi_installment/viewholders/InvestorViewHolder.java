package com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.Investor;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment
        .XiaoxiInstallmentAgreementActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 出借人列表viewHolder
 * Created by chen_bin on 2017/12/13 0013.
 */
public class InvestorViewHolder extends BaseViewHolder<Investor> {

    @BindView(R2.id.top_line_layout)
    View topLineLayout;
    @BindView(R2.id.tv_name)
    TextView tvName;
    @BindView(R2.id.tv_amount)
    TextView tvAmount;
    @BindView(R2.id.tv_see)
    TextView tvSee;

    private String assetOrderId;

    public InvestorViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        tvSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Investor investor = getItem();
                if (investor != null) {
                    Intent intent = new Intent(v.getContext(),
                            XiaoxiInstallmentAgreementActivity.class);
                    intent.putExtra(XiaoxiInstallmentAgreementActivity.ARG_ASSET_ORDER_ID,
                            assetOrderId);
                    intent.putExtra(XiaoxiInstallmentAgreementActivity.ARG_TYPE,
                            XiaoxiInstallmentAgreementActivity.TYPE_LOAN);
                    intent.putExtra(XiaoxiInstallmentAgreementActivity.ARG_INVESTOR_ID,
                            investor.getInvestorId());
                    v.getContext()
                            .startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void setViewData(Context mContext, Investor investor, int position, int viewType) {
        if (investor == null) {
            return;
        }
        tvName.setText(investor.getInvestorName());
        tvAmount.setText(CommonUtil.formatDouble2StringWithTwoFloat(investor.getInvestorAmount()));
    }

    public void setAssetOrderId(String assetOrderId) {
        this.assetOrderId = assetOrderId;
    }

    public void setShowTopLineView(boolean showTopLineView) {
        topLineLayout.setVisibility(showTopLineView ? View.VISIBLE : View.GONE);
    }

}
