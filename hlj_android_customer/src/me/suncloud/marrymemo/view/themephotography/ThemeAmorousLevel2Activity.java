package me.suncloud.marrymemo.view.themephotography;

import android.os.Bundle;

import me.suncloud.marrymemo.Constants;

/**
 * 特色风情二级页
 * Created by jinxin on 2016/10/8.
 */

public class ThemeAmorousLevel2Activity extends BaseThemeAmorousActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        id = getIntent().getLongExtra("id",0);
        type = getIntent().getIntExtra("type", Constants.THEME_TYPE.AMOROUS);
        super.onCreate(savedInstanceState);
    }
}
