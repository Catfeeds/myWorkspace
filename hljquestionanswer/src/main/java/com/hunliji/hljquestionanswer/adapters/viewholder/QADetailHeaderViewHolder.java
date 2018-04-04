package com.hunliji.hljquestionanswer.adapters.viewholder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.modelwrappers.QaAuthor;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.WeddingCarRouteService;
import com.hunliji.hljquestionanswer.HljQuestionAnswer;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hua_rong on 2017/9/27 0027
 * 问题详情的头部信息
 */

public class QADetailHeaderViewHolder extends BaseViewHolder<Question> {

    @BindView(R2.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R2.id.rl_merchant)
    RelativeLayout rlMerchant;
    @BindView(R2.id.iv_ask)
    ImageView ivAsk;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.ib_complain)
    ImageButton ibComplain;
    @BindView(R2.id.tv_question_time)
    TextView tvQuestionTime;
    @BindView(R2.id.tv_question_author)
    TextView tvQuestionAuthor;
    @BindView(R2.id.tv_count)
    TextView tvCount;
    @BindView(R2.id.tv_same_question)
    TextView tvSameQuestion;
    @BindView(R2.id.tv_empty_hint)
    TextView tvEmptyHint;
    @BindView(R2.id.ll_empty)
    LinearLayout llEmpty;
    @BindView(R2.id.tv_complain)
    TextView tvComplain;

    private int totalCount;
    private OnQADetailActionInterface actionInterface;
    private boolean hideComplain;
    private Context mContext;
    private WeddingCarRouteService carRouteService;

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public QADetailHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
    }

    private void initCarRouteService() {
        carRouteService = (WeddingCarRouteService) ARouter.getInstance()
                .build(RouterPath.ServicePath.WEDDING_CAR_SERVICE)
                .navigation();
    }

    /**
     * 用于申诉详情 隐藏申诉
     *
     * @param hideComplain
     */
    public void setComplainHidden(boolean hideComplain) {
        this.hideComplain = hideComplain;
    }

    @Override
    protected void setViewData(
            Context context, final Question question, int position, int viewType) {
        if (question != null) {
            itemView.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(totalCount == 0 ? View.VISIBLE : View.GONE);
            tvCount.setText(context.getString(R.string.label_total_count_answer___qa, totalCount));
            tvTitle.setText(question.getTitle());
            DateTime createdAt = question.getCreatedAt();
            if (createdAt != null) {
                tvQuestionTime.setText(createdAt.toString("yyyy-MM-dd"));
            }
            if (HljQuestionAnswer.isMerchant(context)) {
                rlMerchant.setVisibility(View.GONE);
                tvSameQuestion.setVisibility(View.GONE);
                Integer status = question.getAppealStatus();
                if (!hideComplain) {
                    if (status != null && status == 0) {
                        ibComplain.setVisibility(View.GONE);
                        tvComplain.setVisibility(View.VISIBLE);
                    } else {
                        ibComplain.setVisibility(View.VISIBLE);
                        tvComplain.setVisibility(View.GONE);
                    }
                    tvTitle.setPadding(0, 0, 0, 0);
                } else {
                    tvCount.setVisibility(View.GONE);
                    llEmpty.setVisibility(View.GONE);
                }
                tvQuestionAuthor.setVisibility(View.VISIBLE);
                tvEmptyHint.setText(R.string.hint_no_relevant_answer___qa);
                QaAuthor qaAuthor = question.getUser();
                if (qaAuthor != null) {
                    tvQuestionAuthor.setText(context.getString(R.string.label_author_question___qa,
                            qaAuthor.getName()));
                }
                ibComplain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (actionInterface != null) {
                            actionInterface.onMoreOption(null, getAdapterPosition());
                        }
                    }
                });
            } else {
                tvSameQuestion.setBackgroundResource(question.isFollow() ? R.drawable
                        .sp_r13_half_stroke_primary_solid_trans : R.drawable
                        .sp_r13_half_stroke_line2_solid_trans);
                tvSameQuestion.setTextColor(question.isFollow() ? ContextCompat.getColor(context,
                        R.color.colorPrimary) : ContextCompat.getColor(context,
                        R.color.colorBlack3));
                final Merchant merchant = question.getMerchant();
                if (merchant != null) {
                    tvMerchantName.setText(merchant.getName());
                    tvMerchantName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (merchant.getShopType() == Merchant.SHOP_TYPE_CAR) {
                                //婚车商家跳到婚车二级页
                                if (HljQuestionAnswer.isMerchant(mContext)) {
                                    //商家端没有跳转婚车二级页
                                    return;
                                }
                                if (carRouteService == null) {
                                    initCarRouteService();
                                }
                                carRouteService.gotoWeddingCarActivity(mContext,
                                        merchant.getCityName(),
                                        merchant.getCityCode());
                            } else {
                                ARouter.getInstance()
                                        .build(RouterPath.IntentPath.Customer.MERCHANT_HOME)
                                        .withLong("id", merchant.getId())
                                        .withTransition(R.anim.slide_in_right,
                                                R.anim.activity_anim_default)
                                        .navigation(view.getContext());
                            }
                        }
                    });
                }
                int followCount = question.getFollowCount();
                tvSameQuestion.setText(followCount == 0 ? "同问" : String.format("同问%s",
                        followCount));
                tvSameQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (actionInterface != null) {
                            actionInterface.onFollowQuestion(getItem(), getAdapterPosition());
                        }
                    }
                });
            }
        }
    }

    public void setActionInterface(OnQADetailActionInterface actionInterface) {
        this.actionInterface = actionInterface;
    }


}
