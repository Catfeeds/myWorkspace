package com.hunliji.hljcommonlibrary.models.search;

import com.hunliji.hljcommonlibrary.interfaces.HljRZData;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hua_rong on 2018/1/17
 * 新搜索下拉提示 及类别
 */

public class NewSearchTips implements HljRZData {

    private List<TipSearchType> ECommerces;////电商
    /// 商家merchant、套餐package、婚品shop_product、酒店hotel、婚车car
    private List<TipSearchType> contents;////内容 案例example、笔记note、问答qa、帖子community_thread
    private List<Merchant> merchants;//商家
    private Poster poster;

    public List<TipSearchType> getECommerces() {
        if (ECommerces == null) {
            ECommerces = new ArrayList<>();
        }
        return ECommerces;
    }

    public List<TipSearchType> getContents() {
        if (contents == null) {
            contents = new ArrayList<>();
        }
        return contents;
    }

    public Poster getPoster() {
        return poster;
    }

    public List<Merchant> getMerchants() {
        if (merchants == null) {
            merchants = new ArrayList<>();
        }
        return merchants;
    }

    @Override
    public boolean isEmpty() {
        return CommonUtil.isCollectionEmpty(ECommerces) && CommonUtil.isCollectionEmpty(contents)
                && CommonUtil.isCollectionEmpty(
                merchants) && poster == null;
    }
}
