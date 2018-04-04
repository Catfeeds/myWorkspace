package me.suncloud.marrymemo.adpter.community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
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
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;
import me.suncloud.marrymemo.view.community.RichThreadListActivity;

/**
 * Created by mo_yu on 2016/9/22.精编话题Adapter
 */
public class RichThreadAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private ArrayList<CommunityThread> threads;


    private static final int ITEM_TYPE = 1;//
    private static final int DEFAULT_TYPE = 2;//空视图，防止出现无法解析的数据时，显示异常
    private static final int FOOTER_TYPE = 3;
    private static final int HEADER_TYPE = 4;

    private DisplayMetrics dm;
    private int headWidth;

    private View headerView;
    private View footerView;

    public RichThreadAdapter(
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
                return new RichViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rich_thread_recycler_item, parent, false));
            default:
                return new ExtraBaseViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty_place_holder, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof RichViewHolder) {
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
        } else if (position == getItemCount() - 1 && footerView != null) {
            return HEADER_TYPE;
        } else {
            return ITEM_TYPE;
        }
        //        return DEFAULT_TYPE;
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
            int width = CommonUtil.getDeviceSize(itemView.getContext()).x * 5 / 8;
            int height = width * 9 / 16;
            ivMoreCover.getLayoutParams().width = width;
            ivMoreCover.getLayoutParams().height = height;
            moreView.getLayoutParams().width = width;
            moreView.getLayoutParams().height = height;
        }

        @Override
        protected void setViewData(
                final Context mContext, Object item, int position, int viewType) {
            tvMoreName.setText(mContext.getString(R.string.label_more_rich_thread));
            final Activity activity = (Activity) mContext;
            moreView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(activity, RichThreadListActivity.class);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            });
        }
    }

    class RichViewHolder extends BaseViewHolder<CommunityThread> {
        @BindView(R.id.iv_rich_thread)
        RoundedImageView ivRichThread;
        @BindView(R.id.iv_rich_thread_cover)
        ImageView ivRichThreadCover;
        @BindView(R.id.tv_rich_thread_title)
        TextView tvRichThreadTitle;
        @BindView(R.id.tv_rich_thread_count)
        TextView tvRichThreadCount;
        @BindView(R.id.rich_thread_view)
        RelativeLayout richThreadView;

        int width;
        int height;

        RichViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.width = CommonUtil.getDeviceSize(view.getContext()).x * 3 / 4;
            this.height = width * 9 / 16;
            ivRichThread.getLayoutParams().width = width;
            ivRichThread.getLayoutParams().height = height;
            ivRichThreadCover.getLayoutParams().width = width;
            ivRichThreadCover.getLayoutParams().height = height;
            richThreadView.getLayoutParams().width = width;
            richThreadView.getLayoutParams().height = height;
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
                if (CommonUtil.getDeviceSize(mContext).x <= 720) {
                    tvRichThreadTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                } else {
                    tvRichThreadTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                }
                tvRichThreadCount.setText(mContext.getString(R.string.fmt_watch_count___qa,
                        String.valueOf(item.getClickCount())));
                tvRichThreadTitle.setText(EmojiUtil.parseEmojiByText2(mContext,
                        item.getPages()
                                .getTitle(),
                        CommonUtil.dp2px(mContext, 20)));
                richThreadView.setOnClickListener(new View.OnClickListener() {
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

