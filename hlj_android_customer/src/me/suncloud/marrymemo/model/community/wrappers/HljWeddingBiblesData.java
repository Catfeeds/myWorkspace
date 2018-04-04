package me.suncloud.marrymemo.model.community.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljhttplibrary.entities.HljHttpData;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.model.community.WeddingCatalog;

/**
 * 结婚宝典
 * Created by chen_bin on 2018/3/16 0016.
 */
public class HljWeddingBiblesData extends HljHttpData<List<WeddingCatalog>> {
    @SerializedName(value = "last_users")
    private List<Author> lastUsers;
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "watch_count")
    private int watchCount;
    @SerializedName(value = "share")
    private ShareInfo shareInfo;

    public List<Author> getLastUsers() {
        if (lastUsers == null) {
            lastUsers = new ArrayList<>();
        }
        return lastUsers;
    }

    public String getTitle() {
        return title;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }
}
