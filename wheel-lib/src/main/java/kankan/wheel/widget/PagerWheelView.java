package kankan.wheel.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import kankan.wheel.R;
import kankan.wheel.widget.HorizontalWheel.WheelHorizontalView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

/**
 * Created by Suncloud on 2015/4/17.
 */
public class PagerWheelView extends FrameLayout {

    private Context mContext;
    private WheelHorizontalView pageWheel;
    private int minValue;
    private int maxValue;
    private int index=-1;

    public PagerWheelView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public PagerWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public PagerWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        layoutInflater.inflate(R.layout.page_wheel_layout, this, true);
        pageWheel = (WheelHorizontalView)findViewById(R.id.horizontal_wheel);
        if(index>=0) {
            pageWheel.setCurrentItem(Math.min(index, maxValue - minValue));
        }
        NumericWheelAdapter pageAdapter = new NumericWheelAdapter(mContext,Math.max(minValue,1), Math.max(maxValue,20));
        pageAdapter.setItemResource(R.layout.page_text_view);
        pageAdapter.setItemTextResource(R.id.pager);
        pageWheel.setViewAdapter(pageAdapter);

    }

    public void setVauleChange(int minValue,int maxValue){
        this.minValue=minValue;
        this.maxValue=maxValue;
        if(pageWheel!=null) {
            NumericWheelAdapter pageAdapter = new NumericWheelAdapter(mContext, minValue, maxValue);
            pageAdapter.setItemResource(R.layout.page_text_view);
            pageAdapter.setItemTextResource(R.id.pager);
            pageWheel.setViewAdapter(pageAdapter);
        }
    }


    public int getCurrentItem(){
        if(pageWheel==null){
            return 0;
        }
        return pageWheel.getCurrentItem();
    }



    public void setCurrentItem(int index){
        this.index=index;
        if(pageWheel==null){
            return;
        }
        pageWheel.setCurrentItem(Math.min(index, maxValue - minValue));
    }
}
