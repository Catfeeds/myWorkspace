package com.hunliji.marrybiz.adapter.comment.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.CommonOrderInfo;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljGridView;
import com.hunliji.hljcommonviewlibrary.utils.FixedColumnGridInterface;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.view.chat.WSMerchantChatActivity;
import com.hunliji.marrybiz.widget.RatingView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hua_rong on 2017/6/13.
 * 评论详情--header
 */

public class CommentDetailHeaderViewHolder {

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
    @BindView(R.id.tv_user_nick)
    TextView tvUserNick;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.hgv_view)
    HljGridView hgvView;
    @BindView(R.id.tv_comment_content)
    TextView tvCommentContent;
    @BindView(R.id.rating)
    RatingView rating;
    @BindView(R.id.grade)
    TextView grade;
    @BindView(R.id.ll_header)
    LinearLayout llHeader;
    @BindView(R.id.iv_bottom)
    ImageView ivBottom;
    @BindView(R.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R.id.tv_all_comment)
    TextView tvAllCount;
    @BindView(R.id.tv_watch_count)
    TextView tvWatchCount;
    @BindView(R.id.tv_like_count)
    TextView tvLikeCount;
    @BindView(R.id.line_layout)
    View lineLayout;

    private Context context;
    private String[] ratings;
    private ServiceComment comment;

    public CommentDetailHeaderViewHolder(View view) {
        ButterKnife.bind(this, view);
        context = view.getContext();
        ratings = context.getResources()
                .getStringArray(R.array.rating);
        hgvView.setGridInterface(new FixedColumnGridInterface(CommonUtil.dp2px(context, 4)));
    }

    @OnClick({R.id.tv_user_nick})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_user_nick:
                if (comment != null) {
                    Author author = comment.getAuthor();
                    if (author != null) {
                        intent = new Intent(view.getContext(), WSMerchantChatActivity.class);
                        CustomerUser user = new CustomerUser();
                        user.setAvatar(author.getAvatar());
                        user.setId(author.getId());
                        user.setNick(author.getName());
                        intent.putExtra("user", user);
                        view.getContext()
                                .startActivity(intent);
                        Activity activity = (Activity) view.getContext();
                        activity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
                break;
        }
    }


    public void showOrHideHeaderBottom(List<RepliedComment> replyList) {
        if (replyList == null || replyList.isEmpty()) {
            ivBottom.setVisibility(View.GONE);
            tvAllCount.setVisibility(View.GONE);
            lineLayout.setVisibility(View.GONE);
        } else {
            ivBottom.setVisibility(View.GONE);
            tvAllCount.setVisibility(View.VISIBLE);
            lineLayout.setVisibility(View.VISIBLE);
            tvAllCount.setText(context.getString(R.string.label_all_comment, replyList.size()));
        }
        if (replyList != null) {
            tvCommentCount.setText(context.getString(R.string.label_comment_counts,
                    replyList.size()));
        }
    }

    public void setHeaderView(ServiceComment comment) {
        if (comment != null) {
            this.comment = comment;
            llHeader.setVisibility(View.VISIBLE);
            tvWatchCount.setText(context.getString(R.string.label_watch_counts,
                    comment.getWatchCount()));
            tvCommentCount.setText(context.getString(R.string.label_comment_counts,
                    comment.getCommentCount()));
            tvLikeCount.setText(context.getString(R.string.label_like_counts,
                    comment.getLikesCount()));
            Work work = comment.getWork();
            if (work != null && work.getId() > 0) {
                rlPackageInfo.setVisibility(View.VISIBLE);
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
                            if (dateTime != null) {
                                String weddingTime = dateTime.toString(Constants.DATE_FORMAT_SHORT);
                                tvServeTime.setText(context.getString(R.string.label_serve_time2,
                                        weddingTime));
                            }
                        }
                    }
                }
                int width = CommonUtil.dp2px(context, 99);
                int height = CommonUtil.dp2px(context, 62);
                String avatarPath = ImagePath.buildPath(work.getCoverPath())
                        .width(width)
                        .height(height)
                        .cropPath();
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
                }
            } else {
                rlPackageInfo.setVisibility(View.GONE);
            }
            DateTime dateTime = comment.getCreatedAt();
            if (dateTime != null) {
                tvTime.setText(dateTime.toString(Constants.DATE_FORMAT_SHORT));
            }
            Author author = comment.getAuthor();
            if (author != null) {
                tvUserNick.setText(author.getName());
            }
            tvCommentContent.setText(comment.getContent());
            //评价级别
            rating.setRating(comment.getRating());
            grade.setText(getGrade(comment.getRating()));

            //九宫格图片
            final ArrayList<Photo> photoList = comment.getPhotos();
            if (photoList != null && !photoList.isEmpty()) {
                hgvView.setVisibility(View.VISIBLE);
                hgvView.setDate(photoList);
                hgvView.setItemClickListener(new HljGridView.GridItemClickListener() {
                    @Override
                    public void onItemClick(View itemView, int position) {
                        Intent intent = new Intent(context, PicsPageViewActivity.class);
                        intent.putExtra("position", position);
                        intent.putParcelableArrayListExtra("photos", photoList);
                        context.startActivity(intent);
                        Activity activity = (Activity) context;
                        activity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
            } else {
                hgvView.setVisibility(View.GONE);
            }
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

}
