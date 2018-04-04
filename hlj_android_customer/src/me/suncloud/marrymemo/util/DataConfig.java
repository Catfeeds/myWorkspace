package me.suncloud.marrymemo.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.hunliji.hljcarlibrary.views.activities.WeddingCarSubPageActivity;
import com.hunliji.hljpaymentlibrary.PayAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.view.WeddingCarEntryActivity;

/**
 * Created by Suncloud on 2015/2/14.
 */
public class DataConfig {

    public static final String FOURTH_TAB_TOOL = "tool";
    public static final String FOURTH_TAB_PRODUCT = "product";

    private String merchantRecruit;
    private ArrayList<Long> cityIds; //婚车城市
    private boolean allowIou;
    private String prepayRemind;
    private ArrayList<Long> hotelCityIds;
    private String jsCdnMd5;
    private String jsCdnUrl;
    private boolean isAijiaPeyOpen; // 爱家支付开关
    private String aijiaPayMaxBalanceStr; // 爱家支付允许最高额度,用于钱包页面提示语
    private String advIntroUrl; //值客宝未登录页
    private String photoPrepayRemind;
    private boolean isCardOldOpen;
    private String cardCloseHint;
    private boolean isGuideButtonOpen;
    private String bootScreenPv;
    private String bootScreenClick;
    private String homePageShufflingPv;
    private String homePageShufflingClick;
    private String brideSaidByPv;
    private String brideSaidByClick;
    private ArrayList<Long> advCids;//结婚顾问开通城市
    private ArrayList<Long> hotCids; //落地城市
    private String partnerHelpUrl;
    private boolean passwordLogin;//账密登录
    private String ecardFaqUrl;
    private String ecardTutorialUrl;
    //服务器允许的支付途径
    private ArrayList<String> payTypes;
    //工程中允许的一般支付列表
    private static ArrayList<String> payAgents;

    //mad 启动页广告开关
    private boolean madAdSplashOpen;
    //mad 首页广告位置 从1开始，0不开启
    private int madAdMainBannerIndex;
    //旅拍严选-服务说明 url
    private String travelStrictSelectedInstructionUrl;
    private String userAppWithdrawReadmeUrl;
    //会员相关文案
    private String userCenterMemberRemind;//用户中心会员入口
    private String productDetailMemberRemind;//婚品会员文案
    private String carDetailMemberRemind;//婚车会员文案
    private String introUrl;//会员尊享链接
    private String invitationCardBankListUrl;
    private String fourthTabPage; // 首页第四个tab的配置
    private String fourthTabTitle; // 首页第四个tab的标题配置
    private boolean isShowUserPreparation; //发现页的备婚阶段开关
    private boolean fund;//理财开关 1开 0关

    public DataConfig(JSONObject jsonObject) {
        if (jsonObject != null) {
            userAppWithdrawReadmeUrl = JSONUtil.getString(jsonObject,
                    "user_app_withdraw_readme_url");
            jsCdnMd5 = JSONUtil.getString(jsonObject, "js_cdn_md5");
            jsCdnUrl = JSONUtil.getString(jsonObject, "js_cdn_url");
            prepayRemind = JSONUtil.getString(jsonObject, "prepay_remind");
            merchantRecruit = JSONUtil.getString(jsonObject, "merchant_recruit");
            allowIou = "open".equals(JSONUtil.getString(jsonObject, "white_bar"));
            isAijiaPeyOpen = jsonObject.optInt("aijia_pay_open", 0) > 0;
            isGuideButtonOpen = jsonObject.optInt("guide_register_button_open", 0) > 0;
            aijiaPayMaxBalanceStr = JSONUtil.getString(jsonObject, "aijia_max_balance");
            photoPrepayRemind = JSONUtil.getString(jsonObject, "wedding_photo_prepay_remind");
            advIntroUrl = JSONUtil.getString(jsonObject, "adv_intro_url");
            isCardOldOpen = jsonObject.optBoolean("card_old_version_open", true);
            cardCloseHint = JSONUtil.getString(jsonObject, "card_old_version_close_prompt");
            partnerHelpUrl = JSONUtil.getString(jsonObject, "partner_help_url");
            passwordLogin = jsonObject.optInt("password_login", 0) > 0;//0 不开启 1 开启
            if (!jsonObject.isNull("cids")) {
                JSONArray array = jsonObject.optJSONArray("cids");
                int size = array.length();
                if (size > 0) {
                    cityIds = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        long id = array.optLong(i, 0);
                        if (id > 0) {
                            cityIds.add(id);
                        }
                    }
                }
            }
            if (!jsonObject.isNull("hotel_cids")) {
                JSONArray array = jsonObject.optJSONArray("hotel_cids");
                int size = array.length();
                if (size > 0) {
                    hotelCityIds = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        long id = array.optLong(i, 0);
                        if (id > 0) {
                            hotelCityIds.add(id);
                        }
                    }
                }
            }
            JSONObject pvConfigJson = jsonObject.optJSONObject("pv_config");
            if (pvConfigJson != null) {
                bootScreenPv = JSONUtil.getString(pvConfigJson, "boot_screen_pv");
                bootScreenClick = JSONUtil.getString(pvConfigJson, "boot_screen_click");
                homePageShufflingPv = JSONUtil.getString(pvConfigJson, "home_page_shuffling_pv");
                homePageShufflingClick = JSONUtil.getString(pvConfigJson,
                        "home_page_shuffling_click");
                brideSaidByPv = JSONUtil.getString(pvConfigJson, "bride_said_by_pv");
                brideSaidByClick = JSONUtil.getString(pvConfigJson, "bride_said_by_click");
            }
            if (jsonObject.has("adv_cids")) {
                advCids = new ArrayList<>();
                JSONArray cities = jsonObject.optJSONArray("adv_cids");
                for (int i = 0; i < cities.length(); i++) {
                    Long cid = cities.optLong(i);
                    advCids.add(cid);
                }
            }
            if (jsonObject.has("hot_cids")) {
                hotCids = new ArrayList<>();
                JSONArray cities = jsonObject.optJSONArray("hot_cids");
                for (int i = 0; i < cities.length(); i++) {
                    Long cid = cities.optLong(i);
                    hotCids.add(cid);
                }
            }
            if (jsonObject.optJSONObject("pay_type") != null) {
                payTypes = new ArrayList<>();
                JSONObject payTypeObject = jsonObject.optJSONObject("pay_type");
                Iterator<String> keys = payTypeObject.keys();
                while (keys.hasNext()) {
                    String payType = keys.next();
                    if (payTypeObject.optInt(payType) > 0) {
                        if (payType.equals("walletpay")) {
                            payType = PayAgent.WALLET_PAY;
                        }
                        if (payType.equals("wuyipay")) {
                            payType = PayAgent.XIAO_XI_PAY;
                        }
                        payTypes.add(payType);
                    }
                    if (Constants.DEBUG) {
                        payTypes.add(PayAgent.WALLET_PAY);
                    }
                }
            }
            ecardFaqUrl = JSONUtil.getString(jsonObject, "ecard_faq_url");
            ecardTutorialUrl = JSONUtil.getString(jsonObject, "ecard_tutorial_url");
            madAdSplashOpen = jsonObject.optInt("mad_ad_splash_open") > 0;
            madAdMainBannerIndex = jsonObject.optInt("mad_ad_main_banner_index");
            travelStrictSelectedInstructionUrl = jsonObject.optString("travel_strict_selected_url");

            JSONObject memberJson = jsonObject.optJSONObject("member");
            if (memberJson != null) {
                userCenterMemberRemind = JSONUtil.getString(memberJson,
                        "user_center_member_remind");
                productDetailMemberRemind = JSONUtil.getString(memberJson,
                        "product_detail_member_remind");
                carDetailMemberRemind = JSONUtil.getString(memberJson, "car_detail_member_remind");
                introUrl = JSONUtil.getString(memberJson, "intro_url");
            }
            invitationCardBankListUrl = jsonObject.optString("invitation_card_bank_list_url");
            fourthTabPage = jsonObject.optString("fourth_tab");
            fourthTabTitle = jsonObject.optString("fourth_tab_title");
            isShowUserPreparation = jsonObject.optInt("user_preparation_stage", 0) > 0;
            fund = jsonObject.optInt("fund", 0) > 0;
        }
    }

    public ArrayList<Long> getCityIds() {
        return cityIds;
    }

    public boolean isAllowIou() {
        return false;
    }

    public ArrayList<Long> getHotelCityIds() {
        return hotelCityIds;
    }

    public String getMerchantRecruit() {
        return merchantRecruit;
    }

    public String getPrepayRemind(long propertyId) {
        if (propertyId == 6) {
            return photoPrepayRemind;
        } else {
            return prepayRemind;
        }
    }

    public String getJsCdnMd5() {
        return jsCdnMd5;
    }

    public String getJsCdnUrl() {
        return jsCdnUrl;
    }

    public String getAijiaPayMaxBalanceStr() {
        return aijiaPayMaxBalanceStr;
    }

    public boolean isAijiaPeyOpen() {
        return isAijiaPeyOpen;
    }

    public String getAdvIntroUrl() {
        return advIntroUrl;
    }

    public boolean isCardOldOpen() {
        return isCardOldOpen;
    }

    public String getCardCloseHint() {
        return cardCloseHint;
    }

    public boolean isGuideButtonOpen() {
        return isGuideButtonOpen;
    }

    public String getBootScreenClick() {
        return bootScreenClick;
    }

    public String getBootScreenPv() {
        return bootScreenPv;
    }

    public String getBrideSaidByClick() {
        return brideSaidByClick;
    }

    public String getBrideSaidByPv() {
        return brideSaidByPv;
    }

    public String getHomePageShufflingClick() {
        return homePageShufflingClick;
    }

    public String getHomePageShufflingPv() {
        return homePageShufflingPv;
    }

    public ArrayList<Long> getAdvCids() {
        return advCids;
    }

    public String getTravelStrictSelectedInstructionUrl() {
        return travelStrictSelectedInstructionUrl;
    }

    public ArrayList<Long> getHotCids() {
        if (hotCids == null) {
            hotCids = new ArrayList<>();
        }
        return hotCids;
    }

    public boolean isPasswordLogin() {
        return passwordLogin;
    }

    public ArrayList<String> getPayTypes() {
        if (payTypes == null) {
            payTypes = new ArrayList<>();
        }
        return payTypes;
    }

    public String getPartnerHelpUrl() {
        return partnerHelpUrl;
    }

    public String getEcardFaqUrl() {
        return ecardFaqUrl;
    }

    public String getEcardTutorialUrl() {
        return ecardTutorialUrl;
    }


    /**
     * 获取工程中允许的一般支付列表
     *
     * @return
     */
    public static ArrayList<String> getPayAgents() {
        if (payAgents == null) {
            payAgents = new ArrayList<>();
            payAgents.add(PayAgent.ALI_PAY);
            payAgents.add(PayAgent.LL_PAY);
            payAgents.add(PayAgent.UNION_PAY);
            payAgents.add(PayAgent.WEIXIN_PAY);
            payAgents.add(PayAgent.CMB_PAY);
        }
        return payAgents;
    }

    /**
     * 获取工程中允许的一般支付列表
     *
     * @return
     */
    public static ArrayList<String> getServicePayAgents() {
        ArrayList<String> servicePayAgents = new ArrayList<>();
        servicePayAgents.add(PayAgent.ALI_PAY);
        servicePayAgents.add(PayAgent.LL_PAY);
        servicePayAgents.add(PayAgent.UNION_PAY);
        servicePayAgents.add(PayAgent.WEIXIN_PAY);
        servicePayAgents.add(PayAgent.CMB_PAY);
        servicePayAgents.add(PayAgent.XIAO_XI_PAY);
        return servicePayAgents;
    }


    /**
     * 余额支付列表
     *
     * @return
     */
    public static List<String> getWalletPayAgents() {
        List<String> productPayAgents = new ArrayList<>(getPayAgents());
        productPayAgents.add(PayAgent.WALLET_PAY);
        return productPayAgents;
    }

    public boolean isMadAdSplashOpen() {
        return madAdSplashOpen;
    }

    public int getMadAdMainBannerIndex() {
        return madAdMainBannerIndex;
    }

    public String getUserAppWithdrawReadmeUrl() {
        return userAppWithdrawReadmeUrl;
    }


    public boolean isSupportCar(long cid) {
        return !(cityIds == null || cityIds.isEmpty()) && cityIds.contains(cid);
    }

    public String getUserCenterMemberRemind() {
        return userCenterMemberRemind;
    }

    public String getProductDetailMemberRemind() {
        return productDetailMemberRemind;
    }

    public String getCarDetailMemberRemind() {
        return carDetailMemberRemind;
    }

    public String getIntroUrl() {
        return introUrl;
    }

    public String getInvitationCardBankListUrl() {
        return invitationCardBankListUrl;
    }

    public String getFourthTabPage() {
        return fourthTabPage;
    }

    public String getFourthTabTitle() {
        return fourthTabTitle;
    }

    public boolean isShowUserPreparation() {
        return isShowUserPreparation;
    }

    public boolean isFund() {
        return fund;
    }

    public static void gotoWeddingCarActivity(Context context, City city) {
        boolean b = false;
        if (city == null) {
            city = Session.getInstance()
                    .getMyCity(context);
        }
        if (city != null && city.getId() > 0) {
            DataConfig dataConfig = Session.getInstance()
                    .getDataConfig(context);
            if (dataConfig.getCityIds() != null && !dataConfig.getCityIds()
                    .isEmpty()) {
                b = dataConfig.getCityIds()
                        .contains(city.getId());
            }
        }
        Intent intent;
        if (b) {
            intent = new Intent(context, WeddingCarSubPageActivity.class);
            intent.putExtra(WeddingCarSubPageActivity.ARG_CITY_ID, city.getId());
            intent.putExtra(WeddingCarSubPageActivity.ARG_CITY_NAME, city.getName());
        } else {
            intent = new Intent(context, WeddingCarEntryActivity.class);
            intent.putExtra("city", city);
        }
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    public static boolean isAdvOpen(Context context, @Nullable City city) {
        if (city == null) {
            city = Session.getInstance()
                    .getMyCity(context);
        }
        if (city != null && city.getId() > 0) {
            DataConfig dataConfig = Session.getInstance()
                    .getDataConfig(context);
            if (dataConfig.getAdvCids() != null && !dataConfig.getAdvCids()
                    .isEmpty()) {
                return dataConfig.getAdvCids()
                        .contains(city.getId());
            }
        }
        return false;
    }
}
