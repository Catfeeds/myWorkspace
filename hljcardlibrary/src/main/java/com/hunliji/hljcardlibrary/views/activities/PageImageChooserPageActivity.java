package com.hunliji.hljcardlibrary.views.activities;

import android.os.Bundle;

import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljimagelibrary.views.activities.ChoosePhotoPageActivity;

/**
 * Created by wangtao on 2017/7/6.
 */

public class PageImageChooserPageActivity extends ChoosePhotoPageActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnChooseOk.setBackgroundResource(R.drawable.sl_r3_primary_2_dark);
        btnChooseOk.setText("开始制作");
    }

    @Override
    public void setChooseOkBtn(int count, int limit) {
        btnChooseOk.setEnabled(count > 0);
    }
}
