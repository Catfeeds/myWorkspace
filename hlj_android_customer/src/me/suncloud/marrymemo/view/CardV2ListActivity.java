package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.hunliji.hljcardcustomerlibrary.views.activities.ReceiveGiftCashActivity;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.entity.ImageLoadProgressListener;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.V2.CardPageV2;
import me.suncloud.marrymemo.model.V2.CardV2;
import me.suncloud.marrymemo.task.DrawPageTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.CardResourceUtil;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.FileUtil;
import me.suncloud.marrymemo.util.ImageLoadUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.card.HelpAndFeedbackActivity;
import me.suncloud.marrymemo.widget.RoundProgressDialog;

/**
 * Created by Suncloud on 2016/5/13.
 */
@Route(path = RouterPath.IntentPath.Customer.CARD_V2_LIST)
public class CardV2ListActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<GridView>, ObjectBindAdapter.ViewBinder<CardV2>, AdapterView
        .OnItemClickListener {

    @BindView(R.id.list_view)
    PullToRefreshGridView listView;
    @BindView(R.id.tv_sign_count)
    TextView tvSignCount;
    @BindView(R.id.tv_gift_count)
    TextView tvGiftCount;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tv_cash_count)
    TextView tvCashCount;
    private GetCardsTask cardsTask;
    private ArrayList<CardV2> cards;
    private ObjectBindAdapter<CardV2> adapter;
    private int height;
    private int width;
    private Dialog dialog;
    private CardDownloadTask cardDownloadTask;
    private RoundProgressDialog progressDialog;
    private Realm realm;
    private boolean isNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CardResourceUtil.getInstance()
                .executeFontsTask(this);
        realm = Realm.getDefaultInstance();
        Point point = JSONUtil.getDeviceSize(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = Math.round(point.x / 2 - dm.density * 23);
        height = Math.round(width * 122 / 75);
        cards = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, cards, R.layout.card_item_v2, this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list_v2);
        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(this);
        if (dataConfig != null && !JSONUtil.isEmpty(dataConfig.getEcardFaqUrl())) {
            setOkText(R.string.title_activity_help_and_feedback);
        }
        ButterKnife.bind(this);
        progressBar.setVisibility(View.VISIBLE);
        listView.setOnItemClickListener(this);
        listView.setOnRefreshListener(this);
        listView.setAdapter(adapter);
        cardsTask = new GetCardsTask();
        cardsTask.executeOnExecutor(Constants.LISTTHEADPOOL);
        listView.setOnItemClickListener(this);
        if (!EventBus.getDefault()
                .isRegistered(this)) {
            EventBus.getDefault()
                    .register(this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        Intent intent = new Intent(this, HelpAndFeedbackActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    private void onCountRefresh() {
        User user = Session.getInstance()
                .getCurrentUser(this);
        long signCount = 0;
        long giftCount = 0;
        long cashCount = 0;
        if (user != null && user.getId() > 0) {
            signCount = realm.where(Notification.class)
                    .equalTo("userId", user.getId())
                    .notEqualTo("status", 2)
                    .equalTo("notifyType", Notification.NotificationType.SIGN)
                    .count();
            giftCount = realm.where(Notification.class)
                    .equalTo("userId", user.getId())
                    .notEqualTo("status", 2)
                    .equalTo("notifyType", Notification.NotificationType.GIFT)
                    .equalTo("action", Notification.NotificationAction.RECV_CARD_GIFT)
                    .count();
            cashCount = realm.where(Notification.class)
                    .equalTo("userId", user.getId())
                    .notEqualTo("status", 2)
                    .equalTo("notifyType", Notification.NotificationType.GIFT)
                    .equalTo("action", Notification.NotificationAction.RECV_CASH_GIFT)
                    .count();
        }
        if (signCount == 0) {
            tvSignCount.setVisibility(View.GONE);
        } else if (signCount > 99) {
            tvSignCount.setVisibility(View.VISIBLE);
            tvSignCount.setText("99+");
        } else {
            tvSignCount.setVisibility(View.VISIBLE);
            tvSignCount.setText(String.valueOf(signCount));
        }
        if (giftCount == 0) {
            tvGiftCount.setVisibility(View.GONE);
        } else if (giftCount > 99) {
            tvGiftCount.setVisibility(View.VISIBLE);
            tvGiftCount.setText("99+");
        } else {
            tvGiftCount.setVisibility(View.VISIBLE);
            tvGiftCount.setText(String.valueOf(giftCount));
        }
        if (cashCount == 0) {
            tvCashCount.setVisibility(View.GONE);
        } else if (cashCount > 99) {
            tvCashCount.setVisibility(View.VISIBLE);
            tvCashCount.setText("99+");
        } else {
            tvCashCount.setVisibility(View.VISIBLE);
            tvCashCount.setText(String.valueOf(cashCount));
        }
    }

    @OnClick(R.id.sign_layout)
    public void onSign() {
        tvSignCount.setVisibility(View.GONE);
        Intent intent = new Intent(this, SignListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }


    @OnClick(R.id.gift_layout)
    public void onGift() {
        tvGiftCount.setVisibility(View.GONE);
        Intent intent = new Intent(this, ReceiveGiftCashActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.cash_layout)
    public void onCash() {
        tvCashCount.setVisibility(View.GONE);
        Intent intent = new Intent(this, ReceiveGiftCashActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final CardV2 card = (CardV2) parent.getAdapter()
                .getItem(position);
        if (card != null && card.getId() > 0) {
            Intent intent;
            if (card.isSampleCard()) {
                intent = new Intent(this, CardV2WebActivity.class);
            } else {
                if (!card.getImages(this)
                        .isEmpty() || !card.getFonts(this)
                        .isEmpty()) {
                    if (dialog != null && dialog.isShowing()) {
                        return;
                    }
                    ConnectivityManager connectMgr = (ConnectivityManager) this.getSystemService(
                            Context.CONNECTIVITY_SERVICE);
                    NetworkInfo info = connectMgr.getActiveNetworkInfo();
                    String hintMsg = getString(R.string.msg_card_download);
                    if (info != null && info.getType() != ConnectivityManager.TYPE_WIFI) {
                        hintMsg = getString(R.string.msg_card_download_no_wifi);
                    }
                    dialog = DialogUtil.createDoubleButtonDialog(dialog,
                            CardV2ListActivity.this,
                            hintMsg,
                            getString(R.string.label_download),
                            null,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    if (progressDialog == null) {
                                        progressDialog = JSONUtil.getRoundProgress(
                                                CardV2ListActivity.this);
                                        progressDialog.setMax(100);
                                        progressDialog.setMessage(getString(R.string
                                                .label_card_download));
                                        progressDialog.setOnDismissListener(new DialogInterface
                                                .OnDismissListener() {

                                            @Override
                                            public void onDismiss(DialogInterface dialog) {
                                                if (!CardV2ListActivity.this.isFinishing() &&
                                                        cardDownloadTask != null &&
                                                        !cardDownloadTask.isCancelled()) {
                                                    cardDownloadTask.cancel(true);
                                                    cardDownloadTask = null;
                                                }
                                            }
                                        });
                                    } else {
                                        progressDialog.reset();
                                        progressDialog.show();
                                    }
                                    if (cardDownloadTask != null && !cardDownloadTask.getCard()
                                            .getId()
                                            .equals(card.getId())) {
                                        cardDownloadTask.cancel(true);
                                        cardDownloadTask = null;
                                    }
                                    if (cardDownloadTask == null) {
                                        cardDownloadTask = new CardDownloadTask
                                                (CardV2ListActivity.this,
                                                card);
                                        cardDownloadTask.executeOnExecutor(Constants.INFOTHEADPOOL);
                                    }
                                }
                            });
                    dialog.show();
                    return;
                }
                intent = new Intent(this, CardV2EditActivity.class);
            }
            intent.putExtra("card", card);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else {
            int count = 0;
            for (CardV2 c : cards) {
                if (c != null && c.getId() > 0 && !c.isSampleCard()) {
                    count++;
                }
            }
            if (count >= 20 && !Constants.DEBUG) {
                DialogUtil.createSingleButtonDialog(null,
                        this,
                        getString(R.string.hint_card_count_limit),
                        null,
                        null)
                        .show();
                return;
            }
            if (isNew) {
                isNew = false;
                adapter.notifyDataSetChanged();
            }
            Intent intent = new Intent(this, ThemeV2ListActivity.class);
            for (CardV2 cardV2 : cards) {
                if (cardV2 != null && cardV2.getId() > 0 && !cardV2.isSampleCard()) {
                    intent.putExtra("lastCard", cardV2);
                    break;
                }
            }
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    public void setViewValue(View view, final CardV2 cardV2, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder(view);
            holder.ivCover.getLayoutParams().height = height;
            view.setTag(holder);
        }
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.btnDelete.setTag(cardV2);
        if (cardV2.getId() > 0) {
            CardPageV2 frontPage = cardV2.getFrontPage();
            String key = CardResourceUtil.getInstance()
                    .getPageMapKey(CardV2ListActivity.this, frontPage.getId());
            File file = FileUtil.createPageFile(CardV2ListActivity.this,
                    frontPage.getCardPageKey());
            if (!frontPage.getCardPageKey()
                    .equals(key) && !JSONUtil.isEmpty(key)) {
                FileUtil.deleteFile(FileUtil.createPageFile(CardV2ListActivity.this, key));
            }
            if (file != null && file.exists() && file.length() > 0) {
                if (holder.drawPageTask != null) {
                    holder.drawPageTask.cancel(true);
                    holder.drawPageTask = null;
                }
                ImageLoadUtil.loadImageViewWithoutTransitionNoCache(CardV2ListActivity.this,
                        file.getAbsolutePath(),
                        holder.ivCover);
            } else {
                holder.ivCover.setImageBitmap(null);
                ImageLoadUtil.clear(CardV2ListActivity.this, holder.ivCover);

                OnHttpRequestListener drawPageListener = new OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {
                        if (isFinishing() || cardV2 != holder.btnDelete.getTag()) {
                            return;
                        }
                        ImageLoadUtil.loadImageViewWithoutTransitionNoCache(CardV2ListActivity.this,
                                (String) obj,
                                holder.ivCover);
                    }

                    @Override
                    public void onRequestFailed(Object obj) {

                    }
                };
                if (holder.drawPageTask != null) {
                    if (!frontPage.getCardPageKey()
                            .equals(holder.drawPageTask.getCardPageKey())) {
                        holder.drawPageTask.cancel(true);
                        holder.drawPageTask = null;
                    } else {
                        holder.drawPageTask.setListener(drawPageListener);
                    }
                }
                if (holder.drawPageTask == null) {
                    holder.drawPageTask = new DrawPageTask(CardV2ListActivity.this,
                            frontPage,
                            drawPageListener);
                    holder.drawPageTask.executeOnExecutor(Constants.THEADPOOL);
                }
            }
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(new OnDeleteClickListener(cardV2));
            holder.tvTitle.setVisibility(View.VISIBLE);
            if (cardV2.isSampleCard()) {
                holder.tvTitle.setText(R.string.label_sample);
            } else if (TextUtils.isEmpty(cardV2.getTitle())) {
                holder.tvTitle.setText(R.string.label_untitled);
            } else {
                holder.tvTitle.setText(cardV2.getTitle());
            }
            holder.ivNewTheme.setVisibility(View.GONE);
        } else {
            if (holder.drawPageTask != null) {
                holder.drawPageTask.cancel(true);
                holder.drawPageTask = null;
            }
            ImageLoadUtil.clear(CardV2ListActivity.this, holder.ivCover);
            holder.ivNewTheme.setVisibility(isNew ? View.VISIBLE : View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
            holder.tvTitle.setVisibility(View.GONE);
            holder.ivCover.setImageResource(R.drawable.image_bg_wedding_card_new);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<GridView> refreshView) {
        if (cardsTask != null) {
            cardsTask = new GetCardsTask();
            cardsTask.executeOnExecutor(Constants.LISTTHEADPOOL);
        }
    }

    private class GetCardsTask extends AsyncTask<Object, Object, JSONArray> {

        @Override
        protected JSONArray doInBackground(Object... params) {
            try {
                String json = JSONUtil.getStringFromUrl(Constants.getAbsUrl(Constants.HttpPath
                        .CARD_V2_LIST));
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
        protected void onPostExecute(JSONArray jsonArray) {
            if (isFinishing()) {
                return;
            }
            listView.onRefreshComplete();
            progressBar.setVisibility(View.GONE);
            if (jsonArray != null) {
                bottomLayout.setVisibility(View.VISIBLE);
                cards.clear();
                for (int i = 0, size = jsonArray.length(); i < size; i++) {
                    CardV2 card = new CardV2(jsonArray.optJSONObject(i));
                    if (card.getFrontPage() != null) {
                        cards.add(card);
                    }


                }
                adapter.notifyDataSetChanged();
            }
            if (cards.isEmpty()) {
                View emptyView = listView.getRefreshableView()
                        .getEmptyView();
                if (emptyView == null) {
                    emptyView = findViewById(R.id.empty_hint_layout);
                    listView.setEmptyView(emptyView);
                    Util.setEmptyView(CardV2ListActivity.this,
                            emptyView,
                            R.string.net_disconnected,
                            R.mipmap.icon_no_network,
                            0,
                            0);
                    emptyView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onRefresh(null);
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            super.onPostExecute(jsonArray);
        }
    }

    static class ViewHolder {
        @BindView(R.id.iv_cover)
        ImageView ivCover;
        @BindView(R.id.iv_new_theme)
        ImageView ivNewTheme;
        @BindView(R.id.btn_delete)
        ImageView btnDelete;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        private DrawPageTask drawPageTask;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private class OnDeleteClickListener implements View.OnClickListener {

        private CardV2 card;

        private OnDeleteClickListener(CardV2 card) {
            this.card = card;
        }

        @Override
        public void onClick(View v) {
            if (dialog != null && dialog.isShowing()) {
                return;
            }
            dialog = DialogUtil.createDoubleButtonDialog(dialog,
                    CardV2ListActivity.this,
                    getString(R.string.msg_card_delete_confirm),
                    null,
                    null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            progressBar.setVisibility(View.VISIBLE);
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("id", card.getId());
                                jsonObject.put("deleted", 1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            new StatusHttpPostTask(CardV2ListActivity.this,
                                    new StatusRequestListener() {
                                        @Override
                                        public void onRequestCompleted(
                                                Object object, ReturnStatus returnStatus) {
                                            progressBar.setVisibility(View.GONE);
                                            cards.remove(card);
                                            adapter.notifyDataSetChanged();
                                            card.deleteCardFile(CardV2ListActivity.this);
                                        }

                                        @Override
                                        public void onRequestFailed(
                                                ReturnStatus returnStatus, boolean network) {
                                            progressBar.setVisibility(View.GONE);
                                            Util.postFailToast(CardV2ListActivity.this,
                                                    returnStatus,
                                                    R.string.msg_err_card_delete,
                                                    network);
                                        }
                                    }).executeOnExecutor(Constants.INFOTHEADPOOL,
                                    Constants.getAbsUrl(Constants.HttpPath.CARD_V2_SAVE),
                                    jsonObject.toString());
                        }
                    });
            dialog.show();
        }

    }

    public void onEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.EventType.CARD_UPDATE_FLAG) {
            onRefresh(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onFinish() {
        if (cardDownloadTask != null && !cardDownloadTask.isCancelled()) {
            cardDownloadTask.cancel(true);
            cardDownloadTask = null;
        }
        realm.close();
        super.onFinish();
        if (EventBus.getDefault()
                .isRegistered(this)) {
            EventBus.getDefault()
                    .unregister(this);
        }
    }

    private class CardDownloadTask extends AsyncTask<Object, Integer, Boolean> {

        private CardV2 card;
        private WeakReference<Context> contextWeakReference;
        private int intValue;

        private CardDownloadTask(Context context, CardV2 card) {
            this.contextWeakReference = new WeakReference<>(context);
            this.card = card;
        }

        public CardV2 getCard() {
            return card;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            ArrayList<String> images = card.getImages(contextWeakReference.get());
            ArrayList<String> fonts = card.getFonts(contextWeakReference.get());

            int size = images.size() + fonts.size();
            if (size > 0) {
                final int value = 100 / size;
                int i = 0;
                for (String string : images) {
                    if (contextWeakReference.get() == null || isCancelled()) {
                        return false;
                    }
                    intValue = 100 * i++ / size;
                    File file = FileUtil.createThemeFile(contextWeakReference.get(), string);
                    if (!file.exists() || file.length() == 0) {
                        try {
                            byte[] data = JSONUtil.getByteArrayFromUrl(string,
                                    new ImageLoadProgressListener() {
                                        long contentLength;
                                        long transfereLength;
                                        long time;

                                        @Override
                                        public void transferred(int transferedBytes, String url) {
                                            if (contentLength > 0) {
                                                transfereLength += transferedBytes;
                                                if (time + 300 < System.currentTimeMillis()) {
                                                    time = System.currentTimeMillis();
                                                    publishProgress((int) (intValue +
                                                            (transfereLength * value) /
                                                                    contentLength));
                                                }
                                            }
                                        }

                                        @Override
                                        public void setContentLength(
                                                long contentLength, String url) {
                                            this.contentLength = contentLength;
                                        }
                                    });
                            if (data == null) {
                                return false;
                            }
                            FileOutputStream out = new FileOutputStream(file);
                            out.write(data);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                }
                for (String string : fonts) {
                    if (contextWeakReference.get() == null || isCancelled()) {
                        return false;
                    }
                    intValue = 100 * i++ / size;
                    File file = FileUtil.createFontFile(contextWeakReference.get(), string);
                    if (!file.exists() || file.length() == 0) {
                        try {
                            byte[] data = JSONUtil.getByteArrayFromUrl(string,
                                    new ImageLoadProgressListener() {
                                        long contentLength;
                                        long transfereLength;
                                        long time;

                                        @Override
                                        public void transferred(int transferedBytes, String url) {
                                            if (contentLength > 0) {
                                                transfereLength += transferedBytes;
                                                if (time + 300 < System.currentTimeMillis()) {
                                                    time = System.currentTimeMillis();
                                                    publishProgress((int) (intValue +
                                                            (transfereLength * value) /
                                                                    contentLength));
                                                }
                                            }
                                        }

                                        @Override
                                        public void setContentLength(
                                                long contentLength, String url) {
                                            this.contentLength = contentLength;
                                        }
                                    });
                            if (data == null) {
                                return false;
                            }
                            FileOutputStream out = new FileOutputStream(file);
                            out.write(data);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                }
                publishProgress(100);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (isFinishing()) {
                return;
            }
            if (cardDownloadTask == this) {
                cardDownloadTask = null;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (aBoolean != null && aBoolean) {
                    if (card == null) {
                        return;
                    }
                    Intent intent = new Intent(CardV2ListActivity.this, CardV2EditActivity.class);
                    intent.putExtra("card", card);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                } else {
                    Util.showToast(contextWeakReference.get(),
                            null,
                            R.string.label_card_download_fail);
                }
            }
            super.onPostExecute(aBoolean);
        }

        @Override
        protected void onCancelled() {
            if (isFinishing()) {
                return;
            }
            if (cardDownloadTask == this) {
                cardDownloadTask = null;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    Util.showToast(contextWeakReference.get(),
                            null,
                            R.string.label_card_download_cancelled);
                }
            }
            super.onCancelled();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (isFinishing()) {
                return;
            }
            if (progressDialog != null) {
                progressDialog.setProgress(values[0]);
            }
            super.onProgressUpdate(values);
        }
    }
}