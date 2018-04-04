package me.suncloud.marrymemo.model;

/**
 * Created by werther on 15/9/21.
 */
public class CarSkuItem  implements Identifiable {
    private long id;
    private String value;
    private String property;
    private long property_id;
    private long sku_id;
    private long value_id;

    @Override
    public Long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public String getProperty() {
        return property;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public long getProperty_id() {
        return property_id;
    }

    public void setProperty_id(long property_id) {
        this.property_id = property_id;
    }

    public long getSku_id() {
        return sku_id;
    }

    public void setSku_id(long sku_id) {
        this.sku_id = sku_id;
    }

    public long getValue_id() {
        return value_id;
    }

    public void setValue_id(long value_id) {
        this.value_id = value_id;
    }
}
