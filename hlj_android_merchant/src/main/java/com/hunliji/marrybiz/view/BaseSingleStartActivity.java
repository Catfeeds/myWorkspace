package com.hunliji.marrybiz.view;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by Suncloud on 2015/6/27.
 */
public class BaseSingleStartActivity extends Activity {

    private long startTimeMillis;

    public void startActivityForResult(Intent intent, int requestCode) {
        if (System.currentTimeMillis() - startTimeMillis > 1000) {
            startTimeMillis = System.currentTimeMillis();
            super.startActivityForResult(intent, requestCode);
        }
    }
}
