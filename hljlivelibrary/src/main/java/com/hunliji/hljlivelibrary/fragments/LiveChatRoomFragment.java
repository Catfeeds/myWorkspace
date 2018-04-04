package com.hunliji.hljlivelibrary.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.live.LiveChannel;
import com.hunliji.hljlivelibrary.HljLive;
import com.hunliji.hljlivelibrary.R;
import com.hunliji.hljlivelibrary.R2;
import com.hunliji.hljlivelibrary.activities.LiveChannelActivity;
import com.hunliji.hljlivelibrary.models.LiveMessage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by luohanlin on 2017/11/30.
 */

public class LiveChatRoomFragment extends Fragment {

    public static final String ARG_CHANNEL = "channel";
    private final static String FRAGMENT_TAG_LIVE_MESSAGE = "tag_live_message_fragment";

    @BindView(R2.id.layout)
    RelativeLayout layout;
    @BindView(R2.id.blank_view)
    View blankView;
    @BindView(R2.id.cb_danmaku)
    CheckBox cbDanmaku;
    @BindView(R2.id.title_layout)
    LinearLayout titleLayout;
    @BindView(R2.id.chat_content_layout)
    FrameLayout chatContentLayout;
    @BindView(R2.id.et_content)
    EditText etContent;
    @BindView(R2.id.tv_sender)
    TextView tvSender;
    @BindView(R2.id.chat_bottom_layout)
    LinearLayout chatBottomLayout;
    @BindView(R2.id.click_view)
    View clickView;
    @BindView(R2.id.message_list_layout)
    RelativeLayout messageListLayout;

    Unbinder unbinder;
    private boolean immIsShow;
    private boolean showMenu;

    private InputMethodManager imm;
    private LiveMessage replyMessage;

    private LiveChannel channel;
    private LiveChannelMessageFragment chatMessageFragment;

    public static LiveChatRoomFragment newInstance(LiveChannel channel) {
        Bundle args = new Bundle();
        LiveChatRoomFragment fragment = new LiveChatRoomFragment();
        args.putParcelable(ARG_CHANNEL, channel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValues();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_chat_room, container, false);

        unbinder = ButterKnife.bind(this, view);
        initViews();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLoad();
    }

    private void initValues() {
        if (getArguments() != null) {
            channel = getArguments().getParcelable(ARG_CHANNEL);
        }
        imm = (InputMethodManager) getLiveChannelActivity().getSystemService(Context
                .INPUT_METHOD_SERVICE);
    }

    private void initViews() {
        layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v,
                    int left,
                    int top,
                    int right,
                    int bottom,
                    int oldLeft,
                    int oldTop,
                    int oldRight,
                    int oldBottom) {
                //判断键盘状态
                if (bottom == 0 || oldBottom == 0 || bottom == oldBottom) {
                    return;
                }
                int height = getActivity().getWindow()
                        .getDecorView()
                        .getHeight();
                immIsShow = (double) (bottom - top) / height < 0.8;
                if (immIsShow) {
                    onImmShow();
                } else {
                    onImmHide();
                }
            }
        });
        clickView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //在有回复状态时取消回复
                if (replyMessage != null) {
                    replyMessage = null;
                    etContent.setHint(R.string.hint_content_edit___live);
                }
                if (immIsShow && getLiveChannelActivity().getCurrentFocus() != null) {
                    immIsShow = false;
                    imm.toggleSoftInputFromWindow(getLiveChannelActivity().getCurrentFocus()
                            .getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
                    if (messageListLayout.getVisibility() == View.GONE) {
                        getLiveChannelActivity().setChatRoom(false, false);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void onReplyMessage(LiveMessage liveMessage) {
        this.replyMessage = liveMessage;
        if (replyMessage != null && replyMessage.getUser() != null) {
            etContent.setHint("@" + replyMessage.getUser()
                    .getName());
        } else {
            etContent.setHint(R.string.hint_content_edit___live);
        }
    }

    public void setListVisible(boolean isListVisible) {
        messageListLayout.setVisibility(isListVisible ? View.VISIBLE : View.GONE);
    }

    public void focusAndShowKeyboard() {
        if (!immIsShow && getActivity().getCurrentFocus() != null) {
            etContent.requestFocus();
            imm.toggleSoftInputFromWindow(getActivity().getCurrentFocus()
                    .getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void onImmShow() {
        showMenu = false;
        chatBottomLayout.requestLayout();
    }

    private void onImmHide() {
        if (showMenu) {
            etContent.requestFocus();
        }
    }

    private void initLoad() {
        setFragment();
    }

    private void setFragment() {
        chatMessageFragment = LiveChannelMessageFragment.newInstance(
                HljLive.ROOM.CHAT,
                channel.getId());
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.chat_content_layout, chatMessageFragment, FRAGMENT_TAG_LIVE_MESSAGE);
        ft.commit();
    }

    public void setFragmentVisible() {
        chatMessageFragment.setUserVisibleHint(true);
    }

    @OnClick(R2.id.tv_sender)
    void onSend() {
        if (channel == null) {
            return;
        }
        if (etContent.getText()
                .length() == 0) {
            return;
        }

        getLiveChannelActivity().sendText(replyMessage,
                etContent.getText()
                        .toString());

        replyMessage = null;
        etContent.setText(null);
        etContent.setHint(R.string.hint_content_edit___live);

        if (messageListLayout.getVisibility() == View.GONE) {
            onBlankView();
        } else {
            hideKeyboard();
        }
    }

    private void hideKeyboard() {
        if (immIsShow && getLiveChannelActivity().getCurrentFocus() != null) {
            imm.toggleSoftInputFromWindow(getLiveChannelActivity().getCurrentFocus()
                    .getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @OnClick(R2.id.blank_view)
    void onBlankView() {
        hideKeyboard();
        getLiveChannelActivity().setChatRoom(false, false);
    }

    @OnClick(R2.id.cb_danmaku)
    void onDanmakuCheck() {
        getLiveChannelActivity().onToggleDanmaku(cbDanmaku.isChecked());
    }

    private LiveChannelActivity getLiveChannelActivity() {
        LiveChannelActivity activity = null;
        if (getActivity() instanceof LiveChannelActivity) {
            activity = (LiveChannelActivity) getActivity();
        }

        return activity;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
