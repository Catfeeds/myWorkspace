/**
 *
 */
package me.suncloud.marrymemo.entity;

/**
 * @author iDay
 */
public interface ProgressListener {
    void transferred(long transferedBytes);

    void setContentLength(long contentLength);
}
