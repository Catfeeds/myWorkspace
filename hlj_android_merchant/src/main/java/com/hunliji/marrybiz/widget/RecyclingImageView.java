/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hunliji.marrybiz.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;

/**
 * Sub-class of ImageView which automatically notifies the drawable when it is
 * being displayed.
 */
public class RecyclingImageView extends ImageView {

	public RecyclingImageView(Context context) {
		super(context);
	}

	public RecyclingImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDetachedFromWindow() {
		Drawable drawable = getDrawable();
		if (drawable != null) {
			if (drawable instanceof AsyncBitmapDrawable) {
				AsyncBitmapDrawable asyncDrawable = (AsyncBitmapDrawable) drawable;
				ImageLoadTask loadTask = asyncDrawable.getImageLoadTask();
				if (loadTask != null && !loadTask.isCancelled()) {
					loadTask.cancel(true);
				}
			}
			setImageDrawable(null);
		}
		super.onDetachedFromWindow();
	}
}
