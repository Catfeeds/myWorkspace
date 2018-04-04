/**
 *
 */
package me.suncloud.marrymemo.entity;

/**
 * @author iDay
 */
public interface ImageLoadProgressListener {
    void transferred(int transferedBytes, String url);

    void setContentLength(long contentLength, String url);
}
