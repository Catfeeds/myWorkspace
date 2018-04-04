package com.hunliji.marrybiz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hunliji.marrybiz.R;

import java.util.ArrayList;


/**
 * Created by Suncloud on 2014/11/25.
 */
public class ContactsAdapter extends BaseAdapter {

    private ArrayList<String> mData;
    private Context mContext;

    public ContactsAdapter(Context mContext, ArrayList<String> phones) {
        this.mContext = mContext;
        this.mData = phones;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public String getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.contact_list_item, viewGroup,
                    false);
            ViewHolder holder = new ViewHolder();
            holder.phoneView = (TextView) view.findViewById(R.id.contact_phone);
            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder != null) {
            holder.phoneView.setText(getItem(i));
        }
        return view;
    }

    private class ViewHolder {
        TextView phoneView;
    }
}
