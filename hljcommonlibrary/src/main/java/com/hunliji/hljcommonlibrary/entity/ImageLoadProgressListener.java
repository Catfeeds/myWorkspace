package com.hunliji.hljcommonlibrary.entity;

/**
 * Created by Administrator on 2017/3/30 0030.
 */

public interface ImageLoadProgressListener {

    void transferred(int transferedBytes, String url);

    void setContentLength(long contentLength, String url);

}
