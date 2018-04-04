package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2015/9/3.
 */
public class TwitterReply implements Identifiable {

    private long id;
    private long replyUserId;
    private User user;
    private Date time;
    private String content;
    private String replyUserNick;
    private User replyUser;

    public TwitterReply(){
    }
    public TwitterReply(JSONObject jsonObject){
        if(jsonObject!=null){
            id=jsonObject.optLong("id");
            replyUserId=jsonObject.optLong("reply_user_id");
            user=new User(jsonObject.optJSONObject("user"));
            time= JSONUtil.getDateFromFormatLong(jsonObject,"created_at",true);
            content=JSONUtil.getString(jsonObject, "content");
            replyUserNick=JSONUtil.getString(jsonObject,"reply_user_nick");
            if (!jsonObject.isNull( "reply_user" )){
                replyUser = new User( jsonObject.optJSONObject( "reply_user" ) );
            }

        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public Date getTime() {
        return time;
    }

    public String getReplyUserNick() {
        return replyUserNick;
    }


    public void setMerchant(NewMerchant merchant){
        if(merchant.getUserId()==replyUserId){
            replyUserNick=merchant.getName();
        }
        if(merchant.getUserId()==getUser().getId()){
            user.setAvatar(merchant.getLogoPath());
            user.setNick(merchant.getName());
        }
    }

    public User getReplyUser() {
        return replyUser;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setReplyUserId(long replyUserId) {
        this.replyUserId = replyUserId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setReplyUserNick(String replyUserNick) {
        this.replyUserNick = replyUserNick;
    }

    public void setReplyUser(User replyUser) {
        this.replyUser = replyUser;
    }
}
