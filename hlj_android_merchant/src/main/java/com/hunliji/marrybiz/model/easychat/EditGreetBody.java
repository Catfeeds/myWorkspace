package com.hunliji.marrybiz.model.easychat;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hua_rong on 2017/7/7.
 * 编辑问候语
 */

public class EditGreetBody implements Parcelable {

    @SerializedName(value = "example_speech")
    private String exampleSpeech;//案例，没修改无需传
    @SerializedName(value = "home_speech")
    private String homeSpeech;//主页，没修改无需传
    @SerializedName(value = "package_speech")
    private String packageSpeech;//套餐，没修改无需传

    public String getExampleSpeech() {
        return exampleSpeech;
    }

    public void setExampleSpeech(String exampleSpeech) {
        this.exampleSpeech = exampleSpeech;
    }

    public String getHomeSpeech() {
        return homeSpeech;
    }

    public void setHomeSpeech(String homeSpeech) {
        this.homeSpeech = homeSpeech;
    }

    public String getPackageSpeech() {
        return packageSpeech;
    }

    public void setPackageSpeech(String packageSpeech) {
        this.packageSpeech = packageSpeech;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.exampleSpeech);
        dest.writeString(this.homeSpeech);
        dest.writeString(this.packageSpeech);
    }

    public EditGreetBody() {}

    protected EditGreetBody(Parcel in) {
        this.exampleSpeech = in.readString();
        this.homeSpeech = in.readString();
        this.packageSpeech = in.readString();
    }

    public static final Parcelable.Creator<EditGreetBody> CREATOR = new Parcelable
            .Creator<EditGreetBody>() {
        @Override
        public EditGreetBody createFromParcel(Parcel source) {return new EditGreetBody(source);}

        @Override
        public EditGreetBody[] newArray(int size) {return new EditGreetBody[size];}
    };
}
