package me.suncloud.marrymemo.model.V2;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.suncloud.marrymemo.model.Identifiable;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2016/4/22.
 */
public class ImageHoleV2 implements Identifiable {

    private long id;
    private int x;
    private int y;
    private int w;
    private int h;
    private int zOrder;
    private String path;
    private String maskImagePath;
    private String h5ImagePath;
    private String transInfoStr;

    public ImageHoleV2(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id");
            zOrder = jsonObject.optInt("z_order");
            maskImagePath = JSONUtil.getString(jsonObject, "mask_image_path");
            String frameStr = JSONUtil.getString(jsonObject, "frame");
            if (!JSONUtil.isEmpty(frameStr)) {
                Pattern pattern = Pattern.compile("[^,]+");
                Matcher matcher = pattern.matcher(frameStr);
                int i = 0;
                while (matcher.find()) {
                    String string = matcher.group(0);
                    Double d = Double.valueOf(string);
                    if (d != null && d != Double.NaN) {
                        int point = d.intValue();
                        switch (i) {
                            case 0:
                                x = point;
                                break;
                            case 1:
                                y = point;
                                break;
                            case 2:
                                w = point;
                                break;
                            case 3:
                                h = point;
                                break;
                        }
                        i++;
                        if (i > 3) {
                            break;
                        }
                    }
                }
            }

        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public int getzOrder() {
        return zOrder;
    }

    public String getMaskImagePath() {
        return maskImagePath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setH5ImagePath(String h5ImagePath) {
        this.h5ImagePath = h5ImagePath;
    }

    public String getH5ImagePath() {
        return h5ImagePath;
    }

    public void setTransInfoStr(String transInfoStr) {
        this.transInfoStr = transInfoStr;
    }

    public String getTransInfoStr() {
        if (JSONUtil.isEmpty(transInfoStr)) {
            transInfoStr = "1,0,0,1,0,0";
        }
        return transInfoStr;
    }
}
