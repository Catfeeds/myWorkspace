package com.hunliji.hljcardlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wangtao on 2017/6/10.
 */

public class Card implements Parcelable {

    public static final int COORD_TYPE_BAIDU = 0;
    public static final int COORD_TYPE_GAO_DE = 1;

    private long id;
    @SerializedName("theme_id")
    private long themeId;
    @SerializedName("bride_name")
    private String brideName;
    @SerializedName("groom_name")
    private String groomName;
    private double latitude;
    @SerializedName("longtitude")
    private double longitude;
    private String place;
    private DateTime time;
    private ArrayList<CardPage> pages;
    private Theme theme;
    @SerializedName("front_page")
    private CardPage frontPage;
    @SerializedName("speech_page")
    private CardPage speechPage;
    private boolean closed;//是否关闭状态
    @SerializedName("edit_link")
    private String editLink;
    @SerializedName("preview_only_link")
    private String previewOnlyLink;
    @SerializedName("map_type")
    private int mapType; // 地图坐标体系， 0：百度坐标体系，1：高德坐标体系
    @SerializedName("user_id")
    private long userId;

    private Boolean isCopyCard;//是否是复制的请帖

    public void editCard(
            String brideName,
            String groomName,
            double latitude,
            double longitude,
            String place,
            Date time, int coordType) {
        this.brideName = brideName;
        this.groomName = groomName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.place = place;
        this.time = new DateTime(time);
        this.mapType = coordType;
    }

    public void setTheme(Theme theme) {
        themeId = theme.getId();
        if (theme.getFrontPage() != null) {
            frontPage = new CardPage(theme.getFrontPage());
        }
        if (theme.getSpeechPage() != null) {
            speechPage = new CardPage(theme.getSpeechPage());
        }
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getThemeId() {
        return themeId;
    }

    public String getBrideName() {
        return brideName;
    }

    public String getGroomName() {
        return groomName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPlace() {
        return place;
    }

    public DateTime getTime() {
        return time;
    }

    public ArrayList<CardPage> getPages() {
        return pages;
    }

    public List<CardPage> getAllPages() {
        List<CardPage> allPages = new ArrayList<>();
        if (frontPage != null) {
            allPages.add(frontPage);
        }
        if (!CommonUtil.isCollectionEmpty(pages)) {
            allPages.addAll(pages);
        }
        if (speechPage != null) {
            allPages.add(speechPage);
        }
        return allPages;
    }

    public Theme getTheme() {
        return theme;
    }

    public CardPage getFrontPage() {
        return frontPage;
    }

    public CardPage getSpeechPage() {
        return speechPage;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public void setFrontPage(CardPage frontPage) {
        this.frontPage = frontPage;
    }

    public void setSpeechPage(CardPage speechPage) {
        this.speechPage = speechPage;
    }

    public void setPage(CardPage page) {
        if (frontPage != null && frontPage.getId() == page.getId()) {
            frontPage = page;
            return;
        }
        if (speechPage != null && speechPage.getId() == page.getId()) {
            speechPage = page;
            return;
        }

        if (pages == null) {
            pages = new ArrayList<>();
        }
        int index = pages.size();
        for (CardPage cardPage : pages) {
            if (cardPage.getId() == page.getId()) {
                index = pages.indexOf(cardPage);
                pages.remove(cardPage);
                break;
            }
        }
        pages.add(index, page);
    }

    public void removePage(CardPage cardPage) {
        if(CommonUtil.isCollectionEmpty(pages)){
            return;
        }
        for(CardPage page:pages){
            if(page.getId()==cardPage.getId()){
                pages.remove(page);
                break;
            }
        }
    }

    public void setPages(List<CardPage> pages) {
        if(this.pages==null){
            this.pages=new ArrayList<>();
        }
        this.pages.clear();
        this.pages.addAll(pages);
    }

    public void hideSpeech(boolean isHide) {
        if(speechPage==null){
            return;
        }
        speechPage.setHidden(isHide);
    }

    public String getEditLink() {
        return editLink;
    }

    public String getPreviewOnlyLink() {
        return previewOnlyLink;
    }

    public void setCopyCard(Boolean copyCard) {
        isCopyCard = copyCard;
    }

    public Boolean isCopyCard() {
        if(isCopyCard==null){
            return false;
        }
        return isCopyCard;
    }

    public void setMapType(int mapType) {
        this.mapType = mapType;
    }

    public int getMapType() {
        return mapType;
    }

    public long getUserId() {
        return userId;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.themeId);
        dest.writeString(this.brideName);
        dest.writeString(this.groomName);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.place);
        dest.writeLong(this.time == null ? -1 : this.time.getMillis());
        dest.writeTypedList(this.pages);
        dest.writeParcelable(this.theme, flags);
        dest.writeParcelable(this.frontPage, flags);
        dest.writeParcelable(this.speechPage, flags);
        dest.writeByte(this.closed ? (byte) 1 : (byte) 0);
        dest.writeString(this.editLink);
        dest.writeString(this.previewOnlyLink);
        dest.writeInt(this.mapType);
    }

    public Card() {}

    protected Card(Parcel in) {
        this.id = in.readLong();
        this.themeId = in.readLong();
        this.brideName = in.readString();
        this.groomName = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.place = in.readString();
        long millis = in.readLong();
        if (millis > 0) {
            this.time = new DateTime(millis);
        }
        this.pages = in.createTypedArrayList(CardPage.CREATOR);
        this.theme = in.readParcelable(Theme.class.getClassLoader());
        this.frontPage = in.readParcelable(CardPage.class.getClassLoader());
        this.speechPage = in.readParcelable(CardPage.class.getClassLoader());
        this.closed = in.readByte() != 0;
        this.editLink = in.readString();
        this.previewOnlyLink = in.readString();
        this.mapType = in.readInt();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel source) {return new Card(source);}

        @Override
        public Card[] newArray(int size) {return new Card[size];}
    };
}
