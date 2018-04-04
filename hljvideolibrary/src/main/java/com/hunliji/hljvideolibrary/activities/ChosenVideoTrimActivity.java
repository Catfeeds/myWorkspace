package com.hunliji.hljvideolibrary.activities;

import android.net.Uri;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.R;
import com.hunliji.hljvideolibrary.models.VideoRxEvent;

import java.io.File;
import java.util.Formatter;

/**
 * Created by wangtao on 2017/7/22.
 * 视频选择界面通用的视频剪辑
 */

public class ChosenVideoTrimActivity extends BaseVideoTrimActivity {

    private Photo photo;
    private long duration;
    @Override
    protected void initValues() {
        super.initValues();
        photo = getIntent().getParcelableExtra("photo");
        uri = Uri.fromFile(new File(photo.getImagePath()));
    }

    @Override
    protected void setTvCurrentVideoLengthText(TextView tvLength, float videoTotalMs) {
        this.duration= (long) videoTotalMs;
        Formatter mFormatter = new Formatter();
        int seconds = (int) (videoTotalMs / 1000);
        String secondsStr = mFormatter.format("%3d", seconds)
                .toString();
        tvLength.setText(CommonUtil.fromHtml(this,
                getString(R.string.html_fmt_video_trim_sec___img),
                secondsStr));
    }

    @Override
    protected void onTrimDone(Uri uri) {
        photo.setImagePath(uri.getPath());
        photo.setDuration(duration);
        onBackPressed();
        RxBus.getDefault().post(new VideoRxEvent(VideoRxEvent.RxEventType.VIDEO_CALLBACK,photo));
    }
}
