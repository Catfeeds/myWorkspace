package com.hunliji.hljlivelibrary.api.apiimpl;

import com.hunliji.hljlivelibrary.api.LiveInterface;

/**
 * Created by mo_yu on 16/10/25.
 * 商家客户端与直播相关的api接口
 */
public class MerchantLiveApiImpl implements LiveInterface {

    @Override
    public String liveChannelUrl() {
        return "p/wedding/index.php/Home/APILiveChannel/index";
    }
}
