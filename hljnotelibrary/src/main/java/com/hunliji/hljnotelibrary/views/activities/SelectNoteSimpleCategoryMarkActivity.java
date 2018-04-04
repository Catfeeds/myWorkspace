package com.hunliji.hljnotelibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.note.NoteMark;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.ClearableEditText;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.adapters.NotePrimarySimpleCategoryMarkRecyclerAdapter;
import com.hunliji.hljnotelibrary.adapters.NoteSecondarySimpleCategoryMarkRecyclerAdapter;
import com.hunliji.hljnotelibrary.adapters.viewholder.NotePrimarySimpleCategoryMarkViewHolder;
import com.hunliji.hljnotelibrary.adapters.viewholder.NoteSecondarySimpleCategoryMarkViewHolder;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * 发布-选择标签
 * Created by chen_bin on 2017/6/23 0023.
 */
public class SelectNoteSimpleCategoryMarkActivity extends HljBaseNoBarActivity implements
        NotePrimarySimpleCategoryMarkViewHolder.OnSelectPrimarySimpleCategoryMarkListener,
        NoteSecondarySimpleCategoryMarkViewHolder.OnSelectSecondarySimpleCategoryMarkListener {

    @BindView(R2.id.et_keyword)
    ClearableEditText etKeyword;
    @BindView(R2.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R2.id.primary_category_view)
    RecyclerView primaryCategoryView;
    @BindView(R2.id.secondary_category_view)
    RecyclerView secondaryCategoryView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private NotePrimarySimpleCategoryMarkRecyclerAdapter primaryAdapter;
    private NoteSecondarySimpleCategoryMarkRecyclerAdapter secondaryAdapter;
    private StickyRecyclerHeadersDecoration headersDecor;
    private List<CategoryMark> categoryMarks;
    private List<CategoryMark> searchCategoryMarks;
    private boolean isPrimaryCategoryMarkPressed;
    private HljHttpSubscriber initSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_note_simple_category_mark___note);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initValues();
        initViews();
        initLoad();
    }

    private void initValues() {
        categoryMarks = new ArrayList<>();
        searchCategoryMarks = new ArrayList<>();
    }

    private void initViews() {
        etKeyword.setHint(R.string.label_input_keyword___note);
        //一级分类
        primaryCategoryView.setLayoutManager(new LinearLayoutManager(this));
        primaryAdapter = new NotePrimarySimpleCategoryMarkRecyclerAdapter(this, categoryMarks, searchCategoryMarks);
        primaryAdapter.setOnSelectPrimarySimpleCategoryMarkListener(this);
        primaryCategoryView.setAdapter(primaryAdapter);
        //二级分类
        secondaryCategoryView.setLayoutManager(new LinearLayoutManager(this));
        secondaryAdapter = new NoteSecondarySimpleCategoryMarkRecyclerAdapter(this, categoryMarks, searchCategoryMarks);
        secondaryAdapter.setOnSelectSecondarySimpleCategoryMarkListener(this);
        secondaryCategoryView.setAdapter(secondaryAdapter);
        headersDecor = new StickyRecyclerHeadersDecoration(secondaryAdapter);
        secondaryCategoryView.addItemDecoration(headersDecor);
        secondaryAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });
        secondaryCategoryView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) {
                    return;
                }
                if (isPrimaryCategoryMarkPressed) {
                    isPrimaryCategoryMarkPressed = false;
                    return;
                }
                int position = primaryAdapter.getIndex(layoutManager.findFirstVisibleItemPosition());
                primaryCategoryView.scrollToPosition(position);
                primaryAdapter.setSelectedCategoryMark(primaryAdapter.getItem(position));
                primaryAdapter.notifyDataSetChanged();
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initLoad() {
        if (initSub == null || initSub.isUnsubscribed()) {
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CategoryMark>>>() {
                        @Override
                        public void onNext(HljHttpData<List<CategoryMark>> listHljHttpData) {
                            categoryMarks.clear();
                            if (listHljHttpData != null && !listHljHttpData.isEmpty()) {
                                categoryMarks.addAll(listHljHttpData.getData());
                                primaryAdapter.setSelectedCategoryMark(categoryMarks.get(0));
                            }
                            primaryAdapter.notifyDataSetChanged();
                            secondaryAdapter.notifyDataSetChanged();
                        }
                    })
                    .setDataNullable(true)
                    .setProgressBar(progressBar)
                    .setContentView(contentLayout)
                    .build();
            NoteApi.getSimpleCategoryMarksObb("Note", 18, 0)
                    .subscribe(initSub);
        }
    }

    @OnTextChanged(R2.id.et_keyword)
    void afterTextChanged(Editable s) {
        String keyword = s.toString();
        primaryAdapter.setKeyword(keyword);
        secondaryAdapter.setKeyword(keyword);
        if (!TextUtils.isEmpty(keyword)) {
            searchCategoryMarks.clear();
            for (CategoryMark primaryCategoryMark : categoryMarks) {
                List<CategoryMark> secondaryCategoryMarks = primaryCategoryMark.getChildren();
                if (!CommonUtil.isCollectionEmpty(secondaryCategoryMarks)) {
                    boolean b = false;
                    List<CategoryMark> childCategoryMarks = new ArrayList<>();
                    for (CategoryMark secondaryCategoryMark : secondaryCategoryMarks) {
                        Mark mark = secondaryCategoryMark.getMark();
                        if (mark != null && !TextUtils.isEmpty(mark.getName()) && mark.getName()
                                .contains(keyword)) {
                            b = true;
                            CategoryMark childCategoryMark = new CategoryMark();
                            childCategoryMark.setMark(mark);
                            childCategoryMarks.add(childCategoryMark);
                        }
                    }
                    if (b) {
                        CategoryMark searchCategoryMark = new CategoryMark();
                        searchCategoryMark.setMark(primaryCategoryMark.getMark());
                        searchCategoryMark.setChildren(childCategoryMarks);
                        searchCategoryMarks.add(searchCategoryMark);
                    }
                }
            }
            //自定义标签primary分类
            CategoryMark customCategoryMark = new CategoryMark();
            Mark customMark = new Mark();
            customMark.setName(getString(R.string.label_custom_mark___note));
            customCategoryMark.setMark(customMark);
            List<CategoryMark> customChildCategoryMarks = new ArrayList<>();
            //自定义标签secondary分类
            CategoryMark customChildCategoryMark = new CategoryMark();
            Mark customChildMark = new Mark();
            customChildMark.setName(keyword);
            customChildCategoryMark.setMark(customChildMark);
            customChildCategoryMarks.add(customChildCategoryMark);
            customCategoryMark.setChildren(customChildCategoryMarks);
            searchCategoryMarks.add(customCategoryMark);
        }
        primaryAdapter.setSelectedCategoryMark(primaryAdapter.getItem(0));
        primaryAdapter.notifyDataSetChanged();
        secondaryAdapter.notifyDataSetChanged();
        primaryCategoryView.scrollToPosition(0);
        secondaryCategoryView.scrollToPosition(0);
    }

    @Override
    public void onSelectPrimarySimpleCategoryMark(int position, CategoryMark categoryMark) {
        isPrimaryCategoryMarkPressed = true;
        secondaryCategoryView.scrollToPosition(position);
        primaryAdapter.setSelectedCategoryMark(primaryAdapter.getItem(position));
        primaryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSelectSecondarySimpleCategoryMark(Mark mark) {
        NoteMark noteMark = new NoteMark();
        noteMark.setId(mark.getId());
        noteMark.setName(mark.getName());
        Intent intent = getIntent();
        intent.putExtra("note_mark", noteMark);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    @OnClick(R2.id.btn_back)
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSub);
    }
}