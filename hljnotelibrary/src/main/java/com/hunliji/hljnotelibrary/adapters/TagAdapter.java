package com.hunliji.hljnotelibrary.adapters;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljnotelibrary.interfaces.ITagView;

public abstract class TagAdapter {

    private DataSetObservable mObservable = new DataSetObservable();

    public abstract int getCount();

    public abstract ITagView getItem(int position);

    public ITagView instantiateItem(ViewGroup container, int position) {
        ITagView tagView = getItem(position);
        container.addView((View) tagView);
        return tagView;
    }

    public boolean isViewFromObject(View view, Object object) {
        return object.equals(view);
    }

    public void destroyItem(ViewGroup container, int position, ITagView object) {

    }

    public int getItemId(int position) {
        return position;
    }

    public void notifyDataSetChanged() {
        mObservable.notifyChanged();
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        mObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mObservable.unregisterObserver(observer);
    }

}