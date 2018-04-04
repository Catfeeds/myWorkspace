package com.hunliji.marrybiz.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.LineChartItem;
import com.hunliji.marrybiz.util.JSONUtil;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by luohanlin on 15/3/13.
 */
public class DataAllFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<ListView> {

    private View rootView;
    private PullToRefreshListView listView;
    private View progressBar;
    private ArrayList<LineChartItem> items;
    private boolean isLoad;
    private ChartDataAdapter adapter;


    public static final String CHANNELS_COUNT = "channels_count";
    public static final String SHARE_COUNT = "share_count";
    public static final String PHONES_COUNT = "phones_count";
    public static final String PV = "pv";
    public static final String COLLECTORS_COUNT = "collectors_count";

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM-dd");

    private View footView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items = new ArrayList<>();

        footView = getActivity().getLayoutInflater().inflate(R.layout.empty_placeholder3, null);
        adapter = new ChartDataAdapter(getActivity(), items);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        listView = (PullToRefreshListView) rootView.findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.getRefreshableView().addFooterView(footView);
        listView.setOnRefreshListener(this);
        progressBar = rootView.findViewById(R.id.progressBar);
        if (items.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            if (!isLoad) {
                new GetAllDataSetTask().executeOnExecutor(Constants.LISTTHEADPOOL);
            }
        }

        return rootView;
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        progressBar.setVisibility(View.VISIBLE);
        if (!isLoad) {
            new GetAllDataSetTask().executeOnExecutor(Constants.LISTTHEADPOOL);
        }
    }

    private class ChartDataAdapter extends ArrayAdapter<LineChartItem> {

        public ChartDataAdapter(Context context, List<LineChartItem> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }

    private class GetAllDataSetTask extends AsyncTask<String, Integer, JSONObject>{

        @Override
        protected JSONObject doInBackground(String... params) {
            isLoad = true;
            String url = Constants.getAbsUrl(Constants.HttpPath.GET_DATA_STATISTICS);
            try {
                String json = JSONUtil.getStringFromUrl(getActivity(), url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(jsonObject);
            listView.onRefreshComplete();

            if (jsonObject != null) {
                items.clear();
                listView.onRefreshComplete();
                if (jsonObject.isNull(PV)) {
                    items.add(new LineChartItem(generateDataLine(0, null,
                            getString(R.string.label_pv_statics), getResources().getColor(R.color
                                    .color_orange)), getActivity(),
                            getString(R.string.label_pv_statics),
                            getString(R.string.label_no_data_statics), true));
                } else {
                    Map<String, Float> pvMap = new HashMap<>();
                    JSONObject pvJSON = jsonObject.optJSONObject(PV);

                    Iterator<String> keys = pvJSON.keys();

                    int keysCount = 0;
                    while (keys.hasNext()) {
                        keysCount++;
                        String key = keys.next();
                        float value = (float) pvJSON.optDouble(key, 0);

                        pvMap.put(key, value);
                    }

                    items.add(new LineChartItem(generateDataLine(keysCount, pvMap,
                            getString(R.string.label_pv_statics), getResources().getColor(R.color
                                    .color_orange)), getActivity(),
                            getString(R.string.label_pv_statics), "", false));

                }

                // 暂时隐藏出访问量之外的统计数据

//                if (jsonObject.isNull(PHONES_COUNT)) {
//                    items.add(new LineChartItem(generateDataLine(0, null,
//                            getString(R.string.label_phones_count),
//                            getResources().getColor(R.color.color_green2)), getActivity(),
//                            getString(R.string.label_phones_count),
//                            getString(R.string.label_no_data_statics), true));
//                } else {
//                    Map<String, Float> phonesMap = new HashMap<>();
//                    JSONObject phonesJSON = jsonObject.optJSONObject(PHONES_COUNT);
//
//                    Iterator<String> keys = phonesJSON.keys();
//
//                    int keysCount = 0;
//                    while (keys.hasNext()) {
//                        keysCount++;
//                        String key = keys.next();
//                        float value = (float) phonesJSON.optDouble(key, 0);
//
//                        phonesMap.put(key, value);
//                    }
//
//                    items.add(new LineChartItem(generateDataLine(keysCount, phonesMap,
//                            getString(R.string.label_phones_count),
//                            getResources().getColor(R.color.color_green2)), getActivity(),
//                            getString(R.string.label_phones_count), "", false));
//
//                }
//
//                if (jsonObject.isNull(CHANNELS_COUNT)) {
//                    items.add(new LineChartItem(generateDataLine(0, null,
//                            getString(R.string.label_channels_count),
//                            getResources().getColor(R.color.color_blue3)), getActivity(),
//                            getString(R.string.label_channels_count),
//                            getString(R.string.label_no_data_statics), true));
//                } else {
//                    Map<String, Float> channelsMap = new HashMap<>();
//                    JSONObject channelsJSON = jsonObject.optJSONObject(CHANNELS_COUNT);
//
//                    Iterator<String> keys = channelsJSON.keys();
//
//                    int keysCount = 0;
//                    while (keys.hasNext()) {
//                        keysCount++;
//                        String key = keys.next();
//                        float value = (float) channelsJSON.optDouble(key, 0);
//
//                        channelsMap.put(key, value);
//                    }
//
//                    items.add(new LineChartItem(generateDataLine(keysCount, channelsMap,
//                            getString(R.string.label_channels_count),
//                            getResources().getColor(R.color.color_blue3)), getActivity(),
//                            getString(R.string.label_channels_count), "", false));
//
//                }
//
//                if (jsonObject.isNull(COLLECTORS_COUNT)) {
//                    items.add(new LineChartItem(generateDataLine(0, null,
//                            getString(R.string.label_collects_count),
//                            getResources().getColor(R.color.color_purple)), getActivity(),
//                            getString(R.string.label_collects_count),
//                            getString(R.string.label_no_data_statics), true));
//                } else {
//                    Map<String, Float> collectMap = new HashMap<>();
//                    JSONObject collectJSON = jsonObject.optJSONObject(COLLECTORS_COUNT);
//
//                    Iterator<String> keys = collectJSON.keys();
//
//                    int keysCount = 0;
//                    while (keys.hasNext()) {
//                        keysCount++;
//                        String key = keys.next();
//                        float value = (float) collectJSON.optDouble(key, 0);
//
//                        collectMap.put(key, value);
//                    }
//
//                    items.add(new LineChartItem(generateDataLine(keysCount, collectMap,
//                            getString(R.string.label_collects_count),
//                            getResources().getColor(R.color.color_purple)), getActivity(),
//                            getString(R.string.label_collects_count), "", false));
//
//                }

                adapter.notifyDataSetChanged();

                listView.invalidate();
            }

            if (items.isEmpty()) {
                View emptyView = listView.getRefreshableView().getEmptyView();

                if (emptyView == null) {
                    emptyView = rootView.findViewById(R.id.empty_hint_layout);
                    listView.getRefreshableView().setEmptyView(emptyView);
                }

                ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                        .img_empty_list_hint);
                TextView emptyHintTextView = (TextView) emptyView.findViewById(R.id.empty_hint);
                imgEmptyHint.setVisibility(View.VISIBLE);
                emptyHintTextView.setVisibility(View.VISIBLE);
                if (JSONUtil.isNetworkConnected(getActivity())) {
                    imgEmptyHint.setImageResource(R.mipmap.icon_empty_order);
                    emptyHintTextView.setText(getString(R.string.label_no_data_statics));
                } else {
                    imgEmptyHint.setVisibility(View.GONE);
                    emptyHintTextView.setText(R.string.net_disconnected);
                }
            }

            isLoad = false;
        }
    }

    private LineData generateDataLine(int cnt, Map<String, Float> map, String type, int color) {
        ArrayList<Entry> e = new ArrayList<>();

        if (cnt == 0) {
            // 没有数据,所有的数据置为零
            for (int i = 0; i < 30; i++) {
                e.add(new Entry((int) (Math.random() * 65) + 40, i));
            }
        } else {
            // 有一部分数据, 按照日期推算加入数据中
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -31);

            for (int i = 0; i < 30; i++) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                String date = dateFormat.format(calendar.getTime());
                if (map.containsKey(date)) {
                    e.add(new Entry(map.get(date), i));
                } else {
                    e.add(new Entry(0, i));
                }
            }
        }

        LineDataSet d = new LineDataSet(e, type);
        Utils.init(getResources());
        d.setDrawCubic(false);
        d.setLineWidth(2.0f);
        d.setCircleSize(3.0f);
        d.setHighLightColor(getResources().getColor(R.color.colorPrimary));
        d.setColor(color);
        d.setCircleColor(color);
        d.setDrawValues(true);

        ArrayList<LineDataSet> sets = new ArrayList<>();
        sets.add(d);

        LineData cd = new LineData(getDays());
        return cd;
    }

    private ArrayList<String> getDays() {
        ArrayList<String> m = new ArrayList<>();

        // 从当天日期往前推30天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -31);
        for (int i = 0; i < 30; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            m.add(dateFormat2.format(calendar.getTime()));
        }

        return m;
    }

}
