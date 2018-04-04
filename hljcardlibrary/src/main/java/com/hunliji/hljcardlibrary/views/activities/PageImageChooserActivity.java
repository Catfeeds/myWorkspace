package com.hunliji.hljcardlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.models.CardPage;
import com.hunliji.hljcardlibrary.models.ImageHole;
import com.hunliji.hljcardlibrary.models.ImageInfo;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljimagelibrary.models.Gallery;
import com.hunliji.hljimagelibrary.utils.StaticImageList;
import com.hunliji.hljimagelibrary.views.activities.BaseMediaChooserActivity;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangtao on 2017/7/3.
 */

public class PageImageChooserActivity extends ImageChooserActivity {

    private CardPage page;
    private ImageHole imageHole;
    private ImageInfo imageInfo;
    private boolean isResult;

    private CheckableLinearLayout clTitle;
    private TextView tvTitle;
    private Button btnChooseOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
    }

    private void initData() {
        limit = 1;
        page = getIntent().getParcelableExtra("page");
        imageHole = getIntent().getParcelableExtra("imageHole");
        imageInfo = getIntent().getParcelableExtra("imageInfo");
        isResult=getIntent().getBooleanExtra("result",false);
    }

    @Override
    public void initActionBarView(ViewGroup actionParent) {
        actionView = View.inflate(this, R.layout.page_image_chooser_action_bar___card, actionParent);
        tvTitle = (TextView) actionView.findViewById(R.id.tv_title);
        clTitle = (CheckableLinearLayout) actionView.findViewById(R.id.cl_title);

        tvTitle.setText(R.string.label_all_photos___img);
        actionView.findViewById(R.id.btn_back)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
        clTitle.setOnCheckedChangeListener(new CheckableLinearLayout.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(View view, boolean checked) {
                if (checked) {
                    showGalleryList();
                } else {
                    hindGalleryList();
                }
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
                        onPreview(photos, photos, photos.get(0));
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
    public void onGalleryChange(Gallery gallery) {
        tvTitle.setText(gallery.getName());
    }

    @Override
    public void onSelectedCountChange(int selectedCount) {
        btnChooseOk.setEnabled(selectedCount > 0);
    }

    @Override
    public boolean isTakeAble() {
        return false;
    }


    @Override
    public void onGalleryShowChange(boolean isShow) {
        clTitle.setChecked(isShow);
    }

    @Override
    public void onChooseOk(ArrayList<Photo> selectedPhotos) {
        if(isResult){
            super.onChooseOk(selectedPhotos);
            return;
        }
        Photo photo = selectedPhotos.get(0);
        Intent intent = new Intent(this, PageImageEditActivity.class);
        intent.putExtra("page", page);
        intent.putExtra("imageInfo", imageInfo);
        intent.putExtra("imageHole", imageHole);
        intent.putExtra("photo", photo);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void onPreview(
            List<Photo> photos, List<Photo> selectedPhotos, Photo currentPhoto) {
        ArrayList<String> images = new ArrayList<>();
        ArrayList<String> selectedPaths = new ArrayList<>();
        for (Photo item : photos) {
            if (!item.isVideo()) {
                images.add(item.getImagePath());
            }
        }
        if (selectedPhotos != null) {
            for (Photo item : selectedPhotos) {
                selectedPaths.add(item.getImagePath());
            }
        }
        Intent intent = new Intent(this, PageImageChooserPageActivity.class);
        intent.putExtra("limit", limit);
        StaticImageList.INSTANCE.setImagePaths(images);
        StaticImageList.INSTANCE.setSelectedPaths(selectedPaths);
        intent.putExtra("position", images.indexOf(currentPhoto.getImagePath()));
        intent.putExtra("count_enable", getSelectedMode() == SelectedMode.COUNT);
        startActivityForResult(intent, BaseMediaChooserActivity.SELECTED_PATHS_CHANGE);
        overridePendingTransition(R.anim.activity_anim_in, R.anim.activity_anim_default);
    }
}
