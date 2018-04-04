package com.hunliji.hljmaplibrary.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljmaplibrary.R;
import com.hunliji.hljmaplibrary.R2;
import com.hunliji.hljmaplibrary.views.adapters.PoiResultAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by luohanlin on 2017/7/24.
 */

public class PoiResultFragment extends RefreshFragment implements PoiSearch.OnPoiSearchListener,
        OnItemClickListener {

    public static final String ARG_CITY = "city";
    public static final String ARG_KEYWORD = "keyword";
    @BindView(R2.id.recycle_view)
    RecyclerView recyclerView;
    @BindView(R2.id.empty_view)
    RelativeLayout emptyView;
    Unbinder unbinder;

    private String cityStr;
    private String keyword;
    private ArrayList<PoiItem> items;
    private PoiResultAdapter adapter;
    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    private OnPoiSelectListener onPoiSelectListener;

    public static PoiResultFragment newInstance(String city, String address) {
        Bundle args = new Bundle();
        args.putString(ARG_CITY, city);
        args.putString(ARG_KEYWORD, address);
        PoiResultFragment fragment = new PoiResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnPoiSelectListener(OnPoiSelectListener onPoiSelectListener) {
        this.onPoiSelectListener = onPoiSelectListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cityStr = getArguments().getString(ARG_CITY);
            keyword = getArguments().getString(ARG_KEYWORD);
        }
        initValues();
    }

    private void initValues() {
        items = new ArrayList<>();
        adapter = new PoiResultAdapter(getContext(), items, this);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_poi_search, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        onSearch();
    }

    private void initViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                try {
                    if (getActivity().getCurrentFocus() != null) {
                        ((InputMethodManager) getActivity().getSystemService
                                (INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                                getActivity().getCurrentFocus()
                                        .getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onSearch() {
        items.clear();
        adapter.notifyDataSetChanged();

        int currentPage = 0;
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query(keyword, "", "");
        query.setCityLimit(false);
        query.setPageSize(20); // 设置每页最多返回多少条poiitem

        query.setPageNum(currentPage); // 设置查第一页

        poiSearch = new PoiSearch(getContext(), query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void refresh(Object... params) {
        cityStr = (String) params[0];
        keyword = (String) params[1];
        if (TextUtils.isEmpty(keyword)) {
            items.clear();
            adapter.notifyDataSetChanged();
        } else {
            onSearch();
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if (!CommonUtil.isCollectionEmpty(poiResult.getPois())) {
            emptyView.setVisibility(View.GONE);
            items.addAll(poiResult.getPois());
            adapter.notifyDataSetChanged();
        } else if (!CommonUtil.isCollectionEmpty(poiResult.getSearchSuggestionCitys())) {
            cityStr = poiResult.getSearchSuggestionCitys()
                    .get(0)
                    .getCityName();
            onSearch();
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
        Log.d("", poiItem.getTitle());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(int position, Object object) {
        PoiItem item = (PoiItem) object;
        if (this.onPoiSelectListener != null) {
            this.onPoiSelectListener.onPoiSelect(item);
        }
    }

    public interface OnPoiSelectListener {
        void onPoiSelect(PoiItem poiItem);
    }
}
