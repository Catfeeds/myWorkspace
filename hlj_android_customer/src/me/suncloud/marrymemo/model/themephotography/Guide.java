package me.suncloud.marrymemo.model.themephotography;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

/**
 * Created by mo_yu on 2016/9/20. 全部攻略model
 */

public class Guide implements Parcelable {

    long id;
    @SerializedName(value = "entity_type")
    String entityType;//攻略类型 SubPage:专题 CommunityThread:帖子
    @SerializedName(value = "entity")
    JsonObject entityJson;

    //存储entity手动解析后的数据，不进行自动解析
    CommunityThread thread;
    TopicUrl topic;

    public long getId() {
        return id;
    }

    public String getEntityType() {
        return entityType;
    }

    public Object getEntity() {
        //将字符串转换成jsonObject对象
        Gson gson = GsonUtil.getGsonInstance();
        if (entityType.equals("SubPage")) {
            if (topic == null) {
                topic = gson.fromJson(entityJson.toString(),
                        TopicUrl.class);
            }
            return topic;
        } else if (entityType.equals("CommunityThread")) {
            if (thread == null) {
                thread = gson.fromJson(entityJson.toString(),
                        CommunityThread.class);
            }
            return thread;
        }
        return null;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.entityType);
        dest.writeString(this.entityJson.toString());
    }

    public Guide() {}

    protected Guide(Parcel in) {
        this.id = in.readLong();
        this.entityType = in.readString();
        this.entityJson = (JsonObject) new JsonParser().parse(in.readString());
    }

    public static final Creator<Guide> CREATOR = new Creator<Guide>() {
        @Override
        public Guide createFromParcel(Parcel source) {
            return new Guide(source);
        }

        @Override
        public Guide[] newArray(int size) {return new Guide[size];}
    };
}
