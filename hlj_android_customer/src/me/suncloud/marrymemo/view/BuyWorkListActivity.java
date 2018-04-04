package me.suncloud.marrymemo.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.BigWorkViewHolder;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.hunliji.hljtrackerlibrary.TrackerSite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.FiltrateMenuAdapter;
import me.suncloud.marrymemo.adpter.TagsAdapter;
import me.suncloud.marrymemo.api.buywork.BuyWorkApi;
import me.suncloud.marrymemo.api.buywork.BuyWorkFilter;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.Label;
import me.suncloud.marrymemo.model.MerchantProperty;
import me.suncloud.marrymemo.model.buyWork.SetMeal;
import me.suncloud.marrymemo.util.AnimUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.PropertiesUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.newsearch.NewSearchActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import me.suncloud.marrymemo.widget.BuyWorkHintView;
import me.suncloud.marrymemo.widget.BuyWorkListDialog;
import me.suncloud.marrymemo.widget.CheckableLinearLayout2;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 买套餐列表
 * Created by jinxin on 2016/12/2 0002.
 */

public class BuyWorkListActivity extends HljBaseNoBarActivity implements
        CheckableLinearLayout2.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener,
        PullToRefreshBase.OnRefreshListener, AdapterView.OnItemClickListener, BuyWorkListDialog
        .OnConfirmListener {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.tv_property)
    TextView tvProperty;
    @BindView(R.id.cb_property)
    CheckableLinearLayout2 cbProperty;
    @BindView(R.id.tv_sort)
    TextView tvSort;
    @BindView(R.id.cb_sort)
    CheckableLinearLayout2 cbSort;
    @BindView(R.id.cb_filtrate)
    CheckBox cbFiltrate;
    @BindView(R.id.tab_menu_layout)
    LinearLayout tabMenuLayout;
    @BindView(R.id.grid_tag)
    GridView gridTag;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.left_menu_list)
    ListView leftMenuList;
    @BindView(R.id.right_menu_list)
    ListView rightMenuList;
    @BindView(R.id.list_menu_layout)
    LinearLayout listMenuLayout;
    @BindView(R.id.menu_info_layout)
    FrameLayout menuInfoLayout;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.hint_layout)
    BuyWorkHintView hintView;
    @BindView(R.id.tv_filtrate)
    TextView tvFiltrate;

    private ArrayList<MerchantProperty> properties;
    private MerchantProperty property;//第一级分类
    private MerchantProperty childProperty;//第二级分类
    private Label workSort;//排序
    private List<Label> workSorts;
    private long categoryId;//对应categoryId
    private long propertyId;//对应propertyId
    private FiltrateMenuAdapter rightMenuAdapter;
    private FiltrateMenuAdapter leftMenuAdapter;
    private int dividerHeight;
    private WorkAdapter adapter;
    private View footerView;
    private View endView;
    private View loadView;
    private HljHttpSubscriber refreshSubscriber;//初始 刷新
    private HljHttpSubscriber pageSubscriber;//分页
    private City mCity;
    private List<Work> works;
    private BuyWorkListDialog dialog;
    private BuyWorkFilter filter;
    private List<MerchantProperty> tagSelected;//选择的tag
    private List<MerchantProperty> tags;//选择的tag
    private TagsAdapter tagsAdapter;
    private LinearLayoutManager manager;
    private NoticeUtil noticeUtil;

    @Override
    public String pageTrackTagName() {
        return "定套餐二级";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dividerHeight = Math.max(1, Util.dp2px(this, 1) / 2);
        super.onCreate(savedInstanceState);
        propertyId = getIntent().getLongExtra("id", 0);
        setContentView(R.layout.activity_buy_work_list);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        title.setText(getString(R.string.label_buy_work));
        hintView.setTargetView(cbFiltrate);
        boolean showHint = getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE).getBoolean(Constants.PREF_BUY_WORK_HINT_CLICKED, false);
        if (!showHint) {
            hintView.setVisibility(View.VISIBLE);
        } else {
            hintView.setVisibility(View.GONE);
        }
        noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        filter = new BuyWorkFilter();
        mCity = Session.getInstance()
                .getMyCity(this);
        cbProperty.setOnCheckedChangeListener(this);
        cbSort.setOnCheckedChangeListener(this);
        cbFiltrate.setOnCheckedChangeListener(this);

        initLabels();
        initMenus();
        initView();
        initTagsView();

        initTracker();
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView, "package_list");
    }

    private void initTagsView() {
        tagSelected = new ArrayList<>();
        tagsAdapter = new TagsAdapter(this, tagSelected);
        gridTag.setAdapter(tagsAdapter);
    }

    private void initView() {
        footerView = getLayoutInflater().inflate(R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        endView.setVisibility(View.VISIBLE);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        works = new ArrayList<>();
        adapter = new WorkAdapter(works);
        adapter.setFooterView(footerView);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    private void initMenus() {
        leftMenuAdapter = new FiltrateMenuAdapter(this, R.layout.filtrate_menu_list_item);
        leftMenuList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        leftMenuList.setItemsCanFocus(true);
        leftMenuList.setOnItemClickListener(this);
        leftMenuList.setAdapter(leftMenuAdapter);

        rightMenuAdapter = new FiltrateMenuAdapter(this, R.layout.menu_list_item);
        rightMenuList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        rightMenuList.setItemsCanFocus(true);
        rightMenuList.setOnItemClickListener(this);
        rightMenuList.setAdapter(rightMenuAdapter);
    }

    private void initLabels() {
        // 第一级目录properties获取和同步
        properties = new ArrayList<>(PropertiesUtil.getServerPropertiesFromFile(this));
        new PropertiesUtil.PropertiesSyncTask(1, this, new PropertiesUtil.OnFinishedListener() {
            @Override
            public void onFinish(ArrayList<MerchantProperty> p) {
                properties.clear();
                properties.addAll(p);
                initDefaultLabel(propertyId, categoryId);
            }
        }).execute();

        populateRanks();
        setMenuBtnText();
    }

    private void initDefaultLabel(long propertyId, long childId) {
        for (MerchantProperty property : properties) {
            if (property.getChildren() != null && !property.getChildren()
                    .isEmpty()) {
                MerchantProperty allProperty = new MerchantProperty(null);
                allProperty.setName(getString(R.string.label_all));
                property.getChildren()
                        .add(0, allProperty);
            }
        }
        if (propertyId > 0) {
            for (MerchantProperty merchantProperty : properties) {
                if (merchantProperty.getId() == propertyId) {
                    property = merchantProperty;
                    break;
                }
            }
        }
        if (property == null && !properties.isEmpty()) {
            property = properties.get(0);
        }
        if (property != null && (property.getChildren() != null && childId > 0)) {
            for (MerchantProperty merchantProperty : property.getChildren()) {
                if (childId == merchantProperty.getId()) {
                    childProperty = merchantProperty;
                }
            }
        }
        //开始请求数据
        onFiltrated();
    }

    /**
     * 选出筛选条件,开始请求数据
     */
    private void onFiltrated() {
        hideMenu();
        setMenuBtnText();
        //刷新数据
        onRefresh(recyclerView);
    }

    private boolean hideMenu() {
        boolean isShow = false;
        if (cbProperty.isChecked()) {
            isShow = true;
            cbProperty.setChecked(false);
        }
        if (cbSort.isChecked()) {
            isShow = true;
            cbSort.setChecked(false);
        }
        return isShow;
    }

    private void setMenuBtnText() {
        MerchantProperty propertyLabel = property;
        if (childProperty != null && childProperty.getId() > 0) {
            propertyLabel = childProperty;
        }
        if (propertyLabel != null) {
            tvProperty.setText(propertyLabel.getName());
        }

        if (workSort != null) {
            tvSort.setText(workSort.getName());
        }
    }


    private void populateRanks() {
        workSorts = new ArrayList<>();
        //work 默认排序
        Label menuItem1 = new Label();
        menuItem1.setName(getString(R.string.sort_label14));
        menuItem1.setKeyWord("score");
        menuItem1.setOrder("desc");
        workSorts.add(menuItem1);

        //work 价格最高
        Label menuItem2 = new Label();
        menuItem2.setName(getString(R.string.sort_label11));
        menuItem2.setKeyWord("price_down");
        menuItem2.setOrder("desc");
        workSorts.add(menuItem2);

        //work 价格最低
        Label menuItem3 = new Label();
        menuItem3.setName(getString(R.string.sort_label12));
        menuItem3.setKeyWord("price_up");
        menuItem3.setOrder("asc");
        workSorts.add(menuItem3);

        //work 最新发布
        Label menuItem4 = new Label();
        menuItem4.setName(getString(R.string.sort_label3));
        menuItem4.setKeyWord("create_time");
        menuItem4.setOrder("desc");
        workSorts.add(menuItem4);

        workSort = workSorts.get(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (hintView.getVisibility() == View.VISIBLE) {
                hintView.setVisibility(View.GONE);
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.menu_info_layout)
    public void onMenuInfo() {
        hideMenu();
    }


    @OnClick(R.id.btn_back)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.icon_search)
    public void onSearch() {
        Intent intent = new Intent(this, NewSearchActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.btn_msg)
    public void onSms(View view) {
        if (Util.loginBindChecked(this, Constants.RequestCode.NOTIFICATION_PAGE)) {
            Intent intent = new Intent(this, MessageHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }


    @Override
    public void onCheckedChange(View view, boolean checked) {
        switch (view.getId()) {
            case R.id.cb_property:
                if (properties.isEmpty()) {
                    return;
                }
                if (checked) {
                    cbSort.setChecked(false);
                    leftMenuList.setVisibility(View.VISIBLE);
                    leftMenuAdapter.setData(properties);
                    int position = 0;
                    if (property != null) {
                        position = properties.indexOf(property);
                    } else {
                        property = properties.get(0);
                    }
                    leftMenuList.setItemChecked(position, true);
                    if (property.getChildren() == null || property.getChildren()
                            .isEmpty()) {
                        childProperty = null;
                        rightMenuList.setVisibility(View.GONE);
                    } else {
                        rightMenuList.setVisibility(View.VISIBLE);
                        rightMenuList.setDividerHeight(0);
                        rightMenuAdapter.setData(property.getChildren());
                        position = 0;
                        if (childProperty != null) {
                            position = property.getChildren()
                                    .indexOf(childProperty);
                        } else {
                            childProperty = property.getChildren()
                                    .get(0);
                        }
                        rightMenuList.setItemChecked(position, true);
                    }
                    AnimUtil.showMenu2Animation(menuInfoLayout, menuInfoLayout);
                } else {
                    AnimUtil.hideMenu2Animation(menuInfoLayout, menuInfoLayout);
                }
                break;
            case R.id.cb_sort:
                int position = workSorts.indexOf(workSort);
                if (checked) {
                    cbProperty.setChecked(false);
                    leftMenuList.setVisibility(View.GONE);
                    rightMenuList.setVisibility(View.VISIBLE);
                    rightMenuList.setDividerHeight(dividerHeight);
                    rightMenuAdapter.setData(workSorts);
                    rightMenuList.setItemChecked(position, true);
                    AnimUtil.showMenu2Animation(menuInfoLayout, menuInfoLayout);
                } else {
                    AnimUtil.hideMenu2Animation(menuInfoLayout, menuInfoLayout);
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        //筛选
        switch (compoundButton.getId()) {
            case R.id.cb_filtrate:
                if (b) {
                    if (dialog == null) {
                        final View dialogView = getLayoutInflater().inflate(R.layout
                                        .dialog_buy_work_list,
                                null);
                        dialog = new BuyWorkListDialog(this, R.style.BubbleDialogTheme);
                        dialog.setContentView(dialogView);
                        dialog.setOnConfirmListener(this);
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                cbFiltrate.setChecked(false);
                            }
                        });
                    }
                    if (tags != null) {
                        dialog.setPropertyList(tags);
                    }
                    dialog.show();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                break;
        }
    }

    private void scrollToTop() {
        int position = manager.findFirstVisibleItemPosition();
        if (position > 5) {
            recyclerView.getRefreshableView()
                    .scrollToPosition(5);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.getRefreshableView()
                            .smoothScrollToPosition(0);
                }
            });
        } else {
            recyclerView.getRefreshableView()
                    .smoothScrollToPosition(0);
        }
    }

    @Override
    public void onRefresh(final PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setPullToRefreshBase(refreshView)
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<SetMeal>() {
                        @Override
                        public void onNext(SetMeal setMeal) {
                            int pageCount = 0;
                            if (setMeal != null) {
                                HljHttpData<List<Work>> hljWork = setMeal.getWorks();
                                if (hljWork != null) {
                                    pageCount = hljWork.getPageCount();
                                    works.clear();
                                    if (hljWork.getData() != null) {
                                        works.addAll(hljWork.getData());
                                    }
                                    tags = setMeal.getTags();
                                    adapter.notifyDataSetChanged();
                                    scrollToTop();
                                    if (works.isEmpty()) {
                                        recyclerView.setVisibility(View.GONE);
                                        emptyView.showEmptyView();
                                    } else {
                                        recyclerView.setVisibility(View.VISIBLE);
                                        emptyView.hideEmptyView();
                                    }
                                }
                            }
                            initPagination(pageCount);
                        }
                    })
                    .build();
        }
        Observable<SetMeal> observable = BuyWorkApi.getBuyWork(categoryId,
                propertyId,
                mCity.getId(),
                workSort == null ? "score" : workSort.getKeyWord(),
                filter,
                1);
        observable.subscribe(refreshSubscriber);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<SetMeal> observable = PaginationTool.buildPagingObservable(recyclerView
                        .getRefreshableView(),
                pageCount,
                new PagingListener<SetMeal>() {
                    @Override
                    public Observable<SetMeal> onNextPage(int page) {
                        return BuyWorkApi.getBuyWork(categoryId,
                                propertyId,
                                mCity.getId(),
                                workSort == null ? "score" : workSort.getKeyWord(),
                                filter,
                                page);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable();

        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<SetMeal>() {
                    @Override
                    public void onNext(SetMeal setMeal) {
                        if (setMeal != null) {
                            HljHttpData<List<Work>> hljWork = setMeal.getWorks();
                            if (hljWork != null) {
                                if (hljWork.getData() != null) {
                                    works.addAll(hljWork.getData());
                                }
                                adapter.notifyDataSetChanged();
                                if (works.isEmpty()) {
                                    recyclerView.setVisibility(View.GONE);
                                    emptyView.setVisibility(View.VISIBLE);
                                } else {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    emptyView.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }


    @Override
    protected void onResume() {
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
        super.onResume();
    }


    @Override
    protected void onPause() {
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
        super.onPause();
    }


    @Override
    protected void onFinish() {
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber.unsubscribe();
        }
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        if (dialog != null) {
            dialog.dismiss();
        }
        super.onFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
        Label label = (Label) parent.getAdapter()
                .getItem(position);
        if (label != null) {
            if (label instanceof MerchantProperty) {
                if (parent == leftMenuList) {
                    //选类目
                    MerchantProperty property = (MerchantProperty) label;
                    if (property.getChildren() == null || property.getChildren()
                            .isEmpty()) {
                        this.property = property;
                        propertyId = property.getId();
                        childProperty = null;
                        categoryId = 0;
                        rightMenuList.setVisibility(View.GONE);

                        tagSelected.clear();
                        tagsAdapter.notifyDataSetChanged();
                        if (dialog != null) {
                            dialog.onReset(null);
                        }
                        filter.setTags(null);
                    } else {
                        rightMenuList.setVisibility(View.VISIBLE);
                        rightMenuList.setDividerHeight(0);
                        rightMenuAdapter.setData(property.getChildren());
                        position = -1;
                        this.property = property;
                        propertyId = property.getId();
                        if (childProperty != null) {
                            position = property.getChildren()
                                    .indexOf(childProperty);
                        }
                        if (position < 0) {
                            rightMenuList.setItemChecked(rightMenuList.getCheckedItemPosition(),
                                    false);
                            categoryId = 0;
                        } else {
                            rightMenuList.setItemChecked(position, true);
                            categoryId = childProperty.getId();
                        }
                        return;
                    }
                } else if (parent == rightMenuList) {
                    property = properties.get(leftMenuList.getCheckedItemPosition());
                    childProperty = (MerchantProperty) label;
                    categoryId = childProperty.getId();
                    propertyId = property.getId();

                    tagSelected.clear();
                    tagsAdapter.notifyDataSetChanged();
                    if (dialog != null) {
                        dialog.onReset(null);
                    }
                    filter.setTags(null);
                }
            } else {
                //排序
                workSort = label;
            }

            onFiltrated();
        }
    }

    @Override
    public void onConfirm(
            String priceMin,
            String priceMax,
            HashMap<MerchantProperty, List<? extends Label>> select) {
        if (!JSONUtil.isEmpty(priceMax)) {
            filter.setPriceMax(Double.parseDouble(priceMax));
        } else {
            filter.setPriceMax(-1);
        }
        if (!JSONUtil.isEmpty(priceMin)) {
            filter.setPriceMin(Double.parseDouble(priceMin));
        } else {
            filter.setPriceMin(-1);
        }
        if (select != null && !select.isEmpty()) {
            this.tagSelected.clear();
            List<String> tags = new ArrayList<>();
            Set<MerchantProperty> set = select.keySet();
            Iterator<MerchantProperty> iterator = set.iterator();
            while (iterator.hasNext()) {
                MerchantProperty p = iterator.next();
                List<MerchantProperty> values = (List<MerchantProperty>) select.get(p);
                this.tagSelected.addAll(values);
                StringBuilder builder = new StringBuilder();
                for (MerchantProperty v : values) {
                    if (v != null && v.getId() > 0) {
                        builder.append(v.getMarkId())
                                .append(",");
                    }
                }
                if (builder.length() > 0) {
                    builder.deleteCharAt(builder.lastIndexOf(","));
                }
                tags.add(builder.toString());
            }
            filter.setTags(tags);
        } else {
            filter.setTags(null);
            tagSelected.clear();
        }
        if (!JSONUtil.isEmpty(priceMax) || !JSONUtil.isEmpty(priceMin) || (select != null &&
                !select.isEmpty())) {
            cbFiltrate.setTextColor(getResources().getColor(R.color.colorPrimary));
            tvFiltrate.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            cbFiltrate.setTextColor(getResources().getColor(R.color.colorBlack3));
            tvFiltrate.setTextColor(getResources().getColor(R.color.colorBlack3));
        }
        if (tagSelected.size() > 4) {
            List<MerchantProperty> ps = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                ps.add(tagSelected.get(i));
            }
            tagSelected.clear();
            tagSelected.addAll(ps);
        }
        tagsAdapter.notifyDataSetChanged();
        if (this.tagSelected.isEmpty()) {
            gridTag.setVisibility(View.GONE);
        } else {
            gridTag.setVisibility(View.VISIBLE);
        }
        onRefresh(recyclerView);
    }


    class WorkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
            OnItemClickListener {
        private final int FOOT = 1;
        private final int ITEM = 2;
        private final int DEFAULT = -1;
        private View footerView;
        private List<Work> works;

        public WorkAdapter(List<Work> works) {
            this.works = works;
        }

        public void setFooterView(View footerView) {
            this.footerView = footerView;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case FOOT:
                    return new ExtraViewHolder(footerView);
                case ITEM:
                    View itemView = getLayoutInflater().inflate(R.layout.big_commom_work_item__cv,
                            parent,
                            false);
                    BigWorkViewHolder holder = new BigWorkViewHolder(itemView);
                    holder.setOnItemClickListener(this);
                    return holder;
                default:
                    break;
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
            int type = getItemViewType(position);
            if (type == ITEM) {
                BigWorkViewHolder holder = (BigWorkViewHolder) h;
                holder.setShowBottomLineView(position < works.size() - 1);
                holder.setView(BuyWorkListActivity.this, works.get(position), position, type);
            }
        }

        @Override
        public int getItemCount() {
            return works == null ? 0 : works.size() + ((works != null && footerView != null) ? 1
                    : 0);
        }

        @Override
        public int getItemViewType(int position) {
            int type = DEFAULT;
            if (position != getItemCount() - 1) {
                type = ITEM;
            } else {
                type = FOOT;
            }
            return type;
        }

        @Override
        public void onItemClick(int position, Object object) {
            if (object != null) {
                Work work = (Work) object;
                String link = work.getLink();
                if (JSONUtil.isEmpty(link)) {
                    Intent intent = new Intent(BuyWorkListActivity.this, WorkActivity.class);
                    intent.putExtra("id", work.getId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                } else {
                    HljWeb.startWebView(BuyWorkListActivity.this, link);
                }
            }
        }
    }

    class ExtraViewHolder extends RecyclerView.ViewHolder {
        View itemView;

        public ExtraViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }

}
