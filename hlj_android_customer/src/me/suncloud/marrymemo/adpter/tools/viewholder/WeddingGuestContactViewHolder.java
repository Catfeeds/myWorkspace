package me.suncloud.marrymemo.adpter.tools.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.tools.WeddingGuest;
import me.suncloud.marrymemo.model.tools.WeddingTable;

/**
 * 导入宾客viewHolder
 * Created by chen_bin on 2017/11/24 0024.
 */
public class WeddingGuestContactViewHolder extends BaseViewHolder<WeddingGuest> {

    @BindView(R.id.top_line_layout)
    View topLineLayout;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_table_no)
    TextView tvTableNo;
    @BindView(R.id.check_layout)
    CheckableLinearLayout checkLayout;

    private OnItemClickListener onItemClickListener;

    public WeddingGuestContactViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                }
            }
        });
    }

    @Override
    protected void setViewData(Context mContext, WeddingGuest guest, int position, int viewType) {
        if (guest == null) {
            return;
        }
        checkLayout.setChecked(guest.isSelected());
        tvName.setText(guest.getFullName());
        if (guest.getId() == 0) {
            tvTableNo.setVisibility(View.GONE);
        } else {
            tvTableNo.setVisibility(View.VISIBLE);
            WeddingTable table = guest.getTable();
            tvTableNo.setText(table == null || table.getId() == 0 ? mContext.getString(R.string
                    .label_to_be_arranged) : mContext.getString(
                    R.string.label_table_no,
                    table.getTableNo()));
        }
    }

    public void setShowTopLineView(boolean showTopLineView) {
        topLineLayout.setVisibility(showTopLineView ? View.VISIBLE : View.GONE);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
