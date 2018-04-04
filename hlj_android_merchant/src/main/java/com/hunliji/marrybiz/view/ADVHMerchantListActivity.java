package com.hunliji.marrybiz.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.MenuAdapter;
import com.hunliji.marrybiz.adapter.ObjectBindAdapter;
import com.hunliji.marrybiz.model.ADVHHelper;
import com.hunliji.marrybiz.model.ADVHMerchant;
import com.hunliji.marrybiz.model.ADVHPrice;
import com.hunliji.marrybiz.model.Label;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.MessageEvent;
import com.hunliji.marrybiz.model.Status;
import com.hunliji.marrybiz.task.NewHttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.chat.WSMerchantChatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by Suncloud on 2015/12/21.
 */
public class ADVHMerchantListActivity extends HljBaseActivity implements View
        .OnClickListener, AdapterView.OnItemClickListener, PullToRefreshBase
        .OnRefreshListener<ListView>, AbsListView.OnScrollListener, ObjectBindAdapter
        .ViewBinder<ADVHMerchant> {

    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.helperCount2)
    TextView helperCount2;
    @BindView(R.id.helperViewCount2)
    TextView helperViewCount2;
    @BindView(R.id.count_layout2)
    LinearLayout countLayout2;
    @BindView(R.id.menu_list)
    ListView menuList;
    @BindView(R.id.menu_info_layout)
    RelativeLayout menuInfoLayout;

    private TextView helperCount;
    private TextView helperViewCount;
    private View countLayout;

    private ArrayList<ADVHMerchant> advhMerchants;
    private ObjectBindAdapter<ADVHMerchant> adapter;
    private int lastPage;
    private int currentPage;
    private View headerView;
    private View loadView;
    private TextView endView;
    private boolean isLoad;
    private boolean isEnd;
    private String currentUrl;
    private int status = -1;
    private ArrayList<Label> labels;
    private MenuAdapter menuAdapter;
    private SimpleDateFormat simpleDateFormat;
    private View emptyView;
    private View footerView;
    private Dialog dialog;
    private Dialog dialog2;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        advhMerchants = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, advhMerchants, R.layout.adv_merchant_item, this);
        menuAdapter = new MenuAdapter(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advh_merchant_list);
        setOkText(R.string.label_advh_rule);
        ButterKnife.bind(this);
        findViewById(R.id.filtrate).setOnClickListener(this);

        menuInfoLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (menuList.getAnimation() == null || menuList.getAnimation().hasEnded()) {
                    hideMenuAnimation();
                }
                return true;
            }
        });
        menuList.setItemsCanFocus(true);
        menuList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        menuList.setOnItemClickListener(this);
        menuList.setAdapter(menuAdapter);

        headerView = View.inflate(this, R.layout.adv_merchant_header, null);
        headerView.findViewById(R.id.history).setOnClickListener(this);
        headerView.findViewById(R.id.btn_buy).setOnClickListener(this);
        headerView.findViewById(R.id.btn_buy2).setOnClickListener(this);
        headerView.findViewById(R.id.filtrate).setOnClickListener(this);
        helperCount = (TextView) headerView.findViewById(R.id.helperCount);
        helperViewCount = (TextView) headerView.findViewById(R.id.helperViewCount);
        countLayout = headerView.findViewById(R.id.count_layout);

        footerView = View.inflate(this, R.layout.list_foot_no_more, null);
        loadView = footerView.findViewById(R.id.loading);
        emptyView = footerView.findViewById(R.id.empty_view);
        emptyView.getLayoutParams().height = 0;
        endView = (TextView) footerView.findViewById(R.id.no_more_hint);
        endView.setOnClickListener(this);

        listView.getRefreshableView().addHeaderView(headerView);
        listView.getRefreshableView().addFooterView(footerView);
        listView.getRefreshableView().setOnScrollListener(this);
        listView.getRefreshableView().setOnItemClickListener(this);
        listView.setOnRefreshListener(this);
        listView.setAdapter(adapter);
        MerchantUser user = Session.getInstance().getCurrentUser(this);
        currentPage = lastPage = 1;
        currentUrl = getCurrentUrl();
        progressBar.setVisibility(View.VISIBLE);
        new GetCountTask().executeOnExecutor(Constants.LISTTHEADPOOL, Constants.getAbsUrl(String
                .format(Constants.HttpPath.ADVH_PRICE_LIST, user.getMerchantId())));
        new GetADVHMerchantsTask().executeOnExecutor(Constants.LISTTHEADPOOL, String.format
                (currentUrl, currentPage));
        listView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int
                    oldLeft, int oldTop, int oldRight, int oldBottom) {
                int d = bottom - top - oldBottom + oldTop;
                if (d != 0) {
                    emptyView.getLayoutParams().height = emptyView.getLayoutParams().height + d;
                    emptyView.requestLayout();
                }
            }
        });
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        Intent intent = new Intent(this, HljWebViewActivity.class);
        intent.putExtra("path", Constants.getAbsUrl(Constants.HttpPath.ZHIKEBAO_WEB));
        intent.putExtra("title", getString(R.string.title_activity_advh_list));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    private String getCurrentUrl() {
        StringBuilder url = new StringBuilder(Constants.getAbsUrl(String.format(Constants
                .HttpPath.ADVH_MERCHANT_URL, 20)));
        if (status == 5) {
            url.append("&status[0]=2&status[1]=").append(status);
        } else if (status >= 0) {
            url.append("&status=").append(status);
        }
        url.append(Constants.PAGE_COUNT);
        return url.toString();
    }

    private void setLabels() {
        MerchantUser merchantUser = Session.getInstance().getCurrentUser(this);
        labels = new ArrayList<>();
        Label label1 = new Label(new JSONObject());
        label1.setName(getString(R.string.label_all));
        label1.setId(-1);
        labels.add(label1);
        Label label2 = new Label(new JSONObject());
        label2.setName(getString(R.string.label_adv_status0));
        labels.add(label2);
        Label label3 = new Label(new JSONObject());
        label3.setName(getString(R.string.label_adv_status1));
        label3.setId(1);
        labels.add(label3);
        if (!merchantUser.isOrderSpecial()) {
            Label label4 = new Label(new JSONObject());
            label4.setName(getString(R.string.label_adv_status2));
            label4.setId(2);
            labels.add(label4);
        }
        Label label5 = new Label(new JSONObject());
        label5.setName(getString(R.string.label_adv_status3));
        label5.setId(3);
        labels.add(label5);
        Label label6 = new Label(new JSONObject());
        label6.setName(getString(R.string.label_adv_status4));
        label6.setId(4);
        labels.add(label6);
        if (!merchantUser.isOrderSpecial()) {
            Label label7 = new Label(new JSONObject());
            label7.setName(getString(R.string.label_adv_status5));
            label7.setId(5);
            labels.add(label7);
        }
        menuAdapter.setData(labels);
        menuList.setItemChecked(0, true);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.no_more_hint:
                onScrollStateChanged(listView.getRefreshableView(), AbsListView.OnScrollListener
                        .SCROLL_STATE_IDLE);
                break;
            case R.id.filtrate:
                if (menuInfoLayout.getVisibility() == View.VISIBLE) {
                    hideMenuAnimation();
                } else {
                    showMenuAnimation();
                }
                break;
            case R.id.history:
                intent = new Intent(this, ADVHPurchaseHistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                break;
            case R.id.btn_buy:
            case R.id.btn_buy2:
                intent = new Intent(this, BuyADVHActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                break;
            case R.id.btn_notice_cancel:
                dialog.dismiss();
                break;
            case R.id.dialog_msg_confirm:
                dialog2.dismiss();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object object = parent.getAdapter().getItem(position);
        if (object != null) {
            if (object instanceof ADVHMerchant) {
                Intent intent = new Intent(this, ADVHMerchantActivity.class);
                intent.putExtra("customer_count", Integer.valueOf(count));
                intent.putExtra("advhMerchant", (ADVHMerchant) object);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            } else if (object instanceof Label) {
                menuList.setItemChecked(position, true);
                hideMenuAnimation();
                Label label = (Label) object;
                if (label.getId().intValue() != status) {
                    status = label.getId().intValue();
                    loadView.setVisibility(View.GONE);
                    endView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    emptyView.getLayoutParams().height = 0;
                    emptyView.requestLayout();
                    advhMerchants.clear();
                    adapter.notifyDataSetChanged();
                    currentUrl = getCurrentUrl();
                    currentPage = lastPage = 1;
                    new GetADVHMerchantsTask().executeOnExecutor(Constants.LISTTHEADPOOL, String
                            .format(currentUrl, currentPage));
                }

            }
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            currentPage = 1;
            MerchantUser user = Session.getInstance().getCurrentUser(this);
            new GetCountTask().executeOnExecutor(Constants.LISTTHEADPOOL, Constants.getAbsUrl
                    (String.format(Constants.HttpPath.ADVH_PRICE_LIST, user.getMerchantId())));
            new GetADVHMerchantsTask().executeOnExecutor(Constants.LISTTHEADPOOL, String.format
                    (currentUrl, currentPage));
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    if (JSONUtil.isNetworkConnected(this)) {
                        loadView.setVisibility(View.VISIBLE);
                        endView.setVisibility(View.GONE);
                        currentPage++;
                        new GetADVHMerchantsTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                                String.format(currentUrl, currentPage));
                    } else {
                        endView.setVisibility(View.VISIBLE);
                        loadView.setVisibility(View.GONE);
                        endView.setText(R.string.hint_net_disconnected);
                    }
                } else {
                    break;
                }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int
            totalItemCount) {
        if (countLayout.getTop() + headerView.getTop() <= 0 || firstVisibleItem > 1) {
            if (countLayout2.getVisibility() != View.VISIBLE) {
                countLayout2.setVisibility(View.VISIBLE);
            }
        } else if (countLayout2.getVisibility() == View.VISIBLE) {
            countLayout2.setVisibility(View.GONE);
        }
        if (totalItemCount < 1) {
            return;
        }
        ListView list = listView.getRefreshableView();
        if (list.getChildCount() == 0) {
            return;
        }
        int headerIndex = list.indexOfChild(headerView);
        int footerIndex = list.indexOfChild(footerView);
        int height = 0;
        if (headerIndex >= 0 && footerIndex >= 0) {
            height = view.getHeight() - footerView.getBottom() + headerView.getBottom() +
                    emptyView.getHeight() - countLayout.getHeight();
        }
        if (emptyView.getHeight() != height) {
            emptyView.getLayoutParams().height = height;
            emptyView.requestLayout();
        }
    }

    @Override
    public void setViewValue(View view, ADVHMerchant advhMerchant, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        if (advhMerchant.getHelper() != null) {
            holder.helperName.setText(advhMerchant.getHelper().getRealName());
            holder.phoneNumber.setText(getString(R.string.label_adv_phone, advhMerchant.getHelper
                    ().getPhone()));
        }
        if (!JSONUtil.isEmpty(advhMerchant.getRemark())) {
            holder.remark.setVisibility(View.VISIBLE);
            holder.remark.setText(getString(R.string.label_adv_remark, advhMerchant.getRemark()));
        } else {
            holder.remark.setVisibility(View.GONE);
        }
        holder.action1.setOnClickListener(new OnActionClickListener(advhMerchant));
        holder.action2.setOnClickListener(new OnActionClickListener(advhMerchant));
        holder.action3.setOnClickListener(new OnActionClickListener(advhMerchant));
        holder.action4.setOnClickListener(new OnActionClickListener(advhMerchant));
        showTimeDown(view, advhMerchant);
    }

    private void showTimeDown(View view, ADVHMerchant advhMerchant) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        if (advhMerchant.getStatus() == 0) {
            long timeDown = advhMerchant.getExpiredAt() - new Date().getTime();
            if (advhMerchant.isSpecial() || timeDown >= 1000) {
                holder.actionsLayout.setVisibility(View.VISIBLE);
                holder.action1.setVisibility(View.VISIBLE);
                holder.action2.setVisibility(View.GONE);
                holder.action3.setVisibility(View.GONE);
                holder.action4.setVisibility(View.GONE);
                if (advhMerchant.isSpecial()) {
                    holder.status.setVisibility(View.VISIBLE);
                    holder.status.setText(R.string.label_adv_status0);
                    holder.time.setTextColor(getResources().getColor(R.color.colorGray));
                    if (simpleDateFormat == null) {
                        simpleDateFormat = new SimpleDateFormat(getString(R.string
                                .format_date_type11), Locale.getDefault());
                    }
                    holder.time.setText(simpleDateFormat.format(advhMerchant.getUpdatedAt()));
                    holder.helperName.setPadding(0, 0, Math.round(holder.time.getPaint()
                            .measureText(holder.time.getText().toString()) + holder.time
                            .getPaddingLeft() + holder.time.getPaddingRight()), 0);
                } else {
                    holder.status.setVisibility(View.GONE);
                    holder.time.setText(getString(R.string.label_advh_merchant_count,
                            advhMerchant.getTotalNum()));
                    holder.time.setTextColor(getResources().getColor(R.color.colorPrimary));
                    holder.helperName.setPadding(0, 0, Math.round(holder.time.getPaint()
                            .measureText(holder.time.getText().toString()) + holder.time
                            .getPaddingLeft() + holder.time.getPaddingRight()), 0);
                }
                return;
            } else {
                advhMerchant.setStatus(2);
                advhMerchant.setUpdatedAt(advhMerchant.getExpiredAt());
            }
        }
        holder.status.setVisibility(View.VISIBLE);
        holder.action1.setVisibility(View.GONE);
        holder.time.setTextColor(getResources().getColor(R.color.colorGray));
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(getString(R.string.format_date_type11),
                    Locale.getDefault());
        }
        holder.time.setText(simpleDateFormat.format(advhMerchant.getUpdatedAt()));
        holder.helperName.setPadding(0, 0, Math.round(holder.time.getPaint().measureText(holder
                .time.getText().toString()) + holder.time.getPaddingLeft() + holder.time
                .getPaddingRight()), 0);
        if (advhMerchant.getStatus() == 2 || advhMerchant.getStatus() == 5) {
            holder.status.setText(advhMerchant.getStatus() == 2 ? R.string.label_adv_status2 : R
                    .string.label_adv_status5);
            holder.actionsLayout.setVisibility(View.GONE);
        } else {
            holder.actionsLayout.setVisibility(View.VISIBLE);
            switch (advhMerchant.getStatus()) {
                case 1:
                    holder.status.setText(R.string.label_adv_status1);
                    holder.action2.setVisibility(View.VISIBLE);
                    holder.action2.setText(R.string.btn_adv_action5);
                    break;
                case 3:
                    holder.status.setText(R.string.label_adv_status3);
                    holder.action2.setVisibility(View.VISIBLE);
                    holder.action2.setText(R.string.btn_adv_action2);
                    break;
                case 4:
                    holder.action2.setVisibility(View.GONE);
                    holder.status.setText(R.string.label_adv_status4);
                    break;
            }
            holder.action3.setVisibility(View.VISIBLE);
            if (advhMerchant.getHelper() != null && advhMerchant.getHelper().getUserId() > 0) {
                holder.action4.setVisibility(View.VISIBLE);
            } else {
                holder.action4.setVisibility(View.GONE);
            }
        }
    }

    //    private String getExpiredString(long timeDown) {
    //        int limitSecond = (int) timeDown / 1000;
    //        if (limitSecond > 0) {
    //            return getString(R.string.label_adv_expired, limitSecond / 60, limitSecond % 60);
    //        }
    //        return null;
    //    }

    private class OnActionClickListener implements View.OnClickListener {

        private ADVHMerchant advhMerchant;

        private OnActionClickListener(ADVHMerchant advhMerchant) {
            this.advhMerchant = advhMerchant;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.action1:
                    if (advhMerchant.getStatus() == 0) {
                        if (advhMerchant.isSpecial()) {
                            Intent intent = new Intent(ADVHMerchantListActivity.this,
                                    ADVHMerchantActivity.class);
                            intent.putExtra("customer_count", Integer.valueOf(count));
                            intent.putExtra("advhMerchant", advhMerchant);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim
                                    .activity_anim_default);
                        } else {
                            showDialog(advhMerchant);
                        }
                    }
                    break;
                case R.id.action2:
                    if (advhMerchant.getStatus() == 1) {
                        changeStatus(advhMerchant.getId(), 3, advhMerchant.isSpecial());
                    } else if (advhMerchant.getStatus() == 3) {
                        changeStatus(advhMerchant.getId(), 4, advhMerchant.isSpecial());
                    }
                    break;
                case R.id.action3:
                    if (advhMerchant.getStatus() == 0 || advhMerchant.getStatus() == 2 ||
                            advhMerchant.getStatus() == 5) {
                        return;
                    }
                    ADVHHelper helper = advhMerchant.getHelper();
                    if (helper != null && !JSONUtil.isEmpty(helper.getPhone())) {
                        callUp(Uri.parse("tel:" + helper.getPhone().trim()));
                    }
                    break;
                case R.id.action4:
                    if (advhMerchant.getStatus() == 0 || advhMerchant.getStatus() == 2 ||
                            advhMerchant.getStatus() == 5) {
                        return;
                    }
                    helper = advhMerchant.getHelper();
                    if (helper != null && helper.getUserId() > 0) {
                        CustomerUser user = new CustomerUser();
                        user.setId(helper.getUserId());
                        user.setNick(helper.getNick());
                        user.setAvatar(helper.getAvatar());
                        Intent intent = new Intent(ADVHMerchantListActivity.this,
                                WSMerchantChatActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim
                                .activity_anim_default);
                    }
                    break;
            }
        }
    }

    private void showDialog(final ADVHMerchant advhMerchant) {
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
        button.setText(count < 1 ? R.string.title_activity_buy_advh2 : R.string.btn_adv_action1);
        TextView noticeMsg = (TextView) dialog.findViewById(R.id.tv_notice_msg);
        noticeMsg.setText(count < 1 ? R.string.hint_adv_count_run_out : R.string
                .hint_adv_helper_view);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (count > 0) {
                    changeStatus(advhMerchant.getId(), 1, advhMerchant.isSpecial());
                } else {
                    Intent intent = new Intent(ADVHMerchantListActivity.this, BuyADVHActivity
                            .class);
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

    private void changeStatus(final long id, final int status, boolean isSpecial) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("status", status);
            progressBar.setVisibility(View.VISIBLE);
            new NewHttpPostTask(ADVHMerchantListActivity.this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    JSONObject object = (JSONObject) obj;
                    Status status1 = null;
                    if (object.optJSONObject("status") != null) {
                        status1 = new Status(object.optJSONObject("status"));
                    }
                    if (status1 != null && status1.getRetCode() == 0) {
                        ADVHMerchant advhMerchant = new ADVHMerchant(object.optJSONObject("data"));
                        if (advhMerchant.getStatus() == 5 || advhMerchant.getStatus() == 2) {
                            showDialog2(advhMerchant.getStatus() == 2 ? R.string
                                    .hint_adv_status2_change_err : R.string
                                    .hint_adv_status5_change_err);
                        }
                        if (advhMerchant.getId() > 0) {
                            for (ADVHMerchant advhMerchant1 : advhMerchants) {
                                if (advhMerchant.getId().equals(advhMerchant1.getId())) {
                                    int index = advhMerchants.indexOf(advhMerchant1);
                                    advhMerchants.set(index, advhMerchant);
                                    adapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                        if (status == 1) {
                            MerchantUser user = Session.getInstance().getCurrentUser
                                    (ADVHMerchantListActivity.this);
                            new GetCountTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                                    Constants.getAbsUrl(String.format(Constants.HttpPath
                                            .ADVH_PRICE_LIST, user.getMerchantId())));
                        }
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
                    Util.showToast(ADVHMerchantListActivity.this, status1 == null ? null :
                            status1.getErrorMsg(), strId);
                    //                    if(status == 1&& status1!=null&&status1.getRetCode()
                    // ==261){
                    //                        for (ADVHMerchant advhMerchant1 : advhMerchants) {
                    //                            if (advhMerchant1.getId()==id&&advhMerchant1
                    // .getAuthorizationStatusObb()==0) {
                    //                                advhMerchant1.setStatus(5);
                    //                                advhMerchant1.setUpdatedAt(advhMerchant1
                    // .getExpiredAt());
                    //                                adapter.notifyDataSetChanged();
                    //                                break;
                    //                            }
                    //                        }
                    //                    }

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
                    Util.showToast(ADVHMerchantListActivity.this, null, strId);
                }
            }).executeOnExecutor(Constants.INFOTHEADPOOL, Constants.getAbsUrl(isSpecial ?
                    Constants.HttpPath.SPEIAL_ADVH_MERCHANT_CHANGE_STATUS : Constants.HttpPath
                    .ADVH_MERCHANT_CHANGE_STATUS), jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetCountTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String json = JSONUtil.getStringFromUrl(ADVHMerchantListActivity.this, params[0]);
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
                TextView countView = (TextView) headerView.findViewById(R.id.count);
                countView.setText(String.valueOf(count));
                boolean b = count < 5;
                headerView.findViewById(R.id.hint).setVisibility(b ? View.VISIBLE : View.GONE);
                headerView.findViewById(R.id.btn_buy).setVisibility(b ? View.GONE : View.VISIBLE);
                headerView.findViewById(R.id.advh_buy_list_layout).setVisibility(b ? View.VISIBLE
                        : View.GONE);
                if (b) {
                    JSONArray array = jsonObject.optJSONArray("Template");
                    if (array != null && array.length() > 0) {
                        LinearLayout advhBuyList = (LinearLayout) headerView.findViewById(R.id
                                .advh_buy_list);
                        int viewCount = advhBuyList.getChildCount();
                        int size = array.length();
                        if (viewCount > size) {
                            advhBuyList.removeViews(size, viewCount - size);
                        }
                        for (int i = 0; i < size; i++) {
                            ADVHPrice advhPrice = new ADVHPrice(array.optJSONObject(i));
                            View view = advhBuyList.getChildAt(i);
                            if (view == null) {
                                view = View.inflate(ADVHMerchantListActivity.this, R.layout
                                        .advh_header_price_item, null);
                                advhBuyList.addView(view);
                            }
                            TextView tvAdvhCount = (TextView) view.findViewById(R.id.tv_advh_count);
                            TextView tvDiscount = (TextView) view.findViewById(R.id.tv_discount);
                            TextView tvTotalCount = (TextView) view.findViewById(R.id
                                    .tv_total_count);
                            TextView tvPrice = (TextView) view.findViewById(R.id.tv_price);

                            tvAdvhCount.setText(getString(R.string.label_advh_count, advhPrice
                                    .getQuantity()));
                            tvPrice.setText(getString(R.string.label_price2, Util
                                    .formatDouble2String(advhPrice.getPrice())));
                            if (advhPrice.getDiscountPercent() > 0 && advhPrice
                                    .getDiscountPercent() < 1) {
                                tvDiscount.setVisibility(View.VISIBLE);
                                tvDiscount.setText(getString(R.string.label_discount, Util
                                        .formatDouble2String(advhPrice.getDiscountPercent() * 10)));
                            } else {
                                tvDiscount.setVisibility(View.GONE);
                            }
                            if (advhPrice.getGiftNum() > 0) {
                                tvTotalCount.setVisibility(View.VISIBLE);
                                tvTotalCount.setText(getString(R.string.label_advh_total_count,
                                        advhPrice.getGiftNum(), advhPrice.getGiftNum() +
                                                advhPrice.getQuantity()));
                            } else {
                                tvTotalCount.setVisibility(View.INVISIBLE);
                            }
                        }
                    }

                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    private class GetADVHMerchantsTask extends AsyncTask<String, Object, JSONObject> {

        private String url;

        private GetADVHMerchantsTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(ADVHMerchantListActivity.this, url);
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
            if (isFinishing()) {
                return;
            }
            if (url.equals(String.format(currentUrl, currentPage))) {
                isLoad = false;
                progressBar.setVisibility(View.GONE);
                listView.onRefreshComplete();
                if (jsonObject != null) {
                    lastPage = currentPage;
                    JSONObject extendsJson = jsonObject.optJSONObject("extends");
                    if (extendsJson != null) {
                        int count = extendsJson.optInt("helper_count");
                        int noSeeCount = extendsJson.optInt("not_see");
                        int viewCount = extendsJson.optInt("helper_view_count");
                        MerchantUser user = Session.getInstance().getCurrentUser
                                (ADVHMerchantListActivity.this);
                        if (user.isOrderSpecial()) {
                            helperCount.setText(getString(R.string.label_helper_no_see_count,
                                    noSeeCount));
                            helperCount2.setText(getString(R.string.label_helper_no_see_count,
                                    noSeeCount));
                        } else {
                            helperCount.setText(getString(R.string.label_helper_count, count));
                            helperCount2.setText(getString(R.string.label_helper_count, count));
                        }
                        helperViewCount.setText(getString(R.string.label_helper_view_count,
                                viewCount));
                        helperViewCount2.setText(getString(R.string.label_helper_view_count,
                                viewCount));
                        countLayout.setVisibility(View.VISIBLE);
                    }
                    JSONArray array = jsonObject.optJSONArray("list");
                    if (currentPage == 1) {
                        advhMerchants.clear();
                    }
                    if (array != null && array.length() > 0) {
                        for (int i = 0, size = array.length(); i < size; i++) {
                            advhMerchants.add(new ADVHMerchant(array.optJSONObject(i)));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    int pageCount = jsonObject.optInt("page_count", 0);
                    isEnd = pageCount <= currentPage;
                    if (isEnd) {
                        endView.setVisibility(advhMerchants.isEmpty() ? View.GONE : View.VISIBLE);
                        loadView.setVisibility(View.GONE);
                        endView.setText(R.string.no_more);
                    } else {
                        endView.setVisibility(View.GONE);
                        loadView.setVisibility(View.INVISIBLE);
                    }
                } else if (!advhMerchants.isEmpty()) {
                    currentPage = lastPage;
                    endView.setVisibility(View.VISIBLE);
                    loadView.setVisibility(View.GONE);
                    endView.setText(R.string.hint_net_disconnected);
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'adv_merchant_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.helper_name)
        TextView helperName;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.phone_number)
        TextView phoneNumber;
        @BindView(R.id.remark)
        TextView remark;
        @BindView(R.id.action1)
        Button action1;
        @BindView(R.id.action2)
        Button action2;
        @BindView(R.id.action3)
        Button action3;
        @BindView(R.id.action4)
        Button action4;
        @BindView(R.id.actions_layout)
        RelativeLayout actionsLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void showMenuAnimation() {
        if (countLayout.getTop() + headerView.getTop() > 0 && listView.getRefreshableView()
                .getFirstVisiblePosition() <= 1) {
            listView.getRefreshableView().smoothScrollToPositionFromTop(2, countLayout.getHeight());
            countLayout2.setVisibility(View.VISIBLE);
        }
        if (labels == null || labels.isEmpty()) {
            setLabels();
        }
        menuInfoLayout.setVisibility(View.VISIBLE);
        TranslateAnimation scaleAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1, Animation
                .RELATIVE_TO_SELF, 0);
        scaleAnimation.setDuration(250);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                menuList.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                menuInfoLayout.setBackgroundResource(R.color.transparent_black);
            }
        });
        menuList.startAnimation(scaleAnimation);
    }

    private void hideMenuAnimation() {
        TranslateAnimation scaleAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation
                .RELATIVE_TO_SELF, -1);
        scaleAnimation.setDuration(250);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                menuList.setVisibility(View.VISIBLE);
                menuInfoLayout.setBackgroundResource(android.R.color.transparent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                menuList.setVisibility(View.GONE);
                menuInfoLayout.setVisibility(View.GONE);
            }
        });
        menuList.startAnimation(scaleAnimation);
    }

    public void onEvent(MessageEvent event) {
        if (event.getType() == 5 && event.getObject() != null && event.getObject() instanceof
                ADVHMerchant && !advhMerchants.isEmpty()) {
            ADVHMerchant advhMerchant = (ADVHMerchant) event.getObject();
            for (int i = 0, size = advhMerchants.size(); i < size; i++) {
                ADVHMerchant advhMerchant1 = advhMerchants.get(i);
                if (advhMerchant1.getId().equals(advhMerchant.getId())) {
                    advhMerchants.set(i, advhMerchant);
                    adapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        MerchantUser user = Session.getInstance().getCurrentUser(this);
        new GetCountTask().executeOnExecutor(Constants.LISTTHEADPOOL, Constants.getAbsUrl(String
                .format(Constants.HttpPath.ADVH_PRICE_LIST, user.getMerchantId())));
    }

    @Override
    protected void onResume() {
        //        mHandler.post(mRunnable);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        //        mHandler.removeCallbacks(mRunnable);
        super.onPause();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
