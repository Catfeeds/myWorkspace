package com.hunliji.hljimagelibrary.views.activities;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljimagelibrary.R;
import com.hunliji.hljimagelibrary.models.Gallery;
import com.hunliji.hljimagelibrary.utils.StaticImageList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangtao on 2017/7/22.
 */

public class ImageChooserActivity extends BaseMediaChooserActivity {

    private CheckableLinearLayout clTitle;
    private TextView tvTitle;
    private Button btnPreview;
    private Button btnChooseOk;
    protected View actionView;
    protected View bottomView;

    public final static String ARG_SELECTED_PHOTOS = "selectedPhotos";

    @Override
    public void initActionBarView(ViewGroup actionParent) {
        actionView = View.inflate(this, R.layout.image_chooser_action_bar___img, actionParent);
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
        setStatusBarPaddingColor(ContextCompat.getColor(this, R.color.colorWhite));
    }

    @Override
    public void initBottomBarView(ViewGroup bottomParent) {
        bottomView = View.inflate(this, R.layout.image_chooser_bottom___img, bottomParent);

        btnPreview = (Button) bottomView.findViewById(R.id.btn_preview);
        btnChooseOk = (Button) bottomView.findViewById(R.id.btn_choose_ok);

        btnChooseOk.setText(getString(R.string.btn_next___img, limit > 0 ? "0/" + limit : "0"));

        btnChooseOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChooseOk(adapter.getSelectedPhotos());
            }
        });
        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Photo> photos = adapter.getSelectedPhotos();
                if (CommonUtil.isCollectionEmpty(photos)) {
                    return;
                }
                onPreview(photos, photos, photos.get(0));
            }
        });
    }

    @Override
    public void onGalleryChange(Gallery gallery) {
        tvTitle.setText(gallery.getName());
    }

    @Override
    public void onSelectedCountChange(int selectedCount) {
        btnChooseOk.setText(getString(R.string.btn_next___img,
                limit > 0 ? selectedCount + "/" + limit : String.valueOf(selectedCount)));
        btnChooseOk.setEnabled(selectedCount > 0 && (limit <= 0 || selectedCount <= limit));
        btnPreview.setEnabled(selectedCount > 0);
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
        Intent intent = new Intent(this, ChoosePhotoPageActivity.class);
        StaticImageList.INSTANCE.setImagePaths(images);
        StaticImageList.INSTANCE.setSelectedPaths(selectedPaths);

        intent.putExtra("limit", limit);
        intent.putExtra("position", images.indexOf(currentPhoto.getImagePath()));
        intent.putExtra("count_enable", getSelectedMode() == SelectedMode.COUNT);
        startActivityForResult(intent, BaseMediaChooserActivity.SELECTED_PATHS_CHANGE);
        overridePendingTransition(R.anim.activity_anim_in, R.anim.activity_anim_default);
    }

    @Override
    public void onVideoPreview(Photo currentPhoto) {

    }

    @Override
    public void onTakePhotoClick() {
        onImageForCamera();
    }

    @Override
    public int getSelectedMode() {
        return SelectedMode.NORMAL;
    }

    @Override
    public int getMediaType() {
        return MediaType.IMAGE;
    }

    @Override
    public boolean isTakeAble() {
        return true;
    }

    @Override
    public List<Photo> getExtraMedias() {
        return null;
    }

    @Override
    public void onGalleryShowChange(boolean isShow) {
        clTitle.setChecked(isShow);
    }

    @Override
    public void onChooseOk(ArrayList<Photo> selectedPhotos) {
        Intent intent = getIntent();
        intent.putParcelableArrayListExtra(ARG_SELECTED_PHOTOS, selectedPhotos);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    @Override
    public boolean isGifSupport() {
        return false;
    }
}
