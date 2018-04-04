package com.hunliji.marrybiz.adapter.chat.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.chat.FastReply;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangtao on 2017/11/6.
 */

public class FastReplyViewHolder extends BaseViewHolder<FastReply> {

    @BindView(R.id.line)
    View line;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_check)
    ImageView ivCheck;
    private OnSelectedListener onSelectedClickListener;

    public FastReplyViewHolder(ViewGroup parent,OnSelectedListener onSelectedClickListener) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fast_reply_item, parent, false));
        this.onSelectedClickListener=onSelectedClickListener;
    }

    private FastReplyViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getItem().isSelecked()){
                    return;
                }
                if(onSelectedClickListener!=null){
                    onSelectedClickListener.onSelected(getItem());
                }
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, FastReply item, int position, int viewType) {
        line.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        tvContent.setText(item.getContent());
        ivCheck.setImageResource(item.isSelecked() ? R.drawable.sp_ic_checked : R.drawable
                .sp_ic_unchecked);
    }

    public interface OnSelectedListener {

        public void onSelected(FastReply item);
    }
}
