package me.suncloud.marrymemo.adpter.merchant.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.merchant.HotelMenu;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * Created by wangtao on 2017/9/30.
 */

public class MerchantHomeHotelMenuViewHolder extends BaseViewHolder<HotelMenu> {


    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.iv_arrow)
    ImageView ivArrow;
    @BindView(R.id.line)
    View line;

    private OnItemClickListener onItemClickListener;

    public MerchantHomeHotelMenuViewHolder(ViewGroup parent,OnItemClickListener onItemClickListener) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.merchant_home_hotel_menu_item, parent, false));
        this.onItemClickListener=onItemClickListener;
    }


    private MerchantHomeHotelMenuViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClick(getItemPosition(),getItem());
                }
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, HotelMenu menu, int position, int viewType) {
        line.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        tvName.setText(menu.getName());
        tvPrice.setText(tvPrice.getContext()
                .getString(R.string.label_hotel_price,
                        NumberFormatUtil.formatDouble2String(menu.getPrice())));
    }
}
