package com.hunliji.hljcardcustomerlibrary.views.activities;


import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcardlibrary.views.activities.BaseCardInfoEditActivity;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;

/**
 * Created by wangtao on 2017/6/13.
 */

@Route(path = RouterPath.IntentPath.Card.CARD_INFO_EDIT)
public class CardInfoEditActivity extends BaseCardInfoEditActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
