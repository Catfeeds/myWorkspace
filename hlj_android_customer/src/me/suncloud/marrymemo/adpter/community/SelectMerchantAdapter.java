package me.suncloud.marrymemo.adpter.community;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * Created by mo_yu on 2017/5/3.选择商家
 */

public class SelectMerchantAdapter extends RecyclerView.Adapter<BaseViewHolder<Merchant>> {

    private static final int ITEM_TYPE = 1;
    private static final int HEADER_TYPE = 2;
    private static final int FOOTER_TYPE = 3;
    private static final int CUSTOM_MERCHANT_TYPE = 4;

    private View headerView;
    private View footerView;
    private View customMerchantView;
    private Context context;
    private List<Merchant> list;
    public OnItemClickListener onItemClickListener;

    public SelectMerchantAdapter(Context context, List<Merchant> list) {
        this.context = context;
        this.list = list;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setCustomMerchantView(View customMerchantView) {
        this.customMerchantView = customMerchantView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public BaseViewHolder<Merchant> onCreateViewHolder(
            ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_TYPE:
                return new ExtraBaseViewHolder(headerView);
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            case CUSTOM_MERCHANT_TYPE:
                return new ExtraBaseViewHolder(customMerchantView);
            default:
                return new MerchantViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.select_merchant_list_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<Merchant> holder, int position) {
        if (holder instanceof MerchantViewHolder) {
            holder.setView(context,
                    list.get(headerView == null ? position : position - 1),
                    position,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0) + (headerView != null ? 1 : 0) + (footerView !=
                null ? 1 : 0) + (customMerchantView != null ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerView != null) {
            return HEADER_TYPE;
        } else if (customMerchantView != null && position == getItemCount() - 1) {
            return CUSTOM_MERCHANT_TYPE;
        } else if (footerView != null && (position == getItemCount() - 2 || (customMerchantView ==
                null && position == getItemCount() - 1))) {
            return FOOTER_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }

    class MerchantViewHolder extends BaseViewHolder<Merchant> {
        @BindView(R.id.top_line_layout)
        View topLineLayout;
        @BindView(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @BindView(R.id.tv_merchant_address)
        TextView tvMerchantAddress;
        @BindView(R.id.merchant_view)
        View merchantView;

        MerchantViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(
                Context mContext, final Merchant item, final int position, int viewType) {
            topLineLayout.setVisibility(position == (headerView == null ? 0 : 1) ? View.GONE :
                    View.VISIBLE);
            if (item != null) {
                tvMerchantName.setText(item.getName());
                tvMerchantAddress.setText(item.getAddress());
                merchantView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(position, item);
                        }
                    }
                });
            }
        }
    }
}
