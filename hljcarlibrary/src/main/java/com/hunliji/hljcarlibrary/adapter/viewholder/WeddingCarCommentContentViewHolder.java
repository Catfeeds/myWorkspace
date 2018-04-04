package com.hunliji.hljcarlibrary.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarComment;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.widgets.HljGridView;
import com.hunliji.hljcommonviewlibrary.utils.FixedColumnGridInterface;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jinxin on 2017/12/28 0028.
 */

public class WeddingCarCommentContentViewHolder extends BaseViewHolder<WeddingCarComment> {

    private final static int STATUS_INIT = 0;
    private final static int STATUS_NORMAL = 1;
    private final static int STATUS_EXPAND = 2;
    private final static int STATUS_COLLAPSE = 3;

    @BindView(R2.id.img_avatar)
    RoundedImageView imgAvatar;
    @BindView(R2.id.tv_nick)
    TextView tvNick;
    @BindView(R2.id.btn_menu)
    ImageButton btnMenu;
    @BindView(R2.id.tv_time)
    TextView tvTime;
    @BindView(R2.id.tv_rating_grade)
    TextView tvRatingGrade;
    @BindView(R2.id.tv_bought_des)
    TextView tvBoughtDes;
    @BindView(R2.id.tv_prefix_content)
    TextView tvPrefixContent;
    @BindView(R2.id.tv_suffix_content)
    TextView tvSuffixContent;
    @BindView(R2.id.tv_expand)
    TextView tvExpand;
    @BindView(R2.id.suffix_content_layout)
    LinearLayout suffixContentLayout;
    @BindView(R2.id.tv_content)
    TextView tvContent;
    @BindView(R2.id.tv_collapse)
    TextView tvCollapse;
    @BindView(R2.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R2.id.images_layout)
    HljGridView imagesLayout;
    @BindView(R2.id.layout_divider)
    LinearLayout layoutDivider;
    @BindView(R2.id.divider_line)
    View dividerLine;

    private int logoSize;
    private ContentLayoutChangeListener listener;
    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private boolean needToPicsActivity = false;

    public WeddingCarCommentContentViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        mContext = itemView.getContext();
        logoSize = CommonUtil.dp2px(itemView.getContext(), 40);
        imagesLayout.setGridInterface(new FixedColumnGridInterface(CommonUtil.dp2px(itemView
                        .getContext(),
                2)));
        imagesLayout.setItemClickListener(new HljGridView.GridItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (needToPicsActivity) {
                    itemView.performClick();
                    return;
                }
                WeddingCarComment comment = getItem();
                if (comment != null && !CommonUtil.isCollectionEmpty(comment.getPhotos())) {
                    Intent intent = new Intent(mContext, PicsPageViewActivity.class);
                    intent.putExtra("photos", comment.getPhotos());
                    intent.putExtra("position", position);
                    mContext.startActivity(intent);
                }
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                }
            }
        });
    }

    public void setNeedToPicsActivity(boolean needToPicsActivity) {
        this.needToPicsActivity = needToPicsActivity;
    }

    @Override
    protected void setViewData(
            final Context mContext, final WeddingCarComment carComment,
            final int position,
            int viewType) {
        HljVTTagger.buildTagger(tvExpand)
                .tagName("merchant_comment_see_full")
                .dataType("MerchantComment")
                .dataId(carComment.getId())
                .hitTag();
        HljVTTagger.buildTagger(tvCollapse)
                .tagName("merchant_comment_see_full")
                .dataType("MerchantCommentTag")
                .dataId(carComment.getId())
                .hitTag();

        Glide.with(mContext)
                .load(ImagePath.buildPath(carComment.getAuthor()
                        .getAvatar())
                        .width(logoSize)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_avatar_primary)
                        .error(R.mipmap.icon_avatar_primary))
                .into(imgAvatar);
        tvNick.setText(carComment.getAuthor()
                .getName());
        if (carComment.getCreatedAt() == null) {
            tvTime.setVisibility(View.GONE);
        } else {
            tvTime.setVisibility(View.VISIBLE);
            tvTime.setText(carComment.getCreatedAt()
                    .toString(HljCommon.DateFormat.DATE_FORMAT_SHORT));
        }
        tvRatingGrade.setText(mContext.getResources()
                .getString(R.string.label_rating___car, String.valueOf(carComment.getRating())));
        String knowTypeStr = carComment.getGrade(carComment.getRating());
        if (TextUtils.isEmpty(knowTypeStr)) {
            tvBoughtDes.setVisibility(View.GONE);
        } else {
            tvBoughtDes.setVisibility(View.VISIBLE);
            tvBoughtDes.setText(knowTypeStr);
        }

        setContent(carComment);
        setPhotos(carComment);
    }

    public void setPhotos(WeddingCarComment comment) {
        if (!CommonUtil.isCollectionEmpty(comment.getPhotos()) && comment.getPhotos()
                .size() > 3) {
            comment.setPhotos(new ArrayList<>(comment.getPhotos()
                    .subList(0, 3)));
        }
        if (CommonUtil.isCollectionEmpty(comment.getPhotos())) {
            imagesLayout.setVisibility(View.GONE);
        } else {
            imagesLayout.setVisibility(View.VISIBLE);
            imagesLayout.setDate(comment.getPhotos());
        }
    }

    public void setContent(WeddingCarComment comment) {
        if (TextUtils.isEmpty(comment.getContent())) {
            contentLayout.setVisibility(View.GONE);
        } else {
            contentLayout.setVisibility(View.VISIBLE);
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setMaxLines(3);
            tvContent.setEllipsize(TextUtils.TruncateAt.END);
            tvContent.setText(comment.getContent());
            if (listener != null) {
                tvContent.removeOnLayoutChangeListener(listener);
            }
            listener = new ContentLayoutChangeListener(comment);
            tvContent.addOnLayoutChangeListener(listener);
        }
    }

    public void setDividerLineVisible(boolean show) {
        dividerLine.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private class ContentLayoutChangeListener implements View.OnLayoutChangeListener, View
            .OnClickListener {

        private WeddingCarComment comment;

        public ContentLayoutChangeListener(final WeddingCarComment comment) {
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

    private void setContentStatus(WeddingCarComment comment) {
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
