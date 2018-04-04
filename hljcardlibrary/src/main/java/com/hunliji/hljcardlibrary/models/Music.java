package com.hunliji.hljcardlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

/**
 * Created by jinxin on 2017/6/19 0019.
 */

public class Music implements Parcelable {

    Long id;
    String name;
    @SerializedName(value = "created_at")
    DateTime createdAt;
    @SerializedName(value = "m3u8_path")
    String m3u8Path;
    String mp3;
    @SerializedName(value = "audio_path")
    String audioPath;
    boolean hot;
    @SerializedName(value = "persistent_id")
    String persistentId;
    @SerializedName(value = "persistent_path")
    String persistentPath;
    boolean isNew;

    int kind;//1 录音 2 本地音乐 3 线上音乐
    boolean selected;

    private boolean isNewInit;//用来初始化isNew字段

    public long getId() {
        if(id==null){
            return 0;
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getM3u8Path() {
        return m3u8Path;
    }

    public void setM3u8Path(String m3u8Path) {
        this.m3u8Path = m3u8Path;
    }

    public String getMp3() {
        return mp3;
    }

    public void setMp3(String mp3) {
        this.mp3 = mp3;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public boolean isHot() {
        return hot;
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }

    public String getPersistentId() {
        return persistentId;
    }

    public void setPersistentId(String persistentId) {
        this.persistentId = persistentId;
    }

    public boolean isNew() {
        if (!isNewInit) {
            isNewInit = true;
            isNew = createdAt != null && System.currentTimeMillis() - createdAt.getMillis() < 7 *
                    24 * 60 * 60 * 1000;
        }
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public String getPersistentPath() {
        return persistentPath;
    }

    public void setPersistentPath(String persistentPath) {
        this.persistentPath = persistentPath;
    }

    public Music() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        HljTimeUtils.writeDateTimeToParcel(dest,this.createdAt);
        dest.writeString(this.m3u8Path);
        dest.writeString(this.mp3);
        dest.writeString(this.audioPath);
        dest.writeByte(this.hot ? (byte) 1 : (byte) 0);
        dest.writeString(this.persistentId);
        dest.writeString(this.persistentPath);
        dest.writeByte(this.isNew ? (byte) 1 : (byte) 0);
        dest.writeInt(this.kind);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isNewInit ? (byte) 1 : (byte) 0);
    }

    protected Music(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.m3u8Path = in.readString();
        this.mp3 = in.readString();
        this.audioPath = in.readString();
        this.hot = in.readByte() != 0;
        this.persistentId = in.readString();
        this.persistentPath = in.readString();
        this.isNew = in.readByte() != 0;
        this.kind = in.readInt();
        this.selected = in.readByte() != 0;
        this.isNewInit = in.readByte() != 0;
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel source) {return new Music(source);}

        @Override
        public Music[] newArray(int size) {return new Music[size];}
    };
}
