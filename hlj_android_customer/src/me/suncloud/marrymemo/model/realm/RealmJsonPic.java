package me.suncloud.marrymemo.model.realm;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.models.Photo;

import java.io.Serializable;

import io.realm.RealmObject;


public class RealmJsonPic extends RealmObject implements Parcelable {

    private long id;
    private String path;
    private int width;
    private int height;
    private int kind;
    //本地图片地址
    private String localPath;

    public RealmJsonPic() {
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPath() {
        if (TextUtils.isEmpty(path)) {
            return localPath;
        }
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public long getId() {
        return id;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public Photo convertToPhoto() {
        Photo photo = new Photo();
        photo.setId(this.id);
        photo.setImagePath(getPath());
        photo.setWidth(this.width);
        photo.setHeight(this.height);

        return photo;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.path);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeInt(this.kind);
        dest.writeString(this.localPath);
    }

    protected RealmJsonPic(Parcel in) {
        this.id = in.readLong();
        this.path = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.kind = in.readInt();
        this.localPath = in.readString();
    }

    public static final Creator<RealmJsonPic> CREATOR = new Creator<RealmJsonPic>() {
        @Override
        public RealmJsonPic createFromParcel(Parcel source) {return new RealmJsonPic(source);}

        @Override
        public RealmJsonPic[] newArray(int size) {return new RealmJsonPic[size];}
    };
}
