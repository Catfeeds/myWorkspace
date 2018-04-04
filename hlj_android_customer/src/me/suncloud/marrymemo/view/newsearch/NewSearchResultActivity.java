package me.suncloud.marrymemo.view.newsearch;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.models.search.NewSearchTips;
import com.hunliji.hljcommonlibrary.models.search.TipSearchType;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.HljViewTracker;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.newsearch.NewBaseSearchResultFragment;
import me.suncloud.marrymemo.fragment.newsearch.NewSearchMerchantsResultFragment;
import me.suncloud.marrymemo.fragment.newsearch.NewSearchNoteResultFragment;
import me.suncloud.marrymemo.fragment.newsearch.NewSearchProductsFragment;
import me.suncloud.marrymemo.fragment.newsearch.NewSearchQasResultFragment;
import me.suncloud.marrymemo.fragment.newsearch.NewSearchThreadsResultFragment;
import me.suncloud.marrymemo.fragment.newsearch.NewSearchWeddingCarFragment;
import me.suncloud.marrymemo.fragment.newsearch.NewSearchWorkCaseFragment;
import me.suncloud.marrymemo.widget.ClearableEditText;


/**
 * Created by hua_rong on 2018/1/5
 * 搜索结果页面
 */
@Route(path = RouterPath.IntentPath.Customer.NEW_SEARCH_RESULT_ACTIVITY)
public class NewSearchResultActivity extends HljBaseNoBarActivity {


    public static final String CPM_SOURCE = "search_result";

    @Override
    public String pageTrackTagName() {
        return "搜索结果页";
    }

    @BindView(R.id.et_keyword)
    ClearableEditText etKeyword;
    @BindView(R.id.tv_category)
    TextView tvCategory;
    @BindView(R.id.search_layout)
    LinearLayout searchLayout;
    @BindView(R.id.filter_view_holder)
    LinearLayout filterViewHolder;
    @BindView(R.id.filter_tab_view)
    FrameLayout filterTabView;
    @BindView(R.id.et_keyword2)
    ClearableEditText etKeyword2;
    @BindView(R.id.tv_category2)
    TextView tvCategory2;
    @BindView(R.id.search_layout2)
    LinearLayout searchLayout2;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.iv_arrow)
    ImageView ivArrow;
    @BindView(R.id.iv_arrow2)
    ImageView ivArrow2;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.ll_category)
    LinearLayout llCategory;
    @BindView(R.id.ll_category2)
    LinearLayout llCategory2;

    private InputMethodManager imm;
    private String keyword;
    private HashMap<NewSearchApi.SearchType, NewBaseSearchResultFragment> fragmentMap;
    private SearchMenuPopWindow popWindow;
    private FragmentTransaction ft;
    private List<TipSearchType> tipSearchResults;
    private NewSearchApi.SearchType searchType;
    private HljHttpSubscriber httpSubscriber;
    private int usableHeightPrevious;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initValues();
        if (searchType == null) {
            return;
        }
        initViews();
        onLoad();
        setDefaultStatusBarPadding();
        setKeyboardListener();
        initTracker();
    }

    private void initTracker() {
        HljVTTagger.buildTagger(llCategory)
                .tagName(HljTaggerName.NewSearchResultActivity.BTN_SEARCH_CATEGORY)
                .hitTag();
        HljVTTagger.buildTagger(llCategory2)
                .tagName(HljTaggerName.NewSearchResultActivity.BTN_SEARCH_CATEGORY)
                .hitTag();
    }

    private void initValues() {
        tipSearchResults = new ArrayList<>();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        fragmentMap = new HashMap<>();
        keyword = getIntent().getStringExtra(NewSearchApi.ARG_KEY_WORD);
        searchType = (NewSearchApi.SearchType) getIntent().getSerializableExtra(NewSearchApi
                .ARG_SEARCH_TYPE);
        setCategoryTitle();
    }

    private void setKeyboardListener() {
        coordinatorLayout.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        Rect rect = new Rect();
                        coordinatorLayout.getWindowVisibleDisplayFrame(rect);
                        int usableHeightNow = rect.bottom - rect.top;
                        if (usableHeightNow != usableHeightPrevious) {
                            int usableHeightSansKeyboard = coordinatorLayout.getRootView()
                                    .getHeight();
                            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
                            if (heightDifference <= (usableHeightSansKeyboard / 4)) {
                                //键盘收起
                                setKeyWordWithContent(keyword);
                            }
                            coordinatorLayout.requestLayout();
                            usableHeightPrevious = usableHeightNow;
                        }
                    }
                });
    }

    private void initViews() {
        // 设置Fragment
        setFragments();
        initEtKeyWord(etKeyword);
        initEtKeyWord(etKeyword2);
    }

    private void initEtKeyWord(final ClearableEditText editText) {
        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        // 设置Editor
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH && editText.length() > 0) {
                    //在搜索结果页面修改关键词 不保存 ，只保存修改前的关键词
                    keyword = editText.getText()
                            .toString()
                            .trim();
                    onSearch(editText);
                    onLoad();
                    return false;
                }
                return true;
            }
        });
        editText.setText(keyword);
        editText.setSelection(editText.length());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String content = editText.getText()
                        .toString()
                        .trim();
                setKeyWordWithContent(content);
            }
        });
    }

    private void setKeyWordWithContent(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        if (!etKeyword.getText()
                .toString()
                .trim()
                .equals(content)) {
            etKeyword.setText(content);
            etKeyword.setSelection(etKeyword.length());
        }
        if (!etKeyword2.getText()
                .toString()
                .trim()
                .equals(content)) {
            etKeyword2.setText(content);
            etKeyword2.setSelection(etKeyword2.length());
        }
    }

    private void onLoad() {
        CommonUtil.unSubscribeSubs(httpSubscriber);
        httpSubscriber = HljHttpSubscriber.buildSubscriber(NewSearchResultActivity.this)
                .setOnNextListener(new SubscriberOnNextListener<NewSearchTips>() {
                    @Override
                    public void onNext(NewSearchTips newSearchTips) {
                        tipSearchResults.clear();
                        for (NewSearchApi.SearchType searchType : NewSearchApi.SearchType.values
                                ()) {
                            TipSearchType tipSearchType = new TipSearchType();
                            tipSearchType.setKey(searchType.getType());
                            if (newSearchTips != null) {
                                for (TipSearchType eCommerce : newSearchTips.getECommerces()) {
                                    if (searchType.getType()
                                            .equals(eCommerce.getKey())) {
                                        tipSearchType.setDocCount(eCommerce.getDocCount());
                                    }
                                }
                                for (TipSearchType content : newSearchTips.getContents()) {
                                    if (searchType.getType()
                                            .equals(content.getKey())) {
                                        tipSearchType.setDocCount(content.getDocCount());
                                    }
                                }
                            }
                            tipSearchResults.add(tipSearchType);
                        }
                    }
                })
                .setDataNullable(true)
                .build();
        NewSearchApi.getSearchTips(keyword)
                .subscribe(httpSubscriber);
    }

    @OnClick({R.id.btn_back, R.id.btn_back2})
    void onBackClick() {
        super.onBackPressed();
    }


    @OnClick({R.id.ll_category, R.id.ll_category2})
    public void onCategoryClick(View view) {
        if (searchType == null) {
            return;
        }
        hideKeyboard(null);
        if (CommonUtil.getCollectionSize(tipSearchResults) < 2) {
            return;
        }
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
            return;
        }
        if (popWindow == null) {
            popWindow = new SearchMenuPopWindow(this, tipSearchResults);
            popWindow.setOnSearchItemListener(new SearchMenuPopWindow.OnSearchItemListener() {
                @Override
                public void onSearchItemClick(TipSearchType searchCategory) {
                    if (!TextUtils.isEmpty(searchCategory.getKey()) && !searchCategory.getKey()
                            .equals(searchType.getType())) {
                        searchType = NewSearchApi.getSearchType(searchCategory.getKey());
                        setCategoryTitle();
                        showSelectFragment();
                    }
                    popWindow.dismiss();
                }
            });
        } else {
            popWindow.setSearchResults(tipSearchResults);
        }
        if (searchType != null) {
            popWindow.setSelectCategory(searchType.getType());
        }
        if (view.getId() == R.id.ll_category) {
            popWindow.showAsDropDown(searchLayout);
        } else if (view.getId() == R.id.ll_category2) {
            popWindow.showAsDropDown(searchLayout2);
        }

    }

    /**
     * 请求前 将筛选tab滑上去
     */
    public void setExpanded() {
        appbar.setExpanded(true);
    }

    private void setCategoryTitle() {
        if (searchType != null) {
            tvCategory.setText(searchType.getName());
            tvCategory2.setText(searchType.getName());
        }
    }

    /**
     * 设置搜索结果页面筛选tab
     */
    public void setFilterView(View view) {
        if (view != null) {
            searchLayout2.setVisibility(View.GONE);
            searchLayout.setVisibility(View.VISIBLE);
            filterTabView.setVisibility(View.VISIBLE);
            filterViewHolder.removeAllViews();
            filterViewHolder.addView(view);
        } else {
            searchLayout.setVisibility(View.GONE);
            searchLayout2.setVisibility(View.VISIBLE);
            filterViewHolder.removeAllViews();
            filterTabView.setVisibility(View.GONE);
        }
    }

    private void setFragments() {
        //帖子
        fragmentMap.put(NewSearchApi.SearchType.SEARCH_TYPE_THREAD,
                NewSearchThreadsResultFragment.newInstance(getBundle(NewSearchApi.SearchType
                        .SEARCH_TYPE_THREAD)));
        //婚车
        fragmentMap.put(NewSearchApi.SearchType.SEARCH_TYPE_CAR,
                NewSearchWeddingCarFragment.newInstance(getBundle(NewSearchApi.SearchType
                        .SEARCH_TYPE_CAR)));
        //商家
        fragmentMap.put(NewSearchApi.SearchType.SEARCH_TYPE_MERCHANT,
                NewSearchMerchantsResultFragment.newInstance(getBundle(NewSearchApi.SearchType
                        .SEARCH_TYPE_MERCHANT)));
        //酒店
        fragmentMap.put(NewSearchApi.SearchType.SEARCH_TYPE_HOTEL,
                NewSearchMerchantsResultFragment.newInstance(getBundle(NewSearchApi.SearchType
                        .SEARCH_TYPE_HOTEL)));
        //套餐
        fragmentMap.put(NewSearchApi.SearchType.SEARCH_TYPE_WORK,
                NewSearchWorkCaseFragment.newInstance(getBundle(NewSearchApi.SearchType
                        .SEARCH_TYPE_WORK)));
        //案例
        fragmentMap.put(NewSearchApi.SearchType.SEARCH_TYPE_CASE,
                NewSearchWorkCaseFragment.newInstance(getBundle(NewSearchApi.SearchType
                        .SEARCH_TYPE_CASE)));
        //笔记
        fragmentMap.put(NewSearchApi.SearchType.SEARCH_TYPE_NOTE,
                NewSearchNoteResultFragment.newInstance(getBundle(NewSearchApi.SearchType
                        .SEARCH_TYPE_NOTE)));
        //问答
        fragmentMap.put(NewSearchApi.SearchType.SEARCH_TYPE_QA,
                NewSearchQasResultFragment.newInstance(getBundle(NewSearchApi.SearchType
                        .SEARCH_TYPE_QA)));
        //婚品
        fragmentMap.put(NewSearchApi.SearchType.SEARCH_TYPE_PRODUCT,
                NewSearchProductsFragment.newInstance(getBundle(NewSearchApi.SearchType
                        .SEARCH_TYPE_PRODUCT)));
        showSelectFragment();
    }

    private Bundle getBundle(NewSearchApi.SearchType searchType) {
        Bundle bundle = new Bundle();
        bundle.putString(NewSearchApi.ARG_KEY_WORD, keyword);
        bundle.putSerializable(NewSearchApi.ARG_SEARCH_TYPE, searchType);
        return bundle;
    }

    /**
     * 当前分类对应的fragment
     */
    private void showSelectFragment() {
        if (searchType == null) {
            return;
        }
        FragmentManager manager = getSupportFragmentManager();
        NewBaseSearchResultFragment fragment = (NewBaseSearchResultFragment) manager
                .findFragmentByTag(
                searchType.getName());
        ft = manager.beginTransaction();
        hideFragment();
        if (fragment == null) {
            fragment = fragmentMap.get(searchType);
            if (fragment == null) {
                return;
            }
            fragment.setKeyword(keyword);
            ft.add(R.id.fl_container, fragment, searchType.getName());
        } else {
            fragment.setKeyword(keyword);
            fragment.onSearchMenuRefresh(searchType);
        }
        ft.show(fragment);
        ft.commitAllowingStateLoss();
    }

    private void hideFragment() {
        if (ft != null) {
            for (Map.Entry entry : fragmentMap.entrySet()) {
                NewBaseSearchResultFragment fragment = (NewBaseSearchResultFragment) entry
                        .getValue();
                if (fragment != null && !fragment.isHidden()) {
                    ft.hide(fragment);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
        }
    }

    private void onSearch(ClearableEditText editText) {
        if (searchType == null) {
            return;
        }

        editTracker(keyword);

        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        if (NewSearchUtil.beforeSearchFilter(this, keyword)) {
            //结果页面关键词不保存到历史记录
            return;
        }
        FragmentManager manager = getSupportFragmentManager();
        NewBaseSearchResultFragment fragment = (NewBaseSearchResultFragment) manager
                .findFragmentByTag(
                searchType.getName());
        if (fragment == null) {
            fragment = fragmentMap.get(searchType);
        }
        // 新的关键词搜索时，重置筛选条件
        if (fragment != null) {
            fragment.onKeywordRefresh(keyword);
        }
    }


    /**
     * 输入框统计
     *
     * @param keyword
     */
    private void editTracker(String keyword) {
        HljVTTagger.buildTagger(etKeyword)
                .tagName(HljTaggerName.EDIT_KEYWORD)
                .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_KEYWORD)
                .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_KEYWORD, keyword)
                .hitTag();
        HljViewTracker.fireViewClickEvent(etKeyword);
    }


    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(httpSubscriber);
    }
}
