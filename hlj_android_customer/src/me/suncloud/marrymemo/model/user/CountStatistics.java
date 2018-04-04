package me.suncloud.marrymemo.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chen_bin on 2017/11/9 0009.
 */
public class CountStatistics {
    @SerializedName(value = "answer_count")
    private int answerCount;
    @SerializedName(value = "note_count")
    private int noteCount;
    @SerializedName(value = "post_count")
    private int postCount;
    @SerializedName(value = "question_count")
    private int questionCount;
    @SerializedName(value = "thread_count")
    private int threadCount;
    @SerializedName(value = "example_count")
    private int exampleCount;
    @SerializedName(value = "note_media_count")
    private int noteMediaCount;
    @SerializedName(value = "package_count")
    private int packageCount;
    @SerializedName(value = "product_count")
    private int productCount;
    @SerializedName(value = "sub_page_count")
    private int subPageCount;
    @SerializedName(value = "activity_count")
    private int activityCount;
    @SerializedName(value = "appointment_count")
    private int appointmentCount;
    @SerializedName(value = "car_order_count")
    private int carOrderCount;
    @SerializedName(value = "sev_order_count")
    private int sevOrderCount;
    @SerializedName(value = "shop_order_count")
    private int shopOrderCount;
    @SerializedName(value = "hotel_order_count")
    private int hotelOrderCount;

    public static final String TYPE_COMMUNITY = "community";
    public static final String TYPE_COLLECTION = "collection";
    public static final String TYPE_ORDER = "order";

    public int getAnswerCount() {
        return answerCount;
    }

    public int getNoteCount() {
        return noteCount;
    }

    public int getPostCount() {
        return postCount;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public int getExampleCount() {
        return exampleCount;
    }

    public int getNoteMediaCount() {
        return noteMediaCount;
    }

    public int getPackageCount() {
        return packageCount;
    }

    public int getProductCount() {
        return productCount;
    }

    public int getSubPageCount() {
        return subPageCount;
    }

    public int getActivityCount() {
        return activityCount;
    }

    public int getAppointmentCount() {
        return appointmentCount;
    }

    public int getCarOrderCount() {
        return carOrderCount;
    }

    public int getSevOrderCount() {
        return sevOrderCount;
    }

    public int getShopOrderCount() {
        return shopOrderCount;
    }

    public int getHotelOrderCount() {
        return hotelOrderCount;
    }
}
