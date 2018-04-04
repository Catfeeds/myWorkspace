package me.suncloud.marrymemo.adpter.community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityAuthor;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThreadPages;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker
        .TrackerCommunityThreadViewHolder;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;

/**
 * Created by mo_yu on 2018/3/14.社区活动相关帖子
 */

public class CommunityEventThreadAdapter extends RecyclerView
        .Adapter<BaseViewHolder<CommunityThread>> {

    private Context context;
    private List<CommunityThread> list;
    public static final int ITEM_TYPE = 0;

    public CommunityEventThreadAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<CommunityThread> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder<CommunityThread> onCreateViewHolder(
            ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.community_event_thread_item, parent, false);
        return new EventThreadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<CommunityThread> holder, int position) {
        if (holder instanceof EventThreadViewHolder) {
            holder.setView(context, list.get(position), position, getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_TYPE;
    }

    public class EventThreadViewHolder extends TrackerCommunityThreadViewHolder {

        @BindView(R.id.img_event_thread)
        ImageView imgEventThread;
        @BindView(R.id.tv_event_thread_title)
        TextView tvEventThreadTitle;
        @BindView(R.id.riv_auth_avatar)
        RoundedImageView rivAuthAvatar;
        @BindView(R.id.tv_auth_name)
        TextView tvAuthName;
        @BindView(R.id.tv_click_count)
        TextView tvClickCount;
        @BindView(R.id.tv_post_count)
        TextView tvPostCount;
        private int eventImgWidth;
        private int eventImgHeight;
        private int faceSize;
        private int avatarSize;

        public EventThreadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            eventImgWidth = Math.round((CommonUtil.getDeviceSize(itemView.getContext()).x -
                    CommonUtil.dp2px(
                    itemView.getContext(),
                    24)) / 1.22f);
            eventImgHeight = eventImgWidth / 576 * 326;

            itemView.getLayoutParams().width = eventImgWidth;
            imgEventThread.getLayoutParams().width = eventImgWidth;
            imgEventThread.getLayoutParams().height = eventImgHeight;
            faceSize = CommonUtil.dp2px(itemView.getContext(), 14);
            avatarSize = CommonUtil.dp2px(itemView.getContext(), 20);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity) v.getContext();
                    Intent intent = new Intent();
                    CommunityThread communityThread = getItem();
                    intent.setClass(activity, CommunityThreadDetailActivity.class);
                    intent.putExtra(CommunityThreadDetailActivity.ARG_ID, communityThread.getId());
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            });
        }

        @Override
        public View trackerView() {
            return itemView;
        }

        @Override
        protected void setViewData(
                Context mContext, CommunityThread communityThread, int position, int viewType) {
            List<Photo> photos = communityThread.getShowPhotos();
            String title = communityThread.getShowTitle();
            String imagePath = null;
            if (!CommonUtil.isCollectionEmpty(photos)) {
                imagePath = ImagePath.buildPath(photos.get(0)
                        .getImagePath())
                        .width(eventImgWidth)
                        .height(eventImgHeight)
                        .cropPath();
            }
            Glide.with(context)
                    .load(imagePath)
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(imgEventThread);
            showThreadText(title, communityThread, mContext);
            setAuthView(mContext, communityThread.getAuthor());
            tvClickCount.setText(String.valueOf(communityThread.getClickCount()));
            tvPostCount.setText(String.valueOf(communityThread.getPostCount()));
        }

        /**
         * 话题作者
         */
        private void setAuthView(final Context context, final CommunityAuthor author) {
            String avatarUrl = ImagePath.buildPath(author.getAvatar())
                    .height(avatarSize)
                    .width(avatarSize)
                    .cropPath();
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
         * 话题标志
         */
        private void showThreadText(String title, CommunityThread item, Context mContext) {
            //话题标志
            if (TextUtils.isEmpty(title)) {
                tvEventThreadTitle.setVisibility(View.GONE);
            } else {
                if (item.isRefined() || item.getPages() != null) {
                    SpannableStringBuilder builder = EmojiUtil.parseEmojiByText2(mContext,
                            "  " + title,
                            faceSize);
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
                    tvEventThreadTitle.setText(builder);
                } else {
                    tvEventThreadTitle.setText(EmojiUtil.parseEmojiByText2(mContext,
                            title,
                            faceSize));
                }
            }
        }
    }

}
