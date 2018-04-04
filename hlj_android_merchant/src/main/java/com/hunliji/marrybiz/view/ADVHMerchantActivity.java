package com.hunliji.marrybiz.view;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.ADVHHelper;
import com.hunliji.marrybiz.model.ADVHMerchant;
import com.hunliji.marrybiz.model.ADVHMerchantHistory;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.MessageEvent;
import com.hunliji.marrybiz.model.Status;
import com.hunliji.marrybiz.task.NewHttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.chat.WSMerchantChatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by Suncloud on 2015/12/22.
 */
public class ADVHMerchantActivity extends HljBaseActivity implements View.OnClickListener {


    @BindView(R.id.helper_name)
    TextView helperName;
    @BindView(R.id.helper_phone)
    TextView helperPhone;
    @BindView(R.id.phone_arrow)
    ImageView phoneArrow;
    @BindView(R.id.contact_list)
    LinearLayout contactList;
    @BindView(R.id.remark)
    TextView remark;
    @BindView(R.id.history_list)
    LinearLayout historyList;
    @BindView(R.id.info_layout)
    ScrollView infoLayout;
    @BindView(R.id.count_down)
    TextView countDown;
    @BindView(R.id.count_down_layout)
    LinearLayout countDownLayout;
    @BindView(R.id.action)
    Button action;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.phone_layout)
    LinearLayout phoneLayout;
    @BindView(R.id.remark_layout)
    RelativeLayout remarkLayout;
    @BindView(R.id.status)
    TextView status;

    private ADVHMerchant advhMerchant;
    private SimpleDateFormat simpleDateFormat;
    //    private ExpiredTimeDown expiredTimeDown;
    private Dialog dialog;
    private Dialog dialog2;
    private int count;
    private Dialog copyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long id = getIntent().getLongExtra("id", 0);
        Integer customerCount = (Integer) getIntent().getSerializableExtra("customer_count");
        advhMerchant = (ADVHMerchant) getIntent().getSerializableExtra("advhMerchant");
        if (advhMerchant != null) {
            id = advhMerchant.getId();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advh_merchant);
        setOkButton(R.drawable.icon_chat_bubble_primary_44_43);
        hideOkButton();
        ButterKnife.bind(this);
        phoneLayout.setOnClickListener(this);
        action.setOnClickListener(this);
        if (customerCount == null) {
            count = -1;
            MerchantUser user = Session.getInstance().getCurrentUser(this);
            new GetCountTask().executeOnExecutor(Constants.LISTTHEADPOOL, Constants.getAbsUrl
                    (String.format(Constants.HttpPath.ADVH_PRICE_LIST, user.getMerchantId())));
        } else {
            count = customerCount;
        }
        if (advhMerchant != null && (!advhMerchant.isSpecial() || advhMerchant.getStatus() > 0)) {
            setAdvhMerchantInfo();
        } else if (advhMerchant != null) {
            progressBar.setVisibility(View.VISIBLE);
            changeStatus(id, 1, advhMerchant.isSpecial());
            return;
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
        new GetADVHMerchantTask().executeOnExecutor(Constants.INFOTHEADPOOL, Constants.getAbsUrl
                (String.format(Constants.HttpPath.ADVH_MERCHANT_INFO_URL, id)));
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        if (advhMerchant == null || advhMerchant.getStatus() == 0 || advhMerchant.getStatus() ==
                2 || advhMerchant.getStatus() == 5) {
            return;
        }
        ADVHHelper helper = advhMerchant.getHelper();
        if (helper != null && helper.getUserId() > 0) {
            CustomerUser user = new CustomerUser();
            user.setId(helper.getUserId());
            user.setNick(helper.getNick());
            user.setAvatar(helper.getAvatar());
            Intent intent = new Intent(this, WSMerchantChatActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    private void setAdvhMerchantInfo() {
        infoLayout.setVisibility(View.VISIBLE);
        if (advhMerchant.getStatus() == 4 || advhMerchant.getStatus() == 2 || advhMerchant
                .getStatus() == 5) {
            bottomLayout.setVisibility(View.GONE);
            status.setVisibility(View.VISIBLE);
            if (advhMerchant.getStatus() == 2) {
                status.setText(R.string.label_adv_status2);
            } else {
                status.setText(advhMerchant.getStatus() == 4 ? R.string.label_adv_status4 : R
                        .string.label_adv_status5);
            }
        } else if (advhMerchant.getStatus() == 0) {
            long timeDown = advhMerchant.getExpiredAt() - new Date().getTime();
            if (timeDown >= 1000) {
                bottomLayout.setVisibility(View.VISIBLE);
                action.setText(R.string.btn_adv_action1);
                countDownLayout.setVisibility(View.VISIBLE);
                countDown.setText(getString(R.string.label_advh_merchant_count2, advhMerchant
                        .getTotalNum()));
                //                if (expiredTimeDown != null) {
                //                    expiredTimeDown.cancel();
                //                    expiredTimeDown = null;
                //                }
                //                expiredTimeDown = new ExpiredTimeDown(timeDown, 1000);
                //                expiredTimeDown.start();
                status.setVisibility(View.GONE);
            } else {
                advhMerchant.setStatus(2);
                advhMerchant.setUpdatedAt(advhMerchant.getExpiredAt());
                MessageEvent event = new MessageEvent(5, advhMerchant);
                EventBus.getDefault().post(event);
                bottomLayout.setVisibility(View.GONE);
                status.setVisibility(View.VISIBLE);
                status.setText(R.string.label_adv_status2);
                new GetADVHMerchantTask().executeOnExecutor(Constants.INFOTHEADPOOL, Constants
                        .getAbsUrl(String.format(Constants.HttpPath.ADVH_MERCHANT_INFO_URL,
                                advhMerchant.getId())));
            }
        } else {
            bottomLayout.setVisibility(View.VISIBLE);
            countDownLayout.setVisibility(View.GONE);
            action.setText(advhMerchant.getStatus() == 1 ? R.string.btn_adv_action5 : R.string
                    .btn_adv_action2);
            status.setVisibility(View.VISIBLE);
            status.setText(advhMerchant.getStatus() == 1 ? R.string.label_adv_status1 : R.string
                    .label_adv_status3);
        }
        if (status.getVisibility() == View.VISIBLE) {
            helperName.setPadding(0, 0, Math.round(status.getPaint().measureText(status.getText()
                    .toString()) + status.getPaddingLeft() * 4), 0);
        } else {
            helperName.setPadding(0, 0, 0, 0);
        }
        ADVHHelper helper = advhMerchant.getHelper();
        if (helper != null) {
            helperName.setText(helper.getRealName());
            helperPhone.setText(helper.getPhone());
            phoneLayout.setOnLongClickListener(new CopyListener("电话", helper.getPhone()));
            if (helper.getContacts() != null && !helper.getContacts().isEmpty()) {
                contactList.setVisibility(View.VISIBLE);
                LinkedHashMap<String, String> contacts = helper.getContacts();
                contactList.removeAllViews();
                for (Map.Entry<String, String> entry : contacts.entrySet()) {
                    View view = View.inflate(ADVHMerchantActivity.this, R.layout
                            .adv_helper_contact_item, null);
                    TextView key = (TextView) view.findViewById(R.id.key);
                    TextView value = (TextView) view.findViewById(R.id.value);
                    key.setText(entry.getKey());
                    value.setText(entry.getValue());
                    view.setClickable(true);
                    view.setOnLongClickListener(new CopyListener(entry.getKey(), entry.getValue()));
                    contactList.addView(view);
                }
            } else {
                contactList.setVisibility(View.GONE);
            }
        }
        if (advhMerchant.getStatus() != 0 && advhMerchant.getStatus() != 2 && advhMerchant
                .getStatus() != 5) {
            phoneArrow.setVisibility(View.VISIBLE);
            phoneLayout.setClickable(true);
            if (helper != null && helper.getUserId() > 0) {
                showOkButton();
            } else {
                hideOkButton();
            }
        } else {
            phoneArrow.setVisibility(View.GONE);
            phoneLayout.setClickable(false);
            hideOkButton();
        }
        if (JSONUtil.isEmpty(advhMerchant.getRemark())) {
            remarkLayout.setVisibility(View.GONE);
        } else {
            remarkLayout.setVisibility(View.VISIBLE);
            remark.setText(advhMerchant.getRemark());
        }
    }

    private void setHistoryList() {
        if (advhMerchant.getHistories() != null && !advhMerchant.getHistories().isEmpty()) {
            historyList.setVisibility(View.VISIBLE);
            ArrayList<ADVHMerchantHistory> histories = advhMerchant.getHistories();
            int size = histories.size();
            int count = historyList.getChildCount();
            if (count > size) {
                historyList.removeViews(size, count - size);
            }
            for (int i = 0; i < size; i++) {
                View view = historyList.getChildAt(i);
                if (view == null) {
                    view = View.inflate(ADVHMerchantActivity.this, R.layout.advh_history_item,
                            null);
                    historyList.addView(view);
                }
                ViewHolder holder = (ViewHolder) view.getTag();
                if (holder == null) {
                    holder = new ViewHolder(view);
                    view.setTag(holder);
                }
                if (simpleDateFormat == null) {
                    simpleDateFormat = new SimpleDateFormat(getString(R.string
                            .format_date_type11), Locale.getDefault());
                }
                ADVHMerchantHistory history = histories.get(i);
                holder.tvStatus.setText(history.getStatus());
                if (history.getCreatedAt() != null) {
                    holder.tvDate.setText(simpleDateFormat.format(history.getCreatedAt()));
                }
                if (i == 0) {
                    holder.lineTop.setVisibility(View.GONE);
                    holder.line1.setVisibility(View.VISIBLE);
                    holder.imgDot.setImageResource(R.drawable.icon_status_dot_r);
                    holder.tvDate.setTextColor(getResources().getColor(R.color.colorPrimary));
                    holder.tvStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    holder.lineTop.setVisibility(View.VISIBLE);
                    holder.line1.setVisibility(View.GONE);
                    holder.imgDot.setImageResource(R.drawable.icon_status_dot_g);
                    holder.tvDate.setTextColor(getResources().getColor(R.color.colorGray));
                    holder.tvStatus.setTextColor(getResources().getColor(R.color.colorGray));
                }
                if (i == size - 1) {
                    holder.line3.setVisibility(View.VISIBLE);
                    holder.lineBottom.setVisibility(View.GONE);
                } else {
                    holder.line3.setVisibility(View.GONE);
                    holder.lineBottom.setVisibility(View.VISIBLE);
                }

            }
        } else {
            historyList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phone_layout:
                if (advhMerchant.getStatus() == 0 || advhMerchant.getStatus() == 2 ||
                        advhMerchant.getStatus() == 5) {
                    return;
                }
                ADVHHelper helper = advhMerchant.getHelper();
                if (helper != null && !JSONUtil.isEmpty(helper.getPhone())) {
                    callUp(Uri.parse("tel:" + helper.getPhone().trim()));
                }
                break;
            case R.id.action:
                if (advhMerchant.getStatus() == 0) {
                    showDialog(advhMerchant.getId());
                } else if (advhMerchant.getStatus() == 1) {
                    changeStatus(advhMerchant.getId(), 3, advhMerchant.isSpecial());
                } else if (advhMerchant.getStatus() == 3) {
                    changeStatus(advhMerchant.getId(), 4, advhMerchant.isSpecial());
                }
                break;
            case R.id.btn_notice_cancel:
                dialog.dismiss();
                break;
            case R.id.dialog_msg_confirm:
                dialog2.dismiss();
                break;
        }
    }


    private void showDialog(final long id) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (dialog == null) {
            dialog = new Dialog(this, R.style.BubbleDialogTheme);
            dialog.setContentView(R.layout.dialog_confirm_notice);
            dialog.findViewById(R.id.btn_notice_cancel).setOnClickListener(this);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
        }
        Button button = (Button) dialog.findViewById(R.id.btn_notice_confirm);
        button.setText(count == 0 ? R.string.title_activity_buy_advh2 : R.string.btn_adv_action1);
        TextView noticeMsg = (TextView) dialog.findViewById(R.id.tv_notice_msg);
        noticeMsg.setText(count == 0 ? R.string.hint_adv_count_run_out : R.string
                .hint_adv_helper_view);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (count > 0) {
                    changeStatus(id, 1, advhMerchant.isSpecial());
                } else {
                    Intent intent = new Intent(ADVHMerchantActivity.this, BuyADVHActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            }
        });
        dialog.show();
    }

    private void showDialog2(int id) {
        if (dialog2 != null && dialog2.isShowing()) {
            return;
        }
        if (dialog2 == null) {
            dialog2 = new Dialog(this, R.style.BubbleDialogTheme);
            dialog2.setContentView(R.layout.dialog_msg_single_button);
            dialog2.findViewById(R.id.dialog_msg_content).setVisibility(View.GONE);
            dialog2.findViewById(R.id.dialog_msg_confirm).setOnClickListener(this);
            Window window = dialog2.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
        }
        TextView noticeMsg = (TextView) dialog2.findViewById(R.id.dialog_msg_title);
        noticeMsg.setText(id);
        dialog2.show();
    }

    private void changeStatus(long id, final int status, boolean isSpecial) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("status", status);
            progressBar.setVisibility(View.VISIBLE);
            new NewHttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    JSONObject object = (JSONObject) obj;
                    Status status1 = null;
                    if (object.optJSONObject("status") != null) {
                        status1 = new Status(object.optJSONObject("status"));
                    }
                    if (status1 != null && status1.getRetCode() == 0) {
                        advhMerchant = new ADVHMerchant(object.optJSONObject("data"));
                        if (advhMerchant.getStatus() == 5 || advhMerchant.getStatus() == 2) {
                            showDialog2(advhMerchant.getStatus() == 2 ? R.string
                                    .hint_adv_status2_change_err : R.string
                                    .hint_adv_status5_change_err);
                        }
                        MessageEvent event = new MessageEvent(5, advhMerchant);
                        EventBus.getDefault().post(event);
                        setAdvhMerchantInfo();
                        setHistoryList();
                        return;
                    }
                    int strId = 0;
                    switch (status) {
                        case 1:
                            strId = R.string.hint_adv_status1_change_err;
                            break;
                        case 3:
                            strId = R.string.hint_adv_status3_change_err;
                            break;
                        case 4:
                            strId = R.string.hint_adv_status4_change_err;
                            break;
                    }
                    if (status == 1 && status1 != null && status1.getRetCode() == 255) {
                        count = 0;
                        showDialog(advhMerchant.getId());
                    } else {
                        Util.showToast(ADVHMerchantActivity.this, status1 == null ? null :
                                status1.getErrorMsg(), strId);
                        //                        if(status == 1&& status1!=null&&status1
                        // .getRetCode()==261){
                        //                            new GetADVHMerchantTask().executeOnExecutor
                        // (Constants.INFOTHEADPOOL,Constants.getAbsUrl(String.format(Constants
                        // .HttpPath.ADVH_MERCHANT_INFO_URL,advhMerchant.getId())));
                        //                        }
                    }

                }

                @Override
                public void onRequestFailed(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    int strId = 0;
                    switch (status) {
                        case 1:
                            strId = R.string.hint_adv_status1_change_err;
                            break;
                        case 3:
                            strId = R.string.hint_adv_status3_change_err;
                            break;
                        case 4:
                            strId = R.string.hint_adv_status4_change_err;
                            break;
                    }
                    Util.showToast(ADVHMerchantActivity.this, null, strId);
                }
            }).executeOnExecutor(Constants.INFOTHEADPOOL, Constants.getAbsUrl(isSpecial ? Constants.
                    HttpPath.SPEIAL_ADVH_MERCHANT_CHANGE_STATUS : Constants.HttpPath.
                    ADVH_MERCHANT_CHANGE_STATUS), jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetCountTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String json = JSONUtil.getStringFromUrl(ADVHMerchantActivity.this, params[0]);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json).optJSONObject("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                count = jsonObject.optInt("customer_count");
            }
            super.onPostExecute(jsonObject);
        }
    }

    private class GetADVHMerchantTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String json = JSONUtil.getStringFromUrl(ADVHMerchantActivity.this, params[0]);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json).optJSONObject("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressBar.setVisibility(View.GONE);
            if (jsonObject != null) {
                ADVHMerchant advhMerchant1 = new ADVHMerchant(jsonObject);
                if (advhMerchant1.getId() > 0) {
                    if (advhMerchant1.isSpecial() && advhMerchant1.getStatus() == 0) {
                        changeStatus(advhMerchant1.getId(), 1, advhMerchant1.isSpecial());
                        return;
                    }
                    if (advhMerchant == null || advhMerchant.getStatus() != advhMerchant1
                            .getStatus()) {
                        advhMerchant = advhMerchant1;
                        MessageEvent event = new MessageEvent(5, advhMerchant1);
                        EventBus.getDefault().post(event);
                        setAdvhMerchantInfo();
                    } else {
                        advhMerchant = advhMerchant1;
                    }
                    setHistoryList();
                }
            } else if (advhMerchant == null) {
                findViewById(R.id.empty_hint_layout).setVisibility(View.VISIBLE);
                TextView textEmptyHint = (TextView) findViewById(R.id.text_empty_hint);
                textEmptyHint.setVisibility(View.VISIBLE);
                ImageView imgEmptyHint = (ImageView) findViewById(R.id.img_net_hint);
                imgEmptyHint.setVisibility(View.VISIBLE);
                textEmptyHint.setText(getString(R.string.net_disconnected));
            }
            super.onPostExecute(jsonObject);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'advh_history_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.line_top)
        View lineTop;
        @BindView(R.id.line_bottom)
        View lineBottom;
        @BindView(R.id.img_dot)
        ImageView imgDot;
        @BindView(R.id.line1)
        View line1;
        @BindView(R.id.tv_status)
        TextView tvStatus;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.line3)
        View line3;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    //    public class ExpiredTimeDown extends CountDownTimer {
    //
    //        public ExpiredTimeDown(long millisInFuture, long countDownInterval) {
    //            super(millisInFuture, countDownInterval);
    //        }
    //
    //        @Override
    //        public void onFinish() {
    //            setAdvhMerchantInfo();
    //        }
    //
    //        @Override
    //        public void onTick(long millisUntilFinished) {
    //            countDown.setText(getExpiredString(millisUntilFinished));
    //        }
    //    }

    @Override
    protected void onResume() {
        super.onResume();
        //        if (advhMerchant != null) {
        //            setAdvhMerchantInfo();
        //        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //        if (expiredTimeDown != null) {
        //            expiredTimeDown.cancel();
        //            expiredTimeDown = null;
        //        }
    }

    private class CopyListener implements View.OnLongClickListener {

        private String key;
        private String value;

        public CopyListener(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean onLongClick(View view) {
            ArrayList<String> list = new ArrayList<>();
            list.add(getString(R.string.action_copy) + key);

            copyDialog = new Dialog(ADVHMerchantActivity.this, R.style.BubbleDialogTheme);
            Point point = JSONUtil.getDeviceSize(ADVHMerchantActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_list, null);
            ListView actionList = (ListView) dialogView.findViewById(R.id.list);
            ArrayAdapter arrayAdapter = new ArrayAdapter<>(ADVHMerchantActivity.this, android.R
                    .layout.simple_list_item_1, android.R.id.text1, list);
            actionList.setAdapter(arrayAdapter);
            actionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    copyDialog.cancel();
                    // 只有复制一个选项
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService
                            (CLIPBOARD_SERVICE);
                    clipboardManager.setPrimaryClip(ClipData.newPlainText(getString(R.string
                            .app_name), value));
                    Toast.makeText(ADVHMerchantActivity.this, R.string.hint_post_copy, Toast
                            .LENGTH_SHORT).show();
                }
            });
            copyDialog.setContentView(dialogView);
            Window window = copyDialog.getWindow();
            window.getAttributes().width = Math.round(3 * point.x / 4);
            window.setGravity(Gravity.CENTER);
            copyDialog.show();

            return true;
        }
    }

}
