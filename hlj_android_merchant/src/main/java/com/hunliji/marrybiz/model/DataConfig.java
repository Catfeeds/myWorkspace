package com.hunliji.marrybiz.model;

import com.hunliji.hljpaymentlibrary.PayAgent;
import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Suncloud on 2015/2/14.
 */
public class DataConfig {

    private String merchantRecruit;
    private String prepayRemind;
    private double merchantClassVersion;
    private String photoPrepayRemind;
    //服务器允许的支付途径
    private ArrayList<String> payTypes;
    //工程中允许的一般支付列表
    private static ArrayList<String> payAgents;
    //落地城市
    private ArrayList<Long> hotCids;

    public DataConfig(JSONObject jsonObject) {
        if (jsonObject != null) {
            prepayRemind = JSONUtil.getString(jsonObject, "prepay_remind");
            merchantRecruit = JSONUtil.getString(jsonObject, "merchant_recruit");
            merchantClassVersion = jsonObject.optDouble("merchant_class_version", 0);
            photoPrepayRemind = JSONUtil.getString(jsonObject, "wedding_photo_prepay_remind");
            JSONObject payTypeObject = jsonObject.optJSONObject("merchant_pay_type");
            if (payTypeObject != null) {
                payTypes = new ArrayList<>();
                Iterator<String> keys = payTypeObject.keys();
                while (keys.hasNext()) {
                    String payType = keys.next();
                    if (payTypeObject.optInt(payType) > 0) {
                        if (payType.equals("walletpay")) {
                            payType = PayAgent.WALLET_PAY;
                        }
                        payTypes.add(payType);

                    }
                }
            }
            //落地城市
            if (jsonObject.has("hot_cids")) {
                hotCids = new ArrayList<>();
                JSONArray cities = jsonObject.optJSONArray("hot_cids");
                for (int i = 0; i < cities.length(); i++) {
                    Long cid = cities.optLong(i);
                    hotCids.add(cid);
                }
            }
        }
    }

    public String getMerchantRecruit() {
        return merchantRecruit;
    }

    public double getMerchantClassVersion() {
        return merchantClassVersion;
    }

    public String getPrepayRemind(long propertyId) {
        if (propertyId == 6) {
            return photoPrepayRemind;
        } else {
            return prepayRemind;
        }
    }

    public ArrayList<String> getPayTypes() {
        if (payTypes == null) {
            payTypes = new ArrayList<>();
        }
        return payTypes;
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
            payAgents.add(PayAgent.WALLET_PAY);
        }
        return payAgents;
    }

    public ArrayList<Long> getHotCids() {
        if (hotCids == null) {
            hotCids = new ArrayList<>();
        }
        return hotCids;
    }
}
