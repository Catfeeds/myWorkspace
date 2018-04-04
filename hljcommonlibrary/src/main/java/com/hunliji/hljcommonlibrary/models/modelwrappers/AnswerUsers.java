package com.hunliji.hljcommonlibrary.models.modelwrappers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Suncloud on 2016/8/30.
 */
public class AnswerUsers implements Parcelable {

    private List<QaAuthor> users;
    @SerializedName("user_count")
    private int userCount;

    public int getUserCount() {
        return userCount;
    }

    public List<QaAuthor> getUsers() {
        return users;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.users);
        dest.writeInt(this.userCount);
    }

    public AnswerUsers() {}

    protected AnswerUsers(Parcel in) {
        this.users = in.createTypedArrayList(QaAuthor.CREATOR);
        this.userCount = in.readInt();
    }

    public static final Creator<AnswerUsers> CREATOR = new Creator<AnswerUsers>() {
        @Override
        public AnswerUsers createFromParcel(Parcel source) {return new AnswerUsers(source);}

        @Override
        public AnswerUsers[] newArray(int size) {return new AnswerUsers[size];}
    };
}
