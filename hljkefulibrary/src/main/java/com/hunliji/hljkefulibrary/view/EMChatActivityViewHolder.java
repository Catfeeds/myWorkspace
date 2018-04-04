package com.hunliji.hljkefulibrary.view;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.hunliji.hljcommonlibrary.views.widgets.SpeakEditLayout;
import com.hunliji.hljcommonlibrary.views.widgets.SpeakRecordView;

/**
 * Created by wangtao on 2017/10/26.
 */

public abstract class EMChatActivityViewHolder {

    public RecyclerView rcChat;
    public ImageButton btnImage;
    public ImageButton btnVoice;
    public Button btnSpeak;
    public EditText etContent;
    public ImageButton btnFace;
    public Button btnSend;
    public FrameLayout menuLayout;
    public SpeakEditLayout speakEditLayout;
    public FrameLayout editBarLayout;
    public SpeakRecordView recordView;
    public ProgressBar progressBar;
    public RelativeLayout layout;

    public abstract void bindView(Activity activity);
}
