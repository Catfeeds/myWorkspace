package com.hunliji.marrybiz.view;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ObjectBindAdapter;
import com.hunliji.marrybiz.model.Gallery;
import com.hunliji.marrybiz.model.Photo;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.AnimUtil;
import com.hunliji.marrybiz.util.DialogUtil;
import com.hunliji.marrybiz.util.FileUtil;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.hunliji.marrybiz.util.Size;
import com.hunliji.marrybiz.widget.CheckableLinearLayout2;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by Suncloud on 2015/4/14.
 */
@RuntimePermissions
public class ChoosePhotoActivity extends Activity implements ObjectBindAdapter.ViewBinder<Photo>,
        AdapterView.OnItemClickListener, CheckableLinearLayout2.OnCheckedChangeListener {

    private ArrayList<Photo> list;
    private ArrayList<Photo> allItems;
    private ObjectBindAdapter<Photo> adapter;
    private int size;
    private Toast toast;
    private ArrayList<Photo> selectedItems;
    private ArrayList<String> selectedPaths;
    private ArrayList<Gallery> galleries;
    private TextView title;
    private CheckableLinearLayout2 titleView;
    private View menuView;
    private ListView galleryList;
    private GridView gridView;
    private ObjectBindAdapter<Gallery> galleryAdapter;
    private Button chooseBtn;
    private int limit;
    private String currentPath;
    private ArrayList<Long> galleryIds;
    private int fileIconSize;
    private Button btnPreview;
    private Class targetClass;//图片选择后 确定要跳转到的activty

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        limit = getIntent().getIntExtra("limit", 0);
        String targetClassStr = getIntent().getStringExtra("targetClass");
        if (!JSONUtil.isEmpty(targetClassStr)) {
            try {
                targetClass = Class.forName(targetClassStr);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        Point point = JSONUtil.getDeviceSize(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        size = Math.round((point.x - 16 * dm.density) / 3);
        fileIconSize = Math.round(25 * dm.density);
        list = new ArrayList<>();
        galleries = new ArrayList<>();
        galleryIds = new ArrayList<>();
        allItems = new ArrayList<>();
        selectedItems = new ArrayList<>();
        selectedPaths = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, list, R.layout.photo_gallery_item, this);
        galleryAdapter = new ObjectBindAdapter<>(this,
                galleries,
                R.layout.photo_file_item,
                new ObjectBindAdapter.ViewBinder<Gallery>() {
                    @Override
                    public void setViewValue(View view, Gallery gallery, int position) {
                        if (view.getTag() == null) {
                            ViewHolder holder = new ViewHolder();
                            holder.seletedView = view.findViewById(R.id.icon_selected);
                            holder.imageView = (ImageView) view.findViewById(R.id.file_icon);
                            holder.titleView = (TextView) view.findViewById(R.id.file_title);
                            holder.countView = (TextView) view.findViewById(R.id.file_count);
                            view.setTag(holder);
                        }
                        final ViewHolder holder = (ViewHolder) view.getTag();
                        holder.titleView.setText(gallery.getName());
                        holder.countView.setText(String.valueOf(gallery.getPhotoCount()));
                        if (!galleryIds.isEmpty() && (gallery.getId() == 0 || galleryIds.contains(
                                gallery.getId()))) {
                            holder.seletedView.setVisibility(View.VISIBLE);
                        } else {
                            holder.seletedView.setVisibility(View.GONE);
                        }
                        if (!JSONUtil.isEmpty(gallery.getPath())) {
                            if (!gallery.getPath()
                                    .equals(holder.imageView.getTag())) {
                                ImageLoadTask task = new ImageLoadTask(holder.imageView,
                                        new OnHttpRequestListener() {
                                            @Override
                                            public void onRequestCompleted(Object obj) {
                                                holder.imageView.setScaleType(ImageView.ScaleType
                                                        .CENTER_CROP);
                                            }

                                            @Override
                                            public void onRequestFailed(Object obj) {

                                            }
                                        });
                                holder.imageView.setTag(gallery.getPath());
                                holder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                task.loadImage(gallery.getPath(),
                                        fileIconSize,
                                        ScaleMode.ALL,
                                        new AsyncBitmapDrawable(getResources(),
                                                R.drawable.icon_default_pic,
                                                task));
                            }
                        } else {
                            holder.imageView.setTag(null);
                            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            holder.imageView.setImageResource(R.drawable.icon_default_pic);
                        }

                    }

                    class ViewHolder {
                        View seletedView;
                        ImageView imageView;
                        TextView titleView;
                        TextView countView;
                    }
                });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_photo);
        btnPreview = (Button) findViewById(R.id.preview_btn);
        chooseBtn = (Button) findViewById(R.id.choose_ok);
        menuView = findViewById(R.id.files_layout);
        galleryList = (ListView) findViewById(R.id.fileList);
        galleryList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        galleryList.setOnItemClickListener(new OnGalleryFileClickListener());
        title = (TextView) findViewById(R.id.title);
        menuView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                titleView.setChecked(false);
                return true;
            }
        });
        titleView = (CheckableLinearLayout2) findViewById(R.id.titleView);
        titleView.setOnCheckedChangeListener(this);
        gridView = (GridView) findViewById(R.id.gallery);
        gridView.setOnItemClickListener(this);
        gridView.setAdapter(adapter);
        ChoosePhotoActivityPermissionsDispatcher.getPhotosWithCheck(this);
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

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_right);
    }

    public void onChooseOk(View view) {
        if (!selectedItems.isEmpty()) {
            Intent intent;
            if (targetClass != null) {
                intent = new Intent(this, targetClass);
            } else {
                intent = getIntent();
            }
            intent.putExtra("selectedPhotos", selectedItems);
            setResult(RESULT_OK, intent);
            onBackPressed();
        }
    }

    @Override
    public void setViewValue(View view, Photo image, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.image);
            holder.selectedView = (CheckableLinearLayout2) view.findViewById(R.id.selected_view);
            holder.takePhotoView = view.findViewById(R.id.take_photo_btn);
            holder.selectBtn = view.findViewById(R.id.select_btn);
            view.getLayoutParams().width = size;
            view.getLayoutParams().height = size;
            view.setTag(holder);
        }
        holder.selectBtn.setOnClickListener(new OnImageSelectListener(image));
        if (JSONUtil.isEmpty(image.getImagePath())) {
            holder.takePhotoView.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
            holder.selectedView.setVisibility(View.GONE);
        } else {
            holder.takePhotoView.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.selectedView.setVisibility(View.VISIBLE);
            holder.selectedView.setChecked(selectedItems.contains(image));
            if (!image.getImagePath()
                    .equals(holder.imageView.getTag())) {
                ImageLoadTask task = new ImageLoadTask(holder.imageView);
                holder.imageView.setTag(image.getImagePath());
                task.loadImage(image.getImagePath(),
                        size / 2,
                        ScaleMode.ALL,
                        new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
            }
        }
    }

    @Override
    public void onCheckedChange(View view, boolean checked) {
        if (checked) {
            AnimUtil.showMenuAnimation(menuView, galleryList);
        } else {
            AnimUtil.hideMenuAnimation(menuView, galleryList);
        }
    }

    public void onPreview(View view) {
        Intent intent = new Intent(this, ChoosePhotoPageActivity.class);
        intent.putExtra("limit", limit);
        intent.putExtra("images", selectedItems);
        intent.putExtra("selectedPaths", selectedPaths);
        intent.putExtra("position", 0);
        startActivityForResult(intent, Constants.RequestCode.CHOOSE_PHOTO_PAGE);
    }

    private class ViewHolder {
        View takePhotoView;
        ImageView imageView;
        View selectBtn;
        CheckableLinearLayout2 selectedView;
    }

    private class OnImageSelectListener implements View.OnClickListener {

        private Photo image;

        private OnImageSelectListener(Photo image) {
            this.image = image;
        }

        @Override
        public void onClick(View v) {
            if (image != null && !JSONUtil.isEmpty(image.getImagePath())) {
                if (selectedItems.contains(image)) {
                    selectedItems.remove(image);
                    selectedPaths.remove(image.getImagePath());
                    adapter.notifyDataSetChanged();
                    if (galleryIds.contains(image.getBucketId())) {
                        galleryIds.remove(image.getBucketId());
                        if (!galleryIds.contains(image.getBucketId())) {
                            galleryAdapter.notifyDataSetChanged();
                        }
                    }
                } else if (limit > 0 && selectedItems.size() >= limit) {
                    if (toast == null) {
                        toast = Toast.makeText(ChoosePhotoActivity.this,
                                R.string.hint_choose_photo_limit_out,
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                    }
                    toast.show();
                    return;
                } else {
                    if (image.getWidth() == 0 || image.getHeight() == 0) {
                        Size size = JSONUtil.getImageSizeFromPath(image.getImagePath());
                        image.setHeight(size.getHeight());
                        image.setWidth(size.getWidth());
                    }
                    if (!galleryIds.contains(image.getBucketId())) {
                        galleryIds.add(image.getBucketId());
                        galleryAdapter.notifyDataSetChanged();
                    } else {
                        galleryIds.add(image.getBucketId());
                    }
                    selectedItems.add(image);
                    selectedPaths.add(image.getImagePath());
                    adapter.notifyDataSetChanged();
                }
                if (selectedItems.size() < 1) {
                    btnPreview.setEnabled(false);
                    chooseBtn.setEnabled(false);
                    chooseBtn.setText(R.string.label_choose_ok);
                } else {
                    btnPreview.setEnabled(true);
                    chooseBtn.setEnabled(true);
                    chooseBtn.setText(getString(R.string.label_choose_ok2, selectedItems.size()));
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Photo image = (Photo) parent.getAdapter()
                .getItem(position);
        if (image != null && !JSONUtil.isEmpty(image.getImagePath())) {
            Intent intent = new Intent(this, ChoosePhotoPageActivity.class);
            intent.putExtra("limit", limit);
            intent.putExtra("images", list);
            intent.putExtra("selectedPaths", selectedPaths);
            intent.putExtra("position", list.indexOf(image));
            startActivityForResult(intent, Constants.RequestCode.CHOOSE_PHOTO_PAGE);
            overridePendingTransition(R.anim.activity_anim_in, R.anim.activity_anim_default);
        } else if (image != null && JSONUtil.isEmpty(image.getImagePath())) {
            ChoosePhotoActivityPermissionsDispatcher.onTakePhotoWithCheck(this);
        }
    }

    private class PhotoTask extends AsyncTask<String, Integer, Integer> {

        private Context mContext;

        public PhotoTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected Integer doInBackground(String... params) {
            ContentResolver cr = mContext.getContentResolver();
            Cursor cursor = MediaStore.Images.Media.query(cr,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media
                            .BUCKET_DISPLAY_NAME, "count(*) as photo_count", MediaStore.Images
                            .Media.DATA},
                    MediaStore.Images.Media.MIME_TYPE + "<>? and " + MediaStore.Images.Media.SIZE
                            + ">=?) group by (" + MediaStore.Images.Media.BUCKET_ID,
                    new String[]{"image/gif", "1024"},
                    MediaStore.Images.Media.DATE_ADDED + " desc");
            if (cursor.moveToFirst()) {
                do {
                    Gallery gallery = new Gallery(cursor.getLong(0),
                            cursor.getString(1),
                            cursor.getInt(2),
                            cursor.getString(3));
                    galleries.add(gallery);
                } while (cursor.moveToNext());
            }
            cursor.close();
            String[] strings;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                strings = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media
                        .BUCKET_ID, MediaStore.Images.Media.ORIENTATION, MediaStore.Images.Media
                        .WIDTH, MediaStore.Images.Media.HEIGHT};
            } else {
                strings = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media
                        .BUCKET_ID};
            }
            cursor = MediaStore.Images.Media.query(cr,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    strings,
                    MediaStore.Images.Media.MIME_TYPE + "<>? and " + "" + MediaStore.Images.Media
                            .SIZE + ">=?",
                    new String[]{"image/gif", "1024"},
                    MediaStore.Images.Media.DATE_ADDED + " desc");
            int i = 0;
            list.clear();
            list.add(new Photo(new JSONObject()));
            if (cursor.moveToFirst())
                do {
                    String path = cursor.getString(0);
                    Photo item = new Photo(new JSONObject());
                    item.setBucketId(cursor.getLong(1));
                    item.setImagePath(path);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        if (cursor.getInt(2) % 180 == 0) {
                            item.setWidth(cursor.getInt(3));
                            item.setHeight(cursor.getInt(4));
                        } else {
                            item.setWidth(cursor.getInt(4));
                            item.setHeight(cursor.getInt(3));
                        }
                    }
                    list.add(item);
                    allItems.add(item);
                    i++;
                    if (i == 5) {
                        i = 0;
                        publishProgress(0);
                    }
                } while (cursor.moveToNext());
            cursor.close();
            return list.size();
        }

        @Override
        protected void onPostExecute(Integer result) {
            adapter.notifyDataSetChanged();
            galleries.add(0,
                    new Gallery(0,
                            getString(R.string.label_all_photos),
                            allItems.size(),
                            allItems.isEmpty() ? null : allItems.get(0)
                                    .getImagePath()));
            galleryList.setAdapter(galleryAdapter);
            galleryList.setItemChecked(0, true);
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            adapter.notifyDataSetChanged();
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    private class OnGalleryFileClickListener implements AdapterView.OnItemClickListener {

        private long selectedId;

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Gallery gallery = (Gallery) parent.getAdapter()
                    .getItem(position);
            if (gallery != null) {
                titleView.setChecked(false);
                if (selectedId != gallery.getId()) {
                    selectedId = gallery.getId();
                    title.setText(gallery.getName());
                    list.clear();
                    if (selectedId != 0) {
                        for (Photo image : allItems) {
                            if (image.getBucketId() == selectedId) {
                                list.add(image);
                            }
                        }
                    } else {
                        list.add(new Photo(new JSONObject()));
                        list.addAll(allItems);
                    }
                    adapter.notifyDataSetChanged();
                    gridView.post(new Runnable() {
                        @Override
                        public void run() {
                            gridView.setSelection(0);
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.CHOOSE_PHOTO_PAGE:
                    if (data != null) {
                        ArrayList<String> selectedPaths = data.getStringArrayListExtra(
                                "selectedPaths");
                        if (selectedPaths != null) {
                            boolean done = data.getBooleanExtra("done", false);
                            this.selectedPaths.clear();
                            this.selectedPaths.addAll(selectedPaths);
                            selectedItems.clear();
                            galleryIds.clear();
                            for (Photo image : allItems) {
                                if (selectedPaths.contains(image.getImagePath())) {
                                    selectedItems.add(image);
                                    galleryIds.add(image.getBucketId());
                                }
                            }
                            if (done) {
                                Intent intent = getIntent();
                                intent.putExtra("selectedPhotos", selectedItems);
                                setResult(RESULT_OK, intent);
                                onBackPressed();
                            } else {
                                galleryAdapter.notifyDataSetChanged();
                                adapter.notifyDataSetChanged();
                                if (selectedItems.size() < 1) {
                                    btnPreview.setEnabled(false);
                                    chooseBtn.setEnabled(false);
                                    chooseBtn.setText(R.string.label_choose_ok);
                                } else {
                                    btnPreview.setEnabled(true);
                                    chooseBtn.setEnabled(true);
                                    chooseBtn.setText(getString(R.string.label_choose_ok2,
                                            selectedItems.size()));
                                }
                            }
                        }
                    }
                    break;
                case Constants.RequestCode.PHOTO_FROM_CAMERA:
                    Photo image = new Photo(new JSONObject());
                    Size size = JSONUtil.getImageSizeFromPath(currentPath);
                    image.setWidth(size.getWidth());
                    image.setHeight(size.getHeight());
                    image.setImagePath(currentPath);
                    selectedItems.clear();
                    selectedPaths.clear();
                    selectedItems.add(image);
                    Intent intent = getIntent();
                    intent.putExtra("selectedPhotos", selectedItems);
                    setResult(RESULT_OK, intent);
                    onBackPressed();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onTakePhoto() {
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
        startActivityForResult(intent, Constants.RequestCode.PHOTO_FROM_CAMERA);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void getPhotos() {
        new PhotoTask(this).executeOnExecutor(Constants.LISTTHEADPOOL);
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onDeniedReadPhotos() {
        adapter.notifyDataSetChanged();
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onRationalePhoto(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.permission_r_for_read_external_storage));
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onRationaleCamera(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.permission_r_for_camera));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ChoosePhotoActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}