package com.hunliji.marrybiz.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.CustomSetmealOrder;
import com.hunliji.marrybiz.model.Photo;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.util.DialogUtil;
import com.hunliji.marrybiz.util.FileUtil;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.hunliji.marrybiz.util.Size;
import com.hunliji.marrybiz.util.TextCountWatcher;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.ChoosePhotoActivity;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kankan.wheel.widget.DTPicker;
import kankan.wheel.widget.DatePickerView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by Suncloud on 2016/2/3.
 */
@RuntimePermissions
public class CustomOrderProtocolEditFragment extends RefreshFragment implements View
        .OnClickListener, DTPicker.OnPickerDateListener {

    @BindView(R.id.wedding_date)
    TextView weddingDate;
    @BindView(R.id.protocol_images_layout)
    GridLayout protocolImagesLayout;
    @BindView(R.id.message)
    EditText message;

    private Uri currentUri;
    private ArrayList<Photo> images;
    private int imageViewSize;
    private int imageSize;
    private View addView;
    private Dialog selectImageDialog;
    private Dialog selectTimeDialog;

    private Calendar tempCalendar;
    private Calendar calendar;
    private String messageStr;
    private Button doneBtn;
    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        Point point = JSONUtil.getDeviceSize(getActivity());
        imageViewSize = Math.round((point.x - 20 * dm.density) / 3);
        imageSize = Math.round(imageViewSize - 20 * dm.density);
        images = new ArrayList<>();
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            String currentPath = savedInstanceState.getString("currentUrl");
            if (!JSONUtil.isEmpty(currentPath)) {
                currentUri = Uri.fromFile(new File(currentPath));
            }
        }
        if (getArguments() != null) {
            CustomSetmealOrder order = (CustomSetmealOrder) getArguments().getSerializable("order");
            if (order != null) {
                if (order.getProtocolPhotos() != null && !order.getProtocolPhotos()
                        .isEmpty()) {
                    images.addAll(order.getProtocolPhotos());
                }
                if (order.getWeddingTime() != null) {
                    calendar = Calendar.getInstance();
                    calendar.setTime(order.getWeddingTime());
                }
                messageStr = order.getMessage();
            }
        }
        doneBtn = (Button) getActivity().findViewById(R.id.done_btn);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (currentUri != null) {
            outState.putString("currentUrl", currentUri.getPath());
        }
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_custom_order_protocol_edit,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        rootView.findViewById(R.id.message_layout)
                .setOnClickListener(this);
        rootView.findViewById(R.id.protocol_layout)
                .setOnClickListener(this);
        rootView.findViewById(R.id.wedding_date_layout)
                .setOnClickListener(this);
        message.addTextChangedListener(new TextCountWatcher(message, 50));
        message.setText(messageStr);
        if (calendar != null) {
            weddingDate.setText(new DateTime(calendar.getTime()).toString(Constants
                    .DATE_FORMAT_SHORT));
        }
        for (Photo photo : images) {
            View view = View.inflate(getActivity(), R.layout.protocol_image_item, null);
            setImageValue(photo, view);
            protocolImagesLayout.addView(view, imageViewSize, imageViewSize);
        }
        addView = View.inflate(getActivity(), R.layout.protocol_image_item, null);
        setImageValue(null, addView);
        if (protocolImagesLayout.getChildCount() < 6) {
            protocolImagesLayout.addView(addView, imageViewSize, imageViewSize);
        }
        doneBtn.setEnabled(calendar != null && !images.isEmpty());
        return rootView;
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void setImageValue(Photo photo, View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.image.setOnClickListener(new OnItemClickListener(photo));
        if (photo != null) {
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(new OnDeleteClickListener(view, photo));
            holder.image.setBackgroundColor(getResources().getColor(R.color.colorLine));
            String path = photo.getImagePath();
            if (!JSONUtil.isEmpty(path)) {
                int size = imageSize;
                if ((path.startsWith("http://") || path.startsWith("https://"))) {
                    path = JSONUtil.getImagePath2(photo.getImagePath(), imageSize);
                } else {
                    size = imageSize / 2;
                }
                if (!JSONUtil.isEmpty(path) && !path.equals(holder.image.getTag())) {
                    ImageLoadTask task = new ImageLoadTask(holder.image, 0);
                    holder.image.setTag(path);
                    task.loadImage(path,
                            size,
                            ScaleMode.WIDTH,
                            new AsyncBitmapDrawable(getResources(),
                                    R.mipmap.icon_empty_image,
                                    task));
                }
            }
        } else {
            holder.delete.setVisibility(View.GONE);
            holder.image.setBackgroundResource(R.mipmap.icon_cross_add_white_176_176);
            holder.image.setImageBitmap(null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_layout:
            case R.id.protocol_layout:
                if (getActivity().getCurrentFocus() != null) {
                    ((InputMethodManager) getActivity().getSystemService(Activity
                            .INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            getActivity().getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                break;
            case R.id.action_cancel:
                selectImageDialog.dismiss();
                break;
            case R.id.action_gallery:
                Intent intent = new Intent(getActivity(), ChoosePhotoActivity.class);
                intent.putExtra("limit", 6 - images.size());
                startActivityForResult(intent, Constants.RequestCode.PROTOCOL_IMAGE_FOR_GALLERY);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
                selectImageDialog.dismiss();
                break;
            case R.id.action_camera_photo:
                CustomOrderProtocolEditFragmentPermissionsDispatcher.onTakePhotosWithCheck(this);
                break;
            case R.id.wedding_date_layout:
                if (selectTimeDialog != null && selectTimeDialog.isShowing()) {
                    return;
                }
                if (selectTimeDialog == null) {
                    selectTimeDialog = new Dialog(getActivity(), R.style.BubbleDialogTheme);
                    selectTimeDialog.setContentView(R.layout.dialog_date_picker);
                    selectTimeDialog.findViewById(R.id.close)
                            .setOnClickListener(this);
                    selectTimeDialog.findViewById(R.id.confirm)
                            .setOnClickListener(this);
                    DatePickerView picker = (DatePickerView) selectTimeDialog.findViewById(R.id
                            .picker);
                    picker.setYearLimit(2000, 49);
                    if (calendar != null) {
                        picker.setCurrentCalender(calendar);
                    } else {
                        picker.setCurrentCalender(Calendar.getInstance());
                    }
                    if (tempCalendar != null) {
                        tempCalendar = Calendar.getInstance();
                    }
                    picker.setOnPickerDateListener(this);
                    picker.getLayoutParams().height = Math.round(getResources().getDisplayMetrics
                            ().density * (24 * 8));
                    Window win = selectTimeDialog.getWindow();
                    WindowManager.LayoutParams params = win.getAttributes();
                    Point point = JSONUtil.getDeviceSize(getActivity());
                    params.width = point.x;
                    win.setGravity(Gravity.BOTTOM);
                    win.setWindowAnimations(R.style.dialog_anim_rise_style);
                }
                selectTimeDialog.show();
                break;
            case R.id.close:
                selectTimeDialog.dismiss();
                break;
            case R.id.confirm:
                Calendar nowCalendar = Calendar.getInstance();
                if (tempCalendar != null && tempCalendar.before(nowCalendar)) {
                    Util.showToast(getActivity(), null, R.string.msg_wrong_time);
                    return;
                } else {
                    selectTimeDialog.dismiss();
                    if (tempCalendar != null) {
                        if (calendar == null) {
                            calendar = Calendar.getInstance();
                        }
                        calendar.setTime(tempCalendar.getTime());
                        weddingDate.setText(new DateTime(tempCalendar.getTime()).toString
                                (Constants.DATE_FORMAT_SHORT));
                        doneBtn.setEnabled(calendar != null && !images.isEmpty());
                    }
                }
                break;
        }
    }

    @Override
    public void onPickerDate(int year, int month, int day) {
        if (tempCalendar == null) {
            tempCalendar = new GregorianCalendar(year, month - 1, day);
        } else {
            tempCalendar.set(year, month - 1, day);
        }

    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'protocol_image_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.delete)
        ImageButton delete;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private class OnDeleteClickListener implements View.OnClickListener {

        private View view;
        private Photo photo;

        private OnDeleteClickListener(View view, Photo photo) {
            this.view = view;
            this.photo = photo;
        }

        @Override
        public void onClick(View v) {
            protocolImagesLayout.removeView(view);
            if (images.size() == 6) {
                protocolImagesLayout.addView(addView, imageViewSize, imageViewSize);
            }
            images.remove(photo);
            doneBtn.setEnabled(calendar != null && !images.isEmpty());
        }
    }

    private class OnItemClickListener implements View.OnClickListener {

        private Photo photo;

        private OnItemClickListener(Photo photo) {
            this.photo = photo;
        }

        @Override
        public void onClick(View v) {
            if (photo != null) {
                Intent intent = new Intent(getActivity(), PicsPageViewActivity.class);
                intent.putExtra("photos", images);
                intent.putExtra("position", images.indexOf(photo));
                startActivity(intent);
            } else {
                if (selectImageDialog != null && selectImageDialog.isShowing()) {
                    return;
                }
                if (selectImageDialog == null) {
                    selectImageDialog = new Dialog(getActivity(), R.style.BubbleDialogTheme);
                    selectImageDialog.setContentView(R.layout.dialog_add_menu);
                    selectImageDialog.findViewById(R.id.action_cancel)
                            .setOnClickListener(CustomOrderProtocolEditFragment.this);
                    selectImageDialog.findViewById(R.id.action_gallery)
                            .setOnClickListener(CustomOrderProtocolEditFragment.this);
                    selectImageDialog.findViewById(R.id.action_camera_photo)
                            .setOnClickListener(CustomOrderProtocolEditFragment.this);
                    selectImageDialog.findViewById(R.id.action_camera_video)
                            .setVisibility(View.GONE);
                    Point point = JSONUtil.getDeviceSize(getActivity());
                    Window win = selectImageDialog.getWindow();
                    ViewGroup.LayoutParams params = win.getAttributes();
                    params.width = point.x;
                    win.setGravity(Gravity.BOTTOM);
                    win.setWindowAnimations(R.style.dialog_anim_rise_style);
                }
                selectImageDialog.show();
            }

        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.PROTOCOL_IMAGE_FOR_GALLERY:
                    if (data == null) {
                        return;
                    }
                    ArrayList<Photo> selectedPhotos = (ArrayList<Photo>) data.getSerializableExtra(
                            "selectedPhotos");
                    int index = protocolImagesLayout.indexOfChild(addView);
                    if (selectedPhotos != null && !selectedPhotos.isEmpty() && index >= 0) {
                        for (Photo image : selectedPhotos) {
                            Photo photo = new Photo(new JSONObject());
                            photo.setHeight(image.getHeight());
                            photo.setWidth(image.getWidth());
                            photo.setImagePath(image.getImagePath());
                            images.add(photo);
                            View view = View.inflate(getActivity(),
                                    R.layout.protocol_image_item,
                                    null);
                            setImageValue(photo, view);
                            protocolImagesLayout.addView(view,
                                    index++,
                                    new ViewGroup.LayoutParams(imageViewSize, imageViewSize));
                            if (images.size() == 6) {
                                protocolImagesLayout.removeView(addView);
                                break;
                            }
                        }
                        doneBtn.setEnabled(calendar != null && !images.isEmpty());
                    }
                    break;
                case Constants.RequestCode.PROTOCOL_IMAGE_FOR_CAMERA:
                    String path = JSONUtil.getImagePathForUri(currentUri, getActivity());
                    index = protocolImagesLayout.indexOfChild(addView);
                    if (!JSONUtil.isEmpty(path) && index >= 0) {
                        Size size = JSONUtil.getImageSizeFromPath(path);
                        Photo photo = new Photo(new JSONObject());
                        photo.setImagePath(path);
                        photo.setWidth(size.getWidth());
                        photo.setHeight(size.getHeight());
                        images.add(photo);
                        View view = View.inflate(getActivity(), R.layout.protocol_image_item, null);
                        setImageValue(photo, view);
                        protocolImagesLayout.addView(view,
                                index,
                                new ViewGroup.LayoutParams(imageViewSize, imageViewSize));
                        if (images.size() == 6) {
                            protocolImagesLayout.removeView(addView);
                        }
                        doneBtn.setEnabled(calendar != null && !images.isEmpty());
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void setOrder(CustomSetmealOrder order) {
        messageStr = message.getText()
                .toString();
        if (order != null) {
            order.setMessage(messageStr);
            order.setProtocolPhotos(images);
            if (calendar != null) {
                order.setWeddingTime(calendar.getTime());
            }
        }
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onTakePhotos() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = FileUtil.createImageFile();
        currentUri = Uri.fromFile(file);
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(getContext(),
                    getContext().getPackageName(),file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        } else {
            imageUri = currentUri;
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, Constants.RequestCode.PROTOCOL_IMAGE_FOR_CAMERA);
        selectImageDialog.dismiss();
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onRationaleCamera(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(getActivity(),
                request,
                getString(R.string.permission_r_for_camera));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        CustomOrderProtocolEditFragmentPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}