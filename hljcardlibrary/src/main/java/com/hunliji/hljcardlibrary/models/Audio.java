package com.hunliji.hljcardlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by jinxin on 2017/6/19 0019.
 */

public class Audio implements Parcelable {
    private Music recordMusic;
    private Music fileMusic;
    private Music classicMusic;
    private ArrayList<Music> otherMusics;
    private int kind;

    public Audio() {
        otherMusics = new ArrayList<>();
    }

    public Music getClassicMusic() {
        return classicMusic;
    }

    public void setClassicMusic(Music classicMusic) {
        this.classicMusic = classicMusic;
    }

    public Music getFileMusic() {
        return fileMusic;
    }

    public void setFileMusic(Music fileMusic) {
        this.fileMusic = fileMusic;
    }

    public Music getRecordMusic() {
        return recordMusic;
    }

    public void setRecordMusic(Music recordMusic) {
        this.recordMusic = recordMusic;
    }

    public String getClassicMusicPath() {
        return classicMusic == null ? "" : classicMusic.getAudioPath();
    }

    public String getClassicMusicM3u8Path() {
        return classicMusic == null ? "" : classicMusic.getM3u8Path();
    }

    public String getFileMusicPath() {
        return fileMusic == null ? "" : fileMusic.getAudioPath();
    }

    public String getRecordMusicPath() {
        return recordMusic == null ? "" : recordMusic.getAudioPath();
    }

    public String getClassicMusicName() {
        return classicMusic == null ? "" : classicMusic.getName();
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public String getFileMusicName() {
        return fileMusic == null ? "" : fileMusic.getName();
    }

    public ArrayList<Music> getOtherMusics() {
        if(otherMusics == null){
            otherMusics = new ArrayList<>();
        }
        return otherMusics;
    }

    public String getCurrentPath() {
        switch (kind) {
            case 1:
                return getRecordMusicPath();
            case 2:
                return getFileMusicPath();
            case 3:
                return getClassicMusicPath();
            default:
                return null;
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.recordMusic, flags);
        dest.writeParcelable(this.fileMusic, flags);
        dest.writeParcelable(this.classicMusic, flags);
        dest.writeTypedList(this.otherMusics);
        dest.writeInt(this.kind);
    }

    protected Audio(Parcel in) {
        this.recordMusic = in.readParcelable(Music.class.getClassLoader());
        this.fileMusic = in.readParcelable(Music.class.getClassLoader());
        this.classicMusic = in.readParcelable(Music.class.getClassLoader());
        this.otherMusics = in.createTypedArrayList(Music.CREATOR);
        this.kind = in.readInt();
    }

    public static final Parcelable.Creator<Audio> CREATOR = new Parcelable.Creator<Audio>() {
        @Override
        public Audio createFromParcel(Parcel source) {return new Audio(source);}

        @Override
        public Audio[] newArray(int size) {return new Audio[size];}
    };
}
