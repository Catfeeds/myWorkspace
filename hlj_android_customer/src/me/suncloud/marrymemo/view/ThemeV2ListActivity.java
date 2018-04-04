package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljsharelibrary.HljShare;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.card.CardThemeAdapter;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.V2.CardV2;
import me.suncloud.marrymemo.model.V2.ThemeV2;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.CardResourceUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.TextShareUtil;
import me.suncloud.marrymemo.util.Util;


/**
 * Created by Suncloud on 2016/4/22.
 */
public class ThemeV2ListActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, View.OnClickListener {

    @BindView(R.id.list_view)
    PullToRefreshVerticalRecyclerView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.empty_hint_layout)
    View emptyView;

    private boolean userThemeLock;
    private Dialog shareDialog;
    private TextShareUtil shareUtil;
    private GetShareInfoTask getShareInfoTask;
    private CardV2 lastCard;

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HljShare.RequestCode.SHARE_TO_PENGYOU:
                    userThemeLock = false;
                    if (getAdapter() != null) {
                        getAdapter().setLock(userThemeLock);
                    }
                    CardResourceUtil.getInstance()
                            .setUserThemeLockV2(false);
                    new NewHttpPostTask(ThemeV2ListActivity.this, null).execute(Constants.getAbsUrl(
                            Constants.HttpPath.UNLOCK_THEME_V2_URL));
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lastCard = (CardV2) getIntent().getSerializableExtra("lastCard");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_v2_list);
        setOkText(R.string.label_download_all);
        hideOkText();
        ButterKnife.bind(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (getAdapter().getItemViewType(position)) {
                    case CardThemeAdapter.FREE_THEME_HEADER:
                    case CardThemeAdapter.LOCK_THEME_HEADER:
                        return 3;
                    case CardThemeAdapter.THREE_THEME_ITEM:
                        return 1;
                }
                return 1;
            }
        });
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.getRefreshableView()
                .setLayoutManager(layoutManager);
        listView.getRefreshableView()
                .addItemDecoration(new ThemeItemDecoration(this));
        listView.setOnRefreshListener(this);
        long userId = Session.getInstance()
                .getCurrentUser(this)
                .getId();
        if (CardResourceUtil.getInstance()
                .getThemes()
                .isEmpty() || CardResourceUtil.getInstance()
                .getLockUserId() != userId) {
            progressBar.setVisibility(View.VISIBLE);
            CardResourceUtil.getInstance()
                    .executeThemeV2Task(this);
        } else {
            onPostExecute();
        }
        if (!EventBus.getDefault()
                .isRegistered(this)) {
            EventBus.getDefault()
                    .register(this);
        }
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        CardThemeAdapter adapter = getAdapter();
        if (adapter != null) {
            for (ThemeV2 theme : adapter.getThemes()) {
                if (!theme.isSaved()) {
                    CardResourceUtil.getInstance()
                            .executeThemeDownLoad(this, theme);
                }
            }
        }
    }


    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        CardResourceUtil.getInstance()
                .executeThemeV2Task(ThemeV2ListActivity.this);
    }

    public void onEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.EventType.CARD_THEMEV2_UPDATE_FLAG) {
            onPostExecute();
        } else if (event.getType() == MessageEvent.EventType.CARD_UPDATE_FLAG) {
            finish();
        }
    }

    private CardThemeAdapter getAdapter() {
        if (listView.getRefreshableView()
                .getAdapter() != null) {
            return (CardThemeAdapter) listView.getRefreshableView()
                    .getAdapter();
        } else {
            CardThemeAdapter adapter = new CardThemeAdapter(this,
                    new CardThemeAdapter.ThemeActionClickListener() {
                        @Override
                        public void onDownLoad(ThemeV2 themeV2) {
                            CardResourceUtil.getInstance()
                                    .executeThemeDownLoad(ThemeV2ListActivity
                                            .this, themeV2);

                        }

                        @Override
                        public void onUnlock() {
                            if (shareDialog != null && shareDialog.isShowing()) {
                                return;
                            }
                            if (shareDialog == null) {
                                shareDialog = new Dialog(ThemeV2ListActivity.this,
                                        R.style.BubbleDialogTheme);
                                shareDialog.setContentView(R.layout.dialog_theme_share);
                                shareDialog.findViewById(R.id.share_btn)
                                        .setOnClickListener(ThemeV2ListActivity.this);
                                shareDialog.findViewById(R.id.close_btn)
                                        .setOnClickListener(ThemeV2ListActivity.this);
                                Window window = shareDialog.getWindow();
                                WindowManager.LayoutParams params = window.getAttributes();
                                Point point = JSONUtil.getDeviceSize(ThemeV2ListActivity.this);
                                params.width = Math.round(point.x * 27 / 32);
                            }
                            shareDialog.show();
                        }

                        @Override
                        public void onCreate(ThemeV2 themeV2) {
                            Intent intent = new Intent(ThemeV2ListActivity.this,
                                    CardV2InfoEditActivity.class);
                            intent.putExtra("theme", themeV2);
                            intent.putExtra("lastCard", lastCard);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }

                        @Override
                        public void onPreview(ThemeV2 theme) {
                            Intent intent = new Intent(ThemeV2ListActivity.this,
                                    CardV2WebActivity.class);
                            intent.putExtra("theme", theme);
                            intent.putExtra("lastCard", lastCard);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);

                        }
                    });
            listView.getRefreshableView()
                    .setAdapter(adapter);
            return adapter;
        }
    }

    public void onPostExecute() {
        ArrayList<ThemeV2> themes = new ArrayList<>(CardResourceUtil.getInstance()
                .getThemes());
        progressBar.setVisibility(View.GONE);
        listView.onRefreshComplete();
        CardThemeAdapter adapter = getAdapter();
        if (themes.isEmpty()) {
            adapter.clear();
            Util.setEmptyView(this,
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
            listView.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit()
                    .putLong("cardClickTimeV2", System.currentTimeMillis())
                    .apply();
            userThemeLock = CardResourceUtil.getInstance()
                    .isUserThemeLockV2();
            List<ThemeV2> freeThemes = new ArrayList<>();
            List<ThemeV2> lockThemes = new ArrayList<>();
            boolean downLoadAll = true;
            for (ThemeV2 theme : themes) {
                theme.onSaveCheck(this);
                theme.setDownLoading(false);
                downLoadAll &= theme.isSaved();
                if (theme.isLocked()) {
                    lockThemes.add(theme);
                } else {
                    freeThemes.add(theme);
                }
            }
            if (downLoadAll) {
                hideOkText();
            } else {
                showOkText();
            }
            adapter.setThemes(freeThemes, lockThemes);
            adapter.setLock(userThemeLock);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_btn:
                shareDialog.dismiss();
                if (shareUtil != null) {
                    shareUtil.shareToPengYou();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    if (getShareInfoTask == null) {
                        getShareInfoTask = new GetShareInfoTask();
                        getShareInfoTask.execute();
                    }
                }
                break;
            case R.id.close_btn:
                shareDialog.dismiss();
                break;
        }
    }

    static class ViewHolder {
        @BindView(R.id.iv_thumb)
        ImageView ivThumb;
        @BindView(R.id.iv_new)
        ImageView ivNew;
        @BindView(R.id.progressbar)
        ProgressBar progressbar;
        @BindView(R.id.progressbar_layout)
        LinearLayout progressbarLayout;
        @BindView(R.id.iv_lock)
        ImageView ivLock;
        @BindView(R.id.theme_layout)
        RelativeLayout themeLayout;
        @BindView(R.id.btn_download)
        Button btnDownload;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        userThemeLock = CardResourceUtil.getInstance()
                .isUserThemeLockV2();
        CardResourceUtil.getInstance()
                .setThemeDownloadListener(downLoadListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CardResourceUtil.getInstance()
                .removeThemeDownloadListener(downLoadListener);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (EventBus.getDefault()
                .isRegistered(this)) {
            EventBus.getDefault()
                    .unregister(this);
        }
    }

    private class GetShareInfoTask extends AsyncTask<Object, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                String json = JSONUtil.getStringFromUrl(Constants.getAbsUrl(Constants.HttpPath
                        .TOOLS_SHARE));
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
        protected void onPostExecute(JSONObject jsonObject) {
            progressBar.setVisibility(View.GONE);
            getShareInfoTask = null;
            if (jsonObject != null) {
                String description = jsonObject.optString("desc",
                        getString(R.string.tools_share_msg));
                if (JSONUtil.isEmpty(description)) {
                    description = getString(R.string.tools_share_msg);
                }
                String webDescription = jsonObject.optString("desc2",
                        getString(R.string.tools_share_msg));
                if (JSONUtil.isEmpty(webDescription)) {
                    webDescription = getString(R.string.tools_share_msg);
                }
                String title = jsonObject.optString("title", getString(R.string.tools_share_title));
                if (JSONUtil.isEmpty(title)) {
                    title = getString(R.string.tools_share_title);
                }
                String url = jsonObject.optString("url", "http://dwz.cn/IRby7");
                if (JSONUtil.isEmpty(url)) {
                    title = "http://dwz.cn/IRby7";
                }
                shareUtil = new TextShareUtil(ThemeV2ListActivity.this,
                        url,
                        title,
                        description,
                        webDescription,
                        handler);
            } else {
                shareUtil = new TextShareUtil(ThemeV2ListActivity.this,
                        "http://dwz.cn/IRby7",
                        getString(R.string.tools_share_title),
                        getString(R.string.tools_share_msg),
                        getString(R.string.tools_share_msg),
                        handler);
            }
            shareUtil.shareToPengYou();
            super.onPostExecute(jsonObject);
        }
    }

    private OnHttpRequestListener downLoadListener = new OnHttpRequestListener() {

        @Override
        public void onRequestCompleted(Object obj) {
            CardThemeAdapter adapter = getAdapter();
            if (adapter == null) {
                return;
            }
            ThemeV2 theme = (ThemeV2) obj;
            adapter.notifyDataSetChanged(ThemeV2ListActivity.this, theme);
            if (theme.isSaved()) {
                for (ThemeV2 t : adapter.getThemes()) {
                    if (!t.isSaved()) {
                        showOkText();
                        return;
                    }
                }
                hideOkText();
            }
        }

        @Override
        public void onRequestFailed(Object obj) {
            CardThemeAdapter adapter = getAdapter();
            if (adapter == null) {
                return;
            }
            ThemeV2 theme = (ThemeV2) obj;
            adapter.notifyDataSetChanged(ThemeV2ListActivity.this, theme);

        }
    };

    private class ThemeItemDecoration extends RecyclerView.ItemDecoration {

        private int top;    //模板顶
        private int bottom; //模板底边距
        private int space; //模板间距
        private int edge; //左右边距

        private ThemeItemDecoration(Context context) {
            top = CommonUtil.dp2px(context, 4);
            bottom = CommonUtil.dp2px(context, 12);
            space = CommonUtil.dp2px(context, 12);
            edge = CommonUtil.dp2px(context, 20);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (parent.getAdapter() != null && parent.getAdapter() instanceof CardThemeAdapter) {
                switch (((CardThemeAdapter) parent.getAdapter()).getSpaceType(position)) {
                    case HEADER:
                        outRect.top = 0;
                        outRect.left = 0;
                        outRect.right = 0;
                        outRect.bottom = 0;
                        break;
                    case Left:
                        outRect.top = top;
                        outRect.left = edge;
                        outRect.right = 0;
                        outRect.bottom = bottom;
                        break;
                    case Middle:
                        outRect.top = top;
                        outRect.left = space;
                        outRect.right = space;
                        outRect.bottom = bottom;
                        break;
                    case Right:
                        outRect.top = top;
                        outRect.left = 0;
                        outRect.right = edge;
                        outRect.bottom = bottom;
                        break;
                }
            }
        }
    }
}
