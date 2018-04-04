package me.suncloud.marrymemo.model.community;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityMaterial;

import java.util.List;

/**
 * Created by jinxin on 2018/3/20 0020.
 */

public class ObtainCommunityMaterial {
    @SerializedName("list")
    List<CommunityMaterial> list;
    @SerializedName("share")
    ShareInfo shareInfo;

    public List<CommunityMaterial> getList() {
        return list;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

}
