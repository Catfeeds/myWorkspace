/**
 *
 */
package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.suncloud.marrymemo.util.JSONUtil;

public class Post implements Identifiable {

    /**
     *
     */
    private static final long serialVersionUID = 8426519755301200564L;

    //新版社区新增
    private Long community_thread_id;
    private Long user_id;
    private String message;
    private boolean hidden;
    private String quote;
    private String serial_no;
    private int praised_count;
    private Date created_at;
    private Date updated_at;
    private int by_faker;
    private int status;

    private long id;
    private long groupId;
    private long threadId;
    private String content;
    private String groupTitle;
    private String threadTitle;
    private User user;
    private Date time;
    private User quoteUser;
    private String quoteContent;
    private int quotePosition;
    private int position;
    private int praiseCount;
    private boolean isPraised;
    private boolean isHidden;
    private boolean quoteHidden;
    private boolean groupHidden;
    private boolean threadHidden;
    private Cite cite;
    private ArrayList<Photo> photos;
    private boolean isLandlord;

    public Post(JSONObject object) {
        if (object != null) {
            this.id = object.optLong("id", 0);
            this.user = new User(object.optJSONObject("author"));
            this.praiseCount = object.optInt("praised_count", 0);
            this.position = object.optInt("serial_no", 0);
            this.content = JSONUtil.getString(object, "message");
            this.time = JSONUtil.getDate(object, "created_at");
            this.isPraised = object.optBoolean("is_praised", false);
            this.isHidden = object.optBoolean("hidden", false);
            if (!object.isNull("quote")) {
                JSONObject quoteObject = object.optJSONObject("quote");
                if (quoteObject != null) {
                    this.quoteUser = new User(
                            quoteObject.optJSONObject("author"));
                    this.quotePosition = quoteObject.optInt("serial_no", 0);
                    this.quoteContent = JSONUtil.getString(quoteObject,
                            "message");
                    this.quoteHidden = quoteObject.optBoolean("hidden", false);
                }
            }
            if (!object.isNull("pics")){
                JSONArray array = object.optJSONArray("pics");
                if (array != null) {
                    int size = array.length();
                    if (size > 0) {
                        photos = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            Photo photo = new Photo(array.optJSONObject(i));
                            if (!JSONUtil.isEmpty(photo.getPath())) {
                                photos.add(photo);
                            }
                        }
                    }
                }
            } else if (!object.isNull("media_items")) {
                JSONArray array = object.optJSONArray("media_items");
                if (array != null) {
                    int size = array.length();
                    if (size > 0) {
                        photos = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            Photo photo = new Photo(array.optJSONObject(i));
                            if (!JSONUtil.isEmpty(photo.getPath())) {
                                photos.add(photo);
                            }
                        }
                    }
                }
            }
            if (!object.isNull("group")) {
                JSONObject obj = object.optJSONObject("group");
                if (obj != null) {
                    this.groupId = obj.optLong("id", 0);
                    this.groupTitle = JSONUtil.getString(obj, "title");
                    this.groupHidden = obj.optBoolean("hidden", false);
                }
            }
            if (!object.isNull("thread")) {
                JSONObject obj = object.optJSONObject("thread");
                if (obj != null) {
                    this.threadId = obj.optLong("id", 0);
                    this.threadTitle = JSONUtil.getString(obj, "title");
                    this.threadHidden = obj.optBoolean("hidden", false);
                }
            }
            if (!object.isNull("cite")) {
                JSONObject citeJSon = object.optJSONObject("cite");
                if (citeJSon != null) {
                    cite = new Cite(citeJSon);
                }
            }
            isLandlord = object.optInt("is_Landlord", 0) > 0;

            //新版社区新增
            this.user_id = object.optLong("user_id", 0);
            this.community_thread_id = object.optLong("community_thread_id", 0);
            this.message = JSONUtil.getString(object, "message");
            this.quote = JSONUtil.getString(object, "quote");
            this.serial_no = JSONUtil.getString(object, "serial_no");
            this.created_at = JSONUtil.getDate(object, "created_at");
            this.updated_at = JSONUtil.getDate(object, "updated_at");
            this.hidden = object.optBoolean("hidden", false);
            this.by_faker = object.optInt("by_faker", 0);
            this.status = object.optInt("status", 0);
            this.praised_count = object.optInt("praised_count", 0);
        }

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ArrayList<Photo> getPhotos() {
        return photos == null ? new ArrayList<Photo>() : photos;
    }

    public int getPosition() {
        return position;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(int praiseCount) {
        this.praiseCount = praiseCount;
    }

    public String getQuoteContent() {
        return quoteContent;
    }

    public int getQuotePosition() {
        return quotePosition;
    }

    public User getQuoteUser() {
        return quoteUser;
    }

    public boolean isPraised() {
        return isPraised;
    }

    public void setPraised(boolean isPraised) {
        this.isPraised = isPraised;
    }

    public long getGroupId() {
        return groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public long getThreadId() {
        return threadId;
    }

    public String getThreadTitle() {
        return threadTitle;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public boolean isGroupHidden() {
        return groupHidden;
    }

    public boolean isThreadHidden() {
        return threadHidden;
    }

    public boolean isQuoteHidden() {
        return quoteHidden;
    }

    public Cite getCite() {
        return cite;
    }

    public boolean isLandlord() {
        return isLandlord;
    }

    public Long getCommunity_thread_id() {
        return community_thread_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public String getMessage() {
        return message;
    }

    public String getQuote() {
        return quote;
    }

    public String getSerial_no() {
        return serial_no;
    }

    public int getPraised_count() {
        return praised_count;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public int getBy_faker() {
        return by_faker;
    }

    public int getStatus() {
        return status;
    }


    public void setPraised_count(int praised_count) {
        this.praised_count = praised_count;
    }
}
