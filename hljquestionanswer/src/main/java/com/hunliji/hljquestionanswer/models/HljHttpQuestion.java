package com.hunliji.hljquestionanswer.models;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljhttplibrary.entities.HljHttpData;

/**
 * Created by hua_rong on 2017/9/29
 * 问答列表 的头部需要显示商家信息 新增一个字段
 */

public class HljHttpQuestion<T> extends HljHttpData<T> {

    private Merchant merchant;

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

}
