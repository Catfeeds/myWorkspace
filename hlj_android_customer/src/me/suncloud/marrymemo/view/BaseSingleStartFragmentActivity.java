package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Suncloud on 2015/6/27.
 */
public class BaseSingleStartFragmentActivity extends FragmentActivity {

    private long startTimeMillis;

    public void startActivityForResult(Intent intent,int requestCode){
        if(System.currentTimeMillis()-startTimeMillis>1000) {
            startTimeMillis=System.currentTimeMillis();
            super.startActivityForResult(intent, requestCode);
        }
    }
}
