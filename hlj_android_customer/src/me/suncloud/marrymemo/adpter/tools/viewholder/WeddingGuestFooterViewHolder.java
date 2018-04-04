package me.suncloud.marrymemo.adpter.tools.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.tools.WeddingTable;

/**
 * 编辑宾客footerViewHolder
 * Created by chen_bin on 2017/11/24 0024.
 */
public class WeddingGuestFooterViewHolder extends BaseViewHolder<WeddingTable> {

    @BindView(R.id.tv_import_guests)
    TextView tvImportGuests;
    @BindView(R.id.tv_delete_table)
    TextView tvDeleteTable;
    private OnImportGuestsListener onImportGuestsListener;
    private OnDeleteTableListener onDeleteTableListener;

    public WeddingGuestFooterViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        tvImportGuests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImportGuestsListener != null) {
                    onImportGuestsListener.onImportGuests(getItem());
                }
            }
        });
        tvDeleteTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteTableListener != null) {
                    onDeleteTableListener.onDeleteTable(getItem());
                }
            }
        });
    }

    @Override
    protected void setViewData(Context mContext, WeddingTable table, int position, int viewType) {
        if (table == null) {
            return;
        }
        tvDeleteTable.setVisibility(table.getId() > 0 ? View.VISIBLE : View.GONE);
    }

    public void setOnImportGuestsListener(OnImportGuestsListener onImportGuestsListener) {
        this.onImportGuestsListener = onImportGuestsListener;
    }

    public void setOnDeleteTableListener(OnDeleteTableListener onDeleteTableListener) {
        this.onDeleteTableListener = onDeleteTableListener;
    }

    public interface OnImportGuestsListener {
        void onImportGuests(WeddingTable table);
    }

    public interface OnDeleteTableListener {
        void onDeleteTable(WeddingTable table);
    }
}
