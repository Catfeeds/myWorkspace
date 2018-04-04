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
 * Created by werther on 15/8/10.
 */
public class AddressAreaPickerView extends FrameLayout {

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
    private WheelView areaWheel;
    private boolean scrolling;
    private ArrayWheelAdapter<String> provinceAdapter;
    private ArrayWheelAdapter<String> cityAdapter;
    private ArrayWheelAdapter<String> areaAdapter;
    private LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> areaMap;
    private OnPickerAreaListener onPickerAreaListener;

    public AddressAreaPickerView(Context context) {
        super(context);
        init(context);
    }

    public AddressAreaPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AddressAreaPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        dm = getResources().getDisplayMetrics();
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundId = sp.load(mContext, R.raw.time_picker, 1);

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        layoutInflater.inflate(R.layout.areas_picker, this, true);

        provinceWheel = (WheelView) findViewById(R.id.province);
        cityWheel = (WheelView) findViewById(R.id.city);
        areaWheel = (WheelView) findViewById(R.id.area);
        provinceWheel.setShadowColor(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
        cityWheel.setShadowColor(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
        areaWheel.setShadowColor(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
        provinceWheel.setWheelBackground(R.color.colorWhite);
        cityWheel.setWheelBackground(R.color.colorWhite);
        areaWheel.setWheelBackground(R.color.colorWhite);
        provinceWheel.setBackgroundColor(Color.WHITE);
        cityWheel.setBackgroundColor(Color.WHITE);
        areaWheel.setBackgroundColor(Color.WHITE);
        provinceWheel.setCenterRectOffset((int) dm.density);
        cityWheel.setCenterRectOffset((int) dm.density);
        areaWheel.setCenterRectOffset((int) dm.density);

        String[] cities = {" ", " "};
        LinkedHashMap<String, ArrayList<String>> cityMap = new LinkedHashMap<>();
        cityMap.put(" ", new ArrayList<>(Arrays.asList(cities)));
        areaMap = new LinkedHashMap<>();
        areaMap.put(" ", cityMap);

        final String[] provinces = areaMap.keySet().toArray(new String[0]);
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
                if (!scrolling) {
                    updateAreas(areaWheel, provinceWheel.getCurrentItem(), newValue);
                }

                mHandler.sendEmptyMessage(MSG_SOUND_EFFECT);
            }
        });

        cityWheel.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                scrolling = true;
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                scrolling = false;
                updateAreas(areaWheel, provinceWheel.getCurrentItem(), cityWheel.getCurrentItem());
            }
        });

        areaWheel.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                areaAdapter.setSelectedItem(newValue);
                wheel.invalidateWheel(false);
                mHandler.sendEmptyMessage(MSG_SOUND_EFFECT);
                if (onPickerAreaListener != null) {
                    onPickerAreaListener.onPickerArea(provinceWheel.getCurrentItem(), cityWheel
                            .getCurrentItem(), areaWheel.getCurrentItem());
                }
            }
        });
    }

    private void updateCities(WheelView cityWheel, int index) {
        String[] provinces = areaMap.keySet().toArray(new String[0]);
        LinkedHashMap<String, ArrayList<String>> map = areaMap.get(provinces[index]);
        String[] cities = map.keySet().toArray(new String[0]);
        cityAdapter = new ArrayWheelAdapter<>(mContext, cities);
        cityWheel.setViewAdapter(cityAdapter);
        cityWheel.setCurrentItem(cities.length / 2);
        cityAdapter.setSelectedItem(cities.length / 2);
        updateAreas(areaWheel, provinceWheel.getCurrentItem(), cityWheel.getCurrentItem());
    }

    private void updateAreas(WheelView areaWheel, int provinceIndex, int areaIndex) {
        try{
            String[] provinces = areaMap.keySet().toArray(new String[0]);
            LinkedHashMap<String, ArrayList<String>> map = areaMap.get(provinces[provinceIndex]);
            String[] cities = map.keySet().toArray(new String[0]);
            ArrayList<String> areaList = map.get(cities[areaIndex]);

            String[] areas = areaList.toArray(new String[0]);

            areaAdapter = new ArrayWheelAdapter<>(mContext, areas);
            areaWheel.setViewAdapter(areaAdapter);
            areaWheel.setCurrentItem(areas.length / 2);
            areaAdapter.setSelectedItem(areas.length / 2);
        }catch (ArrayIndexOutOfBoundsException ignored){

        }
    }

    private void playSoundEffect() {
        sp.play(soundId, 0.5f, 0.5f, 0, 0, 1.0f);
    }

    public interface OnPickerAreaListener {
        public void onPickerArea(int index1, int index2, int index3);
    }

    public int[] getSelectedItemIndexs() {
        int[] selected = {provinceWheel.getCurrentItem(), cityWheel.getCurrentItem(), areaWheel
                .getCurrentItem()};
        return selected;
    }

    public void setOnPickerAreaListener(OnPickerAreaListener pickerAreaListener) {
        this.onPickerAreaListener = pickerAreaListener;
    }

    public void setAreaMap(LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>>
                                   areaMap) {
        this.areaMap = areaMap;
        String[] provinces = areaMap.keySet().toArray(new String[0]);
        provinceAdapter = new ArrayWheelAdapter<>(mContext, provinces);
        provinceWheel.setViewAdapter(provinceAdapter);
        provinceWheel.setCurrentItem(provinces.length / 2);
        provinceAdapter.setSelectedItem(provinces.length / 2);

        updateCities(cityWheel, provinceWheel.getCurrentItem());
    }
}
