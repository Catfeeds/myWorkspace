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
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPost;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThreadPages;
import com.hunliji.hljcommonlibrary.models.communitythreads.WeddingPhotoContent;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker
        .TrackerCommunityThreadViewHolder;
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
 * Created by mo_yu on 16/12/15.话题列表通用样式
 */

public class CommonThreadViewHolder extends TrackerCommunityThreadViewHolder {
    @BindView(R2.id.top_layout)
    View topLayout;
    @BindView(R2.id.thread_auth_view)
    View threadAuthView;
    @BindView(R2.id.riv_auth_avatar)
    RoundedImageView rivAuthAvatar;
    @BindView(R2.id.iv_auth_vip)
    ImageView ivAuthVip;
    @BindView(R2.id.tv_auth_name)
    TextView tvAuthName;
    @BindView(R2.id.tv_auth_description)
    TextView tvAuthDescription;
    @BindView(R2.id.tv_auth_wedding_date)
    TextView tvAuthWeddingDate;
    @BindView(R2.id.tv_thread_time)
    TextView tvThreadTime;
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
    @BindView(R2.id.praise_view)
    LinearLayout praiseView;
    @BindView(R2.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R2.id.comment_view)
    LinearLayout commentView;
    @BindView(R2.id.bottom_thread_view)
    RelativeLayout bottomThreadView;
    @BindView(R2.id.community_thread_view)
    LinearLayout communityThreadView;
    @BindView(R2.id.hidden_layout)
    LinearLayout hiddenLayout;
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
    @BindView(R2.id.bottom_thin_line_layout)
    View bottomThinLineLayout;
    @BindView(R2.id.bottom_thick_line_layout)
    View bottomThickLineLayout;
    public final static int COMMUNITY_CHANNEL = 1;//社区频道列表
    public final static int COMMUNITY_SEARCH = 2;//搜索
    public final static int MY_COMMUNITY_THREAD = 3;//我的话题
    public final static int COMMUNITY_GUIDE = 4;//旅拍攻略页

    private OnItemClickListener onItemClickListener;
    private OnPraiseClickListener onPraiseClickListener;
    private OnReplyClickListener onReplyClickListener;
    private CommunityThreadMeasures measures;
    private int type;
    private boolean isShowHotTag = true;//是否能显示新话题标识(默认能显示)
    private boolean isShowNewTag;//是否能显示新话题标识(默认不能能显示)
    private boolean isShowRichTag = true;//是否能显示精编话题标识(默认能显示)
    private int weddingImageWidth;
    private int weddingImageHeight;

    public CommonThreadViewHolder(View itemView, int type) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.type = type;
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
            final Context mContext, final CommunityThread item, final int position, int viewType) {
        final Activity activity = (Activity) mContext;
        //发帖人信息
        authView(activity, item);
        //话题所属频道
        onChannel(item, activity, mContext);
        //晒婚纱照话题
        final WeddingPhotoContent weddingPhotoContent = item.getWeddingPhotoContent();
        if (weddingPhotoContent != null && weddingPhotoContent.getId() > 0) {
            //晒婚纱照
            llThreadTitle.setVisibility(View.GONE);
            llWeddingDress.setVisibility(View.VISIBLE);
            ivSingleThreadImg.setVisibility(View.GONE);
            threadCoverView.setVisibility(View.GONE);
            showWeddingItem(item, weddingPhotoContent, mContext);
        } else {
            //非晒婚纱照标题显示
            llThreadTitle.setVisibility(View.VISIBLE);
            llWeddingDress.setVisibility(View.GONE);
            //pages字段 为null时 话题不是富文本话题
            showRichPage(item, mContext);
        }
        //2016.12.28 统一修去掉更新
        updateTime(item, mContext);
        //点赞
        onPraise(item);
        //回帖
        onReplyPosts(item, position);
        communityThreadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, item);
                }
            }
        });
    }

    /**
     * 晒婚纱照
     *
     * @param item
     * @param weddingPhotoContent
     * @param mContext
     */
    private void showWeddingItem(
            CommunityThread item, WeddingPhotoContent weddingPhotoContent, Context mContext) {
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
                .cropPath();
        if (!TextUtils.isEmpty(imgUrl)) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .apply(new RequestOptions().override(weddingImageWidth, weddingImageHeight)
                            .placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(ivWeddingDress);
        } else {
            Glide.with(mContext)
                    .clear(ivWeddingDress);
            ivWeddingDress.setImageBitmap(null);
        }
    }

    /**
     * pages字段 为null时 话题不是富文本话题
     *
     * @param item
     * @param mContext
     */
    private void showRichPage(CommunityThread item, Context mContext) {
        final String title;
        String content;
        ArrayList<Photo> photos = new ArrayList<>();
        if (item.getPages() == null) {
            title = item.getTitle();
            CommunityPost post = item.getPost();
            content = post.getMessage();
            photos = post.getPhotos();
            setPhotoView(mContext, photos, title, content, item.isShowThree());
        } else {
            CommunityThreadPages threadPages = item.getPages();
            title = threadPages.getTitle();
            content = threadPages.getSubTitle();
            setRichThreadView(mContext, threadPages);
        }
        //话题标题
        showThreadTitle(tvThreadTitle, title, item, mContext);
        // 当为三图（多图）和精选话题时，话题导读最多显示2行
        // 当为单图或无图话题时，话题导读最多显示3行
        showThreadMaxLine(photos, item, content, mContext);
        //当标题和导读都超过两行时，减小间距
        reducePitch(photos, item, mContext);
    }

    /**
     * 社区频道页
     *
     * @param item
     * @param activity
     * @param mContext
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
                            .navigation(mContext);
                }
            });
        }
    }

    /**
     * 当标题和导读都超过两行时，减小间距
     */
    private void reducePitch(ArrayList<Photo> photos, CommunityThread item, Context mContext) {
        if ((photos != null && photos.size() >= 3) || (item.getPages() != null && item.getPages()
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
     * 2016.12.28 统一修去掉更新
     */
    private void updateTime(CommunityThread item, Context mContext) {
        if (item.getLastPostTime() != null && item.getUpdatedAt() != null) {
            if (item.getLastPostTime()
                    .getMillis() <= item.getUpdatedAt()
                    .getMillis()) {
                tvThreadTime.setText(HljTimeUtils.getShowTime(mContext, item.getUpdatedAt()));
            } else {
                tvThreadTime.setText(HljTimeUtils.getShowTime(mContext, item.getLastPostTime()));
            }
        } else {
            if (item.getLastPostTime() != null) {
                tvThreadTime.setText(HljTimeUtils.getShowTime(mContext, item.getLastPostTime()));
            } else {
                tvThreadTime.setText(HljTimeUtils.getShowTime(mContext, item.getUpdatedAt()));
            }
        }
    }

    /**
     * 回帖
     *
     * @param item
     * @param position
     */
    private void onReplyPosts(
            final CommunityThread item, final int position) {
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
                        onItemClickListener.onItemClick(position, item);
                    }
                }
            }
        });
    }

    /**
     * // 当为三图（多图）和精选话题时，话题导读最多显示2行
     * // 当为单图或无图话题时，话题导读最多显示3行
     */
    private void showThreadMaxLine(
            ArrayList<Photo> photos, CommunityThread item, String content, Context mContext) {
        if ((photos != null && photos.size() >= 3 && (type != COMMUNITY_CHANNEL || item
                .isShowThree())) || item.getPages() != null) {
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
     * 点赞
     *
     * @param item
     */
    private void onPraise(final CommunityThread item) {
        int praiseSum = item.getPraisedSum();
        tvPraiseCount.setText(praiseSum == 0 ? "赞" : String.valueOf(praiseSum));
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
     * 话题标题
     *
     * @param title
     * @param item
     * @param mContext
     */
    private void showThreadTitle(
            TextView tvThreadTitle, String title, CommunityThread item, Context mContext) {
        if (TextUtils.isEmpty(title)) {
            tvThreadTitle.setVisibility(View.GONE);
        } else {
            tvThreadTitle.setVisibility(View.VISIBLE);
            //晒婚纱照话题精华tab不显示精，其他话题显示，晒婚纱照话题不显示新
            if ((item.isRefined() && (isShowHotTag || item.getWeddingPhotoContent() == null)) ||
                    (item.getPages() != null && isShowRichTag) || (HljTimeUtils.isThreadNew(
                    item.getCreatedAt()) && isShowNewTag && item.getWeddingPhotoContent() ==
                    null)) {
                SpannableStringBuilder builder = EmojiUtil.parseEmojiByText2(mContext,
                        "  " + title,
                        measures.faceSize);
                if (builder != null) {
                    Drawable drawable;
                    if (item.isRefined() && (isShowHotTag || item.getWeddingPhotoContent() ==
                            null)) {
                        drawable = ContextCompat.getDrawable(mContext,
                                R.mipmap.icon_refined_tag_32_32);
                    } else if (item.getPages() != null) {
                        drawable = ContextCompat.getDrawable(mContext,
                                R.mipmap.icon_recommend_tag_32_32);
                    } else {
                        drawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_new_tag_32_32);
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

    /**
     * 话题发布者信息
     *
     * @param activity
     * @param item
     */
    private void authView(Activity activity, CommunityThread item) {
        switch (type) {
            case COMMUNITY_CHANNEL:
                setAuthView(activity, item.getAuthor());
                break;
            case COMMUNITY_SEARCH:
                setAuthView(activity, item.getAuthor());
                break;
            case MY_COMMUNITY_THREAD:
                setAuthView(activity, item.getAuthor());
                if (item.isHidden()) {
                    hiddenLayout.setVisibility(View.VISIBLE);
                    communityThreadView.setVisibility(View.GONE);
                    return;
                } else {
                    hiddenLayout.setVisibility(View.GONE);
                    communityThreadView.setVisibility(View.VISIBLE);
                }
                break;
            case COMMUNITY_GUIDE:
                threadAuthView.setVisibility(View.GONE);
                break;
        }
    }

    private void setAuthView(final Activity context, final CommunityAuthor author) {
        threadAuthView.setVisibility(View.VISIBLE);
        String avatarUrl = ImageUtil.getImagePath(author.getAvatar(),
                CommonUtil.dp2px(context, 36));
        Glide.with(context)
                .load(avatarUrl)
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_avatar_primary))
                .into(rivAuthAvatar);
        tvAuthName.setText(author.getName());
        //新增会员标识，优先显示达人标识
        if (!TextUtils.isEmpty(author.getSpecialty()) && !"普通用户".equals(author.getSpecialty())) {
            ivAuthVip.setVisibility(View.VISIBLE);
            ivAuthVip.setImageResource(R.mipmap.icon_vip_yellow_28_28);
            tvAuthDescription.setVisibility(View.VISIBLE);
            tvAuthDescription.setText(author.getSpecialty());
            tvAuthWeddingDate.setVisibility(View.VISIBLE);
            tvAuthWeddingDate.setText(HljTimeUtils.getWeddingDate(author.getWeddingDay(),
                    author.isPending(),
                    author.getGender() == 1));
        } else {
            if (author.getMember() != null) {
                ivAuthVip.setVisibility(View.VISIBLE);
                ivAuthVip.setImageResource(R.mipmap.icon_member_28_28);
            } else {
                ivAuthVip.setVisibility(View.GONE);
            }
            tvAuthDescription.setVisibility(View.GONE);
            tvAuthDescription.setText("");
            tvAuthWeddingDate.setVisibility(View.VISIBLE);
            tvAuthWeddingDate.setText(HljTimeUtils.getWeddingDate(author.getWeddingDay(),
                    author.isPending(),
                    author.getGender() == 1));
        }
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
            Context context,
            ArrayList<Photo> photos,
            String title,
            String content,
            boolean isShow) {
        int size = 0;
        if (photos != null) {
            size = photos.size();//图片数量
        }
        ArrayList<ImageView> imgLists = new ArrayList<ImageView>(Arrays.asList(ivThread1,
                ivThread2,
                ivThread3));
        ivSingleThreadImg.setVisibility(View.GONE);
        //社区频道页不显示三图样式
        if (size >= 3 && (type != COMMUNITY_CHANNEL || isShow)) {
            //三图样式时，隐藏描述
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

                if (size > 3) {
                    threadImgCountView.setVisibility(View.VISIBLE);
                    tvThreadImgCount.setText(String.valueOf(photos.size()));
                }
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
                LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) tvThreadTitle
                        .getLayoutParams();
            }
            threadImgCountView.setVisibility(View.GONE);
            ivThreadRight.setVisibility(View.GONE);
            threadCoverView.setVisibility(View.GONE);
        }
    }

    public void setShowChannelView(boolean showChannelView) {
        channelView.setVisibility(showChannelView ? View.VISIBLE : View.GONE);
    }

    public void setShowHotTag(boolean showHotTag) {
        this.isShowHotTag = showHotTag;
    }

    public void setShowNewTag(boolean showNewTag) {
        this.isShowNewTag = showNewTag;
    }

    public void setShowRichTag(boolean showRichTag) {
        this.isShowRichTag = showRichTag;
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
