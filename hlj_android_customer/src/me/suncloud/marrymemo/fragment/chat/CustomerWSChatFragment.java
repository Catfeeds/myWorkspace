package me.suncloud.marrymemo.fragment.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcarlibrary.views.activities.WeddingCarProductDetailActivity;
import com.hunliji.hljchatlibrary.WSRealmHelper;
import com.hunliji.hljchatlibrary.WebSocket;
import com.hunliji.hljchatlibrary.api.ChatApi;
import com.hunliji.hljchatlibrary.views.fragments.WSChatFragment;
import com.hunliji.hljchatlibrary.views.widgets.SpeakView;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSExtObject;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSHints;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSTips;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSTrack;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.models.realm.WSChatAuthor;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljemojilibrary.EmojiTextChaged;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljtrackerlibrary.TrackerHelper;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.merchant.MerchantApi;
import me.suncloud.marrymemo.util.merchant.AppointmentUtil;
import me.suncloud.marrymemo.view.CaseDetailActivity;
import me.suncloud.marrymemo.view.ProductMerchantActivity;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.view.product.ShopProductDetailActivity;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.util.SubscriptionList;

import static android.text.TextUtils.TruncateAt.END;

/**
 * Created by luohanlin on 2017/11/28.
 */

public class CustomerWSChatFragment extends WSChatFragment implements View.OnClickListener {


    private ImageView btnFace;
    private EditText etContent;
    private SpeakView btnSpeak;
    private ImageButton btnVoice;

    private Subscription appointmentSub;
    private Subscription noticeSubscription;
    private Subscriber smartReplySub;
    private String autoMsg;
    private City city;
    private Realm realm;
    private SubscriptionList checkSubs;

    public static CustomerWSChatFragment newInstance(
            User sessionUser,
            String channelId,
            int source,
            WSTrack track,
            long userId,
            String autoMsg,
            City city) {
        Bundle args = new Bundle();
        CustomerWSChatFragment fragment = new CustomerWSChatFragment();
        args.putParcelable(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER, sessionUser);
        args.putString(ARG_CHANNEL_ID, channelId);
        args.putInt(ARG_SOURCE, source);
        args.putParcelable(RouterPath.IntentPath.Customer.BaseWsChat.ARG_WS_TRACK, track);
        args.putLong(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER_ID, userId);
        args.putString(RouterPath.IntentPath.Customer.WsCustomChatActivityPath.ARG_AUTO_MSG,
                autoMsg);
        args.putParcelable(RouterPath.IntentPath.Customer.WsCustomChatActivityPath.ARG_CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initValues() {
        Bundle args = getArguments();
        if (args != null) {
            selfUser = UserSession.getInstance()
                    .getUser(getContext());
            sessionUser = args.getParcelable(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER);
            autoMsg = args.getString(RouterPath.IntentPath.Customer.WsCustomChatActivityPath
                    .ARG_AUTO_MSG);
            channelId = args.getString(ARG_CHANNEL_ID);
            source = args.getInt(ARG_SOURCE, 1);
            wsTrack = args.getParcelable(RouterPath.IntentPath.Customer.BaseWsChat.ARG_WS_TRACK);
            sessionUserId = args.getLong(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER_ID, 0);
            city = args.getParcelable(RouterPath.IntentPath.Customer.WsCustomChatActivityPath
                    .ARG_CITY);
        }
        if (sessionUser != null) {
            sessionUserId = sessionUser.getId();
        }
        super.initValues();
    }

    @Override
    protected void init(User user) {
        super.init(user);
        if (user == null) {
            return;
        }
        initEditView();
        initTracker();

        MerchantUser merchantUser = getMerchantUser();
        if (merchantUser != null && (merchantUser.getShopType() == MerchantUser
                .SHOP_TYPE_PRODUCT)) {
            setProductNoticeView(merchantUser);
        }
        initLoad();
        if (merchantUser != null && city != null) {
            WebSocket.getInstance()
                    .addCity(merchantUser.getMerchantId(), city);
        }
    }

    private void setProductNoticeView(MerchantUser merchantUser) {
        noticeSubscription = MerchantApi.getMerchantNotice((merchantUser).getMerchantId())
                .onErrorReturn(new Func1<Throwable, String>() {
                    @Override
                    public String call(Throwable throwable) {
                        throwable.printStackTrace();
                        return null;
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        initNoticeTopView(s);
                    }
                });
    }


    private void initTracker() {
        TrackerHelper.chatToMerchant(getContext(), sessionUser.getId());
    }

    private void initEditView() {
        View editView = View.inflate(getContext(), R.layout.user_chat_edit_layout, null);
        addEditLayout(editView);
        btnFace = editView.findViewById(R.id.btn_face);
        btnVoice = editView.findViewById(R.id.btn_voice);
        etContent = editView.findViewById(R.id.et_content);
        btnSpeak = editView.findViewById(R.id.btn_speak);
        initSpeakView(btnSpeak);
        editView.findViewById(R.id.btn_image)
                .setOnClickListener(this);
        editView.findViewById(R.id.btn_send)
                .setOnClickListener(this);
        btnFace.setOnClickListener(this);
        btnVoice.setOnClickListener(this);
        //emoji表情输入监听
        etContent.addTextChangedListener(new EmojiTextChaged(etContent,
                CommonUtil.dp2px(getContext(), 20)));
        initContentEditText(etContent);

    }

    public void initNoticeTopView(String noticeStr) {
        if (TextUtils.isEmpty(noticeStr)) {
            shadowView.setVisibility(View.GONE);
            addTopLayout(null);
            return;
        }
        shadowView.setVisibility(View.VISIBLE);
        View shopNoticeView = View.inflate(getContext(),
                R.layout.chat_top_shop_notice_layout,
                null);
        addTopLayout(shopNoticeView);
        final TextView tvContent = shopNoticeView.findViewById(R.id.tv_content);
        final TextView tvExpand = shopNoticeView.findViewById(R.id.tv_expand);
        tvContent.setText(noticeStr);
        tvExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvContent.getMaxLines() == 2) {
                    tvContent.setMaxLines(Integer.MAX_VALUE);
                    tvExpand.setText("点击收起");
                } else {
                    tvContent.setMaxLines(2);
                    tvExpand.setText(R.string.label_all_info);
                }
            }
        });
        tvContent.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        Layout layout = tvContent.getLayout();
                        if (layout == null) {
                            return false;
                        }
                        tvContent.getViewTreeObserver()
                                .removeOnPreDrawListener(this);
                        if (layout.getLineCount() > 2) {
                            tvExpand.setVisibility(View.VISIBLE);
                            tvContent.setMaxLines(2);
                            tvContent.setEllipsize(END);
                            refreshTopLayoutHeight();
                        } else {
                            tvExpand.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });

        chatList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (tvExpand.getVisibility() != View.VISIBLE) {
                    return false;
                }
                if (tvContent.getMaxLines() > 2) {
                    tvContent.setMaxLines(2);
                    tvExpand.setText(R.string.label_all_info);
                }
                return false;
            }
        });
    }

    @Override
    protected void onInitLoadDone(List<WSChat> wsChats) {
        super.onInitLoadDone(wsChats);

        // 自动发送的消息，请求，任务等等
        if (!TextUtils.isEmpty(autoMsg)) {
            sendText(autoMsg);
            autoMsg = null;
        }
        autoSmartReplay();
        filterChatsList(wsChats);
    }

    @Override
    protected void onImmShow() {
        btnFace.setImageResource(R.mipmap.icon_face_gray);
        btnFace.requestLayout();
        super.onImmShow();
    }

    @Override
    protected void onMenuShow() {
        btnFace.setImageResource(R.mipmap.icon_keyboard_round_gray);
        btnFace.requestLayout();
        etContent.requestFocus();
        super.onMenuShow();
    }

    @Override
    protected void onSpeckModeChange() {
        if (etContent.getVisibility() == View.VISIBLE) {
            showMenu = false;
            menuLayout.setVisibility(View.GONE);
            etContent.setVisibility(View.GONE);
            btnSpeak.setVisibility(View.VISIBLE);
            btnFace.setImageResource(R.mipmap.icon_face_gray);
            btnVoice.setImageResource(R.mipmap.icon_keyboard_round_gray);
            if (!immIsShow)
                return;
        } else {
            etContent.setVisibility(View.VISIBLE);
            btnSpeak.setVisibility(View.GONE);
            btnVoice.setImageResource(R.mipmap.icon_audio_gray);
            etContent.requestFocus();
        }
        if (getActivity() != null && getActivity().getCurrentFocus() != null) {
            imm.toggleSoftInputFromWindow(getActivity().getCurrentFocus()
                    .getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void showWork(long id) {
        Intent intent = new Intent(getContext(), WorkActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    protected void showCase(long id) {
        Intent intent = new Intent(getContext(), CaseDetailActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    protected void showCustomMeal(long id) {
        // 不再提供定制套餐的跳转
    }

    @Override
    protected void showProduct(long id) {
        Intent intent = new Intent(getContext(), ShopProductDetailActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    protected void showUser(long id) {
        if (sessionUser == null) {
            return;
        }
        MerchantUser merchantUser = getMerchantUser();
        if (merchantUser != null) {
            gotoMerchant(merchantUser.getMerchantId(), merchantUser.getShopType());
        }
    }

    @Override
    protected void gotoMerchant(long merchantId, int shopType) {
        if (merchantId <= 0) {
            return;
        }
        if (shopType == MerchantUser.SHOP_TYPE_CAR) {
            return;
        }
        Intent intent;
        if (shopType == MerchantUser.SHOP_TYPE_PRODUCT) {
            intent = new Intent(getContext(), ProductMerchantActivity.class);
        } else {
            intent = new Intent(getContext(), MerchantDetailActivity.class);
        }
        intent.putExtra("id", merchantId);
        startActivity(intent);
    }

    @Override
    protected void makeAppointment(long id) {
        if (sessionUser == null) {
            return;
        }
        MerchantUser merchantUser = getMerchantUser();
        if (merchantUser != null) {
            postAppointment(merchantUser.getMerchantId());
        }
    }


    @Override
    protected void showWeddingCar(long id) {
        Intent intent = new Intent(getContext(), WeddingCarProductDetailActivity.class);
        intent.putExtra(WeddingCarProductDetailActivity.ARG_ID, id);
        startActivity(intent);
    }

    @Override
    protected void autoSendSmartReplay(WSChat chat, String text) {
        if (isSmartHintExpired(chat.getCreatedAt())) {
            ToastUtil.showToast(getContext(), "卡片信息已过期", Toast.LENGTH_SHORT);
        } else {
            sendTextAndRead(text);
        }
    }

    private void postAppointment(long merchantId) {
        CommonUtil.unSubscribeSubs(appointmentSub);
        appointmentSub = AppointmentUtil.makeAppointment(getContext(),
                merchantId,
                getMerchantUser() != null ? getMerchantUser().getId() : 0,
                AppointmentUtil.MERCHANT_CHAT,
                new AppointmentUtil.AppointmentCallback() {
                    @Override
                    public void onCallback() {
                        // 成功预约之后直接添加预约成功提示
                        addAppointmentTips();
                    }
                });
    }

    private WSChat initMerchantSendChat(String kind) {
        WSChat chat = new WSChat();
        if (!TextUtils.isEmpty(channelId)) {
            chat.setChannel(channelId);
        }
        chat.setFromId(sessionUser.getId());
        chat.setToId(selfUser.getId());
        chat.setSessionId(sessionUser.getId());
        chat.setSpeaker(getChatAuthor());
        chat.setSpeakerTo(new WSChatAuthor(selfUser));
        chat.setIdStr(sessionUser.getId() + "" + System.currentTimeMillis());
        chat.setKind(kind);
        chat.setCreatedAt(new Date());
        chat.setUserId(selfUser.getId());
        chat.setSource(source);
        return chat;
    }

    private boolean isSmartHintExpired(Date createdAt) {
        if (createdAt == null) {
            return true;
        }
        DateTime dateTime = new DateTime().minusHours(24);
        Date threeDaysAgo = dateTime.toDate();
        if (createdAt.before(threeDaysAgo)) {
            return true;
        }

        return false;
    }

    /**
     * 触发商家智能接待消息
     */
    private void autoSmartReplay() {
        if (sessionUser == null) {
            return;
        }
        checkLastChatByType(WSChat.HINTS,
                WSHints.ACTION_MERCHANT_SMART_REPLY,
                new Action1<WSChat>() {
                    @Override
                    public void call(WSChat chat) {
                        if (chat == null || isSmartHintExpired(chat.getCreatedAt())) {
                            // 先检测本地是否已有商家接待的消息，如果有并且在72个小时之内，则不触发
                            // 否则触发商家发送一条新的接待消息
                            MerchantUser merchantUser = getMerchantUser();
                            if (merchantUser != null) {
                                smartReplySub = new Subscriber() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(Object o) {

                                    }
                                };
                                ChatApi.postForSmartReplay(merchantUser.getId(), selfUser.getId())
                                        .subscribe(smartReplySub);
                            }
                        }
                    }
                });
    }

    /**
     * 检查新消息有没有记录的敏感词，如果有的话，显示"联系方式"敏感提示
     */
    private void filterChatsList(List<WSChat> chats) {
        for (WSChat chat : chats) {
            if (containFilterWords(chat)) {
                addContactWordsTips(chat);
                break;
            }
        }
    }

    @Override
    protected void onFilterMessage(WSChat chat) {
        // 检查新消息有没有记录的敏感词，如果有的话，显示"联系方式"敏感提示
        if (containFilterWords(chat)) {
            addContactWordsTips(chat);
        }
    }

    private boolean containFilterWords(WSChat chat) {
        for (String str : WSTips.ARRAY_CONTACT_FILTER_WORDS) {
            if (!TextUtils.isEmpty(chat.getContent()) && chat.getContent()
                    .contains(str)) {
                return true;
            }
        }
        return false;
    }

    private void addContactWordsTips(final WSChat chatWithWord) {
        if (sessionUser == null) {
            return;
        }
        checkLastChatByType(WSChat.TIPS, WSTips.ACTION_CONTACT_WORDS_TIP, new Action1<WSChat>() {
            @Override
            public void call(WSChat chat) {
                DateTime dateTime = new DateTime(chatWithWord.getCreatedAt()).minusHours(48);
                // 48小时之内只显示一条
                Date twoDaysAgo = dateTime.toDate();
                if (chat == null || chat.getCreatedAt()
                        .before(twoDaysAgo)) {
                    final WSChat newChat = initMerchantSendChat(WSChat.TIPS);
                    newChat.setExtContent(new WSExtObject(new WSTips(WSTips
                            .ACTION_CONTACT_WORDS_TIP,
                            getString(R.string.label_merchant_tips___chat),
                            getString(R.string.label_merchant_chat_first_tips))));
                    WSRealmHelper.saveLocalMessage(newChat);
                    addMessage(newChat);
                }
            }
        });
    }

    private void checkLastChatByType(
            final String kind, final int action, final Action1<WSChat> callback) {
        if (realm == null || realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
        Subscription subscribe = realm.where(WSChat.class)
                .equalTo("userId", selfUser.getId())
                .equalTo("fromId", sessionUser.getId())
                .equalTo("kind", kind)
                .findAllSortedAsync("createdAt", Sort.DESCENDING)
                .asObservable()
                .filter(new Func1<RealmResults<WSChat>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<WSChat> wsChats) {
                        return wsChats.isLoaded();
                    }
                })
                .first()
                .map(new Func1<RealmResults<WSChat>, WSChat>() {
                    @Override
                    public WSChat call(RealmResults<WSChat> wsChats) {
                        if (WSChat.TIPS.equals(kind)) {
                            for (WSChat chat : wsChats) {
                                if (chat.getExtObject(GsonUtil.getGsonInstance())
                                        .getTips()
                                        .getAction() == action) {
                                    return chat;
                                }
                            }
                        } else if (WSChat.HINTS.equals(kind)) {
                            for (WSChat chat : wsChats) {
                                if (chat.getExtObject(GsonUtil.getGsonInstance())
                                        .getHints()
                                        .getAction() == action) {
                                    return chat;
                                }
                            }
                        }
                        return null;
                    }
                })
                .subscribe(new Subscriber<WSChat>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(WSChat chat) {
                        callback.call(chat);
                    }
                });
        if (checkSubs == null) {
            checkSubs = new SubscriptionList();
        }
        checkSubs.add(subscribe);
    }

    private void addAppointmentTips() {
        final WSChat chat = initMerchantSendChat(WSChat.TIPS);
        chat.setExtContent(new WSExtObject(new WSTips(WSTips.ACTION_APPOINTMENT_SUCCESS_TIP,
                getString(R.string.label_success_make_appointment___chat),
                getString(R.string.msg_success_make_appointment___chat))));
        WSRealmHelper.saveLocalMessage(chat);
        addMessage(chat);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_face:
                etContent.setVisibility(View.VISIBLE);
                btnSpeak.setVisibility(View.GONE);
                btnVoice.setImageResource(R.mipmap.icon_audio_gray);
                btnFace.setImageResource(R.mipmap.icon_keyboard_round_gray);
                etContent.requestFocus();
                onShowMenu("face");
                break;
            case R.id.btn_voice:
                onVoiceMode();
                break;
            case R.id.btn_image:
                onAddImage();
                break;
            case R.id.btn_send:
                sendText(etContent.getText()
                        .toString());
                etContent.setText(null);
                break;
        }
    }

    private MerchantUser getMerchantUser() {
        if (sessionUser == null) {
            return null;
        }
        if (sessionUser instanceof MerchantUser) {
            return (MerchantUser) sessionUser;
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        WebSocket.getInstance()
                .removeCity();
        CommonUtil.unSubscribeSubs(appointmentSub, noticeSubscription, smartReplySub, checkSubs);
        if (realm != null) {
            realm.close();
        }
        super.onDestroyView();
    }
}
