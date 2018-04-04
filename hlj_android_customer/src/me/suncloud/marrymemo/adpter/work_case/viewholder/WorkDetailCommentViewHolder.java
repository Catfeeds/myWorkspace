package me.suncloud.marrymemo.adpter.work_case.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.CommentStatistics;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.models.ServiceCommentMark;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljquestionanswer.activities.AskQuestionListActivity;
import com.hunliji.hljquestionanswer.adapters.viewholder.AskQuestionViewHolder;
import com.hunliji.hljquestionanswer.models.HljHttpQuestion;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.comment.viewholder.ServiceCommentBriefInfoViewHolder;
import me.suncloud.marrymemo.adpter.comment.viewholder.ServiceCommentMarksViewHolder;
import me.suncloud.marrymemo.fragment.work_case.WorkDetailFragment;
import me.suncloud.marrymemo.model.NewMerchant;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.view.comment.ServiceCommentListActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;

/**
 * Created by luohanlin on 2017/12/21.
 */

public class WorkDetailCommentViewHolder extends BaseViewHolder {
    @BindView(R.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R.id.tv_comment_percent)
    TextView tvCommentPercent;
    @BindView(R.id.comment_marks_layout)
    RelativeLayout commentMarksLayout;
    @BindView(R.id.comment_brief_info_layout)
    RelativeLayout commentBriefInfoLayout;
    @BindView(R.id.comment_content_layout)
    RelativeLayout commentContentLayout;
    @BindView(R.id.comment_empty_layout)
    RelativeLayout commentEmptyLayout;
    @BindView(R.id.comment_layout)
    LinearLayout commentLayout;
    @BindView(R.id.tv_question_count)
    TextView tvQuestionCount;
    @BindView(R.id.question_brief_info_layout)
    RelativeLayout questionBriefInfoLayout;
    @BindView(R.id.question_content_layout)
    RelativeLayout questionContentLayout;
    @BindView(R.id.question_empty_layout)
    RelativeLayout questionEmptyLayout;
    @BindView(R.id.question_layout)
    LinearLayout questionLayout;
    @BindView(R.id.iv_merchant_avatar)
    RoundedImageView ivMerchantAvatar;
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.iv_level)
    ImageView ivLevel;
    @BindView(R.id.iv_bond)
    ImageView ivBond;
    @BindView(R.id.rating_view_merchant)
    RatingBar ratingViewMerchant;
    @BindView(R.id.tv_merchant_comment_count)
    TextView tvMerchantCommentCount;
    @BindView(R.id.tv_merchant_address)
    TextView tvMerchantAddress;
    @BindView(R.id.iv_merchant_arrow)
    ImageView ivMerchantArrow;
    @BindView(R.id.merchant_layout)
    LinearLayout merchantLayout;
    @BindView(R.id.tv_refund)
    TextView tvRefund;
    @BindView(R.id.refund_layout)
    LinearLayout refundLayout;
    @BindView(R.id.tv_promise)
    TextView tvPromise;
    @BindView(R.id.promise_layout)
    LinearLayout promiseLayout;
    @BindView(R.id.iv_promise_arrow)
    ImageView ivPromiseArrow;
    @BindView(R.id.merchant_promise_layout)
    RelativeLayout merchantPromiseLayout;
    @BindView(R.id.merchant_info_layout)
    RelativeLayout merchantInfoLayout;

    private Context context;
    private Work work;

    public WorkDetailCommentViewHolder(View view, Context cxt, Work wk) {
        super(view);
        this.context = cxt;
        this.work = wk;
        ButterKnife.bind(this, view);
    }

    @Override
    protected void setViewData(Context mContext, Object item, int position, int viewType) {
        setOrderComment();
        setMerchantView(work.getMerchant());
        WorkDetailFragment.WorkCommentsQuestionsCasePhotosZip cqZip = (WorkDetailFragment.WorkCommentsQuestionsCasePhotosZip) item;
        if (cqZip == null) {
            return;
        }
        if (work.getLatestComment() != null && work.getLatestComment()
                .getId() > 0) {
            setCommentMarks(cqZip.httpCommentMark.getData());
        }
        setQuestions(cqZip.httpQuestion);
    }

    private void setOrderComment() {
        ServiceComment comment = work.getLatestComment();
        if (comment == null) {
            comment = work.getMerchant()
                    .getLatestComment();
        }
        if (comment == null || comment.getId() == 0) {
            commentEmptyLayout.setVisibility(View.VISIBLE);
            commentMarksLayout.setVisibility(View.GONE);
        } else {
            commentContentLayout.setVisibility(View.VISIBLE);
            tvCommentCount.setText(context.getString(R.string.label_user_comment3,
                    work.getMerchant()
                            .getCommentsCount()));
            CommentStatistics commentStatistics = work.getMerchant()
                    .getCommentStatistics();
            if (commentStatistics == null || commentStatistics.getGoodRate() <= 0) {
                tvCommentPercent.setVisibility(View.GONE);
            } else {
                tvCommentPercent.setVisibility(View.VISIBLE);
                tvCommentPercent.setText(context.getString(R.string.label_good_rate,
                        String.valueOf(Math.floor(work.getMerchant()
                                .getCommentStatistics()
                                .getGoodRate() * 1000) / 10)));
            }
            //评价内容
            if (commentBriefInfoLayout.getChildCount() == 0) {
                View.inflate(context,
                        R.layout.service_comment_brief_info_list_item,
                        commentBriefInfoLayout);
            }
            View commentView = commentBriefInfoLayout.getChildAt(commentBriefInfoLayout
                    .getChildCount() - 1);
            ServiceCommentBriefInfoViewHolder holder =
                    (ServiceCommentBriefInfoViewHolder) commentView.getTag();
            if (holder == null) {
                holder = new ServiceCommentBriefInfoViewHolder(commentView);
                holder.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, Object object) {
                        onCommentList(0);
                    }
                });
                commentView.setTag(holder);
            }
            holder.setView(context, comment, 0, 0);
        }
        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCommentList(0);
            }
        });
    }

    //设置评论标签
    private void setCommentMarks(List<ServiceCommentMark> marks) {
        if (CommonUtil.isCollectionEmpty(marks) || marks.size() < 5) { //评价标签数小于5个的时候在套餐详情页则不显示
            commentMarksLayout.setVisibility(View.GONE);
        } else {
            commentMarksLayout.setTag(View.VISIBLE);
            if (commentMarksLayout.getChildCount() == 0) {
                View.inflate(context,
                        R.layout.service_comment_marks_flow,
                        commentMarksLayout);
            }
            View marksView = commentMarksLayout.getChildAt(commentMarksLayout.getChildCount() - 1);
            ServiceCommentMarksViewHolder holder = (ServiceCommentMarksViewHolder)
                    marksView.getTag();
            // 1、待改善放在最后；2、待改善为0时不显示。
            ServiceCommentMark mark = null;
            for (ServiceCommentMark m : marks) {
                if (m.getId() == ServiceCommentMark.ID_BAD_REPUTATION) {
                    mark = m;
                    marks.remove(m);
                    break;
                }
            }
            if (mark != null && mark.getCommentsCount() > 0) {
                marks.add(mark);
            }

            if (holder == null) {
                holder = new ServiceCommentMarksViewHolder(marksView);
                holder.setCanCheck(false);
                holder.setCanShowArrowIcon(false);
                holder.setPaddingBottom(CommonUtil.dp2px(context, 4));
                holder.setOnCommentFilterListener(new ServiceCommentMarksViewHolder
                        .OnCommentFilterListener() {
                    @Override
                    public void onCommentFilter(long markId) {
                        onCommentList(markId);
                    }
                });
                marksView.setTag(holder);
            }
            holder.setView(context, marks, 0, 0);
        }
    }

    //评论列表页
    private void onCommentList(long markId) {
        if (work != null && work.getId() > 0 && work.getMerchant() != null && work.getMerchant()
                .getId() > 0) {
            NewMerchant newMerchant = work.getMerchant();
            Merchant merchant = new Merchant();
            merchant.setId(newMerchant.getId());
            merchant.setUserId(newMerchant.getUserId());
            merchant.setName(newMerchant.getName());
            merchant.setLogoPath(newMerchant.getLogoPath());
            merchant.setShopType(newMerchant.getShopType());
            Intent intent = new Intent(context, ServiceCommentListActivity.class);
            intent.putExtra(ServiceCommentListActivity.ARG_MERCHANT, merchant);
            intent.putExtra(ServiceCommentListActivity.ARG_MERCHANT_ID, merchant.getId());
            intent.putExtra(ServiceCommentListActivity.ARG_MERCHANT_USER_ID,
                    merchant.getUserId());
            intent.putExtra(ServiceCommentListActivity.ARG_WORK_ID, work.getId());
            intent.putExtra(ServiceCommentListActivity.ARG_MARK_ID, markId);
            context.startActivity(intent);
        }
    }

    private void setQuestions(HljHttpQuestion<List<Question>> listHljHttpData) {
        if (listHljHttpData == null || CommonUtil.isCollectionEmpty(listHljHttpData.getData())) {
            questionEmptyLayout.setVisibility(View.VISIBLE);
            questionContentLayout.setVisibility(View.GONE);
        } else {
            questionEmptyLayout.setVisibility(View.GONE);
            questionContentLayout.setVisibility(View.VISIBLE);
            tvQuestionCount.setText(context.getString(R.string.label_ask_question_count,
                    listHljHttpData.getTotalCount()));
            if (questionBriefInfoLayout.getChildCount() == 0) {
                View.inflate(context, R.layout.ask_quesdtion_item___qa, questionBriefInfoLayout);
            }
            View questionView = questionBriefInfoLayout.getChildAt(questionBriefInfoLayout
                    .getChildCount() - 1);
            AskQuestionViewHolder holder = (AskQuestionViewHolder) questionView.getTag();
            if (holder == null) {
                holder = new AskQuestionViewHolder(questionView);
                holder.setOnItemClickListener(new OnItemClickListener<Question>() {
                    @Override
                    public void onItemClick(int position, Question object) {
                        questionContentLayout.performClick();
                    }
                });
                questionView.setTag(holder);
            }
            holder.setWorkWithMerchantVisibility(true);
            holder.setView(context,
                    listHljHttpData.getData()
                            .get(0),
                    0,
                    0);
        }
        questionEmptyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAskQuestionList(true);
            }
        });
        questionContentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAskQuestionList(false);
            }
        });
    }

    //问大家列表
    private void onAskQuestionList(boolean isShowKeyboard) {
        if (work != null && work.getId() > 0 && work.getMerchant() != null && work.getMerchant()
                .getId() > 0) {
            Intent intent = new Intent(context, AskQuestionListActivity.class);
            intent.putExtra(AskQuestionListActivity.ARG_MERCHANT_ID,
                    work.getMerchant()
                            .getId());
            intent.putExtra(AskQuestionListActivity.ARG_WORK_ID, work.getId());
            intent.putExtra(AskQuestionListActivity.ARG_SHOW_KEYBOARD, isShowKeyboard);
            context.startActivity(intent);
        }
    }

    private void setMerchantView(NewMerchant merchant) {
        if (merchant == null) {
            return;
        }
        setMerchantPrivilege(merchant);
        ivBond.setVisibility(merchant.getBondSign() != null ? View.VISIBLE : View.GONE);
        int res = 0;
        switch (merchant.getGrade()) {
            case 2:
                res = R.mipmap.icon_merchant_level2_32_32;
                break;
            case 3:
                res = R.mipmap.icon_merchant_level3_32_32;
                break;
            case 4:
                res = R.mipmap.icon_merchant_level4_32_32;
                break;
            default:
                break;
        }
        if (res != 0) {
            ivLevel.setVisibility(View.VISIBLE);
            ivLevel.setImageResource(res);
        } else {
            ivLevel.setVisibility(View.GONE);
        }
        tvMerchantName.setText(merchant.getName());
        tvMerchantAddress.setText(merchant.getAddress());
        String logoPath = JSONUtil.getAvatar(merchant.getLogoPath(), CommonUtil.dp2px(context, 46));
        Glide.with(context)
                .load(logoPath)
                .apply(new RequestOptions().placeholder(R.mipmap.icon_avatar_primary)
                        .dontAnimate())
                .into(ivMerchantAvatar);
        if (merchant.getCommentStatistics() != null) {
            ratingViewMerchant.setRating((float) merchant.getCommentStatistics()
                    .getScore());
        }
        if (merchant.getCommentsCount() > 0) {
            tvMerchantCommentCount.setText(context.getString(R.string.label_comment_count5,
                    merchant.getCommentsCount()));
        } else {
            tvMerchantCommentCount.setText(R.string.label_no_comment2);
        }
    }

    private void setMerchantPrivilege(NewMerchant merchant) {
        if (merchant == null) {
            return;
        }
        StringBuilder promiseBuilder = new StringBuilder();
        if (merchant.getMerchantPromise() != null) {
            for (String promise : merchant.getMerchantPromise()) {
                if (promiseBuilder.length() > 0) {
                    promiseBuilder.append("    ");
                }
                promiseBuilder.append(promise);
            }
        }
        StringBuilder refundBuilder = new StringBuilder();
        if (merchant.getChargeBack() != null) {
            for (String refund : merchant.getChargeBack()) {
                if (refundBuilder.length() > 0) {
                    refundBuilder.append("    ");
                }
                refundBuilder.append(refund);
            }
        }
        if (refundBuilder.length() > 0 || promiseBuilder.length() > 0) {
            merchantPromiseLayout.setVisibility(View.VISIBLE);
            if (promiseBuilder.length() > 0) {
                promiseLayout.setVisibility(View.VISIBLE);
                tvPromise.setText(promiseBuilder.toString());
            } else {
                promiseLayout.setVisibility(View.GONE);
            }
            if (refundBuilder.length() > 0) {
                refundLayout.setVisibility(View.VISIBLE);
                tvRefund.setText(refundBuilder.toString());
            } else {
                refundLayout.setVisibility(View.GONE);
            }
        } else {
            merchantPromiseLayout.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.merchant_layout)
    public void onMerchant() {
        if (work == null || work.getMerchantId() == 0) {
            return;
        }
        Intent intent = new Intent(context, MerchantDetailActivity.class);
        intent.putExtra("id", work.getMerchantId());
        context.startActivity(intent);
    }


    @OnClick(R.id.merchant_promise_layout)
    public void onPromiseInfo() {
        if (work != null && work.getMerchant() != null) {
            HljWeb.startWebView(context,
                    work.getMerchant()
                            .getGuidePath());
        }
    }
}
