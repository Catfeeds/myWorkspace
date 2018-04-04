/**
 *
 */
package me.suncloud.marrymemo.model;

import com.hunliji.hljcommonlibrary.HljCommon;

import org.json.JSONObject;

import java.util.ArrayList;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * @author iDay
 */
public class Photo implements Identifiable {

    private long id;
    private String path;
    private String localPath;
    private int width;
    private int height;
    private String description;
    private int kind;
    private int type;
    private String videoPath;
    private Persistent persistent;
    private String persistentId;

    public Photo(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.path = JSONUtil.getString(jsonObject, "path");
            if (JSONUtil.isEmpty(path)) {
                this.path = JSONUtil.getString(jsonObject, "photo_path");
            }
            if (JSONUtil.isEmpty(path)) {
                this.path = JSONUtil.getString(jsonObject, "img");
            }
            if (JSONUtil.isEmpty(path)) {
                this.path = JSONUtil.getString(jsonObject, "url");
            }
            if (JSONUtil.isEmpty(path)) {
                this.path = JSONUtil.getString(jsonObject, "image_path");
            }
            if (!JSONUtil.isEmpty(path) && !path.startsWith("http")) {
                path = "http://" + path;
            }
            if (JSONUtil.isEmpty(path)) {
                this.path = JSONUtil.getString(jsonObject, "media_path");
            }
            this.width = jsonObject.optInt("width", 0);
            this.height = jsonObject.optInt("height", 0);
            this.id = jsonObject.optLong("id", 0);
            this.kind = jsonObject.optInt("kind", 2);
            this.description = JSONUtil.getString(jsonObject, "description");
            if (JSONUtil.isEmpty(description)) {
                this.description = JSONUtil.getString(jsonObject, "describe");
            }
        }
    }

    public void setVideo(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.videoPath = JSONUtil.getString(jsonObject, "video_path");
            this.width = jsonObject.optInt("width", 0);
            this.height = jsonObject.optInt("height", 0);
            this.id = jsonObject.optLong("id", 0);
            this.kind = jsonObject.optInt("kind", 3);
            this.description = JSONUtil.getString(jsonObject, "description");
            if (!jsonObject.isNull("persistent_path")) {
                persistent = new Persistent(jsonObject.optJSONObject("persistent_path"));
            }
        }
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    public String getDescription() {
        return JSONUtil.isEmpty(description) ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public int getKind() {
        return kind;
    }

    public Persistent getPersistent() {
        return persistent;
    }

    public void setPersistentId(String persistentId) {
        this.persistentId = persistentId;
    }

    public String getPersistentId() {
        return persistentId;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public static com.hunliji.hljcommonlibrary.models.Photo converterToCommonPhoto(Photo photo) {
        com.hunliji.hljcommonlibrary.models.Photo cPhoto = new com.hunliji.hljcommonlibrary
                .models.Photo();
        cPhoto.setId(photo.getId());
        cPhoto.setHeight(photo.getHeight());
        cPhoto.setImagePath(photo.getPath());
        cPhoto.setWidth(photo.getWidth());
        cPhoto.setDescribe(photo.getDescription());

        return cPhoto;
    }

    public static ArrayList<com.hunliji.hljcommonlibrary.models.Photo> converterToCommonPhotos(
            ArrayList<Photo> photos) {
        ArrayList<com.hunliji.hljcommonlibrary.models.Photo> cPhotos = new ArrayList<>();
        for (Photo photo : photos) {
            cPhotos.add(converterToCommonPhoto(photo));
        }

        return cPhotos;
    }
}
