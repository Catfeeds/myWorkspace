package com.hunliji.marrybiz.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.Author;
import com.hunliji.marrybiz.model.Comment;
import com.hunliji.marrybiz.model.CustomSetmeal;
import com.hunliji.marrybiz.model.DataConfig;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.NewMerchant;
import com.hunliji.marrybiz.model.ReturnStatus;
import com.hunliji.marrybiz.model.Work;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.task.NewHttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.ShareUtil;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.util.popuptip.PopupRule;
import com.hunliji.marrybiz.view.shop.ShopWebViewActivity;
import com.hunliji.marrybiz.widget.MyScrollView;
import com.hunliji.marrybiz.widget.RecyclingImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by jinxin on 2016/1/25.
 */
public class CustomSetMealDetailActivity extends HljBaseNoBarActivity implements View
        .OnClickListener, MyScrollView.OnScrollChangedListener {
    private int workImgWidth;
    private CustomSetmeal customSetmeal;
    private DisplayMetrics dm;
    private Point point;

    private View progressBar;
    private View heardViewLayout;
    private View emptyHintLayout;
    private View imgEmptyHint;
    private TextView textEmptyHint;
    private long id;
    private int headPhotoHeight;
    private View actionLayout;
    private View shadowView;
    private MyScrollView scrollView;
    private Button btnOpu;
    //上架下架的标志  0已经下架这里做上架操作  1已经上架这里做下架操作  -1不显示上架下架按钮
    private int isPublished;
    private Dialog shareDialog;
    private ShareUtil shareUtil;
    private Dialog proDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getLongExtra("id", 0);
        isPublished = getIntent().getIntExtra("isPublished", -1);
        dm = getResources().getDisplayMetrics();
        point = JSONUtil.getDeviceSize(this);
        workImgWidth = Math.round(dm.density * 116);
        workImgWidth = Math.round((point.x - 36 * dm.density) / 2);
        headPhotoHeight = Math.round(dm.density * 200);
        setContentView(R.layout.activity_individual_merchant);
        setDefaultStatusBarPadding();

        progressBar = findViewById(R.id.progressBar);
        heardViewLayout = findViewById(R.id.heard_view_layout);
        emptyHintLayout = findViewById(R.id.empty_hint_layout);
        imgEmptyHint = findViewById(R.id.img_empty_hint);
        textEmptyHint = (TextView) findViewById(R.id.text_empty_hint);
        actionLayout = findViewById(R.id.action_layout);
        shadowView = findViewById(R.id.shadow_view);

        scrollView = (MyScrollView) findViewById(R.id.scroll_view);
        scrollView.setOnScrollChangedListener(this);
        btnOpu = (Button) findViewById(R.id.btn_opu);
        btnOpu.setVisibility(isPublished == -1 ? View.GONE : View.VISIBLE);
        if (isPublished == 1) {
            btnOpu.setText(getString(R.string.label_opu_off));
        } else if (isPublished == 0) {
            btnOpu.setText(getString(R.string.label_opu_on));
        }
        progressBar.setVisibility(View.VISIBLE);
        new GetMerchatTask().executeOnExecutor(Constants.INFOTHEADPOOL, id);
        new GetWorksTask().executeOnExecutor(Constants.INFOTHEADPOOL, id);
    }

    public void onBackPressed(View view) {
        finish();
    }

    public void onShare(View view) {
        if (customSetmeal == null || customSetmeal.getShareInfo() == null) {
            return;
        }
        if (shareDialog != null && shareDialog.isShowing()) {
            return;
        }
        if (shareUtil == null) {
            shareUtil = new ShareUtil(this, customSetmeal.getShareInfo(), progressBar, null);
        }
        if (shareDialog == null) {
            shareDialog = Util.initShareDialog(this, this);
        }
        shareDialog.show();
    }

    public void onOpu(View view) {
        if (customSetmeal == null) {
            return;
        }
        MerchantUser user = Session.getInstance()
                .getCurrentUser(this);
        if (isPublished == 0 && !user.isPro()) {
            PopupRule.getDefault()
                    .showProDialog(CustomSetMealDetailActivity.this, progressBar);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        Map<String, Object> map = new HashMap<>();
        map.put("id", customSetmeal.getId());
        map.put("is_published", isPublished == 1 ? 0 : 1);
        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                progressBar.setVisibility(View.GONE);
                JSONObject jsonObject = (JSONObject) obj;
                if (jsonObject != null) {
                    ReturnStatus status = new ReturnStatus(jsonObject.optJSONObject("status"));
                    int statusCode = status.getRetCode();
                    String statusMsg = status.getErrorMsg();
                    if (statusCode == 0) {
                        Util.showToast(R.string.label_opu_take_ok,
                                CustomSetMealDetailActivity.this);
                        if (isPublished == 1) {
                            isPublished = 0;
                            btnOpu.setText(getString(R.string.label_opu_on));
                        } else if (isPublished == 0) {
                            isPublished = 1;
                            btnOpu.setText(getString(R.string.label_opu_off));
                        }
                        Intent intent = getIntent();
                        intent.putExtra("isPublished", isPublished == 1);
                        setResult(Activity.RESULT_OK, intent);
                    } else {
                        Util.showToast(statusMsg, CustomSetMealDetailActivity.this);
                    }
                }
            }

            @Override
            public void onRequestFailed(Object obj) {
                progressBar.setVisibility(View.GONE);
                Util.showToast(R.string.label_opu_take_fail, CustomSetMealDetailActivity.this);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.CUSTOM_SETMEAL_OPU), map);
    }

    private void showEmptyInfo() {
        findViewById(R.id.btn_opu).setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        heardViewLayout.setVisibility(View.GONE);
        emptyHintLayout.setVisibility(View.VISIBLE);
        imgEmptyHint.setVisibility(View.VISIBLE);
        textEmptyHint.setVisibility(View.VISIBLE);
        textEmptyHint.setText(getString(R.string.no_item));
    }

    private void initMealInfo() {
        if (customSetmeal == null) {
            showEmptyInfo();
            return;
        }
        RecyclingImageView headPhoto = (RecyclingImageView) findViewById(R.id.head_photo);
        String headPhotoUrl = JSONUtil.getImagePath(customSetmeal.getHeaderPhoto(),
                Math.round(dm.density * 200));
        if (!JSONUtil.isEmpty(headPhotoUrl)) {
            ImageLoadTask task = new ImageLoadTask(headPhoto, 0);
            headPhoto.setTag(headPhotoUrl);
            task.loadImage(headPhotoUrl,
                    Math.round(dm.density * 200),
                    ScaleMode.HEIGHT,
                    new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
        }

        initMerchantView(customSetmeal.getMerchant());
        initWorkInfo(customSetmeal);
        initPromise(customSetmeal);
    }

    private void initMerchantView(NewMerchant merchant) {
        if (merchant != null) {
            int iconHeight = Math.round(16 * dm.density);
            int iconMargin = Math.round(5 * dm.density);
            findViewById(R.id.merchant_layout).setOnClickListener(this);
            View bondLayout = findViewById(R.id.bond_layout);
            bondLayout.setOnClickListener(this);
            ImageView bondIconView = (ImageView) findViewById(R.id.bond_icon);
            TextView merchantName = (TextView) findViewById(R.id.merchant_name);
            TextView merchantAddress = (TextView) findViewById(R.id.merchant_address);
            ImageView merchantLogo = (ImageView) findViewById(R.id.merchant_logo);
            if (merchant.getBondSign() != null) {
                int width = Math.round(merchant.getBondSign()
                        .getWidth() * iconHeight / merchant.getBondSign()
                        .getHeight());
                String iconUrl = JSONUtil.getImagePathForWxH(merchant.getBondSign()
                        .getIconUrl(), width, iconHeight);
                bondIconView.setVisibility(View.VISIBLE);
                bondLayout.setVisibility(View.VISIBLE);
                bondIconView.getLayoutParams().width = width;
                ImageLoadTask task = new ImageLoadTask(bondIconView, null, 0);
                bondIconView.setTag(iconUrl);
                task.loadImage(iconUrl,
                        width,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
                ((FrameLayout.LayoutParams) merchantName.getLayoutParams()).rightMargin = width +
                        iconMargin;
            } else {
                bondIconView.setVisibility(View.GONE);
                bondLayout.setVisibility(View.GONE);
                ((FrameLayout.LayoutParams) merchantName.getLayoutParams()).rightMargin = 0;
            }
            merchantName.setText(merchant.getName());
            merchantAddress.setText(merchant.getAddress());
            int logoWidth = Math.round(40 * dm.density);
            String logoPath = JSONUtil.getImagePath2(merchant.getLogoPath(), logoWidth);
            if (!JSONUtil.isEmpty(logoPath)) {
                ImageLoadTask loadTask = new ImageLoadTask(merchantLogo, null, 0);
                merchantLogo.setTag(logoPath);
                loadTask.loadImage(logoPath,
                        logoWidth,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(),
                                R.mipmap.icon_empty_image,
                                loadTask));
            }
        }
    }

    /**
     * 定制套餐信息
     */
    private void initWorkInfo(final CustomSetmeal setmeal) {
        if (setmeal != null) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            if (!JSONUtil.isEmpty(setmeal.getTitle())) {
                findViewById(R.id.payment_method_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.property_layout).setVisibility(View.VISIBLE);
                TextView titleView = (TextView) findViewById(R.id.title);
                titleView.setText(setmeal.getTitle());
                TextView property = (TextView) findViewById(R.id.property);
                property.setVisibility(View.VISIBLE);
                property.setText(getString(R.string.propertie1));
            }
            if (!JSONUtil.isEmpty(Util.roundUpDouble2String(setmeal.getActualPrice()))) {
                findViewById(R.id.payment_method_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.price_layout).setVisibility(View.VISIBLE);
                TextView price = (TextView) findViewById(R.id.price);
                price.setText(Util.roundUpDouble2String(setmeal.getActualPrice()));
            }

            if (!JSONUtil.isEmpty(setmeal.getOrderGift())) {
                findViewById(R.id.payment_method_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.earnest_main_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.gift_layout).setVisibility(View.VISIBLE);
                TextView gift = (TextView) findViewById(R.id.gift);
                gift.setText(String.format(getString(R.string.label_gift), setmeal.getOrderGift()));
            }

            Comment comment = setmeal.getComment();
            if (comment != null) {
                findViewById(R.id.comment_layout).setVisibility(View.VISIBLE);
                TextView commentCount = (TextView) findViewById(R.id.comment_count);
                if (comment.getCount() > 1) {
                    findViewById(R.id.more_comment_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.more_comment_layout).setOnClickListener(this);
                    findViewById(R.id.more_comment_layout).setOnClickListener(new View
                            .OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(CustomSetMealDetailActivity.this,
                                    CustomCommentListActivity.class);
                            intent.putExtra("id", new Long(setmeal.getId()));
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    });
                    commentCount.setText(getString(R.string.label_merchant_comment_count1,
                            String.valueOf(comment.getCount())));
                } else {
                    commentCount.setText(String.format(getResources().getString(R.string
                                    .label_merchant_comment_count1),
                            String.valueOf(comment.getCount())));
                }
                ImageView userIcon = (ImageView) findViewById(R.id.avatar);
                TextView userName = (TextView) findViewById(R.id.name);
                TextView content = (TextView) findViewById(R.id.content);
                TextView time = (TextView) findViewById(R.id.time);
                Author commentUser = comment.getUser();
                int size = Math.round(getResources().getDisplayMetrics().density * 30);
                String url = JSONUtil.getAvatar(commentUser == null ? "" : commentUser.getAvatar(),
                        size);
                if (!JSONUtil.isEmpty(url)) {
                    userIcon.setTag(url);
                    ImageLoadTask task = new ImageLoadTask(userIcon, 0);
                    task.loadImage(url,
                            userIcon.getLayoutParams().width,
                            ScaleMode.WIDTH,
                            new AsyncBitmapDrawable(getResources(),
                                    BitmapFactory.decodeResource(getResources(),
                                            R.mipmap.icon_avatar_primary),
                                    task));
                } else {
                    userIcon.setImageResource(R.mipmap.icon_avatar_primary);
                }
                userName.setText(commentUser == null ? "" : commentUser.getName());
                content.setText(comment.getContent());

                if (comment.getTime() != null) {
                    SimpleDateFormat timeFormat = new SimpleDateFormat(getString(R.string
                            .format_date_type4),
                            Locale.getDefault());
                    time.setText(timeFormat.format(comment.getTime()));
                }
            } else {
                findViewById(R.id.comment_layout).setVisibility(View.GONE);
            }

            if (setmeal.getFlowChart() != null) {
                final Point point = JSONUtil.getDeviceSize(this);
                String coverPath = JSONUtil.getImagePath(setmeal.getFlowChart()
                        .getImagePath(), point.x);
                if (!JSONUtil.isEmpty(coverPath)) {
                    final RecyclingImageView cover = (RecyclingImageView) findViewById(R.id.cover);
                    cover.setVisibility(View.VISIBLE);
                    if (setmeal.getFlowChart()
                            .getHeight() > 0 && setmeal.getFlowChart()
                            .getWidth() > 0) {
                        cover.getLayoutParams().height = Math.round(setmeal.getFlowChart()
                                .getHeight() * point.x / setmeal.getFlowChart()
                                .getWidth());
                    }
                    ImageLoadTask task = new ImageLoadTask(cover, new OnHttpRequestListener() {
                        @Override
                        public void onRequestCompleted(Object obj) {
                            Bitmap bitmap = (Bitmap) obj;
                            if (setmeal.getFlowChart()
                                    .getHeight() == 0 || setmeal.getFlowChart()
                                    .getWidth() == 0) {
                                cover.getLayoutParams().height = Math.round(bitmap.getHeight() *
                                        point.x / bitmap.getWidth());
                            }
                        }

                        @Override
                        public void onRequestFailed(Object obj) {

                        }
                    }, true);
                    cover.setTag(coverPath);
                    task.loadImage(coverPath,
                            setmeal.getFlowChart()
                                    .getWidth(),
                            ScaleMode.WIDTH,
                            new AsyncBitmapDrawable(getResources(),
                                    R.mipmap.icon_empty_image,
                                    task));
                }
            }
        }
    }

    private void initPromise(final CustomSetmeal setmeal) {
        if (setmeal != null) {
            if (!JSONUtil.isEmpty(setmeal.getPurchaseNotes())) {
                TextView purchaseNotes = (TextView) findViewById(R.id.purchase_notes);
                purchaseNotes.setText(setmeal.getPurchaseNotes());
                purchaseNotes.setVisibility(View.VISIBLE);
            }

            if (!JSONUtil.isEmpty(setmeal.getPurchaseNotes())) {
                findViewById(R.id.purchase_notes_layout).setVisibility(View.VISIBLE);
                TextView notes = (TextView) findViewById(R.id.purchase_notes);
                notes.setVisibility(View.VISIBLE);
                notes.setText(setmeal.getPurchaseNotes());
            }

            DataConfig config = Session.getInstance()
                    .getDataConfig(this);
            if (setmeal.getMerchant() != null && config != null && !JSONUtil.isEmpty(config
                    .getPrepayRemind(
                    setmeal.getMerchant()
                            .getPropertyId()))) {
                TextView prepayRemindView = (TextView) findViewById(R.id.prepay_remind);
                prepayRemindView.setText(config.getPrepayRemind(setmeal.getMerchant()
                        .getPropertyId()));
                prepayRemindView.setVisibility(View.VISIBLE);
                findViewById(R.id.prepay_line).setVisibility(View.VISIBLE);
            }

            View promiseLayout = findViewById(R.id.promise_layout);
            if (!JSONUtil.isEmpty(setmeal.getPromiseImage())) {
                Point point = JSONUtil.getDeviceSize(this);
                promiseLayout.setVisibility(View.VISIBLE);
                promiseLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (setmeal != null && !JSONUtil.isEmpty(setmeal.getPromiseImage())) {
                            String path = setmeal.getPromiseStaticPath();
                            Intent intent = new Intent(CustomSetMealDetailActivity.this,
                                    HljWebViewActivity.class);
                            intent.putExtra("path", path);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }
                });
                ImageView promiseImage = (ImageView) findViewById(R.id.promise_image);
                promiseImage.getLayoutParams().height = Math.round(point.x * 9 / 40);
                String path = JSONUtil.getImagePathForRound(setmeal.getPromiseImage(), point.x);
                ImageLoadTask task = new ImageLoadTask(promiseImage, null, 0);
                task.loadImage(path,
                        point.x,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
            } else {
                promiseLayout.setVisibility(View.GONE);
            }
        }
    }

    private void initRecommendWorks(View view, Work work) {
        if (view != null && work != null) {
            view.setVisibility(View.VISIBLE);
            view.setTag(work);
            ImageView oWorkImg = (ImageView) view.findViewById(R.id.img_work_cover);
            TextView oWorkTitle = (TextView) view.findViewById(R.id.tv_work_title);
            TextView oWorkPrice = (TextView) view.findViewById(R.id.tv_work_price);
            TextView oWorkCollectCount = (TextView) view.findViewById(R.id.tv_work_collect);
            TextView oWorkPrice1 = (TextView) view.findViewById(R.id.tv_work_price1);
            if (work.getMarketPrice() > 0) {
                oWorkPrice1.setVisibility(View.VISIBLE);
                oWorkPrice1.getPaint()
                        .setAntiAlias(true);
                oWorkPrice1.getPaint()
                        .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                oWorkPrice1.setText(Util.formatDouble2String(work.getMarketPrice()));
            } else {
                oWorkPrice1.setVisibility(View.GONE);
            }
            String url = JSONUtil.getImagePath(work.getCoverPath(), workImgWidth);
            if (!JSONUtil.isEmpty(url)) {
                ImageLoadTask loadTask = new ImageLoadTask(oWorkImg);
                oWorkImg.setTag(url);
                loadTask.loadImage(url,
                        workImgWidth,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(),
                                R.mipmap.icon_empty_image,
                                loadTask));
            }
            oWorkTitle.setText(work.getTitle());
            if (work.getCommodityType() == 0) {
                oWorkPrice.setVisibility(View.VISIBLE);
                oWorkPrice.setText(getString(R.string.label_price,
                        Util.formatDouble2String(work.getShowPrice())));
            } else {
                oWorkPrice.setVisibility(View.INVISIBLE);
            }
            oWorkCollectCount.setText(getString(R.string.label_collect_count,
                    String.valueOf(work.getCollectorsCount())));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Work work = (Work) v.getTag();
                    if (work != null) {
                        Intent intent = new Intent(CustomSetMealDetailActivity.this,
                                WorkActivity.class);
                        intent.putExtra("w_id", work.getId());
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            });
        } else {
            view.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        switch (id) {
            case R.id.merchant_layout:
                MerchantUser merchantUser = Session.getInstance()
                        .getCurrentUser(this);
                if (merchantUser.getShopType() == MerchantUser.SHOP_TYPE_SERVICE) {
                    //0 服务商家，1 婚品商家 2.婚车
                    intent = new Intent(this, ShopWebViewActivity.class);
                    intent.putExtra("title", getString(R.string.label_preview_merchant));
                    intent.putExtra("type", 3);
                    intent.putExtra("path",
                            String.format(Constants.WEB_HOST + Constants.HttpPath.GET_SHOP_PREVIEW,
                                    merchantUser.getMerchantId(),
                                    1));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
                break;
            case R.id.bond_layout:
                intent = new Intent(this, HljWebViewActivity.class);
                intent.putExtra("path",
                        customSetmeal.getMerchant()
                                .getBondSignUrl());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                break;
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

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (scrollView.getScaleY() < headPhotoHeight) {
            int y = scrollView.getScrollY();
            float alpha;
            if (y > headPhotoHeight) {
                alpha = 1;
            } else if (y == 0) {
                alpha = 0;
            } else {
                alpha = (float) y / headPhotoHeight;
            }
            actionLayout.setAlpha(alpha);
            shadowView.setAlpha(1 - alpha);
        }
    }


    class GetMerchatTask extends AsyncTask<Long, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Long... params) {
            try {
                long id = params[0];
                String json = JSONUtil.getStringFromUrl(CustomSetMealDetailActivity.this,
                        Constants.getAbsUrl(String.format(Constants.HttpPath.SETMEAL_DETAIL, id)));
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (isFinishing()) {
                return;
            }
            if (result != null) {
                progressBar.setVisibility(View.GONE);
                JSONObject data = result.optJSONObject("data");
                if (data != null) {
                    customSetmeal = new CustomSetmeal(data);
                    if (customSetmeal.getShareInfo() == null) {
                        findViewById(R.id.btn_share).setVisibility(View.GONE);
                        findViewById(R.id.btn_share2).setVisibility(View.GONE);
                    }
                }
                initMealInfo();
            } else {
                showEmptyInfo();
            }
        }
    }

    class GetWorksTask extends AsyncTask<Long, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Long... params) {
            try {
                long id = params[0];
                String json = JSONUtil.getStringFromUrl(CustomSetMealDetailActivity.this,
                        Constants.getAbsUrl(String.format(Constants.HttpPath.SETMEAL_DETAIL_WORKS,
                                id)));
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (isFinishing()) {
                return;
            }
            if (result != null) {
                JSONObject data = result.optJSONObject("data");
                if (data != null) {
                    progressBar.setVisibility(View.GONE);
                    JSONArray works = data.optJSONArray("works");
                    if (works != null) {
                        findViewById(R.id.recommend_works_layout).setVisibility(View.VISIBLE);
                        LinearLayout workList = (LinearLayout) findViewById(R.id.rec_works);

                        for (int i = 0; i < works.length(); i++) {
                            View view = getLayoutInflater().inflate(R.layout.other_work_item_view1,
                                    null);
                            Work work = new Work(works.optJSONObject(i));
                            initRecommendWorks(view, work);
                            if (i == 0) {
                                DisplayMetrics metrics = getResources().getDisplayMetrics();
                                view.setPadding(view.getPaddingLeft(),
                                        0,
                                        view.getPaddingRight(),
                                        view.getPaddingBottom());
                                RelativeLayout.LayoutParams params = new RelativeLayout
                                        .LayoutParams(
                                        RelativeLayout.LayoutParams.MATCH_PARENT,
                                        Math.round(metrics.density * 84));
                                view.setLayoutParams(params);
                            }
                            if (i == works.length() - 1) {
                                view.findViewById(R.id.line_layout)
                                        .setVisibility(View.GONE);
                            }
                            workList.addView(view, i);
                        }
                    }
                }
            }
        }
    }
}
