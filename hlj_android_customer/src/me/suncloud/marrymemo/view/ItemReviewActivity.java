package me.suncloud.marrymemo.view;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Item;
import me.suncloud.marrymemo.model.Reply;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;

public class ItemReviewActivity extends BaseReviewListActivity {

    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Item item = (Item) getIntent().getSerializableExtra("item");
        if (item != null) {
            id = item.getId();
            count = item.getCommentCount();
            if (item.getKind() == 1) {
                titleStr = getString(R.string.title_activity_review_text);
            } else if (item.getKind() == 2) {
                titleStr = getString(R.string.title_activity_review_photo);
            } else {
                titleStr = getString(R.string.title_activity_review_video);
            }
            headerView = View.inflate(this, R.layout.item_review_heard, null);
            listHeardInit(item);
        } else {
            id = getIntent().getLongExtra("id", 0);
            titleStr = getString(R.string.title_activity_review);
        }
        super.onCreate(savedInstanceState);

    }

    @Override
    public String getUrl(int currentPage) {
        return Constants.getAbsUrl(Constants.HttpPath.GET_STORY_ITEM_REPLY_URL, id,
                currentPage);
    }

    @Override
    public void deleteReply(Reply reply, StatusRequestListener listener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", reply.getId());
            new StatusHttpPostTask(this, listener).execute(Constants.getAbsUrl(Constants
                    .HttpPath
                    .STORY_ITEM_REPLY_DELETE_URL), jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void postReply(String content, Reply reply, StatusRequestListener listener) {
        hideKeyboard(null);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("story_item_id", id);
            JSONObject commentJson = new JSONObject();
            commentJson.put("content", content);
            commentJson.put("user_id", userId);
            if (reply != null) {
                commentJson.put("quote", reply.getUser().getId());
            }
            jsonObject.put("story_item_comment", commentJson);
            if (progressDialog == null) {
                progressDialog = JSONUtil.getRoundProgress(this);
            } else if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
            progressDialog.onLoadComplate();
            progressDialog.setCancelable(false);
            new StatusHttpPostTask(this, listener, progressDialog).execute(Constants.getAbsUrl
                    (Constants.HttpPath.REPLY_STORY_ITEM_URL), jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void listHeardInit(final Item t) {
        Point point = JSONUtil.getDeviceSize(this);
        DisplayMetrics dm = new DisplayMetrics();
        final int width = (int) (point.x - 16 * dm.density);
        int imgWidth;
        if (width <= 805) {
            imgWidth = width;
        } else {
            imgWidth = width * 3 / 4;
        }
        if (t.getKind() == 1) {
            headerView.findViewById(R.id.image_item).setVisibility(View.GONE);
            ((TextView) headerView.findViewById(R.id.text_item_describe)).setText(t
                    .getDescription());
        } else {
            headerView.findViewById(R.id.text_item_describe).setVisibility(View.GONE);
            TextView description = (TextView) headerView.findViewById(R.id.item_describe);
            final ImageView imageView = (ImageView) headerView.findViewById(R.id.story_image);
            String url;
            if (t.getKind() == 2) {
                url = JSONUtil.getImagePath(t.getMediaPath(), imgWidth);
            } else {
                headerView.findViewById(R.id.play).setVisibility(View.VISIBLE);
                if (t.getPersistent() != null
                        && !JSONUtil.isEmpty(t.getPersistent().getScreenShot())) {
                    url = JSONUtil.getImagePath(t.getPersistent()
                            .getScreenShot(), width);
                } else {
                    url = t.getMediaPath() + String.format(Constants.VIDEO_URL, imgWidth)
                            .replace("|", JSONUtil.getURLEncoder());
                }
            }
            if (!JSONUtil.isEmpty(t.getDescription())) {
                description.setVisibility(View.VISIBLE);
                description.setText(t.getDescription());

            } else {
                description.setVisibility(View.GONE);
            }
            if (t.getWidth() != 0 && t.getHight() != 0) {
                ViewGroup.MarginLayoutParams params;
                params = (MarginLayoutParams) imageView.getLayoutParams();
                params.height = Math.round(point.x * t.getHight() / t.getWidth());
            }
            ImageLoadTask task = new ImageLoadTask(imageView,
                    new OnHttpRequestListener() {

                        @Override
                        public void onRequestCompleted(Object obj) {
                            if (t.getWidth() == 0 || t.getHight() == 0) {
                                Bitmap bitmap = (Bitmap) obj;
                                imageView.getLayoutParams().height = Math.round(width * bitmap
                                        .getHeight() / bitmap.getWidth());
                            }
                        }

                        @Override
                        public void onRequestFailed(Object obj) {

                        }
                    });
            imageView.setTag(url);
            AsyncBitmapDrawable asyncDrawable = new AsyncBitmapDrawable(
                    getResources(), R.mipmap.icon_empty_image, task);
            task.loadImage(url, width, ScaleMode.WIDTH, asyncDrawable);
        }

    }

}
