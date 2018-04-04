package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.R;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

/**
 * Created by werther on 16/7/25.
 */
public class HljEmptyView extends FrameLayout {

    private Context mContext;
    private View emptyViewHintView;
    private ImageView imgEmptyHint;
    private ImageView imgNetHint;
    private TextView tvEmptyHint;
    private TextView tvEmptyHint2;
    private String hintStr;
    private int hintId;
    private int hint2Id;
    private int networkHint2Id;
    private int emptyDrawableId;
    private OnNetworkErrorClickListener onNetworkErrorClickListener;//网络异常点击事件
    private OnEmptyClickListener onEmptyClickListener;//空视图点击事件

    public HljEmptyView(Context context) {
        super(context);
        init(context);
    }

    public HljEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HljEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        inflate(context, R.layout.hlj_empty_view___cm, this);
        emptyViewHintView = findViewById(R.id.empty_hint_layout);
        imgEmptyHint = (ImageView) findViewById(R.id.img_empty_hint);
        imgNetHint = (ImageView) findViewById(R.id.img_net_hint);
        tvEmptyHint = (TextView) findViewById(R.id.tv_empty_hint);
        tvEmptyHint2 = (TextView) findViewById(R.id.tv_empty_hint2);
    }

    public void hideEmptyView() {
        emptyViewHintView.setVisibility(GONE);
    }

    public void showEmptyView() {
        if (CommonUtil.isNetworkConnected(mContext)) {
            // 非网络原因,服务器错误,或者数据为空
            showDateError();
        } else {
            showNetworkError();
        }
    }

    public void showDateError() {
        emptyViewHintView.setVisibility(VISIBLE);
        // 非网络原因,服务器错误,或者数据为空
        imgEmptyHint.setVisibility(VISIBLE);
        imgNetHint.setVisibility(GONE);
        tvEmptyHint.setVisibility(VISIBLE);
        if (emptyDrawableId != 0) {
            imgEmptyHint.setImageResource(emptyDrawableId);
        }
        if (hintId != 0) {
            tvEmptyHint.setText(hintId);
        } else if (!TextUtils.isEmpty(hintStr)) {
            tvEmptyHint.setText(hintStr);
        } else {
            tvEmptyHint.setText(R.string.label_empty___cm);
        }


        if (hint2Id != 0) {
            tvEmptyHint2.setVisibility(VISIBLE);
            tvEmptyHint2.setText(hint2Id);
        } else {
            tvEmptyHint2.setVisibility(GONE);
        }
        emptyViewHintView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onEmptyClickListener != null) {
                    onEmptyClickListener.onEmptyClickListener();
                }
            }
        });
    }

    public void showNetworkError() {
        emptyViewHintView.setVisibility(VISIBLE);
        imgNetHint.setVisibility(VISIBLE);
        imgEmptyHint.setVisibility(GONE);
        tvEmptyHint.setText(R.string.label_network_disconnected___cm);
        tvEmptyHint.setVisibility(VISIBLE);
        /**
         * 点击屏幕重新加载的点击事件和提示
         */
        if (networkHint2Id != 0) {
            tvEmptyHint2.setVisibility(VISIBLE);
            tvEmptyHint2.setText(networkHint2Id);
        } else {
            tvEmptyHint2.setVisibility(GONE);
        }
        emptyViewHintView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onNetworkErrorClickListener != null) {
                    onNetworkErrorClickListener.onNetworkErrorClickListener();
                }
            }
        });
    }

    public void setEmptyDrawableId(int emptyDrawableId) {
        this.emptyDrawableId = emptyDrawableId;
    }

    public void setHint2Id(int hint2Id) {
        this.hint2Id = hint2Id;
    }

    public void setHintId(int hintId) {
        this.hintId = hintId;
    }

    public void setHintText(String hintStr) {
        this.hintStr = hintStr;
    }

    public void setNetworkHint2Id(int networkHint2Id) {
        this.networkHint2Id = networkHint2Id;
    }

    public void setNetworkErrorClickListener(
            OnNetworkErrorClickListener onNetworkErrorClickListener) {
        this.onNetworkErrorClickListener = onNetworkErrorClickListener;
    }

    public interface OnNetworkErrorClickListener {
        void onNetworkErrorClickListener();
    }

    public void setOnEmptyClickListener(
            OnEmptyClickListener onEmptyClickListener) {
        this.onEmptyClickListener = onEmptyClickListener;
    }

    public interface OnEmptyClickListener {
        void onEmptyClickListener();
    }
}
