package me.suncloud.marrymemo.model.tools;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luohanlin on 2018/3/22.
 * 日历中品牌配置信息
 */

public class CalendarBrandConf {
    @SerializedName("calendar")
    BrandInfoInCalendar brandInfoInCalendar;
    @SerializedName("card")
    BrandInfoBelowCalendar brandInfoBelowCalendar;

    public BrandInfoInCalendar getBrandInfoInCalendar() {
        return brandInfoInCalendar;
    }

    public BrandInfoBelowCalendar getBrandInfoBelowCalendar() {
        return brandInfoBelowCalendar;
    }

    public class BrandInfoInCalendar {
        @SerializedName("back_image")
        BrandImgConf backImage;

        public BrandImgConf getBackImage() {
            return backImage;
        }
    }

    public class BrandInfoBelowCalendar {
        @SerializedName("back_image")
        BrandImgConf backImage;
        @SerializedName("font_back_image")
        BrandImgConf fontBackImage;

        public BrandImgConf getBackImage() {
            return backImage;
        }

        public BrandImgConf getFontBackImage() {
            return fontBackImage;
        }
    }

    public class BrandImgConf {
        boolean show;
        @SerializedName("url")
        String imgPath;
        @SerializedName("num_font_color")
        String numFontColor;
        @SerializedName("lunar_font_color")
        String lunarFontColor;

        public boolean isShow() {
            return show;
        }

        public String getImgPath() {
            return imgPath;
        }

        public String getNumFontColor() {
            return numFontColor;
        }

        public String getLunarFontColor() {
            return lunarFontColor;
        }
    }

}
