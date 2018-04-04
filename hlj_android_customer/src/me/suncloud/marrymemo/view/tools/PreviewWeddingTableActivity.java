package me.suncloud.marrymemo.view.tools;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.example.suncloud.hljweblibrary.views.widgets.WebBar;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljsharelibrary.adapters.viewholders.ShareActionViewHolder;
import com.hunliji.hljsharelibrary.models.ShareAction;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.hljsharelibrary.utils.ShareUtil;

import me.suncloud.marrymemo.R;

/**
 * 宾客预览
 * Created by chen_bin on 2017/11/28 0028.
 */
public class PreviewWeddingTableActivity extends HljWebViewActivity {

    private WebBar.WebViewBarInterface webViewBarInterface;

    private ShareUtil shareUtil;
    private Dialog shareDialog;
    private ShareInfo shareInfo;

    private ImageButton btnBack;
    private TextView tvTitle;
    private Button btnShare;

    public final static String ARG_SHARE = "share";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shareInfo = getIntent().getParcelableExtra(ARG_SHARE);
    }

    @Override
    protected WebBar initWebBar() {
        return new WebBar(this) {
            @Override
            public void initLayout() {
                View view = inflate(getContext(), R.layout.preview_wedding_table_bar, this);
                btnBack = view.findViewById(R.id.btn_back);
                tvTitle = view.findViewById(R.id.tv_title);
                btnShare = view.findViewById(R.id.btn_share);
                btnBack.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
                btnShare.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onShare();
                    }
                });
            }

            @Override
            public WebViewBarInterface getInterface() {
                if (webViewBarInterface == null) {
                    webViewBarInterface = new WebBar.WebViewBarInterface() {
                        @Override
                        public void setTitle(String title) {
                            tvTitle.setText(title);
                        }

                        @Override
                        public void setCloseEnable(boolean enable) {

                        }

                        @Override
                        public void setShareEnable(boolean enable) {

                        }

                        @Override
                        public void setOkButtonEnable(
                                boolean enable, int textColor, String text, int textSize) {

                        }
                    };
                }
                return webViewBarInterface;
            }

            @Override
            public void setBarClickListener(WebViewBarClickListener clickListener) {

            }
        };
    }

    private void onShare() {
        if (shareInfo == null) {
            return;
        }
        if (shareDialog != null && shareDialog.isShowing()) {
            return;
        }
        if (shareUtil == null) {
            shareUtil = new ShareUtil(this, shareInfo, null);
        }
        if (shareDialog == null) {
            shareDialog = ShareDialogUtil.onWeddingTableShare(this,
                    getString(R.string.label_dialog_wedding_table_share_title),
                    new ShareActionViewHolder.OnShareClickListener() {
                        @Override
                        public void onShare(View v, ShareAction action) {
                            switch (action){
                                case WeiXin:
                                    if (shareUtil != null) {
                                        shareUtil.shareToWeiXin();
                                    }
                                    break;
                                case QQ:
                                    if (shareUtil != null) {
                                        shareUtil.shareToQQ();
                                    }
                                    break;
                                case WeddingTableQRCode:
                                    String url = getPageUrl();
                                    if (!TextUtils.isEmpty(url)) {
                                        Intent intent = new Intent(PreviewWeddingTableActivity.this,
                                                WeddingTableQRCodeActivity.class);
                                        intent.putExtra(WeddingTableQRCodeActivity.ARG_URL, url);
                                    }
                                    break;
                                case SMS:
                                    if (shareUtil != null) {
                                        shareUtil.shareToSms();
                                    }
                                    break;
                            }
                        }
                    });
        }
        shareDialog.show();
    }
}


