package me.suncloud.marrymemo.viewholder.experienceshop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.Comment;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljGridView;
import com.hunliji.hljcommonviewlibrary.utils.FixedColumnGridInterface;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.UserProfileActivity;
import me.suncloud.marrymemo.widget.RatingView;

/**
 * Created by hua_rong on 2017/3/30.
 * 体验店评论holder
 */

public class CommentViewHolder extends BaseViewHolder<Comment> {


    @BindView(R.id.user_icon)
    RoundedImageView userIcon;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.rating)
    RatingView rating;
    @BindView(R.id.grade)
    TextView grade;
    @BindView(R.id.rating_layout)
    LinearLayout ratingLayout;
    @BindView(R.id.user_info_layout)
    LinearLayout userInfoLayout;
    @BindView(R.id.content2)
    TextView content2;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.look_all)
    TextView lookAll;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.images_layout)
    HljGridView imagesLayout;
    @BindView(R.id.user_line)
    View userLine;

    private List<Comment> commentList;
    private String[] ratings;

    public CommentViewHolder(View itemView, List<Comment> commentList) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.commentList = commentList;
        Context context = itemView.getContext();
        imagesLayout.setGridInterface(new FixedColumnGridInterface(CommonUtil.dp2px(itemView
                        .getContext(),
                2)));
        ratings = context.getResources()
                .getStringArray(R.array.rating);
    }

    @Override
    protected void setViewData(
            final Context context, final Comment comment, int position, int viewType) {
        itemView.setVisibility(View.VISIBLE);
        if (comment != null) {
            final Author author = comment.getAuthor();
            int width = CommonUtil.dp2px(context, 40);
            if (author != null) {
                String avatarPath = ImageUtil.getImagePath(author.getAvatar(), width);
                if (!TextUtils.isEmpty(avatarPath)) {
                    Glide.with(context)
                            .asBitmap()
                            .load(avatarPath)
                            .apply(new RequestOptions().dontAnimate()
                                    .placeholder(R.mipmap.icon_avatar_primary)
                                    .error(R.mipmap.icon_avatar_primary))
                            .into(userIcon);
                } else {
                    Glide.with(context)
                            .clear(userIcon);
                }
                userName.setText(author.getName());
                userIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, UserProfileActivity.class);
                        intent.putExtra("id", author.getId());
                        context.startActivity(intent);
                        Activity activity = (Activity) context;
                        activity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
            }

            //评论时间
            if (null != comment.getCreatedAt()) {
                time.setText(comment.getCreatedAt()
                        .toString(Constants.DATE_FORMAT_SHORT));
            }

            if (comment.isExpanded()) {
                content2.setVisibility(View.VISIBLE);
                content.setVisibility(View.GONE);
                lookAll.setText(R.string.label_pack_info);
            } else {
                content2.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                lookAll.setText(R.string.label_all_info1);
            }

            //评论内容
            String commentContent = comment.getContent();
            if (!TextUtils.isEmpty(commentContent)) {
                content2.setText(commentContent.trim());
                content.setText(commentContent.trim());
            }
            ContentLayoutChangeListener listener = new ContentLayoutChangeListener(content,
                    content2,
                    lookAll,
                    comment);
            content.addOnLayoutChangeListener(listener);
            contentLayout.setOnClickListener(listener);
            //评价级别
            rating.setVisibility(View.VISIBLE);

            rating.setRating(comment.getRating());
            grade.setTextColor(ContextCompat.getColor(context, R.color.colorBlack3));
            grade.setText(getGrade(comment.getRating()));
            if (commentList != null) {
                int count = commentList.size();
                if (count > 0) {
                    if (position == count - 1) {
                        userLine.setVisibility(View.GONE);
                    } else {
                        userLine.setVisibility(View.VISIBLE);
                    }
                }
            }
            //九宫格图片
            final List<Photo> photoList = comment.getPhotos();
            if (photoList != null && !photoList.isEmpty()) {
                imagesLayout.setVisibility(View.VISIBLE);
                imagesLayout.setDate(photoList);//九宫格model
                imagesLayout.setItemClickListener(new HljGridView.GridItemClickListener() {
                    @Override
                    public void onItemClick(View itemView, int position) {
                        Intent intent = new Intent(context, PicsPageViewActivity.class);
                        intent.putExtra("position", position);
                        intent.putParcelableArrayListExtra("photos",
                                (ArrayList<? extends Parcelable>) photoList);
                        context.startActivity(intent);
                        Activity activity = (Activity) context;
                        activity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
            } else {
                imagesLayout.setVisibility(View.GONE);
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

    private class ContentLayoutChangeListener implements View.OnLayoutChangeListener, View
            .OnClickListener {
        private Comment comment;
        private TextView content;
        private TextView content2;
        private TextView arrow;
        private static final int MAX_LINE = 4;

        ContentLayoutChangeListener(
                TextView content, TextView content2, TextView arrow, Comment comment) {
            this.content = content;
            this.content2 = content2;
            this.arrow = arrow;
            this.comment = comment;
            arrow.setText(content2.getVisibility() == View.VISIBLE ? R.string.label_pack_info : R
                    .string.label_all_info1);
            Layout l = content.getLayout();
            if (l != null) {
                arrow.setVisibility(isEllipsis(l) ? View.VISIBLE : View.GONE);
            } else if (content2.getLayout() != null) {
                arrow.setVisibility(content2.getLayout()
                        .getLineCount() > MAX_LINE ? View.VISIBLE : View.GONE);
            }
        }

        @Override
        public void onLayoutChange(
                View v,
                int left,
                int top,
                int right,
                int bottom,
                int oldLeft,
                int oldTop,
                int oldRight,
                int oldBottom) {
            final Layout l = content.getLayout();
            if (l != null) {
                content.post(new Runnable() {
                    @Override
                    public void run() {
                        arrow.setVisibility(isEllipsis(l) ? View.VISIBLE : View.GONE);
                    }
                });
            } else if (content2.getLayout() != null) {
                content2.post(new Runnable() {
                    @Override
                    public void run() {
                        arrow.setVisibility(content2.getLayout()
                                .getLineCount() > MAX_LINE ? View.VISIBLE : View.GONE);
                    }
                });
            }
        }

        private boolean isEllipsis(Layout l) {
            int lines = l.getLineCount();
            if (lines > MAX_LINE) {
                return true;
            } else if (lines > 1) {
                for (int i = lines; i > 0; i--) {
                    if (l.getEllipsisCount(i - 1) > 0) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            Layout l = content.getLayout();
            Layout l2 = content2.getLayout();
            if ((l == null || !isEllipsis(l)) && (l2 == null || l2.getLineCount() <= MAX_LINE)) {
                return;
            }
            if (content2.getVisibility() != View.VISIBLE) {
                content2.setVisibility(View.VISIBLE);
                content.setVisibility(View.GONE);
                arrow.setText(R.string.label_pack_info);
            } else {
                content2.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                arrow.setText(R.string.label_all_info1);
            }
            comment.setExpanded(!comment.isExpanded());
        }
    }

}
