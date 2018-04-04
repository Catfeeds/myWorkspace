package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by werther on 15/8/31.
 */
public class ExpressInfo implements Identifiable {

    private static final long serialVersionUID = -4008038449554339067L;
    private long id;
    private String trackingNo;
    private String typeName;
    private String typeCode;
    private ArrayList<ShippingStatus> statusArrayList;

    public ExpressInfo(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id");
            this.trackingNo = jsonObject.optString("tracking_no");
            this.typeName = jsonObject.optString("type_name");
            this.typeCode = jsonObject.optString("type_code");
            JSONArray jsonArray = jsonObject.optJSONArray("details");
            statusArrayList = new ArrayList<>();
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    ShippingStatus status = new ShippingStatus(jsonArray.optJSONObject(i));
                    statusArrayList.add(status);
                }
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public ArrayList<ShippingStatus> getStatusArrayList() {
        return statusArrayList;
    }
}
