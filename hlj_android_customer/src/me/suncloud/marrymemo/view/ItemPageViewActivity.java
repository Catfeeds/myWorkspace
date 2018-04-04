package me.suncloud.marrymemo.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.HljCommon;

import java.util.ArrayList;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Item;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.ShareUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.HackyViewPager;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ItemPageViewActivity extends Activity {
    private static ArrayList<Item> items;
    private Item currentItem;
    private String title;
    private String description;
    private String weiboDescription;
    private String url;
    private Point point;
    private int width;
    private int height;
    private View shareBtn;
    private View reviewBtn;
    private TextView currentView;
    private TextView descriptionView;
    private View progressBar;
    private Dialog dialog;
    private ShareUtil shareUtil;
    private boolean unUpload;
    private Toast toast;
    private long shareItemId;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_page);
        ViewPager mViewPager = (HackyViewPager) findViewById(R.id.pager);
        shareBtn = findViewById(R.id.share);
        reviewBtn = findViewById(R.id.review);
        currentView = (TextView) findViewById(R.id.count);
        descriptionView = (TextView) findViewById(R.id.description);
        descriptionView.setMovementMethod(ScrollingMovementMethod.getInstance());
        progressBar = findViewById(R.id.progressBar);
        findViewById(R.id.title_layout).setBackgroundColor(getResources().getColor(R.color.black7));
        point = JSONUtil.getDeviceSize(this);
        width = Math.round(point.x * 3 / 2);
        height = Math.round(point.y * 5 / 2);
        if (height > JSONUtil.getMaximumTextureSize() && JSONUtil.getMaximumTextureSize() > 0) {
            height = JSONUtil.getMaximumTextureSize();
        }
        Intent intent = getIntent();
        items = (ArrayList<Item>) intent.getSerializableExtra("items");
        unUpload = intent.getBooleanExtra("unUpload", false);
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");
        weiboDescription = intent.getStringExtra("weiboDescription");
        url = intent.getStringExtra("url");
        int position = intent.getIntExtra("position", 0);
        mViewPager.setAdapter(new SamplePagerAdapter(this));
        mViewPager.setCurrentItem(position);
        menuChange(position);
        mViewPager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                menuChange(position);
            }
        });

        // 如果当前点击进入的这个photo是视频的话,直接跳转到播放页面
        if (items.get(position)
                .getKind() == (items.get(position)
                .getType() == Constants.ItemType.StoryItem ? 3 : 2)) {
            gotoPlayVideo(items.get(position));
        }
    }

    public void menuChange(int position) {
        currentView.setText(position + 1 + "/" + items.size());
        currentItem = items.get(position);
        switch (currentItem.getType()) {
            case Constants.ItemType.StoryItem:
                if (currentItem.getKind() == 1) {
                    isShowMenu(0);
                } else {
                    isShowMenu(1);
                }
                break;
            case Constants.ItemType.OpuItem:
            case Constants.ItemType.WorkItem:
                isShowMenu(2);
                break;
            case Constants.ItemType.SnapshotItem:
                isShowMenu(0);
                break;
        }
        if (currentItem.getKind() != (currentItem.getType() == Constants.ItemType.StoryItem ? 1 :
                0) && !JSONUtil.isEmpty(
                currentItem.getDescription())) {
            descriptionView.setVisibility(View.VISIBLE);
            descriptionView.setText(currentItem.getDescription());
        } else {
            descriptionView.setVisibility(View.GONE);
        }
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra("items", items);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Intent intent;
            switch (requestCode) {
                case Constants.Login.ITEM_REVIEW_LOGIN:
                    intent = new Intent(this, ItemReviewActivity.class);
                    intent.putExtra("item", currentItem);
                    intent.putExtra("reviewType", Constants.ReviewType.StoryItemReview);
                    startActivityForResult(intent, Constants.RequestCode.ITEM_REVIEW);
                    break;
                case Constants.RequestCode.ITEM_REVIEW:
                    if (data != null) {
                        int count = data.getIntExtra("count", currentItem.getCommentCount());
                        currentItem.setCommentCount(count);
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void isShowMenu(int type) {
        switch (type) {
            case 0:
                reviewBtn.setVisibility(View.GONE);
                shareBtn.setVisibility(View.GONE);
                break;
            case 1:
                reviewBtn.setVisibility(View.VISIBLE);
                shareBtn.setVisibility(View.VISIBLE);
                break;
            case 2:
                reviewBtn.setVisibility(View.GONE);
                shareBtn.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void onReview(View view) {
        if (unUpload) {
            if (toast == null) {
                toast = Toast.makeText(ItemPageViewActivity.this,
                        R.string.hint_story_no_upload_reiview,
                        Toast.LENGTH_SHORT);
            } else {
                toast.setText(R.string.hint_story_no_upload_reiview);
            }
            toast.show();
            return;
        }
        itemReview(currentItem);
    }

    public void onShare(View view) {
        if (unUpload) {
            if (toast == null) {
                toast = Toast.makeText(ItemPageViewActivity.this,
                        R.string.hint_story_no_upload_share,
                        Toast.LENGTH_SHORT);
            } else {
                toast.setText(R.string.hint_story_no_upload_share);
            }
            toast.show();
            return;
        }
        shareMenu(currentItem);
    }

    private void itemReview(Item item) {
        if (Util.loginBindChecked(ItemPageViewActivity.this, Constants.Login.ITEM_REVIEW_LOGIN)) {
            Intent intent = new Intent(this, ItemReviewActivity.class);
            intent.putExtra("item", item);
            intent.putExtra("reviewType", Constants.ReviewType.StoryItemReview);
            startActivityForResult(intent, Constants.RequestCode.ITEM_REVIEW);
        }
    }

    private void shareMenu(Item item) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        shareItemId = item.getId();
        if (item.getShareInfo() != null) {
            shareUtil = new ShareUtil(this, item.getShareInfo(), progressBar, null);
        } else {
            String imgUrl = null;
            if (item.getKind() == (item.getType() == Constants.ItemType.StoryItem ? 2 : 1)) {
                imgUrl = item.getMediaPath();
            } else {
                if (item.getPersistent() != null && !JSONUtil.isEmpty(item.getPersistent()
                        .getScreenShot())) {
                    imgUrl = item.getPersistent()
                            .getScreenShot();
                }
                if (JSONUtil.isEmpty(imgUrl)) {
                    if (item.getType() == Constants.ItemType.OpuItem) {
                        imgUrl = item.getMediaPath() + HljCommon.QINIU.SCREEN_SHOT_URL_3_SECONDS;
                    } else {
                        imgUrl = item.getMediaPath() + HljCommon.QINIU.SCREEN_SHOT_URL_1_SECONDS;
                    }
                }
            }
            if (JSONUtil.isEmpty(imgUrl)) {
                return;
            }
            shareUtil = new ShareUtil(this,
                    url,
                    title,
                    description,
                    weiboDescription,
                    imgUrl,
                    progressBar);
        }
        if (dialog == null) {
            dialog = Util.initShareDialog(this, shareUtil,null);
        }
        dialog.show();

    }

    public class SamplePagerAdapter extends PagerAdapter {

        private Context mContext;

        public SamplePagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.story_item_view, null, false);
            final Item item = items.get(position);
            final ViewHolder holder = new ViewHolder();
            holder.image = (PhotoView) view.findViewById(R.id.image);
            holder.textLayout = view.findViewById(R.id.text_layout);
            holder.play = view.findViewById(R.id.play);
            holder.progressBar = view.findViewById(R.id.progressBar);
            holder.textItemDescription = (TextView) view.findViewById(R.id.text);

            if (item.getKind() == (item.getType() == Constants.ItemType.StoryItem ? 1 : 0)) {
                holder.progressBar.setVisibility(View.GONE);
                holder.textLayout.setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.GONE);
                holder.textItemDescription.setText(item.getDescription());
                holder.textItemDescription.setMovementMethod(ScrollingMovementMethod.getInstance());
            } else {
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.textLayout.setVisibility(View.GONE);
                holder.image.setVisibility(View.VISIBLE);
                ImageLoadTask task = new ImageLoadTask(holder.image, new OnHttpRequestListener() {

                    @Override
                    public void onRequestCompleted(Object obj) {
                        holder.progressBar.setVisibility(View.GONE);
                        Bitmap bitmap = (Bitmap) obj;
                        float rate = (float) point.x / bitmap.getWidth();
                        int w = point.x;
                        int h = Math.round(bitmap.getHeight() * rate);
                        if (h > point.y || (h * point.x > w * point.y)) {
                            holder.image.setScaleType(ScaleType.CENTER_CROP);
                        } else {
                            holder.image.setScaleType(ScaleType.FIT_CENTER);
                        }
                    }

                    @Override
                    public void onRequestFailed(Object obj) {

                    }
                });
                if (item.getKind() == (item.getType() == Constants.ItemType.StoryItem ? 3 : 2)) {
                    holder.image.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                        @Override
                        public void onPhotoTap(View view, float x, float y) {
                            gotoPlayVideo(item);
                        }
                    });
                    holder.play.setVisibility(View.VISIBLE);
                    if (item.getMediaPath()
                            .startsWith("http")) {
                        String imgUrl;
                        if (item.getPersistent() != null && !JSONUtil.isEmpty(item.getPersistent()
                                .getScreenShot())) {
                            imgUrl = JSONUtil.getImagePath(item.getPersistent()
                                    .getScreenShot(), point.x);
                        } else {
                            if (item.getType() != Constants.ItemType.StoryItem) {
                                imgUrl = item.getMediaPath() + String.format(Constants
                                                .VIDEO_URL_TEN,
                                        point.x)
                                        .replace("|", JSONUtil.getURLEncoder());
                            } else {
                                imgUrl = item.getMediaPath() + String.format(Constants.VIDEO_URL,
                                        point.x)
                                        .replace("|", JSONUtil.getURLEncoder());
                            }
                        }
                        holder.image.setTag(imgUrl);
                        AsyncBitmapDrawable asyncDrawable = new AsyncBitmapDrawable(mContext
                                .getResources(),
                                R.mipmap.icon_empty_image,
                                task);
                        task.loadImage(imgUrl, width, ScaleMode.WIDTH, asyncDrawable);
                    } else {
                        holder.play.setVisibility(View.VISIBLE);
                        holder.image.setTag(item.getMediaPath());
                        task.loadThumbforPath(item.getMediaPath());
                    }
                } else if (item.getKind() == (item.getType() == Constants.ItemType.StoryItem ? 2
                        : 1)) {
                    String imgUrl;
                    holder.play.setVisibility(View.GONE);
                    if (item.getMediaPath()
                            .startsWith("http")) {
                        imgUrl = item.getMediaPath() + String.format(Constants.PHOTO_URL2,
                                width,
                                height);
                    } else {
                        imgUrl = item.getMediaPath();
                    }
                    holder.image.setTag(imgUrl);
                    AsyncBitmapDrawable asyncDrawable = new AsyncBitmapDrawable(mContext
                            .getResources(),
                            R.mipmap.icon_empty_image,
                            task);
                    task.loadImage(imgUrl, width, ScaleMode.WIDTH, asyncDrawable);
                }
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        private class ViewHolder {
            TextView textItemDescription;
            PhotoView image;
            View textLayout;
            View progressBar;
            View play;
        }

    }

    private void gotoPlayVideo(Item item) {
        if (!JSONUtil.isEmpty(item.getMediaPath()) && item.getKind() == (item.getType() ==
                Constants.ItemType.StoryItem ? 3 : 2)) {
            String videoPath;
            if (item.getMediaPath()
                    .startsWith("http")) {
                videoPath = JSONUtil.getVideoPath(item.getPersistent());
                if (JSONUtil.isEmpty(videoPath)) {
                    videoPath = item.getMediaPath()
                            .startsWith("http") ? item.getMediaPath() : Constants.HOST + item
                            .getMediaPath();
                }
            } else {
                videoPath = item.getMediaPath();
            }
            Intent intent = new Intent(this, VideoViewActivity.class);
            intent.putExtra("path", videoPath);
            startActivity(intent);
        }
    }

}
