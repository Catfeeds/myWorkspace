package com.hunliji.marrybiz.fragment.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljchatlibrary.WSRealmHelper;
import com.hunliji.hljchatlibrary.api.ChatApi;
import com.hunliji.hljchatlibrary.views.fragments.WSChatFragment;
import com.hunliji.hljchatlibrary.views.widgets.SpeakView;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.models.realm.WSChatAuthor;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljemojilibrary.EmojiTextChaged;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.customer.CustomerApi;
import com.hunliji.marrybiz.model.Work;
import com.hunliji.marrybiz.model.chat.FastReply;
import com.hunliji.marrybiz.model.customer.MerchantCustomer;
import com.hunliji.marrybiz.modulehelper.ModuleUtils;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.view.CaseDetailActivity;
import com.hunliji.marrybiz.view.SelectWorkOpuActivity;
import com.hunliji.marrybiz.view.WorkActivity;
import com.hunliji.marrybiz.view.chat.FastReplyActivity;
import com.hunliji.marrybiz.view.customer.CustomerDetailEditActivity;
import com.hunliji.marrybiz.view.product.SelectProductActivity;

import io.realm.Realm;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by wangtao on 2018/1/17.
 */

public class MerchantWSChatFragment extends WSChatFragment implements View.OnClickListener {


    private static final int SELECT_WORK_RESULT = 11;
    private static final int SELECT_PRODUCT_RESULT = 12;
    private static final int CUSTOMER_EDIT_RESULT = 13;
    private static final int SELECT_FAST_REPLAY_RESULT = 14;

    private ImageView btnMenu;
    private ImageView btnFace;
    private EditText etContent;
    private SpeakView btnSpeak;
    private ImageButton btnVoice;
    private Button btnSend;

    private TextView tvWxHint;
    private TextView tvPhoneHint;
    private TextView tvWx;
    private TextView tvPhone;


    private MerchantCustomer merchantCustomer;

    private Subscription loadSubscription;

    private MerchantChatFragmentListener merchantChatFragmentListener;


    public static MerchantWSChatFragment newInstance(
            User sessionUser, String channelId, int source, long userId) {
        Bundle args = new Bundle();
        MerchantWSChatFragment fragment = new MerchantWSChatFragment();
        args.putParcelable(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER, sessionUser);
        args.putString(ARG_CHANNEL_ID, channelId);
        args.putInt(ARG_SOURCE, source);
        args.putLong(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initValues() {
        Bundle args = getArguments();
        if (args != null) {
            sessionUser = args.getParcelable(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER);
            channelId = args.getString(ARG_CHANNEL_ID);
            source = args.getInt(ARG_SOURCE, 1);
            sessionUserId = args.getLong(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER_ID, 0);
        }
        selfUser = UserSession.getInstance()
                .getUser(getContext());
        if (sessionUser != null) {
            sessionUserId = sessionUser.getId();
        }
        if (TextUtils.isEmpty(channelId)) {
            try {
                //未传入channelId 从历史记录中获取
                Realm realm = Realm.getDefaultInstance();
                WSChat wsChat = realm.where(WSChat.class)
                        .equalTo("userId", selfUser.getId())
                        .isNotNull("channel")
                        .beginGroup()
                        .equalTo("fromId", sessionUser.getId())
                        .or()
                        .equalTo("toId", sessionUser.getId())
                        .endGroup()
                        .findFirst();
                if (wsChat != null) {
                    channelId = wsChat.getChannel();
                }
                realm.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.initValues();
    }

    @Override
    protected void init(User user) {
        super.init(user);
        if (user == null) {
            return;
        }
        if (merchantChatFragmentListener != null) {
            merchantChatFragmentListener.onCityChange(getChatAuthor().getCityName());
        }
        initMerchantLoad();
    }

    private void initMerchantLoad() {
        MerchantWSChatFragment.this.merchantCustomer = null;
        if (merchantChatFragmentListener != null) {
            merchantChatFragmentListener.onMerchantCustomerChange(null);
        }
        Observable<Boolean> synMessageObb = Observable.just(channelId)
                .concatMap(new Func1<String, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(String channelId) {
                        if (TextUtils.isEmpty(channelId)) {
                            return Observable.just(true);
                        }
                        return ChatApi.getChannelMessagesObb(new WSChatAuthor(selfUser),
                                getChatAuthor(),
                                channelId);
                    }
                })
                .onErrorReturn(new Func1<Throwable, Boolean>() {
                    @Override
                    public Boolean call(Throwable throwable) {
                        throwable.printStackTrace();
                        return false;
                    }
                });
        Observable<MerchantCustomer> customerInfoObb = CustomerApi.getMerchantCustomerDetail(
                sessionUser.getId())
                .onErrorReturn(new Func1<Throwable, MerchantCustomer>() {
                    @Override
                    public MerchantCustomer call(Throwable throwable) {
                        throwable.printStackTrace();
                        return null;
                    }
                });
        loadSubscription = Observable.zip(synMessageObb,
                customerInfoObb,
                new Func2<Boolean, MerchantCustomer, MerchantCustomer>() {
                    @Override
                    public MerchantCustomer call(
                            Boolean aBoolean, MerchantCustomer merchantCustomer) {
                        return merchantCustomer;
                    }
                })
                .subscribe(new Action1<MerchantCustomer>() {
                    @Override
                    public void call(MerchantCustomer merchantCustomer) {
                        if (merchantCustomer != null) {
                            MerchantWSChatFragment.this.merchantCustomer = merchantCustomer;
                            if (merchantChatFragmentListener != null) {
                                merchantChatFragmentListener.onMerchantCustomerChange(
                                        merchantCustomer);
                            }
                            initCustomerTopView();
                            synChatUser();
                        }
                        initEditView();
                        initLoad();
                    }
                });
    }

    public void initEditView() {
        View editView = View.inflate(getContext(), R.layout.merchant_chat_edit_layout, null);
        addEditLayout(editView);
        btnMenu = editView.findViewById(R.id.btn_menu);
        btnFace = editView.findViewById(R.id.btn_face);
        btnVoice = editView.findViewById(R.id.btn_voice);
        etContent = editView.findViewById(R.id.et_content);
        btnSpeak = editView.findViewById(R.id.btn_speak);
        btnSend = editView.findViewById(R.id.btn_send);
        initSpeakView(btnSpeak);
        btnSend.setOnClickListener(this);
        btnMenu.setOnClickListener(this);
        btnFace.setOnClickListener(this);
        btnVoice.setOnClickListener(this);
        //emoji表情输入监听
        etContent.addTextChangedListener(new EmojiTextChaged(etContent,
                CommonUtil.dp2px(getContext(), 20)));

        //发送按钮状态变化
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    if (btnSend.getVisibility() == View.VISIBLE) {
                        btnMenu.setVisibility(View.VISIBLE);
                        btnSend.setVisibility(View.GONE);
                    }
                } else {
                    if (btnSend.getVisibility() != View.VISIBLE) {
                        btnMenu.setVisibility(View.GONE);
                        btnSend.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        initContentEditText(etContent);

        View addMenuView = View.inflate(getContext(), R.layout.ws_add_menu_layout, null);
        addMenuView.setTag("menu");
        menuLayout.addView(addMenuView);

        GridLayout gridLayout = addMenuView.findViewById(R.id.grid_layout);
        View viewSendImage = addMenuView.findViewById(R.id.send_image);
        View viewSendWork = addMenuView.findViewById(R.id.send_work);
        View viewSendOpu = addMenuView.findViewById(R.id.send_opu);
        View viewSendProduct = addMenuView.findViewById(R.id.send_product);
        View viewSendFastReply = addMenuView.findViewById(R.id.send_fast_reply);
        View viewSendAddress = addMenuView.findViewById(R.id.send_address);
        viewSendImage.setOnClickListener(this);
        viewSendWork.setOnClickListener(this);
        viewSendOpu.setOnClickListener(this);
        viewSendProduct.setOnClickListener(this);
        viewSendFastReply.setOnClickListener(this);
        viewSendAddress.setOnClickListener(this);
        if (selfUser instanceof MerchantUser) {
            MerchantUser merchantUser = (MerchantUser) selfUser;
            switch (merchantUser.getShopType()) {
                case MerchantUser.SHOP_TYPE_PRODUCT:
                    gridLayout.removeView(viewSendOpu);
                    gridLayout.removeView(viewSendWork);
                    gridLayout.removeView(viewSendFastReply);
                    gridLayout.removeView(viewSendAddress);
                    break;
                case MerchantUser.SHOP_TYPE_SERVICE:
                    gridLayout.removeView(viewSendProduct);
                    break;
                case MerchantUser.SHOP_TYPE_CAR:
                    gridLayout.removeView(viewSendProduct);
                    gridLayout.removeView(viewSendOpu);
                    gridLayout.removeView(viewSendWork);
                    gridLayout.removeView(viewSendAddress);
                    break;
                default:
                    gridLayout.removeView(viewSendProduct);
                    gridLayout.removeView(viewSendOpu);
                    gridLayout.removeView(viewSendWork);
                    gridLayout.removeView(viewSendFastReply);
                    gridLayout.removeView(viewSendAddress);
                    break;
            }
        }
    }


    private void synChatUser() {
        try {
            if (merchantCustomer == null) {
                return;
            }
            WSChatAuthor wsChatAuthor = getChatAuthor();
            wsChatAuthor.setRemarkName(merchantCustomer.getUserName());
            if (chatFragmentStateListener != null) {
                chatFragmentStateListener.onStateChange(getUserName());
            }
            if (merchantCustomer.getCity() != null && merchantCustomer.getCity()
                    .getId() > 0) {
                wsChatAuthor.setCity(merchantCustomer.getCity());
                if (merchantChatFragmentListener != null) {
                    merchantChatFragmentListener.onCityChange(getChatAuthor().getCityName());
                }
            }
            WSRealmHelper.updateUser(wsChatAuthor);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initCustomerTopView() {
        View customerInfoView = View.inflate(getContext(),
                R.layout.merchant_chat_customer_top_layout,
                null);
        addTopLayout(customerInfoView);
        tvWxHint = customerInfoView.findViewById(R.id.tv_wx_hint);
        tvPhoneHint = customerInfoView.findViewById(R.id.tv_phone_hint);
        tvWx = customerInfoView.findViewById(R.id.tv_wx);
        tvPhone = customerInfoView.findViewById(R.id.tv_phone);
        customerInfoView.findViewById(R.id.wx_layout)
                .setOnClickListener(this);
        customerInfoView.findViewById(R.id.phone_layout)
                .setOnClickListener(this);
        setCustomerView(merchantCustomer);
    }


    private void setCustomerView(MerchantCustomer customer) {
        try {
            if (TextUtils.isEmpty(customer.getUserWechat())) {
                tvWxHint.setVisibility(View.VISIBLE);
                tvWx.setVisibility(View.GONE);
            } else {
                tvWxHint.setVisibility(View.GONE);
                tvWx.setVisibility(View.VISIBLE);
                tvWx.setText(customer.getUserWechat());
            }
            if (TextUtils.isEmpty(customer.getUserPhone())) {
                tvPhoneHint.setVisibility(View.VISIBLE);
                tvPhone.setVisibility(View.GONE);
            } else {
                tvPhoneHint.setVisibility(View.GONE);
                tvPhone.setVisibility(View.VISIBLE);
                tvPhone.setText(customer.getUserPhone());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getUserName() {
        if (merchantCustomer != null && !TextUtils.isEmpty(merchantCustomer.getUserName())) {
            return merchantCustomer.getUserName();
        }
        return super.getUserName();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_face:
                etContent.setVisibility(View.VISIBLE);
                btnSpeak.setVisibility(View.GONE);
                btnVoice.setImageResource(R.mipmap.icon_audio_gray);
                btnMenu.setImageResource(R.mipmap.icon_cross_add_44_44);
                btnFace.setImageResource(R.mipmap.icon_keyboard_round_gray);
                etContent.requestFocus();
                onShowMenu("face");
                break;
            case R.id.btn_voice:
                onVoiceMode();
                break;
            case R.id.btn_menu:
                etContent.setVisibility(View.VISIBLE);
                btnSpeak.setVisibility(View.GONE);
                btnVoice.setImageResource(R.mipmap.icon_audio_gray);
                btnFace.setImageResource(R.mipmap.icon_face_gray);
                btnMenu.setImageResource(R.mipmap.icon_keyboard_round_gray);
                etContent.requestFocus();
                onShowMenu("menu");
                break;
            case R.id.btn_send:
                sendText(etContent.getText()
                        .toString());
                etContent.setText(null);
                break;
            case R.id.send_image:
                onAddImage();
                break;
            case R.id.send_work:
                Intent intent = new Intent(getContext(), SelectWorkOpuActivity.class);
                intent.putExtra("type", 0);
                startActivityForResult(intent, SELECT_WORK_RESULT);
                break;
            case R.id.send_opu:
                intent = new Intent(getContext(), SelectWorkOpuActivity.class);
                intent.putExtra("type", 1);
                startActivityForResult(intent, SELECT_WORK_RESULT);
                break;
            case R.id.send_product:
                // 发送婚品
                intent = new Intent(getContext(), SelectProductActivity.class);
                startActivityForResult(intent, SELECT_PRODUCT_RESULT);
                break;
            case R.id.send_fast_reply:
                startActivityForResult(new Intent(getContext(), FastReplyActivity.class),
                        SELECT_FAST_REPLAY_RESULT);
                break;
            case R.id.send_address:
                com.hunliji.marrybiz.model.MerchantUser merchant = Session.getInstance()
                        .getCurrentUser(getContext());
                sendLocation(merchant.getName(),
                        merchant.getAddress(),
                        merchant.getLatitude(),
                        merchant.getLongitude());
                break;
            case R.id.wx_layout:
            case R.id.phone_layout:
                onEditCustomer();
                break;
        }
    }

    public void onEditCustomer() {
        if (merchantCustomer == null) {
            return;
        }
        Intent intent = new Intent(getContext(), CustomerDetailEditActivity.class);
        intent.putExtra("customer", merchantCustomer);
        intent.putExtra("from", "chat");
        startActivityForResult(intent, CUSTOMER_EDIT_RESULT);
    }

    @Override
    protected void onImmShow() {
        btnMenu.setImageResource(R.mipmap.icon_cross_add_44_44);
        btnFace.setImageResource(R.mipmap.icon_face_gray);
        btnFace.requestLayout();
        btnMenu.requestLayout();
        super.onImmShow();
    }

    @Override
    protected void onSpeckModeChange() {
        if (etContent.getVisibility() == View.VISIBLE) {
            showMenu = false;
            menuLayout.setVisibility(View.GONE);
            etContent.setVisibility(View.GONE);
            btnSpeak.setVisibility(View.VISIBLE);
            btnFace.setImageResource(R.mipmap.icon_face_gray);
            btnMenu.setImageResource(R.mipmap.icon_cross_add_44_44);
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
        intent.putExtra("w_id", id);
        startActivity(intent);
    }

    @Override
    protected void showCase(long id) {
        Intent intent = new Intent(getContext(), CaseDetailActivity.class);
        intent.putExtra("w_id", id);
        startActivity(intent);
    }

    @Override
    protected void showCustomMeal(long id) {

    }

    @Override
    protected void showProduct(long id) {

    }

    @Override
    protected void showUser(long id) {

    }

    @Override
    protected void gotoMerchant(long merchantId, int shopType) {

    }

    @Override
    protected void makeAppointment(long id) {

    }

    @Override
    protected void showWeddingCar(long id) {

    }

    @Override
    protected void autoSendSmartReplay(WSChat chat, String text) {
        // 商家端不实现发送智能回复消息
    }

    @Override
    protected void onFilterMessage(WSChat chat) {
        // 商家端不检查敏感词
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case SELECT_WORK_RESULT:
                    Work work = (Work) data.getSerializableExtra("work");
                    if (work != null) {
                        sendExtraObj(ModuleUtils.getWSProduct(work));
                    }
                    break;
                case SELECT_PRODUCT_RESULT:
                    ShopProduct product = data.getParcelableExtra("product");
                    if (product != null) {
                        sendExtraObj(ModuleUtils.getWSProduct(product));
                    }
                    break;
                case CUSTOMER_EDIT_RESULT:
                    MerchantCustomer customer = data.getParcelableExtra("customer");
                    if (customer != null) {
                        this.merchantCustomer = customer;
                        setCustomerView(customer);
                    }
                    break;
                case SELECT_FAST_REPLAY_RESULT:
                    FastReply reply = data.getParcelableExtra("fast_reply");
                    if (reply != null) {
                        etContent.setText(reply.getContent());
                        etContent.setSelection(etContent.length());
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        CommonUtil.unSubscribeSubs(loadSubscription);
        super.onDestroyView();
    }

    public void setMerchantChatFragmentListener(
            MerchantChatFragmentListener merchantChatFragmentListener) {
        this.merchantChatFragmentListener = merchantChatFragmentListener;
    }

    public interface MerchantChatFragmentListener {
        void onMerchantCustomerChange(MerchantCustomer merchantCustomer);

        void onCityChange(String cityName);
    }
}
