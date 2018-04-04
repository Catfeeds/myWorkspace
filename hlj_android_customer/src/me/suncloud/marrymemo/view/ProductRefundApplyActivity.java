package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wheelpickerlibrary.picker.SingleWheelPicker;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljcommonlibrary.utils.TextCountWatcher;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.JsonPic;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.RefundReason;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.orders.ProductOrder;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.task.QiNiuUploadTask;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.ImageLoadUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.RoundProgressDialog;

public class ProductRefundApplyActivity extends HljBaseActivity {

    @BindView(R.id.tv_refund_reason)
    TextView tvRefundReason;
    @BindView(R.id.tv_refund_reason2)
    TextView tvRefundReason2;
    @BindView(R.id.et_refund_money)
    EditText etRefundMoney;
    @BindView(R.id.tv_refund_amount_hint)
    TextView tvRefundAmountHint;
    @BindView(R.id.et_refund_explain)
    EditText etRefundExplain;
    @BindView(R.id.tv_text_counter)
    TextView tvTextCounter;
    @BindView(R.id.img_1)
    ImageView img1;
    @BindView(R.id.img_2)
    ImageView img2;
    @BindView(R.id.img_3)
    ImageView img3;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.btn_delete_1)
    ImageButton btnDelete1;
    @BindView(R.id.btn_delete_2)
    ImageButton btnDelete2;
    @BindView(R.id.btn_delete_3)
    ImageButton btnDelete3;
    public static final int MAX_DESC_COUNT = 200;
    @BindView(R.id.tv_refund_reason_label)
    TextView tvRefundReasonLabel;
    @BindView(R.id.tv_refund_explain_label)
    TextView tvRefundExplainLabel;
    private ArrayList<String> reasons;
    private ArrayList<RefundReason> reasonArray;
    private Dialog dialog;
    private RefundReason selectedReason;
    private ArrayList<JsonPic> photos;
    private RoundProgressDialog progressDialog;
    private long id;
    private double maxMoney;
    private double shippingFee;
    private int type; // 1:仅退款, 2:退款退货
    private ProductOrder order;
    private boolean isApplyAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_refund_apply);
        ButterKnife.bind(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        Point point = JSONUtil.getDeviceSize(this);
        int imgSize = Math.round((point.x - 4 * 12 * dm.density) / 3);
        img1.getLayoutParams().width = imgSize;
        img1.getLayoutParams().height = imgSize;
        img2.getLayoutParams().width = imgSize;
        img2.getLayoutParams().height = imgSize;
        img3.getLayoutParams().width = imgSize;
        img3.getLayoutParams().height = imgSize;

        id = getIntent().getLongExtra("id", 0);
        type = getIntent().getIntExtra("type", 1);
        if (type == 1) {
            setTitle(getString(R.string.title_activity_product_refund_apply));
            tvRefundReasonLabel.setText(R.string.label_refund_reason);
        } else {
            setTitle(getString(R.string.label_apply_return2));
            tvRefundReasonLabel.setText(R.string.label_return_reason);
        }
        order = getIntent().getParcelableExtra("order");
        isApplyAgain = getIntent().getBooleanExtra("apply_again", false);

        photos = new ArrayList<>();
        reasonArray = new ArrayList<>();
        reasons = new ArrayList<>();

        etRefundExplain.addTextChangedListener(new TextCountWatcher(etRefundExplain,
                tvTextCounter,
                MAX_DESC_COUNT));

        progressBar.setVisibility(View.VISIBLE);
        new GetRefundReasonsTask().execute();
        new GetRefundMaxMoney().execute();
    }

    @OnClick(R.id.btn_submit)
    void onSubmit() {
        if (selectedReason == null) {
            Toast.makeText(this, R.string.msg_empty_refund_reason, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (etRefundMoney.length() == 0) {
            Toast.makeText(this, R.string.msg_empty_refund_money, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        double refundMoney = Double.valueOf(etRefundMoney.getText()
                .toString());
        if (refundMoney > maxMoney) {
            Toast.makeText(this, R.string.msg_refund_money_over_high, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        progressDialog = JSONUtil.getRoundProgress(this);

        if (photos.size() > 0) {
            // 如果有图片则先上传图片,没有的话直接提交数据
            uploadPhotos(0);
        } else {
            postData();
        }
    }


    @OnClick(R.id.select_reason_layout)
    void onSelectReason() {
        if (reasonArray.isEmpty()) {
            return;
        }
        dialog = new Dialog(this, R.style.BubbleDialogTheme);

        View v = getLayoutInflater().inflate(R.layout.single_wheel_picker___cm, null);
        final SingleWheelPicker picker = (SingleWheelPicker) v.findViewById(R.id.picker);
        picker.setItems(reasons);

        TextView close = (TextView) v.findViewById(R.id.close);
        TextView confirm = (TextView) v.findViewById(R.id.confirm);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectIndex = picker.getCurrentItem();
                if (selectIndex < 0 || selectIndex > reasonArray.size() - 1) {
                    return;
                }
                selectedReason = reasonArray.get(selectIndex);
                tvRefundReason.setText(selectedReason.getName());
                if (!JSONUtil.isEmpty(selectedReason.getDesc())) {
                    tvRefundReason2.setVisibility(View.VISIBLE);
                    tvRefundReason2.setText(selectedReason.getDesc());
                } else {
                    tvRefundReason2.setVisibility(View.GONE);
                }
                dialog.cancel();
            }
        });
        dialog.setContentView(v);

        Window win = dialog.getWindow();
        ViewGroup.LayoutParams params = win.getAttributes();
        Point point = new Point();
        getWindowManager().getDefaultDisplay()
                .getSize(point);
        params.width = point.x;
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialog_anim_rise_style);
        dialog.show();
    }

    @OnClick({R.id.img_1, R.id.img_2, R.id.img_3})
    void onSelectPhotos(View view) {
        if ((photos.size() == 0 && view.getId() == R.id.img_1) || (photos.size() == 1 && view
                .getId() == R.id.img_2) || (photos.size() == 2 && view.getId() == R.id.img_3)) {
            Intent intent = new Intent(this, ImageChooserActivity.class);
            intent.putExtra("limit", 3 - photos.size());
            startActivityForResult(intent, Constants.RequestCode.PHOTO_FROM_GALLERY);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @OnClick({R.id.btn_delete_1, R.id.btn_delete_2, R.id.btn_delete_3})
    void onDeletePhotos(View view) {
        switch (view.getId()) {
            case R.id.btn_delete_1:
                if (photos.size() >= 1) {
                    photos.remove(0);
                    setProofPhotos();
                }
                break;
            case R.id.btn_delete_2:
                if (photos.size() >= 2) {
                    photos.remove(1);
                    setProofPhotos();
                }
                break;
            case R.id.btn_delete_3:
                if (photos.size() >= 3) {
                    photos.remove(2);
                    setProofPhotos();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.PHOTO_FROM_GALLERY:
                    if (data != null) {
                        ArrayList<Photo> selectedPhotos = data.getParcelableArrayListExtra(
                                "selectedPhotos");

                        for (Photo photo : selectedPhotos) {
                            JsonPic jsonPic = new JsonPic(new JSONObject());
                            jsonPic.setHeight(photo.getHeight());
                            jsonPic.setWidth(photo.getWidth());
                            jsonPic.setPath(photo.getImagePath());
                            jsonPic.setKind(2);
                            photos.add(jsonPic);
                        }

                        setProofPhotos();
                    }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setProofPhotos() {
        if (photos.size() == 3) {
            ImageLoadUtil.loadImageView(this,
                    photos.get(0)
                            .getPath(),
                    img1);
            ImageLoadUtil.loadImageView(this,
                    photos.get(1)
                            .getPath(),
                    img2);
            ImageLoadUtil.loadImageView(this,
                    photos.get(2)
                            .getPath(),
                    img3);
            btnDelete1.setVisibility(View.VISIBLE);
            btnDelete2.setVisibility(View.VISIBLE);
            btnDelete3.setVisibility(View.VISIBLE);
            img1.setVisibility(View.VISIBLE);
            img2.setVisibility(View.VISIBLE);
            img3.setVisibility(View.VISIBLE);
        } else if (photos.size() == 2) {
            ImageLoadUtil.loadImageView(this,
                    photos.get(0)
                            .getPath(),
                    img1);
            ImageLoadUtil.loadImageView(this,
                    photos.get(1)
                            .getPath(),
                    img2);
            btnDelete1.setVisibility(View.VISIBLE);
            btnDelete2.setVisibility(View.VISIBLE);
            btnDelete3.setVisibility(View.GONE);
            img1.setVisibility(View.VISIBLE);
            img2.setVisibility(View.VISIBLE);
            img3.setVisibility(View.VISIBLE);
            img3.setImageDrawable(null);
        } else if (photos.size() == 1) {
            ImageLoadUtil.loadImageView(this,
                    photos.get(0)
                            .getPath(),
                    img1);
            btnDelete1.setVisibility(View.VISIBLE);
            btnDelete2.setVisibility(View.GONE);
            btnDelete3.setVisibility(View.GONE);
            img1.setVisibility(View.VISIBLE);
            img2.setVisibility(View.VISIBLE);
            img3.setVisibility(View.INVISIBLE);
            img2.setImageDrawable(null);
        } else {
            btnDelete1.setVisibility(View.GONE);
            btnDelete2.setVisibility(View.GONE);
            btnDelete3.setVisibility(View.GONE);
            img1.setVisibility(View.VISIBLE);
            img2.setVisibility(View.INVISIBLE);
            img3.setVisibility(View.INVISIBLE);
            img1.setImageDrawable(null);
        }
    }

    private void uploadPhotos(int currentIndex) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        if (currentIndex < photos.size()) {
            JsonPic pic = photos.get(currentIndex);
            if (!JSONUtil.isEmpty(pic.getPath()) && !pic.getPath()
                    .startsWith("http://")) {
                progressDialog.setMessage(currentIndex + 1 + "/" + photos.size());
                new QiNiuUploadTask(this,
                        new ImageUploadRequestListener(pic),
                        progressDialog).executeOnExecutor(Constants.UPLOADTHEADPOOL,
                        Constants.getAbsUrl(Constants.HttpPath.QINIU_IMAGE_URL),
                        new File(pic.getPath()));
            } else {
                uploadPhotos(currentIndex);
            }
        } else {
            postData();
        }
    }

    private void postData() {
        double refundMoney = Double.valueOf(etRefundMoney.getText()
                .toString());

        String explanation = "";
        if (etRefundExplain.length() > 0) {
            explanation = etRefundExplain.getText()
                    .toString();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_sub_id", id);
            jsonObject.put("money", refundMoney);
            jsonObject.put("type", type);
            jsonObject.put("reason_id", selectedReason.getId());
            jsonObject.put("desc", explanation);
            if (photos.size() > 0) {
                JSONArray jsonArray = new JSONArray();
                for (JsonPic pic : photos) {
                    JSONObject picObj = new JSONObject();
                    picObj.put("img", pic.getPath());
                    picObj.put("width", pic.getWidth());
                    picObj.put("height", pic.getHeight());
                    jsonArray.put(picObj);
                }

                jsonObject.put("proof_photos", jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        progressDialog.onLoadComplate();
        new StatusHttpPostTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                if (progressDialog == null || !progressDialog.isShowing()) {
                    onBackPressed();
                    return;
                }
                progressDialog.setCancelable(false);
                progressDialog.onComplate();
                progressDialog.setOnUpLoadComplate(new RoundProgressDialog.OnUpLoadComplate() {
                    @Override
                    public void onUpLoadCompleted() {
                        EventBus.getDefault()
                                .post(new MessageEvent(MessageEvent.EventType
                                        .PRODUCT_ORDER_REFRESH_FLAG,
                                        null));
                        EventBus.getDefault()
                                .post(new MessageEvent(MessageEvent.EventType
                                        .PRODUCT_REFUND_DETAIL_REFRESH_FLAG,
                                        null));
                        if (isApplyAgain) {
                            Intent intent = getIntent();
                            intent.putExtra("refresh", true);
                            setResult(RESULT_OK, intent);
                            onBackPressed();
                        } else {
                            Intent intent = new Intent(ProductRefundApplyActivity.this,
                                    ProductRefundDetailActivity.class);
                            intent.putExtra("order", order);
                            intent.putExtra("id", id);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }
                });
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressDialog.dismiss();
                Util.postFailToast(ProductRefundApplyActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_product_refund,
                        network);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_PRODUCT_REFUND),
                jsonObject.toString());
    }

    private class ImageUploadRequestListener implements OnHttpRequestListener {

        private JsonPic pic;

        private ImageUploadRequestListener(JsonPic pic) {
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
                int index = photos.indexOf(pic);
                if (index >= 0) {
                    if (width > 0 && height > 0) {
                        pic.setWidth(width);
                        pic.setHeight(height);
                    }
                    pic.setPath(path);
                    uploadPhotos(++index);
                }
            } else {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onRequestFailed(Object obj) {

        }
    }

    private class GetRefundReasonsTask extends AsyncTask<String, Object, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            String url = String.format(Constants.getAbsUrl(Constants.HttpPath
                            .GET_PRODUCT_ORDER_REFUND_REASON),
                    type);
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressBar.setVisibility(View.GONE);
            if (jsonObject != null && jsonObject.optJSONObject("status") != null && jsonObject
                    .optJSONObject(
                    "status")
                    .optInt("RetCode", -1) == 0) {
                JSONArray jsonArray = jsonObject.optJSONArray("data");
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RefundReason reason = new RefundReason(jsonArray.optJSONObject(i));
                        reasonArray.add(reason);
                        reasons.add(reason.getName());
                    }
                    if (!reasonArray.isEmpty()) {
                        selectedReason = reasonArray.get(0);
                        tvRefundReason.setText(selectedReason.getName());
                        if (!JSONUtil.isEmpty(selectedReason.getDesc())) {
                            tvRefundReason2.setVisibility(View.VISIBLE);
                            tvRefundReason2.setText(selectedReason.getDesc());
                        } else {
                            tvRefundReason2.setVisibility(View.GONE);
                        }
                    }
                }
            }

            findViewById(R.id.content_layout).setVisibility(View.VISIBLE);
            super.onPostExecute(jsonObject);
        }
    }

    private class GetRefundMaxMoney extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = String.format(Constants.getAbsUrl(Constants.HttpPath
                            .GET_PRODUCT_REFUND_MAX_MONEY),
                    id);
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus != null && returnStatus.getRetCode() == 0) {
                    JSONObject dataObj = jsonObject.optJSONObject("data");
                    maxMoney = dataObj.optDouble("max_money", 0);
                    shippingFee = dataObj.optDouble("shipping_fee", 0);
                    tvRefundAmountHint.setVisibility(View.VISIBLE);
                    if (order.getStatus() == 88) {
                        etRefundMoney.setText(NumberFormatUtil.formatDouble2StringWithTwoFloat(
                                maxMoney));
                        etRefundMoney.setFocusable(false);
                        tvRefundAmountHint.setVisibility(View.GONE);
                    } else {
                        tvRefundAmountHint.setText(getString(R.string.hint_refund_amount2,
                                NumberFormatUtil.formatDouble2StringWithTwoFloat(maxMoney),
                                NumberFormatUtil.formatDouble2StringWithTwoFloat(shippingFee)));
                        etRefundMoney.setText(NumberFormatUtil.formatDouble2StringWithTwoFloat(
                                maxMoney));
                    }
                } else {
                    tvRefundAmountHint.setVisibility(View.GONE);
                }
            }
            super.onPostExecute(jsonObject);
        }
    }
}
