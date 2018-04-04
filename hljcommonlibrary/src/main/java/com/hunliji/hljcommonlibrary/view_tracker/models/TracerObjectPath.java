package com.hunliji.hljcommonlibrary.view_tracker.models;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * Created by wangtao on 2018/2/12.
 */

public class TracerObjectPath {

    @SerializedName("key")
    private String key;
    @SerializedName("index")
    private Integer index;
    @SerializedName("field")
    private String field;
    @SerializedName("method")
    private String method;
    @SerializedName("child")
    private TracerObjectPath child;

    public Object getObject(Object classObject) {
        if (classObject == null) {
            return null;
        }
        Object object = null;
        if (!TextUtils.isEmpty(key)) {
            if (classObject instanceof Activity) {
                object = getBundleObject(((Activity) classObject).getIntent()
                        .getExtras());
            } else if (classObject instanceof Fragment) {
                object = getBundleObject(((Fragment) classObject).getArguments());
            }else if(classObject instanceof Map){
                try {
                    object = ((Map)classObject).get(key);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        if (object == null) {
            if (index!=null&&classObject instanceof List) {
                object = ((List) classObject).get(index);
            }
        }
        if (object == null) {
            if (!TextUtils.isEmpty(field)) {
                object = getFieldObject(classObject);
            }
        }
        if (object == null) {
            if (!TextUtils.isEmpty(method)) {
                object = getMethodObject(classObject);
            }
        }
        if (object != null && child != null) {
            object = child.getObject(object);
        }
        return object;
    }

    private Object getBundleObject(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        try {
            return bundle.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object getFieldObject(Object object) {
        if (object == null) {
            return null;
        }
        Exception lastException = null;
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz
                .getSuperclass()) {
            try {
                Field objectField = clazz.getDeclaredField(field);
                if (objectField != null) {
                    if (!objectField.isAccessible()) {
                        objectField.setAccessible(true);
                    }
                    return objectField.get(object);
                }
            } catch (Exception e) {
                lastException = e;
            }
        }
        if (lastException != null) {
            lastException.printStackTrace();
        }
        return null;
    }

    private Object getMethodObject(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return object.getClass()
                    .getMethod(method)
                    .invoke(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
