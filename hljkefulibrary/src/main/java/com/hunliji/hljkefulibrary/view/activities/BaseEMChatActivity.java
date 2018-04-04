package com.hunliji.hljkefulibrary.view.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.MenuEditLayout;
import com.hunliji.hljcommonlibrary.views.widgets.SpeakEditLayout;
import com.hunliji.hljcommonlibrary.views.widgets.SpeakRecordView;
import com.hunliji.hljemojilibrary.EmojiTextChaged;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljemojilibrary.widgets.EmojiMenuLayout;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljkefulibrary.HljKeFu;
import com.hunliji.hljkefulibrary.R;
import com.hunliji.hljkefulibrary.adapters.EMChatAdapter;
import com.hunliji.hljkefulibrary.adapters.viewholders.EMChatMessageBaseViewHolder;
import com.hunliji.hljkefulibrary.api.KeFuApi;
import com.hunliji.hljkefulibrary.moudles.EMChat;
import com.hunliji.hljkefulibrary.moudles.EMTrack;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljkefulibrary.utils.EMVoiceUtil;
import com.hunliji.hljkefulibrary.view.EMChatActivityViewHolder;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.Conversation;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.model.ContentFactory;
import com.hyphenate.helpdesk.model.EvaluationInfo;
import com.hyphenate.helpdesk.model.RobotMenuInfo;
import com.hyphenate.helpdesk.model.ToCustomServiceInfo;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/10/26.
 */

@RuntimePermissions
public abstract class BaseEMChatActivity extends HljBaseActivity implements ChatManager
        .MessageListener, EMChatMessageBaseViewHolder.OnChatClickListener {

    private final int PHOTO_FROM_GALLERY = 1;
    private final int PHOTO_FROM_CAMERA = 2;

    protected EMChatActivityViewHolder viewHolder;

    private User user;
    protected Support support;
    protected EMTrack track;
    protected String currentName;
    private EMChatAdapter adapter;
    private LinearLayoutManager layoutManager;

    private View loadView;
    private boolean isEnd;
    private int headerHeight;

    protected Conversation conversation;

    private InputMethodManager imm;
    private boolean immIsShow;
    private String currentPath;
    private boolean audioPermissionChecked;

    private Subscription loginSubscription;
    private Subscription loadSubscription;


    protected abstract EMChatActivityViewHolder initViewHolderBind(Activity activity);


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        viewHolder = initViewHolderBind(this);
        viewHolder.bindView(this);
    }


    protected void initData() {
        user = UserSession.getInstance()
                .getUser(this);
        adapter = new EMChatAdapter(this,
                support == null ? null : support.getAvatar(),
                user.getAvatar());
        ChatClient.getInstance()
                .chatManager()
                .bindChat(currentName);
        if (TextUtils.isEmpty(currentName)) {
            finish();
        }
        ChatClient.getInstance()
                .chatManager()
                .addMessageListener(BaseEMChatActivity.this);
        conversation = ChatClient.getInstance()
                .chatManager()
                .getConversation(currentName);

    }

    protected void loginCheck() {
        viewHolder.progressBar.setVisibility(View.VISIBLE);
        loginSubscription = HljKeFu.loginObb(this)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        userIsLogin();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        viewHolder.progressBar.setVisibility(View.GONE);
                        DialogUtil.createSingleButtonDialog(BaseEMChatActivity.this,
                                getString(R.string.hint_err_em_un_login2___kefu),
                                null,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onBackPressed();
                                    }
                                })
                                .show();
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
    }

    protected abstract void userIsLogin();

    protected void initView() {
        View headerView = getLayoutInflater().inflate(R.layout.chat_top_load_view___kefu, null);
        loadView = headerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        layoutManager = new LinearLayoutManager(this);
        viewHolder.rcChat.setLayoutManager(layoutManager);
        viewHolder.rcChat.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
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
                if (viewHolder.rcChat.getChildCount() > 0) {
                    if (oldBottom > bottom || !isBottom()) {
                        viewHolder.rcChat.scrollBy(0, oldBottom - bottom);
                    }
                }
            }
        });
        viewHolder.rcChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (immIsShow && getCurrentFocus() != null) {
                    immIsShow = false;
                    imm.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            0,
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });
        viewHolder.rcChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        if (adapter.getItemCount() > 0 && !isEnd && CommonUtil.isUnsubscribed(
                                loginSubscription) && layoutManager
                                .findFirstCompletelyVisibleItemPosition() == 0) {
                            if (headerHeight == 0) {
                                headerHeight = loadView.getMeasuredHeight();
                            }
                            loadMoreMessage(adapter.getItem(1)
                                    .getMessage()
                                    .messageId());
                        }
                        break;
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        adapter.setOnChatClickListener(this);
        adapter.setHeaderView(headerView);
        viewHolder.rcChat.setAdapter(adapter);

        viewHolder.layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
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
                if (bottom == 0 || oldBottom == 0 || bottom == oldBottom) {
                    return;
                }
                int height = getWindow().getDecorView()
                        .getHeight();
                immIsShow = (double) (bottom - top) / height < 0.8;
                if (immIsShow) {
                    viewHolder.speakEditLayout.onImmShow();
                } else {
                    viewHolder.speakEditLayout.onImmHide();
                }

            }
        });
        initEditLayout();
        viewHolder.editBarLayout.setVisibility(View.VISIBLE);
    }

    private boolean isBottom() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) viewHolder.rcChat
                .getLayoutManager();
        int lastItemPosition = layoutManager.getItemCount() - 1;
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        if (lastVisiblePosition >= lastItemPosition - 1) {
            View lastVisibleChild = layoutManager.getChildAt(layoutManager.getChildCount() - 1);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= viewHolder.rcChat.getBottom();
            }
        }
        return false;
    }

    protected void loadMessage() {
        if (conversation == null) {
            return;
        }
        loadSubscription = Observable.just(conversation.getAllMsgCount())
                .flatMap(new Func1<Integer, Observable<JSONObject>>() {
                    @Override
                    public Observable<JSONObject> call(Integer integer) {
                        if (integer > 0 || support == null || !support.isSupportRobot()) {
                            return Observable.just(null);
                        }
                        return KeFuApi.getRobotGreeting();
                    }
                })
                .doOnNext(new Action1<JSONObject>() {
                    @Override
                    public void call(JSONObject jsonObject) {
                        if(jsonObject==null){
                            return;
                        }
                        Message message = Message.createReceiveMessage(Message.Type.TXT);
                        EMTextMessageBody body = new EMTextMessageBody("");
                        message.setAttribute("msgtype", jsonObject);
                        message.setFrom(currentName);
                        message.setBody(body);
                        message.setMessageTime(System.currentTimeMillis());
                        message.setStatus(Message.Status.SUCCESS);
                        message.setMsgId(UUID.randomUUID()
                                .toString());
                        ChatClient.getInstance()
                                .chatManager()
                                .saveMessage(message);
                    }
                })
                .onErrorReturn(new Func1<Throwable, JSONObject>() {
                    @Override
                    public JSONObject call(Throwable throwable) {
                        return null;
                    }
                })
                .concatMap(new Func1<JSONObject, Observable<List<Message>>>() {
                    @Override
                    public Observable<List<Message>> call(JSONObject jsonObject) {
                        return Observable.create(new Observable.OnSubscribe<List<Message>>() {
                            @Override
                            public void call(Subscriber<? super List<Message>> subscriber) {
                                conversation.markAllMessagesAsRead();
                                List<Message> messages = conversation.getAllMessages();
                                subscriber.onNext(messages);
                                subscriber.onCompleted();
                            }
                        });
                    }
                })
                .map(new Func1<List<Message>, List<EMChat>>() {
                    @Override
                    public List<EMChat> call(List<Message> messages) {
                        ArrayList<EMChat> chats = new ArrayList<>();
                        if (messages != null && !messages.isEmpty()) {
                            Collections.sort(messages, new Comparator<Message>() {
                                @Override
                                public int compare(Message lhs, Message rhs) {
                                    return (int) (lhs.messageTime() - rhs.messageTime());
                                }
                            });
                            for (Message message : messages) {
                                chats.add(new EMChat(message));
                            }
                            chats.get(0)
                                    .setShowTime(true);
                        }
                        return chats;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setProgressBar(viewHolder.progressBar)
                        .setDataNullable(true)
                        .setOnNextListener(new SubscriberOnNextListener<List<EMChat>>() {
                            @Override
                            public void onNext(List<EMChat> chats) {
                                isEnd = chats.size() < 20;
                                onLoadDone(chats, isEnd);
                                if (!CommonUtil.isCollectionEmpty(chats)) {
                                    adapter.setChats(chats);
                                    viewHolder.rcChat.scrollToPosition(adapter.getItemCount() - 1);
                                }
                                loadView.setVisibility(isEnd ? View.GONE : View.VISIBLE);
                            }
                        })
                        .build());
    }

    private void loadMoreMessage(final String lastId) {
        if (!CommonUtil.isUnsubscribed(loadSubscription)) {
            return;
        }
        loadView.setVisibility(View.VISIBLE);
        loadSubscription = Observable.create(new Observable.OnSubscribe<List<Message>>() {
            @Override
            public void call(Subscriber<? super List<Message>> subscriber) {
                List<Message> messages = conversation.loadMessages(lastId, 20);
                subscriber.onNext(messages);
                subscriber.onCompleted();
            }
        })
                .map(new Func1<List<Message>, List<EMChat>>() {
                    @Override
                    public List<EMChat> call(List<Message> messages) {
                        ArrayList<EMChat> chats = new ArrayList<>();
                        if (messages != null && !messages.isEmpty()) {
                            Collections.sort(messages, new Comparator<Message>() {
                                @Override
                                public int compare(Message lhs, Message rhs) {
                                    return (int) (lhs.messageTime() - rhs.messageTime());
                                }
                            });
                            for (Message message : messages) {
                                chats.add(new EMChat(message));
                            }
                            chats.get(0)
                                    .setShowTime(true);
                        }
                        return chats;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setDataNullable(true)
                        .setOnNextListener(new SubscriberOnNextListener<List<EMChat>>() {
                            @Override
                            public void onNext(List<EMChat> chats) {
                                isEnd = chats.size() < 20;
                                onLoadDone(chats, isEnd);
                                if (!CommonUtil.isCollectionEmpty(chats)) {
                                    int firstPosition = layoutManager
                                            .findFirstVisibleItemPosition();

                                    int offset = layoutManager.getChildAt(0)
                                            .getTop();
                                    adapter.addTopChats(chats);
                                    layoutManager.scrollToPositionWithOffset(chats.size() +
                                                    firstPosition + 1,
                                            headerHeight + offset);
                                }
                                loadView.setVisibility(isEnd ? View.GONE : View.VISIBLE);
                            }
                        })
                        .build());

    }

    private void initEditLayout() {
        viewHolder.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.etContent.length() > 0) {
                    sendText(viewHolder.etContent.getText()
                            .toString());
                    viewHolder.etContent.setText(null);
                }
            }
        });
        viewHolder.speakEditLayout.setOnBindSpeakViewInterface(new SpeakEditLayout
                .OnBindSpeakViewInterface() {
            @Override
            public ImageButton btnVoice() {
                return viewHolder.btnVoice;
            }

            @Override
            public Button btnSpeak() {
                return viewHolder.btnSpeak;
            }
        });
        viewHolder.speakEditLayout.setOnBindMenuViewInterface(new MenuEditLayout
                .OnBindMenuViewInterface() {
            @Override
            public ViewGroup menuLayout() {
                return viewHolder.menuLayout;
            }

            @Override
            public EditText etContent() {
                return viewHolder.etContent;
            }
        });
        viewHolder.speakEditLayout.initView();

        viewHolder.recordView.setMaxSec(60);
        viewHolder.recordView.setUserName(currentName);
        viewHolder.recordView.setFileCallback(new SpeakRecordView.OnRecordFileCallback() {
            @Override
            public void onDone(String filePath, double duration) {
                sendVoice(filePath, (int) duration);
            }
        });

        viewHolder.recordView.setOnSpeakStatusListener(viewHolder.speakEditLayout);


        EmojiMenuLayout emojiMenuLayout = (EmojiMenuLayout) View.inflate(this,
                R.layout.face_menu_layout___emoji,
                null);
        emojiMenuLayout.setMode(EmojiMenuLayout.FaceMode.EMFace);
        emojiMenuLayout.setOnEmojiItemClickListener(new EmojiMenuLayout.OnEmojiItemClickListener() {
            @Override
            public void onEmojiItemClickListener(String emoji) {
                if (emoji.equals("delete")) {
                    EmojiUtil.deleteTextOrImage(viewHolder.etContent);
                } else {
                    StringBuilder ss = new StringBuilder(emoji);
                    int start = viewHolder.etContent.getSelectionStart();
                    int end = viewHolder.etContent.getSelectionEnd();
                    if (start == end) {
                        viewHolder.etContent.getText()
                                .insert(start, ss);
                    } else {
                        viewHolder.etContent.getText()
                                .replace(start, end, ss);
                    }
                }
            }
        });

        viewHolder.speakEditLayout.addMenu(viewHolder.btnFace.getId(), emojiMenuLayout);
        viewHolder.speakEditLayout.addMenu(viewHolder.btnImage.getId(), null);
        viewHolder.speakEditLayout.addTextWatcherListener(new EmojiTextChaged(viewHolder.etContent,
                CommonUtil.dp2px(this, 20)));

        viewHolder.speakEditLayout.setMenuEditLayoutInterface(new MenuEditLayout
                .MenuEditLayoutInterface() {
            @Override
            public void onImageButtonChecked(ImageButton imageButton, boolean isChecked) {
                if (imageButton.getId() == viewHolder.btnImage.getId()) {
                    if (isChecked) {
                        onAddImage();
                    }
                    return;
                }
                if (isChecked) {
                    imageButton.setImageResource(R.mipmap.icon_keyboard_round_gray);
                    return;
                }
                if (imageButton.getId() == viewHolder.btnVoice.getId()) {
                    imageButton.setImageResource(R.mipmap.icon_audio_gray);
                } else if (imageButton.getId() == viewHolder.btnFace.getId()) {
                    imageButton.setImageResource(R.mipmap.icon_face_gray);
                }
            }

            @Override
            public void hideImm() {
                if (!immIsShow || getCurrentFocus() == null) {
                    return;
                }
                imm.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        0,
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }

            @Override
            public void showImm() {
                if (immIsShow || getCurrentFocus() == null) {
                    return;
                }
                imm.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        0,
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }

            @Override
            public boolean isImmShow() {
                return immIsShow;
            }
        });
        viewHolder.speakEditLayout.setSpeakEditLayoutInterface(new SpeakEditLayout
                .SpeakerEditLayoutInterface() {
            @Override
            public boolean onSpeakTouchEvent(MotionEvent event) {
                if (audioPermissionChecked) {
                    return viewHolder.recordView.onSpeakTouchEvent(event);
                }
                BaseEMChatActivityPermissionsDispatcher.onRecordAudioWithCheck(BaseEMChatActivity
                        .this);
                return audioPermissionChecked && viewHolder.recordView.onSpeakTouchEvent(event);
            }
        });
    }

    protected void onCall() {
        if (support == null || TextUtils.isEmpty(support.getPhone())) {
            return;
        }
        String phone = support.getPhone();
        if (TextUtils.isEmpty(phone)) {
            return;
        }
        long currentTime = HljTimeUtils.getServerCurrentTimeMillis();
        DateTime dateTime = new DateTime(currentTime);
        if (dateTime.getHourOfDay() < 9 || dateTime.getHourOfDay() >= (dateTime.getDayOfWeek() >
                5 ? 21 : 23)) {
            DialogUtil.createSingleButtonDialog(this,
                    "客服工作日：9:00~23:00 非工作日：9:00~21:00，您可以留言说明问题，客服将优先为您处理。",
                    "好的",
                    null)
                    .show();
            return;
        }
        try {
            callUp(Uri.parse("tel:" + phone.trim()));
        } catch (Exception ignored) {
        }
    }

    private void onAddImage() {
        DialogUtil.createAddPhotoDialog(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseEMChatActivityPermissionsDispatcher.onReadPhotosWithCheck(BaseEMChatActivity
                        .this);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseEMChatActivityPermissionsDispatcher.onTakePhotosWithCheck(BaseEMChatActivity
                        .this);
            }
        })
                .show();
    }

    protected void sendText(String text) {
        sendTrack();
        Message message = Message.createTxtSendMessage(text, currentName);
        sendMessage(message);
    }

    private void sendImage(String filePath) {
        sendTrack();
        Message message = Message.createImageSendMessage(filePath, false, currentName);
        sendMessage(message);
    }

    private void sendVoice(String filePath, int sec) {
        sendTrack();
        Message message = Message.createVoiceSendMessage(filePath, sec, currentName);
        sendMessage(message);
    }

    private void sendTrack() {
        if (track == null) {
            return;
        }
        try {
            JSONObject jsonMsgType = new JSONObject();
            jsonMsgType.put("track",
                    new JSONObject(GsonUtil.getGsonInstance()
                            .toJson(track)));
            Message message = Message.createTxtSendMessage("", currentName);
            message.setAttribute("msgtype", jsonMsgType);
            sendMessage(message);
        } catch (JSONException ignored) {
        }
        track = null;
    }

    private void sendMessage(final Message message) {
        setMessageAttribute(message);
        final EMChat chat = new EMChat(message);
        adapter.addChat(chat);
        viewHolder.rcChat.scrollToPosition(adapter.getItemCount() - 1);
        RxBus.getDefault()
                .post(new RxEvent(RxEvent.RxEventType.SEND_EM_MESSAGE, message));
        ChatClient.getInstance()
                .chatManager()
                .sendMessage(message, new Callback() {
                    @Override
                    public void onSuccess() {
                        viewHolder.rcChat.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyChat(chat);
                            }
                        });
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.e("EMChat", "send error:  code:" + i + "--msg:" + s);
                        viewHolder.rcChat.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyChat(chat);
                            }
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });

    }

    public void setMessageAttribute(Message message) {
        JSONObject weiJson = null;
        try {
            weiJson = message.getJSONObjectAttribute("weichat");
        } catch (HyphenateException e) {
        }
        if (weiJson == null) {
            weiJson = new JSONObject();
        }
        try {
            JSONObject visitorJson = new JSONObject();
            visitorJson.put("trueName", user.getName());
            visitorJson.put("phone", user.getPhone());
            visitorJson.put("userNickname", user.getNick());
            if (user instanceof CustomerUser) {
                visitorJson.put("description",
                        user.getId() + "," + ((CustomerUser) user).getHometown());
            }
            weiJson.put("visitor", visitorJson);
            if (support != null) {
                weiJson.put("queueName", support.getHxSkillGroup());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        message.setAttribute("weichat", weiJson);
    }

    protected void onLoadDone(List<EMChat> chats, boolean isEnd) {

    }

    @Override
    public void onMessage(List<Message> msgs) {
        for (Message message : msgs) {
            String username = message.from();
            final List<EMChat> chats = new ArrayList<>();
            if (!TextUtils.isEmpty(username) && username.equals(currentName)) {
                conversation.markMessageAsRead(message.messageId());
                chats.add(new EMChat(message));
            }
            if (!CommonUtil.isCollectionEmpty(chats)) {
                viewHolder.rcChat.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addChats(chats);
                        viewHolder.rcChat.scrollToPosition(adapter.getItemCount() - 1);
                    }
                });
            }
        }

    }

    @Override
    public void onCmdMessage(List<Message> list) {

    }

    @Override
    public void onMessageStatusUpdate() {

    }

    @Override
    public void onMessageSent() {

    }

    @Override
    public void resendMessage(EMChat item) {
        adapter.removeChat(item);
        sendMessage(item.getMessage());
    }

    @Override
    public void onTextCopyClick(final String text) {
        ArrayList<String> list = new ArrayList<>();
        list.add(getString(R.string.label_copy___chat));
        DialogUtil.createListDialog(this, list, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(
                        CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(getString(R.string.app_name),
                        text));
                ToastUtil.showToast(BaseEMChatActivity.this,
                        null,
                        R.string.msg_copy_success___chat);
            }
        })
                .show();
    }

    @Override
    public void onEnquiryCancelClick(EMChat chat) {
        conversation.removeMessage(chat.getMessage()
                .messageId());
        adapter.removeChat(chat);
    }

    @Override
    public void onEnquiryConfirmClick(EMChat chat) {
        try {
            EvaluationInfo evaluationInfo = chat.getEvaluationInfo();
            JSONObject enquiryJson = new JSONObject();
            JSONObject argsObject = new JSONObject();
            argsObject.put("inviteId", evaluationInfo.getInviteId());
            argsObject.put("serviceSessionId", evaluationInfo.getSessionId());
            argsObject.put("detail", chat.getEnquiryDetail());
            argsObject.put("summary", chat.getEnquirySummary());
            enquiryJson.put("ctrlType", "enquiry");
            enquiryJson.put("ctrlArgs", argsObject);
            Message message = Message.createTxtSendMessage("用户评价", currentName);
            message.setAttribute("weichat", enquiryJson);
            sendMessage(message);
        } catch (JSONException ignored) {

        }
        onEnquiryCancelClick(chat);
    }

    @Override
    public void onTrackClick(EMTrack track) {
    }

    @Override
    public void onMerchantClick(Merchant merchant) {

    }

    @Override
    public void onRobotMenuClick(Object menuItem) {
        if (menuItem instanceof RobotMenuInfo.Item) {
            Message message = Message.createTxtSendMessage(((RobotMenuInfo.Item) menuItem)
                            .getName(),
                    currentName);
            message.addContent(ContentFactory.createRobotMenuIdInfo(null)
                    .setMenuId(((RobotMenuInfo.Item) menuItem).getId()));
            sendMessage(message);
        } else if (menuItem instanceof String) {
            Message message = Message.createTxtSendMessage((String) menuItem, currentName);
            sendMessage(message);
        }
    }

    @Override
    public void onTransferToKefuClick(ToCustomServiceInfo toCustomServiceInfo) {
        Message message = Message.createTranferToKefuMessage(currentName, toCustomServiceInfo);
        sendMessage(message);
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    void onRecordAudio() {
        audioPermissionChecked = true;
    }


    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO)
    void onRecordPermissionDenied() {
        ToastUtil.showToast(this, null, R.string.msg_recording_permission___cm);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onReadPhotos() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PHOTO_FROM_GALLERY);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onTakePhotos() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = FileUtil.createImageFile();
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this, getPackageName(), f);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            imageUri = Uri.fromFile(f);
        }
        currentPath = f.getAbsolutePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_FROM_CAMERA);
    }


    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onRationaleCamera(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.msg_permission_r_for_camera___cm));
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onRationaleReadExternal(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.msg_permission_r_for_read_external_storage___cm));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        BaseEMChatActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("currentUrl", currentPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        currentPath = savedInstanceState.getString("currentUrl");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String path = null;
            if (requestCode == PHOTO_FROM_CAMERA) {
                path = currentPath;
                currentPath = null;
            } else if (requestCode == PHOTO_FROM_GALLERY) {
                if (data == null) {
                    return;
                }
                Uri uri = data.getData();
                path = ImageUtil.getImagePathForUri(uri, this);
            }
            if (!TextUtils.isEmpty(path)) {
                sendImage(path);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onFinish() {
        ChatClient.getInstance()
                .chatManager()
                .removeMessageListener(this);
        EMVoiceUtil.getInstance()
                .onStop();
        CommonUtil.unSubscribeSubs(loginSubscription, loadSubscription);
        if (!TextUtils.isEmpty(currentName)) {
            ChatClient.getInstance()
                    .chatManager()
                    .unbindChat();
        }
        super.onFinish();
    }

}
