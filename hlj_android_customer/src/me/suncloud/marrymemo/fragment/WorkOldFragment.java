package me.suncloud.marrymemo.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.SmallWorkViewHolder;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljtrackerlibrary.utils.TrackerUtil;
import com.hunliji.hunlijicalendar.ResizeAnimation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ArticlesAdapter;
import me.suncloud.marrymemo.adpter.WorkItemAdapter;
import me.suncloud.marrymemo.adpter.work_case.viewholder.OldWorkRecWorkViewHolder;
import me.suncloud.marrymemo.api.work_case.WorkApi;
import me.suncloud.marrymemo.model.Article;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.Item;
import me.suncloud.marrymemo.model.NewMerchant;
import me.suncloud.marrymemo.model.Place;
import me.suncloud.marrymemo.model.Rule;
import me.suncloud.marrymemo.model.ShareInfo;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.ImageLoadUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.TimeUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.ItemPageViewActivity;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.view.comment.ServiceCommentListActivity;
import me.suncloud.marrymemo.view.work_case.MerchantWorkListActivity;
import me.suncloud.marrymemo.widget.ParallaxScrollListView;
import rx.functions.Func1;

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
    private City city;
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
    private HljHttpSubscriber recommendSub;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        city = Session.getInstance()
                .getMyCity(getActivity());
        items = new ArrayList<>();
        adapter = new ArticlesAdapter(getActivity());
        Point point = JSONUtil.getDeviceSize(getActivity());
        DisplayMetrics dm = getResources().getDisplayMetrics();
        itemsHeight = Math.round(point.x * 3 / 4);
        showHeight = itemsHeight - (int) (45 * dm.density) - HljBaseActivity.getStatusBarHeight(
                getContext());
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_work_old, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        headerView = View.inflate(getContext(), R.layout.work_info_items, null);
        footerView = View.inflate(getContext(), R.layout.work_info_more, null);
        View view = headerView.findViewById(R.id.heard_view);
        view.getLayoutParams().height = itemsHeight;
        listView.addHeaderView(headerView);
        listView.addFooterView(footerView);
        listView.setParallaxImageView(view, itemsHeight);
        listView.setOnScrollListener(this);
        listView.setAdapter(adapter);
        listView.setViewsBounds(2);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() == null) {
            return;
        }
        shadowView = getActivity().findViewById(R.id.shadow_view);
        actionLayout = getActivity().findViewById(R.id.action_layout);
        shadowView.setVisibility(View.VISIBLE);
        actionLayout.setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.action_holder_layout)
                .setVisibility(View.VISIBLE);
        progressBar = getActivity().findViewById(R.id.progressBar);
        initData();
        initHeardItemsView();
        if (work != null) {
            initWorkInfo(work);
            initMerchantView(work.getMerchant());
            initMerchantPrivilege(work.getMerchant());
            if (!isSnapshot) {
                new GetArticlesTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                        Constants.getAbsUrl(String.format(Constants.HttpPath.GET_ARTICLES,
                                work.getId())));
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }

    }

    private void initData() {
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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
                    if (isDetached() || getActivity() == null || getActivity().isFinishing()) {
                        return;
                    }
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
                    Util.formatDouble2String(work.getMarketPrice())) + "  ");
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
        if (config != null && !JSONUtil.isEmpty(config.getPrepayRemind(work.getPropertyId()))) {
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
        if (!JSONUtil.isEmpty(work.getPromiseImage()) && !isSnapshot) {
            Point point = JSONUtil.getDeviceSize(getActivity());
            promiseLayout.setVisibility(View.VISIBLE);
            promiseLayout.setOnClickListener(this);
            ImageView promiseImage = (ImageView) footerView.findViewById(R.id.promise_image);
            promiseImage.getLayoutParams().height = Math.round(point.x * 9 / 40);
            String path = JSONUtil.getImagePathWithoutFormat(work.getPromiseImage(), point.x);
            ImageLoadTask task = new ImageLoadTask(promiseImage, null, null, 0, true, true);
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
            long merchantId = merchant.getId();

            DisplayMetrics dm = getResources().getDisplayMetrics();
            headerView.findViewById(R.id.merchant_layout)
                    .setOnClickListener(this);
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
            if (!isSnapshot) {
                new GetWorksTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                        Constants.getAbsUrl(Constants.HttpPath.GET_MERCHANT_WORKS_AND_CASES,
                                merchant.getId(),
                                "set_meal",
                                null,
                                1,
                                5));
                loadRecommendMeals();
            }
        }
    }


    public void loadRecommendMeals() {
        recommendSub = HljHttpSubscriber.buildSubscriber(getContext())
                .toastHidden()
                .setOnNextListener(new SubscriberOnNextListener<List<com.hunliji.hljcommonlibrary
                        .models.Work>>() {
                    @Override
                    public void onNext(List<com.hunliji.hljcommonlibrary.models.Work> works) {
                        initRecommendWorks(works);
                    }
                })
                .build();
        WorkApi.getRecommendsMeals(city == null ? 0 : city.getId(), work.getId())
                .onErrorReturn(new Func1<Throwable, List<com.hunliji.hljcommonlibrary.models
                        .Work>>() {
                    @Override
                    public List<com.hunliji.hljcommonlibrary.models.Work> call(Throwable
                                                                                       throwable) {
                        throwable.printStackTrace();
                        return null;
                    }
                })
                .subscribe(recommendSub);
    }


    private void initMerchantPrivilege(NewMerchant merchant) {
        if (merchant != null) {
            StringBuilder promiseBuilder = new StringBuilder();
            if (merchant.getMerchantPromise() != null) {
                for (String promise : merchant.getMerchantPromise()) {
                    if (promiseBuilder.length() > 0) {
                        promiseBuilder.append("    ");
                    }
                    promiseBuilder.append(promise);
                }
            }
            StringBuilder refundBuilder = new StringBuilder();
            if (merchant.getChargeBack() != null) {
                for (String refund : merchant.getChargeBack()) {
                    if (refundBuilder.length() > 0) {
                        refundBuilder.append("    ");
                    }
                    refundBuilder.append(refund);
                }
            }
            if (refundBuilder.length() > 0 && promiseBuilder.length() > 0) {
                headerView.findViewById(R.id.merchant_promise_layout)
                        .setVisibility(View.VISIBLE);
                headerView.findViewById(R.id.merchant_promise_layout)
                        .setOnClickListener(this);
                if (promiseBuilder.length() > 0) {
                    headerView.findViewById(R.id.promise_layout)
                            .setVisibility(View.VISIBLE);
                    ((TextView) headerView.findViewById(R.id.tv_promise)).setText(promiseBuilder
                            .toString());
                } else {
                    headerView.findViewById(R.id.promise_layout)
                            .setVisibility(View.GONE);
                }
                if (refundBuilder.length() > 0) {
                    headerView.findViewById(R.id.refund_layout)
                            .setVisibility(View.VISIBLE);
                    ((TextView) headerView.findViewById(R.id.tv_refund)).setText(refundBuilder
                            .toString());
                } else {
                    headerView.findViewById(R.id.refund_layout)
                            .setVisibility(View.GONE);
                }
            } else {
                headerView.findViewById(R.id.merchant_promise_layout)
                        .setVisibility(View.GONE);
            }
        } else {
            headerView.findViewById(R.id.merchant_promise_layout)
                    .setVisibility(View.GONE);
        }
    }

    private class GetArticlesTask extends AsyncTask<String, Object, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            try {
                String json = JSONUtil.getStringFromUrl(strings[0]);
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

    private class GetWorksTask extends AsyncTask<String, Object, JSONObject> {


        @Override
        protected JSONObject doInBackground(String... strings) {
            try {
                String json = JSONUtil.getStringFromUrl(strings[0]);
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
        protected void onPostExecute(JSONObject jsonObject) {
            if (getActivity() == null || getActivity().isFinishing() || isDetached() || !isAdded
                    ()) {
                return;
            }
            int otherWorksCount = 0;
            ArrayList<Work> works = new ArrayList<>();
            if (jsonObject != null) {
                otherWorksCount = jsonObject.optInt("total_count", 0);
                JSONArray array = jsonObject.optJSONArray("list");
                if (array != null && array.length() > 0) {
                    int size = array.length();
                    for (int i = 0; i < size; i++) {
                        Work w = new Work(array.optJSONObject(i));
                        if (!w.getId()
                                .equals(work.getId())) {
                            works.add(w);
                        }
                    }
                }
            }
            initOtherWorks(otherWorksCount, works);
            super.onPostExecute(jsonObject);
        }
    }

    //这个商家的其他套餐
    private void initOtherWorks(int otherWorksCount, ArrayList<Work> works) {
        int size = works.size();
        if (size > 0) {
            // 显示其他套餐
            footerView.findViewById(R.id.other_works_layout)
                    .setVisibility(View.VISIBLE);
            View moreOtherWorks = footerView.findViewById(R.id.more_work_count);
            if (otherWorksCount > 5 || works.size() > 4) {
                moreOtherWorks.setVisibility(View.VISIBLE);
                moreOtherWorks.setOnClickListener(this);
            } else {
                moreOtherWorks.setVisibility(View.GONE);
            }
            for (int i = 0; i < 4; i++) {
                View view = null;
                switch (i) {
                    case 0:
                        view = footerView.findViewById(R.id.other_works_item1);
                        break;
                    case 1:
                        view = footerView.findViewById(R.id.other_works_item2);
                        break;
                    case 2:
                        view = footerView.findViewById(R.id.other_works_item3);
                        break;
                    case 3:
                        view = footerView.findViewById(R.id.other_works_item4);
                        break;
                }
                if (view == null) {
                    continue;
                }
                if (i < size) {
                    view.setVisibility(View.VISIBLE);
                    Work work = works.get(i);
                    view.findViewById(R.id.line_layout)
                            .setVisibility((i == size - 1 && moreOtherWorks.getVisibility() ==
                                    View.GONE) ? View.GONE : View.VISIBLE);
                    setWorkViewValue(view, work, i, "S3/A2");
                } else {
                    view.setVisibility(View.GONE);
                }
            }
        } else {
            // 否则隐藏其他套餐
            footerView.findViewById(R.id.other_works_layout)
                    .setVisibility(View.GONE);
        }
    }

    private void setWorkViewValue(View view, Work work, final int position, final String site) {
        view.setTag(work);
        ImageView iconInstallment = (ImageView) view.findViewById(R.id.img_installment);
        ImageView oWorkImg = (ImageView) view.findViewById(R.id.img_cover);
        TextView oWorkTitle = (TextView) view.findViewById(R.id.tv_work_title);
        TextView oWorkPrice = (TextView) view.findViewById(R.id.tv_work_price);
        TextView oWorkPrice1 = (TextView) view.findViewById(R.id.tv_work_price1);
        TextView oWorkCollectCount = (TextView) view.findViewById(R.id.tv_work_collect);
        TextView tvHotTag = (TextView) view.findViewById(R.id.tv_hot_tag);
        String url = JSONUtil.getImagePath(work.getCoverPath(), Util.dp2px(getActivity(), 116));
        if (!JSONUtil.isEmpty(url)) {
            ImageLoadUtil.loadImageView(this, url, R.mipmap.icon_empty_image, oWorkImg, false);
        }
        iconInstallment.setVisibility(work.isInstallment() ? View.VISIBLE : View.GONE);
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
        oWorkPrice.setText(Util.formatDouble2String(work.getShowPrice()));
        oWorkCollectCount.setText(getString(R.string.label_collect_count,
                work.getCollectorCount()));
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
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Work work = (Work) v.getTag();
                if (work != null) {
                    Intent intent = new Intent(getActivity(), WorkActivity.class);
                    JSONObject jsonObject = TrackerUtil.getSiteJson(site,
                            position + 1,
                            "套餐" + work.getId() + work.getTitle());
                    if (jsonObject != null) {
                        intent.putExtra("site", jsonObject.toString());
                    }
                    intent.putExtra("id", work.getId());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            }
        });
    }

    //推荐
    private void initRecommendWorks(List<com.hunliji.hljcommonlibrary.models.Work> works) {
        if (!CommonUtil.isCollectionEmpty(works)) {
            footerView.findViewById(R.id.recommend_works_layout)
                    .setVisibility(View.VISIBLE);
            LinearLayout worksLayout = footerView.findViewById(R.id.rec_works);
            int size = Math.min(3, works.size());
            for (int i = 0; i < size; i++) {
                View view = View.inflate(getActivity(), R.layout.common_work_item, null);
                view.setVisibility(View.VISIBLE);
                view.findViewById(R.id.line_layout)
                        .setVisibility(i == size - 1 ? View.GONE : View.VISIBLE);
                worksLayout.addView(view);
                OldWorkRecWorkViewHolder viewHolder = new OldWorkRecWorkViewHolder(view);
                viewHolder.setView(getContext(), works.get(i), i, 0);

            }
        } else {
            footerView.findViewById(R.id.recommend_works_layout)
                    .setVisibility(View.GONE);
        }
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        CommonUtil.unSubscribeSubs(recommendSub);
    }

    @Override
    public void onClick(View v) {
        if (work == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.comment_layout:
                NewMerchant newMerchant = work.getMerchant();
                Merchant merchant = new Merchant();
                merchant.setId(newMerchant.getId());
                merchant.setUserId(newMerchant.getUserId());
                merchant.setName(newMerchant.getName());
                merchant.setLogoPath(newMerchant.getLogoPath());
                merchant.setShopType(newMerchant.getShopType());
                Intent intent = new Intent(getContext(), ServiceCommentListActivity.class);
                intent.putExtra(ServiceCommentListActivity.ARG_MERCHANT, merchant);
                intent.putExtra(ServiceCommentListActivity.ARG_MERCHANT_ID, merchant.getId());
                intent.putExtra(ServiceCommentListActivity.ARG_MERCHANT_USER_ID,
                        merchant.getUserId());
                intent.putExtra(ServiceCommentListActivity.ARG_WORK_ID, work.getId());
                startActivity(intent);
                break;
            case R.id.merchant_layout:
                if (work.getMerchantId() > 0) {
                    intent = new Intent(getActivity(), MerchantDetailActivity.class);
                    intent.putExtra("id", work.getMerchantId());
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
                    workDescribeArrow.setImageResource(R.mipmap.icon_arrow_up_gray_26_14);
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
            case R.id.more_work_count:
                if (work.getMerchantId() > 0) {
                    intent = new Intent(getActivity(), MerchantWorkListActivity.class);
                    intent.putExtra("id", work.getMerchantId());
                    intent.putExtra("style", SmallWorkViewHolder.STYLE_COMMON);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
                break;
            case R.id.bond_layout:
                if (!isSnapshot && work.getMerchant() != null && !JSONUtil.isEmpty(work
                        .getMerchant()
                        .getBondSignUrl())) {
                    HljWeb.startWebView(getActivity(),
                            work.getMerchant()
                                    .getBondSignUrl());
                }
                break;
            case R.id.promise_layout:
                HljWeb.startWebView(getActivity(), work.getPromisePath());
                break;
            case R.id.merchant_promise_layout:
                if (work.getMerchant() == null) {
                    return;
                }
                HljWeb.startWebView(getActivity(),
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
            ShareInfo shareInfo = work.getShareInfo(getActivity());
            intent.putExtra("position", items.indexOf(t));
            intent.putExtra("title", shareInfo.getTitle());
            intent.putExtra("description", shareInfo.getDesc());
            intent.putExtra("weiboDescription", shareInfo.getDesc2());
            intent.putExtra("url", shareInfo.getUrl());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(
            AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (actionLayout == null || shadowView == null) {
            return;
        }
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
        if (rule != null && rule.getId() > 0 && (!rule.isTimeAble() || rule.getEnd_time() == null
                || rule.getEnd_time()
                .after(date))) {
            if (!rule.isTimeAble() || rule.getStart_time() == null || rule.getStart_time()
                    .before(date)) {
                saleHolder.prepareLayout.setVisibility(View.GONE);
                saleHolder.price.setText(Util.formatDouble2String(work.getSale_price()));
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
                if (rule.isTimeAble() && rule.getEnd_time() != null) {
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
                saleHolder.price.setText(Util.formatDouble2String(work.getPrice()));
                saleHolder.prepareLabel.setText(rule.getName() + getString(R.string
                        .label_work_discount_label));
                saleHolder.preparePrice.setText(getString(R.string.label_price5,
                        Util.formatDouble2String(work.getSale_price())));
                saleHolder.prepareCountDown.setText(getString(R.string.label_work_left1) +
                        millisFormat(
                        millisInStart));
                if (millisInStart >= 0) {
                    handler.postDelayed(timeDownRun, 1000);
                }
            }
        } else {
            saleHolder.price.setText(Util.formatDouble2String(work.getPrice()));
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
        if (handler != null) {
            handler.removeCallbacks(timeDownRun);
        }
        super.onPause();
    }
}
