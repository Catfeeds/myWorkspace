package com.hunliji.hljhttplibrary.entities;

/**
 * Created by Suncloud on 2016/8/24.
 */
public interface HljUploadListener {

    void transferred(long transBytes);

    void setContentLength(long contentLength);
}
