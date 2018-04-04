package me.suncloud.marrymemo.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.FollowUserFragment;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.Session;

/**
 * Created by Suncloud on 2016/3/1.
 */
public class FansActivity extends HljBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans);
        User user = (User) getIntent().getSerializableExtra("user");
        if (user != null) {
            User my = Session.getInstance()
                    .getCurrentUser(this);
            int emptyId;
            if (my == null || !user.getId()
                    .equals(my.getId())) {
                if (user.getGender() == 1) {
                    setTitle(R.string.title_activity_ta_fan1);
                    emptyId = R.string.hint_empty_ta_fan1;
                } else {
                    setTitle(R.string.title_activity_ta_fan2);
                    emptyId = R.string.hint_empty_ta_fan2;
                }
            } else {
                setTitle(R.string.title_activity_my_fan);
                emptyId = R.string.hint_empty_my_fan;
            }
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            FollowUserFragment fansFragment = FollowUserFragment.newInstance(user.getId(),
                    emptyId,
                    1);
            ft.add(R.id.content, fansFragment, "fansFragment");
            ft.commit();
        }
    }
}
