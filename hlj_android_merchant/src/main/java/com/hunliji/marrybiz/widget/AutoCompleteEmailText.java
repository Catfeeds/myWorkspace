package com.hunliji.marrybiz.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.hunliji.marrybiz.R;

import java.util.ArrayList;

/**
 * Created by luohanlin on 15/4/1.
 */
public class AutoCompleteEmailText extends AutoCompleteTextView {

    private static final String[] emailSuffix = {"@qq.com", "@163.com", "@sina.com",
            "@yahoo.com.cn", "@hotmail.com", "@gmail.com", "@126.com", "@sohu.com", "@yahoo.com",
            "@tom.com", "@21cn.com", "@foxmail.com", "@139.com", "@yeah.net", "@vip.qq.com",
            "@vip.sina.com"};

    public AutoCompleteEmailText(Context context) {
        super(context);
        init(context);
    }

    public AutoCompleteEmailText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AutoCompleteEmailText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        final AutoAdapter adapter = new AutoAdapter(context);
        setAdapter(adapter);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                adapter.mList.clear();
                if (input.length() > 0) {
                    for (int i = 0; i < emailSuffix.length; i++) {
                        adapter.mList.add(input + emailSuffix[i]);
                    }
                }
                adapter.notifyDataSetChanged();
                showDropDown();
            }
        });

        // 默认2个字符后才开始检测, 改为1
        setThreshold(1);
    }

    class AutoAdapter extends BaseAdapter implements Filterable {

        ArrayList<String> mList;
        private Context mContext;
        private AutoFilter mFilter;

        private AutoAdapter(Context mContext) {
            this.mList = new ArrayList<>();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList == null ? null : mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                TextView tv = new TextView(mContext);
                tv.setTextColor(getResources().getColor(R.color.colorBlack2));
                tv.setPadding(4, 4, 4, 4);
                tv.setTextSize(16);

                convertView = tv;
            }

            TextView txt = (TextView) convertView;
            txt.setText(mList.get(position));

            return convertView;
        }

        @Override
        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new AutoFilter();
            }
            return mFilter;
        }

        private class AutoFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (mList == null) {
                    mList = new ArrayList<>();
                }

                results.values = mList;
                results.count = mList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }

            }
        }
    }
}
