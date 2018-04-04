package com.hunliji.hljchatlibrary.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljchatlibrary.R;

/**
 * Created by Suncloud on 2016/10/14.
 */

public class ChatRecordView extends FrameLayout {

    private ImageView ivMic;
    private TextView tvRecordHint;
    private int[] micResources = {R.mipmap.icon_record_animate_01___cm, R.mipmap
            .icon_record_animate_02___cm, R.mipmap.icon_record_animate_03___cm, R.mipmap
            .icon_record_animate_04___cm, R.mipmap.icon_record_animate_05___cm, R.mipmap
            .icon_record_animate_06___cm, R.mipmap.icon_record_animate_07___cm, R.mipmap
            .icon_record_animate_08___cm, R.mipmap.icon_record_animate_09___cm, R.mipmap
            .icon_record_animate_10___cm, R.mipmap.icon_record_animate_11___cm, R.mipmap
            .icon_record_animate_12___cm, R.mipmap.icon_record_animate_13___cm, R.mipmap
            .icon_record_animate_14___cm, R.mipmap.icon_record_animate_15___cm, R.mipmap
            .icon_record_animate_16___cm, R.mipmap.icon_record_animate_17___cm, R.mipmap
            .icon_record_animate_18___cm, R.mipmap.icon_record_animate_19___cm};

    private int micResourcesIndex;
    private int state; //0 录音；1 取消

    public ChatRecordView(Context context) {
        super(context);
    }

    public ChatRecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatRecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ivMic = (ImageView) findViewById(R.id.iv_mic);
        tvRecordHint = (TextView) findViewById(R.id.tv_record_hint);
    }

    public void onStart(){
        setVisibility(VISIBLE);
        state=0;
        micResourcesIndex=0;
        ivMic.setImageResource(micResources[micResourcesIndex]);
        tvRecordHint.setText(R.string.label_move_up_to_cancel___cm);
    }

    public void onStop(){
        setVisibility(GONE);
    }

    public void onRecordState(){
        state=0;
        ivMic.setImageResource(micResources[micResourcesIndex]);
        tvRecordHint.setText(R.string.label_move_up_to_cancel___cm);
    }
    public void onCancelState(){
        state=1;
        ivMic.setImageResource(R.mipmap.icon_record_cancel___cm);
        tvRecordHint.setText(R.string.label_release_to_cancel___cm);
    }

    public void setMicResourcesIndex(int micResourcesIndex) {
        this.micResourcesIndex = micResourcesIndex;
        if(state==0){
            ivMic.setImageResource(micResources[micResourcesIndex]);
        }
    }
}
