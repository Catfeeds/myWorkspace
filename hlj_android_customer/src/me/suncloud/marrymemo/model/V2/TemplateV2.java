package me.suncloud.marrymemo.model.V2;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import me.suncloud.marrymemo.model.Font;
import me.suncloud.marrymemo.model.Identifiable;
import me.suncloud.marrymemo.util.CardResourceUtil;
import me.suncloud.marrymemo.util.FileUtil;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2016/4/22.
 */
public class TemplateV2 implements Identifiable {

    private long id;
    private String name;
    private boolean isLocked;
    private String thumbPath;
    private ArrayList<ImageHoleV2> imageHoles;
    private ArrayList<TextHoleV2> textHoles;
    private String background;
    private int width;
    private int height;
    private boolean isSaved;
    private boolean isDownLoading;
    private int value;

    public TemplateV2(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id");
            isLocked = jsonObject.optInt("is_locked") > 0;
            if(!isLocked) {
                isLocked = jsonObject.optBoolean("is_locked");
            }
            name = JSONUtil.getString(jsonObject, "name");
            thumbPath = JSONUtil.getString(jsonObject, "thumb_path");
            JSONArray imageArray = jsonObject.optJSONArray("image");
            if (imageArray != null && imageArray.length() > 0) {
                imageHoles = new ArrayList<>();
                for (int i = 0, size = imageArray.length(); i < size; i++) {
                    imageHoles.add(new ImageHoleV2(imageArray.optJSONObject(i)));
                }
                Collections.sort(imageHoles, new Comparator<ImageHoleV2>() {
                    @Override
                    public int compare(ImageHoleV2 imageHole1, ImageHoleV2 imageHole2) {
                        return imageHole1.getzOrder() - imageHole2.getzOrder();
                    }

                });
            }
            JSONArray textArray = jsonObject.optJSONArray("text");
            if (textArray != null && textArray.length() > 0) {
                textHoles = new ArrayList<>();
                for (int i = 0, size = textArray.length(); i < size; i++) {
                    TextHoleV2 textHoleV2=new TextHoleV2(textArray.optJSONObject(i));
                    if(textHoleV2.getFontSize()>0) {
                        textHoles.add(textHoleV2);
                    }
                }
            }
            background = JSONUtil.getString(jsonObject, "background");
            width = jsonObject.optInt("width");
            height = jsonObject.optInt("height");
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public ArrayList<ImageHoleV2> getImageHoles() {
        if (imageHoles == null) {
            imageHoles = new ArrayList<>();
        }
        return imageHoles;
    }

    public ArrayList<TextHoleV2> getTextHoles() {
        if (textHoles == null) {
            textHoles = new ArrayList<>();
        }
        return textHoles;
    }

    public String getName() {
        return name;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public String getBackground() {
        return background;
    }

    public int getWidth() {
        if (width == 0) {
            width = 750;
        }
        return width;
    }

    public int getHeight() {
        if (height == 0) {
            height = 1220;
        }
        return height;
    }


    public void onSaveCheck(Context context) {
        isSaved = getImagePaths(context).isEmpty() && getFontPaths(context).isEmpty();
    }

    public boolean isSaved() {
        return isSaved;
    }

    public ArrayList<String> getImagePaths(Context context) {
        ArrayList<String> imagePaths = new ArrayList<>();
        if (!JSONUtil.isEmpty(background)) {
            File file = FileUtil.createThemeFile(context, background);
            if (file == null || !file.exists() || file.length() == 0) {
                imagePaths.add(background);
            }
        }
        for (ImageHoleV2 imageHole : getImageHoles()) {
            String maskPath = imageHole.getMaskImagePath();
            if (!JSONUtil.isEmpty(maskPath) && !imagePaths.contains(maskPath)) {
                File file = FileUtil.createThemeFile(context, maskPath);
                if (file == null || !file.exists() || file.length() == 0) {
                    imagePaths.add(maskPath);
                }
            }
        }
        return imagePaths;
    }

    public ArrayList<String> getFontPaths(Context context) {
        ArrayList<String> fontPaths = new ArrayList<>();
        for (TextHoleV2 textHole : getTextHoles()) {
            Font font = CardResourceUtil.getInstance().getFont(context, textHole.getFontName());
            if (font != null && !fontPaths.contains(font.getUrl())) {
                File file = FileUtil.createFontFile(context, font.getUrl());
                if (file == null || !file.exists() || file.length() == 0) {
                    fontPaths.add(font.getUrl());
                }
            }
        }
        return fontPaths;
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
}
