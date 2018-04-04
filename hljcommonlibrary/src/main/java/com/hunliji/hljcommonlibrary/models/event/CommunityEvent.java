package com.hunliji.hljcommonlibrary.models.event;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by mo_yu on 2018/3/16.社区帖子活动
 */

public class CommunityEvent {
    public static final int EVENT_START = 1;
    public static final int EVENT_END = 2;


    long id;
    String title;
    int status;//1已生效 2已失效
    @SerializedName("hot_thread")
    List<CommunityThread> hotThreads;
    @SerializedName("start_time")
    DateTime startTime;
    @SerializedName("end_time")
    DateTime endTime;
    String image;
    String content;//富文本编辑内容
    @SerializedName("create_at")
    DateTime createAt;
    boolean deleted;
    boolean hidden;
    @SerializedName("last_users")
    List<Author> lastUsers;//最近浏览的用户列表
    @SerializedName("post_count")
    int postCount;//帖子数
    ShareInfo share;
    @SerializedName("updated_at")
    DateTime updatedAt;
    @SerializedName("watch_count")
    int watchCount;//浏览数
    @SerializedName("last_user")
    Author lastUser;//最后浏览的用户

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getStatus() {
        return status;
    }

    public List<CommunityThread> getHotThreads() {
        return hotThreads;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public String getImage() {
        return image;
    }

    public String getContent() {
        return content;
    }

    public DateTime getCreateAt() {
        return createAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isHidden() {
        return hidden;
    }

    public List<Author> getLastUserList() {
        return lastUsers;
    }

    public int getPostCount() {
        return postCount;
    }

    public ShareInfo getShare() {
        return share;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public Author getLastUser() {
        return lastUser;
    }
}
