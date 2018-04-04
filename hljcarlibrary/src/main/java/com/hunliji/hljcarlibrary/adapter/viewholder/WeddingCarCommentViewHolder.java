package com.hunliji.hljcarlibrary.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.CommentMark;
import com.hunliji.hljcommonlibrary.models.CommentStatistics;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarDetailComment;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jinxin on 2017/12/26 0026.
 */

public class WeddingCarCommentViewHolder extends BaseViewHolder<WeddingCarProduct> {

    @BindView(R2.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R2.id.tv_comment_percent)
    TextView tvCommentPercent;
    @BindView(R2.id.comment_marks_layout)
    RelativeLayout commentMarksLayout;
    @BindView(R2.id.comment_brief_info_layout)
    RelativeLayout commentBriefInfoLayout;
    @BindView(R2.id.layout_comment_content)
    LinearLayout layoutCommentContent;
    @BindView(R2.id.layout_empty_comment)
    LinearLayout layoutEmptyComment;
    @BindView(R2.id.comment_layout)
    LinearLayout commentLayout;

    private Context mContext;
    private onWeddingCarCommentClickListener onWeddingCarCommentClickListener;
    private WeddingCarProduct carProduct;
    private List<CommentMark> markList;
    private boolean needToPicsActivity;

    public WeddingCarCommentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
    }

    public void setOnWeddingCarCommentClickListener(
            WeddingCarCommentViewHolder.onWeddingCarCommentClickListener
                    onWeddingCarCommentClickListener) {
        this.onWeddingCarCommentClickListener = onWeddingCarCommentClickListener;
    }

    public void setNeedToPicsActivity(boolean needToPicsActivity) {
        this.needToPicsActivity = needToPicsActivity;
    }

    public void setMarkList(List<CommentMark> markList) {
        this.markList = markList;
    }

    @Override
    protected void setViewData(
            Context mContext, WeddingCarProduct carProduct, int position, int viewType) {
        this.carProduct = carProduct;
        if (carProduct == null) {
            return;
        }
        final WeddingCarDetailComment carDetailComment = carProduct.getMerchantComment();
        if (carDetailComment != null) {
            layoutEmptyComment.setVisibility(View.GONE);
            layoutCommentContent.setVisibility(View.VISIBLE);
            setCommentMark(markList);
            tvCommentCount.setText(mContext.getString(R.string.label_user_comment3___car,
                    carDetailComment
                            .getMerchantCommentsCount()));
            CommentStatistics commentStatistics = carDetailComment.getCommentStatistics();
            if (commentStatistics == null || commentStatistics.getGoodRate() <= 0) {
                tvCommentPercent.setVisibility(View.GONE);
            } else {
                tvCommentPercent.setVisibility(View.VISIBLE);
                tvCommentPercent.setText(mContext.getString(R.string.label_good_rate___car,
                        String.valueOf(Math.floor(commentStatistics
                                .getGoodRate() * 1000) / 10)));
            }
            //评价内容
            if (commentBriefInfoLayout.getChildCount() == 0) {
                View.inflate(mContext, R.layout.wedding_car_comment_list_item___car,
                        commentBriefInfoLayout);
            }
            View commentView = commentBriefInfoLayout.getChildAt(commentBriefInfoLayout
                    .getChildCount() - 1);
            WeddingCarCommentContentViewHolder holder =
                    (WeddingCarCommentContentViewHolder) commentView.getTag();
            if (holder == null) {
                holder = new WeddingCarCommentContentViewHolder(commentView);
                commentView.setTag(holder);
            }
            holder.setNeedToPicsActivity(needToPicsActivity);
            holder.setDividerLineVisible(false);
            holder.setView(mContext, carDetailComment.getLastMerchantComment(), 0, 0);
            commentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(carDetailComment == null){
                        return;
                    }
                    if (onWeddingCarCommentClickListener != null) {
                        onWeddingCarCommentClickListener.onCommentIdList
                                (WeddingCarCommentViewHolder.this.carProduct,
                                carDetailComment.getId());
                    }
                }
            });
            layoutCommentContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(carDetailComment == null){
                        return;
                    }
                    if (onWeddingCarCommentClickListener != null) {
                        onWeddingCarCommentClickListener.onCommentIdList
                                (WeddingCarCommentViewHolder.this.carProduct,
                                carDetailComment.getId());
                    }
                }
            });
        } else {
            layoutCommentContent.setVisibility(View.GONE);
            layoutEmptyComment.setVisibility(View.VISIBLE);
        }
    }

    private void setCommentMark(List<CommentMark> commentMarkList) {
        if (CommonUtil.isCollectionEmpty(commentMarkList)) {
            commentMarksLayout.setVisibility(View.GONE);
        } else {
            commentMarksLayout.setTag(View.VISIBLE);
            commentMarksLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onWeddingCarCommentClickListener != null) {
                        onWeddingCarCommentClickListener.onCommentIdList
                                (WeddingCarCommentViewHolder.this.carProduct,
                                        0L);
                    }
                }
            });
            if (commentMarksLayout.getChildCount() == 0) {
                View.inflate(mContext,
                        R.layout.wedding_car_comment_marks_flow___car,
                        commentMarksLayout);
            }
            View marksView = commentMarksLayout.getChildAt(commentMarksLayout.getChildCount() - 1);
            WeddingCarCommentMarksViewHolder holder = (WeddingCarCommentMarksViewHolder)
                    marksView.getTag();
            if (holder == null) {
                holder = new WeddingCarCommentMarksViewHolder(marksView);
                holder.setCanCheck(false);
                holder.setCanShowArrowIcon(false);
                holder.setPaddingBottom(CommonUtil.dp2px(mContext, 4));
                holder.setOnCommentFilterListener(new WeddingCarCommentMarksViewHolder
                        .OnCommentFilterListener() {
                    @Override
                    public void onCommentFilter(long markId) {
                        if (onWeddingCarCommentClickListener != null) {
                            onWeddingCarCommentClickListener.onCommentMarkIdList(
                                    WeddingCarCommentViewHolder.this.carProduct,
                                    markId);
                        }
                    }
                });
                marksView.setTag(holder);
            }
            holder.setView(mContext, commentMarkList, 0, 0);
        }
    }


    public interface onWeddingCarCommentClickListener {
        void onCommentIdList(WeddingCarProduct carProduct, long commentId);

        void onCommentMarkIdList(WeddingCarProduct carProduct, long markId);
    }
}
