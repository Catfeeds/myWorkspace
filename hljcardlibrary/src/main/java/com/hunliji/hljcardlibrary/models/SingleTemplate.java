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
 * Created by hua_rong on 2017/6/16.
 * 单页模板
 */

public class SingleTemplate implements Parcelable {

    @SerializedName(value = "other_tpl")
    private ArrayList<Template> otherTpl;
    @SerializedName(value = "theme_tpl")
    private ArrayList<Template> themeTpl;
    @SerializedName(value = "user_theme_lock")
    private boolean isThemeLock;

    public ArrayList<Template> getOtherTpl() {
        return otherTpl;
    }

    public void setOtherTpl(ArrayList<Template> otherTpl) {
        this.otherTpl = otherTpl;
    }

    public ArrayList<Template> getThemeTpl() {
        return themeTpl;
    }

    public void setThemeTpl(ArrayList<Template> themeTpl) {
        this.themeTpl = themeTpl;
    }

    public void isThemeLock(
            Context context,
            HljHttpSubscriber checkSub,
            final ChooseTheme.IsLockCallback callback) {
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

    public void isThemeLock(boolean isAvailable, final ChooseTheme.IsLockCallback callback) {
        if (!isThemeLock) {
            if (callback != null) {
                callback.isLockBack(false);
            }
            return;
        }else {
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
        dest.writeTypedList(this.otherTpl);
        dest.writeTypedList(this.themeTpl);
        dest.writeByte(this.isThemeLock ? (byte) 1 : (byte) 0);
    }

    public SingleTemplate() {}

    protected SingleTemplate(Parcel in) {
        this.otherTpl = in.createTypedArrayList(Template.CREATOR);
        this.themeTpl = in.createTypedArrayList(Template.CREATOR);
        this.isThemeLock = in.readByte() != 0;
    }

    public static final Creator<SingleTemplate> CREATOR = new Creator<SingleTemplate>() {
        @Override
        public SingleTemplate createFromParcel(Parcel source) {return new SingleTemplate(source);}

        @Override
        public SingleTemplate[] newArray(int size) {return new SingleTemplate[size];}
    };
}
