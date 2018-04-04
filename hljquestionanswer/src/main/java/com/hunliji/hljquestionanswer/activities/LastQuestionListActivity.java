package com.hunliji.hljquestionanswer.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.fragments.LastQuestionListFragment;

/**
 * Created by mo_yu on 2017/1/4.最新问答列表
 */

public class LastQuestionListActivity extends HljBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_question_list___qa);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_view,
                new LastQuestionListFragment(),
                "LastQuestionListFragment");
        transaction.commit();
    }
}
