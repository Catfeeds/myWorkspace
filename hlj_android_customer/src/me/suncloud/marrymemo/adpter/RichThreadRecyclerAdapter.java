package me.suncloud.marrymemo.adpter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;

/**
 * Created by mo_yu on 2016/9/22.精编话题Adapter
 */
public class RichThreadRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private ArrayList<CommunityThread> communityThreads;

    private static final int RICH_THREAD = 1;
    private static final int DEFAULT_TYPE = 2;//空视图，防止出现无法解析的数据时，显示异常
    private static final int FOOTER_TYPE = 3;

    private int richImgWidth;
    private int richImgHeight;
    private int faceSize;


    private View footerView;

    public RichThreadRecyclerAdapter(
            Context context, ArrayList<CommunityThread> communityThreads) {
        this.context = context;
        this.communityThreads = communityThreads;
        initSize();
    }

    private void initSize() {
        richImgWidth = CommonUtil.getDeviceSize(context).x * 131 / 320;
        richImgHeight = richImgWidth * 9 / 16;
        faceSize = CommonUtil.dp2px(context, 20);
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            case RICH_THREAD:
                return new RichThreadViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rich_thread_list_item, parent, false));
            default:
                return new ExtraBaseViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty_place_holder, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof RichThreadViewHolder && communityThreads != null) {
            holder.setView(context,
                    communityThreads.get(position),
                    position,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return FOOTER_TYPE;
        } else {
            if (communityThreads != null) {
                if (communityThreads.get(position) != null) {
                    return RICH_THREAD;
                }
            }
        }
        return DEFAULT_TYPE;
    }

    @Override
    public int getItemCount() {
        return communityThreads.size() + (footerView == null ? 0 : 1);
    }

    class RichThreadViewHolder extends BaseViewHolder<CommunityThread> {

        @BindView(R.id.iv_rich_thread)
        ImageView ivRichThread;
        @BindView(R.id.tv_rich_thread_channel)
        TextView tvRichThreadChannel;
        @BindView(R.id.tv_rich_thread_watch_count)
        TextView tvRichThreadWatchCount;
        @BindView(R.id.tv_rich_thread_title)
        TextView tvRichThreadTitle;
        @BindView(R.id.rich_thread_item_view)
        RelativeLayout richThreadItemView;
        @BindView(R.id.bottom_line)
        View bottomLine;
        @BindView(R.id.iv_hot_tag)
        ImageView ivHotTag;

        RichThreadViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            ivRichThread.getLayoutParams().width = richImgWidth;
            ivRichThread.getLayoutParams().height = richImgHeight;
        }

        @Override
        protected void setViewData(
                final Context mContext,
                final CommunityThread item,
                final int position,
                int viewType) {

            if (item != null) {
                if (position == communityThreads.size() - 1) {
                    bottomLine.setVisibility(View.GONE);
                } else {
                    bottomLine.setVisibility(View.VISIBLE);
                }
                if (item.getPages() != null) {
                    String url = ImageUtil.getImagePath(item.getPages()
                            .getImgPath(), richImgWidth);
                    if (!TextUtils.isEmpty(url)) {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(url)
                                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                                .into(ivRichThread);
                    } else {
                        Glide.with(mContext)
                                .clear(ivRichThread);
                        ivRichThread.setImageBitmap(null);
                    }
                    tvRichThreadWatchCount.setText(mContext.getString(R.string.fmt_join_count___qa,
                            item.getClickCount()));
                    tvRichThreadTitle.setText(EmojiUtil.parseEmojiByText2(mContext,
                            item.getPages()
                                    .getTitle(),
                            faceSize));
                    richThreadItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context,
                                    CommunityThreadDetailActivity.class);
                            intent.putExtra("id", item.getId());
                            mContext.startActivity(intent);
                        }
                    });
                }
                if (item.isRewriteStyle()) {
                    ivHotTag.setVisibility(View.VISIBLE);
                } else {
                    ivHotTag.setVisibility(View.GONE);
                }

                if (item.getChannel() != null) {
                    tvRichThreadChannel.setText(item.getChannel()
                            .getTitle());
                }
            }
        }
    }
}

