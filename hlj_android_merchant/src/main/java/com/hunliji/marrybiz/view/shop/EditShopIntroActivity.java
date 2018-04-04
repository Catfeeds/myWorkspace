package com.hunliji.marrybiz.view.shop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditShopIntroActivity extends HljBaseActivity {

    @BindView(R.id.et_intro)
    EditText etIntro;
    String intro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_merchant_intro);
        ButterKnife.bind(this);
        setSwipeBackEnable(false);

        intro = getIntent().getStringExtra("intro");
        if (!JSONUtil.isEmpty(intro)) {
            etIntro.setText(intro);
        }
    }

    public void onBackPressed(View view) {
        if (Util.calculateLength(etIntro.getText()
                .toString()) > 100) {
            Toast.makeText(this, R.string.msg_merchant_desc_max_100, Toast.LENGTH_SHORT)
                    .show();
        } else {
            hideKeyboard(etIntro);
            Intent intent = getIntent();
            intent.putExtra("intro",
                    etIntro.getText()
                            .toString());
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    public void onBackPressed() {
        onBackPressed(null);
    }
}
