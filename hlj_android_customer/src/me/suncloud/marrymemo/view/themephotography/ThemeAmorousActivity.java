package me.suncloud.marrymemo.view.themephotography;

import android.os.Bundle;

import me.suncloud.marrymemo.Constants;

/**
 * 特色风情页
 * Created by jinxin on 2016/10/8.
 */

public class ThemeAmorousActivity extends BaseThemeAmorousActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        type = Constants.THEME_TYPE.AMOROUS;
        super.onCreate(savedInstanceState);
    }
}
