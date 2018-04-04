package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 16/2/26.
 * 爱家理财分期数据返回的结果Model
 */
public class AijiaInstallmentResult implements Identifiable {

    private static final long serialVersionUID = 7975494287219954988L;

    private double availableBalance;
    private List<AijiaInstallment> installments;
    private String weddingRepaymentDate; // 一次还款,婚宴还款日期,婚宴还款日期,默认为空
    private int repaymentDay; // 分期还款日,用户还款日，默认为0。O表示未设置还款日

    public AijiaInstallmentResult(JSONObject jsonObject) {
        if (jsonObject != null) {
            availableBalance = jsonObject.optDouble("availableBalance", 0);
            weddingRepaymentDate = JSONUtil.getString(jsonObject, "weddingRepaymentDate");
            repaymentDay = jsonObject.optInt("repaymentDay");
            JSONArray jsonArray = jsonObject.optJSONArray("installmentList");
            installments = new ArrayList<>();
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    AijiaInstallment installment = new AijiaInstallment(jsonArray.optJSONObject(i));
                    installments.add(installment);
                }
            }
        }
    }

    @Override
    public Long getId() {
        return serialVersionUID;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public String getWeddingRepaymentDate() {
        return weddingRepaymentDate;
    }

    public int getRepaymentDay() {
        return repaymentDay;
    }

    public List<AijiaInstallment> getInstallments() {
        return installments;
    }
}
