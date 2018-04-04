package com.hunliji.marrybiz.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljnotelibrary.views.activities.MerchantNoteListActivity;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.util.popuptip.PopupRule;
import com.hunliji.marrybiz.view.HomeActivity;
import com.hunliji.marrybiz.view.MyLevelActivity;
import com.hunliji.marrybiz.view.easychat.EasyChatActivity;
import com.hunliji.marrybiz.view.event.MyEventListActivity;
import com.hunliji.marrybiz.view.shop.EditShopActivity;
import com.hunliji.marrybiz.view.tools.ScheduleManageActivity;

import org.json.JSONObject;

/**
 * Created by jinxin on 2016/9/13.
 */
public class BannerUtil {

    /**
     * 反射调用banner跳转的专用方法,区别于其他方法在于方法名不同,提高反射调用效率,较少必要参数,尽可能使用默认参数
     *
     * @param context   上下文
     * @param poster    Poster实例
     * @param trackData 统计数据,可为空
     */
    public static void bannerJump(
            Context context, Poster poster, @Nullable JSONObject trackData) {
        if (poster.getId() > 0) {
            new HljTracker.Builder(context).eventableType("Poster")
                    .eventableId(poster.getId())
                    .additional("hit")
                    .site(trackData)
                    .build()
                    .send();
        }
        bannerAction(context, poster);
    }

    private static void bannerAction(Context mContext, Poster poster) {
        bannerAction(mContext, poster.getTargetType(), poster.getTargetId(), poster.getUrl());
    }

    public static void bannerAction(
            final Context mContext, int property, long forwardId, String web) {
        if (mContext instanceof Activity) {
            MerchantUser merchantUser = Session.getInstance()
                    .getCurrentUser(mContext);
            Intent intent = null;
            Activity activity = (Activity) mContext;
            PopupRule popupRule = PopupRule.getDefault();
            if (property != 9 && merchantUser != null) {
                switch (property) {
                    case 58:
                        // 问题详情
                        if (forwardId > 0) {
                            intent = new Intent(mContext, QuestionDetailActivity.class);
                            intent.putExtra("questionId", forwardId);
                        }
                        break;
                    case 101: //营销管理
                        if (popupRule.showShopReview(activity, merchantUser)) {
                            return;
                        }
                        intent = new Intent(mContext, HomeActivity.class);
                        intent.putExtra("page_index", 1);
                        break;
                    case 102:
                        intent = new Intent(mContext, EditShopActivity.class);
                        break;
                    case 103://日程管理
                        intent = new Intent(mContext, ScheduleManageActivity.class);
                        break;
                    case 104: // 生意罗盘
                        com.hunliji.hljcommonlibrary.utils.DialogUtil.createSingleButtonDialog(
                                mContext,
                                "模块升级中，暂时无法使用",
                                mContext.getString(R.string.label_confirm),
                                null)
                                .show();
                        //                        if (popupRule.showShopReview(activity,
                        // merchantUser)) {
                        //                            return;
                        //                        }
                        //                        if (!merchantUser.isPro()) {
                        //                            popupRule.showProDialog(activity, null);
                        //                            return;
                        //                        }
                        //                        intent = new Intent(mContext,
                        // BusinessCompassActivity.class);
                        break;
                    case 105: // 我的动态
                        intent = new Intent(mContext, MerchantNoteListActivity.class);
                        break;
                    case 106: // 商家活动
                        if (popupRule.showShopReview(activity, merchantUser)) {
                            return;
                        }
                        if (!merchantUser.isPro()) {
                            popupRule.showProDialog(activity, null);
                            return;
                        }
                        intent = new Intent(mContext, MyEventListActivity.class);
                        break;
                    case 107:
                        intent = new Intent(mContext, HomeActivity.class);
                        intent.putExtra("action", "interaction");
                        break;
                    case 108:
                        //轻松聊
                        if (popupRule.showShopReview(activity, merchantUser)) {
                            return;
                        }
                        if (!merchantUser.isPro()) {
                            popupRule.showProDialog(activity, null);
                            return;
                        }
                        intent = new Intent(mContext, EasyChatActivity.class);
                        break;
                }
            } else if (!JSONUtil.isEmpty(web)) {
                intent = new Intent(mContext, HljWebViewActivity.class);
                intent.putExtra("path", JSONUtil.getWebPath(web));
            }
            if (intent != null) {
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        }
    }
}
