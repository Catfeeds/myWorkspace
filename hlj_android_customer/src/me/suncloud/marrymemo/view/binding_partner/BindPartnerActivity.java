package me.suncloud.marrymemo.view.binding_partner;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.ClearableEditText;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.bindpartner.PartnerApi;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.bindpartner.PartnerBindResult;
import me.suncloud.marrymemo.model.bindpartner.PartnerPostBody;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Observable;

@RuntimePermissions
public class BindPartnerActivity extends HljBaseActivity {

    @BindView(R.id.img_my_avatar)
    RoundedImageView imgMyAvatar;
    @BindView(R.id.img_partner_avatar)
    ImageView imgPartnerAvatar;
    @BindView(R.id.et_phone)
    ClearableEditText etPhone;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    private int gender; // 1:男, 2:女
    private HljHttpSubscriber subscriber;
    private int type; // 登陆相关标准，后续控制

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_partner);
        ButterKnife.bind(this);
        type = getIntent().getIntExtra("type", 0);
        User user = Session.getInstance()
                .getCurrentUser(this);
        if (user != null) {
            Glide.with(this)
                    .load(ImageUtil.getAvatar(user.getAvatar(), CommonUtil.dp2px(this, 68)))
                    .apply(new RequestOptions().dontAnimate())
                    .into(imgMyAvatar);
            gender = user.getGender();
        }

        etPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    // 绑定
                    if (etPhone.length() > 0) {
                        onBind();
                    }
                }
                return false;
            }
        });
    }


    /**
     * 关闭按钮点击
     */
    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        MessageEvent event = new MessageEvent(MessageEvent.EventType.LOGINCHECK, null);
        EventBus.getDefault()
                .post(event);
        finish();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_down);
    }

    @OnClick(R.id.btn_submit)
    void onSubmit() {
        // 绑定
        if (etPhone.length() > 0) {
            onBind();
        }
    }

    private void onBind() {
        if (!CommonUtil.isMobileNO(etPhone.getText()
                .toString())) {
            Toast.makeText(this, R.string.msg_wrong_phone_number, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        final String phone = etPhone.getText()
                .toString();
        PartnerPostBody body = new PartnerPostBody();
        body.setGender(gender);
        body.setPartnerPhone(phone);
        Observable<PartnerBindResult> observable = PartnerApi.getPostBindPartnerObb(body);
        subscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener<PartnerBindResult>() {
                    @Override
                    public void onNext(PartnerBindResult result) {
                        Intent intent = new Intent(BindPartnerActivity.this,
                                AfterBindPartnerActivity.class);
                        // 发送消息给主页，更新提示
                        RxBus.getDefault()
                                .post(new RxEvent(RxEvent.RxEventType.INIT_PARTNER_INVITATION,
                                        null));
                        if (result.getShareInfo() != null) {
                            // 需要去分享
                            intent.putExtra("share_info", result.getShareInfo());
                            intent.putExtra("phone_number", phone);
                        }
                        //type继续往下传
                        intent.putExtra("type", type);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                        finish();
                    }
                })
                .build();

        observable.subscribe(subscriber);
    }

    @OnClick(R.id.tv_add)
    void onAddContact() {
        BindPartnerActivityPermissionsDispatcher.onReadContactWithCheck(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null && requestCode == Constants.RequestCode.GET_CONTACT_NAME) {
                Uri contactData = data.getData();
                ContentResolver cr = getContentResolver();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);
                if (c != null) {
                    if (c.moveToFirst()) {
                        String ContactId = c.getString(c.getColumnIndex(ContactsContract.Contacts
                                ._ID));
                        Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId,
                                null,
                                null);
                        // 取第一个算了
                        if (phone.moveToNext()) {
                            String PhoneNumber = phone.getString(phone.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            PhoneNumber = PhoneNumber.replace("-", "");
                            PhoneNumber = PhoneNumber.replace(" ", "");
                            etPhone.setText(PhoneNumber);
                            phone.close();
                        }
                    }
                    c.close();
                }
            } else {
                Toast.makeText(this, R.string.msg_err_get_contact_name, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    void onReadContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, Constants.RequestCode.GET_CONTACT_NAME);
    }

    @OnShowRationale(Manifest.permission.READ_CONTACTS)
    void onRationaleForReadContact(PermissionRequest request) {
        me.suncloud.marrymemo.util.DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.permission_r_for_read_contact));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        BindPartnerActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
