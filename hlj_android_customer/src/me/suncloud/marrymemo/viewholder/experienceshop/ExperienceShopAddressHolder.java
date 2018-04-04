package me.suncloud.marrymemo.viewholder.experienceshop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.amap.api.maps.model.LatLng;
import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljmaplibrary.HljMap;
import com.hunliji.hljmaplibrary.views.activities.NavigateMapActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * experience_shop_item_address
 * Created by jinxin on 2017/3/24 0024.
 */

public class ExperienceShopAddressHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.map_view)
    public ImageView mapView;

    private Context mContext;
    private String latLng;
    private String name;
    private String address;
    private String lat;
    private String lng;

    private int width;
    private int height;

    public ExperienceShopAddressHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);

        width = CommonUtil.getDeviceSize(mContext).x - CommonUtil.dp2px(mContext, 32);
        height = Math.round((CommonUtil.getDeviceSize(mContext).x - CommonUtil.dp2px(mContext,
                32)) / 2);
        mapView.getLayoutParams().height = height;
        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(latLng)) {
                    return;
                }
                String[] addressArray = latLng.split(",");
                if (addressArray.length < 2) {
                    return;
                }
                lat = addressArray[1];
                lng = addressArray[0];

                LatLng point = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
                Intent intent = new Intent(mContext, NavigateMapActivity.class);
                intent.putExtra(NavigateMapActivity.ARG_LATITUDE, point.latitude);
                intent.putExtra(NavigateMapActivity.ARG_LONGITUDE, point.longitude);
                intent.putExtra(NavigateMapActivity.ARG_TITLE, name);
                intent.putExtra(NavigateMapActivity.ARG_ADDRESS,
                        ExperienceShopAddressHolder.this.address);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_up_to_top,
                        R.anim.activity_anim_default);
            }
        });
    }

    public void setAddress(String latLng, String name, String address) {
        this.latLng = latLng;
        this.name = name;
        this.address = address;

        String[] addressArray = latLng.split(",");
        if (addressArray.length < 2) {
            return;
        }
        lat = addressArray[1];
        lng = addressArray[0];

        LatLng point = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
        Glide.with(mContext)
                .load(HljMap.getAMapUrl(point.longitude,
                        point.latitude,
                        width,
                        height,
                        15,
                        HljCommon.MARKER_ICON_RED))
                .into(mapView);
    }
}
