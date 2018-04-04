package me.suncloud.marrymemo.view.experience;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljmaplibrary.HljMap;
import com.hunliji.hljmaplibrary.views.activities.NavigateMapActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.experience.Store;

/**
 * 体验店预约到店成功
 * Created by jinxin on 2016/11/17 0017.
 */

public class ExperienceShopReservationSucceedActivity extends HljBaseActivity {
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.map_view)
    ImageView mapView;
    @BindView(R.id.tv_phone)
    TextView tvPhone;

    private Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        store = getIntent().getParcelableExtra("store");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_reservation_succeed);
        ButterKnife.bind(this);
        initMapView();
    }

    private void initMapView() {
        int imgWidth = (int) (CommonUtil.getDeviceSize(this).x * 1.0f * 26 / (26 + 6));
        int imgHeight = Math.round(imgWidth * 1.0f / 2);

        if (store != null) {
            tvAddress.setText(store.getAddress());
            tvPhone.setText(store.getContactPhone());
            String latLng = store.getLocation();
            String[] addressArray = latLng.split(",");
            if (addressArray.length < 2) {
                return;
            }
            String lat = addressArray[1];
            String lng = addressArray[0];

            LatLng point = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
            Glide.with(this)
                    .load(HljMap.getAMapUrl(point.longitude,
                            point.latitude,
                            imgWidth,
                            imgHeight,
                            15,
                            HljCommon.MARKER_ICON_RED))
                    .into(mapView);
        }
    }

    @OnClick(R.id.tv_phone)
    void onCall() {
        if (store != null) {
            if (!TextUtils.isEmpty(store.getContactPhone())) {
                callUp(Uri.parse("tel:" + store.getContactPhone()));
            }
        }
    }

    @OnClick({R.id.layout_location, R.id.map_view})
    void onAddress() {
        if (store == null) {
            return;
        }
        String latLng = store.getLocation();
        if (TextUtils.isEmpty(latLng)) {
            return;
        }
        String[] address = latLng.split(",");
        if (address.length < 2) {
            return;
        }
        String lat = address[1];
        String lng = address[0];
        LatLng point = new LatLng(Double.valueOf(lat), Double.valueOf(lng));

        Intent intent = new Intent(this, NavigateMapActivity.class);
        intent.putExtra(NavigateMapActivity.ARG_LATITUDE, point.latitude);
        intent.putExtra(NavigateMapActivity.ARG_LONGITUDE, point.longitude);
        intent.putExtra(NavigateMapActivity.ARG_TITLE, store.getName());
        intent.putExtra(NavigateMapActivity.ARG_ADDRESS, store.getAddress());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }
}
