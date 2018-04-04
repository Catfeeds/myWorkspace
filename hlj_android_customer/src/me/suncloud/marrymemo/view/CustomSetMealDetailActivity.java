package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljtrackerlibrary.HljTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ContactsAdapter;
import me.suncloud.marrymemo.model.Author;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.Comment;
import me.suncloud.marrymemo.model.CustomSetmeal;
import me.suncloud.marrymemo.model.NewMerchant;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.ShareUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.view.comment.ServiceCommentListActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.widget.MyScrollView;
import me.suncloud.marrymemo.widget.RecyclingImageView;

/**
 * Created by jinxin on 2016/1/25.
 */
public class CustomSetMealDetailActivity extends HljBaseNoBarActivity implements View
        .OnClickListener, MyScrollView.OnScrollChangedListener {
    private int workImgWidth;
    private int workImgHeight;
    private CustomSetmeal customSetmeal;
    private DisplayMetrics dm;
    private Point point;

    private View progressBar;
    private View bottomLayout;
    private View heardViewLayout;
    private View emptyHintLayout;
    private View imgEmptyHint;
    private TextView textEmptyHint;
    private long id;
    private boolean orderCheckLoad;
    private int headPhotoHeight;
    private View actionLayout;
    private View actionLayout2;
    private View shadowView;
    private MyScrollView scrollView;
    private Dialog shareDialog;
    private ShareUtil shareUtil;
    private Dialog contactDialog;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HljShare.RequestCode.SHARE_TO_QQZONE:
                    new HljTracker.Builder(CustomSetMealDetailActivity.this).eventableId(id)
                            .eventableType("CustomMeal")
                            .action("share")
                            .additional("QQZone")
                            .build()
                            .add();
                    break;
                case HljShare.RequestCode.SHARE_TO_QQ:
                    new HljTracker.Builder(CustomSetMealDetailActivity.this).eventableId(id)
                            .eventableType("CustomMeal")
                            .action("share")
                            .additional("QQ")
                            .build()
                            .add();
                    break;
                case HljShare.RequestCode.SHARE_TO_PENGYOU:
                    new HljTracker.Builder(CustomSetMealDetailActivity.this).eventableId(id)
                            .eventableType("CustomMeal")
                            .action("share")
                            .additional("Timeline")
                            .build()
                            .add();
                    break;
                case HljShare.RequestCode.SHARE_TO_WEIXIN:
                    new HljTracker.Builder(CustomSetMealDetailActivity.this).eventableId(id)
                            .eventableType("CustomMeal")
                            .action("share")
                            .additional("Session")
                            .build()
                            .add();
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getLongExtra("id", 0);
        dm = getResources().getDisplayMetrics();
        point = JSONUtil.getDeviceSize(this);
        workImgWidth = Math.round(dm.density * 116);
        workImgWidth = Math.round((point.x - 36 * dm.density) / 2);
        workImgHeight = Math.round(workImgWidth * 5 / 8);
        headPhotoHeight = Math.round(dm.density * 200);
        setContentView(R.layout.activity_custom_setmeal_detail);
        setDefaultStatusBarPadding();
        progressBar = findViewById(R.id.progressBar);
        bottomLayout = findViewById(R.id.bottom_layout);
        heardViewLayout = findViewById(R.id.heard_view_layout);
        emptyHintLayout = findViewById(R.id.empty_hint_layout);
        imgEmptyHint = findViewById(R.id.img_empty_hint);
        textEmptyHint = (TextView) findViewById(R.id.text_empty_hint);
        actionLayout = findViewById(R.id.action_layout);
        actionLayout2 = findViewById(R.id.action_layout_2);
        shadowView = findViewById(R.id.shadow_view);

        scrollView = (MyScrollView) findViewById(R.id.scroll_view);
        scrollView.setOnScrollChangedListener(this);

        setActionBarPadding(actionLayout, actionLayout2);

        progressBar.setVisibility(View.VISIBLE);
        new HljTracker.Builder(this).eventableId(id)
                .eventableType("CustomMeal")
                .action("hit")
                .build()
                .add();
        new GetCustomerTask().executeOnExecutor(Constants.INFOTHEADPOOL, id);
        new GetWorksTask().executeOnExecutor(Constants.INFOTHEADPOOL, id);
    }

    public void onBackPressed(View view) {
        finish();
    }

    public void onReservation(View view) {
        if (customSetmeal == null) {
            return;
        }
        if (Util.loginBindChecked(this, Constants.Login.SUBMIT_LOGIN)) {
            new HljTracker.Builder(this).eventableId(id)
                    .eventableType("CustomMeal")
                    .action("hit_buy")
                    .build()
                    .add();
            Intent intent = new Intent(this, CustomSetmealOrderConfirmActivity.class);
            intent.putExtra("customSetmeal", customSetmeal);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    public void onShare(View view) {
        if (customSetmeal == null || customSetmeal.getShareInfo() == null) {
            return;
        }
        if (shareDialog != null && shareDialog.isShowing()) {
            return;
        }
        if (shareUtil == null) {
            shareUtil = new ShareUtil(this, customSetmeal.getShareInfo(), progressBar, handler);
        }
        if (shareDialog == null) {
            shareDialog = Util.initShareDialog(this, shareUtil,null);
        }
        shareDialog.show();
    }

    private void showEmptyInfo() {
        progressBar.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);
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
        String headPhotoUrl = JSONUtil.getImagePathForH(customSetmeal.getHeaderPhoto(),
                Math.round(dm.density * 240));
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
            DisplayMetrics dm = getResources().getDisplayMetrics();
            findViewById(R.id.merchant_layout).setOnClickListener(this);
            View bondLayout = findViewById(R.id.bond_layout);
            bondLayout.setOnClickListener(this);
            ImageView bondIconView = (ImageView) findViewById(R.id.bond_icon);
            TextView merchantName = (TextView) findViewById(R.id.merchant_name);
            TextView merchantAddress = (TextView) findViewById(R.id.merchant_address);
            ImageView merchantLogo = (ImageView) findViewById(R.id.merchant_logo);
            int padding = 0;
            if (merchant.getBondSign() != null) {
                bondIconView.setVisibility(View.VISIBLE);
                bondLayout.setVisibility(View.VISIBLE);
                padding += Math.round(20 * dm.density);
            } else {
                bondIconView.setVisibility(View.GONE);
                bondLayout.setVisibility(View.GONE);
            }
            ImageView levelView = (ImageView) findViewById(R.id.level_icon);
            int res = 0;
            switch (merchant.getGrade()) {
                case 2:
                    res = R.drawable.icon_merchant_level2_149_36;
                    break;
                case 3:
                    res = R.drawable.icon_merchant_level3_149_36;
                    break;
                case 4:
                    res = R.drawable.icon_merchant_level4_149_36;
                    break;
                default:
                    break;
            }
            if (res != 0) {
                levelView.setVisibility(View.VISIBLE);
                levelView.setImageResource(res);
                padding += Math.round(70 * dm.density);
            } else {
                levelView.setVisibility(View.GONE);
            }
            merchantName.setText(merchant.getName());
            merchantName.setPadding(0, 0, padding, 0);
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

                if (setmeal.getMerchant() != null && !JSONUtil.isEmpty(setmeal.getMerchant()
                        .getPropertyName())) {
                    property.setText(setmeal.getMerchant()
                            .getPropertyName());
                } else {
                    property.setText(getString(R.string.propertie1));
                }
            }
            if (!JSONUtil.isEmpty(Util.roundDownDouble2StringPositive(setmeal.getActualPrice()))) {
                findViewById(R.id.payment_method_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.price_layout).setVisibility(View.VISIBLE);
                TextView price = (TextView) findViewById(R.id.price);
                price.setText(Util.roundDownDouble2StringPositive(setmeal.getActualPrice()));
            }

            if (!JSONUtil.isEmpty(setmeal.getOrderGift())) {
                findViewById(R.id.payment_method_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.earnest_main_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.gift_layout).setVisibility(View.VISIBLE);
                TextView gift = (TextView) findViewById(R.id.gift);
                gift.setText(String.format(getString(R.string.label_gift), setmeal.getOrderGift()));
            }

            final Comment comment = setmeal.getComment();
            if (comment != null) {
                findViewById(R.id.comment_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.more_comment_layout).setVisibility(View.VISIBLE);
                TextView commentCount = (TextView) findViewById(R.id.comment_count);
                findViewById(R.id.more_comment_layout).setOnClickListener(new View
                        .OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NewMerchant merchant = setmeal.getMerchant();
                        if (merchant != null && merchant.getId() > 0) {
                            Intent intent = new Intent(CustomSetMealDetailActivity.this,
                                    ServiceCommentListActivity.class);
                            intent.putExtra("id", merchant.getId());
                            intent.putExtra("is_show_rating", false);
                            intent.putExtra("comment_statistics", merchant.getCommentStatistics());
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }
                });
                if (comment.getCount() > 1) {
                    //普通套餐评论
                    commentCount.setText(getString(R.string.label_merchant_comment_count1,
                            comment.getCount()));
                } else {
                    //商家评论
                    commentCount.setText(String.format(getResources().getString(R.string
                            .label_merchant_comment_count)));
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
                        .getPath(), point.x);
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
                        if (!JSONUtil.isEmpty(setmeal.getPromiseImage())) {
                            String path = setmeal.getPromiseStaticPath();
                            City city = Session.getInstance()
                                    .getMyCity(CustomSetMealDetailActivity.this);
                            if (city != null && city.getId()
                                    .intValue() != 0) {
                                path += (path.contains("?") ? "&" : "?") + "city=" + city.getId();
                            }
                            Intent intent = new Intent(CustomSetMealDetailActivity.this,
                                    HljWebViewActivity.class);
                            intent.putExtra("city", city);
                            intent.putExtra("path", path);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }
                });
                ImageView promiseImage = (ImageView) findViewById(R.id.promise_image);
                promiseImage.getLayoutParams().height = Math.round(point.x * 9 / 40);
                String path = JSONUtil.getImagePathWithoutFormat(setmeal.getPromiseImage(),
                        point.x);
                ImageLoadTask task = new ImageLoadTask(promiseImage, null, null, 0, true, true);
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
        if (work != null) {
            view.setVisibility(View.VISIBLE);
            view.setTag(work);
            ImageView iconInstallment = (ImageView) view.findViewById(R.id.img_installment);
            ImageView oWorkImg = (ImageView) view.findViewById(R.id.img_cover);
            TextView oWorkTitle = (TextView) view.findViewById(R.id.tv_work_title);
            TextView oWorkPrice = (TextView) view.findViewById(R.id.tv_work_price);
            TextView oWorkCollectCount = (TextView) view.findViewById(R.id.tv_work_collect);
            TextView oWorkPrice1 = (TextView) view.findViewById(R.id.tv_work_price1);
            TextView tvHotTag = (TextView) view.findViewById(R.id.tv_hot_tag);

            iconInstallment.setVisibility(View.GONE);
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
            if (work.getHotTag() == 1 || work.getHotTag() == 2) {
                oWorkTitle.setLineSpacing(0, 1f);
                oWorkTitle.setMaxLines(1);
                tvHotTag.setVisibility(View.VISIBLE);
                tvHotTag.setText(work.getHotTag() == 1 ? R.string.label_work_rec : R.string
                        .label_work_top);
            } else {
                tvHotTag.setVisibility(View.GONE);
                oWorkTitle.setLineSpacing(0, 1.3f);
                oWorkTitle.setMaxLines(2);
            }
            oWorkTitle.setText(work.getTitle());
            if (work.getCommodityType() == 0) {
                oWorkPrice.setVisibility(View.VISIBLE);
                oWorkPrice.setText(Util.formatDouble2String(work.getShowPrice()));
            } else {
                oWorkPrice.setVisibility(View.INVISIBLE);
            }
            oWorkCollectCount.setText(getString(R.string.label_collect_count,
                    work.getCollectorCount()));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Work work = (Work) v.getTag();
                    if (work != null) {
                        Intent intent = new Intent(CustomSetMealDetailActivity.this,
                                WorkActivity.class);
                        intent.putExtra("id", work.getId());
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
                intent = new Intent(this, MerchantDetailActivity.class);
                intent.putExtra("id", customSetmeal.getMerchantId());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                break;
            case R.id.bond_layout:
                intent = new Intent(this, HljWebViewActivity.class);
                intent.putExtra("path",
                        customSetmeal.getMerchant()
                                .getBondSignUrl());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
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

    public void onSendMsg(View view) {
        if (customSetmeal == null || customSetmeal.getId() == 0) {
            return;
        }
        if (Util.loginBindChecked(this, Constants.Login.CONTACT_LOGIN)) {
            if (customSetmeal.getMerchant() != null && customSetmeal.getMerchant()
                    .getUserId() > 0) {
                NewMerchant merchant = customSetmeal.getMerchant();
                Intent intent = new Intent(this, WSCustomerChatActivity.class);
                MerchantUser user = new MerchantUser();
                user.setNick(merchant.getName());
                user.setId(merchant.getUserId());
                user.setAvatar(merchant.getLogoPath());
                user.setMerchantId(merchant.getId());
                intent.putExtra("user", user);
                if (merchant.getContactPhone() != null && !merchant.getContactPhone()
                        .isEmpty()) {
                    intent.putStringArrayListExtra("contact_phones", merchant.getContactPhone());
                }
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        }
    }

    public void onCallUp(View view) {
        if (customSetmeal == null || customSetmeal.getMerchant() == null) {
            return;
        }
        ArrayList<String> contactPhones = customSetmeal.getMerchant()
                .getContactPhone();
        if (contactPhones == null || contactPhones.isEmpty()) {
            Toast.makeText(this, R.string.msg_no_merchant_number, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (contactPhones.size() == 1) {
            String phone = contactPhones.get(0);
            if (!JSONUtil.isEmpty(phone) && phone.trim()
                    .length() != 0) {
                try {
                    callUp(Uri.parse("tel:" + phone.trim()));
                    new HljTracker.Builder(this).eventableId(customSetmeal.getMerchant()
                            .getId())
                            .eventableType("Merchant")
                            .screen("custom_meal_detail")
                            .action("call")
                            .additional(String.valueOf(customSetmeal.getId()))
                            .build()
                            .add();
                } catch (Exception ignored) {
                }
            }
            return;
        }

        if (contactDialog != null && contactDialog.isShowing()) {
            return;
        }
        if (contactDialog == null) {
            contactDialog = new Dialog(this, R.style.BubbleDialogTheme);
            contactDialog.setContentView(R.layout.dialog_contact_phones);
            Point point = JSONUtil.getDeviceSize(this);
            ListView listView = (ListView) contactDialog.findViewById(R.id.contact_list);
            ContactsAdapter contactsAdapter = new ContactsAdapter(this, contactPhones);
            listView.setAdapter(contactsAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String phone = (String) adapterView.getAdapter()
                            .getItem(i);
                    if (!JSONUtil.isEmpty(phone) && phone.trim()
                            .length() != 0) {
                        try {
                            callUp(Uri.parse("tel:" + phone.trim()));
                            new HljTracker.Builder(CustomSetMealDetailActivity.this).eventableId(
                                    customSetmeal.getMerchant()
                                            .getId())
                                    .eventableType("Merchant")
                                    .screen("custom_meal_detail")
                                    .action("call")
                                    .additional(String.valueOf(customSetmeal.getId()))
                                    .build()
                                    .add();
                        } catch (Exception ignored) {

                        }
                    }
                }
            });
            Window win = contactDialog.getWindow();
            ViewGroup.LayoutParams params = win.getAttributes();
            params.width = Math.round(point.x * 3 / 4);
            win.setGravity(Gravity.CENTER);
        }
        contactDialog.show();
    }

    class GetCustomerTask extends AsyncTask<Long, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Long... params) {
            try {
                long id = params[0];
                String json = JSONUtil.getStringFromUrl(Constants.getAbsUrl(String.format
                        (Constants.HttpPath.SETMEAL_DETAIL,
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
                String json = JSONUtil.getStringFromUrl(Constants.getAbsUrl(String.format
                        (Constants.HttpPath.SETMEAL_DETAIL_WORKS,
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
                            View view = getLayoutInflater().inflate(R.layout.common_work_item,
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case HljShare.RequestCode.SHARE_TO_TXWEIBO:
                    new HljTracker.Builder(this).eventableId(id)
                            .eventableType("CustomMeal")
                            .action("share")
                            .additional("TXWeibo")
                            .build()
                            .add();
                    break;
                case HljShare.RequestCode.SHARE_TO_WEIBO:
                    new HljTracker.Builder(this).eventableId(id)
                            .eventableType("CustomMeal")
                            .action("share")
                            .additional("Weibo")
                            .build()
                            .add();
                    break;
                case Constants.Login.CONTACT_LOGIN:
                    onSendMsg(null);
                    break;
                case Constants.Login.SUBMIT_LOGIN:
                    onReservation(null);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
