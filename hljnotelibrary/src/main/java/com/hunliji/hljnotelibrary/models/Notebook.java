package com.hunliji.hljnotelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.note.NoteAuthor;

/**
 * 笔记本model
 * Created by chen_bin on 2017/6/22 0022.
 */
public class Notebook implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "desc")
    private String desc;
    @SerializedName(value = "collect_count")
    private int collectCount;
    @SerializedName(value = "note_count")
    private int noteCount;
    @SerializedName(value = "pics_count" ,alternate = {"pic_count"})
    private int picsCount;
    @SerializedName(value = "user")
    private NoteAuthor author;
    @SerializedName(value = "user_id")
    private long userId;
    @SerializedName(value = "note_book_type")
    private int noteBookType;//1婚纱照2婚礼筹备3婚品筹备4婚礼人
    @SerializedName(value = "cover_path")
    private String coverPath;
    @SerializedName(value = "share")
    private ShareInfo shareInfo;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public int getCollectCount() {
        return collectCount;
    }

    public int getNoteCount() {
        return noteCount;
    }

    public int getPicsCount() {
        return picsCount;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public NoteAuthor getAuthor() {
        return author;
    }

    public long getUserId() {
        return userId;
    }

    public int getNoteBookType() {
        return noteBookType;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }


    public Notebook() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.desc);
        dest.writeInt(this.collectCount);
        dest.writeInt(this.noteCount);
        dest.writeInt(this.picsCount);
        dest.writeParcelable(this.author, flags);
        dest.writeLong(this.userId);
        dest.writeInt(this.noteBookType);
        dest.writeString(this.coverPath);
        dest.writeParcelable(this.shareInfo, flags);
    }

    protected Notebook(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.desc = in.readString();
        this.collectCount = in.readInt();
        this.noteCount = in.readInt();
        this.picsCount = in.readInt();
        this.author = in.readParcelable(NoteAuthor.class.getClassLoader());
        this.userId = in.readLong();
        this.noteBookType = in.readInt();
        this.coverPath = in.readString();
        this.shareInfo = in.readParcelable(ShareInfo.class.getClassLoader());
    }

    public static final Creator<Notebook> CREATOR = new Creator<Notebook>() {
        @Override
        public Notebook createFromParcel(Parcel source) {return new Notebook(source);}

        @Override
        public Notebook[] newArray(int size) {return new Notebook[size];}
    };
}