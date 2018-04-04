package me.suncloud.marrymemo.adpter.merchant.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.ServiceCommentMark;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.comment.viewholder.ServiceCommentMarksViewHolder;

/**
 * Created by wangtao on 2017/9/29.
 */

public class MerchantHomeCommentMarksHeaderViewHolder extends ServiceCommentMarksViewHolder {


    @BindView(R.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R.id.tv_comment_percent)
    TextView tvCommentPercent;
    @BindView(R.id.tv_comment_empty)
    TextView tvCommentEmpty;
    @BindView(R.id.marks_layout)
    LinearLayout marksLayout;
    @BindView(R.id.comment_empty_layout)
    LinearLayout commentEmptyLayout;

    private OnCommentEmptyClickListener onCommentClickListener;

    public MerchantHomeCommentMarksHeaderViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.merchant_home_comment_marks_header, parent, false));
    }

    public void setOnCommentClickListener(OnCommentEmptyClickListener onCommentClickListener) {
        this.onCommentClickListener = onCommentClickListener;
    }

    public MerchantHomeCommentMarksHeaderViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, itemView);
        setShowBottomLineView(true);
    }

    @Override
    protected void setViewData(
            Context mContext, List<ServiceCommentMark> marks, int position, int viewType) {
        if (CommonUtil.isCollectionEmpty(marks)) {
            marksLayout.setVisibility(View.GONE);
            commentEmptyLayout.setVisibility(View.VISIBLE);
        }
        super.setViewData(mContext, marks, position, viewType);
    }

    public void setCommentCount(int commentCount, Merchant merchant) {
        if (commentCount > 0) {
            tvCommentCount.setText(tvCommentCount.getContext()
                    .getString(R.string.label_user_comment3, commentCount));
            tvCommentPercent.setVisibility(View.VISIBLE);
            tvCommentEmpty.setVisibility(View.GONE);
            if (merchant.getCommentStatistics() == null || merchant.getCommentStatistics()
                    .getGoodRate() <= 0) {
                tvCommentPercent.setVisibility(View.GONE);
            } else {
                tvCommentPercent.setVisibility(View.VISIBLE);
                tvCommentPercent.setText(tvCommentPercent.getContext()
                        .getString(R.string.label_good_rate,
                                String.valueOf(Math.floor(merchant.getCommentStatistics()
                                        .getGoodRate() * 1000) / 10)));
            }
        } else {
            tvCommentCount.setText(R.string.label_user_comment);
            tvCommentPercent.setVisibility(View.GONE);
            tvCommentEmpty.setVisibility(View.VISIBLE);

        }
    }

    @OnClick(R.id.comment_empty_layout)
    public void onCommentEmptyClicked() {
        if (onCommentClickListener != null) {
            onCommentClickListener.onComment();
        }
    }

    public interface OnCommentEmptyClickListener {
        void onComment();
    }
}
