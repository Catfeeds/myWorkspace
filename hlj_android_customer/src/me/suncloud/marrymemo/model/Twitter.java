package me.suncloud.marrymemo.model;

import com.hunliji.hljcommonlibrary.models.Video;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2015/9/2.
 */
public class Twitter implements Identifiable {
    private long id;
    private long userId;
    private long merchantId;
    private int readCount;
    private int commentCount;
    private int praisedSum;
    private boolean is_followed;
    private boolean isPraised;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private NewMerchant merchant;
    private ArrayList<Photo> images;
    private ShareInfo shareInfo;

    //描述字段,新增
    private String describe;
    private ArrayList<Author> praisedUsers;
    private Video videoContent;

    public Twitter(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id");
            userId = jsonObject.optLong("user_id");
            merchantId = jsonObject.optLong("merchant_id");
            readCount = jsonObject.optInt("read_count");
            commentCount = jsonObject.optInt("comment_count");
            praisedSum = jsonObject.optInt("likes_count");
            isPraised = jsonObject.optBoolean("is_like", false);
            if (!isPraised) {
                isPraised = jsonObject.optInt("is_like") > 0;
            }
            is_followed = jsonObject.optBoolean("is_followed", false);
            if (!is_followed) {
                is_followed = jsonObject.optInt("is_followed") > 0;
            }
            createdAt = JSONUtil.getDateFromFormatLong(jsonObject, "created_at", true);
            updatedAt = JSONUtil.getDateFromFormatLong(jsonObject, "updated_at", true);
            if (!jsonObject.isNull("merchant")) {
                merchant = new NewMerchant(jsonObject.optJSONObject("merchant"));
            }
            if (!jsonObject.isNull("content")) {
                JSONArray array = jsonObject.optJSONArray("content");
                if (array != null && array.length() > 0) {
                    images = new ArrayList<>();
                    int size = array.length();
                    for (int i = 0; i < size; i++) {
                        Photo photo = new Photo(array.optJSONObject(i));
                        if (JSONUtil.isEmpty(content)) {
                            content = photo.getDescription();
                        }
                        images.add(photo);
                    }
                }
            }
            if (!jsonObject.isNull("share")) {
                ShareInfo share = new ShareInfo(jsonObject.optJSONObject("share"));
                if (!JSONUtil.isEmpty(share.getTitle()) && !JSONUtil.isEmpty(share.getUrl())) {
                    shareInfo = share;
                }
            }
            describe = JSONUtil.getString(jsonObject, "describe");
            /**
             * 新增点赞用户列表，最多5个就够了
             */
            JSONArray praisedUsersArray = jsonObject.optJSONArray("praise_users");
            praisedUsers = new ArrayList<>();
            if (praisedUsersArray != null && praisedUsersArray.length() > 0) {
                // 最多5个就够了
                for (int i = 0; i < praisedUsersArray.length() && i < 5; i++) {
                    Author user = new Author(praisedUsersArray.optJSONObject(i));
                    praisedUsers.add(user);
                }
            }
            if (!jsonObject.isNull("video_content")) {
                JSONObject content = jsonObject.optJSONObject("video_content");
                videoContent = GsonUtil.getGsonInstance().fromJson(content.toString(), Video.class);
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public ArrayList<Photo> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }
        return images;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public int getReadCount() {
        return readCount;
    }

    public long getMerchantId() {
        if (merchantId > 0) {
            return merchantId;
        }
        return merchant == null ? 0l : merchant.getId();
    }

    public long getUserId() {
        return userId;
    }

    public NewMerchant getMerchant() {
        return merchant;
    }

    public String getContent() {
        if (!JSONUtil.isEmpty(describe)) {
            return describe;
        } else {
            return content;
        }
    }

    public void setMerchant(NewMerchant merchant) {
        this.merchant = merchant;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isPraised() {
        return isPraised;
    }

    public void setPraised(boolean praised) {
        this.isPraised = praised;
    }

    public int getPraisedSum() {
        return praisedSum;
    }

    public void setPraisedSum(int praisedSum) {
        this.praisedSum = praisedSum;
    }

    public boolean is_followed() {
        return is_followed;
    }

    public void setIs_followed(boolean is_followed) {
        this.is_followed = is_followed;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public String getDescribe() {
        return describe;
    }

    public ArrayList<Author> getPraisedUsers() {
        return praisedUsers;
    }

    public void setPraisedUsers(ArrayList<Author> praisedUsers) {
        this.praisedUsers = praisedUsers;
    }

    public Video getVideoContent() {
        return videoContent;
    }
}
