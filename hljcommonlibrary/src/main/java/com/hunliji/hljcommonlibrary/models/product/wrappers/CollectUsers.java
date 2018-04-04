package com.hunliji.hljcommonlibrary.models.product.wrappers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Author;

import java.util.List;

/**
 * Created by Administrator on 2016/11/11.
 */

public class CollectUsers implements Parcelable {

    private int num;
    @SerializedName("list")
    private List<Author> users;

    public int getNum() {
        return num;
    }

    public List<Author> getUsers() {
        return users;
    }

    protected CollectUsers(Parcel in) {
        num = in.readInt();
        users = in.createTypedArrayList(Author.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(num);
        dest.writeTypedList(users);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CollectUsers> CREATOR = new Creator<CollectUsers>() {
        @Override
        public CollectUsers createFromParcel(Parcel in) {
            return new CollectUsers(in);
        }

        @Override
        public CollectUsers[] newArray(int size) {
            return new CollectUsers[size];
        }
    };
}
