package me.suncloud.marrymemo.adpter.filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Label;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.suncloud.marrymemo.R;

/**
 * Created by hua_rong on 2018/3/13
 * 新版的 商家经营范围properties筛选器的adapter
 * 暂时增加部分方法 后续需要在自行增加
 */
public class NewLabelFilterAdapter extends BaseAdapter {

    private List<? extends Label> mData;
    private Context mContext;
    private int mWidth;
    private HashMap<Integer, Boolean> hashMap = new HashMap<>();
    private boolean multiSelected = false;//支持多选

    public NewLabelFilterAdapter(ArrayList<? extends Label> mData, Context mContext, int width) {
        this.mData = mData;
        this.mContext = mContext;
        this.mWidth = width;
        resetDefault();
    }

    public void clearHashMap() {
        for (int i = 0; i < mData.size(); i++) {
            hashMap.put(i, false);
        }
    }

    /**
     * 重置为默认选中
     */
    public void resetDefault() {
        for (int i = 0; i < mData.size(); i++) {
            hashMap.put(i, false);  // 重置为默认
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
            holder.keyWord = convertView.findViewById(R.id.key_word);
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
                if (hashMap.get(position)) {
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

    public List<? extends Label> getChecked() {
        List<Label> sl = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            if (hashMap.get(i)) {
                sl.add(mData.get(i));
            }
        }
        return sl;
    }

    public void setChecked(Label property) {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i)
                    .getId() == property.getId()) {
                clearHashMap();
                hashMap.put(i, true);
                notifyDataSetChanged();
                break;
            }
        }
    }
}

