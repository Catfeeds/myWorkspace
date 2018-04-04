package com.hunliji.marrybiz.inter;

/**
 * 上传图片的subject
 *
 * @link UploadImageViewObserver
 * Created by jinxin on 2015/9/14.
 */
public interface UploadImageTaskSubject {
    void registObserver(UploadImageViewObserver observer);

    void removeObserver(UploadImageViewObserver observer);

    void notifyObservers();
}
