package com.hunliji.hljcommonlibrary.models.note;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * 笔记model
 * Created by chen_bin on 2017/6/22 0022.
 */
public class Note implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "content")
    private String content;
    @SerializedName(value = "created_at")
    private DateTime createdAt;
    @SerializedName(value = "is_choice")
    private boolean isChoice; //是否加精
    @SerializedName(value = "is_join_repository")
    private boolean isJoinRepository; //1加入精品库
    @SerializedName(value = "stick_at")
    private long stickAt;//置顶时间
    @SerializedName(value = "collect_count")
    private int collectCount;
    @SerializedName(value = "comment_count")
    private int commentCount;
    @SerializedName(value = "status")
    private int status;
    @SerializedName(value = "note_book_type")
    private int notebookType;//1婚纱照2婚礼筹备3婚品筹备4婚礼人
    @SerializedName(value = "note_book_id")
    private long noteBookId;
    @SerializedName(value = "cover")
    private NoteMedia cover;
    @SerializedName(value = "user")
    private NoteAuthor author;
    @SerializedName(value = "merchant")
    private Merchant merchant;
    @SerializedName(value = "share")
    private ShareInfo shareInfo;
    @SerializedName(value = "inspirations")
    private List<NoteInspiration> inspirations;
    @SerializedName(value = "city")
    private String city;
    @SerializedName(value = "marks")
    private List<NoteMark> noteMarks;
    @SerializedName(value = "rich_text")
    private String richText;
    @SerializedName(value = "url")
    private String url;
    @SerializedName(value = "note_type")
    private int noteType;//1普通2长文3视频
    @SerializedName(value = "deleted")
    private boolean deleted;//是否删除
    @SerializedName(value = "last_post_at")
    private String lastPostAt; //防止列表重复
    @SerializedName("dt_extend")
    private String dtExtend; //笔记统计标识

    public transient final static int TYPE_NORMAL = 1; //普通
    public transient final static int TYPE_RICH = 2; //长文
    public transient final static int TYPE_VIDEO = 3; //视频

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isChoice() {
        return isChoice;
    }

    public void setChoice(boolean choice) {
        isChoice = choice;
    }

    public boolean isJoinRepository() {
        return isJoinRepository;
    }

    public void setJoinRepository(boolean joinRepository) {
        isJoinRepository = joinRepository;
    }

    public long getStickAt() {
        return stickAt;
    }

    public void setStickAt(long stickAt) {
        this.stickAt = stickAt;
    }

    public int getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getNotebookType() {
        return notebookType;
    }

    public void setNotebookType(int notebookType) {
        this.notebookType = notebookType;
    }

    public long getNoteBookId() {
        return noteBookId;
    }

    public void setNoteBookId(long noteBookId) {
        this.noteBookId = noteBookId;
    }

    public NoteMedia getCover() {
        if (cover == null) {
            cover = new NoteMedia();
        }
        return cover;
    }

    public void setCover(NoteMedia cover) {
        this.cover = cover;
    }

    public NoteAuthor getAuthor() {
        if (author == null) {
            author = new NoteAuthor();
        }
        return author;
    }

    public void setAuthor(NoteAuthor author) {
        this.author = author;
    }

    public Merchant getMerchant() {
        if (merchant == null) {
            merchant = new Merchant();
        }
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public ShareInfo getShareInfo() {
        if (shareInfo == null) {
            shareInfo = new ShareInfo();
        }
        return shareInfo;
    }

    public void setShareInfo(ShareInfo shareInfo) {
        this.shareInfo = shareInfo;
    }

    public List<NoteInspiration> getInspirations() {
        if (inspirations == null) {
            inspirations = new ArrayList<>();
        }
        return inspirations;
    }

    public void setInspirations(List<NoteInspiration> inspirations) {
        this.inspirations = inspirations;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<NoteMark> getNoteMarks() {
        if (noteMarks == null) {
            noteMarks = new ArrayList<>();
        }
        return noteMarks;
    }

    public void setNoteMarks(List<NoteMark> noteMarks) {
        this.noteMarks = noteMarks;
    }

    public String getRichText() {
        return richText;
    }

    public void setRichText(String richText) {
        this.richText = richText;
    }

    public int getNoteType() {
        return noteType;
    }

    public void setNoteType(int noteType) {
        this.noteType = noteType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getLastPostAt() {
        return lastPostAt;
    }

    public String getDtExtend() {
        return dtExtend;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.content);
        HljTimeUtils.writeDateTimeToParcel(dest, this.createdAt);
        dest.writeByte(this.isChoice ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isJoinRepository ? (byte) 1 : (byte) 0);
        dest.writeLong(this.stickAt);
        dest.writeInt(this.collectCount);
        dest.writeInt(this.commentCount);
        dest.writeInt(this.status);
        dest.writeInt(this.notebookType);
        dest.writeLong(this.noteBookId);
        dest.writeParcelable(this.cover, flags);
        dest.writeParcelable(this.author, flags);
        dest.writeParcelable(this.merchant, flags);
        dest.writeParcelable(this.shareInfo, flags);
        dest.writeTypedList(this.inspirations);
        dest.writeString(this.city);
        dest.writeTypedList(this.noteMarks);
        dest.writeString(this.richText);
        dest.writeString(this.url);
        dest.writeInt(this.noteType);
        dest.writeByte(this.deleted ? (byte) 1 : (byte) 0);
        dest.writeString(this.lastPostAt);
        dest.writeString(this.dtExtend);
    }

    public Note() {}

    protected Note(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.content = in.readString();
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.isChoice = in.readByte() != 0;
        this.isJoinRepository = in.readByte() != 0;
        this.stickAt = in.readLong();
        this.collectCount = in.readInt();
        this.commentCount = in.readInt();
        this.status = in.readInt();
        this.notebookType = in.readInt();
        this.noteBookId = in.readLong();
        this.cover = in.readParcelable(NoteMedia.class.getClassLoader());
        this.author = in.readParcelable(NoteAuthor.class.getClassLoader());
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
        this.shareInfo = in.readParcelable(ShareInfo.class.getClassLoader());
        this.inspirations = in.createTypedArrayList(NoteInspiration.CREATOR);
        this.city = in.readString();
        this.noteMarks = in.createTypedArrayList(NoteMark.CREATOR);
        this.richText = in.readString();
        this.url = in.readString();
        this.noteType = in.readInt();
        this.deleted = in.readByte() != 0;
        this.lastPostAt = in.readString();
        this.dtExtend = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel source) {return new Note(source);}

        @Override
        public Note[] newArray(int size) {return new Note[size];}
    };
}