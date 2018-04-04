package me.suncloud.marrymemo.adpter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.CommonThreadViewHolder;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.finder.viewholder.SubPageViewHolder;
import me.suncloud.marrymemo.model.themephotography.Guide;
import me.suncloud.marrymemo.util.CommunityTogglesUtil;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;

/**
 * Created by mo_yu on 2016/8/29.全部攻略Adapter
 */
public class GuideRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private ArrayList<Guide> guideLists;
    private static final String COMMUNITY_THREAD = "CommunityThread";
    private static final String SUB_PAGE = "SubPage";
    private static final int THREAD_TYPE = 1;
    private static final int SUB_PAGE_TYPE = 2;
    private static final int DEFAULT_TYPE = 3;
    private static final int FOOTER_TYPE = 4;
    private View footerView;
    private LayoutInflater inflater;
    private OnReplyItemClickListener onReplyItemClickListener;

    public GuideRecyclerAdapter(
            Context context, ArrayList<Guide> guideLists) {
        this.context = context;
        this.guideLists = guideLists;
        this.inflater = LayoutInflater.from(context);
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setOnReplyItemClickListener(OnReplyItemClickListener onReplyItemClickListener) {
        this.onReplyItemClickListener = onReplyItemClickListener;
    }

    public interface OnReplyItemClickListener {
        void onReply(
                CommunityThread item, int position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            case SUB_PAGE_TYPE:
                return new SubPageViewHolder(inflater.inflate(R.layout.sub_page_list_item,
                        parent,
                        false));
            case THREAD_TYPE:
                CommonThreadViewHolder commonThreadViewHolder = new CommonThreadViewHolder
                        (inflater.inflate(
                        R.layout.common_community_thread_list_item___cv,
                        parent,
                        false), CommonThreadViewHolder.COMMUNITY_GUIDE);
                commonThreadViewHolder.setShowChannelView(false);
                commonThreadViewHolder.setOnPraiseClickListener(new CommonThreadViewHolder
                        .OnPraiseClickListener() {
                    @Override
                    public void onPraiseClick(
                            CommunityThread thread,
                            CheckableLinearButton checkPraised,
                            ImageView imgThumbUp,
                            TextView tvPraiseCount,
                            TextView tvAdd) {
                        CommunityTogglesUtil.onNewCommunityThreadListPraise((Activity) context,
                                checkPraised,
                                imgThumbUp,
                                tvPraiseCount,
                                tvAdd,
                                thread);
                    }
                });
                commonThreadViewHolder.setOnReplyClickListener(new CommonThreadViewHolder
                        .OnReplyClickListener() {
                    @Override
                    public void onReply(CommunityThread item, int position) {
                        if (onReplyItemClickListener != null) {
                            onReplyItemClickListener.onReply(item, position);
                        }
                    }
                });
                return commonThreadViewHolder;
            default:
                return new ExtraBaseViewHolder(inflater.inflate(R.layout.empty_place_holder,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof ExtraBaseViewHolder || CommonUtil.isCollectionEmpty(guideLists)) {
            return;
        }
        holder.setView(context,
                guideLists.get(position)
                        .getEntity(),
                position,
                getItemViewType(position));
        if (holder instanceof CommonThreadViewHolder) {
            CommonThreadViewHolder threadHolder = (CommonThreadViewHolder) holder;
            threadHolder.setShowBottomThickLineView(position < guideLists.size() - 1);
            threadHolder.setOnItemClickListener(new OnItemClickListener<CommunityThread>() {
                @Override
                public void onItemClick(int position, CommunityThread communityThread) {
                    Activity activity = (Activity) context;
                    if (communityThread.getId() != 0) {
                        Intent intent = new Intent(context, CommunityThreadDetailActivity.class);
                        intent.putExtra("id", communityThread.getId());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            });
        } else if (holder instanceof SubPageViewHolder) {
            SubPageViewHolder subPageHolder = (SubPageViewHolder) holder;
            subPageHolder.setShowBottomLineView(position < guideLists.size() - 1);
        }
    }

    /**
     * 攻略类型 SubPage:专题 CommunityThread:帖子
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return FOOTER_TYPE;
        } else {
            if (guideLists != null) {
                switch (guideLists.get(position)
                        .getEntityType()) {
                    case COMMUNITY_THREAD:
                        return THREAD_TYPE;
                    case SUB_PAGE:
                        return SUB_PAGE_TYPE;
                    default:
                        break;
                }
            }
        }
        return DEFAULT_TYPE;
    }

    @Override
    public int getItemCount() {
        return guideLists.size() + (footerView == null ? 0 : 1);
    }
}