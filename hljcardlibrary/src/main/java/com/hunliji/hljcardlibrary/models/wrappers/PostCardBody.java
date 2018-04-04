package com.hunliji.hljcardlibrary.models.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcardlibrary.models.Card;

import org.joda.time.DateTime;

/**
 * Created by wangtao on 2017/7/10.
 */

public class PostCardBody {

    private Long id;
    @SerializedName("theme_id")
    private Long themeId;
    @SerializedName("bride_name")
    private String brideName;
    @SerializedName("groom_name")
    private String groomName;
    private double latitude;
    @SerializedName("longtitude")
    private double longitude;
    private String place;
    private DateTime time;
    @SerializedName("front_page")
    private PostPageBody frontPage;
    @SerializedName("speech_page")
    private PostPageBody speechPage;
    @SerializedName("map_type")
    private int mapType;

    public PostCardBody(Card card) {
        if (card.getId() > 0) {
            this.id = card.getId();
        }
        if (card.getThemeId() > 0) {
            this.themeId = card.getThemeId();
        }
        this.brideName = card.getBrideName();
        this.groomName = card.getGroomName();
        this.latitude = card.getLatitude();
        this.longitude = card.getLongitude();
        this.place = card.getPlace();
        this.time = card.getTime();
        this.mapType = card.getMapType();
        if (card.getFrontPage() != null) {
            frontPage = new PostPageBody(card.getFrontPage());
        }
        if (card.getSpeechPage() != null) {
            speechPage = new PostPageBody(card.getSpeechPage());
        }
    }
}
