package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import me.suncloud.marrymemo.R;

/**
 * Created by Suncloud on 2015/1/20.
 */
public class CopyPopupWindow extends PopupWindow implements PopupWindow.OnDismissListener{
    private View mView ;
    private View anchor ;
    private Context context;
    public CopyPopupWindow(Context context,int resId) {
        this.context=context;
        setOnDismissListener(this);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = mInflater.inflate(resId,null);
        caculateView(mView);
        this.setContentView(mView);
        this.setHeight(mView.getMeasuredHeight());
        this.setWidth(mView.getMeasuredWidth());
        this.setOutsideTouchable(true);
        this.setFocusable(false);
        ColorDrawable mDrawable = new ColorDrawable(context.getResources().getColor(android.R.color.transparent));
        this.setBackgroundDrawable(mDrawable);
    }


    @Override
    public View getContentView() {
        return mView;
    }

    private void caculateView(View view){
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
    }

    @Override
    public void showAsDropDown(@NonNull View anchor, int xoff, int yoff) {
        this.anchor=anchor;
        this.anchor.setBackgroundColor(context.getResources().getColor(R.color.colorLine));
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void onDismiss() {
        if(anchor!=null) {
            this.anchor.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
        }
    }
}
