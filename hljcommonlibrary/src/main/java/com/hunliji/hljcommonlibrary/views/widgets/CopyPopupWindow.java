package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.hunliji.hljcommonlibrary.R;


public class CopyPopupWindow extends PopupWindow implements PopupWindow.OnDismissListener {
    private View mView;
    private View anchor;
    private Context context;

    public CopyPopupWindow(Context context, int resId) {
        this.context = context;
        setOnDismissListener(this);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        mView = mInflater.inflate(resId, null);
        calculateView(mView);
        this.setContentView(mView);
        this.setHeight(mView.getMeasuredHeight());
        this.setWidth(mView.getMeasuredWidth());
        this.setOutsideTouchable(true);
        this.setFocusable(false);
        ColorDrawable mDrawable = new ColorDrawable(ContextCompat.getColor(context,
                android.R.color.transparent));
        this.setBackgroundDrawable(mDrawable);
    }


    @Override
    public View getContentView() {
        return mView;
    }

    private void calculateView(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
    }

    @Override
    public void showAsDropDown(@NonNull View anchor, int xoff, int yoff) {
        this.anchor = anchor;
        this.anchor.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLine));
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void onDismiss() {
        if (anchor != null) {
            this.anchor.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
        }
    }
}
