package com.hunliji.marrybiz.model.easychat;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hua_rong on 2017/7/7.
 * model 修改后 不要自动序列化
 */

public class PendWord implements Parcelable {

    @SerializedName(value = "example_speech")
    private Speech exampleSpeech;//案例审核问候语
    @SerializedName(value = "home_speech")
    private Speech homeSpeech;//主页审核问候语
    @SerializedName(value = "package_speech")
    private Speech packageSpeech;//套餐审核问候语

    public Speech getExampleSpeech() {
        if (exampleSpeech == null){
            exampleSpeech = new Speech();
        }
        return exampleSpeech;
    }

    public void setExampleSpeech(Speech exampleSpeech) {
        this.exampleSpeech = exampleSpeech;
    }

    public Speech getHomeSpeech() {
        if (homeSpeech == null){
            homeSpeech = new Speech();
        }
        return homeSpeech;
    }

    public void setHomeSpeech(Speech homeSpeech) {
        this.homeSpeech = homeSpeech;
    }

    public Speech getPackageSpeech() {
        if (packageSpeech == null){
            packageSpeech = new Speech();
        }
        return packageSpeech;
    }

    public void setPackageSpeech(Speech packageSpeech) {
        this.packageSpeech = packageSpeech;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.exampleSpeech, flags);
        dest.writeParcelable(this.homeSpeech, flags);
        dest.writeParcelable(this.packageSpeech, flags);
    }

    public PendWord() {}

    protected PendWord(Parcel in) {
        this.exampleSpeech = in.readParcelable(Speech.class.getClassLoader());
        this.homeSpeech = in.readParcelable(Speech.class.getClassLoader());
        this.packageSpeech = in.readParcelable(Speech.class.getClassLoader());
    }

    public static final Parcelable.Creator<PendWord> CREATOR = new Parcelable.Creator<PendWord>() {
        @Override
        public PendWord createFromParcel(Parcel source) {return new PendWord(source);}

        @Override
        public PendWord[] newArray(int size) {return new PendWord[size];}
    };
}
