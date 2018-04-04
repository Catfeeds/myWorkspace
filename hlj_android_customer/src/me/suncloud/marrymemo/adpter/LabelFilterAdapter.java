package me.suncloud.marrymemo.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.search.MerchantFilterHotelTableLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Label;

/**
 * Created by mo_yu on 2016/11/15.
 * 商家经营范围properties筛选器的adapter
 */
public class LabelFilterAdapter extends BaseAdapter {

    private ArrayList<? extends Label> mData;
    private Context mContext;
    private int mWidth;
    private HashMap<Integer, Boolean> hashMap = new HashMap<>();
    private boolean hasDefault; // 是否初始一个默认选中，并且保证总有一个被选中
    private boolean multiSelected = false;//支持多选
    private OnKeyWordClickListener onKeyWordClickListener;

    public LabelFilterAdapter(ArrayList<? extends Label> mData, Context mContext, int width) {
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
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.filter_word_item, parent, false);
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
        if (hashMap.get(position)) {
            holder.keyWord.setPressed(true);
        } else {
            holder.keyWord.setPressed(false);
        }
        return convertView;
    }

    public void setMultiSelected(boolean multiSelected) {
        this.multiSelected = multiSelected;
    }

    private class ViewHolder {
        TextView keyWord;
    }

    public Label getCheckPosition() {
        for (int i = 0; i < mData.size(); i++) {
            if (hashMap.get(i)) {
                return mData.get(i);
            }
        }
        return null;
    }

    public MerchantFilterHotelTableLabel getCheckPositionMerchantLabel() {
        Label l = null;
        for (int i = 0; i < mData.size(); i++) {
            if (hashMap.get(i)) {
                l = mData.get(i);
            }
        }
        if (l != null) {
            MerchantFilterHotelTableLabel label = new MerchantFilterHotelTableLabel();
            label.setId(l.getId());
            label.setDesc(l.getDesc());
            label.setName(l.getName());
            label.setKeyWord(l.getKeyWord());
            label.setOrder(l.getOrder());
            label.setType(l.getType());
            return label;
        }

        return null;
    }

    public List<? extends Label> getChecked() {
        List<Label> sl = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            if (hashMap.get(i)) {
                sl.add(mData.get(i));
            }
        }
        return sl;
    }

    public void setChecked(MerchantFilterHotelTableLabel l) {
        Label label = new Label();
        label.setId(l.getId());
        label.setDesc(l.getDesc());
        label.setName(l.getName());
        label.setKeyWord(l.getKeyWord());
        label.setOrder(l.getOrder());
        label.setType(l.getType());
        setChecked(label);
    }

    public void setChecked(Label property) {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i)
                    .getId()
                    .equals(property.getId())) {
                clearHashMap();
                hashMap.put(i, true);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public interface OnKeyWordClickListener {
        void onKeyWorkClick(List<? extends Label> labels);
    }
}
