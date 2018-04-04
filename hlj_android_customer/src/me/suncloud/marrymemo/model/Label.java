package me.suncloud.marrymemo.model;

/**
 * Created by Suncloud on 2016/3/23.
 */
public class Label implements Identifiable {

    protected long id;
    protected String name;
    protected String keyWord;
    protected String desc;
    protected String order;
    protected int type;

    public Label() {}

    public Label(String name) {
        this.name = name;
    }

    public Label(String name, String keyWord) {
        this.name = name;
        this.keyWord = keyWord;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}