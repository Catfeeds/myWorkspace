package me.suncloud.marrymemo.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.hunliji.headerview.StickyListHeadersAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.widget.NewSideBar;

/**
 * Created by Suncloud on 2015/10/31.
 */
public class CityListAdapter extends BaseAdapter implements SectionIndexer,
        StickyListHeadersAdapter {

    //    private ArrayList<City> cities;
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Map.Entry<String, List<City>>> cities;
    private ArrayList<City> searchCity;
    private NewSideBar sideBar;
    private ArrayList<String> sections;
    private String searchText;
    private OnCityClickListener onCityClickListener;

    public CityListAdapter(
            Context context,
            NewSideBar sideBar,
            OnCityClickListener onCityClickListener) {
        this.sideBar = sideBar;
        this.sideBar.setAdapter(this);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.sections = new ArrayList<>();
        this.cities = new ArrayList<>();
        this.searchCity = new ArrayList<>();
        this.onCityClickListener = onCityClickListener;
    }

    public void setCities(List<Map.Entry<String, List<City>>> cities) {
        if (cities != null) {
            this.cities.clear();
            this.cities.addAll(cities);
            notifyDataSetChanged();
            sideBar.setL(getSections());
        }
    }

    public void setSearchText(String text) {
        this.searchText = text;
        this.searchCity.clear();
        if (!JSONUtil.isEmpty(searchText)) {
            for (Map.Entry<String, List<City>> entry : cities) {
                for (City city : entry.getValue()) {
                    if (!JSONUtil.isEmpty(city.getShortPy()) && city.getShortPy()
                            .toLowerCase()
                            .startsWith(searchText.toLowerCase())) {
                        searchCity.add(city);
                    } else if (!JSONUtil.isEmpty(city.getPingying()) && city.getPingying()
                            .toLowerCase()
                            .startsWith(searchText.toLowerCase())) {
                        searchCity.add(city);
                    } else if (!JSONUtil.isEmpty(city.getName()) && city.getName()
                            .contains(searchText)) {
                        searchCity.add(city);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.city_letter_header, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.letter);
        if (JSONUtil.isEmpty(searchText)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(getItem(position).getLetter());
        } else {
            textView.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return getItem(position).getLetterId();
    }

    @Override
    public int getCount() {
        if (!JSONUtil.isEmpty(searchText)) {
            return searchCity.size();
        }
        int count = 0;
        for (Map.Entry<String, List<City>> entry : cities) {
            count += entry.getValue()
                    .size();
        }
        return count;
    }

    @Override
    public City getItem(int position) {
        if (!JSONUtil.isEmpty(searchText)) {
            return searchCity.isEmpty() ? new City() : searchCity.get(position);
        }
        for (Map.Entry<String, List<City>> entry : cities) {
            List<City> cities = entry.getValue();
            if (cities.size() > position) {
                return cities.get(position);
            } else {
                position -= cities.size();
            }
        }
        return new City();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.city_list_item2, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.cityName = (TextView) convertView.findViewById(R.id.city_name);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        final City city = getItem(position);
        holder.cityName.setText(getItem(position).getName());
        if (onCityClickListener != null) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCityClickListener.onCityClick(city);
                }
            });
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView cityName;
    }


    @Override
    public String[] getSections() {
        sections = new ArrayList<>();
        sections.add(context.getString(R.string.label_hot2));
        for (Map.Entry<String, List<City>> entry : cities) {
            sections.add(entry.getKey());
        }
        return sections.toArray(new String[sections.size()]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        int position = 0;
        if (sectionIndex > 1) {
            for (int i = 0; i < sectionIndex - 1; i++) {
                position += cities.get(i)
                        .getValue()
                        .size();
            }
        }
        return position;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }


    public interface OnCityClickListener {
        void onCityClick(City city);
    }

}
