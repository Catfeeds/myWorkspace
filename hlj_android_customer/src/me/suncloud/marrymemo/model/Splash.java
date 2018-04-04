package me.suncloud.marrymemo.model;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Poster;

public class Splash {

    @SerializedName("cover_image")
    private String coverImage;
    @SerializedName("poster")
    private Poster poster;

    public Poster getPoster() {
        return poster;
    }

    public String getCoverImage() {
        return coverImage;
    }
}
