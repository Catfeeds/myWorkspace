package com.hunliji.hljcommonlibrary.models.note;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * 灵感model
 * Created by chen_bin on 2017/6/27 0027.
 */
public class NoteInspiration implements Parcelable {
    private long id;
    @SerializedName(value = "is_followed")
    private boolean isFollowed;
    @SerializedName(value = "media")
    private NoteMedia noteMedia;
    @SerializedName(value = "spots")
    private ArrayList<NoteSpot> noteSpots;
    @SerializedName(value = "note")
    private Note note;

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }

    public NoteMedia getNoteMedia() {
        if (noteMedia == null) {
            noteMedia = new NoteMedia();
        }
        return noteMedia;
    }

    public ArrayList<NoteSpot> getNoteSpots() {
        if (noteSpots == null) {
            noteSpots = new ArrayList<>();
        }
        return noteSpots;
    }

    public Note getNote() {
        return note;
    }

    public long getId() {
        return id;
    }

    public NoteInspiration() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeByte(this.isFollowed ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.noteMedia, flags);
        dest.writeTypedList(this.noteSpots);
        dest.writeParcelable(this.note, flags);
    }

    protected NoteInspiration(Parcel in) {
        this.id = in.readLong();
        this.isFollowed = in.readByte() != 0;
        this.noteMedia = in.readParcelable(NoteMedia.class.getClassLoader());
        this.noteSpots = in.createTypedArrayList(NoteSpot.CREATOR);
        this.note = in.readParcelable(Note.class.getClassLoader());
    }

    public static final Creator<NoteInspiration> CREATOR = new Creator<NoteInspiration>() {
        @Override
        public NoteInspiration createFromParcel(Parcel source) {return new NoteInspiration(source);}

        @Override
        public NoteInspiration[] newArray(int size) {return new NoteInspiration[size];}
    };
}
