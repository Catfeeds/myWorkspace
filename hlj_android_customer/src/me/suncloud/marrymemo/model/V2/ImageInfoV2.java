package me.suncloud.marrymemo.model.V2;

import org.json.JSONObject;

import java.io.Serializable;

import me.suncloud.marrymemo.model.TransInfo;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2016/5/14.
 */
public class ImageInfoV2 implements Serializable {
    private String imagePath;
    private String h5ImagePath;
    private TransInfo transInfo;
    private long holeId;

    public ImageInfoV2(JSONObject json) {
        if (json != null) {
            this.imagePath = JSONUtil.getString(json, "image_path");
            this.h5ImagePath = JSONUtil.getString(json, "h5_hole_image_path");
            this.transInfo = new TransInfo(JSONUtil.getString(json, "trans_info"));
            this.holeId = json.optLong("image_hole_id");
        }
    }

    public String getImagePath() {
        return imagePath;
    }

    public TransInfo getTransInfo() {
        return transInfo;
    }

    public long getHoleId() {
        return holeId;
    }

    public String getH5ImagePath() {
        return h5ImagePath;
    }

    public void setHoleId(long holeId) {
        this.holeId = holeId;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getInfoStr(){
        return h5ImagePath;
    }
}