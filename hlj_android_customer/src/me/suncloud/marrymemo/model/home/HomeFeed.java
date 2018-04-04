package me.suncloud.marrymemo.model.home;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

/**
 * Created by luohanlin on 2017/4/20.
 * 首页Feed流的item
 */

public class HomeFeed implements Parcelable {
    @SerializedName("entity_id")
    private long entityId;
    @SerializedName("entity_type")
    private String entityType;
    private int entityTypeInt;
    private int weight;
    @SerializedName("entity")
    private JsonElement entityJson;
    private String entityJsonString;
    @SerializedName("watch_count")
    private int watchCount;
    @SerializedName("ads")
    private String ads;
    private Object entityObject;
    @SerializedName("dt_extend")
    private String dtExtend; // 首页Feed六中的统计新增字段

    public static final int FEED_TYPE_INT_FOOTER = 3;
    public static final int FEED_TYPE_INT_WORK = 4;
    public static final int FEED_TYPE_INT_CASE = 5;
    public static final int FEED_TYPE_INT_THREAD = 7;
    public static final int FEED_TYPE_INT_POSTER = 8;
    public static final int FEED_TYPE_INT_BANNER = 9;

    public static final String FEED_TYPE_STR_WORK = "Package";
    public static final String FEED_TYPE_STR_CASE = "Example";
    public static final String FEED_TYPE_STR_THREAD = "CommunityThread";
    public static final String FEED_TYPE_STR_POSTER = "Poster";
    public static final String FEED_TYPE_STR_BANNER = "PosterBanner";

    public String getEntityType() {
        return entityType;
    }

    public int getEntityTypeInt() {
        if (entityTypeInt == 0) {
            getEntityObj();
        }
        return entityTypeInt;
    }

    public long getEntityId() {
        return entityId;
    }

    public int getWeight() {
        return weight;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public String getAds() {
        return ads;
    }

    public String getDtExtend() {
        return dtExtend;
    }

    public Object getEntityObj() {
        if (entityObject == null) {
            Log.d("Feed Optimization", "子线程还没有解析完成，需要即时解析");
            parseEntityObj();
        }

        return entityObject;
    }

    public synchronized void parseEntityObj() {
        if (entityObject != null) {
            return;
        }
        try {
            switch (entityType) {
                case FEED_TYPE_STR_WORK:
                    entityTypeInt = FEED_TYPE_INT_WORK;
                    entityObject = GsonUtil.getGsonInstance()
                            .fromJson(entityJson, Work.class);
                    break;
                case FEED_TYPE_STR_CASE:
                    entityTypeInt = FEED_TYPE_INT_CASE;
                    entityObject = GsonUtil.getGsonInstance()
                            .fromJson(entityJson, Work.class);
                    break;
                case FEED_TYPE_STR_THREAD:
                    entityTypeInt = FEED_TYPE_INT_THREAD;
                    entityObject = GsonUtil.getGsonInstance()
                            .fromJson(entityJson, CommunityThread.class);
                    break;
                case FEED_TYPE_STR_POSTER:
                    entityTypeInt = FEED_TYPE_INT_POSTER;
                    entityObject = GsonUtil.getGsonInstance()
                            .fromJson(entityJson, Poster.class);
                    break;
                case FEED_TYPE_STR_BANNER:
                    entityTypeInt = FEED_TYPE_INT_BANNER;
                    entityObject = GsonUtil.getGsonInstance()
                            .fromJson(entityJson, Poster.class);
                    break;
                default:
                    entityObject = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWatchCount(int watchCount) {
        this.watchCount = watchCount;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.entityId);
        dest.writeString(this.entityType);
        dest.writeInt(this.weight);
        dest.writeString(this.entityJson.toString());
        dest.writeInt(this.watchCount);
        dest.writeString(this.ads);
        dest.writeString(this.dtExtend);
    }

    public HomeFeed() {}

    protected HomeFeed(Parcel in) {
        this.entityId = in.readLong();
        this.entityType = in.readString();
        this.weight = in.readInt();
        this.entityJsonString = in.readString();
        this.watchCount = in.readInt();
        this.ads = in.readString();
        this.dtExtend = in.readString();
    }

    public static final Parcelable.Creator<HomeFeed> CREATOR = new Parcelable.Creator<HomeFeed>() {
        @Override
        public HomeFeed createFromParcel(Parcel source) {return new HomeFeed(source);}

        @Override
        public HomeFeed[] newArray(int size) {return new HomeFeed[size];}
    };
}
