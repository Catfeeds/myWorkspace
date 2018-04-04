package com.hunliji.hljcarlibrary.util;

import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarSku;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarSkuItem;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Created by jinxin on 2015/10/9.
 */
public class WeddingCarSkuUtil {
    private List<WeddingCarSku> rawData;//原始数据
    private Map<Long, List<WeddingCarSkuItem>> propertySkuItemList;
    private Map<Long, String> properties;
    private Map<Long, List<WeddingCarSkuItem>> allSkuItem;
    private Map<String, List<WeddingCarSkuItem>> allShowValue;

    public WeddingCarSkuUtil(List<WeddingCarSku> rawData) {
        if (rawData == null || rawData.size() <= 0) {
            return;
        }
        this.rawData = rawData;
        initConstant();
    }

    private void initConstant() {
        initAllSkuItem();
        initPropertySkuItemList();
        initProperty();
        initAllShowValue();
    }

    private void initProperty() {
        properties = new HashMap<>();
        Iterator<Long> iterator = allSkuItem.keySet()
                .iterator();
        while (iterator.hasNext()) {
            long key = iterator.next();
            List<WeddingCarSkuItem> itemList = allSkuItem.get(key);
            if (itemList == null || itemList.isEmpty()) {
                continue;
            }
            for (WeddingCarSkuItem item : itemList) {
                long propertyId = item.getPropertyId();
                properties.put(propertyId, item.getProperty());
            }
        }
    }

    /**
     * 初始化 公里数:800  900 等这种数据
     */
    private void initPropertySkuItemList() {
        propertySkuItemList = new HashMap<>();
        Iterator<Long> iterator = allSkuItem.keySet()
                .iterator();
        while (iterator.hasNext()) {
            long key = iterator.next();
            List<WeddingCarSkuItem> itemList = allSkuItem.get(key);
            if (itemList == null || itemList.isEmpty()) {
                continue;
            }
            for (WeddingCarSkuItem item : itemList) {
                long propertyId = item.getPropertyId();
                List<WeddingCarSkuItem> propertyList = propertySkuItemList.get(propertyId);
                if (propertyList == null) {
                    propertyList = new ArrayList<>();
                    propertySkuItemList.put(propertyId, propertyList);
                }
                propertyList.add(item);
            }
        }
    }

    /**
     * skiId 对应skuItem
     */
    private void initAllSkuItem() {
        allSkuItem = new HashMap<>();
        for (WeddingCarSku sku : rawData) {
            if (sku.getSkuItem() == null || sku.getSkuItem()
                    .isEmpty()) {
                continue;
            }
            allSkuItem.put(sku.getId(), sku.getSkuItem());
        }
    }

    private void initAllShowValue() {
        allShowValue = new HashMap<>();
        Iterator<Long> iterator = propertySkuItemList.keySet()
                .iterator();
        while (iterator.hasNext()) {
            long key = iterator.next();
            List<WeddingCarSkuItem> itemList = propertySkuItemList.get(key);
            for (WeddingCarSkuItem item : itemList) {
                List<WeddingCarSkuItem> showItemList = allShowValue.get(item.getProperty());
                if (showItemList == null) {
                    showItemList = new ArrayList<>();
                    allShowValue.put(item.getProperty(), showItemList);
                }
                if (!hasWeddingCarSkuItem(showItemList, item)) {
                    showItemList.add(item);
                }
            }
        }
    }

    private boolean hasWeddingCarSkuItem(List<WeddingCarSkuItem> itemList, WeddingCarSkuItem item) {
        if (itemList == null || item == null) {
            return false;
        }
        for (WeddingCarSkuItem carSkuItem : itemList) {
            if (carSkuItem.getValueId() == item.getValueId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回所有的skuItem值
     */
    public List<String> getAllPropertyString() {
        List<String> result = new ArrayList<>();
        Iterator<Long> iterator = properties.keySet()
                .iterator();
        while (iterator.hasNext()) {
            long key = iterator.next();
            if (!result.contains(properties.get(key))) {
                result.add(properties.get(key));
            }
        }

        return result;
    }

    public long getPropertyId(String property) {
        if (TextUtils.isEmpty(property)) {
            return -1L;
        }
        Iterator<Long> iterator = properties.keySet()
                .iterator();
        while (iterator.hasNext()) {
            long key = iterator.next();
            String p = properties.get(key);
            if (p.equalsIgnoreCase(property)) {
                return key;
            }
        }
        return -1L;
    }

    /**
     * 根据已经选择的skuItem 找出 包含这些skuItem的sku
     *
     * @return
     */
    public Map<String, List<WeddingCarSkuItem>> getShowValueBySearchItem(
            Map<Long, WeddingCarSkuItem> selectSkuItemCopy) {
        if (selectSkuItemCopy == null || selectSkuItemCopy.isEmpty()) {
            return allShowValue;
        }
        List<WeddingCarSkuItem> itemList = new LinkedList<>();
        itemList.addAll(selectSkuItemCopy.values());
        return getShowValue(searchSkuItems(itemList));
    }

    public Map<String, List<WeddingCarSkuItem>> getAllShowValue() {
        return allShowValue;
    }

    /**
     * @param skuItems skuId List<WeddingCarSkuItem> 的结构
     * @return
     */
    public Map<String, List<WeddingCarSkuItem>> getShowValue(
            Map<Long, List<WeddingCarSkuItem>> skuItems) {
        if (skuItems == null) {
            return null;
        }
        Map<String, List<WeddingCarSkuItem>> showValue = new HashMap<>();
        Iterator<Long> iterator = skuItems.keySet()
                .iterator();
        while (iterator.hasNext()) {
            long key = iterator.next();
            List<WeddingCarSkuItem> itemList = skuItems.get(key);
            for (WeddingCarSkuItem item : itemList) {
                List<WeddingCarSkuItem> valueItemList = showValue.get(item.getProperty());
                if (valueItemList == null) {
                    valueItemList = new ArrayList<>();
                    showValue.put(item.getProperty(), valueItemList);
                }
                if (!hasWeddingCarSkuItem(valueItemList, item)) {
                    valueItemList.add(item);
                }
            }
        }
        return showValue;
    }

    /**
     * 通过 Value_id 比较
     *
     * @param items
     * @param item
     * @return
     */
    private boolean hasItemByValueId(List<WeddingCarSkuItem> items, WeddingCarSkuItem item) {
        boolean result = false;
        if (items == null || items.size() <= 0) {
            return false;
        }
        for (WeddingCarSkuItem it : items) {
            if (it.getValueId() == item.getValueId()) {
                result = true;
                break;
            }
        }
        return result;
    }
    /**
     * 通过 PropertyId 比较
     *
     * @param items
     * @param item
     * @return
     */
    private boolean hasItemByPropertyId(List<WeddingCarSkuItem> items, WeddingCarSkuItem item) {
        boolean result = false;
        if (items == null || items.size() <= 0) {
            return false;
        }
        for (WeddingCarSkuItem it : items) {
            if (it.getPropertyId() == item.getPropertyId()) {
                result = true;
                break;
            }
        }
        return result;
    }


    /**
     * 判断listA 完全在listB中
     *
     * @param listA
     * @param listB
     * @return
     */
    private boolean listAInListBByValueId(
            List<WeddingCarSkuItem> listA, List<WeddingCarSkuItem> listB) {
        if (CommonUtil.isCollectionEmpty(listA) || CommonUtil.isCollectionEmpty(listB)) {
            return false;
        }
        boolean result = true;
        for (int i = 0, iSize = listA.size(); i < iSize; i++) {
            WeddingCarSkuItem itemA = listA.get(i);
            if (!hasItemByValueId(listB, itemA)) {
                //itemA不在listB中
                result = false;
                break;
            }
        }
        return result;
    }



    /**
     * items 是搜索条件
     * 数据结构 skuId List<WeddingCarSkuItem>
     * @param items
     * @return
     */
    public Map<Long, List<WeddingCarSkuItem>> searchSkuItems(List<WeddingCarSkuItem> items) {
        if (CommonUtil.isCollectionEmpty(items)) {
            return allSkuItem;
        }
        Map<Long, List<WeddingCarSkuItem>> searchItem = new HashMap<>();
        Iterator<Long> iterator = allSkuItem.keySet()
                .iterator();
        while (iterator.hasNext()) {
            long key = iterator.next();
            List<WeddingCarSkuItem> itemList = allSkuItem.get(key);
            if (listAInListBByValueId(items, itemList)) {
                searchItem.put(key, itemList);
            }
        }

        return searchItem;
    }

    /**
     * 根据选择item查找sku
     * 所有的满足 才能查找出来
     *
     * @param selectItems
     * @return
     */
    public WeddingCarSku getSelectSku(Map<Long, WeddingCarSkuItem> selectItems) {
        if (selectItems.size() <= 0) {
            return null;
        }

        List<WeddingCarSkuItem> itemList = new LinkedList<>();
        itemList.addAll(selectItems.values());
        return getSelectSku(itemList);
    }

    /**
     * 根据选择item查找sku
     * 所有的满足 才能查找出来
     *
     * @param selectItems
     * @return
     */
    public WeddingCarSku getSelectSku(List<WeddingCarSkuItem> selectItems) {
        Map<Long, List<WeddingCarSkuItem>> searchItems = searchSkuItems(selectItems);
        if (searchItems != null && searchItems.size() == 1) {
            return getCarSkuById(searchItems.keySet()
                    .iterator()
                    .next());
        }
        return null;
    }

    public WeddingCarSku getCarSkuById(long id) {
        WeddingCarSku sku = null;
        for (WeddingCarSku carSku : rawData) {
            if (carSku.getId() == id) {
                sku = carSku;
                break;
            }
        }
        return sku;
    }
}
