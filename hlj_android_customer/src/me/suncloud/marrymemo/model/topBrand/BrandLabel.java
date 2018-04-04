package me.suncloud.marrymemo.model.topBrand;

import me.suncloud.marrymemo.R;

/**
 * 需求描述：
 * 品牌馆新增“标志”的特殊参数： label
 * 可输入数字，分别代表：
 * 1：最大牌，2：最小资，3：超人气，
 * 若不填则不显示标志；
 * 后台增加提示
 * 位置：广告排期—首页频道品牌馆—编辑榜单
 * 内容：特殊参数名2：label：1：最大牌、2：最小资、3：超人气
 * 界面请见附件
 * Created by chen_bin on 2016/12/19 0019.
 */
public enum BrandLabel {

    TOP_BRAND(1, R.drawable.icon_top_brand), //最大牌
    LUXURIOUS(2, R.drawable.icon_luxurious), //最小资
    POPULAR(3, R.drawable.icon_popular); //超人气

    private int type;
    private int drawable;

    BrandLabel(int type, int drawable) {
        this.type = type;
        this.drawable = drawable;
    }

    public int getType() {
        return type;
    }

    public int getDrawable() {
        return drawable;
    }

    public static BrandLabel getBrandLabel(int type) {
        for (BrandLabel l : BrandLabel.values()) {
            if (l.getType() == type) {
                return l;
            }
        }
        return null;
    }
}