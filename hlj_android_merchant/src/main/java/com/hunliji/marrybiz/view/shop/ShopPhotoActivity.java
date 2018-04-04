package com.hunliji.marrybiz.view.shop;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ShopPhotoRecyclerAdapter;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.util.DialogUtil;
import com.hunliji.marrybiz.util.Session;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 店铺照片
 * Created by mo_yu on 2016/8/22.
 */
public class ShopPhotoActivity extends HljBaseActivity implements ShopPhotoRecyclerAdapter
        .OnDeleteClickListener, ShopPhotoRecyclerAdapter.OnItemClickListener {
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private Dialog backDialog;
    private ArrayList<Photo> photos;
    private Photo addPhoto;
    private ShopPhotoRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_recycler_view___cm);
        ButterKnife.bind(this);
        initValue();
        initView();
    }

    private void initValue() {
        addPhoto = new Photo();
        addPhoto.setId(-1);
        photos = getIntent().getParcelableArrayListExtra("selectedPhotos");
        if (photos == null) {
            photos = new ArrayList<>();
        }
        addAddPicJson();
        adapter = new ShopPhotoRecyclerAdapter(this, photos);
    }

    private void initView() {
        MerchantUser user = Session.getInstance()
                .getCurrentUser(this);
        if (user == null) {
            return;
        }
        if (user.isIndividualMerchant()) {
            //四大金刚
            setTitle(R.string.label_personal_show);
        } else {
            long propertyId = user.getPropertyId();
            if (propertyId == 2 || propertyId == 6) {
                //婚礼策划 婚纱摄影
                setTitle(R.string.label_merchant_photos);
            } else if (propertyId == 13) {
                setTitle(R.string.label_merchant_hotel);
            }
        }
        setOkText(R.string.label_save);
        adapter.setOnDeleteClickListener(this);
        adapter.setOnItemClickListener(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setVisibility(View.VISIBLE);
        int padding = CommonUtil.dp2px(this, 8);
        recyclerView.setPadding(padding, 0, padding, 0);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


    /**
     * GridView 添加 添加图片按钮
     */
    private void addAddPicJson() {
        removeAddPicJson();
        if (photos.size() < 12) {
            photos.add(addPhoto);
        }
    }

    private void removeAddPicJson() {
        if (photos.contains(addPhoto)) {
            photos.remove(addPhoto);
        }

    }

    public void onBackPressed(View view) {
        if (backDialog != null && backDialog.isShowing()) {
            return;
        }
        if (backDialog == null) {
            backDialog = DialogUtil.createDoubleButtonDialog(backDialog,
                    this,
                    null,
                    getString(R.string.label_exit_edit),
                    getString(R.string.action_ok),
                    getString(R.string.action_cancel),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            backDialog.dismiss();
                            ShopPhotoActivity.super.onBackPressed();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            backDialog.dismiss();
                        }
                    });
        }
        backDialog.show();
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        removeAddPicJson();
        Intent intent = getIntent();
        intent.putExtra("selectedPhotos", photos);
        setResult(Activity.RESULT_OK, intent);
        onBackPressed();
    }

    public void selectPhoto() {
        removeAddPicJson();
        if (photos.size() < 12) {
            Intent intent = new Intent(this, ImageChooserActivity.class);
            intent.putExtra("limit", Math.min(12 - photos.size(), 12));
            startActivityForResult(intent, Constants.RequestCode.PHOTO_FROM_GALLERY);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else {
            Toast toast = Toast.makeText(this,
                    getString(R.string.label_photos_limit, 12),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
        addAddPicJson();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.RequestCode.PHOTO_FROM_GALLERY) {
                ArrayList<Photo> selectPhoto = data.getParcelableArrayListExtra("selectedPhotos");
                removeAddPicJson();
                if (selectPhoto != null) {
                    photos.addAll(selectPhoto);
                }
                addAddPicJson();
                adapter.notifyDataSetChanged();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDelete(Photo photo, int position) {
        photos.remove(photo);
        addAddPicJson();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Photo photo, int position) {
        if (photo != null) {
            if (photo.getId() != -1) {
                removeAddPicJson();
                Intent intent = new Intent(this, PicsPageViewActivity.class);
                intent.putExtra("photos", photos);
                intent.putExtra("position", position);
                startActivity(intent);
                addAddPicJson();
                adapter.notifyDataSetChanged();
            } else {
                selectPhoto();
            }
        }
    }
}
