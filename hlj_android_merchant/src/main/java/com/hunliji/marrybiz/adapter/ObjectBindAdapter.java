/**
 *
 */
package com.hunliji.marrybiz.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.hunliji.marrybiz.model.Identifiable;

import java.util.List;

/**
 * @author iDay
 */
public class ObjectBindAdapter<T extends Identifiable> extends BaseAdapter implements Filterable {
    protected List<T> data;
    protected LayoutInflater inflater;
    protected int resource;
    protected ViewBinder<T> viewBinder;

    /**
     * @param context
     * @param list
     * @param resource
     * @param viewBinder
     */
    public ObjectBindAdapter(Context context, List<T> list, int resource,
                             ViewBinder<T> viewBinder) {
        super();
        this.data = list;
        this.inflater = LayoutInflater.from(context);
        this.resource = resource;
        this.viewBinder = viewBinder;
    }

    /**
     * @param context
     * @param list
     * @param resource
     */
    public ObjectBindAdapter(Context context, List<T> list, int resource) {
        this(context, list, resource, null);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (position >= data.size()) {
            return position;
        }
        return data.get(position).getId() == null ? position : data.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, resource);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent,
                                        int resource) {
        View v;
        if (convertView == null) {
            v = inflater.inflate(resource, parent, false);
        } else {
            v = convertView;
        }
        bindView(position, v);

        return v;
    }

    protected void bindView(int position, View view) {
        if (position >= data.size()) {
            return;
        }
        final T obj = data.get(position);
        if (obj == null) {
            return;
        }

        final ViewBinder<T> binder = viewBinder;

        if (binder != null) {
            binder.setViewValue(view, obj, position);
        }
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public interface ViewBinder<T extends Identifiable> {
        void setViewValue(View view, T t, int position);
    }

    /**
     * @return the viewBinder
     */
    public ViewBinder<T> getViewBinder() {
        return viewBinder;
    }

    /**
     * @param viewBinder the viewBinder to set
     */
    public void setViewBinder(ViewBinder<T> viewBinder) {
        this.viewBinder = viewBinder;
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }
}
