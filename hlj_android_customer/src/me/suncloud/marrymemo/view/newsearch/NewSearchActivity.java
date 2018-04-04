package me.suncloud.marrymemo.view.newsearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.search.NewHotKeyWord;
import com.hunliji.hljcommonlibrary.models.search.TipSearchType;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.HljViewTracker;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.realm.Realm;
import io.realm.RealmResults;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.newsearch.NewSearchFragment;
import me.suncloud.marrymemo.fragment.newsearch.NewSearchKeywordsFragment;
import me.suncloud.marrymemo.fragment.newsearch.OnSearchItemClickListener;
import me.suncloud.marrymemo.fragment.newsearch.OnSearchKeyWordClickListener;
import me.suncloud.marrymemo.model.realm.NewHistoryKeyWord;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.widget.ClearableEditText;
import rx.Subscriber;

/**
 * Created by hua_rong on 2018/1/5
 * 新搜索 预搜索界面
 */

public class NewSearchActivity extends HljBaseNoBarActivity implements OnSearchItemClickListener,
        OnSearchKeyWordClickListener {

    @Override
    public String pageTrackTagName() {
        return "搜索主页";
    }

    @BindView(R.id.et_keyword)
    ClearableEditText etKeyword;

    private Subscriber distinctSub;
    private Subscriber<RealmResults<NewHistoryKeyWord>> deleteLimitSub;
    private Realm realm;
    private NewSearchApi.InputType inputType;//主界面输入框热搜词
    private NewSearchApi.SearchType searchType;//搜索分类 主要是婚车、婚品 酒店二级搜索起始页 只取婚车、婚品 酒店的热搜词
    private static final String TAG_NEW_KEYWORDS_FRAGMENT = "new_keywords_fragment";
    private static final String TAG_NEW_SEARCH_FRAGMENT = "new_search_fragment";
    private NewSearchFragment searchFragment;
    private NewHotKeyWord hotKeyWord;//输入框热搜索词
    public static final String ARG_HOT_KEY_WORD = "hot_key_word";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_search);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initValues();
        initViews();
        initKeywordEditor();
        showFragment(true, null);
    }

    private void initValues() {
        realm = Realm.getDefaultInstance();
        inputType = (NewSearchApi.InputType) getIntent().getSerializableExtra(NewSearchApi
                .ARG_SEARCH_INPUT_TYPE);
        searchType = (NewSearchApi.SearchType) getIntent().getSerializableExtra(NewSearchApi
                .ARG_SEARCH_TYPE);
        hotKeyWord = getIntent().getParcelableExtra(ARG_HOT_KEY_WORD);
    }

    void initViews() {
        if (hotKeyWord != null) {
            etKeyword.setHint(hotKeyWord.getTitle());
        } else {
            if (inputType != null) {
                switch (inputType) {
                    case TYPE_HOME_PAGE:
                        etKeyword.setHint(R.string.hint_search_edit);
                        break;
                    case TYPE_FINDER:
                        etKeyword.setHint(R.string.hint_search_edit);
                        break;
                    case TYPE_SOCIAL_HOME:
                        etKeyword.setHint(R.string.hint_search_community);
                        break;
                    case TYPE_PRODUCT_HOME:
                        etKeyword.setHint(R.string.hint_search_product);
                        break;
                }
            } else {
                etKeyword.setHint(R.string.hint_search_edit);
            }
        }
    }

    /**
     * 键盘编辑的监听
     */
    private void initKeywordEditor() {
        etKeyword.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        etKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    if (searchFragment != null && searchFragment.getPoster() != null) {
                        BannerUtil.bannerJump(NewSearchActivity.this,
                                searchFragment.getPoster(),
                                null);
                    } else {
                        String keyword = etKeyword.getText()
                                .toString();
                        if (!TextUtils.isEmpty(keyword)) {
                            editTracker(keyword);
                            onSearch(keyword);
                            return false;
                        } else if (hotKeyWord != null) {
                            saveInputHotKeyWord();
                            return false;
                        }
                    }
                }
                return true;
            }
        });
    }

    /**
     * 输入框热搜词 搜索
     */
    private void saveInputHotKeyWord() {
        String hotKeyword = hotKeyWord.getTitle();
        if (!TextUtils.isEmpty(hotKeyword)) {
            editTracker(hotKeyword);
            String category = hotKeyWord.getCategory();
            jumpWithSaveToHistory(hotKeyword, category);
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

    @OnTextChanged(R.id.et_keyword)
    void afterTextChanged(Editable s) {
        String keyword = s.toString()
                .trim();
        boolean empty = TextUtils.isEmpty(keyword);
        showFragment(empty, keyword);
    }

    public void showFragment(boolean isKeyWordFragment, String keyword) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        searchFragment = (NewSearchFragment) fragmentManager.findFragmentByTag(
                TAG_NEW_SEARCH_FRAGMENT);
        NewSearchKeywordsFragment keyWordsFragment = (NewSearchKeywordsFragment) fragmentManager
                .findFragmentByTag(
                TAG_NEW_KEYWORDS_FRAGMENT);
        if (!isKeyWordFragment && searchFragment != null && !searchFragment.isHidden()) {
            searchFragment.refresh(keyword);
            return;
        }
        if (keyWordsFragment != null && !keyWordsFragment.isHidden()) {
            ft.hide(keyWordsFragment);
        }
        if (searchFragment != null && !searchFragment.isHidden()) {
            ft.hide(searchFragment);
        }
        if (isKeyWordFragment) {
            if (keyWordsFragment == null) {
                keyWordsFragment = NewSearchKeywordsFragment.newInstance(searchType, hotKeyWord);
                keyWordsFragment.setKeyWordClickListener(this);
                ft.add(R.id.content_layout, keyWordsFragment, TAG_NEW_KEYWORDS_FRAGMENT);
            } else {
                ft.show(keyWordsFragment);
            }
        } else {
            if (searchFragment == null) {
                searchFragment = NewSearchFragment.newInstance(keyword, searchType);
                searchFragment.setOnSearchItemClickListener(this);
                ft.add(R.id.content_layout, searchFragment, TAG_NEW_SEARCH_FRAGMENT);
            } else {
                ft.show(searchFragment);
                searchFragment.refresh(keyword);
            }
        }
        ft.commitAllowingStateLoss();
    }

    private void onSearch(String keyword) {
        //搜索前判断下 是否指定关键词 如电子请帖。。。
        if (NewSearchUtil.beforeSearchFilter(this, keyword)) {
            saveKeywordToHistory(keyword, null, 0);
            return;
        }
        if (searchFragment != null && !searchFragment.isHidden()) {
            ArrayList<Object> objects = searchFragment.getNewSearchTips();
            //如果没有点击下拉提示item，而是点击的键盘搜索 默认点击第一条数据
            if (!CommonUtil.isCollectionEmpty(objects)) {
                Object object = objects.get(0);
                onSearchTipItemJump(object, keyword);
            }
        }
    }

    private void onSearchTipItemJump(Object object, String keyword) {
        if (object != null) {
            if (object instanceof TipSearchType) {
                jumpToSearchResultActivity(object, keyword);
            } else if (object instanceof Merchant) {
                jumpToCpmMerchantDetailActivity(object);
            }
        }
    }

    /**
     * 普通搜索跳到结果页面
     *
     * @param object 点击的object
     */
    public void jumpToSearchResultActivity(Object object, String keyword) {
        if (!TextUtils.isEmpty(keyword)) {
            TipSearchType tipSearchResult = (TipSearchType) object;
            String category = tipSearchResult.getKey();
            NewSearchUtil.performSearchResultJump(this, keyword, category);
            saveKeywordToHistory(keyword, category, 0);
        }
    }

    /**
     * 跳转到cpm商家详情页面
     */
    public void jumpToCpmMerchantDetailActivity(Object object) {
        Merchant cpmMerchant = (Merchant) object;
        long merchantId = cpmMerchant.getId();
        if (merchantId > 0) {
            Intent intent = new Intent(this, MerchantDetailActivity.class);
            intent.putExtra(MerchantDetailActivity.ARG_ID, merchantId);
            startActivity(intent);
            String name = cpmMerchant.getName();
            if (!TextUtils.isEmpty(name)) {
                saveKeywordToHistory(name.replace("<em>", "")
                        .replace("</em>", ""), null, merchantId);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        }
    }


    /**
     * 存储关键字历史
     */
    public void saveKeywordToHistory(
            String keyword, String category, long merchantId) {
        Subscriber[] subs = NewSearchKeywordHistoryUtil.saveKeywordToHistory(realm,
                distinctSub,
                deleteLimitSub,
                category,
                keyword,
                merchantId);
        distinctSub = subs[0];
        deleteLimitSub = subs[1];
    }

    /**
     * 当正在删除时，取消保存
     */
    public void unSubscribeDistinctDelete() {
        CommonUtil.unSubscribeSubs(distinctSub, deleteLimitSub);
    }


    @OnClick(R.id.btn_back)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(distinctSub, deleteLimitSub);
    }

    @Override
    public void onSearchItemClick(String keyword, Object object) {
        onSearchTipItemJump(object, keyword);
    }

    @Override
    public void onSearchKeyWordClick(String keyword, String searchType) {
        jumpWithSaveToHistory(keyword, searchType);
    }

    /**
     * 跳转并且保存到历史记录
     */
    private void jumpWithSaveToHistory(String keyword, String searchType) {
        if (TextUtils.isEmpty(searchType) && NewSearchUtil.beforeSearchFilter(this, keyword)) {
            saveKeywordToHistory(keyword, null, 0);
            return;
        }
        NewSearchUtil.performSearchResultJump(this, keyword, searchType);
        saveKeywordToHistory(keyword, searchType, 0);
    }

}
