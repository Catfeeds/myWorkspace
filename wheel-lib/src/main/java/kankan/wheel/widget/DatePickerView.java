package kankan.wheel.widget;

import android.content.Context;
import android.util.AttributeSet;


/**
 * Created by luohanlin on 15/1/30.
 */
public class DatePickerView extends DTPicker {

    public DatePickerView(Context context) {
        this(context, null);
    }

    public DatePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DatePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setType(1);
        init();
    }
}
