package com.hunliji.hljcardlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangtao on 2017/6/10.
 */

public class HoleFrame implements Parcelable {

    private int x;
    private int y;
    private int width;
    private int height;

    public HoleFrame(String frameStr) {
        if(TextUtils.isEmpty(frameStr)){
            return;
        }
        Pattern pattern = Pattern.compile("[^,]+");
        Matcher matcher = pattern.matcher(frameStr);
        int i = 0;
        while (matcher.find()) {
            String string = matcher.group(0);
            Double d = Double.valueOf(string);
            if (d != null && d != Double.NaN) {
                int px = d.intValue();
                switch (i) {
                    case 0:
                        x = px;
                        break;
                    case 1:
                        y = px;
                        break;
                    case 2:
                        width = px;
                        break;
                    case 3:
                        height = px;
                        break;
                }
                i++;
                if (i > 3) {
                    break;
                }
            }
        }

    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    protected HoleFrame(Parcel in) {
    }

    public static final Creator<HoleFrame> CREATOR = new Creator<HoleFrame>() {
        @Override
        public HoleFrame createFromParcel(Parcel in) {
            return new HoleFrame(in);
        }

        @Override
        public HoleFrame[] newArray(int size) {
            return new HoleFrame[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
