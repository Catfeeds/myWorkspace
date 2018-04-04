package me.suncloud.marrymemo.model.orders.hotelperoid;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.JsonPhoto;
import com.hunliji.hljcommonlibrary.models.Photo;

import java.util.List;

/**
 * Created by jinxin on 2018/3/9 0009.
 */

public class HotelPeriodImageBody implements Parcelable {

     @SerializedName("contract")
     private List<JsonPhoto> contract;//订单合同
     @SerializedName("partner_card_back")
     private JsonPhoto partnerCardBack;//配偶身份证背面
     @SerializedName("partner_card_front")
     private JsonPhoto partnerCardFront;//配偶身份证正面
     @SerializedName("photo_with_saler")
     private JsonPhoto photoWithSaler;//酒店销售（顾问）合照
     @SerializedName("user_card_back")
     private JsonPhoto userCardBack;//申请人身份证反面
     @SerializedName("user_card_front")
     private JsonPhoto userCardFront;//申请人身份证正面

     public void setContract(List<JsonPhoto> contract) {
          this.contract = contract;
     }

     public void setPartnerCardBack(JsonPhoto partnerCardBack) {
          this.partnerCardBack = partnerCardBack;
     }

     public void setPartnerCardFront(JsonPhoto partnerCardFront) {
          this.partnerCardFront = partnerCardFront;
     }

     public void setPhotoWithSaler(JsonPhoto photoWithSaler) {
          this.photoWithSaler = photoWithSaler;
     }

     public void setUserCardBack(JsonPhoto userCardBack) {
          this.userCardBack = userCardBack;
     }

     public void setUserCardFront(JsonPhoto userCardFront) {
          this.userCardFront = userCardFront;
     }

     @Override
     public int describeContents() { return 0; }

     @Override
     public void writeToParcel(Parcel dest, int flags) {
          dest.writeTypedList(this.contract);
          dest.writeParcelable(this.partnerCardBack, flags);
          dest.writeParcelable(this.partnerCardFront, flags);
          dest.writeParcelable(this.photoWithSaler, flags);
          dest.writeParcelable(this.userCardBack, flags);
          dest.writeParcelable(this.userCardFront, flags);
     }

     public HotelPeriodImageBody() {}

     protected HotelPeriodImageBody(Parcel in) {
          this.contract = in.createTypedArrayList(JsonPhoto.CREATOR);
          this.partnerCardBack = in.readParcelable(JsonPhoto.class.getClassLoader());
          this.partnerCardFront = in.readParcelable(JsonPhoto.class.getClassLoader());
          this.photoWithSaler = in.readParcelable(JsonPhoto.class.getClassLoader());
          this.userCardBack = in.readParcelable(JsonPhoto.class.getClassLoader());
          this.userCardFront = in.readParcelable(JsonPhoto.class.getClassLoader());
     }

     public static final Creator<HotelPeriodImageBody> CREATOR = new
             Creator<HotelPeriodImageBody>() {
          @Override
          public HotelPeriodImageBody createFromParcel(Parcel source) {
               return new HotelPeriodImageBody(source);
          }

          @Override
          public HotelPeriodImageBody[] newArray(int size) {return new HotelPeriodImageBody[size];}
     };
}
