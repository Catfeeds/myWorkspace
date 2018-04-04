package com.hunliji.hljnotelibrary.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.FlowLayout;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.hunliji.hljnotelibrary.models.NoteCategoryMark;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 商家笔记选择标签
 * Created by mo_yu on 2017/3/17.
 */
public class SelectNoteCategoryMarkActivity extends HljBaseActivity {
    @BindView(R2.id.scroll_view)
    ScrollView scrollView;
    @BindView(R2.id.flow_layout)
    FlowLayout flowLayout;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    private ArrayList<NoteCategoryMark> categoryMarks;
    private long markId;
    private HljHttpSubscriber initSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_note_category_mark___note);
        ButterKnife.bind(this);
        initValues();
        initViews();
        initLoad();
    }

    private void initValues() {
        categoryMarks = new ArrayList<>();
        markId = getIntent().getLongExtra("mark_id", 0);
    }

    private void initViews() {
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                initLoad();
            }
        });
    }

    private void initLoad() {
        if (initSub == null || initSub.isUnsubscribed()) {
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<NoteCategoryMark>>>() {
                        @Override
                        public void onNext(
                                HljHttpData<List<NoteCategoryMark>> listHljHttpData) {
                            setData(listHljHttpData);
                        }
                    })
                    .setEmptyView(emptyView)
                    .setProgressBar(progressBar)
                    .setContentView(scrollView)
                    .build();
            NoteApi.getCategoryMarksObb()
                    .subscribe(initSub);
        }
    }

    private void setData(HljHttpData<List<NoteCategoryMark>> listHljHttpData) {
        categoryMarks.clear();
        categoryMarks.addAll(listHljHttpData.getData());
        int count = flowLayout.getChildCount();
        int size = categoryMarks.size();
        if (count > size) {
            flowLayout.removeViews(size, count - size);
        }
        for (int i = 0; i < size; i++) {
            CheckBox checkBox = null;
            final NoteCategoryMark categoryMark = categoryMarks.get(i);
            if (count > i) {
                checkBox = (CheckBox) flowLayout.getChildAt(i);
            }
            if (checkBox == null) {
                View.inflate(this, R.layout.note_category_mark_flow_item___note, flowLayout);
                checkBox = (CheckBox) flowLayout.getChildAt(flowLayout.getChildCount() - 1);
            }
            checkBox.setText(categoryMark.getName());
            checkBox.setChecked(markId > 0 && markId == categoryMark.getMarkId());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setResult(RESULT_OK,
                            getIntent().putExtra("category_mark",
                                    !isChecked ? null : categoryMark));
                    SelectNoteCategoryMarkActivity.super.onBackPressed();
                }
            });
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSub);
    }
}