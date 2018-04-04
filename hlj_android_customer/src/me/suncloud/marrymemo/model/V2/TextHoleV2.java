package me.suncloud.marrymemo.model.V2;

import android.graphics.Matrix;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.suncloud.marrymemo.model.Identifiable;
import me.suncloud.marrymemo.model.TransInfo;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.PageTextView;

/**
 * Created by Suncloud on 2016/4/22.
 */
public class TextHoleV2 implements Identifiable {

    private long id;
    private int x;
    private int y;
    private int w;
    private int h;
    private String colorStr;
    private int fontSize;
    private int alignment;
    private String type;
    private String fontName;
    private String content;
    private TransInfo transInfo;
    private String h5TextPath;
    private String h5TextFrame;
    private boolean showText;
    private int width;
    private int height;
    private int defaultColor;
    private int left;
    private int top;

    public TextHoleV2(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id");
            colorStr = JSONUtil.getString(jsonObject, "color");
            fontSize = jsonObject.optInt("font_size");
            alignment = jsonObject.optInt("alignment");
            type = JSONUtil.getString(jsonObject, "type");
            content = JSONUtil.getString(jsonObject, "content");
            fontName = JSONUtil.getString(jsonObject, "font_name");
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
            transInfo = new TransInfo(JSONUtil.getString(jsonObject, "trans_info"));

            h5TextPath = JSONUtil.getString(jsonObject, "h5_text_image_path");
            showText = jsonObject.optInt("show_text",1)>0;
            String h5FrameStr = JSONUtil.getString(jsonObject, "h5_text_image_frame");
            if (!JSONUtil.isEmpty(h5FrameStr)) {
                Pattern pattern = Pattern.compile("[^,]+");
                Matcher matcher = pattern.matcher(h5FrameStr);
                int i = 0;
                while (matcher.find()) {
                    String string = matcher.group(0);
                    Double d = Double.valueOf(string);
                    if (d != null && d != Double.NaN) {
                        int point = d.intValue();
                        switch (i) {
                            case 0:
                                left = point;
                                break;
                            case 1:
                                top = point;
                                break;
                            case 2:
                                width = point;
                                break;
                            case 3:
                                height = point;
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

    public float getAngle() {
        if (transInfo == null) {
            return 0;
        }
        return transInfo.getAngle();
    }

    public float getScale() {
        if (transInfo == null) {
            return 1;
        }
        return transInfo.getScale2();
    }

    public int getColor() {
        return Util.parseColor(colorStr);
    }

    public int getFontSize() {
        return fontSize;
    }

    public int getAlignment() {
        return alignment;
    }

    public String getType() {
        return type;
    }

    public boolean isSingleLine() {
        return !JSONUtil.isEmpty(type) && ("groom".endsWith(type) || "bride".endsWith(type) ||
                "time".endsWith(type) || "lunar".endsWith(type));
    }

    public String getFontName() {
        return fontName;
    }

    public String getContent() {
        return content;
    }

    public TransInfo getTransInfo() {
        return transInfo;
    }

    public String getTransInfoStr() {
        if (transInfo == null) {
            return "1,0,0,1,0,0";
        }
        return transInfo.getA() + "," + transInfo.getB() + "," + transInfo.getC() + "," +
                transInfo.getD() + "," + transInfo.getTx() + "," + transInfo.getTy();
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isShowText() {
        return showText;
    }

    public String getH5TextPath() {
        return h5TextPath;
    }

    public void setH5TextPath(String h5TextPath) {
        this.h5TextPath = h5TextPath;
    }

    public void setText(PageTextView textView, float scale) {
        showText=!textView.isHide();
        float scaleX = textView.getScaleX() / scale;
        float scaleY = textView.getScaleY() / scale;
        w = textView.getWidth();
        h = textView.getHeight();
        x = (int) ((textView.getLeft() - w * (scale - 1) / 2) / scale);
        y = (int) ((textView.getTop() - h * (scale - 1) / 2) / scale);
        width = (int) (w * scaleX + .5);
        height = (int) (h * scaleY + .5);
        left = (int) (x + w * (1 - scaleX) / 2);
        top = (int) (y + h * (1 - scaleY) / 2);
        content = textView.getText().toString();
        h5TextFrame = left + "," + top + "," + width + "," + height;
        Matrix matrix = new Matrix();
        matrix.postRotate(textView.getRotation());
        matrix.postScale(scaleX, scaleY);
        transInfo.setMatrix(matrix);
        colorStr = Util.parseColor(textView.getTextColor());
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getWidth() {
        if (width <= 0) {
            return w;
        }
        return width;
    }

    public int getHeight() {
        if (height <= 0) {
            return h;
        }
        return height;
    }

    public String getH5TextFrame() {
        if (JSONUtil.isEmpty(h5TextFrame)) {
            return getFrame();
        }
        return h5TextFrame;
    }

    public String getFrame() {
        return x + "," + y + "," + w + "," + h;
    }

    public String getColorStr() {
        return colorStr;
    }


    public String getInfoStr() {
        if(!showText){
            return null;
        }
        if(JSONUtil.isEmpty(content)||JSONUtil.isEmpty(content.trim())){
            return null;
        }
        return x + "," + y + "," + getAngle() + "," + h5TextPath+","+isShowText();
    }

    public int getDefaultColor() {
        if(defaultColor==0){
            defaultColor=getColor();
        }
        return defaultColor;
    }

    public void setDefaultColor(String colorStr) {
        defaultColor=getColor();
    }
}
