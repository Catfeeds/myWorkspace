package me.suncloud.marrymemo.adpter.comment.viewholder;

import android.content.Context;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerServiceCommentViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;

/**
 * 商家主页评论列表viewHolder
 * Created by chen_bin on 2017/4/14 0014.
 */
public abstract class BaseServiceCommentViewHolder extends TrackerServiceCommentViewHolder {
    @BindView(R.id.img_avatar)
    RoundedImageView imgAvatar;
    @BindView(R.id.tv_nick)
    TextView tvNick;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_rating_grade)
    TextView tvRatingGrade;
    @BindView(R.id.tv_bought_des)
    TextView tvBoughtDes;
    @BindView(R.id.tv_prefix_content)
    TextView tvPrefixContent;
    @BindView(R.id.tv_suffix_content)
    TextView tvSuffixContent;
    @BindView(R.id.tv_expand)
    TextView tvExpand;
    @BindView(R.id.suffix_content_layout)
    LinearLayout suffixContentLayout;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_collapse)
    TextView tvCollapse;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    private int logoSize;
    private ContentLayoutChangeListener listener;
    private final static int STATUS_INIT = 0;
    private final static int STATUS_NORMAL = 1;
    private final static int STATUS_EXPAND = 2;
    private final static int STATUS_COLLAPSE = 3;

    public BaseServiceCommentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        logoSize = CommonUtil.dp2px(itemView.getContext(), 40);

    }

    @Override
    protected void setViewData(
            final Context mContext,
            final ServiceComment comment,
            final int position,
            int viewType) {
        if (comment == null) {
            return;
        }

        HljVTTagger.buildTagger(tvExpand)
                .tagName("merchant_comment_see_full")
                .dataType("MerchantComment")
                .dataId(comment.getId())
                .hitTag();
        HljVTTagger.buildTagger(tvCollapse)
                .tagName("merchant_comment_see_full")
                .dataType("MerchantCommentTag")
                .dataId(comment.getId())
                .hitTag();

        Glide.with(mContext)
                .load(ImagePath.buildPath(comment.getAuthor()
                        .getAvatar())
                        .width(logoSize)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_avatar_primary)
                        .error(R.mipmap.icon_avatar_primary))
                .into(imgAvatar);
        tvNick.setText(comment.getAuthor()
                .getName());
        if (comment.getCreatedAt() == null) {
            tvTime.setVisibility(View.GONE);
        } else {
            tvTime.setVisibility(View.VISIBLE);
            tvTime.setText(comment.getCreatedAt()
                    .toString(Constants.DATE_FORMAT_SHORT));
        }
        tvRatingGrade.setText(String.format("%s星 %s",
                comment.getRating(),
                comment.getGrade(comment.getRating())));
        String knowTypeStr = comment.getKnowTypeStr();
        if (TextUtils.isEmpty(knowTypeStr)) {
            tvBoughtDes.setVisibility(View.GONE);
        } else {
            tvBoughtDes.setVisibility(View.VISIBLE);
            tvBoughtDes.setText(knowTypeStr);
        }
        setContent(comment);
    }

    public void setContent(ServiceComment comment) {
        if (TextUtils.isEmpty(comment.getContent())) {
            contentLayout.setVisibility(View.GONE);
        } else {
            setContentStatus(comment);
            contentLayout.setVisibility(View.VISIBLE);
            tvContent.setText(comment.getContent());
            tvPrefixContent.setVisibility(View.VISIBLE);
            tvPrefixContent.setText(comment.getContent());
            if (listener != null) {
                tvPrefixContent.removeOnLayoutChangeListener(listener);
            }
            listener = new ContentLayoutChangeListener(comment);
            tvPrefixContent.addOnLayoutChangeListener(listener);
            tvExpand.setOnClickListener(listener);
            tvCollapse.setOnClickListener(listener);
        }
    }

    private class ContentLayoutChangeListener implements View.OnLayoutChangeListener, View
            .OnClickListener {

        private ServiceComment comment;

        public ContentLayoutChangeListener(final ServiceComment comment) {
            this.comment = comment;
            final Layout l = tvPrefixContent.getLayout();
            if (l != null) {
                final int lineCount = l.getLineCount();
                if (lineCount <= 4) {
                    comment.setContentStatus(STATUS_NORMAL);
                } else if (comment.getContentStatus() == STATUS_INIT || comment.getContentStatus
                        () == STATUS_COLLAPSE) {
                    comment.setContentStatus(STATUS_COLLAPSE);
                    tvSuffixContent.setText(comment.getContent()
                            .subSequence(l.getLineStart(3), l.getLineVisibleEnd(3)));
                }
                setContentStatus(comment);
            }
        }

        @Override
        public void onLayoutChange(
                final View v,
                int left,
                int top,
                int right,
                int bottom,
                int oldLeft,
                int oldTop,
                int oldRight,
                int oldBottom) {
            tvPrefixContent.removeOnLayoutChangeListener(this);
            final Layout l = tvPrefixContent.getLayout();
            if (l != null) {
                final int lineCount = l.getLineCount();
                tvPrefixContent.post(new Runnable() {
                    @Override
                    public void run() {
                        if (lineCount <= 4) {
                            comment.setContentStatus(STATUS_NORMAL);
                        } else if (comment.getContentStatus() == STATUS_INIT || comment
                                .getContentStatus() == STATUS_COLLAPSE) {
                            comment.setContentStatus(STATUS_COLLAPSE);
                            tvSuffixContent.setText(comment.getContent()
                                    .subSequence(l.getLineStart(3), l.getLineVisibleEnd(3)));
                        }
                        setContentStatus(comment);
                    }
                });
            }
        }

        @Override
        public void onClick(View v) {
            if (comment.getContentStatus() == STATUS_COLLAPSE) {
                comment.setContentStatus(STATUS_EXPAND);
            } else if (comment.getContentStatus() == STATUS_EXPAND) {
                comment.setContentStatus(STATUS_COLLAPSE);
            }
            setContentStatus(comment);
        }
    }

    private void setContentStatus(ServiceComment comment) {
        switch (comment.getContentStatus()) {
            case STATUS_NORMAL:
                tvPrefixContent.setVisibility(View.GONE);
                suffixContentLayout.setVisibility(View.GONE);
                tvContent.setVisibility(View.VISIBLE);
                tvCollapse.setVisibility(View.GONE);
                break;
            case STATUS_EXPAND:
                tvPrefixContent.setVisibility(View.GONE);
                suffixContentLayout.setVisibility(View.GONE);
                tvContent.setVisibility(View.VISIBLE);
                tvCollapse.setVisibility(View.VISIBLE);
                break;
            case STATUS_COLLAPSE:
                tvPrefixContent.setVisibility(View.VISIBLE);
                suffixContentLayout.setVisibility(View.VISIBLE);
                tvContent.setVisibility(View.GONE);
                tvCollapse.setVisibility(View.GONE);
                break;
            default:
                tvPrefixContent.setVisibility(View.VISIBLE);
                suffixContentLayout.setVisibility(View.GONE);
                tvContent.setVisibility(View.GONE);
                tvCollapse.setVisibility(View.GONE);
                break;
        }
    }
}