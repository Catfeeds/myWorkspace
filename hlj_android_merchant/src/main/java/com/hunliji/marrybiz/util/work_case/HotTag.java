package com.hunliji.marrybiz.util.work_case;


import com.hunliji.marrybiz.R;

/**
 * 套餐-热标枚举
 * Created by chen_bin on 2016/12/8 0008.
 */
public enum HotTag {

    WORK_REC(1, R.drawable.icon_recommend_tag_primary), //人气推荐
    WORK_TOP(2, R.drawable.icon_top_tag_primary), //本季热卖
    WORK_CHEAP(3, R.drawable.icon_discount_tag_primary); //超值特价

    private int type;
    private int drawable;

    HotTag(int type, int drawable) {
        this.type = type;
        this.drawable = drawable;
    }

    public int getType() {
        return type;
    }

    public int getDrawable() {
        return drawable;
    }

    public static HotTag getHotTag(int type) {
        for (HotTag t : HotTag.values()) {
            if (t.getType() == type) {
                return t;
            }
        }
        return null;
    }
}