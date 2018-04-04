package com.hunliji.hljcardlibrary.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcardlibrary.utils.PrivilegeUtil;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;

import java.util.ArrayList;

/**
 * Created by hua_rong on 2017/6/21.
 */

public class ChooseTheme implements Parcelable {

    @SerializedName(value = "theme_list")
    ArrayList<Theme> themeList;
    @SerializedName(value = "user_theme_lock")
    boolean isThemeLock;

    public ArrayList<Theme> getThemeList() {
        if (themeList == null) {
            themeList = new ArrayList<>();
        }
        return themeList;
    }

    public void setThemeList(ArrayList<Theme> themeList) {
        this.themeList = themeList;
    }

    public void isThemeLock(
            Context context, HljHttpSubscriber checkSub, final IsLockCallback callback) {
        if (!isThemeLock) {
            if (callback != null) {
                callback.isLockBack(false);
            }
            return;
        }
        User user = UserSession.getInstance()
                .getUser(context);
        if (user instanceof CustomerUser) {
            PrivilegeUtil.isVipAvailable(context, checkSub, new PrivilegeUtil.PrivilegeCallback() {
                @Override
                public void checkDone(boolean isAvailable) {
                    if (callback != null) {
                        callback.isLockBack(isAvailable);
                    }
                }
            });
        }
    }

    public void isThemeLock(boolean isAvailable, final IsLockCallback callback) {
        if (!isThemeLock) {
            if (callback != null) {
                callback.isLockBack(false);
            }
            return;
        } else {
            if (callback != null) {
                callback.isLockBack(isAvailable);
            }
        }
    }

    public void setThemeLock(boolean isThemeLock) {
        this.isThemeLock = isThemeLock;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.themeList);
        dest.writeByte(this.isThemeLock ? (byte) 1 : (byte) 0);
    }

    public ChooseTheme() {}

    protected ChooseTheme(Parcel in) {
        this.themeList = in.createTypedArrayList(Theme.CREATOR);
        this.isThemeLock = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ChooseTheme> CREATOR = new Parcelable
            .Creator<ChooseTheme>() {
        @Override
        public ChooseTheme createFromParcel(Parcel source) {return new ChooseTheme(source);}

        @Override
        public ChooseTheme[] newArray(int size) {return new ChooseTheme[size];}
    };

    public interface IsLockCallback {
        void isLockBack(boolean isLock);
    }
}
