package com.hunliji.hljinsurancelibrary.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljinsurancelibrary.R;
import com.hunliji.hljinsurancelibrary.R2;
import com.hunliji.hljinsurancelibrary.models.InsuranceProduct;
import com.hunliji.hljinsurancelibrary.models.MyPolicy;
import com.hunliji.hljinsurancelibrary.models.PolicyCard;
import com.hunliji.hljinsurancelibrary.models.PolicyDetail;
import com.hunliji.hljinsurancelibrary.views.activities.CreateHlbPolicyActivity;
import com.hunliji.hljinsurancelibrary.views.activities.CreateMYBActivity;
import com.hunliji.hljinsurancelibrary.views.activities.MYBDetailActivity;
import com.hunliji.hljinsurancelibrary.views.activities.MYBFailureActivity;
import com.hunliji.hljinsurancelibrary.views.activities.PolicyDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hua_rong on 2017/5/24.
 * 保单
 */

public class PolicyAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final LayoutInflater inflater;
    private Context context;
    private List<MyPolicy> dataList;
    private View footerView;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private int rightMargin1;
    private int rightMargin2;

    public void setDataList(List<MyPolicy> dataList) {
        this.dataList = dataList;
    }

    public void setFooterView(View footView) {
        this.footerView = footView;
    }

    public PolicyAdapter(Context context, List<MyPolicy> dataList) {
        this.context = context;
        this.dataList = dataList;
        inflater = LayoutInflater.from(context);
        rightMargin1 = CommonUtil.dp2px(context, 88);
        rightMargin2 = CommonUtil.dp2px(context, 11);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                View view = inflater.inflate(R.layout.layout_policy_item___ins, parent, false);
                return new PolicyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof PolicyViewHolder) {
            PolicyViewHolder viewHolder = (PolicyViewHolder) holder;
            viewHolder.setView(context,
                    dataList.get(position),
                    position,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return (footerView == null ? 0 : 1) + (dataList == null ? 0 : dataList.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (footerView != null && position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    class PolicyViewHolder extends BaseViewHolder<MyPolicy> {

        @BindView(R2.id.tv_project_name)
        TextView tvProjectName;
        @BindView(R2.id.tv_created_at)
        TextView tvCreatedAt;
        @BindView(R2.id.tv_insured)
        TextView tvInsured;
        @BindView(R2.id.tv_count)
        TextView tvCount;
        @BindView(R2.id.tv_insurance_from)
        TextView tvInsuranceFrom;
        @BindView(R2.id.tv_get)
        TextView tvGet;
        @BindView(R2.id.tv_insurance_card)
        TextView tvInsuranceCard;
        @BindView(R2.id.img_status)
        ImageView imgStatus;
        @BindView(R2.id.tv_refund)
        TextView tvRefund;
        @BindView(R2.id.tv_failure)
        TextView tvFailure;
        @BindView(R2.id.layout_failure)
        RelativeLayout layoutFailure;
        @BindView(R2.id.layout_benefit)
        LinearLayout layoutBenefit;
        @BindView(R2.id.layout_content)
        LinearLayout layoutContent;
        @BindView(R2.id.tv_count_hint)
        TextView tvCountHint;

        PolicyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(
                Context context, final MyPolicy myPolicy, int position, int viewType) {
            if (myPolicy == null) {
                return;
            }
            InsuranceProduct product = myPolicy.getProduct();
            if (product != null) {
                tvProjectName.setText(product.getTitle());
            }
            if (myPolicy.getCreatedAt() != null) {
                tvCreatedAt.setText(String.format("创建时间: %s",
                        myPolicy.getCreatedAt()
                                .toString(HljTimeUtils.DATE_FORMAT_MIDDLE)));
            }
            //为使各个手机冒号对齐，用四个汉字，中间两个汉字透明
            SpannableStringBuilder countHint = new SpannableStringBuilder("份份数数:");
            ForegroundColorSpan countHintSpan = new ForegroundColorSpan(context.getResources()
                    .getColor(R.color.transparent));
            countHint.setSpan(countHintSpan,
                    1,
                    "分数".length() + 1,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            tvCountHint.setText(countHint);

            tvInsured.setText(myPolicy.getParty());
            tvCount.setText(myPolicy.getNum() + "份");
            String rawGiverName = myPolicy.getGiverName();
            if (!TextUtils.isEmpty(rawGiverName)) {
                String giverName = "(" + rawGiverName + "赠)";
                SpannableStringBuilder ssb = new SpannableStringBuilder(giverName);
                ForegroundColorSpan span = new ForegroundColorSpan(context.getResources()
                        .getColor(R.color.colorLink));
                ssb.setSpan(span, 1, giverName.length() - 2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                tvInsuranceFrom.setText(ssb);
            } else {
                tvInsuranceFrom.setText(null);
            }

            PolicyCard card = myPolicy.getCard();
            if (card != null) {
                String brideName = card.getBrideName();
                String groomName = card.getGroomName();
                String insuranceCard = groomName + "和" + brideName + "的婚礼";
                tvInsuranceCard.setText(insuranceCard);
                tvInsuranceCard.setVisibility(View.VISIBLE);
            } else {
                tvInsuranceCard.setText(null);
                tvInsuranceCard.setVisibility(View.GONE);
            }
            //0未支付 1未提交保单 2待生效 3保障中 4已终止 5投保失败 6已退款
            imgStatus.setVisibility(View.GONE);
            tvGet.setVisibility(View.GONE);
            layoutFailure.setVisibility(View.GONE);
            layoutBenefit.setVisibility(View.VISIBLE);
            final int status = myPolicy.getStatus();
            switch (status) {
                case MyPolicy.STATUS_UNPAY:
                    //未支付
                    break;
                case MyPolicy.STATUS_UNSUBMITTED:
                    //未提交保单
                    imgStatus.setVisibility(View.GONE);
                    tvGet.setVisibility(View.VISIBLE);
                    String giverName = myPolicy.getGiverName();
                    if (TextUtils.isEmpty(giverName)) {
                        tvGet.setText("填写保单");
                    } else {
                        tvGet.setText("立即领取");
                    }
                    break;
                case MyPolicy.STATUS_TO_BE_EFFECTIVE:
                    //待生效
                    tvGet.setVisibility(View.GONE);
                    imgStatus.setVisibility(View.VISIBLE);
                    imgStatus.setImageResource(R.mipmap.icon_to_be_effective);
                    break;
                case MyPolicy.STATUS_PROTECT:
                    //保障中
                    tvGet.setVisibility(View.GONE);
                    imgStatus.setVisibility(View.VISIBLE);
                    imgStatus.setImageResource(R.mipmap.icon_in_security);
                    break;
                case MyPolicy.STATUS_FINISHED:
                    //已终止
                    imgStatus.setVisibility(View.VISIBLE);
                    imgStatus.setImageResource(R.mipmap.icon_terminated);
                    tvGet.setVisibility(View.GONE);
                    break;
                case MyPolicy.STATUS_FAILED:
                    //投保失败
                    layoutFailure.setVisibility(View.VISIBLE);
                    layoutBenefit.setVisibility(View.GONE);
                    break;
                case MyPolicy.STATUS_REFUND:
                    //已退款
                    layoutFailure.setVisibility(View.VISIBLE);
                    layoutBenefit.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutContent
                    .getLayoutParams();
            if (params != null) {
                if (imgStatus.getVisibility() == View.VISIBLE) {
                    params.rightMargin = rightMargin1;
                } else {
                    params.rightMargin = rightMargin2;
                }
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int type = myPolicy.getType();
                    Activity activity = (Activity) v.getContext();
                    Intent intent = null;
                    if (type == MyPolicy.TYPE_MI_YUE_BAO) {
                        //蜜月保
                        if (status == MyPolicy.STATUS_UNSUBMITTED) {
                            //未提交保单
                            intent = new Intent(activity, CreateMYBActivity.class);
                            intent.putExtra("id", myPolicy.getId());
                            if (myPolicy.getCard() != null && myPolicy.getCard()
                                    .getTime() != null) {
                                intent.putExtra("weddingDate",
                                        myPolicy.getCard()
                                                .getTime()
                                                .getMillis());
                            }
                        } else if (status == MyPolicy.STATUS_TO_BE_EFFECTIVE || status ==
                                MyPolicy.STATUS_PROTECT || status == MyPolicy.STATUS_FINISHED) {
                            intent = new Intent(activity, MYBDetailActivity.class);
                            intent.putExtra("id", myPolicy.getId());
                        } else {
                            intent = new Intent(activity, MYBFailureActivity.class);
                            intent.putExtra("id", myPolicy.getId());
                        }
                    } else {
                        //婚礼宝以及其他
                        if (status == MyPolicy.STATUS_UNSUBMITTED) {
                            //未提交保单
                            intent = new Intent(activity, CreateHlbPolicyActivity.class);
                            PolicyDetail policyDetail = new PolicyDetail();
                            policyDetail.setId(myPolicy.getId());
                            policyDetail.setProduct(myPolicy.getProduct());
                            intent.putExtra("policy_detail", policyDetail);
                        } else {
                            intent = new Intent(activity, PolicyDetailActivity.class);
                            intent.putExtra("id", myPolicy.getId());
                        }
                    }
                    if (intent != null) {
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            });
        }
    }
}
