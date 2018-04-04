package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 16/5/27.
 * 新版社区回帖model,对应原来的Post
 * 弃用原来的post
 */
public class CommunityPost implements Identifiable {

    private long id;
    private long communityThreadId;
    private long userId;
    private String message;
    private boolean hidden;
    private CommunityPost quotedPost;
    private int serialNo;
    private int praisedCount;
    private Date createdAt;
    private Author author;
    private int quoteCount;
    private CommunityThread communityThread;
    private ArrayList<Photo> photos;
    private boolean isLandlord;
    private boolean isPraised;

    //兼容老post
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
    private boolean isHidden;
    private boolean quoteHidden;
    private boolean groupHidden;
    private boolean threadHidden;
    private Cite cite;
    private String cityName;

    public CommunityPost(JSONObject object) {
        if (object != null) {
            this.id = object.optLong("id", 0);
            this.communityThreadId = object.optLong("community_thread_id", 0);
            this.userId = object.optLong("user_id", 0);
            this.message = JSONUtil.getString(object, "message");
            this.serialNo = object.optInt("serial_no", 0);
            this.createdAt = JSONUtil.getDate(object, "created_at");
            if (!object.isNull("quote")) {
                this.quotedPost = new CommunityPost(object.optJSONObject("quote"));
            }
            this.author = new Author(object.optJSONObject("author"));
            this.quoteCount = object.optInt("quote_count", 0);
            this.communityThread = new CommunityThread(object.optJSONObject("community_thread"));
            photos = new ArrayList<>();
            if (!object.isNull("pics")) {
                JSONArray array = object.optJSONArray("pics");
                if (array != null) {
                    int size = array.length();
                    if (size > 0) {
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
                        for (int i = 0; i < size; i++) {
                            Photo photo = new Photo(array.optJSONObject(i));
                            if (!JSONUtil.isEmpty(photo.getPath())) {
                                photos.add(photo);
                            }
                        }
                    }
                }
            }
            this.hidden = object.optBoolean("hidden", false);
            if (!hidden) {
                this.hidden = object.optInt("hidden", 0) > 0;
            }
            this.isLandlord = object.optBoolean("is_Landlord", false);
            if (!isLandlord) {
                this.isLandlord = object.optInt("is_Landlord", 0) > 0;
            }
            this.isPraised = object.optBoolean("is_praised", false);
            if (!isPraised) {
                this.isPraised = object.optInt("is_praised", 0) > 0;
            }
            this.praisedCount = object.optInt("praised_count", 0);

            this.user = new User(object.optJSONObject("author"));
            this.position = object.optInt("serial_no", 0);
            this.content = JSONUtil.getString(object, "message");
            this.time = JSONUtil.getDate(object, "created_at");
            this.isHidden = object.optBoolean("hidden", false);
            if (!isHidden) {
                this.isHidden = object.optInt("hidden", 0) > 0;
            }
            if (!object.isNull("quote")) {
                JSONObject quoteObject = object.optJSONObject("quote");
                if (quoteObject != null) {
                    this.quoteUser = new User(quoteObject.optJSONObject("author"));
                    this.quotePosition = quoteObject.optInt("serial_no", 0);
                    this.quoteContent = JSONUtil.getString(quoteObject, "message");
                    this.quoteHidden = quoteObject.optBoolean("hidden", false);
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
            cityName = JSONUtil.getString(object, "city_name");
        }

    }

    public CommunityPost() {
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCommunityThreadId(long communityThreadId) {
        this.communityThreadId = communityThreadId;
    }

    @Override
    public Long getId() {
        return id;
    }

    public long getCommunityThreadId() {
        return communityThreadId;
    }

    public long getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public boolean isHidden() {
        return hidden;
    }

    public CommunityPost getQuotedPost() {
        return quotedPost;
    }

    public int getSerialNo() {
        return serialNo;
    }

    public int getPraisedCount() {
        return praisedCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Author getAuthor() {
        return author;
    }

    public int getQuoteCount() {
        return quoteCount;
    }

    public void setQuoteCount(int quoteCount) {
        this.quoteCount = quoteCount;
    }

    public CommunityThread getCommunityThread() {
        return communityThread;
    }

    public ArrayList<Photo> getPhotos() {
        if(photos==null){
            photos=new ArrayList<>();
        }
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public boolean isLandlord() {
        return isLandlord;
    }

    public boolean isPraised() {
        return isPraised;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setPraised(boolean praised) {
        isPraised = praised;
    }

    public void setPraisedCount(int praisedCount) {
        this.praisedCount = praisedCount;
    }

    public long getGroupId() {
        return groupId;
    }

    public long getThreadId() {
        return threadId;
    }

    public String getContent() {
        return content;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public String getThreadTitle() {
        return threadTitle;
    }

    public User getUser() {
        return user;
    }

    public Date getTime() {
        return time;
    }

    public User getQuoteUser() {
        return quoteUser;
    }

    public String getQuoteContent() {
        return quoteContent;
    }

    public int getQuotePosition() {
        return quotePosition;
    }

    public int getPosition() {
        return position;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public boolean isQuoteHidden() {
        return quoteHidden;
    }

    public boolean isGroupHidden() {
        return groupHidden;
    }

    public boolean isThreadHidden() {
        return threadHidden;
    }

    public Cite getCite() {
        return cite;
    }

    public String getCityName() {
        return cityName;
    }

}
