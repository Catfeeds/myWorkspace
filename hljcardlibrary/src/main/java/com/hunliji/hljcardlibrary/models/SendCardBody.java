package com.hunliji.hljcardlibrary.models;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.MinProgramShareInfo;

/**
 * Created by hua_rong on 2017/7/13.
 */

public class SendCardBody {

    @SerializedName(value = "card_id")
    private long cardId;
    private MinProgramShareInfo share;

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public MinProgramShareInfo getShare() {
        return share;
    }

    public void setShare(MinProgramShareInfo share) {
        this.share = share;
    }
}
