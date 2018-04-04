package me.suncloud.marrymemo.model.community;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * 目录
 * Created by chen_bin on 2018/3/15 0015.
 */
public class WeddingCatalog {
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "children")
    private List<WeddingBible> children;

    public String getTitle() {
        return title;
    }

    public List<WeddingBible> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }
}
