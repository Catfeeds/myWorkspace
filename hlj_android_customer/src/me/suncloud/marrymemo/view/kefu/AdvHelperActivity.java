package me.suncloud.marrymemo.view.kefu;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayout;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.Location;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.EmptySubscriber;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljkefulibrary.api.KeFuApi;
import com.hunliji.hljkefulibrary.moudles.EMChat;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljkefulibrary.view.EMChatActivityViewHolder;
import com.hunliji.hljkefulibrary.view.activities.BaseEMChatActivity;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.hljsharelibrary.utils.ShareUtil;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.Message;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.CustomCommonApi;
import me.suncloud.marrymemo.model.AdvItem;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.MerchantProperty;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.WeddingConsult;
import me.suncloud.marrymemo.util.PropertiesUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import rx.Subscription;
import rx.internal.util.SubscriptionList;

/**
 * Created by Suncloud on 2016/3/11.顾问
 */
public class AdvHelperActivity extends BaseEMChatActivity implements View.OnClickListener {


    public final static String ARG_IS_CALENDAR = "is_calendar";
    public final static String ARG_IS_HOTEL = "is_hotel";
    public final static String ARG_ADV_FROM = "adv_from";
    public final static String ARG_FIRST_MSG = "first_msg";

    private View advMenuLayout;
    private GridLayout menuLayout;
    private Button btnMenuOk;

    private RelativeLayout merchantLayout;
    private ImageButton btnCall;
    private ImageView imgAvatar;
    private TextView tvNick;
    private TextView tvTime;
    private TextView tvServerNum;
    private TextView tvMerchantCount;

    private boolean isHotel;
    private boolean isCalendar;
    private String from;
    private String firstMsg;
    private User user;

    private ShareUtil shareUtil;
    private String itemsInfoPath;

    private SubscriptionList addBudgetInfoSubscriptions;
    private Subscription initConsultSubscription;
    private Subscription refreshConsultSubscription;
    private Subscription postReplySubscription;

    @Override
    protected EMChatActivityViewHolder initViewHolderBind(Activity activity) {
        return new ActivityViewHolder();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_helper);
        initAdvData();
        initAdvView();
        loginCheck();
    }

    private void initAdvData() {
        user = Session.getInstance()
                .getCurrentUser(this);
        isHotel = getIntent().getBooleanExtra(ARG_IS_HOTEL, isHotel);
        isCalendar = getIntent().getBooleanExtra(ARG_IS_CALENDAR, isCalendar);
        from = getIntent().getStringExtra(ARG_ADV_FROM);
        firstMsg = getIntent().getStringExtra(ARG_FIRST_MSG);
    }


    private void initAdvView() {
        setOkButton(R.drawable.icon_cross_add_primary_42_42);
        setOkButton2(R.drawable.icon_share_primary_44_44);
        findViewById(R.id.history_layout).setOnClickListener(this);
        findViewById(R.id.btn_call).setOnClickListener(this);

        merchantLayout = findViewById(R.id.merchant_layout);
        btnCall = findViewById(R.id.btn_call);
        imgAvatar = findViewById(R.id.img_avatar);
        tvNick = findViewById(R.id.tv_nick);
        tvTime = findViewById(R.id.tv_time);
        tvServerNum = findViewById(R.id.tv_server_num);
        tvMerchantCount = findViewById(R.id.tv_merchant_count);

        advMenuLayout = View.inflate(this, R.layout.adv_helper_menu_layout, null);
        menuLayout = advMenuLayout.findViewById(R.id.menu_layout);
        btnMenuOk = advMenuLayout.findViewById(R.id.btn_ok);
        btnMenuOk.setOnClickListener(this);
        initProperties();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void userIsLogin() {
        initConsultSubscription = CustomCommonApi.getWeddingConsult(from)
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setProgressBar(viewHolder.progressBar)
                        .setOnNextListener(new SubscriberOnNextListener<WeddingConsult>() {
                            @Override
                            public void onNext(WeddingConsult weddingConsult) {
                                shareUtil = new ShareUtil(AdvHelperActivity.this,
                                        weddingConsult.getShareInfo(),
                                        null);
                                itemsInfoPath = weddingConsult.getInfoPath();
                                refreshAdvCheckProperty(weddingConsult.getItems());
                                initAdvInfo(weddingConsult.getSupport());
                                support = weddingConsult.getSupport();
                                currentName = support.getHxIm();
                                initData();
                                initView();
                                loadMessage();
                            }
                        })
                        .setOnErrorListener(new SubscriberOnErrorListener() {
                            @Override
                            public void onError(Object o) {
                                onBackPressed();
                            }
                        })
                        .build());
    }

    @Override
    protected void onLoadDone(List<EMChat> chats, boolean isEnd) {
        super.onLoadDone(chats, isEnd);
        if (isEnd && !isHotel && !isCalendar) {
            EMChat extraChat = new EMChat(Message.createReceiveMessage(Message.Type.TXT),
                    advMenuLayout);
            chats.add(0, extraChat);
            chats.add(0, newTextMessage(getString(R.string.msg_adv_helper_default_text2)));
            chats.add(0,
                    newTextMessage(getString(R.string.msg_adv_helper_default_text1,
                            support.getNick())));
        }
        if (isHotel) {
            if (conversation.getAllMsgCount() == 0) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        String addrStr = Session.getInstance()
                                .getAddress(AdvHelperActivity.this);
                        if (!TextUtils.isEmpty(addrStr)) {
                            sendText(getString(R.string.msg_hotel_adv_helper_address,
                                    addrStr) + getString(R.string.msg_hotel_adv_helper_ask));
                        } else {
                            sendText(getString(R.string.msg_hotel_adv_helper_ask));
                        }
                    }
                });
            }
        } else if (isCalendar) {
            if (!TextUtils.isEmpty(firstMsg)) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        sendText(firstMsg);
                        postReplySubscription = KeFuApi.postReply(support.getHxIm(),
                                "hotel_schedule",
                                null,
                                null)
                                .subscribe(new EmptySubscriber<JsonElement>());
                    }
                });
            }
        }
    }

    private EMChat newTextMessage(String text) {
        Message messge = Message.createReceiveMessage(Message.Type.TXT);
        messge.setBody(new EMTextMessageBody(text));
        return new EMChat(messge);
    }

    private void initAdvInfo(Support support) {
        merchantLayout.setVisibility(View.VISIBLE);
        btnCall.setVisibility(View.VISIBLE);
        int avatarSize = Util.dp2px(AdvHelperActivity.this, 55);
        Glide.with(imgAvatar)
                .load(ImagePath.buildPath(support.getAvatar())
                        .width(avatarSize)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_avatar_primary)
                        .error(R.mipmap.icon_avatar_primary))
                .into(imgAvatar);
        tvNick.setText(support.getNick());
        tvTime.setText(getString(R.string.label_help_server_time, support.getSerTime()));
        tvServerNum.setText(Html.fromHtml(getString(R.string.label_help_server_num,
                support.getSerNum())));
        if (support.getAdviseMerchantCount() > 0) {
            tvMerchantCount.setVisibility(View.VISIBLE);
            tvMerchantCount.setText(Html.fromHtml(getString(R.string.label_help_merchant_count,
                    support.getAdviseMerchantCount())));
        } else {
            tvMerchantCount.setVisibility(View.GONE);
        }
    }

    private void refreshAdvCheckProperty(List<AdvItem> checkedItems) {
        for (int i = 0, size = menuLayout.getChildCount(); i < size; i++) {
            CheckBox itemView = (CheckBox) menuLayout.getChildAt(i);
            MerchantProperty property = (MerchantProperty) itemView.getTag();
            boolean isChecked = false;
            for (AdvItem item : checkedItems) {
                if ((property.getType() == 0 && property.getId()
                        .equals(item.getProperty())) || (property.getType() > 0 && item.getType()
                        == property.getType())) {
                    isChecked = true;
                    break;
                }
            }
            itemView.setEnabled(!isChecked);
        }
    }

    private void initProperties() {
        ArrayList<MerchantProperty> properties = PropertiesUtil.getPropertiesFromFile(this);
        new PropertiesUtil.PropertiesSyncTask(this, null).execute();
        if (properties == null) {
            properties = new ArrayList<>();
        }
        MerchantProperty menuItem = new MerchantProperty(new JSONObject());
        menuItem.setName(getString(R.string.propertie8));
        menuItem.setType(2);
        properties.add(menuItem);
        MerchantProperty menuItem3 = new MerchantProperty(new JSONObject());
        menuItem3.setName(getString(R.string.propertie10));
        menuItem3.setType(4);
        properties.add(menuItem3);
        for (int i = 0, size = properties.size(); i < size; i++) {
            MerchantProperty property = properties.get(i);
            View.inflate(this, R.layout.adv_helper_menu_item, menuLayout);
            CheckBox itemView = (CheckBox) menuLayout.getChildAt(i);
            itemView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    btnMenuOk.setEnabled(false);
                    for (int i = 0, size = menuLayout.getChildCount(); i < size; i++) {
                        View view = menuLayout.getChildAt(i);
                        if (view != null && view instanceof CheckBox) {
                            if (view.isEnabled() && ((CheckBox) view).isChecked()) {
                                btnMenuOk.setEnabled(true);
                                break;
                            }
                        }
                    }
                }
            });
            itemView.setTag(property);
            itemView.setText(property.getName());
        }
    }

    @Override
    public void onOkButton2Click() {
        if (shareUtil == null) {
            return;
        }
        ShareDialogUtil.onCommonShare(this,shareUtil);
        super.onOkButtonClick();
    }

    @Override
    public void onOkButtonClick() {
        onHistory();
        super.onOkButtonClick();
    }

    public void onHistory() {
        if (!TextUtils.isEmpty(itemsInfoPath)) {
            HljWeb.startWebView(this, itemsInfoPath);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.history_layout:
                onHistory();
                break;
            case R.id.btn_call:
                onCall();
                break;
            case R.id.btn_ok:
                ArrayList<MerchantProperty> checkedItems = new ArrayList<>();
                for (int i = 0, size = menuLayout.getChildCount(); i < size; i++) {
                    CheckBox itemView = (CheckBox) menuLayout.getChildAt(i);
                    if (itemView.isChecked()) {
                        checkedItems.add((MerchantProperty) itemView.getTag());
                        itemView.setEnabled(false);
                    }
                }
                btnMenuOk.setEnabled(false);
                if (!checkedItems.isEmpty()) {
                    String addrStr = Session.getInstance()
                            .getAddress(this);
                    StringBuilder text = new StringBuilder();
                    if (!TextUtils.isEmpty(addrStr)) {
                        text.append(getString(R.string.msg_adv_helper_address, addrStr));
                    }
                    City city = Session.getInstance()
                            .getMyCity(this);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("userid", user.getId());
                    jsonObject.addProperty("cid", city == null ? 0 : city.getId());
                    jsonObject.addProperty("phone", user.getPhone());
                    JsonArray array = new JsonArray();
                    StringBuilder itemsStr = new StringBuilder();
                    for (MerchantProperty menuItem : checkedItems) {
                        if (itemsStr.length() > 0) {
                            itemsStr.append("、");
                        }
                        itemsStr.append(menuItem.getName());
                        JsonObject itemObject = new JsonObject();
                        itemObject.addProperty("type", Math.max(menuItem.getType(), 1));
                        itemObject.addProperty("property", menuItem.getId());
                        array.add(itemObject);
                    }
                    text.append(getString(R.string.msg_adv_helper_ask, itemsStr.toString()));
                    jsonObject.add("items", array);
                    if (addBudgetInfoSubscriptions == null) {
                        addBudgetInfoSubscriptions = new SubscriptionList();
                    }
                    addBudgetInfoSubscriptions.add(CustomCommonApi.addBudgetInfo(jsonObject)
                            .subscribe(new EmptySubscriber<JsonElement>()));
                    sendText(text.toString());
                }
                break;
        }
    }


    private class ActivityViewHolder extends EMChatActivityViewHolder {
        @Override
        public void bindView(Activity activity) {
            rcChat = activity.findViewById(R.id.chat_list);
            btnImage = activity.findViewById(R.id.btn_image);
            btnVoice = activity.findViewById(R.id.btn_voice);
            btnSpeak = activity.findViewById(R.id.btn_speak);
            etContent = activity.findViewById(R.id.et_content);
            btnFace = activity.findViewById(R.id.btn_face);
            btnSend = activity.findViewById(R.id.btn_send);
            menuLayout = activity.findViewById(R.id.menu_layout);
            speakEditLayout = activity.findViewById(R.id.speak_edit_layout);
            editBarLayout = activity.findViewById(R.id.edit_bar_layout);
            recordView = activity.findViewById(R.id.record_view);
            progressBar = activity.findViewById(R.id.progress_bar);
            layout = activity.findViewById(R.id.layout);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (initConsultSubscription == null) {
            return;
        }
        if (!CommonUtil.isUnsubscribed(refreshConsultSubscription, initConsultSubscription)) {
            return;
        }
        refreshConsultSubscription = CustomCommonApi.getWeddingConsult(from)
                .subscribe(new EmptySubscriber<WeddingConsult>() {
                    @Override
                    public void onNext(WeddingConsult weddingConsult) {
                        refreshAdvCheckProperty(weddingConsult.getItems());
                    }
                });
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(addBudgetInfoSubscriptions,
                initConsultSubscription,
                refreshConsultSubscription,
                postReplySubscription);
        super.onFinish();
    }
}
