package com.hunliji.hljcommonlibrary.models.note;

import android.os.Parcel;

import com.hunliji.hljcommonlibrary.models.Media;

/**
 * media
 * Created by chen_bin on 2017/6/27 0027.
 */
public class NoteMedia extends Media {

    public transient final static float RATIO_1_TO_1 = 1.0f;
    public transient final static float RATIO_3_TO_4 = 3.0f / 4.0f;
    public transient final static float RATIO_4_TO_3 = 4.0f / 3.0f;

    public String getCoverPath() {
        if (getType() == Media.TYPE_PHOTO) {
            return getPhoto().getImagePath();
        } else if (getType() == Media.TYPE_VIDEO) {
            return getVideo().getScreenShot();
        } else {
            return null;
        }
    }

    public int[] getSize() {
        int[] size = new int[2];
        if (getType() == Media.TYPE_PHOTO) {
            size[0] = getPhoto().getWidth();
            size[1] = getPhoto().getHeight();
        } else if (getType() == Media.TYPE_VIDEO) {
            size[0] = getVideo().getWidth();
            size[1] = getVideo().getHeight();
        }
        return size;
    }

    public float getRatio() {
        int[] size = getSize();
        int width = size[0];
        int height = size[1];
        if (height == 0) {
            return RATIO_1_TO_1;
        } else {
            float ratio = width * 1.0f / height;
            float rectangle1to1 = Math.abs(ratio - RATIO_1_TO_1);
            float rectangle3to4 = Math.abs(ratio - RATIO_3_TO_4);
            float rectangle4to3 = Math.abs(ratio - RATIO_4_TO_3);
            float min = Math.min(Math.min(rectangle1to1, rectangle3to4), rectangle4to3);
            if (min == rectangle1to1) {
                return RATIO_1_TO_1;
            } else if (min == rectangle3to4) {
                return RATIO_4_TO_3;
            } else {
                return RATIO_3_TO_4;
            }
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {super.writeToParcel(dest, flags);}

    public NoteMedia() {}

    protected NoteMedia(Parcel in) {super(in);}

    public static final Creator<NoteMedia> CREATOR = new Creator<NoteMedia>() {
        @Override
        public NoteMedia createFromParcel(Parcel source) {return new NoteMedia(source);}

        @Override
        public NoteMedia[] newArray(int size) {return new NoteMedia[size];}
    };
}
