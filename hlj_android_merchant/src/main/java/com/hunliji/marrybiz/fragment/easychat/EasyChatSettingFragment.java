package com.hunliji.marrybiz.fragment.easychat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.chat.EasyChatAdapter;
import com.hunliji.marrybiz.api.chat.ChatApi;
import com.hunliji.marrybiz.model.easychat.EasyChat;
import com.hunliji.marrybiz.model.easychat.PendWord;
import com.hunliji.marrybiz.model.easychat.Speech;
import com.hunliji.marrybiz.view.easychat.EasyChatEditActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;

/**
 * Created by wangtao on 2017/8/14.
 */

public class EasyChatSettingFragment extends RefreshFragment implements EasyChatAdapter
        .OnItemEditListener {


    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    Unbinder unbinder;


    private static final int REQUEST_CODE = 0x110;
    private ArrayList<Speech> speeches;
    private String[] titles;
    private HljHttpSubscriber easyChatSubscriber;
    private EasyChatAdapter adapter;
    private View headView;

    public static EasyChatSettingFragment newInstance() {
        Bundle args = new Bundle();
        EasyChatSettingFragment fragment = new EasyChatSettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titles = getResources().getStringArray(R.array.easy_chat_title);
        speeches = new ArrayList<>();
        adapter = new EasyChatAdapter(getContext(), speeches);
        adapter.setOnItemEditListener(this);
        adapter.setTitles(titles);
        headView = View.inflate(getContext(), R.layout.easy_chat_setting_header, null);
        headView.setVisibility(View.INVISIBLE);
        adapter.setHeaderView(headView);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    private void initView() {
        recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onLoad();
            }
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onLoad();
    }

    private void onLoad() {
        CommonUtil.unSubscribeSubs(easyChatSubscriber);
        Observable<EasyChat> easyChatOb = ChatApi.getCheck();
        easyChatSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<EasyChat>() {
                    @Override
                    public void onNext(EasyChat easyChat) {
                        headView.setVisibility(View.VISIBLE);
                        PendWord pendWord = easyChat.getPendWord();
                        speeches.clear();
                        speeches.add(pendWord.getHomeSpeech());
                        speeches.add(pendWord.getPackageSpeech());
                        speeches.add(pendWord.getExampleSpeech());
                        adapter.setDataList(speeches);
                    }
                })
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setProgressBar(progressBar)
                .build();
        easyChatOb.subscribe(easyChatSubscriber);
    }


    @Override
    public void onItemEdit(int position, String speech) {
        if (titles != null && position < titles.length) {
            Intent intent = new Intent(getContext(), EasyChatEditActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("speech", speech);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            onLoad();
        }
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        CommonUtil.unSubscribeSubs(easyChatSubscriber);
        unbinder.unbind();
        super.onDestroyView();
    }
}
