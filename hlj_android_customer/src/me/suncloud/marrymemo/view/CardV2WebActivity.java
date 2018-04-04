package me.suncloud.marrymemo.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.CardV2ShareFragment;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.V2.CardV2;
import me.suncloud.marrymemo.model.V2.ThemeV2;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.CardResourceUtil;
import me.suncloud.marrymemo.util.Util;

/**
 * Created by Suncloud on 2016/6/2.
 */
public class CardV2WebActivity extends HljBaseNoBarActivity {

    @BindView(R.id.webview)
    WebView webView;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.btn_share)
    ImageButton btnShare;
    @BindView(R.id.btn_use)
    Button btnUse;
    private CardV2 card;
    private ThemeV2 theme;
    private CardV2 lastCard;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lastCard = (CardV2) getIntent().getSerializableExtra("lastCard");
        super.onCreate(savedInstanceState);
        String path;
        theme = (ThemeV2) getIntent().getSerializableExtra("theme");
        card = (CardV2) getIntent().getSerializableExtra("card");
        path = getIntent().getStringExtra("path");
        setContentView(R.layout.activity_card_v2_webview);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        if (card != null && TextUtils.isEmpty(path)) {
            if (!card.isSampleCard()) {
                btnShare.setVisibility(View.VISIBLE);
            }
            path = card.getShareLink() + "&isPreview=true";
        } else if (theme != null) {
            path = theme.getPreviewLink();
            btnUse.setVisibility(View.VISIBLE);
            if (!EventBus.getDefault()
                    .isRegistered(this)) {
                EventBus.getDefault()
                        .register(this);
            }
        }
        webView.getSettings()
                .setJavaScriptEnabled(true);

        //        wenbview缓存
        webView.getSettings()
                .setDomStorageEnabled(true);
        webView.getSettings()
                .setAppCachePath(getCacheDir().getAbsolutePath());
        webView.getSettings()
                .setAppCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings()
                    .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!TextUtils.isEmpty(url) && url.startsWith("tel:")) {
                    callUp(Uri.parse(url));
                    return true;
                }
                view.loadUrl(url);
                return false;

            }
        });
        webView.loadUrl(path);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    public void onUse(View view) {
        if (theme.isSaved()) {
            Intent intent = new Intent(this, CardV2InfoEditActivity.class);
            intent.putExtra("theme", theme);
            intent.putExtra("lastCard", lastCard);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else {
            CardResourceUtil.getInstance()
                    .executeThemeDownLoad(this, theme);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            Fragment shareFragment = getSupportFragmentManager().findFragmentByTag("shareFragment");
            if (shareFragment != null) {
                shareFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (theme != null) {
            CardResourceUtil.getInstance()
                    .setThemeDownloadListener(downLoadListener);
        }
    }

    @Override
    protected void onPause() {
        webView.loadUrl("javascript:stopMusic();");
        super.onPause();
        if (theme != null) {
            CardResourceUtil.getInstance()
                    .removeThemeDownloadListener(downLoadListener);
        }
    }

    private OnHttpRequestListener downLoadListener = new OnHttpRequestListener() {
        @Override
        public void onRequestCompleted(Object obj) {
            if (theme == null || isFinishing()) {
                return;
            }
            ThemeV2 theme2 = (ThemeV2) obj;
            if (theme.getId()
                    .equals(theme2.getId())) {
                progressBar.setVisibility(theme2.isDownLoading() ? View.VISIBLE : View.GONE);
                if (theme.isSaved()) {
                    Intent intent = new Intent(CardV2WebActivity.this,
                            CardV2InfoEditActivity.class);
                    intent.putExtra("theme", theme);
                    intent.putExtra("lastCard", lastCard);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            }
        }

        @Override
        public void onRequestFailed(Object obj) {
            if (theme == null || isFinishing()) {
                return;
            }
            ThemeV2 theme2 = (ThemeV2) obj;
            if (theme.getId()
                    .equals(theme2.getId())) {
                progressBar.setVisibility(View.GONE);
                Util.showToast(CardV2WebActivity.this, null, R.string.hint_theme_download_err);
            }
        }
    };

    @Override
    protected void onFinish() {
        webView.loadUrl("about:blank");
        webView.destroy();
        if (EventBus.getDefault()
                .isRegistered(this)) {
            EventBus.getDefault()
                    .unregister(this);
        }
        super.onFinish();
    }

    public void onEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.EventType.CARD_UPDATE_FLAG) {
            finish();
        }
    }

    public void onShare(View view) {
        CardV2ShareFragment dialogFragment = CardV2ShareFragment.newInstance(card);
        dialogFragment.show(getSupportFragmentManager(), "shareFragment");
    }
}
