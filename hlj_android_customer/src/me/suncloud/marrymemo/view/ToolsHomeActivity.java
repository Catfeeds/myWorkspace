package me.suncloud.marrymemo.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.ToolsFragment;

/**
 * Created by luohanlin on 2017/10/18.
 */

public class ToolsHomeActivity extends HljBaseNoBarActivity {

    @Override
    public String pageTrackTagName() {
        return "工具";
    }

    public static final String FRAGMENT_TAG_TOOLS_HOME = "tools_home_fragment";
    private ToolsFragment toolsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools_home);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        toolsFragment = ToolsFragment.newInstance(false);

        ft.add(R.id.content_layout, toolsFragment, FRAGMENT_TAG_TOOLS_HOME);
        ft.commit();
    }
}
