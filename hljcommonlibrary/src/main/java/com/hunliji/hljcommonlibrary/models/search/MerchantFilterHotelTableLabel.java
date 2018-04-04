package com.hunliji.hljcommonlibrary.models.search;

/**
 * Created by werther on 16/12/5.
 */

public class MerchantFilterHotelTableLabel {
    protected long id;
    protected String name;
    protected String keyWord;
    protected String desc;
    protected String order;
    protected int type;
    int min;
    int max;

    public MerchantFilterHotelTableLabel() {
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public String getName() {
        if (id < 0 || (min == 0 && max == 0)) {
            return name;
        }
        if (min > 0) {
            if (max > 0) {
                name = String.valueOf(min) + "~" + String.valueOf(max) + "桌";
            } else {
                name = String.valueOf(min) + "桌以上";
            }
        } else {
            name = String.valueOf(max) + "桌以下";
        }

        return name;
    }

    /**
     * 临时妥协的办法
     *
     * @return
     */
    public int getMinFromName() {
        int min = 0;
        try {
            if (name.contains("~")) {
                String[] strs = name.split("~");
                String minStr = strs[0].split("桌")[0];
                min = Integer.valueOf(minStr);
            } else if (name.contains("桌以上")) {
                String minStr = name.split("桌")[0];
                min = Integer.valueOf(minStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return min;
    }

    /**
     * 临时妥协的办法
     *
     * @return
     */
    public int getMaxFromName() {
        int max = 0;
        try {
            if (name.contains("~")) {
                String[] strs = name.split("~");
                String minStr = strs[1].split("桌")[0];
                max = Integer.valueOf(minStr);
            } else if (name.contains("桌以下")) {
                String minStr = name.split("桌")[0];
                max = Integer.valueOf(minStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return max;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
