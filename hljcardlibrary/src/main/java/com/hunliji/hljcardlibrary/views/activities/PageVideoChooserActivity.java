package com.hunliji.hljcardlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.models.CardPage;
import com.hunliji.hljcardlibrary.models.CardRxEvent;
import com.hunliji.hljcardlibrary.models.ImageHole;
import com.hunliji.hljcardlibrary.models.ImageInfo;
import com.hunliji.hljcardlibrary.utils.CardEditObbUtil;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.models.Gallery;
import com.hunliji.hljimagelibrary.views.activities.BaseMediaChooserActivity;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangtao on 2017/7/22.
 */

public class PageVideoChooserActivity extends BaseMediaChooserActivity {

    private CardPage page;
    private ImageHole imageHole;
    private ImageInfo imageInfo;


    private Button btnChooseOk;
    private Subscription rxBusSubscription;
    private Subscription editSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
        registerRxBusSub();
    }


    private void initData() {
        limit = 1;
        page = getIntent().getParcelableExtra("page");
        imageHole = getIntent().getParcelableExtra("imageHole");
        imageInfo = getIntent().getParcelableExtra("imageInfo");
    }

    private void registerRxBusSub() {
        rxBusSubscription = RxBus.getDefault()
                .toObservable(CardRxEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxBusSubscriber<CardRxEvent>() {
                    @Override
                    protected void onEvent(CardRxEvent cardRxEvent) {
                        switch (cardRxEvent.getType()) {
                            case PAGE_VIDEO_EDIT:
                                finish();
                                break;
                        }
                    }
                });
    }

    @Override
    public void initActionBarView(ViewGroup actionParent) {
        View actionView = View.inflate(this,
                R.layout.page_video_choose_action_bar___card,
                actionParent);
        actionView.findViewById(R.id.btn_back)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
        actionView.findViewById(R.id.item)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<Photo> photos = adapter.getSelectedPhotos();
                        if (CommonUtil.isCollectionEmpty(photos)) {
                            return;
                        }
                        onVideoPreview(photos.get(0));
                    }
                });
        setStatusBarPaddingColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @Override
    public void initBottomBarView(ViewGroup bottomParent) {
        View bottomLayout = View.inflate(this,
                R.layout.page_choose_photo_activity_bottom___card,
                bottomParent);
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
        Intent intent = new Intent(this, PageVideoPreviewActivity.class);
        intent.putExtra("page", page);
        intent.putExtra("imageInfo", imageInfo);
        intent.putExtra("imageHole", imageHole);
        intent.putExtra("photo", currentPhoto);
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
    public void onChooseOk(ArrayList<Photo> selectedPhotos) {
        Photo photo = selectedPhotos.get(0);
        if (photo.getDuration() > 6000) {
            Intent intent = new Intent(this, PageVideoTrimActivity.class);
            intent.putExtra("page", page);
            intent.putExtra("imageInfo", imageInfo);
            intent.putExtra("imageHole", imageHole);
            intent.putExtra("photo", photo);
            startActivity(intent);
        } else {
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
                                    onBackPressed();
                                }
                            })
                            .build());
        }
    }

    @Override
    public void onPreview(
            List<Photo> photos, List<Photo> selectedPhotos, Photo currentPhoto) {
    }

    @Override
    public void onTakePhotoClick() {

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
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(rxBusSubscription, editSubscription);
        super.onFinish();
    }

    @Override
    public boolean isGifSupport() {
        return false;
    }
}
