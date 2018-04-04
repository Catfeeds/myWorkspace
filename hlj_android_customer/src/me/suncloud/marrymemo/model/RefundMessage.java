package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * 商品退款详情中的卖家/买家留言
 * Created by werther on 16/4/21.
 */
public class RefundMessage implements Identifiable {

    private long id;
    private boolean isMerchant;
    private Date createdAt;
    private String content;
    private ArrayList<Photo> proofPhotos;

    public RefundMessage(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            this.isMerchant = !jsonObject.isNull("merchant_id");
            this.content = JSONUtil.getString(jsonObject, "message");
            this.createdAt = JSONUtil.getDateFromFormatLong(jsonObject, "created_at", true);
            this.proofPhotos = new ArrayList<>();
            JSONArray jsonArray = jsonObject.optJSONArray("proof_photos");
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    Photo photo = new Photo(jsonArray.optJSONObject(i));
                    proofPhotos.add(photo);
                }
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public boolean isMerchant() {
        return isMerchant;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getContent() {
        return content;
    }

    public ArrayList<Photo> getProofPhotos() {
        return proofPhotos;
    }
}
