package com.hunliji.hljpaymentlibrary.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.BankCard;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Suncloud on 2016/7/23.
 */
public class BankCardsAdapter extends BaseAdapter {

    private List<BankCard> cards;

    public BankCardsAdapter(List<BankCard> cards) {
        this.cards = cards;
    }

    @Override
    public int getCount() {
        return cards == null ? 0 : cards.size();
    }

    @Override
    public BankCard getItem(int position) {
        return cards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(
            int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bank_card_dialog_item___pay, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.text.setText(convertView.getContext()
                .getString(R.string.fmt_bank_card_info___pay,
                        getItem(position).getBankName(),
                        getItem(position).getCardType(),
                        getItem(position).getAccount()));
        return convertView;
    }

    static class ViewHolder {
        @BindView(R2.id.text)
        TextView text;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
