package com.hunliji.hljlivelibrary.models;

import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;

/**
 * Created by luohanlin on 2017/12/4.
 */

public class LiveIntroItem {
    private boolean isIntroducing;
    private Work work;
    private ShopProduct product;
    private int type;

    public LiveIntroItem(
            boolean isIntroducing, Work work, int type) {
        this.isIntroducing = isIntroducing;
        this.work = work;
        this.product = product;
        this.type = type;
    }

    public LiveIntroItem(
            boolean isIntroducing, ShopProduct product, int type) {
        this.isIntroducing = isIntroducing;
        this.product = product;
        this.type = type;
    }

    public boolean isIntroducing() {
        return isIntroducing;
    }

    public Work getWork() {
        return work;
    }

    public ShopProduct getProduct() {
        return product;
    }

    public int getType() {
        return type;
    }
}
