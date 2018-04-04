package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ThemeUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljemojilibrary.EmojiUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 商家主页问答样式
 * Created by chen_bin on 2017/5/11 0011.
 */
public class MerchantAnswerViewHolder extends BaseViewHolder<Answer> {
    @BindView(R2.id.answer_header_view)
    View answerHeaderView;
    @BindView(R2.id.tv_answer_header_title)
    TextView tvAnswerHeaderTitle;
    @BindView(R2.id.answer_view)
    LinearLayout answerView;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_summary)
    TextView tvSummary;
    @BindView(R2.id.bottom_thin_line_layout)
    View bottomThinLineLayout;
    @BindView(R2.id.bottom_thick_line_layout)
    View bottomThickLineLayout;
    private int faceSize;
    private OnItemClickListener onItemClickListener;

    public MerchantAnswerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        faceSize = CommonUtil.dp2px(itemView.getContext(), 20);
        answerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                }
            }
        });
    }

    @Override
    protected void setViewData(Context context, Answer answer, int position, int viewType) {
        if (answer == null) {
            return;
        }
        tvTitle.setText(EmojiUtil.parseEmojiByText2(context,
                answer.getQuestion()
                        .getTitle(),
                faceSize));
        if (TextUtils.isEmpty(answer.getSummary())) {
            tvSummary.setVisibility(View.GONE);
        } else {
            tvSummary.setVisibility(View.VISIBLE);
            String upCountStr = answer.getUpCount() + context.getString(R.string
                    .label_praise___cv) + " ";
            String summary = "  " + answer.getSummary();
            SpannableStringBuilder builder = EmojiUtil.parseEmojiByText2(context,
                    upCountStr + "   " + summary,
                    faceSize);
            if (builder != null) {
                int colorPrimary = ThemeUtil.getAttrColor(tvSummary.getContext(),
                        R.attr.hljColorPrimary,
                        ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary));
                Drawable drawable = ContextCompat.getDrawable(context,
                        R.mipmap.icon_arrow_right_primary_11_18);
                drawable.mutate()
                        .setColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN);
                drawable.setBounds(0,
                        0,
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
                HljImageSpan span = new HljImageSpan(drawable);
                builder.setSpan(span,
                        upCountStr.length(),
                        upCountStr.length() + 3,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new ForegroundColorSpan(colorPrimary),
                        0,
                        upCountStr.length() - 1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tvSummary.setText(builder);
        }
    }

    public void setShowAnswerHeaderView(boolean showAnswerHeaderView) {
        answerHeaderView.setVisibility(showAnswerHeaderView ? View.VISIBLE : View.GONE);
    }

    public void setAnswerHeaderTitle(String answerHeaderTitle) {
        tvAnswerHeaderTitle.setText(answerHeaderTitle);
    }

    public void setShowBottomThinLineView(boolean showBottomThinLineView) {
        bottomThinLineLayout.setVisibility(showBottomThinLineView ? View.VISIBLE : View.GONE);
        bottomThickLineLayout.setVisibility(View.GONE);
    }

    public void setShowBottomThickLineView(boolean showBottomThickLineView) {
        bottomThinLineLayout.setVisibility(View.GONE);
        bottomThickLineLayout.setVisibility(showBottomThickLineView ? View.VISIBLE : View.GONE);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
