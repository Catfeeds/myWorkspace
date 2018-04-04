package me.suncloud.marrymemo.adpter.merchant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.merchant.HotelMenu;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;

import java.util.List;

import me.suncloud.marrymemo.R;

/**
 * Created by wangtao on 2017/9/7.
 */

public class HotelMenuPriceAdapter extends BaseAdapter {

    private List<HotelMenu> mData;

    public HotelMenuPriceAdapter(List<HotelMenu> mData) {
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public HotelMenu getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.hotel_menu_price_item, viewGroup, false);
        }
        TextView textView = view.findViewById(R.id.tv_price);
        textView.setText(textView.getContext()
                .getString(R.string.label_hotel_price2,
                        NumberFormatUtil.formatDouble2String(getItem(i).getPrice())));
        return view;
    }
}
