package me.suncloud.marrymemo.model.V2;

import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import me.suncloud.marrymemo.model.Identifiable;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2016/4/22.
 */
public class ThemeV2 implements Identifiable {

    private long id;
    private String name;
    private boolean isLocked;
    private String previewLink;
    private String thumbPath;
    private TemplateV2 frontPage;
    private TemplateV2 speechPage;
    private boolean isSaved;
    private boolean isDownLoading;
    private int value;
    private Date createdAt;

    public ThemeV2(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id");
            isLocked = jsonObject.optInt("is_locked") > 0;
            if (!isLocked) {
                isLocked = jsonObject.optBoolean("is_locked");
            }
            name = JSONUtil.getString(jsonObject, "name");
            previewLink = JSONUtil.getString(jsonObject, "preview_link");
            thumbPath = JSONUtil.getString(jsonObject, "thumb_path");
            JSONObject frontObject = jsonObject.optJSONObject("front_page");
            if (frontObject != null) {
                frontPage = new TemplateV2(frontObject);
            }
            JSONObject speechObject = jsonObject.optJSONObject("speech_page");
            if (speechObject != null) {
                speechPage = new TemplateV2(speechObject);
            }
            this.createdAt = JSONUtil.getDateFromFormatLong(jsonObject, "created_at", true);
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPreviewLink() {
        return previewLink;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public TemplateV2 getFrontPage() {
        return frontPage;
    }

    public TemplateV2 getSpeechPage() {
        return speechPage;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void onSaveCheck(Context context) {
        isSaved = true;
        if (frontPage != null) {
            frontPage.onSaveCheck(context);
            if (!frontPage.isSaved()) {
                isSaved = false;
                return;
            }
        }
        if (speechPage != null) {
            speechPage.onSaveCheck(context);
            if (!speechPage.isSaved()) {
                isSaved = false;
            }
        }
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setDownLoading(boolean downLoading) {
        isDownLoading = downLoading;
    }

    public boolean isDownLoading() {
        return isDownLoading;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
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
        return fonts;
    }


    /**
     * @param date 最后一次进入请帖列表时间
     * @return true 模板列表更新提示
     */
    public boolean isNew(Date date) {
        return isNew() && createdAt.after(date);
    }

    /**
     * 创建时间在7天内
     * @return  true 新模板
     */
    public boolean isNew() {
        return createdAt!=null&&System.currentTimeMillis()-createdAt.getTime()<7*24*60*60*1000;
    }
}
