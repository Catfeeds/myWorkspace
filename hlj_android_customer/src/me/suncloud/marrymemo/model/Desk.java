package me.suncloud.marrymemo.model;


/**
 * author:SUNCLOUD
 * 2015/4/3 10:48
 */
public class Desk extends Label {
    private static final long serialVersionUID = -2818839030466981950L;
    private long id;
    private String describe;
    private long desk_start;
    private long desk_end;

    public Desk(String describe, long desk_start, long desk_end) {
        this.describe = this.name = describe;
        this.desk_start = desk_start;
        this.desk_end = desk_end;
    }

    public Desk(String describe, long desk_start, long desk_end,long id) {
        this.describe = this.name = describe;
        this.desk_start = desk_start;
        this.desk_end = desk_end;
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getDesk_start() {
        return desk_start;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public long getDesk_end() {
        return desk_end;
    }

}
