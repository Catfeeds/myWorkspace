/*
 * Copyright 2013, Edmodo, Inc. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" 
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
 */

package com.edmodo.rangebar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Class representing the blue connecting line between the two thumbs.
 */
class ConnectingLine {

    // Member Variables ////////////////////////////////////////////////////////

    private final Paint mPaint;

    private final float mConnectingLineWeight;
    private final float mY;

    private int disEnableColor;
    private int connectingLineColor;

    // Constructor /////////////////////////////////////////////////////////////

    ConnectingLine(Context ctx, float y, float connectingLineWeight, int connectingLineColor, int disEnableColor, boolean isEnable) {

        mConnectingLineWeight = connectingLineWeight;

        // Initialize the paint, set values
        this.disEnableColor = disEnableColor;
        this.connectingLineColor = connectingLineColor;
        mPaint = new Paint();
        mPaint.setStrokeWidth(mConnectingLineWeight);
        mPaint.setAntiAlias(true);
        setEnabled(isEnable);
        mY = y;
    }

    public void setEnabled(boolean enabled) {
        if (!enabled) {
            mPaint.setColor(disEnableColor);
        } else {
            mPaint.setColor(connectingLineColor);
        }
    }

    void draw(Canvas canvas, Thumb leftThumb, Thumb rightThumb) {
        canvas.drawLine(leftThumb.getX(), mY, rightThumb.getX(), mY, mPaint);
    }
}
