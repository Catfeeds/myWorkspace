package me.suncloud.marrymemo.model.tools;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

import me.suncloud.marrymemo.R;

/**
 * Created by chen_bin on 2017/12/12 0012.
 */
public class WeddingCalendarItem implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "date")
    private DateTime date;
    @SerializedName(value = "count")
    private int count;
    @SerializedName(value = "hot")
    private int hot;

    private transient String lunar;
    private transient String week;
    private transient int marriageStatus;
    private transient int engagementStatus;
    private transient int betrothalGiftStatus;
    private transient int uxorilocalMarriageStatus;
    private transient int solarTerm;

    public transient final static int STATUS_YI = 0; //宜
    public transient final static int STATUS_JI = 1; //忌
    public transient final static int STATUS_PING = 2; //平

    public transient final static String KEY_MARRIAGE = "嫁娶";
    public transient final static String KEY_ENGAGEMENT = "订盟";
    public transient final static String KEY_BETROTHAL_GIFT = "纳采";
    public transient final static String KEY_UXORILOCAL_MARRIAGE = "纳婿";

    public transient final static int TYPE_ALL = 0;
    public transient final static int TYPE_MARRIAGE = 1;
    public transient final static int TYPE_ENGAGEMENT = 2;
    public transient final static int TYPE_BETROTHAL_GIFT = 3;
    public transient final static int TYPE_UXORILOCAL_MARRIAGE = 4;

    public transient final static int SOLAR_TERM_SPRING_EQUINOX = 0; //春风
    public transient final static int SOLAR_TERM_SUMMER_SOLSTICE = 1;//夏至
    public transient final static int SOLAR_TERM_AUTUMN_EQUINOX = 2;//秋风
    public transient final static int SOLAR_TERM_WINTER_SOLSTICE = 3;//冬至


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }

    public String getLunar() {
        return lunar;
    }

    public void setLunar(String lunar) {
        this.lunar = lunar;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public int getMarriageStatus() {
        return marriageStatus;
    }

    public void setMarriageStatus(int marriageStatus) {
        this.marriageStatus = marriageStatus;
    }

    public int getEngagementStatus() {
        return engagementStatus;
    }

    public void setEngagementStatus(int engagementStatus) {
        this.engagementStatus = engagementStatus;
    }

    public int getBetrothalGiftStatus() {
        return betrothalGiftStatus;
    }

    public void setBetrothalGiftStatus(int betrothalGiftStatus) {
        this.betrothalGiftStatus = betrothalGiftStatus;
    }

    public int getUxorilocalMarriageStatus() {
        return uxorilocalMarriageStatus;
    }

    public void setUxorilocalMarriageStatus(int uxorilocalMarriageStatus) {
        this.uxorilocalMarriageStatus = uxorilocalMarriageStatus;
    }

    public int getSolarTerm() {
        return solarTerm;
    }

    public void setSolarTerm(int solarTerm) {
        this.solarTerm = solarTerm;
    }

    public int getSolarTermDrawable() {
        switch (solarTerm) {
            case SOLAR_TERM_SPRING_EQUINOX:
                return R.mipmap.image_bg_wedding_calendar_spring_equinox;
            case SOLAR_TERM_SUMMER_SOLSTICE:
                return R.mipmap.image_bg_wedding_calendar_summer_solstice;
            case SOLAR_TERM_AUTUMN_EQUINOX:
                return R.mipmap.image_bg_wedding_calendar_autumn_equinox;
            case SOLAR_TERM_WINTER_SOLSTICE:
                return R.mipmap.image_bg_wedding_calendar_winter_solstice;
            default:
                return 0;
        }
    }

    public int getYJDrawable(int status) {
        switch (status) {
            case STATUS_YI:
                return R.mipmap.icon_yi_tag_94_94;
            case STATUS_JI:
                return R.mipmap.icon_ji_tag_94_94;
            case STATUS_PING:
                return R.mipmap.icon_ping_tag_94_94;
            default:
                return 0;
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        HljTimeUtils.writeDateTimeToParcel(dest, this.date);
        dest.writeInt(this.count);
        dest.writeInt(this.hot);
        dest.writeString(this.lunar);
        dest.writeString(this.week);
        dest.writeInt(this.marriageStatus);
        dest.writeInt(this.engagementStatus);
        dest.writeInt(this.betrothalGiftStatus);
        dest.writeInt(this.uxorilocalMarriageStatus);
    }

    public WeddingCalendarItem() {}

    protected WeddingCalendarItem(Parcel in) {
        this.id = in.readLong();
        this.date = HljTimeUtils.readDateTimeToParcel(in);
        this.count = in.readInt();
        this.hot = in.readInt();
        this.lunar = in.readString();
        this.week = in.readString();
        this.marriageStatus = in.readInt();
        this.engagementStatus = in.readInt();
        this.betrothalGiftStatus = in.readInt();
        this.uxorilocalMarriageStatus = in.readInt();
    }

    public static final Creator<WeddingCalendarItem> CREATOR = new Creator<WeddingCalendarItem>() {
        @Override
        public WeddingCalendarItem createFromParcel(Parcel source) {
            return new WeddingCalendarItem(source);
        }

        @Override
        public WeddingCalendarItem[] newArray(int size) {return new WeddingCalendarItem[size];}
    };
}
