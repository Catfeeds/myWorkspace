package me.suncloud.marrymemo.model.orders;

import com.hunliji.hljpaymentlibrary.models.InstallmentType;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.InstallmentDetail;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import me.suncloud.marrymemo.model.Image;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2016/7/29.
 */
public class Installment implements Serializable {

    private InstallmentType type; // 分期类型
    private String typeName;
    private String icon;
    private ArrayList<InstallmentDetail> details;
    private Image image;

    public Installment(JSONObject jsonObject) {
        if (jsonObject != null) {
            icon = JSONUtil.getString(jsonObject, "icon");
            switch (jsonObject.optInt("type", 0)) {
                case 2:
                    type = InstallmentType.XiaoXi;
                    break;
                default:
                    type = InstallmentType.NotSupported;
                    break;

            }
            typeName = JSONUtil.getString(jsonObject, "type_name");
            JSONArray jsonArray = jsonObject.optJSONArray("detail");
            details = new ArrayList<>();
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject detailObj = jsonArray.optJSONObject(i);
                    InstallmentDetail detail = new InstallmentDetail(detailObj);
                    details.add(detail);
                }
            }
            image = new Image(jsonObject.optJSONObject("image"));
        }
    }

    public String getTypeName() {
        return typeName;
    }

    public String getIcon() {
        return icon;
    }

    public InstallmentType getType() {
        return type;
    }

    public ArrayList<InstallmentDetail> getDetails() {
        return details;
    }

    public Image getImage() {
        return image;
    }

    /**
     * 获取分期最低月供金额
     *
     * @return
     */
    public double getLowestEachPay() {
        double lowest = Double.MAX_VALUE;
        if (details != null && details.size() > 0) {
            for (InstallmentDetail detail : details) {
                if (detail.getEachPay() < lowest) {
                    lowest = detail.getEachPay();
                }
            }
        } else {
            lowest = 0;
        }

        return lowest;
    }
}
