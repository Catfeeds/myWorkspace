package com.hunliji.hljcommonlibrary.models.live;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by mo_yu on 2016/10/24.直播列表
 */

public class LiveChannel implements Parcelable {

    private long id;
    @SerializedName(value = "image_path")
    private String imagePath;
    @SerializedName(value = "state", alternate = "status")
    private int status;//1直播中 2未开始 3已结束
    private String title;
    private int type;//新娘课堂,type=3砍价会
    @SerializedName(value = "watch_count", alternate = "user_count")
    private int watch_count;
    @SerializedName("status_cn")
    private String statusCn;

    private ShareInfo share;

    @SerializedName("live_role")
    private int liveRole;

    @SerializedName(value = "participants", alternate = "users")
    private List<Author> users;

    @SerializedName(value = "start_time")
    private DateTime startTime;//直播开始时间
    @SerializedName(value = "end_time")
    private DateTime endTime;//直播结束时间

    private List<Merchant> merchants;
    @SerializedName("community_thread")
    private CommunityThread thread;
    @SerializedName("is_appointment")
    private boolean isAppointment;


    //直播当前的状态
    public transient static final int LIVE_IN_STATE = 1; //正在直播
    public transient static final int LIVE_PREPARE_STATE = 2;//即将开始
    public transient static final int LIVE_END_STATE = 3;//直播已经结束

    // 1主持 2嘉宾 3普通用户
    public transient static final int LIVE_ROLE_HOST = 1;
    public transient static final int LIVE_ROLE_GUEST_HOST = 2;
    public transient static final int LIVE_ROLE_CUSTOMER = 3;

    public transient static final int TYPE_BARGIN = 3; // 砍价会

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public int getType() {
        return type;
    }

    public int getWatch_count() {
        return watch_count;
    }

    public ShareInfo getShare() {
        return share;
    }

    public String getStatusCn() {
        return statusCn;
    }

    public int getLiveRole() {
        return liveRole;
    }

    public List<Author> getUsers() {
        return users;
    }

    public LiveChannel() {}

    public CommunityThread getThread() {
        if (thread != null && thread.getId() > 0) {
            return thread;
        }
        return null;
    }

    public Merchant getMerchant() {
        if (merchants != null && !merchants.isEmpty()) {
            return merchants.get(0);
        }
        return null;
    }

    public boolean isHost() {
        if (liveRole == LiveChannel.LIVE_ROLE_HOST || liveRole == LiveChannel
                .LIVE_ROLE_GUEST_HOST) {
            return true;
        }

        return false;
    }

    public boolean isExceed() {
        return endTime != null && HljTimeUtils.timeServerTimeZone(endTime)
                .isBefore(HljTimeUtils.getServerCurrentTimeMillis());
    }

    public void setAppointment(boolean appointment) {
        isAppointment = appointment;
    }

    public boolean isAppointment() {
        return isAppointment;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.imagePath);
        dest.writeInt(this.status);
        dest.writeString(this.title);
        dest.writeInt(this.type);
        dest.writeInt(this.watch_count);
        dest.writeString(this.statusCn);
        dest.writeParcelable(this.share, flags);
        dest.writeInt(this.liveRole);
        dest.writeTypedList(this.users);
        HljTimeUtils.writeDateTimeToParcel(dest,this.startTime);
        HljTimeUtils.writeDateTimeToParcel(dest,this.endTime);
        dest.writeTypedList(this.merchants);
        dest.writeParcelable(this.thread, flags);
        dest.writeByte(this.isAppointment ? (byte) 1 : (byte) 0);
    }

    protected LiveChannel(Parcel in) {
        this.id = in.readLong();
        this.imagePath = in.readString();
        this.status = in.readInt();
        this.title = in.readString();
        this.type = in.readInt();
        this.watch_count = in.readInt();
        this.statusCn = in.readString();
        this.share = in.readParcelable(ShareInfo.class.getClassLoader());
        this.liveRole = in.readInt();
        this.users = in.createTypedArrayList(Author.CREATOR);
        this.startTime = HljTimeUtils.readDateTimeToParcel(in);
        this.endTime = HljTimeUtils.readDateTimeToParcel(in);
        this.merchants = in.createTypedArrayList(Merchant.CREATOR);
        this.thread = in.readParcelable(CommunityThread.class.getClassLoader());
        this.isAppointment = in.readByte() != 0;
    }

    public static final Creator<LiveChannel> CREATOR = new Creator<LiveChannel>() {
        @Override
        public LiveChannel createFromParcel(Parcel source) {return new LiveChannel(source);}

        @Override
        public LiveChannel[] newArray(int size) {return new LiveChannel[size];}
    };
}
