package com.hunliji.cardmaster.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Suncloud on 2015/2/14.
 */
public class DataConfig {

    @SerializedName("invitation_card_bank_list_url")
    private String invitationCardBankListUrl;
    @SerializedName("ecard_faq_url")
    private String ecardFaqUrl;
    @SerializedName("ecard_tutorial_url")
    private String ecardTutorialUrl;
    @SerializedName("fund")
    private boolean fund;//理财开关
    private Member member;//会员相关信息

    @SerializedName("pay_type")
    private JsonObject payTypesObject;

    //服务器允许的支付途径
    private ArrayList<String> payTypes;

    public String getInvitationCardBankListUrl() {
        return invitationCardBankListUrl;
    }

    public String getEcardFaqUrl() {
        return ecardFaqUrl;
    }

    public String getEcardTutorialUrl() {
        return ecardTutorialUrl;
    }

    public boolean isFund() {
        return fund;
    }

    public String getIntroUrl() {
        if (member == null) {
            return null;
        }
        return member.getIntroUrl();
    }


//    public ArrayList<String> getPayTypes() {
//        if (payTypes == null) {
//            payTypes = new ArrayList<>();
//            if (payTypesObject != null && payTypesObject.size() > 0) {
//                for (Map.Entry<String, JsonElement> entity : payTypesObject.entrySet()) {
//                    try {
//                        if (entity.getValue()
//                                .getAsInt() > 0) {
//                            String payType = entity.getKey();
//                            if (payType.equals("walletpay")) {
//                                payType = PayAgent.WALLET_PAY;
//                            } else if (payType.equals("wuyipay")) {
//                                payType = PayAgent.XIAO_XI_PAY;
//                            }
//                            payTypes.add(payType);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//        return payTypes;
//    }
//
//    private static ArrayList<String> payAgents;
//
//    /**
//     * 获取工程中允许的一般支付列表
//     *
//     * @return
//     */
//    public static ArrayList<String> getPayAgents() {
//        if (payAgents == null) {
//            payAgents = new ArrayList<>();
//            payAgents.add(PayAgent.ALI_PAY);
//            payAgents.add(PayAgent.LL_PAY);
//            payAgents.add(PayAgent.UNION_PAY);
//            payAgents.add(PayAgent.WEIXIN_PAY);
//            payAgents.add(PayAgent.CMB_PAY);
//        }
//        return payAgents;
//    }
//
//
//    /**
//     * 余额支付列表
//     *
//     * @return
//     */
//    public static List<String> getWalletPayAgents() {
//        List<String> productPayAgents = new ArrayList<>(getPayAgents());
//        productPayAgents.add(PayAgent.WALLET_PAY);
//        return productPayAgents;
//    }

}
