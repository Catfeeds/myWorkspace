package com.hunliji.marrybiz.model;

import java.util.Calendar;

/**
 * Created by LuoHanLin on 14/11/13.
 */
public class ScheduleDay{
    private String mDescription;
    private String mLunarDesc;
    private Calendar mDate;
    private boolean isClosed;
    private boolean hasEvent;

    public ScheduleDay(String mDescription, Calendar mDate) {
        this.mDescription = mDescription;
        this.mDate = (Calendar) mDate.clone();
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public boolean hasEvent() {
        return hasEvent;
    }

    public void setHasEvent(boolean hasEvent) {
        this.hasEvent = hasEvent;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public Calendar getmDate() {
        return mDate;
    }

    public void setmDate(Calendar mDate) {
        this.mDate = mDate;
    }

    public String getmLunarDesc() {
        return mLunarDesc;
    }

    public void setmLunarDesc(String mLunarDesc) {
        this.mLunarDesc = mLunarDesc;
    }
}
