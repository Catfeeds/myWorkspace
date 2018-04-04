package com.hunliji.hljlivelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mo_yu on 2016/10/27.直播相关
 */

public class LiveRelevantWrapper implements Parcelable {

    public static final int TYPE_WORK = 4;
    public static final int TYPE_PRODUCT = 5;

    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "channel_id")
    private long channelId;
    @SerializedName(value = "entity_ids")
    private String entityIds;
    @SerializedName(value = "entity")
    private String entity;
    @SerializedName(value = "deleted")
    private boolean deleted;
    @SerializedName(value = "type")
    private int type;//1:店铺 2:话题 3:问答 4:套餐 5:婚品 6:活动
    @SerializedName(value = "created_at")
    private DateTime createdAt;
    @SerializedName(value = "updated_at")
    private DateTime updatedAt;
    @SerializedName(value = "info")
    private JsonElement info;

    //存储entity手动解析后的数据，不进行自动解析
    private transient ArrayList<CommunityThread> threads = new ArrayList<>();
    private transient ArrayList<Answer> questions = new ArrayList<>();
    private transient ArrayList<Work> works = new ArrayList<>();
    private transient ArrayList<Merchant> merchants = new ArrayList<>();
    private transient ArrayList<ShopProduct> shopProducts = new ArrayList<>();
    private transient ArrayList<EventInfo> events = new ArrayList<>();

    public long getId() {
        return id;
    }

    public long getChannelId() {
        return channelId;
    }

    public String getEntityIds() {
        return entityIds;
    }

    public String getEntity() {
        return entity;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public int getType() {
        return type;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }


    public ArrayList<? extends Parcelable> getInfoList() {
        Gson gson = GsonUtil.getGsonInstance();
        switch (type) {
            case 1:
                if (merchants.size() == 0) {
                    merchants.addAll(Arrays.asList(gson.fromJson(info, Merchant[].class)));
                }
                return merchants;
            case 2:
                if (threads.size() == 0) {
                    threads.addAll(Arrays.asList(gson.fromJson(info, CommunityThread[].class)));
                }
                return threads;
            case 3:
                if (questions.size() == 0) {
                    questions.addAll(Arrays.asList(gson.fromJson(info, Answer[].class)));
                }
                return questions;
            case 4:
                if (works.size() == 0) {
                    works.addAll(Arrays.asList(gson.fromJson(info, Work[].class)));
                }
                return works;
            case 5:
                if (shopProducts.size() == 0) {
                    shopProducts.addAll(Arrays.asList(gson.fromJson(info, ShopProduct[].class)));
                }
                return shopProducts;
            case 6:
                if (events.size() == 0) {
                    events.addAll(Arrays.asList(gson.fromJson(info, EventInfo[].class)));
                }
                return events;
        }
        return new ArrayList<>();
    }

    public LiveRelevantWrapper() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.channelId);
        dest.writeString(this.entityIds);
        dest.writeString(this.entity);
        dest.writeByte(this.deleted ? (byte) 1 : (byte) 0);
        dest.writeInt(this.type);
        dest.writeSerializable(this.createdAt);
        dest.writeSerializable(this.updatedAt);
    }

    protected LiveRelevantWrapper(Parcel in) {
        this.id = in.readLong();
        this.channelId = in.readLong();
        this.entityIds = in.readString();
        this.entity = in.readString();
        this.deleted = in.readByte() != 0;
        this.type = in.readInt();
        this.createdAt = (DateTime) in.readSerializable();
        this.updatedAt = (DateTime) in.readSerializable();
    }

    public static final Creator<LiveRelevantWrapper> CREATOR = new Creator<LiveRelevantWrapper>() {
        @Override
        public LiveRelevantWrapper createFromParcel(Parcel source) {
            return new LiveRelevantWrapper(source);
        }

        @Override
        public LiveRelevantWrapper[] newArray(int size) {
            return new LiveRelevantWrapper[size];
        }
    };
}
