package com.hunliji.marrybiz.adapter.comment.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.CommonOrderInfo;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.view.chat.WSMerchantChatActivity;
import com.hunliji.marrybiz.view.comment.CommentDetailActivity;
import com.hunliji.marrybiz.widget.RatingView;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hua_rong on 2017/9/21
 * 评论管理
 */

public class CommentManagerViewHolder extends BaseViewHolder<ServiceComment> {

    @BindView(R.id.tv_user_nick)
    TextView tvUserNick;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_serve_time)
    TextView tvServeTime;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.rl_package_info)
    RelativeLayout rlPackageInfo;
    @BindView(R.id.tv_comment_content)
    TextView tvCommentContent;
    @BindView(R.id.tv_look_all)
    TextView tvLookAll;
    @BindView(R.id.rating)
    RatingView rating;
    @BindView(R.id.grade)
    TextView grade;
    @BindView(R.id.ll_rating)
    LinearLayout llRating;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_comment_content2)
    TextView tvCommentContent2;
    @BindView(R.id.ll_look_all)
    LinearLayout llLookAll;
    @BindView(R.id.line_layout)
    View lineLayout;
    @BindView(R.id.tv_watch_count)
    TextView tvWatchCount;
    @BindView(R.id.tv_like_count)
    TextView tvLikeCount;
    @BindView(R.id.tv_comment_count)
    TextView tvCommentCount;

    private String[] ratings;
    private ServiceComment comment;
    private Context context;
    private String content;

    public CommentManagerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        context = itemView.getContext();
        ratings = context.getResources()
                .getStringArray(R.array.rating);
    }

    @Override
    protected void setViewData(
            Context mContext, ServiceComment comment, int position, int viewType) {
        if (comment != null) {
            itemView.setVisibility(View.VISIBLE);
            this.comment = comment;
            tvTime.setText(comment.getCreatedAt()
                    .toString(Constants.DATE_FORMAT_SHORT));
            Author author = comment.getAuthor();
            if (author != null) {
                tvUserNick.setText(author.getName());
            }
            tvWatchCount.setText(context.getString(R.string.label_watch_counts,
                    comment.getWatchCount()));
            tvCommentCount.setText(context.getString(R.string.label_comment_counts,
                    comment.getCommentCount()));
            tvLikeCount.setText(context.getString(R.string.label_like_counts,
                    comment.getLikesCount()));
            Work work = comment.getWork();
            if (work != null && work.getId() > 0) {
                rlPackageInfo.setVisibility(View.VISIBLE);
                lineLayout.setVisibility(View.GONE);
                tvTitle.setText(work.getTitle());
                tvPrice.setText(context.getString(R.string.label_price4,
                        String.valueOf(Double.valueOf(work.getShowPrice())
                                .intValue())));
                CommonOrderInfo orderInfo = comment.getCommonOrderInfo();
                if (orderInfo != null) {
                    Merchant merchant = comment.getMerchant();
                    if (merchant != null) {
                        long property_id = merchant.getProperty_id();
                        if (property_id == 6) {//婚纱摄影 服务时间不显示
                            tvServeTime.setVisibility(View.GONE);
                        } else {
                            tvServeTime.setVisibility(View.VISIBLE);
                            DateTime dateTime = orderInfo.getWeddingTime();
                            String weddingTime = "";
                            if (dateTime != null) {
                                weddingTime = dateTime.toString(Constants.DATE_FORMAT_SHORT);
                            }
                            tvServeTime.setText(context.getString(R.string.label_serve_time2,
                                    weddingTime));
                        }
                    }
                }
                int width = CommonUtil.dp2px(context, 99);
                int height = CommonUtil.dp2px(context, 62);
                String avatarPath = ImageUtil.getImagePath2ForWxH(work.getCoverPath(),
                        width,
                        height);
                if (!TextUtils.isEmpty(avatarPath)) {
                    Glide.with(context)
                            .asBitmap()
                            .load(avatarPath)
                            .apply(new RequestOptions().dontAnimate()
                                    .placeholder(R.mipmap.icon_empty_image)
                                    .error(R.mipmap.icon_empty_image))
                            .into(imgCover);
                } else {
                    Glide.with(context)
                            .clear(imgCover);
                    imgCover.setImageBitmap(null);
                }
            } else {
                lineLayout.setVisibility(View.VISIBLE);
                rlPackageInfo.setVisibility(View.GONE);
            }

            //评价级别
            rating.setRating(comment.getRating());
            grade.setText(getGrade(comment.getRating()));

            content = comment.getContent();
            if (TextUtils.isEmpty(content)) {
                content = context.getString(R.string.label_un_comment);
            }
            tvContent.setText(content);
            tvCommentContent.setText(content);
            if (tvContent.getVisibility() == View.GONE) {
                tvContent.setVisibility(View.VISIBLE);
            }
            if (tvContent.getLayout() != null) {
                showView();
            }
            tvContent.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
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
                    tvContent.post(new Runnable() {
                        @Override
                        public void run() {
                            showView();
                        }
                    });
                }
            });
        }
    }

    private void showView() {
        int lineCount = tvContent.getLineCount();
        if (lineCount > 3) {
            Layout layout = tvContent.getLayout();
            tvCommentContent2.setText(content.substring(layout.getLineEnd(1), content.length()));
            tvContent.setVisibility(View.GONE);
            llLookAll.setVisibility(View.VISIBLE);
        } else {
            tvContent.setVisibility(View.VISIBLE);
            llLookAll.setVisibility(View.GONE);
        }
    }


    private String getGrade(int rating) {
        if (ratings != null) {
            if (rating < 1) {
                return "";
            } else if (rating > 5) {
                return ratings[4];
            }
            return ratings[rating - 1];
        }
        return "";
    }

    @OnClick({R.id.ll_detail, R.id.tv_user_nick, R.id.tv_reply_comment})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_detail:
                goCommentDetail(view, false);
                break;
            case R.id.tv_user_nick:
                if (comment != null) {
                    Context context = view.getContext();
                    Author author = comment.getAuthor();
                    Intent intent = new Intent(context, WSMerchantChatActivity.class);
                    CustomerUser user = new CustomerUser();
                    user.setAvatar(author.getAvatar());
                    user.setId(author.getId());
                    user.setNick(author.getName());
                    intent.putExtra("user", user);
                    context.startActivity(intent);
                    Activity activity = (Activity) context;
                    activity.overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
                break;
            case R.id.tv_reply_comment:
                goCommentDetail(view, true);
                break;
        }
    }

    private void goCommentDetail(View view, boolean isReplyComment) {
        if (comment != null) {
            Activity activity = (Activity) view.getContext();
            Intent intent = new Intent(activity, CommentDetailActivity.class);
            intent.putExtra("order_comment_id", comment.getId());
            intent.putExtra("is_reply_comment", isReplyComment);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }


}






