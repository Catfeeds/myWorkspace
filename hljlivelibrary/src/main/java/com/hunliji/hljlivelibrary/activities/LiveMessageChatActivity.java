package com.hunliji.hljlivelibrary.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.live.LiveChannel;
import com.hunliji.hljlivelibrary.HljLive;
import com.hunliji.hljlivelibrary.R;
import com.hunliji.hljlivelibrary.R2;
import com.hunliji.hljlivelibrary.fragments.LiveMessageFragment;
import com.hunliji.hljlivelibrary.websocket.LiveSocket;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LiveMessageChatActivity extends FragmentActivity {

    public static final String ARG_CHANNEL = "channel";
    @BindView(R2.id.blank_view)
    View blankView;
    @BindView(R2.id.cb_danmaku)
    CheckBox cbDanmaku;
    @BindView(R2.id.title_layout)
    LinearLayout titleLayout;
    @BindView(R2.id.content_layout)
    FrameLayout contentLayout;
    @BindView(R2.id.et_content)
    EditText etContent;
    @BindView(R2.id.tv_sender)
    TextView tvSender;

    public static final String ARG_HISTORY_MESSAGE_DATA = "history_message";
    private LiveMessageFragment fragment;
    private final static String FRAGMENT_TAG_LIVE_MESSAGE = "tag_live_message_fragment";
    private LiveChannel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_message_chat);
        ButterKnife.bind(this);

        initValues();
        initViews();
        initLoad();
    }

    private void initValues() {
        channel = getIntent().getParcelableExtra(ARG_CHANNEL);
    }

    private void initViews() {
        fragment = LiveMessageFragment.newInstance(HljLive.ROOM.CHAT, channel.getId());
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.content_layout, fragment, FRAGMENT_TAG_LIVE_MESSAGE);
        ft.commit();
    }

    private void initLoad() {
    }

    @OnClick(R2.id.blank_view)
    void onBlankView() {
        onBackPressed();
    }
}
