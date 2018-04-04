package com.hunliji.marrybiz.view.easychat;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.chat.EasyChatEditAdapter;
import com.hunliji.marrybiz.api.chat.ChatApi;
import com.hunliji.marrybiz.model.easychat.EditGreetBody;
import com.hunliji.marrybiz.util.TextFaceCountWatcher;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

/**
 * Created by hua_rong on 2017/7/7.
 * 轻松聊编辑
 */

public class EasyChatEditActivity extends HljBaseActivity implements EasyChatEditAdapter
        .OnItemSelectedListener {

    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.tv_text_count)
    TextView tvTextCount;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.ll_view)
    LinearLayout llView;
    private EditGreetBody editGreetBody;
    private HljHttpSubscriber saveSubscriber;
    private String speech;
    private int titlePosition;
    private HljHttpSubscriber subscriber;
    private ArrayList<String> templates;
    private EasyChatEditAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        speech = getIntent().getStringExtra("speech");
        titlePosition = getIntent().getIntExtra("position", 0);
        setContentView(R.layout.activity_easy_chat_edit);
        ButterKnife.bind(this);
        initView();
        onLoad();
        initNetError();
        String title = null;
        switch (titlePosition) {
            case 0:
                title = "店铺主页设置";
                break;
            case 1:
                title = "套餐详情设置";
                break;
            case 2:
                title = "案例详情设置";
                break;
        }
        setTitle(title);
        TextFaceCountWatcher watcher = new TextFaceCountWatcher(this,
                etContent,
                tvTextCount,
                100,
                CommonUtil.dp2px(this, 14));
        watcher.setAfterTextChangedListener(new TextFaceCountWatcher.AfterTextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    String content = s.toString()
                            .trim();
                    if (!TextUtils.isEmpty(content)) {
                        tvTextCount.setText(String.format("%s/100", content.length()));
                        switch (titlePosition) {
                            case 0:
                                editGreetBody.setHomeSpeech(content);
                                break;
                            case 1:
                                editGreetBody.setPackageSpeech(content);
                                break;
                            case 2:
                                editGreetBody.setExampleSpeech(content);
                                break;
                        }
                        btnSave.setEnabled(!content.equals(speech));
                    } else {
                        tvTextCount.setText("0/100");
                    }
                }
            }
        });
        etContent.addTextChangedListener(watcher);
        if (!TextUtils.isEmpty(speech)) {
            etContent.setText(speech);
        }
    }

    private void initNetError() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onLoad();
            }
        });
    }

    private void onLoad() {
        Observable<HljHttpData<ArrayList<String>>> observable = ChatApi.getTemplate();
        subscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<ArrayList<String>>>() {
                    @Override
                    public void onNext(HljHttpData<ArrayList<String>> hljHttpData) {
                        btnSave.setVisibility(View.VISIBLE);
                        if (hljHttpData != null) {
                            ArrayList<String> list = hljHttpData.getData();
                            if (!CommonUtil.isCollectionEmpty(list)) {
                                templates.clear();
                                templates.addAll(list);
                                adapter.setDataList(templates);
                            }
                        }
                    }
                })
                .setContentView(llView)
                .setProgressBar(progressBar)
                .setEmptyView(emptyView)
                .build();
        observable.subscribe(subscriber);
    }

    private void initView() {
        editGreetBody = new EditGreetBody();
        templates = new ArrayList<>();
        adapter = new EasyChatEditAdapter(this, templates);
        adapter.setSpeech(speech);
        adapter.setOnItemSelectedListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


    @OnClick(R.id.btn_save)
    void onBtnSave() {
        Observable<EditGreetBody> observable = ChatApi.postEditGreet(editGreetBody);
        saveSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<EditGreetBody>() {
                    @Override
                    public void onNext(EditGreetBody editGreet) {
                        setResult(RESULT_OK);
                        EasyChatEditActivity.this.finish();
                    }
                })
                .build();
        observable.subscribe(saveSubscriber);
    }


    @Override
    public void onItemSelect(int selectPosition) {
        etContent.setText(templates.get(selectPosition));
        etContent.setSelection(templates.get(selectPosition)
                .length());
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(saveSubscriber, subscriber);
    }
}
