/**
 * 
 */
package com.hunliji.marrybiz.task;

import java.lang.ref.WeakReference;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

/**
 * @author iDay
 * 
 */
public class AsyncBitmapDrawable extends BitmapDrawable {
	private final WeakReference<ImageLoadTask> imageLoadTaskReference;

	public AsyncBitmapDrawable(Resources res, int resource,
			ImageLoadTask imageLoadTask) {
		this(res, BitmapFactory.decodeResource(res, resource), imageLoadTask);
	}

	public AsyncBitmapDrawable(Resources res, Bitmap bitmap,
			ImageLoadTask imageLoadTask) {
		super(res, bitmap);
		imageLoadTaskReference = new WeakReference<ImageLoadTask>(imageLoadTask);
	}

	public ImageLoadTask getImageLoadTask() {
		return imageLoadTaskReference.get();
	}
}
