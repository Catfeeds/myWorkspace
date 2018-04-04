package me.suncloud.marrymemo.model;

import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.models.realm.ChatDraft;

import java.util.Date;

/**
 * Created by Suncloud on 2015/11/30.
 */
public abstract class LastMessage implements Identifiable {

    protected String content;
    protected Date time;
    protected long sessionId;
    protected String sessionNick;
    protected String sessionAvatar;
    protected int unreadSessionCount;
    private String draftContent;
    private Date draftTime;

    @Override
    public Long getId() {
        return 0L;
    }

    public Date getTime() {
        return time;
    }

    public String getContent() {
        if(!TextUtils.isEmpty(draftContent)){
            return draftContent;
        }
        return content;
    }

    public long getSessionId() {
        return sessionId;
    }

    public String getSessionAvatar() {
        return sessionAvatar;
    }

    public String getSessionNick() {
        return sessionNick;
    }

    public int getUnreadSessionCount() {
        return unreadSessionCount;
    }

    public void setUnreadSessionCount(int unreadSessionCount) {
        this.unreadSessionCount = unreadSessionCount;
    }

    public void setDraft(ChatDraft draft) {
        this.draftContent = draft.getContent();
        this.draftTime = draft.getDate();
    }

    public void clearDraft(){
        this.draftContent=null;
        this.draftTime=null;
    }

    public Date getSortTime() {
        if(time==null){
            return draftTime;
        }
        if(draftTime==null){
            return time;
        }
        return time.after(draftTime)?time:draftTime;
    }

    public void setDraftContent(String draftContent) {
        this.draftContent = draftContent;
    }

    public String getDraftContent() {
        return draftContent;
    }

    public Date getDraftTime() {
        return draftTime;
    }

    public int getSortLevel(){
//        if(unreadSessionCount>0){
//            return 2;
//        }
        return 1;
    }



    /**
     * @param anotherMessage
     * @return 1 this>anotherMessage;
     */
    public int compareTo(LastMessage anotherMessage){
        if (this.getSortLevel() == anotherMessage.getSortLevel()) {
            if (this.getSortTime() == null) {
                return 1;
            } else if (anotherMessage.getSortTime() == null) {
                return -1;
            } else {
                return anotherMessage.getSortTime()
                        .compareTo(this.getSortTime());
            }
        } else {
            return anotherMessage.getSortLevel() - this.getSortLevel();
        }
    }
}
