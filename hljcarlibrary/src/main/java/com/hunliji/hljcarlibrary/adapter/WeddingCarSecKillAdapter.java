package com.hunliji.hljcarlibrary.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.adapter.viewholder.WeddingCarSecKillViewHolder;
import com.hunliji.hljcarlibrary.models.SecKill;
import com.hunliji.hljcarlibrary.views.activities.WeddingCarProductDetailActivity;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinxin on 2018/1/3 0003.
 */

public class WeddingCarSecKillAdapter extends RecyclerView.Adapter<BaseViewHolder<SecKill>>
        implements OnItemClickListener<SecKill>, WeddingCarSecKillViewHolder
        .onWeddingCarSecKillClickListener {

    private Context mContext;
    private LayoutInflater inflater;
    private List<SecKill> secKillList;
    private onWeddingCarSecKillAdapterListener onWeddingCarSecKillAdapterListener;
    private OnItemClickListener<SecKill> onItemClickListener;

    public WeddingCarSecKillAdapter(Context mContext) {
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
        this.secKillList = new ArrayList<>();
    }

    public void setSecKillList(List<SecKill> secKillList) {
        this.secKillList.clear();
        if (secKillList != null) {
            this.secKillList.addAll(secKillList);
        }
        notifyDataSetChanged();
    }

    public void setOnWeddingCarSecKillAdapterListener(
            WeddingCarSecKillAdapter.onWeddingCarSecKillAdapterListener
                    onWeddingCarSecKillAdapterListener) {
        this.onWeddingCarSecKillAdapterListener = onWeddingCarSecKillAdapterListener;
    }

    public void setOnItemClickListener(OnItemClickListener<SecKill> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public BaseViewHolder<SecKill> onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = inflater.inflate(R.layout.wedding_car_sec_kill_item___car,
                viewGroup,
                false);
        WeddingCarSecKillViewHolder carSecKillViewHolder = new WeddingCarSecKillViewHolder
                (itemView);
        carSecKillViewHolder.setOnItemClickListener(this);
        carSecKillViewHolder.setOnWeddingCarSecKillClickListener(this);
        return carSecKillViewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<SecKill> holder, int i) {
        holder.setView(mContext, secKillList.get(i), i, 0);
    }

    @Override
    public int getItemCount() {
        return secKillList.size();
    }

    @Override
    public void onItemClick(int position, SecKill secKill) {


        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(position, secKill);
        }
    }

    @Override
    public void onBtnNow(SecKill secKill) {
        if (onWeddingCarSecKillAdapterListener != null) {
            onWeddingCarSecKillAdapterListener.onBtnNow(secKill);
        }
    }

    public interface onWeddingCarSecKillAdapterListener {
        void onBtnNow(SecKill secKill);
    }
}
