package me.suncloud.marrymemo.adpter.community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.community.PrizeThreadListActivity;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;

/**
 * Created by mo_yu on 2016/9/22.有奖话题Adapter
 */
public class PrizeThreadAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private ArrayList<CommunityThread> threads;


    private static final int ITEM_TYPE = 1;
    private static final int DEFAULT_TYPE = 2;//空视图，防止出现无法解析的数据时，显示异常
    private static final int FOOTER_TYPE = 3;
    private static final int HEADER_TYPE = 4;

    private View headerView;
    private View footerView;

    public PrizeThreadAdapter(
            Context context, ArrayList<CommunityThread> threads) {
        this.context = context;
        this.threads = threads;
    }


    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_TYPE:
                return new ExtraBaseViewHolder(headerView);
            case FOOTER_TYPE:
                return new FooterViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.more_thread_footer_view, parent, false));
            case ITEM_TYPE:
                return new PrizeViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.prize_thread_recycler_item, parent, false));
            default:
                return new ExtraBaseViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty_place_holder, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof PrizeViewHolder) {
            holder.setView(context,
                    threads.get(headerView == null ? position : position - 1),
                    position,
                    getItemViewType(position));
        } else if (holder instanceof FooterViewHolder) {
            holder.setView(context, null, position, getItemViewType(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerView != null) {
            return HEADER_TYPE;
        } else if (position == getItemCount() - 1) {
            return FOOTER_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(threads.size(),
                5) + (footerView == null ? 0 : 1) + (headerView == null ? 0 : 1);
    }

    class FooterViewHolder extends BaseViewHolder {

        @BindView(R.id.iv_more_cover)
        ImageView ivMoreCover;
        @BindView(R.id.tv_more_name)
        TextView tvMoreName;
        @BindView(R.id.more_view)
        RelativeLayout moreView;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            int width = CommonUtil.dp2px(itemView.getContext(), 58);
            int height = CommonUtil.dp2px(itemView.getContext(), 71);
            moreView.getLayoutParams().width = width;
            moreView.getLayoutParams().height = height;
        }

        @Override
        protected void setViewData(
                final Context mContext, Object item, int position, int viewType) {
            final Activity activity = (Activity) mContext;
            moreView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(activity, PrizeThreadListActivity.class);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            });
        }
    }

    class PrizeViewHolder extends BaseViewHolder<CommunityThread> {
        @BindView(R.id.iv_rich_thread)
        RoundedImageView ivRichThread;
        @BindView(R.id.iv_rich_thread_cover)
        ImageView ivRichThreadCover;
        @BindView(R.id.tv_prize_thread_title)
        TextView tvPrizeThreadTitle;
        @BindView(R.id.prize_thread_view)
        RelativeLayout prizeThreadView;

        int width;
        int height;

        PrizeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.width = CommonUtil.dp2px(view.getContext(), 120);
            this.height = CommonUtil.dp2px(view.getContext(), 71);
            ivRichThread.getLayoutParams().width = width;
            ivRichThread.getLayoutParams().height = height;
            ivRichThreadCover.getLayoutParams().width = width;
            ivRichThreadCover.getLayoutParams().height = height;
            prizeThreadView.getLayoutParams().width = width;
            prizeThreadView.getLayoutParams().height = height;
        }

        @Override
        protected void setViewData(
                final Context mContext, final CommunityThread item, int position, int viewType) {
            if (item.getPages() != null) {
                String url = ImageUtil.getImagePath(item.getPages()
                        .getImgPath(), width);
                Glide.with(mContext)
                        .load(url)
                        .apply(new RequestOptions().dontAnimate()
                                .placeholder(R.mipmap.icon_empty_image))
                        .into(ivRichThread);
                tvPrizeThreadTitle.setText(EmojiUtil.parseEmojiByText2(mContext,
                        item.getPages()
                                .getTitle(),
                        CommonUtil.dp2px(mContext, 18)));
                prizeThreadView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, CommunityThreadDetailActivity.class);
                        intent.putExtra("id", item.getId());
                        mContext.startActivity(intent);
                    }
                });
            }
        }
    }

}

