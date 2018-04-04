package me.suncloud.marrymemo.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Poster;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * author:Bezier
 * 2015/1/30 15:32
 */
public class Category implements Parcelable {

    @SerializedName(value = "id")
    long id;
    @SerializedName(value = "target_id", alternate = {"forward_id"})
    long targetId;
    @SerializedName(value = "name")
    String name;
    @SerializedName(value = "cover_image")
    String coverImage;
    @SerializedName(value = "description")
    String description;
    @SerializedName(value = "target_url", alternate = {"url"})
    String url;
    @SerializedName(value = "position")
    int position;
    @SerializedName(value = "target_type", alternate = {"property"})
    int targetType;
    Poster poster;  // 用于存储poster跳转数据的实体,在分类页跳转的时候使用

    public Category(JSONObject json) {
        if (json != null) {
            id = json.optLong("id", 0);
            coverImage = JSONUtil.getString(json, "cover_image");
            description = JSONUtil.getString(json, "description");
            position = json.optInt("position", 0);
            name = JSONUtil.getString(json, "name");
            poster = new Poster();
            poster.setTitle(name);
            poster.setPath(coverImage);

            // 解析poster数据
            String url;
            if (json.isNull("target_url")) {
                url = JSONUtil.getString(json, "url");
            } else {
                url = JSONUtil.getString(json, "target_url");
            }
            int targetType;
            if (json.isNull("target_type")) {
                targetType = json.optInt("property");
            } else {
                targetType = json.optInt("target_type");
            }

            long targetId;
            if (json.isNull("target_id")) {
                targetId = json.optLong("forward_id", 0);
            } else {
                targetId = json.optLong("target_id", 0);
            }
            poster.setTargetId(targetId);
            poster.setTargetType(targetType);
            poster.setUrl(url);
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.targetId);
        dest.writeString(this.name);
        dest.writeString(this.coverImage);
        dest.writeString(this.description);
        dest.writeString(this.url);
        dest.writeInt(this.position);
        dest.writeInt(this.targetType);
        dest.writeParcelable(this.poster, flags);
    }

    protected Category(Parcel in) {
        this.id = in.readLong();
        this.targetId = in.readLong();
        this.name = in.readString();
        this.coverImage = in.readString();
        this.description = in.readString();
        this.url = in.readString();
        this.position = in.readInt();
        this.targetType = in.readInt();
        this.poster = in.readParcelable(Poster.class.getClassLoader());
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {return new Category(source);}

        @Override
        public Category[] newArray(int size) {return new Category[size];}
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public String getName() {
        return TextUtils.isEmpty(name) ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getTargetType() {
        return targetType;
    }

    public void setTargetType(int targetType) {
        this.targetType = targetType;
    }

    public Poster getPoster() {
        if (poster != null) {
            return poster;
        }
        poster = new Poster();
        poster.setTitle(name);
        poster.setPath(coverImage);
        poster.setTargetId(targetId);
        poster.setTargetType(targetType);
        poster.setUrl(url);
        return poster;
    }

    public void setPoster(Poster poster) {
        this.poster = poster;
    }
}
