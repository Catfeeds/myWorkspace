package com.hunliji.marrybiz.model.tools;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.marrybiz.R;

import java.util.List;

/**
 * 店铺检测的model
 * Created by chen_bin on 2016/9/13 0013.
 */
public class CheckInfo implements Parcelable {
    @SerializedName(value = "extra")
    private List<String> extra;
    @SerializedName(value = "check")
    private boolean check;
    @SerializedName(value = "num")
    private int num;
    private int status;

    public transient final static int WORK_CHECK = 0;
    public transient final static int WORK_QUALITY = 1;
    public transient final static int CASE_CHECK = 2;
    public transient final static int CASE_QUALITY = 3;
    public transient final static int MERCHANT_FEED_COUNT = 4;
    public transient final static int MARKET_TOOL = 5;
    public transient final static int ORDER_NUM = 6;
    public transient final static int COMMENT = 7;

    public transient final static String[] titles = {"套餐数量", "套餐质量", "近30天案例更新数", "案例质量",
            "近30天动态更新数", "营销工具设置", "近30天新增订单", "近30天顾客评价",};

    public transient final static String[] contents = {"丰富的套餐，客户选择会更多", "影响客户下单的重要因素",
            "客户能持续看到您的新作", "影响客户感官的重要因素", "客户能对您更加了解", "提升客户转化率", "查看您的订单状况", "了解顾客口碑",};

    public transient final static String[] reachContents = {"已达到平台平均标准", "已上架套餐质量均达标",
            "近30天发布的案例数已达标", "已发布案例质量均达标", "近30天更新的动态数已达标", "使用情况良好", "近30天新增%s个订单",
            "近30天内无差评和中评",};

    public transient final static String[] unReachContents = {"至少上架%s个套餐", "%s个套餐内容存在提升空间",
            "建议近30天至少发布%s个案例", "%s个案例内容存在提升空间，建议优化", "坚持更新，增加店铺曝光", "%s功能未正常使用", "建议进行推广提升订单量",
            "请努力提升服务积累真实评价",};

    public transient final static String[] urls = {"p/wedding/index" + "" + "" + "" +
            ".php/Admin/APIMerchantCheck/PackageCheck", "p/wedding/index" + "" + "" + "" +
            ".php/Admin/APIMerchantCheck/PackageQuality", "p/wedding/index" + "" + "" + "" +
            ".php/Admin/APIMerchantCheck/CaseCheck", "p/wedding/index" + "" + "" + "" +
            ".php/Admin/APIMerchantCheck/CaseQuality", "p/wedding/index" + "" + "" + "" +
            ".php/Admin/APIMerchantCheck/MerchantFeedCount", "p/wedding/index" + "" + "" + "" +
            ".php/Admin/APIMerchantCheck/marketTool", "p/wedding/index" + "" + "" + "" +
            ".php/Admin/APIMerchantCheck/orderNum", "p/wedding/index" + "" + "" + "" +
            ".php/Admin/APIMerchantCheck/comment",};

    public transient final static int[] drawables = {R.drawable.icon_check_work_num, R.drawable
            .icon_check_work_quality, R.drawable.icon_check_case_num, R.drawable
            .icon_check_case_quality, R.drawable.icon_check_twitter_num, R.drawable
            .icon_check_tool, R.drawable.icon_check_new_order_num, R.drawable
            .icon_check_negative_comment_num,};

    public List<String> getExtra() {
        return extra;
    }

    public void setExtra(List<String> extra) {
        this.extra = extra;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.extra);
        dest.writeByte(this.check ? (byte) 1 : (byte) 0);
        dest.writeInt(this.num);
        dest.writeInt(this.status);
    }

    public CheckInfo() {}

    protected CheckInfo(Parcel in) {
        this.extra = in.createStringArrayList();
        this.check = in.readByte() != 0;
        this.num = in.readInt();
        this.status = in.readInt();
    }

    public static final Creator<CheckInfo> CREATOR = new Creator<CheckInfo>() {
        @Override
        public CheckInfo createFromParcel(Parcel source) {return new CheckInfo(source);}

        @Override
        public CheckInfo[] newArray(int size) {return new CheckInfo[size];}
    };
}