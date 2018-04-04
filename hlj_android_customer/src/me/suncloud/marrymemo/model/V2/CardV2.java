package me.suncloud.marrymemo.model.V2;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import me.suncloud.marrymemo.model.Audio;
import me.suncloud.marrymemo.model.Identifiable;
import me.suncloud.marrymemo.util.JSONUtil;
import com.hunliji.hljcardlibrary.utils.Lunar;
import me.suncloud.marrymemo.util.TimeUtil;
import me.suncloud.marrymemo.util.Util;

/**
 * Created by Suncloud on 2016/4/26.
 */
public class CardV2 implements Identifiable {

    public static final int COORD_TYPE_BAIDU = 0;
    public static final int COORD_TYPE_GAO_DE = 1;

    private long id;
    private String groomName;
    private String brideName;
    private Date time;
    private String place;
    private CardPageV2 frontPage;
    private CardPageV2 speechPage;
    private ArrayList<CardPageV2> pages;
    private double longitude;
    private double latitude;
    private String version;
    private String speech;
    private Audio audio;
    private String shareLink;
    private long themeId;
    private boolean isSampleCard;
    private String title;
    private int mapType; // 地图坐标体系， 0：百度坐标体系，1：高德坐标体系

    public CardV2(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            themeId = jsonObject.optLong("theme_id");
            this.groomName = JSONUtil.getString(jsonObject, "groom_name");
            this.brideName = JSONUtil.getString(jsonObject, "bride_name");
            this.time = TimeUtil.getCardDate(jsonObject, "time");
            this.place = JSONUtil.getString(jsonObject, "place");
            this.speech = JSONUtil.getString(jsonObject, "speech");
            if (!jsonObject.isNull("front_page")) {
                frontPage = new CardPageV2(jsonObject.optJSONObject("front_page"));
                frontPage.setFront(true);
            }
            if (!jsonObject.isNull("speech_page")) {
                speechPage = new CardPageV2(jsonObject.optJSONObject("speech_page"));
                if (JSONUtil.isEmpty(speech) && speechPage != null) {
                    for (TextHoleV2 textHole : speechPage.getTexts()) {
                        if ("speech".equals(textHole.getType())) {
                            speech = textHole.getContent();
                            break;
                        }
                    }
                }
                speechPage.setSpeech(true);
            }
            JSONArray array = jsonObject.optJSONArray("pages");
            if (array != null && array.length() > 0) {
                pages = new ArrayList<>();
                for (int i = 0, size = array.length(); i < size; i++) {
                    CardPageV2 cardPage = new CardPageV2(array.optJSONObject(i));
                    pages.add(cardPage);
                }
            }
            this.longitude = jsonObject.optDouble("longtitude", 0);
            this.latitude = jsonObject.optDouble("latitude", 0);
            this.version = JSONUtil.getString(jsonObject, "version");
            this.shareLink = JSONUtil.getString(jsonObject, "share_link");
            if (!jsonObject.isNull("audios_attributes")) {
                audio = new Audio(jsonObject.optJSONArray("audios_attributes"));
            }
            isSampleCard = jsonObject.optInt("is_sample_card") > 0;
            title = JSONUtil.getString(jsonObject, "title");
            this.mapType = jsonObject.optInt("map_type", 0);
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getGroomName() {
        return groomName;
    }

    public String getBrideName() {
        return brideName;
    }

    public Date getTime() {
        return time;
    }

    public String getPlace() {
        return place;
    }

    public CardPageV2 getFrontPage() {
        return frontPage;
    }

    public CardPageV2 getSpeechPage() {
        return speechPage;
    }

    public ArrayList<CardPageV2> getPages() {
        if (pages == null) {
            pages = new ArrayList<>();
        }
        return pages;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getVersion() {
        return version;
    }

    public String getSpeech() {
        return speech;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setGroomName(String groomName) {
        this.groomName = groomName;
    }

    public void setBrideName(String brideName) {
        this.brideName = brideName;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setSpeech(String speech) {
        this.speech = speech;
    }

    public long getThemeId() {
        return themeId;
    }

    public boolean isSampleCard() {
        return isSampleCard;
    }

    public void setTheme(ThemeV2 theme) {
        this.themeId = theme.getId();
        if (frontPage == null && theme.getFrontPage() != null) {
            frontPage = new CardPageV2(new JSONObject());
            frontPage.setFront(true);
            frontPage.setTemplate(theme.getFrontPage());
        }
        if (speechPage == null && theme.getSpeechPage() != null) {
            speechPage = new CardPageV2(new JSONObject());
            speechPage.setSpeech(true);
            speechPage.setTemplate(theme.getSpeechPage());
        }
    }

    public void editDefaultPage(Context context, CardV2 card) {
        if (frontPage != null) {
            for (TextHoleV2 textHole : frontPage.getTexts()) {
                switch (textHole.getType()) {
                    case "groom":
                        textHole.setContent(card.getGroomName());
                        break;
                    case "bride":
                        textHole.setContent(card.getBrideName());
                        break;
                    case "time":
                        if (card.getTime() != null) {
                            textHole.setContent(Util.getCardTimeString(context, card.getTime()));
                        }
                        break;
                    case "location":
                        textHole.setContent(card.getPlace());
                        break;
                    case "lunar":
                        if (card.getTime() != null) {
                            textHole.setContent(new Lunar(card.getTime()).toString());
                        }
                        break;
                }
            }
        }
        if (speechPage != null && !JSONUtil.isEmpty(card.getSpeech())) {
            for (TextHoleV2 textHole : speechPage.getTexts()) {
                if ("speech".equals(textHole.getType())) {
                    textHole.setContent(card.getSpeech());
                }
            }
        }
    }

    public void deleteCardFile(Context context) {
        if (frontPage != null) {
            frontPage.deletePageFile(context);
        }
        if (speechPage != null) {
            speechPage.deletePageFile(context);
        }
        for (CardPageV2 page : getPages()) {
            page.deletePageFile(context);
        }
    }


    public ArrayList<String> getImages(Context context) {
        ArrayList<String> images = new ArrayList<>();
        if (frontPage != null) {
            for (String string : frontPage.getImagePaths(context)) {
                if (!images.contains(string)) {
                    images.add(string);
                }
            }
        }
        if (speechPage != null) {
            for (String string : speechPage.getImagePaths(context)) {
                if (!images.contains(string)) {
                    images.add(string);
                }
            }
        }
        for (CardPageV2 page : getPages()) {
            for (String string : page.getImagePaths(context)) {
                if (!images.contains(string)) {
                    images.add(string);
                }
            }
        }
        return images;
    }

    public ArrayList<String> getFonts(Context context) {
        ArrayList<String> fonts = new ArrayList<>();
        if (frontPage != null) {
            for (String string : frontPage.getFontPaths(context)) {
                if (!fonts.contains(string)) {
                    fonts.add(string);
                }
            }
        }
        if (speechPage != null) {
            for (String string : speechPage.getFontPaths(context)) {
                if (!fonts.contains(string)) {
                    fonts.add(string);
                }
            }
        }
        for (CardPageV2 page : getPages()) {
            for (String string : page.getFontPaths(context)) {
                if (!fonts.contains(string)) {
                    fonts.add(string);
                }
            }
        }
        return fonts;
    }

    public String getTitle() {
        return title;
    }

    public int getMapType() {
        return mapType;
    }

    public void setMapType(int mapType) {
        this.mapType = mapType;
    }
}
