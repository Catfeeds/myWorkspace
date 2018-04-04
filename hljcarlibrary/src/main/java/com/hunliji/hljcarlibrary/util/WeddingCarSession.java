package com.hunliji.hljcarlibrary.util;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcarlibrary.models.CarShoppingCartItem;
import com.hunliji.hljcarlibrary.models.WeddingCarCartList;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by jinxin on 2018/1/12 0012.
 */

public class WeddingCarSession {

    private WeddingCarCartList currentCarCart;
    private ArrayList<WeddingCarCartList> carCatMap;
    private User currentUser;

    private WeddingCarSession() {

    }

    public static WeddingCarSession getInstance() {
        return SessionHolder.INSTANCE;
    }

    private static class SessionHolder {
        private static final WeddingCarSession INSTANCE = new WeddingCarSession();
    }

    public User getCurrentUser(Context context) {
        if (currentUser == null && context != null) {
            currentUser = UserSession.getInstance()
                    .getUser(context);
        }
        return currentUser;
    }

    private void postRefreshCarCartCountRxEvent() {
        RxBus.getDefault()
                .post(new RxEvent(RxEvent.RxEventType.WEDDING_CAR_CART_COUNT, null));
    }

    public void addCarCart(Context context, CarShoppingCartItem cartItem) {
        if (getCurrentUser(context) == null || getCurrentUser(context).getId() <= 0) {
            return;
        }
        ArrayList<CarShoppingCartItem> cartItems = getCurrentCarCart(context,
                getCurrentUser(context).getId(),
                true).getItems();
        for (CarShoppingCartItem item : cartItems) {
            if (item.getId() == cartItem.getId()) {
                item.addQuantity(cartItem.getQuantity());
                saveCarCatMap(context);
                postRefreshCarCartCountRxEvent();
                return;
            }
        }
        cartItems.add(cartItem);
        saveCarCatMap(context);
        postRefreshCarCartCountRxEvent();
    }

    public void clearCart(Context context) {
        if (getCurrentUser(context) != null && getCurrentUser(context).getId() > 0 && !getCarCatMap(
                context).isEmpty()) {
            WeddingCarCartList cartList = getCurrentCarCart(context, currentUser.getId(), false);
            cartList.getItems()
                    .clear();
            carCatMap.remove(cartList);
            currentCarCart = null;
            saveCarCatMap(context);
        }
        postRefreshCarCartCountRxEvent();
    }

    public void removeCarCart(Context context, CarShoppingCartItem cartItem) {
        if (getCurrentUser(context) != null && getCurrentUser(context).getId() > 0) {
            WeddingCarCartList cartList = getCurrentCarCart(context, currentUser.getId(), false);
            CarShoppingCartItem removeItem = null;
            for (CarShoppingCartItem item : cartList.getItems()) {
                if (item.getId() == cartItem.getId()) {
                    item.subtractQuantity(cartItem.getQuantity());
                    removeItem = item;
                    break;
                }
            }
            if (removeItem != null) {
                if (removeItem.getQuantity() <= 0) {
                    cartList.getItems()
                            .remove(removeItem);
                }
                if (cartList.getItems()
                        .isEmpty()) {
                    carCatMap.remove(cartList);
                    currentCarCart = null;
                }
                saveCarCatMap(context);
            }
        }
        postRefreshCarCartCountRxEvent();
    }

    public void cartQuantityChange(Context context, long id, int kind) {
        ArrayList<CarShoppingCartItem> cartItems = getCarCartItems(context);
        for (CarShoppingCartItem item : cartItems) {
            if (item.getId() == id) {
                if (kind == 0) {
                    item.quantityPlus();
                } else {
                    item.quantitySubtract();
                }
                saveCarCatMap(context);
                return;
            }
        }
        postRefreshCarCartCountRxEvent();
    }

    public int getCarCartCount(Context context, long id) {
        int count = 0;
        ArrayList<CarShoppingCartItem> cartItems = getCarCartItems(context);
        for (CarShoppingCartItem item : cartItems) {
            if (id == 0 || item.getId() == id) {
                count += item.getQuantity();
            }
        }
        return count;
    }


    public ArrayList<CarShoppingCartItem> getCarCartItems(Context context) {
        if (getCurrentUser(context) != null && getCurrentUser(context).getId() > 0) {
            return getCurrentCarCart(context, currentUser.getId(), false).getItems();
        }
        return new ArrayList<>();
    }

    public boolean carCartCityChecked(Context context, long cityId) {
        if (cityId > 0 && getCurrentUser(context) != null && getCurrentUser(context).getId() > 0) {
            WeddingCarCartList cartList = getCurrentCarCart(context, currentUser.getId(), false);
            return cartList.cityChecked(cityId);
        }
        return false;
    }

    private WeddingCarCartList getCurrentCarCart(
            Context context, long userId, boolean isAdd) {
        if (currentCarCart == null || currentCarCart.getUserId() != userId) {
            currentCarCart = new WeddingCarCartList();
            for (WeddingCarCartList cartList : getCarCatMap(context)) {
                if (cartList.getUserId() == userId) {
                    currentCarCart = cartList;
                    return currentCarCart;
                }
            }
            if (isAdd) {
                currentCarCart.setUserId(userId);
                carCatMap.add(currentCarCart);
            }
        }
        return currentCarCart;
    }

    private ArrayList<WeddingCarCartList> getCarCatMap(Context context) {
        if (carCatMap == null) {
            carCatMap = new ArrayList<>();
            if (context.getFileStreamPath(HljCommon.FileNames.CAR_CART_FILE) != null && context
                    .getFileStreamPath(
                    HljCommon.FileNames.CAR_CART_FILE)
                    .exists()) {
                try {
                    InputStream in = context.openFileInput(HljCommon.FileNames.CAR_CART_FILE);
                    String jsonStr = CommonUtil.readStreamToString(in);
                    if (!TextUtils.isEmpty(jsonStr)) {
                        ArrayList<WeddingCarCartList> lists = GsonUtil.getGsonInstance()
                                .fromJson(jsonStr,
                                        new TypeToken<ArrayList<WeddingCarCartList>>() {}.getType
                                                ());
                        carCatMap.addAll(lists);
                    }
                    ArrayList<WeddingCarCartList> emptyItem = new ArrayList<>();
                    for (WeddingCarCartList cartList : carCatMap) {
                        if (cartList.getItems()
                                .isEmpty()) {
                            emptyItem.add(cartList);
                        }
                    }
                    if (!emptyItem.isEmpty()) {
                        carCatMap.removeAll(emptyItem);
                        saveCarCatMap(context);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return carCatMap;
    }

    private void saveCarCatMap(Context context) {
        if (carCatMap != null && !carCatMap.isEmpty()) {
            try {
                OutputStreamWriter out = new OutputStreamWriter(context.openFileOutput(HljCommon
                                .FileNames.CAR_CART_FILE,
                        Context.MODE_PRIVATE));
                String carMap = GsonUtil.getGsonInstance()
                        .toJson(carCatMap);
                out.write(carMap);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            context.deleteFile(HljCommon.FileNames.CAR_CART_FILE);
        }
    }

}
