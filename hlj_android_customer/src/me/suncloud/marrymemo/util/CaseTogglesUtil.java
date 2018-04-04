package me.suncloud.marrymemo.util;

import android.content.Context;
import android.content.IntentFilter;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import org.json.JSONObject;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.finder.FinderApi;
import me.suncloud.marrymemo.task.HttpDeleteTask;
import me.suncloud.marrymemo.task.HttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;

/**
 * Created by mo_yu on 2018/2/10.案例关注和喜欢工具类
 */

public class CaseTogglesUtil {

    private static HljHttpSubscriber collectSubscriber;

    public static void onCollectCase(
            final Context context,
            final Work work,
            final onCollectCompleteListener onCollectCompleteListener) {
        if (!Util.loginBindChecked(context, Constants.Login.LIKE_LOGIN)) {
            return;
        }
        collectSubscriber = HljHttpSubscriber.buildSubscriber(context)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        work.setCollected(!work.isCollected());
                        if (work.isCollected()) {
                            if (Util.isNewFirstCollect(context, 4)) {
                                Util.showFirstCollectNoticeDialog(context, 4);
                            } else {
                                Util.showToast(R.string.hint_collect_complete, context);
                            }
                        } else {
                            Util.showToast(R.string.hint_discollect_complete, context);
                        }
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        if (onCollectCompleteListener != null) {
                            onCollectCompleteListener.onCollectComplete(false, null);
                        }
                    }
                })
                .build();
        if (work.isCollected()) {
            FinderApi.deleteCaseCollectObb(work.getId())
                    .subscribe(collectSubscriber);
        } else {
            FinderApi.postCaseCollectObb(work.getId())
                    .subscribe(collectSubscriber);
        }
    }

    public interface onCollectCompleteListener {
        void onCollectComplete(boolean isSuccess, String msg);
    }

    public static void onDestroy() {
        CommonUtil.unSubscribeSubs(collectSubscriber);
    }

}
