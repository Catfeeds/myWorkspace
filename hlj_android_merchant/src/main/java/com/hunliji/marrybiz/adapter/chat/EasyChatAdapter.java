package com.hunliji.marrybiz.adapter.chat;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.easychat.Speech;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hua_rong on 2017/6/28.
 */

public class EasyChatAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private static final int ITEM_HEADER = 1;
    private static final int ITEM = 0;
    private View headerView;
    private String[] titles;
    private LayoutInflater mLayoutInflater;
    private Context context;

    public void setTitles(String[] titles) {
        this.titles = titles;
    }


    public void setDataList(List<Speech> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    private List<Speech> dataList;

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }


    public EasyChatAdapter(Context context, ArrayList<Speech> dataList) {
        this.dataList = dataList;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_HEADER:
                return new ExtraBaseViewHolder(headerView);
            default:
                View itemView = mLayoutInflater.inflate(R.layout.easy_chat_setting_item,
                        parent,
                        false);
                return new EasyChatViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof EasyChatViewHolder) {
            EasyChatViewHolder viewHolder = (EasyChatViewHolder) holder;
            viewHolder.setView(context,
                    getItem(position),
                    position - (headerView == null ? 0 : 1),
                    getItemViewType(position));
        }
    }

    private Speech getItem(int position) {
        return dataList.get(headerView == null ? position : position - 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView != null && position == 0) {
            return ITEM_HEADER;
        }
        return ITEM;
    }

    @Override
    public int getItemCount() {
        return (dataList == null ? 0 : dataList.size()) + (dataList != null && headerView != null
                ? 1 : 0);
    }

    public class EasyChatViewHolder extends BaseViewHolder<Speech> {

        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_state)
        TextView tvState;
        @BindView(R.id.iv_warning)
        ImageView ivWarning;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.line)
        View line;
        @BindView(R.id.ll_content)
        LinearLayout llContent;

        public EasyChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void setViewData(
                Context context,
                final Speech speech,
                final int position,
                int viewType) {
            //0待审核 1通过 2不通过
            if (position == dataList.size() - 1) {
                line.setVisibility(View.GONE);
            }
            if (titles != null && position < titles.length) {
                tvName.setText(titles[position]);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemEditListener != null) {
                        onItemEditListener.onItemEdit(position, speech.getSpeech());
                    }
                }
            });
            int status = speech.getStatus();
            switch (status) {
                case 0:
                    tvState.setText("未设置");
                    tvState.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
                    llContent.setVisibility(View.GONE);
                    break;
                case 1:
                    tvState.setText("已设置");
                    tvState.setTextColor(ContextCompat.getColor(context, R.color.colorSuccess));
                    llContent.setVisibility(View.VISIBLE);
                    ivWarning.setVisibility(View.GONE);
                    tvContent.setText(speech.getSpeech());
                    break;
                case 2:
                    tvState.setText("审核未通过");
                    ivWarning.setVisibility(View.VISIBLE);
                    tvState.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                    llContent.setVisibility(View.VISIBLE);
                    tvContent.setText(speech.getReason());
                    break;
            }
        }
    }

    public void setOnItemEditListener(OnItemEditListener onItemEditListener) {
        this.onItemEditListener = onItemEditListener;
    }

    private OnItemEditListener onItemEditListener;

    public interface OnItemEditListener {
        void onItemEdit(int position, String speech);
    }

}
