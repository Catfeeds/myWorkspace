package me.suncloud.marrymemo.model.weddingdress;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;

import java.util.List;

/**
 * Created by hua_rong on 2017/5/16.
 * 晒婚纱照评价话题
 */

public class WeddingCommentBody {

    @SerializedName(value = "thread_id")
    Long threadId;
    String message;
    @SerializedName(value = "quote_id")
    Long quoteId;
    @SerializedName(value = "pics")
    List<Photo> photos;

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Long quoteId) {
        this.quoteId = quoteId;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}
