package me.suncloud.marrymemo.model.community;

public class LiveEntranceData {
    public static final int STATUS_LIVING = 1; // 进行中
    public static final int STATUS_PREPARE = 2; // 预热中
    private String desc;
    private int status;

    public String getDesc() {
        return desc;
    }

    public int getStatus() {
        return status;
    }
}
