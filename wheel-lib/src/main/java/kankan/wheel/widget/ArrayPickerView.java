package kankan.wheel.widget;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import kankan.wheel.R;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

/**
 * Created by luohanlin on 15/4/2.
 */
public class ArrayPickerView extends FrameLayout{

    private final static int MSG_SOUND_EFFECT = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SOUND_EFFECT:
                    playSoundEffect();
                    break;
            }
        }
    };
    private Context mContext;
    private DisplayMetrics dm;
    private SoundPool sp;
    private int selectItem=-1;
    private int soundId;
    private WheelView arrayWheel;
    private String[] strs =  {"", "", "", ""};
    private OnPickerArrayListener onPickerArrayListener;
    private ArrayWheelAdapter<String> adapter;

    public ArrayPickerView(Context context) {
        super(context);
        init(context);
    }

    public ArrayPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ArrayPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        dm = getResources().getDisplayMetrics();
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundId = sp.load(mContext, R.raw.time_picker, 1);

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        layoutInflater.inflate(R.layout.array_picker, this, true);
        arrayWheel = (WheelView) findViewById(R.id.array_wheel);

        arrayWheel.setShadowColor(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
        arrayWheel.setWheelBackground(R.color.colorWhite);
        arrayWheel.setBackgroundColor(Color.WHITE);
        arrayWheel.setCenterRectOffset((int) dm.density);

        adapter = new ArrayWheelAdapter<>(mContext, strs);
        arrayWheel.setViewAdapter(adapter);
        if(selectItem<0) {
            arrayWheel.setCurrentItem(strs.length / 2);
            adapter.setSelectedItem(strs.length / 2);
        }else{
            arrayWheel.setCurrentItem(selectItem);
            adapter.setSelectedItem(selectItem);
        }

        arrayWheel.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                mHandler.sendEmptyMessage(MSG_SOUND_EFFECT);
                adapter.setSelectedItem(newValue);
                wheel.invalidateWheel(false);
                if (onPickerArrayListener != null) {
                    onPickerArrayListener.onPickArray(newValue);
                }
            }
        });
    }

    private void playSoundEffect() {
        sp.play(soundId, 0.5f, 0.5f, 0, 0, 1.0f);
    }

    public void setData(String[] strs) {
        this.strs = strs;
        adapter = new ArrayWheelAdapter<>(mContext, strs);
        arrayWheel.setViewAdapter(adapter);
        arrayWheel.setCurrentItem(strs.length / 2);
        adapter.setSelectedItem(strs.length / 2);
    }

    public interface OnPickerArrayListener{
        public void onPickArray(int position);
    }

    public void setSelect(int selectItem){
        this.selectItem=selectItem;
        arrayWheel.setCurrentItem(selectItem);
        adapter.setSelectedItem(selectItem);
    }

    public void setOnPickerArrayListener(OnPickerArrayListener onPickerArrayListener) {
        this.onPickerArrayListener = onPickerArrayListener;
    }

    public int getSelectedItem() {
        return arrayWheel.getCurrentItem();
    }
}
