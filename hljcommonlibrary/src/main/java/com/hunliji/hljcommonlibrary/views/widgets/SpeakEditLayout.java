package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.hunliji.hljcommonlibrary.R;

/**
 * 语音按钮编辑框
 * Created by wangtao on 2017/3/14.
 */

public class SpeakEditLayout extends MenuEditLayout implements SpeakRecordView
        .OnSpeakStatusListener {

    private ImageButton btnVoice;
    private Button btnSpeak;
    private SpeakerEditLayoutInterface layoutInterface;
    private OnBindSpeakViewInterface onBindInterface;


    public SpeakEditLayout(Context context) {
        super(context);
    }

    public SpeakEditLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpeakEditLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSpeakEditLayoutInterface(SpeakerEditLayoutInterface layoutInterface) {
        this.layoutInterface = layoutInterface;
    }

    public void setOnBindSpeakViewInterface(OnBindSpeakViewInterface onBindInterface) {
        this.onBindInterface = onBindInterface;
    }

    public void initView() {
        super.initView();
        if (onBindInterface == null) {
            throw new NullPointerException("onBindInterface is null");
        }
        btnVoice = onBindInterface.btnVoice();
        btnSpeak = onBindInterface.btnSpeak();
        if(btnVoice==null||btnSpeak==null){
            throw new RuntimeException("bind view is Null");
        }
        btnVoice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onSpeakerModeChange();
            }
        });
        btnSpeak.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return layoutInterface != null && layoutInterface.onSpeakTouchEvent(event);
            }
        });
        addMenuVisibleListener(new MenuVisibleListener() {
            @Override
            public void onMenuVisibleChanged(boolean isVisible) {
                if (isVisible && etContent.getVisibility() != View.VISIBLE) {
                    etContent.setVisibility(View.VISIBLE);
                    btnSpeak.setVisibility(View.GONE);
                    etContent.requestFocus();
                }
            }
        });
    }

    private void onSpeakerModeChange() {
        if (menuLayoutInterface == null) {
            return;
        }
        if (etContent.getVisibility() == View.VISIBLE) {
            menuLayoutInterface.onImageButtonChecked(btnVoice, true);
            hideMenu();
            etContent.setVisibility(View.GONE);
            btnSpeak.setVisibility(View.VISIBLE);
            menuLayoutInterface.hideImm();
        } else {
            menuLayoutInterface.onImageButtonChecked(btnVoice, false);
            etContent.setVisibility(View.VISIBLE);
            btnSpeak.setVisibility(View.GONE);
            etContent.requestFocus();
            menuLayoutInterface.showImm();
        }
    }

    @Override
    protected void onMenuButtonCheckedChange(View v, boolean isChecked) {
        super.onMenuButtonCheckedChange(v, isChecked);
        if (isChecked) {
            menuLayoutInterface.onImageButtonChecked(btnVoice, false);
        }
    }

    @Override
    public void onStart() {
        btnSpeak.setPressed(true);
    }

    @Override
    public void onStop() {
        btnSpeak.setPressed(false);
        btnSpeak.setText(R.string.action_speak_start___cm);
    }

    @Override
    public void onCancel() {
        btnSpeak.setText(R.string.label_release_to_cancel___cm);

    }

    public String getContent() {
        String text = etContent.getText()
                .toString();
        etContent.setText(null);
        return text;
    }

    @Override
    public void onRecord() {
        btnSpeak.setText(R.string.action_speak_done___cm);
    }

    public interface SpeakerEditLayoutInterface {

        boolean onSpeakTouchEvent(MotionEvent event);
    }

    public interface OnBindSpeakViewInterface {
        ImageButton btnVoice();

        Button btnSpeak();
    }
}
