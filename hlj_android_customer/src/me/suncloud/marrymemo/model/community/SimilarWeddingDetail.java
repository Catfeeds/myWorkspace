package me.suncloud.marrymemo.model.community;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Author;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by chen_bin on 2018/3/15 0015.
 */
public class SimilarWeddingDetail {
    @SerializedName(value = "is_sign_in")
    private boolean isSignIn;
    @SerializedName(value = "progress_rate")
    private float progressRate;
    @SerializedName(value = "total_count")
    private int totalCount;
    @SerializedName(value = "users")
    private List<Author> users;

    public boolean isSignIn() {
        return isSignIn;
    }

    public float getProgressRate() {
        return progressRate;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public List<Author> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }
}
