package com.hunliji.marrybiz.view.kefu;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.hunliji.hljkefulibrary.view.EMChatActivityViewHolder;
import com.hunliji.hljkefulibrary.view.activities.BaseEMChatActivity;
import com.hunliji.marrybiz.R;

/**
 * Created by wangtao on 2017/10/26.
 */

public class EMChatActivity extends BaseEMChatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat___kefu);
        initEMChatData();
        loginCheck();
    }

    private void initEMChatData() {
        support = getIntent().getParcelableExtra("support");
        if (support != null) {
            currentName = support.getHxIm();
        } else {
            currentName = getIntent().getStringExtra("name");
        }
        if (support != null && !TextUtils.isEmpty(support.getPhone())) {
            setOkButton(R.mipmap.icon_call_round_primary_54_54);
        }
        setTitle(support == null ? currentName : support.getNick());
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        onCall();
    }

    @Override
    protected EMChatActivityViewHolder initViewHolderBind(Activity activity) {
        return new ActivityViewHolder();
    }

    @Override
    protected void userIsLogin() {
        initData();
        initView();
        loadMessage();
    }


    private class ActivityViewHolder extends EMChatActivityViewHolder {
        @Override
        public void bindView(Activity activity) {
            rcChat = activity.findViewById(R.id.chat_list);
            btnImage = activity.findViewById(R.id.btn_image);
            btnVoice = activity.findViewById(R.id.btn_voice);
            btnSpeak = activity.findViewById(R.id.btn_speak);
            etContent = activity.findViewById(R.id.et_content);
            btnFace = activity.findViewById(R.id.btn_face);
            btnSend = activity.findViewById(R.id.btn_send);
            menuLayout = activity.findViewById(R.id.menu_layout);
            speakEditLayout = activity.findViewById(R.id.speak_edit_layout);
            editBarLayout = activity.findViewById(R.id.edit_bar_layout);
            recordView = activity.findViewById(R.id.record_view);
            progressBar = activity.findViewById(R.id.progress_bar);
            layout = activity.findViewById(R.id.layout);
        }
    }
}
