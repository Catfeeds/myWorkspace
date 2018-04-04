package com.hunliji.marrybiz.util.modules;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.NoteMerchantListService;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.LinkUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.popuptip.PopupRule;

/**
 * Created by jinxin on 2017/7/13 0013.
 */
@Route(path = RouterPath.ServicePath.GO_NOTE_ADS_WEB_VIEW)
public class NoteMerchantListImpl implements NoteMerchantListService {

    @Override
    public boolean isShowShopReview(Activity activity) {
        MerchantUser user = Session.getInstance()
                .getCurrentUser(activity);
        return PopupRule.getDefault()
                .showShopReview(activity, user);
    }

    @Override
    public void onNoteAdsWebView(final Activity activity, final View progressBar) {
        LinkUtil.getInstance(activity)
                .getLink(Constants.LinkNames.MERCHANT_FEED_EDU_URL, new OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {
                        if (!activity.isFinishing()) {
                            progressBar.setVisibility(View.GONE);
                            String url = (String) obj;
                            if (!TextUtils.isEmpty(url)) {
                                Intent intent = new Intent(activity, HljWebViewActivity.class);
                                intent.putExtra("path", url);
                                activity.startActivity(intent);
                                activity.overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.activity_anim_default);
                            }
                        }
                    }

                    @Override
                    public void onRequestFailed(Object obj) {
                        if (!activity.isFinishing()) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    public void init(Context context) {

    }
}
