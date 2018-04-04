package com.hunliji.marrybiz.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.fragment.CustomOrderPriceEditFragment;
import com.hunliji.marrybiz.fragment.CustomOrderProtocolEditFragment;
import com.hunliji.marrybiz.model.CustomSetmealOrder;
import com.hunliji.marrybiz.model.MessageEvent;
import com.hunliji.marrybiz.model.Photo;
import com.hunliji.marrybiz.model.Status;
import com.hunliji.marrybiz.task.NewHttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.task.QiNiuUploadTask;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.widget.RoundProgressDialog;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by Suncloud on 2016/2/3.
 */
public class CustomOrderEditActivity extends HljBaseActivity {
    @BindView(R.id.hint)
    TextView hint;
    @BindView(R.id.line1)
    View line1;
    @BindView(R.id.line2)
    View line2;
    @BindView(R.id.img_dot1)
    ImageView imgDot1;
    @BindView(R.id.img_dot2)
    ImageView imgDot2;
    @BindView(R.id.title1)
    TextView title1;
    @BindView(R.id.title2)
    TextView title2;
    @BindView(R.id.accept_order_layout)
    RelativeLayout acceptOrderLayout;
    @BindView(R.id.done_btn)
    Button doneBtn;

    private boolean isEdit;
    private int editType;
    private CustomSetmealOrder order;
    private RoundProgressDialog progressDialog;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isEdit = getIntent().getBooleanExtra("isEdit", false);
        editType = getIntent().getIntExtra("editType", 0);
        order = (CustomSetmealOrder) getIntent().getSerializableExtra("order");
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_custom_order_edit);
        ButterKnife.bind(this);
        acceptOrderLayout.setVisibility(isEdit ? View.GONE : View.VISIBLE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (isEdit && editType == 1) {
            CustomOrderPriceEditFragment customOrderPriceEditFragment =
                    (CustomOrderPriceEditFragment) Fragment.instantiate(
                    this,
                    CustomOrderPriceEditFragment.class.getName());
            Bundle args = new Bundle();
            args.putSerializable("order", order);
            customOrderPriceEditFragment.setArguments(args);
            ft.add(R.id.fragment_content,
                    customOrderPriceEditFragment,
                    "customOrderPriceEditFragment");
            setTitle(R.string.title_activity_change_order_price);
        } else {
            CustomOrderProtocolEditFragment customOrderProtocolEditFragment =
                    (CustomOrderProtocolEditFragment) Fragment.instantiate(
                    this,
                    CustomOrderProtocolEditFragment.class.getName());
            Bundle args = new Bundle();
            args.putSerializable("order", order);
            customOrderProtocolEditFragment.setArguments(args);
            ft.add(R.id.fragment_content,
                    customOrderProtocolEditFragment,
                    "customOrderProtocolEditFragment");
            if (isEdit) {
                setTitle(R.string.label_edit_protocol);
            } else {
                doneBtn.setText(R.string.label_next);
                getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager
                        .OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            hint.setText(R.string.hint_change_order_price2);
                            title1.setTextColor(getResources().getColor(R.color.colorPrimary));
                            line1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            imgDot1.setImageResource(R.drawable.icon_status_dot_r);
                            doneBtn.setText(R.string.label_accept_order3);
                        } else {
                            hint.setText(R.string.hint_uploading_protocol);
                            title1.setTextColor(getResources().getColor(R.color.colorGray3));
                            line1.setBackgroundColor(getResources().getColor(R.color.gray8));
                            imgDot1.setImageResource(R.drawable.icon_status_dot_g);
                            doneBtn.setText(R.string.label_next);
                        }
                    }
                });
            }
        }
        ft.commit();
    }

    public void onDone(View view) {
        hideKeyboard(null);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_content);
        if (fragment != null) {
            if (fragment instanceof CustomOrderProtocolEditFragment) {
                ((CustomOrderProtocolEditFragment) fragment).setOrder(order);
                if (!isEdit) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    CustomOrderPriceEditFragment customOrderPriceEditFragment =
                            (CustomOrderPriceEditFragment) Fragment.instantiate(
                            this,
                            CustomOrderPriceEditFragment.class.getName());
                    Bundle args = new Bundle();
                    args.putSerializable("order", order);
                    customOrderPriceEditFragment.setArguments(args);
                    ft.replace(R.id.fragment_content,
                            customOrderPriceEditFragment,
                            "customOrderPriceEditFragment");
                    ft.addToBackStack(null);
                    ft.commit();
                    return;
                }
            } else if (fragment instanceof CustomOrderPriceEditFragment && !(
                    (CustomOrderPriceEditFragment) fragment).setOrder(
                    order)) {
                return;
            }
        }
        showHintDialog(1);
    }

    private void onUpload() {
        if (progressDialog == null) {
            progressDialog = JSONUtil.getRoundProgress(this);
            if (!isEdit) {
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        title2.setTextColor(getResources().getColor(R.color.colorGray3));
                        line2.setBackgroundColor(getResources().getColor(R.color.gray8));
                        imgDot2.setImageResource(R.drawable.icon_status_dot_g);
                    }
                });
            }
        }
        title2.setTextColor(getResources().getColor(R.color.colorPrimary));
        line2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        imgDot2.setImageResource(R.drawable.icon_status_dot_r);
        progressDialog.show();
        upLoadPic(0);
    }

    private void upLoadInfo() {
        if (order == null) {
            return;
        }
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", order.getId());
            jsonObject.put("actual_price", order.getActualPrice());
            jsonObject.put("earnest_money", order.getEarnestMoney());
            jsonObject.put("message", order.getMessage());
            if (order.getWeddingTime() != null) {
                jsonObject.put("wedding",
                        new DateTime(order.getWeddingTime()).toString(Constants.DATE_FORMAT_LONG));
            }
            if (!isEdit) {
                jsonObject.put("is_receiving", true);
            }
            JSONArray array = new JSONArray();
            for (Photo photo : order.getProtocolPhotos()) {
                if (!JSONUtil.isEmpty(photo.getImagePath())) {
                    JSONObject object = new JSONObject();
                    object.put("height", photo.getHeight());
                    object.put("width", photo.getWidth());
                    object.put("img", photo.getImagePath());
                    array.put(object);
                }
            }
            jsonObject.put("protocol_images", array);
            progressDialog.setCancelable(false);
            progressDialog.onLoadComplate();
            new NewHttpPostTask(this, new OnHttpRequestListener() {

                @Override
                public void onRequestFailed(Object obj) {
                    progressDialog.dismiss();
                    Util.showToast(CustomOrderEditActivity.this,
                            null,
                            R.string.msg_failed_submit_account_info);
                }

                @Override
                public void onRequestCompleted(Object obj) {
                    JSONObject object = (JSONObject) obj;
                    Status status = null;
                    if (object != null && object.optJSONObject("status") != null) {
                        status = new Status(object.optJSONObject("status"));
                    }
                    if (status != null && status.getRetCode() == 0) {
                        if (object.optJSONObject("data") != null) {
                            order = new CustomSetmealOrder(object.optJSONObject("data"));
                            EventBus.getDefault()
                                    .post(new MessageEvent(8, order));
                        }
                        if (progressDialog == null || !progressDialog.isShowing()) {
                            finish();
                            overridePendingTransition(0, R.anim.slide_out_right);
                        } else {
                            progressDialog.setCancelable(false);
                            progressDialog.onComplate();
                            progressDialog.setOnUpLoadComplate(new RoundProgressDialog
                                    .OnUpLoadComplate() {

                                @Override
                                public void onUpLoadCompleted() {
                                    finish();
                                    overridePendingTransition(0, R.anim.slide_out_right);
                                }
                            });
                        }
                    } else {
                        progressDialog.dismiss();
                        Util.showToast(CustomOrderEditActivity.this,
                                status == null ? null : status.getErrorMsg(),
                                R.string.msg_failed_submit_account_info);
                    }

                }
            }, progressDialog).execute(Constants.getAbsUrl(Constants.HttpPath.EDIT_CUSTOM_ORDER),
                    jsonObject.toString());
        } catch (JSONException ignored) {

        }
    }

    private void upLoadPic(int currentIndex) {
        if (order == null) {
            return;
        }
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        int size = order.getProtocolPhotos()
                .size();
        if (currentIndex < size) {
            Photo photo = order.getProtocolPhotos()
                    .get(currentIndex);
            if (!JSONUtil.isEmpty(photo.getImagePath()) && !photo.getImagePath()
                    .startsWith("http://")) {
                progressDialog.setMessage(currentIndex + 1 + "/" + size);
                new QiNiuUploadTask(this,
                        new ImageUploadRequestListener(photo),
                        progressDialog).execute(Constants.getAbsUrl(Constants.HttpPath
                        .QINIU_IMAGE_URL),
                        new File(photo.getImagePath()));
            } else {
                upLoadPic(++currentIndex);
            }
        } else {
            upLoadInfo();
        }
    }


    private class ImageUploadRequestListener implements OnHttpRequestListener {

        private Photo pic;

        private ImageUploadRequestListener(Photo pic) {
            this.pic = pic;
        }

        @Override
        public void onRequestCompleted(Object obj) {
            if (isFinishing()) {
                return;
            }
            JSONObject json = (JSONObject) obj;
            String path = null;
            int width = 0;
            int height = 0;
            if (json != null) {
                String key = JSONUtil.getString(json, "image_path");
                String domain = JSONUtil.getString(json, "domain");
                width = json.optInt("width", 0);
                height = json.optInt("height", 0);
                if (!JSONUtil.isEmpty(key) && !JSONUtil.isEmpty(domain)) {
                    path = domain + key;
                }
            }
            if (!JSONUtil.isEmpty(path)) {
                int index = order.getProtocolPhotos()
                        .indexOf(pic);
                if (index >= 0) {
                    if (width > 0 && height > 0) {
                        pic.setWidth(width);
                        pic.setHeight(height);
                    }
                    pic.setImagePath(path);
                    upLoadPic(++index);
                }
            } else {
                progressDialog.dismiss();
                Util.showToast(CustomOrderEditActivity.this,
                        null,
                        R.string.msg_failed_submit_account_info);
            }
        }

        @Override
        public void onRequestFailed(Object obj) {
            progressDialog.dismiss();
            Util.showToast(CustomOrderEditActivity.this,
                    null,
                    R.string.msg_failed_submit_account_info);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            showHintDialog(0);
        }
    }

    private void showHintDialog(final int type) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (dialog == null) {
            dialog = new Dialog(this, R.style.BubbleDialogTheme);
            dialog.setContentView(R.layout.dialog_confirm_notice);
            dialog.findViewById(R.id.btn_notice_cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
        }
        dialog.findViewById(R.id.btn_notice_confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (type == 0) {
                            finish();
                            overridePendingTransition(0, R.anim.slide_out_right);
                        } else {
                            onUpload();
                        }
                    }
                });
        TextView noticeMsg = (TextView) dialog.findViewById(R.id.tv_notice_msg);
        if (isEdit) {
            if (type == 0) {
                noticeMsg.setText(editType == 0 ? R.string.hint_order_edit_exit : R.string.hint_order_price_edit_exit);
            } else {
                noticeMsg.setText(editType == 0 ? R.string.hint_order_edit_accept : R.string.hint_order_price_edit_accept);
            }
        } else {
            TextView noticeTitle = (TextView) dialog.findViewById(R.id.tv_msg_title);
            noticeTitle.setVisibility(View.VISIBLE);
            if (type == 0) {
                noticeTitle.setText(R.string.title_order_exit);
                noticeMsg.setText(R.string.hint_order_exit);
            } else {
                noticeTitle.setText(R.string.title_order_accept);
                noticeMsg.setText(R.string.hint_order_accept);
            }
        }
        dialog.show();

    }
}
