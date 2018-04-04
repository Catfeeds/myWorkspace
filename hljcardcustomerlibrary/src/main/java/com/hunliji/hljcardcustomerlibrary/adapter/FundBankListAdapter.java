package com.hunliji.hljcardcustomerlibrary.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcommonlibrary.models.Bank;
import com.hunliji.hljcommonlibrary.models.BankCard;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 银行列表
 * Created by chen_bin on 2017/8/16 0016.
 */
public class FundBankListAdapter extends BaseAdapter {
    private List<BankCard> banks;

    public FundBankListAdapter(List<BankCard> banks) {
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
                    .inflate(R.layout.fund_bank_list_item, parent, false);
            convertView.setTag(new BankViewHolder(convertView));
        }
        BankViewHolder holder = (BankViewHolder) convertView.getTag();
        BankCard bank = banks.get(position);
        holder.tvName.setText(bank.getBankName());
        holder.lineLayout.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        int logoWidth = CommonUtil.dp2px(parent.getContext(), 28);
        Glide.with(parent.getContext())
                .load(ImagePath.buildPath(bank.getLogoPath())
                        .width(logoWidth)
                        .height(logoWidth)
                        .cropPath())
                .into(holder.imgLogo);
        return convertView;
    }

    static class BankViewHolder {
        @BindView(R2.id.img_logo)
        ImageView imgLogo;
        @BindView(R2.id.tv_name)
        TextView tvName;
        @BindView(R2.id.line_layout)
        View lineLayout;

        BankViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
