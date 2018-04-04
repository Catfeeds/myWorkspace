package com.hunliji.hljcardlibrary.models.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcardlibrary.models.CardPage;
import com.hunliji.hljcardlibrary.models.ImageInfo;
import com.hunliji.hljcardlibrary.models.TextInfo;

import java.util.List;

/**
 * Created by wangtao on 2017/7/10.
 */

public class PostPageBody {

    @SerializedName("card_id")
    private Long cardId;
    private Long id;
    @SerializedName("page_template_id")
    private Long pageTemplateId;
    @SerializedName("images")
    private List<ImageInfo> imageInfos;
    @SerializedName("texts")
    private List<TextInfo> textInfos;

    public PostPageBody(CardPage page) {
        if (page.getCardId() > 0) {
            this.cardId = page.getCardId();
        }
        if (page.getId() > 0) {
            this.id = page.getId();
        }
        if (page.getPageTemplateId() > 0) {
            this.pageTemplateId = page.getPageTemplateId();
        }
        this.imageInfos = page.getImageInfos();
        this.textInfos = page.getTextInfos();
    }
}
