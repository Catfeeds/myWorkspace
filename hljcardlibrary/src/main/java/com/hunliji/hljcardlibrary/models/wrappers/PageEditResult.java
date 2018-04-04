package com.hunliji.hljcardlibrary.models.wrappers;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcardlibrary.models.CardPage;

/**
 * Created by wangtao on 2017/6/27.
 */

public class PageEditResult implements Parcelable {

    @SerializedName("card_page")
    private CardPage cardPage;
    @SerializedName("h5_page")
    private JsonElement h5Page;
    private String h5PageStr;

    public CardPage getCardPage() {
        return cardPage;
    }

    public String getH5PageStr() {
        if(!TextUtils.isEmpty(h5PageStr)){
            return h5PageStr;
        }
        if(h5Page!=null){
            h5PageStr=h5Page.toString();
        }
        return h5PageStr;
    }

    public void setH5PageStr(String h5PageStr) {
        this.h5PageStr = h5PageStr;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.cardPage, flags);
        dest.writeString(this.getH5PageStr());
    }

    public PageEditResult() {}

    protected PageEditResult(Parcel in) {
        this.cardPage = in.readParcelable(CardPage.class.getClassLoader());
        this.h5PageStr = in.readString();
    }

    public static final Creator<PageEditResult> CREATOR = new Creator<PageEditResult>() {
        @Override
        public PageEditResult createFromParcel(Parcel source) {return new PageEditResult(source);}

        @Override
        public PageEditResult[] newArray(int size) {return new PageEditResult[size];}
    };
}
