package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljmaplibrary.views.activities.NavigateMapActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter.ViewBinder;
import me.suncloud.marrymemo.model.Item;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.Story;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.ShareUtil;
import me.suncloud.marrymemo.util.Util;

public class BaseStoryActivity extends HljBaseNoBarActivity implements ViewBinder<Item>,
        OnClickListener, OnItemClickListener {
    protected ArrayList<Item> items;
    protected ObjectBindAdapter<Item> adapter;
    protected Story story;
    private View heardView;
    private View footerView;
    private ListView listView;
    private Item item;
    private View progressBar;
    private int width;
    private int imgWidth;
    private TextView reviewCount;
    private TextView collectCount;
    private ImageButton collecteButton;
    private LikeClickListener likeClickListener;
    private Dialog dialog;
    private Toast toast;
    private ShareUtil shareUtil;
    private long shareItemId;
    private Dialog menuDialog;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setDefaultStatusBarPadding();
        Point point = JSONUtil.getDeviceSize(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = Math.round(point.x - 16 * dm.density);
        if (width <= 805) {
            imgWidth = width;
        } else {
            imgWidth = width * 3 / 4;
        }
        listView = (ListView) findViewById(R.id.story_list);
        progressBar = findViewById(R.id.progressBar);
        collecteButton = (ImageButton) findViewById(R.id.collect);
        heardView = getLayoutInflater().inflate(R.layout.story_heard_item, null);
        reviewCount = (TextView) heardView.findViewById(R.id.review_count);
        collectCount = (TextView) heardView.findViewById(R.id.collect_count);
        footerView = getLayoutInflater().inflate(R.layout.list_foot_no_more_2, null);
        heardView.setVisibility(View.GONE);
        footerView.setVisibility(View.GONE);
        listView.addHeaderView(heardView);
        listView.addFooterView(footerView);
        listView.setOnItemClickListener(this);
        items = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, items, R.layout.story_item);
        adapter.setViewBinder(this);
        listView.setAdapter(adapter);

    }

    protected void showStory(final Story story) {
        this.story = story;
        reviewCount.setText(getString(R.string.label_review_count3, story.getCommentCount()));
        collectCount.setText(getString(R.string.label_like_count, story.getCollectCount()));
        TextView title = (TextView) heardView.findViewById(R.id.story_title);
        TextView userName = (TextView) heardView.findViewById(R.id.user_name);
        ImageView userIcon = (ImageView) heardView.findViewById(R.id.ower_icon);
        title.setText(getString(R.string.label_story_title, story.getTitle()));
        if (story.getUser() != null) {
            userName.setText(story.getUser()
                    .getNick());
            String url = story.getUser()
                    .getAvatar();
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

            userIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BaseStoryActivity.this, UserProfileActivity.class);
                    intent.putExtra("id",
                            story.getUser()
                                    .getId());
                    startActivity(intent);
                }
            });
        }
        heardView.setVisibility(View.VISIBLE);
        footerView.setVisibility(View.VISIBLE);
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void setViewValue(View view, final Item t, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder();
            holder.imageItemView = view.findViewById(R.id.image_item);
            holder.collectView = (ImageView) view.findViewById(R.id.collect);
            holder.locationView = (TextView) view.findViewById(R.id.location);
            holder.play = view.findViewById(R.id.paly);
            holder.shareView = view.findViewById(R.id.share);
            holder.reviewView = view.findViewById(R.id.review);
            holder.textItemDescribe = (TextView) view.findViewById(R.id.text_item_describe);
            holder.imageItemDescribe = (TextView) view.findViewById(R.id.item_describe);
            holder.reviewCount = (TextView) view.findViewById(R.id.review_count);
            holder.collectCount = (TextView) view.findViewById(R.id.collect_count);
            holder.imageView = (ImageView) view.findViewById(R.id.story_image);
            view.setTag(holder);
        }
        final ViewHolder holder = (ViewHolder) view.getTag();
        if (t.getKind() == 1) {
            holder.imageItemView.setVisibility(View.GONE);
            holder.textItemDescribe.setVisibility(View.VISIBLE);
            holder.textItemDescribe.setText(t.getDescription());
        } else {
            holder.reviewCount.setText(getString(R.string.label_review_count3,
                    t.getCommentCount()));
            holder.reviewView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (story.getExtraId() != 0) {
                        if (toast == null) {
                            toast = Toast.makeText(BaseStoryActivity.this,
                                    R.string.hint_story_no_upload_reiview,
                                    Toast.LENGTH_SHORT);
                        } else {
                            toast.setText(R.string.hint_story_no_upload_reiview);
                        }
                        toast.show();
                        return;
                    }
                    if (Util.loginBindChecked(BaseStoryActivity.this,
                            Constants.Login.ITEM_REVIEW_LOGIN)) {
                        item = t;
                        Intent intent = new Intent(BaseStoryActivity.this,
                                ItemReviewActivity.class);
                        intent.putExtra("item", item);
                        intent.putExtra("reviewType", Constants.ReviewType.StoryItemReview);
                        startActivityForResult(intent, Constants.RequestCode.STORY_ITEM_REVIEW);
                    } else {
                        item = t;
                    }
                }
            });
            holder.collectCount.setText(getString(R.string.label_like_count, t.getCollectCount()));
            if (t.isCollected()) {
                holder.collectView.setImageResource(R.mipmap.icon_praise_primary_40_40);
            } else {
                holder.collectView.setImageResource(R.mipmap.icon_praise_gray_40_40);
            }
            holder.collectView.setOnClickListener(new LikeClickListener(t,
                    holder.collectView,
                    holder.collectCount));
            holder.textItemDescribe.setVisibility(View.GONE);
            holder.imageItemView.setVisibility(View.VISIBLE);
            if (!JSONUtil.isEmpty(t.getDescription())) {
                holder.imageItemDescribe.setVisibility(View.VISIBLE);
                holder.imageItemDescribe.setText(t.getDescription());
            } else {
                holder.imageItemDescribe.setVisibility(View.GONE);
            }
            ViewGroup.MarginLayoutParams params;
            params = (MarginLayoutParams) holder.imageView.getLayoutParams();
            if ((t.getWidth() != 0) && (t.getHight() != 0)) {
                params.height = Math.round(width * t.getHight() / t.getWidth());
            }
            ImageLoadTask task = new ImageLoadTask(holder.imageView, new OnHttpRequestListener() {

                @Override
                public void onRequestCompleted(Object obj) {
                    if ((t.getWidth() == 0) || (t.getHight() == 0)) {
                        Bitmap bitmap = (Bitmap) obj;
                        ViewGroup.MarginLayoutParams params;
                        params = (MarginLayoutParams) holder.imageView.getLayoutParams();
                        params.height = Math.round(width * bitmap.getHeight() / bitmap.getWidth());
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {

                }
            });
            String url = t.getMediaPath();
            if (url.startsWith("http")) {
                if (t.getKind() == 2) {
                    url = JSONUtil.getImagePath(t.getMediaPath(), imgWidth);
                    holder.play.setVisibility(View.GONE);
                } else {
                    if (t.getPersistent() != null && !JSONUtil.isEmpty(t.getPersistent()
                            .getScreenShot())) {
                        url = JSONUtil.getImagePath(t.getPersistent()
                                .getScreenShot(), imgWidth);
                    } else {
                        url = t.getMediaPath() + String.format(Constants.VIDEO_URL, imgWidth)
                                .replace("|", JSONUtil.getURLEncoder());
                    }
                    holder.play.setVisibility(View.VISIBLE);
                }
                holder.imageView.setTag(url);
                AsyncBitmapDrawable asyncDrawable = new AsyncBitmapDrawable(getResources(),
                        R.mipmap.icon_empty_image,
                        task);
                task.loadImage(url, imgWidth, ScaleMode.WIDTH, asyncDrawable);
            } else {
                holder.imageView.setTag(t.getMediaPath());
                if (t.getKind() == 3) {
                    task.loadThumbforPath(t.getMediaPath());
                    holder.play.setVisibility(View.VISIBLE);
                } else {
                    holder.play.setVisibility(View.GONE);
                    task.loadImage(t.getMediaPath(),
                            imgWidth,
                            ScaleMode.ALL,
                            new AsyncBitmapDrawable(getResources(),
                                    R.mipmap.icon_empty_image,
                                    task));
                }
            }

        }
        holder.shareView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (story.getExtraId() != 0) {
                    if (toast == null) {
                        toast = Toast.makeText(BaseStoryActivity.this,
                                R.string.hint_story_no_upload_share,
                                Toast.LENGTH_SHORT);
                    } else {
                        toast.setText(R.string.hint_story_no_upload_share);
                    }
                    toast.show();
                    return;
                }
                if (story.getId() > 0) {
                    if (t.getShareInfo() != null) {
                        shareUtil = new ShareUtil(BaseStoryActivity.this,
                                t.getShareInfo(),
                                progressBar,
                                null);
                    } else {
                        String imgUrl = null;
                        if (t.getKind() == 2) {
                            imgUrl = t.getMediaPath();
                        } else {
                            if (t.getPersistent() != null && !JSONUtil.isEmpty(t.getPersistent()
                                    .getScreenShot())) {
                                imgUrl = t.getPersistent()
                                        .getScreenShot();
                            }
                            if (JSONUtil.isEmpty(imgUrl)) {
                                imgUrl = t.getMediaPath() + HljCommon.QINIU
                                        .SCREEN_SHOT_URL_1_SECONDS;
                            }
                        }
                        if (JSONUtil.isEmpty(imgUrl)) {
                            return;
                        }
                        shareUtil = new ShareUtil(BaseStoryActivity.this,
                                story.getShareInfo(BaseStoryActivity.this),
                                progressBar,
                                null);
                        shareUtil.setImgUrl(imgUrl);
                    }
                    shareItemId = t.getId();
                    share();
                }
            }
        });
        holder.locationView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseStoryActivity.this, NavigateMapActivity.class);
                intent.putExtra(NavigateMapActivity.ARG_TITLE, t.getPlace());
                intent.putExtra(NavigateMapActivity.ARG_ADDRESS, t.getPlace());
                intent.putExtra(NavigateMapActivity.ARG_LATITUDE, t.getLatitude());
                intent.putExtra(NavigateMapActivity.ARG_LONGITUDE, t.getLongitude());
                startActivity(intent);
            }
        });
        if (t.hasPlace() && !JSONUtil.isEmpty(t.getPlace()) && t.getLatitude() != 0 && t
                .getLongitude() != 0) {
            holder.locationView.setVisibility(View.VISIBLE);
            holder.locationView.setText("#" + t.getPlace());
        } else {
            holder.locationView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void loginComplete(int type) {
        if (story != null && story.getId()
                .intValue() != 0) {
            if (type == Constants.Login.LIKE_LOGIN) {
                if (story.getCollected()) {
                    story.setCollectCount(story.getCollectCount() - 1);
                    collecteButton.setImageResource(R.drawable.icon_praise_primary_40_40_3);
                } else {
                    story.setCollectCount(story.getCollectCount() + 1);
                    collecteButton.setImageResource(R.mipmap.icon_praise_primary_40_40);
                }
                collectCount.setText(getString(R.string.label_like_count, story.getCollectCount()));
                story.setCollected(!story.getCollected());
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", story.getId());
                    new StatusHttpPostTask(this, new StatusRequestListener() {

                        @Override
                        public void onRequestCompleted(
                                @Nullable Object object, ReturnStatus returnStatus) {
                        }

                        @Override
                        public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                            Util.postFailToast(BaseStoryActivity.this, returnStatus, 0, network);
                            if (story.getCollected()) {
                                story.setCollectCount(story.getCollectCount() - 1);
                                collecteButton.setImageResource(R.drawable
                                        .icon_praise_primary_40_40_3);
                            } else {
                                story.setCollectCount(story.getCollectCount() + 1);
                                collecteButton.setImageResource(R.mipmap.icon_praise_primary_40_40);
                            }
                            collectCount.setText(getString(R.string.label_like_count,
                                    story.getCollectCount()));
                            story.setCollected(!story.getCollected());
                        }
                    }).execute(Constants.getAbsUrl(Constants.HttpPath.STORY_COLLECT_URL),
                            jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_cancel:
                if (menuDialog != null) {
                    menuDialog.dismiss();
                }
                break;
            case R.id.action_edit:
                menuDialog.dismiss();
                break;
            case R.id.action_sync:
                menuDialog.dismiss();
                break;
            case R.id.action_delete:
                menuDialog.dismiss();
                break;
            default:
                break;
        }
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    public void reviewStory(View v) {
        if (story == null) {
            return;
        }
        if (story.getExtraId() != 0) {
            if (toast == null) {
                toast = Toast.makeText(BaseStoryActivity.this,
                        R.string.hint_story_no_upload_reiview,
                        Toast.LENGTH_SHORT);
            } else {
                toast.setText(R.string.hint_story_no_upload_reiview);
            }
            toast.show();
            return;
        }
        if (story.getId()
                .intValue() != 0) {
            if (Util.loginBindChecked(BaseStoryActivity.this, Constants.Login.REVIEW_LOGIN)) {
                Intent intent = new Intent(this, StoryReviewListActivity.class);
                intent.putExtra("id", story.getId());
                intent.putExtra("count", story.getCommentCount());
                startActivityForResult(intent, Constants.RequestCode.STORY_REVIEW);
            }
        }
    }

    public void collectStory(View v) {
        if (story == null) {
            return;
        }
        if (story.getExtraId() != 0) {
            if (toast == null) {
                toast = Toast.makeText(BaseStoryActivity.this,
                        R.string.hint_story_no_upload_collect,
                        Toast.LENGTH_SHORT);
            } else {
                toast.setText(R.string.hint_story_no_upload_collect);
            }
            toast.show();
            return;
        }
        if (story.getId()
                .intValue() != 0) {
            if (Util.loginBindChecked(BaseStoryActivity.this, Constants.Login.LIKE_LOGIN)) {
                loginComplete(Constants.Login.LIKE_LOGIN);
            }
        }
    }

    private void share() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (dialog == null) {
            dialog = Util.initShareDialog(this, shareUtil,null);
        }
        dialog.show();
    }

    public void shareStory(View v) {
        if (story == null) {
            return;
        }
        if (story.getExtraId() != 0) {
            if (toast == null) {
                toast = Toast.makeText(BaseStoryActivity.this,
                        R.string.hint_story_no_upload_share,
                        Toast.LENGTH_SHORT);
            } else {
                toast.setText(R.string.hint_story_no_upload_share);
            }
            toast.show();
            return;
        }
        if (story.getId() > 0 && story.getShareInfo(this) != null) {
            shareUtil = new ShareUtil(this, story.getShareInfo(this), progressBar, null);
            shareItemId = 0;
            share();
        }
    }

    public void editStory(View v) {
        if (story == null) {
            return;
        }
        if (menuDialog != null && menuDialog.isShowing()) {
            return;
        }
        if (menuDialog == null) {
            menuDialog = new Dialog(this, R.style.BubbleDialogTheme);
            menuDialog.setContentView(R.layout.dialog_story_edit_memu);
            menuDialog.findViewById(R.id.action_edit)
                    .setOnClickListener(this);
            menuDialog.findViewById(R.id.action_sync)
                    .setOnClickListener(this);
            menuDialog.findViewById(R.id.action_delete)
                    .setOnClickListener(this);
            menuDialog.findViewById(R.id.action_cancel)
                    .setOnClickListener(this);
            Window win = menuDialog.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = point.x;
            win.setAttributes(params);
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        menuDialog.findViewById(R.id.action_sync)
                .setVisibility(story.getExtraId() > 0 ? View.VISIBLE : View.GONE);
        menuDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Intent intent;
            switch (requestCode) {
                case Constants.Login.REVIEW_LOGIN:
                    intent = new Intent(this, StoryReviewListActivity.class);
                    intent.putExtra("id", story.getId());
                    intent.putExtra("count", story.getCommentCount());
                    startActivityForResult(intent, Constants.RequestCode.STORY_REVIEW);
                    break;
                case Constants.Login.LIKE_LOGIN:
                    loginComplete(Constants.Login.LIKE_LOGIN);
                    break;
                case Constants.Login.PRAISE_LOGIN:
                    loginComplete(Constants.Login.PRAISE_LOGIN);
                    break;
                case Constants.Login.ITEM_REVIEW_LOGIN:
                    intent = new Intent(BaseStoryActivity.this, ItemReviewActivity.class);
                    intent.putExtra("item", item);
                    intent.putExtra("reviewType", Constants.ReviewType.StoryItemReview);
                    startActivityForResult(intent, Constants.RequestCode.STORY_ITEM_REVIEW);
                    break;
                case Constants.RequestCode.ITEM_PAGE_VIEW:
                    if (data != null) {
                        @SuppressWarnings("unchecked") ArrayList<Item> nItems = (ArrayList<Item>)
                                data.getSerializableExtra(
                                "items");
                        items.clear();
                        items.addAll(nItems);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case Constants.RequestCode.STORY_ITEM_REVIEW:
                    if (data != null) {
                        int count = data.getIntExtra("count", item.getCommentCount());
                        item.setCommentCount(count);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case Constants.Login.ITEM_LIKE_LOGIN:
                    likeClickListener.collectComplete();
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(
            AdapterView<?> parent, View view, int position, long id) {
        Item item = (Item) parent.getAdapter()
                .getItem(position);
        if (item != null) {
            Intent intent = new Intent(this, ItemPageViewActivity.class);
            intent.putExtra("items", items);
            intent.putExtra("title",
                    story.getShareInfo(this)
                            .getTitle());
            intent.putExtra("description",
                    story.getShareInfo(this)
                            .getDesc());
            intent.putExtra("weiboDescription",
                    story.getShareInfo(this)
                            .getDesc2());
            intent.putExtra("url",
                    story.getShareInfo(this)
                            .getUrl());
            if (story.getExtraId() != 0) {
                intent.putExtra("unUpload", true);
            }
            intent.putExtra("position", items.indexOf(item));
            startActivityForResult(intent, Constants.RequestCode.ITEM_PAGE_VIEW);
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
            if (result != null) {
                if (!result.isNull("story")) {
                    Story story = new Story(result.optJSONObject("story"));
                    Intent intent = getIntent();
                    intent.putExtra("story", story);
                    setResult(RESULT_OK, intent);
                    if (story.getCollected()) {
                        collecteButton.setImageResource(R.mipmap.icon_praise_primary_40_40);
                    } else {
                        collecteButton.setImageResource(R.drawable.icon_praise_primary_40_40_3);
                    }
                    showStory(story);
                }
                if (!result.isNull("items")) {
                    items.clear();
                    JSONArray array = result.optJSONArray("items");
                    int size = array.length();
                    if (size > 0) {
                        for (int i = 0; i < size; i++) {
                            Item item = new Item(array.optJSONObject(i));
                            switch (item.getKind()) {
                                case 1:
                                    if (!JSONUtil.isEmpty(item.getDescription())) {
                                        item.setType(Constants.ItemType.StoryItem);
                                        items.add(item);
                                    }
                                    break;
                                case 2:
                                case 3:
                                    if (!JSONUtil.isEmpty(item.getMediaPath())) {
                                        item.setType(Constants.ItemType.StoryItem);
                                        items.add(item);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        if (size > 1) {
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
                    footerView.findViewById(R.id.no_more_hint)
                            .setVisibility(View.VISIBLE);
                }
            } else {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                }
                if (items.isEmpty()) {
                    View emptyView = listView.getEmptyView();
                    if (emptyView == null) {
                        emptyView = findViewById(R.id.empty_hint_layout);
                        listView.setEmptyView(emptyView);
                    }
                    ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
                    TextView textEmptyHint = (TextView) emptyView.findViewById(R.id
                            .text_empty_hint);

                    imgNetHint.setVisibility(View.VISIBLE);
                    textEmptyHint.setVisibility(View.VISIBLE);
                }

            }
            super.onPostExecute(result);
        }
    }

    private class ViewHolder {
        View imageItemView;
        View reviewView;
        View shareView;
        ImageView collectView;
        ImageView imageView;
        TextView textItemDescribe;
        TextView imageItemDescribe;
        TextView reviewCount;
        TextView collectCount;
        TextView locationView;
        View play;
    }

    private class LikeClickListener implements View.OnClickListener {
        private Item item;
        private TextView collectCount;
        private ImageView collectView;

        public LikeClickListener(
                Item item, ImageView collectView, TextView collectCount) {
            super();
            this.item = item;
            this.collectView = collectView;
            this.collectCount = collectCount;

        }

        @Override
        public void onClick(View v) {
            if (story.getExtraId() != 0) {
                if (toast == null) {
                    toast = Toast.makeText(BaseStoryActivity.this,
                            R.string.hint_story_no_upload_collect,
                            Toast.LENGTH_SHORT);
                } else {
                    toast.setText(R.string.hint_story_no_upload_collect);
                }
                toast.show();
                return;
            }
            if (Util.loginBindChecked(BaseStoryActivity.this, Constants.Login.ITEM_LIKE_LOGIN)) {
                collectComplete();
            } else {
                likeClickListener = this;
            }
        }

        private void collectComplete() {
            if (!item.isCollected()) {
                item.setCollected(true);
                collectView.setImageResource(R.mipmap.icon_praise_primary_40_40);
                item.setCollectCount(item.getCollectCount() + 1);
                collectCount.setText(getString(R.string.label_like_count, item.getCollectCount()));
            } else {
                item.setCollected(false);
                collectView.setImageResource(R.mipmap.icon_praise_gray_40_40);
                item.setCollectCount(item.getCollectCount() - 1);
                collectCount.setText(getString(R.string.label_like_count, item.getCollectCount()));
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("story_item_id", item.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new StatusHttpPostTask(BaseStoryActivity.this, null).execute(Constants.getAbsUrl(
                    Constants.HttpPath.STORY_ITEM_COLLECT_URL), jsonObject.toString());
        }

    }

}