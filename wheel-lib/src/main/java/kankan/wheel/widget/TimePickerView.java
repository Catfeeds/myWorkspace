package kankan.wheel.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by luohanlin on 15/1/30.
 */
public class TimePickerView extends DTPicker{
    public TimePickerView(Context context) {
        this(context, null);
    }

    public TimePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setType(2);
        init();
    }
}
