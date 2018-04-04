package com.hunliji.hljnotelibrary.views.activities;

import android.os.Bundle;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.views.fragments.MyNoteListFragment;

/**
 * 我的笔记
 * Created by jinxin on 2017/6/26 0026.
 */

public class MyNoteListActivity extends HljBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_content);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_content,
                        MyNoteListFragment.newInstance(MyNoteListFragment.TYPE_MY_NOTE,
                                UserSession.getInstance()
                                        .getUser(this)
                                        .getId()))
                .commit();
    }
}
