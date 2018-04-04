package me.suncloud.marrymemo.model.budget;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.ArrayList;

import me.suncloud.marrymemo.model.Identifiable;
import me.suncloud.marrymemo.model.PointD;

/**
 * Created by jinxin on 2016/5/27.
 */
public class BudgetCategory implements Parcelable {
    private long id;
    private long pid;
    private String title;
    private String icon;
    private boolean checked;
    private int kind;//算法种类
    private double money;//类别计算以后的金额
    private double rate;//当前所占的比例
    private ArrayList<Double> rates;//比例数组
    private ArrayList<PointD> moneyRange;//价格区间  每一个算法的类别对应一种区间

    public static final String CPM_TITLE = "婚纱摄影"; // 显示cpmlist的类别

    public BudgetCategory(JSONObject json) {
        if (json != null) {
            id = json.optLong("id", 0);
            pid = json.optLong("pid", 0);
            title = json.optString("title");
            money = json.optDouble("budgetMoney", 0);
            icon = json.optString("icon");
            rates = new ArrayList();
            moneyRange = new ArrayList();
        }
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean toggleChecked() {
        this.checked = !checked;
        return checked;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPid() {
        return pid;
    }

    public String getTitle() {
        return title;
    }

    public String getIcon() {
        return icon;
    }

    public long getId() {
        return id;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setRates(int position, Double rate) {
        rates.add(position, rate);
    }

    public void setMoneyRange(int position, PointD pointD) {
        moneyRange.add(position, pointD);
    }

    public ArrayList<Double> getRates() {
        return rates;
    }

    public ArrayList<PointD> getMoneyRange() {
        return moneyRange;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.pid);
        dest.writeString(this.title);
        dest.writeString(this.icon);
        dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
        dest.writeInt(this.kind);
        dest.writeDouble(this.money);
        dest.writeDouble(this.rate);
        dest.writeList(this.rates);
        dest.writeList(this.moneyRange);
    }

    protected BudgetCategory(Parcel in) {
        this.id = in.readLong();
        this.pid = in.readLong();
        this.title = in.readString();
        this.icon = in.readString();
        this.checked = in.readByte() != 0;
        this.kind = in.readInt();
        this.money = in.readDouble();
        this.rate = in.readDouble();
        this.rates = new ArrayList<Double>();
        in.readList(this.rates, Double.class.getClassLoader());
        this.moneyRange = new ArrayList<PointD>();
        in.readList(this.moneyRange, PointD.class.getClassLoader());
    }

    public static final Parcelable.Creator<BudgetCategory> CREATOR = new Parcelable.Creator<BudgetCategory>() {
        @Override
        public BudgetCategory createFromParcel(Parcel source) {return new BudgetCategory(source);}

        @Override
        public BudgetCategory[] newArray(int size) {return new BudgetCategory[size];}
    };
}
