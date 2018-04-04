package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.TextCountWatcher;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.CustomSetmealOrder;
import me.suncloud.marrymemo.model.JsonPic;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.Photo;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.task.QiNiuUploadTask;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.widget.CheckableLinearLayout2;
import me.suncloud.marrymemo.widget.RoundProgressDialog;

/**
 * Created by Suncloud on 2015/6/10. 用于给本地服务订单和定制套餐订单的评价页面
 */
public class CommentNewWorkActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<JsonPic>, AdapterView.OnItemClickListener {
    @BindView(R.id.rating_layout)
    LinearLayout ratingLayout;
    @BindView(R.id.content)
    EditText etContent;
    @BindView(R.id.tv_comment_hint)
    TextView tvCommentHint;
    @BindView(R.id.pics_list)
    GridView picsList;
    private ObjectBindAdapter<JsonPic> adapter;
    private LinkedList<JsonPic> jsonPics;
    private RoundProgressDialog progressDialog;
    private Dialog dialog;
    private JsonPic addPic;//kind -1
    private JsonPic addPicHint;//kind -2
    private CustomSetmealOrder customSetmealOrder;
    private boolean isCustomOrder;
    private boolean isPlanner;
    private boolean isTestStore;
    private long plannerId;
    private long testStoreId;
    private int itemHeight;
    private int mRating;
    private int currentIndex;
    //1：去过实体店 、2：买过套餐
    private int knowType;
    //评论的类型 套餐 Constants.ENTRY_ITEM.WORK 商家 Constants.ENTRY_ITEM.MERCHANT
    private int commentType;
    private int imageWidth;
    private final int ADDIMAGE = -1;
    private final int ADDIMAGEHINT = -2;
    private final int DELETE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isCustomOrder = getIntent().getBooleanExtra("is_custom_order", false);
        commentType = getIntent().getIntExtra("commentType", 0);
        knowType = getIntent().getIntExtra("know_type", 0);
        isPlanner = getIntent().getBooleanExtra("isPlanner", false);
        isTestStore = getIntent().getBooleanExtra("isTestStore", false);
        mRating = getIntent().getIntExtra("rating", 0);
        if (isCustomOrder) {
            customSetmealOrder = (CustomSetmealOrder) getIntent().getSerializableExtra(
                    "custom_order");
        } else if (isPlanner) {  //是否是统筹师评论
            plannerId = getIntent().getLongExtra("plannerId", 0);
        } else if (isTestStore) {
            testStoreId = getIntent().getLongExtra("testStoreId", 0);
        }
        Point point = CommonUtil.getDeviceSize(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        imageWidth = Math.round((point.x - dm.density * (24 + 30 + 20) * 1.0f) / 4);
        itemHeight = imageWidth + Math.round(dm.density * 20);
        jsonPics = new LinkedList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_new_work);
        ButterKnife.bind(this);
        for (int i = 0, size = ratingLayout.getChildCount(); i < size; i++) {
            CheckableLinearLayout2 view = (CheckableLinearLayout2) ratingLayout.getChildAt(i);
            view.setOnClickListener(new OnRatingClickListener(i + 1));
            view.setChecked(i < mRating);
        }
        etContent.setHint(commentType == Constants.ENTRY_ITEM.MERCHANT ? R.string
                .hint_work_comment3 : R.string.hint_work_comment2);
        etContent.addTextChangedListener(new TextCountWatcher(etContent, 2000));
        tvCommentHint.setText(CommonUtil.fromHtml(this, getString(R.string.html_comment_hint)));
        tvCommentHint.setVisibility(isShowCommentTip() ? View.VISIBLE : View.GONE);
        initAddPic();
        adapter = new ObjectBindAdapter<>(this, jsonPics, R.layout.image_item_s, this);
        picsList.setAdapter(adapter);
        picsList.setOnItemClickListener(this);
    }

    private void initAddPic() {
        addPic = new JsonPic(new JSONObject());
        addPic.setKind(ADDIMAGE);
        addPicHint = new JsonPic(new JSONObject());
        addPicHint.setKind(ADDIMAGEHINT);
        addAddPicJson();
    }

    /**
     * GridView 添加 添加图片按钮
     */
    private void addAddPicJson() {
        if (jsonPics.contains(addPic)) {
            jsonPics.remove(addPic);
        }
        if (jsonPics.contains(addPicHint)) {
            jsonPics.remove(addPicHint);
        }
        if (jsonPics.size() < 9) {
            jsonPics.addLast(addPic);
        }
        if (jsonPics.size() == 1) {
            jsonPics.addLast(addPicHint);
        }
        if (jsonPics.size() > 4 && jsonPics.size() < 9) {
            picsList.getLayoutParams().height = itemHeight * 2;
        } else if (jsonPics.size() <= 4) {
            picsList.getLayoutParams().height = itemHeight;
        } else if (jsonPics.size() == 9) {
            picsList.getLayoutParams().height = itemHeight * 3;
        }
        tvCommentHint.setVisibility(isShowCommentTip() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBackPressed() {
        hideKeyboard(null);
        dialog = DialogUtil.createDoubleButtonDialog(dialog,
                this,
                getString(R.string.label_share_hint),
                getString(R.string.label_ok),
                getString(R.string.label_refuse),
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        CommentNewWorkActivity.super.onBackPressed();
                    }
                });
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void onComment(View view) {
        if (mRating == 0) {
            Toast.makeText(this, R.string.msg_comment_merchant, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (tvCommentHint.getVisibility() == View.VISIBLE) {
            ToastUtil.showToast(this, null, R.string.hint_comment_add_photos);
            return;
        }
        if (etContent.length() < 15) {
            Toast toast = Toast.makeText(this,
                    R.string.msg_comment_content_length1,
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        if (progressDialog == null) {
            progressDialog = JSONUtil.getRoundProgress(this);
        }
        progressDialog.setCancelable(true);
        progressDialog.show();
        if (!CommonUtil.isCollectionEmpty(jsonPics)) {
            if (jsonPics.contains(addPic)) {
                jsonPics.remove(addPic);
            }
            if (jsonPics.contains(addPicHint)) {
                jsonPics.remove(addPicHint);
            }
            currentIndex = 0;
            upLoadPic();
        } else {
            upLoadCommentInfo();
        }
    }

    private void upLoadPic() {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        if (currentIndex < jsonPics.size()) {
            JsonPic photo = jsonPics.get(currentIndex);
            progressDialog.setMessage(currentIndex + 1 + "/" + (jsonPics.size()));
            if (!JSONUtil.isEmpty(photo.getPath()) && !photo.getPath()
                    .startsWith("http://") && !photo.getPath()
                    .startsWith("https://")) {
                new QiNiuUploadTask(this, new OnHttpRequestListener() {

                    @Override
                    public void onRequestFailed(Object obj) {}

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
                            JsonPic pic = jsonPics.get(currentIndex);
                            if (width > 0 && height > 0) {
                                pic.setWidth(width);
                                pic.setHeight(height);
                            }
                            pic.setPath(path);
                            currentIndex++;
                            upLoadPic();
                        } else {
                            progressDialog.dismiss();
                        }
                    }
                }, progressDialog).execute(Constants.getAbsUrl(Constants.HttpPath.QINIU_IMAGE_URL),
                        new File(photo.getPath()));
            } else {
                currentIndex++;
                upLoadPic();
            }
        } else {
            upLoadCommentInfo();
        }
    }

    private void upLoadCommentInfo() {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        progressDialog.setCancelable(false);
        progressDialog.onLoadComplate();
        String url = null;
        try {
            JSONObject jsonObject = new JSONObject();
            if (isCustomOrder && customSetmealOrder != null) {
                jsonObject.put("id", customSetmealOrder.getId());
                jsonObject.put("set_meal_id",
                        customSetmealOrder.getCustomSetmeal()
                                .getId());
                url = Constants.getAbsUrl(Constants.HttpPath.CUSTOM_SETMEAL_ORDER_COMMENT);
            } else if (isPlanner && plannerId > 0) {
                jsonObject.put("entity_id", plannerId);
                jsonObject.put("entity_type", "TestStoreOrganizer");
                url = Constants.getAbsUrl(Constants.HttpPath.COMMENT_TEST_STORE);
            } else if (isTestStore && testStoreId > 0) {
                jsonObject.put("entity_id", testStoreId);
                jsonObject.put("entity_type", "TestStore");
                url = Constants.getAbsUrl(Constants.HttpPath.COMMENT_TEST_STORE);
            }
            jsonObject.put("rating", mRating);
            jsonObject.put("content",
                    etContent.getText()
                            .toString());
            if (!CommonUtil.isCollectionEmpty(jsonPics)) {
                JSONArray array = new JSONArray();
                for (JsonPic pic : jsonPics) {
                    if (!TextUtils.isEmpty(pic.getPath())) {
                        JSONObject picJson = new JSONObject();
                        picJson.put("img", pic.getPath());
                        picJson.put("height", pic.getHeight());
                        picJson.put("width", pic.getWidth());
                        array.put(picJson);
                    }
                }
                if (array.length() > 0) {
                    jsonObject.put("imgs", array);
                }
            }
            Log.i("picJson", jsonObject.toString());
            new NewHttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    JSONObject jsonObject = (JSONObject) obj;
                    if (jsonObject != null && !jsonObject.isNull("status")) {
                        ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject(
                                "status"));
                        if (returnStatus.getRetCode() == 0) {
                            Toast.makeText(CommentNewWorkActivity.this,
                                    R.string.msg_comment_success,
                                    Toast.LENGTH_SHORT)
                                    .show();
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.onComplate();
                                progressDialog.setCancelable(false);
                                progressDialog.setOnUpLoadComplate(new RoundProgressDialog
                                        .OnUpLoadComplate() {

                                    @Override
                                    public void onUpLoadCompleted() {
                                        onUpLoadDone();
                                    }
                                });
                            } else {
                                onUpLoadDone();
                            }
                        } else {
                            progressDialog.dismiss();
                            if (!JSONUtil.isEmpty(returnStatus.getErrorMsg())) {
                                Toast.makeText(CommentNewWorkActivity.this,
                                        returnStatus.getErrorMsg(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                Toast.makeText(CommentNewWorkActivity.this,
                                        R.string.msg_comment_err,
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(CommentNewWorkActivity.this,
                                R.string.msg_comment_err,
                                Toast.LENGTH_SHORT)
                                .show();
                    }

                }

                @Override
                public void onRequestFailed(Object obj) {
                    progressDialog.dismiss();
                    Toast.makeText(CommentNewWorkActivity.this,
                            R.string.msg_comment_err,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }, progressDialog).execute(url, jsonObject.toString());
        } catch (JSONException e) {
            progressDialog.dismiss();
            e.printStackTrace();
        }
    }

    private void onUpLoadDone() {
        if (isCustomOrder) {
            EventBus.getDefault()
                    .post(new MessageEvent(MessageEvent.EventType.CUSTOM_SETMEAL_ORDER_REFRESH_FLAG,
                            null));
        } else if (isTestStore) {
            EventBus.getDefault()
                    .post(new MessageEvent(MessageEvent.EventType.COMMENT_TEST_STORE_ACTIVITY,
                            null));
        } else if (isPlanner) {
            EventBus.getDefault()
                    .post(new MessageEvent(MessageEvent.EventType.COMMENT_PLANNER_ACTIVITY, null));
        }
        super.onBackPressed();
    }

    public void selectPhoto() {
        if (jsonPics.size() < 10) {
            Intent intent = new Intent(CommentNewWorkActivity.this, ImageChooserActivity.class);
            intent.putExtra("limit",
                    Math.min(jsonPics.contains(addPicHint) ? 11 - jsonPics.size() : 10 - jsonPics
                                    .size(),
                            9));
            startActivityForResult(intent, Constants.RequestCode.PHOTO_FROM_GALLERY);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else {
            Toast toast = Toast.makeText(CommentNewWorkActivity.this,
                    R.string.hint_post_photos_limit,
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.PHOTO_FROM_GALLERY:
                    if (data != null) {
                        ArrayList<com.hunliji.hljcommonlibrary.models.Photo> selectedPhotos =
                                data.getParcelableArrayListExtra(
                                "selectedPhotos");
                        for (com.hunliji.hljcommonlibrary.models.Photo photo : selectedPhotos) {
                            JsonPic jsonPic = new JsonPic(new JSONObject());
                            jsonPic.setHeight(photo.getHeight());
                            jsonPic.setWidth(photo.getWidth());
                            jsonPic.setPath(photo.getImagePath());
                            jsonPic.setKind(2);
                            jsonPics.add(jsonPic);
                        }
                        addAddPicJson();
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case DELETE:
                    ArrayList<Photo> deleteData = (ArrayList<Photo>) data.getSerializableExtra(
                            "deleteData");
                    if (deleteData != null) {
                        for (int i = 0; i < deleteData.size(); i++) {
                            Photo photo = deleteData.get(i);
                            for (int j = 0; j < jsonPics.size(); j++) {
                                JsonPic pic = jsonPics.get(j);
                                if (photo.getPath()
                                        .equalsIgnoreCase(pic.getPath())) {
                                    jsonPics.remove(pic);
                                }
                            }
                        }
                        addAddPicJson();
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setViewValue(View view, final JsonPic jsonPic, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.icon);
            holder.addView = (ImageView) view.findViewById(R.id.add_image);
            holder.deleteView = view.findViewById(R.id.delete);
            holder.addViewHint = (TextView) view.findViewById(R.id.add_image_hint);
            holder.itemView = view.findViewById(R.id.image_layout);
            view.setTag(holder);
        }
        ViewGroup.LayoutParams imageParams = holder.imageView.getLayoutParams();
        if (imageParams != null) {
            imageParams.width = imageWidth;
            imageParams.height = imageWidth;
        }

        holder.addView.setPadding(0, 0, 0, 0);
        ViewGroup.LayoutParams addParams = holder.addView.getLayoutParams();
        if (addParams != null) {
            addParams.width = imageWidth;
            addParams.height = imageWidth;
        }

        ViewGroup.LayoutParams addHintParams = holder.addViewHint.getLayoutParams();
        if (addHintParams != null) {
            addHintParams.width = imageWidth;
            addHintParams.height = imageWidth;
        }

        if (!JSONUtil.isEmpty(jsonPic.getPath())) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.addView.setVisibility(View.GONE);
            holder.addViewHint.setVisibility(View.GONE);
            holder.deleteView.setVisibility(View.VISIBLE);
            ImageLoadTask task = new ImageLoadTask(holder.imageView, 0);
            task.loadImage(jsonPic.getPath(),
                    imageWidth,
                    ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
            view.findViewById(R.id.delete)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            jsonPics.remove(jsonPic);
                            if (jsonPics.size() == 1) {
                                jsonPics.clear();
                            }
                            addAddPicJson();
                            adapter.notifyDataSetChanged();
                        }
                    });
        } else {
            int kind = jsonPic.getKind();
            if (kind == ADDIMAGE) {
                holder.addView.setImageDrawable(ContextCompat.getDrawable(this,
                        R.drawable.icon_cross_add_white_gray));
                holder.addView.setVisibility(View.VISIBLE);
            } else if (kind == ADDIMAGEHINT) {
                holder.addView.setVisibility(View.GONE);
                holder.addViewHint.setVisibility(View.VISIBLE);
            }
            holder.imageView.setVisibility(View.GONE);
            holder.deleteView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JsonPic jsonPic = (JsonPic) parent.getAdapter()
                .getItem(position);
        if (jsonPic != null && JSONUtil.isEmpty(jsonPic.getPath())) {
            selectPhoto();
        } else {
            ArrayList<Photo> viewPhotos = new ArrayList<>();
            for (JsonPic pic : jsonPics) {
                if (pic == addPic || pic == addPicHint) {
                    continue;
                }
                Photo photo = new Photo(null);
                photo.setPath(pic.getPath());
                viewPhotos.add(photo);
            }
            Intent intent = new Intent(this, LocalPicsPageViewActivity.class);
            intent.putExtra("photos", viewPhotos);
            intent.putExtra("position", position);
            intent.putExtra("delete", true);
            intent.putExtra("showActionLayout", true);
            intent.putExtra("showIndicator", false);
            startActivityForResult(intent, DELETE);
        }
    }

    private class OnRatingClickListener implements View.OnClickListener {

        private int rating;

        private OnRatingClickListener(int rating) {
            this.rating = rating;
        }

        @Override
        public void onClick(View v) {
            if (rating != mRating) {
                mRating = rating;
                tvCommentHint.setVisibility(isShowCommentTip() ? View.VISIBLE : View.GONE);
                for (int i = 0, size = ratingLayout.getChildCount(); i < size; i++) {
                    ((CheckableLinearLayout2) ratingLayout.getChildAt(i)).setChecked(i < mRating);
                }
            }
        }
    }

    private class ViewHolder {
        ImageView imageView;
        ImageView addView;
        TextView addViewHint;
        View deleteView;
        View itemView;
    }

    private boolean isShowCommentTip() {
        return mRating > 0 && mRating <= 3 && jsonPics.contains(addPicHint) && !isCustomOrder;
    }
}