package me.suncloud.marrymemo.modulehelper;

import android.content.Context;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.TrackCase;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.TrackMerchant;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.TrackProduct;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.TrackWeddingCarProduct;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.TrackWork;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSTrack;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljcommonlibrary.view_tracker.HljViewTracker;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljpushlibrary.utils.PushUtil;
import com.hunliji.hljpushlibrary.websocket.PushSocket;

import org.joda.time.DateTime;

import java.io.IOException;

import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.ShopProduct;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.model.shoppingcard.ShoppingCartGroup;
import me.suncloud.marrymemo.model.shoppingcard.ShoppingCartItem;

/**
 * Created by werther on 16/8/8.
 * 主工程内使用子模块的一些工具方法
 */
public class ModuleUtils {
    /**
     * 将用户信息传入HljHttp模块中,所有子模块中使用的用户信息都有HljHttp模块中获取
     * 这个方法也是子模块中唯一的用户信息来源,也是主模块传入用户信息的唯一接口
     * 所以每一次用户信息改变,都要进行子模块用户信息设置
     *
     * @param context
     * @param user
     */
    public static void setUserToModules(Context context, User user) {
        CustomerUser customerUser = new CustomerUser();
        customerUser.setId(user.getId());
        customerUser.setAvatar(user.getAvatar2());
        customerUser.setNick(user.getNick());
        customerUser.setName(user.getRealName());
        customerUser.setToken(user.getToken());
        customerUser.setPhone(user.getPhone());
        customerUser.setWeddingDay(user.getWeddingDay());
        customerUser.setIsPending(user.getIsPending());
        customerUser.setSpecialty(user.getSpecialty());
        customerUser.setMember(user.getCommomMember());

        try {
            UserSession.getInstance()
                    .setUser(context, customerUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HljViewTracker.INSTANCE.setCurrentUserId(user.getId());
    }

    /**
     * 注销子模块中的用户信息
     *
     * @param context
     */
    public static void logoutModules(Context context) {
        UserSession.getInstance()
                .logout(context);
        HljViewTracker.INSTANCE.setCurrentUserId(-1);
        PushUtil.INSTANCE.onClear(context);
        PushSocket.INSTANCE.onEnd();

    }

    /**
     * 将城市信息更新到子模块中，程序初始化时调用，改变城市的时候也会调用
     *
     * @param city
     */
    public static void setCityToModules(City city) {
        HljViewTracker.INSTANCE.setCurrentCityId(city == null ? -1 : city.getId());
        PushSocket.INSTANCE.sendChangeCity(city == null ? -1 : city.getId());
    }


    public static void saveCommonCity(Context context, City city) {
        com.hunliji.hljcommonlibrary.models.City comCity = new com.hunliji.hljcommonlibrary
                .models.City();
        comCity.setCid(city.getId());
        comCity.setName(city.getName());
        LocationSession.getInstance()
                .saveCity(context, comCity);
    }

    public static WSTrack getWSTrack(Object track) {
        if (track == null) {
            return null;
        }
        WSTrack wsTrack = new WSTrack("发起咨询页");
        if (track instanceof Work) {
            if (((Work) track).getCommodityType() == 0) {
                Work work = (Work) track;
                TrackWork trackWork = new TrackWork(work.getId(),
                        work.getTitle(),
                        work.getCoverPath(),
                        work.getShowPrice(),
                        work.getMarketPrice());
                wsTrack.setAction(WSTrack.WORK);
                wsTrack.setWork(trackWork);
            } else {
                Work work = (Work) track;
                TrackCase trackCase = new TrackCase(work.getId(),
                        work.getTitle(),
                        work.getCoverPath());
                wsTrack.setAction(WSTrack.CASE);
                wsTrack.setCase(trackCase);
            }
            return wsTrack;
        } else if (track instanceof ShopProduct) {
            ShopProduct shopProduct = (ShopProduct) track;
            Photo photo = new Photo();
            photo.setImagePath(shopProduct.getPhoto());
            photo.setWidth(shopProduct.getWidth());
            photo.setHeight(shopProduct.getHeight());
            TrackProduct trackProduct = new TrackProduct(shopProduct.getId(),
                    shopProduct.getTitle(),
                    photo,
                    shopProduct.getPrice(),
                    shopProduct.getMarketPrice());
            wsTrack.setAction(WSTrack.PRODUCT);
            wsTrack.setProduct(trackProduct);
            return wsTrack;
        } else if (track instanceof com.hunliji.hljcommonlibrary.models.product.ShopProduct) {
            com.hunliji.hljcommonlibrary.models.product.ShopProduct shopProduct = (com.hunliji
                    .hljcommonlibrary.models.product.ShopProduct) track;
            TrackProduct trackProduct = new TrackProduct(shopProduct.getId(),
                    shopProduct.getTitle(),
                    shopProduct.getCoverImage(),
                    shopProduct.getShowPrice(),
                    shopProduct.getMarketPrice());
            wsTrack.setAction(WSTrack.PRODUCT);
            wsTrack.setProduct(trackProduct);
            return wsTrack;
        } else if (track instanceof Merchant) {
            Merchant merchant = (Merchant) track;
            TrackMerchant trackMerchant = new TrackMerchant(merchant.getId(),
                    merchant.getUserId(),
                    merchant.getName(),
                    merchant.getLogoPath(),
                    merchant.getShopType());
            wsTrack.setAction(WSTrack.MERCHANT);
            wsTrack.setMerchant(trackMerchant);
            return wsTrack;
        } else if (track instanceof com.hunliji.hljcommonlibrary.models.Work) {
            com.hunliji.hljcommonlibrary.models.Work work = (com.hunliji
                    .hljcommonlibrary.models.Work) track;
            if (work.getCommodityType() == 0) {
                TrackWork trackWork = new TrackWork(work.getId(),
                        work.getTitle(),
                        work.getCoverPath(),
                        work.getShowPrice(),
                        work.getMarketPrice());
                wsTrack.setAction(WSTrack.WORK);
                wsTrack.setWork(trackWork);
            } else {
                TrackCase trackCase = new TrackCase(work.getId(),
                        work.getTitle(),
                        work.getCoverPath());
                wsTrack.setAction(WSTrack.CASE);
                wsTrack.setCase(trackCase);
            }
            return wsTrack;
        }
        return null;
    }

    public static WSTrack getWSTrack(Merchant merchant, String imagePath) {
        if (merchant == null) {
            return null;
        }
        WSTrack wsTrack = new WSTrack("发起咨询页");
        TrackMerchant trackMerchant = new TrackMerchant(merchant.getId(),
                merchant.getUserId(),
                merchant.getName(),
                merchant.getLogoPath(),
                merchant.getShopType());
        wsTrack.setAction(WSTrack.SHOW_WINDOW);
        wsTrack.setImagePath(imagePath);
        wsTrack.setMerchant(trackMerchant);
        return wsTrack;
    }

    public static ShoppingCartGroup initShoppingCartGroup(
            com.hunliji.hljcommonlibrary.models.product.ShopProduct shopProduct,
            com.hunliji.hljcommonlibrary.models.product.Sku sku,
            int count) {
        ShoppingCartItem cartItem = new ShoppingCartItem();
        cartItem.setProduct(shopProduct);
        cartItem.setSku(sku);
        cartItem.setQuantity(count);
        cartItem.setCreatedAt(new DateTime());
        cartItem.setValid(true);
        ShoppingCartGroup cartGroup = new ShoppingCartGroup();
        cartGroup.addItem(cartItem);
        Merchant merchant = shopProduct.getMerchant();
        cartGroup.setMerchant(merchant);
        return cartGroup;
    }
}
