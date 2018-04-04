package com.hunliji.hljcarlibrary.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarSku;

/**
 * Created by werther on 15/9/22.
 */
public class CarShoppingCartItem implements Parcelable {

    private CarCartCheck carCartCheck;
    private WeddingCarProduct carProduct;
    private WeddingCarSku carSku;
    private boolean checked;
    private int quantity;
    private boolean isValid;

    public CarShoppingCartItem(WeddingCarProduct carProduct, WeddingCarSku carSku, int quantity) {
        this.carProduct = carProduct;
        this.carSku = carSku;
        this.quantity = quantity;

    }

    public void addQuantity(int quantity){
        this.quantity+=quantity;
    }

    public void subtractQuantity(int quantity){
        this.quantity-=quantity;
    }


    public boolean quantityPlus() {
        if (carCartCheck!=null&&carCartCheck.getShowNum()>0&& carCartCheck.getShowNum()<= quantity) {
            return false;
        }
        quantity++;
        return true;
    }

    public boolean quantitySubtract() {
        if (quantity > 1) {
            quantity--;
            return true;
        }
        return false;
    }

    public CarCartCheck getCarCartCheck() {
        return carCartCheck;
    }


    public long getId() {
        return carSku!=null?carSku.getId():0;
    }

    public void setCarCartCheck(CarCartCheck carCartCheck) {
        this.carCartCheck = carCartCheck;
        this.isValid=carCartCheck.isPublished();
    }

    public WeddingCarProduct getCarProduct() {
        return carProduct;
    }

    public WeddingCarSku getCarSku() {
        return carSku;
    }

    public boolean isChecked() {
        return checked;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public CarShoppingCartItem cartClone(){
        CarShoppingCartItem carShoppingCartItem=new CarShoppingCartItem(carProduct,carSku,quantity);
        if(carCartCheck!=null) {
            carShoppingCartItem.setCarCartCheck(carCartCheck);
        }
        return carShoppingCartItem;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.carCartCheck,
                flags);
        dest.writeParcelable(this.carProduct, flags);
        dest.writeParcelable(this.carSku, flags);
        dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
        dest.writeInt(this.quantity);
        dest.writeByte(this.isValid ? (byte) 1 : (byte) 0);
    }

    protected CarShoppingCartItem(Parcel in) {
        this.carCartCheck = in.readParcelable(CarCartCheck.class.getClassLoader());
        this.carProduct = in.readParcelable(WeddingCarProduct.class.getClassLoader());
        this.carSku = in.readParcelable(WeddingCarSku.class.getClassLoader());
        this.checked = in.readByte() != 0;
        this.quantity = in.readInt();
        this.isValid = in.readByte() != 0;
    }

    public static final Parcelable.Creator<CarShoppingCartItem> CREATOR = new Parcelable
            .Creator<CarShoppingCartItem>() {
        @Override
        public CarShoppingCartItem createFromParcel(Parcel source) {
            return new CarShoppingCartItem(source);
        }

        @Override
        public CarShoppingCartItem[] newArray(int size) {return new CarShoppingCartItem[size];}
    };
}
