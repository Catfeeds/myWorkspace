package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.os.Bundle;

import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.views.fragments.CustomerCardListFragment;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;

/**
 * Created by wangtao on 2017/6/12.
 */

public class CardListActivity extends HljBaseNoBarActivity {
    @Override
    public String pageTrackTagName() {
        return "请帖首页";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_content);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_content, CustomerCardListFragment.newInstance())
                .commit();
    }
}
