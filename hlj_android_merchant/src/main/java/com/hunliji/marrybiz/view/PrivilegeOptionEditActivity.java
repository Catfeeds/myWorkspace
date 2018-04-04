package com.hunliji.marrybiz.view;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ObjectBindAdapter;
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
import com.hunliji.marrybiz.widget.RoundProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by Suncloud on 2016/1/19.
 */
public class PrivilegeOptionEditActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<PrivilegeOption> {

    private Privilege privilege;
    private ListView listView;
    private ObjectBindAdapter<PrivilegeOption> adapter;
    private ArrayList<PrivilegeOption> options;
    private int size;
    private RoundProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        size = Math.round(dm.density * 60);
        privilege = getIntent().getParcelableExtra("privilege");
        options = new ArrayList<>();
        options.addAll(privilege.getOptions());
        adapter = new ObjectBindAdapter<>(this, options, R.layout.option_item_view, this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privilege_option_edit);
        setTitle(getString(R.string.title_activity_privilege_edit, privilege.getName()));
        View headerView = View.inflate(this, R.layout.option_item_header_view, null);
        View footerView = View.inflate(this, R.layout.option_item_footer_view, null);
        TextView title = (TextView) headerView.findViewById(R.id.title);
        TextView hint = (TextView) headerView.findViewById(R.id.hint);
        if (privilege.getRule() > 0) {
            hint.setText(getString(R.string.privilege_option_limit, privilege.getRule()));
        }
        title.setText(R.string.label_select_scope);
        View bottomLayout = footerView.findViewById(R.id.bottom_layout);
        bottomLayout.setBackgroundColor(Color.TRANSPARENT);
        bottomLayout.setPadding(bottomLayout.getPaddingLeft(),
                Math.round(dm.density * 30),
                bottomLayout.getPaddingRight(),
                bottomLayout.getPaddingBottom());
        footerView.findViewById(R.id.btn_action)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSave();
                    }
                });
        listView = (ListView) findViewById(R.id.list_view);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.addHeaderView(headerView, null, false);
        listView.addFooterView(footerView, null, false);
        listView.setAdapter(adapter);
        if (!options.isEmpty()) {
            for (int i = 0, size = options.size(); i < size; i++) {
                if (options.get(i)
                        .getStatus() > 0) {
                    listView.setItemChecked(i + 1, true);
                }
            }
        }
    }

    private void onSave() {
        if (privilege == null || options.isEmpty()) {
            return;
        }
        JSONArray ids = new JSONArray();
        for (int i = 0, size = options.size(); i < size; i++) {
            PrivilegeOption option = options.get(i);
            if (listView.isItemChecked(i + 1)) {
                option.setStatus(1);
                ids.put(option.getId());
            } else {
                option.setStatus(0);
            }
        }
        if (ids.length() < Math.max(1, privilege.getRule())) {
            Util.showToast(this,
                    getString(R.string.hint_privilege_option_rule,
                            Math.max(1, privilege.getRule())),
                    0);
            return;
        }
        try {
            String url;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("grade_privilege_id", privilege.getId());
            jsonObject.put("ids", ids);
            if (privilege.getStatus() > 0) {
                url = Constants.getAbsUrl(Constants.HttpPath.PRIVILEGE_EDIT);
                jsonObject.put("status", 1);
            } else {
                url = Constants.getAbsUrl(Constants.HttpPath.PRIVILEGE_ADD);
            }
            if (progressDialog == null) {
                progressDialog = JSONUtil.getRoundProgress(this);
            } else {
                progressDialog.show();
            }
            progressDialog.setCancelable(false);
            progressDialog.onLoadComplate();
            new NewHttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    if (isFinishing()) {
                        return;
                    }
                    JSONObject jsonObject = (JSONObject) obj;
                    Status status = new Status(jsonObject.optJSONObject("status"));
                    if (status.getRetCode() == 0) {
                        privilege.setStatus(3);
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.setCancelable(false);
                            progressDialog.onComplate();
                            progressDialog.setOnUpLoadComplate(new RoundProgressDialog
                                    .OnUpLoadComplate() {

                                @Override
                                public void onUpLoadCompleted() {
                                    EventBus.getDefault()
                                            .post(new MessageEvent(6, privilege));
                                    onBackPressed();
                                }
                            });
                        } else {
                            EventBus.getDefault()
                                    .post(new MessageEvent(6, privilege));
                            onBackPressed();
                        }
                        return;
                    }
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        Util.showToast(PrivilegeOptionEditActivity.this,
                                status.getErrorMsg(),
                                R.string.msg_failed_finish_order);
                    }

                }

                @Override
                public void onRequestFailed(Object obj) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        Util.showToast(PrivilegeOptionEditActivity.this,
                                null,
                                R.string.msg_failed_finish_order);
                    }

                }
            }, progressDialog).executeOnExecutor(Constants.INFOTHEADPOOL,
                    url,
                    jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setViewValue(View view, PrivilegeOption privilegeOption, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            holder.check.setVisibility(View.VISIBLE);
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

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'option_item_view.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
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
        @BindView(R.id.check)
        ImageView check;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
