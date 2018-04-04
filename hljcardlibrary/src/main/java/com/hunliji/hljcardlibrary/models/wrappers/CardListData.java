package com.hunliji.hljcardlibrary.models.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcardlibrary.models.Card;

import java.util.List;

/**
 * Created by wangtao on 2017/6/24.
 */

public class CardListData {

    @SerializedName("has_old")
    private boolean hasOld;
    @SerializedName("list")
    private List<Card> cards;
    @SerializedName("notice")
    private CardNotice cardNotice;

    public boolean isHasOld() {
        return hasOld;
    }

    public List<Card> getCards() {
        return cards;
    }

    public CardNotice getCardNotice() {
        return cardNotice;
    }
}
