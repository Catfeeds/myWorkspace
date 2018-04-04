package me.suncloud.marrymemo.adpter.merchant.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.merchant.HotelHall;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.HotelCalendarActivity;

/**
 * Created by wangtao on 2017/9/30.
 */

public class MerchantHomeHotelHallViewHolder extends BaseViewHolder<HotelHall> {


    @BindView(R.id.iv_cover)
    ImageView ivCover;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_table_count)
    TextView tvTableCount;
    @BindView(R.id.tv_area)
    TextView tvArea;
    @BindView(R.id.btn_schedule)
    Button btnSchedule;
    @BindView(R.id.line)
    View line;

    private int width;
    private int height;

    private long merchantId;

    private OnItemClickListener onItemClickListener;

    public MerchantHomeHotelHallViewHolder(ViewGroup parent,OnItemClickListener onItemClickListener,long merchantId) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.merchant_home_hotel_hall_item, parent, false));
        this.onItemClickListener=onItemClickListener;
        this.merchantId=merchantId;
    }

    private MerchantHomeHotelHallViewHolder(View itemView) {
        super(itemView);
        width = CommonUtil.dp2px(itemView.getContext(), 126);
        height = CommonUtil.dp2px(itemView.getContext(), 74);
        ButterKnife.bind(this, itemView);
        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AuthUtil.loginBindCheck(view.getContext())) {
                    return;
                }
                Intent intent = new Intent(view.getContext(), HotelCalendarActivity.class);
                intent.putExtra("id", merchantId);
                intent.putExtra("hall_id", getItem().getId());
                view.getContext().startActivity(intent);
            }
        });
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
            Context mContext, HotelHall hall, int position, int viewType) {
        line.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
        Glide.with(ivCover)
                .load(ImagePath.buildPath(hall.getCoverUrl())
                        .width(width)
                        .height(height)
                        .cropPath())
                .into(ivCover);
        tvName.setText(hall.getName());
        tvTableCount.setText(String.valueOf(hall.getMaxTableNum()));
        tvArea.setText(tvArea.getContext()
                .getString(R.string.label_hall_area2, Util.formatDouble2String(hall.getArea())));
    }
}
