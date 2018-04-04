package com.hunliji.hljlivelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.hunliji.hljcommonlibrary.models.Media;
import com.hunliji.hljcommonlibrary.models.note.NoteSpot;

import java.util.List;

/**
 * Created by luohanlin on 2017/11/30.
 */

public class LiveSpotMedia implements Parcelable {
    private Media media;
    private List<NoteSpot> spots;

    public LiveSpotMedia(Media media) {
        this.media = media;
    }

    public LiveSpotMedia(
            Media media, List<NoteSpot> spots) {
        this.media = media;
        this.spots = spots;
    }

    public Media getMedia() {
        return media;
    }

    public List<NoteSpot> getSpots() {
        return spots;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.media, flags);
        dest.writeTypedList(this.spots);
    }

    protected LiveSpotMedia(Parcel in) {
        this.media = in.readParcelable(Media.class.getClassLoader());
        this.spots = in.createTypedArrayList(NoteSpot.CREATOR);
    }

    public static final Parcelable.Creator<LiveSpotMedia> CREATOR = new Parcelable
            .Creator<LiveSpotMedia>() {
        @Override
        public LiveSpotMedia createFromParcel(Parcel source) {return new LiveSpotMedia(source);}

        @Override
        public LiveSpotMedia[] newArray(int size) {return new LiveSpotMedia[size];}
    };
}
