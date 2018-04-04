package com.hunliji.marrybiz.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hunlijicalendar.ResizeAnimation;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ArticlesAdapter;
import com.hunliji.marrybiz.adapter.WorkItemAdapter;
import com.hunliji.marrybiz.model.Article;
import com.hunliji.marrybiz.model.DataConfig;
import com.hunliji.marrybiz.model.Item;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.NewMerchant;
import com.hunliji.marrybiz.model.Place;
import com.hunliji.marrybiz.model.Rule;
import com.hunliji.marrybiz.model.Work;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.TimeUtil;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.ItemPageViewActivity;
import com.hunliji.marrybiz.view.WorkCommentListActivity;
import com.hunliji.marrybiz.view.shop.ShopWebViewActivity;
import com.hunliji.marrybiz.widget.FlowLayout;
import com.hunliji.marrybiz.widget.ParallaxScrollListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Suncloud on 2016/1/13.
 */
public class WorkOldFragment extends RefreshFragment implements View.OnClickListener,
        WorkItemAdapter.OnItemClickListener, AbsListView.OnScrollListener {

    @BindView(R.id.list_view)
    ParallaxScrollListView listView;

    private View progressBar;
    private View shadowView;
    private View actionLayout;
    private View headerView;
    private View footerView;
    private TextView workDesc;
    private ImageView workDescribeArrow;
    private TextView countView;

    private Work work;
    private Article locationArticle;

    private int itemsHeight;
    private int showHeight;
    private int workDescribeLayoutHeight;
    private boolean workDescribeVisible = true;

    private ArticlesAdapter adapter;
    private ArrayList<Item> items;

    private SaleTimeViewHolder saleHolder;
    private Handler handler = new Handler();
    private Runnable timeDownRun = new Runnable() {
        @Override
        public void run() {
            setSaleView();
        }
    };
    private boolean isSnapshot;
    private Unbinder unbinder;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        shadowView = getActivity().findViewById(R.id.shadow_view);
        actionLayout = getActivity().findViewById(R.id.action_layout);
        progressBar = getActivity().findViewById(R.id.progressBar);
        items = new ArrayList<>();
        adapter = new ArticlesAdapter(getActivity());
        Point point = JSONUtil.getDeviceSize(getActivity());
        DisplayMetrics dm = getResources().getDisplayMetrics();
        itemsHeight = Math.round(point.x * 3 / 4);
        showHeight = itemsHeight - (int) (45 * dm.density);
        if (getArguments() != null) {
            work = (Work) getArguments().getSerializable("work");
            isSnapshot = getArguments().getBoolean("isSnapshot");
            String json = getArguments().getString("json");
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray array = jsonObject.optJSONArray("media_items");
                if (array != null && array.length() > 0) {
                    for (int i = 0, size = array.length(); i < size; i++) {
                        Item item = new Item(array.optJSONObject(i));
                        if (!JSONUtil.isEmpty(item.getMediaPath())) {
                            item.setType(Constants.ItemType.WorkItem);
                            items.add(item);
                        }
                    }
                }
                array = jsonObject.optJSONArray("places");
                if (array != null && array.length() > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0, size = array.length(); i < size; i++) {
                        Place place = new Place(array.optJSONObject(i));
                        if (builder.length() > 0) {
                            builder.append(",")
                                    .append(place.getName());
                        } else {
                            builder.append(place.getName());
                        }
                    }
                    if (builder.length() > 0) {
                        locationArticle = new Article(new JSONObject());
                        locationArticle.setName(getString(R.string.label_services));
                        locationArticle.setDescribe(builder.toString());
                    }
                }
                if (isSnapshot) {
                    ArrayList<Article> articles = new ArrayList<>();
                    if (locationArticle != null) {
                        articles.add(locationArticle);
                    }
                    array = jsonObject.optJSONArray("work_items");
                    if (array != null && array.length() > 0) {
                        for (int i = 0, size = array.length(); i < size; i++) {
                            Article article = new Article(array.optJSONObject(i));
                            if (!JSONUtil.isEmpty(article.getName()) && (!JSONUtil.isEmpty
                                    (article.getDescribe()) || !article.getImages()
                                    .isEmpty())) {
                                articles.add(article);
                            }

                        }
                    }
                    adapter.setArticles(articles);
                    progressBar.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_work_old, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        headerView = View.inflate(getActivity(), R.layout.work_info_new_items, null);
        footerView = View.inflate(getActivity(), R.layout.work_info_footer, null);
        View view = headerView.findViewById(R.id.heard_view);
        view.getLayoutParams().height = itemsHeight;
        listView.addHeaderView(headerView);
        listView.addFooterView(footerView);
        listView.setParallaxImageView(view, itemsHeight);
        listView.setOnScrollListener(this);
        listView.setAdapter(adapter);
        listView.setViewsBounds(2);
        initHeardItemsView();
        if (work != null) {
            initWorkInfo(work);
            initMerchantView(work.getMerchant());
            initMerchantPrivilege(work.getMerchant());
            if (!isSnapshot) {
                new GetArticlesTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                        Constants.getAbsUrl(String.format(Constants.HttpPath.GET_ARTICLES,
                                work.getId())));
            }
        }
        return rootView;
    }

    private void initHeardItemsView() {
        WorkItemAdapter itemAdapter = new WorkItemAdapter(getActivity(), items);
        itemAdapter.setOnItemClickListener(this);
        countView = (TextView) headerView.findViewById(R.id.count);
        countView.setText(getString(R.string.label_pic_count, 1, items.size()));
        final ViewPager itemsView = (ViewPager) headerView.findViewById(R.id.items_view);
        itemsView.setAdapter(itemAdapter);
        itemsView.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                int count = items.size();
                countView.setText(getString(R.string.label_pic_count, 1 + position % count, count));
            }
        });
        if (items.size() > 1) {
            itemsView.setCurrentItem(items.size() * 100 - 1);
            itemsView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    itemsView.setCurrentItem(itemsView.getCurrentItem() + 1);
                }
            }, 300);
        }
    }


    private void initWorkInfo(Work work) {
        setSaleView();
        if (work.getServices() != null) {
            headerView.findViewById(R.id.services_item_title)
                    .setVisibility(View.VISIBLE);
            headerView.findViewById(R.id.service_items_layout)
                    .setVisibility(View.VISIBLE);
            int size = work.getServices()
                    .size();
            if (size > 0) {
                CheckBox item1 = (CheckBox) headerView.findViewById(R.id.item1);
                CheckBox item2 = (CheckBox) headerView.findViewById(R.id.item2);
                CheckBox item3 = (CheckBox) headerView.findViewById(R.id.item3);
                CheckBox item4 = (CheckBox) headerView.findViewById(R.id.item4);
                for (int i = 0; i < size; i++) {
                    String key = work.getServices()
                            .get(i);
                    switch (key) {
                        case "主持人":
                            item3.setChecked(true);
                            break;
                        case "摄影师":
                            item1.setChecked(true);
                            break;
                        case "摄像师":
                            item2.setChecked(true);
                            break;
                        case "化妆师":
                            item4.setChecked(true);
                            break;
                    }
                }
            }
        }
        TextView titleView = (TextView) headerView.findViewById(R.id.title);
        titleView.setText(work.getTitle());
        TextView property = (TextView) headerView.findViewById(R.id.property);
        property.setText(work.getKind());
        ((CheckBox) headerView.findViewById(R.id.discount1)).setChecked(work.isAllowEarnest());
        ((CheckBox) headerView.findViewById(R.id.discount2)).setChecked(work.isCheaperIfAllIn());

        TextView originalPrice = (TextView) headerView.findViewById(R.id.original_price);
        if (work.getMarketPrice() > 0) {
            originalPrice.setVisibility(View.VISIBLE);
            originalPrice.getPaint()
                    .setAntiAlias(true);
            originalPrice.getPaint()
                    .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            originalPrice.setText("  " + getString(R.string.label_price,
                    Util.formatFloat2String(work.getMarketPrice())) + "  ");
        } else {
            originalPrice.setVisibility(View.GONE);
        }
        workDesc = (TextView) headerView.findViewById(R.id.tv_work_describe);
        workDescribeArrow = (ImageView) headerView.findViewById(R.id.arrow_work_describe);
        headerView.findViewById(R.id.describe_title_layout)
                .setOnClickListener(this);
        workDesc.setText(work.getDescribe());
        if (work.getCommentsCount() > 0 && !isSnapshot) {
            headerView.findViewById(R.id.comment_layout)
                    .setVisibility(View.VISIBLE);
            headerView.findViewById(R.id.comment_layout)
                    .setOnClickListener(this);
            TextView commentCount = (TextView) headerView.findViewById(R.id.comment_count);
            commentCount.setText(getString(R.string.label_comment_count5, work.getCommentsCount()));
        } else {
            headerView.findViewById(R.id.comment_layout)
                    .setVisibility(View.GONE);
        }

        String notes = work.getPurchaseNotes();
        String remind = null;
        DataConfig config = Session.getInstance()
                .getDataConfig(getActivity());
        if (work.getMerchant() != null && work.getMerchant()
                .getPropertyId() != 6 && config != null && !JSONUtil.isEmpty(config.getPrepayRemind(
                work.getPropertyId()))) {
            remind = config.getPrepayRemind(work.getPropertyId());
        }
        if (JSONUtil.isEmpty(notes) && JSONUtil.isEmpty(remind)) {
            footerView.findViewById(R.id.purchase_notes_layout)
                    .setVisibility(View.GONE);
        } else {
            footerView.findViewById(R.id.purchase_notes_layout)
                    .setVisibility(View.VISIBLE);
            TextView purchaseNotes = (TextView) footerView.findViewById(R.id.purchase_notes);
            TextView prepayRemind = (TextView) footerView.findViewById(R.id.prepay_remind);
            if (!JSONUtil.isEmpty(notes) && !JSONUtil.isEmpty(remind)) {
                footerView.findViewById(R.id.prepay_line)
                        .setVisibility(View.VISIBLE);
            } else {
                footerView.findViewById(R.id.prepay_line)
                        .setVisibility(View.GONE);
            }
            purchaseNotes.setText(notes);
            prepayRemind.setText(remind);
            purchaseNotes.setVisibility(JSONUtil.isEmpty(notes) ? View.GONE : View.VISIBLE);
            prepayRemind.setVisibility(JSONUtil.isEmpty(remind) ? View.GONE : View.VISIBLE);
        }


        View promiseLayout = footerView.findViewById(R.id.promise_layout);
        if (!JSONUtil.isEmpty(work.getPromiseImage())) {
            Point point = JSONUtil.getDeviceSize(getActivity());
            promiseLayout.setVisibility(View.VISIBLE);
            promiseLayout.setOnClickListener(this);
            ImageView promiseImage = (ImageView) footerView.findViewById(R.id.promise_image);
            promiseImage.getLayoutParams().height = Math.round(point.x * 9 / 40);
            String path = JSONUtil.getImagePathForRound(work.getPromiseImage(), point.x);
            ImageLoadTask task = new ImageLoadTask(promiseImage, null, null, 0, true);
            task.loadImage(path,
                    point.x,
                    ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
        } else {
            promiseLayout.setVisibility(View.GONE);
        }

    }

    private void initMerchantView(NewMerchant merchant) {
        if (merchant != null) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            headerView.findViewById(R.id.merchant_layout)
                    .setOnClickListener(this);
            if (work.isLvpai()) {
                headerView.findViewById(R.id.layout)
                        .setVisibility(View.GONE);
            }
            View bondLayout = headerView.findViewById(R.id.bond_layout);
            bondLayout.setOnClickListener(this);
            ImageView bondIconView = (ImageView) headerView.findViewById(R.id.bond_icon);
            TextView merchantName = (TextView) headerView.findViewById(R.id.merchant_name);
            TextView merchantAddress = (TextView) headerView.findViewById(R.id.merchant_address);
            ImageView merchantLogo = (ImageView) headerView.findViewById(R.id.merchant_logo);
            int padding = 0;
            if (merchant.getBondSign() != null) {
                bondIconView.setVisibility(View.VISIBLE);
                bondLayout.setVisibility(isSnapshot ? View.GONE : View.VISIBLE);
                padding += Math.round(20 * dm.density);

            } else {
                bondIconView.setVisibility(View.GONE);
                bondLayout.setVisibility(View.GONE);
            }
            ImageView levelView = (ImageView) headerView.findViewById(R.id.level_icon);
            int res = 0;
            switch (merchant.getGrade()) {
                case 2:
                    res = R.drawable.icon_merchant_level2;
                    break;
                case 3:
                    res = R.drawable.icon_merchant_level3;
                    break;
                case 4:
                    res = R.drawable.icon_merchant_level4;
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
            merchantName.setPadding(0, 0, padding, 0);
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

    private void initMerchantPrivilege(NewMerchant merchant) {
        if (merchant != null) {
            ArrayList<String> promiseItems = new ArrayList<>();
            if (merchant.getMerchantPromise() != null) {
                promiseItems.addAll(merchant.getMerchantPromise());
            }
            if (merchant.getChargeBack() != null) {
                promiseItems.addAll(merchant.getChargeBack());
            }
            if (!promiseItems.isEmpty()) {
                headerView.findViewById(R.id.merchant_promise_layout)
                        .setVisibility(View.VISIBLE);
                headerView.findViewById(R.id.merchant_promise_layout)
                        .setOnClickListener(this);
                FlowLayout itemsLayout = (FlowLayout) headerView.findViewById(R.id.items_layout);
                int size = promiseItems.size();
                int count = itemsLayout.getChildCount();
                if (count > size) {
                    itemsLayout.removeViews(size, count - size);
                }
                for (int i = 0; i < size; i++) {
                    String item = promiseItems.get(i);
                    View view = null;
                    if (i < count) {
                        view = itemsLayout.getChildAt(i);
                    }
                    if (view == null) {
                        view = View.inflate(getActivity(), R.layout.merchant_promise_item, null);
                        itemsLayout.addView(view);
                    }
                    TextView itemView = (TextView) view.findViewById(R.id.item);
                    itemView.setText(item);
                }
            } else {
                headerView.findViewById(R.id.merchant_promise_layout)
                        .setVisibility(View.GONE);
            }
        } else {
            headerView.findViewById(R.id.promise_layout)
                    .setVisibility(View.GONE);
        }
    }

    private class GetArticlesTask extends AsyncTask<String, Object, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            try {
                String json = JSONUtil.getStringFromUrl(getActivity(), strings[0]);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json).optJSONArray("work_items");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if (getActivity() == null || getActivity().isFinishing() || isDetached() || !isAdded
                    ()) {
                return;
            }
            progressBar.setVisibility(View.GONE);
            if (jsonArray != null) {
                int size = jsonArray.length();
                ArrayList<Article> articles = new ArrayList<>();
                if (locationArticle != null) {
                    articles.add(locationArticle);
                }
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        Article article = new Article(jsonArray.optJSONObject(i));
                        if (!JSONUtil.isEmpty(article.getName()) && (!JSONUtil.isEmpty(article
                                .getDescribe()) || !article.getImages()
                                .isEmpty())) {
                            articles.add(article);
                        }
                    }
                }
                adapter.setArticles(articles);
            }
            super.onPostExecute(jsonArray);
        }
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        if (work == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.comment_layout:
                Intent intent = new Intent(getActivity(), WorkCommentListActivity.class);
                intent.putExtra("w_id", work.getId());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
                break;
            case R.id.merchant_layout:
                MerchantUser merchantUser = Session.getInstance()
                        .getCurrentUser(getContext());
                if (merchantUser.getShopType() == MerchantUser.SHOP_TYPE_SERVICE) {
                    //0 服务商家，1 婚品商家 2.婚车
                    intent = new Intent(getContext(), ShopWebViewActivity.class);
                    intent.putExtra("title", getString(R.string.label_preview_merchant));
                    intent.putExtra("type", 3);
                    intent.putExtra("path",
                            String.format(Constants.WEB_HOST + Constants.HttpPath.GET_SHOP_PREVIEW,
                                    merchantUser.getMerchantId(),
                                    1));
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
                break;
            case R.id.describe_title_layout:
                ResizeAnimation resizeAnimation;
                if (workDescribeVisible) {
                    workDescribeLayoutHeight = workDesc.getMeasuredHeight();
                    workDescribeArrow.setImageResource(R.mipmap.icon_arrow_right_gray_14_26);
                    resizeAnimation = new ResizeAnimation(workDesc, 0);
                    resizeAnimation.setDuration(150);
                    resizeAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            workDescribeVisible = false;
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                } else {
                    workDescribeArrow.setImageResource(R.drawable.icon_arrow_up_gray_24_16);
                    resizeAnimation = new ResizeAnimation(workDesc, workDescribeLayoutHeight);
                    resizeAnimation.setDuration(150);
                    resizeAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            workDescribeVisible = true;
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
                workDesc.startAnimation(resizeAnimation);
                break;
            case R.id.bond_layout:
                if (!isSnapshot && work.getMerchant() != null && !JSONUtil.isEmpty(work
                        .getMerchant()
                        .getBondSignUrl())) {
                    String path = work.getMerchant()
                            .getBondSignUrl();
                    intent = new Intent(getActivity(), HljWebViewActivity.class);
                    intent.putExtra("path", path);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
                break;
            case R.id.promise_layout:
                if (!JSONUtil.isEmpty(work.getPromisePath())) {
                    String path = work.getPromisePath();
                    intent = new Intent(getActivity(), HljWebViewActivity.class);
                    intent.putExtra("path", path);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
                break;
            case R.id.merchant_promise_layout:
                if (work.getMerchant() == null) {
                    return;
                }
                Util.startWebView(getActivity(),
                        work.getMerchant()
                                .getGuidePath());
                break;
            default:
                break;
        }
    }

    @Override
    public void OnItemClickListener(Object item, int position) {
        Item t = (Item) item;
        if (t != null) {
            Intent intent = new Intent(getActivity(), ItemPageViewActivity.class);
            intent.putExtra("items", items);
            intent.putExtra("position", items.indexOf(t));
            startActivity(intent);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(
            AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (headerView != null && work != null) {
            float alpha;
            if (-headerView.getTop() > showHeight) {
                alpha = 1;
            } else if (headerView.getTop() >= 0) {
                alpha = 0;
            } else {
                alpha = (float) -headerView.getTop() / showHeight;
            }
            actionLayout.setAlpha(alpha);
            shadowView.setAlpha(1 - alpha);
        }
    }

    private void setSaleView() {
        if (saleHolder == null) {
            saleHolder = new SaleTimeViewHolder();
        }
        Rule rule = work.getRule();
        Date date = new Date();
        if (rule != null && rule.getId() > 0 && (rule.getEnd_time() == null || rule.getEnd_time()
                .after(date))) {
            if (rule.getStart_time() == null || rule.getStart_time()
                    .before(date)) {
                saleHolder.prepareLayout.setVisibility(View.GONE);
                saleHolder.price.setText(Util.formatFloat2String(work.getSale_price()));
                if (JSONUtil.isEmpty(rule.getShowtxt())) {
                    saleHolder.type.setVisibility(View.GONE);
                } else {
                    saleHolder.type.setVisibility(View.VISIBLE);
                    saleHolder.type.setText(work.getRule()
                            .getShowtxt());
                }
                if (work.getLimit_num() > 0) {
                    saleHolder.limitCountLayout.setVisibility(View.VISIBLE);
                    saleHolder.limitCount.setText(String.valueOf(work.getLimit_num() - work
                            .getLimit_sold_out()));
                } else {
                    saleHolder.limitCountLayout.setVisibility(View.GONE);
                }
                if (rule.getEnd_time() != null) {
                    saleHolder.activityCountDown.setVisibility(View.VISIBLE);
                    long millisInFuture = rule.getEndTimeInMillis() - date.getTime();
                    saleHolder.activityCountDown.setText(getString(R.string.label_work_left3,
                            millisFormat(millisInFuture)));
                    if (millisInFuture >= 0) {
                        handler.postDelayed(timeDownRun, 1000);
                    }
                }
            } else {
                long millisInStart = rule.getStartTimeInMillis() - date.getTime();
                saleHolder.prepareLayout.setVisibility(View.VISIBLE);
                saleHolder.type.setVisibility(View.GONE);
                saleHolder.limitCountLayout.setVisibility(View.GONE);
                saleHolder.activityCountDown.setVisibility(View.GONE);
                saleHolder.price.setText(Util.formatFloat2String(work.getActualPrice()));
                saleHolder.prepareLabel.setText(rule.getName() + getString(R.string
                        .label_work_discount_label));
                saleHolder.preparePrice.setText(getString(R.string.label_price5,
                        Util.formatFloat2String(work.getSale_price())));
                saleHolder.prepareCountDown.setText(getString(R.string.label_work_left1) +
                        millisFormat(
                        millisInStart));
                if (millisInStart >= 0) {
                    handler.postDelayed(timeDownRun, 1000);
                }
            }
        } else {
            saleHolder.price.setText(Util.formatFloat2String(work.getActualPrice()));
            saleHolder.type.setVisibility(View.GONE);
            saleHolder.activityCountDown.setVisibility(View.GONE);
            saleHolder.limitCountLayout.setVisibility(View.GONE);
            saleHolder.prepareLayout.setVisibility(View.GONE);
        }
    }

    private String millisFormat(long millisTime) {
        int days = (int) (millisTime / (1000 * 60 * 60 * 24));
        return (days > 0 ? getString(R.string.label_day,
                days) : "") + TimeUtil.countDownMillisFormat(getActivity(), millisTime);
    }

    private class SaleTimeViewHolder {
        View prepareLayout;
        View limitCountLayout;
        TextView type;
        TextView price;
        TextView preparePrice;
        TextView prepareLabel;
        TextView limitCount;
        TextView activityCountDown;
        TextView prepareCountDown;

        private SaleTimeViewHolder() {
            prepareLayout = headerView.findViewById(R.id.prepare_layout);
            limitCountLayout = headerView.findViewById(R.id.limit_count_layout);
            price = (TextView) headerView.findViewById(R.id.price);
            type = (TextView) headerView.findViewById(R.id.discount_type);
            prepareLabel = (TextView) headerView.findViewById(R.id.prepare_label);
            preparePrice = (TextView) headerView.findViewById(R.id.prepare_price);
            limitCount = (TextView) headerView.findViewById(R.id.limit_count);
            activityCountDown = (TextView) headerView.findViewById(R.id.activity_count_down);
            prepareCountDown = (TextView) headerView.findViewById(R.id.prepare_count_down);
        }

    }

    @Override
    public void onResume() {
        if (work != null && work.getRule() != null && work.getRule()
                .getId() > 0 && headerView != null) {
            setSaleView();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(timeDownRun);
        super.onPause();
    }
}
