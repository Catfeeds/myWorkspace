package com.hunliji.marrybiz.model.shoptheme;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hua_rong on 2017/5/19.
 * 主题模板
 */

public class ShopTheme implements Parcelable {

    long id;//1,2,3,4,5
    String title;//'典雅白','尊贵黑','清新绿','雅典蓝','樱花粉'
    @SerializedName(value = "cover_path")
    String path;
    @SerializedName(value = "theme")
    int theme;//获取旗舰版配置 0 不是旗舰版

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.path);
        dest.writeInt(this.theme);
    }

    public ShopTheme() {}

    protected ShopTheme(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.path = in.readString();
        this.theme = in.readInt();
    }

    public static final Creator<ShopTheme> CREATOR = new Creator<ShopTheme>() {
        @Override
        public ShopTheme createFromParcel(Parcel source) {return new ShopTheme(source);}

        @Override
        public ShopTheme[] newArray(int size) {return new ShopTheme[size];}
    };
}
