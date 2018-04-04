package com.hunliji.hljcommonlibrary.models.realm;


import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Poster;

/**
 * Created by Suncloud on 2016/9/2.
 */
public class NotificationExtra {

    private ExFeed feed;
    private ExSetMeal set_meal;
    private ExCustomSetMeal custom_set_meal;
    private ExMerchant merchant;
    private ExShop shop;
    private ExCar car;
    private ExOrder order;
    private ExExpand expand;
    private ExSubPage subPage;
    private ExAnswer answer;
    private Poster poster;

    public ExFeed getFeed() {
        return feed;
    }

    public ExSetMeal getSetMeal() {
        return set_meal;
    }

    public ExCustomSetMeal getCustomSetMeal() {
        return custom_set_meal;
    }

    public ExMerchant getMerchant() {
        return merchant;
    }

    public ExShop getShop() {
        return shop;
    }

    public ExCar getCar() {
        return car;
    }

    public ExOrder getOrder() {
        return order;
    }

    public ExExpand getExpand() {
        return expand;
    }

    public ExAnswer getAnswer() {
        return answer;
    }

    public ExSubPage getSubPage() {
        return subPage;
    }

    public Poster getPoster() {
        return poster;
    }

    /**
     * 动态
     */
    public class ExFeed {
        String image;

        public String getImage() {
            return image;
        }
    }

    /**
     * 套餐
     */
    public class ExSetMeal {

        String cover_path;
        String title;
//        transient double actual_price;

        public String getCoverPath() {
            return cover_path;
        }

        public String getTitle() {
            return title;
        }

//        public double getActualPrice() {
//            return actual_price;
//        }
    }

    /**
     * 自选套餐
     */
    public class ExCustomSetMeal {

        String cover_path;
        String title;
//        transient double actual_price;
        String merchant;

        public String getCoverPath() {
            return cover_path;
        }

        public String getTitle() {
            return title;
        }

//        public double getActualPrice() {
//            return actual_price;
//        }

        public String getMerchantName() {
            return merchant;
        }
    }

    /**
     * 商家
     */
    public class ExMerchant {

        String name;
//        transient String logo_path;
//        transient long id;

        public String getName() {
            return name;
        }
//
//        public String getLogoPath() {
//            return logo_path;
//        }
//
//        public double getId() {
//            return id;
//        }
    }

    /**
     * 商品
     */
    public class ExShop {

        String title;
        String cover_path;
        String merchant;

        public String getTitle() {
            return title;
        }

        public String getCoverPath() {
            return cover_path;
        }

        public String getMerchantName() {
            return merchant;
        }
    }


    /**
     * 婚车
     */
    public class ExCar {

        String title;
        String cover_path;
//        transient String merchant;

        public String getTitle() {
            return title;
        }

        public String getCoverPath() {
            return cover_path;
        }
//
//        public String getMerchantName() {
//            return merchant;
//        }
    }

    /**
     * 订单
     */
    public class ExOrder {

        String parent_order_no;

        public String getOrderNo() {
            return parent_order_no;
        }
    }

    /**
     * 帖子，问答
     */
    public class ExExpand {

        String title;
        String cover_path;
//        transient String message;
        int serial_no;

        public String getTitle() {
            return title;
        }

        public String getCoverPath() {
            return cover_path;
        }
//
//        public String getMessage() {
//            return message;
//        }

        public int getSerialNo() {
            return serial_no;
        }
    }

    /**
     * 专题
     */
    public class ExSubPage {

        String img;

        public String getImagePath() {
            return img;
        }
    }
    /**
     * 专题
     */
    public class ExAnswer {

        @SerializedName(value = "user_id",alternate = "userId")
        long userId;

        public long getUserId() {
            return userId;
        }
    }
}
