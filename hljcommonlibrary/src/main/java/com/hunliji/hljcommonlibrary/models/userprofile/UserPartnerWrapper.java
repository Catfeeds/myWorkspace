package com.hunliji.hljcommonlibrary.models.userprofile;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by werther on 16/8/29.
 * 用户主页包含用户和伴侣两个账户信息的wrapper类
 */
public class UserPartnerWrapper implements Parcelable {
    UserProfileWrapper user;
    UserProfileWrapper partner;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.partner, flags);
    }

    public UserPartnerWrapper() {}

    protected UserPartnerWrapper(Parcel in) {
        this.user = in.readParcelable(UserProfile.class.getClassLoader());
        this.partner = in.readParcelable(UserProfile.class.getClassLoader());
    }

    public static final Parcelable.Creator<UserPartnerWrapper> CREATOR = new Parcelable
            .Creator<UserPartnerWrapper>() {
        @Override
        public UserPartnerWrapper createFromParcel(Parcel source) {
            return new UserPartnerWrapper(source);
        }

        @Override
        public UserPartnerWrapper[] newArray(int size) {return new UserPartnerWrapper[size];}
    };

    public UserProfileWrapper getUser() {
        return user;
    }

    public UserProfileWrapper getPartner() {
        return partner;
    }

    public void setCurrentUserById(long userId) {
        if (user.getUserProfile()
                .getId() != userId) {
            swapProfile();
        }
    }

    /**
     * 交换user和partner
     */
    public void swapProfile() {
        UserProfileWrapper tmp = user;
        user = partner;
        partner = tmp;
    }
}
