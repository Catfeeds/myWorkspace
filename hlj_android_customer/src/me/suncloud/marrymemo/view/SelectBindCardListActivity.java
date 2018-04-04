package me.suncloud.marrymemo.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.UserBindBankCard;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.LLPayer;
import me.suncloud.marrymemo.util.ScaleMode;

public class SelectBindCardListActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<UserBindBankCard>, PullToRefreshBase.OnRefreshListener<ListView>, AdapterView
        .OnItemClickListener {

    @BindView(R.id.list)
    PullToRefreshListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private ArrayList<UserBindBankCard> userBindBankCards;
    private ObjectBindAdapter<UserBindBankCard> adapter;
    private boolean isLoad;
    private LLPayer llPayer;
    private int width;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_bind_card_list);
        ButterKnife.bind(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = (int) (dm.density * 22);
        llPayer = (LLPayer) getIntent().getSerializableExtra("payer");

        View headView = getLayoutInflater().inflate(R.layout.select_bind_card_list_head, null);
        View footView = getLayoutInflater().inflate(R.layout.select_bind_card_list_footer, null);
        TextView footText = (TextView) footView.findViewById(R.id.tv_hint);
        footText.setText(Html.fromHtml(getString(R.string.label_select_bind_card2)));
        listView.getRefreshableView()
                .addHeaderView(headView);
        listView.getRefreshableView()
                .addFooterView(footView);
        listView.getRefreshableView()
                .setOnItemClickListener(this);
        userBindBankCards = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this,
                userBindBankCards,
                R.layout.select_bank_card_list_item,
                this);

        listView.setOnRefreshListener(this);
        listView.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);
        new GetBankCardList().executeOnExecutor(Constants.LISTTHEADPOOL);
    }

    @Override
    public void setViewValue(View view, UserBindBankCard userBindBankCard, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder(view);

            view.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder) view.getTag();
        String logoPath = JSONUtil.getImagePath(userBindBankCard.getLogoPath(), width);
        if (!JSONUtil.isEmpty(logoPath)) {
            holder.imgBankLogo.setTag(logoPath);
            ImageLoadTask task = new ImageLoadTask(holder.imgBankLogo, null, 0);
            task.loadImage(logoPath,
                    holder.imgBankLogo.getLayoutParams().width,
                    ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
        } else {
            holder.imgBankLogo.setImageBitmap(null);
        }
        holder.tvBankName.setText(userBindBankCard.getBankName());
        holder.tvBankCardId.setText("**  " + userBindBankCard.getAccount());
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            new GetBankCardList().executeOnExecutor(Constants.LISTTHEADPOOL);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserBindBankCard userBindBankCard = (UserBindBankCard) parent.getAdapter()
                .getItem(position);
        if (userBindBankCard != null) {
            llPayer.findPasswordStep2(this, userBindBankCard);
        }
    }

    private class GetBankCardList extends AsyncTask<String, Object, JSONObject> {

        public GetBankCardList() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.getAbsUrl(Constants.HttpPath.GET_USER_BIND_BANK_CARD_LIST);
            try {
                String json = JSONUtil.getStringFromUrl(url);
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
            progressBar.setVisibility(View.GONE);
            if (jsonObject != null) {
                isLoad = false;
                listView.onRefreshComplete();
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus != null && returnStatus.getRetCode() == 0) {
                    // 获取用户支付信息成功
                    userBindBankCards.clear();
                    JSONArray jsonArray = jsonObject.optJSONArray("data");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            UserBindBankCard card = new UserBindBankCard(jsonArray.optJSONObject
                                    (i));
                            userBindBankCards.add(card);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            if (userBindBankCards.isEmpty()) {
                View emptyView = listView.getRefreshableView()
                        .getEmptyView();
                if (emptyView == null) {
                    emptyView = findViewById(R.id.empty_hint_layout);
                    emptyView.findViewById(R.id.text_empty_hint)
                            .setVisibility(View.VISIBLE);
                    listView.setEmptyView(emptyView);
                }
                emptyView.setVisibility(View.VISIBLE);
                ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id.img_empty_hint);
                ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
                TextView textEmptyHint = (TextView) emptyView.findViewById(R.id.text_empty_hint);
                if (JSONUtil.isNetworkConnected(SelectBindCardListActivity.this)) {
                    imgNetHint.setVisibility(View.GONE);
                    imgEmptyHint.setVisibility(View.VISIBLE);
                    textEmptyHint.setText(R.string.no_item);
                } else {
                    imgNetHint.setVisibility(View.VISIBLE);
                    imgEmptyHint.setVisibility(View.GONE);
                    textEmptyHint.setText(R.string.net_disconnected);
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'select_bank_card_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.img_bank_logo)
        ImageView imgBankLogo;
        @BindView(R.id.tv_bank_name)
        TextView tvBankName;
        @BindView(R.id.tv_bank_card_id)
        TextView tvBankCardId;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
