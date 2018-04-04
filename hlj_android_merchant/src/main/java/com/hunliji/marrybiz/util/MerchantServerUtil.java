package com.hunliji.marrybiz.util;

import android.content.Context;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.merchantservice.MarketItem;
import com.hunliji.marrybiz.model.orders.BdProduct;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/30.
 */

public class MerchantServerUtil {

    private List<MarketItem> marketItems;

    private static class SingleHolder {
        private static final MerchantServerUtil INSTANCE = new MerchantServerUtil();
    }

    public static final MerchantServerUtil getInstance() {
        return SingleHolder.INSTANCE;
    }

    /**
     * 从本地存储的文件中读取properties列表
     *
     * @param mContext
     * @return
     */
    public List<MarketItem> getMarketItemFromFile(Context mContext) {
        if (marketItems == null) {
            marketItems = new ArrayList<>();
        }
        marketItems.clear();
        JSONArray array = null;
        try {
            array = (new JSONArray(JSONUtil.readStreamToString(mContext.getResources()
                    .openRawResource(R.raw.merchant_server))));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (array != null) {
            int size = array.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    MarketItem marketItem = new MarketItem(array.optJSONObject(i));
                    marketItems.add(marketItem);
                }
            }
        }
        return marketItems;
    }

    public MarketItem getMarketItem(Context context, long productId) {
        List<MarketItem> marketItems = getMarketItemFromFile(context);
        if (!CommonUtil.isCollectionEmpty(marketItems)) {
            for (MarketItem marketItem : marketItems) {
                if (marketItem.getProductId() == productId) {
                    return marketItem;
                }
            }
        }
        return null;
    }

    public int getMerchantServerImgRes(long id) {
        if (id == BdProduct.ZHUAN_YE_BAN) {
            return R.mipmap.icon_zhuan_ye_ban;
        } else if (id == BdProduct.BAO_ZHENG_JIN) {
            return R.mipmap.icon_bao_zhengz_jin;
        } else if (id == BdProduct.QI_JIAN_BAN) {
            return R.mipmap.icon_qi_jian_ban;
        } else if (id == BdProduct.XIAO_CHENG_XU) {
            return R.mipmap.icon_xiao_cheng_xu;
        } else if (id == BdProduct.TAO_CAN_RE_BIAO) {
            return R.mipmap.icon_tao_can_re_biao;
        } else if (id == BdProduct.DING_DAN_KE_TUI) {
            return R.mipmap.icon_ding_dan_ke_tui;
        } else if (id == BdProduct.SHANG_JIA_CHENG_NUO) {
            return R.mipmap.icon_shang_jia_cheng_nuo;
        } else if (id == BdProduct.DAO_DIAN_LI) {
            return R.mipmap.icon_dao_dian_li;
        } else if (id == BdProduct.DUO_DIAN_GUAN_LI) {
            return R.mipmap.icon_duo_dian_guan_li;
        } else if (id == BdProduct.TUI_JIAN_CHU_CHUANG) {
            return R.mipmap.icon_tui_jian_chu_chuang;
        } else if (id == BdProduct.ZHU_TI_MU_BAN) {
            return R.mipmap.icon_zhu_ti_mu_ban;
        } else if (id == BdProduct.WEI_GUAN_WANG) {
            return R.mipmap.icon_wei_guan_wang;
        } else if (id == BdProduct.HUO_DONG_WEI_CHUAN_DAN) {
            return R.mipmap.icon_huo_dong_wei_chuan_dan;
        } else if (id == BdProduct.QING_SONG_LIAO) {
            return R.mipmap.icon_qing_song_liao;
        } else if (id == BdProduct.TIAN_YAN_XI_TONG) {
            return R.mipmap.icon_tian_yan_xi_tong;
        } else if (id == BdProduct.YOU_HUI_JUAN) {
            return R.mipmap.icon_you_hui_juan;
        } else if (id == BdProduct.JU_KE_BAO) {
            return R.mipmap.icon_ju_ke_bao;
        } else if (id == BdProduct.WEDDING_WALL) {
            return R.mipmap.icon_wedding_wall_big;
        }else if (id == BdProduct.YUN_KE) {
            return R.mipmap.icon_yun_ke_big;
        }
        return -1;
    }

    public int getMerchantServerRoundImgRes(long id) {
        if (id == BdProduct.XIAO_CHENG_XU) {
            return R.mipmap.icon_min_progrom;
        } else if (id == BdProduct.TAO_CAN_RE_BIAO) {
            return R.mipmap.icon_package_hot;
        } else if (id == BdProduct.DING_DAN_KE_TUI) {
            return R.mipmap.icon_return_order;
        } else if (id == BdProduct.SHANG_JIA_CHENG_NUO) {
            return R.mipmap.icon_merchant_promise;
        } else if (id == BdProduct.DAO_DIAN_LI) {
            return R.mipmap.icon_shop_gift;
        } else if (id == BdProduct.DUO_DIAN_GUAN_LI) {
            return R.mipmap.icon_shop_manager_market;
        } else if (id == BdProduct.TUI_JIAN_CHU_CHUANG) {
            return R.mipmap.icon_recommended_window;
        } else if (id == BdProduct.ZHU_TI_MU_BAN) {
            return R.mipmap.icon_theme_template_market;
        } else if (id == BdProduct.WEI_GUAN_WANG) {
            return R.mipmap.icon_micro_website;
        } else if (id == BdProduct.HUO_DONG_WEI_CHUAN_DAN) {
            return R.mipmap.icon_event_flyer;
        } else if (id == BdProduct.QING_SONG_LIAO) {
            return R.mipmap.icon_easy_chat;
        } else if (id == BdProduct.TIAN_YAN_XI_TONG) {
            return R.mipmap.icon_eye_system;
        } else if (id == BdProduct.YOU_HUI_JUAN) {
            return R.mipmap.icon_coupon_90_90;
        } else if (id == BdProduct.JU_KE_BAO) {
            return R.mipmap.icon_poly_off_treasure;
        } else if (id == BdProduct.WEDDING_WALL) {
            return R.mipmap.icon_wedding_wall;
        }
        return -1;
    }
}
