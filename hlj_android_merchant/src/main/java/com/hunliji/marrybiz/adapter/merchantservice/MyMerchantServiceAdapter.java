package com.hunliji.marrybiz.adapter.merchantservice;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.merchantservice.viewholder.MerchantServerViewHolder;
import com.hunliji.marrybiz.model.merchantservice.MerchantServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinxin on 2018/1/24 0024.
 */

public class MyMerchantServiceAdapter extends RecyclerView.Adapter<BaseViewHolder> implements
        MerchantServerViewHolder.MerchantServerViewHolderClickListener {

    private final int TYPE_ITEM = 10;
    private final int TYPE_FOOTER = 11;

    private Context mContext;
    private LayoutInflater inflater;
    private List<MerchantServer> serverList;
    private View footerView;
    private onMerchantServerAdapterClickListener onMerchantServerAdapterClickListener;

    public MyMerchantServiceAdapter(Context mContext){
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        serverList = new ArrayList<>();
    }

    public void setOnMerchantServerAdapterClickListener(MyMerchantServiceAdapter
                                                                .onMerchantServerAdapterClickListener onMerchantServerAdapterClickListener) {
        this.onMerchantServerAdapterClickListener = onMerchantServerAdapterClickListener;
    }

    public void setServerList(List<MerchantServer> serverList) {
        this.serverList.clear();
        if (!CommonUtil.isCollectionEmpty(serverList)) {
            this.serverList.addAll(serverList);
        }
        notifyDataSetChanged();
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case TYPE_ITEM:
                itemView = inflater.inflate(R.layout.merchant_server_list_item, parent, false);
                MerchantServerViewHolder holder = new MerchantServerViewHolder(itemView);
                holder.setMerchantServerViewHolderClickListener(this);
                return holder;
            case TYPE_FOOTER:
                if (footerView != null) {
                    return new ExtraBaseViewHolder(footerView);
                }
                return null;
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof MerchantServerViewHolder) {
            MerchantServerViewHolder serverViewHolder = (MerchantServerViewHolder) holder;
            serverViewHolder.setLineVisible(position != getItemCount() - 1 - 1);
            serverViewHolder.setView(mContext, serverList.get(position), position, TYPE_ITEM);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return CommonUtil.isCollectionEmpty(serverList) ? 0 : CommonUtil.getCollectionSize(
                serverList) + 1;
    }

    @Override
    public void onLook(MerchantServer server) {
        if(onMerchantServerAdapterClickListener != null){
            onMerchantServerAdapterClickListener.onLook(server);
        }
    }

    @Override
    public void onUse(MerchantServer server) {
        if(onMerchantServerAdapterClickListener != null){
            onMerchantServerAdapterClickListener.onUse(server);
        }
    }

    @Override
    public void onFeed(MerchantServer server) {
        if(onMerchantServerAdapterClickListener != null){
            onMerchantServerAdapterClickListener.onFeed(server);
        }
    }

    @Override
    public void onUpdate(MerchantServer server) {
        if (onMerchantServerAdapterClickListener != null) {
            onMerchantServerAdapterClickListener.onUpdate(server);
        }
    }

    public interface onMerchantServerAdapterClickListener {
        void onLook(MerchantServer server);

        void onUse(MerchantServer server);

        void onFeed(MerchantServer server);

        void onUpdate(MerchantServer server);
    }

}
