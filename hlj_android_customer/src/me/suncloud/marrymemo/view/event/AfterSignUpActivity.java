package me.suncloud.marrymemo.view.event;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljkefulibrary.utils.SupportUtil;
import com.hunliji.hljmaplibrary.views.activities.NavigateMapActivity;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.Hashtable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ContactsAdapter;
import me.suncloud.marrymemo.api.event.EventApi;
import me.suncloud.marrymemo.util.CustomerSupportUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;

/**
 * 活动报名结果界面
 * Created by chen_bin on 2016/8/10 0010.
 */
public class AfterSignUpActivity extends HljBaseNoBarActivity {
    @BindView(R.id.btn_detail)
    Button btnDetail;
    @BindView(R.id.btn_report)
    Button btnReport;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.img_after_status)
    ImageView imgAfterStatus;
    @BindView(R.id.tv_after_status)
    TextView tvAfterStatus;
    @BindView(R.id.btn_see_winner)
    Button btnSeeWinner;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.left_split)
    ImageView leftSplit;
    @BindView(R.id.right_split)
    ImageView rightSplit;
    @BindView(R.id.sign_up_layout)
    LinearLayout signUpLayout;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.time_title_layout)
    LinearLayout timeTitleLayout;
    @BindView(R.id.tv_time_title)
    TextView tvTimeTitle;
    @BindView(R.id.fee_layout)
    LinearLayout feeLayout;
    @BindView(R.id.tv_fee_count)
    TextView tvFeeCount;
    @BindView(R.id.address_layout)
    LinearLayout addressLayout;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.valid_code_layout)
    LinearLayout validCodeLayout;
    @BindView(R.id.code_layout)
    RelativeLayout codeLayout;
    @BindView(R.id.img_valid_code)
    ImageView imgValidCode;
    @BindView(R.id.tv_valid_code)
    TextView tvValidCode;
    @BindView(R.id.img_avatar)
    RoundedImageView imgAvatar;
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.merchant_address_layout)
    LinearLayout merchantAddressLayout;
    @BindView(R.id.tv_merchant_address)
    TextView tvMerchantAddress;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R.id.btn_call)
    ImageButton btnCall;
    private EventInfo eventInfo;
    private long id;
    private int codeSize;
    private int logoSize;
    private HljHttpSubscriber initSub;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_sign_up);
        ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout);
        initValues();
        initViews();
        initLoad();
    }

    private void initValues() {
        id = getIntent().getLongExtra("id", 0);
        codeSize = Util.dp2px(this, 66) + Util.sp2px(this, 102);
        logoSize = Util.dp2px(this, 32);
    }

    private void initViews() {
        codeLayout.getLayoutParams().width = codeSize + Util.dp2px(this, 10);
        imgValidCode.getLayoutParams().width = codeSize;
        imgValidCode.getLayoutParams().height = codeSize;
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                initLoad();
            }
        });
    }

    private void initLoad() {
        if (initSub == null || initSub.isUnsubscribed()) {
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<EventInfo>() {
                        @Override
                        public void onNext(EventInfo eventInfo) {
                            setSignUpResultData(eventInfo);
                        }
                    })
                    .setProgressBar(progressBar)
                    .setEmptyView(emptyView)
                    .setContentView(scrollView)
                    .build();
            EventApi.getEventDetailObb(id)
                    .subscribe(initSub);
        }
    }

    private void setSignUpResultData(EventInfo eventInfo) {
        this.eventInfo = eventInfo;
        if (eventInfo == null || eventInfo.getId() == 0) {
            return;
        }
        btnDetail.setVisibility(View.VISIBLE);
        btnReport.setVisibility(View.VISIBLE);
        tvTitle.setText(eventInfo.getTitle());
        if (TextUtils.isEmpty(eventInfo.getShowTimeTitle())) {
            timeTitleLayout.setVisibility(View.GONE);
        } else {
            timeTitleLayout.setVisibility(View.VISIBLE);
            tvTimeTitle.setText(eventInfo.getShowTimeTitle());
        }
        if (eventInfo.getSignUpFee() <= 0) {
            feeLayout.setVisibility(View.GONE);
        } else {
            feeLayout.setVisibility(View.VISIBLE);
            tvFeeCount.setText(String.format(getString(R.string.label_fee),
                    eventInfo.getSignUpFee()));
        }
        if (TextUtils.isEmpty(eventInfo.getAddress())) {
            addressLayout.setVisibility(View.GONE);
        } else {
            addressLayout.setVisibility(View.VISIBLE);
            tvAddress.setText(eventInfo.getAddress());
        }
        if (eventInfo.getWinnerLimit() <= 0) {
            imgAfterStatus.setVisibility(View.VISIBLE);
            tvAfterStatus.setText(R.string.label_sign_up_success);
        } else if (eventInfo.getSignUpInfo()
                .getStatus() >= 2) {
            imgAfterStatus.setVisibility(View.VISIBLE);
            tvAfterStatus.setText(R.string.hint_get_winner_done);
        } else if (eventInfo.isStatus() && eventInfo.getSignUpInfo()
                .getStatus() == 1) {
            imgAfterStatus.setVisibility(View.GONE);
            tvAfterStatus.setText(R.string.msg_disable_winner);
            btnSeeWinner.setVisibility(View.VISIBLE);
        } else {
            imgAfterStatus.setVisibility(View.VISIBLE);
            tvAfterStatus.setText(R.string.label_sign_up_wait_success);
        }
        if (eventInfo.getSignUpInfo()
                .getStatus() < 2) {
            leftSplit.setVisibility(View.GONE);
            rightSplit.setVisibility(View.GONE);
            signUpLayout.setBackgroundResource(R.drawable.image_bg_round_white);
            validCodeLayout.setVisibility(View.GONE);
        } else {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText(String.format(getString(R.string.label_keep_mobile_freely),
                    eventInfo.getSignUpInfo()
                            .getTel()));
            leftSplit.setVisibility(View.VISIBLE);
            rightSplit.setVisibility(View.VISIBLE);
            signUpLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite));
            validCodeLayout.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(eventInfo.getSignUpInfo()
                    .getValidCodeUrl())) {
                imgValidCode.setImageResource(R.mipmap.icon_empty_image);
            } else {
                new GetQRCodeTask().execute(eventInfo.getSignUpInfo()
                        .getValidCodeUrl());
            }
            if (!TextUtils.isEmpty(eventInfo.getSignUpInfo()
                    .getValidCode())) {
                tvValidCode.setVisibility(View.VISIBLE);
                tvValidCode.setText(eventInfo.getSignUpInfo()
                        .getValidCode());
            } else {
                tvValidCode.setVisibility(View.GONE);
            }
        }
        if (eventInfo.getMerchant() == null || eventInfo.getMerchant()
                .getId() == 0) {
            imgAvatar.setImageResource(R.drawable.icon_jixiaoxi_240_240);
            tvMerchantName.setText(R.string.app_name);
            merchantAddressLayout.setVisibility(View.GONE);
        } else {
            Glide.with(this)
                    .load(ImagePath.buildPath(eventInfo.getMerchant()
                            .getLogoPath())
                            .width(logoSize)
                            .cropPath())
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_avatar_primary)
                            .error(R.mipmap.icon_avatar_primary))
                    .into(imgAvatar);
            tvMerchantName.setText(eventInfo.getMerchant()
                    .getName());
            tvMerchantAddress.setText(eventInfo.getMerchant()
                    .getAddress());
        }

        if (eventInfo.getMerchant() != null && eventInfo.getMerchant()
                .getId() > 0 && CommonUtil.isCollectionEmpty(eventInfo.getMerchant()
                .getContactPhone())) {
            btnCall.setVisibility(View.GONE);
        }
    }

    //获取二维码
    private class GetQRCodeTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                Hashtable<EncodeHintType, Object> qrParam = new Hashtable<>();
                qrParam.put(EncodeHintType.CHARACTER_SET, "utf-8");
                qrParam.put(EncodeHintType.MARGIN, 0);
                BitMatrix bitMatrix = new MultiFormatWriter().encode(params[0],
                        BarcodeFormat.QR_CODE,
                        codeSize,
                        codeSize,
                        qrParam);
                int w = bitMatrix.getWidth();
                int h = bitMatrix.getHeight();
                int[] data = new int[w * h];
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        if (bitMatrix.get(x, y))
                            data[y * w + x] = 0xff000000;
                        else
                            data[y * w + x] = -1;
                    }
                }
                Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                bitmap.setPixels(data, 0, w, 0, 0, w, h);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (!isFinishing() && bitmap != null) {
                imgValidCode.setImageBitmap(bitmap);
            }
        }
    }

    //详情
    @OnClick(R.id.btn_detail)
    public void onDetail() {
        if (eventInfo != null && eventInfo.getId() > 0) {
            Intent intent = new Intent(this, EventDetailActivity.class);
            intent.putExtra("id", eventInfo.getId());
            startActivity(intent);
        }
    }

    //举报
    @OnClick(R.id.btn_report)
    public void onReport() {
        if (eventInfo == null) {
            return;
        }
        if (eventInfo.getReportInfo() != null) {
            Intent intent = new Intent(this, AfterReportEventActivity.class);
            intent.putExtra("eventInfo", eventInfo);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ReportEventActivity.class);
            intent.putExtra("eventInfo", eventInfo);
            startActivityForResult(intent, Constants.RequestCode.POST_REPORT);
        }
    }

    //查看中奖名单
    @OnClick(R.id.btn_see_winner)
    public void onSeeWinner() {
        if (eventInfo != null && eventInfo.getId() > 0) {
            Intent intent = new Intent(this, WinnerListActivity.class);
            intent.putExtra("id", eventInfo.getId());
            startActivity(intent);
        }
    }

    //私信
    @OnClick(R.id.btn_chat)
    public void onChat() {
        if (eventInfo == null) {
            return;
        }
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        if (eventInfo.getMerchant() == null || eventInfo.getMerchant()
                .getId() == 0) {
            CustomerSupportUtil.goToSupport(this, Support.SUPPORT_KIND_DEFAULT_ROBOT);
        } else {
            Merchant merchant = eventInfo.getMerchant();
            Intent intent = new Intent(this, WSCustomerChatActivity.class);
            intent.putExtra("user", merchant.toUser());
            if (!CommonUtil.isCollectionEmpty(merchant.getContactPhone())) {
                intent.putStringArrayListExtra("contact_phones", merchant.getContactPhone());
            }
            startActivity(intent);
        }
    }

    //打电话
    @OnClick(R.id.btn_call)
    public void onCall() {
        if (eventInfo == null) {
            return;
        }
        if (eventInfo.getMerchant() == null || eventInfo.getMerchant()
                .getId() == 0) {
            progressBar.setVisibility(View.VISIBLE);
            SupportUtil.getInstance(this)
                    .getSupport(this,
                            Support.SUPPORT_KIND_DEFAULT_ROBOT,
                            new SupportUtil.SimpleSupportCallback() {
                                @Override
                                public void onSupportCompleted(Support support) {
                                    super.onSupportCompleted(support);
                                    if (isFinishing()) {
                                        return;
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    try {
                                        if (support != null && !TextUtils.isEmpty(support
                                                .getPhone()) && support.getPhone()
                                                .trim()
                                                .length() > 0) {
                                            callUp(Uri.parse("tel:" + support.getPhone()
                                                    .trim()));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailed() {
                                    super.onFailed();
                                    if (!isFinishing()) {
                                        ToastUtil.showToast(AfterSignUpActivity.this,
                                                null,
                                                R.string.msg_get_supports_error);
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
        } else {
            ArrayList<String> contactPhones = eventInfo.getMerchant()
                    .getContactPhone();
            if (CommonUtil.isCollectionEmpty(contactPhones)) {
                ToastUtil.showToast(this, "", R.string.msg_no_merchant_number);
                return;
            }
            if (contactPhones.size() == 1) {
                String phone = contactPhones.get(0);
                try {
                    if (!TextUtils.isEmpty(phone) && phone.trim()
                            .length() > 0) {
                        callUp(Uri.parse("tel:" + phone.trim()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            Dialog contactDialog = new Dialog(this, R.style.BubbleDialogTheme);
            contactDialog.setContentView(R.layout.dialog_contact_phones);
            Point point = CommonUtil.getDeviceSize(this);
            ListView listView = (ListView) contactDialog.findViewById(R.id.contact_list);
            ContactsAdapter contactsAdapter = new ContactsAdapter(this, contactPhones);
            listView.setAdapter(contactsAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String phone = (String) adapterView.getAdapter()
                            .getItem(i);
                    try {
                        if (!TextUtils.isEmpty(phone) && phone.trim()
                                .length() > 0) {
                            callUp(Uri.parse("tel:" + phone.trim()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            Window win = contactDialog.getWindow();
            if (win != null) {
                ViewGroup.LayoutParams params = win.getAttributes();
                params.width = Math.round(point.x * 3 / 4);
                win.setGravity(Gravity.CENTER);
            }
            contactDialog.show();
        }
    }

    //用户信息
    @OnClick(R.id.user_info_layout)
    public void onUserInfo() {
        if (eventInfo != null && eventInfo.getMerchant() != null && eventInfo.getMerchant()
                .getId() > 0) {
            Intent intent = new Intent(this, MerchantDetailActivity.class);
            intent.putExtra("id",
                    eventInfo.getMerchant()
                            .getId());
            startActivity(intent);
        }
    }

    //地址
    @OnClick(R.id.merchant_address_layout)
    public void onAddress() {
        if (eventInfo != null && eventInfo.getMerchant() != null && eventInfo.getMerchant()
                .getId() > 0) {
            Merchant merchant = eventInfo.getMerchant();
            Intent intent = new Intent(this, NavigateMapActivity.class);
            intent.putExtra(NavigateMapActivity.ARG_TITLE, merchant.getName());
            intent.putExtra(NavigateMapActivity.ARG_ADDRESS, merchant.getAddress());
            intent.putExtra(NavigateMapActivity.ARG_LATITUDE, merchant.getLatitude());
            intent.putExtra(NavigateMapActivity.ARG_LONGITUDE, merchant.getLongitude());
            startActivity(intent);
        }
    }

    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.POST_REPORT:
                    if (data != null) {
                        eventInfo = data.getParcelableExtra("eventInfo");
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSub);
    }
}