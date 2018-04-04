package com.hunliji.hljcommonlibrary.models.search;

import java.util.List;


/**
 * Created by werther on 16/12/5.
 */

public class MerchantFilterProperty {
    long id;
    String name;
    List<MerchantFilterProperty> children;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<MerchantFilterProperty> getChildren() {
        return children;
    }
}
