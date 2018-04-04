package kankan.wheel.widget;


import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by luohanlin on 15/1/30.
 */
public class DateTimePickerView extends DTPicker {
    public DateTimePickerView(Context context) {
        this(context, null);
    }

    public DateTimePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DateTimePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setType(0);
        init();
    }

}
