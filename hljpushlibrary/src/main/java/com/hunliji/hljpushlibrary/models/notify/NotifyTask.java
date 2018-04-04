package com.hunliji.hljpushlibrary.models.notify;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

/**
 * Created by wangtao on 2017/11/30.
 */

public class NotifyTask {

    public static final int SHOW_KIND_TEXT=1;
    public static final int SHOW_KIND_IMAGE=2;

    @SerializedName("id")
    private long id;
    @SerializedName("kind")
    private int kind;
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;
    @SerializedName("image_path")
    private String imagePath;
    @SerializedName("poster")
    private Poster poster;
    @SerializedName("end_at")
    private DateTime endTime;

    public NotifyTask(Poster poster) {
        this.poster = poster;
    }

    public long getId() {
        return id;
    }

    public int getKind() {
        return kind;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Poster getPoster() {
        return poster;
    }

    public boolean isExceed() {
        return endTime != null && HljTimeUtils.timeServerTimeZone(endTime)
                .isBefore(HljTimeUtils.getServerCurrentTimeMillis());
    }
}
