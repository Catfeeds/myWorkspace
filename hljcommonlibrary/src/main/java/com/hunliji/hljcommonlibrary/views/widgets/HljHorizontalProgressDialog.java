package com.hunliji.hljcommonlibrary.views.widgets;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.R;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

/**
 * Created by hua_rong on 2017/5/12.
 * 水平上传进度条dialog
 */

public class HljHorizontalProgressDialog extends Dialog {

    private String title;
    private int progress;
    private long contentLength;//单图片总长度
    private int totalCount;//上传图片总数
    private int uploadPosition;//初始上传的位置(默认从第一张开始)
    private int currentProgress;//当前单图片的初始加载进度

    private TextView tvProgress;
    private View uploadView;
    private View networkErrorView;
    private View btnConfirm;
    private OnConfirmClickListener onConfirmClickListener;
    private HljUploadProgressBar progressBar;
    private Handler mViewUpdateHandler;

    public HljHorizontalProgressDialog(Context context) {
        super(context);
    }

    public HljHorizontalProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            window.setContentView(R.layout.hlj_dialog_horizontal_upload);
            TextView tvTitle = (TextView) window.findViewById(R.id.tv_title);
            TextView tvNetState = (TextView) window.findViewById(R.id.tv_net_state);
            tvProgress = (TextView) window.findViewById(R.id.tv_progress);
            progressBar = (HljUploadProgressBar) window.findViewById(R.id.progress_bar);
            networkErrorView = window.findViewById(R.id.network_error_view);
            uploadView = window.findViewById(R.id.upload_view);
            btnConfirm = window.findViewById(R.id.btn_confirm);
            TextView btnCancel = (TextView) window.findViewById(R.id.btn_cancel);
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onConfirmClickListener != null) {
                        onConfirmClickListener.onConfirmClick();
                    }
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            Point point = CommonUtil.getDeviceSize(getContext());
            int width = (int) (point.x * 0.75);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = width;
            lp.gravity = Gravity.CENTER;
            window.setAttributes(lp);
            int textWidth = (int) tvProgress.getPaint()
                    .measureText("100%");
            progressBar.getLayoutParams().width = width - textWidth - CommonUtil.dp2px(getContext(),
                    60);
            if (!TextUtils.isEmpty(title)) {
                tvTitle.setText(title);
            }
            tvNetState.setVisibility(CommonUtil.isWifi(getContext()) ? View.GONE : View.VISIBLE);
            mViewUpdateHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    Log.d("progress", "==mViewUpdateHandler progress==" + progress);
                    progressBar.setProgress(progress);
                    tvProgress.setText(String.format("%s%%", String.valueOf(progress)));
                    return false;
                }
            });
        } else {
            dismiss();
        }
    }

    public interface OnConfirmClickListener {
        void onConfirmClick();
    }

    public void setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setUploadPosition(int uploadPosition) {
        this.uploadPosition = uploadPosition;
        if (totalCount > 0) {
            progress = uploadPosition * 100 / totalCount;
            currentProgress = progress;
            Log.e("progress", "==uploadPosition progress==" + progress + "==uploadPosition==" + uploadPosition);
            onProgressChanged();
        }
    }

    /**
     * 设置当前上传的图片列表小标
     *
     * @param currentIndex 当前上传的图片列表小标
     */
    public void setCurrentIndex(int currentIndex) {
        if (totalCount > 0) {
            progress = (currentIndex + uploadPosition) * 100 / totalCount;
            currentProgress = progress;
            Log.d("progress", "==currentIndex progress==" + progress + "==currentIndex==" + currentIndex);
            onProgressChanged();
        }
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * 设置单图片当前进度
     *
     * @param transBytes 单图片当前进度
     */
    public void setTransBytes(long transBytes) {
        if (contentLength != 0 && transBytes != 0 && totalCount != 0) {
            progress = currentProgress + (int) ((transBytes * 100 / contentLength) / totalCount);
            Log.d("progress", "==transBytes progress==" + progress + "==transBytes==" + transBytes);
            onProgressChanged();
        }
    }

    private void onProgressChanged() {
        if (progress > 100) {
            progress = 100;
        }
        if (mViewUpdateHandler != null && !mViewUpdateHandler.hasMessages(0)) {
            mViewUpdateHandler.sendEmptyMessage(0);
        }
    }

    public void showNetworkErrorView() {
        uploadView.setVisibility(View.GONE);
        networkErrorView.setVisibility(View.VISIBLE);
    }

    public void showUploadView() {
        uploadView.setVisibility(View.VISIBLE);
        networkErrorView.setVisibility(View.GONE);
    }
}
