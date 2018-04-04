package com.hunliji.hljcardlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.hunliji.hljcardlibrary.adapter.viewholders.CardViewHolder;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangtao on 2017/6/13.
 */

public class CardAdapter extends RecyclerView.Adapter<BaseViewHolder<Card>> {

    private Context context;
    private List<Card> cards;

    public CardAdapter(Context context) {
        this.context = context;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder<Card> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CardViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<Card> holder, int position) {
        holder.setView(context, cards.get(position), position, getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return cards == null ? 0 : cards.size();
    }

    public void deleteCard(Card card) {
        if (cards == null) {
            cards = new ArrayList<>();
        }
        for (Card c : cards) {
            if (card.getId() == c.getId()) {
                int index = cards.indexOf(c);
                cards.remove(index);
                notifyItemRemoved(index);
                return;
            }
        }
    }

    public void updateCardCover(long cardId) {
        if (CommonUtil.isCollectionEmpty(cards)) {
            return;
        }
        for (Card c : cards) {
            if (cardId == c.getId()) {
                notifyItemChanged(cards.indexOf(c));
                break;
            }
        }
    }
}
