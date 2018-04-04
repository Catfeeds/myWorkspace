package com.hunliji.marrybiz.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.MessageEvent;
import com.hunliji.marrybiz.model.Privilege;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.popuptip.PopupRule;
import com.hunliji.marrybiz.view.coupon.CouponEduActivity;
import com.hunliji.marrybiz.view.coupon.MyCouponListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import rx.Subscription;

/**
 * Created by Suncloud on 2016/1/9.
 */
public class SalesManagementActivity extends HljBaseActivity implements AdapterView
        .OnItemClickListener, PullToRefreshBase.OnRefreshListener<ListView> {

    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private PrivilegeAdapter adapter;
    private ArrayList<Privilege> privileges;
    private int size;
    private GetRecordsTask recordsTask;
    private MerchantUser merchantUser;
    private Dialog dialog;
    private Subscription rxBusEventSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        ButterKnife.bind(this);
        merchantUser = Session.getInstance()
                .getCurrentUser(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        size = Math.round(dm.density * 36);
        privileges = new ArrayList<>();
        adapter = new PrivilegeAdapter();
        listView.setOnRefreshListener(this);
        listView.getRefreshableView()
                .setOnItemClickListener(this);
        listView.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);
        recordsTask = new GetRecordsTask();
        recordsTask.executeOnExecutor(Constants.LISTTHEADPOOL,
                Constants.getAbsUrl(Constants.HttpPath.RECORD_LIST));
        registerRxBusEvent();
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case CREATE_COUPON_SUCCESS:
                                    if (privileges.isEmpty()) {
                                        return;
                                    }
                                    int size = privileges.size() - 1;
                                    for (int i = size; i >= 0; i--) {
                                        Privilege privilege = privileges.get(i);
                                        if (privilege.getId() == 20) {
                                            privilege.setStatus(1);
                                            View view = listView.getRefreshableView()
                                                    .getChildAt(i + 1);
                                            if (view == null) {
                                                return;
                                            }
                                            ViewHolder holder = (ViewHolder) view.getTag();
                                            if (holder == null) {
                                                return;
                                            }
                                            holder.status.setText(R.string
                                                    .hint_privilege_status1_2);
                                            break;
                                        }
                                    }
                                    break;
                            }
                        }
                    });
        }
    }

    private class GetRecordsTask extends AsyncTask<String, Object, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                String json = JSONUtil.getStringFromUrl(SalesManagementActivity.this, params[0]);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json).optJSONArray("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray array) {
            progressBar.setVisibility(View.GONE);
            listView.onRefreshComplete();
            if (array != null) {
                privileges.clear();
                Privilege privilege1 = null;
                Privilege privilege2 = null;
                if (array.length() > 0) {
                    for (int i = 0, size = array.length(); i < size; i++) {
                        Privilege privilege = new Privilege(array.optJSONObject(i));
                        if (privilege.getId() == 17) {
                            privilege1 = privilege;
                        } else if (privilege.getId() == 18) {
                            privilege2 = privilege;
                        }
                        privileges.add(privilege);
                    }
                }
                if (privilege1 != null & privilege2 != null && privilege2.isLevelAchieved()) {
                    privileges.remove(privilege1);
                }
                adapter.notifyDataSetChanged();
            }
            if (privileges.isEmpty()) {
                View emptyView = listView.getRefreshableView()
                        .getEmptyView();
                if (emptyView == null) {
                    emptyView = findViewById(R.id.empty_hint_layout);
                    listView.getRefreshableView()
                            .setEmptyView(emptyView);
                }
                ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id.img_empty_hint);
                ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
                TextView emptyHintTextView = (TextView) emptyView.findViewById(R.id
                        .text_empty_hint);
                emptyHintTextView.setVisibility(View.VISIBLE);
                if (JSONUtil.isNetworkConnected(SalesManagementActivity.this)) {
                    imgEmptyHint.setVisibility(View.VISIBLE);
                    imgNetHint.setVisibility(View.GONE);
                    emptyHintTextView.setText(R.string.no_item);
                } else {
                    imgEmptyHint.setVisibility(View.GONE);
                    imgNetHint.setVisibility(View.VISIBLE);
                    emptyHintTextView.setText(R.string.net_disconnected);
                }
            }
            recordsTask = null;
            super.onPostExecute(array);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Privilege privilege = (Privilege) parent.getAdapter()
                .getItem(position);
        if (privilege != null && privilege.getId() > 0) {
            if (!merchantUser.isPro()) {
                PopupRule.getDefault()
                        .showBondDialog(SalesManagementActivity.this,
                                getString(R.string.hint_merchant_pro__qa),
                                0);
                return;
            } else if (privilege.getId() == 6 || privilege.getId() == 5) {
                if (!merchantUser.isBondSign()) {
                    PopupRule.getDefault()
                            .showBondDialog(SalesManagementActivity.this,
                                    getString(R.string.hint_privilege_bond_sign2),
                                    2);
                    return;
                }
            } else if (!privilege.isLevelAchieved() && privilege.getLevel() > 0) {
                int level;
                switch (privilege.getLevel()) {
                    case 1:
                        level = R.string.label_level1;
                        break;
                    case 2:
                        level = R.string.label_level2;
                        break;
                    case 3:
                        level = R.string.label_level3;
                        break;
                    default:
                        level = R.string.label_level4;
                        break;
                }
                PopupRule.getDefault()
                        .showBondDialog(SalesManagementActivity.this,
                                getString(R.string.hint_privilege_level_achieved2,
                                        getString(level)),
                                1);
                return;
            }
            if (privilege.getId() == 17 || privilege.getId() == 18) {
                Intent intent = new Intent(this, WorkMarketActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
            //我的优惠券界面
            else if (privilege.getId() == 20) {
                Intent intent = new Intent();
                if (privilege.getStatus() == 1) {
                    intent.setClass(this, MyCouponListActivity.class);
                } else {
                    intent.setClass(this, CouponEduActivity.class);
                    intent.putExtra("path", privilege.getUrl());
                }
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            } else if (!JSONUtil.isEmpty(privilege.getUrl())) {
                Intent intent = new Intent(this, PrivilegeActivity.class);
                intent.putExtra("privilege", privilege);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        }
    }


    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (recordsTask == null) {
            recordsTask = new GetRecordsTask();
            recordsTask.executeOnExecutor(Constants.LISTTHEADPOOL,
                    Constants.getAbsUrl(Constants.HttpPath.RECORD_LIST));
        }
    }

    public void setViewValue(View view, Privilege privilege, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.line.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        String status = null;
        if (privilege.getId() == 6 || privilege.getId() == 5) {
            if (!merchantUser.isPro() || !merchantUser.isBondSign()) {
                status = getString(R.string.hint_privilege_bond_sign);
            }
        } else if ((!merchantUser.isPro() || !privilege.isLevelAchieved()) && privilege.getLevel
                () > 0) {
            int level;
            switch (privilege.getLevel()) {
                case 1:
                    level = R.string.label_level1;
                    break;
                case 2:
                    level = R.string.label_level2;
                    break;
                case 3:
                    level = R.string.label_level3;
                    break;
                default:
                    level = R.string.label_level4;
                    break;
            }
            status = getString(R.string.hint_privilege_level_achieved, getString(level));
        } else if (!merchantUser.isPro()) {
            status = getString(R.string.hint_privilege_merchant_pro);
        }
        if (privilege.getId() != 17 && privilege.getId() != 18 && JSONUtil.isEmpty(status)) {
            switch (privilege.getStatus()) {
                case 1:
                    status = getString(privilege.getId() == 20 ? R.string
                            .hint_privilege_status1_2 : R.string.hint_privilege_status1);
                    break;
                case 2:
                    status = getString(R.string.hint_privilege_status2);
                    break;
                case 3:
                    status = getString(R.string.hint_privilege_status3);
                    break;
                case 4:
                    status = getString(R.string.hint_privilege_status4);
                    break;
                default:
                    status = getString(R.string.hint_privilege_status0);
                    break;
            }
        }
        holder.status.setText(status);
        holder.name.setText(privilege.getName());
        holder.describe.setText(privilege.getDescribe());
        String path = JSONUtil.getImagePathForRound(privilege.getLogo(), size);
        if (!JSONUtil.isEmpty(path)) {
            if (!path.equals(holder.icon.getTag())) {
                ImageLoadTask task = new ImageLoadTask(holder.icon, 0, true);
                holder.icon.setTag(path);
                task.loadImage(path,
                        size,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
            }
        } else {
            holder.icon.setTag(null);
            holder.icon.setImageBitmap(null);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'level_privilege_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.line)
        View line;
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.describe)
        TextView describe;
        @BindView(R.id.status)
        TextView status;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void onEvent(MessageEvent event) {
        if (event.getType() == 6 && event.getObject() != null && event.getObject() instanceof
                Privilege && !privileges.isEmpty()) {
            Privilege privilege = (Privilege) event.getObject();
            for (int i = 0, size = privileges.size(); i < size; i++) {
                Privilege privilege1 = privileges.get(i);
                if (privilege1.getId()
                        .equals(privilege.getId())) {
                    privileges.set(i, privilege);
                    adapter.notifyDataSetChanged();
                    onRefresh(null);
                    return;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        if (!EventBus.getDefault()
                .isRegistered(this)) {
            EventBus.getDefault()
                    .register(this);
        }
        super.onResume();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (EventBus.getDefault()
                .isRegistered(this)) {
            EventBus.getDefault()
                    .unregister(this);
        }
        CommonUtil.unSubscribeSubs(rxBusEventSub);
    }

    class PrivilegeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return privileges == null ? 0 : privileges.size();
        }

        @Override
        public Object getItem(int position) {
            return privileges.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(SalesManagementActivity.this)
                        .inflate(R.layout.level_privilege_item, parent, false);
            }
            setViewValue(convertView, privileges.get(position), position);
            return convertView;
        }
    }
}
