package com.hunliji.hljhttplibrary.entities;

import com.hunliji.hljcommonlibrary.interfaces.HljRZData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by werther on 16/11/1.
 * 同步多个网络请求返回值打包类的接口，所有的ResultZip都建议继承这个类使其可以在请求结束时统一做空数据判断
 */

public class HljHttpResultZip {

    /**
     * 获取该对象中含有的所有自定义成员变量
     *
     * @return
     */
    public List<Field> getFields() {
        Field[] fields = this.getClass()
                .getDeclaredFields();
        List<Field> fieldList = new ArrayList<>();
        for (Field field : fields) {
            // 只获取带有@HljRZFiled注解的成员属性
            HljRZField hljRzField = field.getAnnotation(HljRZField.class);
            if (hljRzField != null) {
                field.setAccessible(true);
                fieldList.add(field);
            }
        }

        return fieldList;
    }

    /**
     * 判断该对象中的自定义的成员是不是全为空
     *
     * @return
     */
    public boolean isDataEmpty() {
        List<Field> fields = getFields();
        if (fields == null || fields.isEmpty()) {
            return true;
        } else {
            try {
                for (Field field : fields) {
                    Object o = field.get(this);
                    if (o != null) {
                        // 只要其中一个不为null，那整个数据就是非空的，否则那就是空的
                        if (o instanceof HljRZData) {
                            HljRZData rzData = (HljRZData) o;
                            if (!rzData.isEmpty()) {
                                return false;
                            }
                        } else if (o instanceof Collection) {
                            if (!((Collection) o).isEmpty()) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }
    }
}
