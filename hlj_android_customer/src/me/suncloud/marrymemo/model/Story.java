package me.suncloud.marrymemo.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;


public class Story implements Identifiable {

    /**
     *
     */
    private static final long serialVersionUID = -7601967341566018286L;

    private long id;
    private long extraId;
    private String title;
    private Integer praiseCount;
    private Integer collectCount;
    private boolean change;
    private String imagePath;
    private String description;
    private String version;
    private boolean praised;
    private boolean collected;
    private User user;
    private String url;
    private boolean isOpen;
    private boolean isComplete;
    private int commentCount;
    private ShareInfo shareInfo;

    //新增
    private ArrayList<Photo> photos = new ArrayList<>();
    private Tag tag;

    public Story(JSONObject json) {
        this.id = json.optLong( "id", 0 );
        this.title = JSONUtil.getString( json, "title" );
        this.praiseCount = json.optInt( "praise_count", 0 );
        this.commentCount = json.optInt( "comment_count", 0 );
        this.collectCount = json.optInt( "collect_count", 0 );
        this.imagePath = JSONUtil.getString( json, "cover_path" );
        this.description = JSONUtil.getString( json, "description" );
        this.version = String.valueOf( json.optLong( "version", 0 ) );
        this.praised = json.optBoolean( "praised", false );
        if (!praised) {
            this.praised = json.optBoolean( "is_praised", false );
            if (!praised) {
                this.praised = json.optInt( "is_praised", 0 ) > 0;
            }
        }
        this.collected = json.optBoolean( "collected", false );
        this.user = json.isNull( "user" ) ? null : new User( json.optJSONObject( "user" ) );
        this.isOpen = json.optBoolean( "opened", true );

        if (collectCount == 0) {
            this.collectCount = json.optInt( "collects_count" );
        }
        if (praiseCount == 0) {
            this.praiseCount = json.optInt( "praises_count" );
        }
        if (commentCount == 0) {
            this.commentCount = json.optInt( "comments_count" );
        }
        if (!json.isNull( "share" )) {
            ShareInfo share = new ShareInfo( json.optJSONObject( "share" ) );
            if (!JSONUtil.isEmpty( share.getTitle() ) && !JSONUtil.isEmpty( share.getUrl() )) {
                shareInfo = share;
            }
        }

        if (!json.isNull( "images" )) {
            JSONArray jsonArray = json.optJSONArray( "images" );
            for (int i = 0; i < jsonArray.length(); i++) {
                photos.add( new Photo( jsonArray.optJSONObject( i ) ) );
            }
        }

        if (!json.isNull( "tag" )) {
            tag = new Tag( json.optJSONObject( "tag" ) );
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return JSONUtil.isEmpty( imagePath ) ? null : imagePath;
    }

    public void setCover(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean getPraised() {
        return praised;
    }

    public void setPraised(boolean praised) {
        this.praised = praised;
    }

    public boolean getCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public long getExtraId() {
        return extraId;
    }

    public void setExtraId(long extraId) {
        this.extraId = extraId;
    }

    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
    }

    public String getUrl() {
        return Constants.getAbsUrl( Constants.HttpPath.STORY_SHARE_URL, id );
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public ShareInfo getShareInfo(Context context) {
        if (shareInfo == null) {
            shareInfo = new ShareInfo( new JSONObject() );
            shareInfo.setTitle( context.getString( R.string.label_story_share ) );
            shareInfo.setDesc( context.getString( R.string.story_share_msg, title ) );
            shareInfo.setDesc2( context.getString( R.string.story_share_weibo_msg, title ) );
            shareInfo.setIcon( imagePath );
            shareInfo.setUrl( getUrl() );
        }
        return shareInfo;
    }

}
