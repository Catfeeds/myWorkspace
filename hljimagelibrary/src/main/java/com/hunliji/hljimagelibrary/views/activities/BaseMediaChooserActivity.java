package com.hunliji.hljimagelibrary.views.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljimagelibrary.R;
import com.hunliji.hljimagelibrary.adapters.ChoosePhotoAdapter;
import com.hunliji.hljimagelibrary.models.Gallery;
import com.hunliji.hljimagelibrary.models.Size;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljimagelibrary.utils.LoadMediaObbUtil;
import com.hunliji.hljimagelibrary.utils.StaticImageList;
import com.hunliji.hljimagelibrary.views.fragments.GalleryChooseFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/7/21.
 */
@RuntimePermissions
public abstract class BaseMediaChooserActivity extends HljBaseNoBarActivity implements
        GalleryChooseFragment.OnGalleryListListener, ChoosePhotoAdapter.OnPhotoListInterface {

    protected int limit;
    private ArrayList<String> selectedPaths;

    private ArrayList<Photo> allItems;
    private ArrayList<Gallery> galleries;
    private long currentGalleryId;

    private View emptyHintLayout;
    private TextView tvEmptyHint;
    private RecyclerView recyclerView;
    private View noticeLayout;
    private TextView tvNoticeContent;
    private View fragmentView;

    private Subscription gallerySubscription;
    private Subscription photoSubscription;

    private GalleryChooseFragment galleryChooseFragment;

    protected ChoosePhotoAdapter adapter;

    public final static int IMAGE_FROM_CAMERA = 101;
    public final static int SELECTED_PATHS_CHANGE = 102;

    private String currentPath;


    public static class MediaType {
        public static final int IMAGE = 0;
        public static final int VIDEO = 1;
        public static final int ALL = 2;
    }

    public static class SelectedMode {
        public static final int NORMAL = 0;
        public static final int COUNT = 1;
    }

    public static final String INTENT_LIMIT = "limit";
    public static final String INTENT_SELECTED_PATHS = "selectedPaths";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_media_chooser_base___img);
        setDefaultStatusBarPadding();
        initBaseData();
        initBaseView();
        BaseMediaChooserActivityPermissionsDispatcher.loadMediasWithCheck(this);
    }

    private void initBaseData() {
        allItems = new ArrayList<>();
        limit = getIntent().getIntExtra(INTENT_LIMIT, limit);
        selectedPaths = getSelectedPaths();
    }

    public ArrayList<String> getSelectedPaths() {
        if (!CommonUtil.isCollectionEmpty(selectedPaths)) {
            return selectedPaths;
        }
        return getIntent().getStringArrayListExtra(INTENT_SELECTED_PATHS);
    }

    private void initBaseView() {
        initActionBarView((ViewGroup) findViewById(R.id.action_layout));
        initBottomBarView((ViewGroup) findViewById(R.id.bottom_layout));
        emptyHintLayout = findViewById(R.id.empty_hint_layout);
        tvEmptyHint = (TextView) findViewById(R.id.tv_empty_hint);
        noticeLayout = findViewById(R.id.notice_layout);
        tvNoticeContent = (TextView) findViewById(R.id.tv_notice_content);
        findViewById(R.id.btn_notice_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noticeLayout.setVisibility(View.GONE);
            }
        });

        fragmentView = findViewById(R.id.fragment_content);
        fragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager
                .OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    fragmentView.setBackgroundColor(Color.TRANSPARENT);
                    onGalleryShowChange(false);
                } else {
                    fragmentView.setBackgroundColor(ContextCompat.getColor
                            (BaseMediaChooserActivity.this,
                            R.color.trans_black));
                    onGalleryShowChange(true);
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setItemAnimator(null);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                try {
                    return adapter.getItemViewType(position) < 0 ? 3 : 1;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 1;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new ItemDecoration(this));
        initAdapter();
    }


    private void initAdapter() {
        adapter = new ChoosePhotoAdapter(this);
        adapter.setHeaderView(initHeaderView());
        adapter.setCountEnable(getSelectedMode() == SelectedMode.COUNT);
        adapter.setLimit(limit);
        adapter.setPhotoListInterface(this);
        recyclerView.setAdapter(adapter);
    }

    protected View initHeaderView() {
        return View.inflate(this, R.layout.photo_list_empty_header___img, null);
    }


    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onDeniedLoadMedias() {
        emptyHintLayout.setVisibility(View.VISIBLE);
        tvEmptyHint.setVisibility(View.VISIBLE);
        tvEmptyHint.setText(R.string.label_photo_empty_permission___img);
        recyclerView.setVisibility(View.GONE);
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onRationale(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.msg_permission_r_for_read_external_storage___cm));
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void loadMedias() {
        Observable<Photo> photoObservable = null;
        switch (getMediaType()) {
            case MediaType.IMAGE:
                photoObservable = LoadMediaObbUtil.queryImageObb(getContentResolver(),
                        isGifSupport());
                break;
            case MediaType.VIDEO:
                photoObservable = LoadMediaObbUtil.queryVideoObb(getContentResolver());
                break;
            case MediaType.ALL:
                photoObservable = LoadMediaObbUtil.queryVideoObb(getContentResolver())
                        .concatWith(LoadMediaObbUtil.queryImageObb(getContentResolver(),
                                isGifSupport()));
                break;
        }
        if (photoObservable == null) {
            return;
        }
        photoSubscription = photoObservable.buffer(300, TimeUnit.MILLISECONDS)
                .filter(new Func1<List<Photo>, Boolean>() {
                    @Override
                    public Boolean call(List<Photo> photos) {
                        return !photos.isEmpty();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Photo>>() {

                    @Override
                    public void onStart() {
                        adapter.setTakePhoto(isTakeAble());
                        List<Photo> extraMedias = getExtraMedias();
                        if (!CommonUtil.isCollectionEmpty(extraMedias)) {
                            allItems.addAll(extraMedias);
                            adapter.addPhotos(extraMedias);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        loadGalleries();
                        if (!CommonUtil.isCollectionEmpty(selectedPaths)) {
                            Observable.from(allItems)
                                    .filter(new Func1<Photo, Boolean>() {
                                        @Override
                                        public Boolean call(Photo photo) {
                                            return selectedPaths.contains(photo.getImagePath());
                                        }
                                    })
                                    .toSortedList(new Func2<Photo, Photo, Integer>() {
                                        @Override
                                        public Integer call(Photo photo, Photo photo2) {
                                            return selectedPaths.indexOf(photo.getImagePath()) -
                                                    selectedPaths.indexOf(
                                                    photo2.getImagePath());
                                        }
                                    })
                                    .subscribe(new Action1<List<Photo>>() {
                                        @Override
                                        public void call(List<Photo> photos) {
                                            adapter.setSelectedPhotos(new ArrayList<>(photos));
                                            onSelectedCountChange(photos.size());
                                        }
                                    });
                        }
                        if (CommonUtil.isCollectionEmpty(allItems) && !isTakeAble()) {
                            recyclerView.setVisibility(View.GONE);
                            showEmptyView();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        showEmptyView();
                    }

                    @Override
                    public void onNext(List<Photo> list) {
                        allItems.addAll(list);
                        adapter.addPhotos(list);
                    }
                });
    }

    private void loadGalleries() {
        gallerySubscription = Observable.just(new Gallery(0,
                getString(R.string.label_all_photos___img),
                allItems.size(),
                allItems.isEmpty() ? null : allItems.get(0)
                        .getImagePath()))
                .concatWith(Observable.from(allItems)
                        .filter(new Func1<Photo, Boolean>() {
                            @Override
                            public Boolean call(Photo photo) {
                                return photo.isVideo();
                            }
                        })
                        .toList()
                        .filter(new Func1<List<Photo>, Boolean>() {
                            @Override
                            public Boolean call(List<Photo> photos) {
                                return photos != null && !photos.isEmpty();
                            }
                        })
                        .map(new Func1<List<Photo>, Gallery>() {
                            @Override
                            public Gallery call(List<Photo> photos) {
                                return new Gallery(Gallery.ALL_VIDEO_ID,
                                        "所有视频",
                                        photos.size(),
                                        photos.get(0)
                                                .getImagePath());
                            }
                        }))
                .concatWith(LoadMediaObbUtil.queryImageGalleryObb(getContentResolver(),
                        isGifSupport()))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Gallery>>() {
                    @Override
                    public void call(List<Gallery> list) {
                        galleries = new ArrayList<>(list);
                    }
                });
    }

    protected void showEmptyView() {
        emptyHintLayout.setVisibility(View.VISIBLE);
        tvEmptyHint.setVisibility(View.VISIBLE);
        tvEmptyHint.setText(getMediaType() == MediaType.VIDEO ? R.string.label_video_empty___img
                : R.string.label_photo_empty___img);

    }


    protected void showGalleryList() {
        if (CommonUtil.isCollectionEmpty(galleries)) {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("galleryChooseFragment");
        if (fragment != null) {
            return;
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (galleryChooseFragment == null) {
            galleryChooseFragment = GalleryChooseFragment.newInstance(galleries,
                    adapter.getBucketIds());
        } else {
            galleryChooseFragment.refresh(adapter.getBucketIds());
        }
        ft.setCustomAnimations(R.anim.slide_in_from_top,
                R.anim.slide_out_to_top,
                R.anim.slide_in_from_top,
                R.anim.slide_out_to_top);
        ft.add(R.id.fragment_content, galleryChooseFragment, "galleryChooseFragment");
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    protected void hindGalleryList() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }


    public void onImageForCamera() {
        BaseMediaChooserActivityPermissionsDispatcher.onTakePictureWithCheck(this);
    }

    @Override
    public void onGalleryChoose(Gallery gallery) {
        if (currentGalleryId == gallery.getId()) {
            return;
        }
        currentGalleryId = gallery.getId();
        Observable.from(allItems)
                .filter(new Func1<Photo, Boolean>() {
                    @Override
                    public Boolean call(Photo photo) {
                        if (currentGalleryId == Gallery.ALL_VIDEO_ID) {
                            return photo.isVideo();
                        }
                        return currentGalleryId == 0 || photo.getBucketId() == currentGalleryId;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Photo>>() {
                    @Override
                    public void call(List<Photo> photos) {
                        adapter.setTakePhoto(isTakeAble() && currentGalleryId == 0);
                        adapter.setPhotos(photos);
                        recyclerView.scrollToPosition(0);
                    }
                });
        onGalleryChange(gallery);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onTakePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = FileUtil.createImageFile();
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this, getPackageName(), f);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            imageUri = Uri.fromFile(f);
        }
        currentPath = f.getAbsolutePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE_FROM_CAMERA);
    }


    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onRationaleCamera(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.msg_permission_r_for_camera___cm));
    }

    protected void showNotice(String noticeContent) {
        noticeLayout.setVisibility(View.VISIBLE);
        tvNoticeContent.setText(noticeContent);
    }

    public abstract void initActionBarView(ViewGroup actionParent);

    public abstract void initBottomBarView(ViewGroup bottomParent);

    public abstract void onChooseOk(ArrayList<Photo> selectedPhotos);

    public abstract int getSelectedMode();

    public abstract int getMediaType();

    public abstract boolean isTakeAble();

    public abstract List<Photo> getExtraMedias();

    public abstract void onGalleryShowChange(boolean isShow);

    public abstract void onGalleryChange(Gallery gallery);

    public abstract boolean isGifSupport();

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }
        if (onBackPressedCheck()) {
            super.onBackPressed();
        }
    }

    protected boolean onBackPressedCheck() {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case IMAGE_FROM_CAMERA:
                    if (TextUtils.isEmpty(currentPath)) {
                        return;
                    }
                    Photo item = new Photo();
                    Size size = ImageUtil.getImageSizeFromPath(currentPath);
                    item.setWidth(size.getWidth());
                    item.setHeight(size.getHeight());
                    item.setImagePath(currentPath);
                    ArrayList<Photo> selectedPhotos = new ArrayList<>();
                    selectedPhotos.add(item);
                    onChooseOk(selectedPhotos);
                    break;
                case SELECTED_PATHS_CHANGE:
                    if (data != null) {
                        final ArrayList<String> selectedPaths = data.getStringArrayListExtra(
                                "selectedPaths");
                        final boolean done = data.getBooleanExtra("done", false);
                        if (selectedPaths != null) {
                            Observable.from(allItems)
                                    .filter(new Func1<Photo, Boolean>() {
                                        @Override
                                        public Boolean call(Photo photo) {
                                            return selectedPaths.contains(photo.getImagePath());
                                        }
                                    })
                                    .toSortedList(new Func2<Photo, Photo, Integer>() {
                                        @Override
                                        public Integer call(Photo photo, Photo photo2) {
                                            return selectedPaths.indexOf(photo.getImagePath()) -
                                                    selectedPaths.indexOf(
                                                    photo2.getImagePath());
                                        }
                                    })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<List<Photo>>() {
                                        @Override
                                        public void call(List<Photo> photos) {
                                            adapter.setSelectedPhotos(new ArrayList<>(photos));
                                            if (done) {
                                                onChooseOk(new ArrayList<>(photos));
                                            }
                                        }
                                    });
                        }
                    }
                    break;

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("currentUrl", currentPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        currentPath = savedInstanceState.getString("currentUrl");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        BaseMediaChooserActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onFinish() {
        StaticImageList.INSTANCE.onDestroy();
        CommonUtil.unSubscribeSubs(photoSubscription, gallerySubscription);
        super.onFinish();
    }

    private class ItemDecoration extends RecyclerView.ItemDecoration {

        private int space;
        private int edge;

        private ItemDecoration(Context context) {
            space = CommonUtil.dp2px(context, 2);
            edge = CommonUtil.dp2px(context, 4);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            try {
                ChoosePhotoAdapter.ItemSpace spaceType = adapter.getSpaceType(parent
                        .getChildAdapterPosition(
                        view));
                switch (spaceType) {
                    case Left:
                        outRect.set(space + edge, space, space, space);
                        break;
                    case Right:
                        outRect.set(space, space, space + edge, space);
                        break;
                    case Middle:
                        outRect.set(space, space, space, space);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
