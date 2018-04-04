package me.suncloud.marrymemo.model;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.*;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljkefulibrary.moudles.Support;

import java.util.List;

/**
 * Created by wangtao on 2017/10/26.
 */

public class WeddingConsult {

    @SerializedName("support")
    private Support support;
    @SerializedName("items_info")
    private String infoPath;
    @SerializedName("item")
    private AdvItemsObject itemsObject;
    @SerializedName("share")
    private com.hunliji.hljcommonlibrary.models.ShareInfo shareInfo;


    public List<AdvItem> getItems() {
        return itemsObject == null ? null : itemsObject.getItems();
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public Support getSupport() {
        return support;
    }

    public String getInfoPath() {
        return infoPath;
    }

    private class AdvItemsObject {
        @SerializedName("item")
        private List<AdvItem> items;

        public List<AdvItem> getItems() {
            return items;
        }
    }

}
