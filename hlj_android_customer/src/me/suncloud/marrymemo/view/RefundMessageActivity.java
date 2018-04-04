package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.utils.TextCountWatcher;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.JsonPic;
import me.suncloud.marrymemo.model.Photo;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.task.QiNiuUploadTask;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.widget.RoundProgressDialog;

/**
 * 留言凭证activity
 * Created by jinxin on 2016/4/6.
 */
public class RefundMessageActivity extends HljBaseActivity {
    private final int MAX = 200;
    private EditText message;
    private TextView messageCount;
    private LinkedList<JsonPic> images;
    private JsonPic addImage;
    private RecyclerView list;
    private ImageAdapter imageAdapter;
    private int imageWidth;
    private RoundProgressDialog progressDialog;
    private int imageIndex;
    private List<Photo> upLoadCompletePhoto;
    private long orderSubId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderSubId = getIntent().getLongExtra("order_sub_id", 0);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        imageWidth = Math.round(dm.density * 90);
        setContentView(R.layout.activity_refund_message);
        message = (EditText) findViewById(R.id.message);
        messageCount = (TextView) findViewById(R.id.message_count);
        list = (RecyclerView) findViewById(R.id.image_list);
        message.addTextChangedListener(new TextCountWatcher(message, messageCount, MAX));
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        addImage = new JsonPic(new JSONObject());
        images = new LinkedList<>();
        addLastImage();
        imageAdapter = new ImageAdapter();
        list.setLayoutManager(manager);
        list.setAdapter(imageAdapter);
        upLoadCompletePhoto = new ArrayList<>();
    }

    public void onSubmit(View view) {
        String content = message.getText()
                .toString();
        if (JSONUtil.isEmpty(content)) {
            Toast.makeText(this, getString(R.string.label_refund_message2), Toast.LENGTH_SHORT)
                    .show();
            return;
        } else {
            if (progressDialog == null) {
                progressDialog = JSONUtil.getRoundProgress(this);
            }
            if (images.contains(addImage)) {
                images.remove(addImage);
            }
            if (images.size() <= 0) {
                progressDialog.setMessage((0) + "/" + images.size());
                onRefundMessage();
            } else {
                uploadImage();
            }
        }
    }

    private void addLastImage() {
        if (images.size() < 3) {
            if (images.contains(addImage)) {
                images.remove(addImage);
            }
            images.addLast(addImage);
        }
    }

    private void deleteImage(int position) {
        if (position < images.size()) {
            images.remove(position);
        }
        addLastImage();
        imageAdapter.notifyDataSetChanged();
    }

    private void uploadImage() {
        if (progressDialog == null) {
            return;
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        progressDialog.setMessage((imageIndex + 1) + "/" + images.size());
        JsonPic pic = images.get(imageIndex);
        String uploadUrl = Constants.getAbsUrl(Constants.HttpPath.QINIU_IMAGE_URL);
        new QiNiuUploadTask(this, new UploadRequestListener(), progressDialog).execute(uploadUrl,
                new File(pic.getPath()));
    }

    private Photo getUploadCompletePhoto(JSONObject object) {
        if (object == null) {
            return null;
        }
        Photo photo = new Photo(new JSONObject());
        String domain = JSONUtil.getString(object, "domain");
        String key = JSONUtil.getString(object, "image_path");
        int width = object.optInt("width");
        int height = object.optInt("height");
        String path;
        if (!JSONUtil.isEmpty(domain) && !JSONUtil.isEmpty("key")) {
            path = domain + key;
            photo.setPath(path);
        }
        if (width > 0 && height > 0) {
            photo.setWidth(width);
            photo.setHeight(height);
        }
        return photo;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == Constants.RequestCode.PHOTO_FROM_GALLERY) {
            ArrayList<com.hunliji.hljcommonlibrary.models.Photo> selectedPhotos = data
                    .getParcelableArrayListExtra(
                    "selectedPhotos");
            if (images.contains(addImage)) {
                images.remove(addImage);
            }
            for (com.hunliji.hljcommonlibrary.models.Photo photo : selectedPhotos) {
                JsonPic jsonPic = new JsonPic(new JSONObject());
                jsonPic.setHeight(photo.getHeight());
                jsonPic.setWidth(photo.getWidth());
                jsonPic.setPath(photo.getImagePath());
                jsonPic.setKind(2);
                images.add(jsonPic);
            }
            addLastImage();
            imageAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onRefundMessage() {
        try {
            JSONObject params = new JSONObject();
            params.put("order_sub_id", orderSubId);
            params.put("message",
                    message.getText()
                            .toString());
            JSONArray array = new JSONArray();
            for (Photo photo : upLoadCompletePhoto) {
                if (!JSONUtil.isEmpty(photo.getPath())) {
                    JSONObject picJson = new JSONObject();
                    picJson.put("img", photo.getPath());
                    picJson.put("height", photo.getHeight());
                    picJson.put("width", photo.getWidth());
                    array.put(picJson);
                }
            }
            if (array.length() > 0) {
                params.put("proof_photos", array);
            }
            new StatusHttpPostTask(RefundMessageActivity.this, new StatusRequestListener() {

                @Override
                public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                    if (isFinishing()) {
                        return;
                    }
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    imageIndex = 0;
                    upLoadCompletePhoto.clear();
                    addLastImage();
                    imageAdapter.notifyDataSetChanged();
                    if (returnStatus.getErrorMsg()
                            .equals("success")) {
                        Toast.makeText(RefundMessageActivity.this,
                                getString(R.string.label_refund_message_success),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                    Intent intent = getIntent();
                    intent.putExtra("refresh", true);
                    setResult(RESULT_OK, intent);
                    onBackPressed();
                }

                @Override
                public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                    if (isFinishing()) {
                        return;
                    }
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    imageIndex = 0;
                    upLoadCompletePhoto.clear();
                    addLastImage();
                    imageAdapter.notifyDataSetChanged();
                    Toast.makeText(RefundMessageActivity.this,
                            getString(R.string.label_refund_message_fail),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_REFUND_MESSAGE),
                    params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView delete;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            delete = (ImageView) itemView.findViewById(R.id.delete);
        }
    }

    class ImageAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View holderView = getLayoutInflater().inflate(R.layout.product_comment_image_item,
                    null);
            ViewHolder holder = new ViewHolder(holderView);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            JsonPic pic = images.get(position);
            if (JSONUtil.isEmpty(pic.getPath())) {
                holder.image.setImageResource(R.drawable.icon_cross_add_white_gray);
                holder.delete.setVisibility(View.GONE);
            } else {
                holder.image.setVisibility(View.VISIBLE);
                holder.delete.setVisibility(View.VISIBLE);

                holder.image.setTag(pic.getPath());
                ImageLoadTask task = new ImageLoadTask(holder.image, 0, true);
                task.loadImage(pic.getPath(),
                        imageWidth,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
            }
            holder.image.setOnClickListener(new OnImageClickListener(position));
            holder.delete.setOnClickListener(new OnImageClickListener(position));
        }

        @Override
        public int getItemCount() {
            return images.size();
        }
    }

    class OnImageClickListener implements View.OnClickListener {
        private int position;

        public OnImageClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            JsonPic pic = images.get(position);
            switch (v.getId()) {
                case R.id.image:
                    if (JSONUtil.isEmpty(pic.getPath())) {
                        Intent intent = new Intent(RefundMessageActivity.this,
                                ImageChooserActivity.class);
                        intent.putExtra("limit", Math.min(3 + 1 - images.size(), 3));
                        startActivityForResult(intent, Constants.RequestCode.PHOTO_FROM_GALLERY);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                    break;
                case R.id.delete:
                    deleteImage(position);
                    break;
                default:
                    break;
            }
        }
    }

    class UploadRequestListener implements OnHttpRequestListener {

        @Override
        public void onRequestCompleted(Object obj) {
            if (isFinishing()) {
                return;
            }
            if (obj != null) {
                Photo photo = getUploadCompletePhoto((JSONObject) obj);
                if (!JSONUtil.isEmpty(photo.getPath())) {
                    upLoadCompletePhoto.add(photo);
                }
                imageIndex++;
                if (imageIndex < images.size()) {
                    //图片上传未完成
                    uploadImage();
                } else {
                    //所有图片上传完成
                    onRefundMessage();
                }
            }
        }

        @Override
        public void onRequestFailed(Object obj) {
            if (isFinishing()) {
                return;
            }
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }
}
