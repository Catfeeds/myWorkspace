package me.suncloud.marrymemo.adpter.user;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.user.viewholder.UserCommentViewHolder;

/**
 * 用户主页-用户评价列表adapter
 * Created by chen_bin on 2017/6/12 0012.
 */
public class UserCommentListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View footerView;
    private List<ServiceComment> comments;
    private LayoutInflater inflater;
    private boolean isShowMenu;
    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_FOOTER = 1;
    private OnItemClickListener onItemClickListener;
    private UserCommentViewHolder.OnMenuListener onMenuListener;

    public UserCommentListAdapter(
            Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<ServiceComment> getComments() {
        return comments;
    }

    public void setComments(List<ServiceComment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public void addComments(List<ServiceComment> comments) {
        if (!CommonUtil.isCollectionEmpty(comments)) {
            int start = getItemCount() - getFooterViewCount();
            this.comments.addAll(comments);
            notifyItemRangeInserted(start, comments.size());
        }
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setShowMenu(boolean showMenu) {
        this.isShowMenu = showMenu;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnMenuListener(UserCommentViewHolder.OnMenuListener onMenuListener) {
        this.onMenuListener = onMenuListener;
    }

    @Override
    public int getItemCount() {
        return getFooterViewCount() + CommonUtil.getCollectionSize(comments);
    }

    @Override
    public int getItemViewType(int position) {
        if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
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
                UserCommentViewHolder commentViewHolder = new UserCommentViewHolder(inflater
                        .inflate(
                        R.layout.user_comment_list_item,
                        parent,
                        false));
                commentViewHolder.setOnItemClickListener(onItemClickListener);
                commentViewHolder.setOnMenuListener(onMenuListener);
                return commentViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                if (holder instanceof UserCommentViewHolder) {
                    UserCommentViewHolder commentViewHolder = (UserCommentViewHolder) holder;
                    commentViewHolder.setShowMenu(isShowMenu);
                    commentViewHolder.setShowTopLineView(position != 0);
                    commentViewHolder.setView(context, comments.get(position), position, viewType);
                }
                break;
        }
    }
}
