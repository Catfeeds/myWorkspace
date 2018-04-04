package com.hunliji.hljcarlibrary.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.Label;
import com.hunliji.hljcommonviewlibrary.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mo_yu on 2016/11/15.
 * 筛选器的adapter
 */
public class CarLabelFilterAdapter extends BaseAdapter {

    private ArrayList<Label> mData;
    private Context mContext;
    private int mWidth;
    private HashMap<Integer, Boolean> hashMap = new HashMap<>();
    private boolean hasDefault; // 是否初始一个默认选中，并且保证总有一个被选中
    private boolean multiSelected = false;//支持多选
    private OnKeyWordClickListener onKeyWordClickListener;

    public CarLabelFilterAdapter(
            ArrayList<Label> mData, Context mContext, int width) {
        this.mData = mData;
        this.mContext = mContext;
        this.mWidth = width;
        resetDefault();
    }

    public void setOnKeyWordClickListener(OnKeyWordClickListener onKeyWordClickListener) {
        this.onKeyWordClickListener = onKeyWordClickListener;
    }

    public void setHasDefault(boolean hasDefault) {
        this.hasDefault = hasDefault;
    }

    public void clearHashMap() {
        for (int i = 0; i < mData.size(); i++) {
            hashMap.put(i, false);
        }
    }

    /**
     * 展开 收起 调用这个方法 保存之前的状态
     */
    public void resetHashMap() {
        for (int i = 0, size = mData.size(); i < size; i++) {
            if (hashMap.containsKey(i)) {
                hashMap.put(i, hashMap.get(i));
            } else {
                hashMap.put(i, false);
            }
        }
    }

    /**
     * 重置为默认选中
     */
    public void resetDefault() {
        for (int i = 0; i < mData.size(); i++) {
            hashMap.put(i, false);  // 重置为默认
            if (mData.get(i)
                    .getId() < 0 && hasDefault) {
                hashMap.put(i, true);
            } else {
                hashMap.put(i, false);
            }
        }
    }

    /**
     * 重置回之前的状态
     */
    public void resetWithLabels(List<Label> labels) {
        if (hashMap == null || mData == null) {
            return;
        }
        hashMap.clear();
        for (int i = 0; i < mData.size(); i++) {
            Label label = mData.get(i);
            hashMap.put(i, false);
            for (Label selectLabel : labels) {
                if (label.getId() == selectLabel.getId()) {
                    hashMap.put(i, true);
                }
            }
        }
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData == null ? null : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mData.get(position)
                .getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.service_filter_word_item___cv, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.keyWord = (TextView) convertView.findViewById(R.id.key_word);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.keyWord
                    .getLayoutParams();
            params.width = mWidth;
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.keyWord.setText(mData.get(position)
                .getName());
        holder.keyWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hashMap.get(position) && !hasDefault) {
                    if (!multiSelected) {
                        clearHashMap();
                    } else {
                        hashMap.put(position, false);
                    }
                } else {
                    if (!multiSelected) {
                        clearHashMap();
                    }
                    hashMap.put(position, true);
                }
                if (onKeyWordClickListener != null) {
                    onKeyWordClickListener.onKeyWorkClick(getChecked());
                }
                notifyDataSetChanged();
            }
        });
        if (hashMap.size() > position && hashMap.get(position)) {
            holder.keyWord.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            holder.keyWord.setBackgroundResource(R.drawable.sp_r2_car_gold);
        } else {
            holder.keyWord.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack3));
            holder.keyWord.setBackgroundResource(R.drawable.sp_r2_stroke_gray_solid_f5f5f5);
        }
        return convertView;
    }

    public void setMultiSelected(boolean multiSelected) {
        this.multiSelected = multiSelected;
    }

    private class ViewHolder {
        TextView keyWord;
    }

    public Label getCheckItem() {
        for (int i = 0; i < mData.size(); i++) {
            if (hashMap.get(i)) {
                return mData.get(i);
            }
        }
        return null;
    }

    public List<Label> getChecked() {
        List<Label> sl = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            if (hashMap.get(i)) {
                sl.add(mData.get(i));
            }
        }
        return sl;
    }

    public void setChecked(Label label) {
        if (label == null) {
            return;
        }
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i)
                    .getId() == (label.getId())) {
                clearHashMap();
                hashMap.put(i, true);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void setChecked(int position) {
        for (int i = 0; i < mData.size(); i++) {
            if (i == position) {
                clearHashMap();
                hashMap.put(i, true);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public interface OnKeyWordClickListener {
        void onKeyWorkClick(List<Label> labels);
    }
}
