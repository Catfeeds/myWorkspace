package com.hunliji.hljcardlibrary.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcardlibrary.utils.FontUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wangtao on 2017/6/10.
 */

public class Template implements Parcelable  {

    private long id;
    private int width;
    private int height;
    @SerializedName("background")
    private String background;

    @SerializedName("image")
    private List<ImageHole> imageHoles;
    @SerializedName("text")
    private List<TextHole> textHoles;

    @SerializedName("is_locked")
    private boolean isLocked;
    @SerializedName("is_member")
    private boolean isMember;
    @SerializedName("support_video")
    private boolean isSupportVideo;
    @SerializedName(value = "thumb_path")
    private String thumbPath;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public List<ImageHole> getImageHoles() {
        if(imageHoles==null){
            return new ArrayList<>();
        }
        return imageHoles;
    }

    public List<TextHole> getTextHoles() {
        if(textHoles==null){
            return new ArrayList<>();
        }
        return textHoles;
    }

    public List<ImageHole> getSortImageHoles() {
        if(imageHoles==null){
            return new ArrayList<>();
        }
        Collections.sort(imageHoles, new Comparator<ImageHole>() {
            @Override
            public int compare(ImageHole imageHole1, ImageHole imageHole2) {
                return imageHole1.getzIndex() - imageHole2.getzIndex();
            }

        });
        return imageHoles;
    }

    public List<TextHole> getSortTextHoles() {
        if(textHoles==null){
            return new ArrayList<>();
        }
        Collections.sort(textHoles, new Comparator<TextHole>() {
            @Override
            public int compare(TextHole textHole1, TextHole textHole2) {
                return textHole1.getzIndex() - textHole2.getzIndex();
            }

        });
        return textHoles;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public boolean isMember() {
        return isMember;
    }

    public boolean isSupportVideo() {
        return isSupportVideo;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.background);
        dest.writeTypedList(this.imageHoles);
        dest.writeList(this.textHoles);
        dest.writeByte(this.isLocked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isMember ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSupportVideo ? (byte) 1 : (byte) 0);
        dest.writeString(this.thumbPath);
    }

    public Template() {}

    protected Template(Parcel in) {
        this.id = in.readLong();
        this.width = in.readInt();
        this.height = in.readInt();
        this.background = in.readString();
        this.imageHoles = in.createTypedArrayList(ImageHole.CREATOR);
        this.textHoles = new ArrayList<>();
        in.readList(this.textHoles, TextHole.class.getClassLoader());
        this.isLocked = in.readByte() != 0;
        this.isMember = in.readByte() != 0;
        this.isSupportVideo = in.readByte() != 0;
        this.thumbPath = in.readString();
    }

    public static final Creator<Template> CREATOR = new Creator<Template>() {
        @Override
        public Template createFromParcel(Parcel source) {return new Template(source);}

        @Override
        public Template[] newArray(int size) {return new Template[size];}
    };

    public ArrayList<String> getImagePaths(Context context) {
        ArrayList<String> imagePaths = new ArrayList<>();
        if (!TextUtils.isEmpty(background)) {
            File file = FileUtil.createThemeFile(context, background);
            if (file == null || !file.exists() || file.length() == 0) {
                imagePaths.add(background);
            }
        }
        for (ImageHole imageHole : getImageHoles()) {
            String maskPath = imageHole.getMaskImagePath();
            if (!TextUtils.isEmpty(maskPath) && !imagePaths.contains(maskPath)) {
                File file = FileUtil.createThemeFile(context, maskPath);
                if (file == null || !file.exists() || file.length() == 0) {
                    imagePaths.add(maskPath);
                }
            }
        }
        return imagePaths;
    }

    public ArrayList<String> getFontPaths(Context context) {
        ArrayList<String> fontPaths = new ArrayList<>();
        for (TextHole textHole : getTextHoles()) {
            Font font = FontUtil.getInstance().getFont(context, textHole.getFontName());
            if(font==null){
                continue;
            }
            String fontPath = font.getUrl();
            if (!TextUtils.isEmpty(fontPath) && !fontPaths.contains(fontPath)) {
                File file = FileUtil.createFontFile(context, fontPath);
                if (file == null || !file.exists() || file.length() == 0) {
                    fontPaths.add(fontPath);
                }
            }
        }
        return fontPaths;
    }
}
