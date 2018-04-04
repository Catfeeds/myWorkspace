package me.suncloud.marrymemo.model.finder;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

/**
 * cmp通用model
 * Created by chen_bin on 2017/9/7 0007.
 */
public class CPMFeed {
    @SerializedName(value = "entity_type")
    private int entityType;
    @SerializedName("entity")
    private JsonElement entity;

    private transient boolean isShowSimilarIcon = true; //是否显示找相似icon
    private transient Object entityObj;
    private transient long entityObjId;

    public transient final static int ENTITY_TYPE_MERCHANT = 1; //店铺
    public transient final static int ENTITY_TYPE_WORK = 2; //套餐
    public transient final static int ENTITY_TYPE_CASE = 3; //案例

    public int getEntityType() {
        return entityType;
    }

    public boolean isShowSimilarIcon() {
        return isShowSimilarIcon;
    }

    public void setShowSimilarIcon(boolean showSimilarIcon) {
        isShowSimilarIcon = showSimilarIcon;
    }

    public Object getEntityObj() {
        if (entityObj != null) {
            return entityObj;
        }
        switch (entityType) {
            case ENTITY_TYPE_MERCHANT:
                entityObj = GsonUtil.getGsonInstance()
                        .fromJson(entity, Merchant.class);
                break;
            case ENTITY_TYPE_WORK:
            case ENTITY_TYPE_CASE:
                entityObj = GsonUtil.getGsonInstance()
                        .fromJson(entity, Work.class);
                break;
        }
        return entityObj;
    }

    public long getEntityObjId() {
        if (entityObjId != 0) {
            return entityObjId;
        }
        Object obj = getEntityObj();
        if (obj instanceof Merchant) {
            entityObjId = ((Merchant) obj).getId();
        } else if (obj instanceof Work) {
            entityObjId = ((Work) obj).getId();
        }
        return entityObjId;
    }
}
