package me.suncloud.marrymemo.adpter.community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPost;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker
        .TrackerCommunityThreadViewHolder;
import com.hunliji.hljcommonviewlibrary.models.CommunityFeed;
import com.hunliji.hljemojilibrary.EmojiUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.MarqueeModel;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;
import me.suncloud.marrymemo.widget.MarqueeWithImageView;

/**
 * Created by mo_yu on 2018/3/12.今日热门话题
 */

public class HotCommunityThreadAdapter extends RecyclerView
        .Adapter<BaseViewHolder<CommunityThread>> {

    private Context context;
    private List<CommunityFeed> list;
    public static final int ITEM_TYPE = 0;
    private static final int DEFAULT_TYPE = 1;

    public HotCommunityThreadAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<CommunityFeed> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder<CommunityThread> onCreateViewHolder(
            ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.community_hot_thread_list_item, parent, false);
            return new ThreadViewHolder(view);
        }
        return new ExtraBaseViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.empty_place_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<CommunityThread> holder, int position) {
        if (holder instanceof ThreadViewHolder) {
            holder.setView(context,
                    (CommunityThread) list.get(position)
                            .getEntity(),
                    position,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position)
                .getEntityType()
                .equals(CommunityFeed.COMMUNITY_THREAD)) {
            return ITEM_TYPE;
        }
        return DEFAULT_TYPE;
    }

    public class ThreadViewHolder extends TrackerCommunityThreadViewHolder {

        @BindView(R.id.tv_thread_title)
        TextView tvThreadTitle;
        @BindView(R.id.mv_thread_comment)
        MarqueeWithImageView mvThreadComment;
        @BindView(R.id.card_view)
        LinearLayout cardView;
        @BindView(R.id.img_discuss_gif)
        ImageView imgDiscussGif;
        private List<MarqueeModel> commentLists;
        private int faceSize;

        public ThreadViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            commentLists = new ArrayList<>();
            faceSize = CommonUtil.dp2px(context, 16);
            int itemWidth = Math.round((CommonUtil.getDeviceSize(itemView.getContext()).x -
                    CommonUtil.dp2px(
                    itemView.getContext(),
                    16)) / 1.2f);
            cardView.getLayoutParams().width = itemWidth;
            cardView.getLayoutParams().height = (itemWidth - CommonUtil.dp2px(context,
                    14)) * 180 / 540 + CommonUtil.dp2px(itemView.getContext(), 14);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goCommunityThreadDetail(v.getContext());
                }
            });
            mvThreadComment.setOnItemClickListener(new MarqueeWithImageView.OnItemClickListener() {
                @Override
                public void onItemClick(int position, TextView textView) {
                    goCommunityThreadDetail(itemView.getContext());
                }
            });
        }

        private void goCommunityThreadDetail(Context context) {
            Activity activity = (Activity) context;
            Intent intent = new Intent();
            CommunityThread communityThread = getItem();
            intent.setClass(activity, CommunityThreadDetailActivity.class);
            intent.putExtra(CommunityThreadDetailActivity.ARG_ID, communityThread.getId());
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }

        @Override
        protected void setViewData(
                Context mContext, CommunityThread item, int position, int viewType) {
            Glide.with(mContext)
                    .load(R.drawable.gif_discuss_primary)
                    .into(imgDiscussGif);
            tvThreadTitle.setText(EmojiUtil.parseEmojiByText2(mContext,
                    "#" + item.getShowTitle(),
                    faceSize));
            if (!CommonUtil.isCollectionEmpty(item.getPosts())) {
                int size = Math.min(item.getPosts()
                        .size(), 10);
                for (int i = 0; i < size; i++) {
                    CommunityPost post = item.getPosts()
                            .get(i);
                    MarqueeModel marqueeModel = new MarqueeModel();
                    marqueeModel.setHasImage(true);
                    marqueeModel.setTitle(EmojiUtil.parseEmojiByText2(mContext,
                            post.getMessage(),
                            faceSize));
                    marqueeModel.setImagePath(post.getAuthor()
                            .getAvatar());
                    commentLists.add(marqueeModel);
                }
                mvThreadComment.startWithList(commentLists);
            }
        }

        @Override
        public View trackerView() {
            return itemView;
        }
    }
}
