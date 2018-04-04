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
 * Created by wangtao on 2017/10/10.
 */

public class HotelHallViewHolder extends BaseViewHolder<HotelHall> {



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

    private int width;
    private int height;

    private long merchantId;

    private OnItemClickListener onItemClickListener;

    public HotelHallViewHolder(ViewGroup parent, OnItemClickListener onItemClickListener, long merchantId) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hotel_hall_item, parent, false));
        this.onItemClickListener=onItemClickListener;
        this.merchantId=merchantId;
    }

    private HotelHallViewHolder(View itemView) {
        super(itemView);
        width = CommonUtil.getDeviceSize(itemView.getContext()).x;
        height = Math.round(width*5/8);
        ButterKnife.bind(this, itemView);
        ivCover.getLayoutParams().height=height;
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
