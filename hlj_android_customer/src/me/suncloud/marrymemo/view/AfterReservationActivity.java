package me.suncloud.marrymemo.view;

import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;

import static me.suncloud.marrymemo.R.id.tv_merchant_name;

public class AfterReservationActivity extends HljBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_reservation);

        String logoPath = getIntent().getStringExtra("logo_path");
        String name = getIntent().getStringExtra("name");

        Point point = JSONUtil.getDeviceSize(this);
        int logoSize = Math.round(point.x * 37 / 160);

        ImageView logoImg = (ImageView) findViewById(R.id.img_logo);
        TextView merchantNameTv = (TextView) findViewById(tv_merchant_name);

        ViewGroup.MarginLayoutParams params;
        params = (ViewGroup.MarginLayoutParams) logoImg.getLayoutParams();
        params.width = logoSize;
        params.height = logoSize;

        String url = JSONUtil.getImagePath2(logoPath, logoSize);
        if (!JSONUtil.isEmpty(url)) {
            logoImg.setTag(url);
            ImageLoadTask task = new ImageLoadTask(logoImg, null, 0);
            AsyncBitmapDrawable image = new AsyncBitmapDrawable(getResources(),
                    R.mipmap.icon_empty_image,
                    task);
            task.loadImage(url, logoSize, ScaleMode.WIDTH, image);
        } else {
            logoImg.setImageBitmap(null);
        }

        merchantNameTv.setText(name);
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
