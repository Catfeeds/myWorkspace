package com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders.AuthItemViewHolder;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.AuthItem;

import java.util.List;


/**
 * 授信认证项adapter
 * Created by chen_bin on 2017/8/10 0010.
 */
public class AuthItemListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View headerView;

    private List<AuthItem> authItems;

    private int groupIndex = -1;

    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;

    private final static int ITEM_TYPE_HEADER = 0;
    private final static int ITEM_TYPE_GROUP_INDEX = 1;
    private final static int ITEM_TYPE_LIST = 2;

    public AuthItemListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setAuthItems(List<AuthItem> authItems) {
        this.authItems = authItems;
        notifyDataSetChanged();
    }

    private int getHeaderViewCount() {
        return headerView != null ? 1 : 0;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public int getGroupIndexViewCount() {
        return groupIndex > -1 ? 1 : 0;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return getHeaderViewCount() + getGroupIndexViewCount() + CommonUtil.getCollectionSize(
                authItems);
    }

    @Override
    public int getItemViewType(int position) {
        if (getHeaderViewCount() > 0 && position == 0) {
            return ITEM_TYPE_HEADER;
        } else if (getGroupIndexViewCount() > 0 && position == getHeaderViewCount() + groupIndex) {
            return ITEM_TYPE_GROUP_INDEX;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case ITEM_TYPE_GROUP_INDEX:
                return new ExtraBaseViewHolder(inflater.inflate(R.layout
                                .auth_item_group_index_item___pay,
                        parent,
                        false));
            default:
                AuthItemViewHolder authItemViewHolder = new AuthItemViewHolder(inflater.inflate(R
                                .layout.auth_item_list_item___pay,
                        parent,
                        false));
                authItemViewHolder.setOnItemClickListener(onItemClickListener);
                return authItemViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                if (holder instanceof AuthItemViewHolder) {
                    int index = position - getHeaderViewCount();
                    if (index > groupIndex) {
                        index = index - getGroupIndexViewCount();
                    }
                    AuthItemViewHolder authItemViewHolder = (AuthItemViewHolder) holder;
                    authItemViewHolder.setShowBottomLineView(index < authItems.size() - 1 &&index != groupIndex - 1);
                    authItemViewHolder.setView(context,authItems.get(index), index, viewType);
                }
                break;
        }
    }
}