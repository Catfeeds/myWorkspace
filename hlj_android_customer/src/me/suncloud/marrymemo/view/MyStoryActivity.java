package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.db.StoryDBAdapter;
import me.suncloud.marrymemo.db.StoryItemDBAdapter;
import me.suncloud.marrymemo.model.Item;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.Story;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.task.QiNiuUploadTask;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.RoundProgressDialog;

public class MyStoryActivity extends BaseStoryActivity {

    private StoryItemDBAdapter itemdb;
    private Dialog deleteDialog;
    private StoryDBAdapter storydb;
    private StoryItemDBAdapter itemsdb;
    private View progressBar;
    private RoundProgressDialog progressDialog;
    private ArrayList<Item> filesItems;
    private ArrayList<Item> allItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allItems=new ArrayList<>();
        storydb = new StoryDBAdapter(this);
        itemsdb = new StoryItemDBAdapter(this);
        setContentView(R.layout.activity_story);
        progressBar = findViewById(R.id.progressBar);
        story = (Story) getIntent().getSerializableExtra("story");
        itemdb = new StoryItemDBAdapter(this);
        findViewById(R.id.more).setVisibility(View.VISIBLE);
        getStoryInfo(story);
    }

    public void getStoryInfo(Story story) {
        if (story.getExtraId() == 0) {
            new GetStoryInfo().execute(Constants.getAbsUrl(String.format(Constants.HttpPath
                    .STORY_INFO_URL,
                    story.getId())));
        } else {
            itemdb.open();
            items.clear();
            allItems.clear();
            Cursor cursor = itemdb.getItemList(story.getExtraId());
            if (cursor.moveToFirst()) {
                do {
                    Item item = new Item(new JSONObject());
                    item.setId(cursor.getLong(0));
                    item.setDescription(cursor.getString(1));
                    item.setExtraId(cursor.getLong(2));
                    item.setHeight(cursor.getInt(3));
                    item.setKind(cursor.getInt(4));
                    item.setMediaPath(cursor.getString(5));
                    item.setPosition(cursor.getLong(7));
                    item.setWidth(cursor.getInt(10));
                    item.detele(cursor.getInt(6) != 0);
                    item.setCommentCount(cursor.getInt(11));
                    item.setCollectCount(cursor.getInt(12));
                    item.setLatitude(cursor.getDouble(13));
                    item.setLongitude(cursor.getDouble(14));
                    item.setPlace(cursor.getString(15));
                    item.setType(Constants.ItemType.StoryItem);
                    if (!JSONUtil.isEmpty(item.getPlace())
                            && item.getLatitude() != 0 && item.getLongitude() != 0) {
                        item.setHasPlace(true);
                    }
                    allItems.add(item);
                    if(!item.isDetele()) {
                        items.add(item);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            itemdb.close();
            showStory(story);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.STORY_EDIT:
                    story = (Story) data.getSerializableExtra("story");
                    getStoryInfo(story);
                    Intent intent = getIntent();
                    intent.putExtra("story", story);
                    setResult(RESULT_OK, intent);
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_edit:
                if (story.getId().intValue() == 0
                        || JSONUtil.isNetworkConnected(this)) {
                    Intent intent = new Intent(this, NewStoryActivity.class);
                    intent.putExtra("story", story);
                    startActivityForResult(intent, Constants.RequestCode.STORY_EDIT);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                } else {
                    Util.showToast(this, null, R.string.hint_edit_unconnected);
                }
                break;
            case R.id.action_sync:
                onStoryUpLoad();
                break;
            case R.id.action_delete:
                if (deleteDialog != null && deleteDialog.isShowing()) {
                    return;
                }
                if (deleteDialog == null) {
                    deleteDialog = new Dialog(this, R.style.BubbleDialogTheme);
                    deleteDialog.setContentView(R.layout.dialog_confirm);
                    deleteDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View
                            .OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteDialog.cancel();
                        }
                    });
                    deleteDialog.findViewById(R.id.btn_confirm).setOnClickListener(this);
                    TextView tvMsg = (TextView) deleteDialog.findViewById(R.id.tv_alert_msg);
                    tvMsg.setText(R.string.msg_story_delete_confirm);
                    Window window = deleteDialog.getWindow();
                    WindowManager.LayoutParams params = window.getAttributes();
                    Point point = JSONUtil.getDeviceSize(this);
                    params.width = Math.round(point.x * 27 / 32);
                    window.setAttributes(params);
                }
                deleteDialog.show();
                break;
            case R.id.btn_confirm:
                deleteDialog.cancel();
                if (story.getId() == 0) {
                    deleteLocalStory(story);
                    Intent intent = getIntent();
                    intent.putExtra("isDeleted", true);
                    setResult(RESULT_OK, intent);
                    onBackPressed();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("id", story.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new StatusHttpPostTask(this, new StatusRequestListener() {
                        @Override
                        public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                            progressBar.setVisibility(View.GONE);
                            if (story.getExtraId() > 0) {
                                deleteLocalStory(story);
                            }
                            Intent intent = getIntent();
                            intent.putExtra("isDeleted", true);
                            setResult(RESULT_OK, intent);
                            onBackPressed();
                        }

                        @Override
                        public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                            progressBar.setVisibility(View.GONE);
                            Util.postFailToast(MyStoryActivity.this, returnStatus, R.string
                                    .msg_channel_delete_error, network);

                        }
                    }).execute(Constants.getAbsUrl(Constants.HttpPath.DELETE_STORY_URL),
                            jsonObject.toString());
                }
                break;
        }
        super.onClick(view);
    }

    public void deleteLocalStory(Story story) {
        storydb.open();
        storydb.deleteLocalTitle(story.getExtraId());
        storydb.close();
        itemsdb.open();
        itemsdb.deleteALLTitle(story.getExtraId());
        itemsdb.close();
    }

    public void onStoryUpLoad() {
        if (JSONUtil.isEmpty(story.getTitle())) {
            Util.showToast(this, null, R.string.hint_story_title_empty);
            return;
        }
        if (JSONUtil.isEmpty(story.getCover())) {
            Util.showToast(this, null, R.string.hint_story_cover_empty);
            return;

        }
        if (!JSONUtil.isNetworkConnected(this)) {
            Util.showToast(this, null, R.string.hint_upload_unconnected);

        }
        if (progressDialog == null) {
            progressDialog = JSONUtil.getRoundProgress(this);
        } else {
            progressDialog.reset();
            progressDialog.show();
        }
        progressDialog.setCancelable(false);
        if (!story.getCover().startsWith("http://")) {
            progressDialog.setMessage(getString(R.string.hint_upload_cover));
            progressDialog.setProgress(0);
            new QiNiuUploadTask(this, new OnHttpRequestListener() {

                @Override
                public void onRequestFailed(Object obj) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onRequestCompleted(Object obj) {
                    if (isFinishing()) {
                        return;
                    }
                    JSONObject json = (JSONObject) obj;
                    if (json != null) {
                        String key = JSONUtil.getString(json, "image_path");
                        String domain = JSONUtil.getString(json, "domain");
                        if (!JSONUtil.isEmpty(key) && !JSONUtil.isEmpty(domain)) {
                            String cover = domain + key;
                            story.setCover(cover);
                            storydb.open();
                            storydb.updateTitle(story, story.getExtraId());
                            storydb.close();
                            if (progressDialog != null
                                    && progressDialog.isShowing()) {
                                upLoadItems();
                                return;
                            }
                        }
                    }
                    progressDialog.dismiss();
                }
            }, progressDialog).execute(Constants.getAbsUrl(Constants.HttpPath.QINIU_IMAGE_URL),
                    new File(story.getCover()));
        } else {
            upLoadItems();
        }
    }

    public void upLoadItems() {
        if (filesItems == null) {
            filesItems = new ArrayList<>();
        }
        filesItems.clear();
        for (Item item : items) {
            if (!JSONUtil.isEmpty(item.getMediaPath())
                    && !item.getMediaPath().startsWith("http://")) {
                filesItems.add(item);
            }
        }
        if (!filesItems.isEmpty()) {
            upLoadFile(0);
        } else {
            synchronizeStoryInfo();
        }
    }


    public void upLoadFile(int index) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        Item item = filesItems.get(index);
        String uploadUrl;
        if (item.getKind() == 3) {
            uploadUrl = Constants.getAbsUrl(Constants.HttpPath.QINIU_VIDEO_URL) + String.format
                    (Constants.LABEl_FROM, "StoryItem");
        } else {
            uploadUrl = Constants.getAbsUrl(Constants.HttpPath.QINIU_IMAGE_URL);
        }
        progressDialog.setMessage((index + 1) + "/" + filesItems.size());
        new QiNiuUploadTask(this, new ItemUploadRequestListener(item), progressDialog).execute
                (uploadUrl,
                new File(item.getMediaPath()));

    }


    private class ItemUploadRequestListener implements OnHttpRequestListener {

        private Item item;

        private ItemUploadRequestListener(Item item) {
            this.item = item;
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
            String persistent_id;
            if (json != null) {
                width = json.optInt("width", 0);
                height = json.optInt("height", 0);
                if (item.getKind() == 3) {
                    String key = JSONUtil.getString(json, "video_path");
                    if (!JSONUtil.isEmpty(key)) {
                        path = Constants.QINIU_HOST + key;
                    }
                    persistent_id = JSONUtil.getString(json,
                            "persistent_id");
                    if (!JSONUtil.isEmpty(persistent_id)) {
                        item.setPersistentId(persistent_id);
                    }
                } else {
                    String domain = JSONUtil.getString(json, "domain");
                    String key = JSONUtil.getString(json, "image_path");
                    if (!JSONUtil.isEmpty(key) && !JSONUtil.isEmpty(domain)) {
                        path = domain + key;
                    }
                }
            }
            if (!JSONUtil.isEmpty(path)) {
                item.setMediaPath(path);
                if (width > 0 && height > 0) {
                    item.setWidth(width);
                    item.setHeight(height);
                }
                itemsdb.open();
                itemsdb.updateTitle(item, item.getExtraId());
                itemsdb.close();
                int index = filesItems.indexOf(item);
                if (index >= 0) {
                    adapter.notifyDataSetChanged();
                    index++;
                    if (index < filesItems.size()) {
                        upLoadFile(index);
                    } else {
                        synchronizeStoryInfo();
                    }
                    return;
                }
            }
            progressDialog.dismiss();

        }

        @Override
        public void onRequestFailed(Object obj) {
            progressDialog.dismiss();
        }
    }


    private void synchronizeStoryInfo() {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        progressDialog.onLoadComplate();
        JSONObject storyObject = new JSONObject();
        try {
            storyObject.put("title", story.getTitle());
            storyObject.put("cover_path", story.getCover());
            storyObject.put("state", 1);
            storyObject.put("opened", story.isOpen() ? 1 : 0);
            if (story.getId() > 0) {
                storyObject.put("story_id", story.getId());
            }
            JSONArray array = new JSONArray();
            for (Item item : allItems) {
                JSONObject itemObject = new JSONObject();
                itemObject.put("position", item.getPosition());
                itemObject.put("kind", item.getKind());
                if (item.getKind() != 1) {
                    itemObject.put("media_path", item.getMediaPath());
                    itemObject.put("height", item.getHight());
                    itemObject.put("width", item.getWidth());
                    if (!JSONUtil.isEmpty(item.getPersistentId())) {
                        itemObject.put("persistent_id", item.getPersistentId());
                    }
                }
                if (item.getId() > 0) {
                    itemObject.put("id", item.getId());
                    if (item.isDetele()) {
                        itemObject.put("_destroy", 1);
                    }
                }
                itemObject.put("description", item.getDescription());
                if (story.getId() > 0) {
                    itemObject.put("story_id", story.getId());
                }
                if (item.hasPlace() && item.getLatitude() != 0
                        && item.getLongitude() != 0
                        && !JSONUtil.isEmpty(item.getPlace())) {
                    itemObject.put("has_place", 1);
                    itemObject.put("latitude", item.getLatitude());
                    itemObject.put("longitude", item.getLongitude());
                    itemObject.put("place", item.getPlace());
                } else {
                    itemObject.put("has_place", "0");
                }
                array.put(itemObject);
            }
            if (array.length() > 0) {
                storyObject.put("story_items_attributes", array);
            }
        } catch (JSONException ignored) {
        }
        new StatusHttpPostTask(this, new StatusRequestListener() {

            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                JSONObject json = (JSONObject) object;
                if (json != null) {
                    long storyId = json.optLong("story_id");
                    story.setId(storyId);
                    deleteLocalStory(story);
                    Intent intent = getIntent();
                    intent.putExtra("isRefresh", true);
                    setResult(RESULT_OK, intent);
                    new GetStoryInfo().execute(Constants.getAbsUrl(String.
                            format(Constants.HttpPath.STORY_INFO_URL, story.getId())));
                    if (progressDialog != null
                            && progressDialog.isShowing()) {
                        progressDialog.setCancelable(false);
                        progressDialog.onComplate();
                    }
                } else {
                    progressDialog.dismiss();
                    Util.postFailToast(MyStoryActivity.this, returnStatus, R.string
                            .hint_story_modify_error, false);
                }

            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressDialog.dismiss();
                Util.postFailToast(MyStoryActivity.this, returnStatus, R.string
                        .hint_story_modify_error, network);
            }
        }, progressDialog).execute(Constants.getAbsUrl(Constants.HttpPath.NEW_POST_STORY_URL),
                storyObject.toString());
    }

}