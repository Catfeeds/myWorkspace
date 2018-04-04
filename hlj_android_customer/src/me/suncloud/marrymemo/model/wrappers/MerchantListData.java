package me.suncloud.marrymemo.model.wrappers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import java.util.List;

/**
 * Created by wangtao on 2016/12/9.
 */

public class MerchantListData implements Parcelable {

    @SerializedName("normal_merchant")
    private HljHttpData<List<Merchant>> normalMerchant;
    @SerializedName("popular_merchant")
    private JsonElement popularJson;
    private HljHttpData<List<Merchant>> popularMerchant;
    @SerializedName("parent_cid")
    private long parentCid;
    @SerializedName("parent_city")
    private String parentCity;

    protected MerchantListData(Parcel in) {
    }

    public static final Creator<MerchantListData> CREATOR = new Creator<MerchantListData>() {
        @Override
        public MerchantListData createFromParcel(Parcel in) {
            return new MerchantListData(in);
        }

        @Override
        public MerchantListData[] newArray(int size) {
            return new MerchantListData[size];
        }
    };

    public HljHttpData<List<Merchant>> getNormalMerchant() {
        return normalMerchant;
    }

    public HljHttpData<List<Merchant>> getPopularMerchant() {
        if (popularMerchant != null) {
            return popularMerchant;
        }
        try {
            popularMerchant = GsonUtil.getGsonInstance()
                    .fromJson(popularJson,
                            new TypeToken<HljHttpData<List<Merchant>>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return popularMerchant;
    }

    public long getParentCid() {
        return parentCid;
    }

    public String getParentCity() {
        return parentCity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
