package com.hunliji.hljmaplibrary.views.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.hunliji.hljmaplibrary.R;

import butterknife.ButterKnife;

/**
 * Created by luohanlin on 2017/7/28.
 * 请帖专用的定位标注
 */

public class CardLocationMapActivity extends LocationMapActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_location_map);
        ButterKnife.bind(this);
        init(savedInstanceState);
        setStatusBarPaddingColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }
}
