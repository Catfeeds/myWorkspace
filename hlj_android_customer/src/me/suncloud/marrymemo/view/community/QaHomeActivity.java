package me.suncloud.marrymemo.view.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljquestionanswer.activities.CreateQuestionTitleActivity;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.community.QaHomeFragment;
import me.suncloud.marrymemo.util.Util;

/**
 * Created by mo_yu on 2018/3/19.问答二级页
 */

public class QaHomeActivity extends HljBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_content);
        setOkButton(R.drawable.icon_create_question_primary);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_content, new QaHomeFragment(), "QaHomeFragment");
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        if (Util.loginBindChecked(this)) {
            Intent intent = new Intent();
            intent.setClass(this, CreateQuestionTitleActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.activity_anim_default);
        }
    }
}
