package me.suncloud.marrymemo.adpter.marry.viewholder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Poster;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.marry.MarryTask;
import me.suncloud.marrymemo.model.marry.Template;
import me.suncloud.marrymemo.util.BannerUtil;

/**
 * Created by hua_rong on 2017/12/8
 */

public class MarryTaskViewHolder extends BaseViewHolder<MarryTask> {

    @BindView(R.id.iv_box)
    ImageView ivBox;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_expire)
    TextView tvExpire;
    @BindView(R.id.view_line)
    View viewLine;
    @BindView(R.id.iv_tag)
    ImageView ivTag;
    @BindView(R.id.iv_poster)
    ImageView ivPoster;
    @BindView(R.id.ll_expire)
    LinearLayout llExpire;

    private Context context;
    private OnMarryTaskClickListener onMarryTaskListener;

    public MarryTaskViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        context = itemView.getContext();
    }

    @OnClick(R.id.rl_check)
    void onCheck(View view) {
        ivBox.setSelected(!ivBox.isSelected());
        if (onMarryTaskListener != null) {
            onMarryTaskListener.onMarryTaskCheck(getItem());
        }
    }

    @Override
    protected void setViewData(
            Context mContext, MarryTask marryTask, int position, int viewType) {
        if (marryTask == null) {
            return;
        }
        viewLine.setVisibility(marryTask.isLastLine() ? View.GONE : View.VISIBLE);
        llExpire.setVisibility(View.GONE);
        ivPoster.setVisibility(View.GONE);
        ivTag.setVisibility(View.GONE);
        tvExpire.setTextColor(ContextCompat.getColor(context, R.color.colorBlack3));
        ivBox.setSelected(false);
        switch (marryTask.getType()) {
            case MarryTask.TYPE_EXPIRED:
            case MarryTask.TYPE_RECENT:
            case MarryTask.TYPE_REMOTE:
                tvTitle.setTextColor(ContextCompat.getColor(context, R.color.colorBlack2));
                tvTitle.setText(marryTask.getTitle());
                ivTag.setVisibility(marryTask.getToTa() == 0 ? View.VISIBLE : View.GONE);
                if (marryTask.getExpireAt() != null) {
                    llExpire.setVisibility(View.VISIBLE);
                    tvExpire.setVisibility(View.VISIBLE);
                    tvExpire.setText(String.format(Locale.getDefault(),
                            "%s到期",
                            marryTask.getExpireAt()
                                    .toString("yyyy.MM.dd")));
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onMarryTaskListener != null) {
                            onMarryTaskListener.onMarryTaskItemClick(getItem());
                        }
                    }
                });
                break;
            case MarryTask.TYPE_SYSTEM:
                tvTitle.setTextColor(ContextCompat.getColor(context, R.color.colorBlack2));
                tvTitle.setText(marryTask.getTitle());
                bannerJump(marryTask);
                break;
            case MarryTask.TYPE_COMPLETED:
                if (marryTask.getTemplateId() == null) {
                    ivTag.setVisibility(marryTask.getToTa() == 0 ? View.VISIBLE : View.GONE);
                }
                tvTitle.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
                String title = marryTask.getTitle();
                if (!TextUtils.isEmpty(title)) {
                    SpannableString ss = new SpannableString(title);
                    ss.setSpan(new StrikethroughSpan(),
                            0,
                            title.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvTitle.setText(ss);
                }
                bannerJump(marryTask);
                ivBox.setSelected(true);
                break;
        }
        if (marryTask.getType() == MarryTask.TYPE_EXPIRED) {
            tvExpire.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }

    }

    private void bannerJump(MarryTask marryTask) {
        Template template = marryTask.getTemplate();
        if (template != null) {
            tvExpire.setTextColor(ContextCompat.getColor(context, R.color.colorLink));
            final Poster poster = template.getConfig();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (poster != null) {
                        BannerUtil.bannerJump(context, poster, null);
                    }
                }
            });
            if (poster != null) {
                ivPoster.setVisibility(View.VISIBLE);
                llExpire.setVisibility(View.VISIBLE);
                tvExpire.setText(poster.getTitle());
            }
        }
    }

    public void setOnMarryTaskListener(OnMarryTaskClickListener onMarryTaskListener) {
        this.onMarryTaskListener = onMarryTaskListener;
    }


}
