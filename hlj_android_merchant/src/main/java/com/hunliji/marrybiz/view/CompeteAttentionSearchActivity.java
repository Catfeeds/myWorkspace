package com.hunliji.marrybiz.view;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ObjectBindAdapter;
import com.hunliji.marrybiz.model.NewMerchant;
import com.hunliji.marrybiz.task.HttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jinxin on 2016/6/21.
 */
public class CompeteAttentionSearchActivity extends HljBaseNoBarActivity
        implements TextWatcher, ObjectBindAdapter.ViewBinder<NewMerchant> {
    @BindView(R.id.et_search)
    AutoCompleteTextView etSearch;
    @BindView(R.id.btn_clear)
    ImageView btnClear;
    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.img_empty_hint)
    ImageView imgEmptyHint;
    @BindView(R.id.text_empty_hint)
    TextView textEmptyHint;
    @BindView(R.id.empty_hint_layout)
    LinearLayout emptyHintLayout;

    private ObjectBindAdapter<NewMerchant> adapter;
    private List<NewMerchant> mData;
    private boolean operated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compete_attention_search);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        operated = false;
        mData = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this,
                mData,
                R.layout.compete_attention_item,
                this);
        etSearch.addTextChangedListener(this);
        etSearch.setOnEditorActionListener(new TextView
                .OnEditorActionListener() {
            @Override
            public boolean onEditorAction(
                    TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //搜索
                    String key = etSearch.getEditableText()
                            .toString();
                    key = key.trim();
                    if (JSONUtil.isEmpty(key)) {
                        return true;
                    }
                    refresh(key);
                    return true;
                }
                return false;
            }
        });
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView.getRefreshableView()
                .setAdapter(adapter);
    }

    private void refresh(Object... params) {
        String key = (String) params[0];
        String url = Constants.getAbsUrl(Constants.HttpPath.SEARCH_COMPETE,
                key);
        progressBar.setVisibility(View.VISIBLE);
        new SearchTask().executeOnExecutor(Constants.LISTTHEADPOOL, url);
    }

    @Override
    public void onBackPressed() {
        if (operated) {
            setResult(Activity.RESULT_OK);
        }
        super.onBackPressed();
    }

    @OnClick({R.id.btn_clear, R.id.search_action_text})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_clear:
                etSearch.setText("");
                break;
            case R.id.search_action_text:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
    }

    @Override
    public void beforeTextChanged(
            CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(
            CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s != null && s.length() > 0) {
            btnClear.setVisibility(View.VISIBLE);
        } else {
            btnClear.setVisibility(View.GONE);
        }
    }

    private void setEmptyView() {
        if (mData.isEmpty()) {
            View emptyView = listView.getRefreshableView()
                    .getEmptyView();
            if (emptyView == null) {
                emptyView = emptyHintLayout;
                imgEmptyHint.setVisibility(View.VISIBLE);
                textEmptyHint.setText(getString(R.string.no_item));
                textEmptyHint.setVisibility(View.VISIBLE);
                emptyHintLayout.setVisibility(View.VISIBLE);
            }
            listView.getRefreshableView()
                    .setEmptyView(emptyView);
        }
    }

    @Override
    public void setViewValue(
            View view, NewMerchant merchant, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.name.setText(merchant.getName());
        if (merchant.isFocus()) {
            //已关注
            holder.hasAttentionLayout.setVisibility(View.VISIBLE);
            holder.attention.setVisibility(View.GONE);
        } else {
            //未关注
            holder.hasAttentionLayout.setVisibility(View.GONE);
            holder.attention.setVisibility(View.VISIBLE);
        }
        holder.attention.setTag(position);
        holder.hasAttentionLayout.setTag(position);
        OnAttentionClickListener listener = new OnAttentionClickListener();
        holder.attention.setOnClickListener(listener);
        holder.hasAttentionLayout.setOnClickListener(listener);
    }

    private void onAttention(final NewMerchant merchant) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> params = new HashMap<>();
        if (merchant.isFocus()) {
            //取消竞对
            params.put("user_id", String.valueOf(merchant.getUserId()));
            String url = Constants.getAbsUrl(Constants.HttpPath.CANCEL_COMPETE,
                    merchant.getId());
            new HttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    if (obj != null) {
                        JSONObject res = (JSONObject) obj;
                        if (res != null) {
                            JSONObject status = res.optJSONObject("status");
                            if (status != null) {
                                String msg = status.optString("msg");
                                if (msg != null && msg.equals("success")) {
                                    merchant.setFocus(false);
                                    Toast.makeText(
                                            CompeteAttentionSearchActivity.this,
                                            getString(R.string
                                                    .label_cancel_compete_success),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(
                                            CompeteAttentionSearchActivity.this,
                                            getString(R.string
                                                    .label_cancel_compete_fail),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CompeteAttentionSearchActivity.this,
                            getString(R.string.label_cancel_compete_fail),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }).execute(url, params);
        } else {
            //添加竞对
            params.put("merchant_id",
                    merchant.getId()
                            .toString());
            String url = Constants.getAbsUrl(Constants.HttpPath.ADD_COMPETE,
                    merchant.getUserId());
            new HttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    if (obj != null) {
                        JSONObject res = (JSONObject) obj;
                        if (res != null) {
                            JSONObject status = res.optJSONObject("status");
                            if (status != null) {
                                String msg = status.optString("msg");
                                if (msg != null && msg.equals("success")) {
                                    merchant.setFocus(true);
                                    Toast.makeText(
                                            CompeteAttentionSearchActivity.this,
                                            getString(R.string
                                                    .label_add_compete_success),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(
                                            CompeteAttentionSearchActivity.this,
                                            getString(R.string
                                                    .label_add_compete_fail),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CompeteAttentionSearchActivity.this,
                            getString(R.string.label_add_compete_fail),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }).execute(url, params);
        }
    }

    class OnAttentionClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            operated = true;
            int position = (int) v.getTag();
            if (position >= 0 && position < mData.size()) {
                NewMerchant merchant = mData.get(position);
                onAttention(merchant);
            }
        }
    }

    class SearchTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(
                        CompeteAttentionSearchActivity.this,
                        url);
                if (!JSONUtil.isEmpty(json)) {
                    return new JSONObject(json);
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressBar.setVisibility(View.GONE);
            if (jsonObject != null) {
                JSONArray data = jsonObject.optJSONArray("data");
                if (data != null) {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item = data.optJSONObject(i);
                        NewMerchant merchant = new NewMerchant(item);
                        if (merchant != null && merchant.getId() > 0) {
                            mData.add(merchant);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            setEmptyView();
        }
    }

    static class ViewHolder {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.attention)
        TextView attention;
        @BindView(R.id.has_attention_layout)
        LinearLayout hasAttentionLayout;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
