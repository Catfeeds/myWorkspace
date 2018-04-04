package com.hunliji.marrybiz.widget;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.hunliji.marrybiz.R;

import java.util.ArrayList;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class MyMarkerView extends MarkerView {

    private TextView tvContent;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
    }

    @Override
    public int getXOffset(float xpos) {
        return 0;
    }

    @Override
    public int getYOffset(float ypos) {
        return 0;
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
//    @Override
//    public void refreshContent(Entry e, int dataSetIndex, ArrayList<String> xVals) {
//
//
//    }
//
//    @Override
//    public int getXOffset() {
//        // this will center the marker-view horizontally
//        return -(getWidth() / 2);
//    }
//
//    @Override
//    public int getYOffset() {
//        // this will cause the marker-view to be above the selected value
//        return -getHeight();
//    }
}
