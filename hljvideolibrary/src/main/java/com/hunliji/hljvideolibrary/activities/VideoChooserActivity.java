package com.hunliji.hljvideolibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.models.Gallery;
import com.hunliji.hljimagelibrary.views.activities.BaseMediaChooserActivity;
import com.hunliji.hljvideolibrary.R;
import com.hunliji.hljvideolibrary.models.VideoRxEvent;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;


/**
 * Created by wangtao on 2017/7/22.
 */

public class VideoChooserActivity extends BaseMediaChooserActivity {

    private Button btnChooseOk;
    private int minDurationInSeconds;
    private int maxDurationInSeconds;
    private Subscription rxSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
        registerRxBus();

    }

    private void initData() {
        limit = 1;
        minDurationInSeconds = getIntent().getIntExtra(BaseVideoTrimActivity.ARGS_MIN_VIDEO_LENGTH,
                1);
        maxDurationInSeconds = getIntent().getIntExtra(BaseVideoTrimActivity.ARGS_MAX_VIDEO_LENGTH,
                6);
    }

    private void registerRxBus() {
        if (rxSubscription != null && !rxSubscription.isUnsubscribed()) {
            return;
        }
        rxSubscription = RxBus.getDefault()
                .toObservable(VideoRxEvent.class)
                .subscribe(new RxBusSubscriber<VideoRxEvent>() {
                    @Override
                    protected void onEvent(VideoRxEvent videoRxEvent) {
                        switch (videoRxEvent.getType()) {
                            case VIDEO_CALLBACK:
                                Intent intent = getIntent();
                                intent.putExtra("photo", (Photo) videoRxEvent.getObject());
                                setResult(RESULT_OK, intent);
                                onBackPressed();
                                finish();
                                break;
                        }
                    }
                });
    }

    @Override
    public void initActionBarView(ViewGroup actionParent) {
        View actionView = View.inflate(this, R.layout.video_chooser_action_bar___img, actionParent);
        actionView.findViewById(R.id.btn_back)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
        setStatusBarPaddingColor(ContextCompat.getColor(this, R.color.colorWhite));
    }

    @Override
    public void initBottomBarView(ViewGroup bottomParent) {
        View bottomLayout = View.inflate(this, R.layout.video_chooser_bottom___img, bottomParent);
        btnChooseOk = (Button) bottomLayout.findViewById(R.id.btn_choose_ok);
        btnChooseOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChooseOk(adapter.getSelectedPhotos());
            }
        });
    }

    @Override
    public void onSelectedCountChange(int selectedCount) {
        btnChooseOk.setEnabled(selectedCount > 0);
    }

    @Override
    public void onVideoPreview(Photo currentPhoto) {
        Intent intent = new Intent(this, VideoChooserPreviewActivity.class);
        intent.putExtra("photo", currentPhoto);
        intent.putExtra(BaseVideoTrimActivity.ARGS_MIN_VIDEO_LENGTH, minDurationInSeconds);
        intent.putExtra(BaseVideoTrimActivity.ARGS_MAX_VIDEO_LENGTH, maxDurationInSeconds);
        startActivity(intent);
    }

    @Override
    public int getSelectedMode() {
        return SelectedMode.NORMAL;
    }

    @Override
    public int getMediaType() {
        return MediaType.VIDEO;
    }

    @Override
    public void onTakePhotoClick() {

    }

    @Override
    public void onChooseOk(ArrayList<Photo> selectedPhotos) {
        Photo photo = selectedPhotos.get(0);
        Intent intent = new Intent(this, ChosenVideoTrimActivity.class);
        intent.putExtra("photo", photo);
        intent.putExtra(BaseVideoTrimActivity.ARGS_MIN_VIDEO_LENGTH, minDurationInSeconds);
        intent.putExtra(BaseVideoTrimActivity.ARGS_MAX_VIDEO_LENGTH, maxDurationInSeconds);
        startActivity(intent);
    }

    @Override
    public boolean isTakeAble() {
        return false;
    }

    @Override
    public List<Photo> getExtraMedias() {
        return null;
    }

    @Override
    public void onGalleryShowChange(boolean isShow) {

    }

    @Override
    public void onGalleryChange(Gallery gallery) {

    }

    @Override
    public void onPreview(
            List<Photo> photos, List<Photo> selectedPhotos, Photo currentPhoto) {

    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(rxSubscription);
        super.onFinish();
    }

    @Override
    public boolean isGifSupport() {
        return false;
    }
}
