package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityAuthor;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThreadPages;
import com.hunliji.hljcommonlibrary.models.communitythreads.WeddingPhotoContent;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker
        .TrackerRecommendThreadViewHolder;
import com.hunliji.hljcommonviewlibrary.models.CommunityFeed;
import com.hunliji.hljcommonviewlibrary.utils.CommunityThreadMeasures;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 16/12/15.热门话题列表
 */

@Deprecated
// TODO: 2018/3/28 wangtao 删除
public class RecommendThreadViewHolder extends TrackerRecommendThreadViewHolder {
    @BindView(R2.id.riv_auth_avatar)
    RoundedImageView rivAuthAvatar;
    @BindView(R2.id.iv_auth_vip)
    ImageView ivAuthVip;
    @BindView(R2.id.tv_auth_name)
    TextView tvAuthName;
    @BindView(R2.id.tv_thread_time)
    TextView tvThreadTime;
    @BindView(R2.id.thread_auth_view)
    LinearLayout threadAuthView;
    @BindView(R2.id.tv_thread_title)
    TextView tvThreadTitle;
    @BindView(R2.id.tv_thread_content)
    TextView tvThreadContent;
    @BindView(R2.id.iv_thread_right)
    ImageView ivThreadRight;
    @BindView(R2.id.iv_thread_1)
    ImageView ivThread1;
    @BindView(R2.id.iv_thread_2)
    ImageView ivThread2;
    @BindView(R2.id.iv_thread_3)
    ImageView ivThread3;
    @BindView(R2.id.thread_img_view)
    LinearLayout threadImgView;
    @BindView(R2.id.tv_thread_img_count)
    TextView tvThreadImgCount;
    @BindView(R2.id.thread_img_count_view)
    LinearLayout threadImgCountView;
    @BindView(R2.id.thread_cover_view)
    RelativeLayout threadCoverView;
    @BindView(R2.id.iv_single_thread_img)
    ImageView ivSingleThreadImg;
    @BindView(R2.id.tv_channel_name)
    TextView tvChannelName;
    @BindView(R2.id.channel_view)
    LinearLayout channelView;
    @BindView(R2.id.img_thumb_up)
    ImageView imgThumbUp;
    @BindView(R2.id.check_praised)
    CheckableLinearButton checkPraised;
    @BindView(R2.id.tv_praise_count)
    TextView tvPraiseCount;
    @BindView(R2.id.tv_praise_add)
    TextView tvPraiseAdd;
    @BindView(R2.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R2.id.comment_view)
    LinearLayout commentView;
    @BindView(R2.id.bottom_thread_view)
    RelativeLayout bottomThreadView;
    @BindView(R2.id.community_thread_view)
    LinearLayout communityThreadView;
    @BindView(R2.id.bottom_layout)
    View bottomLayout;
    @BindView(R2.id.ll_wedding_dress)
    LinearLayout llWeddingDress;
    @BindView(R2.id.rl_wedding_image)
    RelativeLayout rlWeddingImage;
    @BindView(R2.id.iv_wedding_dress)
    ImageView ivWeddingDress;
    @BindView(R2.id.tv_wedding_title)
    TextView tvWeddingTitle;
    @BindView(R2.id.tv_wedding_price)
    TextView tvWeddingPrice;
    @BindView(R2.id.tv_wedding_image_count)
    TextView tvWeddingImageCount;
    @BindView(R2.id.ll_thread_title)
    LinearLayout llThreadTitle;

    private OnItemClickListener onItemClickListener;
    private OnPraiseClickListener onPraiseClickListener;
    private OnReplyClickListener onReplyClickListener;
    private CommunityThreadMeasures measures;
    private int weddingImageWidth;
    private int weddingImageHeight;

    public RecommendThreadViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        Context context = itemView.getContext();
        Point point = CommonUtil.getDeviceSize(context);
        measures = new CommunityThreadMeasures(context.getResources()
                .getDisplayMetrics(), point);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) ivSingleThreadImg
                .getLayoutParams();
        params.height = measures.singleImgHeight;
        params.width = measures.singleImgWidth;
        weddingImageWidth = point.x - CommonUtil.dp2px(context, 32);
        weddingImageHeight = weddingImageWidth * 9 / 16;
        rlWeddingImage.getLayoutParams().width = weddingImageWidth;
        rlWeddingImage.getLayoutParams().height = weddingImageHeight;

    }

    @Override
    public View trackerView() {
        return communityThreadView;
    }

    @Override
    protected void setViewData(
            final Context mContext,
            final CommunityFeed recommendThread,
            final int position,
            int viewType) {
        final Activity activity = (Activity) mContext;
        final CommunityThread item = (CommunityThread) recommendThread.getEntity();

        threadAuthView.setVisibility(View.VISIBLE);
        tvChannelName.setVisibility(View.VISIBLE);
        setAuthView(activity, item.getAuthor());
        final WeddingPhotoContent weddingPhotoContent = item.getWeddingPhotoContent();
        //话题所属频道
        onChannel(item, activity, mContext);
        if (weddingPhotoContent != null && weddingPhotoContent.getId() != 0) {
            //晒婚纱照
            llThreadTitle.setVisibility(View.GONE);
            ivSingleThreadImg.setVisibility(View.GONE);
            threadCoverView.setVisibility(View.GONE);
            llWeddingDress.setVisibility(View.VISIBLE);
            showWeddingItem(item, weddingPhotoContent, mContext);
        } else {
            //pages字段 为null时 话题不是富文本话题
            llThreadTitle.setVisibility(View.VISIBLE);
            llWeddingDress.setVisibility(View.GONE);
            showRichPage(recommendThread, item, mContext);
        }
        //去掉“新娘说-社区-每日热帖”中的话题列表项中的时间标签
        tvThreadTime.setVisibility(View.GONE);
        //点赞
        onPraise(item);
        //回帖
        onReplyPosts(item, position, recommendThread);
        bottomLayout.setVisibility(View.VISIBLE);
        communityThreadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, recommendThread);
                }
            }
        });
    }

    /**
     * 晒婚纱照item
     *
     * @param item
     * @param weddingPhotoContent
     * @param mContext
     */
    private void showWeddingItem(
            CommunityThread item, WeddingPhotoContent weddingPhotoContent, Context mContext) {
        //话题标题
        showThreadTitle(tvWeddingTitle, item.getTitle(), item, mContext);
        String cityName = weddingPhotoContent.getCity();
        int price = Double.valueOf(weddingPhotoContent.getPrice())
                .intValue();
        String text;
        if (TextUtils.isEmpty(cityName) && price > 0) {
            text = String.format("￥%s", String.valueOf(price));
        } else if (!TextUtils.isEmpty(cityName) && price == 0) {
            text = cityName;
        } else if (!TextUtils.isEmpty(cityName) && price > 0) {
            text = String.format("%s · ￥%s", cityName, String.valueOf(price));
        } else {
            text = "";
        }
        tvWeddingPrice.setText(text);
        int imageCount = item.getPicsCount();
        tvWeddingImageCount.setText(String.format("%s张图", String.valueOf(imageCount)));
        Photo photo = weddingPhotoContent.getPhoto();
        String imagePath = photo.getImagePath();
        String imgUrl = ImagePath.buildPath(imagePath)
                .width(weddingImageWidth)
                .height(weddingImageHeight)
                .path();
        if (!TextUtils.isEmpty(imgUrl)) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(ivWeddingDress);
        } else {
            Glide.with(mContext)
                    .clear(ivWeddingDress);
            ivWeddingDress.setImageBitmap(null);
        }
    }

    /**
     * 回帖
     */
    private void onReplyPosts(
            final CommunityThread item, final int position, final CommunityFeed recommendThread) {
        tvCommentCount.setText(item.getPostCount() == 0 ? "回复" : String.valueOf(item.getPostCount
                ()));
        //回复数为0时，点击回复跳转到回复页面
        commentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.getPostCount() == 0) {
                    if (onReplyClickListener != null) {
                        onReplyClickListener.onReply(item, position);
                    }
                } else {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position, recommendThread);
                    }
                }
            }
        });
    }

    /**
     * 点赞
     *
     * @param item
     */
    private void onPraise(final CommunityThread item) {
        tvPraiseCount.setText(item.getPraisedSum() == 0 ? "赞" : String.valueOf(item.getPraisedSum
                ()));
        //替换点赞动画
        checkPraised.setChecked(item.isPraised());
        checkPraised.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPraiseClickListener != null) {
                    onPraiseClickListener.onPraiseClick(item,
                            checkPraised,
                            imgThumbUp,
                            tvPraiseCount,
                            tvPraiseAdd);
                }
            }
        });
    }

    /**
     * 话题所属频道
     */
    private void onChannel(
            final CommunityThread item, final Activity activity, final Context mContext) {
        if (item.getChannel() != null) {
            tvChannelName.setText(Html.fromHtml(mContext.getString(R.string
                            .label_thread_channel___cm,
                    item.getChannel()
                            .getTitle())));
            channelView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommunityChannel channel = item.getChannel();
                    ARouter.getInstance()
                            .build(RouterPath.IntentPath.Customer.COMMUNITY_CHANNEL)
                            .withLong("id", channel.getId())
                            .navigation(activity);
                }
            });
        }
    }


    /**
     * pages字段 为null时 话题不是富文本话题
     */
    private void showRichPage(
            CommunityFeed recommendThread, CommunityThread item, Context mContext) {
        //pages字段 为null时 话题不是富文本话题
        String title;
        String content;
        ArrayList<Photo> photos = null;
        if (item.getPages() == null) {
            title = item.getTitle();
            content = item.getPost()
                    .getMessage();

            photos = item.getPost()
                    .getPhotos();
            setPhotoView(mContext, photos, title, content, recommendThread.isShowThree());
        } else {
            CommunityThreadPages threadPages = item.getPages();
            title = threadPages.getTitle();
            content = threadPages.getSubTitle();
            setRichThreadView(mContext, threadPages);
        }
        //话题标志
        showThreadTitle(tvThreadTitle, title, item, mContext);
        //当标题和导读都超过两行时，减小间距
        reducePitch(photos, item, mContext, recommendThread);
        // 当为三图（多图）和精选话题时，话题导读最多显示2行
        // 当为单图或无图话题时，话题导读最多显示3行
        showThreadMaxLine(photos, item, content, mContext, recommendThread);
    }

    /**
     * // 当为三图（多图）和精选话题时，话题导读最多显示2行
     * // 当为单图或无图话题时，话题导读最多显示3行
     */
    private void showThreadMaxLine(
            ArrayList<Photo> photos,
            CommunityThread item,
            String content,
            Context mContext,
            CommunityFeed recommendThread) {
        // 当为三图（多图）和精选话题时，话题导读最多显示2行
        // 当为单图或无图话题时，话题导读最多显示3行
        if ((photos != null && photos.size() >= 3 && recommendThread.isShowThree()) || item
                .getPages() != null) {
            tvThreadContent.setMaxLines(2);
        } else {
            tvThreadContent.setMaxLines(3);
        }
        if (!TextUtils.isEmpty(content)) {
            tvThreadContent.setVisibility(View.VISIBLE);
            tvThreadContent.setText(EmojiUtil.parseEmojiByText2(mContext,
                    content,
                    measures.faceSize));
        } else {
            tvThreadContent.setVisibility(View.GONE);
        }
    }

    /**
     * //当标题和导读都超过两行时，减小间距
     */
    private void reducePitch(
            ArrayList<Photo> photos,
            CommunityThread item,
            Context mContext,
            CommunityFeed recommendThread) {
        //当标题和导读都超过两行时，减小间距
        if ((photos != null && photos.size() >= 3 && recommendThread.isShowThree()) || (item
                .getPages() != null && item.getPages()
                .getImgPath() != null)) {
            ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) bottomThreadView
                    .getLayoutParams();
            param.topMargin = CommonUtil.dp2px(mContext, 10);
        } else {
            ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) bottomThreadView
                    .getLayoutParams();
            param.topMargin = CommonUtil.dp2px(mContext, 4);
        }
    }


    /**
     * 话题标志
     */
    private void showThreadTitle(
            TextView tvThreadTitle, String title, CommunityThread item, Context mContext) {
        //话题标志
        if (TextUtils.isEmpty(title)) {
            tvThreadTitle.setVisibility(View.GONE);
        } else {
            if (item.isRefined() || item.getPages() != null) {
                SpannableStringBuilder builder = EmojiUtil.parseEmojiByText2(mContext,
                        "  " + title,
                        measures.faceSize);
                if (builder != null) {
                    Drawable drawable;
                    if (item.isRefined()) {
                        drawable = ContextCompat.getDrawable(mContext,
                                R.mipmap.icon_refined_tag_32_32);
                    } else {
                        drawable = ContextCompat.getDrawable(mContext,
                                R.mipmap.icon_recommend_tag_32_32);
                    }
                    drawable.setBounds(0,
                            0,
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight());
                    builder.setSpan(new HljImageSpan(drawable),
                            0,
                            1,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                tvThreadTitle.setText(builder);
            } else {
                tvThreadTitle.setText(EmojiUtil.parseEmojiByText2(mContext,
                        title,
                        measures.faceSize));
            }
        }
    }

    private void setAuthView(final Activity context, final CommunityAuthor author) {
        String avatarUrl = ImageUtil.getImagePath(author.getAvatar(),
                CommonUtil.dp2px(context, 36));
        Glide.with(context)
                .load(avatarUrl)
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_avatar_primary))
                .into(rivAuthAvatar);
        tvAuthName.setText(author.getName());
        rivAuthAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ARouter.getInstance()
                        .build(RouterPath.IntentPath.Customer.USER_PROFILE)
                        .withLong("id", author.getId())
                        .navigation(context);
            }
        });
    }

    /**
     * 精编话题
     */
    private void setRichThreadView(Context context, CommunityThreadPages threadPages) {
        //富文本帖子图片
        threadCoverView.setVisibility(View.GONE);
        ivThreadRight.setVisibility(View.GONE);
        ivSingleThreadImg.setVisibility(View.VISIBLE);
        String imgUrl = ImageUtil.getImagePath(threadPages.getImgPath(), measures.singleImgWidth);
        if (!TextUtils.isEmpty(imgUrl)) {
            Glide.with(context)
                    .load(imgUrl)
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(ivSingleThreadImg);
        } else {
            Glide.with(context)
                    .clear(ivSingleThreadImg);
            ivSingleThreadImg.setVisibility(View.GONE);
        }
    }

    /**
     * 设置话题图片
     */
    private void setPhotoView(
            Context context, ArrayList<Photo> photos, String title, String content, boolean show) {
        int size = 0;
        if (photos != null) {
            size = photos.size();//图片数量
        }
        ArrayList<ImageView> imgLists = new ArrayList<>(Arrays.asList(ivThread1,
                ivThread2,
                ivThread3));
        ivSingleThreadImg.setVisibility(View.GONE);
        //社区频道页不显示三图样式
        if (size >= 3 && show) {
            //三图样式时
            threadCoverView.setVisibility(View.VISIBLE);
            ivThreadRight.setVisibility(View.GONE);
            threadImgCountView.setVisibility(View.GONE);
            for (int i = 0; i < 3; i++) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imgLists.get(i)
                        .getLayoutParams();
                params.width = measures.dynamicImgWidth;
                params.height = measures.dynamicImgWidth;

                String url = ImageUtil.getImagePath(photos.get(i)
                        .getImagePath(), measures.dynamicImgWidth);
                if (!TextUtils.isEmpty(url)) {
                    imgLists.get(i)
                            .setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(url)
                            .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                            .into(imgLists.get(i));
                } else {
                    imgLists.get(i)
                            .setVisibility(View.GONE);
                }
            }
            if (size > 3) {
                threadImgCountView.setVisibility(View.VISIBLE);
                tvThreadImgCount.setText(String.valueOf(photos.size()));
            }
        } else if (size > 0) {
            String url = ImageUtil.getImagePath(photos.get(0)
                    .getImagePath(), measures.rightImgWidth);
            threadCoverView.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(url)) {
                ivThreadRight.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(url)
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                        .into(ivThreadRight);
            } else {
                ivThreadRight.setVisibility(View.GONE);
            }
        } else {
            if (TextUtils.isEmpty(content) && !TextUtils.isEmpty(title)) {
            }
            threadImgCountView.setVisibility(View.GONE);
            ivThreadRight.setVisibility(View.GONE);
            threadCoverView.setVisibility(View.GONE);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnPraiseClickListener(OnPraiseClickListener onPraiseClickListener) {
        this.onPraiseClickListener = onPraiseClickListener;
    }

    public void setOnReplyClickListener(OnReplyClickListener onReplyClickListener) {
        this.onReplyClickListener = onReplyClickListener;
    }

    public interface OnPraiseClickListener {
        void onPraiseClick(
                CommunityThread thread,
                CheckableLinearButton checkPraised,
                ImageView imgThumbUp,
                TextView tvPraiseCount,
                TextView tvAdd);
    }

    public interface OnReplyClickListener {
        void onReply(CommunityThread item, int position);
    }
}
