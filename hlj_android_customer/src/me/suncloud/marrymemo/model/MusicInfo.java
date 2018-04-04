package me.suncloud.marrymemo.model;

import java.math.BigDecimal;

public class MusicInfo implements Identifiable {
    /**
     *
     */
    private static final long serialVersionUID = 4969697248837603170L;

    private Long id;
    private String name;
    private long duration;
    private String url;
    private float size;

    public MusicInfo(Long id, String name, String path, long duration, int size) {
        super();
        this.id = id;
        this.name = name;
        this.url = path;
        this.duration = duration;
        float mSize = (float) size / (1024 * 1024);
        BigDecimal b = new BigDecimal(mSize);
        this.size = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    @Override
    public Long getId() {
        return id;
    }

    public long getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public float getSize() {
        return size;
    }
}