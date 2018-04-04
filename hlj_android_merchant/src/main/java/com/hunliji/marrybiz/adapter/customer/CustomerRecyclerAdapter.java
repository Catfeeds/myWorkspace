package com.hunliji.marrybiz.adapter.customer;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.customer.MerchantCustomer;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jinxin on 2017/8/14 0014.
 */

public class CustomerRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    final int ITEM_FOOTER = 10;
    final int ITEM_ITEM = 11;

    public static final int ITEM_NAME = 12;
    public static final int ITEM_PHONE = 13;

    private Context mContext;
    private List<MerchantCustomer> customerList;
    private View footerView;
    private OnItemClickListener<MerchantCustomer> onItemClickListener;
    private String searchWord;
    private int type;

    public CustomerRecyclerAdapter(Context mContext) {
        customerList = new ArrayList<>();
        this.mContext = mContext;
        this.type = ITEM_NAME;
    }

    public void setCustomerList(List<MerchantCustomer> customerList) {
        this.customerList.clear();
        if (!CommonUtil.isCollectionEmpty(customerList)) {
            this.customerList.addAll(customerList);
        }
        setFooterCount();
        notifyDataSetChanged();
    }

    public void addCustomerList(List<MerchantCustomer> customerList) {
        if (!CommonUtil.isCollectionEmpty(customerList)) {
            int start = getItemCount() - (footerView != null ? 1 : 0);
            this.customerList.addAll(customerList);
            setFooterCount();
            notifyItemRangeInserted(start, customerList.size());
        }
    }

    public void clearItems() {
        this.customerList.clear();
        notifyDataSetChanged();
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setOnItemClickListener(OnItemClickListener<MerchantCustomer> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            case ITEM_ITEM:
                View itemView = LayoutInflater.from(mContext)
                        .inflate(R.layout.customer_detail_item, parent, false);
                return new ItemViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        if (viewType == ITEM_ITEM) {
            holder.setView(mContext, customerList.get(position), position, viewType);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position, customerList.get(position));
                    }
                }
            });
        } else if (viewType == ITEM_FOOTER) {
            setFooterCount();
        }
    }

    private void setFooterCount() {
        if (footerView == null) {
            return;
        }
        TextView endView = (TextView) footerView.findViewById(R.id.no_more_hint);
        endView.setText(customerList.size() + "个客资");
        endView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemViewType(int position) {
        return position == getItemCount() - 1 ? ITEM_FOOTER : ITEM_ITEM;
    }

    @Override
    public int getItemCount() {
        return customerList.isEmpty() ? 0 : (customerList.size() + (footerView == null ? 0 : 1));
    }

    class ItemViewHolder extends BaseViewHolder<MerchantCustomer> {

        @BindView(R.id.img_avatar)
        RoundedImageView imgAvatar;
        @BindView(R.id.tv_account_name)
        TextView tvAccountName;
        @BindView(R.id.line)
        View line;
        @BindView(R.id.vip_logo)
        ImageView vipLogo;

        int width;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            width = CommonUtil.dp2px(mContext, 44);
        }

        @Override
        public void setViewData(
                Context mContext, MerchantCustomer customer, int position, int viewType) {
            if (customer == null || customer.getId() <= 0) {
                return;
            }
            if (customer.getUser() != null) {
                Glide.with(mContext)
                        .load(ImagePath.buildPath(customer.getUser()
                                .getAvatar())
                                .width(width)
                                .height(width)
                                .cropPath())
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_avatar_primary))
                        .into(imgAvatar);
            }
            String name = null;
            if (type == ITEM_NAME) {
                name = customer.getUserName();
                if (customer.getUser() != null) {
                    if (TextUtils.isEmpty(name)) {
                        name = customer.getUser()
                                .getNick();
                    }
                }
            } else if (type == ITEM_PHONE) {
                name = customer.getUserPhone();
                if (TextUtils.isEmpty(name) && customer.getUser() != null) {
                    name = customer.getUser()
                            .getPhone();
                }
            }
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(searchWord) && name.contains(
                    searchWord)) {
                int index = name.indexOf(searchWord);
                int len = searchWord.length();
                ForegroundColorSpan span = new ForegroundColorSpan(ContextCompat.getColor(mContext,
                        R.color.colorPrimary));
                SpannableString spanString = new SpannableString(name);
                spanString.setSpan(span, index, index + len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvAccountName.setText(spanString);
            } else {
                tvAccountName.setText(name);
            }
            line.setVisibility(position < getItemCount() - 1 ? View.VISIBLE : View.GONE);
            User user = customer.getUser();
            if (user != null && user.getExtend() != null && user.getExtend()
                    .getHljMemberPrivilege() > 0) {
                vipLogo.setVisibility(View.VISIBLE);
            } else {
                vipLogo.setVisibility(View.GONE);
            }
        }
    }
}
