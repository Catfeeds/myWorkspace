package com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.interfaces.HljRZData;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.AuthItem;

import java.util.List;

/**
 * 小犀分期-认证项查询
 * Created by chen_bin on 16/7/18.
 */
public class XiaoxiInstallmentAuthItemsData implements HljRZData {
    @SerializedName(value = "authItems")
    private List<AuthItem> authItems;

    private transient int groupIndex = -1; //用于公积金跟信用卡的分组

    public List<AuthItem> getAuthItems() {
        return authItems;
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public int getStatus() {
        if (CommonUtil.isCollectionEmpty(authItems)) {
            return AuthItem.STATUS_UNAUTHORIZED;
        }
        int realNameStatus = AuthItem.STATUS_UNAUTHORIZED;
        int bankCardStatus = AuthItem.STATUS_UNAUTHORIZED;
        int userInfoStatus = AuthItem.STATUS_UNAUTHORIZED;
        int houseFundStatus = AuthItem.STATUS_AUTHORIZED;
        int creditCardBillStatus = AuthItem.STATUS_AUTHORIZED;
        for (AuthItem authItem : authItems) {
            switch (authItem.getCode()) {
                case AuthItem.CODE_REAL_NAME:
                    realNameStatus = authItem.getStatus();
                    break;
                case AuthItem.CODE_BANK_CARD:
                    bankCardStatus = authItem.getStatus();
                    break;
                case AuthItem.CODE_BASIC_USER_INFO:
                    userInfoStatus = authItem.getStatus();
                    break;
                case AuthItem.CODE_HOUSE_FUND:
                    houseFundStatus = authItem.getStatus();
                    break;
                case AuthItem.CODE_CREDIT_CARD_BILL:
                    creditCardBillStatus = authItem.getStatus();
                    break;
            }
        }
        if (realNameStatus == AuthItem.STATUS_AUTHORIZED && bankCardStatus == AuthItem
                .STATUS_AUTHORIZED && userInfoStatus == AuthItem.STATUS_AUTHORIZED &&
                (houseFundStatus == AuthItem.STATUS_AUTHORIZED || creditCardBillStatus ==
                        AuthItem.STATUS_AUTHORIZED)) {
            return AuthItem.STATUS_AUTHORIZED;
        } else if (realNameStatus == AuthItem.STATUS_UNAUTHORIZED || bankCardStatus == AuthItem
                .STATUS_UNAUTHORIZED || userInfoStatus == AuthItem.STATUS_UNAUTHORIZED ||
                (houseFundStatus == AuthItem.STATUS_UNAUTHORIZED && creditCardBillStatus ==
                        AuthItem.STATUS_UNAUTHORIZED)) {
            return AuthItem.STATUS_UNAUTHORIZED;
        } else {
            return AuthItem.STATUS_EXPIRED;
        }
    }

    /**
     * 通过授信项code获取status
     *
     * @param code
     * @return
     */
    public int getAuthItemStatusByCode(int code) {
        int status = AuthItem.STATUS_UNAUTHORIZED;
        if (!CommonUtil.isCollectionEmpty(authItems)) {
            for (AuthItem authItem : authItems) {
                if (authItem.getCode() == code) {
                    status = authItem.getStatus();
                    break;
                }
            }
        }
        return status;
    }

    @Override
    public boolean isEmpty() {
        return CommonUtil.isCollectionEmpty(authItems);
    }

}