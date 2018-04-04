package com.hunliji.marrybiz.model;

import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.models.realm.ChannelStick;
import com.hunliji.hljcommonlibrary.models.realm.ChatDraft;
import com.hunliji.hljcommonlibrary.models.realm.ExtendMember;

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
    protected int unreadMessageCount;
    protected int unreadTrackCount;
    private boolean isStick;
    private Date stickTime;
    private String draftContent;
    private Date draftDate;
    protected ExtendMember extend;

    @Override
    public Long getId() {
        return 0L;
    }


    public Date getTime() {
        return time;
    }

    public String getContent() {
        if (!TextUtils.isEmpty(draftContent)) {
            return draftContent;
        }
        return content;
    }

    public ExtendMember getExtend() {
        return extend;
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

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(int unreadSessionCount) {
        this.unreadMessageCount = unreadSessionCount;
    }

    public void setSessionNick(String sessionNick) {
        this.sessionNick = sessionNick;
    }

    public boolean isStick() {
        return isStick;
    }

    public void setStick(boolean isStick) {
        this.isStick = isStick;
    }

    public void setStick(ChannelStick stick) {
        this.stickTime = stick.getStickAt();
        this.isStick = true;
    }

    public void setStickTime(Date stickTime) {
        this.stickTime = stickTime;
    }

    public Date getStickTime() {
        if (time != null && time.after(stickTime)) {
            return time;
        }
        return stickTime;
    }

    public void setDraft(ChatDraft draft) {
        this.draftContent = draft.getContent();
        this.draftDate = draft.getDate();
    }

    public void clearDraft() {
        this.draftContent = null;
        this.draftDate = null;
    }

    public Date getSortTime() {
        if (isStick) {
            return stickTime;
        }
        if (time == null) {
            return draftDate;
        }
        if (draftDate == null) {
            return time;
        }
        return time.after(draftDate) ? time : draftDate;
    }

    public void setDraftContent(String draftContent) {
        this.draftContent = draftContent;
    }

    public String getDraftContent() {
        return draftContent;
    }

    public void setUnreadTrackCount(int unreadTrackCount) {
        this.unreadTrackCount = unreadTrackCount;
    }

    public int getUnreadTrackCount() {
        return unreadTrackCount;
    }

    public int getSortLevel() {
        if (isStick) {
            return 4;
        } else if (unreadMessageCount > 0) {
            return 3;
        } else if (unreadTrackCount > 0) {
            return 2;
        }
        return 1;
    }

    /**
     * @param anotherMessage
     * @return 1 this>anotherMessage;
     */
    public int compareTo(LastMessage anotherMessage) {
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
