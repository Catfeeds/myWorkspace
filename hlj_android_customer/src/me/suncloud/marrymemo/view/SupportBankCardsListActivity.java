package me.suncloud.marrymemo.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import me.suncloud.marrymemo.model.BinkInfo;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;

public class SupportBankCardsListActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<BinkInfo> {

    @BindView(R.id.listView)
    PullToRefreshListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private ArrayList<BinkInfo> binkInfos;
    private ObjectBindAdapter<BinkInfo> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_bank_cards_list);
        ButterKnife.bind(this);

        View headView = getLayoutInflater().inflate(R.layout.empty_10_height_layout, null);
        listView.getRefreshableView()
                .addHeaderView(headView);

        binkInfos = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this,
                binkInfos,
                R.layout.support_bank_card_list_item,
                this);
        listView.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        new GetBankCardList().executeOnExecutor(Constants.LISTTHEADPOOL);
    }


    @Override
    public void setViewValue(View view, BinkInfo binkInfo, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder(view);

            view.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder) view.getTag();
        String logoPath = JSONUtil.getImagePath(binkInfo.getLogoPath(),
                holder.imgBankLogo.getLayoutParams().width);
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
        holder.tvBankName.setText(binkInfo.getBankName());
        holder.tvCardType.setText(binkInfo.getCardTypeStr());
    }

    private class GetBankCardList extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.getAbsUrl(Constants.HttpPath.LLPAY_SUPPORT_BANK_LIST);
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
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus != null && returnStatus.getRetCode() == 0) {
                    // 获取用户支付信息成功
                    binkInfos.clear();
                    JSONArray jsonArray = jsonObject.optJSONArray("data");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            BinkInfo card = new BinkInfo(jsonArray.optJSONObject(i));
                            binkInfos.add(card);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            if (binkInfos.isEmpty()) {
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
                if (JSONUtil.isNetworkConnected(SupportBankCardsListActivity.this)) {
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
     * 'support_bank_card_list_item.xml'
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
        @BindView(R.id.tv_card_type)
        TextView tvCardType;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
