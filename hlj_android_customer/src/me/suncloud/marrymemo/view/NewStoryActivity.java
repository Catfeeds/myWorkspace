package me.suncloud.marrymemo.view;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Video;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter.ViewBinder;
import me.suncloud.marrymemo.db.StoryDBAdapter;
import me.suncloud.marrymemo.db.StoryItemDBAdapter;
import me.suncloud.marrymemo.model.Item;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.Story;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.task.QiNiuUploadTask;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.FileUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Size;
import me.suncloud.marrymemo.util.TrackerUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.RoundProgressDialog;
import me.suncloud.marrymemo.widget.RoundProgressDialog.OnUpLoadComplate;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class NewStoryActivity extends HljBaseActivity implements ViewBinder<Item>,
        OnClickListener, OnItemClickListener {
    private int imgWidth;
    private int upLoadIndex;
    private Uri cropUri;
    private Story story;
    private String coverPath;
    private String currentPath;
    private Item editItem;
    private boolean upLoadOk;
    private boolean isChange;
    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            if (from != to) {
                Item item = items.get(from);
                items.remove(item);
                items.add(to, item);
                long position = item.getPosition();
                adapter.notifyDataSetChanged();
                if (from > to) {
                    for (int i = to; i < from; i++) {
                        Item t = items.get(i);
                        t.setPosition(items.get(i + 1)
                                .getPosition());
                    }
                } else {
                    for (int i = to; i > from; i--) {
                        Item t = items.get(i);
                        t.setPosition(items.get(i - 1)
                                .getPosition());
                    }
                }
                item = items.get(from);
                item.setPosition(position);
                isChange = true;
            }
        }
    };
    private boolean isFinish;
    private DragSortListView listView;
    private View heardView;
    private View editLayout;
    private View saveView;
    private View progressBar;
    private ImageView coverView;
    private EditText titleEdit;
    private TextView titleView;
    private CheckBox openBox;
    private ArrayList<Item> items;
    private ArrayList<Item> allItems;
    private ArrayList<Item> filesItems;
    private View menuLayout;
    private StoryDBAdapter storydb;
    private StoryItemDBAdapter storyItemdb;
    private ObjectBindAdapter<Item> adapter;
    private Dialog dialog;
    private boolean isFirst;
    private RoundProgressDialog progressDialog;
    private boolean isCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stroy_new);
        setOkText(R.string.story_upload2);
        SharedPreferences preferences = getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE);
        setSwipeBackEnable(false);
        isCreate = getIntent().getBooleanExtra("isCreate", false);
        isFirst = preferences.getBoolean("is_first_create_story", true);
        if (isFirst) {
            findViewById(R.id.hint_view).setVisibility(View.VISIBLE);
            findViewById(R.id.hint_text).setVisibility(View.VISIBLE);
            findViewById(R.id.hint_view).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    hiddenHint();
                }
            });
            findViewById(R.id.hint_text).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    hiddenHint();
                }
            });
        }
        Point point = JSONUtil.getDeviceSize(this);
        imgWidth = point.x / 4;
        items = new ArrayList<Item>();
        allItems = new ArrayList<Item>();
        listView = (DragSortListView) findViewById(R.id.story_item_list);
        dragSortListViewInt();
        progressBar = findViewById(R.id.progressBar);
        menuLayout = findViewById(R.id.menu_layout);
        coverView = (ImageView) findViewById(R.id.cover);
        titleEdit = (EditText) findViewById(R.id.title);
        titleEdit.addTextChangedListener(mTextWatcher);
        openBox = (CheckBox) findViewById(R.id.private_off);
        editLayout = findViewById(R.id.edit_layout);
        editLayout.setOnClickListener(this);
        findViewById(R.id.preview).setOnClickListener(this);
        findViewById(R.id.photo).setOnClickListener(this);
        findViewById(R.id.edit).setOnClickListener(this);
        findViewById(R.id.video).setOnClickListener(this);
        heardView = getLayoutInflater().inflate(R.layout.story_edit_heard, null);
        View footerView = getLayoutInflater().inflate(R.layout.story_edit_footer2, null);
        saveView = footerView.findViewById(R.id.save_view);
        saveView.setOnClickListener(this);
        titleView = (TextView) heardView.findViewById(R.id.story_title);
        heardView.findViewById(R.id.edit_view)
                .setOnClickListener(this);
        TextView userName = (TextView) heardView.findViewById(R.id.user_name);
        ImageView userIcon = (ImageView) heardView.findViewById(R.id.ower_icon);
        User user = Session.getInstance()
                .getCurrentUser(this);
        if (user != null) {
            userName.setText(user.getNick());
            String url = user.getAvatar();
            if (!JSONUtil.isEmpty(url)) {
                ImageLoadTask iconTask = new ImageLoadTask(userIcon, 0);
                userIcon.setTag(url);
                iconTask.loadImage(url,
                        userIcon.getLayoutParams().width,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(),
                                R.mipmap.icon_avatar_primary,
                                iconTask));
            } else {
                userIcon.setImageResource(R.mipmap.icon_avatar_primary);
            }
        }
        listView.addHeaderView(heardView);
        listView.addFooterView(footerView);
        listView.setOnItemClickListener(this);
        adapter = new ObjectBindAdapter<Item>(this, items, R.layout.story_edit_item);
        adapter.setViewBinder(this);
        listView.setAdapter(adapter);
        listView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        storydb = new StoryDBAdapter(this);
        storyItemdb = new StoryItemDBAdapter(this);
        story = (Story) getIntent().getSerializableExtra("story");
        long storyId = getIntent().getLongExtra("storyId", 0);
        user = Session.getInstance()
                .getCurrentUser(this);
        if (storyId != 0) {
            new GetStoryInfo().execute(Constants.getAbsUrl(String.format(Constants.HttpPath
                            .STORY_INFO_URL,
                    storyId)));
        } else if (story != null) {
            if (story.getExtraId() == 0) {
                new GetStoryInfo().execute(Constants.getAbsUrl(String.format(Constants.HttpPath
                                .STORY_INFO_URL,
                        story.getId())));
            } else {
                showStoryLocality();
            }
        } else {
            progressBar.setVisibility(View.GONE);
            story = new Story(new JSONObject());
            story.setUser(user);
            story.setChange(true);
        }

    }

    public void hiddenHint() {
        if (isFirst) {
            findViewById(R.id.hint_text).setVisibility(View.GONE);
            findViewById(R.id.hint_view).setVisibility(View.GONE);
            SharedPreferences preferences = getSharedPreferences(Constants.PREF_FILE,
                    Context.MODE_PRIVATE);
            isFirst = false;
            preferences.edit()
                    .putBoolean("is_first_create_story", false)
                    .apply();
        }

    }

    public void showStoryLocality() {
        titleView.setText(getString(R.string.label_story_title, story.getTitle()));
        titleEdit.setText(story.getTitle());
        openBox.setChecked(!story.isOpen());
        TextView reviewCount = (TextView) heardView.findViewById(R.id.review_count);
        TextView collectCount = (TextView) heardView.findViewById(R.id.collect_count);
        reviewCount.setText(getString(R.string.label_review_count3, story.getCommentCount()));
        collectCount.setText(getString(R.string.label_like_count, story.getCollectCount()));
        if (!JSONUtil.isEmpty(story.getCover())) {
            String path = story.getCover();
            coverView.setVisibility(View.VISIBLE);
            ImageLoadTask task = new ImageLoadTask(coverView, 0);
            task.loadImage(path,
                    coverView.getMeasuredWidth(),
                    ScaleMode.ALL,
                    new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
        }
        getItemList();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onOkButtonClick() {
        hiddenHint();
        onStroyUpLoad();
        super.onOkButtonClick();
    }

    public void onStroyUpLoad() {
        if (JSONUtil.isEmpty(story.getTitle())) {
            Toast toast = Toast.makeText(NewStoryActivity.this,
                    R.string.hint_story_title_empty,
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        if (JSONUtil.isEmpty(story.getCover())) {
            Toast toast = Toast.makeText(NewStoryActivity.this,
                    R.string.hint_story_cover_empty,
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;

        }
        if (!JSONUtil.isNetworkConnected(NewStoryActivity.this)) {
            Toast toast = Toast.makeText(NewStoryActivity.this,
                    R.string.hint_upload_unconnected,
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        }
        progressDialog = JSONUtil.getRoundProgress(this);
        if (!story.getCover()
                .startsWith("http://")) {
            progressDialog.setMessage(getString(R.string.hint_upload_cover));
            new QiNiuUploadTask(this, new OnHttpRequestListener() {

                @Override
                public void onRequestFailed(Object obj) {

                }

                @Override
                public void onRequestCompleted(Object obj) {

                    if (isFinish) {
                        return;
                    }
                    JSONObject json = (JSONObject) obj;
                    if (json != null) {
                        String key = JSONUtil.getString(json, "image_path");
                        String domain = JSONUtil.getString(json, "domain");
                        if (!JSONUtil.isEmpty(key) && !JSONUtil.isEmpty(domain)) {
                            String cover = domain + key;
                            story.setCover(cover);
                            upLoadItems();
                        } else {
                            progressDialog.dismiss();
                        }
                    } else {
                        progressDialog.dismiss();
                    }
                }
            }, progressDialog).execute(Constants.getAbsUrl(Constants.HttpPath.QINIU_IMAGE_URL),
                    new File(story.getCover()));
        } else {
            upLoadItems();
        }

    }

    public void upLoadItems() {
        if (filesItems == null) {
            filesItems = new ArrayList<Item>();
        }
        filesItems.clear();
        for (Item item : items) {
            if (!JSONUtil.isEmpty(item.getMediaPath()) && !item.getMediaPath()
                    .startsWith("http://")) {
                filesItems.add(item);
            }
        }
        if (!filesItems.isEmpty()) {
            upLoadIndex = 0;
            upLoadFile();
        } else {
            upLoadStoryInfo();
        }
    }

    public void upLoadFile() {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        Item item = filesItems.get(upLoadIndex);
        String uploadUrl;
        if (item.getKind() == 3) {
            uploadUrl = Constants.getAbsUrl(Constants.HttpPath.QINIU_VIDEO_URL) + String.format(
                    Constants.LABEl_FROM,
                    "StoryItem");
        } else {
            uploadUrl = Constants.getAbsUrl(Constants.HttpPath.QINIU_IMAGE_URL);
        }
        progressDialog.setMessage((upLoadIndex + 1) + "/" + filesItems.size());
        new QiNiuUploadTask(this, new OnHttpRequestListener() {

            @Override
            public void onRequestFailed(Object obj) {

            }

            @Override
            public void onRequestCompleted(Object obj) {
                if (isFinish) {
                    return;
                }
                JSONObject json = (JSONObject) obj;
                if (json != null) {
                    Item item = filesItems.get(upLoadIndex);
                    String path = null;
                    if (item.getKind() == 3) {
                        String domain = JSONUtil.getString(json, "domain");
                        String key = JSONUtil.getString(json, "video_path");
                        if (!JSONUtil.isEmpty(key) && !JSONUtil.isEmpty(domain)) {
                            path = domain + key;
                        }
                        String persistentId = JSONUtil.getString(json, "persistent_id");
                        if (!JSONUtil.isEmpty(persistentId)) {
                            item.setPersistentId(persistentId);
                        }
                    } else {
                        String domain = JSONUtil.getString(json, "domain");
                        String key = JSONUtil.getString(json, "image_path");
                        int width = json.optInt("width", 0);
                        int height = json.optInt("height", 0);
                        if (width > 0 && height > 0) {
                            item.setWidth(width);
                            item.setHeight(height);
                        }
                        if (!JSONUtil.isEmpty(key) && !JSONUtil.isEmpty(domain)) {
                            path = domain + key;
                        }
                    }
                    if (!JSONUtil.isEmpty(path)) {
                        item.setMediaPath(path);
                        if (progressDialog != null && progressDialog.isShowing()) {
                            upLoadIndex++;
                            if (upLoadIndex < filesItems.size()) {
                                upLoadFile();
                            } else {
                                upLoadStoryInfo();
                            }
                        }
                    } else {
                        progressDialog.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
                }
            }
        }, progressDialog).execute(uploadUrl, new File(item.getMediaPath()));

    }

    @Override
    protected void onFinish() {
        isFinish = true;
        super.onFinish();
    }

    public void upLoadStoryInfo() {
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
                if (item.hasPlace() && item.getLatitude() != 0 && item.getLongitude() != 0 &&
                        !JSONUtil.isEmpty(
                        item.getPlace())) {
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
                    deleteLocalStory();
                    story.setExtraId(0);
                    upLoadOk = true;
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.setCancelable(false);
                        progressDialog.onComplate();
                        progressDialog.setOnUpLoadComplate(new OnUpLoadComplate() {

                            @Override
                            public void onUpLoadCompleted() {
                                onBackPressed();
                            }
                        });
                    } else {
                        onBackPressed();
                    }
                } else {
                    progressDialog.dismiss();
                    Util.postFailToast(NewStoryActivity.this,
                            returnStatus,
                            R.string.hint_story_modify_error,
                            false);
                }

            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressDialog.dismiss();
                Util.postFailToast(NewStoryActivity.this,
                        returnStatus,
                        R.string.hint_story_modify_error,
                        network);
            }
        }, progressDialog).execute(Constants.getAbsUrl(Constants.HttpPath.NEW_POST_STORY_URL),
                storyObject.toString());
    }

    public void dragSortListViewInt() {
        listView.setDividerHeight(0);
        listView.setDropListener(onDrop);
        DragSortController mController = new DragSortController(listView);
        mController.setDragInitMode(DragSortController.ON_LONG_PRESS);
        mController.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        listView.setFloatViewManager(mController);
        listView.setOnTouchListener(mController);
    }

    public void getItemList() {
        storyItemdb.open();
        Cursor cursor = storyItemdb.getItemList(story.getExtraId());
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
                item.setLatitude(cursor.getDouble(13));
                item.setLongitude(cursor.getDouble(14));
                item.setPlace(cursor.getString(15));
                item.setType(Constants.ItemType.StoryItem);
                if (!JSONUtil.isEmpty(item.getPlace()) && item.getLatitude() != 0 && item
                        .getLongitude() != 0) {
                    item.setHasPlace(true);
                }
                item.setPersistentId(cursor.getString(16));
                allItems.add(item);
                if (!item.isDetele()) {
                    items.add(item);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        storyItemdb.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setViewValue(View view, final Item t, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.item_cover);
            holder.textView = (TextView) view.findViewById(R.id.item_describe);
            holder.location = view.findViewById(R.id.location);
            holder.delete = view.findViewById(R.id.cancel_icon);
            holder.play = view.findViewById(R.id.paly);
            ViewGroup.MarginLayoutParams params;
            params = (MarginLayoutParams) holder.imageView.getLayoutParams();
            params.height = imgWidth;
            params.width = imgWidth;
            params = (MarginLayoutParams) holder.textView.getLayoutParams();
            params.height = imgWidth;
            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.delete.setOnClickListener(new DeleteOnClickListener(t));
        if (JSONUtil.isEmpty(t.getDescription())) {
            holder.textView.setGravity(Gravity.CENTER);
            holder.textView.setText(null);
        } else {
            holder.textView.setGravity(Gravity.LEFT);
            holder.textView.setText(t.getDescription());
        }

        if (t.getKind() == 1) {
            holder.imageView.setVisibility(View.GONE);
            holder.play.setVisibility(View.GONE);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
            if (!t.getMediaPath()
                    .startsWith("http") && t.getKind() == 3) {
                holder.play.setVisibility(View.VISIBLE);
                ImageLoadTask task = new ImageLoadTask(holder.imageView);
                task.loadThumbforPath(t.getMediaPath());
            } else {
                String url = t.getMediaPath();
                if (url.startsWith("http")) {
                    if (t.getKind() == 3) {
                        holder.play.setVisibility(View.VISIBLE);
                        if (t.getPersistent() != null && !JSONUtil.isEmpty(t.getPersistent()
                                .getScreenShot())) {
                            url = JSONUtil.getImagePath(t.getPersistent()
                                    .getScreenShot(), imgWidth);
                        } else {
                            url = t.getMediaPath() + String.format(Constants.VIDEO_URL, imgWidth)
                                    .replace("|", JSONUtil.getURLEncoder());
                        }
                    } else {
                        holder.play.setVisibility(View.GONE);
                        url = t.getMediaPath() + String.format(Constants.PHOTO_URL3,
                                imgWidth,
                                imgWidth);
                    }
                } else {
                    holder.play.setVisibility(View.GONE);
                }
                ImageLoadTask task = new ImageLoadTask(holder.imageView);
                task.loadImage(url,
                        imgWidth,
                        ScaleMode.ALL,
                        new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
            }
        }
        if (t.hasPlace()) {
            holder.location.setVisibility(View.VISIBLE);
        } else {
            holder.location.setVisibility(View.GONE);
        }
    }

    public void onPreview(View view) {
        Intent intent = new Intent(this, StoryPreviewActivity.class);
        intent.putExtra("story", story);
        intent.putExtra("items", items);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    public void onSelectPhoto(View view) {
        selectPhoto(0);
    }

    public void onSelectVideo(View view) {
        selectPhoto(1);
    }

    public void onEditText(View view) {
        Intent intent = new Intent(this, TextEditActivity.class);
        intent.putExtra("title", getString(R.string.title_activity_story_text));
        intent.putExtra("textcount", 500);
        startActivityForResult(intent, Constants.RequestCode.STORY_ITEM_TEXT_EDIT);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    public void onStoryInfoEdit(View view) {
        if (JSONUtil.isEmpty(coverPath)) {
            return;
        }
        if (JSONUtil.isEmpty(titleEdit.getText()
                .toString()
                .trim())) {
            return;
        }
        if (!isChange) {
            if (!titleEdit.getText()
                    .toString()
                    .endsWith(story.getTitle())) {
                isChange = true;
            }
            if (!coverPath.equals(story.getCover())) {
                isChange = true;
            }
            if (story.isOpen() == openBox.isChecked()) {
                isChange = true;
            }
            if (isChange) {
                saveView.setVisibility(View.VISIBLE);
            }
        }
        story.setTitle(titleEdit.getText()
                .toString());
        story.setCover(coverPath);
        story.setOpen(!openBox.isChecked());
        titleView.setText(getString(R.string.label_story_title, titleEdit.getText()));
        editLayout.setVisibility(View.GONE);

    }

    public void onChooseCover(View v) {
        selectPhoto(2);
    }

    public void selectPhoto(int position) {
        if (position == 0) {
            Intent intent = new Intent(NewStoryActivity.this, ImageChooserActivity.class);
            startActivityForResult(intent, Constants.RequestCode.PHOTO_FROM_GALLERY);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else {
            if (dialog != null && dialog.isShowing()) {
                return;
            }
            dialog = new Dialog(this, R.style.BubbleDialogTheme);
            dialog.setContentView(R.layout.dialog_add_menu);
            dialog.findViewById(R.id.action_cancel)
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            if (position == 2) {
                dialog.findViewById(R.id.action_camera_video)
                        .setVisibility(View.GONE);
                dialog.findViewById(R.id.action_gallery)
                        .setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                NewStoryActivityPermissionsDispatcher.onReadPhotosWithCheck(
                                        NewStoryActivity.this);
                            }
                        });
                dialog.findViewById(R.id.action_camera_photo)
                        .setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                NewStoryActivityPermissionsDispatcher.onTakePhotosWithCheck(
                                        NewStoryActivity.this);
                            }
                        });
            } else if (position == 1) {
                dialog.findViewById(R.id.action_camera_photo)
                        .setVisibility(View.GONE);
                dialog.findViewById(R.id.action_gallery)
                        .setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                NewStoryActivityPermissionsDispatcher.onReadVideosWithCheck(
                                        NewStoryActivity.this);
                            }
                        });
                dialog.findViewById(R.id.action_camera_video)
                        .setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                NewStoryActivityPermissionsDispatcher
                                        .goToTakeVideoActivityWithCheck(
                                        NewStoryActivity.this);
                            }
                        });

            }
            dialog.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    menuLayout.setVisibility(View.VISIBLE);
                }
            });
            Window win = dialog.getWindow();
            LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = point.x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
            menuLayout.setVisibility(View.GONE);
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri;
            switch (requestCode) {
                case Constants.RequestCode.PHOTO_FROM_GALLERY:
                    if (data != null) {
                        ArrayList<Photo> selectedPhotos = data.getParcelableArrayListExtra(
                                "selectedPhotos");
                        for (Photo photo : selectedPhotos) {
                            Item item = new Item(new JSONObject());
                            item.setKind(2);
                            item.setType(Constants.ItemType.StoryItem);
                            if (items.isEmpty()) {
                                item.setPosition(1);
                            } else {
                                item.setPosition(items.get(items.size() - 1)
                                        .getPosition() + 1);
                            }
                            item.setWidth(photo.getWidth());
                            item.setHeight(photo.getHeight());
                            item.setMediaPath(photo.getImagePath());
                            items.add(item);
                            allItems.add(item);
                        }
                        adapter.notifyDataSetChanged();
                        isChange = true;
                    }
                    break;
                case Constants.RequestCode.PHOTO_FROM_CAMERA:
                    addItem(2, currentPath, null);
                    break;
                case Constants.RequestCode.VIDEO_FROM_CAMERA:
                    if (data != null) {
                        String path = data.getStringExtra("path");
                        addItem(3, path, null);
                    }
                    break;
                case Constants.RequestCode.VIDEO_FROM_GALLERY:
                    if (data != null) {
                        String[] filePathColumn = {MediaStore.Video.Media.DATA};
                        uri = data.getData();
                        Cursor cursor = getContentResolver().query(uri,
                                filePathColumn,
                                null,
                                null,
                                null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            String thumbPath = cursor.getString(cursor.getColumnIndex
                                    (filePathColumn[0]));
                            cursor.close();
                            addItem(3, thumbPath, null);
                        } else {
                            String thumbPath = uri.getPath();
                            addItem(3, thumbPath, null);
                        }
                    }
                    break;
                case Constants.RequestCode.STORY_ITEM_TEXT_EDIT:
                    if (data != null) {
                        String text = data.getStringExtra("text");
                        if (!JSONUtil.isEmpty(text)) {
                            addItem(1, null, text);
                        }
                    }
                    break;
                case Constants.RequestCode.STORY_ITEM_DESCRIBE_EDIT:
                    if (data != null) {
                        String text = data.getStringExtra("text");
                        double lat = data.getDoubleExtra("lat", 0);
                        double lon = data.getDoubleExtra("lon", 0);
                        String place = data.getStringExtra("address");
                        if (lat != 0 && lon != 0 && !JSONUtil.isEmpty(place)) {
                            editItem.setLatitude(lat);
                            editItem.setLongitude(lon);
                            editItem.setPlace(place);
                            editItem.setHasPlace(true);
                        } else {
                            editItem.setLatitude(0);
                            editItem.setLongitude(0);
                            editItem.setPlace(null);
                            editItem.setHasPlace(false);
                        }
                        if (!JSONUtil.isEmpty(text)) {
                            editItem.setDescription(text);
                        }
                        adapter.notifyDataSetChanged();
                        isChange = true;
                    }
                    break;
                case Constants.RequestCode.PHOTO_CROP:
                    if (cropUri != null) {
                        setCover(cropUri);
                    }
                    break;
                case Constants.RequestCode.COVER_FROM_GALLERY:
                    if (data == null) {
                        return;
                    }
                    uri = Uri.fromFile(new File(JSONUtil.getImagePathForUri(data.getData(), this)));
                    if (uri != null) {
                        setCover(uri);
                        showPhotoCrop(new File(JSONUtil.getImagePathForUri(uri,
                                NewStoryActivity.this)));
                    }
                    break;
                case Constants.RequestCode.COVER_FROM_CAMERA:
                    uri = Uri.fromFile(new File(currentPath));
                    if (uri != null) {
                        setCover(uri);
                        showPhotoCrop(new File(currentPath));
                    }
                default:
                    break;
            }
            if (isChange) {
                saveView.setVisibility(View.VISIBLE);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addItem(int type, String path, String text) {
        Item item = new Item(new JSONObject());
        item.setType(Constants.ItemType.StoryItem);
        item.setKind(type);
        if (type == 1) {
            item.setDescription(text);
        } else {
            if (type == 2) {
                Size size = JSONUtil.getImageSizeFromPath(path);
                item.setWidth(size.getWidth());
                item.setHeight(size.getHeight());
            } else {
                Bitmap image = ThumbnailUtils.createVideoThumbnail(path,
                        Video.Thumbnails.MINI_KIND);
                if (image != null) {
                    item.setWidth(image.getWidth());
                    item.setHeight(image.getHeight());
                    image.recycle();
                }
            }
            item.setMediaPath(path);
        }
        if (items.isEmpty()) {
            item.setPosition(1);
        } else {
            item.setPosition(items.get(items.size() - 1)
                    .getPosition() + 1);
        }
        items.add(item);
        allItems.add(item);
        adapter.notifyDataSetChanged();
        isChange = true;
    }

    private void setCover(Uri uri) {
        String path = JSONUtil.getImagePathForUri(uri, this);
        if (!JSONUtil.isEmpty(path)) {
            coverPath = path;
            ImageLoadTask task = new ImageLoadTask(coverView, new OnHttpRequestListener() {

                @Override
                public void onRequestFailed(Object obj) {

                }

                @Override
                public void onRequestCompleted(Object obj) {
                    coverView.setScaleType(ScaleType.CENTER_CROP);

                }
            });
            coverView.setTag(path);
            task.loadImage(coverPath,
                    coverView.getWidth(),
                    ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(), R.drawable.icon_pic_white, task));
        }

    }

    public void showPhotoCrop(File file) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, getPackageName(), file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 16);
        intent.putExtra("aspectY", 9);
        intent.putExtra("scale", true);
        File f = FileUtil.createCropImageFile();
        cropUri = Uri.fromFile(f);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, Constants.RequestCode.PHOTO_CROP);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_view:
                editLayout.setVisibility(View.VISIBLE);
                String path = story.getCover();
                coverPath = path;
                openBox.setChecked(!story.isOpen());
                titleEdit.setText(story.getTitle());
                if (!JSONUtil.isEmpty(path)) {
                    if (!path.equals(coverView.getTag())) {
                        ImageLoadTask task = new ImageLoadTask(coverView,
                                new OnHttpRequestListener() {

                                    @Override
                                    public void onRequestFailed(Object obj) {

                                    }

                                    @Override
                                    public void onRequestCompleted(Object obj) {
                                        coverView.setScaleType(ScaleType.CENTER_CROP);

                                    }
                                });
                        coverView.setTag(path);
                        task.loadImage(coverPath,
                                coverView.getWidth(),
                                ScaleMode.WIDTH,
                                new AsyncBitmapDrawable(getResources(),
                                        R.drawable.icon_pic_white,
                                        task));
                    }
                } else {
                    coverView.setImageResource(R.drawable.icon_pic_white);
                    coverView.setScaleType(ScaleType.CENTER_INSIDE);
                }
                break;
            case R.id.edit_layout:
                hideKeyboard(v);
                editLayout.setVisibility(View.GONE);
                break;
            case R.id.preview:
                hiddenHint();
                onPreview(v);
                break;
            case R.id.photo:
                hiddenHint();
                onSelectPhoto(v);
                break;
            case R.id.edit:
                hiddenHint();
                onEditText(v);
                break;
            case R.id.video:
                hiddenHint();
                onSelectVideo(v);
                break;
            case R.id.save_view:
                saveStory();
                break;
        }
    }

    public void saveStory() {
        if (story.getExtraId() == 0) {
            long extraId = System.currentTimeMillis();
            story.setExtraId(extraId);
            storydb.open();
            storydb.insertTitle(story, extraId);
            storydb.close();
            if (story.getId() != 0) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", story.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new StatusHttpPostTask(this, null).executeOnExecutor(Constants.INFOTHEADPOOL,
                        Constants.getAbsUrl(Constants.HttpPath.LOCK_STORY_URL),
                        jsonObject.toString());
            }
        } else {
            storydb.open();
            storydb.updateTitle(story, story.getExtraId());
            storydb.close();
            storyItemdb.open();
            storyItemdb.deleteALLTitle(story.getExtraId());
            storyItemdb.close();
        }
        storyItemdb.open();
        for (Item item : allItems) {
            storyItemdb.insertTitle(item, System.currentTimeMillis(), story.getExtraId());
        }
        storyItemdb.close();
        isChange = false;
        saveView.setVisibility(View.GONE);
        Intent intent = getIntent();
        intent.putExtra("story", story);
        setResult(RESULT_OK, intent);
    }

    public void deleteLocalStory() {
        if (story.getExtraId() != 0) {
            storydb.open();
            storydb.deleteLocalTitle(story.getExtraId());
            storydb.close();
            storyItemdb.open();
            storyItemdb.deleteALLTitle(story.getExtraId());
            storyItemdb.close();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = (Item) parent.getAdapter()
                .getItem(position);
        if (item != null) {
            Intent intent = new Intent(NewStoryActivity.this, TextEditActivity.class);
            editItem = item;
            if (!JSONUtil.isEmpty(editItem.getDescription())) {
                intent.putExtra("text", editItem.getDescription());
            }
            if (editItem.getKind() == 1) {
                intent.putExtra("title", getString(R.string.title_activity_story_text));
            } else {
                intent.putExtra("showLocation", true);
                if (item.hasPlace()) {
                    intent.putExtra("address", item.getPlace());
                    intent.putExtra("lat", item.getLatitude());
                    intent.putExtra("lon", item.getLongitude());
                }
                if (editItem.getKind() == 2) {
                    intent.putExtra("title", getString(R.string.title_activity_story_photo));
                } else {
                    intent.putExtra("title", getString(R.string.title_activity_story_video));
                }
            }
            intent.putExtra("textcount", 500);
            startActivityForResult(intent, Constants.RequestCode.STORY_ITEM_DESCRIBE_EDIT);
        }
    }

    @Override
    public void onBackPressed() {
        hiddenHint();
        if (editLayout.getVisibility() == View.VISIBLE) {
            editLayout.setVisibility(View.GONE);
            return;
        }
        if (!upLoadOk) {
            if (isChange) {
                if (dialog != null && dialog.isShowing()) {
                    return;
                }
                dialog = new Dialog(this, R.style.BubbleDialogTheme);
                View view = getLayoutInflater().inflate(R.layout.dialog_msg_notice, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.img_notice);
                Button tvConfirm = (Button) view.findViewById(R.id.btn_notice_confirm);
                Button tvCancel = (Button) view.findViewById(R.id.btn_notice_cancel);
                tvCancel.setVisibility(View.VISIBLE);
                TextView tvMsg = (TextView) view.findViewById(R.id.tv_notice_msg);
                tvMsg.setText(R.string.hint_story_back2);
                tvConfirm.setText(R.string.action_save_exit);
                tvCancel.setText(R.string.action_exit);
                imageView.setImageResource(R.drawable.icon_notice_bell_primary);

                tvCancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();
                    }
                });
                tvConfirm.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        saveStory();
                        isChange = false;
                        onBackPressed();
                    }
                });

                dialog.setContentView(view);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                Point point = JSONUtil.getDeviceSize(this);
                params.width = Math.round(point.x * 5 / 7);
                window.setAttributes(params);
                dialog.show();
            } else {
                super.onBackPressed();
            }
        } else {
            Intent intent = getIntent();
            intent.putExtra("story", story);
            setResult(RESULT_OK, intent);
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCreate) {
            TrackerUtil.onTCAgentPageStart(this, "创建故事");
        } else {
            TrackerUtil.onTCAgentPageStart(this, "编辑故事");
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        private int editStart;
        private int editEnd;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = titleEdit.getSelectionStart();
            editEnd = titleEdit.getSelectionEnd();
            titleEdit.removeTextChangedListener(mTextWatcher);
            if (editStart == 0) {
                editStart = s.length();
                editEnd = s.length();
                if (editStart > 40) {
                    editStart = 40;
                }
            }
            while (Util.getTextLength(s) > 20) {
                s.delete(editStart - 1, editEnd);
                editStart--;
                editEnd--;
                if (editEnd > s.length()) {
                    editEnd = s.length();
                }
            }
            titleEdit.setSelection(editStart);
            titleEdit.addTextChangedListener(mTextWatcher);

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (isCreate) {
            TrackerUtil.onTCAgentPageEnd(this, "创建故事");
        } else {
            TrackerUtil.onTCAgentPageEnd(this, "编辑故事");
        }
    }

    public class GetStoryInfo extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String json = JSONUtil.getStringFromUrl(params[0]);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json).optJSONObject("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            progressBar.setVisibility(View.GONE);
            if (result != null) {
                if (!result.isNull("story")) {
                    story = new Story(result.optJSONObject("story"));
                    titleView.setText(getString(R.string.label_story_title, story.getTitle()));
                    titleEdit.setText(story.getTitle());
                    openBox.setChecked(!story.isOpen());
                    TextView reviewCount = (TextView) heardView.findViewById(R.id.review_count);
                    TextView collectCount = (TextView) heardView.findViewById(R.id.collect_count);
                    reviewCount.setText(getString(R.string.label_review_count3,
                            story.getCommentCount()));
                    collectCount.setText(getString(R.string.label_like_count,
                            story.getCollectCount()));
                    if (!JSONUtil.isEmpty(story.getCover())) {
                        String path = JSONUtil.getImagePath(story.getCover(),
                                coverView.getMeasuredWidth());
                        coverView.setVisibility(View.VISIBLE);
                        Bitmap placeholder = BitmapFactory.decodeResource(getResources(),
                                R.mipmap.icon_empty_image);
                        ImageLoadTask task = new ImageLoadTask(coverView, 0);
                        task.loadImage(path,
                                coverView.getMeasuredWidth(),
                                ScaleMode.ALL,
                                new AsyncBitmapDrawable(getResources(), placeholder, task));
                    }
                }
                if (!result.isNull("items")) {
                    items.clear();
                    JSONArray array = result.optJSONArray("items");
                    int size = array.length();
                    if (size > 0) {
                        for (int i = 0; i < size; i++) {
                            Item item = new Item(array.optJSONObject(i));
                            item.setType(Constants.ItemType.StoryItem);
                            items.add(item);
                            allItems.add(item);
                        }
                        if (!items.isEmpty()) {
                            if (items.size() > 1) {
                                Collections.sort(items, new Comparator<Item>() {
                                    @Override
                                    public int compare(Item item1, Item item2) {
                                        return item1.getPosition()
                                                .compareTo(item2.getPosition());
                                    }
                                });
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            super.onPostExecute(result);
        }
    }

    private class ViewHolder {
        ImageView imageView;
        TextView textView;
        View location;
        View delete;
        View play;
    }

    private class DeleteOnClickListener implements View.OnClickListener {
        private Item item;

        public DeleteOnClickListener(Item item) {
            super();
            this.item = item;

        }

        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(NewStoryActivity.this, R.style.BubbleDialogTheme);
            View view = getLayoutInflater().inflate(R.layout.dialog_msg_notice, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.img_notice);
            Button tvConfirm = (Button) view.findViewById(R.id.btn_notice_confirm);
            Button tvCancel = (Button) view.findViewById(R.id.btn_notice_cancel);
            tvCancel.setVisibility(View.VISIBLE);
            TextView tvMsg = (TextView) view.findViewById(R.id.tv_notice_msg);
            tvMsg.setText(R.string.hint_detele_item);
            imageView.setImageResource(R.drawable.icon_delete_primary_158_154);

            tvConfirm.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    items.remove(item);
                    adapter.notifyDataSetChanged();
                    if (item.getId() != 0) {
                        item.detele(true);
                    } else {
                        allItems.remove(item);
                    }
                    isChange = true;
                    saveView.setVisibility(View.VISIBLE);
                    dialog.cancel();
                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            dialog.setContentView(view);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(NewStoryActivity.this);
            params.width = Math.round(point.x * 5 / 7);
            window.setAttributes(params);
            dialog.show();

        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onReadPhotos() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.RequestCode.COVER_FROM_GALLERY);
        dialog.dismiss();
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onReadVideos() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, Constants.RequestCode.VIDEO_FROM_GALLERY);
        dialog.dismiss();
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onTakePhotos() {
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
        startActivityForResult(intent, Constants.RequestCode.COVER_FROM_CAMERA);
        dialog.dismiss();
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest
            .permission.WRITE_EXTERNAL_STORAGE})
    void goToTakeVideoActivity() {
        Intent intent = new Intent(NewStoryActivity.this, TakeVideoActivity.class);
        startActivityForResult(intent, Constants.RequestCode.VIDEO_FROM_CAMERA);
        dialog.dismiss();
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest
            .permission.WRITE_EXTERNAL_STORAGE})
    void onRationaleTakeVideo(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.permission_r_for_take_video));
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onRationaleCamera(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.permission_r_for_camera));
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onRationaleReadExternal(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.permission_r_for_read_external_storage));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        NewStoryActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}