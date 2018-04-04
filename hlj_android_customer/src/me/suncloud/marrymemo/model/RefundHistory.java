package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * 商品退款详情中的各种历史记录
 * Created by werther on 16/4/21.
 */
public class RefundHistory implements Identifiable {

    private long id;
    private Date createdAt;
    private int status;
    private long userId;
    private String event;
    private String details;
    private JSONObject objectChanges;
    private int refundType;

    public RefundHistory(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            this.createdAt = JSONUtil.getDateFromFormatLong(jsonObject, "created_at", true);
            this.status = jsonObject.optInt("status", 0);
            this.userId = jsonObject.optInt("user_id", 0);
            this.event = JSONUtil.getString(jsonObject, "event");
            this.details = JSONUtil.getString(jsonObject, "details");
            this.objectChanges = jsonObject.optJSONObject("object_changes");
            if (objectChanges != null) {
                this.refundType = objectChanges.optInt("refund_type", 0);
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public int getStatus() {
        return status;
    }

    public long getUserId() {
        return userId;
    }

    public String getEvent() {
        return event;
    }

    public String getDetails() {
        return details;
    }

    public JSONObject getObjectChanges() {
        return objectChanges;
    }

    /**
     * 当前这个操作是否有服务器自动完成
     * @return
     */
    public boolean isAutoBySystem() {
        return userId == 0;
    }

    public int getRefundType() {
        return refundType;
    }
}

