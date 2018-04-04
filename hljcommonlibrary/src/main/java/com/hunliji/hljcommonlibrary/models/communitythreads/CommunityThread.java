package com.hunliji.hljcommonlibrary.models.communitythreads;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by werther on 16/8/25.
 * 社区话题
 */
public class CommunityThread implements Parcelable {
    long id;
    String title;
    @SerializedName(value = "last_post_time")
    DateTime lastPostTime;
    @SerializedName(value = "created_at")
    DateTime createdAt;
    @SerializedName(value = "post_count")
    int postCount;
    @SerializedName(value = "praised_sum")
    int praisedSum;
    @SerializedName(value = "is_praised", alternate = "is_like")
    boolean isPraised;
    @SerializedName(value = "is_collected")
    boolean isCollected;
    @SerializedName(value = "have_pics")
    boolean havePics;
    @SerializedName(value = "hidden")
    boolean isHidden;
    CommunityAuthor author;
    @SerializedName(value = "community_channel", alternate = "channel")
    CommunityChannel channel;
    @SerializedName(value = "community_group")
    CommunityGroup group;
    @SerializedName(value = "share")
    ShareInfo shareInfo;
    @SerializedName(value = "allow_modify")
    boolean allowModify;//是否允许修改
    @SerializedName(value = "is_refined")
    boolean isRefined;//精标志位
    @SerializedName(value = "is_top")
    boolean isTop;//置顶标志位
    @SerializedName(value = "is_rewrite_style")
    boolean isRewriteStyle;//精选样式（有奖话题hot标志位）
    @SerializedName(value = "praise_users")
    ArrayList<CommunityAuthor> praisedUsers;
    @SerializedName(value = "pages")
    CommunityThreadPages pages;    //富文本
    @SerializedName(value = "start_serial_nos")
    ArrayList<Integer> paginationNoList;// 用于给翻页操作存储的页面对应的楼层no数据列表
    CommunityPost post;
    @SerializedName(value = "click_count")
    int clickCount;//浏览数
    @SerializedName(value = "updated_at")
    DateTime updatedAt;//话题更新时间

    //晒婚纱照字段,不为空表示该话题为晒婚纱照话题
    @SerializedName("community_photo_thread")
    private WeddingPhotoContent weddingPhotoContent;
    @SerializedName("community_thread_item")
    private ArrayList<WeddingPhotoItem> weddingPhotoItems;
    @SerializedName(value = "pics_count")
    private int picsCount;

    //显示字段，与数据解析无关
    private boolean isShowThree;//是否能显示三图样式
    private List<Photo> showPhotos;//第一张为封面图
    private String showTitle;//标题
    private String showSubtitle;//副标题
    //热门话题添加回复列表字段
    private List<CommunityPost> posts;

    public List<Photo> getShowPhotos() {
        if (CommonUtil.isCollectionEmpty(showPhotos)) {
            showPhotos = new ArrayList<>();
            if (weddingPhotoContent != null && weddingPhotoContent.getPhoto() != null) {
                showPhotos.add(weddingPhotoContent.getPhoto());
            } else if (pages != null) {
                Photo photo = new Photo();
                photo.setImagePath(pages.getImgPath());
                showPhotos.add(photo);
            } else if (!CommonUtil.isCollectionEmpty(post.getPhotos())) {
                showPhotos.addAll(post.getPhotos());
            }
        }
        return showPhotos;
    }

    public String getShowTitle() {
        if (showTitle == null) {
            if (pages != null) {
                showTitle = pages.getTitle();
            } else {
                showTitle = title;
            }
        }
        return showTitle;
    }

    public String getShowSubtitle() {
        if (showSubtitle == null) {
            if (weddingPhotoContent != null) {
                showSubtitle = weddingPhotoContent.getPreface();
            } else if (pages != null) {
                showSubtitle = pages.getSubTitle();
            } else if (post != null) {
                showSubtitle = post.getMessage();
            }
        }
        return showSubtitle;
    }

    public int getPicsCount() {
        return picsCount;
    }

    public void setPicsCount(int picsCount) {
        this.picsCount = picsCount;
    }

    public WeddingPhotoContent getWeddingPhotoContent() {
        return weddingPhotoContent;
    }

    public ArrayList<WeddingPhotoItem> getWeddingPhotoItems() {
        return weddingPhotoItems;
    }

    public void setWeddingPhotoItems(ArrayList<WeddingPhotoItem> weddingPhotoItems) {
        this.weddingPhotoItems = weddingPhotoItems;
    }

    public boolean isShowThree() {
        return isShowThree;
    }

    public void setShowThree(boolean showThree) {
        isShowThree = showThree;
    }

    public CommunityThread() {}

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public DateTime getLastPostTime() {
        return lastPostTime;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public int getPraisedSum() {
        return praisedSum;
    }

    public boolean isPraised() {
        return isPraised;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public boolean isHavePics() {
        return havePics;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public CommunityAuthor getAuthor() {
        if (author == null) {
            author = new CommunityAuthor();
        }
        return author;
    }

    public CommunityChannel getChannel() {
        if (channel == null) {
            channel = new CommunityChannel();
        }
        return channel;
    }

    public CommunityGroup getGroup() {
        return group;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public boolean isAllowModify() {
        return allowModify;
    }

    public boolean isRefined() {
        return isRefined;
    }

    public boolean isTop() {
        return isTop;
    }

    public boolean isRewriteStyle() {
        return isRewriteStyle;
    }

    public ArrayList<CommunityAuthor> getPraisedUsers() {
        if (praisedUsers == null) {
            return new ArrayList<>();
        }
        return praisedUsers;
    }

    public CommunityThreadPages getPages() {
        return pages;
    }

    public ArrayList<Integer> getPaginationNoList() {
        return paginationNoList;
    }

    public CommunityPost getPost() {
        if (post == null) {
            post = new CommunityPost();
        }
        return post;
    }

    public void setPraised(boolean praised) {
        this.isPraised = praised;
    }

    public void setPraisedSum(int sum) {
        this.praisedSum = sum;
    }

    public int getClickCount() {
        return clickCount;
    }

    /**
     * 获取混晒照总数目
     *
     * @return
     */
    public int getWeddingPhotoCount() {
        int photoCount = 0;
        if (weddingPhotoItems != null && !weddingPhotoItems.isEmpty()) {
            for (WeddingPhotoItem item : weddingPhotoItems) {
                photoCount += item.getPhotos()
                        .size();
            }
        }

        return photoCount;
    }

    /**
     * 压入一个user到点赞队列最前面
     *
     * @param user
     */
    public void pushedPraisedUser(CommunityAuthor user) {
        if (praisedUsers == null) {
            praisedUsers = new ArrayList<>();
        }
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
            CommunityAuthor user = praisedUsers.get(i);
            if (user.getId() == id) {
                index = i;
            }
        }
        if (index != -1) {
            praisedUsers.remove(index);
        }
    }

    public List<CommunityPost> getPosts() {
        return posts;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeSerializable(this.lastPostTime);
        dest.writeSerializable(this.createdAt);
        dest.writeInt(this.postCount);
        dest.writeInt(this.praisedSum);
        dest.writeByte(this.isPraised ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCollected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.havePics ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isHidden ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.author, flags);
        dest.writeParcelable(this.channel, flags);
        dest.writeParcelable(this.group, flags);
        dest.writeParcelable(this.shareInfo, flags);
        dest.writeByte(this.allowModify ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isRefined ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isTop ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isRewriteStyle ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.praisedUsers);
        dest.writeParcelable(this.pages, flags);
        dest.writeList(this.paginationNoList);
        dest.writeParcelable(this.post, flags);
        dest.writeInt(this.clickCount);
        dest.writeSerializable(this.updatedAt);
        dest.writeByte(this.isShowThree ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.weddingPhotoContent, flags);
        dest.writeTypedList(this.weddingPhotoItems);
        dest.writeInt(this.picsCount);
        dest.writeTypedList(this.posts);
    }

    protected CommunityThread(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.lastPostTime = (DateTime) in.readSerializable();
        this.createdAt = (DateTime) in.readSerializable();
        this.postCount = in.readInt();
        this.praisedSum = in.readInt();
        this.isPraised = in.readByte() != 0;
        this.isCollected = in.readByte() != 0;
        this.havePics = in.readByte() != 0;
        this.isHidden = in.readByte() != 0;
        this.author = in.readParcelable(CommunityAuthor.class.getClassLoader());
        this.channel = in.readParcelable(CommunityChannel.class.getClassLoader());
        this.group = in.readParcelable(CommunityGroup.class.getClassLoader());
        this.shareInfo = in.readParcelable(ShareInfo.class.getClassLoader());
        this.allowModify = in.readByte() != 0;
        this.isRefined = in.readByte() != 0;
        this.isTop = in.readByte() != 0;
        this.isRewriteStyle = in.readByte() != 0;
        this.praisedUsers = in.createTypedArrayList(CommunityAuthor.CREATOR);
        this.pages = in.readParcelable(CommunityThreadPages.class.getClassLoader());
        this.paginationNoList = new ArrayList<Integer>();
        in.readList(this.paginationNoList, Integer.class.getClassLoader());
        this.post = in.readParcelable(CommunityPost.class.getClassLoader());
        this.clickCount = in.readInt();
        this.updatedAt = (DateTime) in.readSerializable();
        this.isShowThree = in.readByte() != 0;
        this.weddingPhotoContent = in.readParcelable(WeddingPhotoContent.class.getClassLoader());
        this.weddingPhotoItems = in.createTypedArrayList(WeddingPhotoItem.CREATOR);
        this.picsCount = in.readInt();
        this.posts = in.createTypedArrayList(CommunityPost.CREATOR);
    }

    public static final Creator<CommunityThread> CREATOR = new Creator<CommunityThread>() {
        @Override
        public CommunityThread createFromParcel(Parcel source) {return new CommunityThread(source);}

        @Override
        public CommunityThread[] newArray(int size) {return new CommunityThread[size];}
    };
}
