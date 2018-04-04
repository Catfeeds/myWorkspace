package me.suncloud.marrymemo.adpter.community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ObjectBindAdapter;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPost;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.communitythreads.WeddingPhotoItem;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearGroup;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljimagelibrary.utils.OnPhotosItemClickListener;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.ImageLoadUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.community.CommunityIntentUtil;
import me.suncloud.marrymemo.view.UserProfileActivity;
import me.suncloud.marrymemo.view.community.CommunityChannelActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.view.wedding_dress.ViewLargeImageActivity;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutButton;
import me.suncloud.marrymemo.widget.HorizontalListView;
import me.suncloud.marrymemo.widget.community.WeddingGroupPhotosView;

/**
 * Created by luohanlin on 2017/5/4.
 * 话题详情内容和回复列表合并显示的Adapter
 */

public class ThreadDetailsAdapter extends RecyclerView.Adapter<BaseViewHolder<CommunityPost>> {

    private Context context;
    private CommunityThread thread;
    private ArrayList<CommunityPost> posts;
    private ArrayList<CommunityPost> hotPosts;
    private RecyclerView recyclerView;
    private int totalPostCount;
    private View footerView;

    private static final int ITEM_TYPE_POST = 1;
    private static final int ITEM_TYPE_PHOTO_ITEM = 2;
    private static final int ITEM_TYPE_PHOTO_MERCHANT = 3;
    private static final int ITEM_TYPE_PHOTO_FROM_CHANNEL = 4;
    private static final int ITEM_TYPE_TAB_VIEW = 5;
    private static final int ITEM_TYPE_FOOTER = 6;
    public static final int ITEM_TYPE_HOT_POST_TAG = 7;
    public static final int ITEM_TYPE_HOT_POST = 8;
    public static final int ITEM_TYPE_ALL_POST_TAG = 9;

    private int groupPhotoWithSize; // 组图item和tab view所占的数目
    private int photoItemSize;
    private boolean isWeddingPhotoThread;
    private int emojiSize;
    private int singleReplyImgWidth;
    private int singleReplyImgHeight;
    private int listImgSize;

    private boolean isAll = true; // 是否是查看全部，而非只看楼主
    private boolean isOrderByHot = true; // 排序方式是否是默认的hot排序
    private boolean isFromChannel;

    private OnTabViewCheckedListener onTabViewCheckedListener;
    private OnItemClickListener onItemClickListener;
    private OnPraisePostListener onPraisePostListener;
    private OnItemReplyListener onItemReplyListener;
    private View.OnLongClickListener onContentLongClickListener;
    private OnPraiseGroupPhotoListener onPraiseGroupPhotoListener;
    private boolean isTagAndHotHide;
    private int hotPostViewSize;
    private int hotPostTagIndex;
    private int allPostTagIndex;

    public ThreadDetailsAdapter(
            Context context,
            ArrayList<CommunityPost> ps,
            RecyclerView recyclerView,
            boolean fromChannel) {
        this.context = context;
        this.posts = ps;
        this.recyclerView = recyclerView;
        this.isFromChannel = fromChannel;
        initValues();
    }

    private void initValues() {
        emojiSize = CommonUtil.dp2px(context, 20);
        singleReplyImgWidth = CommonUtil.dp2px(context, 180);
        singleReplyImgHeight = CommonUtil.dp2px(context, 135);
        listImgSize = (int) ((CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context,
                40 + 24 + 10 + 20)) / 3.4);
    }

    public void setHotPosts(ArrayList<CommunityPost> hotItems) {
        this.hotPosts = hotItems;
    }

    public void setTotalPostCount(int totalPostCount) {
        this.totalPostCount = totalPostCount;
    }

    public void setTagAndHotHide(boolean tagAndHotHide) {
        isTagAndHotHide = tagAndHotHide;
        if (tagAndHotHide) {
            if (hotPosts != null) {
                hotPosts.clear();
            }
            groupPhotoWithSize = 0; // 在评论列表之前的所有元素都当做是header元素
            photoItemSize = 0;
        } else {
            if (isWeddingPhotoThread) {
                // 有婚纱照
                photoItemSize = thread.getWeddingPhotoItems()
                        .size();
                groupPhotoWithSize = photoItemSize + 3; //  一个商家信息，一个来自频道信息, 一个tab view
            }
        }
        hotPostViewSize = isShowingHotPost() ? (hotPosts.size() + 1) : 0;
        allPostTagIndex = groupPhotoWithSize + hotPostViewSize;
    }

    public boolean isTagAndHotHide() {
        return isTagAndHotHide;
    }

    public void setThread(CommunityThread thread) {
        this.thread = thread;
        this.isWeddingPhotoThread = thread.getWeddingPhotoContent() != null && thread
                .getWeddingPhotoItems() != null && !thread.getWeddingPhotoItems()
                .isEmpty();
        groupPhotoWithSize = 0; // 在评论列表之前的所有元素都当做是header元素
        photoItemSize = 0;
        if (isWeddingPhotoThread) {
            // 有婚纱照
            photoItemSize = thread.getWeddingPhotoItems()
                    .size();
            groupPhotoWithSize = photoItemSize + 3; //  一个商家信息，一个来自频道信息, 一个tab view
        }
    }

    public void setFooterView(View footerView) {
        if (this.footerView == null) {
            this.footerView = footerView;
            notifyItemInserted(getItemCount() - 1);
        } else {
            this.footerView = footerView;
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public void setOnTabViewCheckedListener(OnTabViewCheckedListener onTabViewCheckedListener) {
        this.onTabViewCheckedListener = onTabViewCheckedListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemReplyListener(OnItemReplyListener onItemReplyListener) {
        this.onItemReplyListener = onItemReplyListener;
    }

    public void setOnPraisePostListener(OnPraisePostListener onPraisePostListener) {
        this.onPraisePostListener = onPraisePostListener;
    }

    public void setOnContentLongClickListener(
            View.OnLongClickListener onContentLongClickListener) {
        this.onContentLongClickListener = onContentLongClickListener;
    }

    public void setOnPraiseGroupPhotoListener(
            OnPraiseGroupPhotoListener onPraiseGroupPhotoListener) {
        this.onPraiseGroupPhotoListener = onPraiseGroupPhotoListener;
    }

    public void setTabParams(boolean all, boolean hot) {
        this.isAll = all;
        this.isOrderByHot = hot;
        if (!isAll && hotPosts != null) {
            hotPosts.clear();
        }
        notifyItemChanged(getTabViewPosition());
    }

    public int getTabViewPosition() {
        if (isWeddingPhotoThread) {
            return groupPhotoWithSize - 1;
        } else {
            return -1;
        }
    }

    public int getAllPostTagIndex() {
        return allPostTagIndex;
    }

    @Override
    public BaseViewHolder<CommunityPost> onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        BaseViewHolder holder = null;
        switch (viewType) {
            case ITEM_TYPE_PHOTO_ITEM:
                holder = new GroupViewHolder(inflater.inflate(R.layout
                                .wedding_photo_group_recyclerview_item,
                        parent,
                        false));
                break;
            case ITEM_TYPE_PHOTO_MERCHANT:
                holder = new MerchantViewHolder(inflater.inflate(R.layout
                                .thread_detail_merchant_item,
                        parent,
                        false));
                break;
            case ITEM_TYPE_PHOTO_FROM_CHANNEL:
                holder = new FromInfoViewHolder(inflater.inflate(R.layout
                                .thread_detail_from_info_layout2,
                        parent,
                        false));
                break;
            case ITEM_TYPE_TAB_VIEW:
                holder = new TabViewHolder(inflater.inflate(R.layout.thread_detail_tab_layout,
                        parent,
                        false));
                break;
            case ITEM_TYPE_POST:
            case ITEM_TYPE_HOT_POST:
                holder = new PostItemViewHolder(inflater.inflate(R.layout.new_post_list_item_view,
                        parent,
                        false), false);
                break;
            case ITEM_TYPE_FOOTER:
                holder = new EmptyViewHolder(footerView);
                break;
            case ITEM_TYPE_HOT_POST_TAG:
                holder = new HotPostTagViewHolder(inflater.inflate(R.layout.thread_posts_tag_layout,
                        parent,
                        false));
                break;
            case ITEM_TYPE_ALL_POST_TAG:
                holder = new AllPostTagViewHolder(inflater.inflate(R.layout.thread_posts_tag_layout,
                        parent,
                        false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<CommunityPost> holder, int position) {
        holder.setView(context, getItem(position), position, getItemViewType(position));
    }

    @Override
    public int getItemViewType(int position) {
        hotPostViewSize = isShowingHotPost() ? (hotPosts.size() + 1) : 0;
        allPostTagIndex = groupPhotoWithSize + hotPostViewSize;
        if (footerView != null && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else if (position < groupPhotoWithSize) {
            // 组图类型或者tab view
            if (position < photoItemSize) {
                return ITEM_TYPE_PHOTO_ITEM;
            } else if (position == photoItemSize) {
                return ITEM_TYPE_PHOTO_MERCHANT;
            } else if (position == photoItemSize + 1) {
                return ITEM_TYPE_PHOTO_FROM_CHANNEL;
            } else {
                return ITEM_TYPE_TAB_VIEW;
            }
        } else if (position >= groupPhotoWithSize && position < allPostTagIndex) {
            if (position == groupPhotoWithSize) {
                return ITEM_TYPE_HOT_POST_TAG;
            } else {
                return ITEM_TYPE_HOT_POST;
            }
        } else {
            if (position == allPostTagIndex) {
                return ITEM_TYPE_ALL_POST_TAG;
            } else {
                return ITEM_TYPE_POST;
            }
        }
    }

    public CommunityPost getItem(int position) {
        if (footerView != null && position == getItemCount() - 1) {
            return null;
        } else if (position < groupPhotoWithSize) {
            // 组图类型或者tab view
            if (position < photoItemSize) {
                return null;
            } else if (position == photoItemSize) {
                return null;
            } else if (position == photoItemSize + 1) {
                return null;
            } else {
                return null;
            }
        } else if (position >= groupPhotoWithSize && position < allPostTagIndex) {
            if (position == groupPhotoWithSize) {
                return null;
            } else {
                return hotPosts.get(position - groupPhotoWithSize - 1);
            }
        } else {
            if (position == allPostTagIndex) {
                return null;
            } else {
                return posts.get(position - allPostTagIndex - 1);
            }
        }
    }

    public WeddingPhotoItem getWeddingPhotoItem(int position) {
        return thread.getWeddingPhotoItems()
                .get(position);
    }

    private boolean isShowingHotPost() {
        return hotPosts != null && !hotPosts.isEmpty() && !isTagAndHotHide;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        count += posts == null ? 0 : posts.size() + 1;
        count += groupPhotoWithSize;
        count += footerView == null ? 0 : 1;

        hotPostViewSize = isShowingHotPost() ? (hotPosts.size() + 1) : 0;
        count += hotPostViewSize;

        return count;
    }

    class GroupViewHolder extends BaseViewHolder<CommunityPost> {
        @BindView(R.id.photos_view)
        WeddingGroupPhotosView photosView;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.tv_praise_hint)
        TextView tvPraiseHint;
        @BindView(R.id.check_praised)
        CheckableLinearLayoutButton checkPraised;
        @BindView(R.id.img_thumb_up)
        ImageView imgThumbUp;
        @BindView(R.id.tv_praise_count)
        TextView tvPraisedCount;
        @BindView(R.id.tv_add)
        TextView tvAdd;

        GroupViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(
                Context mContext, final CommunityPost post, final int position, int viewType) {
            final WeddingPhotoItem item = getWeddingPhotoItem(position);
            if (item == null) {
                return;
            }

            if (TextUtils.isEmpty(item.getDescription())) {
                tvPraiseHint.setVisibility(View.VISIBLE);
                tvContent.setVisibility(View.GONE);
            } else {
                tvPraiseHint.setVisibility(View.INVISIBLE);
                tvContent.setVisibility(View.VISIBLE);
                tvContent.setText(item.getDescription());
            }

            tvPraisedCount.setText(item.getLikesCount() > 0 ? String.valueOf(item.getLikesCount()
            ) : "赞");
            photosView.setPhotos(item.getPhotos(),
                    !item.isCollapseViewOpened(),
                    new WeddingGroupPhotosView.OnPhotoClickListener() {
                        @Override
                        public void onPhotoClick(Photo photo, int photoP) {
                            Intent intent = new Intent(context, ViewLargeImageActivity.class);
                            intent.putExtra("thread", thread);
                            intent.putExtra("item_position", position);
                            intent.putExtra("photo_position", photoP);
                            ((Activity) context).startActivityForResult(intent,
                                    Constants.RequestCode.WEDDING_PHOTO_THREAD_VIEW_LARGE_IMAGE);
                            ((Activity) context).overridePendingTransition(R.anim
                                            .slide_in_from_bottom,
                                    R.anim.activity_anim_default);
                        }
                    });
            photosView.setOnShowAllPhotoListener(new WeddingGroupPhotosView
                    .OnShowAllPhotoListener() {
                @Override
                public void onShowAllPhoto() {
                    item.setCollapseViewOpened(true);
                }
            });
            checkPraised.setChecked(item.isPraised());
            checkPraised.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPraiseGroupPhotoListener != null) {
                        onPraiseGroupPhotoListener.onPraiseGroup(position,
                                item,
                                checkPraised,
                                imgThumbUp,
                                tvPraisedCount,
                                tvAdd);
                    }
                }
            });
        }
    }

    class MerchantViewHolder extends BaseViewHolder<CommunityPost> {
        @BindView(R.id.iv_merchant_avatar)
        RoundedImageView ivMerchantAvatar;
        @BindView(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @BindView(R.id.iv_level)
        ImageView ivLevel;
        @BindView(R.id.iv_bond)
        ImageView ivBond;
        @BindView(R.id.rating_view_merchant)
        RatingBar ratingViewMerchant;
        @BindView(R.id.tv_merchant_comment_count)
        TextView tvMerchantCommentCount;
        @BindView(R.id.tv_merchant_address)
        TextView tvMerchantAddress;
        @BindView(R.id.iv_merchant_arrow)
        ImageView ivMerchantArrow;
        @BindView(R.id.merchant_info_layout)
        RelativeLayout merchantInfoLayout;
        @BindView(R.id.iv_merchant_avatar_2)
        RoundedImageView ivMerchantAvatar2;
        @BindView(R.id.tv_merchant_name_2)
        TextView tvMerchantName2;
        @BindView(R.id.merchant_info_layout_2)
        RelativeLayout merchantInfoLayout2;
        @BindView(R.id.merchant_view)
        LinearLayout merchantView;

        MerchantViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(
                final Context mContext, CommunityPost item, int position, int viewType) {

            if (thread.getWeddingPhotoContent()
                    .getMerchant() != null) {
                merchantView.setVisibility(View.VISIBLE);
                merchantInfoLayout.setVisibility(View.VISIBLE);
                merchantInfoLayout2.setVisibility(View.GONE);
                setMerchantView(thread.getWeddingPhotoContent()
                        .getMerchant());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, MerchantDetailActivity.class);
                        intent.putExtra("id",
                                thread.getWeddingPhotoContent()
                                        .getMerchant()
                                        .getId());
                        mContext.startActivity(intent);
                        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
            } else if (!TextUtils.isEmpty(thread.getWeddingPhotoContent()
                    .getUnrecordedMerchantName())) {
                merchantView.setVisibility(View.VISIBLE);
                merchantInfoLayout.setVisibility(View.GONE);
                merchantInfoLayout2.setVisibility(View.VISIBLE);
                tvMerchantName2.setText(thread.getWeddingPhotoContent()
                        .getUnrecordedMerchantName());
            } else {
                merchantView.setVisibility(View.GONE);
            }
        }


        private void setMerchantView(Merchant merchant) {
            if (merchant == null) {
                return;
            }
            ivBond.setVisibility(merchant.getBondSign() != null ? View.VISIBLE : View.GONE);
            int res = 0;
            switch (merchant.getGrade()) {
                case 2:
                    res = R.mipmap.icon_merchant_level2_32_32;
                    break;
                case 3:
                    res = R.mipmap.icon_merchant_level3_32_32;
                    break;
                case 4:
                    res = R.mipmap.icon_merchant_level4_32_32;
                    break;
                default:
                    break;
            }
            if (res != 0) {
                ivLevel.setVisibility(View.VISIBLE);
                ivLevel.setImageResource(res);
            } else {
                ivLevel.setVisibility(View.GONE);
            }
            tvMerchantName.setText(merchant.getName());
            tvMerchantAddress.setText(merchant.getAddress());
            Glide.with(context)
                    .load(ImagePath.buildPath(merchant.getLogoPath())
                            .width(CommonUtil.dp2px(context, 60))
                            .height(CommonUtil.dp2px(context, 60))
                            .path())
                    .apply(new RequestOptions()
                            .dontAnimate())
                    .into(ivMerchantAvatar);
            if (merchant.getCommentStatistics() != null) {
                ratingViewMerchant.setRating((float) merchant.getCommentStatistics()
                        .getScore());
            }
            if (merchant.getCommentCount() > 0) {
                tvMerchantCommentCount.setText(context.getString(R.string.label_comment_count5,
                        merchant.getCommentCount()));
            } else {
                tvMerchantCommentCount.setText(R.string.label_no_comment2);
            }
        }
    }

    class FromInfoViewHolder extends BaseViewHolder<CommunityPost> {
        @BindView(R.id.img_channel_cover)
        RoundedImageView imgChannelCover;
        @BindView(R.id.tv_from_channel)
        TextView tvFromChannel;
        @BindView(R.id.from_layout)
        LinearLayout fromLayout;

        FromInfoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(
                final Context mContext, CommunityPost item, int position, int viewType) {
            if (isFromChannel) {
                fromLayout.setVisibility(View.GONE);
            } else if (thread.getChannel() != null) {
                fromLayout.setVisibility(View.VISIBLE);
                final CommunityChannel communityChannel = thread.getChannel();
                Glide.with(mContext)
                        .load(ImagePath.buildPath(communityChannel.getCoverPath())
                                .width(CommonUtil.dp2px(mContext, 30))
                                .height(CommonUtil.dp2px(mContext, 30))
                                .path())
                        .apply(new RequestOptions()
                                .dontAnimate())
                        .into(imgChannelCover);
                tvFromChannel.setText(communityChannel.getTitle());
                fromLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommunityIntentUtil.startCommunityChannelIntent(v.getContext(),
                                communityChannel.getId());
                    }
                });
            }
        }
    }


    class TabViewHolder extends BaseViewHolder<CommunityPost> {
        @BindView(R.id.cb_all)
        CheckableLinearButton cbAll;
        @BindView(R.id.cb_owner)
        CheckableLinearButton cbOwner;
        @BindView(R.id.check_group)
        CheckableLinearGroup checkGroup;
        @BindView(R.id.tv_sort_label)
        TextView tvSortLabel;
        @BindView(R.id.sort_layout)
        LinearLayout sortLayout;
        @BindView(R.id.content_layout)
        RelativeLayout contentLayout;

        TabViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(
                Context mContext, CommunityPost item, int position, int viewType) {
            checkGroup.setOnCheckedChangeListener(onCheckedChangeListener);
            if (isAll) {
                cbAll.setChecked(true);
                sortLayout.setVisibility(View.VISIBLE);
            } else {
                cbOwner.setChecked(true);
                sortLayout.setVisibility(View.INVISIBLE);
            }
            if (isOrderByHot) {
                tvSortLabel.setText(R.string.label_sort_default);
            } else {
                tvSortLabel.setText(R.string.label_sort_new);
            }
            sortLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTabViewCheckedListener != null) {
                        onTabViewCheckedListener.onOrderMenuClicked(v);
                    }
                }
            });
        }

        CheckableLinearGroup.OnCheckedChangeListener onCheckedChangeListener = new
                CheckableLinearGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CheckableLinearGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.cb_all:
                        if (onTabViewCheckedListener != null) {
                            onTabViewCheckedListener.onAllChecked();
                        }
                        break;
                    case R.id.cb_owner:
                        if (onTabViewCheckedListener != null) {
                            onTabViewCheckedListener.onOwnerChecked();
                        }
                        break;
                }
            }
        };
    }

    class HotPostTagViewHolder extends BaseViewHolder<CommunityPost> {
        @BindView(R.id.img_tag)
        ImageView imgTag;
        @BindView(R.id.tv_label)
        TextView tvLabel;
        @BindView(R.id.tv_empty_text)
        TextView tvEmptyText;
        @BindView(R.id.empty_view)
        LinearLayout emptyView;
        @BindView(R.id.tag_top_margin)
        View tagTopMargin;
        @BindView(R.id.content_layout)
        View contentLayout;

        HotPostTagViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(
                Context mContext,
                CommunityPost item,
                int position,
                int viewType) {
            tagTopMargin.setVisibility(View.GONE);
            tvLabel.setText("热门跟帖");
            imgTag.setImageResource(R.mipmap.icon_hot_tag_32_32);
            if (isTagAndHotHide) {
                contentLayout.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);
            } else {
                contentLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    class AllPostTagViewHolder extends BaseViewHolder<CommunityPost> {
        @BindView(R.id.img_tag)
        ImageView imgTag;
        @BindView(R.id.tv_label)
        TextView tvLabel;
        @BindView(R.id.tv_empty_text)
        TextView tvEmptyText;
        @BindView(R.id.empty_view)
        LinearLayout emptyView;
        @BindView(R.id.tag_top_margin)
        View tagTopMargin;
        @BindView(R.id.content_layout)
        View contentLayout;

        AllPostTagViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(
                Context mContext,
                CommunityPost item,
                int position,
                int viewType) {
            if (isTagAndHotHide) {
                contentLayout.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);
            } else {
                contentLayout.setVisibility(View.VISIBLE);
            }
            imgTag.setImageResource(R.mipmap.icon_comment_round_gray_primary_32_32);
            if (hotPostViewSize > 0) {
                tagTopMargin.setVisibility(View.VISIBLE);
            } else {
                tagTopMargin.setVisibility(View.GONE);
            }

            if (!isAll) {
                // owner's posts
                tvLabel.setText(mContext.getString(R.string.label_all_posts_tag, totalPostCount));
                if (totalPostCount == 0) {
                    tvEmptyText.setText("楼主没有发表跟帖哟");
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                }
            } else {
                tvLabel.setText(mContext.getString(R.string.label_all_posts_tag, totalPostCount));
                if (totalPostCount == 0) {
                    tvEmptyText.setText("还没有回帖,快来抢沙发~");
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                }
            }
        }
    }

    class PostItemViewHolder extends BaseViewHolder<CommunityPost> {
        @BindView(R.id.item_content_layout)
        RelativeLayout itemContentLayout;
        @BindView(R.id.img_avatar)
        RoundedImageView imgAvatar;
        @BindView(R.id.tv_user_nick)
        TextView tvUserNick;
        @BindView(R.id.tv_user_specialty)
        TextView tvUserSpecialty;
        @BindView(R.id.tv_owner_label)
        TextView tvOwnerLabel;
        @BindView(R.id.tv_wedding_time)
        TextView tvWeddingTime;
        @BindView(R.id.tv_post_time)
        TextView tvPostTime;
        @BindView(R.id.user_info_layout)
        LinearLayout userInfoLayout;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.btn_see_all)
        Button btnSeeAll;
        @BindView(R.id.img_reply_photos1)
        ImageView imgReplyPhotos1;
        @BindView(R.id.reply_imgs_layout)
        LinearLayout replyImgsLayout;
        @BindView(R.id.tv_quote_user_nick)
        TextView tvQuoteUserNick;
        @BindView(R.id.tv_quote_time)
        TextView tvQuoteTime;
        @BindView(R.id.tv_quote_position)
        TextView tvQuotePosition;
        @BindView(R.id.quote_user_layout)
        LinearLayout quoteUserLayout;
        @BindView(R.id.tv_quote_content)
        TextView tvQuoteContent;
        @BindView(R.id.tv_quote_position2)
        TextView tvQuotePosition2;
        @BindView(R.id.quote_hidden_layout)
        FrameLayout quoteHiddenLayout;
        @BindView(R.id.quote_layout)
        LinearLayout quoteLayout;
        @BindView(R.id.tv_reply_count)
        TextView tvReplyCount;
        @BindView(R.id.bottom_layout)
        LinearLayout bottomLayout;
        @BindView(R.id.img_daren)
        ImageView imgDaren;
        @BindView(R.id.check_praised)
        CheckableLinearLayoutButton checkPraised;
        @BindView(R.id.img_thumb_up)
        ImageView imgThumbUp;
        @BindView(R.id.tv_praise_count)
        TextView tvPraisedCount;
        @BindView(R.id.tv_add)
        TextView tvAdd;
        @BindView(R.id.tv_city)
        TextView tvCity;
        @BindView(R.id.tv_post_position)
        TextView tvPostPosition;
        @BindView(R.id.img_list)
        HorizontalListView imgsList;
        @BindView(R.id.reply_count_layout)
        View replyCountLayout;

        boolean isHotPost;

        PostItemViewHolder(View view, boolean isHotPost) {
            super(view);
            this.isHotPost = isHotPost;
            ButterKnife.bind(this, view);
            btnSeeAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 展开全部内容
                    tvContent.setMaxLines(Integer.MAX_VALUE);
                    btnSeeAll.setVisibility(View.GONE);
                }
            });
        }

        @Override
        protected void setViewData(
                final Context mContext,
                final CommunityPost item,
                final int position,
                int viewType) {
            if (item == null) {
                return;
            }
            itemContentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position, item);
                    }
                }
            });
            ImageLoadUtil.loadImageViewWithoutTransition(mContext,
                    item.getAuthor()
                            .getAvatar(),
                    imgAvatar);
            if (thread.getAuthor()
                    .getId() == item.getAuthor()
                    .getId()) {
                tvOwnerLabel.setVisibility(View.VISIBLE);
            } else {
                tvOwnerLabel.setVisibility(View.GONE);
            }
            tvUserNick.setText(item.getAuthor()
                    .getName());
            if (TextUtils.isEmpty(item.getAuthor()
                    .getSpecialty()) || item.getAuthor()
                    .getSpecialty()
                    .equals("普通用户")) {
                if (item.getAuthor()
                        .getMember() != null) {
                    imgDaren.setVisibility(View.VISIBLE);
                    imgDaren.setImageResource(R.mipmap.icon_member_28_28);
                } else {
                    imgDaren.setVisibility(View.GONE);
                }
                tvUserSpecialty.setVisibility(View.GONE);
            } else {
                imgDaren.setVisibility(View.VISIBLE);
                imgDaren.setImageResource(R.mipmap.icon_vip_yellow_28_28);
                tvUserSpecialty.setText(item.getAuthor()
                        .getSpecialty());
                tvUserSpecialty.setVisibility(View.VISIBLE);
            }
            tvPostPosition.setText(String.valueOf(item.getSerialNo()));
            tvPostPosition.setVisibility(isHotPost ? View.GONE : View.VISIBLE);
            if (TextUtils.isEmpty(item.getCityName())) {
                tvCity.setVisibility(View.INVISIBLE);
            } else {
                tvCity.setVisibility(View.VISIBLE);
                tvCity.setText(item.getCityName());
            }

            if (item.getAuthor()
                    .getWeddingDay() != null && item.getAuthor()
                    .isPending() != 0) {
                if (item.getAuthor()
                        .getWeddingDay()
                        .isBefore(new DateTime())) {
                    tvWeddingTime.setText(item.getAuthor()
                            .getGender() == 1 ? mContext.getString(R.string.label_married2) :
                            mContext.getString(
                            R.string.label_married));
                } else {
                    tvWeddingTime.setText(mContext.getString(R.string.label_wedding_time1,
                            item.getAuthor()
                                    .getWeddingDay()
                                    .toString("yyyy-MM-dd")));
                }
                tvWeddingTime.setVisibility(View.VISIBLE);
            } else {
                tvWeddingTime.setText(item.getAuthor()
                        .getGender() == 1 ? "" : mContext.getString(R.string.label_no_wedding_day));
                tvWeddingTime.setVisibility(View.VISIBLE);
            }

            tvPostTime.setText(HljTimeUtils.getShowTime(mContext, item.getCreatedAt()));
            tvContent.setText(EmojiUtil.parseEmojiByText2(mContext, item.getMessage(), emojiSize));
            if (item.getPhotos() != null && item.getPhotos()
                    .size() > 0) {
                if (item.getPhotos()
                        .size() == 1) {
                    replyImgsLayout.setVisibility(View.VISIBLE);
                    imgsList.setVisibility(View.GONE);

                    imgReplyPhotos1.setVisibility(View.VISIBLE);
                    imgReplyPhotos1.getLayoutParams().width = singleReplyImgWidth;
                    imgReplyPhotos1.getLayoutParams().height = singleReplyImgHeight;
                    Glide.with(mContext)
                            .load(ImagePath.buildPath(item.getPhotos()
                                    .get(0)
                                    .getImagePath())
                                    .width(singleReplyImgWidth)
                                    .path())
                            .into(imgReplyPhotos1);
                    imgReplyPhotos1.setOnClickListener(new OnPhotosItemClickListener(mContext,
                            item.getPhotos(),
                            0));
                } else {
                    replyImgsLayout.setVisibility(View.GONE);
                    imgsList.setVisibility(View.VISIBLE);
                    ObjectBindAdapter<Photo> adapter = new ObjectBindAdapter<>(context,
                            item.getPhotos(),
                            R.layout.thread_reply_imgs_item_view,
                            new ImageListViewBinder(item.getPhotos()));
                    imgsList.setAdapter(adapter);
                    imgsList.setOnItemClickListener(new OnPhotosItemClickListener(context,
                            item.getPhotos()));
                    imgsList.getLayoutParams().height = listImgSize;
                    imgsList.setParentView(recyclerView);
                }
            } else {
                replyImgsLayout.setVisibility(View.GONE);
                imgsList.setVisibility(View.GONE);
            }

            if (item.getQuotedPost() != null && item.getQuotedPost()
                    .getId() != 0 && item.getQuotedPost()
                    .getId() != item.getCommunityThread()
                    .getPost()
                    .getId()) {
                quoteLayout.setVisibility(View.VISIBLE);
                tvQuotePosition.setText(mContext.getString(R.string.label_thread_position,
                        item.getQuotedPost()
                                .getSerialNo()));
                tvQuotePosition2.setText(mContext.getString(R.string.label_thread_position,
                        item.getQuotedPost()
                                .getSerialNo()));
                if (item.getQuotedPost()
                        .isHidden()) {
                    quoteUserLayout.setVisibility(View.GONE);
                    tvQuoteContent.setVisibility(View.GONE);
                    quoteHiddenLayout.setVisibility(View.VISIBLE);
                } else {
                    quoteUserLayout.setVisibility(View.VISIBLE);
                    tvQuoteContent.setVisibility(View.VISIBLE);
                    quoteHiddenLayout.setVisibility(View.GONE);
                    tvQuoteUserNick.setText(item.getQuotedPost()
                            .getAuthor()
                            .getName() + ":");
                    if (!JSONUtil.isEmpty(item.getQuotedPost()
                            .getMessage())) {
                        tvQuoteContent.setText(EmojiUtil.parseEmojiByText2(mContext,
                                item.getQuotedPost()
                                        .getMessage(),
                                emojiSize));
                    } else {
                        tvQuoteContent.setText("[图片]");
                    }
                }
            } else {
                quoteLayout.setVisibility(View.GONE);
            }

            checkPraised.setChecked(item.isPraised());
            tvPraisedCount.setText(item.getPraisedCount() > 0 ? String.valueOf(item
                    .getPraisedCount()) : "赞");
            checkPraised.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPraisePostListener != null) {
                        onPraisePostListener.onPraisePost(position,
                                item,
                                checkPraised,
                                imgThumbUp,
                                tvPraisedCount,
                                tvAdd);
                    }
                }
            });
            replyCountLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemReplyListener != null) {
                        onItemReplyListener.onItemReply(position, item);
                    }
                }
            });
            imgAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserProfileActivity.class);
                    intent.putExtra("id",
                            item.getAuthor()
                                    .getId());
                    mContext.startActivity(intent);
                }
            });
            if (onContentLongClickListener != null) {
                tvContent.setOnLongClickListener(onContentLongClickListener);
            }
            tvContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position, item);
                    }
                }
            });
        }
    }

    private class ImageListViewBinder implements ObjectBindAdapter.ViewBinder<Photo> {
        private List<Photo> photos;

        public ImageListViewBinder(List<Photo> photos) {
            this.photos = photos;
        }

        @Override
        public void setViewValue(View view, Photo photo, int position) {
            ReplyImageViewHolder holder = (ReplyImageViewHolder) view.getTag();
            if (holder == null) {
                holder = new ReplyImageViewHolder();
                holder.imageView = (ImageView) view.findViewById(R.id.image_view);
                holder.headView = view.findViewById(R.id.head_view);
                holder.footView = view.findViewById(R.id.foot_view);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder
                        .imageView.getLayoutParams();
                params.width = listImgSize;
                params.height = listImgSize;
                view.setTag(holder);
            }

            if (position != 0) {
                holder.headView.setVisibility(View.VISIBLE);
            } else {
                holder.headView.setVisibility(View.GONE);
            }
            if (this.photos.size() - 1 == position) {
                holder.footView.setVisibility(View.VISIBLE);
            } else {
                holder.footView.setVisibility(View.GONE);
            }


            String url = ImageUtil.getImagePath(photo.getImagePath(), listImgSize);
            Glide.with(context)
                    .load(url)
                    .apply(new RequestOptions()
                            .dontAnimate())
                    .into(holder.imageView);
        }
    }

    private class ReplyImageViewHolder {
        ImageView imageView;
        View headView;
        View footView;
    }

    class EmptyViewHolder extends BaseViewHolder<CommunityPost> {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void setViewData(
                Context mContext, CommunityPost item, int position, int viewType) {

        }
    }

    public interface OnPraisePostListener {
        void onPraisePost(
                int position,
                CommunityPost post,
                CheckableLinearLayoutButton checkPraised,
                ImageView imgThumb,
                TextView tvPraisedCount,
                TextView tvAdd);
    }

    public interface OnPraiseGroupPhotoListener {
        void onPraiseGroup(
                int position,
                WeddingPhotoItem item,
                CheckableLinearLayoutButton checkPraised,
                ImageView imgThumb,
                TextView tvPraisedCount,
                TextView tvAdd);
    }

    public interface OnItemClickListener {
        void onItemClick(int position, CommunityPost post);
    }

    public interface OnItemReplyListener {
        void onItemReply(int position, CommunityPost post);
    }

    public interface OnTabViewCheckedListener {
        void onAllChecked();

        void onOwnerChecked();

        void onOrderMenuClicked(View view);
    }
}
