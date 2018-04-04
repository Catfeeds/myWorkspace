package me.suncloud.marrymemo.adpter.comment.viewholder;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.FlowLayout;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.UserProfileActivity;
import me.suncloud.marrymemo.view.WorkActivity;

/**
 * 服务订单评价详情headerViewHolder
 * Created by chen_bin on 2017/4/14 0014.
 */
public class ServiceCommentDetailHeaderViewHolder extends BaseServiceCommentViewHolder {
    @BindView(R.id.btn_menu)
    ImageButton btnMenu;
    @BindView(R.id.tv_work_title)
    TextView tvWorkTitle;
    @BindView(R.id.work_layout)
    LinearLayout workLayout;
    @BindView(R.id.images_layout)
    FlowLayout imagesLayout;
    @BindView(R.id.img_avatars_praised)
    ImageView imgAvatarsPraised;
    @BindView(R.id.praise_avatars_list_layout)
    LinearLayout praiseAvatarsListLayout;
    @BindView(R.id.tv_avatars_praised_count)
    TextView tvAvatarsPraisedCount;
    @BindView(R.id.praise_avatars_layout)
    LinearLayout praiseAvatarsLayout;
    @BindView(R.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R.id.tv_comment_empty_hint)
    TextView tvCommentEmptyHint;
    private int imageWidth;
    private OnMenuListener onMenuListener;

    public ServiceCommentDetailHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.imageWidth = CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px(
                itemView.getContext(),
                32);
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getItem() == null) {
                    return;
                }
                Author author = getItem().getAuthor();
                if (author != null && author.getId() > 0) {
                    Intent intent = new Intent(view.getContext(), UserProfileActivity.class);
                    intent.putExtra("id", author.getId());
                    view.getContext()
                            .startActivity(intent);
                }
            }
        });
        workLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getItem() == null) {
                    return;
                }
                Work work = getItem().getWork();
                if (work != null && work.getId() > 0) {
                    Intent intent = new Intent(view.getContext(), WorkActivity.class);
                    intent.putExtra("id", work.getId());
                    view.getContext()
                            .startActivity(intent);
                }
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onMenuListener != null) {
                    onMenuListener.onMenu(getAdapterPosition(), getItem());
                }
            }
        });
    }

    @Override
    public View trackerView() {
        return null;
    }

    @Override
    protected void setViewData(
            Context mContext, ServiceComment comment, int position, int viewType) {
        if (comment == null) {
            return;
        }
        super.setViewData(mContext, comment, position, viewType);
        setCommentType(mContext, comment);
        setPhotos(mContext, comment);
        addPraisedUsers(mContext, comment);
        setCommentCount(comment);
    }

    private void setCommentType(Context context, ServiceComment comment) {
        Work work = comment.getWork();
        if (work == null || work.getId() == 0) {
            workLayout.setVisibility(View.GONE);
        } else {
            workLayout.setVisibility(View.VISIBLE);
            tvWorkTitle.setText(context.getString(R.string.label_comment_type_work,
                    comment.getWork()
                            .getTitle()));
        }
    }

    private void setPhotos(Context context, final ServiceComment comment) {
        if (CommonUtil.isCollectionEmpty(comment.getPhotos())) {
            imagesLayout.setVisibility(View.GONE);
        } else {
            imagesLayout.setVisibility(View.VISIBLE);
            int count = imagesLayout.getChildCount();
            int size = comment.getPhotos()
                    .size();
            if (count > size) {
                imagesLayout.removeViews(size, count - size);
            }
            for (int i = 0; i < size; i++) {
                Photo photo = comment.getPhotos()
                        .get(i);
                View view = null;
                if (count > i) {
                    view = imagesLayout.getChildAt(i);
                }
                if (view == null) {
                    View.inflate(context, R.layout.image_item___cm, imagesLayout);
                    view = imagesLayout.getChildAt(imagesLayout.getChildCount() - 1);
                }
                ImageView imgCover = view.findViewById(R.id.image);
                int imageHeight = photo.getWidth() <= 0 ? photo.getWidth() : Math.round
                        (imageWidth * photo.getHeight() * 1.0f / photo.getWidth());
                imgCover.getLayoutParams().width = imageWidth;
                imgCover.getLayoutParams().height = imageHeight;
                Glide.with(context)
                        .load(ImagePath.buildPath(photo.getImagePath())
                                .width(imageWidth)
                                .height(imageHeight)
                                .cropPath())
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                                .error(R.mipmap.icon_empty_image))
                        .into(imgCover);
                final int position = i;
                imgCover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), PicsPageViewActivity.class);
                        intent.putExtra("position", position);
                        intent.putExtra("photos", comment.getPhotos());
                        view.getContext()
                                .startActivity(intent);
                    }
                });
            }
        }
    }

    public void setCommentCount(ServiceComment comment) {
        int commentCount = comment.getRepliedComments()
                .size();
        comment.setCommentCount(commentCount);
        tvCommentCount.setText(itemView.getContext()
                .getString(R.string.label_comment_count7, commentCount));
        tvCommentEmptyHint.setVisibility(commentCount <= 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setContent(ServiceComment comment) {
        if (TextUtils.isEmpty(comment.getContent())) {
            contentLayout.setVisibility(View.GONE);
        } else {
            contentLayout.setVisibility(View.VISIBLE);
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(comment.getContent());
        }
    }

    public void addPraisedUsers(Context context, final ServiceComment comment) {
        if (CommonUtil.isCollectionEmpty(comment.getPraisedUsers())) {
            praiseAvatarsLayout.setVisibility(View.GONE);
        } else {
            praiseAvatarsLayout.setVisibility(View.VISIBLE);
            int count = praiseAvatarsListLayout.getChildCount();
            int maxPraisedCount = getMaxPraisedCount(context, comment);
            if (maxPraisedCount >= comment.getPraisedUsers()
                    .size()) {
                tvAvatarsPraisedCount.setVisibility(View.GONE);
            } else {
                tvAvatarsPraisedCount.setVisibility(View.VISIBLE);
                tvAvatarsPraisedCount.setText(context.getString(R.string.label_merge_count,
                        comment.getLikesCount()));
                comment.setPraisedUsers(new ArrayList<>(comment.getPraisedUsers()
                        .subList(0, maxPraisedCount)));
            }
            if (count > maxPraisedCount) {
                praiseAvatarsListLayout.removeViews(maxPraisedCount, count - maxPraisedCount);
            }
            for (int i = 0; i < maxPraisedCount; i++) {
                View view = null;
                final Author author = comment.getPraisedUsers()
                        .get(i);
                if (count > i) {
                    view = praiseAvatarsListLayout.getChildAt(i);
                }
                if (view == null) {
                    View.inflate(context,
                            R.layout.service_comment_praised_list_item,
                            praiseAvatarsListLayout);
                    view = praiseAvatarsListLayout.getChildAt(praiseAvatarsListLayout
                            .getChildCount() - 1);
                }
                RoundedImageView imgAvatar = view.findViewById(R.id.img_avatar);
                Glide.with(context)
                        .load(ImagePath.buildPath(author.getAvatar())
                                .width(CommonUtil.dp2px(itemView.getContext(), 28))
                                .cropPath())
                        .apply(new RequestOptions().dontAnimate()
                                .placeholder(R.mipmap.icon_avatar_primary)
                                .error(R.mipmap.icon_avatar_primary))
                        .into(imgAvatar);
            }
        }
    }

    public void removePraisedUser(Context context, ServiceComment comment, Author author) {
        if (!CommonUtil.isCollectionEmpty(comment.getPraisedUsers())) {
            for (int i = 0, size = comment.getPraisedUsers()
                    .size(); i < size; i++) {
                Author user = comment.getPraisedUsers()
                        .get(i);
                if (user.getId() == author.getId()) {
                    comment.getPraisedUsers()
                            .remove(i);
                    addPraisedUsers(context, comment);
                    break;
                }
            }
        }
    }

    private int getMaxPraisedCount(Context context, ServiceComment comment) {
        int screenWidth = CommonUtil.getDeviceSize(context).x;
        int itemWidth = CommonUtil.dp2px(context, 33);
        int offset = CommonUtil.dp2px(context, 62);
        int maxPraisedCount = (screenWidth - offset) / itemWidth;
        if (maxPraisedCount >= comment.getPraisedUsers()
                .size()) {
            maxPraisedCount = comment.getPraisedUsers()
                    .size();
        } else {
            offset = offset + CommonUtil.dp2px(context, 10) + CommonUtil.sp2px(context,
                    13 * (2 + String.valueOf(comment.getLikesCount())
                            .length()));
            maxPraisedCount = (screenWidth - offset) / itemWidth;
        }
        return maxPraisedCount;
    }

    public void setShowMenu(boolean showMenu) {
        btnMenu.setVisibility(showMenu ? View.VISIBLE : View.GONE);
    }

    public void setOnMenuListener(OnMenuListener onMenuListener) {
        this.onMenuListener = onMenuListener;
    }

    public interface OnMenuListener {
        void onMenu(int position, ServiceComment comment);
    }

}