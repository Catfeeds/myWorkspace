package com.hunliji.marrybiz.model.merchantservice;

import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.orders.BdProduct;

import java.io.Serializable;

/**
 * Created by mo_yu on 2018/1/29.商家服务本地数据
 */

public enum MerchantServerEnum {
    YOU_HUI_JUAN(BdProduct.YOU_HUI_JUAN, R.mipmap.icon_coupon_90_90, "优惠券"), TIAN_YAN_XI_TONG(
            BdProduct.TIAN_YAN_XI_TONG,
            R.mipmap.icon_eye_system,
            "天眼系统"), JU_KE_BAO(BdProduct.JU_KE_BAO,
            R.mipmap.icon_poly_off_treasure,
            "聚客宝"), QING_SONG_LIAO(BdProduct.QING_SONG_LIAO,
            R.mipmap.icon_easy_chat,
            "轻松聊"), HUO_DONG_WEI_CHUAN_DAN(BdProduct.HUO_DONG_WEI_CHUAN_DAN,
            R.mipmap.icon_event_flyer,
            "活动微传单"), WEDDING_WALL(BdProduct.WEDDING_WALL,
            R.mipmap.icon_wedding_wall,
            "婚礼墙"), XIAO_CHENG_XU(BdProduct.XIAO_CHENG_XU,
            R.mipmap.icon_min_progrom,
            "小程序"), WEI_GUAN_WANG(BdProduct.WEI_GUAN_WANG,
            R.mipmap.icon_micro_website,
            "微官网"), ZHU_TI_MU_BAN(BdProduct.ZHU_TI_MU_BAN,
            R.mipmap.icon_theme_template_market,
            "主题模板"), TUI_JIAN_CHU_CHUANG(BdProduct.TUI_JIAN_CHU_CHUANG,
            R.mipmap.icon_recommended_window,
            "推荐橱窗"), DUO_DIAN_GUAN_LI(BdProduct.DUO_DIAN_GUAN_LI,
            R.mipmap.icon_shop_manager_market,
            "多店管理"), DAO_DIAN_LI(BdProduct.DAO_DIAN_LI,
            R.mipmap.icon_shop_gift,
            "到店礼"), SHANG_JIA_CHENG_NUO(BdProduct.SHANG_JIA_CHENG_NUO,
            R.mipmap.icon_merchant_promise,
            "商家承诺"), DING_DAN_KE_TUI(BdProduct.DING_DAN_KE_TUI,
            R.mipmap.icon_return_order,
            "订单可退"), TAO_CAN_RE_BIAO(BdProduct.TAO_CAN_RE_BIAO, R.mipmap.icon_package_hot, "套餐热标");

    private long productId;
    private int icon;
    private String title;

    MerchantServerEnum(int productId, int icon, String title) {
        this.productId = productId;
        this.icon = icon;
        this.title = title;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
