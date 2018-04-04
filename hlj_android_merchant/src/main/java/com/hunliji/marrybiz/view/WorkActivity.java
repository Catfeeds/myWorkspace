package com.hunliji.marrybiz.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.fragment.WorkFragment;
import com.hunliji.marrybiz.fragment.WorkOldFragment;
import com.hunliji.marrybiz.model.Meta;
import com.hunliji.marrybiz.model.ReturnStatus;
import com.hunliji.marrybiz.model.Work;
import com.hunliji.marrybiz.task.NewHttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Suncloud on 2016/1/21.
 */
public class WorkActivity extends HljBaseNoBarActivity {

    private Work work;
    private JSONObject workObject;

    private RefreshFragment fragment;
    private boolean isSnapshot;

    private View progressBar;

    private final static int MSG_INIT_VIEW = 0xA00;
    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT_VIEW:
                    new GetWorkTask().onPostExecute(workObject);
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String workJson = getIntent().getStringExtra("workJson");
        isSnapshot = getIntent().getBooleanExtra("isSnapshot", false);
        if (!JSONUtil.isEmpty(workJson)) {
            try {
                workObject = new JSONObject(workJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        setActionBarPadding(findViewById(R.id.action_holder_layout),
                findViewById(R.id.action_holder_layout2));
        setSwipeBackEnable(false);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        if (isSnapshot) {
            TextView textView = (TextView) findViewById(R.id.title);
            textView.setText(R.string.title_activity_snapshot);
            String orderId = getIntent().getStringExtra("order_no");
            long prdid = getIntent().getLongExtra("work_id", 0);
            int prdtype = getIntent().getIntExtra("type", 0);
            try {
                JSONObject map = new JSONObject();
                map.put("order_no", orderId);
                map.put("prdid", prdid);
                map.put("prdtype", prdtype);
                map.put("admin", 1);
                new NewHttpPostTask(this, new OnHttpRequestListener() {

                    @Override
                    public void onRequestFailed(Object obj) {
                        setEmptyView(null);
                    }

                    @Override
                    public void onRequestCompleted(Object obj) {
                        JSONObject json = (JSONObject) obj;
                        initWorkJson(json.optJSONObject("work"));
                        Meta meta = new Meta(json.optJSONObject("meta"));
                        if (meta.getStatusCode() == 404 && !JSONUtil.isEmpty(meta.getErrorMsg())) {
                            setEmptyView(meta.getErrorMsg());
                        } else {
                            setEmptyView(null);
                        }

                    }
                }, null).execute(Constants.getAbsUrl(Constants.HttpPath.NEW_SNAPSHOT_INFO),
                        map.toString());
            } catch (JSONException ignored) {

            }
        } else if (workObject == null) {
            long id = getIntent().getLongExtra("w_id", 0);
            new GetWorkTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                    Constants.getAbsUrl(String.format(Constants.HttpPath.NEW_WORK_INFO, id)));
        } else {
            new Thread() {

                @Override
                public void run() {
                    super.run();
                    handler.sendEmptyMessageDelayed(MSG_INIT_VIEW, 400);
                }

            }.start();
        }
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }

    private class GetWorkTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            try {
                String json = JSONUtil.getStringFromUrl(WorkActivity.this, strings[0]);
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
                }
            }
            if (status != null && status.getRetCode() == 404 && !JSONUtil.isEmpty(status
                    .getErrorMsg())) {
                setEmptyView(status.getErrorMsg());
            } else {
                setEmptyView(null);
            }

        }
    }

    private void initWorkJson(JSONObject jsonObject) {
        if (jsonObject != null) {
            work = new Work(jsonObject);
            if (work.getId() == 0) {
                return;
            }
            if (fragment == null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                fragment = (RefreshFragment) Fragment.instantiate(WorkActivity.this,
                        work.getVersion() > 0 ? WorkFragment.class.getName() : WorkOldFragment
                                .class.getName());
                Bundle bundle = new Bundle();
                bundle.putBoolean("isSnapshot", isSnapshot);
                bundle.putSerializable("work", work);
                bundle.putString("json", jsonObject.toString());
                fragment.setArguments(bundle);
                ft.add(R.id.fragment, fragment, "workFragment");
                if (!this.isFinishing()) {
                    ft.commitAllowingStateLoss();
                }
            } else {
                fragment.refresh(work);
            }
        }
    }

    private void setEmptyView(String msg) {
        if (work == null || work.getId() == 0) {
            progressBar.setVisibility(View.GONE);
            findViewById(R.id.action_holder_layout2).setAlpha(1);
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
}
