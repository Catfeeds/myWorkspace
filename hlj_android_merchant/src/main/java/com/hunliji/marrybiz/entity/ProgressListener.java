/**
 * 
 */
package com.hunliji.marrybiz.entity;

/**
 * @author iDay
 *
 */
public interface ProgressListener {
	void transferred(int transferedBytes);
	void setContentLength(long contentLength);
}
