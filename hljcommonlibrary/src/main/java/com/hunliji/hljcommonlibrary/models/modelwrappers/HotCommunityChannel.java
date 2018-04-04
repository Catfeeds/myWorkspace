package com.hunliji.hljcommonlibrary.models.modelwrappers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;

import org.joda.time.DateTime;

/**
 * Created by mo_yu on 2016/5/18. 热门频道数据
 */
public class HotCommunityChannel implements Parcelable {

    private Long id;
    @SerializedName(value = "entity_id")
    private Long entityId;
    private Long cid;
    @SerializedName(value = "entity_type")
    private String entityType;
    @SerializedName(value = "good_title")
    private String goodTitle;
    private int type;
    @SerializedName(value = "created_at")
    private DateTime createdAt;
    @SerializedName(value = "updated_at")
    private DateTime updatedAt;
    private int weight;
    @SerializedName(value = "is_auto")
    private boolean isAuto;
    private CommunityChannel entity;


    public HotCommunityChannel(String path, String cityName, int count) {
        CommunityChannel communityChannel = new CommunityChannel();
        this.entity = communityChannel;
        this.entity.setTitle(cityName);
        this.entity.setCoverPath(path);
        this.entity.setTodayWatchCount(count);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getGoodTitle() {
        return goodTitle;
    }

    public void setGoodTitle(String goodTitle) {
        this.goodTitle = goodTitle;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isAuto() {
        return isAuto;
    }

    public void setAuto(boolean auto) {
        isAuto = auto;
    }

    public CommunityChannel getEntity() {
        return entity;
    }

    public void setEntity(CommunityChannel entity) {
        this.entity = entity;
    }

    public HotCommunityChannel() {}


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.entityId);
        dest.writeValue(this.cid);
        dest.writeString(this.entityType);
        dest.writeString(this.goodTitle);
        dest.writeInt(this.type);
        dest.writeSerializable(this.createdAt);
        dest.writeSerializable(this.updatedAt);
        dest.writeInt(this.weight);
        dest.writeByte(this.isAuto ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.entity, flags);
    }

    protected HotCommunityChannel(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.entityId = (Long) in.readValue(Long.class.getClassLoader());
        this.cid = (Long) in.readValue(Long.class.getClassLoader());
        this.entityType = in.readString();
        this.goodTitle = in.readString();
        this.type = in.readInt();
        this.createdAt = (DateTime) in.readSerializable();
        this.updatedAt = (DateTime) in.readSerializable();
        this.weight = in.readInt();
        this.isAuto = in.readByte() != 0;
        this.entity = in.readParcelable(CommunityChannel.class.getClassLoader());
    }

    public static final Creator<HotCommunityChannel> CREATOR = new Creator<HotCommunityChannel>() {
        @Override
        public HotCommunityChannel createFromParcel(Parcel source) {
            return new HotCommunityChannel(source);
        }

        @Override
        public HotCommunityChannel[] newArray(int size) {return new HotCommunityChannel[size];}
    };
}
