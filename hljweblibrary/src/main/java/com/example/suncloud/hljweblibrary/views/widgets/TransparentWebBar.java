package com.example.suncloud.hljweblibrary.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.example.suncloud.hljweblibrary.R;

/**
 * Created by wangtao on 2017/1/19.
 */

public class TransparentWebBar extends WebBar {

    private View btnShare;

    private WebViewBarInterface webViewBarInterface;
    private WebViewBarClickListener clickListener;

    public TransparentWebBar(Context context) {
        super(context);
    }

    public TransparentWebBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TransparentWebBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initLayout() {
        View view = inflate(getContext(), R.layout.web_bar_trans___web, this);
        btnShare = view.findViewById(R.id.btn_share);
        view.findViewById(R.id.btn_back)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clickListener != null) {
                            clickListener.onBackPressed();
                        }
                    }
                });
        btnShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onSharePressed();
                }
            }
        });
    }

    @Override
    public WebViewBarInterface getInterface() {
        if (webViewBarInterface == null) {
            webViewBarInterface = new WebViewBarInterface() {
                @Override
                public void setTitle(String title) {
                }

                @Override
                public void setCloseEnable(boolean enable) {
                }

                @Override
                public void setShareEnable(boolean enable) {
                    btnShare.setVisibility(enable ? VISIBLE : GONE);
                }

                @Override
                public void setOkButtonEnable(
                        boolean enable,
                        int textColor,
                        String text,
                        int textSize) {

                }
            };
        }
        return webViewBarInterface;
    }

    @Override
    public void setBarClickListener(WebViewBarClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
