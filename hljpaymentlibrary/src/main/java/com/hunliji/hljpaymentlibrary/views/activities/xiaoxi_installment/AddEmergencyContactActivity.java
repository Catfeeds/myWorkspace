package com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.AuthItem;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.EmergencyContact;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.XiaoxiInstallmentUser;
import com.hunliji.hljpaymentlibrary.utils.xiaoxi_installment.XiaoxiInstallmentAuthorization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 小犀分期-基本信息-紧急联系人信息
 * Created by chen_bin on 2017/8/10 0010.
 */
@RuntimePermissions
public class AddEmergencyContactActivity extends HljBaseActivity {

    @BindView(R2.id.et_spouse_name)
    EditText etSpouseName;
    @BindView(R2.id.tv_spouse_mobile)
    TextView tvSpouseMobile;
    @BindView(R2.id.et_contact_name)
    EditText etContactName;
    @BindView(R2.id.tv_contact_type)
    TextView tvContactType;
    @BindView(R2.id.tv_contact_mobile)
    TextView tvContactMobile;

    private TextView currentTvMobile;

    private Dialog confirmDialog;
    private Dialog contactTypeDialog;

    private XiaoxiInstallmentUser user;

    private List<String> contactTypes;

    private boolean isAuto;
    private boolean isEdit;

    private int contactType;

    private Subscription getContactSub;
    private Subscription getRiskContactsSub;
    private HljHttpSubscriber uploadSub;

    private static final int REQUEST_CODE_READ_CONTACT = 1;

    public static final String ARG_USER = "user";
    public static final String ARG_IS_EDIT = "is_edit";
    public static final String ARG_IS_AUTO = "is_auto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_add_emergency_contact___pay);
        ButterKnife.bind(this);
        initValues();
        setEmergencyContacts();
    }

    private void initValues() {
        user = getIntent().getParcelableExtra(ARG_USER);
        isEdit = getIntent().getBooleanExtra(ARG_IS_EDIT, false);
        isAuto = getIntent().getBooleanExtra(ARG_IS_AUTO, false);

        contactTypes = Arrays.asList(getResources().getStringArray(R.array.contactTypes));
    }

    private void setEmergencyContacts() {
        if (!isEdit) {
            return;
        }
        if (user == null || CommonUtil.isCollectionEmpty(user.getContacts())) {
            return;
        }
        List<EmergencyContact> contacts = user.getContacts();
        for (int i = 0, size = Math.min(2, contacts.size()); i < size; i++) {
            EmergencyContact contact = contacts.get(i);
            if (contact.getType() == EmergencyContact.TYPE_SPOUSE) {
                etSpouseName.setText(contact.getName());
                etContactName.setSelection(etContactName.length());
                tvSpouseMobile.setText(contact.getMobile());
            } else {
                etContactName.setText(contact.getName());
                tvContactMobile.setText(contact.getMobile());
                contactType = contact.getType();
                tvContactType.setText(contactTypes.get(Math.min(contactType - 1,
                        contacts.size() - 1)));
            }
        }
    }

    @OnClick({R2.id.spouse_mobile_layout, R2.id.contact_mobile_layout})
    void onSelectMobile(View v) {
        currentTvMobile = v.getId() == R.id.spouse_mobile_layout ? tvSpouseMobile : tvContactMobile;
        AddEmergencyContactActivityPermissionsDispatcher.onReadContactWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    void onReadContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_READ_CONTACT);
    }

    @OnShowRationale(Manifest.permission.READ_CONTACTS)
    void onRationale(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.msg_permission_r_for_read_contact___cm));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AddEmergencyContactActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_READ_CONTACT:
                if (data == null) {
                    return;
                }
                getSelectedContact(data.getData());
                if (currentTvMobile == tvSpouseMobile) { //选择配偶联系方式时才去读取本地通讯录列表
                    getRiskContacts();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取当前配偶/紧急联系人的号码
     *
     * @param uri
     */
    private void getSelectedContact(final Uri uri) {
        CommonUtil.unSubscribeSubs(getContactSub);
        getContactSub = Observable.create(new Observable.OnSubscribe<EmergencyContact>() {
            @Override
            public void call(Subscriber<? super EmergencyContact> subscriber) {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                EmergencyContact contact = null;
                try {
                    ContentResolver cr = getContentResolver();
                    Cursor c = cr.query(uri, null, null, null, null);
                    if (c != null && c.getCount() > 0) {
                        c.moveToFirst();
                        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                                new String[]{c.getString(c.getColumnIndex(ContactsContract
                                        .Contacts._ID))},
                                null);
                        if (cursor != null && cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            String mobile = cursor.getString(cursor.getColumnIndex
                                    (ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if (!TextUtils.isEmpty(mobile)) {
                                mobile = mobile.replaceAll("\\s+", "")
                                        .replaceAll("-", "");
                                String name = cursor.getString(cursor.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                contact = new EmergencyContact();
                                contact.setMobile(mobile);
                                contact.setName(name);
                            }
                            cursor.close();
                        }
                        c.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                subscriber.onNext(contact);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<EmergencyContact>() {
                    @Override
                    public void call(EmergencyContact contact) {
                        if (contact == null || CommonUtil.isEmpty(contact.getMobile())) {
                            ToastUtil.showToast(AddEmergencyContactActivity.this,
                                    getString(R.string.msg_err_get_contact___cm,
                                            getString(R.string.app_name)),
                                    0);
                            return;
                        }
                        showConfirmMobileDialog(contact);
                    }
                });
    }

    /**
     * 获取手机通讯录列表，用作风控处理
     */
    private void getRiskContacts() {
        CommonUtil.unSubscribeSubs(getRiskContactsSub);
        getRiskContactsSub = Observable.create(new Observable.OnSubscribe<JsonArray>() {
            @Override
            public void call(Subscriber<? super JsonArray> subscriber) {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                JsonArray jsonArray = null;
                try {
                    ContentResolver cr = getContentResolver();
                    Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                    if (cursor != null && cursor.getCount() > 0) {
                        jsonArray = new JsonArray();
                        while (cursor.moveToNext()) {
                            String mobile = cursor.getString(cursor.getColumnIndex
                                    (ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if (!TextUtils.isEmpty(mobile)) {
                                JsonObject jsonObject = new JsonObject();
                                mobile = mobile.replaceAll("\\s+", "")
                                        .replaceAll("-", "");
                                String name = cursor.getString(cursor.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                jsonObject.addProperty("phone", mobile);
                                jsonObject.addProperty("name", name);
                                jsonArray.add(jsonObject);
                            }
                        }
                        cursor.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                subscriber.onNext(jsonArray);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<JsonArray>() {
                    @Override
                    public void call(JsonArray jsonArray) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("user_id",
                                UserSession.getInstance()
                                        .getUser(AddEmergencyContactActivity.this)
                                        .getId());
                        jsonObject.add("contacts", jsonArray);
                        user.setRiskData(jsonObject);
                    }
                });
    }

    /**
     * 确定手机号码
     *
     * @param contact
     */
    private void showConfirmMobileDialog(final EmergencyContact contact) {
        if (confirmDialog != null && confirmDialog.isShowing()) {
            return;
        }
        if (confirmDialog == null) {
            confirmDialog = DialogUtil.createDialog(this,
                    R.layout.dialog_confirm_emergency_contact___pay);
            confirmDialog.findViewById(R.id.btn_cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmDialog.dismiss();
                        }
                    });
        }
        TextView tvAlertTitle = confirmDialog.findViewById(R.id.tv_alert_title);
        if (currentTvMobile == tvSpouseMobile) { //配偶
            tvAlertTitle.setText("确认配偶手机号");
        } else {
            tvAlertTitle.setText("确认联系人手机号");
        }
        TextView tvAlertMsg = confirmDialog.findViewById(R.id.tv_alert_msg);
        tvAlertMsg.setText(contact.getMobile());
        confirmDialog.findViewById(R.id.btn_confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmDialog.dismiss();
                        currentTvMobile.setText(contact.getMobile());
                    }
                });
        confirmDialog.show();
    }

    @OnClick(R2.id.contact_type_layout)
    void onSelectContactType() {
        if (contactTypeDialog != null && contactTypeDialog.isShowing()) {
            return;
        }
        if (contactTypeDialog == null) {
            contactTypeDialog = DialogUtil.createSingleWheelPickerDialog(this,
                    contactTypes,
                    Math.min(contactType - 1, contactTypes.size() - 1),
                    new DialogUtil.OnWheelSelectedListener() {
                        @Override
                        public void onWheelSelected(int position, String str) {
                            if (position < contactTypes.size() - 1) {
                                contactType = position + 1;
                            } else {
                                contactType = EmergencyContact.TYPE_COLLEAGUE;
                            }
                            tvContactType.setText(str);
                        }
                    });
        }
        contactTypeDialog.show();
    }

    @SuppressWarnings("unchecked")
    @OnClick(R2.id.btn_next)
    void onNext() {
        if (etSpouseName.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_enter_spouse_name___pay);
            return;
        }
        if (tvSpouseMobile.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_select_spouse_mobile___pay);
            return;
        }
        if (etContactName.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_enter_contact_name___pay);
            return;
        }
        if (tvContactType.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_select_contact_type___pay);
            return;
        }
        if (tvContactMobile.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_select_contact_mobile___pay);
            return;
        }
        if (TextUtils.equals(tvSpouseMobile.getText(), tvContactMobile.getText())) {
            ToastUtil.showToast(this,
                    null,
                    R.string.hint_spouse_mobile_cant_same_contact_mobile___pay);
            return;
        }
        List<EmergencyContact> contacts = new ArrayList<>();

        //配偶信息
        EmergencyContact contact = new EmergencyContact();
        contact.setName(etSpouseName.getText()
                .toString());
        contact.setMobile(tvSpouseMobile.getText()
                .toString());
        contact.setType(EmergencyContact.TYPE_SPOUSE);
        contacts.add(contact);

        //紧急联系人信息
        contact = new EmergencyContact();
        contact.setName(etContactName.getText()
                .toString());
        contact.setMobile(tvContactMobile.getText()
                .toString());
        contact.setType(contactType);
        contacts.add(contact);

        user.setContacts(contacts);
        CommonUtil.unSubscribeSubs(uploadSub);
        uploadSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        RxBus.getDefault()
                                .post(new PayRxEvent(PayRxEvent.RxEventType
                                        .ADD_BASIC_USER_INFO_SUCCESS,
                                        null));
                        XiaoxiInstallmentAuthorization.getInstance()
                                .onCurrentItemAuthorized(AddEmergencyContactActivity.this,
                                        AuthItem.CODE_BASIC_USER_INFO,
                                        isAuto);
                    }
                })
                .setDataNullable(true)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .build();
        XiaoxiInstallmentApi.uploadUserInfoObb(this, user)
                .subscribe(uploadSub);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(uploadSub, getContactSub, getRiskContactsSub);
    }

}