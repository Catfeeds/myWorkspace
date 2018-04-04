package me.suncloud.marrymemo.util;

import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.suncloud.marrymemo.model.CarSku;
import me.suncloud.marrymemo.model.CarSkuItem;

/**
 * carsku util
 * Created by jinxin on 2015/10/9.
 */
public class CarSkuUtil {
    private static String TAG = CarSkuUtil.class.getName();
    private List<CarSku> mData;
    private Set<CarSkuItem> propertys;
    private CarSkuItem lastSearchItem;
    /**
     * all skuitem
     */
    private SparseArray<List<CarSkuItem>> allSkuItem;

    private Map<String, List<CarSkuItem>> showValue;

    private LinkedList<CarSkuItem> searchItem;
    private int skuItemCount = -1;

    private OnSizeChangedListener<CarSkuItem> onSizeChangedListener;

    private int oneSkuItemSize;

    public CarSkuUtil(List<CarSku> mData) {
        if (mData == null || mData.size() <= 0) {
            return;
        }
        this.mData = mData;
        propertys = new LinkedHashSet<>();
        allSkuItem = getAllSkuItem();
        searchItem = new LinkedList<CarSkuItem>();
    }

    private Set<CarSkuItem> getPropertys(SparseArray<List<CarSkuItem>> skuItems) {
        Set<CarSkuItem> propertys = new LinkedHashSet<>();
        for (int i = 0; i < skuItems.size(); i++) {
            List<CarSkuItem> value = skuItems.valueAt(i);
            if (value == null || value.size() <= 0) {
                continue;
            }
            for (CarSkuItem item : value) {
                if (!hasPro(propertys, item)) {
                    propertys.add(item);
                }
            }
        }
        return propertys;
    }

    public Set<CarSkuItem> getPropertys() {
        return getPropertys(allSkuItem);
    }

    public Set<String> getProsString() {
        Set<String> result = new HashSet<String>();
        Set<CarSkuItem> items = getPropertys();
        for (CarSkuItem item : items) {
            result.add(item.getProperty());
        }
        return result;
    }

    private boolean hasPro(Set<CarSkuItem> pros, CarSkuItem item) {
        boolean result = false;
        for (CarSkuItem pro : pros) {
            if (pro.getProperty_id() == item.getProperty_id()) {
                result = true;
                break;
            }
        }
        return result;
    }

    public Map<String, List<CarSkuItem>> getShowValue1() {
        return getShowValue(searchSkuItems(searchItem));
    }

    public Map<String, List<CarSkuItem>> getShowValue() {
        return getShowValue(allSkuItem);
    }

    public Map<String, List<CarSkuItem>> getShowValue(SparseArray<List<CarSkuItem>> skuItems) {
        showValue = new LinkedHashMap<>();
        if (skuItems == null || skuItems.size() <= 0) {
//            Log.e(TAG, "search result is null or result size is zero");
//            logMap(showValue);
            return showValue;
        }
        propertys = getPropertys(skuItems);
        for (CarSkuItem property : propertys) {
            List<CarSkuItem> list = new ArrayList<CarSkuItem>();
            //propertys
            for (int i = 0; i < skuItems.size(); i++) {
                List<CarSkuItem> value = skuItems.valueAt(i);
                if (value == null || value.size() <= 0) {
                    continue;
                }
                for (CarSkuItem item : value) {
                    if (property.getProperty_id() == item.getProperty_id()) {
                        if (!hasItemValue(list, item)) {
                            list.add(item);
                        }
                    }
                }
            }
            if(showValue.get(property.getProperty())==null) {
                showValue.put(property.getProperty(), list);
            }else{
                List<CarSkuItem> valueList=showValue.get(property.getProperty());
                valueList.addAll(list);
            }
        }
//        logMap(showValue);
        return showValue;
    }

    private void logMap(Map<String, List<CarSkuItem>> value) {
//        Log.e(TAG, "show values");
        for (String key : value.keySet()) {
            List<CarSkuItem> values = value.get(key);
            StringBuilder builder = new StringBuilder();
            builder.append(key).append("-->");
            for (CarSkuItem item : values) {
                builder.append(item.getProperty()).append(":").append(item.getValue()).append("|");
            }
//            Log.e(TAG, builder.toString());
        }
    }

    /**
     * compare by Value_id
     *
     * @param items
     * @param item
     * @return
     */
    private boolean hasItemValue(List<CarSkuItem> items, CarSkuItem item) {
        boolean result = false;
        if (items == null || items.size() <= 0) {
            return false;
        }
        for (CarSkuItem it : items) {
            if (it.getValue_id() == item.getValue_id()) {
                result = true;
                break;
            }
        }
        return result;
    }


    private SparseArray<List<CarSkuItem>> getAllSkuItem() {
        allSkuItem = new SparseArray<List<CarSkuItem>>();
        for (CarSku sku : mData) {
            if (sku.getItems() == null || sku.getItems().size() <= 0) {
                continue;
            }
            allSkuItem.put(sku.getId().intValue(), sku.getItems());
        }
        for (int i = 0; i < allSkuItem.size(); i++) {
            int id = allSkuItem.keyAt(i);
            StringBuilder buffer = new StringBuilder();
            List<CarSkuItem> items = allSkuItem.valueAt(i);
            for (CarSkuItem item : items) {
                buffer.append(item.getProperty()).append(":").append(item.getValue()).append("||");
            }
//            Log.e(TAG, "id:" + id);
//            Log.e(TAG, buffer.toString());
        }
        skuItemCount = allSkuItem.size();
        return allSkuItem;
    }

    /**
     * 找出所有包含这一项的sku
     *
     * @param item
     * @return
     */
    private SparseArray<List<CarSkuItem>> searchSkuItems(SparseArray<List<CarSkuItem>> searchItem, CarSkuItem item) {
        long itemValueId = item.getValue_id();
        SparseArray<List<CarSkuItem>> search = new SparseArray<List<CarSkuItem>>();
        for (int i = 0; i < searchItem.size(); i++) {
            //id is sku id
            int id = searchItem.keyAt(i);
            // value is sku point to carskuitem
            List<CarSkuItem> value = searchItem.valueAt(i);
            for (CarSkuItem valueItem : value) {
                if (valueItem.getValue_id() == itemValueId) {
                    search.put(id, value);
                    continue;
                }
            }
        }
        return search;
    }

    /**
     * 返回 包含选中规格的sku
     *
     * @return
     */
    public List<CarSku> getSeachSkuItems() {
        List<CarSku> res = new LinkedList<>();
        SparseArray<List<CarSkuItem>> data = searchSkuItems(searchItem);
        for (int i = 0; i < data.size(); i++) {
            List<CarSkuItem> temp = data.valueAt(i);
            for (CarSkuItem item : temp) {
                CarSku sku = getCarSkuById((int) item.getSku_id());
                if (!res.contains(sku)) {
                    res.add(sku);
                }
            }
        }
        return res;
    }

    /**
     * 找出包含这一项的sku
     *
     * @param searchItemData
     * @param searchItems
     * @return
     */
    /*
    public SparseArray<List<CarSkuItem>> searchSkuItems(SparseArray<List<CarSkuItem>> searchItemData, List<CarSkuItem> searchItems) {
        SparseArray<List<CarSkuItem>> search = new SparseArray<List<CarSkuItem>>();
        for (int i = 0; i < searchItemData.size(); i++) {
            int id = searchItemData.keyAt(i);
            List<CarSkuItem> data = searchItemData.valueAt(i);
            for (CarSkuItem item : searchItems) {
                if (hasItemValue(data, item)) {
                    search.put(id, data);
                    break;
                }
            }
        }
        return search;
    }
    */

    /**
     * items 是搜索条件
     *
     * @param items
     * @return
     */
    public SparseArray<List<CarSkuItem>> searchSkuItems(List<CarSkuItem> items) {
        SparseArray<List<CarSkuItem>> searchItem = allSkuItem;
        StringBuilder builder = new StringBuilder();
        for (CarSkuItem item : items) {
            builder.append(item.getValue()).append("|");
            searchItem = searchSkuItems(searchItem, item);
        }
//        Log.e(TAG, builder.toString());
        return searchItem;
    }


    public SparseArray<List<CarSkuItem>> addSearchItem(CarSkuItem item) {
        Iterator iterator = searchItem.iterator();
        while (iterator.hasNext()) {
            CarSkuItem it = (CarSkuItem) iterator.next();
            if (it.getProperty_id() == item.getProperty_id()) {
                //已经有相同类别的值
                iterator.remove();
            }
        }
        searchItem.add(item);
        onSizeChange();
        return searchSkuItems(searchItem);
    }

    public SparseArray<List<CarSkuItem>> removeSearchItem(CarSkuItem item) {
        if (hasItemValue(searchItem, item)) {
            lastSearchItem = item;
            Iterator iterator = searchItem.iterator();
            while (iterator.hasNext()) {
                CarSkuItem it = (CarSkuItem) iterator.next();
                if (it.getValue_id() == item.getValue_id()) {
                    //已经有相同类别的值
                    iterator.remove();
                }
            }
        }
        onSizeChange();
        return searchSkuItems(searchItem);
    }

    public SparseArray<List<CarSkuItem>> setCurrentPro(String pro) {
        LinkedList<CarSkuItem> tempItem = new LinkedList<>();
        tempItem.addAll(searchItem);
        if (hasItemValue(tempItem, pro)) {
            removeItemValue(tempItem, pro);
        }
        return searchSkuItems(tempItem);
    }

    private void removeItemValue(LinkedList<CarSkuItem> items, String pro) {
        for (int i = 0; i < items.size(); i++) {
            CarSkuItem item = items.get(i);
            if (item.getProperty().equalsIgnoreCase(pro)) {
                items.remove(i);
            }
        }
    }

    private boolean hasItemValue(LinkedList<CarSkuItem> items, String pro) {
        boolean result = false;
        if (items == null || items.size() <= 0) {
            return false;
        }
        for (CarSkuItem it : items) {
            if (pro.equalsIgnoreCase(it.getProperty())) {
                result = true;
                break;
            }
        }
        return result;
    }

    public CarSkuItem getLastSearchItem() {
        if (lastSearchItem != null) {
            Log.e(TAG, "lastSearchItem-->" + lastSearchItem.getProperty() + ":" + lastSearchItem.getValue());
            return lastSearchItem;
        }
        return null;
    }

    public LinkedList<CarSkuItem> getSearchItem() {
        return searchItem;
    }

    public int getSearchItemCount() {
        int count = searchItem.size();
        return count;
    }

    /**
     * 根据选择item查找sku
     * 所有的满足 才能查找出来
     *
     * @param selcetItems
     * @return
     */
    public CarSku getSelectSku(SparseArray<CarSkuItem> selcetItems) {
        int id = -1;
        boolean found = false;
        if (selcetItems.size() <= 0) {
            return null;
        }
        List<Boolean> result = new ArrayList<Boolean>();
        for (int i = 0; i < allSkuItem.size(); i++) {
            if (found) {
                break;
            }
            //skuid
            id = allSkuItem.keyAt(i);
            //sku 对应的skuitem items 去判断有没有包含这个selectItems
            List<CarSkuItem> items = allSkuItem.valueAt(i);
            if (items.size() != selcetItems.size()) {
                continue;
            }
            for (int j = 0; j < selcetItems.size(); j++) {
                CarSkuItem selectItem = selcetItems.valueAt(j);
                if (hasItemValue(items, selectItem)) {
                    result.add(true);
                    if (result.size() == selcetItems.size()) {
                        found = true;
                    }
                } else {
                    result.clear();
                    break;
                }
            }
        }
        if (result.size() == selcetItems.size()) {
            return getCarSkuById(id);
        }
        return null;
    }

    /**
     * 根据选择item查找sku
     * 所有的满足 才能查找出来
     *
     * @param selcetItems
     * @return
     */
    public CarSku getSelectSku(List<CarSkuItem> selcetItems) {
        SparseArray<CarSkuItem> search = new SparseArray<CarSkuItem>();
        for (CarSkuItem item : selcetItems) {
            search.put((int) item.getProperty_id(), item);
        }
        return getSelectSku(search);
    }

    public CarSku getCarSkuById(int id) {
        CarSku sku = null;
        for (CarSku carSku : mData) {
            if (carSku.getId() == id) {
                sku = carSku;
                break;
            }
        }
        return sku;
    }

    public void setOneSkuItemSize(int oneSkuItemSize) {
        this.oneSkuItemSize = oneSkuItemSize;
    }

    public int getSkuItemCount() {
        return skuItemCount;
    }

    public interface OnSizeChangedListener<T> {
        void onSizeChanged(List<T> data);
    }

    public void setOnSizeChangedListener(OnSizeChangedListener<CarSkuItem> onSizeChangedListener) {
        this.onSizeChangedListener = onSizeChangedListener;
    }

    private void onSizeChange() {
        if (this.onSizeChangedListener != null) {
            this.onSizeChangedListener.onSizeChanged(searchItem);
        }
    }
}
