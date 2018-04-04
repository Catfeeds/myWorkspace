package me.suncloud.marrymemo.adpter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
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
 * 热帖排行榜adapter
 * Created by chen_bin on 2016/12/20 0020.
 */
public class HotThreadRankListRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View footerView;
    private ArrayList<CommunityThread> threads;
    private int[] icons;
    private LayoutInflater inflater;
    private int imageSize;
    private int faceSize;
    private static final int ITEM_TYPE_LIST = 0;
    private static final int ITEM_TYPE_FOOTER = 1;
    private ContentLayoutChangeListener listener;

    public HotThreadRankListRecyclerAdapter(
            Context context, ArrayList<CommunityThread> threads) {
        this.context = context;
        this.threads = threads;
        this.inflater = LayoutInflater.from(context);
        this.imageSize = CommonUtil.dp2px(context, 86);
        this.faceSize = CommonUtil.dp2px(context, 18);
        TypedArray typedArray = context.getResources()
                .obtainTypedArray(R.array.ranking_icons);
        int len = typedArray.length();
        this.icons = new int[len];
        for (int i = 0; i < len; i++) {
            this.icons[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
    }

    //添加footerView
    public void addFooterView(View footerView) {
        if (this.footerView == null) {
            this.footerView = footerView;
            notifyItemInserted(getItemCount() - 1);
        } else {
            this.footerView = footerView;
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public void setItems(List<CommunityThread> items) {
        this.threads.clear();
        if (items != null) {
            this.threads.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void addItems(List<CommunityThread> items) {
        if (items != null && !items.isEmpty()) {
            int start = this.threads.size();
            if (start > 0) {
                notifyItemChanged(start - 1);
            }
            this.threads.addAll(items);
            notifyItemRangeInserted(start, items.size());
        }
    }

    @Override
    public int getItemCount() {
        return threads.size() + (footerView != null ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (footerView != null && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                return new HotThreadRankViewHolder(inflater.inflate(R.layout.common_rank_list_item,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                holder.setView(context, threads.get(position), position, viewType);
                break;
        }
    }

    public class HotThreadRankViewHolder extends BaseViewHolder<CommunityThread> {
        @BindView(R.id.img_cover)
        RoundedImageView imgCover;
        @BindView(R.id.img_rank)
        ImageView imgRank;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.line_layout)
        View lineLayout;

        public HotThreadRankViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tvName.setVisibility(View.VISIBLE);
        }

        @Override
        protected void setViewData(
                final Context context,
                final CommunityThread thread,
                final int position,
                int viewType) {
            lineLayout.setVisibility(position < threads.size() - 1 ? View.VISIBLE : View.GONE);
            if (position < icons.length) {
                imgRank.setVisibility(View.VISIBLE);
                imgRank.setImageResource(icons[position]);
            } else {
                imgRank.setVisibility(View.GONE);
            }
            String imagePath = null;
            if (!CommonUtil.isCollectionEmpty(thread.getShowPhotos())) {
                imagePath = ImagePath.buildPath(thread.getShowPhotos()
                        .get(0)
                        .getImagePath())
                        .width(imageSize)
                        .path();
            }
            Glide.with(context)
                    .load(imagePath)
                    .apply(new RequestOptions().dontAnimate())
                    .into(imgCover);
            tvTitle.setText(EmojiUtil.parseEmojiByText2(context, thread.getShowTitle(), faceSize));
            if (listener != null) {
                tvTitle.removeOnLayoutChangeListener(listener);
            }
            listener = new ContentLayoutChangeListener(tvTitle,
                    tvContent,
                    thread.getShowSubtitle());
            tvTitle.addOnLayoutChangeListener(listener);
            tvName.setText(thread.getAuthor()
                    .getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (thread.getId() > 0) {
                        Intent intent = new Intent(context, CommunityThreadDetailActivity.class);
                        intent.putExtra("id", thread.getId());
                        intent.putExtra("serial_no",
                                thread.getPost()
                                        .getSerialNo());
                        context.startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            });
        }
    }

    private class ContentLayoutChangeListener implements View.OnLayoutChangeListener {
        private TextView tvTitle;
        private TextView tvContent;
        private String content;

        public ContentLayoutChangeListener(TextView tvTitle, TextView tvContent, String content) {
            this.tvTitle = tvTitle;
            this.tvContent = tvContent;
            this.content = content;
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
            tvTitle.removeOnLayoutChangeListener(this);
            final Layout l = tvTitle.getLayout();
            if (l != null) {
                tvTitle.post(new Runnable() {
                    @Override
                    public void run() {
                        tvContent.setMaxLines(l.getLineCount() >= 2 ? 1 : 2);
                        tvContent.setText(EmojiUtil.parseEmojiByText2(context, content, faceSize));
                    }
                });
            }
        }
    }
}