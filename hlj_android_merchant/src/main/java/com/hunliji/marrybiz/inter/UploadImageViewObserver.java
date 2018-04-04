package com.hunliji.marrybiz.inter;

/**
 * 上传图片task的观察者
 *
 * @link UploadImageTaskSubject
 * Created by jinxin on 2015/9/14.
 */
public interface UploadImageViewObserver {
    void update(String uploadValue, int currentValue, int totalValue);
}
