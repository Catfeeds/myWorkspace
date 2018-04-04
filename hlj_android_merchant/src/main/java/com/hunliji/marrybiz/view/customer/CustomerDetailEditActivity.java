package com.hunliji.marrybiz.view.customer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljchatlibrary.WSRealmHelper;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.realm.WSChatAuthor;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.customer.CustomerApi;
import com.hunliji.marrybiz.model.customer.MerchantCustomer;
import com.hunliji.marrybiz.model.customer.MerchantCustomerModifyBody;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.chat.WSMerchantChatActivity;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import kankan.wheel.widget.DTPicker;
import kankan.wheel.widget.DatePickerView;

/**
 * 客资详情包括编辑activity
 * Created by jinxin on 2017/8/10 0010.
 */

public class CustomerDetailEditActivity extends HljBaseActivity implements View.OnClickListener,
        DTPicker.OnPickerDateListener {

    @BindView(R.id.img_avatar)
    RoundedImageView imgAvatar;
    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.edit_name)
    EditText editName;
    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.edit_wei_chat)
    EditText editWeiChat;
    @BindView(R.id.tv_wedding_date)
    TextView tvWeddingDate;
    @BindView(R.id.tv_deal_aspiration)
    TextView tvDealAspiration;
    @BindView(R.id.edit_remark)
    EditText editRemark;
    @BindView(R.id.tv_remark_count)
    TextView tvRemarkCount;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.vip_logo)
    ImageView vipLogo;

    private Dialog aspirationDialog;
    private Dialog selectTimeDialog;
    private Dialog saveDialog;
    private Calendar tempCalendar;
    private HljHttpSubscriber saveSubscriber;
    private MerchantCustomer merchantCustomer;
    private boolean isContentChange;
    private Object[] saveData = new Object[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail_edit);
        ButterKnife.bind(this);

        initConstant();
        initLoad();
    }

    private void initConstant() {
        setSwipeBackEnable(false);
        line.setVisibility(View.GONE);
    }

    private void initLoad() {
        merchantCustomer = getIntent().getParcelableExtra("customer");
        if (merchantCustomer != null) {
            setMerchantCustomer(merchantCustomer);
        }
    }

    public void setMerchantCustomer(MerchantCustomer merchantCustomer) {
        this.merchantCustomer = merchantCustomer;
        if (merchantCustomer != null) {
            if (merchantCustomer.getUser() != null) {
                int width = CommonUtil.dp2px(this, 44);
                Glide.with(this)
                        .load(ImagePath.buildPath(merchantCustomer.getUser()
                                .getAvatar())
                                .width(width)
                                .height(width)
                                .cropPath())
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_avatar_primary))
                        .into(imgAvatar);
                tvAccountName.setText(merchantCustomer.getUser()
                        .getNick());
                if (merchantCustomer.getUser()
                        .getExtend() != null && merchantCustomer.getUser()
                        .getExtend()
                        .getHljMemberPrivilege() > 0) {
                    vipLogo.setVisibility(View.VISIBLE);
                } else {
                    vipLogo.setVisibility(View.GONE);
                }
            }
            editName.setText(merchantCustomer.getUserName());
            editPhone.setText(merchantCustomer.getUserPhone());
            editWeiChat.setText(merchantCustomer.getUserWechat());
            editRemark.setText(merchantCustomer.getRemark());
            try {
                if (!TextUtils.isEmpty(merchantCustomer.getWeddingday())) {
                    SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_FORMAT_SHORT);
                    tvWeddingDate.setText(format.format(format.parse(merchantCustomer
                            .getWeddingday())));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvDealAspiration.setText(getDealWill(merchantCustomer.getDealWill()));

            ContentChangeTextWatcher changeTextWatcher = new ContentChangeTextWatcher(null,
                    null,
                    0);
            editName.addTextChangedListener(changeTextWatcher);
            editPhone.addTextChangedListener(changeTextWatcher);
            editWeiChat.addTextChangedListener(changeTextWatcher);
            editRemark.addTextChangedListener(new ContentChangeTextWatcher(editRemark,
                    tvRemarkCount,
                    200));
        }
    }

    private String getDealWill(int dealWill) {
        switch (dealWill) {
            case 0:
                return "低";
            case 1:
                return "一般";
            case 2:
                return "高";
            default:
                return null;
        }
    }

    @Override
    public void onBackPressed() {
        if (merchantCustomer == null || !isContentChange) {
            super.onBackPressed();
        } else {
            showSaveDialog();
        }
    }

    @OnClick(R.id.btn_call)
    void onCall() {
        if (TextUtils.isEmpty(editPhone.getText()
                .toString())) {
            Toast.makeText(this, "请输入电话号码！", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        String phone = editPhone.getText()
                .toString();
        callUp(Uri.parse("tel:" + phone.trim()));
    }

    @OnClick(R.id.btn_send)
    void onSendMsg() {
        if (merchantCustomer == null) {
            return;
        }
        String from = getIntent().getStringExtra("from");
        boolean gotoChat = TextUtils.isEmpty(from) || !from.equals("chat");
        //未修改内容直接返回
        if (isContentChange) {
            saveCustomerMessage(gotoChat);
        } else if (gotoChat) {
            Intent intent = new Intent(CustomerDetailEditActivity.this,
                    WSMerchantChatActivity.class);
            intent.putExtra("user", merchantCustomer.getUser());
            startActivity(intent);
        } else {
            super.onBackPressed();
        }

    }

    @OnClick(R.id.tv_deal_aspiration)
    void onAspiration() {
        showAspirationDialog();
    }

    @OnClick(R.id.tv_wedding_date)
    void onWeddingDate() {
        showDateDialog();
    }

    private void showSaveDialog() {
        if (saveDialog != null && saveDialog.isShowing()) {
            return;
        }

        if (saveDialog == null) {
            saveDialog = DialogUtil.createDoubleButtonDialog(this,
                    "保存客资信息？",
                    getString(R.string.button_ok),
                    getString(R.string.button_cancel),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //确定
                            saveCustomerMessage(false);
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //取消
                            CustomerDetailEditActivity.super.onBackPressed();
                        }
                    });
        }
        saveDialog.show();
    }

    private void showAspirationDialog() {
        if (aspirationDialog != null && aspirationDialog.isShowing()) {
            return;
        }

        if (aspirationDialog == null) {
            aspirationDialog = new Dialog(this, R.style.BubbleDialogTheme);
            aspirationDialog.setContentView(R.layout.dialog_aspiration);
            aspirationDialog.findViewById(R.id.btn_high)
                    .setOnClickListener(this);
            aspirationDialog.findViewById(R.id.btn_normal)
                    .setOnClickListener(this);
            aspirationDialog.findViewById(R.id.btn_low)
                    .setOnClickListener(this);
            aspirationDialog.setCanceledOnTouchOutside(true);
            Window win = aspirationDialog.getWindow();
            ViewGroup.LayoutParams params = win.getAttributes();
            params.width = CommonUtil.getDeviceSize(this).x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        aspirationDialog.show();
    }

    private void showDateDialog() {
        if (selectTimeDialog != null && selectTimeDialog.isShowing()) {
            return;
        }
        if (selectTimeDialog == null) {
            selectTimeDialog = new Dialog(this, R.style.BubbleDialogTheme);
            selectTimeDialog.setContentView(R.layout.dialog_date_picker);
            selectTimeDialog.findViewById(R.id.close)
                    .setOnClickListener(this);
            selectTimeDialog.findViewById(R.id.confirm)
                    .setOnClickListener(this);
            DatePickerView picker = (DatePickerView) selectTimeDialog.findViewById(R.id.picker);
            picker.setYearLimit(Calendar.getInstance()
                    .get(Calendar.YEAR), 49);
            picker.setCurrentCalender(Calendar.getInstance());
            if (tempCalendar != null) {
                tempCalendar = Calendar.getInstance();
            }
            picker.setOnPickerDateListener(this);
            picker.getLayoutParams().height = Math.round(getResources().getDisplayMetrics()
                    .density * (24 * 8));
            Window win = selectTimeDialog.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            params.width = CommonUtil.getDeviceSize(this).x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        selectTimeDialog.show();
    }

    private void saveCustomerMessage(final boolean sendMsg) {
        if (saveSubscriber != null && !saveSubscriber.isUnsubscribed()) {
            return;
        }
        saveSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                    @Override
                    public void onNext(HljHttpResult hljHttpResult) {
                        if (hljHttpResult != null) {
                            if (hljHttpResult.getStatus() != null) {
                                if (hljHttpResult.getStatus()
                                        .getRetCode() == 0) {
                                    Toast.makeText(CustomerDetailEditActivity.this,
                                            "保存客资信息成功",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                    merchantCustomer.setId((Long) saveData[0]);
                                    merchantCustomer.setDealWill((Integer) saveData[1]);
                                    merchantCustomer.setRemark((String) saveData[2]);
                                    merchantCustomer.setUserName((String) saveData[3]);
                                    merchantCustomer.setUserPhone((String) saveData[4]);
                                    merchantCustomer.setUserWechat((String) saveData[5]);
                                    merchantCustomer.setWeddingday((String) saveData[6]);
                                    Realm realm = Realm.getDefaultInstance();
                                    WSChatAuthor author = realm.where(WSChatAuthor.class)
                                            .equalTo("id",
                                                    merchantCustomer.getUser()
                                                            .getId())
                                            .findFirst();
                                    if (author != null) {
                                        author = realm.copyFromRealm(author);
                                        author.setRemarkName(merchantCustomer.getUserName());
                                        author.setCity(merchantCustomer.getCity());
                                        WSRealmHelper.updateUser(author);
                                    }
                                    realm.close();

                                    if (!sendMsg) {
                                        Intent data = getIntent();
                                        data.putExtra("customer", merchantCustomer);
                                        setResult(Activity.RESULT_OK, data);
                                        CustomerDetailEditActivity.super.onBackPressed();
                                    } else {
                                        Intent intent = new Intent(CustomerDetailEditActivity.this,
                                                WSMerchantChatActivity.class);
                                        intent.putExtra("user", merchantCustomer.getUser());
                                        startActivity(intent);
                                    }
                                } else {
                                    Toast.makeText(CustomerDetailEditActivity.this,
                                            hljHttpResult.getStatus()
                                                    .getMsg(),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    }
                })
                .build();

        MerchantCustomerModifyBody body = new MerchantCustomerModifyBody();
        String dealWill = tvDealAspiration.getText()
                .toString();
        saveData[0] = merchantCustomer.getId();
        body.setId(merchantCustomer.getId());

        int deal_will = getDealWill(dealWill);
        saveData[1] = deal_will;
        body.setDeal_will(deal_will);

        String remark = editRemark.getText()
                .toString();
        body.setRemark(remark);
        saveData[2] = remark;

        String name = editName.getText()
                .toString();
        body.setUser_name(name);
        saveData[3] = name;

        String phone = editPhone.getText()
                .toString();
        body.setUser_phone(phone);
        saveData[4] = phone;

        String weChat = editWeiChat.getText()
                .toString();
        body.setUser_wechat(weChat);
        saveData[5] = weChat;

        String weddingDate = tvWeddingDate.getText()
                .toString();
        body.setWeddingday(weddingDate);
        saveData[6] = weddingDate;

        CustomerApi.saveCustomer(body)
                .subscribe(saveSubscriber);
    }

    private int getDealWill(String dealWill) {
        // 0低 1 一般 2高
        int deal_will;
        switch (dealWill) {
            case "高":
                deal_will = 2;
                break;
            case "一般":
                deal_will = 1;
                break;
            case "低":
                deal_will = 0;
                break;
            default:
                deal_will = 1;
        }
        return deal_will;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_high:
                isContentChange = true;
                tvDealAspiration.setText("高");
                aspirationDialog.dismiss();
                break;
            case R.id.btn_normal:
                isContentChange = true;
                tvDealAspiration.setText("一般");
                aspirationDialog.dismiss();
                break;
            case R.id.btn_low:
                isContentChange = true;
                tvDealAspiration.setText("低");
                aspirationDialog.dismiss();
                break;
            case R.id.close:
                selectTimeDialog.dismiss();
                break;
            case R.id.confirm:
                isContentChange = true;
                selectTimeDialog.dismiss();
                if (tempCalendar != null) {
                    tvWeddingDate.setText(new DateTime(tempCalendar.getTime()).toString(Constants
                            .DATE_FORMAT_SHORT));
                }
                break;
        }
    }

    @Override
    public void onPickerDate(int year, int month, int day) {
        if (tempCalendar == null) {
            tempCalendar = new GregorianCalendar(year, month - 1, day);
        } else {
            tempCalendar.set(year, month - 1, day);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(saveSubscriber);
    }

    class ContentChangeTextWatcher implements TextWatcher {
        private EditText editText;
        private TextView numText;
        private int limitCount;

        public ContentChangeTextWatcher(EditText editText, TextView numText, int limitCount) {
            this.editText = editText;
            this.numText = numText;
            this.limitCount = limitCount;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            isContentChange = true;
            if (editText != null && numText != null) {
                int editStart = editText.getSelectionStart();
                int editEnd = editText.getSelectionEnd();
                editText.removeTextChangedListener(this);
                if (editStart == 0) {
                    editStart = s.length();
                    editEnd = s.length();
                    if (editStart > limitCount * 2) {
                        editStart = limitCount * 2;
                    }
                }
                int outCount = Util.getTextLength(s) - limitCount;
                while (outCount > 0) {
                    editStart -= outCount;
                    s.delete(editStart, editEnd);
                    editEnd = editStart;
                    outCount = Util.getTextLength(s) - limitCount;
                }
                numText.setText(String.valueOf(limitCount - Util.getTextLength(s)));
                editText.setSelection(editStart);
                editText.addTextChangedListener(this);
            }
        }
    }

}
