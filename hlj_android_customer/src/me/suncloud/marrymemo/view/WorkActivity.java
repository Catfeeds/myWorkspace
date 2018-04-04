package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljpaymentlibrary.models.InstallmentType;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.hunliji.hljtrackerlibrary.TrackerHelper;
import com.hunliji.hljtrackerlibrary.utils.TrackerUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.HLJCustomerApplication;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ContactsAdapter;
import me.suncloud.marrymemo.fragment.WorkOldFragment;
import me.suncloud.marrymemo.fragment.work_case.WorkDetailFragment;
import me.suncloud.marrymemo.model.NewMerchant;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.modulehelper.ModuleUtils;
import me.suncloud.marrymemo.task.HttpDeleteTask;
import me.suncloud.marrymemo.task.HttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.ShareUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.util.merchant.AppointmentUtil;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import me.suncloud.marrymemo.view.orders.ServiceOrderConfirmActivity;
import rx.Subscription;

/**
 * Created by Suncloud on 2016/1/13.
 */
@Route(path = RouterPath.IntentPath.Customer.WORK_ACTIVITY)
public class WorkActivity extends HljBaseNoBarActivity {

    private final static int MSG_INIT_VIEW = 0xA00;

    public static final String CPM_SOURCE = "meal_detail_recommands";


    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.iv_collect)
    ImageView imgCollect;
    @BindView(R.id.li_collect)
    LinearLayout liCollect;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.title)
    TextView titleView;
    @BindView(R.id.buttons_layout)
    LinearLayout buttonsLayout;
    @BindView(R.id.buttons_layout2)
    LinearLayout buttonsLayout2;
    @BindView(R.id.msg_notice_layout)
    RelativeLayout msgNoticeLayout;
    @BindView(R.id.btn_buy)
    Button btnBuy;
    @BindView(R.id.tv_solo_out)
    TextView tvSoldOut;
    @BindView(R.id.tv_collect)
    TextView tvCollect;
    @BindView(R.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R.id.action_layout)
    LinearLayout actionLayout;
    @BindView(R.id.btn_call)
    LinearLayout btnCall;

    private Work work;
    private String parentName;
    private boolean isResume;
    private JSONObject workObject;

    private Dialog shareDialog;
    private Dialog contactDialog;
    private ShareUtil shareUtil;

    private boolean isSnapshot;
    private Dialog installmentDialog;

    private NoticeUtil noticeUtil;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HljShare.RequestCode.SHARE_TO_TXWEIBO:
                case HljShare.RequestCode.SHARE_TO_WEIBO:
                case HljShare.RequestCode.SHARE_TO_QQZONE:
                case HljShare.RequestCode.SHARE_TO_QQ:
                case HljShare.RequestCode.SHARE_TO_PENGYOU:
                case HljShare.RequestCode.SHARE_TO_WEIXIN:
                    TrackerHelper.postShareAction(WorkActivity.this, work.getId(), "set_meal");
                    new HljTracker.Builder(WorkActivity.this).eventableId(work.getId())
                            .eventableType("Package")
                            .screen("package_detail")
                            .action("share")
                            .additional(HljShare.getShareTypeName(msg.what))
                            .build()
                            .add();
                    break;
                case MSG_INIT_VIEW:
                    new GetWorkTask().onPostExecute(workObject);
                    break;
            }
            return false;
        }
    });
    private JSONObject siteJson;
    private String ads;
    private Subscription appointmentSub;

    public final static String ARG_ID = "id";

    @Override
    public String pageTrackTagName() {
        boolean isSnapshot = getIntent().getBooleanExtra("isSnapshot", false);
        if (isSnapshot) {
            return super.pageTrackTagName();
        }
        return "套餐详情页";
    }

    @Override
    public VTMetaData pageTrackData() {
        boolean isSnapshot = getIntent().getBooleanExtra("isSnapshot", false);
        if (isSnapshot) {
            return super.pageTrackData();
        }
        long id = getIntent().getLongExtra(ARG_ID, 0);
        return new VTMetaData(id, "Package");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        ButterKnife.bind(this);

        setActionBarPadding(actionHolderLayout, actionLayout, msgNoticeLayout);

        initValues();

        initLoad();
    }

    private void initValues() {
        String workJson = getIntent().getStringExtra("workJson");
        String site = getIntent().getStringExtra("site");
        ads = getIntent().getStringExtra("ads");
        isSnapshot = getIntent().getBooleanExtra("isSnapshot", false);
        if (!JSONUtil.isEmpty(site)) {
            try {
                siteJson = new JSONObject(site);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (siteJson == null && getApplication() != null && getApplication() instanceof
                HLJCustomerApplication) {
            siteJson = TrackerUtil.getSiteJson(null, 0, TrackerHelper.getActivityHistory(this));
        }

        if (!JSONUtil.isEmpty(workJson)) {
            try {
                workObject = new JSONObject(workJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initLoad() {
        progressBar.setVisibility(View.VISIBLE);
        if (isSnapshot) {
            buttonsLayout.setVisibility(View.GONE);
            buttonsLayout2.setVisibility(View.GONE);
            msgNoticeLayout.setVisibility(View.GONE);
            liCollect.setVisibility(View.GONE);
            titleView.setText(R.string.title_activity_new_snapshot);
            long orderId = getIntent().getLongExtra("order_id", 0);
            long setMealId = getIntent().getLongExtra("set_meal_id", 0);
            String url = String.format(Constants.getAbsUrl(Constants.HttpPath
                            .GET_SERVICE_ORDER_WORK_SNAPSHOT),
                    orderId,
                    setMealId);
            new GetSnapshotTask().executeOnExecutor(Constants.INFOTHEADPOOL, url);
        } else {
            long id = getIntent().getLongExtra("id", 0);
            new HljTracker.Builder(this).eventableId(id)
                    .eventableType("Package")
                    .screen("package_detail")
                    .action("hit")
                    .additional(ads)
                    .site(siteJson)
                    .build()
                    .send();
            if (workObject != null) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        handler.sendEmptyMessageDelayed(MSG_INIT_VIEW, 400);
                    }
                }.start();
            } else {
                new GetWorkTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                        Constants.getAbsUrl(String.format(Constants.HttpPath.NEW_WORK_INFO, id)));
            }
        }
    }

    @OnClick(R.id.merchant_home_layout)
    public void onMerchant(View view) {
        if (work != null && work.getMerchantId() > 0) {
            Intent intent = new Intent(this, MerchantDetailActivity.class);
            intent.putExtra("id", work.getMerchantId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @OnClick(R.id.btn_message2)
    public void onMessage(View view) {
        if (Util.loginBindChecked(this, Constants.Login.MSG_LOGIN)) {
            Intent intent = new Intent(this, MessageHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    private class GetWorkTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            try {
                String json = JSONUtil.getStringFromUrl(strings[0]);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (isFinishing()) {
                return;
            }
            ReturnStatus status = null;
            if (result != null) {
                status = new ReturnStatus(result.optJSONObject("status"));
                if (result.optJSONObject("data") != null) {
                    initWorkJson(result.optJSONObject("data")
                            .optJSONObject("work"));
                    if (work != null && work.getId() > 0) {
                        findViewById(R.id.bottom_layout).setVisibility(View.VISIBLE);
                        if (work.isSoldOut()) {
                            tvSoldOut.setVisibility(View.VISIBLE);
                            tvSoldOut.setText(getString(R.string.label_sold_out,
                                    getString(R.string.label_work)));
                            btnBuy.setEnabled(false);
                        } else {
                            tvSoldOut.setVisibility(View.GONE);
                            btnBuy.setEnabled(true);
                        }
                        if (work.getVersion() > 0) {
                            btnBuy.setText(R.string.label_reservation);
                        }
                    }
                }
            }
            if (status != null && status.getRetCode() == 404 && !JSONUtil.isEmpty(status
                    .getErrorMsg())) {
                setEmptyView(status.getErrorMsg());
            } else {
                setEmptyView(null);
            }
            super.onPostExecute(result);
        }
    }

    private void initWorkJson(JSONObject jsonObject) {
        if (jsonObject != null) {
            work = new Work(jsonObject);
            if (work.getId() == 0) {
                return;
            }
            if (work.isSoldOut()) {
                Toast.makeText(WorkActivity.this, R.string.msg_work_sold_out, Toast.LENGTH_SHORT)
                        .show();
            }
            if (work.isCollected()) {
                imgCollect.setImageResource(R.drawable.icon_collect_primary_44_44_selected);
                tvCollect.setText(getString(R.string.label_has_collection));
            } else {
                imgCollect.setImageResource(R.drawable.icon_collect_primary_44_44_normal);
                tvCollect.setText(getString(R.string.label_collect));
            }
            if (!JSONUtil.isEmpty(work.getKind())) {
                parentName = work.getKind() + "套餐详情";
                if (isResume) {
                    me.suncloud.marrymemo.util.TrackerUtil.onTCAgentPageStart(WorkActivity.this,
                            parentName);
                }
            }
            if (work.getMerchant() == null) {
                return;
            }
            if (work.getMerchant() == null || CommonUtil.isCollectionEmpty(work.getMerchant()
                    .getContactPhone())) {
                btnCall.setVisibility(View.GONE);
            }
            RefreshFragment fragment = (RefreshFragment) getSupportFragmentManager()
                    .findFragmentByTag(
                    "workFragment");
            if (fragment == null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (work.getVersion() > 0) {
                    fragment = WorkDetailFragment.newInstance(work,
                            isSnapshot,
                            jsonObject.toString());
                } else {
                    fragment = (RefreshFragment) Fragment.instantiate(WorkActivity.this,
                            WorkOldFragment.class.getName());
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isSnapshot", isSnapshot);
                    bundle.putSerializable("work", work);
                    bundle.putString("json", jsonObject.toString());
                    fragment.setArguments(bundle);
                }
                ft.add(R.id.fragment, fragment, "workFragment");
                ft.commitAllowingStateLoss();
            } else {
                fragment.refresh(work);
            }
        }
    }

    private void setEmptyView(String msg) {
        if (work == null || work.getId() == 0) {
            progressBar.setVisibility(View.GONE);
            findViewById(R.id.action_layout).setAlpha(1);
            findViewById(R.id.shadow_view).setAlpha(0);
            View emptyView = findViewById(R.id.empty_hint_layout);
            emptyView.setVisibility(View.VISIBLE);
            TextView textEmptyHint = (TextView) findViewById(R.id.text_empty_hint);
            textEmptyHint.setVisibility(View.VISIBLE);
            if (JSONUtil.isNetworkConnected(WorkActivity.this)) {
                ImageView imgEmptyHint = (ImageView) findViewById(R.id.img_empty_hint);
                imgEmptyHint.setVisibility(View.VISIBLE);
                if (!JSONUtil.isEmpty(msg)) {
                    textEmptyHint.setText(msg);
                } else {
                    textEmptyHint.setText(getString(R.string.no_item));
                }
            } else {
                ImageView imgEmptyHint = (ImageView) findViewById(R.id.img_net_hint);
                imgEmptyHint.setVisibility(View.VISIBLE);
                textEmptyHint.setText(getString(R.string.net_disconnected));
            }
        }
    }

    @OnClick({R.id.btn_back, R.id.btn_back2})
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        } else {
            super.onBackPressed();
        }
    }


    @OnClick(R.id.btn_buy)
    public void onBuy(View view) {
        if (work == null || work.getId() == 0) {
            return;
        }
        if (work.getVersion() > 0) {
            onReservation();
        } else {
            onBuy();
        }
    }

    public void onBuy() {
        if (work.isSoldOut()) {
            Toast.makeText(this, R.string.msg_work_sold_out, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (work.isInstallment() && work.getInstallment() != null && work.getInstallment()
                .getType() == InstallmentType.NotSupported) {
            if (installmentDialog == null) {
                installmentDialog = DialogUtil.createSingleButtonDialog(null,
                        this,
                        getString(R.string.msg_installment_not_support),
                        null,
                        null);
            }
            installmentDialog.show();
            return;

        }
        new HljTracker.Builder(this).eventableId(work.getId())
                .eventableType("Package")
                .screen("package_detail")
                .action("hit_buy")
                .build()
                .add();
        // 及时抢购还未开始，可以以原件购买
        if (Util.loginBindChecked(this, Constants.Login.SUBMIT_LOGIN)) {
            // 普通套餐和分期套餐的下单路径不一样
            Intent intent = new Intent(this, ServiceOrderConfirmActivity.class);
            intent.putExtra(ServiceOrderConfirmActivity.ARG_WORK, work);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    private void onReservation() {
        if (work == null || work.getMerchant() == null) {
            return;
        }
        if (Util.loginBindChecked(this, Constants.Login.SUBMIT_LOGIN)) {
            CommonUtil.unSubscribeSubs(appointmentSub);
            appointmentSub = AppointmentUtil.makeAppointment(this,
                    work.getMerchant()
                            .getId(),
                    work.getMerchant()
                            .getUserId(),
                    AppointmentUtil.WORKCASE,
                    null);
        }
    }

    @OnClick(R.id.btn_call)
    public void onPhoneContact(View view) {
        if (work.getMerchant() == null) {
            return;
        }
        ArrayList<String> contactPhones = work.getMerchant()
                .getContactPhone();
        if (contactPhones == null || contactPhones.isEmpty()) {
            Toast.makeText(this, R.string.msg_no_merchant_number, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (contactPhones.size() == 1) {
            String phone = contactPhones.get(0);
            if (!JSONUtil.isEmpty(phone) && phone.trim()
                    .length() != 0) {
                new HljTracker.Builder(this).eventableId(work.getMerchant()
                        .getUserId())
                        .eventableType("Merchant")
                        .screen("package_detail")
                        .action("call")
                        .build()
                        .add();
                try {
                    callUp(Uri.parse("tel:" + phone.trim()));
                    new HljTracker.Builder(this).eventableId(work.getMerchant()
                            .getUserId())
                            .eventableType("Merchant")
                            .screen("package_detail")
                            .action("real_call")
                            .build()
                            .add();
                } catch (Exception ignored) {
                }
            }
            return;
        }

        if (contactDialog != null && contactDialog.isShowing()) {
            return;
        }
        if (contactDialog == null) {
            contactDialog = new Dialog(this, R.style.BubbleDialogTheme);
            contactDialog.setContentView(R.layout.dialog_contact_phones);
            Point point = JSONUtil.getDeviceSize(this);
            ListView listView = (ListView) contactDialog.findViewById(R.id.contact_list);
            ContactsAdapter contactsAdapter = new ContactsAdapter(this, contactPhones);
            listView.setAdapter(contactsAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String phone = (String) adapterView.getAdapter()
                            .getItem(i);
                    if (!JSONUtil.isEmpty(phone) && phone.trim()
                            .length() != 0) {
                        new HljTracker.Builder(WorkActivity.this).eventableId(work.getMerchant()
                                .getUserId())
                                .eventableType("Merchant")
                                .screen("package_detail")
                                .action("call")
                                .additional(String.valueOf(work.getId()))
                                .build()
                                .add();
                        try {
                            callUp(Uri.parse("tel:" + phone.trim()));
                            new HljTracker.Builder(WorkActivity.this).eventableId(work.getMerchant()
                                    .getUserId())
                                    .eventableType("Merchant")
                                    .screen("package_detail")
                                    .action("real_call")
                                    .additional(String.valueOf(work.getId()))
                                    .build()
                                    .add();
                        } catch (Exception ignored) {

                        }
                    }
                }
            });
            Window win = contactDialog.getWindow();
            ViewGroup.LayoutParams params = win.getAttributes();
            params.width = Math.round(point.x * 3 / 4);
            win.setGravity(Gravity.CENTER);
        }
        contactDialog.show();
    }

    @OnClick(R.id.btn_chat)
    public void onContact() {
        if (work == null || work.getId() == 0) {
            return;
        }
        if (Util.loginBindChecked(this, Constants.Login.CONTACT_LOGIN)) {
            if (work.getMerchant() != null && work.getMerchant()
                    .getUserId() > 0) {
                NewMerchant merchant = work.getMerchant();
                Intent intent = new Intent(this, WSCustomerChatActivity.class);
                MerchantUser user = new MerchantUser();
                user.setNick(merchant.getName());
                user.setId(merchant.getUserId());
                user.setAvatar(merchant.getLogoPath());
                user.setMerchantId(merchant.getId());
                intent.putExtra("user", user);
                intent.putExtra("ws_track", ModuleUtils.getWSTrack(work));
                if (merchant.getContactPhone() != null && !merchant.getContactPhone()
                        .isEmpty()) {
                    intent.putStringArrayListExtra("contact_phones", merchant.getContactPhone());
                }
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        }
    }

    @OnClick(R.id.li_collect)
    public void onCollect(View view) {
        if (work == null || work.getId() == 0) {
            return;
        }
        if (Util.loginBindChecked(this, Constants.Login.LIKE_LOGIN)) {
            if (work.isCollected()) {
                imgCollect.setImageResource(R.drawable.icon_collect_primary_44_44_normal);
                tvCollect.setText(getString(R.string.label_collect));
                new HljTracker.Builder(WorkActivity.this).eventableId(work.getId())
                        .eventableType("Package")
                        .screen("package_detail")
                        .action("del_collect")
                        .build()
                        .add();
                new HttpDeleteTask(this, new OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {
                        JSONObject object = (JSONObject) obj;
                        if (object != null && object.optBoolean("result", false)) {
                            work.setCollected(false);
                            Util.showToast(R.string.hint_discollect_complete, WorkActivity.this);
                        }
                    }

                    @Override
                    public void onRequestFailed(Object obj) {

                    }
                }).execute(Constants.getAbsUrl(String.format(Constants.HttpPath.WORK_COLLECT,
                        work.getId())));
            } else {
                imgCollect.setImageResource(R.drawable.icon_collect_primary_44_44_selected);
                tvCollect.setText(getString(R.string.label_has_collection));
                new HljTracker.Builder(WorkActivity.this).eventableId(work.getId())
                        .eventableType("Package")
                        .screen("package_detail")
                        .action("collect")
                        .build()
                        .add();
                new HttpPostTask(this, new OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {
                        JSONObject object = (JSONObject) obj;
                        if (object != null && object.optBoolean("result", false)) {
                            work.setCollected(true);
                            if (Util.isNewFirstCollect(WorkActivity.this, 4)) {
                                Util.showFirstCollectNoticeDialog(WorkActivity.this, 4);
                            } else {
                                Util.showToast(R.string.hint_collect_complete, WorkActivity.this);
                            }
                        }
                    }

                    @Override
                    public void onRequestFailed(Object obj) {

                    }
                }).execute(Constants.getAbsUrl(String.format(Constants.HttpPath.WORK_COLLECT,
                        work.getId())), null);
            }
        }
    }

    @OnClick(R.id.btn_share2)
    public void onShare(View view) {
        if (work == null || work.getId() == 0) {
            return;
        }
        if (shareDialog != null && shareDialog.isShowing()) {
            return;
        }
        if (shareUtil == null) {
            shareUtil = new ShareUtil(this, work.getShareInfo(this), progressBar, handler);
        }
        if (shareDialog == null) {
            shareDialog = Util.initShareDialog(this, shareUtil, null);
        }
        shareDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case HljShare.RequestCode.SHARE_TO_TXWEIBO:
                case HljShare.RequestCode.SHARE_TO_WEIBO:
                    handler.sendEmptyMessage(requestCode);
                    break;
                case Constants.Login.LIKE_LOGIN:
                    onCollect(null);
                    break;
                case Constants.Login.SUBMIT_LOGIN:
                    onBuy(null);
                    break;
                case Constants.Login.CONTACT_LOGIN:
                    onContact();
                    break;
                case Constants.Login.MSG_LOGIN:
                    onMessage(null);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        }
        noticeUtil.onResume();
        isResume = true;
        super.onResume();
        if (!JSONUtil.isEmpty(parentName)) {
            me.suncloud.marrymemo.util.TrackerUtil.onTCAgentPageStart(this, parentName);
        }
    }

    @Override
    protected void onPause() {
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        }
        noticeUtil.onPause();
        isResume = false;
        super.onPause();
        if (!JSONUtil.isEmpty(parentName)) {
            me.suncloud.marrymemo.util.TrackerUtil.onTCAgentPageEnd(this, parentName);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(appointmentSub);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    private class GetSnapshotTask extends AsyncTask<String, Object, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String json = JSONUtil.getStringFromUrl(params[0]);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus.getRetCode() != 0) {
                    setEmptyView(TextUtils.isEmpty(returnStatus.getErrorMsg()) ? null :
                            returnStatus.getErrorMsg());
                } else {
                    JSONObject data = jsonObject.optJSONObject("data");
                    initWorkJson(data.optJSONObject("work"));
                }
            } else {
                setEmptyView(null);
            }
            super.onPostExecute(jsonObject);
        }
    }

}
