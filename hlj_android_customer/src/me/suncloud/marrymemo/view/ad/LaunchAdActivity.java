package me.suncloud.marrymemo.view.ad;

import android.os.Bundle;

import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.ad.LaunchAdFragment;
import me.suncloud.marrymemo.util.BannerUtil;

/**
 * Created by wangtao on 2018/2/23.
 */

public class LaunchAdActivity extends HljBaseNoBarActivity implements LaunchAdFragment
        .LaunchAdFragmentInterface {

    public static final int SHOW_DURATION=15*60*1000;
    private LaunchAdFragment launchAdFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_content);
        launchAdFragment=LaunchAdFragment.newInstance(false);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_content, launchAdFragment)
                .commit();
    }

    @Override
    public void onLaunchFinish() {
        finish();
        overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
    }

    @Override
    public void onPosterClick(Poster poster) {
        BannerUtil.bannerJump(this, poster, null);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(launchAdFragment!=null&&launchAdFragment.isFinishEnabled()){
            onLaunchFinish();
        }
    }

}
