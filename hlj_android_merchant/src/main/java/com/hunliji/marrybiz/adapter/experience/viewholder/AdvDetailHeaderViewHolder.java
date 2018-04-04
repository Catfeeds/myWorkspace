package com.hunliji.marrybiz.adapter.experience.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.experience.AdvDetail;
import com.hunliji.marrybiz.model.experience.AdvOrder;
import com.hunliji.marrybiz.model.experience.Lead;
import com.hunliji.marrybiz.view.experience.AdvListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jinxin on 2017/12/19 0019.
 */

public class AdvDetailHeaderViewHolder extends BaseViewHolder<AdvDetail> {

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.img_call)
    ImageView imgCall;
    @BindView(R.id.img_sms)
    ImageView imgSms;
    @BindView(R.id.tv_we_chat)
    TextView tvWeChat;
    @BindView(R.id.tv_qq)
    TextView tvQq;
    @BindView(R.id.tv_remark)
    TextView tvRemark;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_sender)
    TextView tvSender;
    @BindView(R.id.tv_seller)
    TextView tvSeller;
    @BindView(R.id.img_arrive_shop)
    ImageView imgArriveShop;
    @BindView(R.id.layout_qq)
    LinearLayout layoutQq;
    @BindView(R.id.layout_we_chat)
    LinearLayout layoutWeChat;
    @BindView(R.id.line_chat)
    View lineChat;
    @BindView(R.id.remark_layout)
    View remarkLayout;
    @BindView(R.id.remarks_layout)
    View remarksLayout;
    @BindView(R.id.tv_wedding_date)
    TextView tvWeddingDate;
    @BindView(R.id.tv_budget)
    TextView tvBudget;
    @BindView(R.id.tv_likes)
    TextView tvLikes;
    @BindView(R.id.order_no_layout)
    LinearLayout orderNoLayout;
    @BindView(R.id.admin_layout)
    LinearLayout adminLayout;
    @BindView(R.id.tv_seller_label)
    TextView tvSellerLabel;
    @BindView(R.id.date_remark_layout)
    LinearLayout dateRemarkLayout;
    @BindView(R.id.budget_remark_layout)
    LinearLayout budgetRemarkLayout;
    @BindView(R.id.favor_remark_layout)
    LinearLayout favorRemarkLayout;


    private OnAdvDetailHeaderClickListener onAdvDetailHeaderClickListener;
    private AdvDetail shopDetail;
    private int advType;

    public AdvDetailHeaderViewHolder(View itemView, int type) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.advType = type;
    }

    public void setOnAdvDetailHeaderClickListener(
            OnAdvDetailHeaderClickListener onAdvDetailHeaderClickListener) {
        this.onAdvDetailHeaderClickListener = onAdvDetailHeaderClickListener;
    }

    @Override
    protected void setViewData(
            Context mContext, AdvDetail shopDetail, int position, int viewType) {
        this.shopDetail = shopDetail;
        if (shopDetail == null) {
            return;
        }
        Lead lead = shopDetail.getLead();
        int imgSmsRes = (lead != null && lead.getUserId() > 0) ? R.drawable
                .icon_chat_bubble_black_46_44 : R.drawable.icon_chat_bubble_gray_46_44;
        imgSms.setImageResource(imgSmsRes);
        String phone1 = lead == null ? null : lead.getPhone();
        String phone2 = (lead == null || lead.getPartnerUserInfo() == null) ? null : lead
                .getPartnerUserInfo()
                .getPhone();
        String phone = phone1 + (CommonUtil.isEmpty(phone2) ? "" : " | " + phone2);
        int imgCallRes = TextUtils.isEmpty(phone) ? R.drawable.icon_call_gray_38_44 : R.drawable
                .icon_call_black_38_44;
        imgCall.setImageResource(imgCallRes);
        if (advType == AdvListActivity.ADV_FOR_OTHERS && !CommonUtil.isEmpty(shopDetail.getLead()
                .getNickName())) {
            tvName.setText(shopDetail.getLead()
                    .getNickName());
        } else {
            tvName.setText(shopDetail.getLead()
                    .getName());
        }
        tvPhone.setText(phone);
        String weChat = lead == null || lead.getPartnerUserInfo() == null ? null : lead
                .getPartnerUserInfo()
                .getWeixin();
        tvWeChat.setText(weChat);
        layoutWeChat.setVisibility(TextUtils.isEmpty(weChat) ? View.GONE : View.VISIBLE);
        String qq = lead == null || lead.getPartnerUserInfo() == null ? null : lead
                .getPartnerUserInfo()
                .getQq();
        tvQq.setText(qq);
        layoutQq.setVisibility(TextUtils.isEmpty(qq) ? View.GONE : View.VISIBLE);
        if (layoutWeChat.getVisibility() == View.VISIBLE || layoutQq.getVisibility() == View
                .VISIBLE) {
            lineChat.setVisibility(View.VISIBLE);
        } else {
            lineChat.setVisibility(View.GONE);
        }

        AdvOrder order = shopDetail.getOrder();
        tvNumber.setText(shopDetail.getOrderNo());
        tvTime.setText(shopDetail.getCreatedAt() == null ? null : shopDetail.getCreatedAt()
                .toString(Constants.DATE_FORMAT_LONG));
        tvSender.setText(shopDetail.getAdmin() == null ? null : shopDetail.getAdmin()
                .getNickname());
        tvSeller.setText(shopDetail.getExpSaler());
        imgArriveShop.setImageResource(shopDetail.isCome() ? R.mipmap.icon_switch_on_4cd964 : R
                .mipmap.icon_switch_off_4cd964);

        if (advType == AdvListActivity.ADV_FOR_OTHERS) {
            // 客资推荐单的视图
            remarkLayout.setVisibility(View.GONE);
            remarksLayout.setVisibility(View.VISIBLE);

            if (order.getDemand() != null) {
                if (order.getDemand()
                        .getWeddingDay() == null) {
                    tvWeddingDate.setText("未定");
                } else {
                    tvWeddingDate.setText(order.getDemand()
                            .getWeddingDay()
                            .toString("yyyy年MM月dd日"));
                }
                if (CommonUtil.isEmpty(order.getDemand()
                        .getUserHobby())) {
                    favorRemarkLayout.setVisibility(View.GONE);
                } else {
                    favorRemarkLayout.setVisibility(View.VISIBLE);
                    tvLikes.setText(order.getDemand()
                            .getUserHobby());
                }
                if (order.getDemand()
                        .getWeddingBudget() > 0) {
                    budgetRemarkLayout.setVisibility(View.VISIBLE);
                    tvBudget.setText(mContext.getString(R.string.label_money,
                            NumberFormatUtil.formatDouble2String(order.getDemand()
                                    .getWeddingBudget())));
                } else {
                    budgetRemarkLayout.setVisibility(View.GONE);
                }
            }
        } else {
            // 体验店推荐单的视图修改
            remarkLayout.setVisibility(View.VISIBLE);
            remarksLayout.setVisibility(View.GONE);

            tvRemark.setText(order == null ? null : order.getRemark());
        }
        if (advType == AdvListActivity.ADV_FOR_OTHERS) {
            orderNoLayout.setVisibility(View.GONE);
            adminLayout.setVisibility(View.GONE);
            tvSellerLabel.setText("对接人：");
        } else {
            orderNoLayout.setVisibility(View.VISIBLE);
            adminLayout.setVisibility(View.VISIBLE);
            tvSellerLabel.setText("对接销售：");
        }
    }

    private String getPhone() {
        return shopDetail.getLead() == null ? null : shopDetail.getLead()
                .getPhone();
    }

    @OnClick(R.id.img_call)
    void onCall() {
        if (onAdvDetailHeaderClickListener != null) {
            onAdvDetailHeaderClickListener.onCall(getPhone());
        }
    }

    @OnClick(R.id.img_sms)
    void onSms() {
        if (shopDetail == null || shopDetail.getLead() == null || shopDetail.getLead()
                .getUserId() <= 0) {
            return;
        }
        if (onAdvDetailHeaderClickListener != null) {
            onAdvDetailHeaderClickListener.onSms(shopDetail);
        }
    }

    @OnClick(R.id.tv_seller)
    void onSeller() {
        if (onAdvDetailHeaderClickListener != null) {
            onAdvDetailHeaderClickListener.onSale(tvSeller.getText()
                    .toString(), tvSeller);
        }
    }

    @OnClick(R.id.img_arrive_shop)
    void onArriveShop() {
        if (onAdvDetailHeaderClickListener != null) {
            onAdvDetailHeaderClickListener.onCheckedChanged(shopDetail.isCome());
        }
    }

    public interface OnAdvDetailHeaderClickListener {

        void onCall(String phone);

        void onSms(AdvDetail detail);

        void onSale(String name, TextView tvName);

        void onCheckedChanged(boolean isChecked);
    }
}
