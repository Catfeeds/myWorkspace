package com.hunliji.hljquestionanswer.models.wrappers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Suncloud on 2016/8/26.
 * 提交问题
 */
public class PostQuestionBody implements Parcelable {

    private Long id;
    private String title;
    private String content;
    private List<Long> marks;
    private int type;//新增：1.社区问答 2.商家问答
    @SerializedName(value = "merchant_id")
    private Long merchantId;
    @SerializedName(value = "set_meal_id")
    private Long setMealId;

    public void setType(int type) {
        this.type = type;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public void setSetMealId(Long setMealId) {
        this.setMealId = setMealId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMarks(List<Long> marks) {
        this.marks = marks;
    }

    public PostQuestionBody() {}

    protected PostQuestionBody(Parcel in) {
        title = in.readString();
        content = in.readString();
    }

    public static final Creator<PostQuestionBody> CREATOR = new Creator<PostQuestionBody>() {
        @Override
        public PostQuestionBody createFromParcel(Parcel in) {
            return new PostQuestionBody(in);
        }

        @Override
        public PostQuestionBody[] newArray(int size) {
            return new PostQuestionBody[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(content);
    }
}
