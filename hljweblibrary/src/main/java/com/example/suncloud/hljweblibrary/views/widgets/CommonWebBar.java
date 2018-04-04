package com.example.suncloud.hljweblibrary.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.R;

/**
 * Created by wangtao on 2017/1/19.
 */

public class CommonWebBar extends WebBar {

    private View btnShare;
    private TextView tvTitle;
    private TextView tvCloseHint;
    private int closeEnableCount;

    private WebViewBarInterface webViewBarInterface;
    private WebViewBarClickListener clickListener;
    private TextView tvOkText;

    public CommonWebBar(Context context) {
        super(context);
    }

    public CommonWebBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonWebBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initLayout() {
        View view = inflate(getContext(), R.layout.web_bar_common___web, this);
        btnShare = view.findViewById(R.id.btn_share);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvOkText = (TextView) view.findViewById(R.id.tv_ok_text);
        tvCloseHint = (TextView) view.findViewById(R.id.tv_close_hint);
        view.findViewById(R.id.back_layout)
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
        tvOkText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null){
                    clickListener.onOkButtonPressed();
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
                    tvTitle.setText(title);
                }

                @Override
                public void setCloseEnable(boolean enable) {
                    closeEnableCount++;
                    tvCloseHint.setVisibility(enable && closeEnableCount > 1 ? VISIBLE : GONE);
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
                    if (enable) {
                        tvOkText.setVisibility(VISIBLE);
                        tvOkText.setText(text);
                        tvOkText.setTextColor(textColor);
                        tvOkText.setTextSize(textSize);
                        btnShare.setVisibility(GONE);
                    }else {
                        tvOkText.setVisibility(GONE);
                    }
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
