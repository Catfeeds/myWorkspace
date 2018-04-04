package me.suncloud.marrymemo.adpter;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.MerchantProperty;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * 买套餐页 已选择的类别adapter
 * Created by jinxin on 2016/12/9 0009.
 */

public class TagsAdapter extends BaseAdapter {
    private List<MerchantProperty> properties;
    private Context mContext;
    private int itemWidth;

    public TagsAdapter(Context mContext, List<MerchantProperty> ps) {
        this.mContext = mContext;
        properties = ps;
        Point point = JSONUtil.getDeviceSize(this.mContext);
        DisplayMetrics dm = this.mContext.getResources()
                .getDisplayMetrics();
        itemWidth = Math.round((point.x - dm.density * 10 * 5) / 4);
    }

    @Override
    public int getCount() {
        return properties == null ? 0 : properties.size();
    }

    @Override
    public Object getItem(int i) {
        return properties.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.buy_work_tags_item, viewGroup, false);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.tvName.getLayoutParams().width = itemWidth;
        MerchantProperty p = properties.get(i);
        if (p != null && p.getId() > 0) {
            holder.tvName.setText(p.getName());
        }
        return view;
    }

    class ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
