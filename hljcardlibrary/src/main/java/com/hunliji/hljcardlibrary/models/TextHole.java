package com.hunliji.hljcardlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.ThemeUtil;

/**
 * Created by wangtao on 2017/6/10.
 */

public class TextHole implements Parcelable {

    private long id;
    private String type;
    @SerializedName("color")
    private String colorStr;
    @SerializedName("font_name")
    private String fontName;
    @SerializedName("font_size")
    private int fontSize;

    @SerializedName("content")
    private String defaultContent;
    @SerializedName("default_h5_hole_image_path")
    private String defaultH5ImagePath;

    @SerializedName("frame")
    private String frameStr;

    private int alignment;
    @SerializedName("line_spacing")
    private int lineSpace;
    @SerializedName("z_index")
    private int zIndex;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getColor() {
        return ThemeUtil.parseColor(colorStr);
    }

    public String getFontName() {
        return fontName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public String getDefaultContent() {
        return defaultContent;
    }

    public void setDefaultContent(String defaultContent) {
        this.defaultContent = defaultContent;
    }

    public String getDefaultH5ImagePath() {
        return defaultH5ImagePath;
    }

    public HoleFrame getHoleFrame() {
        return new HoleFrame(frameStr);
    }

    public Layout.Alignment getAlignment() {
        switch (alignment){
            case 2:
                return Layout.Alignment.ALIGN_OPPOSITE;
            case 1:
                return Layout.Alignment.ALIGN_CENTER;
            default:
                return Layout.Alignment.ALIGN_NORMAL;
        }
    }

    public int getLineSpace() {
        return Math.max(0,lineSpace);
    }

    public int getzIndex() {
        return zIndex;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.type);
        dest.writeString(this.colorStr);
        dest.writeString(this.fontName);
        dest.writeInt(this.fontSize);
        dest.writeString(this.defaultContent);
        dest.writeString(this.defaultH5ImagePath);
        dest.writeString(this.frameStr);
        dest.writeInt(this.alignment);
        dest.writeInt(this.lineSpace);
        dest.writeInt(this.zIndex);
    }

    public TextHole() {}

    protected TextHole(Parcel in) {
        this.id = in.readLong();
        this.type = in.readString();
        this.colorStr = in.readString();
        this.fontName = in.readString();
        this.fontSize = in.readInt();
        this.defaultContent = in.readString();
        this.defaultH5ImagePath = in.readString();
        this.frameStr = in.readString();
        this.alignment = in.readInt();
        this.lineSpace = in.readInt();
        this.zIndex = in.readInt();
    }

    public static final Creator<TextHole> CREATOR = new Creator<TextHole>() {
        @Override
        public TextHole createFromParcel(Parcel source) {return new TextHole(source);}

        @Override
        public TextHole[] newArray(int size) {return new TextHole[size];}
    };
}
