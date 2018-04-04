package me.suncloud.marrymemo.model.ad;

import android.os.Parcel;

import com.hunliji.hljcommonlibrary.models.Poster;

import java.util.List;

/**
 * Created by wangtao on 2016/12/2.
 */

public class MadPoster extends Poster {

    private String imgurl; //图片地址
    private String clickurl; //点击跳转
    private List<String> imgtracking; //展示统计
    private List<String> thclkurl; // 点击统计

    @Override
    public String getUrl() {
        return clickurl;
    }

    @Override
    public int getTargetType() {
        return -1;
    }

    @Override
    public String getPath() {
        return imgurl;
    }

    public List<String> getImgtracking() {
        return imgtracking;
    }

    public List<String> getThclkurl() {
        return thclkurl;
    }

    public MadPoster() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.imgurl);
        dest.writeString(this.clickurl);
        dest.writeStringList(this.imgtracking);
        dest.writeStringList(this.thclkurl);
    }

    protected MadPoster(Parcel in) {
        super(in);
        this.imgurl = in.readString();
        this.clickurl = in.readString();
        this.imgtracking = in.createStringArrayList();
        this.thclkurl = in.createStringArrayList();
    }

    public static final Creator<MadPoster> CREATOR = new Creator<MadPoster>() {
        @Override
        public MadPoster createFromParcel(Parcel source) {return new MadPoster(source);}

        @Override
        public MadPoster[] newArray(int size) {return new MadPoster[size];}
    };
}
