package me.suncloud.marrymemo.view;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljtrackerlibrary.HljTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.db.StoryDBAdapter;
import me.suncloud.marrymemo.db.StoryItemDBAdapter;
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
import me.suncloud.marrymemo.util.ShareUtil;
import com.hunliji.hljcommonlibrary.utils.TextCountWatcher;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.RoundProgressDialog;
import me.suncloud.marrymemo.widget.RoundProgressDialog.OnUpLoadComplate;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MyStoryListActivity extends BaseStoryListActivity implements OnClickListener {

    @Override
    public String pageTrackTagName() {
        return "我的故事";
    }

    public static final Executor STORIESTHEADPOOL = Executors.newFixedThreadPool(1);
    private StoryDBAdapter storyDb;
    private StoryItemDBAdapter itemsDb;
    private ShareUtil shareUtil;
    private Dialog dialog;
    private Dialog shareDialog;
    private long shareStoryId;
    private User user;
    private View progressBar;
    private EditText titleEdit;
    private ImageView coverView;
    private String cover;
    private Uri cropUri;
    private CheckBox openBox;
    private RoundProgressDialog createProgressDialog;
    private View newStroyView;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HljShare.RequestCode.SHARE_TO_QQ:
                    new HljTracker.Builder(MyStoryListActivity.this)
                            .eventableId(shareStoryId)
                            .eventableType("Story")
                            .screen("my_story_list_page")
                            .action("share")
                            .additional("QQ")
                            .build()
                            .add();
                    break;
                case HljShare.RequestCode.SHARE_TO_QQZONE:
                    new HljTracker.Builder(MyStoryListActivity.this)
                            .eventableId(shareStoryId)
                            .eventableType("Story")
                            .screen("my_story_list_page")
                            .action("share")
                            .additional("QQZone")
                            .build()
                            .add();
                    break;
                case HljShare.RequestCode.SHARE_TO_PENGYOU:
                    new HljTracker.Builder(MyStoryListActivity.this)
                            .eventableId(shareStoryId)
                            .eventableType("Story")
                            .screen("my_story_list_page")
                            .action("share")
                            .additional("Timeline")
                            .build()
                            .add();
                    break;
                case HljShare.RequestCode.SHARE_TO_WEIXIN:
                    new HljTracker.Builder(MyStoryListActivity.this)
                            .eventableId(shareStoryId)
                            .eventableType("Story")
                            .screen("my_story_list_page")
                            .action("share")
                            .additional("Session")
                            .build()
                            .add();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        footerView = getLayoutInflater().inflate(R.layout.list_foot_no_more_2, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_story);
        progressBar = findViewById(R.id.progressBar);
        storyDb = new StoryDBAdapter(this);
        itemsDb = new StoryItemDBAdapter(this);
        user = Session.getInstance()
                .getCurrentUser(this);
        titleEdit = (EditText) findViewById(R.id.title);
        openBox = (CheckBox) findViewById(R.id.private_off);
        newStroyView = findViewById(R.id.new_story_layout);
        newStroyView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyboard(null);
                newStroyView.setVisibility(View.GONE);
            }
        });
        coverView = (ImageView) findViewById(R.id.cover);
        titleEdit.addTextChangedListener(new TextCountWatcher(titleEdit, 20));
        progressBar.setVisibility(View.VISIBLE);
        onLoadCompleted(true);
        onRefresh(listView);
    }

    public void onNewStory(View view) {
        if (newStroyView.getVisibility() != View.VISIBLE) {
            openBox.setChecked(false);
            coverView.setImageResource(R.drawable.icon_pic_white);
            coverView.setScaleType(ScaleType.CENTER_INSIDE);
            cover = null;
            titleEdit.setText(null);
            newStroyView.setVisibility(View.VISIBLE);
        }
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (newStroyView.getVisibility() == View.VISIBLE) {
            newStroyView.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    public void hideKeyboard(View view) {
        if (this.getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    this.getCurrentFocus()
                            .getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Story story = (Story) parent.getAdapter()
                .getItem(position);
        if (story != null) {
            if (story.getId()
                    .intValue() == 0 || JSONUtil.isNetworkConnected(MyStoryListActivity.this)) {
                Intent intent = new Intent(this, MyStoryActivity.class);
                if (story.getId()
                        .intValue() == 0) {
                    intent.putExtra("hideMenu", true);
                }
                intent.putExtra("story", story);
                startActivityForResult(intent, Constants.RequestCode.STORY_EDIT);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            } else {
                Toast toast = Toast.makeText(MyStoryListActivity.this,
                        R.string.hint_edit_unconnected,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.STORY_CREATE:
                case Constants.RequestCode.STORY_EDIT:
                    Story story = null;
                    boolean isDeleted = false;
                    boolean isRefresh = false;
                    if (data != null) {
                        story = (Story) data.getSerializableExtra("story");
                        isDeleted = data.getBooleanExtra("isDeleted", false);
                        isRefresh = data.getBooleanExtra("isRefresh", false);
                    }
                    if (story == null) {
                        if (isRefresh) {
                            onRefresh(null);
                        }
                        return;
                    }
                    for (Story s : stories) {
                        if ((s.getId() > 0 && s.getId()
                                .equals(story.getId())) || (s.getExtraId() > 0 && s.getExtraId()
                                == story.getExtraId())) {
                            stories.set(stories.indexOf(s), story);
                            break;
                        }
                    }
                    if (!stories.contains(story)) {
                        stories.add(story);
                    } else if (isDeleted) {
                        stories.remove(story);
                    }
                    adapter.notifyDataSetChanged();
                    if (requestCode == Constants.RequestCode.STORY_CREATE && story.getId() > 0 &&
                            !JSONUtil.isEmpty(story.getCover())) {
                        shareNewStory(story);
                    }
                    break;
                case Constants.RequestCode.PHOTO_CROP:
                    if (cropUri != null) {
                        setCover(cropUri);
                    }
                    break;
                case Constants.RequestCode.PHOTO_FROM_GALLERY:
                    if (data == null) {
                        return;
                    }
                    Uri uri = data.getData();
                    if (uri != null) {
                        setCover(uri);
                        String path = JSONUtil.getImagePathForUri(uri, this);
                        if(!TextUtils.isEmpty(path)) {
                            showPhotoCrop(new File(path));
                        }
                    }
                    break;
                case Constants.RequestCode.PHOTO_FROM_CAMERA:
                    uri = Uri.fromFile(new File(cover));
                    if (uri != null) {
                        setCover(uri);
                        showPhotoCrop(new File(cover));
                    }
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setCover(Uri uri) {
        String path = JSONUtil.getImagePathForUri(uri, this);
        if (!JSONUtil.isEmpty(path)) {
            cover = path;
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
            task.loadImage(cover,
                    coverView.getWidth(),
                    ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(), R.drawable.icon_pic_white, task));
        }

    }

    public void showPhotoCrop(File file) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this,
                    getPackageName(),
                    file);
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
    public void onDataLoad(int page) {
        User user = Session.getInstance()
                .getCurrentUser(this);
        new GetMyStoriesTask().executeOnExecutor(STORIESTHEADPOOL,
                Constants.getAbsUrl(String.format(Constants.HttpPath.USER_STORIES_URL,
                        user.getId())));
    }

    public void shareNewStory(Story story) {
        if (shareDialog != null && shareDialog.isShowing()) {
            return;
        }
        shareStoryId = story.getId();
        shareUtil = new ShareUtil(this, story.getShareInfo(this), progressBar, handler);
        if (shareDialog == null) {
            shareDialog = new Dialog(this, R.style.BubbleDialogTheme);
            shareDialog.setContentView(R.layout.dialog_share_story);
            shareDialog.findViewById(R.id.share_pengyou)
                    .setOnClickListener(this);
            shareDialog.findViewById(R.id.share_weixing)
                    .setOnClickListener(this);
            shareDialog.findViewById(R.id.share_weibo)
                    .setOnClickListener(this);
            shareDialog.findViewById(R.id.share_qq)
                    .setOnClickListener(this);
            Window win = shareDialog.getWindow();
            LayoutParams params = win.getAttributes();
            win.setAttributes(params);
            win.setGravity(Gravity.CENTER);
        }
        shareDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share_pengyou:
                shareUtil.shareToPengYou();
                shareDialog.dismiss();
                break;
            case R.id.share_weixing:
                shareUtil.shareToWeiXing();
                shareDialog.dismiss();
                break;
            case R.id.share_weibo:
                shareUtil.shareToWeiBo();
                shareDialog.dismiss();
                break;
            case R.id.share_qq:
                shareUtil.shareToQQ();
                shareDialog.dismiss();
                break;
            case R.id.action_cancel:
                shareDialog.dismiss();
                break;
            default:
                break;
        }
    }

    public void onChooseCover(View v) {
        hideKeyboard(null);
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (dialog == null) {
            dialog = new Dialog(this, R.style.BubbleDialogTheme);
            dialog.setContentView(R.layout.dialog_add_menu);
            dialog.findViewById(R.id.action_cancel)
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

            dialog.findViewById(R.id.action_camera_video)
                    .setVisibility(View.GONE);
            dialog.findViewById(R.id.action_gallery)
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            MyStoryListActivityPermissionsDispatcher.onReadPhotosWithCheck(
                                    MyStoryListActivity.this);
                        }
                    });
            dialog.findViewById(R.id.action_camera_photo)
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            MyStoryListActivityPermissionsDispatcher.onTakePhotosWithCheck(
                                    MyStoryListActivity.this);
                        }
                    });
            Window win = dialog.getWindow();
            Point point = JSONUtil.getDeviceSize(this);
            LayoutParams params = win.getAttributes();
            params.width = point.x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        dialog.show();
    }

    public void upLoadCover() {
        if (cover.startsWith("http://") || cover.startsWith("https://")) {
            upLoadStoryInfo();
        } else {
            if (createProgressDialog == null) {
                createProgressDialog = JSONUtil.getRoundProgress(this);
            } else {
                createProgressDialog.reset();
                createProgressDialog.show();
            }
            createProgressDialog.setMessage(getString(R.string.hint_upload_cover));
            createProgressDialog.setProgress(0);
            new QiNiuUploadTask(this,
                    new OnHttpRequestListener() {

                        @Override
                        public void onRequestFailed(Object obj) {
                            if (createProgressDialog != null && createProgressDialog.isShowing()) {
                                createProgressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onRequestCompleted(Object obj) {
                            JSONObject json = (JSONObject) obj;
                            if (json != null) {
                                String key = JSONUtil.getString(json, "image_path");
                                String domain = JSONUtil.getString(json, "domain");
                                if (!JSONUtil.isEmpty(key) && !JSONUtil.isEmpty(domain)) {
                                    cover = domain + key;
                                    upLoadStoryInfo();
                                } else {
                                    if (createProgressDialog != null && createProgressDialog
                                            .isShowing()) {
                                        createProgressDialog.dismiss();
                                    }
                                }
                            } else {
                                if (createProgressDialog != null && createProgressDialog
                                        .isShowing()) {
                                    createProgressDialog.dismiss();
                                }
                            }
                        }
                    },
                    createProgressDialog).execute(Constants.getAbsUrl(Constants.HttpPath
                    .QINIU_IMAGE_URL),
                    new File(cover));
        }
    }

    public void upLoadStoryInfo() {
        if (createProgressDialog == null || !createProgressDialog.isShowing()) {
            return;
        }
        createProgressDialog.onLoadComplate();
        JSONObject storyObject = new JSONObject();
        try {
            storyObject.put("title",
                    titleEdit.getText()
                            .toString());
            storyObject.put("cover_path", cover);
            storyObject.put("state", 1);
            storyObject.put("opened", openBox.isChecked() ? 0 : 1);
        } catch (JSONException ignored) {
        }
        new StatusHttpPostTask(this, new StatusRequestListener() {

            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                JSONObject json = (JSONObject) object;
                if (json != null) {
                    long storyId = json.optLong("story_id");
                    final Intent intent = new Intent(MyStoryListActivity.this,
                            NewStoryActivity.class);
                    intent.putExtra("storyId", storyId);
                    intent.putExtra("isCreate", true);
                    if (createProgressDialog != null && createProgressDialog.isShowing()) {
                        createProgressDialog.setCancelable(false);
                        createProgressDialog.onComplate();
                        createProgressDialog.setOnUpLoadComplate(new OnUpLoadComplate() {

                            @Override
                            public void onUpLoadCompleted() {
                                startActivityForResult(intent, Constants.RequestCode.STORY_CREATE);
                                overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.activity_anim_default);
                                newStroyView.setVisibility(View.GONE);
                                onRefresh(listView);
                            }
                        });
                    } else {
                        startActivityForResult(intent, Constants.RequestCode.STORY_CREATE);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                        newStroyView.setVisibility(View.GONE);
                        onRefresh(listView);
                    }
                } else {
                    if (createProgressDialog != null && createProgressDialog.isShowing()) {
                        createProgressDialog.dismiss();
                    }
                    Util.postFailToast(MyStoryListActivity.this,
                            returnStatus,
                            R.string.hint_story_modify_error,
                            false);
                }

            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                if (createProgressDialog != null && createProgressDialog.isShowing()) {
                    createProgressDialog.dismiss();
                }
                Util.postFailToast(MyStoryListActivity.this,
                        returnStatus,
                        R.string.hint_story_modify_error,
                        network);
            }
        }, createProgressDialog).execute(Constants.getAbsUrl(Constants.HttpPath.NEW_POST_STORY_URL),
                storyObject.toString());

    }

    public void upLoadStory(View view) {
        if (JSONUtil.isEmpty(cover)) {
            return;
        }
        if (JSONUtil.isEmpty(titleEdit.getText()
                .toString()
                .trim())) {
            return;
        }
        upLoadCover();
    }

    public class GetMyStoriesTask extends AsyncTask<String, Object, ArrayList<Story>> {

        @Override
        protected ArrayList<Story> doInBackground(String... params) {
            ArrayList<Story> tempStories = new ArrayList<>();
            ArrayList<Story> localeStories = new ArrayList<>();
            ArrayList<Story> netStory = new ArrayList<>();
            storyDb.open();
            Cursor cursor = storyDb.getStoryList(user.getId());
            if (cursor.moveToFirst()) {
                do {
                    Story story = new Story(new JSONObject());
                    story.setTitle(cursor.getString(2));
                    story.setCover(cursor.getString(6));
                    story.setExtraId(cursor.getLong(1));
                    story.setUser(user);
                    story.setId(cursor.getLong(0));
                    story.setCommentCount(cursor.getInt(8));
                    story.setCollectCount(cursor.getInt(10));
                    story.setOpen(cursor.getInt(11) != 0);
                    localeStories.add(story);
                } while (cursor.moveToNext());
            }
            cursor.close();
            storyDb.close();
            try {
                String json = JSONUtil.getStringFromUrl(params[0]);
                if (JSONUtil.isEmpty(json)) {
                    return localeStories;
                }
                JSONObject jsonObject = new JSONObject(json).optJSONObject("data");
                JSONArray array = null;
                if (jsonObject != null) {
                    array = jsonObject.getJSONArray("list");
                }
                if (array != null) {
                    int size = array.length();
                    if (size > 0) {
                        for (int i = 0; i < size; i++) {
                            Story story = new Story(array.optJSONObject(i));
                            story.setUser(user);
                            if (!localeStories.isEmpty()) {
                                boolean isLocale = false;
                                for (Story s : localeStories) {
                                    if (s.getId()
                                            .equals(story.getId())) {
                                        s.setCommentCount(story.getCommentCount());
                                        s.setCollectCount(story.getCollectCount());
                                        storyDb.open();
                                        storyDb.updateStoryCount(s);
                                        storyDb.close();
                                        s.setComplete(true);
                                        isLocale = true;
                                        break;
                                    }
                                }
                                if (!isLocale) {
                                    netStory.add(story);
                                }
                            } else {
                                netStory.add(story);
                            }
                        }
                    }
                    for (Story s : localeStories) {
                        if (s.getId() > 0 && !s.isComplete()) {
                            storyDb.open();
                            storyDb.updateStoryToLocale(s.getId());
                            storyDb.close();
                            itemsDb.open();
                            itemsDb.updateItemToLocale(s.getExtraId());
                            itemsDb.close();
                            s.setId(0L);
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            if (!localeStories.isEmpty()) {
                tempStories.addAll(localeStories);
            }
            if (!netStory.isEmpty()) {
                tempStories.addAll(netStory);
            }
            return tempStories;
        }

        @Override
        protected void onPostExecute(ArrayList<Story> result) {
            progressBar.setVisibility(View.GONE);
            listView.onRefreshComplete();
            if (result != null) {
                stories.clear();
                stories.addAll(result);
            }
            adapter.notifyDataSetChanged();
            if (stories.isEmpty()) {
                View emptyView = listView.getRefreshableView()
                        .getEmptyView();
                if (emptyView == null) {
                    emptyView = findViewById(R.id.empty_hint_layout);
                    ImageView img = (ImageView) emptyView.findViewById(R.id.img_empty_hint);
                    img.setImageDrawable(getResources().getDrawable(R.drawable.icon_empty_story));
                    TextView tvEmptyHint = (TextView) emptyView.findViewById(R.id.btn_empty_hint);
                    tvEmptyHint.setText(getString(R.string.btn_add_story));
                    TextView tvEmptyHint1 = (TextView) emptyView.findViewById(R.id.text_empty_hint);
                    tvEmptyHint1.setText(getString(R.string.hint_story_empty));
                    tvEmptyHint1.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.VISIBLE);
                    img.setVisibility(View.VISIBLE);
                    tvEmptyHint.setVisibility(View.VISIBLE);
                    tvEmptyHint.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onNewStory(null);
                        }
                    });
                    listView.getRefreshableView()
                            .setEmptyView(emptyView);
                }
            }
            super.onPostExecute(result);
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onReadPhotos() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.RequestCode.PHOTO_FROM_GALLERY);
        dialog.dismiss();
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onTakePhotos() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = FileUtil.createImageFile();
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this,
                    getPackageName(),
                    f);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        } else {
            imageUri = Uri.fromFile(f);
        }
        cover = f.getAbsolutePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, Constants.RequestCode.PHOTO_FROM_CAMERA);
        dialog.dismiss();
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
        MyStoryListActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}