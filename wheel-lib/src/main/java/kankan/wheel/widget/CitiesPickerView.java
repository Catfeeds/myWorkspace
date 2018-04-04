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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import kankan.wheel.R;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

/**
 * Created by luohanlin on 15/4/1.
 */
public class CitiesPickerView extends FrameLayout {

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
    private int soundId;
    private WheelView provinceWheel;
    private WheelView cityWheel;
    private boolean scrolling;
    private LinkedHashMap<String, ArrayList<String>> cityMap;
    private ArrayWheelAdapter<String> provinceAdapter;
    private ArrayWheelAdapter<String> cityAdapter;
    private OnPickerCityListener onPickerCityListener;

    public CitiesPickerView(Context context) {
        super(context);
        init(context);
    }

    public CitiesPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CitiesPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        dm = getResources().getDisplayMetrics();
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundId = sp.load(mContext, R.raw.time_picker, 1);

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        layoutInflater.inflate(R.layout.cities_picker, this, true);

        provinceWheel = (WheelView) findViewById(R.id.province);
        cityWheel = (WheelView) findViewById(R.id.city);
        provinceWheel.setShadowColor(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
        cityWheel.setShadowColor(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
        provinceWheel.setWheelBackground(R.color.colorWhite);
        cityWheel.setWheelBackground(R.color.colorWhite);
        provinceWheel.setBackgroundColor(Color.WHITE);
        cityWheel.setBackgroundColor(Color.WHITE);
        provinceWheel.setCenterRectOffset((int) dm.density);
        cityWheel.setCenterRectOffset((int) dm.density);


        String[] cities = {"a", "b"};
        cityMap = new LinkedHashMap<>();
        cityMap.put("a", new ArrayList<>(Arrays.asList(cities)));

        String[] provinces = cityMap.keySet().toArray(new String[0]);
        provinceAdapter = new ArrayWheelAdapter<>(mContext, provinces);
        provinceWheel.setViewAdapter(provinceAdapter);
        provinceWheel.setCurrentItem(provinces.length / 2);
        provinceAdapter.setSelectedItem(provinces.length / 2);

        updateCities(cityWheel, provinceWheel.getCurrentItem());

        provinceWheel.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                provinceAdapter.setSelectedItem(newValue);
                wheel.invalidateWheel(false);
                if (!scrolling) {
                    updateCities(cityWheel, newValue);
                }
                mHandler.sendEmptyMessage(MSG_SOUND_EFFECT);
            }
        });

        provinceWheel.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                scrolling = true;
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                scrolling = false;
                updateCities(cityWheel, provinceWheel.getCurrentItem());
            }
        });

        cityWheel.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                cityAdapter.setSelectedItem(newValue);
                wheel.invalidateWheel(false);
                mHandler.sendEmptyMessage(MSG_SOUND_EFFECT);
                if (onPickerCityListener != null) {
                    onPickerCityListener.onPickCity(newValue, cityMap.get(provinceWheel
                            .getCurrentItem()).get(newValue));
                }
            }
        });
    }

    private void playSoundEffect() {
        sp.play(soundId, 0.5f, 0.5f, 0, 0, 1.0f);
    }

    private void updateCities(WheelView city, int index) {
        String[] provinces = cityMap.keySet().toArray(new String[0]);
        ArrayList<String> cityList = cityMap.get(provinces[index]);
        String[] cities = cityList.toArray(new String[0]);
        cityAdapter = new ArrayWheelAdapter<>(mContext, cities);
        city.setViewAdapter(cityAdapter);
        city.setCurrentItem(cities.length / 2);
        cityAdapter.setSelectedItem(cities.length / 2);
    }

    public interface OnPickerCityListener {
        public void onPickCity(int position, String city);
    }

    public void setOnPickerCityListener(OnPickerCityListener onPickerCityListener) {
        this.onPickerCityListener = onPickerCityListener;
    }

    public int[] getSelectedItemIndexs() {
        int[] selected = {provinceWheel.getCurrentItem(), cityWheel.getCurrentItem()};
        return selected;
    }

    public void setCityMap(LinkedHashMap<String, ArrayList<String>> cityMap) {
        this.cityMap = cityMap;
        String[] provinces = cityMap.keySet().toArray(new String[0]);
        provinceAdapter = new ArrayWheelAdapter<>(mContext, provinces);
        provinceWheel.setViewAdapter(provinceAdapter);
        provinceWheel.setCurrentItem(provinces.length / 2);
        provinceAdapter.setSelectedItem(provinces.length / 2);

        updateCities(cityWheel, provinceWheel.getCurrentItem());
    }
}
