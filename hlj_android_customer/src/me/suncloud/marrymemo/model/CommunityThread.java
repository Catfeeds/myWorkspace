package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by mo_yu on 2016/5/9.频道下的话题
 */
public class CommunityThread implements Identifiable {

    private Long id;
    private String title;
    private Date lastPostTime;
    private Date createdAt;
    private int postCount;
    private int praisedSum;
    private boolean isPraised;
    private boolean isCollected;
    private CommunityPost post;
    private boolean havePics;
    private boolean isHidden;
    private Author author;
    private User user;
    private CommunityChannel channel;
    private CommunityGroup group;
    private ShareInfo shareInfo;
    private boolean allowModify;
    private boolean isRefined;
    private boolean isTop;
    private ArrayList<User> praisedUsers;
    private Date updatedAt;
    //富文本
    private ThreadPages threadPages;
    private ArrayList<Integer> paginationNoList; // 用于给翻页操作存储的页面对应的楼层no数据列表
    private ThreadTitleTag titleTag;
    private String cityName;

    public CommunityThread(JSONObject json) {
        if (json != null) {
            id = json.optLong("id", 0);
            title = JSONUtil.getString(json, "title");
            lastPostTime = JSONUtil.getDateFromFormatLong(json, "last_post_time", true);
            createdAt = JSONUtil.getDateFromFormatLong(json, "created_at", true);
            updatedAt = JSONUtil.getDateFromFormatLong(json, "updated_at", true);
            postCount = json.optInt("post_count", 0);
            praisedSum = json.optInt("praised_sum", 0);
            if (praisedSum == 0) {
                praisedSum = json.optInt("praised_count", 0);
            }
            isTop = json.optBoolean("is_top", false);
            if (!isTop) {
                isTop = json.optInt("is_top") > 0;
            }
            isRefined = json.optBoolean("is_refined", false);
            if (!isRefined) {
                isRefined = json.optInt("is_refined") > 0;
            }

            isPraised = json.optBoolean("is_praised", false);
            if (!isPraised) {
                isPraised = json.optInt("is_praised") > 0;
                /**
                 * 兼容首页feeds流
                 */
                if (!isPraised()) {
                    isPraised = json.optBoolean("is_like", false);
                    if (!isPraised) {
                        isPraised = json.optInt("is_like") > 0;
                    }
                }
            }
            isCollected = json.optBoolean("is_collected", false);
            if (!isCollected) {
                isCollected = json.optInt("is_collected") > 0;
            }
            allowModify = json.optInt("allow_modify") > 0;

            havePics = json.optBoolean("have_pics", false);
            if (!havePics) {
                havePics = json.optInt("have_pics") > 0;
            }
            if (!json.isNull("community_channel")) {
                channel = new CommunityChannel(json.optJSONObject("community_channel"));
            }
            if (!json.isNull("community_group")) {
                group = new CommunityGroup(json.optJSONObject("community_group"));
            }
            if (!json.isNull("post")) {
                post = new CommunityPost(json.optJSONObject("post"));
            }
            if (!json.isNull("author")) {
                author = new Author(json.optJSONObject("author"));
            }
            if (!json.isNull("author")) {
                user = new User(json.optJSONObject("author"));
            }
            ShareInfo share = new ShareInfo(json.optJSONObject("share"));
            if (!JSONUtil.isEmpty(share.getTitle()) && !JSONUtil.isEmpty(share.getUrl())) {
                shareInfo = share;
            }
            isHidden = json.optBoolean("hidden", false);
            if (!isHidden) {
                isHidden = json.optInt("hidden") > 0;
            }
            JSONArray praisedUsersArray = json.optJSONArray("praise_users");
            praisedUsers = new ArrayList<>();
            if (praisedUsersArray != null && praisedUsersArray.length() > 0) {
                // 最多5个就够了
                for (int i = 0; i < praisedUsersArray.length() && i < 5; i++) {
                    User user = new User(praisedUsersArray.optJSONObject(i));
                    praisedUsers.add(user);
                }
            }

            if (!json.isNull("pages")) {
                threadPages = new ThreadPages(json.optJSONObject("pages"));
            }

            paginationNoList = new ArrayList<>();
            JSONArray array = json.optJSONArray("start_serial_nos");
            if (array != null && array.length() > 0) {
                int pagerCount = array.length();
                for (int i = 0; i < pagerCount; i++) {
                    paginationNoList.add(array.optInt(i));
                }
            }

            if (!json.isNull("tag")) {
                titleTag = new ThreadTitleTag(json.optJSONObject("tag"));
            }
            cityName = JSONUtil.getString(json, "city_name");
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getLastPostTime() {
        return lastPostTime;
    }

    public int getPostCount() {
        return postCount;
    }

    public int getPraisedSum() {
        return praisedSum;
    }

    public boolean isPraised() {
        return isPraised;
    }

    public CommunityPost getPost() {
        if (post!=null){
            return post;
        }else {
            return new CommunityPost(null);
        }

    }

    public Author getAuthor() {
        if (author!=null){
            return author;
        }else {
            return new Author(null);
        }
    }

    public User getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLastPostTime(Date last_post_time) {
        this.lastPostTime = last_post_time;
    }

    public void setPostCount(int post_count) {
        this.postCount = post_count;
    }

    public void setPraisedSum(int praisedSum) {
        this.praisedSum = praisedSum;
    }

    public void setPraised(boolean praised) {
        this.isPraised = praised;
    }

    public void setPost(CommunityPost post) {
        this.post = post;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public CommunityChannel getChannel() {
        return channel;
    }

    public void setChannel(CommunityChannel channel) {
        this.channel = channel;
    }

    public CommunityGroup getGroup() {
        return group;
    }

    public void setGroup(CommunityGroup group) {
        this.group = group;
    }

    public boolean isHavePics() {
        return havePics;
    }

    public void setHavePics(boolean have_pics) {
        this.havePics = have_pics;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setIsTop(boolean is_top) {
        this.isTop = is_top;
    }

    public boolean isRefined() {
        return isRefined;
    }

    public void setIsRefined(boolean is_refined) {
        this.isRefined = is_refined;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public boolean isAllowModify() {
        return allowModify;
    }

    public ArrayList<User> getPraisedUsers() {
        return praisedUsers;
    }

    /**
     * 压入一个user到点赞队列最前面
     *
     * @param user
     */
    public void pushedPraisedUser(User user) {
        if (praisedUsers.isEmpty()) {
            praisedUsers.add(user);
        } else {
            praisedUsers.add(praisedUsers.get(praisedUsers.size() - 1));
            for (int i = praisedUsers.size() - 1; i > 0; i--) {
                praisedUsers.set(i, praisedUsers.get(i - 1));
            }
            praisedUsers.set(0, user);
        }
    }

    /**
     * 删除一个指定id的点赞user
     *
     * @param id
     */
    public void removePraisedUser(long id) {
        int index = -1;
        for (int i = 0; i < praisedUsers.size(); i++) {
            User user = praisedUsers.get(i);
            if (user.getId()
                    .equals(id)) {
                index = i;
            }
        }
        if (index != -1) {
            praisedUsers.remove(index);
        }
    }

    public ThreadPages getThreadPages() {
        return threadPages;
    }

    public void setThreadPages(ThreadPages threadPages) {
        this.threadPages = threadPages;
    }

    public ArrayList<Integer> getPaginationNoList() {
        return paginationNoList;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public ThreadTitleTag getTitleTag() {
        return titleTag;
    }
    public String getCityName() {
        return cityName;
    }
}
