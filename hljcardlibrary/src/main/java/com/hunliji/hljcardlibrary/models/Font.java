package com.hunliji.hljcardlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Font implements Parcelable {


    private long id;
    private String logo;
    private String url;
    private String name;

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    protected Font(Parcel in) {
        id = in.readLong();
        logo = in.readString();
        url = in.readString();
        name = in.readString();
    }

    public static final Creator<Font> CREATOR = new Creator<Font>() {
        @Override
        public Font createFromParcel(Parcel in) {
            return new Font(in);
        }

        @Override
        public Font[] newArray(int size) {
            return new Font[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(logo);
        dest.writeString(url);
        dest.writeString(name);
    }
}
