package me.suncloud.marrymemo.view.budget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.hljsharelibrary.utils.ShareUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.budget.BudgetAdapter;
import me.suncloud.marrymemo.api.budget.BudgetApi;
import me.suncloud.marrymemo.api.tools.ToolsApi;
import me.suncloud.marrymemo.fragment.ToolsFragment;
import me.suncloud.marrymemo.fragment.user.UserFragment;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.budget.BudgetCategory;
import me.suncloud.marrymemo.model.budget.BudgetCategoryListWrapper;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.NewBudgetUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.marry.MarryBookActivity;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func3;

/**
 * 结婚预算
 * Created by jinxin on 2017/11/20 0020.
 */

public class NewWeddingBudgetActivity extends HljBaseNoBarActivity implements BudgetAdapter
        .onBudgetAdapterListener {

    @Override
    public String pageTrackTagName() {
        return "结婚预算";
    }

    //预算金额
    public static final String ARG_FIGURE = "arg_figure";
    //进入这个页面的来源
    public static final String ARG_FROM = "arg_from_class";
    //方式2 进入会传来一个已经选择的list，并且会调用onNewIntent
    public static final String ARG_SELECTED_CATEGORY = "arg_selected_category";

    public final int ARG_FIGURE_RESULT_CODE = 101;

    public final int ARG_CATEGORY_RESULT_CODE = 102;

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;
    @BindView(R.id.action_layout)
    LinearLayout actionLayout;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.layout_type)
    LinearLayout layoutType;

    private ShareUtil shareUtil;
    private HljHttpSubscriber loadSub;
    private double figure;
    private List<BudgetCategory> allCategoryList;//总的数据
    private List<BudgetCategoryListWrapper> selectedList;//已经选择的
    private LongSparseArray<List<BudgetCategory>> groupCategoryList;//预算的类别 分别婚前 婚礼 婚后 三种

    private BudgetAdapter budgetAdapter;
    private LinearLayoutManager manager;
    private Handler handler;
    private CpmRunnable cpmRunnable;
    private HljHttpSubscriber cpmSub;
    private List<Work> cpmWorks;
    private String fromClass;
    private Subscription rxBusSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_budget);
        ButterKnife.bind(this);

        initConstant();
        initWidget();
        initLoad();
        registerRxBusEvent();
    }

    private void initConstant() {
        if (getIntent() != null) {
            fromClass = getIntent().getStringExtra(ARG_FROM);
            figure = getIntent().getDoubleExtra(ARG_FIGURE, 0D);
        }
        allCategoryList = new ArrayList<>();
        selectedList = new ArrayList<>();
        groupCategoryList = new LongSparseArray<>();

        handler = new Handler();
        cpmRunnable = new CpmRunnable();
        cpmWorks = new ArrayList<>();

        budgetAdapter = new BudgetAdapter(this,
                groupCategoryList,
                selectedList,
                allCategoryList,
                cpmWorks);
        budgetAdapter.setOnBudgetAdapterListener(this);
    }

    private void initWidget() {
        setActionBarPadding(actionLayout);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setAdapter(budgetAdapter);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .addOnScrollListener(onScrollListener);
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int position = manager.findFirstVisibleItemPosition();
            if (position >= 1) {
                layoutType.setVisibility(View.VISIBLE);
                updateType(position);
            } else {
                layoutType.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }
    };

    private void updateType(int position) {
        if (position == 0) {
            return;
        }
        //去除header
        position--;
        if (position < selectedList.size()) {
            BudgetCategoryListWrapper wrapper = selectedList.get(position);
            if (wrapper != null) {
                BudgetCategory p = NewBudgetUtil.getInstance()
                        .getCategoryById(allCategoryList, wrapper.getPid());
                if (p != null) {
                    tvHint.setText(p.getTitle());
                }
            }
        }
    }

    private void initLoad() {
        loadSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                    @Override
                    public void onNext(ResultZip resultZip) {
                        setResultZip(resultZip);
                    }
                })
                .build();
        User user = Session.getInstance()
                .getCurrentUser(this);
        Observable<JsonElement> shareInfoObb = BudgetApi.getShareInfo(user == null ? 0 : user
                .getId());
        Observable<JsonElement> budgetObb = BudgetApi.getBudgetInfo();
        Observable<JsonElement> budgetCategoryObb = BudgetApi.getBudgetCategory();

        Observable<ResultZip> zipObb = Observable.zip(budgetObb,
                shareInfoObb,
                budgetCategoryObb,
                new Func3<JsonElement, JsonElement, JsonElement, ResultZip>() {
                    @Override
                    public ResultZip call(
                            JsonElement budgetInfo,
                            JsonElement shareInfo,
                            JsonElement categoryInfo) {
                        ResultZip resultZip = new ResultZip();
                        resultZip.budgetInfo = budgetInfo;
                        resultZip.shareInfo = shareInfo;
                        resultZip.budgetCategory = categoryInfo;
                        return resultZip;
                    }
                });
        zipObb.subscribe(loadSub);

    }

    private void registerRxBusEvent() {
        if (rxBusSubscription == null || rxBusSubscription.isUnsubscribed()) {
            rxBusSubscription = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case CLOSE_BUDGET:
                                    finish();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
        }
    }

    private void setResultZip(ResultZip zip) {
        setShareInfo(zip.shareInfo);
        setBudgetCategory(zip.budgetCategory);
        if (fromClass != null && (fromClass.equals(ToolsFragment.class.getSimpleName()) ||
                fromClass.equals(
                MarryBookActivity.class.getSimpleName()) || fromClass.equals(UserFragment.class
                .getSimpleName()) || fromClass.equals(
                BannerUtil.class.getSimpleName()))) {
            //默认全选
            resetFigureKind(allCategoryList);
            configSelectList(allCategoryList);
        } else {
            setBudgetInfo(zip.budgetInfo);
        }
        refresh();
    }

    private void setShareInfo(JsonElement shareInfoJson) {
        if (shareInfoJson != null) {
            JsonElement share = shareInfoJson.getAsJsonObject()
                    .get("share");
            if (share != null) {
                if (share != null) {
                    ShareInfo shareInfo = GsonUtil.getGsonInstance()
                            .fromJson(share, ShareInfo.class);
                    shareUtil = new ShareUtil(this, shareInfo, null);
                }
            }
        }
    }

    private void setBudgetCategory(JsonElement categoryJson) {
        allCategoryList.clear();
        if (categoryJson != null) {
            JsonArray list = categoryJson.getAsJsonObject()
                    .get("list")
                    .getAsJsonArray();
            if (list != null) {
                try {
                    for (int i = 0, size = list.size(); i < size; i++) {
                        JSONObject item = new JSONObject(list.get(i)
                                .toString());
                        BudgetCategory category = new BudgetCategory(item);
                        if (category.getPid() > 0) {
                            category.setChecked(true);
                        }
                        NewBudgetUtil.getInstance()
                                .configBudCategory(this, category);
                        allCategoryList.add(category);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setBudgetInfo(JsonElement budgetJson) {
        selectedList.clear();
        figure = 0D;
        if (budgetJson != null) {
            try {
                JSONObject json = new JSONObject(budgetJson.toString());
                String detailStr = json.optString("detail");
                JSONArray detail = new JSONArray(detailStr);
                if (detail != null) {
                    String money = detail.optString(0);
                    if (money != null) {
                        figure = Double.parseDouble(money);
                    }
                    List<BudgetCategory> parentKey = new ArrayList<>();
                    JSONArray titles = detail.optJSONArray(2);
                    if (titles != null) {
                        for (int i = 0; i < titles.length(); i++) {
                            String title = titles.optString(i);
                            BudgetCategory p = NewBudgetUtil.getInstance()
                                    .getCategoryByTitle(allCategoryList, title);
                            parentKey.add(p);
                        }
                    }

                    List<BudgetCategory> categories = new ArrayList<>();
                    JSONArray items = detail.optJSONArray(3);
                    if (items != null) {
                        for (int i = 0; i < items.length(); i++) {
                            String itemJson = items.optString(i);
                            BudgetCategory category = new BudgetCategory(new JSONObject(itemJson));
                            categories.add(NewBudgetUtil.getInstance()
                                    .configBudCategory(this, category));
                            if (category.getTitle()
                                    .equals(BudgetCategory.CPM_TITLE)) {
                                postLoadCpmRunnable(category.getMoney());
                            }
                        }
                    }

                    for (BudgetCategory p : parentKey) {
                        List<BudgetCategory> childList = groupCategoryList.get(p.getId());
                        if (childList == null) {
                            childList = new ArrayList<>();
                            groupCategoryList.put(p.getId(), childList);
                        }
                        for (BudgetCategory child : categories) {
                            if (child.getPid() == p.getId()) {
                                childList.add(child);
                            }
                        }
                    }

                    for (int i = 0, size = groupCategoryList.size(); i < size; i++) {
                        long key = groupCategoryList.keyAt(i);
                        List<BudgetCategory> childList = groupCategoryList.get(key);
                        for (int j = 0; j < childList.size(); j++) {
                            BudgetCategoryListWrapper wrapper = new BudgetCategoryListWrapper();
                            wrapper.setBudgetCategory(childList.get(j));
                            wrapper.setShowHeader(j == 0);
                            wrapper.setCollectPosition(j);
                            wrapper.setPid(key);
                            selectedList.add(wrapper);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void refresh() {
        budgetAdapter.setFigure(figure);
        budgetAdapter.notifyDataSetChanged();
    }

    private void postLoadCpmRunnable(double money) {
        handler.removeCallbacks(cpmRunnable);
        cpmRunnable.setMoney(money);
        handler.postDelayed(cpmRunnable, 500);
    }

    private void loadCpmList(double budget) {
        CommonUtil.unSubscribeSubs(cpmSub);
        cpmSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Work>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Work>> listHljHttpData) {
                        cpmWorks.clear();
                        cpmWorks.addAll(listHljHttpData.getData());
                        budgetAdapter.notifyCpm();
                    }
                })
                .setDataNullable(true)
                .build();
        ToolsApi.getBudgetCpmList(budget)
                .subscribe(cpmSub);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            figure = intent.getDoubleExtra(ARG_FIGURE, 0D);
            ArrayList<BudgetCategory> selectedCategory = intent.getParcelableArrayListExtra(
                    ARG_SELECTED_CATEGORY);
            resetFigureKind(selectedCategory);
            for (BudgetCategory sb : selectedCategory) {
                if (sb.getTitle()
                        .equals(BudgetCategory.CPM_TITLE)) {
                    postLoadCpmRunnable(sb.getMoney());
                }
            }
            configSelectList(selectedCategory);
            budgetAdapter.notifyDataSetChanged();
        }
    }

    private void resetFigureKind(List<BudgetCategory> selectedCategory) {
        int kind = NewBudgetUtil.getInstance()
                .getKind(figure, selectedCategory);
        NewBudgetUtil.getInstance()
                .computeMoney(kind, figure, selectedCategory);
    }

    private void configSelectList(
            List<BudgetCategory> categoryList) {
        groupCategoryList.clear();
        selectedList.clear();

        for (BudgetCategory p : categoryList) {
            if (p.getPid() == 0) {
                continue;
            }
            List<BudgetCategory> childList = groupCategoryList.get(p.getPid());
            if (childList == null) {
                childList = new ArrayList<>();
                groupCategoryList.put(p.getPid(), childList);
            }
            childList.add(p);
        }

        for (int i = 0, size = groupCategoryList.size(); i < size; i++) {
            long key = groupCategoryList.keyAt(i);
            List<BudgetCategory> childList = groupCategoryList.get(key);
            for (int j = 0; j < childList.size(); j++) {
                BudgetCategoryListWrapper wrapper = new BudgetCategoryListWrapper();
                wrapper.setBudgetCategory(childList.get(j));
                wrapper.setShowHeader(j == 0);
                wrapper.setCollectPosition(j);
                wrapper.setPid(key);
                selectedList.add(wrapper);
            }
        }
    }

    public void onBackPressed(View view) {
        super.onBackPressed();
    }

    @OnClick(R.id.btn_share_w)
    void onShare() {
        if (shareUtil == null) {
            return;
        }
        ShareDialogUtil.onCommonShare(this, shareUtil);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        SaveRunnable saveRunnable = new SaveRunnable(groupCategoryList, figure);
        handler.post(saveRunnable);
        CommonUtil.unSubscribeSubs(loadSub, rxBusSubscription);
    }

    @Override
    public void onModifyCategory() {
        Intent intent = new Intent(this, NewWeddingBudgetCategoryActivity.class);
        intent.putExtra(NewWeddingBudgetCategoryActivity.ARG_FIGURE, figure);
        intent.putParcelableArrayListExtra(NewWeddingBudgetCategoryActivity.ARG_SELECTED_CATEGORY,
                getSelectCategoryList());
        startActivityForResult(intent, ARG_CATEGORY_RESULT_CODE);
    }

    @Override
    public void onRevertBudget() {
        Intent intent = new Intent(this, NewWeddingBudgetFigureActivity.class);
        intent.putParcelableArrayListExtra(NewWeddingBudgetFigureActivity.ARG_SELECT_LIST,
                getSelectCategoryList());
        startActivityForResult(intent, ARG_FIGURE_RESULT_CODE);
    }

    @Override
    public void onCalculateFigure(double figure) {
        this.figure = figure;
    }

    private ArrayList<BudgetCategory> getSelectCategoryList() {
        ArrayList<BudgetCategory> categories = new ArrayList<>();
        for (BudgetCategoryListWrapper wrapper : selectedList) {
            categories.add(wrapper.getBudgetCategory());
        }
        return categories;
    }

    @Override
    public void onLoadCpm(double money) {
        postLoadCpmRunnable(money);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ARG_FIGURE_RESULT_CODE) {
                onFigureResultOk(data);
            } else if (requestCode == ARG_CATEGORY_RESULT_CODE) {
                onCategoryResultOk(data);
            }
        }
    }

    private void onFigureResultOk(Intent data) {
        figure = data.getDoubleExtra(ARG_FIGURE, 0D);
        ArrayList<BudgetCategory> categories = new ArrayList<>();
        for (BudgetCategoryListWrapper wrapper : selectedList) {
            categories.add(wrapper.getBudgetCategory());
        }
        resetFigureKind(categories);
        refresh();
    }

    private void onCategoryResultOk(Intent data) {
        ArrayList<BudgetCategory> categories = data.getParcelableArrayListExtra(
                ARG_SELECTED_CATEGORY);
        resetFigureKind(categories);
        configSelectList(categories);
        refresh();
    }

    class CpmRunnable implements Runnable {

        double money;

        public void setMoney(double money) {
            this.money = money;
        }

        @Override
        public void run() {
            loadCpmList(money);
        }
    }

    class SaveRunnable implements Runnable {

        private LongSparseArray<List<BudgetCategory>> groupCategoryList;
        private double figure;

        public SaveRunnable(
                LongSparseArray<List<BudgetCategory>> groupCategoryList, double figure) {
            this.groupCategoryList = groupCategoryList;
            this.figure = figure;
        }

        @Override
        public void run() {
            saveBudget();
        }

        private void saveBudget() {
            try {
                if (groupCategoryList.size() <= 0) {
                    return;
                }
                JSONObject param = new JSONObject();
                JSONArray detail = new JSONArray();
                detail.put(CommonUtil.formatDouble2String(figure));
                JSONArray money = new JSONArray();
                JSONArray title = new JSONArray();
                JSONArray items = new JSONArray();
                for (int i = 0; i < groupCategoryList.size(); i++) {
                    long key = groupCategoryList.keyAt(i);
                    List<BudgetCategory> value = groupCategoryList.get(key);
                    String moneyD = NewBudgetUtil.getInstance()
                            .getComputeMoney(value);
                    money.put(moneyD);
                    BudgetCategory p = NewBudgetUtil.getInstance()
                            .getCategoryById(allCategoryList, key);
                    if (p != null) {
                        title.put(p.getTitle());
                    }
                    for (BudgetCategory category : value) {
                        if (category.getMoney() <= 0) {
                            continue;
                        }
                        JSONObject item = new JSONObject();
                        item.put("id", category.getId());
                        item.put("title", category.getTitle());
                        item.put("checked", category.isChecked());
                        item.put("icon", category.getIcon());
                        item.put("budgetMoney", category.getMoney());
                        item.put("pid", category.getPid());
                        items.put(item);
                    }
                }
                detail.put(money)
                        .put(title)
                        .put(items);
                param.put("pay_money", CommonUtil.formatDouble2String(figure));
                param.put("detail", detail.toString());
                new NewHttpPostTask(getApplicationContext(), new OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {
                        User user = Session.getInstance()
                                .getCurrentUser(getApplicationContext());
                        if (user == null || user.getId() == 0) {
                            return;
                        }
                        getApplicationContext().getSharedPreferences(Constants.PREF_FILE,
                                Context.MODE_PRIVATE)
                                .edit()
                                .putBoolean(HljCommon.SharedPreferencesNames.WEDDING_BUDGET +
                                                user.getId(),
                                        true)
                                .apply();
                    }

                    @Override
                    public void onRequestFailed(Object obj) {
                    }
                }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_EDIT_BUDGET),
                        param.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class ResultZip {
        JsonElement shareInfo;
        JsonElement budgetInfo;
        JsonElement budgetCategory;
    }
}
