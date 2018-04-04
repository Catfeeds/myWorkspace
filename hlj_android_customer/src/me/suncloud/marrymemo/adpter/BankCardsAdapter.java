package me.suncloud.marrymemo.adpter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.UserBindBankCard;

/**
 * Created by Suncloud on 2016/7/23.
 */
public class BankCardsAdapter extends BaseAdapter {

    private ArrayList<UserBindBankCard> cards;

    public BankCardsAdapter(ArrayList<UserBindBankCard> cards) {
        this.cards = cards;
    }

    @Override
    public int getCount() {
        return cards == null ? 0 : cards.size();
    }

    @Override
    public UserBindBankCard getItem(int position) {
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
        if (holder != null) {
            holder.text.setText(convertView.getContext()
                    .getString(R.string.label_current_bank,
                            getItem(position).getBankName(),
                            getItem(position).getCardType(),
                            getItem(position).getAccount()));
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.text)
        TextView text;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
