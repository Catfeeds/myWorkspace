package com.hunliji.marrybiz.adapter.tools.viewholder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.tools.CheckInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 店铺检测viewHolder
 * Created by chen_bin on 2017/5/18 0018.
 */
public class CheckShopViewHolder extends BaseViewHolder<CheckInfo> {
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_content)
    public TextView tvContent;
    @BindView(R.id.img_next)
    public ImageView imgNext;
    @BindView(R.id.line_layout)
    View lineLayout;
    private OnItemClickListener onItemClickListener;

    public CheckShopViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected void setViewData(
            Context mContext, final CheckInfo item, final int position, int viewType) {
        if (item == null) {
            return;
        }
        tvTitle.setText(CheckInfo.titles[position]);
        imgCover.setImageResource(CheckInfo.drawables[position]);
        if (item.getStatus() == 0) {
            tvContent.setText(CheckInfo.contents[position]);
            tvContent.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack2));
        } else if (item.isCheck()) {
            tvContent.setText(String.format(CheckInfo.reachContents[position], item.getNum()));
            tvContent.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack2));
        } else {
            String msg = null;
            if (position != CheckInfo.MARKET_TOOL) {
                msg = String.valueOf(item.getNum());
            } else {
                List<String> extra = item.getExtra();
                if (!CommonUtil.isCollectionEmpty(extra)) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0, size = extra.size(); i < size; i++) {
                        sb.append(extra.get(i));
                        if (i < size - 1) {
                            sb.append("，");
                        }
                    }
                    msg = sb.toString();
                }
            }
            tvContent.setText(String.format(CheckInfo.unReachContents[position], msg));
            tvContent.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        }
        if (item.isCheck() || position == CheckInfo.ORDER_NUM || position == CheckInfo.COMMENT) {
            imgNext.setVisibility(View.GONE);
            itemView.setClickable(false);
        } else {
            imgNext.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position, item);
                    }
                }
            });
        }
    }

    public void setShowBottomLiveView(boolean showBottomLiveView) {
        lineLayout.setVisibility(showBottomLiveView ? View.VISIBLE : View.GONE);
    }
}
