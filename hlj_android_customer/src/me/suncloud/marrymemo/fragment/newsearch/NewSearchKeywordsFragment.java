package me.suncloud.marrymemo.fragment.newsearch;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.search.NewHotKeyWord;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmError;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.realm.NewHistoryKeyWord;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.view.newsearch.NewSearchActivity;
import me.suncloud.marrymemo.widget.FlowLayoutForTextView;
import rx.Subscriber;

/**
 * Created by hua_rong on 2018/1/5
 * 搜索关键词
 */

public class NewSearchKeywordsFragment extends RefreshFragment {

    @BindView(R.id.flow_hot_words)
    FlowLayoutForTextView flowHotWords;
    @BindView(R.id.hot_keywords_layout)
    LinearLayout hotKeywordsLayout;
    @BindView(R.id.flow_histories)
    FlowLayoutForTextView flowHistories;
    @BindView(R.id.history_keywords_layout)
    LinearLayout historyKeywordsLayout;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;

    private Unbinder unbinder;
    private Realm realm;
    private List<NewHistoryKeyWord> historyKeywords;
    private HljHttpSubscriber hotWordsSub;
    private Subscriber<RealmResults<NewHistoryKeyWord>> historySub;
    private Dialog deleteConfirmDlg;
    private static final String ARG_SEARCH_CATEGORY = "search_category";//搜索分类取对应热搜词
    private NewSearchApi.SearchType searchType;
    private List<NewHotKeyWord> newHotKeyWords;
    private OnSearchKeyWordClickListener keyWordClickListener;
    private static final String ARG_HOT_KEY_WORD = "hot_key_word";
    private NewHotKeyWord newHotKeyWord;//输入框热词 与热搜词重复需要过滤掉

    public static NewSearchKeywordsFragment newInstance(
            NewSearchApi.SearchType searchType, NewHotKeyWord newHotKeyWord) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_SEARCH_CATEGORY, searchType);
        bundle.putParcelable(ARG_HOT_KEY_WORD, newHotKeyWord);
        NewSearchKeywordsFragment fragment = new NewSearchKeywordsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValues();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_keywords, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLoad();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次回来这个界面都重新加载最新的历史记录
        loadSearchHistories();
    }

    private void initValues() {
        newHotKeyWords = new ArrayList<>();
        historyKeywords = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        if (getArguments() != null) {
            searchType = (NewSearchApi.SearchType) getArguments().getSerializable(
                    ARG_SEARCH_CATEGORY);
            newHotKeyWord = getArguments().getParcelable(ARG_HOT_KEY_WORD);
        }
    }

    private void initLoad() {
        CommonUtil.unSubscribeSubs(hotWordsSub);
        hotWordsSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<List<NewHotKeyWord>>() {
                    @Override
                    public void onNext(List<NewHotKeyWord> list) {
                        newHotKeyWords.clear();
                        newHotKeyWords.addAll(list);
                        setHotWordsFlow();
                    }
                })
                .build();
        NewSearchApi.getHotSearchWords(searchType, newHotKeyWord)
                .subscribe(hotWordsSub);
    }

    /**
     * 从数据库中加载搜索历史
     */
    private void loadSearchHistories() {
        historySub = new Subscriber<RealmResults<NewHistoryKeyWord>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(RealmResults<NewHistoryKeyWord> searchHistoryWords) {
                historyKeywords.clear();
                if (searchHistoryWords != null && !searchHistoryWords.isEmpty()) {
                    historyKeywords.addAll(searchHistoryWords);
                }
                setHistoryWordsFlow();
                if (!historyKeywords.isEmpty()) {
                    CommonUtil.unSubscribeSubs(historySub);
                }
            }
        };
        try {
            realm.where(NewHistoryKeyWord.class)
                    .findAllSortedAsync("id", Sort.DESCENDING)
                    .asObservable()
                    .subscribe(historySub);
        } catch (RealmError e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示热词
     */
    private void setHotWordsFlow() {
        if (!newHotKeyWords.isEmpty()) {
            hotKeywordsLayout.setVisibility(View.VISIBLE);
            flowHotWords.removeAllViews();
            for (final NewHotKeyWord keyword : newHotKeyWords) {
                View itemView = View.inflate(getContext(),
                        R.layout.new_search_hot_word_layout,
                        null);
                TextView tvKeyword = itemView.findViewById(R.id.tv_name);
                tvKeyword.setHeight(CommonUtil.dp2px(getContext(), 30));
                LinearLayout itemLayout = itemView.findViewById(R.id.item_layout);
                tvKeyword.setText(keyword.getTitle());
                if (keyword.isHot()) {
                    tvKeyword.setTextColor(ContextCompat.getColor(getContext(),
                            R.color.colorPrimary));
                    itemLayout.setBackgroundResource(R.drawable.sp_r2_stroke_primary_solid_white);
                } else {
                    tvKeyword.setTextColor(ContextCompat.getColor(getContext(),
                            R.color.colorBlack2));
                    itemLayout.setBackgroundResource(R.drawable.sp_r2_color_background);
                }


                /**=========================统计====================================**/
                HljVTTagger.buildTagger(itemView)
                        .tagName(HljTaggerName.HOT_KEYWORD)
                        .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_KEYWORD)
                        .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_KEYWORD, keyword.getTitle())
                        .hitTag();
                /**=========================统计====================================**/

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (keyWordClickListener != null) {
                            keyWordClickListener.onSearchKeyWordClick(keyword.getTitle(),
                                    keyword.getCategory());
                        }
                    }
                });
                flowHotWords.addView(itemView);
            }
        } else {
            hotKeywordsLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 显示搜索历史
     */
    private void setHistoryWordsFlow() {
        if (!historyKeywords.isEmpty()) {
            historyKeywordsLayout.setVisibility(View.VISIBLE);
            flowHistories.removeAllViews();
            for (final NewHistoryKeyWord keyword : historyKeywords) {
                TextView tvKeyword = (TextView) View.inflate(getContext(),
                        R.layout.history_keyword_text_view,
                        null);

                String title = keyword.getKeyword();
                String category = keyword.getCategory();
                if (!TextUtils.isEmpty(category)) {
                    title = NewSearchApi.getTitle(title, category);
                }

                /**=========================统计====================================**/
                HljVTTagger.buildTagger(tvKeyword)
                        .tagName(HljTaggerName.HISTORY_KEYWORD)
                        .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_KEYWORD)
                        .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_KEYWORD, title)
                        .hitTag();
                /**=========================统计====================================**/

                tvKeyword.setText(title);
                tvKeyword.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack2));
                tvKeyword.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tvKeyword.setHeight(CommonUtil.dp2px(getContext(), 30));
                tvKeyword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        long merchantId = keyword.getMerchantId();
                        if (merchantId > 0) {
                            //用户点击下拉提示中的店铺推荐，则系统在历史搜索中记录用户点击的店铺全称，点击跳转至对应店铺详情页
                            Intent intent = new Intent(view.getContext(),
                                    MerchantDetailActivity.class);
                            intent.putExtra(MerchantDetailActivity.ARG_ID, merchantId);
                            startActivity(intent);
                            if (getSearchActivity() != null) {
                                getSearchActivity().saveKeywordToHistory(keyword.getKeyword(),
                                        null,
                                        merchantId);
                            }
                            if (getActivity() != null) {
                                getActivity().overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.activity_anim_default);
                            }
                        } else {
                            if (keyWordClickListener != null) {
                                keyWordClickListener.onSearchKeyWordClick(keyword.getKeyword(),
                                        keyword.getCategory());
                            }
                        }
                    }
                });
                flowHistories.addView(tvKeyword);
            }
        } else {
            historyKeywordsLayout.setVisibility(View.GONE);
        }
    }

    private NewSearchActivity getSearchActivity() {
        return (NewSearchActivity) getActivity();
    }

    /**
     * 清空历史记录
     */
    @OnClick(R.id.btn_delete)
    void deleteAllHistory() {
        deleteConfirmDlg = DialogUtil.createDoubleButtonDialog(deleteConfirmDlg,
                getContext(),
                "确定清空搜索历史？",
                "确定",
                "取消",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteConfirmDlg.cancel();
                        if (getSearchActivity() != null) {
                            getSearchActivity().unSubscribeDistinctDelete();
                        }
                        realm.beginTransaction();
                        realm.where(NewHistoryKeyWord.class)
                                .findAll()
                                .deleteAllFromRealm();
                        realm.commitTransaction();
                        // 删除完成后刷新
                        historyKeywords.clear();
                        setHistoryWordsFlow();
                    }
                });
        deleteConfirmDlg.show();
    }

    public void setKeyWordClickListener(OnSearchKeyWordClickListener keyWordClickListener) {
        this.keyWordClickListener = keyWordClickListener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (realm != null) {
            realm.close();
        }
        CommonUtil.unSubscribeSubs(historySub, hotWordsSub);
    }

    @Override
    public void refresh(Object... params) {}
}
