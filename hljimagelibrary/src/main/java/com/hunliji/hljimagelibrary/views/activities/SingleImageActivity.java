package com.hunliji.hljimagelibrary.views.activities;

import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljimagelibrary.R;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.PageImageRequestListener;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Suncloud on 2016/10/18.
 */

public class SingleImageActivity extends HljBaseNoBarActivity {

    private PhotoView pvImage;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image___img);
        setSwipeBackEnable(false);
        setActionBarPadding(findViewById(R.id.action_holder_layout));
        Point point = CommonUtil.getDeviceSize(this);
        int width = Math.round(point.x * 3 / 2);
        int height = Math.round(point.y * 5 / 2);
        pvImage = (PhotoView) findViewById(R.id.pv_image);
        TextView tvDescription = (TextView) findViewById(R.id.tv_description);
        progressBar = findViewById(R.id.progress_bar);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        String description = getIntent().getStringExtra("description");
        String path = getIntent().getStringExtra("path");

        if (TextUtils.isEmpty(description)) {
            tvDescription.setVisibility(View.GONE);
        } else {
            tvDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(description);
            tvDescription.setMovementMethod(ScrollingMovementMethod.getInstance());
        }
        Glide.with(this)
                .load(ImagePath.buildPath(path)
                        .width(width)
                        .height(height)
                        .path())
                .apply(new RequestOptions().fitCenter()
                        .placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .listener(new PageImageRequestListener(pvImage, progressBar))
                .into(pvImage);
    }
}
