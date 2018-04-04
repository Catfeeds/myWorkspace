package me.suncloud.marrymemo.util.community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljhttplibrary.authorization.UserSession;

import org.joda.time.DateTime;

import java.util.Calendar;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.WeddingDateFragment;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.community.CommunityChannelActivity;
import me.suncloud.marrymemo.view.community.SimilarWeddingActivity;

/**
 * Created by mo_yu on 2018/3/20.社区频道跳转处理
 */

public class CommunityIntentUtil {

    public static void startCommunityChannelIntent(final Context context, long id) {
        Activity activity = (Activity) context;
        Intent intent = null;
        if (id == CommunityChannel.ID_SIMILAR_WEDDING) {
            if (Util.loginBindChecked(activity)) {
                User user = UserSession.getInstance()
                        .getUser(activity);
                if (user.getWeddingDay() == null) {
                    if (activity instanceof FragmentActivity) {
                        WeddingDateFragment fragment = WeddingDateFragment.newInstance(new DateTime(
                                Calendar.getInstance()
                                        .getTime()));
                        fragment.show(((FragmentActivity) activity).getSupportFragmentManager(),
                                "WeddingDateFragment");
                        fragment.setOnDateSelectedListener(new WeddingDateFragment
                                .onDateSelectedListener() {
                            @Override
                            public void onDateSelected(Calendar calendar) {
                                context.startActivity(new Intent(context,
                                        SimilarWeddingActivity.class));
                            }
                        });
                    }
                } else {
                    intent = new Intent(activity, SimilarWeddingActivity.class);
                }
            }
        } else {
            intent = new Intent(activity, CommunityChannelActivity.class);
            intent.putExtra("id", id);
        }
        if (intent != null) {
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }
}
