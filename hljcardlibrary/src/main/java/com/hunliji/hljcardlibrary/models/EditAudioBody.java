package com.hunliji.hljcardlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by jinxin on 2017/6/20 0020.
 */

public class EditAudioBody implements Parcelable {
    List<Music> audios;
    long id;


    public long getId() {
        return id;
    }

    public void setId(long cardId) {
        this.id = cardId;
    }

    public List<Music> getAudios() {
        return audios;
    }

    public void setAudios(List<Music> audios) {
        this.audios = audios;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.audios);
        dest.writeLong(this.id);
    }

    public EditAudioBody() {}

    protected EditAudioBody(Parcel in) {
        this.audios = in.createTypedArrayList(Music.CREATOR);
        this.id = in.readLong();
    }

    public static final Creator<EditAudioBody> CREATOR = new Creator<EditAudioBody>() {
        @Override
        public EditAudioBody createFromParcel(Parcel source) {return new EditAudioBody(source);}

        @Override
        public EditAudioBody[] newArray(int size) {return new EditAudioBody[size];}
    };
}
