package com.hunliji.hljcardlibrary.views.activities;

import android.net.Uri;
import android.widget.TextView;

import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.models.CardPage;
import com.hunliji.hljcardlibrary.models.ImageHole;
import com.hunliji.hljcardlibrary.models.ImageInfo;
import com.hunliji.hljcardlibrary.utils.CardEditObbUtil;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljvideolibrary.activities.BaseVideoTrimActivity;
import com.hunliji.hljvideolibrary.scalablevideo.ScalableType;

import java.io.File;
import java.util.Formatter;

import rx.Subscription;

/**
 * Created by wangtao on 2017/7/4.
 * 请帖视频剪辑页面，视频固定比例，且静音
 */

public class PageVideoTrimActivity extends BaseVideoTrimActivity {


    private CardPage page;
    private ImageHole imageHole;
    private ImageInfo imageInfo;
    private Photo photo;
    private Subscription editSubscription;

    @Override
    protected void initValues() {
        photo = getIntent().getParcelableExtra("photo");
        page = getIntent().getParcelableExtra("page");
        imageHole = getIntent().getParcelableExtra("imageHole");
        imageInfo = getIntent().getParcelableExtra("imageInfo");
        minDurationInSeconds = 1;
        maxDurationInSeconds = 6;
        uri = Uri.fromFile(new File(photo.getImagePath()));
        videoTrimView.setMuted(true);
        videoTrimView.setScalableType(ScalableType.CENTER_CROP);
    }

    @Override
    protected void setTvCurrentVideoLengthText(TextView tvLength, float videoTotalMs) {
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
        editSubscription = CardEditObbUtil.uploadPageForVideoObb(this,
                page,
                imageHole,
                imageInfo,
                photo)
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setProgressDialog(DialogUtil.createProgressDialog(this))
                        .setOnNextListener(new SubscriberOnNextListener() {
                            @Override
                            public void onNext(Object o) {
                                try {
                                    if (!PageVideoTrimActivity.this.uri.getPath()
                                            .equals(photo.getImagePath())) {
                                        FileUtil.deleteFile(new File(photo.getImagePath()));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                onBackPressed();
                            }
                        })
                        .build());
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(editSubscription);
        super.onFinish();
    }
}
