package me.suncloud.marrymemo.adpter.merchant.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * Created by wangtao on 2017/10/10.
 */

public class HotelMenuNameViewHolder extends BaseViewHolder<String> {


    @BindView(R.id.tv_name)
    TextView tvName;

    public HotelMenuNameViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hotel_menu_name, parent, false));
    }

    private HotelMenuNameViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    protected void setViewData(
            Context mContext, String item, int position, int viewType) {
        tvName.setText(item);
    }
}
