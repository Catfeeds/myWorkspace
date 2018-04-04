package me.suncloud.marrymemo.model.V2;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.suncloud.marrymemo.model.Identifiable;
import me.suncloud.marrymemo.util.FileUtil;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2016/4/26.
 */
public class CardPageV2 implements Identifiable {
    private long id;
    private ArrayList<ImageInfoV2> images;
    private ArrayList<TextHoleV2> texts;
    private TemplateV2 template;
    private boolean isFront;
    private boolean isSpeech;
    private boolean hidden;
    private boolean delete;

    public CardPageV2(JSONObject object) {
        if (object != null) {
            this.id = object.optLong("id");
            int pageType = object.optInt("page_type");
            isFront = pageType == 1;
            isSpeech = pageType == 2;
            if (!object.isNull("images")) {
                JSONArray array = null;
                try {
                    array = new JSONArray(object.optString("images"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (array != null && array.length() > 0) {
                    images = new ArrayList<>();
                    for (int i = 0, size = array.length(); i < size; i++) {
                        images.add(new ImageInfoV2(array.optJSONObject(i)));
                    }
                }
            }
            template = new TemplateV2(object.optJSONObject("page_template"));
            if (!object.isNull("texts")) {
                JSONArray array = null;
                try {
                    array = new JSONArray(object.optString("texts"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (array != null && array.length() > 0) {
                    texts = new ArrayList<>();
                    for (int i = 0, size = array.length(); i < size; i++) {
                        TextHoleV2 text = new TextHoleV2(array.optJSONObject(i));
                        if (text.getFontSize() > 0) {
                            if (template != null) {
                                for (TextHoleV2 textHole : template.getTextHoles()) {
                                    if (text.getId()
                                            .equals(textHole.getId())) {
                                        text.setDefaultColor(textHole.getColorStr());
                                        break;
                                    }
                                }
                            }
                            texts.add(text);
                        }
                    }
                }
            }
            hidden = object.optInt("hidden") > 0;
        }
    }

    public ArrayList<ImageInfoV2> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }
        return images;
    }

    public ArrayList<ImageHoleV2> getImageHoles() {
        if (template != null) {
            return template.getImageHoles();
        }
        return new ArrayList<>();
    }

    public ArrayList<TextHoleV2> getTexts() {
        if (texts == null) {
            texts = new ArrayList<>();
        }
        return texts;
    }

    @Override
    public Long getId() {
        return id;
    }

    public boolean isFront() {
        return isFront;
    }

    public boolean isSpeech() {
        return isSpeech;
    }

    public void setFront(boolean front) {
        this.isFront = front;
    }

    public void setSpeech(boolean speech) {
        isSpeech = speech;
        getImages().clear();
        getTexts().clear();
        getImageHoles().clear();
    }

    public void setTemplate(TemplateV2 template) {
        this.template = template;
        texts = new ArrayList<>();
        texts.addAll(template.getTextHoles());
    }

    public TemplateV2 getTemplate() {
        return template;
    }

    public String getCardPageKey() {
        String keyInfo = String.valueOf(id);
        try {
            JSONObject jsonObject = new JSONObject();
            for (TextHoleV2 textInfo : getTexts()) {
                jsonObject.put(textInfo.getId()
                        .toString(), textInfo.getInfoStr());
            }
            for (ImageInfoV2 imageInfo : getImages()) {
                jsonObject.put(String.valueOf(imageInfo.getHoleId()), imageInfo.getInfoStr());
            }
            jsonObject.put("id", id);
            keyInfo = jsonObject.toString();
        } catch (JSONException ignored) {

        }
        return keyInfo;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isDelete() {
        return delete;
    }

    public void deletePageFile(Context context) {
        for (ImageInfoV2 imageInfo : getImages()) {
            if (!JSONUtil.isEmpty(imageInfo.getImagePath())) {
                FileUtil.deleteFile(FileUtil.createPageFile(context, imageInfo.getImagePath()));
            }
            if (!JSONUtil.isEmpty(imageInfo.getH5ImagePath())) {
                FileUtil.deleteFile(FileUtil.createPageFile(context, imageInfo.getH5ImagePath()));
            }
        }
        FileUtil.deleteFile(FileUtil.createPageFile(context, getCardPageKey()));
    }


    public void deletePageFile(Context context, CardPageV2 cardPageV2) {
        for (ImageInfoV2 imageInfo : getImages()) {
            for (ImageInfoV2 imageInfo2 : cardPageV2.getImages()) {
                if (imageInfo.getHoleId() != imageInfo2.getHoleId()) {
                    continue;
                }
                if (!JSONUtil.isEmpty(imageInfo.getImagePath()) && !imageInfo.getImagePath()
                        .equals(imageInfo2.getImagePath())) {
                    FileUtil.deleteFile(FileUtil.createPageFile(context, imageInfo.getImagePath()));
                }
                if (!JSONUtil.isEmpty(imageInfo.getH5ImagePath()) && !imageInfo.getH5ImagePath()
                        .equals(imageInfo2.getH5ImagePath())) {
                    FileUtil.deleteFile(FileUtil.createPageFile(context,
                            imageInfo.getH5ImagePath()));
                }
            }
        }
    }


    public ArrayList<String> getImagePaths(Context context) {
        if (template == null) {
            return new ArrayList<>();
        }
        return template.getImagePaths(context);
    }

    public ArrayList<String> getFontPaths(Context context) {
        if (template == null) {
            return new ArrayList<>();
        }
        return template.getFontPaths(context);
    }

}
