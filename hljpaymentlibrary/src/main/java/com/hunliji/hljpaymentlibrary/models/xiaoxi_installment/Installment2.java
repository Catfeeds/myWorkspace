package com.hunliji.hljpaymentlibrary.models.xiaoxi_installment;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;

import java.util.ArrayList;

/**
 * Created by luohanlin on 2017/9/6.
 */

public class Installment2 {
    @SerializedName("type_name")
    String typeName;
    int type;
    String icon;
    Photo image;
    @SerializedName("detail")
    ArrayList<InstallmentDetail> details;

    public String getTypeName() {
        return typeName;
    }

    public int getType() {
        return type;
    }

    public String getIcon() {
        return icon;
    }

    public Photo getImage() {
        return image;
    }

    public ArrayList<InstallmentDetail> getDetails() {
        return details;
    }
}
