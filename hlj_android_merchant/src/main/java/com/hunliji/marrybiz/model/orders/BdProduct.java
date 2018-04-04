package com.hunliji.marrybiz.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mo_yu on 2017/12/13.BD产品
 */

public class BdProduct implements Parcelable {

    public static final int ZHUAN_YE_BAN = 12;//专业版
    public static final int BAO_ZHENG_JIN = 14;//保证金
    public static final int QI_JIAN_BAN = 50;//旗舰版
    public static final int XIAO_CHENG_XU = 65;//小程序
    public static final int TAO_CAN_RE_BIAO = 70;//套餐热标
    public static final int DING_DAN_KE_TUI = 71;//订单可退
    public static final int SHANG_JIA_CHENG_NUO = 72;//商家承诺
    public static final int DAO_DIAN_LI = 73;//到店礼
    public static final int DUO_DIAN_GUAN_LI = 74;//多店管理
    public static final int TUI_JIAN_CHU_CHUANG = 75;//推荐橱窗
    public static final int ZHU_TI_MU_BAN = 76;//主体模板
    public static final int WEI_GUAN_WANG = 77;//微官网
    public static final int HUO_DONG_WEI_CHUAN_DAN = 79;//活动微传单
    public static final int QING_SONG_LIAO = 80;//轻松聊
    public static final int TIAN_YAN_XI_TONG = 81;//天眼系统
    public static final int YOU_HUI_JUAN = 82;//优惠卷
    public static final int JU_KE_BAO = 53;//聚客宝
    public static final int WEDDING_WALL = 78;//婚礼墙
    public static final long YUN_KE = -1;//

    long id;
    String title;
    int type;//类型 0店铺产品 1广告产品 2转化产品 3其他

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.type);
    }

    public BdProduct() {}

    protected BdProduct(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<BdProduct> CREATOR = new Parcelable.Creator<BdProduct>
            () {
        @Override
        public BdProduct createFromParcel(Parcel source) {return new BdProduct(source);}

        @Override
        public BdProduct[] newArray(int size) {return new BdProduct[size];}
    };
}
