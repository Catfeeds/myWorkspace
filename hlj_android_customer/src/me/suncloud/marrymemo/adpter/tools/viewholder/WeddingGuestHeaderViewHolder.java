package me.suncloud.marrymemo.adpter.tools.viewholder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.tools.WeddingTable;

/**
 * 编辑宾客headerViewHolder
 * Created by chen_bin on 2017/11/24 0024.
 */
public class WeddingGuestHeaderViewHolder extends BaseViewHolder<WeddingTable> {

    @BindView(R.id.et_table_name)
    EditText etTableName;
    @BindView(R.id.btn_subtract)
    ImageButton btnSubtract;
    @BindView(R.id.tv_max_num)
    TextView tvMaxNum;
    @BindView(R.id.btn_plus)
    ImageButton btnPlus;
    @BindView(R.id.table_layout)
    LinearLayout tableLayout;
    @BindView(R.id.tv_total_num)
    TextView tvTotalNum;
    @BindView(R.id.total_num_layout)
    LinearLayout totalNumLayout;

    public WeddingGuestHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        etTableName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                WeddingTable table = getItem();
                if (table != null) {
                    table.setTableName(s.toString());
                }
            }
        });
        btnSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeddingTable table = getItem();
                if (table != null && table.getMaxNum() > 0) {
                    table.setMaxNum(table.getMaxNum() - 1);
                    setTotalNum(v.getContext(), table);
                }
            }
        });
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeddingTable table = getItem();
                if (table != null) {
                    table.setMaxNum(table.getMaxNum() + 1);
                    setTotalNum(v.getContext(), table);
                }
            }
        });
    }

    @Override
    protected void setViewData(Context mContext, WeddingTable table, int position, int viewType) {
        if (table == null) {
            return;
        }
        tableLayout.setVisibility(table.getId() > 0 ? View.VISIBLE : View.GONE);
        etTableName.setText(table.getTableName());
        etTableName.setSelection(etTableName.length());
        setTotalNum(mContext, table);
    }

    public void setTotalNum(Context context, WeddingTable table) {
        if (table.getId() == 0) {
            tvTotalNum.setText(context.getString(R.string.label_need_total_num,
                    table.getTotalNum()));
        } else {
            String numStr = context.getString(R.string.label_arranged_total_num,
                    table.getTotalNum(),
                    table.getMaxNum());
            if (table.getTotalNum() <= table.getMaxNum()) {
                tvTotalNum.setText(numStr);
            } else {
                SpannableStringBuilder builder = new SpannableStringBuilder(numStr);
                builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,
                        R.color.colorPrimary)),
                        0,
                        String.valueOf(table.getTotalNum())
                                .length(),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                tvTotalNum.setText(builder);
            }
        }
        tvMaxNum.setText(String.valueOf(table.getMaxNum()));
        totalNumLayout.setVisibility(!CommonUtil.isCollectionEmpty(table.getGuests()) ? View
                .VISIBLE : View.GONE);
    }
}
