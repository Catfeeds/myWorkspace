package com.hunliji.hljcarlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Switch;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.product.ProductTopic;
import com.hunliji.hljcommonlibrary.models.realm.NotificationExtra;
import com.hunliji.hljcommonlibrary.models.story.Story;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import java.util.List;

/**
 * Created by mo_yu on 2017/12/29.婚车必修课
 */

public class CarLesson implements Parcelable {

    public static final int THREAD_SOURCE = 0;
    public static final int TOPIC_SOURCE = 1;

    public static final int REQUIRED_COURSE = 0;
    public static final int NEWS_INFORMATION = 1;

    long id;
    @SerializedName("is_select")
    boolean isSelect;
    int type;//0必修课， 1新闻资讯
    @SerializedName("source")
    JsonObject entityJson;
    @SerializedName("source_type")
    int sourceType;//0帖子， 1专题

    //存储entity手动解析后的数据，不进行自动解析
    transient CommunityThread thread;
    transient TopicUrl topicUrl;

    public long getId() {
        return id;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public int getType() {
        return type;
    }

    public String getTypeStr() {
        switch (type) {
            case REQUIRED_COURSE:
                return "必修课";
            case NEWS_INFORMATION:
                return "新闻资讯";
        }
        return "必修课";
    }

    public synchronized Object getEntityJson() {
        //将字符串转换成jsonObject对象
        Gson gson = GsonUtil.getGsonInstance();
        switch (sourceType) {
            case THREAD_SOURCE:
                if (thread == null) {
                    thread = gson.fromJson(entityJson.toString(), CommunityThread.class);
                }
                return thread;
            case TOPIC_SOURCE:
                if (topicUrl == null) {
                    topicUrl = gson.fromJson(entityJson.toString(), TopicUrl.class);
                }
                return topicUrl;
        }
        return null;
    }

    public int getSourceType() {
        return sourceType;
    }

    public CarLesson() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
        dest.writeInt(this.type);
        dest.writeString(this.entityJson.toString());
        dest.writeInt(this.sourceType);
    }

    protected CarLesson(Parcel in) {
        this.id = in.readLong();
        this.isSelect = in.readByte() != 0;
        this.type = in.readInt();
        this.entityJson = (JsonObject) new JsonParser().parse(in.readString());
        this.sourceType = in.readInt();
    }

    public static final Creator<CarLesson> CREATOR = new Creator<CarLesson>() {
        @Override
        public CarLesson createFromParcel(Parcel source) {return new CarLesson(source);}

        @Override
        public CarLesson[] newArray(int size) {return new CarLesson[size];}
    };
}
