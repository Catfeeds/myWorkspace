package com.hunliji.marrybiz.adapter.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.marrybiz.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hua_rong on 2017/7/7.
 * 轻松聊编辑
 */

public class EasyChatEditAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private Context context;
    private List<String> dataList;
    private LayoutInflater mLayoutInflater;
    private SparseBooleanArray sparseBooleanArray;
    private String speech;

    public void setSpeech(String speech) {
        this.speech = speech;
    }

    public EasyChatEditAdapter(Context context, List<String> dataList) {
        this.context = context;
        this.dataList = dataList;
        mLayoutInflater = LayoutInflater.from(context);
        sparseBooleanArray = new SparseBooleanArray();
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
        if (!TextUtils.isEmpty(speech)) {
            if (dataList.contains(speech)) {
                int position = dataList.indexOf(speech);
                sparseBooleanArray.put(position, true);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_easy_chat_edit, parent, false);
        return new EasyChatEditViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof EasyChatEditViewHolder) {
            EasyChatEditViewHolder viewHolder = (EasyChatEditViewHolder) holder;
            viewHolder.setView(context,
                    dataList.get(position),
                    position,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }


    class EasyChatEditViewHolder extends BaseViewHolder<String> {

        @BindView(R.id.iv_check)
        ImageView ivCheck;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.view_line)
        View viewLine;

        EasyChatEditViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sparseBooleanArray.clear();
                    sparseBooleanArray.put(getAdapterPosition(), true);
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.onItemSelect(getAdapterPosition());
                    }
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        protected void setViewData(Context mContext, String speech, final int position, int viewType) {
            tvContent.setText(speech);
            viewLine.setVisibility(position == dataList.size() - 1 ? View.GONE : View.VISIBLE);
            ivCheck.setImageResource(sparseBooleanArray.get(position) ? R.drawable
                    .icon_easy_chat_selected : R.mipmap.icon_check_shop_cart_w);
        }
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    private OnItemSelectedListener onItemSelectedListener;

    public interface OnItemSelectedListener {
        void onItemSelect(int position);
    }

}
