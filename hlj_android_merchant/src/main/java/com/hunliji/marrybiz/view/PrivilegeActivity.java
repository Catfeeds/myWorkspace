package com.hunliji.marrybiz.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.utils.WebUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ObjectBindAdapter;
import com.hunliji.marrybiz.jsinterface.WebHandler;
import com.hunliji.marrybiz.model.MessageEvent;
import com.hunliji.marrybiz.model.Privilege;
import com.hunliji.marrybiz.model.PrivilegeOption;
import com.hunliji.marrybiz.model.Status;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.task.NewHttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.widget.ShSwitchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by Suncloud on 2016/1/19.
 */
public class PrivilegeActivity extends HljBaseActivity implements ShSwitchView
        .OnSwitchStateChangeListener, ObjectBindAdapter.ViewBinder<PrivilegeOption> {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.switch_default)
    ShSwitchView switchDefault;
    @BindView(R.id.switch_layout)
    LinearLayout switchLayout;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.privilege_content_layout)
    RelativeLayout privilegeContentLayout;
    @BindView(R.id.privilege_item_list)
    ListView privilegeItemList;
    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.btn_open)
    Button btnOpen;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tv_status_alert)
    TextView tvStatusAlert;

    private Privilege privilege;
    private View contentView;
    private ObjectBindAdapter<PrivilegeOption> adapter;
    private ArrayList<PrivilegeOption> options;
    private int size;
    private boolean isCreate;
    private boolean onClose;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        size = Math.round(getResources().getDisplayMetrics().density * 60);
        options = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, options, R.layout.option_item_view, this);
        privilege = getIntent().getParcelableExtra("privilege");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privilege);
        ButterKnife.bind(this);
        setTitle(privilege.getName());
        title.setText(privilege.getName());
        initWebView();
        View headerView = View.inflate(this, R.layout.option_item_header_view, null);
        View footerView = View.inflate(this, R.layout.option_item_footer_view, null);
        ((Button) footerView.findViewById(R.id.btn_action)).setText(R.string.btn_edit);
        footerView.findViewById(R.id.btn_action)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onEdit(null);
                    }
                });
        privilegeItemList.addHeaderView(headerView);
        privilegeItemList.addFooterView(footerView);
        privilegeItemList.setAdapter(adapter);
        loadUrl(privilege.getUrl());
        progressBar.setVisibility(View.VISIBLE);
        switchDefault.setOnSwitchStateChangeListener(this);
        isCreate = true;
        new GetPrivilegeTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                Constants.getAbsUrl(String.format(Constants.HttpPath.PRIVILEGE_INFO,
                        privilege.getId())));
    }

    public void onEdit(View view) {
        Intent intent;
        if (privilege.getOptions() != null && !privilege.getOptions()
                .isEmpty()) {
            intent = new Intent(this, PrivilegeOptionEditActivity.class);
        } else {
            intent = new Intent(this, PrivilegeContentEditActivity.class);
        }
        intent.putExtra("privilege", privilege);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void setViewValue(View view, PrivilegeOption privilegeOption, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.lineLayout.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        holder.name.setText(privilegeOption.getTitle());
        holder.describe.setText(privilegeOption.getDetail());
        String path = JSONUtil.getImagePathForRound(privilegeOption.getImagePath(), size);
        if (!JSONUtil.isEmpty(path)) {
            ImageLoadTask task = new ImageLoadTask(holder.icon, 0, true);
            holder.icon.setTag(path);
            task.loadImage(path,
                    size,
                    ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
        } else {
            holder.icon.setImageBitmap(null);
        }
    }

    public void onSwitch(View view) {
        if (switchDefault.isOn()) {
            if (onClose) {
                return;
            }
            if (dialog != null && dialog.isShowing()) {
                return;
            }
            if (dialog == null) {
                dialog = new Dialog(this, R.style.BubbleDialogTheme);
                dialog.setContentView(R.layout.dialog_confirm_notice);
                TextView noticeMsg = (TextView) dialog.findViewById(R.id.tv_notice_msg);
                noticeMsg.setText(getString(R.string.hint_privilege_close, privilege.getName()));
                dialog.findViewById(R.id.btn_notice_confirm)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                onClose();
                            }
                        });

                dialog.findViewById(R.id.btn_notice_cancel)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                Window window = dialog.getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                Point point = JSONUtil.getDeviceSize(this);
                params.width = Math.round(point.x * 27 / 32);
                window.setAttributes(params);
            }
            dialog.show();
        } else {
            onEdit(null);
        }
    }

    private void onClose() {
        onClose = true;
        progressBar.setVisibility(View.VISIBLE);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("grade_privilege_id", privilege.getId());
            if (privilege.getOptions() != null && !privilege.getOptions()
                    .isEmpty()) {
                JSONArray array = new JSONArray();
                for (PrivilegeOption option : privilege.getOptions()) {
                    array.put(option.getId());
                }
                jsonObject.put("ids", array);
            } else {
                jsonObject.put("content", privilege.getContent());
            }
            jsonObject.put("status", 0);
            new NewHttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    onClose = false;
                    JSONObject jsonObject = (JSONObject) obj;
                    Status status = new Status(jsonObject.optJSONObject("status"));
                    if (status.getRetCode() == 0) {
                        privilege.setStatus(2);
                        EventBus.getDefault()
                                .post(new MessageEvent(6, privilege));
                        return;
                    }
                    progressBar.setVisibility(View.GONE);
                    Util.showToast(PrivilegeActivity.this,
                            status.getErrorMsg(),
                            R.string.hint_close_day_fail);

                }

                @Override
                public void onRequestFailed(Object obj) {
                    onClose = false;
                    progressBar.setVisibility(View.GONE);
                    Util.showToast(PrivilegeActivity.this, null, R.string.hint_close_day_fail);

                }
            }).executeOnExecutor(Constants.INFOTHEADPOOL,
                    Constants.getAbsUrl(Constants.HttpPath.PRIVILEGE_EDIT),
                    jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetPrivilegeTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String json = JSONUtil.getStringFromUrl(PrivilegeActivity.this, params[0]);
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
            progressBar.setVisibility(View.GONE);
            if (jsonObject != null) {
                privilege.editPrivilege(jsonObject);
                if (privilege.getOptions() != null && !privilege.getOptions()
                        .isEmpty()) {
                    initListView();
                } else {
                    initContentView();
                }
                initView();
            } else if (isCreate) {
                View emptyView = findViewById(R.id.empty_hint_layout);
                ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
                TextView emptyHintTextView = (TextView) emptyView.findViewById(R.id
                        .text_empty_hint);
                emptyHintTextView.setVisibility(View.VISIBLE);
                imgNetHint.setVisibility(View.VISIBLE);
                emptyHintTextView.setText(R.string.net_disconnected);
            }
            isCreate = false;
            super.onPostExecute(jsonObject);
        }
    }

    private void initView() {
        switch (privilege.getStatus()) {
            case 0:
                btnOpen.setVisibility(View.VISIBLE);
                webView.setVisibility(View.VISIBLE);
                switchLayout.setVisibility(View.GONE);
                contentView.setVisibility(View.GONE);
                tvStatusAlert.setVisibility(View.GONE);
                break;
            case 2:
                btnOpen.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                switchLayout.setVisibility(View.VISIBLE);
                contentView.setVisibility(View.GONE);
                tvStatusAlert.setVisibility(View.GONE);
                switchDefault.setOn(false);
                break;
            default:
                if (privilege.getStatus() == 4) {
                    tvStatusAlert.setVisibility(View.VISIBLE);
                    tvStatusAlert.setText(getString(R.string.hint_privilege_refuse,
                            privilege.getReason()));
                } else if (privilege.getStatus() == 3) {
                    tvStatusAlert.setVisibility(View.VISIBLE);
                    tvStatusAlert.setText(getString(R.string.hint_merchant_reviewing));
                } else {
                    tvStatusAlert.setVisibility(View.GONE);
                }
                btnOpen.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
                switchLayout.setVisibility(View.VISIBLE);
                contentView.setVisibility(View.VISIBLE);
                switchDefault.setOn(true);
                break;
        }
    }

    private void loadUrl(String path) {
        path = WebUtil.addPathQuery(this, path);
        Map<String, String> header = WebUtil.getWebHeaders(this);
        if (!header.isEmpty()) {
            webView.loadUrl(path, header);
        } else {
            webView.loadUrl(path);
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initWebView() {
        webView.getSettings()
                .setJavaScriptEnabled(true);
        webView.getSettings()
                .setAllowFileAccess(true);
        //        wenbview缓存
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCachePath(getCacheDir().getAbsolutePath());
        webView.getSettings().setAppCacheEnabled(true);
        if (Constants.DEBUG) {
            webView.getSettings()
                    .setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings()
                    .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        WebHandler webHandler = new WebHandler(this, null, webView, null);
        webView.addJavascriptInterface(webHandler, "handler");

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!JSONUtil.isEmpty(url) && url.startsWith("tel:")) {
                    callUp(Uri.parse(url));
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    private void initContentView() {
        contentView = privilegeContentLayout;
        content.setText(privilege.getContent());
    }

    private void initListView() {
        contentView = privilegeItemList;
        options.clear();
        for (PrivilegeOption option : privilege.getOptions()) {
            if (option.getStatus() > 0) {
                options.add(option);
            }
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onSwitchStateChange(boolean isOn) {
        webView.setVisibility(isOn ? View.GONE : View.VISIBLE);
        contentView.setVisibility(isOn ? View.VISIBLE : View.GONE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault()
                .isRegistered(this)) {
            EventBus.getDefault()
                    .register(this);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (webView != null) {
            webView.loadUrl("about:blank");
            setContentView(new FrameLayout(this));
        }
        if (EventBus.getDefault()
                .isRegistered(this)) {
            EventBus.getDefault()
                    .unregister(this);
        }
    }

    public void onEvent(MessageEvent event) {
        if (event.getType() == 6 && event.getObject() != null && event.getObject() instanceof
                Privilege) {
            Privilege privilege1 = (Privilege) event.getObject();
            if (privilege1.getId()
                    .equals(privilege.getId())) {
                privilege = privilege1;
                if (privilege.getOptions() != null && !privilege.getOptions()
                        .isEmpty()) {
                    initListView();
                } else {
                    initContentView();
                }
                initView();
                new GetPrivilegeTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                        Constants.getAbsUrl(String.format(Constants.HttpPath.PRIVILEGE_INFO,
                                privilege.getId())));
            }
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'option_item_view.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     * .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.line_layout)
        View lineLayout;
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.describe)
        TextView describe;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
