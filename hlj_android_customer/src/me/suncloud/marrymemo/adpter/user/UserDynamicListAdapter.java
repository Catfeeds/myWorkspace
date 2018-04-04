package me.suncloud.marrymemo.adpter.user;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.user.viewholder.UserDynamicViewHolder;
import me.suncloud.marrymemo.model.user.UserDynamic;

/**
 * 用户动态adapter
 * Created by chen_bin on 2017/11/28 0028.
 */
public class UserDynamicListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View footerView;
    private List<UserDynamic> dynamics;
    private LayoutInflater inflater;
    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_FOOTER = 1;

    public UserDynamicListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setDynamics(List<UserDynamic> dynamics) {
        this.dynamics = dynamics;
        notifyDataSetChanged();
    }

    public void addDynamics(List<UserDynamic> dynamics) {
        if (!CommonUtil.isCollectionEmpty(dynamics)) {
            int start = getItemCount() - getFooterViewCount();
            this.dynamics.addAll(dynamics);
            notifyItemRangeInserted(start, dynamics.size());
        }
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public int getItemCount() {
        return getFooterViewCount() + CommonUtil.getCollectionSize(dynamics);
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
                return new UserDynamicViewHolder(inflater.inflate(R.layout.user_dynamic_list_item,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                if (holder instanceof UserDynamicViewHolder) {
                    UserDynamicViewHolder dynamicViewHolder = (UserDynamicViewHolder) holder;
                    dynamicViewHolder.setShowTopLineView(position > 0);
                    dynamicViewHolder.setView(context, dynamics.get(position), position, viewType);
                }
                break;
        }
    }

}
