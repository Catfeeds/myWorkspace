package me.suncloud.marrymemo.view.wallet;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.webkit.WebView;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.PointRecord;

/**
 * Created by luohanlin on 2018/1/2.
 */

public class GoldMarketWebViewActivity extends HljWebViewActivity implements HljWebViewActivity
        .OnOkTextInterface {

    private PointRecord pointRecord;

    @Override
    protected String getPageUrl() {
        if (pointRecord == null) {
            pointRecord = (PointRecord) getIntent().getSerializableExtra("pointRecord");
        }
        if (pointRecord != null) {
            return pointRecord.getHomePage();
        }
        return super.getPageUrl();
    }

    @Override
    protected void pageFinished(WebView view, String url) {
        super.pageFinished(view, url);
        if (okTextInterface == null) {
            okTextInterface = this;
        }
        barInterface.setOkButtonEnable(okTextInterface.onOkTextEnable(),
                okTextInterface.okTextColor(),
                okTextInterface.okText(),
                okTextInterface.okTextSize());
    }

    @Override
    public boolean onOkTextEnable() {
        if (webView == null) {
            return false;
        } else
            return !webView.canGoBack();
    }

    @Override
    public int okTextColor() {
        return ContextCompat.getColor(this, R.color.colorPrimary);
    }

    @Override
    public int okTextSize() {
        return 14;
    }

    @Override
    public String okText() {
        return "金币说明";
    }

    @Override
    public void onOkButtonPressed() {
        if (pointRecord == null) {
            pointRecord = (PointRecord) getIntent().getSerializableExtra("pointRecord");
        }
        if (pointRecord == null) {
            return;
        }
        Intent intent = new Intent(this, HljWebViewActivity.class);
        intent.putExtra(HljWebViewActivity.ARG_PATH, pointRecord.getCoinPage());
        startActivity(intent);
    }
}
