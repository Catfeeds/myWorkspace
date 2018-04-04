package com.hunliji.marrybiz.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ObjectBindAdapter;
import com.hunliji.marrybiz.model.Item;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.ReturnStatus;
import com.hunliji.marrybiz.model.Work;
import com.hunliji.marrybiz.model.WorkDescribe;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.view.shop.ShopWebViewActivity;
import com.makeramen.rounded.RoundedImageView;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Suncloud on 2015/12/24.
 */
public class CaseDetailActivity extends HljBaseNoBarActivity implements
        ObservableScrollViewCallbacks, ObjectBindAdapter.ViewBinder<Item>, AdapterView
        .OnItemClickListener {

    @BindView(R.id.list)
    ObservableListView listScrollView;
    @BindView(R.id.action_bar)
    RelativeLayout actionBar;
    @BindView(R.id.img_merchant_logo)
    RoundedImageView imgMerchantLogo;
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.sticky)
    RelativeLayout sticky;
    @BindView(R.id.header)
    LinearLayout header;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private ArrayList<Item> items;
    private View heardView;
    private JSONObject workObject;
    private ObjectBindAdapter<Item> adapter;
    private TextView contentView;
    private int width;
    private int imageWidth;
    private int mBaseTranslationY;
    private int merchantLogoSize;
    private Work work;

    private final static int MSG_INIT_VIEW = 0xA00;
    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT_VIEW:
                    new GetWorkTask().onPostExecute(workObject);
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        items = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, items, R.layout.opu_item, this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_detail);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();

        Point point = JSONUtil.getDeviceSize(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        imageWidth = width = Math.round(point.x - 16 * dm.density);
        if (width > 805) {
            imageWidth = Math.round(width * 3 / 4);
        }
        merchantLogoSize = Math.round(dm.density * 30);
        ViewCompat.setElevation(header, 1);
        listScrollView.setScrollViewCallbacks(this);
        listScrollView.addHeaderView(getLayoutInflater().inflate(R.layout.header_padding,
                listScrollView,
                false)); // toolbar
        listScrollView.addHeaderView(getLayoutInflater().inflate(R.layout.header_padding,
                listScrollView,
                false)); // sticky view

        heardView = View.inflate(CaseDetailActivity.this, R.layout.case_item_list_heard, null);
        View footerView = View.inflate(CaseDetailActivity.this,
                R.layout.case_item_list_footer,
                null);
        listScrollView.addHeaderView(heardView);
        listScrollView.addFooterView(footerView);
        listScrollView.setAdapter(adapter);
        listScrollView.setOnItemClickListener(this);
        contentView = (TextView) heardView.findViewById(R.id.case_content);

        String workJson = getIntent().getStringExtra("workJson");
        if (!JSONUtil.isEmpty(workJson)) {
            try {
                workObject = new JSONObject(workJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        progressBar.setVisibility(View.VISIBLE);
        if (workObject == null) {
            long id = getIntent().getLongExtra("w_id", 0);
            new GetWorkTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(Constants.getAbsUrl(Constants.HttpPath.NEW_WORK_INFO), id));
        } else {
            new Thread() {

                @Override
                public void run() {
                    super.run();
                    handler.sendEmptyMessageDelayed(MSG_INIT_VIEW, 400);
                }

            }.start();
        }
    }

    public void onMerchantClick(View view) {
        MerchantUser merchantUser = Session.getInstance()
                .getCurrentUser(this);
        if (merchantUser.getShopType() == MerchantUser.SHOP_TYPE_SERVICE) {
            //0 服务商家，1 婚品商家 2.婚车
            Intent intent = new Intent(this, ShopWebViewActivity.class);
            intent.putExtra("title", getString(R.string.label_preview_merchant));
            intent.putExtra("type", 3);
            intent.putExtra("path",
                    String.format(Constants.WEB_HOST + Constants.HttpPath.GET_SHOP_PREVIEW,
                            merchantUser.getMerchantId(),
                            1));
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    public void onBackPressed(View view) {
        super.onBackPressed();
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (dragging) {
            int toolbarHeight = actionBar.getHeight();
            if (firstScroll) {
                float currentHeaderTranslationY = ViewHelper.getTranslationY(header);
                if (-toolbarHeight < currentHeaderTranslationY) {
                    mBaseTranslationY = scrollY;
                }
            }
            float headerTranslationY = ScrollUtils.getFloat(-(scrollY - mBaseTranslationY),
                    -toolbarHeight,
                    0);
            ViewPropertyAnimator.animate(header)
                    .cancel();
            ViewHelper.setTranslationY(header, headerTranslationY);
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        mBaseTranslationY = 0;

        if (scrollState == ScrollState.DOWN) {
            showToolbar();
        } else if (scrollState == ScrollState.UP) {
            int toolbarHeight = actionBar.getHeight();
            int scrollY = listScrollView.getCurrentScrollY();
            if (toolbarHeight <= scrollY) {
                hideToolbar();
            } else {
                showToolbar();
            }
        } else {
            if (!toolbarIsShown() && !toolbarIsHidden()) {
                showToolbar();
            }
        }
    }

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(header) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(header) == -actionBar.getHeight();
    }

    private void showToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(header);
        if (headerTranslationY != 0) {
            ViewPropertyAnimator.animate(header)
                    .cancel();
            ViewPropertyAnimator.animate(header)
                    .translationY(0)
                    .setDuration(200)
                    .start();
        }
    }

    private void hideToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(header);
        int toolbarHeight = actionBar.getHeight();
        if (headerTranslationY != -toolbarHeight) {
            ViewPropertyAnimator.animate(header)
                    .cancel();
            ViewPropertyAnimator.animate(header)
                    .translationY(-toolbarHeight)
                    .setDuration(200)
                    .start();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = (Item) parent.getAdapter()
                .getItem(position);
        if (item != null) {
            Intent intent = new Intent(this, ItemPageViewActivity.class);
            intent.putExtra("items", items);
            intent.putExtra("position", items.indexOf(item));
            startActivity(intent);
        }
    }

    @Override
    public void setViewValue(View view, final Item item, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder();
            holder.describe = (TextView) view.findViewById(R.id.item_describe);
            holder.imageView = (ImageView) view.findViewById(R.id.item_image);
            holder.play = view.findViewById(R.id.play);
            view.setTag(holder);
        }
        final ViewHolder holder = (ViewHolder) view.getTag();
        if (!JSONUtil.isEmpty(item.getDescription())) {
            holder.describe.setVisibility(View.VISIBLE);
            holder.describe.setText(item.getDescription());
        } else {
            holder.describe.setVisibility(View.GONE);
        }

        if (item.getHight() != 0 && item.getWidth() != 0) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.imageView
                    .getLayoutParams();
            params.height = Math.round(width * item.getHight() / item.getWidth());
        }
        String url;
        if (item.getKind() == 2) {
            if (item.getPersistent() != null && !JSONUtil.isEmpty(item.getPersistent()
                    .getScreenShot())) {
                url = JSONUtil.getImagePath(item.getPersistent()
                        .getScreenShot(), imageWidth);
            } else {
                url = item.getMediaPath() + String.format(Constants.VIDEO_URL_TEN, imageWidth)
                        .replace("|", JSONUtil.getURLEncoder());
            }
            holder.play.setVisibility(View.VISIBLE);
        } else {
            holder.play.setVisibility(View.GONE);
            url = JSONUtil.getImagePath(item.getMediaPath(), imageWidth);
        }
        if (!JSONUtil.isEmpty(url)) {
            ImageLoadTask task = new ImageLoadTask(holder.imageView, new OnHttpRequestListener() {

                @Override
                public void onRequestCompleted(Object obj) {
                    if (item.getHight() == 0 || item.getWidth() == 0) {
                        Bitmap bm = (Bitmap) obj;
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)
                                holder.imageView.getLayoutParams();
                        params.height = Math.round(width * bm.getHeight() / bm.getWidth());
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {

                }
            });
            holder.imageView.setTag(url);
            task.loadImage(url,
                    imageWidth,
                    ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
        } else {
            holder.imageView.setImageBitmap(null);
        }
    }

    private class GetWorkTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            try {
                String json = JSONUtil.getStringFromUrl(CaseDetailActivity.this, strings[0]);
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
        protected void onPostExecute(JSONObject jsonObject) {
            if (isFinishing()) {
                return;
            }
            progressBar.setVisibility(View.GONE);
            listScrollView.setVisibility(View.VISIBLE);
            String emptyStr = null;
            if (jsonObject != null) {
                ReturnStatus status = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (status.getRetCode() == 404 && !JSONUtil.isEmpty(status.getErrorMsg())) {
                    emptyStr = status.getErrorMsg();
                }

                if (jsonObject.optJSONObject("data") != null) {
                    initWorkJson(jsonObject.optJSONObject("data")
                            .optJSONObject("work"));
                }
            }

            if (work == null || work.getId() == 0) {
                View empty = listScrollView.getEmptyView();
                if (empty == null) {
                    empty = findViewById(R.id.empty_hint_layout);
                    listScrollView.setEmptyView(empty);
                }
                TextView textEmptyHint = (TextView) empty.findViewById(R.id.text_empty_hint);
                textEmptyHint.setVisibility(View.VISIBLE);
                if (JSONUtil.isNetworkConnected(CaseDetailActivity.this)) {
                    ImageView imgEmptyHint = (ImageView) findViewById(R.id.img_empty_hint);
                    imgEmptyHint.setVisibility(View.VISIBLE);
                    if (!JSONUtil.isEmpty(emptyStr)) {
                        textEmptyHint.setText(emptyStr);
                    } else {
                        textEmptyHint.setText(getString(R.string.no_item));
                    }
                } else {
                    ImageView imgEmptyHint = (ImageView) findViewById(R.id.img_net_hint);
                    imgEmptyHint.setVisibility(View.VISIBLE);
                    textEmptyHint.setText(getString(R.string.net_disconnected));
                }
            }

            super.onPostExecute(jsonObject);
        }
    }

    private void initWorkJson(JSONObject jsonObject) {
        if (jsonObject != null) {
            work = new Work(jsonObject);
            if (jsonObject.optJSONArray("media_items") != null && jsonObject.optJSONArray(
                    "media_items")
                    .length() > 0) {
                JSONArray array = jsonObject.optJSONArray("media_items");
                for (int i = 0, size = array.length(); i < size; i++) {
                    Item item = new Item(array.optJSONObject(i));
                    if (!JSONUtil.isEmpty(item.getMediaPath())) {
                        item.setType(Constants.ItemType.OpuItem);
                        items.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            sticky.setVisibility(View.VISIBLE);
            heardView.setVisibility(View.VISIBLE);
            LinearLayout heardDescribeLayout = (LinearLayout) heardView.findViewById(R.id
                    .case_describe);
            int emptyCount = 0;
            if (work.getWorkDescribes() != null && !work.getWorkDescribes()
                    .isEmpty()) {
                heardDescribeLayout.setVisibility(View.VISIBLE);
                for (WorkDescribe workDescribe : work.getWorkDescribes()) {
                    if (!workDescribe.getName()
                            .isEmpty() && !workDescribe.getDescribe()
                            .isEmpty()) {
                        View itemView = getLayoutInflater().inflate(R.layout.description_item,
                                null);
                        TextView key = (TextView) itemView.findViewById(R.id.key);
                        TextView value = (TextView) itemView.findViewById(R.id.value);
                        key.setText(workDescribe.getName() + "：");
                        value.setText(workDescribe.getDescribe());
                        heardDescribeLayout.addView(itemView);
                    } else {
                        ++emptyCount;
                    }

                }
            }
            TextView title = (TextView) heardView.findViewById(R.id.tv_title);
            title.setText(work.getTitle());

            if (work.getWorkDescribes() == null || emptyCount == work.getWorkDescribes()
                    .size()) {
                heardDescribeLayout.setVisibility(View.GONE);
            }

            if (!JSONUtil.isEmpty(work.getDescribe())) {
                contentView.setText(work.getDescribe()
                        .replaceAll("(\\s|\t|\r|\n)" + "*$|^" + "(\\s|\t|\r|\n)*", ""));
            }

            if (work.getMerchant() != null) {
                String logoPath = JSONUtil.getImagePath2(work.getMerchant()
                        .getLogoPath(), merchantLogoSize);
                if (!JSONUtil.isEmpty(logoPath)) {
                    ImageLoadTask loadTask = new ImageLoadTask(imgMerchantLogo, null, 0);
                    imgMerchantLogo.setTag(logoPath);
                    loadTask.loadImage(logoPath,
                            merchantLogoSize,
                            ScaleMode.WIDTH,
                            new AsyncBitmapDrawable(getResources(),
                                    R.mipmap.icon_empty_image,
                                    loadTask));
                }
                tvMerchantName.setText(work.getMerchant()
                        .getName());
            }
        }
    }

    private class ViewHolder {
        TextView describe;
        ImageView imageView;
        View play;
    }
}
