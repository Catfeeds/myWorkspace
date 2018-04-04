package me.suncloud.marrymemo.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

import me.suncloud.marrymemo.R;

/**
 * 用户动态
 * Created by chen_bin on 2017/11/28 0028.
 */
public class UserDynamic implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "entity_id")
    private long entityId;
    @SerializedName(value = "created_at")
    private DateTime createdAt;
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "desc")
    private String desc;
    @SerializedName(value = "type")
    private int type;

    public enum UserDynamicType {

        TYPE_WEDDING_ACCOUNT(1, R.mipmap.icon_user_dynamic_wedding_account), //支出(账本)
        TYPE_RECORD_WEDDING_TASK(2, R.mipmap.icon_user_dynamic_wedding_task), //事项(任务)
        TYPE_COMPLETE_WEDDING_TASK(3, R.mipmap.icon_user_dynamic_wedding_task), //完成(任务)
        TYPE_WORK(4, R.mipmap.icon_user_dynamic_collect), //收藏套餐
        TYPE_CASE(5, R.mipmap.icon_user_dynamic_collect), //收藏案例
        TYPE_PRODUCT(6, R.mipmap.icon_user_dynamic_collect), //收藏婚品
        TYPE_NOTE(7, R.mipmap.icon_user_dynamic_collect), //收藏笔记
        TYPE_SHOPPING_CART(8, R.mipmap.icon_user_dynamic_shopping_cart), //加入购物车
        TYPE_MERCHANT(9, R.mipmap.icon_user_dynamic_merchant), //商家
        TYPE_CARD(10, R.mipmap.icon_user_dynamic_card); //电子请柬详情

        private int type;
        private int drawable;

        public int getType() {
            return type;
        }

        public int getDrawable() {
            return drawable;
        }

        UserDynamicType(int type, int drawable) {
            this.type = type;
            this.drawable = drawable;
        }

        public static UserDynamicType getType(int type) {
            for (UserDynamicType t : UserDynamicType.values()) {
                if (t.getType() == type) {
                    return t;
                }
            }
            return null;
        }
    }

    public long getId() {
        return id;
    }

    public long getEntityId() {
        return entityId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public int getType() {
        return type;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.entityId);
        HljTimeUtils.writeDateTimeToParcel(dest, this.createdAt);
        dest.writeString(this.title);
        dest.writeString(this.desc);
        dest.writeInt(this.type);
    }

    public UserDynamic() {}

    protected UserDynamic(Parcel in) {
        this.id = in.readLong();
        this.entityId = in.readLong();
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.title = in.readString();
        this.desc = in.readString();
        this.type = in.readInt();
    }

    public static final Creator<UserDynamic> CREATOR = new Creator<UserDynamic>() {
        @Override
        public UserDynamic createFromParcel(Parcel source) {
            return new UserDynamic(source);
        }

        @Override
        public UserDynamic[] newArray(int size) {return new UserDynamic[size];}
    };
}
