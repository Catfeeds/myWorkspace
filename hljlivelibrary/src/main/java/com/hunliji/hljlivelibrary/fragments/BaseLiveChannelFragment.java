package com.hunliji.hljlivelibrary.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.live.LiveChannel;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljlivelibrary.activities.LiveChannelActivity;
import com.hunliji.hljlivelibrary.adapters.StickMessageAdapter;
import com.hunliji.hljlivelibrary.models.LiveMessage;
import com.hunliji.hljlivelibrary.models.LiveRxEvent;
import com.hunliji.hljlivelibrary.models.wrappers.LiveSocketData;
import com.hunliji.hljlivelibrary.models.wrappers.LiveSocketMessage;
import com.hunliji.hljlivelibrary.utils.LiveVoicePlayUtil;
import com.hunliji.hljlivelibrary.widget.LiveStatusViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;

/**
 * Created by luohanlin on 2017/11/24.
 */

public abstract class BaseLiveChannelFragment extends RefreshFragment {

    public static final String ARG_CHANNEL = "channel";
    LiveChannel channel;
    LiveChannelMessageFragment liveMessageFragment;
    LiveStatusViewHolder statusViewHolder;
    private Subscription liveEventSub;
    List<LiveMessage> stickMessages;
    private StickMessageAdapter stickMessageAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValues();
    }

    @Override
    public void onResume() {
        super.onResume();
        initLiveEventSub();
    }

    @Override
    public void onPause() {
        super.onPause();
        //停止语音播放
        LiveVoicePlayUtil.getInstance()
                .onStop();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSocketConnect();
    }

    private void initSocketConnect() {
        LiveChannelActivity activity = getLiveChannelActivity();
        if (activity != null) {
            getLiveChannelActivity().initSocketAndSendEvents();
        }
    }

    protected void setStickMessageLayout(List<LiveMessage> newStickMessages) {
        if (stickMessages == null) {
            stickMessages = new ArrayList<>();
        }
        this.stickMessages.clear();
        this.stickMessages.addAll(newStickMessages);

        Collections.sort(stickMessages, new Comparator<LiveMessage>() {
            @Override
            public int compare(LiveMessage o1, LiveMessage o2) {
                if (o1.getLiveContent()
                        .getVideoMedia() != null && o2.getLiveContent()
                        .getVideoMedia() == null) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        RecyclerView recyclerView = getStickRecyclerView();
        if (recyclerView == null) {
            return;
        }
        if (CommonUtil.isCollectionEmpty(this.stickMessages)) {
            recyclerView.setVisibility(View.GONE);
        } else if (stickMessageAdapter == null) {
            stickMessageAdapter = new StickMessageAdapter(getContext(), this.stickMessages);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), HORIZONTAL, false));
            recyclerView.setFocusable(false);
            recyclerView.setAdapter(stickMessageAdapter);
        } else {
            stickMessageAdapter.notifyDataSetChanged();
        }
    }

    protected void addStickMessage(List<LiveMessage> addStickMessage) {
        ArrayList<LiveMessage> newStickList = new ArrayList<>();
        for (LiveMessage message : addStickMessage) {
            if (message.isDeleted()) {
                // 删除已置顶消息
                for (LiveMessage m : stickMessages) {
                    if (m.getId() == message.getId()) {
                        stickMessages.remove(m);
                        break;
                    }
                }
            } else {
                newStickList.add(message);
            }
        }
        if (!CommonUtil.isCollectionEmpty(this.stickMessages)) {
            newStickList.addAll(this.stickMessages);
        }

        setStickMessageLayout(newStickList);
    }

    protected abstract void initViews();


    protected abstract void setDanmakuViews(List<LiveMessage> liveMessages);

    protected abstract RecyclerView getStickRecyclerView();

    public abstract void toggleDanmaku(boolean visible);

    public abstract void backRoleRoom();

    public abstract void onShopping();


    @Override
    public void refresh(Object... params) {

    }

    void initValues() {
        if (getArguments() != null) {
            channel = getArguments().getParcelable(ARG_CHANNEL);
        }
    }

    void updateMerchantInfo(Merchant merchant) {
        LiveChannelActivity activity = getLiveChannelActivity();
        if (activity != null) {
            getLiveChannelActivity().updateMerchantInfo(merchant);
        }
    }

    void updateChannelInfo(LiveChannel liveChannel) {
        LiveChannelActivity activity = getLiveChannelActivity();
        if (activity != null) {
            setStatusLayout(liveChannel);
            activity.updateChannelInfo(liveChannel);
        }
    }

    void onLiveRxEvent(LiveRxEvent liveRxEvent) {
        if (liveRxEvent.getChannelId() != channel.getId()) {
            return;
        }
        switch (liveRxEvent.getType()) {
            case NEW_MESSAGE:
                LiveSocketData data = (LiveSocketData) liveRxEvent.getObject();
                if (data == null) {
                    return;
                }
                LiveSocketMessage socketMessage = data.getMessage();
                List<LiveMessage> stickMessages = new ArrayList<>();
                List<LiveMessage> allLiveMessages = socketMessage.getLiveMessages();
                if (!CommonUtil.isCollectionEmpty(allLiveMessages)) {
                    for (LiveMessage message : allLiveMessages) {
                        if (message.isStick()) {
                            stickMessages.add(message);
                        }
                    }

                }
                if (socketMessage.isHistory()) {
                    // 历史消息，刷新全部置顶消息
                    setStickMessageLayout(stickMessages);
                } else {
                    // 新增置顶消息
                    addStickMessage(stickMessages);
                }
                setDanmakuViews(socketMessage.getChatMessages());
                break;
            case CHANNEL_UPDATE:
                // 更新直播信息
                updateChannelInfo((LiveChannel) liveRxEvent.getObject());
                break;
            case MERCHANT_UPDATE:
                // 更新商家信息
                updateMerchantInfo((Merchant) liveRxEvent.getObject());
                break;
        }

    }

    private void initLiveEventSub() {
        CommonUtil.unSubscribeSubs(liveEventSub);
        liveEventSub = RxBus.getDefault()
                .toObservable(LiveRxEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxBusSubscriber<LiveRxEvent>() {
                    @Override
                    protected void onEvent(LiveRxEvent liveRxEvent) {
                        onLiveRxEvent(liveRxEvent);
                    }
                });
    }

    void setStatusLayout(LiveChannel liveChannel) {
        if (statusViewHolder == null) {
            statusViewHolder = new LiveStatusViewHolder(getContext());
        }
    }

    protected LiveChannelActivity getLiveChannelActivity() {
        LiveChannelActivity activity = null;
        if (getActivity() instanceof LiveChannelActivity) {
            activity = (LiveChannelActivity) getActivity();
        }

        return activity;
    }

    protected Merchant getCurrentMerchant() {
        LiveChannelActivity activity = getLiveChannelActivity();
        if (activity != null) {
            return activity.getCurrentMerchant();
        }

        return null;
    }

    @Override
    public void onDestroyView() {
        CommonUtil.unSubscribeSubs(liveEventSub);
        super.onDestroyView();
    }
}
