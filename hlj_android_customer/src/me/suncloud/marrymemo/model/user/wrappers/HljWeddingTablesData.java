package me.suncloud.marrymemo.model.user.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljhttplibrary.entities.HljHttpData;

import java.util.List;

import me.suncloud.marrymemo.model.tools.WeddingTable;

/**
 * Created by chen_bin on 2017/11/24 0024.
 */
public class HljWeddingTablesData extends HljHttpData<List<WeddingTable>> {
    @SerializedName(value = "share")
    private ShareInfo shareInfo;
    @SerializedName(value = "preview_url")
    private String previewUrl;

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

}

