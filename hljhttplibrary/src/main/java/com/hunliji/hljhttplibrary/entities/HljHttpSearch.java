package com.hunliji.hljhttplibrary.entities;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.Collection;

/**
 * Created by hua_rong on 2018/1/17
 * V3搜索结果页面 商家 、套餐、案例的cpm列表结构与正常列表一样
 */

public class HljHttpSearch<T> extends HljHttpData<T> {

    private T cpmList;

    public T getCpmList() {
        return cpmList;
    }

    public void setCpmList(T cpmList) {
        this.cpmList = cpmList;
    }

    @Override
    public boolean isEmpty() {
        return !(getCpmList() instanceof Collection && !CommonUtil.isCollectionEmpty((Collection)
                getCpmList())) && super.isEmpty();
    }

}
