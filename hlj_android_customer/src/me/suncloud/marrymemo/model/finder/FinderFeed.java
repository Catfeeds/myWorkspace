package me.suncloud.marrymemo.model.finder;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.note.NoteMark;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

/**
 * 发现页feeds流
 * Created by chen_bin on 2018/2/5 0005.
 */
public class FinderFeed {
    private JsonElement entity;
    private String type;
    private boolean isShowSimilarIcon = true; //是否显示找相似icon
    private boolean isShowRelevantHint; //笔记中提及，提供X个套餐，提供X个案例

    private transient Object entityObj;
    private transient long entityObjId;

    public transient final static String TYPE_MERCHANT = "merchant"; //商家
    public transient final static String TYPE_PRODUCT = "shop_product"; //婚品
    public transient final static String TYPE_MARK = "mark"; //标签
    public transient final static String TYPE_WORK = "package"; //套餐
    public transient final static String TYPE_CASE = "example"; //案例
    public transient final static String TYPE_NOTE = "note"; //笔记

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonElement getEntity() {
        return entity;
    }

    public boolean isShowSimilarIcon() {
        return isShowSimilarIcon;
    }

    public void setShowSimilarIcon(boolean showSimilarIcon) {
        isShowSimilarIcon = showSimilarIcon;
    }

    public boolean isShowRelevantHint() {
        return isShowRelevantHint;
    }

    public void setShowRelevantHint(boolean showRelevantHint) {
        isShowRelevantHint = showRelevantHint;
    }

    public Object getEntityObj() {
        if (entityObj != null) {
            return entityObj;
        }
        Gson gson = GsonUtil.getGsonInstance();
        switch (type) {
            case TYPE_MERCHANT:
                entityObj = gson.fromJson(entity, Merchant.class);
                break;
            case TYPE_PRODUCT:
                entityObj = gson.fromJson(entity, ShopProduct.class);
                break;
            case TYPE_MARK:
                entityObj = gson.fromJson(entity, NoteMark.class);
                break;
            case TYPE_WORK:
            case TYPE_CASE:
                entityObj = gson.fromJson(entity, Work.class);
                break;
            case TYPE_NOTE:
                entityObj = gson.fromJson(entity, Note.class);
                break;
        }
        return entityObj;
    }

    public void setEntityObj(Object entityObj) {
        this.entityObj = entityObj;
    }

    public long getEntityObjId() {
        if (entityObjId != 0) {
            return entityObjId;
        }
        Object obj = getEntityObj();
        if (obj instanceof Merchant) {
            entityObjId = ((Merchant) obj).getId();
        } else if (obj instanceof ShopProduct) {
            entityObjId = ((ShopProduct) obj).getId();
        } else if (obj instanceof NoteMark) {
            entityObjId = ((NoteMark) obj).getId();
        } else if (obj instanceof Work) {
            entityObjId = ((Work) obj).getId();
        } else if (obj instanceof Note) {
            entityObjId = ((Note) obj).getId();
        }
        return entityObjId;
    }

    public void convertToEntity(FinderFeed feed) {
        setEntityObj(feed.getEntityObj());
    }
}