package me.suncloud.marrymemo.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcardcustomerlibrary.views.activities.BalanceActivity;
import com.hunliji.hljcardcustomerlibrary.views.activities.CardListActivity;
import com.hunliji.hljcarlibrary.views.activities.WeddingCarProductDetailActivity;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljhttplibrary.utils.FinancialSwitch;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljlivelibrary.activities.LiveChannelActivity;
import com.hunliji.hljnotelibrary.views.activities.CreatePhotoNoteActivity;
import com.hunliji.hljnotelibrary.views.activities.NoteDetailActivity;
import com.hunliji.hljnotelibrary.views.activities.NoteMarkDetailActivity;
import com.hunliji.hljnotelibrary.views.activities.NotebookActivity;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.MyInstallmentActivity;
import com.hunliji.hljquestionanswer.activities.AnswerDetailActivity;
import com.hunliji.hljquestionanswer.activities.CreateQuestionTitleActivity;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;
import com.hunliji.hljtrackerlibrary.HljTracker;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.ad.MadApi;
import me.suncloud.marrymemo.fragment.WeddingDateFragment;
import me.suncloud.marrymemo.fragment.merchant.FindMerchantHomeFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.PointRecord;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.ad.MadPoster;
import me.suncloud.marrymemo.task.HttpGetTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.merchant.AppointmentUtil;
import me.suncloud.marrymemo.view.AijiaWebViewActivity;
import me.suncloud.marrymemo.view.BuyWorkActivity;
import me.suncloud.marrymemo.view.BuyWorkListActivity;
import me.suncloud.marrymemo.view.CaseDetailActivity;
import me.suncloud.marrymemo.view.CaseListActivity;
import me.suncloud.marrymemo.view.HotelChannelActivity;
import me.suncloud.marrymemo.view.LightUpActivity;
import me.suncloud.marrymemo.view.MainActivity;
import me.suncloud.marrymemo.view.MarkDetailActivity;
import me.suncloud.marrymemo.view.MerchantAnswersActivity;
import me.suncloud.marrymemo.view.MyStoryListActivity;
import me.suncloud.marrymemo.view.PreFindHotelActivity;
import me.suncloud.marrymemo.view.ProductMerchantActivity;
import me.suncloud.marrymemo.view.ShoppingCategoryActivity;
import me.suncloud.marrymemo.view.ShoppingCategoryDetailActivity;
import me.suncloud.marrymemo.view.StoryActivity;
import me.suncloud.marrymemo.view.StoryListActivity;
import me.suncloud.marrymemo.view.WXWallActivity;
import me.suncloud.marrymemo.view.WeddingDayProgramsActivity;
import me.suncloud.marrymemo.view.WeddingPreparedListActivity;
import me.suncloud.marrymemo.view.WeddingRegisterActivity;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.binding_partner.BindPartnerActivity;
import me.suncloud.marrymemo.view.binding_partner.BindingPartnerActivity;
import me.suncloud.marrymemo.view.brigade.BrigadeLimitBuyActivity;
import me.suncloud.marrymemo.view.brigade.BrigadeWeekHotsActivity;
import me.suncloud.marrymemo.view.budget.NewWeddingBudgetActivity;
import me.suncloud.marrymemo.view.budget.NewWeddingBudgetFigureActivity;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.view.child.BasePictorialWorkListActivity;
import me.suncloud.marrymemo.view.child.ChildPictorialListActivity;
import me.suncloud.marrymemo.view.child.PictorialSubPageListActivity;
import me.suncloud.marrymemo.view.community.CommunityChannelActivity;
import me.suncloud.marrymemo.view.community.CommunityEventActivity;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;
import me.suncloud.marrymemo.view.community.HotThreadRankListActivity;
import me.suncloud.marrymemo.view.community.ObtainWeddingMaterialsActivity;
import me.suncloud.marrymemo.view.community.QaHomeActivity;
import me.suncloud.marrymemo.view.community.SimilarWeddingActivity;
import me.suncloud.marrymemo.view.community.WeddingBibleActivity;
import me.suncloud.marrymemo.view.event.EventDetailActivity;
import me.suncloud.marrymemo.view.event.EventListActivity;
import me.suncloud.marrymemo.view.experience.ExperienceShopActivity;
import me.suncloud.marrymemo.view.finder.SelectedSubPageListActivity;
import me.suncloud.marrymemo.view.finder.SubPageDetailActivity;
import me.suncloud.marrymemo.view.finder.SubPageMarkListActivity;
import me.suncloud.marrymemo.view.kefu.AdvHelperActivity;
import me.suncloud.marrymemo.view.kepler.HljKeplerActivity;
import me.suncloud.marrymemo.view.lvpai.AroundLvPaiActivity;
import me.suncloud.marrymemo.view.lvpai.NinetyNineLvPaiActivity;
import me.suncloud.marrymemo.view.marry.MarryBookActivity;
import me.suncloud.marrymemo.view.marry.MarryTaskActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.view.merchant.WeddingCompereChannelActivity;
import me.suncloud.marrymemo.view.merchant.WeddingDressChannelActivity;
import me.suncloud.marrymemo.view.merchant.WeddingDressPhotoChannelActivity;
import me.suncloud.marrymemo.view.merchant.WeddingMakeupChannelActivity;
import me.suncloud.marrymemo.view.merchant.WeddingPhotoChannelActivity;
import me.suncloud.marrymemo.view.merchant.WeddingPlanChannelActivity;
import me.suncloud.marrymemo.view.merchant.WeddingShootingChannelActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import me.suncloud.marrymemo.view.product.ProductSubPageMarkListActivity;
import me.suncloud.marrymemo.view.product.ShopProductDetailActivity;
import me.suncloud.marrymemo.view.themephotography.OnePayAllInclusiveActivity;
import me.suncloud.marrymemo.view.themephotography.ThemeAmorousActivity;
import me.suncloud.marrymemo.view.themephotography.ThemeAmorousCityActivity;
import me.suncloud.marrymemo.view.themephotography.ThemeAmorousLevel2Activity;
import me.suncloud.marrymemo.view.themephotography.ThemeHotCityActivity;
import me.suncloud.marrymemo.view.themephotography.ThemeLightLuxuryActivity;
import me.suncloud.marrymemo.view.themephotography.TravelChannelActivity;
import me.suncloud.marrymemo.view.tools.WeddingCalendarActivity;
import me.suncloud.marrymemo.view.tools.WeddingTableListActivity;
import me.suncloud.marrymemo.view.topBrand.WeddingBrandActivity;
import me.suncloud.marrymemo.view.topBrand.WeddingPhotoTopActivity;
import me.suncloud.marrymemo.view.wallet.CertificateCenterListActivity;
import me.suncloud.marrymemo.view.wallet.FinancialHomeActivity;
import me.suncloud.marrymemo.view.wallet.GoldMarketWebViewActivity;
import me.suncloud.marrymemo.view.wallet.MyCouponListActivity;
import me.suncloud.marrymemo.view.wallet.MyRedPacketListActivity;
import me.suncloud.marrymemo.view.wallet.OpenMemberActivity;
import rx.Subscription;

/**
 * Created by werther on 16/9/6.
 * Banner相关帮助类,Banner跳转
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
        City city = Session.getInstance()
                .getMyCity(context);

        bannerRoute(context, poster, city, false, trackData, false);
    }

    public static void bannerAction(
            Context mContext,
            int targetType,
            long targetId,
            String url,
            City city,
            boolean backMain) {
        Poster poster = new Poster();
        poster.setUrl(url);
        poster.setTargetId(targetId);
        poster.setTargetType(targetType);
        bannerRoute(mContext, poster, city, backMain, null, false);
    }

    /**
     * banner跳转
     *
     * @param mContext 上下文
     * @param poster   poster实例
     * @param city     城市
     * @param backMain 是否回到主界面
     * @param site     统计数据
     */
    public static void bannerAction(
            Context mContext, Poster poster, City city, boolean backMain, JSONObject site) {
        bannerRoute(mContext, poster, city, backMain, site, false);
    }

    /**
     * banner的路由跳转
     *
     * @param mContext  上下文
     * @param poster    poster实例
     * @param city      所在城市
     * @param backMain  是否返回主页
     * @param site      统计数据
     * @param isNewTask 是否需要在newTask中打开新的activity
     */
    private static void bannerRoute(
            final Context mContext,
            Poster poster,
            City city,
            boolean backMain,
            JSONObject site,
            final boolean isNewTask) {
        if (poster == null) {
            return;
        }
        int enterAnim = R.anim.slide_in_right;
        Intent intent = null;
        User user = Session.getInstance()
                .getCurrentUser(mContext);
        long forwardId = poster.getTargetId();
        switch (poster.getTargetType()) {
            case -1:
                //mad 广告poster
                String path = poster.getUrl();
                if (poster instanceof MadPoster) {
                    MadApi.trackMadAction(((MadPoster) poster).getThclkurl());
                }
                if (!JSONUtil.isEmpty(path)) {
                    intent = new Intent(mContext, HljWebViewActivity.class);
                    intent.putExtra("path", path);
                }
                break;
            case 2:
            case 3:
                break;
            case 4:
                break;
            case 5:
                if (forwardId != 0) {
                    intent = new Intent(mContext, MerchantDetailActivity.class);
                    intent.putExtra("id", forwardId);
                }
                break;
            case 6:
                break;
            case 7:

                break;
            case 8:
                if (forwardId != 0) {
                    intent = new Intent(mContext, StoryActivity.class);
                    intent.putExtra("id", forwardId);
                }
                break;
            case 9:
                path = JSONUtil.getWebPath(poster.getUrl());
                if (!JSONUtil.isEmpty(path)) {
                    intent = new Intent(mContext, HljWebViewActivity.class);
                    intent.putExtra("city", city);
                    intent.putExtra("bar_style", (int) forwardId);
                    intent.putExtra("path", path);
                }
                break;
            case 10:
                break;
            case 11:
                break;
            case 12:
                break;
            case 13:
            case 96:
                // 13是普通的话题，96是新加的婚纱照类别的话题，在Android这边都是同一个东西的不同显示
                if (forwardId > 0) {
                    intent = new Intent(mContext, CommunityThreadDetailActivity.class);
                    intent.putExtra("id", forwardId);
                    intent.putExtra("backMain", backMain);
                    if (!JSONUtil.isEmpty(poster.getUrl())) {
                        try {
                            int serialNo = Integer.valueOf(poster.getUrl());
                            if (serialNo > 0) {
                                intent.putExtra("serial_no", serialNo);
                            }
                        } catch (NumberFormatException ignored) {

                        }
                    }
                }
                break;
            case 14:
                if (forwardId != 0) {
                    intent = new Intent(mContext, CaseDetailActivity.class);
                    intent.putExtra("id", forwardId);
                }
                break;
            case 15:
                if (forwardId != 0) {
                    intent = new Intent(mContext, WorkActivity.class);
                    intent.putExtra("id", forwardId);
                }
                break;
            case 16:
                break;
            case 17:
                break;
            case 18:
                break;
            case 19:
                DataConfig.gotoWeddingCarActivity(mContext, city);
                break;
            case 20:
                ARouter.getInstance()
                        .build(RouterPath.IntentPath.Customer.MerchantListActivityPath
                                .MERCHANT_LIST_ACTIVITY)
                        .withSerializable(FindMerchantHomeFragment.ARG_CITY, city)
                        .navigation(mContext);
                break;
            case 21:
                break;
            case 22:
                intent = new Intent(mContext, WeddingCalendarActivity.class);
                break;
            case 23:
                if (Util.loginBindChecked(null, mContext, 0, isNewTask)) {
                    intent = new Intent(mContext, MyStoryListActivity.class);
                }
                break;
            case 24:
                intent = new Intent(mContext, WeddingRegisterActivity.class);
                break;
            case 25:
                if (Util.loginBindChecked(null, mContext, 0, isNewTask)) {
                    intent = new Intent(mContext, MarryTaskActivity.class);
                }
                break;
            case 26:
                if (Util.loginBindChecked(null, mContext, 0, isNewTask)) {
                    intent = new Intent(mContext, CardListActivity.class);
                }
                break;
            case 27:
                if (Util.loginBindChecked(null, mContext, 0, isNewTask)) {
                    intent = new Intent(mContext, WXWallActivity.class);
                }
                break;
            case 28:
                if (Util.loginBindChecked(null, mContext, 0, isNewTask)) {
                    intent = new Intent(mContext, MarryBookActivity.class);
                }
                break;
            case 29:
                if (Util.loginBindChecked(null, mContext, 0, isNewTask)) {
                    intent = new Intent(mContext, WeddingDayProgramsActivity.class);
                }
                break;
            case 30:
                if (Util.loginBindChecked(null, mContext, 0, isNewTask)) {
                    intent = new Intent(mContext, WeddingTableListActivity.class);
                }
                break;
            case 31:
            case 32:
                if (forwardId > 0) {
                    intent = new Intent(mContext, WeddingCarProductDetailActivity.class);
                    intent.putExtra(WeddingCarProductDetailActivity.ARG_ID, forwardId);
                }
                break;
            case 34:
                ARouter.getInstance()
                        .build(RouterPath.IntentPath.Customer.MerchantListActivityPath
                                .MERCHANT_LIST_ACTIVITY)
                        .withLong(RouterPath.IntentPath.Customer.MerchantListActivityPath
                                        .ARG_PROPERTY_ID,
                                RouterPath.IntentPath.Customer.MerchantListActivityPath.Property
                                        .HOTEL)
                        .withSerializable(FindMerchantHomeFragment.ARG_CITY, city)
                        .navigation(mContext);
                break;
            case 35:
                break;
            case 36:
                intent = new Intent(mContext, StoryListActivity.class);
                break;
            case 37:
                break;
            case 38:
                intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(MainActivity.ARG_MAIN_ACTION, "groupInfo");
                break;
            case 39:
                switch ((int) forwardId) {
                    //婚礼策划
                    case Merchant.PROPERTY_WEDDING_PLAN:
                        intent = new Intent(mContext, WeddingPlanChannelActivity.class);
                        break;
                    //婚纱摄影
                    case Merchant.PROPERTY_WEDDING_DRESS_PHOTO:
                        intent = new Intent(mContext, WeddingDressPhotoChannelActivity.class);
                        break;
                    //婚纱礼服
                    case Merchant.PROPERTY_WEDDING_DRESS:
                        intent = new Intent(mContext, WeddingDressChannelActivity.class);
                        break;
                    //婚礼司仪
                    case Merchant.PROPERTY_WEDDING_COMPERE:
                        intent = new Intent(mContext, WeddingCompereChannelActivity.class);
                        break;
                    //婚礼跟妆
                    case Merchant.PROPERTY_WEDDING_MAKEUP:
                        intent = new Intent(mContext, WeddingMakeupChannelActivity.class);
                        break;
                    //婚礼摄影
                    case Merchant.PROPERTY_WEDDING_PHOTO:
                        intent = new Intent(mContext, WeddingPhotoChannelActivity.class);
                        break;
                    //婚礼摄像
                    case Merchant.PROPERTY_WEDDING_SHOOTING:
                        intent = new Intent(mContext, WeddingShootingChannelActivity.class);
                        break;
                    default:
                        intent = new Intent(mContext, BuyWorkListActivity.class);
                        intent.putExtra("id", forwardId);
                        break;
                }
                break;
            case 40:
                if (forwardId > 0) {
                    intent = new Intent(mContext, ShopProductDetailActivity.class);
                    intent.putExtra("id", forwardId);
                }
                break;
            case 41:
                if (forwardId > 0) {
                    intent = new Intent(mContext, ProductMerchantActivity.class);
                    intent.putExtra("id", forwardId);
                }
                break;
            //动态详情(废弃了)
            case 42:
                break;
            //婚品分类列表(废弃了)
            case 43:
                break;
            case 44:
                intent = new Intent(mContext, WeddingPreparedListActivity.class);
                break;
            case 45:
                // 爱家分期理财主页入口
                intent = new Intent(mContext, AijiaWebViewActivity.class);
                intent.putExtra("path",
                        Constants.getAbsUrl(Constants.HttpPath.AIJIA_FINANCIAL_USER_CENTER));
                break;
            case 46:
                // 婚礼顾问
                // 所有从poster跳转的都要先进入宣传页, 不会直接进入顾问页面
                DataConfig dataConfig = Session.getInstance()
                        .getDataConfig(mContext);
                if (dataConfig != null && !JSONUtil.isEmpty(dataConfig.getAdvIntroUrl())) {
                    String advIntroUrl = dataConfig.getAdvIntroUrl();
                    advIntroUrl = advIntroUrl + (advIntroUrl.contains("?") ? "&" : "?") + "city="
                            + (city == null ? 0 : city.getId());
                    intent = new Intent(mContext, HljWebViewActivity.class);
                    intent.putExtra("city", city);
                    intent.putExtra("path", advIntroUrl);
                }
                break;
            case 47:
                // RN入口，已移除通用的匹配跳转（如：hljrn://www.hunliji.com/travel_limit_buy?id=15）
                // 解析并限定只保留限时团购(如上url)的跳转
                String[] strings = poster.getUrl()
                        .split("=");
                String id = strings.length > 1 ? strings[1] : "";
                if (poster.getUrl()
                        .startsWith("hljrn") && poster.getUrl()
                        .contains("travel_limit_buy") && !TextUtils.isEmpty(id)) {
                    intent = new Intent(mContext, BrigadeLimitBuyActivity.class);
                    intent.putExtra(BrigadeLimitBuyActivity.ARG_ID, Long.valueOf(id));
                }
                break;
            case 48:
                if (FinancialSwitch.INSTANCE.isClosed(mContext)) {
                    return;
                }
                // 金融超市
                intent = new Intent(mContext, FinancialHomeActivity.class);
                break;
            case 49:
                // 预算
                if (Util.loginBindChecked(null, mContext, 0, isNewTask)) {
                    // 从配置文件中读取用户是否设置过预算
                    boolean aBoolean = mContext.getSharedPreferences(Constants.PREF_FILE,
                            Context.MODE_PRIVATE)
                            .getBoolean(HljCommon.SharedPreferencesNames.WEDDING_BUDGET + user
                                            .getId(),
                                    false);
                    if (aBoolean) {
                        intent = new Intent(mContext, NewWeddingBudgetActivity.class);
                    } else {
                        new HttpGetTask(new OnHttpRequestListener() {
                            @Override
                            public void onRequestCompleted(Object obj) {
                                JSONArray detail = null;
                                JSONObject data = ((JSONObject) obj).optJSONObject("data");
                                if (data != null) {
                                    String detailString = data.optString("detail");
                                    if (!JSONUtil.isEmpty(detailString)) {
                                        try {
                                            detail = new JSONArray(detailString);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                User user = Session.getInstance()
                                        .getCurrentUser(mContext);
                                if (user == null || user.getId() == 0) {
                                    return;
                                }
                                Intent intent;
                                if (detail != null && detail.length() > 0) {
                                    intent = new Intent(mContext, NewWeddingBudgetActivity.class);
                                    mContext.getSharedPreferences(Constants.PREF_FILE,
                                            Context.MODE_PRIVATE)
                                            .edit()
                                            .putBoolean(HljCommon.SharedPreferencesNames
                                                            .WEDDING_BUDGET + user.getId(),
                                                    true)
                                            .commit();
                                } else {
                                    intent = new Intent(mContext,
                                            NewWeddingBudgetFigureActivity.class);
                                    intent.putExtra(NewWeddingBudgetFigureActivity.ARG_FROM,
                                            BannerUtil.class.getSimpleName());
                                }
                                if (isNewTask) {
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                }
                                mContext.startActivity(intent);
                                if (mContext instanceof Activity) {
                                    ((Activity) mContext).overridePendingTransition(R.anim
                                                    .slide_in_right,
                                            R.anim.activity_anim_default);
                                }
                            }

                            @Override
                            public void onRequestFailed(Object obj) {
                            }
                        }).execute(Constants.getAbsUrl(Constants.HttpPath.GET_BUDGET_INFO));
                    }
                }
                break;
            case 51:
                // 社区频道
                if (forwardId > 0) {
                    if (forwardId == CommunityChannel.ID_SIMILAR_WEDDING) {
                        if (Util.loginBindChecked(mContext)) {
                            intent = new Intent(mContext, SimilarWeddingActivity.class);
                        }
                    } else {
                        intent = new Intent(mContext, CommunityChannelActivity.class);
                        intent.putExtra("id", forwardId);
                    }
                }
                break;
            case 52:   // 金币页
            case 210:  // 签到推送
                if (Util.loginBindChecked(null, mContext, 0, isNewTask)) {
                    PointRecord pointRecord = PointUtil.getInstance()
                            .getPointRecord(mContext, user.getId());
                    if (pointRecord == null) {
                        PointUtil.getInstance()
                                .syncPointRecord(mContext,
                                        user.getId(),
                                        new OnHttpRequestListener() {
                                            @Override
                                            public void onRequestCompleted(Object obj) {
                                                User user = Session.getInstance()
                                                        .getCurrentUser(mContext);
                                                if (user != null && obj != null && obj instanceof
                                                        PointRecord && user.getId() == (
                                                                (PointRecord) obj).getUserId()) {
                                                    Intent intent = new Intent(mContext,
                                                            GoldMarketWebViewActivity.class);
                                                    intent.putExtra("pointRecord",
                                                            (PointRecord) obj);
                                                    if (isNewTask) {
                                                        intent.addFlags(Intent
                                                                .FLAG_ACTIVITY_NEW_TASK);
                                                    }
                                                    mContext.startActivity(intent);
                                                    if (mContext instanceof Activity) {
                                                        ((Activity) mContext)
                                                                .overridePendingTransition(

                                                                R.anim.slide_in_right,
                                                                R.anim.activity_anim_default);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onRequestFailed(Object obj) {

                                            }
                                        });
                    } else {
                        intent = new Intent(mContext, GoldMarketWebViewActivity.class);
                        intent.putExtra("pointRecord", pointRecord);
                    }
                }
                break;
            //分类页
            case 53:
                break;
            case 54:
                // 标签详情
                if (forwardId > 0) {
                    intent = new Intent(mContext, MarkDetailActivity.class);
                    intent.putExtra("markId", forwardId);
                }
                break;
            case 55:
                // 预约到店
                if (forwardId > 0) {
                    Subscription appointmentSubscription = AppointmentUtil.makeAppointment(mContext,
                            forwardId,
                            AppointmentUtil.DEFAULT,
                            0,
                            null);
                    if (mContext instanceof HljBaseActivity) {
                        ((HljBaseActivity) mContext).insertSubFromOutSide(appointmentSubscription);
                    }
                }
                break;
            case 56:
                // 专题页
                if (forwardId > 0) {
                    intent = new Intent(mContext, SubPageDetailActivity.class);
                    intent.putExtra("id", forwardId);
                }
                break;
            case 57:
                // 活动页
                if (forwardId > 0) {
                    intent = new Intent(mContext, EventDetailActivity.class);
                    intent.putExtra("id", forwardId);
                }
                break;
            case 58:
                // 问题详情
                if (forwardId > 0) {
                    intent = new Intent(mContext, QuestionDetailActivity.class);
                    intent.putExtra("questionId", forwardId);
                }
                break;
            case 59:
                // 回答详情
                if (forwardId > 0) {
                    intent = new Intent(mContext, AnswerDetailActivity.class);
                    intent.putExtra("answerId", forwardId);
                }
                break;
            case 60:
                // 问题标签页
                if (forwardId > 0) {
                    intent = new Intent(mContext,
                            com.hunliji.hljquestionanswer.activities.QAMarkDetailActivity.class);
                    intent.putExtra("id", forwardId);
                }
                break;
            case 61:
                //新娘说中的问答页
                intent = new Intent(mContext, QaHomeActivity.class);
                break;
            //专题标签列表页
            case 62:
                if (forwardId > 0) {
                    intent = new Intent(mContext, SubPageMarkListActivity.class);
                    intent.putExtra("markId", forwardId);
                }
                break;
            case 63:
                //旅拍热城
                intent = new Intent(mContext, ThemeHotCityActivity.class);
                break;
            case 64:
                //旅拍单城
                if (forwardId > 0) {
                    intent = new Intent(mContext, ThemeAmorousCityActivity.class);
                    intent.putExtra("type", Constants.THEME_TYPE.UNIT);
                    intent.putExtra("id", forwardId);
                }
                break;
            case 65:// 跳转到消息中心
                if (Util.loginBindChecked(null,
                        mContext,
                        Constants.RequestCode.NOTIFICATION_PAGE,
                        isNewTask)) {
                    intent = new Intent(mContext, MessageHomeActivity.class);
                }
                break;
            case 66:
                //旅拍特色风情
                intent = new Intent(mContext, ThemeAmorousActivity.class);
                break;
            case 67:
                //旅拍热色风情二级页
                if (forwardId > 0) {
                    intent = new Intent(mContext, ThemeAmorousLevel2Activity.class);
                    intent.putExtra("type", Constants.THEME_TYPE.AMOROUSLEVEL2);
                    intent.putExtra("id", forwardId);
                }
                break;
            case 68:
                //轻奢优品
                intent = new Intent(mContext, ThemeLightLuxuryActivity.class);
                break;
            case 69:
                //体验店
                if (forwardId != 0) {
                    intent = new Intent(mContext, ExperienceShopActivity.class);
                    intent.putExtra("id", forwardId);
                }
                break;
            case 70:
                //新娘说中的直播页
                intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(MainActivity.ARG_MAIN_ACTION, MainActivity.MAIN_ACTION_LIVE);
                break;
            case 71:
                if (forwardId > 0 && AuthUtil.loginBindCheck(mContext)) {
                    intent = new Intent(mContext, LiveChannelActivity.class);
                    intent.putExtra("id", forwardId);
                }
                break;
            case 72:
                //品牌馆
                intent = new Intent(mContext, WeddingBrandActivity.class);
                break;
            case 73:
                intent = new Intent(mContext, WeddingPhotoTopActivity.class);
                break;
            //酒店频道
            case 74:
                intent = new Intent(mContext, HotelChannelActivity.class);
                break;
            //婚品频道
            case 75:
                intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(MainActivity.ARG_MAIN_ACTION, MainActivity.MAIN_ACTION_PRODUCT);
                break;
            //领券中心
            case 76:
                intent = new Intent(mContext, CertificateCenterListActivity.class);
                intent.putExtra("id", forwardId);
                break;
            //看案例
            case 77:
                intent = new Intent(mContext, CaseListActivity.class);
                break;
            //订套餐
            case 78:
                intent = new Intent(mContext, BuyWorkActivity.class);
                break;
            // 结伴中心
            case 79:
                intent = new Intent(mContext, BindingPartnerActivity.class);
                break;
            // 我的红包页
            case 80:
                if (AuthUtil.loginBindCheck(mContext)) {
                    intent = new Intent(mContext, MyRedPacketListActivity.class);
                }
                break;
            // 我的优惠券页
            case 81:
                if (AuthUtil.loginBindCheck(mContext)) {
                    intent = new Intent(mContext, MyCouponListActivity.class);
                }
                break;
            //婚品富文本专题
            case 82:
                if (forwardId > 0) {
                    intent = new Intent(mContext, SubPageDetailActivity.class);
                    intent.putExtra("productSubPageId", forwardId);
                }
                break;
            //婚宴酒店顾问
            case 83:
                if (AuthUtil.loginBindCheck(mContext)) {
                    if (city == null) {
                        city = Session.getInstance()
                                .getMyCity(mContext);
                    }
                    boolean isOpen = false;
                    dataConfig = Session.getInstance()
                            .getDataConfig(mContext);
                    if (city != null && city.getId() > 0 && dataConfig != null && dataConfig
                            .getAdvCids() != null && !dataConfig.getAdvCids()
                            .isEmpty()) {
                        isOpen = dataConfig.getAdvCids()
                                .contains(city.getId());
                    }
                    if (isOpen) {
                        intent = new Intent(mContext, AdvHelperActivity.class);
                        intent.putExtra(AdvHelperActivity.ARG_IS_HOTEL, true);
                    } else {
                        intent = new Intent(mContext, LightUpActivity.class);
                        intent.putExtra("city", city);
                        intent.putExtra(AdvHelperActivity.ARG_IS_HOTEL, true);
                        intent.putExtra("type", 3);
                        enterAnim = R.anim.fade_in;
                    }
                }
                break;
            //平安普惠
            case 84:
                if (Util.loginBindChecked(null, mContext, 0, isNewTask)) {
                    intent = new Intent(mContext, HljKeplerActivity.class);
                }
                break;
            //婚品专题标签页
            case 85:
                if (forwardId > 0) {
                    intent = new Intent(mContext, ProductSubPageMarkListActivity.class);
                    intent.putExtra("id", forwardId);
                }
                break;
            //提问页
            case 86:
                if (Util.loginBindChecked(null, mContext, 0, isNewTask)) {
                    intent = new Intent(mContext, CreateQuestionTitleActivity.class);
                }
                break;
            //活动列表页（热门活动）
            case 87:
                intent = new Intent(mContext, EventListActivity.class);
                break;
            //专题列表页
            case 88:
                intent = new Intent(mContext, SelectedSubPageListActivity.class);
                break;
            //商家回答页
            case 89:
                if (forwardId > 0) {
                    intent = new Intent(mContext, MerchantAnswersActivity.class);
                    intent.putExtra("id", forwardId);
                }
                break;
            case 90:
                //新娘说中的社区
                intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(MainActivity.ARG_MAIN_ACTION, MainActivity.MAIN_ACTION_COMMUNITY);
                break;
            //同城备婚
            case 91:
                intent = new Intent(mContext, WeddingBibleActivity.class);
                break;
            case 92:
                intent = new Intent(mContext, HotThreadRankListActivity.class);
                break;
            case 93:
                if (forwardId == Support.SUPPORT_KIND_DEFAULT) {
                    forwardId = Support.SUPPORT_KIND_DEFAULT_ROBOT;
                }
                CustomerSupportUtil.goToSupport(mContext, (int) forwardId);
                break;
            case 94:
                //婚品分类页面 (全部分类)
                intent = new Intent(mContext, ShoppingCategoryActivity.class);
                intent.putExtra("parentId", forwardId);
                break;
            case 95:
                //婚品分类页面 (二级分类)
                intent = new Intent(mContext, ShoppingCategoryDetailActivity.class);
                intent.putExtra("parentId", forwardId);
                break;
            case 97:
                //旅拍专场列表页面
                intent = new Intent(mContext, TravelChannelActivity.class);
                break;
            case 98:
                //会员尊享跳转
                intent = new Intent(mContext, OpenMemberActivity.class);
                break;
            case 99:
                if (forwardId > 0) {
                    intent = new Intent(mContext, WSCustomerChatActivity.class);
                    intent.putExtra(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER_ID,
                            forwardId);
                }
                break;
            case 200:
                //婚礼保产品详情
                if (!TextUtils.isEmpty(poster.getUrl())) {
                    intent = new Intent(mContext, HljWebViewActivity.class);
                    intent.putExtra("path", poster.getUrl());
                }
                break;
            case 201:
                //笔记详情页
                if (forwardId > 0) {
                    intent = new Intent(mContext, NoteDetailActivity.class);
                    intent.putExtra("note_id", forwardId);
                }
                break;
            case 202:
                //发笔记
                if (!AuthUtil.loginBindCheck(mContext)) {
                    return;
                }
                intent = new Intent(mContext, CreatePhotoNoteActivity.class);
                break;
            case 203:
                //发笔记（分笔记类别）notebook_type(笔记类型)
                if (!AuthUtil.loginBindCheck(mContext)) {
                    return;
                }
                intent = new Intent(mContext, CreatePhotoNoteActivity.class);
                intent.putExtra(CreatePhotoNoteActivity.ARG_NOTEBOOK_TYPE, (int) forwardId);
                break;
            case 204:
                //笔记标签详情页
                if (forwardId > 0) {
                    intent = new Intent(mContext, NoteMarkDetailActivity.class);
                    intent.putExtra(NoteMarkDetailActivity.ARG_MARK_ID, forwardId);
                }
                break;
            case 205:
                //本周热门
                intent = new Intent(mContext, BrigadeWeekHotsActivity.class);
                break;
            case 206:
                //笔记本
                if (forwardId > 0) {
                    intent = new Intent(mContext, NotebookActivity.class);
                    intent.putExtra("note_book_id", forwardId);
                }
                break;
            case 207:
                //旅拍限时团购
                if (forwardId > 0) {
                    intent = new Intent(mContext, BrigadeLimitBuyActivity.class);
                    intent.putExtra(BrigadeLimitBuyActivity.ARG_ID, forwardId);
                }
                break;
            case 208:
                //我的小犀分期
                intent = new Intent(mContext, MyInstallmentActivity.class);
                break;
            case 209:
                //三步找酒店
                intent = new Intent(mContext, PreFindHotelActivity.class);
                break;
            case 213: //用户动态
                intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(MainActivity.ARG_MAIN_ACTION, MainActivity.MAIN_ACTION_SIGN_IN);
                break;
            case 211:
                if (user == null || user.getPartnerUid() > 0) {
                    return;
                }
                // 邀请另一半
                intent = new Intent(mContext, BindPartnerActivity.class);
                break;
            case 212:
                if (user == null || user.getId() == 0) {
                    return;
                }
                //余额明细页
                intent = new Intent(mContext, BalanceActivity.class);
                break;
            case 214:
                // 微旅拍
                intent = new Intent(mContext, AroundLvPaiActivity.class);
            case 215:
                if (forwardId == Merchant.WEDDING_DRESS_PHOTO_second_CATEGORY_CHILD_ID) {
                    //儿童摄影
                    intent = new Intent(mContext, ChildPictorialListActivity.class);
                    intent.putExtra(BasePictorialWorkListActivity.ARG_SECOND_CATEGORY_ID,
                            Merchant.WEDDING_DRESS_PHOTO_second_CATEGORY_CHILD_ID);
                    break;
                } else if (forwardId == Merchant.WEDDING_DRESS_PHOTO_second_CATEGORY_PHOTO_ID) {
                    //写真摄影
                    intent = new Intent(mContext, PictorialSubPageListActivity.class);
                    intent.putExtra(BasePictorialWorkListActivity.ARG_SECOND_CATEGORY_ID,
                            Merchant.WEDDING_DRESS_PHOTO_second_CATEGORY_PHOTO_ID);

                }
                break;
            case 216:
                // 结婚记账
                if (Util.loginBindChecked(null, mContext, 0, isNewTask)) {
                    intent = new Intent(mContext, MarryBookActivity.class);
                }
                break;
            case 217:
                // 9.9抢旅拍
                intent = new Intent(mContext, NinetyNineLvPaiActivity.class);
                break;
            case 218:
                //一价全包
                intent = new Intent(mContext, OnePayAllInclusiveActivity.class);
                break;
            case 220:
                // 新娘说同婚期
                if (Util.loginBindChecked(mContext)) {
                    if (user.getWeddingDay() == null) {
                        if (mContext instanceof FragmentActivity) {
                            WeddingDateFragment fragment = WeddingDateFragment.newInstance(new
                                    DateTime(
                                    Calendar.getInstance()
                                            .getTime()));
                            fragment.show(((FragmentActivity) mContext).getSupportFragmentManager(),
                                    "WeddingDateFragment");
                            fragment.setOnDateSelectedListener(new WeddingDateFragment
                                    .onDateSelectedListener() {
                                @Override
                                public void onDateSelected(Calendar calendar) {
                                    mContext.startActivity(new Intent(mContext,
                                            SimilarWeddingActivity.class));
                                }
                            });
                        }
                    } else {
                        intent = new Intent(mContext, SimilarWeddingActivity.class);
                    }
                }
                break;
            case 221:
                // 新娘说领资料
                if (Util.loginBindChecked(mContext)) {
                    intent = new Intent(mContext, ObtainWeddingMaterialsActivity.class);
                }
                break;
            case 222:
                // 新娘说活动详情
                if (forwardId > 0) {
                    intent = new Intent(mContext, CommunityEventActivity.class);
                    intent.putExtra(CommunityEventActivity.ARG_ID, forwardId);
                }
                break;
            default:
                break;
        }

        if (intent != null) {
            if (isNewTask) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            mContext.startActivity(intent);
            if (mContext instanceof Activity) {
                ((Activity) mContext).overridePendingTransition(enterAnim,
                        R.anim.activity_anim_default);
            }
        }
        if (site != null) {
            switch (poster.getTargetType()) {
                case 26:
                    new HljTracker.Builder(mContext).eventableType("ToolWxCard")
                            .action("hit")
                            .site(site)
                            .build()
                            .send();
                    break;
                case 19:
                    new HljTracker.Builder(mContext).eventableType("Car")
                            .action("page_in")
                            .site(site)
                            .build()
                            .send();
                    break;
                case 39:
                    new HljTracker.Builder(mContext).eventableType("MerchantProperty")
                            .action("hit")
                            .site(site)
                            .build()
                            .send();
                    break;
            }
        }
        if (poster.getId() > 0) {
            new HljTracker.Builder(mContext).eventableType("Poster")
                    .eventableId(poster.getId())
                    .action("hit")
                    .site(site)
                    .build()
                    .send();
        }
    }
}
