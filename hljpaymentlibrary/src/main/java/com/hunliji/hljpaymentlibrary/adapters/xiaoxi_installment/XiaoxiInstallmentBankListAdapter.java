package com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Bank;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 银行列表
 * Created by chen_bin on 2017/8/16 0016.
 */
public class XiaoxiInstallmentBankListAdapter extends BaseAdapter {
    private List<Bank> banks;

    public XiaoxiInstallmentBankListAdapter(List<Bank> banks) {
        this.banks = banks;
    }

    @Override
    public int getCount() {
        return CommonUtil.getCollectionSize(banks);
    }

    @Override
    public Object getItem(int position) {
        return banks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(
            int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.xiaoxi_installment_bank_list_item___pay, parent, false);
            convertView.setTag(new BankViewHolder(convertView));
        }
        BankViewHolder holder = (BankViewHolder) convertView.getTag();
        Bank bank = banks.get(position);
        holder.tvName.setText(bank.getName());
        holder.lineLayout.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        return convertView;
    }

    static class BankViewHolder {
        @BindView(R2.id.tv_name)
        TextView tvName;
        @BindView(R2.id.line_layout)
        View lineLayout;

        BankViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
